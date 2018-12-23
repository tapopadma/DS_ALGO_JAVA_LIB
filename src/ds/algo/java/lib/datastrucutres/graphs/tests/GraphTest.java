package ds.algo.java.lib.datastrucutres.graphs.tests;

import java.io.PrintStream;
import java.util.Scanner;

import ds.algo.java.lib.datastrucutres.graphs.Graph;

public class GraphTest {

	static final PrintStream out = System.out;
	
	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		int n = sc.nextInt();
		int m = sc.nextInt();
		/*Graph Gtf = new Graph(n, m, true, false);
		for(int i=1;i<=m;++i) {
			Gtf.addEdge(sc.nextInt(), sc.nextInt());
		}
		out.println(Gtf.hasCycle());*/
		/*Graph Gff = new Graph(n, m, false, false);
		for(int i=1;i<=m;++i) {
			Gff.addEdge(sc.nextInt(), sc.nextInt());
		}
		out.println(Gff.hasCycle());*/
		Graph Gft = new Graph(n, m, false, true);
		for(int i=1;i<=m;++i) {
			Gft.addEdge(sc.nextInt(), sc.nextInt(), sc.nextInt());
		}
		int [] cost = Gft.bellmanFord(1);
		for(int i=0;i<cost.length;++i) {
			out.println(cost[i]);
		}
	}

}
