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
		
		int NN = 100005;
		int MOD = 1000000007;
		
		public void solve(InputReader in, PrintWriter out) {
			Line L = new Line();
			L.a = in.nextDouble();
			L.b = in.nextDouble();
			L.c = in.nextDouble();
			Point A = new Point();
			A.x = in.nextDouble();A.y = in.nextDouble();
			Point B = new Point();
			B.x = in.nextDouble();B.y = in.nextDouble();
			Point Ax = L.getXProjection(A);
			Point Ay = L.getYProjection(A);
			Point Bx = L.getXProjection(B);
			Point By = L.getYProjection(B);
			double ans = A.manhattanDistance(B);
			//xx
			if(Ax != null && Bx != null) {
				ans = Math.min(ans, 
					A.cartesianDistance(Ax) + Ax.cartesianDistance(Bx)
					+ Bx.cartesianDistance(B));
			}
			//xy
			if(Ax != null && By != null) {
				ans = Math.min(ans, 
					A.cartesianDistance(Ax) + Ax.cartesianDistance(By)
					+ By.cartesianDistance(B));
			}
			//yx
			if(Ay != null && Bx != null) {
				ans = Math.min(ans,
					A.cartesianDistance(Ay) + Ay.cartesianDistance(Bx)
					+ Bx.cartesianDistance(B));
			}
			//yy
			if(Ay != null && By != null) {
				ans = Math.min(ans,
					A.cartesianDistance(Ay) + Ay.cartesianDistance(By)
					+ By.cartesianDistance(B));
			}
			out.println(ans);
		}
		
		class Line {
			public double a, b, c;
			public Line() {
				
			}
			
			public Point getXProjection(Point p) {
				if(a == 0) {
					return null;
				}
				return new Point((-this.c-this.b*p.y)/a, p.y);
			}
			
			public Point getYProjection(Point p) {
				if(b == 0) {
					return null;
				}
				return new Point(p.x, (-this.c-this.a*p.x)/b);
			}
			
		}
		
		class Point {
			public double x;
			public double y;
			public Point(double x, double y) {
				this.x = x;
				this.y = y;
			}
			public Point() {
				// TODO Auto-generated constructor stub
			}
			public double cartesianDistance(Point p) {
				return Math.sqrt(square(this.x - p.x) + square(this.y - p.y));
			}
			
			public double manhattanDistance(Point p) {
				return Math.abs(this.x - p.x) + Math.abs(this.y - p.y);
			}
			
			double square(double x) {
				return x * x;
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

        public double nextDouble() {
        	return Double.parseDouble(next());
        }
        
    }

}