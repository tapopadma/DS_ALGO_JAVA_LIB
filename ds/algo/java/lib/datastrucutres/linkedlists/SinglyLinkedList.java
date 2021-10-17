package ds.algo.java.lib.datastrucutres.linkedlists;

public class SinglyLinkedList extends UniDirectionalLinkedList{

	public SinglyLinkedList() {
		super();
	}

	public void print()  {
		if(hasLoop()) {
			System.out.println("Loop detected");
			return;
		}
		for(Node ptr = head;ptr != null;ptr=ptr.next) {
			System.out.print(ptr.data+"->");
		}
		System.out.println("");
	}

	public void pushBack(int value)  {
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

	public void pushFront(int value) {
		Node oldHead = head;
		head = new Node(value, oldHead);
		++length;
	}

	@Override
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

	@Override
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

	@Override
	public void deleteAt(int index)  {
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

	@Override
	public boolean contains(int value)  {
		for(Node ptr=head;ptr!=null;ptr=ptr.next) {
			if(ptr.data == value) {
				return true;
			}
		}
		return false;
	}
	
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
	
	public SinglyLinkedList addAll(SinglyLinkedList L) {
		if(L.size() == 0) {
			return this;
		}
		Node ptr = L.getNodeAt(0);
		while(ptr != null) {
			this.pushBack(ptr.data);
			ptr = ptr.next;
		}
		return this;
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
	
	public SinglyLinkedList reverse() {
		reverse(head);
		return this;
	}
	
	public SinglyLinkedList subList(int fromIndex, int toIndex) {
		SinglyLinkedList L = new SinglyLinkedList();
		int index = 0;
		for(Node ptr = head;ptr != null;ptr=ptr.next,++index) {
			if(index >= fromIndex && index <= toIndex) {
				L.pushBack(ptr.data);
			}
		}
		return L;
	}
	
	public boolean equals(SinglyLinkedList L) {
		if(this.size() != L.size()) {
			return false;
		}
		for(Node ptr=head, ptr1=L.getNodeAt(0);ptr != null;
				ptr=ptr.next,ptr1=ptr1.next) {
			if(ptr.data != ptr1.data) {
				return false;
			}
		}
		return true;
	}
	
	public boolean isPalindrome() {
		SinglyLinkedList firstHalf = this.subList(0, size()/2 - 1);
		SinglyLinkedList secondHalf = this.subList(
				(this.size()+1)/2, length - 1).reverse();
		return firstHalf.equals(secondHalf);
	}

	Node[] swap(Node node1, Node node2) {
		if(node1 == node2) {
			return new Node[] { node2, node1 };
		}
		Node prev1 = getPreviousNode(node1);
		Node prev2 = getPreviousNode(node2);
		Node cur1 = head;
		if(prev1 != null) {
			cur1 = prev1.next;
		}
		Node cur2 = prev2.next;
		Node next1 = cur1.next;
		Node next2 = cur2.next;
		if(prev1 != null) {
			prev1.next = cur2;
		} else {
			head = cur2;
		}
		if(cur2 != next1) {
			cur2.next = next1;
		} else {
			cur2.next = cur1;
		}
		if(prev2 != cur1) {
			prev2.next = cur1;
		} else {
			prev2.next = next1;
		}
		cur1.next = next2;
		return new Node[] { node2, node1 };
	}
	
	public void swap(int index1, int index2) {
		if(index1 == index2) {
			return;
		}
		if(index1 > index2) {
			int temp = index1;index1 = index2;index2 = temp;
		}
		if(index1 >= length && index2 >= length) {
			return;
		}
		Node prev1 = null;
		if(index1 > 0) {
			prev1 = getNodeAt(index1 - 1);
		}
		Node prev2 = getNodeAt(index2 - 1);
		Node cur1 = head;
		if(prev1 != null) {
			cur1 = prev1.next;
		}
		Node cur2 = prev2.next;
		Node next1 = cur1.next;
		Node next2 = cur2.next;
		if(prev1 != null) {
			prev1.next = cur2;
		} else {
			head = cur2;
		}
		if(cur2 != next1) {
			cur2.next = next1;
		} else {
			cur2.next = cur1;
		}
		if(prev2 != cur1) {
			prev2.next = cur1;	
		} else {
			prev2.next = next1;
		}
		cur1.next = next2;
	}
	
	Node getPreviousNode(Node node) {
		for(Node ptr=head;ptr!=null;ptr=ptr.next) {
			if(ptr.next == node) {
				return ptr;
			}
		}
		return null;
	}
	
	Node[] partition(Node start, Node end) {
		Node pivot = end;
		Node i = null;
		for(Node j=start;j!=end;j=j.next) {
			if(j.data <= pivot.data) {
				if(i == null) {
					i = start;
					start = j;
				} else {
					i = i.next;
				}
				Node[] ij = swap(i, j);
				i = ij[0];j = ij[1];
			}
		}
		Node ptr = start;
		if(i != null) {
			ptr = i.next;
		}
		Node[] pe = swap(ptr, end);
		pivot = pe[0];end = pe[1];
		if(i == null) {
			start = pe[0];
		}
		return new Node[] {pivot, start, end};
	}
	
	void quickSort(Node start, Node end) {
		Node[] result = partition(start, end);
		Node pivot = result[0];
		start = result[1];
		end = result[2];
		if(pivot != start) {
			quickSort(start, getPreviousNode(pivot));
		}
		if(pivot != end) {
			quickSort(pivot.next, end);
		}
	}
	
	//Quick sort
	public SinglyLinkedList sort() {
		if(length == 0) {
			return this;
		}
		quickSort(head, getNodeAt(length - 1));
		return this;
	}
	
	public boolean isSorted() {
		int prevVal = -1000000000;
		for(Node ptr = head;ptr != null;ptr=ptr.next) {
			if(ptr.data < prevVal) {
				return false;
			}
			prevVal = ptr.data;
		}
		return true;
	}
	
	public SinglyLinkedList reverse(int fromIndex, int toIndex) {
		int len = this.length;
		return this.subList(0, fromIndex - 1).addAll(
				this.subList(fromIndex, toIndex).reverse())
				.addAll(this.subList(toIndex + 1, len - 1));
	}
	
	SinglyLinkedList mergeSort(SinglyLinkedList L) {
		if(L.size() < 2) {
			return L;
		}
		return merge(mergeSort(L.subList(0, L.size()/2 - 1)),
				mergeSort(L.subList(L.size()/2, L.size()-1)));
	}
	
	SinglyLinkedList merge(SinglyLinkedList L1, SinglyLinkedList L2) {
		L1.print();L2.print();
		SinglyLinkedList L = new SinglyLinkedList();
		Node start1 = L1.getNodeAt(0), start2 = L2.getNodeAt(0);
		while(true) {
			if(start1 == null && start2 == null) {
				break;
			}
			if(start1 == null) {
				L.pushBack(start2.data);
				start2 = start2.next;
				continue;
			}
			if(start2 == null) {
				L.pushBack(start1.data);
				start1 = start1.next;
				continue;
			}
			if(start1.data < start2.data) {
				L.pushBack(start1.data);
				start1 = start1.next;
			} else {
				L.pushBack(start2.data);
				start2 = start2.next;
			}
		}
		return L;
	}
	
	public SinglyLinkedList mergeSort() {
		if(size() == 0) {
			return this;
		}
		return mergeSort(this);
	}
	
}
