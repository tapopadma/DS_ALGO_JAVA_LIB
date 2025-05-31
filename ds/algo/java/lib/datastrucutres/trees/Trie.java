package ds.algo.java.lib.datastrucutres.trees;

import java.util.List;
import java.util.Arrays;

public class Trie {

	class Node {
		Node[] children;
		boolean wordEnds;
		public Node() {
			wordEnds = false;
			children = new Node[26];
		}
	}

	Node root;

	public Trie() {
		root = null;
	}

	Node add(Node cur, int i, String word) {
		if(cur == null) {
			cur = new Node();
		}
		if(i == word.length()) {
			cur.wordEnds = true;
			return cur;
		}
		int j = word.charAt(i)-'a';
		cur.children[j] = add(cur.children[j], i+1, word);
		return cur;
	}

	public void add(String word) {
		root = add(root, 0, word);
	}

	boolean search(Node cur, int i, String word) {
		if(i == word.length()) {
			return cur != null && cur.wordEnds;
		}
		if(cur == null) {
			return false;
		}
		int j = word.charAt(i)-'a';
		return search(cur.children[j], i + 1, word);
	}

	public boolean search(String word) {
		return search(root, 0, word);
	}

	boolean noSuccessors(Node cur) {
		if(cur == null) {
			return true;
		}
		for(int i=0;i<26;++i) {
			if(cur.children[i] != null) {
				return false;
			}
		}
		return true;
	}
	
	Node delete(Node cur, int i, String word) {
		if(i == word.length()) {
			if(cur != null) {
				cur.wordEnds = false;
			}
			if(noSuccessors(cur)) {
				return null;
			}
			return cur;
		}
		if(cur == null) {
			return cur;
		}
		int j = word.charAt(i) - 'a';
		cur.children[j] = delete(cur.children[j], i+1, word);
		if(cur.children[j] == null && noSuccessors(cur)) {
			return null;
		}
		return cur;
	}

	public void delete(String word) {
		root = delete(root, 0, word);
	}

	public void clear() {
		root = null;
	}

}