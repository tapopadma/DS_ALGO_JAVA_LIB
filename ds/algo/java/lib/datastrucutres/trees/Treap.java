package design;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


class Node {
	static final Random random = new Random();
	int value;
	int frequency;
	int size;
	long priority;
	Node left;
	Node right;
	Node(int v) {
		value=v;frequency=1;size=1;priority=random.nextLong();
		left=right=null;
	}
}

/* Treap=Tree+Heap=Best replacement of AVL tree and red-black tree.
 * 
 * It works just like normal BST, but there's an additional `priority` assigned randomly to each node based on which
 * priority queue operations are performed in the BST upon every insertion/removal. So highest priority is the top most root node.
 * 
 * Rationale: Each node having random 64bit priority is mathematically equivalent to inserting n nodes into BST in random order which
 * is O(logn). Hence each treap operation is guaranteed to be O(logn).
 * 
 * Best usecase: Normal sorted sets like TreeSet don't allow finding kth element in O(logn) because they don't track subtree size. Treap
 * is the best resort for such scenarios. Binary search+Segment Tree is definitely worse here which is O(logn*logn).
 * */
class Treap {
	Node root;
	
	Node rotateLeft(Node cur) {
		Node right = cur.right;
		cur.right=right.left;
		right.left=cur;
		refresh(cur);
		refresh(right);
		return right;
	}

	Node rotateRight(Node cur) {
		Node left = cur.left;
		cur.left=left.right;
		left.right=cur;
		refresh(cur);
		refresh(left);
		return left;
	}
	
	int getnonNullSize(Node cur) {
		return (cur==null?0:cur.size);
	}
	
	void refresh(Node cur) {
		cur.size=getnonNullSize(cur.left)+getnonNullSize(cur.right)+cur.frequency;
	}
	
	Node add(Node cur, int val) {
		if(cur==null) {
			return new Node(val);
		}
		if(cur.value==val) {
			++cur.frequency;
		} else if(cur.value<val) {
			cur.right=add(cur.right,val);
			if(cur.right.priority>cur.priority) {
				cur=rotateLeft(cur);
			}
		} else {
			cur.left=add(cur.left,val);
			if(cur.left.priority>cur.priority) {
				cur=rotateRight(cur);
			}
		}
		refresh(cur);
		return cur;
	}
	
	public void add(int val) {
		root=add(root,val);
	}
	
	Node remove(Node cur, int val) {
		if(cur==null)return cur;
		if(cur.value==val) {
			if(cur.frequency>1) {
				--cur.frequency;
			} else {
				if(cur.left==null)return cur.right;
				if(cur.right==null)return cur.left;
				if(cur.left.priority>cur.right.priority) {
					cur=rotateRight(cur);
					cur.right=remove(cur.right,val);
				} else {
					cur=rotateLeft(cur);
					cur.left=remove(cur.left,val);
				}
			}
		} else if(cur.value < val) {
			cur.right=remove(cur.right,val);
		} else {
			cur.left=remove(cur.left,val);
		}
		refresh(cur);
		return cur;
	}
	
	public void remove(int val) {
		root=remove(root,val);
	}
	
	int kthLargest(Node cur, int k) {
		if(cur==null) return -1;
		int rightSize = getnonNullSize(cur.right);
		if(rightSize>=k)return kthLargest(cur.right,k);
		if(rightSize+cur.frequency>=k)return cur.value;
		return kthLargest(cur.left,k-rightSize-cur.frequency);
	}
	
	public int kthLargest(int k) {
		return kthLargest(root,k);
	}
}



package design;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.*;
import org.junit.jupiter.api.*;

class DesignTest {
	

	@Test
	public void testDefault() throws Exception {
		Treap treap = new Treap();
		treap.add(1);
		assertEquals(1, treap.kthLargest(1));
		assertEquals(-1, treap.kthLargest(2));
		treap.add(2);
		treap.add(5);
		treap.add(3);
		treap.add(2);
		treap.add(2);
		assertEquals(5, treap.kthLargest(1));
		assertEquals(3, treap.kthLargest(2));
		assertEquals(2, treap.kthLargest(3));
		assertEquals(2, treap.kthLargest(4));
		assertEquals(2, treap.kthLargest(5));
		assertEquals(1, treap.kthLargest(6));
		assertEquals(-1, treap.kthLargest(7));
		treap.remove(2);
		treap.remove(2);
		treap.remove(2);
		assertEquals(5, treap.kthLargest(1));
		assertEquals(3, treap.kthLargest(2));
		assertEquals(1, treap.kthLargest(3));
		assertEquals(-1, treap.kthLargest(4));
		treap.remove(5);
		assertEquals(3, treap.kthLargest(1));
		assertEquals(1, treap.kthLargest(2));
		assertEquals(-1, treap.kthLargest(3));
		treap.remove(3);
		assertEquals(1, treap.kthLargest(1));
		assertEquals(-1, treap.kthLargest(2));
		treap.remove(0);
		assertEquals(1, treap.kthLargest(1));
		assertEquals(-1, treap.kthLargest(2));
		treap.remove(1);
		assertEquals(-1, treap.kthLargest(1));
		treap.add(100);
		treap.add(2000);
		treap.add(300);
		assertEquals(2000, treap.kthLargest(1));
		assertEquals(300, treap.kthLargest(2));
	}
	
	@Test
	public void testLargeLoad() {
		Treap treap = new Treap();
		Random random = new Random();
		List<Integer> list = new ArrayList<Integer>();
		for(int i=0;i<1000000;++i) {
			int val = random.nextInt(10000000);
			list.add(val);
			treap.add(val);
		}
		Collections.sort(list);
		for(int i=0;i<1000000;++i) {
			assertEquals(list.get(i), treap.kthLargest(1000000-i));
		}
	}
	
}
