package ds.algo.java.lib.datastrucutres.linkedlists;

public abstract class UniDirectionalLinkedList {

	protected class Node {
		int data;
		Node next;
		protected Node() {

		}
		protected Node(int data) {
			this.data = data;
			this.next = null;
		}
		protected Node(int data, Node next) {
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
