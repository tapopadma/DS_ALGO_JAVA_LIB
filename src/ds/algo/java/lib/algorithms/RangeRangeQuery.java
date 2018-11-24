package ds.algo.java.lib.algorithms;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ds.algo.java.lib.datastrucutres.trees.FenwickTree;
import ds.algo.java.lib.io.FastInputReader;

/**
 * @author tapopadma<br>
 *         Given an Array A of length N (1e5) Given Q (1e5) queries where each
 *         query is L R X Y i.e find the number of elements in the range [L, R]
 *         that lie in the Range [X, Y] X,Y,L,R <= 1e5 Sol: O(QlogN)
 */
public class RangeRangeQuery implements StandardAlgoSolver {

	int N = 100005;
	int[] a = new int[N];
	List<Integer> aa;
	int[][] qL = new int[N][4];

	@Override
	public void solve(FastInputReader in, PrintWriter out) {
		int n = in.nextInt();
		aa = new ArrayList<>();
		for (int i = 0; i < n; ++i) {
			a[i] = in.nextInt();
			aa.add(a[i]);
		}
		aa = aa.stream().sorted().collect(Collectors.toList());
		int q = in.nextInt();
		Map<Integer, List<CustomQuery>> numToIndexListMap = new HashMap<>();
		for (int i = 0; i < q; ++i) {
			int l = in.nextInt(), r = in.nextInt(), x = in.nextInt(), y = in.nextInt();
			--l;
			--r;
			int ix = getIndexOfMinimumNotSmaller(x);
			if(ix == -1) {
				continue;
			}
			int iy = getIndexOfMaximumNotGreater(y);
			if(iy == -1) {
				continue;
			}
			int xx = (ix > 0 ? aa.get(ix - 1) : -1);
			int yy = aa.get(iy);
			
			List<CustomQuery> queries = new ArrayList<>();
			int index = r;
			int num = yy;
			if (num != -1) {
				if (numToIndexListMap.containsKey(num)) {
					queries = numToIndexListMap.get(num);
				}
				queries.add(new CustomQuery(index, 0, i));
				numToIndexListMap.put(num, queries);
			}

			if (num != -1 && l > 0) {
				index = l - 1;
				queries.add(new CustomQuery(index, 1, i));
				numToIndexListMap.put(num, queries);
			}

			queries = new ArrayList<>();
			index = r;
			num = xx;
			if (num != -1) {
				if (numToIndexListMap.containsKey(num)) {
					queries = numToIndexListMap.get(num);
				}
				queries.add(new CustomQuery(index, 2, i));
				numToIndexListMap.put(num, queries);
			}

			if (num != -1 && l > 0) {
				index = l - 1;
				queries.add(new CustomQuery(index, 3, i));
				numToIndexListMap.put(num, queries);
			}
		}
		List<Integer> b = new ArrayList<>();
		for (int i = 0; i < n; ++i) {
			b.add(i);
		}
		b = b.stream().sorted((b1, b2) -> Integer.valueOf(a[b1])
				.compareTo(Integer.valueOf(a[b2])))
				.collect(Collectors.toList());

		FenwickTree T = new FenwickTree(new int[n]);

		for (int i = 0; i < b.size(); ++i) {
			int idx = b.get(i);
			T.update(idx, +1);
			if (i == b.size()-1 || a[b.get(i + 1)] != a[idx]) {
				if (numToIndexListMap.containsKey(a[idx])) {
					List<CustomQuery> queries = numToIndexListMap.get(a[idx]);
					for (CustomQuery cq : queries) {
						qL[cq.queryIndex][cq.part] = T.query(cq.index);
					}
				}
			}
		}
		for (int i = 0; i < q; ++i) {
			out.println(qL[i][0] - qL[i][1] - qL[i][2] + qL[i][3]);
		}
	}

	int getIndexOfMaximumNotGreater(int num) {
		int lo = 0, hi = aa.size() - 1, mid;
		while (lo < hi) {
			mid = (lo + hi + 1) / 2;
			if (aa.get(mid) == num) {
				lo = mid;
				break;
			}
			if (aa.get(mid) > num) {
				hi = mid - 1;
			} else {
				lo = mid + 1;
			}
		}
		return (aa.get(lo) <= num ? lo : -1);
	}

	int getIndexOfMinimumNotSmaller(int num) {
		int lo = 0, hi = aa.size() - 1, mid;
		while (lo < hi) {
			mid = (lo + hi) / 2;
			if (aa.get(mid) == num) {
				lo = mid;
				break;
			}
			if (aa.get(mid) < num) {
				lo = mid + 1;
			} else {
				hi = mid - 1;
			}
		}
		return (aa.get(lo) >= num ? lo : -1);
	}

	class CustomQuery {
		int index;
		int part;
		int queryIndex;

		public CustomQuery(int index, int part, int queryIndex) {
			this.index = index;
			this.part = part;
			this.queryIndex = queryIndex;
		}
	}

}
