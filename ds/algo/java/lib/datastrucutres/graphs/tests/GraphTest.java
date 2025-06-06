package ds.algo.java.lib.datastrucutres.graphs.tests;

import java.io.PrintStream;
import java.util.Scanner;

import ds.algo.java.lib.datastrucutres.graphs.Graph;

public class GraphTest {

  static final PrintStream out = System.out;

  static void validate(int[][] a, int[][] b) {
    for(int i=0;i<a.length;++i){
        for(int j=0;j<a[0].length;++j) {
            if(a[i][j]!=b[i+1][j+1]){
                throw new RuntimeException("x y don't match i,j " + i + " " + j);
            }
        }
    }
  }

  static void validate(int[] a, int[] b) {
    for(int i=0;i<a.length;++i){
        validate(a[i],b[i+1]);
    }
  }

  static void validate(boolean a, boolean b) {
    if(a != b) {
        throw new RuntimeException("a b don't match " + a + " " + b);
    }
  }

  static void validate(int a, int b) {
    if(a != b) {
        throw new RuntimeException("a b don't match " + a + " " + b);
    }
  }

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
    validate(
        new int[][]{new int[]{1,1,1,1},new int[]{1,1,1,1},new int[]{1,1,1,1},new int[]{0,0,0,1}},
        new Graph().buildFromAdjMatrix(new int[][]{new int[]{0,1,1,0},new int[]{0,0,1,0},new int[]{1,0,0,1},new int[]{0,0,0,1}}).transitiveClosure());
    validate(
        true, new Graph()
        .buildGraph(6, new int[][]{new int[]{1,2},new int[]{1,3},new int[]{4,5},new int[]{5, 6},new int[]{4,6}})
        .hasCycleUndirectedWithParentTracking());
    validate(
        false, new Graph()
        .buildGraph(4, new int[][]{new int[]{1,2},new int[]{2,3},new int[]{4,3}})
        .hasCycleUndirectedWithParentTracking());
    validate(true, new Graph().buildWeightedGraph(3,new int[][]{new int[]{1,2,-1},new int[]{2,3,2},new int[]{3,1,-2}}, true).detectNegativeCycle(1));
    validate(true, new Graph().buildWeightedGraph(5,new int[][]{new int[]{1,2,4},new int[]{2,3,1},new int[]{3,4,-2},new int[]{4,5,-1},new int[]{5,2,1}}, true)
        .detectNegativeCycle(1));
    validate(false, new Graph().buildWeightedGraph(5,new int[][]{new int[]{1,2,4},new int[]{2,3,1},new int[]{3,4,-2},new int[]{4,5,-1},new int[]{5,2,2}}, true)
        .detectNegativeCycle(1));
    validate(false, new Graph().buildWeightedGraph(5,new int[][]{new int[]{1,2,4},new int[]{2,3,1},new int[]{3,4,-2},new int[]{4,5,-1},new int[]{5,2,3}}, true)
        .detectNegativeCycle(1));
    validate(new int[]{0,4,8,10,10},new Graph()
        .buildWeightedGraph(5,new int[][]{new int[]{1,2,4},new int[]{1,3,8},new int[]{2,5,6},new int[]{3,4,2},new int[]{4,5,10}})
        .dijkstra(1));
    validate(new int[]{0, 5, 6, 6, 7},new Graph()
        .buildWeightedGraph(5,new int[][]{new int[]{1,2,5},new int[]{2,3,1},new int[]{2,4,2},new int[]{3,5,1},new int[]{5,4,-1}},true)
        .bellmanFord(1));
    validate(new int[]{Integer.MAX_VALUE,0,2,6,5,3},new Graph()
        .buildWeightedGraph(6,new int[][]{new int[]{1,2,5},new int[]{1,3,3},new int[]{2,4,6},new int[]{2,3,2},new int[]{3,5,4},new int[]{3,6,2},
            new int[]{3,4,7},new int[]{4,5,-1},new int[]{5,6,-2}},true)
        .shortestPathDAGNegativeWeights(2));
    validate(new int[]{0,1,1,2,5,1,2,2,2},new Graph()
        .buildWeightedGraph(9,new int[][]{new int[]{1,2,1},new int[]{1,4,2},new int[]{2,3,0},new int[]{2,5,6},new int[]{3,6,0},new int[]{4,7,2},
            new int[]{4,5,5},new int[]{5,6,6},new int[]{5,8,5},new int[]{6,9,3},new int[]{7,8,2},new int[]{8,9,2}})
        .minimisePathMaxima(1));
    validate(4,new Graph()
        .buildWeightedGraph(7,new int[][]{new int[]{1,7,7},new int[]{1,2,2},new int[]{2,3,3},new int[]{2,4,3},new int[]{7,4,3},new int[]{4,6,1},
            new int[]{7,6,1},new int[]{3,6,1},new int[]{1,5,5},new int[]{5,7,2}})
        .noOfShortestPaths(1, 7));
    validate(new int[]{0,4,12,19,21,11,9,8,14},new Graph()
        .buildWeightedGraph(9,new int[][]{new int[]{1, 2, 4},new int[]{1, 8, 8},new int[]{2, 3, 8},new int[]{2, 8, 11},new int[]{3, 4, 7},new int[]{3, 9, 2},
            new int[]{4, 5, 9},new int[]{4, 6, 14},new int[]{5, 6, 10},new int[]{6, 7, 2},new int[]{7, 8, 1},new int[]{7,9, 6},new int[]{8,9, 7}})
        .dijkstraLightWeight(1));
    validate(6,new Graph()
        .buildWeightedGraph(5,new int[][]{new int[]{1, 2, 2},new int[]{2,3,2},new int[]{3,4,4},new int[]{4,2,1},new int[]{2,5,1},new int[]{1,5,3}})
        .findMinWeightCycle());
  }
}
