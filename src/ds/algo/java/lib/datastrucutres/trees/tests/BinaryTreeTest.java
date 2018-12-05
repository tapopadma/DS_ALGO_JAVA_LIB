package ds.algo.java.lib.datastrucutres.trees.tests;

import ds.algo.java.lib.datastrucutres.trees.BinaryTree;

public class BinaryTreeTest {

	public static void main(String[] args) {
		BinaryTree T = new BinaryTree();
		T.insert(1);
		T.insert(1, true, 2);
		T.insert(1, false, 3);
		T.insert(3, true, 9);
		T.insert(9, false, 10);
		T.insert(2, true, 5);
		T.insert(2, false, 4);
		T.insert(4, true, 6);
		T.insert(4, false, 7);
		T.insert(7, true, 8);
		T.inorder();
		T.convertToDoublyLinkedList().print();
	}

}
