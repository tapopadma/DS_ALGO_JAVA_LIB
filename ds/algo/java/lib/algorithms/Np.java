package ds.algo.java.lib.algorithms;

/**
 * Both NP (complete) and NP hard problems are being solved here. NP problems are basically the
 * problems which can't be solved in polynomial time (e.g. O(n^2), O(n^3) etc), but exponential time
 * (e.g. O(2^n) etc) and their solutions can be verified in polynomial time.
 *
 * <p>NP - NP or NP complete problems have the solution that can be verified in polynomial time.
 * e.g. Travelling Salesman Problem, subset sum etc.
 *
 * <p>NP hard - NP hard problems have the solution that can't be verified in polynomial time. e.g.
 * Tower of hanoi, Halting problem etc.
 */
public final class Np {

  void move(int n, char a, char b, char c) {
    if (n == 0) {
      return;
    }
    move(n - 1, a, c, b);
    System.out.println(n + " moved from " + a + " to " + b);
    move(n - 1, c, b, a);
  }

  // total number of moves is 2^n - 1. so iterate in each step i as follows:
  // 1. i%3 == 0: Move top disk between A and C
  // 2. i%3 == 1: Move top disk between A and B
  // 3. else: Move top disk between C and B.
  // but we will solve with just recursion for simplicity.
  public void solveTowerOfHanoi(int n) {
    move(n, 'a', 'b', 'c');
  }
}
