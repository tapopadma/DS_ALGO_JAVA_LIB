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


  // The usual array based queue implementation is straightforward for enqueueing but takes O(n) time for Dequeing.
  // The circular array based queue implementation is O(1) for all where the actual queue just floats around the circle
  // based on operations. Similar approach can be used for Deques too.
  public void buildQueueWithCircularArray(List<Integer> l) {
    System.out.println("buildQueueWithCircularArray==");
    int[] arr = new int[l.size()];
    int front = 0;
    int size = 0;
    for(int i=0;i < l.size()/2;++i) {
      System.out.println("pushing " + l.get(i));
      arr[(front + size)%l.size()] = l.get(i);
      size++;
    }
    for(int i=0;i < l.size()/2;++i) {
      int popped = arr[front];
      System.out.println("popping " + popped);
      front = (front + 1)%l.size();
      size--;
      System.out.println("pushing " + popped);
      arr[(front + size)%l.size()] = popped;
      size++;
    }
    for(int i=l.size()/2;i < l.size();++i) {
      System.out.println("pushing " + l.get(i));
      arr[(front + size)%l.size()] = l.get(i);
      size++;
    }
    for(int i=0;i < l.size();++i) {
      int popped = arr[front];
      System.out.println("popping " + popped);
      front = (front + 1)%l.size();
      size--;
    }
  }

  @Override
  public void solve(FastInputReader in, PrintWriter out) {
    buildStackWithOneQueue(Arrays.asList(1, 2, 3, 4, 5));
    buildQueueWithOneStackNRecursion(Arrays.asList(1, 2, 3, 4, 5));
    buildQueueWithCircularArray(Arrays.asList(1, 2, 3, 4, 5, 6));
  }
}
