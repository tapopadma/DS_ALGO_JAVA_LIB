import static org.junit.Assert.*;

import java.util.*;
import java.util.stream.*;
import org.apache.commons.lang3.tuple.*;
import org.junit.*;
import org.junit.runner.*;


public class StockExchange {

	private static int TRADE_ID = 1;
	private static int ORDER_ID = 1;

	enum OrderType {
		BUY,SELL;
	}

	class Order {
		int orderId;
		OrderType orderType;
		int price;// price per stock
		int quantity; // number of stocks to be treaded.
		Order(int a,OrderType o, int b,int c) {
			orderId=a;
			orderType=o;
			price=b;
			quantity=c;
		}
	}

	class Trade {
		int tradeId;
		int sellOrderId;
		int buyOrderId;
		int quantity;
		int price;
		long timestamp;
		Trade(int a,int b,int c,int d,int e,long f){
			tradeId=a;
			sellOrderId=b;
			buyOrderId=c;
			quantity=d;
			price=e;
			timestamp=f;
		}
	}

	class SEEngine {
		TreeSet<Order> sellOrders, buyOrders;

		SEEngine() {
			sellOrders = new TreeSet<>((s1,s2)->s1.price==s2.price?Integer.compare(s1.orderId,s2.orderId):Integer.compare(s1.price, s2.price));
			buyOrders = new TreeSet<>((b1,b2)->b1.price==b2.price?Integer.compare(b1.orderId,b2.orderId):Integer.compare(b2.price, b1.price));
		}

		List<Trade> processOrder(Order order) {
			List<Trade> trades = new ArrayList<>();
			if(order.orderType.equals(OrderType.BUY)) {
				while(!sellOrders.isEmpty() && sellOrders.first().price <= order.price && order.quantity > 0) {
					Order sellOrder = sellOrders.first();
					int tradeQuantity = Math.min(order.quantity, sellOrder.quantity);
					int tradePrice = sellOrder.price;
					sellOrder.quantity = sellOrder.quantity - tradeQuantity;
					order.quantity = order.quantity - tradeQuantity;
					if(sellOrder.quantity==0)sellOrders.pollFirst();
					trades.add(new Trade(TRADE_ID++,sellOrder.orderId,order.orderId,tradeQuantity,tradePrice,System.currentTimeMillis()));
				}
				if(order.quantity > 0){
					buyOrders.add(order);
				}
			} else {
				while(!buyOrders.isEmpty() && buyOrders.first().price >= order.price && order.quantity > 0) {
					Order buyOrder = buyOrders.first();
					int tradeQuantity = Math.min(order.quantity, buyOrder.quantity);
					int tradePrice = order.price;
					buyOrder.quantity = buyOrder.quantity - tradeQuantity;
					order.quantity = order.quantity - tradeQuantity;
					if(buyOrder.quantity==0)buyOrders.pollFirst();
					trades.add(new Trade(TRADE_ID++,order.orderId,order.orderId,tradeQuantity,tradePrice,System.currentTimeMillis()));
				}
				if(order.quantity > 0){
					sellOrders.add(order);
				}
			}
			return trades;
		}
	}
	

	@Test
	public void testSinglePurchaseSell() throws Exception{
		SEEngine se = new SEEngine();
		se.processOrder(new Order(ORDER_ID++,OrderType.BUY,10,5));
		se.processOrder(new Order(ORDER_ID++,OrderType.SELL,9,3));
		assertTrue(se.sellOrders.isEmpty());
		assertEquals(se.buyOrders.size(),1);
		assertEquals(se.buyOrders.first().price,10);
		assertEquals(se.buyOrders.first().quantity,2);
	}

	public static void main(String[] args){
		JUnitCore.main("StockExchange");
	}
}