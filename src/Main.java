import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class Main {

	static class Task {
		
		int NN = 1000006;
		int MOD = 1000000007;
		
		public void solve(InputReader in, PrintWriter out) {
			int n = in.nextInt(), m = in.nextInt();
			long [] a = new long[n];
			long sum = 0;
			for(int i=0;i<n;++i) {
				a[i] = in.nextLong();
				sum += a[i];
			}
			long used = 0;
			Arrays.sort(a);
			long h = a[n - 1];
			for(int i=n-1;i>=0;--i) {
				if(i == 0) {
					used += Math.max(1, h);
				} else {
					if(h > a[i - 1]) {
						used += (h - a[i - 1]);
						h = a[i - 1];
					} else {
						++used;
						--h;
					}
				}
			}
			out.println(sum - used);
		}
	}
	
	public static void main(String[] args) {
        Task solver = new Task();
        
        // Standard IO
        InputStream inputStream = System.in;
        OutputStream outputStream = System.out;
        InputReader in = new InputReader(inputStream);
        PrintWriter out = new PrintWriter(outputStream);
        solver.solve(in, out);
        out.close();
        
        /*// File IO
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
        fout.close();*/
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
