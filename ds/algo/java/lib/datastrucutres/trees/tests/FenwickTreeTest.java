package ds.algo.java.lib.datastrucutres.trees.tests;

import java.util.Scanner;

import ds.algo.java.lib.datastrucutres.trees.FenwickTree;

public class FenwickTreeTest {

	static int [] a;
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		int n = scanner.nextInt();
		a = new int[n];
		for(int i=0;i<n;++i) {
			a[i] = scanner.nextInt();
		}
		FenwickTree T = new FenwickTree(a);
		int q = scanner.nextInt();
		while(q-- > 0) {
			int type = scanner.nextInt();
			if(type == 0) {
				int index = scanner.nextInt();
				int value = scanner.nextInt();
				T.update(index, value);
			} else {
				int index = scanner.nextInt();
				System.out.println("Prefix sum: " + T.query(index));
			}
		}
	}

}
