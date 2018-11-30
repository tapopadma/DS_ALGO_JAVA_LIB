package ds.algo.java.lib.datastrucutres.trees;

/**
 * A balanced BST with O(logN) insert/delete functionality
 * Reference:  https://drive.google.com/open?id=1FKUtQgNVlPyC3tut4YSbkCC65F2qk-mk
 * @author tapopadma
 *
 */
public class AVLTree{
	class Node {
		long data;
		int freq;
		int height;
		Node left;
		Node right;
		public Node(long data) {
			this.data = data;
			this.freq = 1;
			this.height = 0;
			this.left = null;
			this.right = null;
		}
	}
	Node root;
	public void add(long value) {
		root = insert(root, value);
	}
	
	Node insert(Node node, long value) {
		if(node == null) {
			node = new Node(value);
			return node;
		}
		if(node.data == value) {
			++node.freq;
			return node;
		}
		if(node.data > value) {
			node.left = insert(node.left, value);
		} else {
			node.right = insert(node.right, value);
		}
		node = updateHeight(node);
		node = balance(node);
		return node;
	}
	
	Node updateHeight(Node node) {
		if(node == null) {
			return null;
		}
		int height = 0;
		if(node.left != null) {
			height = Math.max(height, node.left.height + 1);
		}
		if(node.right != null) {
			height = Math.max(height, node.right.height + 1);
		}
		node.height = height;
		return node;
	}
	
	public void remove(long value) {
		root = delete(root, value);
	}
	
	Node delete(Node node, long value) {
		if(node == null) {
			return null;
		}
		if(node.data == value) {
			if(node.freq == 1) {
				if(node.left == null && node.right == null) {
					return null;
				}
				if(node.left == null) {
					return node.right;
				}
				if(node.right == null) {
					return node.left;
				}
				Node ptr = node.left;
				while(ptr.right != null) {
					ptr = ptr.right;
				}
				node.data = ptr.data;
				node.left = delete(node.left, ptr.data);
			} else {
				node.freq = node.freq - 1;
			}
		}
		else if(node.data > value) {
			node.left = delete(node.left, value);
		} else {
			node.right = delete(node.right, value);
		}
		node = updateHeight(node);
		node = balance(node);
		return node;
	}
	
	boolean isLeaf(Node node) {
		return node != null && node.left == null && node.right == null;
	}
	
	public long getMax() {
		Node ptr = root;
		while(ptr.right != null) {
			ptr = ptr.right;
		}
		return ptr.data;
	}
	
	int getLeftHeight(Node node) {
		if(node == null) {
			return 0;
		}
		return (node.left == null ? 0 : node.left.height + 1);
	}
	
	int getRightHeight(Node node) {
		if(node == null) {
			return 0;
		}
		return (node.right == null ? 0 : node.right.height + 1);
	}
	
	Node balance(Node node) {
		if(node == null) {
			return null;
		}
		int lh = getLeftHeight(node);
		int rh = getRightHeight(node);
		if(Math.abs(lh-rh) < 2) {
			return node;
		}
		if(lh > rh) {
			Node lnode = node.left;
			int llh = getLeftHeight(lnode);
			int lrh = getRightHeight(lnode);
			if(llh > lrh) {//LL
				node.left = lnode.right;
				lnode.right = node;
				node = lnode;
				node.right = updateHeight(node.right);
				node = updateHeight(node);
			} else {//LR
				Node lrnode = lnode.right;
				lnode.right = lrnode.left;
				lrnode.left = lnode;
				node.left = lrnode;
				node.left.left = updateHeight(node.left.left);
				node.left = updateHeight(node.left);
				node = updateHeight(node);
				node = balance(node);	
			}
		} else {
			Node rnode = node.right;
			int rlh = getLeftHeight(rnode);
			int rrh = getRightHeight(rnode);
			if(rrh > rlh) {//RR
				node.right = rnode.left;
				rnode.left = node;
				node = rnode;
				node.left = updateHeight(node.left);
				node = updateHeight(node);
			} else {
				Node rlnode = rnode.left;
				rnode.left = rlnode.right;
				rlnode.right = rnode;
				node.right = rlnode;
				node.right.right = updateHeight(node.right.right);
				node.right = updateHeight(node.right);
				node = updateHeight(node);
				node = balance(node);
			}
		}
		return node;
	}
	
	public void print() {
		inorder(root);System.out.println("");
	}
	
	public void inorder(Node node) {
		if(node == null) {
			return;
		}
		inorder(node.left);
		System.out.print("("+node.data+"[" + node.freq+"])");
		inorder(node.right);
	}
	
	public void clear() {
		 this.root = null;
	}
	
	public int getDepth() {
		return root == null ? 0 : root.height;
	}
	
}
