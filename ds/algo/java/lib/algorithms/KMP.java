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

    // Create hash of strings to match them. If hash matches then that's a potential match, so just
    // verify that the strings match actually.
    // The hash function used = (b^(m-1)*c0 + b^(m-2)*c1 + b^(m-3)*c3 + ... + cm_1)%p, for a base b = size of character set = 256,
    // ci being the character value, m denoting the size of the pattern and p being a prime number.s
	public void robinKarpPatternMatching(String T, String P) {
		int b = 256;
		int p = 1000000007;
		int n = T.length(), m = P.length();
		int bm1 = 1;
		for(int i=1;i<m;++i) {
			bm1 = (bm1*b)%p;
		}
		if(m > n || m == 0) {
			return;
		}
		long tHash = 0, pHash = 0;
		for(int i=0;i<n;++i) {
			if(i < m) {
				pHash = (1L*b*pHash + P.charAt(i))%p;
			}
			tHash = (1L*b*(tHash-(i >= m ? 1L*bm1*T.charAt(i-m): 0)) + T.charAt(i))%p;
			while(tHash < 0) {
				tHash += p;
			}
			if(pHash == tHash && i >= m-1) {
				boolean match = true;
				for(int j=0;j<m;++j) {
					if(T.charAt(i-m+j+1)!=P.charAt(j)) {
						match = false;break;
					}
				}
				if(match) {
					System.out.println("matched: " + (i - m + 1));
				}
			}
		}
	}

	int[] buildZ(String s) {
		int n = s.length();
		int[] z = new int[n];
		int l = 0, r = 0;
		for(int i=1;i<n;++i) {
			if(i > r) {
				l = r = i;
				while(r < n && s.charAt(r-l) == s.charAt(r)) {
					++r;
				}
				z[i] = r-l;--r;
			} else {
				if(i+z[i-l]-1 <r) {
					z[i] = z[i-l];
				} else {
					++r;l=i;
					while(r < n && s.charAt(r-l) == s.charAt(r)) {
						++r;
					}
					z[i] = r-l;--r;
				}
			}
		}
		return z;
	}

	public void zPatternMatching(String T, String P) {
		int[] z = buildZ(P + T);
		for(int i=0;i<P.length()+T.length();++i) {
			if(z[i] >= P.length()) {
				System.out.println("matched: " + (i - P.length()));
			}
		}
	}

	int [] lps;

    // Build LPS, use it during matching i.e. if there's a mismatch retry with the next character of the longest prefix
    // that is a suffix. O(n+m).
	public void kMPPatternMatching(String T, String P) {
		buildLps(P);
		matchPattern(T, P);
	}
	
	@Override
	public void solve(FastInputReader in, PrintWriter out) {
		kMPPatternMatching("aabaacaadaabaaba", "aaba");
		zPatternMatching("aabaacaadaabaaba", "aaba");
		robinKarpPatternMatching("aabaacaadaabaaba", "aaba");
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
