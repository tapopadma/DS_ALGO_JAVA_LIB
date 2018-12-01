package ds.algo.java.lib.algorithms;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import ds.algo.java.lib.io.FastInputReader;

/**
 * Given an empty array and million valid queries
 * to 1. find maximum in the linkedlist
 * or 2. remove an element from front
 * or 3. append an element to end
 * Simple approach is to maintain a heap / AVL tree to
 * process each query in logN and hence NlogN
 * But there's better approach to maintain a doubly 
 * linkedlist of magic indexes where magic index is 
 * defined as the maximum index i from left in the array
 * such that a[j] <= a[i] for all j < i and a[j] < a[i]
 * for all j > i. The beauty of this approach is updating
 * the linked list, that is for processing query 2 just
 * keep removing the head of the linked list till it lies
 * outside the valid range. for processing query 3 just
 * add the new index to the end of linked list but before
 * that keep removing tail till its value is smaller than
 * current value. for processing query 1 just return the
 * value corresponding to the head (Because that is the
 * definition of magic index). In this way we process all
 * queries without processing a magic index more than once
 * on the linkedlist and hence O(N).
 * @author tapopadma
 *
 */
public class RMQAdvanced implements StandardAlgoSolver{
	
	
	int NN = 1000000;
	
	@Override
	public void solve(FastInputReader in, PrintWriter out) {
		List<Integer> a = new ArrayList<>();
		LinkedList<Integer> L = new LinkedList<>();
		int startIndex = 0;
		int endIndex = -1;
		int q = in.nextInt();
		while(q-- > 0) {//O(q+n)
			int type = in.nextInt();
			if(type == 1) {
				out.println(a.get(L.getFirst()));
			} else if(type == 2) {
				++startIndex;
				while(!L.isEmpty()) {
					if(L.getFirst() < startIndex) {
						L.removeFirst();
					} else {
						break;
					}
				}
			} else {
				a.add(in.nextInt());
				++endIndex;
				while(!L.isEmpty()) {
					int pos = L.getLast();
					int value = a.get(pos);
					if(value <= a.get(endIndex)) {
						L.removeLast();
					} else {
						break;
					}
				}
				L.addLast(endIndex);
			}
		}
	}
	
	
}
