package ds.algo.java.lib.datastrucutres.trees;

public class BinarySearchTree extends BinaryTree{

	public BinarySearchTree() {
		root = null;
	}
	
	Node insert(Node cur, int value) {
		if(cur == null) {
			return new Node(null, null, value);
		} else {
			if(value == cur.data) {
				cur.data = value;
			}
			else if(value < cur.data) {
				cur.left = insert(cur.left, value);
			} else {
				cur.right = insert(cur.right, value);
			}
			return cur;
		}
	}
	
	public void put(int value) {
		root = insert(root, value);
	}
	
	Node search(Node cur, int key) {
		if(cur == null) {
			return null;
		}
		if(key == cur.data) {
			return cur;
		}
		if(key < cur.data) {
			return search(cur.left, key);
		} else {
			return search(cur.right, key);
		}
	}
	
	public int get(int key) {
		Node result = search(root, key);
		return (result == null) ? -1 : result.data;
	}
	
	Node delete(Node cur, int key) {
		if(cur == null) {
			return null;
		}
		if(cur.data == key) {
			Node prev = null;
			if(cur.left != null) {
				Node ptr = cur.left;
				while(ptr.right != null) {
					ptr = ptr.right;
				}
				prev = ptr;
			}
			if(prev == null) {
				return cur.right;
			}
			cur.data = prev.data;
			cur.left = delete(cur.left, cur.data);
		}
		if(key < cur.data) {
			cur.left = delete(cur.left, key);
		} else {
			cur.right = delete(cur.right, key);
		}
		return cur;
	}
	
	public void remove(int key) {
		root = delete(root, key);
	}
	
	public boolean containsKey(int key) {
		return search(root, key) != null;
	}
	
}
