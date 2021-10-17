package ds.algo.java.lib.algorithms;

import java.io.PrintWriter;

import ds.algo.java.lib.io.FastInputReader;


/**
 * The key idea:<br>
 * Assume a window of size k on string T that is a prefix match
 * on P. Also assume that the next character is not a match or
 * k = P.length(). Now our job is to find next start point
 * l on T such that l is not to the right of the current window and
 * it might match with P from l onwards. This implies all characters
 * from l till end of current window must match with prefix of P
 * because P.length() >= currentWindow.end - l + 1. But since
 * current window is already a match with the prefix of P hence
 * this remainder segment must be same as suffix of the same length.
 * Hence this remainder segment must be match with prefix as well as
 * suffix of P. But we must look for the largest such remainder segment
 * as larger its length lesser we slide P over T rightwards and 
 * hence no chance to miss any match. Hence we calc lps of P and 
 * just increment i = i + 1 whereas j = lps[j].<br>
 * The idea to calculate lps:<br>
 * this is standard dp approach. every time new character added
 * we would look for the first valid longest prefix that matches
 * with suffix.
 * total time: O(n)
 * @author tapopadma
 *
 */
public class KMP implements StandardAlgoSolver{

	int [] lps;
	
	@Override
	public void solve(FastInputReader in, PrintWriter out) {
		String T = in.next();
		String P = in.next();
		buildLps(P);
		matchPattern(T, P);
	}

	void matchPattern(String T, String P) {
		int n = T.length(), m = P.length();
		for(int i=0, j=0;i<n;++i) {
			if(T.charAt(i) == P.charAt(j)) {
				++j;
				if(j == m) {
					System.out.println("matched: " + (i - m + 1));
					j = lps[j - 1];
				}
			} else {
				if(j > 0) {
					--i;
					j = lps[j - 1];
				}
			}
		}
	}
	
	void buildLps(String P) {
		int n = P.length();
		lps = new int[n];
		lps[0] = 0;
		int ptr = 0;
		for(int i=1;i<n;++i) {
			if(P.charAt(ptr) == P.charAt(i)) {
				lps[i] = ++ptr;
			} else {
				lps[i] = 0;
				while(ptr > 0) {
					ptr = lps[ptr - 1];
					if(P.charAt(ptr) == P.charAt(i)) {
						lps[i] = ++ptr;break;
					}
				}
			}
		}
	}
	
}
