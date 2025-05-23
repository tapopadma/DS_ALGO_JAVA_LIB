package ds.algo.java.lib.datastrucutres.queue;

import ds.algo.java.lib.algorithms.StandardAlgoSolver;
import java.io.PrintWriter;
import ds.algo.java.lib.io.FastInputReader;
import java.util.List;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.stream.Collectors;
import java.util.Arrays;

public class Deques implements StandardAlgoSolver {

  // With an initial list {0} and a string s, it's asked to insert a number i (i>=1 && i<=n)
  // to the front of i-1 if s[i] = 'F' else to the back of i-1.
  //
  // Simplest way is to process in the reverse order and just append a smaller number either to the back or front
  // of the deque.
  List<Integer> insertOnEitherSideOfLastInsertedElement(String s) {
    Deque<Integer> q = new ArrayDeque<>();
    q.addLast(s.length());
    for(int i=s.length()-2;i>=-1;--i) {
      if(s.charAt(i+1) == 'F') {
        q.addLast(i+1);
      } else {
        q.addFirst(i+1);
      }
    }
    return q.stream().collect(Collectors.toList());
  }

  @Override
  public void solve(FastInputReader in, PrintWriter out) {
    validateLogic(insertOnEitherSideOfLastInsertedElement("FBBFB"), Arrays.asList(1,2,4,5,3,0));
    validateLogic(insertOnEitherSideOfLastInsertedElement("BBBBBB"), Arrays.asList(0,1,2,3,4,5,6));
  }
}
