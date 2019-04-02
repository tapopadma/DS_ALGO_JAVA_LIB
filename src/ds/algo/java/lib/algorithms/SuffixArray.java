package ds.algo.java.lib.algorithms;

import java.io.PrintWriter;
import java.util.Arrays;

import ds.algo.java.lib.io.FastInputReader;

/**
 * This algorithm sorts suffixes of the string 
 * by using character matching at exponential pace.
 * O(n*logn*logn)
 * @author tapopadma
 *
 */
public class SuffixArray implements StandardAlgoSolver {
	
	/**
	 * total number of steps needed to sort correctly
	 */
	int stepsNeeded=0;
	
	/**
	 * P[stp][i] stores weight of the suffix starting at i
	 * after sorting all suffixes based on their prefix of
	 * length (1<<stp)
	 */
	int [][] P;
	
	class Triplet {
		Integer first, second, index;
		public Triplet(int f, int s, int i) {
			this.first = f;
			this.second = s;
			this.index = i;
		}
	}
	
	/**
	 * L[i] stores (weight of suffix at i, that at i + 2^(stp-1),i)
	 * and then is sorted based on (first,second) together.
	 */
	Triplet [] L;
	
	void buildSuffixArray(String s) {
		int n = s.length();
		P = new int[20][n];
		L = new Triplet[n];
		for(int i=0;i<n;++i) {
			P[0][i] = s.charAt(i);
		}
		++stepsNeeded;
		for(int cnt=1, stp=1;cnt<n;cnt<<=1,++stp, ++stepsNeeded) {
			for(int i=0;i<n;++i) {
				L[i] = new Triplet(
						P[stp - 1][i],
						i+cnt < n ? P[stp-1][i+cnt] : -1,
						i);
			}
			Arrays.sort(L, (t1, t2) -> 
			t1.first.equals(t2.first)
			? t1.second.compareTo(t2.second)
			: t1.first.compareTo(t2.first));
			for(int i=0;i<n;++i) {
				P[stp][L[i].index] = (i > 0
						&& L[i-1].first == L[i].first
						&& L[i-1].second == L[i].second
						? P[stp][L[i-1].index] : i);
			}
		}
	}

	/**
	 * finds length of longest common prefix
	 * between suffix starting at x and y
	 * by using the weights stored in every step in P[][] 
	 * @param x
	 * @param y
	 * @return
	 */
	int getLCP(int x, int y, int n) {
		int ret = 0;
		for(int i=stepsNeeded-1;i>=0;--i) {
			if(x < n && y < n && P[i][x] == P[i][y]) {
				ret += 1<<i;
				x += 1<<i;
				y += 1<<i;
			}
		}
		return ret;
	}
	
	@Override
	public void solve(FastInputReader in, PrintWriter out) {
		String s = in.next();
		int n = s.length();
		buildSuffixArray(s);
		for(int i=0;i<n;++i) {
			for(int j=L[i].index;j<n;++j) {
				out.print(s.charAt(j));
			}
			out.println("");
		}
		int i = in.nextInt();
		int j = in.nextInt();
		out.println("LCP("+s.substring(i)
		+","+s.substring(j)+"): "+getLCP(i, j, n));
	}

}
