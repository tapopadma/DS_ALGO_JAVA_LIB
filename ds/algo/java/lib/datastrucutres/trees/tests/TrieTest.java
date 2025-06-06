package ds.algo.java.lib.datastrucutres.trees.tests;

import ds.algo.java.lib.datastrucutres.trees.Trie;
import ds.algo.java.lib.datastrucutres.trees.HuffmanTree;

public class TrieTest {

  static void validateLogic(boolean x, boolean y) {
      if (x != y) {
        throw new RuntimeException("x , y don't match: " + x + " " + y);
      }
  }

  public static void main(String[] args) {
    Trie T = new Trie();
    T.add("the");
    T.add("a");
    T.add("there");
    T.add("answer");
    T.add("any");
    T.add("by");
    T.add("bye");
    T.add("their");
    validateLogic(T.search("the"), true);
    validateLogic(T.search("these"), false);
    T.delete("these");
    T.delete("the");
    T.add("these");
    validateLogic(T.search("these"), true);
    T.delete("these");
    T.delete("the");
    T.delete("a");
    T.delete("there");
    T.delete("answer");
    T.delete("any");
    T.delete("by");
    T.delete("their");
    validateLogic(T.search("bye"), true);
    validateLogic(T.search("by"), false);
    T.delete("bye");
    validateLogic(T.search("bye"), false);
    validateLogic(T.search("empty"), false);

    HuffmanTree tree = new HuffmanTree();
    tree.build("abbcccdddeeeefffff").printMapping();
  }
}
