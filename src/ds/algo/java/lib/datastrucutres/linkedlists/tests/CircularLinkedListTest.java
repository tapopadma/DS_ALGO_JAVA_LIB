package ds.algo.java.lib.datastrucutres.linkedlists.tests;

import java.util.Arrays;

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
		L.clear();
		L.insertAt(0, 1);
		L.insertAt(0, 2);
		Arrays.asList(L.splitToHalves()).stream().forEach(
				list -> {
					System.out.println("====");
					list.print();					
				}
				);
		L.clear();
		L.insertAt(0, 1);
		L.insertAt(0, 2);
		L.insertAt(1, 3);
		L.insertAt(2, 4);
		Arrays.asList(L.splitToHalves()).stream().forEach(
				list -> {
					System.out.println("====");
					list.print();					
				}
				);
	}

}
