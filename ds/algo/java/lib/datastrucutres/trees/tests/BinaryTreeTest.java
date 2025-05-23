package ds.algo.java.lib.datastrucutres.trees.tests;

import ds.algo.java.lib.datastrucutres.trees.BinaryTree;

public class BinaryTreeTest {

  public static void main(String[] args) {
    BinaryTree T = new BinaryTree();
    T.insert(1);
    T.insert(1, true, 2);
    T.insert(1, false, 3);
    T.insert(3, true, 9);
    T.insert(9, false, 10);
    T.insert(2, true, 5);
    T.insert(2, false, 4);
    T.insert(4, true, 6);
    T.insert(4, false, 7);
    T.insert(7, true, 8);
    T.inorder();
    T.convertToDoublyLinkedList().print();
    T.clear();
    T.buildTreeFromInPreOrder(
            new int[] {2, 1, 7, 5, 9, 10, 8, 4, 6, 3}, new int[] {1, 2, 3, 4, 5, 7, 8, 9, 10, 6})
        .postOrder();
    T.clear();
    T.buildTreeFromInPreOrder(
            new int[] {2, 1, 7, 5, 9, 10, 8, 4, 6, 3}, new int[] {1, 2, 3, 4, 5, 7, 8, 9, 10, 6})
        .iterativePostOrderTwoStacks();
    T.clear();
    BinaryTree T1 = new BinaryTree();
    T1.buildTreeFromInPreOrder(new int[] {7, 5, 9, 8, 4}, new int[] {4, 5, 7, 8, 9}).postOrder();
    T.isSubTree(T1);
    // System.out.println(T.LCA(6, 10));
    // 1 -- 3    /-- 10  /--- 7
    //   \   \_ 9 /-----4      \-- 8
    //    \_____ 2       \___ 6
    //            \-- 5
    T.clear();
    BinaryTree.Node n1 = T.add(null, false, 1);
    BinaryTree.Node n2 = T.add(n1, true, 2);
    BinaryTree.Node n3 = T.add(n1, false, 3);
    BinaryTree.Node n9 = T.add(n3, true, 9);
    T.add(n9, false, 10);
    T.add(n2, true, 5);
    BinaryTree.Node n4 = T.add(n2, false, 4);
    T.add(n4, true, 6);
    BinaryTree.Node n7 = T.add(n4, false, 7);
    T.add(n7, true, 8);
    T.preOrder();
    T.iterativePreOrder();
    T.postOrder();
    T.iterativePostOrder();
    T.iterativePostOrderTwoStacks();
    T.inorder();
    T.iterativeInOrder();
    T.fetchIterativeAncestorList(n7);
    T.fetchRightView();
    T.spiralOrder();
  }
}
