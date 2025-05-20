package ds.algo.java.lib.datastrucutres.graphs.tests;

import java.io.PrintStream;
import java.util.Scanner;

import ds.algo.java.lib.datastrucutres.graphs.Graph;

public class GraphTest {

  static final PrintStream out = System.out;

  public static void main(String[] args) {

    // Scanner sc = new Scanner(System.in);
    // int n = sc.nextInt();
    // int m = sc.nextInt();
    // /*Graph Gtf = new Graph(n, m, true, false);
    // for(int i=1;i<=m;++i) {
    // 	Gtf.addEdge(sc.nextInt(), sc.nextInt());
    // }
    // out.println(Gtf.hasCycle());*/
    // /*Graph Gff = new Graph(n, m, false, false);
    // for(int i=1;i<=m;++i) {
    // 	Gff.addEdge(sc.nextInt(), sc.nextInt());
    // }
    // out.println(Gff.hasCycle());*/
    // Graph Gft = new Graph(n, m, false, true);
    // for (int i = 1; i <= m; ++i) {
    //   Gft.addEdge(sc.nextInt(), sc.nextInt(), sc.nextInt());
    // }
    // int[] cost = Gft.bellmanFord(1);
    // for (int i = 0; i < cost.length; ++i) {
    //   out.println(cost[i]);
    // }
    System.out.println(new Graph().isBipartite(4, new int[][] {{1, 2}, {1, 3}, {2, 3}, {3, 4}}));
    System.out.println(new Graph().isBipartite(2, new int[][] {{1, 2}, {2, 1}}));
    System.out.println(
        new Graph().isReachable(5, new int[][] {{2, 1}, {2, 3}, {3, 1}, {3, 4}, {5, 3}}, 2, 4));
    System.out.println(
        new Graph().isReachable(5, new int[][] {{2, 1}, {2, 3}, {3, 1}, {3, 4}, {5, 3}}, 1, 4));
    System.out.println(
        new Graph()
            .maxCostPathWithMaxKNodes(
                3, new int[][] {{1, 2, 100}, {2, 3, 100}, {1, 3, 500}}, 1, 3, 2));
  }
}
