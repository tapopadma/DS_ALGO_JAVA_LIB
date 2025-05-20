package ds.algo.java.lib.datastrucutres.queue;

import ds.algo.java.lib.algorithms.StandardAlgoSolver;
import java.io.PrintWriter;
import ds.algo.java.lib.io.FastInputReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

public class Queues implements StandardAlgoSolver {

  // For PUSH, add element to the queue, pop from the queue and then push one by one except the last
  // element.
  // For POP, just pop from the queue.
  //
  // If 2 queues were allowed to be used then PUSH would have happened normally but during POP we
  // would pop all except last and push to second queue and pop the last element and then push all
  // the elements in the second queue back to the first queue.
  public void buildStackWithOneQueue(List<Integer> l) {
    Queue<Integer> q = new LinkedList<>();
    for (int i : l) {
      System.out.println("pushing " + i);
      int n = q.size();
      q.add(i);
      while (n-- > 0) {
        q.add(q.poll());
      }
    }
    int n = l.size();
    while (n-- > 0) {
      System.out.println("popping " + q.poll());
    }
  }

  // For PUSH, just push to the stack.
  // For POP, find the first element recursively and pop it and push back rest recursively.
  int popQueueBuiltWithOneStackNRecursion(Stack<Integer> q) {
    if (q.size() == 1) {
      return q.pop();
    }
    int last = q.pop();
    int res = popQueueBuiltWithOneStackNRecursion(q);
    q.push(last);
    return res;
  }

  public void buildQueueWithOneStackNRecursion(List<Integer> l) {
    Stack<Integer> q = new Stack<>();
    for (int i : l) {
      System.out.println("pushing " + i);
      q.push(i);
    }
    int n = l.size();
    while (n-- > 0) {
      System.out.println("popping " + popQueueBuiltWithOneStackNRecursion(q));
    }
  }

  @Override
  public void solve(FastInputReader in, PrintWriter out) {
    buildStackWithOneQueue(Arrays.asList(1, 2, 3, 4, 5));
    buildQueueWithOneStackNRecursion(Arrays.asList(1, 2, 3, 4, 5));
  }
}
