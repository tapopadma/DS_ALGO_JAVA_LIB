package ds.algo.java.lib.datastrucutres.trees;


/**
 * 
 * @author tapopadma <br>
 * A tree that can give prefix sum 
 * of an array(zero-indexed) in O(logN) keeping track of
 * each update by index. Update is also done in O(logN).<br>
 * <b>Analysis:<b><br>
 * Any update on any index from i-(i&-i) + 1 to i is 
 * simply added to i in logN operation. Thus prefix sum 
 * on ith index would be
 * sum of all such indexes i, i-(i&-i), ....
 */
public class FenwickTree {

	int [] T;
	int n = 0;
	
	// How it works ?
	// Basically this tree helps query prefix sum of i by splitting i into sum of powers of 2 and also by storing sum of powers of 2.
	// e.g. sum(1..12) = sum(9..12) + sum(1..8)
	public FenwickTree(int [] a) {
		n = a.length;
		T = new int[n + 1];
		for(int i=0;i<n;++i) {
			update(i + 1, a[i]);
		}
	}
	
	/**
	 * Next index is obtained by adding decimal value
	 * of the least significant bit to the current index.
	 * @param index
	 * @param value
	 */
	public void update(int index, int value) {
		++index;
		while(index <= n) {
			T[index] += value;
			index += index&-index;
		}
	}
	
	/**
	 * Next index is obtained by toggling
	 * the least significant bit of the current index
	 * @param index
	 * @return
	 */
	public int query(int index) {
		++index;
		int ret = 0;
		while(index > 0) {
			ret += T[index];
			index -= index&-index;
		}
		return ret;
	}
	
}
