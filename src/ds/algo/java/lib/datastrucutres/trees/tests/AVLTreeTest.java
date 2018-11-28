package ds.algo.java.lib.datastrucutres.trees.tests;

import ds.algo.java.lib.datastrucutres.trees.AVLTree;

public class AVLTreeTest {

	public static void main(String[] args) {
		AVLTree avl = new AVLTree();
		// 3 3 1 2 1 3
		avl.add(3);avl.add(3);avl.add(1);avl.add(2);avl.add(1);avl.add(3);
		avl.print();
		avl.remove(3);avl.remove(3);avl.remove(3);avl.remove(2);
		avl.print();
		avl.clear();avl.print();
		avl.add(10);avl.add(10);
		avl.add(20);avl.add(20);
		avl.add(30);avl.add(30);
		avl.add(5);
		avl.add(15);
		avl.add(12);
		avl.add(30);
		avl.add(18);
		avl.add(16);
		avl.add(50);
		avl.print();
		avl.remove(18);
		avl.print();
		System.out.println(avl.getMax());
		System.out.println(avl.getDepth());
		avl.remove(20);avl.remove(20);avl.remove(20);
		avl.remove(30);avl.remove(30);avl.remove(30);
		avl.print();
	}

}
