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
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.PriorityQueue;

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

  class NonRepeatingElementSubArrayNode {
    public int i;
    public int po;
    public int no;
    public NonRepeatingElementSubArrayNode() {}
  }

  class NonRepeatingElementSubArraySegmentTree {
    int n;
    int[] T;
    static final int MIN = -2000000000;
    public NonRepeatingElementSubArraySegmentTree(int n){
      this.n = n;
      T = new int[n*4];
      for(int i=0;i<n*4;++i) {
        T[i] = MIN;
      }
    }
    void update(int i, int l, int r, int x, int v) {
      if(x > r || x < l){
        return;
      }
      if(l == r) {
        T[i] = v;
        return;
      }
      int m= (l + r) / 2;
      update(i*2, l, m, x, v);
      update(1+i*2, 1+m, r, x, v);
      T[i] = Math.max(T[i*2], T[i*2+1]);
    }
    public void update(int x, int v) {
      update(1, 1, n, x, v);
    }
    int query(int i, int l, int r, int x, int y) {
      if(x > r || y < l) {
        return MIN;
      }
      if(x <= l && r <= y) {
        return T[i];
      }
      int m = (l + r) / 2;
      return Math.max(query(2*i, l, m, x, y), query(1+2*i, 1+m, r, x, y));
    }
    public int query(int x, int y) {
      return query(1, 1, n, x, y);
    }
  }

  // To be able to say if there's an element in a subarray [L,R] whose frequency is exactly 1,
  // we should just check if its previous occurence po and next occurence no are < l and > r respectively.
  //
  // Just storing an element along with its both occurences (po,i,no) and sorting based on po and then
  // finding prefix maximum no for i in given [L,R] in the query such that prefix contains only elements with po < L
  // should be enough. For po to be < L, offline processing of queries is necessary and For storing range maximum segment
  // tree is necessary. O(NlogN + QlogN), O(N).
  void checkNonRepeatingElementExistenceInSubarray(List<Integer> l, List<List<Integer>> queries) {
    Map<Integer, Integer> mp = new HashMap<>();
    List<NonRepeatingElementSubArrayNode> list = new ArrayList<>();
    for(int i=0;i<l.size();++i) {
      NonRepeatingElementSubArrayNode node = new NonRepeatingElementSubArrayNode();
      node.i = i;
      node.po = mp.containsKey(l.get(i)) ? mp.get(l.get(i)) : -1;
      list.add(node);
      mp.put(l.get(i), i);
    }
    mp.clear();
    for(int i=l.size()-1;i>=0;--i) {
      list.get(i).no = mp.containsKey(l.get(i)) ? mp.get(l.get(i)) : l.size();
      mp.put(l.get(i), i);
    }
    Collections.sort(list, (n1, n2) -> Integer.valueOf(n1.po).compareTo(n2.po));
    for(int i=0;i< queries.size();++i) {
      queries.get(i).add(i);
    }
    boolean[] ans = new boolean[queries.size()];
    Collections.sort(queries, (query1, query2) -> Integer.valueOf(query1.get(0)).compareTo(query2.get(0)));
    NonRepeatingElementSubArraySegmentTree tree = new NonRepeatingElementSubArraySegmentTree(l.size() + 1);
    int i = 0, j = 0;
    while(j < queries.size()) {
      while(i < list.size() && list.get(i).po + 1 < queries.get(j).get(0)) {
        tree.update(list.get(i).i + 1, list.get(i).no + 1);
        ++i;
      }
      ans[queries.get(j).get(2)] = tree.query(queries.get(j).get(0), queries.get(j).get(1)) > queries.get(j).get(1);
      ++j;
    }
    for(boolean yn: ans) {
      System.out.print(yn ? "Yes " : "No ");
    }
    System.out.println();
  }

  public int minSwapsToSortArray(List<Integer> l) {
    List<Integer> lcopy = new ArrayList<>(l);
    Collections.sort(lcopy);
    Map<Integer, Integer> mp = new HashMap<>();
    for(int i=0;i<l.size();++i) {
      mp.put(lcopy.get(i), i);
    }
    int ans = 0;
    boolean[] vis = new boolean[Collections.max(l)+1];
    for(int i=0;i<l.size();++i) {
      int x = l.get(i);
      if(vis[x] || mp.get(x)==i) {
        continue;
      }
      int cnt = 0;
      while(!vis[x]) {
        ++cnt;
        vis[x] = true;
        x = l.get(mp.get(x));
      }
      ans += cnt - 1;
    }
    return ans;
  }

  // greedy: for position i maintain prevMaxCoverage and curMaxCoverage, if prevMaxCoverage ends at i then a jump is necessary so increment jump if 
  // curMaxCoverage is ahead else return -1, Also update prevMaxCoverage to cur one. O(n)
  public int minJumpsToReachEndOfArray(int[] a) {
    int prevMaxCoverage = 0;// The maximum reachability before i.
    int curMaxCoverage = 0;// The maximum reachability i onwards.
    int minJump = 0;
    for(int i=0;i<a.length;++i) {
      curMaxCoverage = Math.max(curMaxCoverage, i+a[i]);
      if(curMaxCoverage >= a.length-1) {
        return minJump+1;
      }
      if(prevMaxCoverage==i) { // no more jump possibility from preceeding points, so a jump from current point is the only way.
        if(curMaxCoverage == i) { // no more jump possibility from current point either.
          return -1;
        } else {
          prevMaxCoverage = curMaxCoverage;
          ++minJump;
        }
      }
    }
    return -1;
  }

  int findLISInsertionPoint(List<Integer> list, int key) {
    if(list.isEmpty() || list.get(list.size()-1) < key) {
      return list.size();
    }
    int lo = 0, hi = list.size() - 1, mid = 0;
    while(lo < hi) {
      mid = (lo+hi)/2;
      if(list.get(mid) >= key) {
        hi=mid;
      } else {
        lo = mid + 1;
      }
    }
    return lo;
  }

  // Greedy: At i maintain sorted subsequence list such that if a[i] > last element in it just append, else just find insert point j such that a[i] <= l[j],
  // and just replace l[j] with a[i] to enhance any chance of the list being appendable without decreasing its length at all. This can also print lis
  // with parent[] array. O(nlogn)
  //
  // alternative is to use dp+RMQ per i but that could need encoding big numbers and extra space for segment tree.
  public int longestIncreasingSubsequence(int[] a) {
    List<Integer> list = new ArrayList<>();
    for(int val: a) {
      int idx = findLISInsertionPoint(list, val);
      if(idx == list.size()) {
        list.add(val);
      } else {
        list.set(idx, val);
      }
    }
    return list.size();
  }

  // dp: place 1 in one of the (n-1) places j, if j comes to place 1 then dp[n-2] else still dp[n-1] to derange in the places except first place.
  // dp[n] = (n-1)*(dp[n-2]+dp[n-1])
  public int countDerangements(int n) {
    int[] dp = new int[n + 1];
    dp[0]=1;dp[1]=0;
    for(int i=2;i<=n;++i) {
      dp[i] = (i-1)*(dp[i-2]+dp[i-1]);
    }
    return dp[n];
  }

  // max non-overlapping interval: sort by end time and validate. O(nlogn)
  public int maxActivitySelection(int[][] a) {
    Arrays.sort(a,(a1,a2)->Integer.compare(a1[1], a2[1]));
    int maxActivity = 0;
    int allowedStart = 0;
    for(int i=0;i<a.length;++i) {
      if(a[i][0] <= allowedStart) {
        continue;
      }
      allowedStart = a[i][1];
      ++maxActivity;
    }
    return maxActivity;
  }

  // schedule the jobs (each takes 1 unit time to finish) within their deadline to achieve max profit of running the job.
  //
  // By the time t we must choose top t highest profit jobs with deadline <= t. Process in sorted deadline to not miss out
  // a job to be run just because their was higher profit job that could be executed.
  public int jobSequenceSelection(int[] deadline, int[] profit) {
    int[][] a = new int[deadline.length][2];
    for(int i=0;i<deadline.length;++i){
      a[i][0] = deadline[i];a[i][1] = profit[i];
    }
    PriorityQueue<Integer> jobsTobeExecuted = new PriorityQueue<>();//profit
    Arrays.sort(a, (a1,a2)->Integer.compare(a1[0],a2[0]));
    for(int i=0;i<a.length;++i) {
      int t = a[i][0];
      if(jobsTobeExecuted.size() < t) {
        jobsTobeExecuted.add(a[i][1]);
      } else {
        if(jobsTobeExecuted.peek() < a[i][1]) {// assume deadline[] has only positive integers.
          jobsTobeExecuted.poll();
          jobsTobeExecuted.add(a[i][1]);
        }
      }
    }
    int maxTotalProfit = 0;
    for(int p: jobsTobeExecuted) {
      maxTotalProfit += p;
    }
    return maxTotalProfit;
  }

  public void mergeIntervals(int[][] a) {
    Arrays.sort(a, (a1,a2)->Integer.compare(a1[0],a2[0]));
    int lastEnd = -1;
    for(int i=0;i<a.length;++i) {
      if(a[i][0] > lastEnd) {
        if(lastEnd >= 0) {
          System.out.println(lastEnd);
        }
        lastEnd = a[i][1];
        System.out.print(a[i][0] + " ");
      } else {
        lastEnd = Math.max(lastEnd, a[i][1]);
      }
    }
    System.out.println(lastEnd);
  }

  public int maxIntervalOverlaps(int[][] a) {
    Arrays.sort(a, (a1,a2)->Integer.compare(a1[0],a2[0]));
    PriorityQueue<Integer> q = new PriorityQueue<>((i,j)->Integer.compare(a[i][1],a[j][1]));
    int maxOverlap = 0;
    for(int i=0;i<a.length;++i) {
      while(!q.isEmpty() && a[q.peek()][1] < a[i][0]) {
        q.poll();
      }
      q.add(i);
      maxOverlap = Math.max(maxOverlap, q.size());
    }
    return maxOverlap;
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
    checkNonRepeatingElementExistenceInSubarray(Arrays.asList(1,2,1,3,2,3,4,4,2,3,5,6,2,1,8,9,2),
      Arrays.asList(dynamicList(1, 6),dynamicList(7,8), dynamicList(6, 10), dynamicList(1, 17)));
    validateLogic(minSwapsToSortArray(Arrays.asList(10, 19, 6, 3, 5)), 2);
    validateLogic(3, minJumpsToReachEndOfArray(new int[]{1, 3, 5, 8, 9, 2, 6, 7, 6, 8, 9}));
    validateLogic(2, minJumpsToReachEndOfArray(new int[]{1, 4, 3, 2, 6, 7}));
    validateLogic(-1, minJumpsToReachEndOfArray(new int[]{0,10,20}));
    validateLogic(3,longestIncreasingSubsequence(new int[]{3, 10, 2, 1, 20}));
    validateLogic(4,longestIncreasingSubsequence(new int[]{50, 3, 10, 7, 40, 80}));
    validateLogic(1,longestIncreasingSubsequence(new int[]{3, 2}));
    validateLogic(9, countDerangements(4));
    validateLogic(4, maxActivitySelection(new int[][]{new int[]{1,2},new int[]{3,4},new int[]{0,6},new int[]{5,7},new int[]{8,9},new int[]{5,9}}));
    validateLogic(1, maxActivitySelection(new int[][]{new int[]{10,20},new int[]{12,25},new int[]{20,30}}));
    validateLogic(127, jobSequenceSelection(new int[]{2, 1, 2, 1, 1},new int[]{100, 19, 27, 25,15}));
    mergeIntervals(new int[][]{new int[]{7, 8}, new int[]{1, 5}, new int[]{2, 4}, new int[]{4, 6}});
    validateLogic(3, maxIntervalOverlaps(new int[][]{
      {900, 910},
      {940, 1200},
      {950, 1120},
      {1100, 1130},
      {1500, 1900},
      {1800, 2000}}));
    validateLogic(1, maxIntervalOverlaps(new int[][]{{1,3},{5,7}}));
  }
}
