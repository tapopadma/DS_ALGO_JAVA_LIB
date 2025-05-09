package ds.algo.java.lib.algorithms;

import ds.algo.java.lib.io.FastInputReader;
import java.io.PrintWriter;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/** */
public class Sorts implements StandardAlgoSolver {

  int[] a;
  int n = 1000;
  Random rand = ThreadLocalRandom.current();

  void buildArray() {
    a = new int[n];
    for (int i = 0; i < n; ++i) {
      a[i] = rand.nextInt(n) + 1;
    }
  }

  void validate() {
    for (int i = 0; i + 1 < n; ++i) {
      if (a[i] > a[i + 1]) {
        throw new RuntimeException("Not sorted ");
      }
    }
  }

  void selectionSort() {
    for (int i = 0; i < n; ++i) {
      for (int j = i + 1; j < n; ++j) {
        if (a[i] > a[j]) {
          int t = a[i];
          a[i] = a[j];
          a[j] = t;
        }
      }
    }
  }

  void bubbleSort() {
    for (int i = 0; i < n; ++i) {
      for (int j = 0; j + 1 < n - i; j++) {
        if (a[j] > a[j + 1]) {
          int t = a[j];
          a[j] = a[j + 1];
          a[j + 1] = t;
        }
      }
    }
  }

  void insertionSort() {
    for (int i = 1; i < n; ++i) {
      int j = i;
      while (j > 0 && a[j - 1] > a[j]) {
        int t = a[j];
        a[j] = a[j - 1];
        a[j - 1] = t;
        j--;
      }
    }
  }

  public void solve(FastInputReader in, PrintWriter out) {
    buildArray();
    selectionSort();
    validate();
    buildArray();
    bubbleSort();
    validate();
    buildArray();
    insertionSort();
    validate();
  }
}
