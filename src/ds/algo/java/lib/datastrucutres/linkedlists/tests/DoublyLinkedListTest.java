package ds.algo.java.lib.datastrucutres.linkedlists.tests;

import ds.algo.java.lib.datastrucutres.linkedlists.DoublyLinkedList;

public class DoublyLinkedListTest {

	public static void main(String[] args) {
		DoublyLinkedList L = new DoublyLinkedList();
		L.insert(0, 1);
		L.insert(0, 2);
		L.insert(1, 3);
		L.insert(2, 5);
		L.insert(2, 4);
		L.print();
	}

}
