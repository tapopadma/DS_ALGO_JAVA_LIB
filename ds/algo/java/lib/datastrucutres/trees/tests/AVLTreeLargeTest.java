package ds.algo.java.lib.datastrucutres.trees.tests;

import ds.algo.java.lib.datastrucutres.trees.AVLTree;

public class AVLTreeLargeTest {

	public static void main(String[] args) {
		AVLTree avl = new AVLTree();
		int NN = 1000000;
		for(int i=NN;i>=1;--i) {
			avl.add(i);
		}
		for(int i=NN/2;i<=NN;++i) {
			avl.remove(i);
		}
		System.out.println(avl.getMax() + " " + avl.getDepth());
	}

}
