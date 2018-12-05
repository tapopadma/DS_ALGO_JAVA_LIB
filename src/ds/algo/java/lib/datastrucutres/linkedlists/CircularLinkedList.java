package ds.algo.java.lib.datastrucutres.linkedlists;

public class CircularLinkedList extends UniDirectionalLinkedList{

	public CircularLinkedList() {
		super();
	}
	
	public CircularLinkedList(Node head, int length) {
		super(head, length);
	}
	
	@Override
	public void print() {
		if(size() == 0) {
			return;
		}
		for(Node ptr = head;;ptr=ptr.next) {
			System.out.print(ptr.data+"->");
			if(ptr.next == head) {
				break;
			}
		}
		System.out.println("");
		
	}

	@Override
	public Node getNodeAt(int index) {
		index %= length;
		for(Node ptr = head;;ptr=ptr.next) {
			if(index == 0) {
				return ptr;
			}
			--index;
			if(ptr.next == head) {
				break;
			}
		}
		return null;
	}

	@Override
	public void insertAt(int index, int value) {
		if(length == 0) {
			head = new Node();
			head.data = value;
			head.next = head;
			++length;
			return;
		}
		index %= length;
		Node node = getNodeAt(index);
		node.next = new Node(value, node.next);
		++length;
	}

	@Override
	public void deleteAt(int index) {
		index = (index - 1 + length) %length;
		Node node = getNodeAt(index);
		node.next = node.next.next;
		--length;
	}

	@Override
	public boolean contains(int value) {
		if(length == 0) {
			return false;
		}
		for(Node ptr = head;;ptr=ptr.next) {
			if(ptr.data == value) {
				return true;
			}
			if(ptr.next == head) {
				break;
			}
			ptr = ptr.next;
		}
		return false;
	}	
	
	public CircularLinkedList [] splitToHalves() {
		if(size()%2 != 0 || head == null) {
			return null;
		}
		Node slow = head;
		Node fast = head;
		while(true) {
			slow = slow.next;
			fast = fast.next.next;
			if(fast == head) {
				break;
			}
		}
		Node head1 = head;
		Node tail1 = head;
		while(tail1.next != slow) {
			tail1 = tail1.next;
		}
		Node head2 = slow;
		Node tail2 = slow;
		while(slow.next != head) {
			slow = slow.next;
		}
		tail1.next = head1;
		tail2.next = head2;
		return new CircularLinkedList [] {
				new CircularLinkedList(head1, size()/2),
				new CircularLinkedList(head2, length/2)
		};
	}
	
}
