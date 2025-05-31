package ds.algo.java.lib.datastrucutres.trees.tests;

import ds.algo.java.lib.datastrucutres.trees.BinaryTree;
import java.util.Arrays;

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
    T.convertFromDoublyLinkedList(T.convertToDoublyLinkedList()).preOrder();
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
    T.iterativeInOrderMorrisTraversal();
    T.fetchIterativeAncestorList(n7);
    T.fetchRightView();
    T.spiralOrder();
    T.levelOrder();
    System.out.println(T.depthOrHeight());
    T.allNodesKdistanceFromTargetNode(4, 2);
    T.allNodesKdistanceFromTargetNode(4, 1);
    T.allNodesKdistanceFromTargetNode(4, 3);
    T.boundary();
    System.out.println("-----");
    T.clear();
    BinaryTree.Node root = T.add(null, false, -15);
    root.addLeft(5).addLeft(-8).addLeft(2);root.left.left.addRight(6);root.left.addRight(1);
    root.addRight(6).addRight(9).addRight(0).addRight(-1).addLeft(10);root.right.right.right.addLeft(4);
    root.right.addLeft(3);
    System.out.println(T.maxPathSumBetweenLeaves());
    T.clear();
    root = T.add(null, false, 1);
    root.addLeft(-2).addLeft(8);root.left.addRight(-1);
    root.addRight(3).addRight(-5);root.right.addLeft(4);
    System.out.println(T.maxPathSumBetweenLeaves());
    T.clear();
    root = T.add(null, false, 1);
    root.addLeft(3).addLeft(2);root.left.addRight(1).addLeft(1);
    root.addRight(-1).addRight(5).addRight(6);root.right.addLeft(4).addLeft(1);root.right.left.addRight(2);
    T.allDownwardPathsOfSumK(5);
    T.clear();
    root = T.add(null, false, 8);
    root.addLeft(4).addRight(2).addRight(1);root.left.addLeft(3).addLeft(3);root.left.left.addRight(-2);
    root.addRight(5).addRight(2);
    T.allDownwardPathsOfSumK(7);
    // 1 -- 3    /-- 10  /--- 7
    //   \   \_ 9 /-----4      \-- 8
    //    \_____ 2       \___ 6
    //            \-- 5
    T.clear();
    root = T.add(null, false, 1);
    root.addRight(3).addLeft(9).addRight(10);
    root.addLeft(2).addRight(4).addRight(7).addLeft(8);
    root.left.addLeft(5);root.left.right.addLeft(6);
    T.preOrder();
    T.delete(4);
    T.preOrder();
    T.clear();
    root = T.add(null, false, 1);
    root.addRight(3).addLeft(9).addRight(10);
    root.addLeft(2).addRight(4).addRight(7).addLeft(8);
    root.left.addLeft(5);root.left.right.addLeft(6);
    BinaryTree Tmirr = new BinaryTree();
    BinaryTree.Node rootM = Tmirr.add(null, false, 1);
    rootM.addLeft(3).addRight(9).addLeft(10);
    rootM.addRight(2).addLeft(4).addLeft(7).addRight(8);
    rootM.right.addRight(5);rootM.right.left.addRight(6);
    T.isMirror(Tmirr);
    T.diameter();
    T.distanceBetweenTwoNodes(Arrays.asList(Arrays.asList(9, 6), Arrays.asList(5, 2), Arrays.asList(1, 4), Arrays.asList(7, 10)));
    T.buildTreeFromPostInOrder(new int[]{4, 5, 2, 3, 1}, new int[]{4, 2, 5, 1, 3});
    T.clear();
    root = T.add(null, false, 10);
    root.addLeft(5);
    root.addRight(20).addRight(25);
    root.right.addLeft(9);
    System.out.println(T.isBST());
    T.clear();
    root = T.add(null, false, 2);
    root.addLeft(1);
    root.addRight(3).addRight(5);
    System.out.println(T.isBST());
  }
}
