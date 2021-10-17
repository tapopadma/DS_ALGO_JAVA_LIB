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
	
	public void preOrder() {
		preOrderTraversal(root);
	}
	
	void preOrderTraversal(Node node) {
		if(node == null) {
			return;
		}
		System.out.print(node.data + " ");
		preOrderTraversal(node.left);
		preOrderTraversal(node.right);
	}
	
	public void postOrder() {
		postOrderTraversal(root);System.out.println("");
	}
	
	void postOrderTraversal(Node node) {
		if(node == null) {
			return;
		}
		postOrderTraversal(node.left);
		postOrderTraversal(node.right);
		System.out.print(node.data + " ");
	}
	
	public BinaryTree buildTreeFromInPreOrder(int [] in, int [] pre) {
		int n = in.length;
		int [] positionInorder = new int[100000];
		for(int i=0;i<n;++i) {
			positionInorder[in[i]] = i;
		}
		root = (Node)buildTreeFromInPreOrder(
				0, 0, n - 1, pre, n, positionInorder)[0];
		return this;
	}
	
	Object[] buildTreeFromInPreOrder(int it, int l, int r, 
			int [] pre, int n, int [] positionInorder) {
		if(it >= n) {
			return new Object[] {null, it};
		}
		int node = pre[it];
		Node newNode = null;
		if(positionInorder[node] >= l && positionInorder[node] <= r) {
			newNode = new Node(null, null, node);
			Object [] res = buildTreeFromInPreOrder(
					it+1, l, positionInorder[node] - 1,
					pre, n, positionInorder);
			newNode.left = (Node)res[0];
			res = buildTreeFromInPreOrder(
					(int)res[1], positionInorder[node] + 1, r,
					pre, n, positionInorder);
			newNode.right = (Node)res[0];
			return new Object[] {newNode, (int)res[1]};
		} else {
			return new Object [] {null, it};
		}
	}	
	
	boolean isSubTree(Node node1, Node node2, boolean matched) {
		if(node2 == null) {
			return true;
		}
		if(node1 == null) {
			return false;
		}
		if(matched) {
			if(node1.data != node2.data) {
				return false;
			}
			return isSubTree(node1.left, node2.left, matched)
					&& isSubTree(node1.right, node2.right, matched);
			
		} else {
			if(node1.data != node2.data) {
				return isSubTree(node1.left, node2, false) ||
						isSubTree(node1.right, node2, false);
			} else {
				return isSubTree(node1.left, node2.left, true)
						&& isSubTree(node1.right, node2.right, true);
			}
		}
	}
	
	public boolean isSubTree(BinaryTree T) {
		boolean status = isSubTree(root, T.root, false);
		if(status) {
			System.out.println("YES");
		} else {
			System.out.println("NO");
		}
		return status;
	}
	
	int size(Node node) {
		if(node == null) {
			return 0;
		}
		return 1 + size(node.left) + size(node.right);
	}
	
	public int size() {
		return size(root);
	}
	
	void dfs(Node cur, Node prev, int [][] pa, int [] depth) {
		if(cur == null) {
			return;
		}
		if(prev == null) {
			pa[cur.data][0] = -1;
			depth[cur.data] = 0;
		} else {
			pa[cur.data][0] = prev.data;
			depth[cur.data] = depth[prev.data] + 1;
		}
		dfs(cur.left, cur, pa, depth);
		dfs(cur.right, cur, pa, depth);
	}
	
	/**
	 * This method assumes the tree has nodes with data [1, n]
	 * @param data1
	 * @param data2
	 * @return
	 */
	public int LCA(int data1, int data2) {
		int n = size();
		int [][] pa = new int[n + 1][30];
		int [] depth = new int[n + 1];
		dfs(root, null, pa, depth);
		for(int j=1;j<30;++j) {
			for(int i=1;i<=n;++i) {
				if(pa[i][j - 1] != -1) {
					pa[i][j] = pa[pa[i][j - 1]][j - 1];					
				} else {
					pa[i][j] = -1;
				}
			}
		}
		if(depth[data1] > depth[data2]) {
			int temp = data1;data1 = data2;data2 = temp;
		}
		for(int j=29;j>=0;--j) {
			if(pa[data2][j] != -1 && depth[pa[data2][j]] >= depth[data1]) {
				data2 = pa[data2][j];
			}
		}
		if(data1 == data2) {
			return data1;
		}
		for(int j=29;j>=0;--j) {
			if(pa[data1][j] != -1 && pa[data1][j] != pa[data2][j]) {
				data1 = pa[data1][j];
				data2 = pa[data2][j];
			}
		}
		return pa[data1][0];
	}
	
}
