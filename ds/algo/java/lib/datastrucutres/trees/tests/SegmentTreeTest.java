package ds.algo.java.lib.datastrucutres.trees.tests;

import ds.algo.java.lib.datastrucutres.trees.SegmentTree;
import java.util.Arrays;

class SegmentTreeTest {

	static void validateLogic(int x, int y) {
      if (x != y) {
        throw new RuntimeException("x , y don't match: " + x + " " + y);
      }
	}
	
	public static void main(String[] args) {
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
		SegmentTree sLazy = new SegmentTree();
		sLazy.lazyPropagation(Arrays.asList(1, 2, 3, 5, 4, 2), 
			Arrays.asList(Arrays.asList(1, 2, 2),Arrays.asList(3, 5, 1),Arrays.asList(2, 5, 3),Arrays.asList(5, 6, 3)),
			Arrays.asList(Arrays.asList(1, 6),Arrays.asList(3, 5),Arrays.asList(1, 2),Arrays.asList(5, 6), Arrays.asList(2, 4))
		);
		SegmentTree sPersistent = new SegmentTree();
		sPersistent.persistentSegmentTree(Arrays.asList(1, 2, 1, 4, 7, 9, 2, 3, 3, 8, 6, 5, 2), 
			Arrays.asList(Arrays.asList(1, 13, 2, 5),Arrays.asList(3, 10, 2, 5),
				Arrays.asList(1, 13, 1, 5),Arrays.asList(1, 13, 3, 8),Arrays.asList(1, 13, 2, 2),
		    Arrays.asList(3, 8, 2, 2)));
		SegmentTree treeMaxSumSubarray = new SegmentTree();
		treeMaxSumSubarray.maxSubarraySumSegmentTree(Arrays.asList(-2, -3, 4, -1, -2, 1, 5, -3),
			Arrays.asList(Arrays.asList(1, 10)),
			Arrays.asList(Arrays.asList(5, 8), Arrays.asList(1, 3), Arrays.asList(3,7)));
		validateLogic(new SegmentTree().LIS(Arrays.asList(1, 3, 5, 4, 7)), 4);
	}
}
