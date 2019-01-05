import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.StringTokenizer;


public class Main {

	static class Task {
		
		int NN = 1000006;
		int MOD = 998244353;
		int INF = 2000000000;
		long INFINITY = 1000000000000000000L;
		
		int [] a;
		int [][] dp = new int[10000][20];
		
		int S;
		
		public void solve(InputReader in, PrintWriter out) {
			int n = in.nextInt();
			a = new int[n];
			S = 0;
			for(int i=0;i<n;++i) {
				a[i] = in.nextInt();
				S += a[i];
			}
			for(int i=0;i<10000;++i) {
				for(int j=0;j<=n;++j)
					dp[i][j] = -1;
			}
			out.println(rec(0, n)?"YES":"NO");
		}
		
		boolean rec(int sum, int n) {			
			if(dp[sum][n] != -1)
				return dp[sum][n] == 1;
			if(n == 0) {
				dp[sum][n] = ((Math.abs(2*sum-S))%360 == 0?1:0);
				return dp[sum][n] == 1;
			}
			boolean ret = rec(sum, n - 1) || rec(sum + a[n - 1], n - 1);
			if(ret)
				dp[sum][n] = 1;
			else
				dp[sum][n] = 0;
			return ret;
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