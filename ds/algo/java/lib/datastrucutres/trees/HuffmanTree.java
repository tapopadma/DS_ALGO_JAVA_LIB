package ds.algo.java.lib.datastrucutres.trees;

import java.util.Map;
import java.util.HashMap;
import java.util.PriorityQueue;

public class HuffmanTree {

	class Node {
		char ch;
		int freq;
		Node left;
		Node right;
		public Node(char ch, int freq) {
			this.ch = ch;
			this.freq = freq;
		}
	}

	Node root;

	// Huffman tree is basically to encode a document by mapping each character to a bit-stream.
	// The mapping is done based on frequency of the characters in the document.
	//
	// For the mapping, basically 2 characters are chosen with lowest freq and then their sum is determined and
	// then all the 3 information are stored in nodes such that the sum is the parent node and the least frequent node
	// popped becomes the left child node and the other one is the right child node. This is continued until all the 
	// characters get allotted to a parent.
	// This way eah character is a leaf node and its path from parent is the encoded bit-stream given left link is denoted
	// as 0 and right as 1.
	public HuffmanTree build(String document) {
		Map<Character, Integer> mp = new HashMap<>();
		for(int i=0;i<document.length();++i) {
			char c = document.charAt(i);
			int cnt = mp.containsKey(c) ? 1+mp.get(c) : 1;
			mp.put(c, cnt);
		}
		PriorityQueue<Node> q = new PriorityQueue<>(
			(n1, n2) -> n1.freq==n2.freq ? Integer.valueOf(n1.ch).compareTo(Integer.valueOf(n2.ch))
			:Integer.valueOf(n1.freq).compareTo(n2.freq));
		for(char c: mp.keySet()) {
			q.add(new Node(c, mp.get(c)));
		}
		if(q.size() <= 1) {
			return this;
		}
		while(q.size() > 1) {
			Node n1 = q.poll();
			Node n2 = q.poll();
			Node n3 = new Node(' ', n1.freq + n2.freq);
			n3.left = n1;
			n3.right = n2;
			q.add(n3);
		}
		root = q.poll();
		return this;
	}

	void printMapping(Node cur, String path) {
		if(cur.ch != ' ') {
			// leaf
			System.out.println(cur.ch + " -> " + path);
			return;
		}
		printMapping(cur.left, path + "0");
		printMapping(cur.right, path + "1");
	}

	public void printMapping() {
		if(root == null) {
			System.out.println("0");
			return;
		}
		printMapping(root, "");
	}

}