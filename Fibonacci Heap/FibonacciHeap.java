import java.util.ArrayList;
import java.util.HashMap;

/**
 * FibonacciHeap
 *
 * An implementation of fibonacci heap over integers.
 */
public class FibonacciHeap {

	private static int cutsCounter = 0;
	private static int linksCounter = 0;
	private HeapNode minimumHeap = null;
	private int numberOfTrees = 0;
	private int nodesCounter = 0;
	private int markedNodes = 0;
	private HeapNode rightEdge = null; // Pointer to the end of the list.

	public FibonacciHeap() {
		cutsCounter = 0;
		linksCounter = 0;
	}

	/**
	 * public boolean isEmpty()
	 *
	 * precondition: none
	 * 
	 * The method returns true if and only if the heap is empty.
	 * 
	 */
	public boolean isEmpty() {// O(1)
		return numberOfTrees == 0;
	}

	/**
	 * public HeapNode insert(int key)
	 *
	 * Creates a node (of type HeapNode) which contains the given key, and inserts
	 * it into the heap.
	 */
	public HeapNode insert(int key) {// O(1)
		HeapNode insertNode = new HeapNode(key);
		if (isEmpty()) {
			minimumHeap = insertNode;
			rightEdge = insertNode;
			rightEdge.setNext(rightEdge);
			rightEdge.setPrevious(rightEdge);
			numberOfTrees = 1;
		} else {
			insertHelper(insertNode);
			if (key < minimumHeap.getKey()) {
				minimumHeap = insertNode;
			}
		}
		nodesCounter++;
		return insertNode;
	}

	// Insert from left.
	private void insertHelper(HeapNode newNode) {// changing pointers in order to insert newNode to the heap. O(1)
		HeapNode oldNode = rightEdge.getNext();
		oldNode.getPrevious().setNext(newNode);
		newNode.setPrevious(oldNode.getPrevious());
		newNode.setNext(oldNode);
		oldNode.setPrevious(newNode);
		numberOfTrees++;

	}

	/**
	 * public void deleteMin()
	 *
	 * Delete the node containing the minimum key.
	 *
	 */
	public void deleteMin() { // deleting minimumHeap, inserting all it's children to the heap and
								// consolidating. O(logn) amortized
		// Insert all children to list.
		insertNodeChildren(minimumHeap);

		if (nodesCounter == 1) {
			rightEdge = null;
			minimumHeap = null;
			nodesCounter = 0;
			numberOfTrees = 0;
			markedNodes = 0;
		} else {
			// Remove min from list.
			minimumHeap.getPrevious().setNext(minimumHeap.getNext());
			minimumHeap.getNext().setPrevious(minimumHeap.getPrevious());
			if (minimumHeap == rightEdge) {
				rightEdge = minimumHeap.getPrevious();
			}
			nodesCounter--;
			numberOfTrees--;

			if (numberOfTrees != 0) {
				int numOfBuckets = (int) (Math.floor(Math.log(nodesCounter) / Math.log(2)) + 1);
				HeapNode[] arrayNodes = new HeapNode[numOfBuckets];
				// now need to cut off the minimal node from nodes list
				toBuckets(arrayNodes);
				fromBuckets(arrayNodes);
			}

		}
	}

	private HeapNode fromBuckets(HeapNode[] nodesArray) { // transforming an array of Binomial heaps into a Fibonacci
															// heap. O(logn)
		HeapNode x = null;
		for (int i = 0; i < nodesArray.length; i++) {
			if (nodesArray[i] != null) {
				if (x == null) {
					x = nodesArray[i];
					x.setNext(x);
					x.setPrevious(x);
					rightEdge = x;
					minimumHeap = x;
				} else {
					this.insertHelper(nodesArray[i]);

				}
			}
		}
		// updating num of trees and minimal node in the heap
		HeapNode minKeyNode = rightEdge;
		int numOfNodes = 1;
		HeapNode iter = rightEdge.getNext();
		while (iter != rightEdge) {
			if (iter.key < minKeyNode.key) {
				minKeyNode = iter;
			}
			numOfNodes++;
			iter = iter.getNext();
		}
		numberOfTrees = numOfNodes;
		minimumHeap = minKeyNode;

		return x;
	}

	private void toBuckets(HeapNode[] nodesArray) {// consolidating the heap into an array of Binomial heaps. O(logn)
		HeapNode x = rightEdge;
		x.getPrevious().setNext(null);
		while (x != null) {
			HeapNode y = x;
			x = x.getNext();
			while (nodesArray[y.rank] != null) {
				y = link(y, nodesArray[y.rank]);
				nodesArray[y.rank - 1] = null;
			}
			nodesArray[y.rank] = y;
			y.setNext(null);
			y.setPrevious(null);
		}

	}

	public HeapNode link(HeapNode a, HeapNode b) {// Linking two nodes with the same rank by changing pointers. O(1)
		// a is the new root
		if (a.key < b.key) {
			linkHelper(a, b);
			return a;
		}
		// b is the new root
		else {
			linkHelper(b, a);
			return b;
		}
	}

	public void linkHelper(HeapNode root, HeapNode son) {// Helping change the pointers of link. O(1)
		HeapNode rootSon = root.getChild();
		if (root.rank > 0) {
			HeapNode temp = rootSon.getNext();
			temp.getPrevious().setNext(son);
			son.setPrevious(temp.getPrevious());
			son.setNext(temp);
			temp.setPrevious(son);
		} else {
			root.setChild(son);
			son.setNext(son);
			son.setPrevious(son);
		}
		son.setParent(root);
		root.rank++;
		linksCounter++;
	}

	// Adding node's children to roots list.
	private void insertNodeChildren(HeapNode deletedNode) {// inserting all of deleteNode children to the heap. O(logn)
		HeapNode temp = deletedNode.getChild();
		if (temp != null) {
			HeapNode x = temp.getNext();
			while (x.getNext() != temp) {
				HeapNode y = x.getNext();
				insertHelper(x);
				x.setParent(null);
				x = y;
			}
			if (x != temp) {
				insertHelper(x);
				x.setParent(null);
			}
			insertHelper(temp);
			temp.setParent(null);
		}
	}

	/**
	 * public HeapNode findMin()
	 *
	 * Return the node of the heap whose key is minimal.
	 *
	 */
	public HeapNode findMin() {// O(1)
		return minimumHeap;
	}

	/**
	 * public void meld (FibonacciHeap heap2)
	 *
	 * Meld the heap with heap2
	 *
	 */
	public void meld(FibonacciHeap heap2) {// Changing pointers of rightEdge and 'leftEdge' of both heaps in O(1)
		if ((isEmpty() && heap2.isEmpty()) || (heap2.isEmpty() && !isEmpty())) {
			return;
		} else if (isEmpty() && !heap2.isEmpty()) {
			this.minimumHeap = heap2.findMin();
			this.rightEdge = heap2.getRightEdge();
		} else {
			// Find min.
			minimumHeap = (minimumHeap.getKey() < heap2.findMin().getKey()) ? minimumHeap : heap2.findMin();
			HeapNode leftEdge = heap2.getRightEdge().getNext();
			heap2.getRightEdge().setNext(rightEdge.getNext());
			getRightEdge().getNext().setPrevious(heap2.getRightEdge());
			rightEdge.setNext(leftEdge);
			leftEdge.setPrevious(rightEdge);

			rightEdge = heap2.getRightEdge();
			updateStatsAfterMeld(heap2);
		}
	}

	private void updateStatsAfterMeld(FibonacciHeap heap2) {// updating fields after meld. O(1)
		this.nodesCounter += heap2.size();
		this.numberOfTrees += heap2.getNumOfTrees();
	}

	public int getNumOfTrees() {// O(1)
		return numberOfTrees;
	}

	/**
	 * public int size()
	 *
	 * Return the number of elements in the heap
	 * 
	 */
	public int size() {// O(1)
		return nodesCounter;
	}

	/**
	 * public int[] countersRep()
	 *
	 * Return a counters array, where the value of the i-th entry is the number of
	 * trees of order i in the heap.
	 * 
	 */
	public int[] countersRep() {// counting all the trees in the heap by their ranks. O(logn)
		int numOfBuckets = (int) (Math.floor(Math.log(nodesCounter) / Math.log(2)) + 1);
		int[] arr = new int[numOfBuckets];
		HeapNode x = rightEdge;
		while (x.getNext() != rightEdge) {
			arr[x.rank]++;
			x = x.getNext();
		}
		// Add previous of the right edge rank because the loop breaks before it.
		arr[rightEdge.getPrevious().rank]++;
		return arr;
	}

	/**
	 * public void delete(HeapNode x)
	 *
	 * Deletes the node x from the heap.
	 *
	 */
	public void delete(HeapNode x) {// O1
		if (x.getKey() == minimumHeap.getKey()) {
			deleteMin();
		} else {
			decreaseKey(x, x.getKey() - minimumHeap.getKey() + 1);
			deleteMin();
		}
	}

	private void cascadingCut(HeapNode x) { // cutting node x from its parent to the heap. O(logn) WC, O(1) amortized
		HeapNode y = x.getParent();
		cut(x);
		if (y != null && y.getParent() != null) {
			if (!y.getMarkBit()) {
				y.setMarkBit(true);
				markedNodes++;
			} else {
				cascadingCut(y);
			}
		}
	}

	private void cut(HeapNode x) {// changing pointers in order to remove x from the heap. O(1)
		// Parent of the node.
		HeapNode y = x.getParent();

		// Change children pointers.
		if (y != null) {
			cutsCounter++;
			y.rank--;
			if (x.getNext() == x) {
				y.setChild(null);
			} else {
				// Need to find leftmost child.
				if (x.getParent().getChild() == x) {
					y.setChild(x.getNext());
				}
				x.getPrevious().setNext(x.getNext());
				x.getNext().setPrevious(x.getPrevious());
			}
			x.setParent(null);
			insertHelper(x);
			if (x.getMarkBit()) {
				x.setMarkBit(false);
				markedNodes--;
			}

		}

	}

	/**
	 * public void decreaseKey(HeapNode x, int delta)
	 *
	 * The function decreases the key of the node x by delta. The structure of the
	 * heap should be updated to reflect this chage (for example, the cascading cuts
	 * procedure should be applied if needed).
	 */
	public void decreaseKey(HeapNode x, int delta) {// Subtracting delta from x key. /use cascadingCut if neede. O(logn)
													// WC, O(1) amortized
		if ((x.key - delta) < minimumHeap.getKey()) {
			minimumHeap = x;
		}
		if (x.getParent() == null || (x.getParent().key < (x.key - delta))) {
			x.key -= delta;
		} else {
			x.key -= delta;
			cascadingCut(x);
		}
	}

	/**
	 * public int potential()
	 *
	 * This function returns the current potential of the heap, which is: Potential
	 * = #trees + 2*#marked The potential equals to the number of trees in the heap
	 * plus twice the number of marked nodes in the heap.
	 */
	public int potential() {// O(1)
		return numberOfTrees + 2 * markedNodes; // should be replaced by student code
	}

	/**
	 * public static int totalLinks()
	 *
	 * This static function returns the total number of link operations made during
	 * the run-time of the program. A link operation is the operation which gets as
	 * input two trees of the same rank, and generates a tree of rank bigger by one,
	 * by hanging the tree which has larger value in its root on the tree which has
	 * smaller value in its root.
	 */
	public static int totalLinks() {// O(1)
		return linksCounter; // should be replaced by student code
	}

	/**
	 * public static int totalCuts()
	 *
	 * This static function returns the total number of cut operations made during
	 * the run-time of the program. A cut operation is the operation which
	 * diconnects a subtree from its parent (during decreaseKey/delete methods).
	 */
	public static int totalCuts() {// O(1)
		return cutsCounter; // should be replaced by student code
	}

	/**
	 * public static int[] kMin(FibonacciHeap H, int k)
	 *
	 * This static function returns the k minimal elements in a binomial tree H. The
	 * function should run in O(k(logk + deg(H)).
	 */
	public static int[] kMin(FibonacciHeap H, int k) {// O(k(logk + deg(H))
		// Creating array and initialize parameters.
		int[] arr = new int[k];
		if (!H.isEmpty() && k > 0) {
			int index = 0;
			HashMap<Integer, HeapNode> heapNodeMap = new HashMap<Integer, HeapNode>();
			FibonacciHeap referenceHeap = new FibonacciHeap();

			// Inserting first element to reference heap.
			HeapNode newMinNode = H.findMin();
			referenceHeap.insert(newMinNode.getKey());
			arr[index] = newMinNode.getKey();
			index++;

			while (index < k) {
				// Get neighbors.
				HeapNode[] neighborsArray = getAllNeighbors(newMinNode);
				for (int i = 0; i < neighborsArray.length; i++) {
					if (!heapNodeMap.keySet().contains(neighborsArray[i].getKey())) {
						referenceHeap.insert(neighborsArray[i].getKey());
						heapNodeMap.put(neighborsArray[i].getKey(), neighborsArray[i]);
					}
				}

				// Get children.
				HeapNode[] childrenArray = getAllChildren(newMinNode);
				// Add them to reference heap.
				for (int i = 0; i < childrenArray.length; i++) {
					if (!heapNodeMap.keySet().contains(childrenArray[i].getKey())) {
						referenceHeap.insert(childrenArray[i].getKey());
						heapNodeMap.put(childrenArray[i].getKey(), childrenArray[i]);
					}
				}
				referenceHeap.deleteMin();
				newMinNode = heapNodeMap.get(referenceHeap.findMin().getKey());
				arr[index] = newMinNode.getKey();
				index++;
			}
		}

		return arr;
	}

	// Get node children.
	private static HeapNode[] getAllChildren(HeapNode node) {// For kMin, return and array of HeapNode children of node.
																// O(logk)
		int childrenNum = node.rank;
		if (childrenNum == 0) {
			return new HeapNode[0];
		}
		HeapNode[] arr = new HeapNode[childrenNum];
		HeapNode x = node.getChild();
		for (int i = 0; i < childrenNum; i++) {
			arr[i] = x;
			x = x.getNext();
		}

		return arr;
	}

	// Get node neighbors.
	private static HeapNode[] getAllNeighbors(HeapNode node) {// For kMin, return an array of all of node parent's
																// children excluding node. O(logk)
		HeapNode temp = node.getNext();
		ArrayList<HeapNode> arrList = new ArrayList<HeapNode>();
		while (temp.getNext() != node) {
			arrList.add(temp);
			temp = temp.getNext();
		}

		if (arrList.size() == 0) {
			return new HeapNode[0];
		}

		HeapNode[] heapsArr = new HeapNode[arrList.size()];
		int index = 0;
		for (HeapNode n : arrList) {
			heapsArr[index] = n;
			index++;
		}

		return heapsArr;
	}

	public HeapNode getRightEdge() {// O(1)
		return rightEdge;
	}

	/**
	 * public class HeapNode
	 * 
	 * If you wish to implement classes other than FibonacciHeap (for example
	 * HeapNode), do it in this file, not in another file
	 * 
	 */
	public class HeapNode {

		public int key;
		public int rank = 0;
		private HeapNode parent = null;
		private HeapNode child = null;
		private HeapNode next = this;
		private HeapNode previous = this;
		private boolean markBit = false;

		public HeapNode(int key) {// O(1)
			this.key = key;
		}

		public int getKey() {// O(1)
			return this.key;
		}

		public HeapNode getNext() {// O(1)
			return next;
		}

		public HeapNode getPrevious() {// O(1)
			return previous;
		}

		public HeapNode getChild() {// O(1)
			return child;
		}

		public HeapNode getParent() {// O(1)
			return parent;
		}

		public void setNext(HeapNode nextNode) {// O(1)
			next = nextNode;
		}

		public void setPrevious(HeapNode prevNode) {// O(1)
			previous = prevNode;
		}

		public void setChild(HeapNode childNode) {// O(1)
			child = childNode;
		}

		public void setParent(HeapNode patentNode) {// O(1)
			parent = patentNode;
		}

		public void setMarkBit(boolean mark) {// O(1)
			markBit = mark;
		}

		public boolean getMarkBit() {// O(1)
			return markBit;
		}
	}
}