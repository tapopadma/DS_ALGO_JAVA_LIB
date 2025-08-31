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
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

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
  int[] degree;
  int[] discovered;
  int[] minDiscovered;

  final int INF = 2000000000;
  int globalTimer = 0;

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

    public UnionFind(){}

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

  Map<Integer, Set<Integer>> buildMutableGraph() {
    Map<Integer, Set<Integer>> mutableGraph = new HashMap<>();
    for(int i=1;i<=n;++i) {
      mutableGraph.put(i, new HashSet<>());
      for(int j: graph.get(i)) {
        mutableGraph.get(i).add(j);
      }
    }
    return mutableGraph;
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

  // undirected weighted graph
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
   * Undirected weighted graph:
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
                (dist[i] != dist[j])
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

  void sortTopologically(int x, Stack<Integer> q, boolean isWeighted) {
    if(visited[x]){
      return;
    }
    visited[x] = true;
    if(isWeighted) {
      for(Edge e: graphW.get(x)) {
        sortTopologically(e.node, q, isWeighted);
      }
    } else {
      for(int y: graph.get(x)) {
        sortTopologically(y, q, isWeighted);
      }
    }
    q.push(x);
  }

  // Directed acyclic graphs ONLY.
  // just process the nodes in any order. Process all the descendants of the node and then push it to the stack.
  // At the end unload the stack. O(m) O(n)
  // Alternative is Kahn's algo: find all nodes with 0 indegree and push to Q, perform bfs while decrementing each child's indegree & tracking 0 indegree.
  public List<Integer> sortTopologically(boolean isWeighted) {
    Stack<Integer> q = new Stack<>();
    visited = new boolean[n+1];
    for(int i=1;i<=n;++i) {
      sortTopologically(i, q, isWeighted);
    }
    List<Integer> l = new ArrayList<>();
    while(!q.isEmpty()) {
      int x = q.pop();
      l.add(x);
      System.out.print(x + " ");
    }
    System.out.println();
    return l;
  }

  public List<Integer> sortTopologically() {
    return sortTopologically(false);
  }

  void sortTopologicallyUsingDepartureTime(int x, int[] departure, int[] timer) {
    if(visited[x]) {
      return;
    }
    visited[x] = true;
    for(int y: graph.get(x)) {
      sortTopologicallyUsingDepartureTime(y, departure, timer);
    }
    departure[++timer[0]] = x;
  }

  // DAG:
  // In the dfs for a given node if we say entry of the node for processing as arrival and exit of the node after all its descendants are processed as
  // departure, then the default approach (stack based) can be interpreted as the decreasing order of departure time of the nodes. O(m) O(n)
  public void sortTopologicallyUsingDepartureTime() {
    visited = new boolean[n + 1];
    int[] departure = new int[n + 1];
    int[] timer = new int[]{0};
    for(int i=1;i<=n;++i) {
      sortTopologicallyUsingDepartureTime(i, departure, timer);
    }
    for(int i=n;i>=1;--i) {
      System.out.print(departure[i] + " ");
    }
    System.out.println();
  }

  // Summary: 
  // Undirected unweighted graph: np-hard for cyclic graphs, for acyclic/trees just recursively dfs to find diameter (or) do double bfs to find farthest of farthest.
  // Undirected weighted graph: np-hard for cyclic graphs, for acyclic/trees just recursively dfs to find diameter (or) do double bfs to find farthest of farthest.
  // Directed unweighted graph: np-hard if cycles, for DAG dp style updates can be done to distances of nodes in topological sorted order (only).
  // Directed weighted graph: np-hard if cycles, for DAG dp style updates can be done to distances of nodes in topological sorted order (only).
  //
  // Directed Acyclic Graph (DAG) ONLY:
  // This is similar to finding shortest path for DAGs. Since toposorting are only valid for DAGs and topo sorting performs evaluation of all the ancestor nodes
  // that point to a given node to evaluate for a given node, dp based on toposorting is the way. O(m).
  public int longestPath() {
    dist = new int[n + 1];
    Arrays.fill(dist, Integer.MIN_VALUE);
    List<Integer> topoSortedNodes = sortTopologically(true);
    HashSet<Integer> sources = new HashSet<>();
    for(int i=1;i<=n;++i) {
      sources.add(i);
    }
    for(int i=1;i<=n;++i) {
      for(Edge e: graphW.get(i)) {
        if(sources.contains(e.node)) {
          sources.remove(e.node);
        }
      }
    }
    for(int x: sources) {
      dist[x] = 0;
    }
    int maxDistanceAlongTheEdges = 0;
    for(int x: topoSortedNodes) {
      for(Edge e: graphW.get(x)) {
        dist[e.node] = Math.max(dist[e.node], dist[x] + e.weight);
        maxDistanceAlongTheEdges = Math.max(maxDistanceAlongTheEdges, dist[e.node]);
      }
    }
    return maxDistanceAlongTheEdges;
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

  // Summary for shortest path:
  // Undirected unweighted: bfs irrespective of cycle existence
  // Undirected +ve weighted: dijkstra irrespective of cycle existence
  // Undirected -ve weighted: bellman ford / floyd warshall
  // Directed unweighted: bfs / dijkstra irrespective of cycle existence
  // Directed cyclic +ve weighted: dijkstra 
  // Directed cyclic -ve weighted: bellman ford
  // Directed acyclic weighted: topo-sorted dp irrespective of +/- weights. 
  //
  // Directed acyclic graph, negative weight graph:
  // while this can be solved using bellman ford but in O(n*m).
  // Since it's no-cycle graph, we can use dp style updates in topological order to achieve more efficient solution.
  // Dijkstra won't work here because dijkstra doesn't process longer entry point to a node if the shorter entry point to it is already processed because it assumes
  // the longer entry point is anyway going to get bigger with more weights added to it (due to +ve weight assumption).
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

  // villages 1 to N need to be connected for water with least cost by either building well or connecting a pipe with another village.
  // 
  // Building a well in i can be remodelled as connecting i to a pseudo vertex 0 at the cost = wellCost[i].
  // With this the problem is as simple as finding mst in a weighted undirected graph. O(mlogm). 
  public int pesudoVertexKruskal(int villageCount, int[] wellCost, int[][] pipeCost) {
    n = villageCount;
    List<FullEdge> edges = new ArrayList<>();
    for(int i=1;i<=n;++i) {
      edges.add(new FullEdge(0, i, wellCost[i - 1]));
    }
    for(int[] edge: pipeCost) {
      edges.add(new FullEdge(edge[0], edge[1], edge[2]));
    }
    Collections.sort(edges, (e1, e2) -> Integer.compare(e1.weight, e2.weight));
    uf = new UnionFind[n + 1];
    for(int i=0;i<=n;++i) {
      uf[i] = new UnionFind();
      uf[i].parent = i;
      uf[i].rank = 0;
    }
    int minCost = 0;
    for(FullEdge e: edges) {
      if(find(e.from) != find(e.to)) {
        minCost += e.weight;
        union(e.from, e.to);
      }
    }
    return minCost;
  }

  void findArticulationPoints(int x, int px, int[] timer) {
    visited[x] = true;
    discovered[x] = ++timer[0];
    minDiscovered[x] = discovered[x];
    int childSubTreeWithoutBackedge = 0;
    for(int y: graph.get(x)) {
      if(y == px) {
        continue;
      }
      if(visited[y]) {
        minDiscovered[x] = Math.min(minDiscovered[x], discovered[y]);
      } else {
        findArticulationPoints(y, x, timer);
        minDiscovered[x] = Math.min(minDiscovered[x], minDiscovered[y]);
        if(minDiscovered[y] >= discovered[x]) {
          childSubTreeWithoutBackedge++;
        }
      }
    }
    if(childSubTreeWithoutBackedge > 0) {
      // atleast 1 case of no ancestral connection, this could be articulation point if not a root.
      if(px != -1) {
        System.out.print(x + " ");
      } else if(childSubTreeWithoutBackedge > 1) { // in case of root the noOf independent subtrees without backedge mustBe >= 2.
        System.out.print(x + " ");
      }
    }
  }

  // Undirected unweighted graph:
  // articulation point: the vertex after deleting which & cutting all its edges the graph's connected components increases 
  // i.e. the graph gets disconnected if was connected earlier.
  //
  // In dfs for vertex u if any of its child has the descendant tree that's not connected to any of its ascendants then it's articulation point.
  // (boundary condition: root of dfs is specially treated).
  // This can be modeled to say that if the minimum of discovery time of any child's descendant tree is greater equals to discovery time
  // of u then it's articulation point (boundary condition needs special handling). O(m) O(n).
  public void findArticulationPoints() {
    discovered = new int[n + 1];
    visited = new boolean[n + 1];
    minDiscovered = new int[n + 1];
    for(int i=1;i<=n;++i) {
      if(visited[i]) {
        continue;
      }
      findArticulationPoints(i, -1, new int[]{0});
    }
    System.out.println();
  }

  void findBiConnectedComponents(int x, int px, Stack<Integer> q) {
    visited[x] = true;
    discovered[x] = minDiscovered[x] = ++globalTimer;
    for(int y: graph.get(x)) {
      if(y == px) {
        continue;
      }
      if(visited[y]) {
        minDiscovered[x] = Math.min(minDiscovered[x], discovered[y]);
      } else {
        findBiConnectedComponents(y, x, q);
        minDiscovered[x] = Math.min(minDiscovered[x], minDiscovered[y]);
        if(minDiscovered[y] == discovered[x]) {
          // One case of articulation point which can serve as root of a biconnected component.
          // This y-subtree won't be useful later because for ancestors of x, minDiscovered would clearly exceed their discovered values.
          // So pop y-subtree.
          System.out.print(x + " ");
          while(!q.isEmpty() && minDiscovered[q.peek()] >= discovered[x]) {// some backedges could already exist
            System.out.print(q.pop() + " "); // it could be possible that q contains multiple independent backedges from y-subtree, but since they're all 
            // connected through y, they together represent 1 valid BCC instead of individual BCCs which would otherwise have become error in this code. BCC
            // is by definition the maximal set of edges without any articulation points.
          }
          System.out.println();
        } else if(minDiscovered[y] > discovered[x]) {
          q.pop();// pop y since it will no longer contribute to biconn graph. BUT a graph of 2 nodes is also called biconnected.
          System.out.println(x + "  " + y);
        }
      }
    }
    // for tracking this path later from the ancestors.
    q.push(x);
  }

  // Undirected unweighted graph:
  // BiConnected graph: the graph in which each node is doubly connected i.e. no node is an articulation point. 
  //
  // BiConnected component in a graph would mean a subgraph within which there's no such node removing which will change the number of connected
  // components within the subgraph.
  //
  // Within dfs for current node u & its child node v if minDiscovered[v]==discovered[u] then u along with subtree rooted with has to be
  // a biconnected component. O(m) O(n)
  public void findBiConnectedComponents() {
    visited = new boolean[n + 1];
    discovered = new int[n + 1];
    minDiscovered = new int[n + 1];
    globalTimer = 0;
    Stack<Integer> q = new Stack<>();
    for(int i=1;i<=n;++i) {
      if(!visited[i]) {
        findBiConnectedComponents(i, -1, q);
      }
    }
  }


  List<List<Integer>> findBridges(int x, int px) {
    visited[x] = true;
    discovered[x] = minDiscovered[x] = ++globalTimer;
    List<List<Integer>> bridges = new ArrayList<>();
    for(int y: graph.get(x)) {
      if(y == px) {
        continue;
      }
      if(visited[y]) {
        minDiscovered[x] = Math.min(minDiscovered[x], discovered[y]);
      } else {
        bridges.addAll(findBridges(y, x));
        minDiscovered[x] = Math.min(minDiscovered[x], minDiscovered[y]);
        if(minDiscovered[y] == discovered[y]) {
          bridges.add(new ArrayList<>(List.of(x,y)));
        }
      }
    }
    return bridges;
  }

  // Undirected unweighted graph:
  // bridge is the edge just removing which will increase the connected components in the graph.
  //
  // Similar to finding articulation point, if for u-v, minDiscovered[v]=discovered[v] then it's a bridge. O(m) O(n).
  public List<List<Integer>> findBridges() {
    visited = new boolean[n + 1];
    discovered = new int[n + 1];
    minDiscovered = new int[n + 1];
    List<List<Integer>> bridges = new ArrayList<>();
    globalTimer = 0;
    for(int i=1;i<=n;++i) {
      if(!visited[i]) {
        bridges.addAll(findBridges(i, -1));
      }
    }
    return bridges;
  }

  // Undirected unweighted graph:
  // eulerian cycle - starts from any node u, visits every edge exactly once, returns back to u, for any u in this cycle.
  // eulerian path - starts from any node u, visits every edge exactly once, returns back to v (v!=u), for any u,v in this path.
  //
  // If every node has even degree then there's a eulerian cycle, but if every node except 2 nodes have even degrees then it's eulerian
  // path because start from o1 and end at o2 for other nodes e the number of entry=exit.
  //
  // Articulation point style DFS with check like discovered[u]=minDiscovered[v] WON'T work, because each edge within the connected
  // components mayn't be traversable due to upper cap of 1.
  public int findEulerianCycleOrEulerianPath() {
    degree = new int[n + 1];
    for(int i=1;i<=n;++i) {
      for(int j: graph.get(i)) {
        ++degree[i];++degree[j];
      }
    }
    int e = 0, o = 0;
    for(int i=1;i<=n;++i) {
      degree[i]/=2;
      if(degree[i]%2==0) {
        ++e;
      } else {
        ++o;
      }
    }
    return e==n ? 2 : (o==2 ? 1 : 0);
  }

  void tagTheBridges(int x, int px, Map<Integer, Set<Integer>> bridges) {
    visited[x] = true;
    discovered[x] = minDiscovered[x] = ++globalTimer;
    for(int y: graph.get(x)) {
      if(y == px){
        continue;
      }
      if(visited[y]) {
        minDiscovered[x] = Math.min(minDiscovered[x], discovered[y]);
      } else {
        tagTheBridges(y, x, bridges);
        minDiscovered[x] = Math.min(minDiscovered[x], minDiscovered[y]);
        if(minDiscovered[y] == discovered[y]) {
          bridges.putIfAbsent(x, new HashSet<>());bridges.putIfAbsent(y, new HashSet<>());
          bridges.get(x).add(y);bridges.get(y).add(x);
        }
      }
    }
  }

  void printEulerianCycleOrEulerianPath(int x, Map<Integer, Set<Integer>> bridges, Map<Integer, Set<Integer>> mutableGraph) {
    System.out.print(x + " ");
    for(int y: mutableGraph.get(x)) {
      if(!bridges.containsKey(x) || !bridges.get(x).contains(y)) {
        mutableGraph.get(x).remove(y);mutableGraph.get(y).remove(x);
        printEulerianCycleOrEulerianPath(y, bridges, mutableGraph);
        return;
      }
    }
    for(int y: mutableGraph.get(x)) {
      mutableGraph.get(x).remove(y);mutableGraph.get(y).remove(x);
      printEulerianCycleOrEulerianPath(y, bridges, mutableGraph);
      return;
    }
  }

  // in case of eulerian path, start from odd degree vertex and prefer non-bridge edges whenever possible. O(m)
  // for directed graphs the conditions are: indeg=outdeg && scc.
  public void printEulerianCycleOrEulerianPath() {
    Map<Integer, Set<Integer>> bridges = new HashMap<>();
    visited = new boolean[n + 1];
    discovered = new int[n + 1];
    minDiscovered = new int[n + 1];
    globalTimer = 0;
    tagTheBridges(1, -1, bridges);
    if(findEulerianCycleOrEulerianPath() == 0) {
      return;
    }
    Map<Integer, Set<Integer>> mutableGraph = buildMutableGraph();
    for(int i=1;i<=n;++i) {
      if(degree[i]%2 != 0) {
        printEulerianCycleOrEulerianPath(i, bridges, mutableGraph);
        System.out.println();
        return;
      }
    }
    printEulerianCycleOrEulerianPath(1, bridges, mutableGraph);
    System.out.println();
  }

  void findStronglyConnectedComponents(int x, Stack<Integer> q) {
    visited[x] = true;
    discovered[x] = minDiscovered[x] = ++globalTimer;
    q.push(x);
    for(int y: graph.get(x)) {
      if(visited[y]) {
        minDiscovered[x] = Math.min(minDiscovered[x], discovered[y]);
      } else {
        findStronglyConnectedComponents(y, q);
        minDiscovered[x] = Math.min(minDiscovered[x], minDiscovered[y]);
        if(minDiscovered[y] > discovered[x]) {
          while(!q.isEmpty()) {
            System.out.print(q.peek() + " ");
            if(q.pop() == y) {
              break;
            }
          }
          System.out.println();
        }
      }
    }
  }

  // Directed unweighted graph:
  // scc: the subgraph in which each vertex is reachable from every other vertex.
  //
  // in dfs for vertex u and its child v if the minDiscovered[v] = discovered[u] then both belong to same scc, else v rooted subtree is separate scc.
  // O(m)
  public void findStronglyConnectedComponents() {
    visited = new boolean[n + 1];
    discovered = new int[n + 1];
    minDiscovered = new int[n + 1];
    globalTimer = 0;
    Stack<Integer> q = new Stack<>();
    for(int i=1;i<=n;++i) {
      if(visited[i]) {
        continue;
      }
      findStronglyConnectedComponents(i, q);
      while(!q.isEmpty()) {
        System.out.print(q.pop() + " ");
      }
      System.out.println();
    }
  }

  int findMinCapInMaxFlowPath(int source, int target, int[][] residue, int[] parent) {
    Queue<Integer> q = new ArrayDeque<>();
    q.add(source);
    Arrays.fill(visited, false);
    visited[source] = true;
    boolean reachable = false;
    while(!q.isEmpty()) {
      int x = q.poll();
      if(x == target) {
        reachable = true;
        break;
      }
      for(Edge e: graphW.get(x)) {
        if(!visited[e.node] && residue[x][e.node] > 0) {
          q.add(e.node);
          visited[e.node] = true;
          parent[e.node] = x;
        }
      }
    }
    int minCapacity = Integer.MAX_VALUE;
    while(reachable && target != source) {
      minCapacity = Math.min(minCapacity, residue[parent[target]][target]);
      target = parent[target];
    }
    return minCapacity;
  }

  // Directed weighted graph:
  // For a given source s target t, the maximum weight that can flow from s to t such that total inweight=total outweight for all interim vertex.
  //
  // Clone the graph, bfs to find any one path from s to t with initial max_flow=0, if no path found return else find the minWeight along the path,
  // subtract it from each edge along the actual direction and add it to each (virtual) edge in the opposite direction such that the consumed capacity
  // can be reverted upto 0 if needed later, add this to the initial max_flow, continue until there's no path. O(m*m*n), each bfs saturates an edge in the
  // shortest path length and each edge can appear in n-1 such shortest path.
  public int maxFlowFordFulkerson(int source, int target) {// this extension of fordfulkerson is called edmond karp  
    visited = new boolean[n + 1];
    int[] parent = new int[n + 1];
    int[][] residue = new int[n + 1][n + 1];
    for(int i=1;i<=n;++i) {
      for(Edge e: graphW.get(i)) {
        residue[i][e.node] = e.weight;
      }
    }
    int maxFlow = 0;
    int minCapacity = 0;
    while((minCapacity = findMinCapInMaxFlowPath(source, target, residue, parent)) != Integer.MAX_VALUE) {
      int temp = target;
      while(temp != source) {
        residue[parent[temp]][temp] -= minCapacity;
        residue[temp][parent[temp]] += minCapacity;
        temp = parent[temp];
      }
      maxFlow += minCapacity;
    }
    return maxFlow;
  }

  boolean dinicFlowPathExists(int source, int target, int[] level) {
    Queue<Integer> q = new ArrayDeque<>();
    q.add(source);
    Arrays.fill(level, -1);
    level[source] = 0;
    while(!q.isEmpty()) {
      int x = q.poll();
      for(Edge e: graphW.get(x)) {
        if(level[e.node]==-1 && adj[x][e.node] > 0) {
          level[e.node] = level[x] + 1;
          q.add(e.node);
        }
      }
    }
    return level[target] != -1;
  }

  int attemptSendFlows(int source, int target, int minCapacity, int[] level, int[] edgePtr) {
    if(source == target) {
      return minCapacity;
    }
    while(edgePtr[source]<graphW.get(source).size()) {
      Edge e = graphW.get(source).get(edgePtr[source]);
      if(level[source]+1==level[e.node] && adj[source][e.node] > 0) {
        int curMinCapacity = attemptSendFlows(e.node, target, 
                Math.min(minCapacity, adj[source][e.node]), level, edgePtr);
        if(curMinCapacity > 0) {
          adj[source][e.node] -= curMinCapacity;
          adj[e.node][source] += curMinCapacity;
          return curMinCapacity;
        }
      }
      ++edgePtr[source];
    }
    return 0;
  }


  // Directed weighted graph:
  //
  // Similar approach as edmonkarp but instead of finding minCap everytime via bfs and then updating residue graph, the flows can be 
  // triggered simultaneously based on bfsLevel of the nodes. O(m*n*n).
  public int maxFlowDinic(int source, int target) {
    adj = new int[n+1][n+1];
    int[] level = new int[n + 1];
    for(int i=1;i<=n;++i) {
      for(Edge e: graphW.get(i)) {
        adj[i][e.node] = e.weight;
      }
    }
    int maxFlow = 0;
    while(dinicFlowPathExists(source, target, level)) {
      int[] edgePtr = new int[n + 1];
      int minCapacity = 0;
      while((minCapacity=attemptSendFlows(source, target, Integer.MAX_VALUE, level, edgePtr)) > 0) {
        maxFlow += minCapacity;
      }
    }
    return maxFlow;
  }

 
  // Directed weighted graph:
  // mincut is the minimum sum of weights of set of edges (aka cut set) removing which will make s and t in different sets. There can be many 
  // cutset in a graph but mincut is the one that achieves the most optimal cutset. According to maxflow-mincut theorem the maximum flow in a 
  // graph is the capacity of its mincut (a sense of mutual effects).
  //
  // Just run ford folkerson or dinic and build the final residue graph, any edge that connects reachable to unreachable is part of mincut. O(m*n*n).
  public void findMinCuts(int source, int target) {
    int minCutValue = maxFlowDinic(source, target); // ford-fulkerson can also be used.
    System.out.println("mincut: " + minCutValue);
    Set<Integer> reachable = new HashSet<>();
    Queue<Integer> q = new ArrayDeque<>();
    q.add(source);reachable.add(source);int tim=300000;
    while(!q.isEmpty()) {
      int x = q.poll();
      for(Edge e: graphW.get(x)) {
        if(reachable.contains(e.node)) {
          continue;
        }
        if(adj[x][e.node] > 0) {
          q.add(e.node);reachable.add(e.node);
        } else {
          System.out.println(x + " " + e.node);
        }
      }
    }
  }

  // Directed unweighted graph:
  // In Bipartite graph there are exactly 2 different sets and each edge connects 2 vertexes from different set. Bipartite matching
  // means choosing a set of edges such that no 2 edges share the same vertex. Max BM means maximum size of such matching.
  //
  // This can be remodelled to say that if we supply a unit of flow from each vertex in set1 then what's the max flow that can be achieved
  // at the sink. Add a vertex 0 as source and vertex n+1 as target. O(m*m*n).
  public int maxBipartiteMatching() {
    graphW = new ArrayList<>();
    for(int i=0;i<=n+2;++i) {
      graphW.add(new ArrayList<>());
    }
    for(int i=1;i<=n;++i) {
      for(int j: graph.get(i)) {
        graphW.get(1).add(new Edge(i+1, 1));
        graphW.get(i+1).add(new Edge(j+1, 1));
        graphW.get(j+1).add(new Edge(n+2, 1));
      }
    }
    n+=2;
    return maxFlowFordFulkerson(1, n);
  }

  boolean maxBipartiteMatchingHitNTrial(int x, int[] match) {
    for(int y: graph.get(x)) {
      if(!isInStack[y]) {
        isInStack[y] = true;
        if(match[y]==-1 || maxBipartiteMatchingHitNTrial(match[y],match)) {
          match[y]=x;
          return true;
        }
      }
    }
    return false;
  }

  // Alternative to maxflow approach is to apply hit and trial i.e. For each node check if y can be a match for x by recursively checking every unvisited edges.
  // If y isn't assigned any x1 then x can simply be assigned , else if x1 can be assigned to something unvisited then x can be assigned to y based on feasibility.
  // O(n*m)O(m).
  public int maxBipartiteMatchingHitNTrial() {
    int[] match = new int[n + 1];Arrays.fill(match, -1);
    int matchCount = 0;
    for(int i=1;i<=n;++i) {
      isInStack = new boolean[n + 1];
      if(maxBipartiteMatchingHitNTrial(i,match))++matchCount;
    }
    return matchCount;
  }

  int bitCount(int mask) {
    int cnt = 0;
    while(mask > 0) {
      cnt += (mask&1);mask>>=1;
    }
    return cnt;
  }

  int travellingSalesManMinCost(int x, int mask, int[][] dp) {
    if(dp[x][mask] != -1) {
      return dp[x][mask];
    }
    if(bitCount(mask) == n) {
      if(x==1 || adj[x][1] > 0) {
        return dp[x][mask] = adj[x][1];
      } else {
        return dp[x][mask] = Integer.MAX_VALUE;
      }
    }
    int ret = Integer.MAX_VALUE;
    for(Edge e: graphW.get(x)) {
      if(((mask>>(e.node-1))&1) == 0) {
        ret = Math.min(ret, e.weight + travellingSalesManMinCost(e.node, mask | (1<<(e.node-1)), dp));
      }
    }
    return dp[x][mask] = ret;
  }

  // Undirected weighted graph, directed weighted graph:
  // Just try all possibilities with a bitmask state. O(n*2^n).
  public int travellingSalesManMinCost() {
    adj = new int[n+1][n+1];
    int[][] dp = new int[n+1][1<<(n+1)];
    for(int i=1;i<=n;++i){
      Arrays.fill(dp[i], -1);
    }
    for(int i=1;i<=n;++i) {
      for(Edge e: graphW.get(i)) {
        adj[i][e.node] = e.weight;
      }
    }
    return travellingSalesManMinCost(1, 1, dp);
  }
}
