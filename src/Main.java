import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.StringTokenizer;


public class Main {

	static class Task {
		
		int NN = 100005;
		int MOD = 1000000007;
		long INF = 10000000000000000L;
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
			if(dp[x][n] != -1) {
				return dp[x][n];
			}
			for(int i=0;i<k && i < n;++i) {
				ret = Math.max(ret, rec(x - 1, n - i - 1, k) + a[n - i - 1]);
			}
			return dp[x][n] = ret;
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
			for(int i=1;i<=x;++i) {
				long max = -INF;
				int maxPos = -1;
				LinkedList<Integer> L = new LinkedList<>();
				for(int j=1;j<=n;++j) {
					while(!L.isEmpty()) {
						int pos = L.getFirst();
						if(pos < j-K) {
							L.removeFirst();
						} else {
							break;
						}
					}
					long currentValue = dp[i - 1][j - 1] + a[j - 1];
					max = currentValue;
					maxPos = j - 1;
					while(!L.isEmpty()) {
						int pos = L.getLast();
						long value = dp[i-1][pos] + a[pos];
						if(value <= max) {
							L.removeLast();
						} else {
							break;
						}
					}
					L.addLast(maxPos);
					dp[i][j] = dp[i - 1][L.getFirst()] + a[L.getFirst()];
				}
			}
			out.println(dp[x][n] < 0 ? -1 : dp[x][n]);
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