package ds.algo.java.lib.datastrucutres.queue;

import java.util.HashMap;
import java.util.Map;

import ds.algo.java.lib.datastrucutres.linkedlists.DoublyLinkedList;
import ds.algo.java.lib.datastrucutres.linkedlists.DoublyLinkedList.Node;

/**
 * A DoublyLinkedList containing key,value pairs
 * A HashMap containing key to object values map
 * Every operation in O(1)
 * @author tapopadma
 *
 */
public class LRUCache {

	class Entry {
		public Object value;
		public String key;
		public Entry(String key, Object value) {
			this.value = value;
			this.key = key;
		}
	}
	
	DoublyLinkedList<Entry> values;
	
	Map<String, Node> keyToValueMap;
	
	int capacity;
	
	public LRUCache(int capacity) {
		values = new DoublyLinkedList<>();
		keyToValueMap = new HashMap<>();
		this.capacity = capacity;
	}
	
	public Object get(String key) {
		if(keyToValueMap.containsKey(key)) {
			Node node = keyToValueMap.get(key);
			Entry entry = (Entry) node.data;
			values.remove(node);
			keyToValueMap.remove(key);
			keyToValueMap.put(key, values.addFirst(entry));
			return entry.value;
		}
		return null;
	}
	
	public void put(String key, Object value) {
		if(keyToValueMap.containsKey(key)) {
			values.remove(keyToValueMap.get(key));
			keyToValueMap.remove(key);
		}
		if(values.size() == this.capacity) {
			Node node = values.removeLast();
			Entry entry = (Entry) node.data;
			keyToValueMap.remove(entry.key);
		}
		keyToValueMap.put(key, values.addFirst(
				new Entry(key, value)));
	}
	
}
