package ds.algo.java.lib.datastrucutres.linkedlists;

import ds.algo.java.lib.datastrucutres.linkedlists.LinkedList.Node;

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
	
	public void print() {
		for(Node ptr = head;ptr != null;ptr=ptr.next) {
			System.out.print(ptr.data+"<->");
		}
		System.out.println("");
	}
	
}
