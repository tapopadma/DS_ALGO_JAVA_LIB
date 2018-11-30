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
				AVLTree tree = new AVLTree();
				for(int j=1;j<=n;++j) {
					tree.add(dp[i - 1][j - 1] + a[j - 1]);
					if(j-1-K >= 0) {
						tree.remove(dp[i - 1][j - 1 - K] + a[j - 1 - K]);
					}
					dp[i][j] = tree.getMax();
				}
			}
			out.println(dp[x][n] < 0 ? -1 : dp[x][n]);
		}
		
		public class AVLTree{
			class Node {
				long data;
				int freq;
				int height;
				Node left;
				Node right;
				public Node(long data) {
					this.data = data;
					this.freq = 1;
					this.height = 0;
					this.left = null;
					this.right = null;
				}
			}
			Node root;
			public void add(long value) {
				root = insert(root, value);
			}
			
			Node insert(Node node, long value) {
				if(node == null) {
					node = new Node(value);
					return node;
				}
				if(node.data == value) {
					++node.freq;
					return node;
				}
				if(node.data > value) {
					node.left = insert(node.left, value);
				} else {
					node.right = insert(node.right, value);
				}
				node = updateHeight(node);
				node = balance(node);
				return node;
			}
			
			Node updateHeight(Node node) {
				if(node == null) {
					return null;
				}
				int height = 0;
				if(node.left != null) {
					height = Math.max(height, node.left.height + 1);
				}
				if(node.right != null) {
					height = Math.max(height, node.right.height + 1);
				}
				node.height = height;
				return node;
			}
			
			public void remove(long value) {
				root = delete(root, value);
			}
			
			Node delete(Node node, long value) {
				if(node == null) {
					return null;
				}
				if(node.data == value) {
					if(node.freq == 1) {
						if(node.left == null && node.right == null) {
							return null;
						}
						if(node.left == null) {
							return node.right;
						}
						if(node.right == null) {
							return node.left;
						}
						Node ptr = node.left;
						while(ptr.right != null) {
							ptr = ptr.right;
						}
						node.data = ptr.data;
						node.left = delete(node.left, ptr.data);
					} else {
						node.freq = node.freq - 1;
					}
				}
				else if(node.data > value) {
					node.left = delete(node.left, value);
				} else {
					node.right = delete(node.right, value);
				}
				node = updateHeight(node);
				node = balance(node);
				return node;
			}
			
			boolean isLeaf(Node node) {
				return node != null && node.left == null && node.right == null;
			}
			
			public long getMax() {
				Node ptr = root;
				while(ptr.right != null) {
					ptr = ptr.right;
				}
				return ptr.data;
			}
			
			int getLeftHeight(Node node) {
				if(node == null) {
					return 0;
				}
				return (node.left == null ? 0 : node.left.height + 1);
			}
			
			int getRightHeight(Node node) {
				if(node == null) {
					return 0;
				}
				return (node.right == null ? 0 : node.right.height + 1);
			}
			
			Node balance(Node node) {
				if(node == null) {
					return null;
				}
				int lh = getLeftHeight(node);
				int rh = getRightHeight(node);
				if(Math.abs(lh-rh) < 2) {
					return node;
				}
				if(lh > rh) {
					Node lnode = node.left;
					int llh = getLeftHeight(lnode);
					int lrh = getRightHeight(lnode);
					if(llh > lrh) {//LL
						node.left = lnode.right;
						lnode.right = node;
						node = lnode;
						node.right = updateHeight(node.right);
						node = updateHeight(node);
					} else {//LR
						Node lrnode = lnode.right;
						lnode.right = lrnode.left;
						lrnode.left = lnode;
						node.left = lrnode;
						node.left.left = updateHeight(node.left.left);
						node.left = updateHeight(node.left);
						node = updateHeight(node);
						node = balance(node);	
					}
				} else {
					Node rnode = node.right;
					int rlh = getLeftHeight(rnode);
					int rrh = getRightHeight(rnode);
					if(rrh > rlh) {//RR
						node.right = rnode.left;
						rnode.left = node;
						node = rnode;
						node.left = updateHeight(node.left);
						node = updateHeight(node);
					} else {
						Node rlnode = rnode.left;
						rnode.left = rlnode.right;
						rlnode.right = rnode;
						node.right = rlnode;
						node.right.right = updateHeight(node.right.right);
						node.right = updateHeight(node.right);
						node = updateHeight(node);
						node = balance(node);
					}
				}
				return node;
			}
			
			public void print() {
				inorder(root);System.out.println("");
			}
			
			public void inorder(Node node) {
				if(node == null) {
					return;
				}
				inorder(node.left);
				if(node.data >= 0) {
					System.out.print("("+node.data+"[" + node.freq+"])");
				}
				inorder(node.right);
			}
			
			public void clear() {
				 this.root = null;
			}
			
			public int getDepth() {
				return root == null ? 0 : root.height;
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
