package ds.algo.java.lib.datastrucutres.queue;

public class Queue {

	int [] a;
	final int NN = 100000;
	int tot = NN;
	int l = 0, r = -1;
	
	public Queue() {
		a = new int[NN];
	}
	
	public void push(int data) {
		r = (r + 1)%NN;
		a[r] = data;
	}
	
	public void pop() {
		l = (l + 1)%NN;
	}
	
	public int front() {
		return a[l];
	}
	
	public void clear() {
		l = 0;r = -1;
	}
	
}
