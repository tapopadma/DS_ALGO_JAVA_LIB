package ds.algo.java.lib.datastrucutres.trees;

import ds.algo.java.lib.datastrucutres.linkedlists.DoublyLinkedList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * For simplicity avoid duplication of keys
 *
 * @author tapopadma
 */
public class BinaryTree {

  public class Node {
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
   *
   * @param postFix
   */
  public BinaryTree buildFromPostFix(String postFix) {
    return this;
  }

  public void clear() {
    root = null;
  }

  void fetchRightView(Node node, int level, int[] levelProcessed, List<Integer> view) {
    if (node == null) {
      return;
    }
    if (levelProcessed[0] < level) {
      levelProcessed[0] = level;
      view.add(node.data);
    }
    fetchRightView(node.right, level + 1, levelProcessed, view);
    fetchRightView(node.left, level + 1, levelProcessed, view);
  }

  // In the level order traversal, fetch first or right most node per level.
  public void fetchRightView() {
    List<Integer> view = new ArrayList<>();
    int[] levelProcessed = new int[] {-1};
    fetchRightView(root, 0, levelProcessed, view);
    for (int i : view) {
      System.out.print(i + " ");
    }
    System.out.println("");
  }

  public void fetchIterativeAncestorList(Node node) {
    Map<Node, Node> parent = new HashMap<>();
    Stack<Node> q = new Stack<>();
    q.push(root);
    while (!q.isEmpty()) {
      Node cur = q.pop();
      if (cur == node) {
        cur = parent.get(cur);
        while (cur != null) {
          System.out.print(cur.data + " ");
          cur = parent.get(cur);
        }
        System.out.println("");
        break;
      }
      if (cur.left != null) {
        parent.put(cur.left, cur);
        q.push(cur.left);
      }
      if (cur.right != null) {
        parent.put(cur.right, cur);
        q.push(cur.right);
      }
    }
  }

  // push current root to stack, pop current root. if the current root's left child
  // exists (i.e. not processed to output yet), add the right child then current root then its left
  // child to the stack, else just add the current root to the output.
  public void iterativeInOrder() {
    List<Integer> l = new ArrayList<>();
    Set<Integer> processed = new HashSet<>();
    Stack<Node> q = new Stack<>();
    q.push(root);
    while (!q.isEmpty()) {
      Node cur = q.pop();
      if (cur.left != null && !processed.contains(cur.left.data)) {
        q.push(cur);
        q.push(cur.left);
      } else {
        l.add(cur.data);
        processed.add(cur.data);
        if (cur.right != null) {
          q.push(cur.right);
        }
      }
    }
    for (int i : l) {
      System.out.print(i + " ");
    }
    System.out.println("");
  }

  public void inOrderTraversal(Node cur) {
    if (cur == null) {
      return;
    }
    inOrderTraversal(cur.left);
    System.out.print(cur.data + " ");
    inOrderTraversal(cur.right);
  }

  public void inorder() {
    inOrderTraversal(root);
    System.out.println("");
  }

  public Node add(Node parent, boolean isLeft, int data) {
    Node node = new Node(null, null, data);
    if (parent == null) {
      root = node;
    } else {
      if (isLeft) {
        parent.left = node;
      } else {
        parent.right = node;
      }
    }
    return node;
  }

  public void insert(int data) {
    if (root == null) {
      root = new Node(null, null, data);
    }
  }

  public void insert(int target, boolean isLeft, int data) {
    root = insert(root, target, isLeft, data);
  }

  Node insert(Node node, int target, boolean isLeft, int data) {
    if (node == null) {
      return null;
    }
    if (target == node.data) {
      if (isLeft) {
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

  public DoublyLinkedList<Integer> convertToDoublyLinkedList() {
    return convertToDoublyLinkedList(root);
  }

  DoublyLinkedList<Integer> convertToDoublyLinkedList(Node node) {
    DoublyLinkedList<Integer> L = new DoublyLinkedList<>();
    if (node == null) {
      return L;
    }
    L.addAll(convertToDoublyLinkedList(node.left));
    L.addLast(node.data);
    L.addAll(convertToDoublyLinkedList(node.right));
    return L;
  }

  // push current root to stack, pop current root and add it to output after pushing its right and
  // left
  // respectively in the stack. process till the stack is not empty.
  public void iterativePreOrder() {
    List<Integer> l = new ArrayList<>();
    Stack<Node> q = new Stack<>();
    q.push(root);
    while (!q.isEmpty()) {
      Node cur = q.pop();
      l.add(cur.data);
      if (cur.right != null) {
        q.push(cur.right);
      }
      if (cur.left != null) {
        q.push(cur.left);
      }
    }
    for (int i : l) {
      System.out.print(i + " ");
    }
    System.out.println("");
  }

  public void preOrder() {
    preOrderTraversal(root);
    System.out.println("");
  }

  void preOrderTraversal(Node node) {
    if (node == null) {
      return;
    }
    System.out.print(node.data + " ");
    preOrderTraversal(node.left);
    preOrderTraversal(node.right);
  }

  boolean childrenNotProcessed(Node left, Node right, int last) {
    if (left == null && right == null) {
      return true;
    }
    if (left != null && right != null) {
      return left.data != last && right.data != last;
    }
    if (left != null) {
      return left.data != last;
    }
    return right.data != last;
  }

  // push current root to stack, pop current root. if the current root has at least one child (i.e.
  // not processed to output yet), add
  // the root then its right then left child to the stack else just add the current root to the
  // output.
  public void iterativePostOrder() {
    List<Integer> l = new ArrayList<>();
    Stack<Node> q = new Stack<>();
    q.push(root);
    while (!q.isEmpty()) {
      Node cur = q.pop();
      if ((cur.left != null || cur.right != null)
          && (l.isEmpty() || childrenNotProcessed(cur.left, cur.right, l.get(l.size() - 1)))) {
        q.push(cur);
        if (cur.right != null) {
          q.push(cur.right);
        }
        if (cur.left != null) {
          q.push(cur.left);
        }
      } else {
        l.add(cur.data);
      }
    }
    for (int i : l) {
      System.out.print(i + " ");
    }
    System.out.println("");
  }

  // since in post order we defer cur and defer its left and right as the next step, so push to one
  // stack then pop the top and push it to another stack after pushing its children in the old
  // stack.
  public void iterativePostOrderTwoStacks() {
    Stack<Node> q1 = new Stack<>();
    Stack<Node> q2 = new Stack<>();
    q1.push(root);
    while (!q1.isEmpty()) {
      Node cur = q1.pop();
      q2.push(cur);
      if (cur.left != null) {
        q1.push(cur.left);
      }
      if (cur.right != null) {
        q1.push(cur.right);
      }
    }
    while (!q2.isEmpty()) {
      Node cur = q2.pop();
      System.out.print(cur.data + " ");
    }
    System.out.println("");
  }

  public void postOrder() {
    postOrderTraversal(root);
    System.out.println("");
  }

  void postOrderTraversal(Node node) {
    if (node == null) {
      return;
    }
    postOrderTraversal(node.left);
    postOrderTraversal(node.right);
    System.out.print(node.data + " ");
  }

  public BinaryTree buildTreeFromInPreOrder(int[] in, int[] pre) {
    int n = in.length;
    int[] positionInorder = new int[100000];
    for (int i = 0; i < n; ++i) {
      positionInorder[in[i]] = i;
    }
    root = (Node) buildTreeFromInPreOrder(0, 0, n - 1, pre, n, positionInorder)[0];
    return this;
  }

  Object[] buildTreeFromInPreOrder(int it, int l, int r, int[] pre, int n, int[] positionInorder) {
    if (it >= n) {
      return new Object[] {null, it};
    }
    int node = pre[it];
    Node newNode = null;
    if (positionInorder[node] >= l && positionInorder[node] <= r) {
      newNode = new Node(null, null, node);
      Object[] res =
          buildTreeFromInPreOrder(it + 1, l, positionInorder[node] - 1, pre, n, positionInorder);
      newNode.left = (Node) res[0];
      res =
          buildTreeFromInPreOrder(
              (int) res[1], positionInorder[node] + 1, r, pre, n, positionInorder);
      newNode.right = (Node) res[0];
      return new Object[] {newNode, (int) res[1]};
    } else {
      return new Object[] {null, it};
    }
  }

  boolean isSubTree(Node node1, Node node2, boolean matched) {
    if (node2 == null) {
      return true;
    }
    if (node1 == null) {
      return false;
    }
    if (matched) {
      if (node1.data != node2.data) {
        return false;
      }
      return isSubTree(node1.left, node2.left, matched)
          && isSubTree(node1.right, node2.right, matched);

    } else {
      if (node1.data != node2.data) {
        return isSubTree(node1.left, node2, false) || isSubTree(node1.right, node2, false);
      } else {
        return isSubTree(node1.left, node2.left, true) && isSubTree(node1.right, node2.right, true);
      }
    }
  }

  public boolean isSubTree(BinaryTree T) {
    boolean status = isSubTree(root, T.root, false);
    if (status) {
      System.out.println("YES");
    } else {
      System.out.println("NO");
    }
    return status;
  }

  int size(Node node) {
    if (node == null) {
      return 0;
    }
    return 1 + size(node.left) + size(node.right);
  }

  public int size() {
    return size(root);
  }

  void dfs(Node cur, Node prev, int[][] pa, int[] depth) {
    if (cur == null) {
      return;
    }
    if (prev == null) {
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
   *
   * @param data1
   * @param data2
   * @return
   */
  public int LCA(int data1, int data2) {
    int n = size();
    int[][] pa = new int[n + 1][30];
    int[] depth = new int[n + 1];
    dfs(root, null, pa, depth);
    for (int j = 1; j < 30; ++j) {
      for (int i = 1; i <= n; ++i) {
        if (pa[i][j - 1] != -1) {
          pa[i][j] = pa[pa[i][j - 1]][j - 1];
        } else {
          pa[i][j] = -1;
        }
      }
    }
    if (depth[data1] > depth[data2]) {
      int temp = data1;
      data1 = data2;
      data2 = temp;
    }
    for (int j = 29; j >= 0; --j) {
      if (pa[data2][j] != -1 && depth[pa[data2][j]] >= depth[data1]) {
        data2 = pa[data2][j];
      }
    }
    if (data1 == data2) {
      return data1;
    }
    for (int j = 29; j >= 0; --j) {
      if (pa[data1][j] != -1 && pa[data1][j] != pa[data2][j]) {
        data1 = pa[data1][j];
        data2 = pa[data2][j];
      }
    }
    return pa[data1][0];
  }
}
