package ds.algo.java.lib.datastrucutres.linkedlists;

public class LinkedList {

	class Node {
		int key;
		Node next;
		public Node() {

		}
		public Node(int key) {
			this.key = key;
			this.next = null;
		}
		public Node(int key, Node next) {
			this.key = key;
			this.next = next;
		}
	}

	Node head;
	int length;
	
	public LinkedList() {
		head = null;
		length = 0;
	}

	public int size() {
		return length;
	}
	
	public void print() {
		for(Node ptr = head;ptr != null;ptr=ptr.next) {
			System.out.println(ptr.key);
		}
	}
	
	//O(N)
	public void pushBack(int value) {
		if(head == null) {
			head = new Node(value);
		} else {
			Node ptr = head;
			while(ptr.next != null) {
				ptr = ptr.next;
			}
			ptr.next = new Node(value);
		}
	}
	
	//O(1)	
	public void pushFront(int value) {
		Node oldHead = head;
		head = new Node(value, oldHead);
	}
	
	//O(N)
	public int get(int index) {
		if(index >= length)
			return -1;
		int idx = 0;
		Node ptr = head;
		while(idx < index) {
			idx++; ptr = ptr.next;
		}
		return ptr.key;
	}
	
	//O(N)
	public void insertAt(int index, int value) {
		if(index >= length) {
			return;
		}
		int idx = 0;
		Node ptr = head;
		while(idx < index) {
			++idx;
			ptr = ptr.next;
		}
		Node oldNext = ptr.next;
		ptr.next = new Node(value, oldNext);
	}
	
	//O(N)
	public void deleteAt(int index) {
		if(index >= length){
			return;
		}
		if(index == 0) {
			head = head.next;
		} else {
			int idx = 0;
			Node ptr = head;
			while(idx + 1 < index) {
				++idx;
				ptr = ptr.next;
			}
			ptr.next = ptr.next.next;
		}
	}
	
	//O(N)
	public boolean contains(int value) {
		for(Node ptr=head;ptr!=null;ptr=ptr.next) {
			if(ptr.key == value) {
				return true;
			}
		}
		return false;
	}
	
	//O(N)
	public boolean hasLoop() {
		if(length == 0) {
			return false;
		}
		return true;
	}
	
}
