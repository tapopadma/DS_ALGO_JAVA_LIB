package ds.algo.java.lib.datastrucutres.matrix;

public class Matrix {

	long [][] a;
	int n, m;
	public Matrix(int n, int m) {
		a = new long[n][m];
		this.n = n;this.m = m;
	}
	
	public Matrix(long [][] d) {
		this.n= d.length;
		this.m = d[0].length;
		a = new long[n][m];
		for(int i=0;i<n;++i) {
			for(int j=0;j<m;++j) {
				a[i][j] = d[i][j];
			}
		}
	}
	
	public Matrix multiply(Matrix mat) {
		if(this.m != mat.n)
			return null;
		Matrix res = new Matrix(this.n, mat.m);
		for(int i=0;i<this.n;++i) {
			for(int j=0;j<mat.m;++j) {
				long total = 0;
				for(int k=0;k<this.m;++k) {
					total = (total + this.a[i][k]*mat.a[k][j]);
				}
				res.a[i][j] = total;
			}
		}
		return res;
	}
	
	public Matrix unit() {
		Matrix res = new Matrix(this.n, this.m);
		for(int i=0;i<n;++i) {
			for(int j=0;j<m;++j) {
				if(i == j) {
					res.a[i][j] = 1;
				} else {
					res.a[i][j] = 0;
				}
			}
		}
		return res;
	}
	
	public Matrix clone() {
		Matrix res = new Matrix(this.n, this.m);
		for(int i=0;i<n;++i) {
			for(int j=0;j<m;++j) {
				res.a[i][j] = this.a[i][j];
			}
		}
		return res;
	}
	
	public Matrix pow(long exponent) {
		Matrix ret = this.unit(), sq = this.clone();
		while(exponent > 0) {
			if(exponent%2 != 0) {
				ret = ret.multiply(sq);
			}
			exponent >>= 1;
			sq = sq.multiply(sq);
		}
		return ret;
	}
	
}
