package ds.algo.java.lib.datastrucutres.linkedlists.tests;

import ds.algo.java.lib.datastrucutres.linkedlists.DoublyLinkedList;

public class DoublyLinkedListTest {

	public static void main(String[] args) {
		DoublyLinkedList<Integer> L = new DoublyLinkedList<>();
		L.insert(0, 1);
		L.insert(0, 2);
		L.insert(1, 3);
		L.insert(2, 5);
		L.insert(2, 4);
		L.print();
		L.reverse().print();
		L.addFirst(6);
		L.addLast(0);
		L.reverse().print();
		L.removeFirst();L.print();
		L.removeAt(4);L.print();
		L.removeLast();L.print();
		L.reverse().print();
		L.clear();
		L.addLast(1);L.addLast(2);L.addLast(5);L.addLast(8);
		DoublyLinkedList<Integer> L1 = new DoublyLinkedList<>();
		L1.addLast(3);L1.addLast(4);L1.addLast(6);L1.addLast(7);
		L = L.merge(L1);
		L.print();
	}

}
