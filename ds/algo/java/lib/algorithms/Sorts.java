package ds.algo.java.lib.algorithms;

import ds.algo.java.lib.io.FastInputReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/** Implementation of various sorting algorithms. */
public class Sorts implements StandardAlgoSolver {

  int[] a;
  int n = 1000;
  Random rand = ThreadLocalRandom.current();

  void buildArray(int n1) {
    n = n1;
    buildArray();
  }

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

  // choose smallest element from i to n and swap with i.
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

  // push the largest element to (n-i-1)th position.
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

  // insert ith element in the left sorted subarray by pushing it to right position in the left.
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

  void mergeSort(int l, int r) {
    if (l >= r) {
      return;
    }
    int mid = (l + r) / 2;
    mergeSort(l, mid);
    mergeSort(mid + 1, r);
    List<Integer> a1 = new ArrayList<>();
    List<Integer> a2 = new ArrayList<>();
    for (int i = l; i <= mid; ++i) {
      a1.add(a[i]);
    }
    for (int i = mid + 1; i <= r; ++i) {
      a2.add(a[i]);
    }
    int i = 0;
    int j = 0;
    int k = l;
    while (i < a1.size() || j < a2.size()) {
      if (i == a1.size()) {
        a[k++] = a2.get(j++);
      } else if (j == a2.size()) {
        a[k++] = a1.get(i++);
      } else {
        if (a1.get(i) < a2.get(j)) {
          a[k++] = a1.get(i++);
        } else {
          a[k++] = a2.get(j++);
        }
      }
    }
  }

  // sort each half and merge both sorted halves in sorted order. O(nlogn) but O(n) space.
  void mergeSort() {
    mergeSort(0, n - 1);
  }

  int positionThePivot(int l, int r) {
    int j = l;
    for (int i = l; i < r; ++i) {
      if (a[i] < a[r]) {
        int t = a[j];
        a[j] = a[i];
        a[i] = t;
        j++;
      }
    }
    int t = a[j];
    a[j] = a[r];
    a[r] = t;
    return j;
  }

  void quickSort(int l, int r) {
    if (l >= r) {
      return;
    }
    int pivot = positionThePivot(l, r);
    quickSort(l, pivot - 1);
    quickSort(pivot + 1, r);
  }

  // choose last element as pivot, swap all numbers smaller than pivot to the left and finally swap
  // pivot with first bigger number. O(nlogn) on avg, O(n^2) on worst case.
  void quickSort() {
    quickSort(0, n - 1);
  }

  void countSort(int div) {
    int[] cnt = new int[10];
    int[] b = new int[n];
    for (int i = 0; i < 10; ++i) {
      cnt[i] = 0;
    }
    for (int i = 0; i < n; ++i) {
      cnt[(a[i] / div) % 10]++;
    }
    for (int i = 1; i < 10; ++i) {
      cnt[i] += cnt[i - 1];
    }
    for (int i = n - 1; i >= 0; --i) {
      int d = (a[i] / div) % 10;
      b[cnt[d] - 1] = a[i];
      --cnt[d];
    }
    for (int i = 0; i < n; ++i) {
      a[i] = b[i];
    }
  }

  // (count) sort the numbers in the increasing order of significance of their digits. O(d*n)
  void radixSort() {
    for (int div = 1; ; div *= 10) {
      boolean found = false;
      for (int i = 0; i < n; ++i) {
        if (a[i] >= div) {
          found = true;
          break;
        }
      }
      if (!found) {
        break;
      }
      countSort(div);
    }
  }

  @Override
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
    buildArray(1000000);
    mergeSort();
    validate();
    buildArray(100000);
    quickSort();
    validate();
    buildArray(200000);
    radixSort();
    validate();
  }
}
