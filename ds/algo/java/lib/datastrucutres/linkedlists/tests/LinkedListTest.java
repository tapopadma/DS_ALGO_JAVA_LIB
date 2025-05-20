package ds.algo.java.lib.datastrucutres.linkedlists.tests;

import ds.algo.java.lib.datastrucutres.linkedlists.SinglyLinkedList;

public class LinkedListTest {

  public static void main(String[] args) {
    SinglyLinkedList L = new SinglyLinkedList();
    L.pushBack(2);
    L.pushFront(1);
    L.pushFront(0);
    L.pushBack(3);
    L.pushBack(4);
    L.print();
    L.insertAt(3, 4);
    L.print();
    L.deleteAt(4);
    L.print();
    System.out.println(L.contains(4));
    System.out.println(L.get(L.size() / 2));
    L.reverse();
    L.print();
    L.reverse();
    L.print();
    L.subList(0, 1).print();
    L.subList(3, 4).print();
    System.out.println(L.isPalindrome());
    L.clear();
    L.print();
    L.pushBack(2);
    L.pushFront(4);
    L.pushBack(4);
    System.out.println(L.isPalindrome());
    L.print();
    L.joinTailAt(1);
    System.out.println(L.getLoopStartIndex() + " " + L.getLoopLength());
    L.clear();
    L.pushBack(1);
    L.swap(0, 0);
    L.print();
    L.pushBack(2);
    L.swap(1, 0);
    L.print();
    L.pushBack(3);
    L.swap(0, 2);
    L.swap(0, 1);
    L.swap(1, 2);
    L.print();
    L.reverse().print();
    L.sort();
    L.print();
    L.clear();
    L.pushBack(5);
    L.sort();
    L.print();
    L.pushBack(2);
    L.sort();
    L.print();
    L.clear();
    L.pushBack(1);
    L.pushBack(4);
    L.pushBack(3);
    L.pushBack(2);
    L.pushBack(5);
    L.subList(1, 3).reverse().print();
    L.reverse(1, 3).print();
    L = L.reverse().mergeSort();
    L.print();
    L.rotateLeft(L.getNodeAt(1), L.getNodeAt(3)).print();
    L.rotateLeft(L.getNodeAt(0), L.getNodeAt(2)).print();
    L.rotateLeft(L.getNodeAt(0), L.getNodeAt(1)).print();
    L.rotateLeft(L.getNodeAt(4), L.getNodeAt(4)).print();
  }
}
