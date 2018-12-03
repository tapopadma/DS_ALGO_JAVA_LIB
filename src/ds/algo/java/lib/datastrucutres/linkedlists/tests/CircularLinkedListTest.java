package ds.algo.java.lib.datastrucutres.linkedlists.tests;

import ds.algo.java.lib.datastrucutres.linkedlists.CircularLinkedList;

public class CircularLinkedListTest {

	public static void main(String[] args) {
		CircularLinkedList L = new CircularLinkedList();
		L.insertAt(-1, 1);
		L.insertAt(0, 2);
		L.print();L.clear();L.print();
		L.insertAt(0, 1);
		L.insertAt(0, 2);
		L.insertAt(0, 3);
		L.print();
		L.deleteAt(2);
		L.print();
	}

}
