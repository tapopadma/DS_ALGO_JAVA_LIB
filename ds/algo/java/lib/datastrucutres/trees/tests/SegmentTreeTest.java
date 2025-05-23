package ds.algo.java.lib.datastrucutres.trees.tests;

import ds.algo.java.lib.datastrucutres.trees.SegmentTree;

class SegmentTreeTest {

	static void validateLogic(int x, int y) {
      if (x != y) {
        throw new RuntimeException("x , y don't match: " + x + " " + y);
      }
	}
	
	public static void main(String[] args) {
		System.out.println("sdfsdf");
		SegmentTree treeMx = new SegmentTree(new int[] {9, 8, 7, 1, 5, 4, 3, 2, 6}
		, SegmentTree.Type.MAX);
		SegmentTree treeMn = new SegmentTree(new int[] {9, 8, 7, 1, 5, 4, 3, 2, 6}
		, SegmentTree.Type.MIN);
		SegmentTree treeSum = new SegmentTree(new int[] {9, 8, 7, 1, 5, 4, 3, 2, 6}
		, SegmentTree.Type.SUM);
		validateLogic(6, treeMx.query(6, 9));
		validateLogic(2, treeMn.query(6, 9));
		validateLogic(15, treeSum.query(6, 9));
		treeMx.update(7, 32);
		treeMx.update(6, 0);
		treeMx.update(5, 36);
		validateLogic(36, treeMx.query(5, 9));
		treeMn.update(7, 3);
		treeMn.update(6, 0);
		treeMn.update(5, 36);
		validateLogic(0, treeMn.query(5, 9));
		treeSum.update(7, 0);
		treeSum.update(6, 0);
		treeSum.update(5, 0);
		validateLogic(8, treeSum.query(5, 9));
	}

}
