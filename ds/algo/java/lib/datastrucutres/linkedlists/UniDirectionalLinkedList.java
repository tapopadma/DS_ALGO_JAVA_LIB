package ds.algo.java.lib.datastrucutres.linkedlists;

public abstract class UniDirectionalLinkedList {

	public static class Node {
		public int data;
		public Node next;
		public Node() {

		}
		public Node(int data) {
			this.data = data;
			this.next = null;
		}
		public Node(int data, Node next) {
			this.data = data;
			this.next = next;
		}
	}
	
	protected Node head;
	protected int length;
	
	public UniDirectionalLinkedList() {
		head = null;
		length = 0;
	}
	
	public UniDirectionalLinkedList(Node head, int length) {
		this.head = head;
		this.length = length;
	}
	
	public int size() {
		return length;
	}
	
	public abstract void print();
	
	public abstract Node getNodeAt(int index);
	
	public int get(int index) {
		return getNodeAt(index).data;
	}
	
	public abstract void insertAt(int index, int value);
	
	public abstract void deleteAt(int index);
	
	public abstract boolean contains(int value);
	
	public void clear()  {
		head = null;length = 0;
	}
	
}
