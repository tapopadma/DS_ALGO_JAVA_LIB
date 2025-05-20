package ds.algo.java.lib.algorithms;

import java.io.PrintWriter;

import ds.algo.java.lib.io.FastInputReader;
import java.util.List;

public interface StandardAlgoSolver {

  void solve(FastInputReader in, PrintWriter out);

  default void validateLogic(int x, int y) {
    if (x != y) {
      throw new RuntimeException("x , y don't match: " + x + " " + y);
    }
  }
  ;

  default void validateLogic(boolean x, boolean y) {
    if (x != y) {
      throw new RuntimeException("x , y don't match: " + x + " " + y);
    }
  }
  ;

  default void validateLogic(String x, String y) {
    if (!x.equals(y)) {
      throw new RuntimeException("x , y don't match: " + x + " " + y);
    }
  }
  ;

  default void validateLogic(List<Integer> x, List<Integer> y) {
    if (!x.equals(y)) {
      throw new RuntimeException("x , y don't match: " + x + " " + y);
    }
  }
  ;
}
