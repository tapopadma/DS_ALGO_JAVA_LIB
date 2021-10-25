package ds.algo.java.lib.datastrucutres.trees.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ds.algo.java.lib.datastrucutres.trees.SegmentTree;

class SegmentTreeTest {
	
	static SegmentTree treeMx;
	static SegmentTree treeMn;
	static SegmentTree treeSum;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		System.out.println("sdfsdf");
		treeMx = new SegmentTree(new int[] {9, 8, 7, 1, 5, 4, 3, 2, 6}
		, SegmentTree.Type.MAX);
		treeMn = new SegmentTree(new int[] {9, 8, 7, 1, 5, 4, 3, 2, 6}
		, SegmentTree.Type.MIN);
		treeSum = new SegmentTree(new int[] {9, 8, 7, 1, 5, 4, 3, 2, 6}
		, SegmentTree.Type.SUM);
	}

	@Test
	void testQuery() {
		assertEquals(6, treeMx.query(6, 9));
		assertEquals(2, treeMn.query(6, 9));
		assertEquals(15, treeSum.query(6, 9));
		treeMx.update(7, 32);
		treeMx.update(6, 0);
		treeMx.update(5, 36);
		assertEquals(36, treeMx.query(5, 9));
		treeMn.update(7, 3);
		treeMn.update(6, 0);
		treeMn.update(5, 36);
		assertEquals(0, treeMn.query(5, 9));
		treeSum.update(7, 0);
		treeSum.update(6, 0);
		treeSum.update(5, 0);
		assertEquals(8, treeSum.query(5, 9));
	}

}
