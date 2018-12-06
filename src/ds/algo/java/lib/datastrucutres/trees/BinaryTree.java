package ds.algo.java.lib.datastrucutres.trees;

import ds.algo.java.lib.datastrucutres.linkedlists.DoublyLinkedList;

/**
 * For simplicity avoid duplication of keys
 * @author tapopadma
 *
 */
public class BinaryTree {

	protected class Node {
		Node left;
		Node right;
		int data;
		public Node(Node left, Node right, int value) {
			this.left = left;
			this.right = right;
			this.data = value;
		}
		
	}
	
	protected Node root;
	
	public BinaryTree() {
		root = null;
	}
	
	/**
	 * Builds binary tree from postfix using Stack
	 * @param postFix
	 */
	public BinaryTree buildFromPostFix(String postFix) {
		return this;
	}
	
	public void clear() {
		root = null;
	}
	
	public void inOrderTraversal(Node cur) {
		if(cur == null) {
			return;
		}
		inOrderTraversal(cur.left);
		System.out.print(cur.data + ", ");
		inOrderTraversal(cur.right);
	}
	
	public void inorder() {
		inOrderTraversal(root);
		System.out.println("");
	}
	
	public void insert(int data) {
		if(root == null) {
			root = new Node(null, null, data);
		}
	}
	
	public void insert(int target, boolean isLeft, int data) {
		root = insert(root, target, isLeft, data);
	}
	
	Node insert(Node node, int target, boolean isLeft, int data) {
		if(node == null) {
			return null;
		}
		if(target == node.data) {
			if(isLeft) {
				node.left = new Node(null, null, data);
			} else {
				node.right = new Node(null, null, data);
			}
		} else {
			node.left = insert(node.left, target, isLeft, data);
			node.right = insert(node.right, target, isLeft, data);
		}
		return node;
	}
	
	public DoublyLinkedList convertToDoublyLinkedList() {
		return convertToDoublyLinkedList(root);
	}
	
	DoublyLinkedList convertToDoublyLinkedList(Node node) {
		DoublyLinkedList L = new DoublyLinkedList();
		if(node == null) {
			return L;
		}
		L.addAll(convertToDoublyLinkedList(node.left));
		L.addLast(node.data);
		L.addAll(convertToDoublyLinkedList(node.right));			
		return L;
	}	
	
}
