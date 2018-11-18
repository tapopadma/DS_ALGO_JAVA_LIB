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
		
		Map<Long, Long> freq;
		long [] a;
		
		public void solve(InputReader in, PrintWriter out) {
			int t = in.nextInt();
			for(int testcaseNo = 1;testcaseNo <= t;++testcaseNo) {
				int n = in.nextInt();
				int p = in.nextInt();
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
        solver.solve(in, out);
        //solver.solve(fin, fout);
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
