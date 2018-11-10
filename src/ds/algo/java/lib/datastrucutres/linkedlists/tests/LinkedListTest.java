package ds.algo.java.lib.datastrucutres.linkedlists.tests;

import ds.algo.java.lib.datastrucutres.linkedlists.LinkedList;

public class LinkedListTest {

	public static void main(String[] args) {
		LinkedList L = new LinkedList();
		L.pushBack(2);
		L.pushFront(1);
		L.pushFront(0);
		L.pushBack(3);
		L.pushBack(4);
		L.print();
		L.insertAt(3, 4);
		L.print();
		L.deleteAt(4);
		L.print();
		System.out.println(L.contains(4));
		System.out.println(L.get(L.size()/2));
		L.reverse();
		L.print();
		L.reverse();
		L.print();
		L.subList(0, 1).print();
		L.subList(3, 4).print();
		System.out.println(L.isPalindrome());
		L.clear();
		L.print();
		L.pushBack(2);
		L.pushFront(4);
		L.pushBack(4);
		System.out.println(L.isPalindrome());
		L.print();
		L.joinTailAt(1);
		System.out.println(L.getLoopStartIndex() + " " + L.getLoopLength());
	}

}
