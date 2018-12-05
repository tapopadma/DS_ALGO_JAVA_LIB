package ds.algo.java.lib.datastrucutres.linkedlists;

public class DoublyLinkedList {

	class Node{
		int data;
		Node prev;
		Node next;
		public Node(int data){
			this.data = data;
			this.prev = this.next = null;
		}
		public Node(int data, Node prev, Node next) {
			this.data = data;
			this.prev = prev;
			this.next = next;
		}
	}
	
	Node head;
	int length;
	
	public DoublyLinkedList() {
		head = null;
		length = 0;
	}
	
	public void insert(int position, int data) {
		if(length == 0) {
			head = new Node(data);
		} else {
			Node ptr = getNodeAt(position);
			if(ptr == null) {
				return;
			}
			Node ptrNext = ptr.next;
			Node node = new Node(data, ptr, ptrNext);
			ptr.next = node;
			if(ptrNext != null) {
				ptrNext.prev = node;			
			}
		}
		++length;
	}
	
	public void addLast(int data) {
		if(size() == 0) {
			head = new Node(data);
			++length;
			return;
		}
		Node tail = getNodeAt(length - 1);
		Node node = new Node(data, tail, null);
		tail.next = node;
		tail = node;
		++length;
	}
	
	public void addAll(DoublyLinkedList L) {
		if(size() == 0) {
			this.head = L.getNodeAt(0);
			this.length = L.size();
			return;
		}
		Node tail = getNodeAt(size() - 1);
		if(L.size() > 0) {
			tail.next = L.getNodeAt(0);
		}
		this.length += L.size();
	}
	
	public void addFirst(int data) {
		Node node = new Node(data, null, head);
		head.prev = node;
		head = node;
		++length;
	}

	public void removeLast() {
		Node tail = getNodeAt(length - 1);
		--length;
		if(head == tail) {
			head = null;
			return;
		}
		tail = tail.prev;
		tail.next = null;
	}
	
	public void removeFirst() {
		--length;
		if(head.next == null) {
			head = null;return;
		}
		head.next.prev = null;
		head = head.next;
	}
	
	public void removeAt(int index) {
		if(index == 0) {
			removeFirst();return;
		}
		if(index == length - 1) {
			removeLast();return;
		}
		Node node = getNodeAt(index);
		if(node == null) {
			return;
		}
		node.prev.next = node.next;
		if(node.next != null) {
			node.next.prev = node.prev;
		}
		--length;
	}
	
	public int get(int index) {
		return getNodeAt(index).data;
	}
	
	public Node getNodeAt(int index) {
		if(index >= length)
			return null;
		int idx = 0;
		Node ptr = head;
		while(idx < index) {
			idx++; ptr = ptr.next;
		}
		return ptr;
	}
	
	Node reverse(Node node) {
		if(node == null) {
			return null;
		}
		Node nextNode = reverse(node.next);
		if(nextNode == null) {
			head = node;
			return node;
		}
		nextNode.next = node;
		node.prev = nextNode;
		node.next = null;
		return node;
	}
	
	public DoublyLinkedList reverse() {
		reverse(head);
		return this;
	}
	
	public void print() {
		for(Node ptr = head;ptr != null;ptr=ptr.next) {
			System.out.print(ptr.data+"<->");
		}
		System.out.println("");
	}
	
	public int size() {
		return length;
	}
	
}
