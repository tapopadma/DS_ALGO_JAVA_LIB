package ds.algo.java.lib.datastrucutres.trees.tests;

import java.io.PrintStream;
import java.util.Arrays;
import ds.algo.java.lib.datastrucutres.trees.BinarySearchTree;
import ds.algo.java.lib.datastrucutres.linkedlists.SinglyLinkedList;

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
		SinglyLinkedList l = new SinglyLinkedList();
		l.pushBack(1);l.pushBack(2);l.pushBack(3);l.pushBack(4);l.pushBack(5);l.pushBack(6);l.pushBack(7);
		tree.convertFromLinkedList(l).preOrder();
		tree.clear(); l.clear();
		l.pushBack(1);l.pushBack(2);l.pushBack(3);l.pushBack(4);l.pushBack(5);l.pushBack(6);
		tree.convertFromLinkedList(l).preOrder();
		tree.clear();
		tree.convertFromLevelOrder(new int[]{7, 4, 12, 3, 6, 8, 1, 5, 10}).preOrder();
		tree.lcaNonPreprocessed(Arrays.asList(Arrays.asList(6, 8), Arrays.asList(1, 5)));
		tree.clear();
		tree.put(10);tree.put(5);tree.root.addRight(8);tree.put(2);tree.root.left.addRight(20);
		tree.fixTwoSwappedNodesAndRecoverBST().preOrder();
		tree.clear();
		tree.put(3);tree.put(1);tree.put(5);
		BinarySearchTree tree1 = new BinarySearchTree();
		tree1.put(4);tree1.put(2);tree1.put(6);
		tree.merge(tree1);
		tree.clear();
		tree.put(8);tree.put(2);tree.put(10);tree.put(1);
		tree1.clear();
		tree1.put(5);tree1.put(3);tree1.put(0);
		tree.merge(tree1);
		tree.clear();
		tree.put(10);tree.put(5);tree.put(8);tree.put(2);tree.put(20);
		tree.convertFromDoublyLinkedList(tree.convertToDoublyLinkedList()).preOrder();
	}

}
