package ds.algo.java.lib.datastrucutres.trees.tests;

import java.io.PrintStream;

import ds.algo.java.lib.datastrucutres.trees.BinarySearchTree;

public class BinarySearchTreeTest {

	static PrintStream out = System.out;
	
	public static void main(String[] args) {
		BinarySearchTree tree = new BinarySearchTree();
		tree.put(23, 1);tree.inorder();
		tree.put(12, 2);
		out.println(tree.get(12));
		out.println(tree.containsKey(12));
		tree.inorder();
		tree.put(12, 9);
		tree.put(8, 7);
		tree.put(99, 0);
		tree.inorder();
		tree.clear();
		tree.put(8, 8);
		tree.put(5, 5);
		tree.put(5, 57);
		tree.inorder();
		tree.remove(8);
		tree.inorder();
		tree.clear();
		tree.put(23, 21);
		tree.put(10, 9);
		tree.put(100, 99);
		tree.put(1, 9);
		tree.put(15, 2);
		tree.put(14, 99);
		tree.put(19, 9);
		tree.put(17, 17);
		tree.inorder();
		tree.remove(23);tree.inorder();
		tree.remove(100);tree.inorder();
		tree.remove(15);tree.inorder();
		tree.remove(14);tree.inorder();
		tree.remove(1);tree.inorder();
		tree.remove(10);tree.inorder();
		tree.remove(17);tree.inorder();
		tree.remove(19);tree.inorder();
	}

}
