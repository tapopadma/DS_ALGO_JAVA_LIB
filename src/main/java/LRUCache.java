package design;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;



interface LRUCache {
	
	int get(int key);
	void put(int key, int val);	
	void delete(int key);
	
}

class Node {
	int key,val;
	Node left,right;
	Node(int d,int v) {
		key=d;val=v;left=right=null;
	}
}

class LRUCacheFromScratch implements LRUCache {

	Node head,tail;
	Map<Integer, Node> map;
	int capacity;
	
	public LRUCacheFromScratch(int cap) {
		map = new HashMap<Integer, Node>();
		head=tail=null;
		capacity=cap;
	}
	
	@Override
	public int get(int key) {
		if(map.containsKey(key)) {
			updateRecency(key,null);
			return map.get(key).val;
		} else {
			return -1;
		}
	}
	
	void updateRecency(int key, Integer val) {
		Node node = map.get(key);
		if(val!=null) node.val=val;
		map.put(key, node);
		if(head==tail)return;
		if(node==head) {
			head=node.right;
			if(head==null)tail=null;
			else {
				head.left=null;
				tail.right=node;node.left=tail;
				tail=node;
			}
		} else if(node != tail){
			Node l = node.left;
			Node r = node.right;
			l.right=r;r.left=l;
			tail.right=node;node.left=tail;tail=node;
		}
	}
	
	void removeOldest() {
		int old=head.key;
		head=head.right;
		if(head!=null)head.left=null;
		else tail=null;
		map.remove(old);
	}

	@Override
	public void put(int key, int val) {
		if(head==null) {
			head=tail=new Node(key,val);
			map.put(key, head);
		} else {
			if(map.containsKey(key)) {
				updateRecency(key, val);
			} else {
				if(map.size()==capacity) {
					removeOldest();
				}
				Node node = new Node(key,val);
				tail.right=node;node.left=tail;tail=node;
				map.put(key, node);
			}
		}
	}

	@Override
	public void delete(int key) {
		if(map.containsKey(key)) {
			Node node = map.get(key);
			if(node==head) {
				head=node.right;
				if(head==null)tail=null;
				else head.left=null;
			} else if(node == tail){
				tail=tail.left;
				if(tail!=null)tail.right=null;
				else head=null;
			} else {
				Node l = node.left;
				Node r = node.right;
				l.right=r;r.left=l;
			}
			map.remove(key);
		}
	}
	
}

class LRUCacheFromInBuilt implements LRUCache {
	
	LinkedHashMap<Integer, Integer> cache;
	int capacity;
	
	public LRUCacheFromInBuilt(int cap) {
		cache = new LinkedHashMap<Integer, Integer>();
		capacity=cap;
	}
	
	@Override
	public void put(int key, int val) {
		if(cache.containsKey(key)) {
			cache.remove(key);
		} else if(cache.size()==capacity) {
			int oldest = cache.keySet().iterator().next();
			cache.remove(oldest);
		}
		cache.put(key, val);
	}

	@Override
	public int get(int key) {
		if(!cache.containsKey(key)) return -1;
		int val = cache.get(key);
		cache.remove(key);
		cache.put(key, val);
		return val;
	}

	@Override
	public void delete(int key) {
		if(cache.containsKey(key)) {
			cache.remove(key);
		}
	}
}

class LRUCacheFactory {
	
	LRUCache buildLruCache(String param, int capacity) {
		switch(param) {
		case "inbuilt":
			return new LRUCacheFromInBuilt(capacity);
		case "scratch":
			return new LRUCacheFromScratch(capacity);
		default:
			return new LRUCacheFromInBuilt(capacity);
		}
	}
	
}

class Cache {
	LRUCache cache;
	Cache(String param, int capacity) {
		LRUCacheFactory factory = new LRUCacheFactory();
		cache = factory.buildLruCache(param, capacity);
	}
	
	void put(int k, int v) {
		cache.put(k, v);
	}
	
	int get(int k) {
		return cache.get(k);
	}
	
	void delete(int k) {
		cache.delete(k);
	}
	
}


package design;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.*;
import org.junit.jupiter.api.*;

class DesignTest {
	
	
	@Test
	public void testInbuilt() throws Exception {
		Cache cache = new Cache("inbuilt", 2);
		cache.put(1, 2);
		cache.put(1, 3);
		assertEquals(3,cache.get(1));
		cache.put(1, 1);
		assertEquals(1,cache.get(1));
		cache.put(2, 3);
		cache.put(2, 2);
		assertEquals(1,cache.get(1));
		assertEquals(2,cache.get(2));
		cache.put(3, 3);
		assertEquals(-1,cache.get(1));
		assertEquals(2,cache.get(2));
		assertEquals(3,cache.get(3));
		cache.delete(3);
		assertEquals(-1,cache.get(3));
	}
	
	@Test
	public void testFromScratch() throws Exception {
		Cache cache = new Cache("scratch", 2);
		cache.put(1, 2);
		cache.put(1, 3);
		assertEquals(3,cache.get(1));
		cache.put(1, 1);
		assertEquals(1,cache.get(1));
		cache.put(2, 3);
		cache.put(2, 2);
		assertEquals(1,cache.get(1));
		assertEquals(2,cache.get(2));
		cache.put(3, 3);
		assertEquals(-1,cache.get(1));
		assertEquals(2,cache.get(2));
		assertEquals(3,cache.get(3));
		cache.delete(3);
		assertEquals(-1,cache.get(3));
	}
	
}
