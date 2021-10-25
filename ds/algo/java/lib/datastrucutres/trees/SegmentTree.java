package ds.algo.java.lib.datastrucutres.trees;

import java.util.Arrays;

public class SegmentTree {
	
	public enum Type {
		SUM, MIN, MAX;
	}
	
	private static final int INF = (1<<31)-1;

	int []a;
	Type type = Type.SUM;
	int []T;
	int n;
	
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
			T[i]=val;return;
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
}
