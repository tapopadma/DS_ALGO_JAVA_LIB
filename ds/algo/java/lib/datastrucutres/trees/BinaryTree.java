package ds.algo.java.lib.datastrucutres.trees;

import ds.algo.java.lib.datastrucutres.linkedlists.DoublyLinkedList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Arrays;

/**
 * For simplicity avoid duplication of keys
 *
 * @author tapopadma
 */
public class BinaryTree {

  public static class Node {
    public Node left;
    public Node right;
    public int data;

    public Node(Node left, Node right, int value) {
      this.left = left;
      this.right = right;
      this.data = value;
    }

    public Node(int value) {
      this.left = null;
      this.right = null;
      this.data = value;
    }

    public Node addLeft(int value) {
      this.left = new Node(null, null, value);
      return this.left;
    }
    public Node addRight(int value) {
      this.right = new Node(null, null, value);
      return this.right;
    }
  }

  public Node root;
  int[] level;
  int[][] pa;

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

  // Simply visit to the left most while pushing to stack, pop last to add to o/p, then repeat for its right child or until stack is empty. O(n),O(h).
  //
  // alternative: O(n),O(n) push current root to stack, pop current root. if the current root's left child
  // exists (i.e. not processed to output yet), add the right child then current root then its left
  // child to the stack, else just add the current root to the output.
  public void iterativeInOrder() {
    List<Integer> l = new ArrayList<>();
    Stack<Node> q = new Stack<>();
    Node cur = root;
    while (cur != null || !q.isEmpty()) {
      while(cur != null) {
        q.push(cur);
        cur = cur.left;
      }
      cur = q.pop();
      l.add(cur.data);
      cur = cur.right;
    }
    for (int i : l) {
      System.out.print(i + " ");
    }
    System.out.println("");
  }

  // O(n) O(1). Morris traversal is about joining inorder predecessor node to current node through a right pointer and later destroying it.
  public void iterativeInOrderMorrisTraversal() {
    List<Integer> l = new ArrayList<>();
    Node cur = root;
    while(cur != null) {
      if(cur.left == null) {
        l.add(cur.data);
        cur = cur.right;
      } else {
        Node prev = cur.left;
        while(prev.right != null && prev.right != cur) {
          prev = prev.right;
        }
        if(prev.right == null) {
          prev.right = cur;
          cur = cur.left;
        } else {
          prev.right = null;
          l.add(cur.data);
          cur = cur.right;
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

  int deepestRightMost(Node root) {
    if(root == null) {
      return -1;
    }
    Queue<Node> q = new ArrayDeque<>();
    q.add(root);
    int res = root.data;
    while(!q.isEmpty()) {
      Node cur = q.poll();
      res = cur.data;
      if(cur.left != null) {
        q.add(cur.left);
      }
      if(cur.right != null) {
        q.add(cur.right);
      }
    }
    return res;
  }

  public void delete(int data) {
    // random strategy: replace target with deepest right most node and delete latter.
    int deepestRightMostData = deepestRightMost(root);
    root = deleteLeaf(root, deepestRightMostData);
    root = removeActualDeletedData(root, data, deepestRightMostData);
  }

  Node removeActualDeletedData(Node cur, int data, int deepestRightMostData) {
    if(cur ==  null) {
      return cur;
    }
    if(cur.data == data) {
      cur.data = deepestRightMostData;
      return cur;
    }
    cur.left = removeActualDeletedData(cur.left, data, deepestRightMostData);
    cur.right = removeActualDeletedData(cur.right, data, deepestRightMostData);
    return cur;
  }

  Node deleteLeaf(Node cur, int target) {
    if(cur == null) {
      return cur;
    }
    if(cur.data == target) {
      return null;
    }
    cur.left = deleteLeaf(cur.left, target);
    cur.right = deleteLeaf(cur.right, target);
    return cur;
  }

  Node convertFromDoublyLinkedList(DoublyLinkedList<Integer>.Node head, DoublyLinkedList<Integer>.Node end) {
    if(head == end) {
      return null;
    }
    DoublyLinkedList<Integer>.Node ptr1 = head, ptr2 = head;
    while(ptr2.next != end && ptr2.next.next != end) {
      ptr2 = ptr2.next.next;
      ptr1 = ptr1.next;
    }
    Node cur = new Node(ptr1.data);
    cur.left = (ptr1 == head ? null : convertFromDoublyLinkedList(head, ptr1));
    cur.right = convertFromDoublyLinkedList(ptr1.next, end);
    return cur;
  }

  public BinaryTree convertFromDoublyLinkedList(DoublyLinkedList<Integer> l) {
    root = convertFromDoublyLinkedList(l.head, null);
    return this;
  }

  public DoublyLinkedList<Integer> convertToDoublyLinkedList() {
    return convertToDoublyLinkedList(root);
  }

  // alternative is to inorder traverse using morris approach w/o destroying right pointers and construct dll accordingly.
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

  void dfs(Node cur, Node prev, int[][] pa, int[] level) {
    if (cur == null) {
      return;
    }
    if (prev == null) {
      pa[cur.data][0] = -1;
      level[cur.data] = 0;
    } else {
      pa[cur.data][0] = prev.data;
      level[cur.data] = level[prev.data] + 1;
    }
    dfs(cur.left, cur, pa, level);
    dfs(cur.right, cur, pa, level);
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
    pa = new int[n + 1][30];
    level = new int[n + 1];
    dfs(root, null, pa, level);
    for (int j = 1; j < 30; ++j) {
      for (int i = 1; i <= n; ++i) {
        if (pa[i][j - 1] != -1) {
          pa[i][j] = pa[pa[i][j - 1]][j - 1];
        } else {
          pa[i][j] = -1;
        }
      }
    }
    if (level[data1] > level[data2]) {
      int temp = data1;
      data1 = data2;
      data2 = temp;
    }
    for (int j = 29; j >= 0; --j) {
      if (pa[data2][j] != -1 && level[pa[data2][j]] >= level[data1]) {
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

  // normal bfs
  public void levelOrder() {
    Queue<Node> q = new LinkedList<>();
    q.add(root);
    while(!q.isEmpty()) {
      Node cur = q.poll();
      System.out.print(cur.data + " ");
      if(cur.left != null) {
        q.add(cur.left);
      }
      if(cur.right != null) {
        q.add(cur.right);
      }
    }
    System.out.println("");
  }

  // just use one deque and pop everything from back and push its children to front right to left if level is even, else
  // pop everything from front and push its chidlren to back from right to left.
  public void spiralOrder() {
    Deque<Node> q = new ArrayDeque<>();
    q.addFirst(root);
    boolean east = false;
    while(!q.isEmpty()) {
      int size = q.size();
      while(size-- > 0) {
        if(!east) {
          Node cur = q.removeLast();
          System.out.print(cur.data + " ");
          if(cur.right != null) {
            q.addFirst(cur.right);
          }
          if(cur.left != null){
            q.addFirst(cur.left);
          }
        } else {
          Node cur = q.removeFirst();
          System.out.print(cur.data + " ");
          if(cur.left != null){
            q.addLast(cur.left);
          }
          if(cur.right != null) {
            q.addLast(cur.right);
          }
        }
      }
      east = !east;
    }
    System.out.println("");
  }

  int depthOrHeight(Node cur) {
    if(cur == null) {
      return -1;
    }
    return Math.max(depthOrHeight(cur.left), depthOrHeight(cur.right)) + 1;
  }

  public int depthOrHeight() {
    return depthOrHeight(root);
  }

  List<Integer> allChildrenKdistanceFromTargetNode(Node cur, int k) {
    List<Integer> ret = new ArrayList<>();
    if(cur == null || k < 0) {
      return ret;
    }
    if(k == 0) {
      ret.add(cur.data);
      return ret;
    }
    ret.addAll(allChildrenKdistanceFromTargetNode(cur.left, k-1));
    ret.addAll(allChildrenKdistanceFromTargetNode(cur.right, k-1));
    return ret;
  }

  int allNodesKdistanceFromTargetNode(Node cur, int target, List<Integer> l, int k) {
    if(cur == null) {
      return -1;
    }
    if(cur.data == target) {
      l.addAll(allChildrenKdistanceFromTargetNode(cur, k));
      return 0;
    }
    int dl = allNodesKdistanceFromTargetNode(cur.left, target, l, k);
    if(dl >= 0) {
      if(k==dl+1) {
        l.add(cur.data);
      } else {
        l.addAll(allChildrenKdistanceFromTargetNode(cur.right, k-dl-1-1));
      }
      return dl + 1;
    }
    int dr = allNodesKdistanceFromTargetNode(cur.right, target, l, k);
    if(dr >= 0) {
      if(k==dr+1) {
        l.add(cur.data);
      } else {
        l.addAll(allChildrenKdistanceFromTargetNode(cur.left, k-dr-1-1));
      }
      return dr + 1;
    }
    return -1;
  }

  // assume each node with distinct value.
  // Basically use a dfs to search target then invoke a method to list all nodes at k-x distance away from each relevant node as root.
  // descendants of target and those of its parents are the relevant nodes.a
  public void allNodesKdistanceFromTargetNode(int target, int k) {
    List<Integer> l = new ArrayList<>();
    allNodesKdistanceFromTargetNode(root, target, l, k);
    for(int i: l) {
      System.out.print(i + " ");
    }
    System.out.println("");
  }

  int[] maxPathSumBetweenLeaves(Node cur) {
    if(cur == null) {
      return new int[]{0,0};
    }
    int [] retl = maxPathSumBetweenLeaves(cur.left);
    int [] retr = maxPathSumBetweenLeaves(cur.right);
    int maxCostDepth = cur.data + Math.max(retl[0], retr[0]);
    int maxPathSum = Math.max(retl[1], Math.max(retr[1], cur.data + retl[0] + retr[0]));
    return new int[]{maxCostDepth, maxPathSum};
  }

  // find max sum starting from left subtree + right subtree + root. search similarly in both left n right subtree.
  public int maxPathSumBetweenLeaves() {
    return maxPathSumBetweenLeaves(root)[1];
  }

  List<List<Integer>> prependToPaths(int e, List<List<Integer>> paths) {
    for(List<Integer> path: paths) {
      path.add(0, e);
    }
    return paths;
  }

  List<List<Integer>> allDownwardPathsOfSumKStartingFromNode(Node cur, int k) {
    List<List<Integer>> ret = new ArrayList<>();
    if(cur == null) {
      return ret;
    }
    if(cur.data == k){
      ret.add(new ArrayList<>(List.of(cur.data)));
    }
    ret.addAll(prependToPaths(cur.data, allDownwardPathsOfSumKStartingFromNode(cur.left, k-cur.data)));
    ret.addAll(prependToPaths(cur.data, allDownwardPathsOfSumKStartingFromNode(cur.right, k-cur.data)));
    return ret;
  }

  List<List<Integer>> allDownwardPathsOfSumK(Node cur, int k) {
    List<List<Integer>> ret = new ArrayList<>();
    if(cur == null) {
      return ret;
    }
    ret.addAll(allDownwardPathsOfSumKStartingFromNode(cur, k));
    ret.addAll(allDownwardPathsOfSumK(cur.left, k));
    ret.addAll(allDownwardPathsOfSumK(cur.right, k));
    return ret;
  }

  // for each root grab all paths starting from it and then recursively grab the same for all its descendants.
  public void allDownwardPathsOfSumK(int k) {
    List<List<Integer>> l = allDownwardPathsOfSumK(root, k);
    for(List<Integer> path: l) {
      for(int i: path) {
        System.out.print(i + "->");
      }
      System.out.println("");
    }
  }

  // there's a O(1) space approach using morris traversal that will track all leaves in left subtree before right. 
  // in fact morris traversal can be a replacement of recursing a tree in general.
  List<Integer> grabLeaves(Node cur) {
    List<Integer> ret = new ArrayList<>();
    if(cur == null) {
      return ret;
    }
    if(cur.left == null && cur.right == null) {
      ret.add(cur.data);
    }
    ret.addAll(grabLeaves(cur.left));
    ret.addAll(grabLeaves(cur.right));
    return ret;
  }

  // root + left branach from root's child till last non-leaf + right branach from root's child till last non-leaf + all leafs
  public void boundary() {
    if(root == null) {
      return;
    }
    // add root first if it's not leaf
    if(root.left != null || root.right != null) {
      System.out.print(root.data + " ");
    }
    // add left boundary
    Node cur = root.left;
    while(cur != null && (cur.left!=null || cur.right != null)) {
      System.out.print(cur.data + " ");
      if(cur.left != null) {
        cur = cur.left;
      } else {
        cur = cur.right;
      }
    }
    // add leafs
    for(int i : grabLeaves(root)) {
      System.out.print(i + " ");
    }
    // add right boundary
    Stack<Integer> q = new Stack<>();
    cur = root.right;
    while(cur != null && (cur.left!=null || cur.right != null)) {
      q.push(cur.data);
      if(cur.right != null) {
        cur = cur.right;
      } else {
        cur = cur.left;
      }
    }
    while(!q.isEmpty()) {
      System.out.print(q.pop() + " ");
    }
    System.out.println("");
  }

  boolean isMirror(Node cur1, Node cur2) {
    if((cur1 == null && cur2 != null) || (cur1 != null && cur2 == null)) {
      return false;
    }
    if(cur1 == null) {
      return true;
    }
    return cur1.data==cur2.data && isMirror(cur1.right, cur2.left) && isMirror(cur1.left, cur2.right);
  }

  public void isMirror(BinaryTree t) {
    System.out.println(isMirror(root, t.root));
  }

  int diameter(Node cur) {
    if(cur == null) {
      return -1;
    }
    return Math.max(1 + depthOrHeight(cur.left) + 1 + depthOrHeight(cur.right), Math.max(diameter(cur.left), diameter(cur.right)));
  }

  public void diameter() {
    System.out.println(diameter(root));
  }

  Object[] buildTreeFromPostInOrder(int rPost, int lIn, int rIn, int[] post, Map<Integer,Integer> posInorder) {
    if(lIn > rIn) {
      return new Object[]{null,rPost};
    }
    int rootData = post[rPost];
    Node root = new Node(rootData);
    Object[] res = buildTreeFromPostInOrder(rPost-1,posInorder.get(rootData)+1,rIn,post,posInorder);
    root.right = (Node)res[0];
    res = buildTreeFromPostInOrder((int)res[1], lIn, posInorder.get(rootData)-1,post, posInorder);
    root.left = (Node)res[0];
    return new Object[]{root, res[1]};
  }

  public void buildTreeFromPostInOrder(int[] post, int[] in) {
    Map<Integer, Integer> positionInorder = new HashMap<>();
    for(int i=0;i<in.length;++i) {
      positionInorder.put(in[i], i);
    }
    root = (Node)buildTreeFromPostInOrder(post.length-1, 0, in.length-1,post,positionInorder)[0];
    this.preOrder();
  }

  public void distanceBetweenTwoNodes(List<List<Integer>> xys) {
    for(List<Integer> xy: xys) {
      int x = xy.get(0), y = xy.get(1);
      int lca = LCA(x, y);
      if(lca == x || lca == y) {
        System.out.println("dist " + x + " " + y + " " + (int)Math.abs(level[x]-level[y]));
      } else {
        System.out.println("dist " + x + " " + y + " " + (level[x]+level[y]-2*level[lca]));
      }
    }
  }

  int[] isBST(Node cur) {
    if(cur==null) {
      return new int[]{1, Integer.MIN_VALUE, Integer.MAX_VALUE};
    }
    int[] res1 = isBST(cur.left);
    int[] res2 = isBST(cur.right);
    boolean res = res1[0]==1 && res2[0]==1 && (res1[1] == Integer.MIN_VALUE || res1[1] < cur.data) && (res2[2] == Integer.MAX_VALUE || cur.data < res2[2]);
    return new int[]{res?1:0, Math.max(res2[1], cur.data), Math.min(res1[2], cur.data)};
  }

  // max in left sub-tree < root, min in right sub-tree > root, left subtree is bst, right subtree is bst.
  public boolean isBST() {
    return isBST(root)[0]==1;
  }

  void serialize(Node cur, List<Integer> list) {
    if(cur == null) {
      list.add(-1);
      return;      
    }
    list.add(cur.data);
    serialize(cur.left, list);
    serialize(cur.right, list);
  }

  // add root to list, check its left recursively if present else append -1 to list, check its right recursively if present else append -1 to list.
  public List<Integer> serialize() {
    List<Integer> arr = new ArrayList<>();
    serialize(root, arr);
    return arr;
  }

  Node deserialize(int[] idx, List<Integer> list) {
    if(list.get(idx[0]) == -1) {
      ++idx[0];
      return null;
    }
    Node cur = new Node(list.get(idx[0]++));
    cur.left = deserialize(idx, list);
    cur.right = deserialize(idx, list);
    return cur;
  }

  // similar to serialize , opposite pattern.
  public void deserialize(List<Integer> list) {
    root = deserialize(new int[]{0}, list);
    this.preOrder();
  }

}
