package ds.algo.java.lib.datastrucutres.linkedlists;

public class DoublyLinkedList<T> {

	public class Node{
		public T data;
		public Node prev;
		public Node next;
		public Node(T data){
			this.data = data;
			this.prev = this.next = null;
		}
		public Node(T data, Node prev, Node next) {
			this.data = data;
			this.prev = prev;
			this.next = next;
		}
	}
	
	public Node head;
	int length;
	
	public DoublyLinkedList() {
		head = null;
		length = 0;
	}

	public void clear() {
		head = null;
		length = 0;
	}
	
	public Node insert(int position, T data) {
		if(length == 0) {
			head = new Node(data);
			++length;
			return head;
		} else {
			Node ptr = getNodeAt(position);
			if(ptr == null) {
				return null;
			}
			Node ptrNext = ptr.next;
			Node node = new Node(data, ptr, ptrNext);
			ptr.next = node;
			if(ptrNext != null) {
				ptrNext.prev = node;			
			}
			++length;
			return node;
		}
	}
	
	public Node addLast(T data) {
		if(size() == 0) {
			head = new Node(data);
			++length;
			return head;
		}
		Node tail = getNodeAt(length - 1);
		Node node = new Node(data, tail, null);
		tail.next = node;
		tail = node;
		++length;
		return node;
	}
	
	public void addAll(DoublyLinkedList<T> L) {
		if(size() == 0) {
			this.head = L.getNodeAt(0);
			this.length = L.size();
			return;
		}
		Node tail = getNodeAt(size() - 1);
		if(L.size() > 0) {
			tail.next = L.getNodeAt(0);L.getNodeAt(0).prev = tail;
		}
		this.length += L.size();
	}
	
	public Node addFirst(T data) {
		++length;
		Node node = new Node(data, null, head);
		if(head == null) {
			head = node;
			return head;
		}
		head.prev = node;
		head = node;
		return node;
	}

	public Node removeLast() {
		Node tail = getNodeAt(length - 1);
		--length;
		if(head == tail) {
			head = null;
			return tail;
		}
		Node oldTail = tail;
		tail = tail.prev;
		tail.next = null;
		return oldTail;
	}
	
	public void removeFirst() {
		--length;
		if(head.next == null) {
			head = null;return;
		}
		head.next.prev = null;
		head = head.next;
	}
	
	public void removeAt(int index) {
		if(index == 0) {
			removeFirst();return;
		}
		if(index == length - 1) {
			removeLast();return;
		}
		Node node = getNodeAt(index);
		if(node == null) {
			return;
		}
		node.prev.next = node.next;
		if(node.next != null) {
			node.next.prev = node.prev;
		}
		--length;
	}
	
	public void remove(Node node) {
		--length;
		if(head == null) {
			return;
		}
		if(node == head) {
			head = node.next;
			return;
		}
		node.prev.next = node.next;
	}
	
	public T get(int index) {
		return getNodeAt(index).data;
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
	
	Node reverse(Node node) {
		if(node == null) {
			return null;
		}
		Node nextNode = reverse(node.next);
		if(nextNode == null) {
			head = node;
			return node;
		}
		nextNode.next = node;
		node.prev = nextNode;
		node.next = null;
		return node;
	}
	
	public DoublyLinkedList<T> reverse() {
		reverse(head);
		return this;
	}
	
	public void print() {
		for(Node ptr = head;ptr != null;ptr=ptr.next) {
			System.out.print(ptr.data+"<->");
		}
		System.out.println("");
	}
	
	public int size() {
		return length;
	}

	// merge 2 sorted doubly linked lists in O(1) space.
	public DoublyLinkedList merge(DoublyLinkedList l) {
		Node head1 = head;
		Node head2 = l.head;
		if(head1 == null || head2 == null) {
			return head1 == null ? l : this;
		}
		if((int)head1.data > (int)head2.data) {
			head = head2;
			head2=head2.next;
		} else {
			head1 = head1.next;
		}
		Node prev = head;
		while(head1 != null || head2 != null) {
			if(head1 == null) {
				prev.next = head2;head2.prev = prev;
				prev = head2;
				head2 = head2.next;
				continue;
			}
			if(head2 == null) {
				prev.next = head1;head1.prev = prev;
				prev = head1;
				head1 = head1.next;
				continue;
			}
			if((int)head1.data < (int)head2.data) {
				if(prev.next == head1) {
					prev = head1;
					head1 = head1.next;
				} else {
					prev.next = head1;head1.prev=prev;
					head1 = head1.next;
					prev.next.next = head2;head2.prev=prev.next;
					prev = prev.next;
				}
			} else {
				if(prev.next == head2) {
					prev = head2;
					head2 = head2.next;
				} else {
					prev.next = head2;head2.prev = prev;
					head2 = head2.next;
					prev.next.next = head1;head1.prev=prev.next;
					prev = prev.next;
				}
			}
		}
		return this;
	}
	
}
