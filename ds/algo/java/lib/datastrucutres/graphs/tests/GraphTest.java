package ds.algo.java.lib.datastrucutres.graphs.tests;

import java.io.PrintStream;
import java.util.Scanner;
import java.util.List;

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

  static void validate(List<List<Integer>> a, List<List<Integer>> b) {
    for(int i=0;i<a.size();++i){
        if(!((a.get(i).get(0) == b.get(i).get(0) && a.get(i).get(1) == b.get(i).get(1)) ||
            (a.get(i).get(0) == b.get(i).get(1) && a.get(i).get(1) == b.get(i).get(0)))) {
            throw new RuntimeException("x y don't match i,j " + a.get(i).get(0) + " " + a.get(i).get(1) + " != " + b.get(i).get(0) + " " + b.get(i).get(1));
        }
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
    validate(3, new Graph().pesudoVertexKruskal(3,new int[]{1,2,2},new int[][]{new int[]{1,2,1},new int[]{2,3,1}}));
    validate(4, new Graph().pesudoVertexKruskal(4,new int[]{1,1,1,1},new int[][]{new int[]{1,2,100},new int[]{2,3,100},new int[]{2,4,50}}));
    new Graph()
        .buildGraph(6, new int[][]{new int[]{3, 4}, new int[]{4, 2}, new int[]{5, 1}, new int[]{5, 2}, new int[]{6, 1}, new int[]{6, 3}},true)
        .sortTopologically();
    new Graph()
        .buildGraph(6, new int[][]{new int[]{3, 4}, new int[]{4, 2}, new int[]{5, 1}, new int[]{5, 2}, new int[]{6, 1}, new int[]{6, 3}},true)
        .sortTopologicallyUsingDepartureTime();
    validate(15, new Graph()
        .buildWeightedGraph(6, new int[][]{new int[]{1, 2, 5},new int[]{1, 3, 3},new int[]{2, 4, 6},
            new int[]{2, 3, 2},new int[]{3, 5, 4},new int[]{3, 6, 2},new int[]{3, 4, 7},new int[]{4, 6, 1},new int[]{4, 5, -1},
            new int[]{5, 6, -2}},true)
        .longestPath());
    new Graph()
        .buildGraph(5, new int[][]{new int[]{1, 2}, new int[]{2, 5}, new int[]{5, 4}, new int[]{5, 3}, new int[]{3, 4}})
        .findArticulationPoints();
    new Graph()
        .buildGraph(7, new int[][]{new int[]{1,2}, new int[]{2, 3}, new int[]{3, 4}, new int[]{2, 5}, new int[]{5, 6}, 
            new int[]{6, 2}, new int[]{2, 7},new int[]{7, 1}})
        .findArticulationPoints();
    new Graph()
        .buildGraph(7, new int[][]{new int[]{1,2}, new int[]{2, 3}, new int[]{3, 4}, new int[]{2, 5}, new int[]{5, 6}, 
            new int[]{6, 2}, new int[]{2, 7},new int[]{7, 1}})
        .findBiConnectedComponents();
    new Graph()
        .buildGraph(7, new int[][]{new int[]{1,2}, new int[]{2, 3}, new int[]{3, 4}, new int[]{4, 5}, 
            new int[]{5, 6}, new int[]{5, 7}, new int[]{6, 3},new int[]{7, 2}})
        .findBiConnectedComponents();
    new Graph()
        .buildGraph(12, new int[][]{
                            new int[]{1, 2},
                            new int[]{2, 3},
                            new int[]{2, 4},
                            new int[]{3, 4},
                            new int[]{3, 5},
                            new int[]{4, 5},
                            new int[]{2, 6},
                            new int[]{1, 7},
                            new int[]{6, 7},
                            new int[]{6, 8},
                            new int[]{6, 9},
                            new int[]{8, 9},
                            new int[]{9, 10},
                            new int[]{11, 12}})
        .findBiConnectedComponents();
    validate(List.of(List.of(4,5),List.of(1,4)), new Graph()
        .buildGraph(5, new int[][]{new int[]{2,1},new int[]{1,3},new int[]{3,2},new int[]{1,4},new int[]{4,5}})
        .findBridges());
    validate(List.of(List.of(2,7)), new Graph()
        .buildGraph(7, new int[][]{new int[]{1,2},new int[]{2,3},new int[]{3,1},new int[]{2,4},new int[]{2,5},new int[]{2,7},
                        new int[]{4,6},new int[]{5,6}})
        .findBridges());
    validate(2, new Graph()
        .buildGraph(5, new int[][]{new int[]{1,2},new int[]{1,3},new int[]{1,4},new int[]{1,5},new int[]{4,5},new int[]{2,3}})
    .findEulerianCycleOrEulerianPath());
    new Graph()
        .buildGraph(5, new int[][]{new int[]{1,2},new int[]{1,3},new int[]{1,4},new int[]{1,5},new int[]{4,5},new int[]{2,3}})
    .printEulerianCycleOrEulerianPath();
    validate(1, new Graph()
        .buildGraph(5, new int[][]{new int[]{1,2},new int[]{1,3},new int[]{1,4},new int[]{4,5},new int[]{2,3}})
    .findEulerianCycleOrEulerianPath());
    new Graph()
        .buildGraph(5, new int[][]{new int[]{1,2},new int[]{1,3},new int[]{1,4},new int[]{4,5},new int[]{2,3}})
    .printEulerianCycleOrEulerianPath();
    validate(0, new Graph()
        .buildGraph(5, new int[][]{new int[]{1,2},new int[]{1,3},new int[]{1,4},new int[]{4,5},new int[]{2,3},new int[]{2,4}})
    .findEulerianCycleOrEulerianPath());
    new Graph()
        .buildGraph(5, new int[][]{new int[]{1,2},new int[]{1,3},new int[]{1,4},new int[]{4,5},new int[]{2,3},new int[]{2,4}})
    .printEulerianCycleOrEulerianPath();
    new Graph()
        .buildGraph(5, new int[][]{new int[]{2, 1}, new int[]{1, 3}, new int[]{3, 2}, new int[]{1, 4}, new int[]{4, 5}}, true)
        .findStronglyConnectedComponents();
    new Graph()
        .buildGraph(4, new int[][]{new int[]{1, 2}, new int[]{2, 3}, new int[]{3, 4}}, true)
        .findStronglyConnectedComponents();
    new Graph()
        .buildGraph(7, new int[][]{new int[]{1, 2}, new int[]{2, 3}, new int[]{3, 1}, new int[]{2, 4},new int[]{2, 5},
                            new int[]{2, 7}, new int[]{4, 6},new int[]{5, 6}}, true)
        .findStronglyConnectedComponents();
    new Graph()
        .buildGraph(5, new int[][]{new int[]{1, 2},new int[]{2, 3}, new int[]{3, 4}, new int[]{3, 5}, new int[]{4, 1}, new int[]{5, 3}})
        .findStronglyConnectedComponents();
    validate(23, new Graph()
        .buildWeightedGraph(6, new int[][]{new int[]{1, 2, 16},new int[]{1, 3, 13},new int[]{2, 4, 12},
            new int[]{2, 3, 10},new int[]{3, 2, 4},new int[]{3, 5, 14},new int[]{4, 3, 9},new int[]{5, 4, 7},new int[]{4, 6, 20},
            new int[]{5, 6, 4}},true)
        .maxFlowFordFulkerson(1,6));
    validate(23, new Graph()
        .buildWeightedGraph(6, new int[][]{new int[]{1, 2, 16},new int[]{1, 3, 13},new int[]{2, 4, 12},
            new int[]{2, 3, 10},new int[]{3, 2, 4},new int[]{3, 5, 14},new int[]{4, 3, 9},new int[]{5, 4, 7},new int[]{4, 6, 20},
            new int[]{5, 6, 4}},true)
        .maxFlowDinic(1,6));
    new Graph()
        .buildWeightedGraph(6, new int[][]{new int[]{1, 2, 16},new int[]{1, 3, 13},new int[]{2, 4, 12},
            new int[]{2, 3, 10},new int[]{3, 2, 4},new int[]{3, 5, 14},new int[]{4, 3, 9},new int[]{5, 4, 7},new int[]{4, 6, 20},
            new int[]{5, 6, 4}},true)
        .findMinCuts(1,6);
    validate(5, new Graph()
        .buildGraph(12, new int[][]{new int[]{1, 8},new int[]{1, 9}, new int[]{3, 7}, new int[]{3, 10}, new int[]{4, 9}, new int[]{5, 9},
                        new int[]{5, 10}, new int[]{6, 12}}, true)
        .maxBipartiteMatching());
    validate(80, new Graph()
        .buildWeightedGraph(4, new int[][]{new int[]{1, 2, 10},new int[]{1, 3, 15},new int[]{1, 4, 20},
            new int[]{2, 3, 35},new int[]{2, 4, 25},new int[]{3, 4, 30}})
        .travellingSalesManMinCost());
  }
}
