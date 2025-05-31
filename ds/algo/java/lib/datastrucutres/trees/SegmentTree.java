package ds.algo.java.lib.datastrucutres.trees;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import ds.algo.java.lib.datastrucutres.trees.BinaryTree.Node;

public class SegmentTree {

	class MaxSumSubarrayNode {
		int maxPrefixSum;
		int maxSuffixSum;
		int totalSum;
		int maxSubarraySum;
		public MaxSumSubarrayNode() {
			this.maxPrefixSum = this.maxSuffixSum = this.totalSum = this.maxSubarraySum = Integer.MIN_VALUE;
		}
	}
	
	public enum Type {
		SUM, MIN, MAX, MAX_SUM_SUBARRAY;
	}
	
	private static final int INF = (1<<31)-1;

	int []a;
	Type type = Type.SUM;
	int []T;
	int [][]Tlazy;
	int n;
	List<Node> persistentRoots;
	MaxSumSubarrayNode[] Tmss;
	
	public SegmentTree() {

	}

	public SegmentTree(int []a, Type type) {
		this.n = a.length;
		this.a = Arrays.copyOf(a, n);
		this.type = type;
		this.T= new int[4*n];
		for(int i=0;i<4*n;++i)T[i]=getInvalid();
		for(int i=0;i<n;++i)update(i+1, a[i]);
	}
	
	public void update(int x, int val) {
		update(1, 1, n, x, val);
	}
	void update(int i, int l, int r, int x, int val) {
		if(x > r || x < l)
			return;
		if(l==r) {
			T[i]=val;
			return;
		}
		int m = (l+r)/2;
		update(2*i, l, m, x, val);
		update(1+2*i, 1+m, r, x, val);
		T[i]=process(T[i*2], T[i*2+1]);
	}
	
	public int query(int x, int y) {
		return query(1, 1, n, x, y);
	}
	
	int query(int i, int l, int r, int x, int y) {
		if(x > r || y < l)
			return getInvalid();
		if(x <= l && r <= y)
			return T[i];
		int m = (l+r)/2;
		return process(query(2*i, l, m, x, y), query(1+2*i, 1+m, r, x, y));
	}
	
	int process(int x, int y) {
		switch (this.type) {
			case MIN:
				return Math.min(x, y);
			case MAX:
				return Math.max(x, y);
			default:
				return x+y;
		}
	}
	
	int getInvalid() {
		switch (this.type) {
			case MIN:
				return INF;
			case MAX:
				return -INF;
			default:
				return 0;
		}
	}

	void propagate(int i, int l, int r) {
		Tlazy[i][0] += (r-l+1) * Tlazy[i][1];
		if(2*i < 4*n) {
			Tlazy[2*i][1] += Tlazy[i][1];
		}
		if(1+2*i < 4*n) {
			Tlazy[1+2*i][1] += Tlazy[i][1];
		}
		Tlazy[i][1] = 0;
	}

	void updateLazyPropagation(int i, int l, int r, int x, int y, int val) {
		propagate(i, l, r);
		if(x > r || y < l) {
			return;
		}
		if(x <= l && r <= y) {
			Tlazy[i][1] += val;
			propagate(i, l, r);
			return;
		}
		int m = (l + r)/2;
		updateLazyPropagation(2*i, l, m, x, y, val);
		updateLazyPropagation(2*i+1, 1+m, r, x, y, val);
		Tlazy[i][0] = Tlazy[2*i][0] + Tlazy[2*i + 1][0];
	}

	void updateLazyPropagation(int x, int y, int val) {
		updateLazyPropagation(1, 1, n, x, y, val);
	}

	int queryLazyPropagation(int i, int l, int r, int x, int y) {
		propagate(i, l, r);
		if(x > r || y < l) {
			return 0;
		}
		if(x <= l && r <= y) {
			return Tlazy[i][0];
		}
		int m = (l + r)/2;
		return queryLazyPropagation(i*2, l, m, x, y) + queryLazyPropagation(1+i*2, 1+m, r, x, y);
	}

	int queryLazyPropagation(int x, int y) {
		return queryLazyPropagation(1, 1, n, x, y);
	}

    // For update of a range instead of an index, just store the value to be added in the target segments and
    // propagate the values downwards when the next time the segments are encountered during any function call.
    // As part of propagation add the value multiplied by range length to the sum field of the segment and then
    // pass the value to both children.
	public void lazyPropagation(List<Integer> l, List<List<Integer>> updates, List<List<Integer>> queries) {
		n = l.size();
		Tlazy = new int[4*n][2];
		for(int i=0;i<n;++i) {
			updateLazyPropagation(i+1, i+1, l.get(i));
		}
		for(int i: l) {
			System.out.print(i + " ");
		}
		System.out.println("");
		System.out.println("Before");
		for(List<Integer> query: queries) {
			System.out.println(query.get(0) + " " + query.get(1) + " " + queryLazyPropagation(query.get(0), query.get(1)));
		}
		for(List<Integer> update: updates) {
			updateLazyPropagation(update.get(0), update.get(1), update.get(2));
		}
		System.out.println("After");
		for(List<Integer> query: queries) {
			System.out.println(query.get(0) + " " + query.get(1) + " " + queryLazyPropagation(query.get(0), query.get(1)));
		}
	}

	Node updatePersistentSegmentTree(Node prev, Node cur, int l, int  r, int x, int val) {
		if(x > r || x < l) {
			return prev;
		}
		if(cur == null) {
			cur = new Node(val);
		}
		if(l == r) {
			return cur;
		}
		int m = (l + r) / 2;
		cur.left = updatePersistentSegmentTree(prev!=null?prev.left:null, cur.left, l, m, x, val);
		cur.right = updatePersistentSegmentTree(prev!=null?prev.right:null, cur.right, 1+m, r, x, val);
		cur.data = (cur.left!=null?cur.left.data:0) + (cur.right!=null?cur.right.data:0);
		return cur;
	}

    void updatePersistentSegmentTree(int x, int val) {
    	Node prev = persistentRoots.isEmpty() ? null: persistentRoots.get(persistentRoots.size() - 1);
    	persistentRoots.add(updatePersistentSegmentTree(prev, null, 1, n, x, val));
    }

    int queryPersistentSegmentTree(Node cur, int l, int r, int x, int y) {
    	if(cur == null) {
    		return 0;
    	}
    	if(x > r || y < l) {
    		return 0;
    	}
    	if(x <= l && r <= y) {
    		return cur.data;
    	}
    	int m = (l + r) / 2;
    	return queryPersistentSegmentTree(cur.left, l, m, x, y) + queryPersistentSegmentTree(cur.right, 1+m, r, x, y);
    }

    int queryPersistentSegmentTree(int v, int x, int y) {
    	if(v < 0) {
    		return 0;
    	}
    	return queryPersistentSegmentTree(persistentRoots.get(v), 1, n, x, y);
    }

	// persistent segment tree is just the m copy of a segment tree in an efficient way i.e. by reusing the existing versions.
	// The simplest use case is count the number of numbers in [x, y] that exist in the subarray [l,r] of an array, we
	// store a segment tree for each index of the array to store the segment tree at each index so as to be able to give
	// analytics in the prefix.
	public void persistentSegmentTree(List<Integer> list, List<List<Integer>> queries) {
		System.out.println("-----");
		for(int x: list) {
			System.out.print(x + " ");
		}
		System.out.println();
		persistentRoots = new ArrayList<>();
		n = list.size();
		int[] f = new int[n+1];
		for(int x: list) {
			// ideally people build the initial tree entirely from list, but this code is specifically based on above use case.
			// so our initial tree is empty and for each index we are just adding up a new version.
			//
			// assume list has elements in range [1,n] only.
			updatePersistentSegmentTree(x, ++f[x]);
		}
		for(List<Integer> query: queries) {
			int l = query.get(0), r = query.get(1), x = query.get(2), y = query.get(3);
			System.out.println(l + " " + r + " " + x + " " + y + " " + (queryPersistentSegmentTree(r-1, x, y)-queryPersistentSegmentTree(l-2,x,y)));
		}
	}

	void buildMSSSegmentTree(int i, int l, int r, List<Integer> list) {
		if(l == r) {
			Tmss[i] = new MaxSumSubarrayNode();
			Tmss[i].maxPrefixSum = Tmss[i].maxSuffixSum = Tmss[i].totalSum = Tmss[i].maxSubarraySum = list.get(l-1);
			return;
		}
		int m = (l + r) / 2;
		buildMSSSegmentTree(i*2, l, m, list);
		buildMSSSegmentTree(1+i*2, 1+m, r, list);
		Tmss[i].maxPrefixSum = Math.max(Tmss[i*2].maxPrefixSum, Tmss[i*2].totalSum + Tmss[1+i*2].maxPrefixSum);
		Tmss[i].maxSuffixSum = Math.max(Tmss[1+i*2].maxSuffixSum, Tmss[1+i*2].totalSum + Tmss[i*2].maxSuffixSum);
		Tmss[i].totalSum = Tmss[i*2].totalSum + Tmss[1+i*2].totalSum;
		Tmss[i].maxSubarraySum = Math.max(
			Math.max(Tmss[i*2].maxSubarraySum, Tmss[1+i*2].maxSubarraySum), 
			Tmss[i*2].maxSuffixSum + Tmss[1+i*2].maxPrefixSum);
	}

	void updateMSSSegmentTree(int i, int l, int r, int x, int val) {
		if(x > r || x < l) {
			return;
		}
		if(l == r) {
			Tmss[i].maxPrefixSum = Tmss[i].maxSuffixSum = Tmss[i].totalSum = Tmss[i].maxSubarraySum = val;
			return;
		}
		int m = (l + r) / 2;
		updateMSSSegmentTree(i*2, l, m, x, val);
		updateMSSSegmentTree(1+i*2, 1+m, r, x, val);
		Tmss[i].maxPrefixSum = Math.max(Tmss[i*2].maxPrefixSum, Tmss[i*2].totalSum + Tmss[1+i*2].maxPrefixSum);
		Tmss[i].maxSuffixSum = Math.max(Tmss[1+i*2].maxSuffixSum, Tmss[1+i*2].totalSum + Tmss[i*2].maxSuffixSum);
		Tmss[i].totalSum = Tmss[i*2].totalSum + Tmss[1+i*2].totalSum;
		Tmss[i].maxSubarraySum = Math.max(
			Math.max(Tmss[i*2].maxSubarraySum, Tmss[1+i*2].maxSubarraySum), 
			Tmss[i*2].maxSuffixSum + Tmss[1+i*2].maxPrefixSum);
	}

	void updateMSSSegmentTree(int x, int val) {
		updateMSSSegmentTree(1, 1, n, x, val);
	}

	MaxSumSubarrayNode queryMSSSegmentTree(int i, int l, int r, int x, int y) {
		if(x > r || y < l) {
			return new MaxSumSubarrayNode();
		}
		if(x <= l && r <= y) {
			return Tmss[i];
		}
		int m = (l + r) / 2;
		MaxSumSubarrayNode left = queryMSSSegmentTree(i*2, l, m, x, y);
		MaxSumSubarrayNode right = queryMSSSegmentTree(1+i*2, 1+m, r, x, y);
		MaxSumSubarrayNode res = new MaxSumSubarrayNode();
		res.maxPrefixSum = Math.max(left.maxPrefixSum, left.totalSum + right.maxPrefixSum);
		res.maxSuffixSum = Math.max(right.maxSuffixSum, right.totalSum + left.maxSuffixSum);
		res.totalSum = left.totalSum + right.totalSum;
		res.maxSubarraySum = Math.max(
			Math.max(left.maxSubarraySum, right.maxSubarraySum), 
			left.maxSuffixSum + right.maxPrefixSum);
		return res;
	}

	int queryMSSSegmentTree(int x, int y) {
		return queryMSSSegmentTree(1, 1, n, x, y).maxSubarraySum;
	}

    // Multiple queries asking to find max sum of (contiguous) sub-array in given interval of the array. The idea
    // is still segment tree, just the operation that is done on left and right child to update current child is different.
    //
    // 4 metrics are used to determine this: max prefix sum = max(left child's max prefix sum, left child's total sum+right child's max prefix sum),
    // max suffix sum = max(right child's max suffix sum, right child's total sum + left child's max suffix sum),
    // total sum = left child's total sum + right child's total sum,
    // max subarray sum = max(left child's max subarray sum, right child's max subarray sum, left child's max suffix sum+right child's max prefix sum);
	public void maxSubarraySumSegmentTree(List<Integer> list, List<List<Integer>> updates, List<List<Integer>> queries) {
		System.out.println("---");
		for (int i: list ) {
			System.out.print(i + " ");
		}
		System.out.println();
		n = list.size();
		Tmss = new MaxSumSubarrayNode[4*n];
		for(int i=0;i<4*n;++i){
			Tmss[i] = new MaxSumSubarrayNode();
		}
		buildMSSSegmentTree(1, 1, n, list);
		for(List<Integer> query: queries) {
			System.out.println(query.get(0) + " " + query.get(1) + " " + queryMSSSegmentTree(query.get(0),query.get(1)));
		}
		for(List<Integer> update: updates) {
			updateMSSSegmentTree(update.get(0),update.get(1));
		}
		for(List<Integer> query: queries) {
			System.out.println(query.get(0) + " " + query.get(1) + " " + queryMSSSegmentTree(query.get(0),query.get(1)));
		}
	}

    // at position i look for max of dp[j] where j belongs to [1,l[i]-1], so dp[i] will be 1+max to denote longest increasing subsequence.
	public int LIS(List<Integer> l) {
		type = Type.MAX;
		n = Collections.max(l);
		T = new int[n*4];
		int ans = 0;
		for(int i=0;i<l.size();++i) {
			int x = l.get(i);
			int mx = x>1?query(1, x-1):0;
			int res = mx + 1;
			ans = Math.max(ans, res);
			update(x, res);
		}
		return ans;
	}
}
