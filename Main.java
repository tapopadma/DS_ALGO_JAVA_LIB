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
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.stream.Collectors;
 
 
public class Main {
 
	static class Task {
		
		int NN = 200005;
		int MOD = 998244353;
		int INF = 2000000000;
		long INFINITY = (1L<<63)-1;
 
		class Tuple {
			Integer [] ara;
			Integer x, y, z, t, w;
			public Tuple(Integer... a) {
				this.ara = a;
				if(ara.length > 0) this.x = ara[0];
				if(ara.length > 1) this.y = ara[1];
				if(ara.length > 2) this.z = ara[2];
				if(ara.length > 3) this.t = ara[3];
				if(ara.length > 4) this.w = ara[4];
			}
		}

		List<Integer>[]g;
		Stack<Integer> s = new Stack<>();
		boolean[] instack;boolean[] vis;

		public void solve(InputReader in, PrintWriter out) throws Exception {
			int n = in.nextInt();
			int m = in.nextInt();
			g=new ArrayList[n+1];
			r=new int[n+1];
			instack=new boolean[n+1];Arrays.fill(instack, false);
			vis=new boolean[n+1];Arrays.fill(vis, false);
			for(int i=1;i<=n;++i)g[i]=new ArrayList();
			for(int i=1;i<=m;++i) {
				int u = in.nextInt();
				int v = in.nextInt();
				g[u].add(v);
			}
			for(int i=1;i<=n;++i) {
				Collections.sort(g[i], (u,v)->Integer.valueOf(v).compareTo(u));
			}
			for(int i=1;i<=n;++i){
				if(hasCycle(i)){
					out.println(-1);return;
				}
			}
			Arrays.fill(vis, false);
			for(int i=n;i>=1;--i) {
				dfs(i);
			}
			TreeSet<Integer>q=new TreeSet<>((i,j)->
			r[i]==r[j]?Integer.valueOf(i).compareTo(j)
			:Integer.valueOf(r[j]).compareTo(r[i]));
			for(int i=1;i<=n;++i){
				q.add(i);
			}
			while(!q.isEmpty()) {
				out.print(q.pollFirst()+" ");
			}
			out.println();
		}

		int []r;

		int dfs(int x) {
			if(vis[x])return -1;
			int mx = -1;
			vis[x]=true;
			for(int y:g[x]) {
				mx = Math.max(mx, dfs(y));
			}
			s.push(x);
			r[x]=mx+1;
			return r[x];
		}

		boolean hasCycle(int x) {
			if(instack[x])return true;
			if(vis[x])return false;
			vis[x]=true;
			instack[x]=true;
			for(int y:g[x]) {
				if(hasCycle(y))return true;
			}
			instack[x]=false;
			return false;
		}

	}
	
	static void prepareIO(boolean isFileIO) throws Exception {
		//long t1 = System.currentTimeMillis();
		Task solver = new Task();
		// Standard IO
		if(!isFileIO) { 
			InputStream inputStream = System.in;
	        OutputStream outputStream = System.out;
	        InputReader in = new InputReader(inputStream);
	        PrintWriter out = new PrintWriter(outputStream);
	        solver.solve(in, out);
	        //out.println("time(s): " + (1.0*(System.currentTimeMillis()-t1))/1000.0);
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
	        //fout.println("time(s): " + (1.0*(System.currentTimeMillis()-t1))/1000.0);
	        fout.close();
		}
	}
	
	public static void main(String[] args) throws Exception {
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