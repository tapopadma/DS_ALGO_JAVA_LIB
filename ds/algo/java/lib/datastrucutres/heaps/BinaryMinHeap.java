package ds.algo.java.lib.datastrucutres.heaps;


/**
 * push : pushes new data to heap
 * pop  : pops the minimum data from heap
 * top  : returns the minimum data
 * @author tapopadma
 *
 */
public class BinaryMinHeap {

	int [] a;
	int tot;
	final int NN = 2000000;
	
	public BinaryMinHeap() {
		a = new int[NN];
		tot = 0;
	}
	
	public void push(int val) {
		a[++tot] = val;
		int i = tot;
		while(i/2 >= 1 && a[i/2] > a[i]) {
			int t = a[i/2];a[i/2]=a[i];a[i] = t;
			i /= 2;
		}
	}
	
	public void pop() {
		int i = 1;a[i] = a[tot--];
		while(i <= tot) {
			if(i*2 > tot) {
				break;
			}
			if(i*2+1 > tot) {
				if(a[i*2] < a[i]) {
					int t = a[i];a[i]=a[i*2];a[i*2]=t;
				}
				break;
			}
			int mn = Math.min(a[i*2], a[i*2+1]);
			if(mn < a[i]) {
				if(mn == a[i*2]) {
					int t = a[i];a[i]=a[i*2];a[i*2]=t;
					i = i * 2;
				} else {
					int t = a[i];a[i]=a[i*2 + 1];a[i*2 + 1]=t;
					i = i * 2 + 1;
				}
			} else {
				break;
			}
		}
	}
	
	public int top() {
		return tot > 0 ? a[1] : -1;
	}
	
	public boolean isEmpty() {
		return tot == 0;
	}
	
}
