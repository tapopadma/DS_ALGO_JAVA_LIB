package ds.algo.java.lib.algorithms;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ds.algo.java.lib.io.FastInputReader;

public class Tree implements StandardAlgoSolver{

    @Override
    public void solve(FastInputReader in, PrintWriter out) {
        int n = in.nextInt();
        List<Tuple>[] g = new ArrayList[n];
        for(int i=0;i<n;++i)g[i]=new ArrayList<>();
        for(int i=1;i<n;++i){
            int u = in.nextInt();
            int v = in.nextInt();
            int w = in.nextInt();
            g[u].add(new Tuple(v, w));
            g[v].add(new Tuple(u, w));
        }
        Tuple diameter = findDiameter(g);
        out.println("("+diameter.x+", "+diameter.y+") "+diameter.z);
    }
    
    /**
     * 1. Find farthest node s from a random node r.
     * 2. Find farthest node t from node s.
     * Now (s, t) form the diameter in the graph.
     * 
     * Logic: any 2 farthest distance away pair would always pass through the diameter.
     * 
     * ALTERNATIVE: max(sum(max1,max2) for each node) 
     * using dfs https://www.spoj.com/submit/PT07Z/id=28570086.
     * @param g
     * @return tuple(start, end, diameter)
     */
    public Tuple findDiameter(List<Tuple>[] g) {
        int [] d = new int[g.length];
        Arrays.fill(d, 0);  
        dfs(1, -1, g, d);
        Tuple t1 = findFarthest(d);
        Arrays.fill(d, 0);
        dfs(t1.y, -1, g, d);
        Tuple t2 = findFarthest(d);
        return new Tuple(t1.y, t2.y, t2.x);
    }

    Tuple findFarthest(int [] d) {
        Tuple ret = null;
        for(int i=0;i<d.length;++i) {
            if(ret == null || d[i] > ret.x) {
                ret = new Tuple(d[i], i);
            }
        }
        return ret;
    }

    void dfs(int x, int px, List<Tuple>[] g, int [] d) {
        for(Tuple e: g[x]) {
            if(e.x==px)continue;
            d[e.x.intValue()]=d[x]+e.y;
            dfs(e.x.intValue(), x, g, d);
        }
    }

}
