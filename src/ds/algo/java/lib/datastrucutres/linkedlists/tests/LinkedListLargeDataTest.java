package ds.algo.java.lib.datastrucutres.linkedlists.tests;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import ds.algo.java.lib.datastrucutres.linkedlists.LinkedList;

public class LinkedListLargeDataTest {

	static int NN = 10000;
	
	public static void main(String[] args) {
		LinkedList L = new LinkedList();
		Random rand = ThreadLocalRandom.current();
		int [] a = new int[NN];
		for(int i=0;i<NN;++i) {
			a[i] = i + 1;
			int j = rand.nextInt(i + 1);
			int t = a[i];a[i] = a[j];a[j] = t;
		}
		for(int i=0;i<NN;++i) {
			L.pushBack(a[i]);
		}
		System.out.println("BEFORE:");
		L.print();
		L = L.sort();
		System.out.println("AFTER:");
		L.print();
		System.out.println(L.isSorted());
	}

}
