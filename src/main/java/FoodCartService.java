package design;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


interface Repository {
	boolean add(int cartId, int restaurantId, String food, int quantity);
	boolean remove(int cartId, int restaurantId, String food);
	boolean isAllFoodAvailable(int cartId);
	void addMenu(int restaurantId, String food, int price);
	void removeMenu(int restaurantId, String food);
	int reportPrice(int cartId);
}

class Restaurant {
	int id;
	Map<String, Integer> menu;
	public Restaurant(int i) {
		id=i;
		menu = new HashMap<>();
	}
	public boolean isFoodAvailable(String food) {
		return menu.containsKey(food);
	}
	public void addFood(String food, int price) {
		menu.put(food,price);
	}
	public void removeFood(String food) {
		menu.remove(food);
	}
	public int getPrice(String food) {
		return menu.get(food);
	}
}

class Cart {
	final int id;
	final Restaurant restaurant;
	final Map<String, Integer> foods;
	
	public Cart(int i, Restaurant r) {
		id=i;
		restaurant = r;
		foods = new HashMap<String, Integer>(); 
	}
	public boolean add(int restaurantId, String food, int quantity) {
		if(restaurantId!=restaurant.id || !restaurant.isFoodAvailable(food))return false;
		foods.put(food, foods.getOrDefault(food, 0)+quantity);
		return true;
	}
	public boolean remove(int restaurantId, String food) {
		if(restaurantId!=restaurant.id)return false;
		if(!foods.containsKey(food))return false;
		foods.remove(food);
		return true;
	}
	public boolean isAllFoodAvailable() {
		for(String food: foods.keySet())if(!restaurant.isFoodAvailable(food)) return false;
		return true;
	}
	public int getCartPrice() {
		int price = 0;
		for(String food: foods.keySet()) {
			int quantity = foods.get(food);
			price += quantity * restaurant.getPrice(food);
		}
		return price;
	}
}

interface PricingService {
	int getTax(Restaurant restaurant, int base);
	int getDeliveryFee(Restaurant restaurant);
	int getCouponDiscount(Restaurant restaurant, int netprice);
	void addTax(int restaurantId, int tax);
	void addDeliveryFee(int restaurantId, int fee);
	void addCoupon(int restaurantId, int minPrice, int discount);
}

class Coupon {
	int minPrice;
	int discount;
	public Coupon(int m, int d) {
		minPrice=m;discount=d;
	}
}

class FoodPricingService implements PricingService {
	final Map<Integer, Integer> tax;
	final Map<Integer, Integer> deliveryFee;
	final Map<Integer, Coupon> coupons;
	
	public FoodPricingService() {
		tax = new HashMap<Integer, Integer>();
		deliveryFee = new HashMap<Integer, Integer>();
		coupons = new HashMap<Integer, Coupon>();
	}

	@Override
	public int getTax(Restaurant restaurant, int base) {
		return tax.get(restaurant.id);
	}

	@Override
	public int getDeliveryFee(Restaurant restaurant) {
		return deliveryFee.get(restaurant.id);
	}

	@Override
	public int getCouponDiscount(Restaurant restaurant, int netprice) {
		Coupon coupon = coupons.get(restaurant.id);
		if(netprice >= coupon.minPrice) return coupon.discount;
		return 0;
	}
	@Override
	public void addTax(int restaurantId, int tax) {
		this.tax.put(restaurantId, tax);
	}
	@Override
	public void addDeliveryFee(int restaurantId, int fee) {
		this.deliveryFee.put(restaurantId, fee);
	}
	@Override
	public void addCoupon(int restaurantId, int minPrice, int discount) {
		this.coupons.put(restaurantId, new Coupon(minPrice, discount));
	}
}

class InmemoryRepository implements Repository {

	final Map<Integer, Cart> carts;
	final Map<Integer, Restaurant> restaurants;
	final PricingService pricingService;
	
	public InmemoryRepository(PricingService pricingService) {
		carts = new HashMap<Integer, Cart>();
		restaurants = new HashMap<Integer, Restaurant>();
		this.pricingService=pricingService;
	}
	
	@Override
	public boolean add(int cartId, int restaurantId, String food, int quantity) {
		if(!restaurants.containsKey(restaurantId)) return false;
		carts.putIfAbsent(cartId, new Cart(cartId,restaurants.get(restaurantId)));
		return carts.get(cartId).add(restaurantId, food, quantity);
	}

	@Override
	public boolean remove(int cartId, int restaurantId, String food) {
		if(!restaurants.containsKey(restaurantId)) return false;
		if(!carts.containsKey(cartId))return false;
		return carts.get(cartId).remove(restaurantId, food);
	}

	@Override
	public boolean isAllFoodAvailable(int cartId) {
		if(!carts.containsKey(cartId))return false;
		return carts.get(cartId).isAllFoodAvailable();
	}
	
	@Override
	public void addMenu(int restaurantId, String food, int price) {
		restaurants.putIfAbsent(restaurantId, new Restaurant(restaurantId));
		restaurants.get(restaurantId).addFood(food, price);
	}
	
	@Override
	public void removeMenu(int restaurantId, String food) {
		restaurants.putIfAbsent(restaurantId, new Restaurant(restaurantId));
		restaurants.get(restaurantId).removeFood(food);
	}

	@Override
	public int reportPrice(int cartId) {
		if(!carts.containsKey(cartId))return 0;
		Cart cart = carts.get(cartId);
		Restaurant restaurant = cart.restaurant;
		int base = cart.getCartPrice();
		int netprice = base;
		netprice += pricingService.getTax(restaurant, base);
		netprice += pricingService.getDeliveryFee(restaurant);
		netprice -= pricingService.getCouponDiscount(restaurant, netprice);
		return netprice;
	}
	
}


class CartService {
	final Repository repository;
	
	public CartService(Repository repository) {
		this.repository=repository;
	}
	
	public boolean add(int cartId, int restaurantId, String food, int quantity) {
		return repository.add(cartId, restaurantId, food, quantity);
	}
	
	public boolean remove(int cartId, int restaurantId, String food) {
		return repository.remove(cartId, restaurantId, food);
	}
	
	public boolean isValidOrder(int cartId) {
		return repository.isAllFoodAvailable(cartId);
	}
	
	public int reportPrice(int cartId) {
		return repository.reportPrice(cartId);
	}
}
package design;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.*;
import org.junit.jupiter.api.*;

class DesignTest {
	

	@Test
	public void testCartService() throws Exception {
		PricingService pricingService = new FoodPricingService();
		pricingService.addTax(1, 2);
		pricingService.addDeliveryFee(1, 3);
		pricingService.addCoupon(1, 50, 10);
		Repository repository = new InmemoryRepository(pricingService);
		repository.addMenu(1, "burger", 10);
		repository.addMenu(1, "pizza", 30);
		repository.addMenu(1, "idli", 2);
		repository.addMenu(1, "cheese", 10);
		CartService service = new CartService(repository);
		
		assertTrue(service.add(1, 1, "burger", 2));
		assertTrue(service.add(1, 1, "pizza", 1));
		assertTrue(service.isValidOrder(1));
		
		repository.removeMenu(1, "pizza");
		assertFalse(service.isValidOrder(1));
		repository.addMenu(1, "pizza", 35);
		
		assertEquals(50, service.reportPrice(1));
	}

}
