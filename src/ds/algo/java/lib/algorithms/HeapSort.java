package ds.algo.java.lib.algorithms;

import java.io.PrintWriter;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import ds.algo.java.lib.datastrucutres.heaps.BinaryMinHeap;
import ds.algo.java.lib.io.FastInputReader;

public class HeapSort implements StandardAlgoSolver{

	@Override
	public void solve(FastInputReader in, PrintWriter out) {
		BinaryMinHeap h = new BinaryMinHeap();
		Random rand = ThreadLocalRandom.current();
		int NN = 1000000;
		int [] a = new int[NN];
		for(int i=0;i<NN;++i) {
			a[i] = i + 1;
			int j = rand.nextInt(i + 1);
			int t = a[i];a[i] = a[j];a[j] = t;
		}
		for(int i=0;i<NN;++i) {
			h.push(a[i]);
		}
		out.println("After sorting:");
		int tot = 0;
		while(!h.isEmpty()) {
			a[tot++] = h.top();h.pop();
		}
		for(int i=1;i<tot;++i) {
			if(a[i] < a[i - 1]) {
				out.print("FAILED");
				return;
			}
		}
		out.println("SORTED.");
	}

}
