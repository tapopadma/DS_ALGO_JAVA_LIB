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
		int MOD = 1000000007;
		long [] exp2 = new long[NN];
		long [] fac = new long[NN];
		
		long expo(long x, long n) {
			long ret = 1;
			long sq = x;
			while(n > 0) {
				if((n&1L)!=0) {
					ret = (ret * sq) % MOD;
				}
				sq = (sq * sq) % MOD;
				n >>= 1L;
			}
			return ret;
		}
		
		long ncr(long n, long r) {
			long ret = fac[(int) n];
			ret = (ret * expo(fac[(int) (n - r)], MOD - 2L))%MOD;
			ret = (ret * expo(fac[(int) (r)], MOD - 2L))%MOD;
			return ret;
		}
		
		public void solve(InputReader in, PrintWriter out) {
			fac[0] = 1L;
			exp2[0] = 1L;
			for(int i=1;i<NN;++i) {
				fac[i] = (fac[i - 1] * i * 1L)%MOD;
				exp2[i] = (exp2[i - 1] * 2L)%MOD;
			}
			int t = in.nextInt();
			for(int testcaseNo = 1;testcaseNo <= t;++testcaseNo) {
				long n = in.nextLong(), m =in.nextLong();
				long ans = fac[(int) (2L*n)];
				long factor = -1;
				for(long r = 1;r<=m;++r) {
					factor *= -1;
					long val = factor * ncr(m, r);
					val = (val * exp2[(int) r])%MOD;
					val = (val * fac[(int) (2L*n-r)])%MOD;
					ans = (ans - val + MOD) % MOD;
				}
				out.println("Case #" + testcaseNo + ": " + ans);
			}
		}
	}
	
	public static void main(String[] args) {
		InputStream inputStream = System.in;
        OutputStream outputStream = System.out;
        InputReader in = new InputReader(inputStream);
        PrintWriter out = new PrintWriter(outputStream);
        String IPfilePath = "/home/tapopadma/Downloads/ip.in";
        String OPfilePath = "/home/tapopadma/Downloads/op.out";
        InputReader fin = new InputReader(IPfilePath);
        PrintWriter fout = null;
        try {
			fout = new PrintWriter(new File(OPfilePath));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Task solver = new Task();
        //solver.solve(in, out);
        solver.solve(fin, fout);
        out.close();
       fout.close();
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
