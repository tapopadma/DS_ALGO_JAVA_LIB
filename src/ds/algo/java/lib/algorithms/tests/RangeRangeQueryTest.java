package ds.algo.java.lib.algorithms.tests;

import ds.algo.java.lib.algorithms.RangeRangeQuery;

public class RangeRangeQueryTest {

	//input
/*  10
	2 9 1 7 4 5 5 8 6 9
	10
	1 10 100 1000
	1 10 2 9
	1 10 1 1
	3 3 1 1
	2 9 1 6
	4 7 2 10
	5 8 1 5
	2 6 2 4
	3 6 1 10
	2 8 8 10*/
	//output
/*  0
	9
	1
	1
	5
	4
	3
	1
	4
	2*/
	public static void main(String[] args) {
		StandardSolverExector.solve(new RangeRangeQuery(), true);
	}

}
