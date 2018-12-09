package ds.algo.java.lib.datastrucutres.queue.tests;

import ds.algo.java.lib.datastrucutres.queue.LRUCache;

public class LRUCacheTest {

	public static void main(String[] args) {

		LRUCache L = new LRUCache(2);
		L.put("a", 1);
		L.put("b", 'b');
		System.out.println(L.get("b"));
		L.put("c", 9.0);
		System.out.println(L.get("a"));
	}

}
