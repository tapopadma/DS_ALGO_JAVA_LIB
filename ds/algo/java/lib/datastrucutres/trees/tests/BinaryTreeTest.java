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
		T.clear();
		T.buildTreeFromInPreOrder(
				new int[] {2, 1, 7, 5, 9, 10, 8, 4, 6, 3},
				new int[] {1, 2, 3, 4, 5, 7, 8, 9, 10, 6}
				).postOrder();
		BinaryTree T1 = new BinaryTree();
		T1.buildTreeFromInPreOrder(
				new int[] {7, 5, 9, 8, 4},
				new int[] {4, 5, 7, 8, 9}
				).postOrder();
		T.isSubTree(T1);
		System.out.println(T.LCA(6, 10));
	}

}
