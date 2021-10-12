package ds.algo.java.lib.algorithms;

import java.io.PrintWriter;

import ds.algo.java.lib.io.FastInputReader;

/**
 * Library containing basic math theorems used across CP.
 *
 */
public class Math implements StandardAlgoSolver{

    /**
     * TL;DR O(n^0.5) algo to calculate Euler's totient function aka phi(n).
     * 
     * Fermat's little theorem says: 
     * if p is prime => a^p - a = 0 mod p => if gcd(a,p)=1 a^(p-1) - 1 = 0 mod p.
     * Euler's theorem generalises above theorem and says :
     * a^phi(n)-1 = 0 mod n if gcd(a,n)=1 where phi(n) is the number of i < n such that
     * gcd(i, n) = 1.
     * For instance, phi(1) = 1, phi(2) = 1, phi(6) = 2.
     * @param n
     * @return phi(n)
     */
    public int phi(int n) {
        int ret = n;
        for(int p=2;p*p<=n;++p) {
            if(n%p==0){
                while(n%p==0)n/=p;
                ret=(ret-ret/p);
            }
        }
        if(n>1) {
            ret = (ret - ret/n);
        }
        return ret;
    }

    @Override
    public void solve(FastInputReader in, PrintWriter out) {
        int t = in.nextInt();
        while(t-->0) {
            int n = in.nextInt();
            out.println(phi(n));
        }
    }
}
