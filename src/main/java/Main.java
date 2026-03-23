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
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.*;
import java.text.*;
 
 
public class Main {
 
	static class Task {

		public void solve(InputReader in, PrintWriter out) throws Exception {
			int t = in.nextInt();
			while(t-->0){
				int n = in.nextInt(), m = in.nextInt();
				String s = in.nextLine();
				Set<Integer> b = new HashSet<>();
				while(m-->0) {
					b.add(in.nextInt());
				}
				List<Integer> black = new ArrayList<>();
				int v = 1;
				if(b.contains(v))black.add(1);b.remove(1);
				for(int i=0;i<n;++i) {
					char c = s.charAt(i);
					if(c=='B') {
						while(b.contains(v+1)) {
							black.add(v+1);v++;b.remove(v);
						}
						black.add(++v);
						while(b.contains(v+1)) {
							black.add(v+1);v++;b.remove(v);
						}
						++v;
					} else {
						v++;
						black.add(v);b.remove(v);
					}
				}
				for(int bl: b)black.add(bl);
				out.println(black.size());
				black.stream().sorted().forEach(bl->out.print(bl + " "));
				out.println();
			}
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
	        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss yyyy-MM-dd");
			Date date = new Date();
	        fout.println("\n\ntime(s): " + dateFormat.format(date));
	        fout.close();
		}
	}
	
	public static void main(String[] args) throws Exception {
        prepareIO(false/*args[0].equals("true")*/);
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