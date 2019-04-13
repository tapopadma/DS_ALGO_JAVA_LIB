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
		
		int NN = 200005;
		int MOD = 1000000007;
		int INF = 2000000000;
		long INFINITY = 2000000000000000000L;

		int stepsNeeded=0;
		
		int [][] P;
		
		class Triplet {
			Integer first, second, index;
			public Triplet(int f, int s, int i) {
				this.first = f;
				this.second = s;
				this.index = i;
			}
		}
		
		Triplet [] L;
		
		int [] count;
		Triplet [] temp;
		
		void countSort() {
			int n = L.length;
			int range = Math.max(30, n + 1);
		    count = new int[range];
		    temp = new Triplet[n];
		    for(int i=0;i<range;++i) {
		    	count[i] = 0;
		    }
		    for(int i = 0 ;i < n ; i++) {
		        ++count[L[i].second + 1];
		    }
		    for(int i = 1 ; i  < range ; i++) {
		        count[i] += count[i-1];
		    }
		    for(int i = 0 ; i<n ; i++) {
		        temp[count[L[i].second +1] - 1] = L[i];
		        --count[L[i].second + 1];
		    }  
		    for(int i=0;i<range;++i) {
		    	count[i] = 0;
		    }
		    for(int i = 0 ; i < n ; i ++) {
		        ++count[temp[i].first + 1];
		    }
		    for(int i = 1 ; i<range ; i++) {
		        count[i] += count[i-1];
		    }
		    for(int i = n- 1; i>=0 ; i--) {
		        L[count[temp[i].first + 1] - 1] = temp[i];
		        count[temp[i].first + 1]--;
		    }
		}
		
		void buildSuffixArray(String s) {
			int n = s.length();
			P = new int[20][n];
			L = new Triplet[n];
			for(int i=0;i<n;++i) {
				P[0][i] = s.charAt(i) - 'a';
			}
			++stepsNeeded;
			for(int cnt=1, stp=1;cnt<n;cnt<<=1,++stp, ++stepsNeeded) {
				for(int i=0;i<n;++i) {
					L[i] = new Triplet(
							P[stp - 1][i],
							i+cnt < n ? P[stp-1][i+cnt] : -1,
							i);
				}
				countSort();
				for(int i=0;i<n;++i) {
					P[stp][L[i].index] = (i > 0
							&& L[i-1].first == L[i].first
							&& L[i-1].second == L[i].second
							? P[stp][L[i-1].index] : i);
				}
			}
		}

		int getLCP(int x, int y, int n) {
			int ret = 0;
			for(int i=stepsNeeded-1;i>=0;--i) {
				if(x < n && y < n && P[i][x] == P[i][y]) {
					ret += 1<<i;
					x += 1<<i;
					y += 1<<i;
				}
			}
			return ret;
		}
		
		public void solve(InputReader in, PrintWriter out) {
			String s = in.next();
			buildSuffixArray(s);
			int n = s.length();
			Triplet[] a = new Triplet[n];
			long [] sum = new long[n];
			for(int i=0;i<n;++i) {				
				a[i] = new Triplet(n-L[i].index - (i > 0 ?
						getLCP(L[i].index, L[i-1].index, n) : 0),
						L[i].index, -1);
				sum[i] = (i > 0 ? sum[i-1] : 0) + a[i].first;
			}
			int q = in.nextInt();
			while(q-->0) {
				int k = in.nextInt();
				int lo = 0, hi = n - 1, mid = 0;
				while(lo < hi) {
					mid = (lo + hi) / 2;
					if(sum[mid] < k) {
						lo = mid + 1;
					} else {
						hi = mid;
					}
				}
				int suffixIndex = a[lo].second;
				int suffixLength = n - suffixIndex;
				int distinctPrefixes = a[lo].first;
				k -= (lo > 0 ? sum[lo - 1] : 0);
				int endIndex = suffixLength - distinctPrefixes
						+ k - 1;
				out.println(s.substring(suffixIndex, suffixIndex + endIndex + 1));
			}
		}
		
	}
	
	static void prepareIO(boolean isFileIO) {
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