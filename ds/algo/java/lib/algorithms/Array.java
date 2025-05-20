package ds.algo.java.lib.algorithms;

import ds.algo.java.lib.algorithms.StandardAlgoSolver;
import ds.algo.java.lib.io.FastInputReader;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Stack;

public class Array implements StandardAlgoSolver {

  // For position i, pop any element from the stack that are smaller than l[i]. now push l[i]. next
  // greater for ith position is the top of the stack before pushing l[i].
  List<Integer> nextGreater(List<Integer> l) {
    List<Integer> res = new ArrayList<>();
    int n = l.size();
    for (int i = 0; i < n; ++i) {
      res.add(-1);
    }
    Stack<Integer> q = new Stack<>();
    for (int i = n - 1; i >= 0; --i) {
      while (!q.isEmpty() && l.get(i) >= l.get(q.peek())) {
        q.pop();
      }
      res.set(i, q.isEmpty() ? -1 : q.peek());
      q.push(i);
    }
    return res;
  }

  // Preprocess the string and mark the positions that contribute to a valid bracket sequence.
  // This means mark l[i] = 1 if s[i] is '(' and contributing to a valid bracket sequence and mark
  // r[i] = 1 if s[i] is ')' and contributing to a valid bracket sequence, else these values are 0.
  //
  // Now for each query range [x,y], the longest subsequence would require to exclude all the '('
  // that appear before x and could contribute to all the valid ')' between x and y. i.e.
  // (prefixsum(r[y]) - prefixsum(l[x-1]))*2.
  void longestValidBracketSubSequenceInQueryRange(String s, int[][] queryRanges) {
    int[] l = new int[s.length()];
    int[] r = new int[s.length()];
    Stack<Integer> q = new Stack<>();
    for (int i = 0; i < s.length(); ++i) {
      if (s.charAt(i) == '(') {
        q.push(i);
      } else {
        if (!q.isEmpty()) {
          l[q.pop()] = r[i] = 1;
        }
      }
    }
    for (int i = 1; i < s.length(); ++i) {
      l[i] = l[i - 1] + l[i];
      r[i] = r[i - 1] + r[i];
    }
    for (int i = 0; i < queryRanges.length; ++i) {
      int x = queryRanges[i][0];
      int y = queryRanges[i][1];
      int res = (r[y] - (x == 0 ? 0 : l[x - 1])) * 2;
      System.out.print(res + " ");
    }
    System.out.println("");
  }

  // Since the minimum of the subset l[i..j] decides any area of the histogram, now let's say l[i]
  // is the minimum in a subset l[j..k] then j and k should be the boundary till next smaller
  // elements on both sides.
  int maxAreaInHistograms(List<Integer> l) {
    int[] prevSmaller = new int[l.size()];
    Stack<Integer> q = new Stack<>();
    for (int i = 0; i < l.size(); ++i) {
      while (!q.isEmpty() && l.get(i) <= l.get(q.peek())) {
        q.pop();
      }
      prevSmaller[i] = q.isEmpty() ? -1 : q.peek();
      q.push(i);
    }
    q.clear();
    int[] nextSmaller = new int[l.size()];
    for (int i = l.size() - 1; i >= 0; --i) {
      while (!q.isEmpty() && l.get(i) <= l.get(q.peek())) {
        q.pop();
      }
      nextSmaller[i] = q.isEmpty() ? -1 : q.peek();
      q.push(i);
    }
    int area = 0;
    for (int i = 0; i < l.size(); ++i) {
      area =
          Math.max(
              area,
              l.get(i) * ((nextSmaller[i] == -1 ? l.size() : nextSmaller[i]) - prevSmaller[i] - 1));
    }
    return area;
  }

  // For the sliding window of size k maintain a dequeue of size k and keep adding new elements to
  // its end after removing any existing smaller elements. This way the deque will always have the
  // elements sorted in descending order and the first element will be the current maximum which can
  // be popped from front before appending a next element at the end. O(n) time, O(k) space.
  List<Integer> maxOfAllKSubArray(List<Integer> l, int k) {
    List<Integer> res = new ArrayList<>();
    Deque<Integer> q = new ArrayDeque<>();
    for (int i = 0; i < k && i < l.size(); ++i) {
      while (!q.isEmpty() && l.get(i) >= l.get(q.peekLast())) {
        q.pollLast();
      }
      q.addLast(i);
    }
    res.add(l.get(q.peekFirst()));
    for (int i = k; i < l.size(); ++i) {
      while (!q.isEmpty() && l.get(i) >= l.get(q.peekLast())) {
        q.pollLast();
      }
      q.addLast(i);
      res.add(l.get(q.peekFirst()));
      if (q.peekFirst() <= i - k) {
        q.pollFirst();
      }
    }
    return res;
  }

  @Override
  public void solve(FastInputReader in, PrintWriter out) {
    validateLogic(
        nextGreater(Arrays.asList(5, 1, 2, 5, 2, 6, 7, 2, 4, 1, 9, 8, 1, 1, 2, 2)),
        Arrays.asList(5, 2, 3, 5, 5, 6, 10, 8, 10, 10, -1, -1, 14, 14, -1, -1));
    validateLogic(maxAreaInHistograms(Arrays.asList(60, 20, 50, 40, 10, 50, 60)), 100);
    longestValidBracketSubSequenceInQueryRange(
        "())(())(())(", new int[][] {{4, 11}, {3, 4}, {0, 2}, {0, 4}, {1, 2}});
    validateLogic(
        maxOfAllKSubArray(Arrays.asList(1, 2, 3, 1, 4, 5, 2, 3, 6), 3),
        Arrays.asList(3, 3, 4, 5, 5, 5, 6));
  }
}
