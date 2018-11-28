import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

public class Main {

	static class Task {
		
		int NN = 100005;
		int MOD = 1000000007;
		long INF = 1000000000000000000L;
		long [] a;
		long [][] dp;
		
		long rec(int x, int n, int k) {
			long ret = -INF;
			if(x == 0) {
				if(n < k) {
					return 0;
				} else {
					return -INF;
				}
			}
			if(n == 0) {
				return -INF;
			}
			for(int i=0;i<k && i < n;++i) {
				ret = Math.max(ret, rec(x - 1, n - i - 1, k) + a[n - i - 1]);
			}
			return ret;
		}
		
		public void solve(InputReader in, PrintWriter out) {
			int n = in.nextInt(), K = in.nextInt(), x = in.nextInt();
			a = new long[n];
			dp = new long[x + 1][n + 1];
			for(int i=0;i<n;++i) {
				a[i] = in.nextLong();
			}
			for(int j=0;j<=n;++j) {
				if(j < K) {
					dp[0][j] = 0;
				} else {
					dp[0][j] = -INF;
				}
			}
			for(int i=1;i<=x;++i) {
				dp[i][0] = -INF;
			}
			Map<Long, Integer> mp;
			Heap st;
			for(int i=1;i<=x;++i) {
				mp = new TreeMap<>();
				st = new Heap();
				for(int j=1;j<=n;++j) {
					long value = dp[i - 1][j - 1] + a[j - 1];
					int cnt = 0;
					if(mp.containsKey(value)) {
						cnt = mp.get(value);
					}
					++cnt;
					mp.put(value, cnt);
					if(cnt == 1) {
						st.add(value);
					}
					if(j - 1 - K >= 0) {
						value = dp[i - 1][j - 1 - K] + a[j - 1 - K];
						cnt = 0;
						if(mp.containsKey(value)) {
							cnt = mp.get(value);
						}
						--cnt;
						if(cnt <= 0) {
							st.remove(value);
						}
					}
					dp[i][j] = st.getMax();
				}
			}
			long ans = dp[x][n];
			if(ans < 0) {
				ans = -1;
			}
			out.println(ans);
		}
		
		class Heap{
			Map<Long, Integer> freq = new HashMap<>();
			long [] T = new long[5555];
			int tot = 0;
			
			public void add(long value) {
				if(freq.containsKey(value)) {
					int cnt = freq.get(value);
					++cnt;
					freq.put(value, cnt);
					return;
				}
				freq.put(value, 1);
				T[++tot] = value;
				int idx = tot;
				while(idx/2 >= 1 && T[idx/2] < T[idx]) {
					long temp = T[idx/2];
					T[idx/2] = T[idx];
					T[idx] = temp;
					idx/=2;
				}			
			}
			
			public void remove(long value) {
				if(freq.containsKey(value)) {
					int cnt = freq.get(value);
					--cnt;
					if(cnt == 0) {
						freq.remove(value);
					}else {
						freq.put(value, cnt);	
					}
				}
			}
			
			public long getMax() {
				while(!freq.containsKey(T[1])) {
					removeMax();
				}
				return T[1];
			}
			
			void removeMax() {
				T[1] = T[tot--];
				int idx = 1;
				while(true) {
					if(idx*2 > tot) {
						break;						
					}
					if(Math.max(T[idx*2], (idx*2+1) <= tot ? T[idx*2+1]: -INF-100) < T[idx]) {
						break;
					}
					if(idx*2 + 1 <= tot && T[idx*2+1] > T[idx*2]) {
						long temp = T[idx];T[idx] = T[idx*2+1];T[idx*2+1]=temp;
						idx = idx * 2 + 1;
					}
					else {
						long temp = T[idx];T[idx] = T[idx*2];T[idx*2]=temp;
						idx = idx * 2;
					}
				}
			}
			
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

    }

}
