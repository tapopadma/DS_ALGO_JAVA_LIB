package ds.algo.java.lib.datastrucutres.trees;

public class BinarySearchTree {

	class Node {
		Node left;
		Node right;
		int key;
		int value;
		public Node(Node left, Node right, int key, int value) {
			this.left = left;
			this.right = right;
			this.key = key;
			this.value = value;
		}
		
	}
	
	Node root;
	boolean isBalanced;
	
	public BinarySearchTree() {
		root = null;
		isBalanced = false;
	}
	
	public BinarySearchTree(boolean isBalanced) {
		this.isBalanced = isBalanced;
	}
	
	Node insert(Node cur, int key, int value) {
		if(cur == null) {
			return new Node(null, null, key, value);
		} else {
			if(key == cur.key) {
				cur.value = value;
			}
			else if(key < cur.key) {
				cur.left = insert(cur.left, key, value);
			} else {
				cur.right = insert(cur.right, key, value);
			}
			return cur;
		}
	}
	
	public void put(int key, int value) {
		root = insert(root, key, value);
	}
	
	public void clear() {
		root = null;
	}
	
	Node search(Node cur, int key) {
		if(cur == null) {
			return null;
		}
		if(key == cur.key) {
			return cur;
		}
		if(key < cur.key) {
			return search(cur.left, key);			
		} else {
			return search(cur.right, key);
		}
	}
	
	public int get(int key) {
		Node result = search(root, key);
		return (result == null) ? -1 : result.value;
	}
	
	Node delete(Node cur, int key) {
		if(cur == null) {
			return null;
		}
		if(cur.key == key) {
			Node prev = null;
			Node prevParent = null;
			if(cur.left != null) {
				Node ptr = cur.left;
				while(ptr.right != null) {
					prevParent = ptr;
					ptr = ptr.right;
				}
				prev = ptr;
			}
			if(prev == null) {
				return cur.right;
			}
			if(prevParent != null) {
				prevParent.right = prev.left;
				prev.left = cur.left;
				prev.right = cur.right;
				return prev;
			}
			prev.right = cur.right;
			return prev;
		}
		if(key < cur.key) {
			cur.left = delete(cur.left, key);
			return cur;
		} else {
			cur.right = delete(cur.right, key);
			return cur;
		}
	}
	
	public void remove(int key) {
		root = delete(root, key);
	}
	
	public boolean containsKey(int key) {
		return search(root, key) != null;
	}
	
	void inOrderTraversal(Node cur) {
		if(cur == null) {
			return;
		}
		inOrderTraversal(cur.left);
		System.out.print("(" + cur.key + ", " + cur.value + ")");
		inOrderTraversal(cur.right);
	}
	
	public void inorder() {
		inOrderTraversal(root);
		System.out.println("");
	}
	
}
