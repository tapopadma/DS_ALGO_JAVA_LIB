package ds.algo.java.lib.datastrucutres.linkedlists;

public class LinkedList {

	class Node {
		int key;
		Node next;
		public Node() {

		}
		public Node(int key) {
			this.key = key;
			this.next = null;
		}
		public Node(int key, Node next) {
			this.key = key;
			this.next = next;
		}
	}

	Node head;
	int length;
	
	public LinkedList() {
		head = null;
		length = 0;
	}

	public int size() {
		return length;
	}
	
	public void print() {
		if(hasLoop()) {
			System.out.println("Loop detected");
			return;
		}
		for(Node ptr = head;ptr != null;ptr=ptr.next) {
			System.out.print(ptr.key+"->");
		}
		System.out.println("");
	}
	
	//O(N)
	public void pushBack(int value) {
		if(head == null) {
			head = new Node(value);
		} else {
			Node ptr = head;
			while(ptr.next != null) {
				ptr = ptr.next;
			}
			ptr.next = new Node(value);
		}
		++length;
	}
	
	//O(1)	
	public void pushFront(int value) {
		Node oldHead = head;
		head = new Node(value, oldHead);
		++length;
	}
	
	public Node getNodeAt(int index) {
		if(index >= length)
			return null;
		int idx = 0;
		Node ptr = head;
		while(idx < index) {
			idx++; ptr = ptr.next;
		}
		return ptr;
	}
	
	//O(N)
	public int get(int index) {
		return getNodeAt(index).key;
	}
	
	//O(N)
	public void insertAt(int index, int value) {
		if(index >= length) {
			return;
		}
		int idx = 0;
		Node ptr = head;
		while(idx < index) {
			++idx;
			ptr = ptr.next;
		}
		Node oldNext = ptr.next;
		ptr.next = new Node(value, oldNext);
		++length;
	}
	
	//O(N)
	public void deleteAt(int index) {
		if(index >= length){
			return;
		}
		if(index == 0) {
			head = head.next;
		} else {
			int idx = 0;
			Node ptr = head;
			while(idx + 1 < index) {
				++idx;
				ptr = ptr.next;
			}
			ptr.next = ptr.next.next;
		}
		--length;
	}
	
	//O(N)
	public boolean contains(int value) {
		for(Node ptr=head;ptr!=null;ptr=ptr.next) {
			if(ptr.key == value) {
				return true;
			}
		}
		return false;
	}
	
	//O(N)
	public boolean hasLoop() {
		return getLoopLength() > 0; 
	}
	
	public int getLoopLength() {
		Node slow = head;
		Node fast = head;
		while(true) {
			if(fast == null || fast.next == null) {
				return 0;
			}
			slow = slow.next;
			fast = fast.next.next;
			if(slow == fast) {
				break;
			}
		}
		int len = 1;
		Node ptr = slow;
		while(ptr.next != slow) {
			ptr = ptr.next;++len;
		}
		return len;
	}
	
	public int getLoopStartIndex() {
		return length - getLoopLength();
	}
	
	public void joinTailAt(int index) {
		Node tail = getNodeAt(length - 1);
		tail.next = getNodeAt(index);
	}
	
	Node reverse(Node node) {
		if(node == null) {
			return null;
		}
		Node nextNode = reverse(node.next);
		if(nextNode != null) {
			nextNode.next = node;
		} else {
			head = node;
		}
		node.next = null;
		return node;
	}
	
	public LinkedList reverse() {
		reverse(head);
		return this;
	}
	
	public LinkedList subList(int fromIndex, int toIndex) {
		LinkedList L = new LinkedList();
		int index = 0;
		for(Node ptr = head;ptr != null;ptr=ptr.next,++index) {
			if(index >= fromIndex && index <= toIndex) {
				L.pushBack(ptr.key);
			}
		}
		return L;
	}
	
	public boolean equals(LinkedList L) {
		if(this.size() != L.size()) {
			return false;
		}
		for(Node ptr=head, ptr1=L.getNodeAt(0);ptr != null;ptr=ptr.next,ptr1=ptr1.next) {
			if(ptr.key != ptr1.key) {
				return false;
			}
		}
		return true;
	}
	
	public boolean isPalindrome() {
		LinkedList firstHalf = this.subList(0, size()/2 - 1);
		LinkedList secondHalf = this.subList((this.size()+1)/2, length - 1).reverse();
		return firstHalf.equals(secondHalf);
	}
	
	public void clear() {
		head = null;length = 0;
	}
	
	public void swap(int index1, int index2) {
		if(index1 > index2) {
			int temp = index1;index1 = index2;index2 = temp;
		}
		//if()
	}
	
}
