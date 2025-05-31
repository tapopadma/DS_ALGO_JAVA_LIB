package ds.algo.java.lib.datastrucutres.trees;

import java.util.Queue;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Collections;
import ds.algo.java.lib.datastrucutres.linkedlists.SinglyLinkedList;
import ds.algo.java.lib.datastrucutres.linkedlists.DoublyLinkedList;

public class BinarySearchTree extends BinaryTree{

	public BinarySearchTree() {
		root = null;
	}
	
	Node insert(Node cur, int value) {
		if(cur == null) {
			return new Node(null, null, value);
		} else {
			if(value == cur.data) {
				cur.data = value;
			}
			else if(value < cur.data) {
				cur.left = insert(cur.left, value);
			} else {
				cur.right = insert(cur.right, value);
			}
			return cur;
		}
	}
	
	public void put(int value) {
		root = insert(root, value);
	}
	
	Node search(Node cur, int key) {
		if(cur == null) {
			return null;
		}
		if(key == cur.data) {
			return cur;
		}
		if(key < cur.data) {
			return search(cur.left, key);
		} else {
			return search(cur.right, key);
		}
	}
	
	public int get(int key) {
		Node result = search(root, key);
		return (result == null) ? -1 : result.data;
	}
	
	Node delete(Node cur, int key) {
		if(cur == null) {
			return null;
		}
		if(cur.data == key) {
			Node prev = null;
			if(cur.left != null) {
				Node ptr = cur.left;
				while(ptr.right != null) {
					ptr = ptr.right;
				}
				prev = ptr;
			}
			if(prev == null) {
				return cur.right;
			}
			cur.data = prev.data;
			cur.left = delete(cur.left, cur.data);
		}
		if(key < cur.data) {
			cur.left = delete(cur.left, key);
		} else {
			cur.right = delete(cur.right, key);
		}
		return cur;
	}
	
	public void remove(int key) {
		root = delete(root, key);
	}
	
	public boolean containsKey(int key) {
		return search(root, key) != null;
	}

	Node convertFromLinkedList(SinglyLinkedList.Node head, SinglyLinkedList.Node tail) {
		if(head == null) {
			return null;
		}
		SinglyLinkedList.Node ptr1 = head, ptr2 = head;
		SinglyLinkedList.Node prevPtr1 = ptr1;
		while(ptr2.next != (tail==null?null:tail.next) && ptr2.next.next != (tail==null?null:tail.next)) {
			ptr2 = ptr2.next.next;
			prevPtr1 = ptr1;
			ptr1 = ptr1.next;
		}
		Node cur = new Node(ptr1.data);
		if(head == tail) {
			return cur;
		}
		cur.left = prevPtr1==ptr1 ? null: convertFromLinkedList(head, prevPtr1);
		cur.right = convertFromLinkedList(ptr1.next, tail);
		return cur;
	}

	// find middle , create root and split recursively.
	public BinarySearchTree convertFromLinkedList(SinglyLinkedList l) {
		root = convertFromLinkedList(l.getNodeAt(0), l.getNodeAt(l.size()-1));
		return this;
	}

    // Add a node from level order to a queue, poll it, grab next available elements from level order
    // and check if they can be a valid parent-child pair by checking the valid range of values of the parent,
    // add the child if valid, update the range for the new child node, push it to queue and continue. O(n),O(n).
	public BinarySearchTree convertFromLevelOrder(int[] levelOrder) {
		if(levelOrder.length == 0) {
			return this;
		}
		root = new Node(levelOrder[0]);
		Queue<Node> q = new ArrayDeque<>();
		Map<Node, List<Integer>> range = new HashMap<>();
		range.put(root, Arrays.asList(Integer.MIN_VALUE, root.data-1, root.data + 1, Integer.MAX_VALUE));
		q.add(root);
		int i = 1;
		while(!q.isEmpty()) {
			Node cur = q.poll();
			List<Integer> curRange = range.get(cur);
			if(i == levelOrder.length) {
				break;
			}
			if(curRange.get(0) <= levelOrder[i] && levelOrder[i] <= curRange.get(1)) {
				cur.left = new Node(levelOrder[i]);
				range.put(cur.left, Arrays.asList(curRange.get(0), cur.left.data-1, cur.left.data+1, cur.data - 1));
				q.add(cur.left);
				++i;
				if(i == levelOrder.length) {
					break;
				}
			}
			if(curRange.get(2) <= levelOrder[i] && levelOrder[i] <= curRange.get(3)) {
				cur.right = new Node(levelOrder[i]);
				range.put(cur.right, Arrays.asList(cur.data+1, cur.right.data - 1, cur.right.data + 1, curRange.get(3)));
				q.add(cur.right);
				++i;
			}
		}
		return this;
	}

	Node lcaNonPreprocessed(Node cur, int x, int y) {
		if(cur == null) {
			return cur;
		}
		if(cur.data > x && cur.data > y) {
			return lcaNonPreprocessed(cur.left, x, y);
		} else if(cur.data < x && cur.data < y) {
			return lcaNonPreprocessed(cur.right, x, y);
		} else {
			return cur;
		}
	}

    // start from root, if root is above both x and y's value then lca is on left sub treee, else if root is below both x and y's value 
    // then lca is on right sub treee, else return root.
    public void lcaNonPreprocessed(List<List<Integer>>xys) {
    	for(List<Integer> xy: xys) {
    		int x = xy.get(0), y = xy.get(1);
    		System.out.println("lca of " + x + " " + y + " " + lcaNonPreprocessed(root, x, y).data);
    	}
    }

    List<Integer> inOrder(Node cur) {
      List<Integer> ret = new ArrayList<>();
      if (cur == null) {
        return ret;
      }
      ret.addAll(inOrder(cur.left));
      ret.add(cur.data);
      ret.addAll(inOrder(cur.right));
      return ret;
    }

    void applyInOrder(Node cur, int[] idx, List<Integer> inorder) {
    	if(cur == null) {
    		return;
    	}
    	applyInOrder(cur.left, idx, inorder);
    	cur.data = inorder.get(idx[0]++);
    	applyInOrder(cur.right, idx, inorder);
    }

    // generate inorder array, sort it, apply it on the existing skeleton in the inorder pattern. O(nlogn),O(n).
    public BinarySearchTree fixTwoSwappedNodesAndRecoverBST() {
    	List<Integer> inorder = inOrder(root);
    	Collections.sort(inorder);
    	applyInOrder(root, new int[]{0}, inorder);
    	return this;
    }

    public void merge(BinarySearchTree t) {
    	DoublyLinkedList<Integer> l1 = convertToDoublyLinkedList();// this could have been single linkedlist too.
    	DoublyLinkedList<Integer> l2 = t.convertToDoublyLinkedList();
    	l1.merge(l2).print();
    }
	
}
