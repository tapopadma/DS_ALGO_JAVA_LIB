package ds.algo.java.lib.datastrucutres.trees.tests;

import java.io.PrintStream;

import ds.algo.java.lib.datastrucutres.trees.BinarySearchTree;

public class BinarySearchTreeTest {

	static PrintStream out = System.out;
	
	public static void main(String[] args) {
		BinarySearchTree tree = new BinarySearchTree();
		tree.put(23);tree.inorder();
		tree.put(12);
		out.println(tree.get(12));
		out.println(tree.containsKey(12));
		tree.inorder();
		tree.put(12);
		tree.put(8);
		tree.put(99);
		tree.inorder();
		tree.clear();
		tree.put(8);
		tree.put(5);
		tree.put(5);
		tree.inorder();
		tree.remove(8);
		tree.inorder();
		tree.clear();
		tree.put(23);
		tree.put(10);
		tree.put(100);
		tree.put(1);
		tree.put(15);
		tree.put(14);
		tree.put(19);
		tree.put(17);
		tree.inorder();
		tree.remove(23);tree.inorder();
		tree.remove(100);tree.inorder();
		tree.remove(15);tree.inorder();
		tree.remove(14);tree.inorder();
		tree.remove(1);tree.inorder();
		tree.remove(10);tree.inorder();
		tree.remove(17);tree.inorder();
		tree.remove(19);tree.inorder();
		tree.clear();
	}

}
