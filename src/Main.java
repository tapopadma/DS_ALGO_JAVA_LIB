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
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

public class Main {

	static class Task {
		
		int NN = 1000006;
		int MOD = 1000000007;
		
		public void solve(InputReader in, PrintWriter out) {
			int n = in.nextInt();
			int [] a = new int[n];
			int [] gr = new int[n];
			int [] le = new int[n];
			for(int i=0;i<n;++i) {
				a[i] = in.nextInt();
			}
			for(int i=0;i<n;++i) {
				int cnt = 0;
				for(int j=i+1;j<n;++j) {
					if(a[j - 1] < a[j]) {
						++cnt;
						if(cnt > 4) {
							out.println(-1);return;
						}
					} else {
						break;
					}
				}
				gr[i] = cnt;
			}
			for(int i=0;i<n;++i) {
				int cnt = 0;
				for(int j=i+1;j<n;++j) {
					if(a[j - 1] > a[j]) {
						++cnt;
						if(cnt > 4) {
							out.println(-1);return;
						}
					} else {
						break;
					}
				}
				le[i] = cnt;
			}
			List<Integer> process = new ArrayList<>();
			for(int i=0;i<n;++i) {
				process.add(i);
			}
			process = process.stream()
					.sorted((j, i)->Integer.valueOf(gr[i]+le[i])
							.compareTo(Integer.valueOf(gr[j] + le[j]))
					).collect(Collectors.toList());
			int [] b = new int[n];
			for(int idx: process) {
				if(b[idx] > 0 && (idx > 1 && (gr[idx - 1] + 1 == gr[idx]
						|| le[idx - 1] + 1 == le[idx]))) {
					continue;
				}
				if(le[idx] > 0) {
					b[idx] = 5;
					int cnt = le[idx];
					int idx1 = idx;
					while(cnt-- > 0) {
						++idx1;
						if(b[idx1] > 0) {
							if(b[idx1] > b[idx1 - 1] - 1) {
								out.println(-1);return;
							}
							continue;
						}
						b[idx1] = b[idx1 - 1] - 1;
					}
				} else if(gr[idx] > 0){
					b[idx] = 1;
					int cnt = gr[idx];
					int idx1 = idx;
					while(cnt-- > 0) {
						++idx1;
						if(b[idx1] > 0) {
							if(b[idx1] < b[idx1 - 1] + 1) {
								out.println(-1);return;
							}
							continue;
						}
						b[idx1] = b[idx1 - 1] + 1;
					}
				}
			}
			if(b[0] == 0) {
				int pos = -1;
				for(int i=0;i<n;++i) {
					if(b[i] > 0) {
						pos = i;break;
					}
				}
				if(pos == -1) {
					for(int i=0;i<n;++i) {
						out.print((((i%2)==0)?1:2) + " ");
					}
					out.println("");return;
				}
				for(int i=pos-1;i>=0;--i) {
					int gap = pos - i;
					b[i] = (((gap%2)==0)?a[pos]:a[pos + 1]);
				}
			}
			for(int i=0;i<n;++i) {
				if(b[i] > 0) {
					continue;
				}
				if((i + 1) < n && b[i - 2] == b[i + 1]) {
					b[i] = getDiff(new int[] {b[i - 1], b[i + 1]});
				} else {
					b[i] = b[i - 2];
				}
			}
			for(int i=0;i<n;++i) {
				out.print(b[i] + " ");
			}
			out.println("");
		}
		
		int getDiff(int [] r) {
			for(int i=1;i<=5;++i) {
				boolean found = false;
				for(int j=0;j<r.length;++j) {
					if(i == r[j]) {
						found = true;break;
					}
				}
				if(!found) {
					return i;
				}
			}
			return -1;
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
        String IPfilePath = "~/Downloads/ip.in";
        String OPfilePath = "~/Downloads/op.out";
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
