package ds.algo.java.lib.datastrucutres.trees;

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
		balance();
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
		balance();
	}
	
	Node delete(Node node, long value) {
		if(node == null) {
			return null;
		}
		if(node.data == value) {
			--node.freq;
			if(node.freq == 0) {
				if(node.left == null && node.right == null) {
					return null;
				}
				if(node.left == null) {
					return node.right;
				}
				if(node.right == null) {
					return node.left;
				}
				if(node.left.right == null) {
					node.left.right = node.right;
					node.left = updateHeight(node.left);
					return node.left;
				}
				Node ptr1 = node.left;
				Node ptr2 = ptr1;
				while(ptr2.right != null) {
					ptr1 = ptr2;
					ptr2 = ptr2.right;
				}
				ptr1.right = null;
				ptr2.left = node.left;
				ptr2.right = node.right;
				ptr2.left = updateHeightToRightRecursive(ptr2.left);
				ptr2 = updateHeight(ptr2);
				return ptr2;
			}
			return node;
		}
		if(node.data > value) {
			node.left = delete(node.left, value);
		} else {
			node.right = delete(node.right, value);
		}
		node = updateHeight(node);
		return node;
	}
	
	Node updateHeightToRightRecursive(Node node) {
		if(node == null) {
			return null;
		}
		node.right = updateHeightToRightRecursive(node.right);
		node = updateHeight(node);
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
	
	void balance() {
		if(root == null) {
			return;
		}
		int lh = (root.left == null ? 0 : root.left.height);
		int rh = (root.right == null ? 0 : root.right.height);
		if(Math.abs(lh-rh) < 2) {
			return;
		}
		if(lh > rh) {
			Node lroot = root.left;
			int llh = (lroot.left == null ? 0 : lroot.left.height);
			int lrh = (lroot.right == null ? 0 : lroot.right.height);
			if(llh > lrh) {//LL
				root.left = lroot.right;
				lroot.right = root;
				root = lroot;
				root.right = updateHeight(root.right);
				root = updateHeight(root);
				
			} else {//LR
				Node lrroot = lroot.right;
				lroot.right = lrroot.left;
				lrroot.left = lroot;
				root.left = lrroot;
				root.left.left = updateHeight(root.left.left);
				root.left = updateHeight(root.left);
				root = updateHeight(root);
				balance();	
			}
		} else {
			Node rroot = root.right;
			int rlh = (rroot.left == null ? 0 : rroot.left.height);
			int rrh = (rroot.right == null ? 0 : rroot.right.height);
			if(rrh > rlh) {//RR
				root.right = rroot.left;
				rroot.left = root;
				root = rroot;
				root.left = updateHeight(root.left);
				root = updateHeight(root);
			} else {
				Node rlroot = rroot.left;
				rroot.left = rlroot.right;
				rlroot.right = rroot;
				root.right = rlroot;
				root.right.right = updateHeight(root.right.right);
				root.right = updateHeight(root.right);
				root = updateHeight(root);
				balance();
			}
		}
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
