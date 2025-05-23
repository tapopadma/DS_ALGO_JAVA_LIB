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
import java.lang.Math;

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

  // For the sliding window of size k maintain a Deque of size k and keep adding new elements to
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

  // Basically a station i would require (stations[i] - costToNextStation[i]) value to be >= 0 to be 
  // able to reach the next gas station. This means this compensation should carry forward from a circularly previous 
  // station j (< i).So an ideal solution would be to store the prefix sum of above deficit values and then 
  // checking for a given i to be the answer the minimum of prefix sum starting with i on both sides of i to be >= 0.
  //
  // But above would be O(n)time O(n)space. There's a better pattern in this i.e. while evaluating just the prefix sum
  // starting with 0, if a negative value is encountered it means the answer can't be anything <= i because everything before
  // this was positive and will make the negative value even more negative. So the answer if exists should be > i. Also since 
  // like above we want minima on both side of i to be 0. This means that the total sum should be >= 0.
  int findStartGasStationToCompleteCircularTour(List<Integer> stations, List<Integer> costToNextStation) {
    int validStart = 0;
    int gasLeft = 0;
    int totalSum = 0;
    for(int i=0;i<stations.size();++i) {
      gasLeft += stations.get(i) - costToNextStation.get(i);
      totalSum += stations.get(i) - costToNextStation.get(i);
      if(gasLeft < 0) {
        validStart = i + 1;
        gasLeft = 0;
      }
    }
    if(totalSum < 0) {
      return -1;
    }
    return validStart;
  }

  // simply, removing elements other than the end elements is going to be useless. So just targetting 
  // the element reachable from either end with max k operations and contributing to 
  // the max current diff should be simply targetted in each iteration. O(n) time & O(1) space.
  //
  // There's another O(n) time & O(n) space solution of sliding windowing n-k sized subarrays to find
  // the minimum max diff using a Deque.
  int minimiseMaxConsecutiveDifferenceByRemovingKElements(List<Integer> list, int k) {
    // given list is non-decreasing.
    k = Math.min(k, list.size() - 1);
    int l = 0;
    boolean lConsumed = true;
    int mxl = 0;
    int l1 = l;
    int r = list.size() - 1;
    boolean rConsumed = true;
    int mxr = 0;
    int r1 = r;
    while(k > 0 && l < r) {
      if(lConsumed) {
        for(int i=l+1;i<=l+k && i < list.size();++i) {
          int d = list.get(i) - list.get(i - 1);
          if(d >= mxl) {
            mxl = d;
            l1 = i - 1;
          }
        }
        lConsumed = false;
      }  
      if(rConsumed) {
        for(int i=r-1;i>=r-k && i >=0;--i) {
          int d = list.get(i + 1) - list.get(i);
          if(d >= mxr) {
            mxr = d;
            r1 = i + 1;
          }
        }
        rConsumed = false;
      }
      if(mxl >= mxr) {
        k -= (l1 - l + 1);
        l = l1 + 1;
        lConsumed = true;
        mxl = 0;
        l1 = l;
      } else {
        k -= (r - r1 + 1);
        r = r1 - 1;
        rConsumed = true;
        mxr = 0;
        r1 = 1;
      }
    }
    int ans = 0;
    for(int i=l+1;i<=r;++i) {
      ans = Math.max(ans, list.get(i) - list.get(i - 1));
    }
    return ans;
  }

  // maintain min and max over a sliding window using deques and discard from left
  // whenever diff exceeds x.
  int largestSubArrayWithMaxAnyPairDiffNotMoreThanX(List<Integer> l, int x) {
    if(x < 0){
      return 0;
    }
    Deque<Integer> qMin = new ArrayDeque<>();
    Deque<Integer> qMax = new ArrayDeque<>();
    int ans = 1;
    int start = 0;
    for(int i=0;i<l.size();++i) {
      while(!qMin.isEmpty() && l.get(i) <= l.get(qMin.getLast())) {
        qMin.removeLast();
      }
      qMin.addLast(i);
      while(!qMax.isEmpty() && l.get(i) >= l.get(qMax.getLast())) {
        qMax.removeLast();
      }
      qMax.addLast(i);
      while(l.get(qMax.getFirst()) - l.get(qMin.getFirst()) > x) {
        start = Math.min(qMin.getFirst(), qMax.getFirst()) + 1;
        if(qMin.getFirst() < qMax.getFirst()) {
          qMin.removeFirst();
        } else {
          qMax.removeFirst();
        }
      }
      ans = Math.max(ans, i-start+1);
    }
    return ans;
  }

  // ideal solution is dp where dp[i] = max sum achieved by taking l[i] with given constraints and
  // dp[i] would depend on dp[j] where j >= i-k and dp[j] is the max for any possible j. But this dp
  // would require traversing last k dps which is O(n*k).
  // To optimise a deque can be maintained to track max dp in the sliding window of size k, in O(n).
  int maximumSumSubSequenceWithAtmostKDistantConsecutiveElements(List<Integer> l, int k) {
    // l may have -ve numbers.
    Deque<Integer> q = new ArrayDeque<>();
    int []dp = new int[l.size()];
    for(int i=0;i<l.size();++i) {
      while(!q.isEmpty() && q.getFirst() < i-k) {
        q.removeFirst();
      }
      dp[i] = (q.isEmpty()?0:(i >= k ? dp[q.getFirst()] : Math.max(0, dp[q.getFirst()]))) + l.get(i);
      while(!q.isEmpty() && dp[q.getLast()] <= dp[i]) {
        q.removeLast();
      }
      q.addLast(i);
    }
    int ans = 0;
    for(int i=l.size()-k;i<l.size();++i) {
      ans = Math.max(ans, dp[i]);
    }
    return ans;
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
    validateLogic(findStartGasStationToCompleteCircularTour(Arrays.asList(1, 2, 3, 4, 5), Arrays.asList(3, 4, 5, 1, 2)), 3);
    validateLogic(minimiseMaxConsecutiveDifferenceByRemovingKElements(Arrays.asList(3, 7, 8, 10, 14), 2), 2);
    validateLogic(largestSubArrayWithMaxAnyPairDiffNotMoreThanX(Arrays.asList(8, 4, 2, 6, 7), 4), 3);
    validateLogic(largestSubArrayWithMaxAnyPairDiffNotMoreThanX(Arrays.asList(5, 10, 1, 2, 4, 7, 2), 5), 4);
    validateLogic(maximumSumSubSequenceWithAtmostKDistantConsecutiveElements(Arrays.asList(10, -5, -2, 4, 0, 3), 3), 17);
    validateLogic(maximumSumSubSequenceWithAtmostKDistantConsecutiveElements(Arrays.asList(1, -5, -20, 4, -1, 3, -6, -3), 2), 0);
  }
}
