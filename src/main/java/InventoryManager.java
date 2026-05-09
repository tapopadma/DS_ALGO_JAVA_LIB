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
