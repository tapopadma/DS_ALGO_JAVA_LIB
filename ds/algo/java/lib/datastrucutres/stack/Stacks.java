package ds.algo.java.lib.datastrucutres.stack;

import ds.algo.java.lib.algorithms.StandardAlgoSolver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import java.util.List;
import java.io.PrintWriter;
import ds.algo.java.lib.io.FastInputReader;

public class Stacks implements StandardAlgoSolver {

  void insertAtBottom(Stack<Integer> q, int value) {
    if (q.isEmpty()) {
      q.add(value);
    } else {
      int top = q.pop();
      insertAtBottom(q, value);
      q.add(top);
    }
  }

  void reverseWithoutStack(Stack<Integer> q) {
    if (q.isEmpty()) {
      return;
    }
    int top = q.pop();
    reverseWithoutStack(q);
    insertAtBottom(q, top);
  }

  Stack<Integer> build(List<Integer> l) {
    Stack<Integer> q = new Stack<>();
    for (int i = 0; i < l.size(); i++) {
      q.add(l.get(i));
    }
    return q;
  }

  List<Integer> convert(Stack<Integer> q) {
    List<Integer> l = new ArrayList<>();
    Stack<Integer> q1 = new Stack<>();
    while (!q.isEmpty()) {
      q1.add(q.pop());
    }
    while (!q1.isEmpty()) {
      l.add(q1.pop());
    }
    return l;
  }

  boolean areReversed(List<Integer> l1, List<Integer> l2) {
    if (l1.size() != l2.size()) {
      return false;
    }
    for (int i = 0; i < l1.size(); i++) {
      if (!l1.get(i).equals(l2.get(l2.size() - i - 1))) {
        return false;
      }
    }
    return true;
  }

  @Override
  public void solve(FastInputReader in, PrintWriter out) {
    List<Integer> l = Arrays.asList(1, 2, 3, 4, 5);
    Stack<Integer> q = build(l);
    reverseWithoutStack(q);
    validateLogic(areReversed(l, convert(q)), true);
  }
}
