package ds.algo.java.lib.datastrucutres.linkedlists;

public class CircularLinkedList extends UniDirectionalLinkedList{

	public CircularLinkedList() {
		super();
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
	
}
