package ds.algo.java.lib.datastrucutres.graphs;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Assume nodes numbered from 1 to n and m edges.<br>
 * To detect negative weight cycle in a graph
 * just need to find shortest path for all nodes
 * using either Floyd Warshall or Bellman Ford and
 * after that check if the shortest path is still shorter
 * than the evaluated ones for any node.<br>
 * Prim's MST is same logic as dijkstra; updating and choosing the total cost
 * is the only extra thing in dijkstra.
 * @author tapopadma
 *
 */
public class Graph {

	/**
	 * number of nodes
	 */
	int n;
	/**
	 * number of edges
	 */
	int m;
	
	boolean isDirected;
	
	boolean isWeighted;
	
	LinkedList<Edge> [] G;	
	
	boolean isCycleDetected;
	
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
			return "("+from+", "+to+")->"+weight;
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
	
	UnionFind [] uf;
	List<FullEdge> edges;
	int [][] W;
	
	
	public Graph(int n, int m, boolean isDirected,
			boolean isWeighted) {
		this.n = n;
		this.m = m;
		W = new int[n + 1][n + 1];
		edges = new ArrayList<>();
		this.isDirected = isDirected;
		this.isWeighted = isWeighted;
		this.isCycleDetected = false;
		G = new LinkedList[n+1];
		uf = new UnionFind[n + 1];		
		for(int i=1;i<=n;++i) {
			G[i] = new LinkedList<>();
			uf[i] = new UnionFind(i, 0);
			for(int j=1;j<=n;++j) {
				W[i][j] = (i == j) ? 0 : INF;
			}
		}
	}
	
	public void addEdge(int u, int v) {
		addEdge(u, v, 1);
	}
	
	public void addEdge(int u, int v, int w) {
		G[u].add(new Edge(v, w));
		W[u][v] = w;
		if(!isDirected) {
			G[v].add(new Edge(u, w));
			W[v][u] = w;
			if(find(u) == find(v)) {
				this.isCycleDetected = true;
			}
			union(u, v);
		}
		edges.add(new FullEdge(u, v, w));
	}
	
	public boolean hasCycle() {
		if(isDirected) {
			return hasCycleDirected();
		}
		return hasCycleUndirected();
	}
	
	void union(int u, int v) {
		u = find(u);
		v = find(v);
		if(uf[u].rank == uf[v].rank) {
			++uf[v].rank;
			uf[u].parent = v;
		} else {
			if(uf[u].rank > uf[v].rank) {
				uf[v].parent = u;
			} else {
				uf[u].parent = v;
			}
		}
	}
	
	int find(int x) {
		return uf[x].parent == x 
				? x 
				: (uf[x].parent = find(uf[x].parent));
	}
	
	boolean hasCycleUndirected() {
		return isCycleDetected;
	}
	
	boolean [] visited;
	boolean [] isInStack;
	
	boolean dfs(int cur) {
		if(isInStack[cur]) {
			return true;
		}
		if(visited[cur]) {
			return false;
		}
		visited[cur] = true;
		isInStack[cur] = true;
		for(Edge e: G[cur]) {
			if(dfs(e.node)) {
				return true;
			}
		}
		isInStack[cur] = false;
		return false;
	}
	
	/**
	 * Cycle exists if a node is visited that is still 
	 * in dfs recursion stack 
	 * @return
	 */
	boolean hasCycleDirected() {
		visited = new boolean[n + 1];
		isInStack = new boolean[n + 1];
		for(int i=1;i<=n;++i){
			visited[i] = false;
			isInStack[i] = false;
		}
		for(int i=1;i<=n;++i) {
			if(dfs(i)) {
				return true;
			}
		}
		return false;
	}
	
	public List<FullEdge> kruskal() {
		
		List<FullEdge> ret = new ArrayList<>();
		edges = edges.stream().sorted((e1, e2)
				-> Integer.valueOf(e1.weight)
				.compareTo(Integer.valueOf(e2.weight)))
		.collect(Collectors.toList());
		for(int i=1;i<=n;++i) {
			uf[i].parent = i;uf[i].rank = 0;
		}
		for(FullEdge e: edges) {
			if(find(e.from) != find(e.to)) {
				ret.add(e);
			}
			union(e.from, e.to);
		}
		return ret;
	}
	
	int [] dist;
	final int INF = 2000000000; 
	
	/**
	 * note the difference btn this and dijkstra
	 * @return
	 */
	public int [] prim() {
		dist = new int[n + 1];
		for(int i=1;i<=n;++i) {
			dist[i] = INF;
		}
		dist[1] = 0;
		TreeSet<Integer> Q = new TreeSet<>((i, j) -> 
				(dist[i]==dist[j]) 
				? Integer.valueOf(dist[i]).compareTo(Integer.valueOf(dist[j]))
				: Integer.valueOf(i).compareTo(Integer.valueOf(j)));
		Q.add(1);
		while(!Q.isEmpty()) {
			int node = Q.pollFirst();
			for(Edge e: G[node]) {
				if(dist[e.node] > e.weight) {
					Q.remove(e.node);
					dist[e.node] = e.weight;
					Q.add(e.node);
				}
			}
		}
		return dist;
	}
	
	/**
	 * O(mlogn) , ds + greedy
	 * fetch the closest node then update adjacent
	 * @param source
	 * @return
	 */
	public int [] dijkstra(int source) {
		dist = new int[n + 1];
		for(int i=1;i<=n;++i) {
			dist[i] = INF;
		}
		dist[source] = 0;
		TreeSet<Integer> Q = new TreeSet<>((i, j) -> 
				(dist[i]==dist[j]) 
				? Integer.valueOf(dist[i]).compareTo(Integer.valueOf(dist[j]))
				: Integer.valueOf(i).compareTo(Integer.valueOf(j)));
		Q.add(source);
		while(!Q.isEmpty()) {
			int node = Q.pollFirst();
			for(Edge e: G[node]) {
				if(dist[e.node] > dist[node] + e.weight) {
					Q.remove(e.node);
					dist[e.node] = dist[node] + e.weight;
					Q.add(e.node);
				}
			}
		}
		return dist;
	}
	
	/**
	 * O(n*m) , dp 
	 * compare and update dist value for m edges n-1 times.
	 * an edge may contribute to shortest path of length i on ith update
	 * max length of any shortest path n - 1 
	 * @param source
	 * @return
	 */
	public int [] bellmanFord(int source) {
		dist = new int[n + 1];
		for(int i=1;i<=n;++i) {
			dist[i] = INF;
		}
		dist[source] = 0;
		for(int i=1;i<n;++i) {
			for(FullEdge e: edges) {
				if(dist[e.from] + e.weight < dist[e.to]) {
					dist[e.to] = dist[e.from] + e.weight; 
				}
			}
		}
		return dist;
	}
	
	/**
	 * O(n^3) , dp
	 * for every pair we try to update cost by considering an intermediate node 
	 * @return
	 */
	public int [][] floydWarshall() {
		int [][] d = new int[n+1][n+1];
		for(int i=1;i<=n;++i) {
			for(int j=1;j<=n;++j) {
				d[i][j] = W[i][j];
			}
		}
		for(int k=1;k<=n;++k) {
			for(int i=1;i<=n;++i) {
				for(int j=1;j<=n;++j) {
					if(d[i][j] > d[i][k] + d[k][j]) {
						d[i][j] = d[i][k] + d[k][j];
					}
				}
			}
		}
		return d;
	}
	
}
