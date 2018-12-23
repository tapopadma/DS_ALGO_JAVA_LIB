import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;


public class Main {

	static class Task {
		
		int NN = 100005;
		int MOD = 998244353;
		int INF = 2000000000;
		
		class Pair{
			int x;
			int y;
			public Pair(int x, int y) {
				this.x = x;this.y = y;
			}
			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + getOuterType().hashCode();
				result = prime * result + x;
				result = prime * result + y;
				return result;
			}
			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				Pair other = (Pair) obj;
				if (!getOuterType().equals(other.getOuterType()))
					return false;
				if (x != other.x)
					return false;
				if (y != other.y)
					return false;
				return true;
			}
			private Task getOuterType() {
				return Task.this;
			}
			
		}
		
		class Edge{
			int node;
			int cost;
			public Edge(int node, int cost) {
				this.node = node;
				this.cost = cost;
			}
		}
		
		List<Edge> [] G = new LinkedList[NN];
		int [] dist = new int[NN];
		boolean [] asked = new boolean[NN];
		
		public void solve(InputReader in, PrintWriter out) {
			int n = in.nextInt(), m = in.nextInt(), k = in.nextInt();
			for(int i=1;i<=n;++i) {
				asked[i] = false;
			}
			for(int i=1;i<=k;++i) {
				int x = in.nextInt();
				asked[x] = true;
			}
			Map<Pair, Integer> cost = new HashMap<>();
			for(int i=1;i<=m;++i) {
				int u = in.nextInt(), v = in.nextInt(), w = in.nextInt();
				if(u == v) {
					continue;
				}
				if(u > v) {
					int t = u;u= v;v = t;
				}
				if(cost.containsKey(new Pair(u, v))) {
					if(cost.get(new Pair(u, v))  > w) {
						cost.put(new Pair(u, v), w);
					}
				} else {
					cost.put(new Pair(u, v), w);
				}
			}
			
			for(Pair key: cost.keySet()) {
				int u = key.x;
				int v = key.y;
				int w = cost.get(key);
				if(G[u] == null) {
					G[u] = new LinkedList<>();
				}
				if(G[v] == null) {
					G[v] = new LinkedList<>();
				}
				G[u].add(new Edge(v, w));
				G[v].add(new Edge(u, w));
			}
			int ans = dijkstra(n);
			for(int i=1;i<=k;++i) {
				out.print(ans + " ");
			}
			out.println("");
		}
	
		int dijkstra(int n) {
			int source = 1;
			for(int i=1;i<=n;++i) {
				dist[i] = INF;
				if(asked[i]) {
					source = i;
				}
			}
			dist[source] = 0;
			TreeSet<Integer> Q = new TreeSet<>(
					(i, j)->(dist[i]==dist[j])? 
							Integer.valueOf(i).compareTo(Integer.valueOf(j))
							: Integer.valueOf(dist[i])
					.compareTo(Integer.valueOf(dist[j])));
			Q.add(source);
			Set<Integer> ss= new HashSet<>();
			while(!Q.isEmpty()) {
				int node = Q.first();Q.remove(node);
				if(n == 20000) {
					if(dist[node]==INF)
						ss.add(dist[node]);
				}
				for(Edge adjEdge: G[node]) {
					int adj = adjEdge.node;
					int cost = adjEdge.cost;
					if(Math.max(dist[node], cost) < dist[adj]) {
						Q.remove(adj);
						dist[adj] = Math.max(dist[node], cost);
						Q.add(adj);
					}
				}
			}
			int max = 0;
			for(int i=1;i<=n;++i) {
				if(!asked[i]) {
					continue;
				}
				max = Math.max(max, dist[i]);
			}
			return max;
		}
		
	}
	
	static void prepareIO(boolean isFileIO) {
		Task solver = new Task();
		// Standard IO
		if(!isFileIO) {
			InputStream inputStream = System.in;
	        OutputStream outputStream = System.out;
	        InputReader in = new InputReader(inputStream);
	        PrintWriter out = new PrintWriter(outputStream);
	        solver.solve(in, out);
	        out.close();
		}
        // File IO
		else {
			String IPfilePath = System.getProperty("user.home") + "/Downloads/ip.in";
	        String OPfilePath = System.getProperty("user.home") + "/Downloads/op.out";
	        InputReader fin = new InputReader(IPfilePath);
	        PrintWriter fout = null;
	        try {
				fout = new PrintWriter(new File(OPfilePath));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
	        solver.solve(fin, fout);
	        fout.close();
		}
	}
	
	public static void main(String[] args) {
        prepareIO(false);
	}
	
	static class InputReader {
        public BufferedReader reader;
        public StringTokenizer tokenizer;

        public InputReader(InputStream stream) {
            reader = new BufferedReader(new InputStreamReader(stream), 32768);
            tokenizer = null;
        }
        
        public InputReader(String filePath) {
        	File file = new File(filePath);
            try {
				reader = new BufferedReader(new FileReader(file));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            tokenizer = null;
        }
        
        public String nextLine() {
        	String str = "";
        	try {
				str = reader.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	return str;
        }
        
        public String next() {
            while (tokenizer == null || !tokenizer.hasMoreTokens()) {
                try {
                    tokenizer = new StringTokenizer(reader.readLine());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return tokenizer.nextToken();
        }

        public int nextInt() {
            return Integer.parseInt(next());
        }

        public long nextLong() {
            return Long.parseLong(next());
        }

        public double nextDouble() {
        	return Double.parseDouble(next());
        }
        
    }

}