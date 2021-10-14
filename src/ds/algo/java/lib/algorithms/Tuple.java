package ds.algo.java.lib.algorithms;

public class Tuple {
    Integer [] ara;
    Integer x, y, z, t, w;
    public Tuple(Integer... a) {
        this.ara = a;
        if(ara.length > 0) this.x = ara[0];
        if(ara.length > 1) this.y = ara[1];
        if(ara.length > 2) this.z = ara[2];
        if(ara.length > 3) this.t = ara[3];
        if(ara.length > 4) this.w = ara[4];
    }
}
