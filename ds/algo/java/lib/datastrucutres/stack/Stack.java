package ds.algo.java.lib.datastrucutres.stack;

public class Stack {

	int [] a;
	int top = -1;
	final int MAXN = 100000;
	
	public Stack() {
		a = new int[MAXN];
	}
	
	public void push(int data) {
		if(top+1 == MAXN) {
			return;
		}
		a[++top] = data;
	}
	
	public void pop() {
		--top;
	}
	
	public boolean isEmpty() {
		return top == -1;
	}
	
	public void clear() {
		top = -1;
	}
	
	public int top() {
		return a[top];
	}
	
}
