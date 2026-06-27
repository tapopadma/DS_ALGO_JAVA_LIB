package design;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Warehouse {
	int id;
	ConcurrentHashMap<Integer, Integer> inventory;
	ConcurrentHashMap<Integer, Lock> locks;
	
	public Warehouse(int id) {
		inventory = new ConcurrentHashMap<Integer, Integer>();
		locks = new ConcurrentHashMap<Integer, Lock>();
		this.id=id;
	}
	
	public boolean addStock(int product, int quantity) {
		inventory.compute(product, (k,v)-> {
			if(v==null)return quantity;
			return quantity+v;
		});
		return true;
	}
	public boolean removeStock(int product, int quantity) {
		boolean[] success = new boolean[] {true};
		inventory.compute(product, (k,v)-> {
			if(v==null || v < quantity) {
				success[0]=false;
				return v;
			}
			return v-quantity;
		});
		return success[0];
	}
	public boolean transferStock(Warehouse target, int product, int quantity) {
		boolean transferred = true;
		int from = id;
		int to = target.id;
		List<Integer> ids = new ArrayList<>(List.of(from,to));
		Collections.sort(ids);
		for(int id: ids) {
			Lock lock = locks.compute(id, (k,v)->{
				if(v!=null) return v;
				return new ReentrantLock();
			});
			lock.lock();
		}
		boolean removed = removeStock(product, quantity);
		if(removed) {
			target.addStock(product, quantity);
		} else {
			transferred=false;
		}
		for(int i=ids.size()-1;i>=0;--i) {
			locks.get(ids.get(i)).unlock();
		}
		return transferred;
	}
	@Override
	public String toString() {
		return id+": "+inventory.toString();
	}
}

class InventoryManager {
	Map<Integer, Warehouse> warehouses;
	
	public InventoryManager() {
		warehouses = new HashMap<Integer, Warehouse>();
	}
	public void addWarehouse(int warehouse) {
		warehouses.put(warehouse, new Warehouse(warehouse));
	}
	
	public boolean addStock(int warehouse, int product, int quantity) {
		if(!warehouses.containsKey(warehouse)) return false;
		return warehouses.get(warehouse).addStock(product, quantity);
	}
	
	public boolean removeStock(int warehouse, int product, int quantity) {
		if(!warehouses.containsKey(warehouse)) return false;
		return warehouses.get(warehouse).removeStock(product, quantity);
	}
	
	public boolean transferStock(int fromWarehouse, int toWarehouse, int product, int quantity) {
		if(!warehouses.containsKey(fromWarehouse)||
				!warehouses.containsKey(toWarehouse)) return false;
		return warehouses.get(fromWarehouse)
				.transferStock(warehouses.get(toWarehouse), product, quantity);
	}
	
	public String fetchStock(int warehouse) {
		return warehouses.get(warehouse).toString();
	}
	
}

package design;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import org.junit.jupiter.api.*;

class DesignTest {
	
	private InventoryManager buildInventoryManager() {
		InventoryManager manager = new InventoryManager();
		manager.addWarehouse(1);manager.addWarehouse(2);manager.addWarehouse(3);
		manager.addStock(1, 1, 10);manager.addStock(1, 2, 6);
		manager.addStock(2, 1, 20);manager.addStock(2, 3, 16);
		manager.addStock(3, 2, 10);manager.addStock(3, 4, 1);
		return manager;
	}
	
	@Test
	public void testSingleUser() throws Exception {
		System.out.println();
		InventoryManager manager = buildInventoryManager(); 
		assertEquals(manager.removeStock(2, 2, 1), false);
		assertEquals(manager.addStock(2, 2, 1), true);
		assertEquals(manager.transferStock(1, 2, 2, 1), true);
		System.out.println(manager.fetchStock(1));
		System.out.println(manager.fetchStock(2));
	}
	
	@Test
	public void testMultiUser() throws Exception {
		System.out.println();
		InventoryManager manager = buildInventoryManager();
		List<Thread> requests = new ArrayList<Thread>();
		requests.add(new Thread(()-> {
			manager.transferStock(2, 1, 3, 10);
		}));
		requests.add(new Thread(()-> {
			manager.transferStock(2, 3, 3, 10);
		}));
		requests.add(new Thread(()-> {
			manager.removeStock(2, 3, 5);
		}));
		for(Thread r: requests) {
			r.start();
		}
		for(Thread r: requests) {
			r.join();
		}
		for(int i=1;i<=3;++i)System.out.println(manager.fetchStock(i));
	}

}


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
	boolean add(int warehouseId, int productId, int quantity);
	boolean remove(int warehouseId, int productId, int quantity);
	boolean transfer(int fromWarehouseId, int toWarehouseId, int productId, int quantity);
	void configureAlert(int warehouseId, int productId, AlertConfig config);
	Stock getStock(int warehouseId, int productId);
}

class AlertConfig {
	int threshold;
	Runnable callback;
	
	public AlertConfig(int t, Runnable r) {
		threshold=t;callback=r;
	}
	
	void trigger() {
		callback.run();
	}
}

class Stock {
	int productId;
	int quantity;
	Stock(int p, int q) {
		productId=p;quantity=q;
	}
}

class Warehouse {
	int id;
	final Map<Integer, Stock> stock;
	final Map<Integer, AlertConfig> alerts;
	
	public Warehouse(int i) {
		id=i;
		stock = new ConcurrentHashMap<Integer, Stock>();
		alerts = new ConcurrentHashMap<Integer, AlertConfig>();
	}
	void triggerAlert(int productId) {
		if(alerts.containsKey(productId)) {
			alerts.get(productId).trigger();
		}
	}
	public boolean add(int productId, int quantity) {
		stock.compute(productId, (k,v)-> {
			if(v==null)v = new Stock(k,quantity);
			else v.quantity += quantity;
			return v;
		});
		return true;
	}
	public boolean remove(int productId, int quantity) {
		var result = new Object() {boolean success=false;};
		stock.compute(productId, (k,v)-> {
			if(v!=null&&v.quantity>=quantity) {
				result.success=true;
				v.quantity-=quantity;
				if(alerts.containsKey(productId) && alerts.get(productId).threshold>v.quantity) {
					triggerAlert(productId);
				}
			}
			return v;
		});
		return result.success;
	}
	public boolean transfer(Warehouse toWarehouse, int productId, int quantity) {
		if(this.remove(productId, quantity)) {
			toWarehouse.add(productId, quantity);
			return true;
		}
		return false;
	}
	public void setAlert(int productId, AlertConfig config) {
		alerts.put(productId, config);
	}
}

class InmemoryRepository implements Repository {
	
	final Map<Integer, Warehouse> warehouses;
	final Map<Integer, Lock> locks;
	
	public InmemoryRepository() {
		warehouses = new ConcurrentHashMap<Integer, Warehouse>();
		locks = new ConcurrentHashMap<Integer, Lock>();
	}

	@Override
	public boolean add(int warehouseId, int productId, int quantity) {
		var result = new Object() {boolean success=false;};
		warehouses.compute(warehouseId, (k,v)-> {
			if(v==null)v=new Warehouse(k);
			result.success = v.add(productId, quantity);
			return v;
		});
		return result.success;
	}

	@Override
	public boolean remove(int warehouseId, int productId, int quantity) {
		var result = new Object() {boolean success=false;};
		warehouses.compute(warehouseId, (k,v)-> {
			if(v==null)return v;
			result.success = v.remove(productId, quantity);
			return v;
		});
		return result.success;
	}

	@Override
	public boolean transfer(int fromWarehouseId, int toWarehouseId, int productId, int quantity) {
		List<Integer> list = new ArrayList<>();
		list.add(fromWarehouseId);
		list.add(toWarehouseId);
		Collections.sort(list);
		for(int k: list) {
			locks.putIfAbsent(k, new ReentrantLock());
			locks.get(k).lock();
		}
		boolean success = false;
		try {
			if(warehouses.containsKey(fromWarehouseId)&&warehouses.containsKey(toWarehouseId)) {
				success = warehouses.get(fromWarehouseId).transfer(warehouses.get(toWarehouseId), productId, quantity);
			}
		} finally {
			Collections.reverse(list);
			for(int k: list) {
				locks.get(k).unlock();
			}
		}
		return success;
	}

	@Override
	public void configureAlert(int warehouseId, int productId, AlertConfig config) {
		warehouses.get(warehouseId).setAlert(productId, config);
	}
	
	@Override
	public Stock getStock(int warehouseId, int productId) {
		return warehouses.get(warehouseId).stock.getOrDefault(productId, null);
	}
	
}

class WarehouseManagerService {
	final Repository repository;
	
	public WarehouseManagerService(Repository repository) {
		this.repository=repository;
	}
	
	public boolean add(int warehouseId, int productId, int quantity) {
		return repository.add(warehouseId, productId, quantity);
	}
	public boolean remove(int warehouseId, int productId, int quantity) {
		return repository.remove(warehouseId, productId, quantity);
	}
	public boolean transfer(int fromWarehouseId, int toWarehouseId, int productId, int quantity) {
		return repository.transfer(fromWarehouseId, toWarehouseId, productId, quantity);
	}
	public void configureAlert(int warehouseId, int productId, AlertConfig config) {
		repository.configureAlert(warehouseId, productId, config);
	}
}
package design;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.*;
import org.junit.jupiter.api.*;

class DesignTest {
	

	@Test
	public void testBasic() throws Exception {
		Repository repository = new InmemoryRepository();
		WarehouseManagerService service = new WarehouseManagerService(repository);
		
		assertTrue(service.add(1, 1, 5));
		assertTrue(service.add(2, 1, 1));
		assertTrue(service.transfer(1, 2, 1, 1));
		assertTrue(service.add(2, 2, 10));
		service.configureAlert(2, 2, new AlertConfig(6, ()->System.out.print("outofstock alert!!!")));
		assertTrue(service.transfer(2, 1, 2, 5));
		
		assertEquals(5, repository.getStock(1, 2).quantity);
		assertEquals(5, repository.getStock(2, 2).quantity);
	}
	
	@Test
	public void testAsync() throws Exception {
		Repository repository = new InmemoryRepository();
		WarehouseManagerService service = new WarehouseManagerService(repository);
		
		assertTrue(service.add(1, 1, 5000));
		assertTrue(service.add(2, 1, 5000));
		List<Thread> requests = new ArrayList<Thread>();
		for(int i=0;i<20;++i) {
			requests.add(new Thread(()->service.transfer(1, 2, 1, 10)));
		}
		for(int i=0;i<20;++i) {
			requests.add(new Thread(()->service.transfer(2, 1, 1, 10)));
		}
		for(Thread t: requests)t.start();
		for(Thread t: requests)t.join();
		assertEquals(5000, repository.getStock(1, 1).quantity);
		assertEquals(5000, repository.getStock(2, 1).quantity);
	}

}
