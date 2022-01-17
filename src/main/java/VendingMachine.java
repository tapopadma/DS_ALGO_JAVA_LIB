import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.JUnitCore;

/**
 * 
 * @author tapopadma
 * 1 - shows available coffee & price
 * 2 - takes coffee type as input
 * 3 - takes coins as input
 * 4 - prepares coffee
 * 5 - returns coins
 */
public class VendingMachine{
	public VendingMachine() {
		// TODO Auto-generated constructor stub
	}
	
	enum CoffeeType{
		LATTE(10),CAPUCCINO(15),KOPI(5),BLACK(10);
		int price;
		private CoffeeType(int price) {
			this.price=price;
		}
	}
	
	class Server{
		Map<CoffeeType, Integer>availbleInMl;
		CoffeeType coffeInProgress;
		Map<Integer, Integer>moneyAvailableMap;
		int amountToReturn;
		Map<Integer, Integer>coinsToReturnMap;
		public Server(Map<CoffeeType, Integer>availbleInMl) {
			this.availbleInMl=availbleInMl;
			this.coffeInProgress=null;
			this.moneyAvailableMap=new HashMap<>();
			this.amountToReturn=0;
			coinsToReturnMap=null;
		}
		public void selectCoffee(CoffeeType c) {
			coffeInProgress=c;
		}
		void addToFund(Map<Integer, Integer>coinMap) {
			for(int coin:coinMap.keySet()) {
				int count=coinMap.get(coin);
				count+=moneyAvailableMap.containsKey(coin)
						?moneyAvailableMap.get(coin):0;
				moneyAvailableMap.put(coin, count);
			}
		}
		void removeFromFund(Map<Integer, Integer> coinMap) {
			for(int coin:coinMap.keySet()) {
				int count=moneyAvailableMap.get(coin);
				count-=coinMap.get(coin);
				moneyAvailableMap.put(coin, count);
			}
		}
		
		int countCoinToFindSum(int i, int sum, List<Integer>coins,
				Map<Integer, Integer>coinsMap) {
			if(sum==0)return 0;
			if(i==coins.size())return -1;
			for(int c=0;c<=coinsMap.get(coins.get(i))
					&&c*coins.get(i)<=sum;++c) {
				if(countCoinToFindSum(
						i+1, sum-c*coins.get(i), coins, coinsMap)>=0)
					return c;
			}
			return -1;
		}
		
		Map<Integer, Integer> subsetSum(int sum, Map<Integer, Integer> extraMap){
			Set<Integer> coins=new HashSet<>();
			coins.addAll(moneyAvailableMap.keySet());
			coins.addAll(extraMap.keySet());
			Map<Integer, Integer>coinsMap=coins.stream()
					.collect(
							Collectors.toMap(Function.identity(), 
							c->(moneyAvailableMap.containsKey(c)
							?moneyAvailableMap.get(c):0)
							+(extraMap.containsKey(c)
							?extraMap.get(c):0)));
			Map<Integer, Integer>subsetMap=new HashMap<>();
			List<Integer> coinList=coins.stream().toList();
			if(countCoinToFindSum(
					0,sum,coinList,coinsMap)>=0) {
				for(int i=0;i<coins.size();++i) {
					int c=countCoinToFindSum(i, sum, coinList, coinsMap);
					subsetMap.put(coinList.get(i), c);
					sum-=c*coinList.get(i);
				}
				return subsetMap;
			}
			return null;
		}
		public void insertCoins(Integer... coins) throws Exception{
			Map<Integer, Integer>coinMap=
					Arrays.stream(coins)
					.collect(
					Collectors.groupingBy(Function.identity(),
							Collectors.reducing(0, (ans,e)->ans+1)));
			amountToReturn=coinMap.keySet()
					.stream().mapToInt(c->coinMap.get(c)*c).sum()
					-coffeInProgress.price;
			if(coinMap.keySet().stream().max((c1,c2)->c1.compareTo(c2)).get()
					> 100) {
				throw new Exception("Max 100 cents allowed");
			}
			if(amountToReturn<0)throw new Exception("insufficient amount");
			if(amountToReturn>0) {
				if((coinsToReturnMap=subsetSum(amountToReturn, coinMap))
						==null){
					throw new Exception("insufficient changes");
				}
			}else {
				coinsToReturnMap=new HashMap<>();
			}
			addToFund(coinMap);
		}
		
		public Map<Integer, Integer> returnCoins(){
			availbleInMl.put(coffeInProgress,
					availbleInMl.get(coffeInProgress)-10);
			coffeInProgress=null;
			removeFromFund(coinsToReturnMap);
			Map<Integer, Integer>resMap=coinsToReturnMap;
			coinsToReturnMap=null;amountToReturn=0;
			return resMap;
		}
		
	}
	
	Server server=new Server(
			new HashMap<>() {{
				put(CoffeeType.BLACK,100);
				put(CoffeeType.CAPUCCINO,200);
				put(CoffeeType.KOPI,40);
				put(CoffeeType.LATTE,400);
			}});
	
	@Test
	public void server_forValidCoins_succeeds() throws Exception {
		server.selectCoffee(CoffeeType.CAPUCCINO);
		server.insertCoins(15,10);
		assertEquals(server.returnCoins().get(10).intValue(),1);
		server.selectCoffee(CoffeeType.KOPI);
		server.insertCoins(5);
		assertTrue(server.returnCoins().isEmpty());
		assertEquals(server.moneyAvailableMap.get(5).intValue(), 1);
		assertEquals(server.moneyAvailableMap.get(15).intValue(), 1);
		server.selectCoffee(CoffeeType.BLACK);
		server.insertCoins(15);
		assertEquals(server.returnCoins().get(5).intValue(),1);
	}
	
	public static void main(String[] args) {
		JUnitCore.main("VendingMachine");
	}
}