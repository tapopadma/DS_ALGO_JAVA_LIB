package ds.algo.java.lib.datastrucutres.graphs;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.TreeSet;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.Stack;
import java.util.Arrays;

/**
 * Assume nodes numbered from 1 to n and m edges.<br>
 * To detect negative weight cycle in a graph just need to find shortest path for all nodes using
 * either Floyd Warshall or Bellman Ford and after that check if the shortest path is still shorter
 * than the evaluated ones for any node.<br>
 * Prim's MST is same logic as dijkstra; updating and choosing the total cost is the only extra
 * thing in dijkstra.
 *
 * exclusion: johnson's algo (combination of bellmanford + dijkstra).
 * 
 * @author tapopadma
 */
public class Graph {

  /** number of nodes */
  int n;

  /** number of edges */
  int m;

  boolean isDirected;

  boolean isWeighted;

  List<LinkedList<Edge>> G;

  boolean isCycleDetected;

  int[][] adj;

  UnionFind[] uf;
  List<FullEdge> edges;
  int[][] W;
  boolean[] visited;
  boolean[] isInStack;
  int[] dist;
  final int INF = 2000000000;

  List<List<Integer>> graph;
  List<List<Edge>> graphW;

  public Graph() {}

  public Graph(int n, int m, boolean isDirected, boolean isWeighted) {
    this.n = n;
    this.m = m;
    W = new int[n + 1][n + 1];
    edges = new ArrayList<>();
    this.isDirected = isDirected;
    this.isWeighted = isWeighted;
    this.isCycleDetected = false;
    G = new ArrayList<>();
    uf = new UnionFind[n + 1];
    G.add(new LinkedList<>());
    for (int i = 1; i <= n; ++i) {
      G.add(new LinkedList<>());
      uf[i] = new UnionFind(i, 0);
      for (int j = 1; j <= n; ++j) {
        W[i][j] = (i == j) ? 0 : INF;
      }
    }
  }

  public void addEdge(int u, int v) {
    addEdge(u, v, 1);
  }

  public void addEdge(int u, int v, int w) {
    G.get(u).add(new Edge(v, w));
    W[u][v] = w;
    if (!isDirected) {
      G.get(v).add(new Edge(u, w));
      W[v][u] = w;
      if (find(u) == find(v)) {
        this.isCycleDetected = true;
      }
      union(u, v);
    }
    edges.add(new FullEdge(u, v, w));
  }

  public class FullEdge {
    int from;
    int to;
    int weight;

    public FullEdge(int from, int to, int weight) {
      this.from = from;
      this.to = to;
      this.weight = weight;
    }

    public String toString() {
      return "(" + from + ", " + to + ")->" + weight;
    }
  }

  class Edge {
    int node;
    int weight;

    public Edge(int node, int weight) {
      this.node = node;
      this.weight = weight;
    }
  }

  class UnionFind {
    int parent;
    int rank;

    public UnionFind(int parent, int rank) {
      this.parent = parent;
      this.rank = rank;
    }
  }

  public Graph buildFromAdjMatrix(int[][] a) {
    n = a.length;
    adj = new int[n + 1][n + 1];
    for(int i=1;i<=n;++i){
      for(int j=1;j<=n;++j) {
        adj[i][j] = a[i-1][j-1];
      }
    }
    return this;
  }

  // 1-indexed nodes.
  public Graph buildGraph(int nodeCount, int[][] edges, boolean isDirected) {
    n = nodeCount;m = edges.length;
    graph = new ArrayList<>();
    graph.add(new ArrayList<>());
    for (int i = 1; i <= n; ++i) {
      graph.add(new ArrayList<>());
    }
    for (int i = 0; i < m; ++i) {
      graph.get(edges[i][0]).add(edges[i][1]);
      if (!isDirected) {
        graph.get(edges[i][1]).add(edges[i][0]);
      }
    }
    return this;
  }

  public Graph buildGraph(int nodeCount, int[][] edges) {
    return buildGraph(nodeCount, edges, false);
  }

  public Graph buildWeightedGraph(
      int nodeCount, int[][] edges, boolean isDirected) {
    n = nodeCount;
    m = edges.length;
    graphW = new ArrayList<>();
    graphW.add(new ArrayList<>());
    for (int i = 1; i <= n; ++i) {
      graphW.add(new ArrayList<>());
    }
    for (int i = 0; i < edges.length; ++i) {
      graphW.get(edges[i][0]).add(new Edge(edges[i][1], edges[i][2]));
      if (!isDirected) {
        graphW.get(edges[i][1]).add(new Edge(edges[i][0], edges[i][2]));
      }
    }
    return this;
  }

  public Graph buildWeightedGraph(int nodeCount, int[][] edges) {
    return buildWeightedGraph(nodeCount, edges, false);
  }

  boolean hasCycleUndirectedWithParentTracking(int x, int y) {
    if(visited[x]) {
      return true;
    }
    visited[x] = true;
    for(int e: graph.get(x)) {
      if(e == y) {
        continue;
      }
      if(hasCycleUndirectedWithParentTracking(e, x)) {
        return true;
      }
    }
    return false;
  }

  // just dfs like a tree and if there's any visited non-parent node in adjacency then return true;
  public boolean hasCycleUndirectedWithParentTracking() {
    visited = new boolean[n+1];
    for(int i=1;i<=n;++i) {
      if(!visited[i] && hasCycleUndirectedWithParentTracking(i,-1)) {
        return true;
      }
    }
    return false;
  }

  public boolean hasCycle() {
    if (isDirected) {
      return hasCycleDirected();
    }
    return hasCycleUndirected();
  }

  void union(int u, int v) {
    u = find(u);
    v = find(v);
    if (uf[u].rank == uf[v].rank) {
      ++uf[v].rank;
      uf[u].parent = v;
    } else {
      if (uf[u].rank > uf[v].rank) {
        uf[v].parent = u;
      } else {
        uf[u].parent = v;
      }
    }
  }

  int find(int x) {
    return uf[x].parent == x ? x : (uf[x].parent = find(uf[x].parent));
  }

  boolean hasCycleUndirected() {
    return isCycleDetected;
  }

  boolean dfs(int cur) {
    if (isInStack[cur]) {
      return true;
    }
    if (visited[cur]) {
      return false;
    }
    visited[cur] = true;
    isInStack[cur] = true;
    for (Edge e : G.get(cur)) {
      if (dfs(e.node)) {
        return true;
      }
    }
    isInStack[cur] = false;
    return false;
  }

  /**
   * Cycle exists if a node is visited that is still in dfs recursion stack
   *
   * @return
   */
  boolean hasCycleDirected() {
    visited = new boolean[n + 1];
    isInStack = new boolean[n + 1];
    for (int i = 1; i <= n; ++i) {
      visited[i] = false;
      isInStack[i] = false;
    }
    for (int i = 1; i <= n; ++i) {
      if (dfs(i)) {
        return true;
      }
    }
    return false;
  }

  public List<FullEdge> kruskal() {
    List<FullEdge> ret = new ArrayList<>();
    edges =
        edges.stream()
            .sorted((e1, e2) -> Integer.valueOf(e1.weight).compareTo(Integer.valueOf(e2.weight)))
            .collect(Collectors.toList());
    for (int i = 1; i <= n; ++i) {
      uf[i].parent = i;
      uf[i].rank = 0;
    }
    for (FullEdge e : edges) {
      if (find(e.from) != find(e.to)) {
        ret.add(e);
      }
      union(e.from, e.to);
    }
    return ret;
  }

  /**
   * note the difference btn this and dijkstra
   *
   * @return
   */
  public int[] prim() {
    dist = new int[n + 1];
    for (int i = 1; i <= n; ++i) {
      dist[i] = INF;
    }
    dist[1] = 0;
    TreeSet<Integer> Q =
        new TreeSet<>(
            (i, j) ->
                (dist[i] == dist[j])
                    ? Integer.valueOf(dist[i]).compareTo(Integer.valueOf(dist[j]))
                    : Integer.valueOf(i).compareTo(Integer.valueOf(j)));
    Q.add(1);
    while (!Q.isEmpty()) {
      int node = Q.pollFirst();
      for (Edge e : G.get(node)) {
        if (dist[e.node] > e.weight) {
          Q.remove(e.node);
          dist[e.node] = e.weight;
          Q.add(e.node);
        }
      }
    }
    return dist;
  }

  /**
   * Directed graph, undirected graph:
   * O(mlogn) , ds + greedy fetch the closest node then update adjacent, not for negative weights.
   *
   * @param source
   * @return
   */
  public int[] dijkstra(int source) {
    dist = new int[n + 1];
    for (int i = 1; i <= n; ++i) {
      dist[i] = INF;
    }
    dist[source] = 0;
    TreeSet<Integer> Q =
        new TreeSet<>(
            (i, j) ->
                dist[i] != dist[j]
                    ? Integer.valueOf(dist[i]).compareTo(dist[j])
                    : Integer.valueOf(i).compareTo(j));
    Q.add(source);
    while (!Q.isEmpty()) {
      int node = Q.pollFirst();
      for (Edge e : graphW.get(node)) {
        if (dist[e.node] > dist[node] + e.weight) {
          Q.remove(e.node);
          dist[e.node] = dist[node] + e.weight;
          Q.add(e.node);
        }
      }
    }
    return dist;
  }

  // Directed weighted graph: Negative cycle is the cycle with negative weight sum. 
  // A simple dfs with cumulative sum won't work because it's anyways not optimal in case there are multiple entry points
  // to a node. We can't use visited in that dfs.
  //
  // Bellman ford technique used to find shortest possible distance to each node j from a source node i. If still there's
  // an edge that leads to shorter distance from i then there's definitely a negative weight since bellman ford guarantees 
  // shortest path to all j in exactly n-1 mutations, also even if a cycle has a few negative edges but cycle sum is non-negative
  // then that would have stopped mutating the start node of the cycle which had a smaller dist value earlier. so basically the
  // cycle sum remains due in case of negative cycle. O(n*m)
  //
  // Additionally choosing an appropriate source i is also important to detect negative cycle. If a chosen i is too far
  // from negative cycle then it won't be detected.So choosing i makes it O(n^2*m). This can also be done in floyd warshall.
  public boolean detectNegativeCycle(int source) {
    dist = new int[n + 1];
    for(int i=1;i<=n;++i){
      dist[i] = Integer.MAX_VALUE;
    }
    dist[source] = 0;
    for(int i=1;i<n;++i) {
      for(int j=1;j<=n;++j) {
        for(Edge e: graphW.get(j)) {
          if(dist[j] != Integer.MAX_VALUE && dist[j] + e.weight < dist[e.node]) {
            dist[e.node] = dist[j] + e.weight;//edge relaxatioin.
          }
        }
      }
    }
    for(int j=1;j<=n;++j) {
      for(Edge e: graphW.get(j)) {
        if(dist[j] != Integer.MAX_VALUE && dist[j] + e.weight < dist[e.node]) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Directed graph, negative weight graph, negative cycle graph:
   * O(n*m) , dp compare and update dist value for m edges n-1 times. an edge may contribute to
   * shortest path of length i on ith update max length of any shortest path n - 1. each path
   * of length <= n-1 gets processed if relevant.
   *
   * @param source
   * @return
   */
  public int[] bellmanFord(int source) {
    dist = new int[n + 1];
    for (int i = 1; i <= n; ++i) {
      dist[i] = INF;
    }
    dist[source] = 0;
    for (int i = 1; i < n; ++i) {
      for(int j=1;j<=n;++j) {
        for (Edge e : graphW.get(j)) {
          if (dist[j] + e.weight < dist[e.node]) {
            dist[e.node] = dist[j] + e.weight;
          }
        }
      }
    }
    return dist;
  }

  /**
   * Directed graph, undirected graph, negative weight graph:
   * (but not negative cycle graph)
   * O(n^3) , dp for every pair we try to update cost by considering an intermediate node.
   * Basically shortest path between every pair would have been evaluated by the time all possible triplets are evaluated.
   *
   * @return
   */
  public int[][] floydWarshall() {
    int[][] d = new int[n + 1][n + 1];
    for (int i = 1; i <= n; ++i) {
      for (int j = 1; j <= n; ++j) {
        d[i][j] = W[i][j];
      }
    }
    for (int k = 1; k <= n; ++k) {
      for (int i = 1; i <= n; ++i) {
        for (int j = 1; j <= n; ++j) {
          if (d[i][j] > d[i][k] + d[k][j]) {
            d[i][j] = d[i][k] + d[k][j];
          }
        }
      }
    }
    return d;
  }

  // matrix to represent reachability of a pair of nodes. O(n^3) (for both directed & undirected graphs).
  // for sparse graph it can be O(n^2) with use of adjacency list and dfs.
  public int[][] transitiveClosure() {
    int[][] t = new int[n + 1][n + 1];
    for(int i=1;i<=n;++i) {
      for(int j=1;j<=n;++j){
        t[i][j] = adj[i][j] | (i==j?1:0);
      }
    }
    for (int k = 1; k <= n; ++k) {
      for (int i = 1; i <= n; ++i) {
        for (int j = 1; j <= n; ++j) {
          t[i][j] = t[i][j] | (adj[i][k] & adj[k][j]);
        }
      }
    }
    return t;
  }

  // start coloring with alternating pattern. if pattern is violated anywhere return false. skip
  // already colored node.
  boolean isBipartite(List<List<Integer>> graph, int node, int prevNode, int[] color) {
    if (prevNode == -1) {
      color[node] = 0;
    } else if (color[node] == color[prevNode] || node == prevNode) {
      return false;
    } else if (color[node] == -1) {
      color[node] = 1 - color[prevNode];
    } else {
      return true;
    }
    for (int i : graph.get(node)) {
      if (!isBipartite(graph, i, node, color)) {
        return false;
      }
    }
    return true;
  }

  // Bipartite graph is where every edge connects nodes from either of the exactly 2 different
  // groups.
  public boolean isBipartite(int nodeCount, int[][] edges) {
    int[] color = new int[nodeCount + 1];
    graph = new ArrayList<>();
    buildGraph(nodeCount, edges);
    for (int i = 1; i <= nodeCount; ++i) {
      color[i] = -1;
    }
    for (int i = 1; i <= nodeCount; ++i) {
      if (color[i] == -1 && !isBipartite(graph, i, -1, color)) {
        return false;
      }
    }
    return true;
  }

  boolean isReachable(List<List<Integer>> graph, int node, int target, boolean[] visited) {
    if (node == target) {
      return true;
    }
    if (visited[node]) {
      return false;
    }
    visited[node] = true;
    for (int i : graph.get(node)) {
      if (i != node && isReachable(graph, i, target, visited)) {
        return true;
      }
    }
    return false;
  }

  public boolean isReachable(int nodeCount, int[][] edges, int start, int end) {
    graph = new ArrayList<>();
    buildGraph(nodeCount, edges, true);
    boolean[] visited = new boolean[nodeCount + 1];
    return isReachable(graph, start, end, visited);
  }

  // Since there's a upper limit on the path length, we just use plain bfs till the specified path
  // length.
  int findMaxCostPathWithMaxKNodes(
      List<List<Edge>> graph, int start, int end, int k, int[] dist, int[] pathLength) {
    int maxCost = -1; // max cost path if unreachable.
    dist[start] = 0;
    pathLength[start] = 1;
    Queue<Integer> q = new ArrayDeque<>();
    q.add(start);
    while (!q.isEmpty()) {
      int i = q.poll();
      if (i == end) {
        maxCost = Math.max(maxCost, dist[i]);
      }
      for (Edge e : graph.get(i)) {
        if (dist[i] + e.weight > dist[e.node] && pathLength[i] < k) {
          q.add(e.node);
          dist[e.node] = dist[i] + e.weight;
          pathLength[e.node] = pathLength[i] + 1;
        }
      }
    }
    return maxCost;
  }

  // max cost path in a weighted digraph with max k nodes for positive edges.
  public int maxCostPathWithMaxKNodes(int nodeCount, int[][] edges, int start, int end, int k) {
    graphW = new ArrayList<>();
    int[] dist = new int[nodeCount + 1];
    int[] pathLength = new int[nodeCount + 1];
    for (int i = 1; i <= nodeCount; ++i) {
      dist[i] = -INF;
    }
    buildWeightedGraph(nodeCount, edges, true);
    return findMaxCostPathWithMaxKNodes(graphW, start, end, k, dist, pathLength);
  }

  void toposort(int x, Stack<Integer> q) {
    if(visited[x]) {
      return;
    }
    visited[x] = true;
    for(Edge e: graphW.get(x)) {
      toposort(e.node, q);      
    }
    q.add(x);
  }

  // Directed acyclic graph, negative weight graph:
  // while this can be solved using bellman ford but in O(n*m).
  // Since it's no-cycle graph, we can use dp style updates in topological order to achieve more efficient solution.
  //
  // topo sort nodes, grab each node , update its adjacent nodes' dist value. O(m).
  public int[] shortestPathDAGNegativeWeights(int source) {
    dist = new int[n + 1];
    for(int i=1;i<=n;++i) {
      dist[i] = Integer.MAX_VALUE;
    }
    visited = new boolean[n + 1];
    Stack<Integer> q = new Stack<>();
    for(int i=1;i<=n;++i) {
      toposort(i, q);
    }
    dist[source] = 0;
    while(!q.isEmpty()) {
      int x = q.pop();
      for(Edge e: graphW.get(x)) {
        if(dist[x]!=Integer.MAX_VALUE) {
          dist[e.node] = Math.min(dist[e.node], dist[x] + e.weight);
        }
      }
    }
    return dist;
  }

  // directed weighted graph, undirected weighted graph:
  // modified dijkstra would just find the path to each node that minimises
  // the max weight on the path. O(mlogn).
  public int[] minimisePathMaxima(int source) {
    dist = new int[n + 1];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[source] = 0;
    TreeSet<Integer> q = new TreeSet<>((i, j) -> dist[i]==dist[j]?Integer.valueOf(i).compareTo(j):
        Integer.valueOf(dist[i]).compareTo(dist[j]));
    q.add(source);
    while(!q.isEmpty()) {
      int i = q.pollFirst();
      for(Edge e: graphW.get(i)) {
        if(dist[e.node] > Math.max(dist[i], e.weight)) {
          q.remove(e.node);
          dist[e.node] = Math.max(dist[i],e.weight);
          q.add(e.node);
        }
      }
    }
    return dist;
  }

  // Directed weighted graph:
  // modified dijkstra, store noOfWays alongwith shortest distance in dist. for u->v if dist[u]+w == dist[v] then 
  // cumulative addition else if dist[u]+w < dist[v] then set way as 1 with updated shorter path. O(mlogN).
  public int noOfShortestPaths(int source, int target) {
    dist = new int[n + 1];int[] ways = new int[n + 1];
    Arrays.fill(dist, Integer.MAX_VALUE);Arrays.fill(ways, 0);
    dist[source] = 0;ways[source] = 1;
    TreeSet<Integer> q = new TreeSet<>((i, j) -> dist[j]==dist[j]?Integer.valueOf(i).compareTo(j)
        :Integer.valueOf(dist[i]).compareTo(dist[j]));
    q.add(source);
    while(!q.isEmpty()) {
      int x = q.pollFirst();
      for(Edge e: graphW.get(x)) {
        if(dist[e.node] > dist[x] + e.weight) {
          q.remove(e.node);
          dist[e.node] = dist[x] + e.weight;
          ways[e.node] = ways[x];
          q.add(e.node);
        } else if(dist[e.node] == dist[x] + e.weight) {
          ways[e.node] += ways[x];
        }
      }
    }
    return ways[target];
  }

  // Directed graph, undirected graph:
  // If all weights in the graph are under a small limit W, then dijkstra can be modified to choose nodes per each possible distance from 1 to (n-1)*w
  // without a need of priority queue.
  // The general idea of finding shortest path (consider dijkstra/bellman/topsort/bfs) is to find the nearest nodes and upate their adjacent first.
  //
  // So this can be done directly based on distance values, actually a dp inspired solution. O(m + W*n).
  public int[] dijkstraLightWeight(int source) {
    dist = new int[n + 1];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[source] = 0;
    List<Set<Integer>> l = new ArrayList<>();
    int W = 0;
    for(int i=1;i<=n;++i) {
      for(Edge e: graphW.get(i)) {
        W = Math.max(W, e.weight);
      }
    }
    W = W*(n - 1);
    for(int i=0;i<=W;++i) {
      l.add(new HashSet<>());
    }
    l.get(0).add(source);
    for(int i=0;i<=W;++i) {
      for(int j: l.get(i)) {
        for(Edge e: graphW.get(j)) {
          if(dist[j]+e.weight < dist[e.node]) {
            if(dist[e.node] <= W) {
              l.get(dist[e.node]).remove(e.node);
            }
            dist[e.node] = dist[j] + e.weight;
            l.get(dist[e.node]).add(e.node);
          }
        }
      }
    }
    return dist;
  }

  int findMinWeightCycle(int x, int y, int w) {
    dist = new int[n + 1];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[x] = 0;
    TreeSet<Integer> q = new TreeSet<>((i, j)->dist[i]==dist[j]?Integer.valueOf(i).compareTo(j)
      : Integer.valueOf(dist[i]).compareTo(dist[j]));
    q.add(x);
    while(!q.isEmpty()) {
      int i = q.pollFirst();
      for(Edge e: graphW.get(i)) {
        if(!((x==i && y==e.node)||(y==i && x==e.node)) && dist[e.node] > dist[i] + e.weight) {
          q.remove(e.node);
          dist[e.node] = dist[i] + e.weight;
          q.add(e.node);
        }
      }
    }
    return dist[y] + w;
  }

  // Undirected weighted graph:
  // Consider each edge and check the shortest path to reach from u to v without using edge u-v. O(m*mlogn).
  public int findMinWeightCycle() {
    int minWeightSum = Integer.MAX_VALUE;
    for(int i=1;i<=n;++i) {
      for(Edge e: graphW.get(i)) {
        minWeightSum = Math.min(minWeightSum, findMinWeightCycle(i, e.node, e.weight));
      }
    }
    return minWeightSum;
  }

}
