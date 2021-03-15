/**
 *
 * AVLTree
 *
 * An implementation of a AVL Tree with distinct integer keys and info
 *rony kositsky 205817893
 *gartenberg 311126205
 */

public class AVLTree {

	private AVLNode root = null;
	private int treeSize = 0;

	/**
	 * public boolean empty()
	 *
	 * returns true if and only if the tree is empty
	 *
	 */
	public boolean empty() { // O(1)
		return root == null;
	}

	/**
	 * public String search(int k)
	 *
	 * returns the info of an item with key k if it exists in the tree otherwise,
	 * returns null
	 */
	public String search(int k) { // O(logn)
		if (root != null) {
			AVLNode node = root;
			while (node.rank != -1) {
				if (k > node.key) {
					node = (AVLNode) node.getRight();
				} else if (k < node.key) {
					node = (AVLNode) node.getLeft();
				} else {
					return node.info;
				}
			}
		}
		return null;
	}

	/**
	 * public int insert(int k, String i)
	 *
	 * inserts an item with key k and info i to the AVL tree. the tree must remain
	 * valid (keep its invariants). returns the number of rebalancing operations, or
	 * 0 if no rebalancing operations were necessary. returns -1 if an item with key
	 * k already exists in the tree.
	 */
	public int insert(int k, String i) { // O(logn) - because of insertRec
		int count = 0;
		// Insert Root.
		if (root == null) {
			root = new AVLNode(k, i);
			root.setParent(null);
		} else {
			AVLNode mNode = insertRec(root, k, i);
			if (mNode == null) {
				return -1;
			} else {
				count = fixTreeInsertion(mNode);
				if (count == 0) {
					// Check if parent had a leaf, if he didnt so we need to add rank down the
					// branch
					if (((AVLNode) mNode.getLeft()).rank == -1 || ((AVLNode) mNode.getRight()).rank == -1) {
						updateRanks(mNode);
					}
				}
			}
		}
		treeSize++;
		return count;
	}

	private AVLNode insertRec(AVLNode node, int k, String i) { // return the node inserted, if already exists in tree
																// return -1. O(logn)

		if (((AVLNode) node.getLeft()).rank == -1 && k < node.key) {
			node.subTreeSize++;
			node.setLeft(new AVLNode(k, i));
			node.getLeft().setParent(node);
			return node;
		} else if (((AVLNode) node.getRight()).rank == -1 && k > node.key) {
			node.subTreeSize++;
			node.setRight(new AVLNode(k, i));
			node.getRight().setParent(node);
			return node;
		} else {
			if (k < node.key) {
				node.subTreeSize++;
				return insertRec((AVLNode) node.getLeft(), k, i);
			} else if (k > node.key) {
				node.subTreeSize++;
				return insertRec((AVLNode) node.getRight(), k, i);
			}
		}

		return null;
	}

	private void updateRanks(AVLNode newNode) {//update ranks when leaf is no longer leaf. O(logn)
		AVLNode parentNode = newNode;
		while (parentNode != null) {
			parentNode.rank++;
			parentNode = (AVLNode) parentNode.getParent();
		}
	}

	private int fixTreeInsertion(AVLNode node) { // rebalance the tree after node inserted. O(logn)
		int count = 0;
		while ((AVLNode) node != null) {
			NodeStatus nodeStatus = new NodeStatus(node);
			if (nodeStatus.insertCaseOne()) {
				// promote
				node.rank += 1;
				count++;
				node = (AVLNode) node.getParent();
			} else if (nodeStatus.insertCaseTwo()) {
				// rotate
				nodeStatus.insertSingleRotate();
				count += 2;
				node = (AVLNode) node.getParent().getParent();
			} else if (nodeStatus.insertCaseThree()) {
				// double rotate
				nodeStatus.insertDoubleRotate();
				count += 5;
				break;
			} else {
				break;
			}
		}

		return count;
	}

	// Node status helper.
	// Helping for insertion and deletion fixes.
	private class NodeStatus {
		AVLNode node;
		int current;
		int right;
		int left;

		NodeStatus(AVLNode mNode) {
			current = mNode.rank;
			left = ((AVLNode) mNode.getLeft()).rank;
			right = ((AVLNode) mNode.getRight()).rank;
			node = mNode;
		}

		private boolean insertCaseOne() { // O(const)
			if (current - left == 0 && current - right == 1) {
				return true;
			}
			if (current - left == 1 && current - right == 0) {
				return true;

			}
			return false;
		}

		private boolean insertCaseTwo() { // O(const)
			if (current - left == 0 && current - right == 2) {
				int leftLeft = ((AVLNode) ((AVLNode) node.getLeft().getLeft())).rank;
				if (left - leftLeft == 1) {
					return true;
				}
			}
			if (current - right == 0 && current - left == 2) {
				int rightRight = ((AVLNode) ((AVLNode) node.getRight().getRight())).rank;
				if (right - rightRight == 1) {
					return true;
				}
			}

			return false;
		}

		private boolean insertCaseThree() { // O(const)
			if (current - left == 0 && current - right == 2) {
				int leftLeft = ((AVLNode) ((AVLNode) node.getLeft().getLeft())).rank;
				if (left - leftLeft == 2) {
					return true;
				}
			}
			if (current - right == 0 && current - left == 2) {
				int rightRight = ((AVLNode) ((AVLNode) node.getRight().getRight())).rank;
				if (right - rightRight == 2) {
					return true;
				}
			}

			return false;
		}

		private boolean deleteCaseOne() { // O(const)
			if (current - left == 2 && current - right == 2) {
				return true;
			}
			return false;
		}

		private boolean deleteCaseTwo() { // O(const)
			if (current - right == 3) {
				if ((left - ((AVLNode) node.getLeft().getRight()).rank == 1
						&& (left - ((AVLNode) node.getLeft().getLeft()).rank == 1))) {
					return true;
				}
			}
			if (current - left == 3) {
				if ((right - ((AVLNode) node.getRight().getRight()).rank == 1
						&& ((right - ((AVLNode) node.getRight().getLeft()).rank) == 1))) {
					return true;
				}
			}
			return false;
		}

		private boolean deleteCaseThree() { // O(const)
			if (current - right == 3) {
				if (left - ((AVLNode) node.getLeft().getRight()).rank == 2) {
					return true;
				}
			}
			if (current - left == 3) {
				if (right - ((AVLNode) node.getRight().getLeft()).rank == 2) {
					return true;
				}
			}
			return false;
		}

		private boolean deleteCaseFour() { // O(const)
			if (current - right == 3) {
				if (left - ((AVLNode) node.getLeft().getLeft()).rank == 2) {
					return true;
				}
			}
			if (current - left == 3) {
				if (right - ((AVLNode) node.getRight().getRight()).rank == 2) {
					return true;
				}
			}
			return false;
		}

		// For insertion case 2.
		private void insertSingleRotate() { // O(const)
			if (current - left == 0) {
				singleRotateRight(this.node);
			} else {
				singleRotateLeft(this.node);
			}
		}

		// For deletion case 2.
		private void deleteSingleRotationCaseTwo() { // O(const)
			if (current - left == 3) {
				singleRotateLeft(this.node);
			} else {
				singleRotateRight(this.node);
			}
			((AVLNode) this.node.getParent()).rank++;
		}

		// For deletion case 3.
		private void deleteSingleRotationCaseThree() { // O(const)
			if (current - left == 3) {
				singleRotateLeft(this.node);
			} else {
				singleRotateRight(this.node);
			}

			this.node.rank--;
		}

		// For deletion case 4.
		private void deleteDoubleRotate() { // O(const)
			if (current - left == 3) {
				singleRotateRight((AVLNode) node.getRight());
				((AVLNode) node.getRight()).rank++;
				singleRotateLeft(this.node);
			} else {
				singleRotateLeft((AVLNode) node.getLeft());
				((AVLNode) node.getLeft()).rank++;
				singleRotateRight(this.node);
			}

			this.node.rank--;
		}

		// For insertion case 3.
		private void insertDoubleRotate() { // O(const)
			if (current - left == 0) {
				singleRotateLeft((AVLNode) node.getLeft());
				singleRotateRight(this.node);
			} else {
				singleRotateRight((AVLNode) node.getRight());
				singleRotateLeft(this.node);
			}
			((AVLNode) this.node.getParent()).rank++;

		}

		private void singleRotateRight(AVLNode node) { // O(const)
			node.getLeft().setParent(node.getParent());
			node.setParent(node.getLeft());
			AVLNode temp = (AVLNode) node.getLeft().getRight();
			node.getLeft().setRight(node);
			node.setLeft(temp);
			temp.setParent(node);
			node.rank -= 1;
			// Adjusting tree sizes
			int tempSize = node.subTreeSize;
			((AVLNode) node.getParent()).subTreeSize = tempSize;
			node.subTreeSize = 1 + ((AVLNode) node.getLeft()).subTreeSize + ((AVLNode) node.getRight()).subTreeSize;
			if (root == node) {
				root = (AVLNode) node.getParent();
			} else {
				// Setting parent parent new right or new left.
				if (node.getParent().getParent().getRight().getKey() == node.key) {
					node.getParent().getParent().setRight(node.getParent());
				} else {
					node.getParent().getParent().setLeft(node.getParent());
				}
			}
		}

		private void singleRotateLeft(AVLNode node) { // O(const)
			node.getRight().setParent(node.getParent());
			node.setParent(node.getRight());
			AVLNode temp = (AVLNode) node.getRight().getLeft();
			node.getRight().setLeft(node);
			node.setRight(temp);
			temp.setParent(node);
			node.rank -= 1;
			// Adjusting tree sizes
			int tempSize = node.subTreeSize;
			((AVLNode) node.getParent()).subTreeSize = tempSize;
			node.subTreeSize = 1 + ((AVLNode) node.getLeft()).subTreeSize + ((AVLNode) node.getRight()).subTreeSize;
			if (root == node) {
				root = (AVLNode) node.getParent();
			} else {
				// Setting parent parent new right or new left.
				if (node.getParent().getParent().getRight().getKey() == node.key) {
					node.getParent().getParent().setRight(node.getParent());
				} else {
					node.getParent().getParent().setLeft(node.getParent());
				}
			}
		}
	}

	/**
	 * public int delete(int k)
	 *
	 * deletes an item with key k from the binary tree, if it is there; the tree
	 * must remain valid (keep its invariants). returns the number of rebalancing
	 * operations, or 0 if no rebalancing operations were needed. returns -1 if an
	 * item with key k was not found in the tree.
	 */
	public int delete(int k) { //delete item with key k from tree O(logn)
		int count = 0;
		// Delete root.
		if (root.key == k) {
			if (treeSize == 1) {
				root = null;
			} else {
				deleteRoot();
				count = fixTreeDeletion(root);
			}
		} else {
			AVLNode mNode = deleteHelper(root, k);
			if (mNode == null) {
				return -1;
			} else {
				count = fixTreeDeletion(mNode);
			}
		}
		treeSize--;
		return count;
	}

	private void decreaseSubTreeSizes(AVLNode node) {//from leaf to root at most. O(logn)
		while (node != null) {
			node.subTreeSize--;
			node = (AVLNode) node.getParent();
		}
	}

	private int fixTreeDeletion(AVLNode node) { //O(logn)
		int count = 0;
		while ((AVLNode) node != null) {
			NodeStatus nodeStatus = new NodeStatus(node);
			if (nodeStatus.deleteCaseOne()) {
				// Demote
				node.rank--;
				count++;
				node = (AVLNode) node.getParent();
			} else if (nodeStatus.deleteCaseTwo()) {
				// rotate
				nodeStatus.deleteSingleRotationCaseTwo();
				count += 4;
				break;
			} else if (nodeStatus.deleteCaseThree()) {
				// rotate
				nodeStatus.deleteSingleRotationCaseThree();
				count += 3;
				node = (AVLNode) node.getParent().getParent();
			} else if (nodeStatus.deleteCaseFour()) {
				// double rotate
				nodeStatus.deleteDoubleRotate();
				count += 6;
				node = (AVLNode) node.getParent().getParent();
			} else {
				break;
			}
		}

		return count;
	}

	private AVLNode deleteHelper(AVLNode node, int k) { //fine node to delete.O(logn)
		while (node.rank != -1) {
			if (k > node.key) {
				node = (AVLNode) node.getRight();
			} else if (k < node.key) {
				node = (AVLNode) node.getLeft();
			} else {
				AVLNode tempNode = (AVLNode) node.getParent();
				DeleteNode(node, tempNode);
				return tempNode;
			}
		}

		return null;
	}

	// Returns pointer to the desired node.
	private AVLNode findNode(AVLNode node, int k) { //fine specific node O(logn)
		while (node.rank != -1) {
			if (k > node.key) {
				node = (AVLNode) node.getRight();
			} else if (k < node.key) {
				node = (AVLNode) node.getLeft();
			} else {
				return node;
			}
		}
		return null;
	}

	private void DeleteNode(AVLNode node, AVLNode parent) {// O(logn)
		if (root == node) {
			deleteRoot();
		} else if (node.isLeaf()) {
			if (parent.getLeft() == node) {
				parent.setLeft(new AVLNode());
			} else {
				parent.setRight(new AVLNode());
			}
			// Decreasing sub tree size property after deleting node.
			decreaseSubTreeSizes(parent);
		} else if (isUnary(node)) {
			// Decreasing sub tree size property after deleting node.
			decreaseSubTreeSizes(parent);
			bypassUnaryNode(node, parent);
		}
		// Binary node.
		else {
			AVLNode successor = findSuccessor(node);
			node.key = successor.key;
			node.info = successor.info;
			DeleteNode(successor, (AVLNode) successor.getParent());
		}
	}

	private void deleteRoot() {// O(logn)
		if (((AVLNode) root.getRight()).rank != -1) {
			AVLNode successor = findSuccessor(root);
			root.key = successor.key;
			root.info = successor.info;
			DeleteNode(successor, (AVLNode) successor.getParent());
		} else {
			this.root = (AVLNode) root.getLeft();
			root.setParent(null);
		}
	}

	private boolean isUnary(AVLNode node) {//O(1)
		if (((AVLNode) node.getLeft()).rank == -1 && ((AVLNode) node.getRight()).rank != -1) {
			return true;
		}
		if (((AVLNode) node.getLeft()).rank != -1 && ((AVLNode) node.getRight()).rank == -1) {
			return true;
		}

		return false;
	}

	private void bypassUnaryNode(AVLNode node, AVLNode parent) {//O(1)
		// Node is left son
		if (parent.getLeft() == node) {
			// If node has left child
			if (((AVLNode) node.getLeft()).rank != -1) {
				parent.setLeft(node.getLeft());
				parent.getLeft().setParent(parent);
			}
			// Else node has right child
			else {
				parent.setLeft(node.getRight());
				parent.getLeft().setParent(parent);
			}
		}
		// Node is right son
		else {
			// If node has left child
			if (((AVLNode) node.getLeft()).rank != -1) {
				parent.setRight(node.getLeft());
				parent.getRight().setParent(parent);
			}
			// Else node has right child
			else {
				parent.setRight(node.getRight());
				parent.getRight().setParent(parent);
			}
		}
	}

	private AVLNode findSuccessor(AVLNode node) {//O(logn)
		if (((AVLNode) node.getRight()).rank != -1) {
			return minimalNode((AVLNode) node.getRight());
		}
		AVLNode y = (AVLNode) node.getParent();
		while (((AVLNode) y).rank != -1 && node == y.getRight()) {
			AVLNode temp = (AVLNode) node.getParent();
			node = y;
			y = temp;
		}

		return y;
	}

	private AVLNode minimalNode(AVLNode node) {//O(logn)
		AVLNode mNode = node;
		while (((AVLNode) mNode.getLeft()).rank != -1) {
			mNode = (AVLNode) mNode.getLeft();
		}
		return mNode;
	}

	/**
	 * public String min()
	 *
	 * Returns the info of the item with the smallest key in the tree, or null if
	 * the tree is empty
	 */
	public String min() {	//O(logn)
		if (root != null) {
			AVLNode mNode = root;
			while (mNode.getLeft() != null) {
				mNode = (AVLNode) mNode.getLeft();
			}
			return mNode.info;
		}
		return null;
	}

	/**
	 * public String max()
	 *
	 * Returns the info of the item with the largest key in the tree, or null if the
	 * tree is empty
	 */
	public String max() { //O(logn)
		if (root != null) {
			AVLNode mNode = root;
			while (mNode.getRight() != null) {
				mNode = (AVLNode) mNode.getRight();
			}
			return mNode.info;
		}
		return null;
	}

	/**
	 * public int[] keysToArray()
	 *
	 * Returns a sorted array which contains all keys in the tree, or an empty array
	 * if the tree is empty.
	 */
	public int[] keysToArray() { //O(n)
		int[] arr = new int[treeSize];
		indexOfArray = 0;
		keysToArrayRec(root, arr);
		return arr;
	}

	private int indexOfArray = 0;

	private void keysToArrayRec(AVLNode node, int[] array) {//O(n)
		if (node.rank == -1) {
			return;
		}
		keysToArrayRec((AVLNode) node.getLeft(), array);
		array[indexOfArray] = node.key;
		indexOfArray++;
		keysToArrayRec((AVLNode) node.getRight(), array);
	}

	/**
	 * public String[] infoToArray()
	 *
	 * Returns an array which contains all info in the tree, sorted by their
	 * respective keys, or an empty array if the tree is empty.
	 */
	public String[] infoToArray() {//O(n)
		String[] arr = new String[treeSize];
		infoToArrayRec(root, arr, 0);

		return arr;
	}

	private void infoToArrayRec(AVLNode node, String[] array, int index) {//O(n)
		if (node.rank == -1) {
			return;
		}
		infoToArrayRec((AVLNode) node.getLeft(), array, index);
		array[index] = node.info;
		index++;
		infoToArrayRec((AVLNode) node.getRight(), array, index);
	}

	/**
	 * public int size()
	 *
	 * Returns the number of nodes in the tree.
	 *
	 * precondition: none postcondition: none
	 */
	public int size() {//O(1)
		return treeSize;
	}

	/**
	 * public int getRoot()
	 *
	 * Returns the root AVL node, or null if the tree is empty
	 *
	 * precondition: none postcondition: none
	 */
	public IAVLNode getRoot() {//O(1)
		return root;
	}

	/**
	 * public string split(int x)
	 *
	 * splits the tree into 2 trees according to the key x. Returns an array [t1,
	 * t2] with two AVL trees. keys(t1) < x < keys(t2). precondition: search(x) !=
	 * null postcondition: none
	 */
	public AVLTree[] split(int x) {// O(logn)
		if (search(x) == null) {
			this.insert(x, "");
		}

		boolean isRoot = false;
		if (root.key == x) {
			isRoot = true;
		}

		AVLNode desiredNode = findNode(root, x);
		AVLTree largerTree = new AVLTree();
		AVLTree smallerTree = new AVLTree();

		// Initializing Trees
		if (((AVLNode) desiredNode.getLeft()).rank != -1) {
			smallerTree = createSubTree((AVLNode) desiredNode.getLeft());
			smallerTree.root.setParent(null);
		}

		if (((AVLNode) desiredNode.getRight()).rank != -1) {
			largerTree = createSubTree((AVLNode) desiredNode.getRight());
			largerTree.root.setParent(null);
		}

		if (!isRoot) {
			AVLNode parent = (AVLNode) desiredNode.getParent();
			// Starting to climb up
			if (parent != root) {
				AVLNode grandpa = (AVLNode) parent.getParent();
				while (grandpa != null) {
					if (parent.key > desiredNode.key) {
						largerTree.join(new AVLNode(parent.key, parent.info),
								createSubTree((AVLNode) parent.getRight()));
					} else {
						smallerTree.join(new AVLNode(parent.key, parent.info),
								createSubTree((AVLNode) parent.getLeft()));
					}

					parent = grandpa;
					grandpa = (AVLNode) parent.getParent();
				}
			}

			// Final step with the root
			if (root.key > desiredNode.key) {
				largerTree.join(new AVLNode(root.key, root.info), createSubTree((AVLNode) root.getRight()));
			} else {
				smallerTree.join(new AVLNode(root.key, root.info), createSubTree((AVLNode) root.getLeft()));
			}
		}

		smallerTree.treeSize = smallerTree.root.subTreeSize;
		largerTree.treeSize=largerTree.root.subTreeSize;
		AVLTree[] avlTreesArray = new AVLTree[] { smallerTree, largerTree };
		return avlTreesArray;

	}

	private AVLTree createSubTree(AVLNode node) {//O(1)
		AVLTree subTree = new AVLTree();
		if (node.rank != -1) {
			subTree.root = node;
			node.setParent(null);
			subTree.treeSize = subTree.root.subTreeSize;
		}
		return subTree;
	}

	/**
	 * public join(IAVLNode x, AVLTree t)
	 *
	 * joins t and x with the tree. Returns the complexity of the operation (rank
	 * difference between the tree and t) precondition: keys(x,t) < keys() or
	 * keys(x,t) > keys() postcondition: none
	 */
	public int join(IAVLNode x, AVLTree t) { // check which tree is higher, and which tree is "heavier". then join.
		// O(logn)
		AVLNode addNode = (AVLNode) x;
		AVLNode newNode = (AVLNode) t.getRoot();
		AVLNode originalNode = (AVLNode) this.root;
		if (newNode == null) {
			if (originalNode == null) { // Both tree are empty
				this.root = addNode;
				this.root.subTreeSize = addNode.subTreeSize;
				this.treeSize = 1;
				return 1;
			}
			this.insert(addNode.key, addNode.info);
			return ((AVLNode) this.getRoot()).rank + 1;
		} else if (originalNode == null) {// only original tree is empty
			this.root = newNode;
			int nRank = newNode.rank;
			this.insert(addNode.key, addNode.info);
			return nRank + 1;
		}
		int complexity = Math.abs((this.root.rank) - (newNode.rank)) + 1;
		if (newNode.rank > originalNode.rank) { // new tree is higher than original tree
			this.root = newNode;
			if (newNode.key > originalNode.key) { // new tree keys are bigger than original tree keys
				newNode = goDownLeft(newNode, originalNode.rank);
				joinFromLeft(addNode, originalNode, newNode);
			} else {// new tree keys are smaller than original tree keys
				newNode = goDownRight(newNode, originalNode.rank);
				joinFromRight(addNode, newNode, originalNode);
			}
		} else { // original tree is higher than new tree
			if (originalNode.key > newNode.key) {// new tree keys are smaller than original tree keys
				originalNode = goDownLeft(originalNode, newNode.rank);
				joinFromLeft(addNode, newNode, originalNode);
			} else {// new tree keys are bigger than original tree keys
				originalNode = goDownRight(originalNode, newNode.rank);
				joinFromRight(addNode, originalNode, newNode);
			}
		}
		fixTreeInsertion((AVLNode) addNode.getParent());
		treeSize = this.root.subTreeSize;
		return complexity;
	}

	private void increaseSubTreeSize(AVLNode node, int num) {//go up tree and update size after join. O(logn)
		while (node != null) {
			node.subTreeSize += num;
			node = (AVLNode) node.getParent();
		}
	}

	private void joinFromLeft(AVLNode addNode, AVLNode smallTree, AVLNode bigTree) { // join smallTree from left to
		// bigTree with addNode -
		// O(const)
		if (smallTree == null) {
			smallTree = new AVLNode();
		}
		if (bigTree == null) {
			bigTree = new AVLNode();
		}
		if (bigTree.getParent() == null) {
			addNode.setLeft(smallTree);
			addNode.setRight(bigTree);
			smallTree.setParent(addNode);
			bigTree.setParent(addNode);
			this.root = addNode;
			addNode.rank = bigTree.rank + 1;
			root.subTreeSize = 1 + smallTree.subTreeSize + bigTree.subTreeSize;
		} else {
			addNode.setParent(bigTree.getParent());
			bigTree.getParent().setLeft(addNode);
			addNode.setLeft(smallTree);
			smallTree.setParent(addNode);
			addNode.setRight(bigTree);
			bigTree.setParent(addNode);
			addNode.rank = smallTree.rank + 1;
			increaseSubTreeSize(bigTree, smallTree.subTreeSize + 1);
			addNode.subTreeSize = 1 + smallTree.subTreeSize + ((AVLNode) addNode.getRight()).subTreeSize;
		}
		this.treeSize = root.subTreeSize;
	}

	private void joinFromRight(AVLNode addNode, AVLNode bigTree, AVLNode smallTree) {// join smallTree from right to
		// bigTree with addNode -
		// O(const)
		if (smallTree == null) {
			smallTree = new AVLNode();
		}
		if (bigTree == null) {
			bigTree = new AVLNode();
		}
		if (bigTree.getParent() == null) {
			addNode.setLeft(bigTree);
			addNode.setRight(smallTree);
			smallTree.setParent(addNode);
			bigTree.setParent(addNode);
			this.root = addNode;
			addNode.rank = bigTree.rank + 1;
			root.subTreeSize = 1 + smallTree.subTreeSize + bigTree.subTreeSize;
		} else {
			addNode.setParent(bigTree.getParent());
			bigTree.getParent().setRight(addNode);
			addNode.setRight(smallTree);
			smallTree.setParent(addNode);
			addNode.setLeft(bigTree);
			bigTree.setParent(addNode);
			addNode.rank = smallTree.rank + 1;
			increaseSubTreeSize(bigTree, smallTree.subTreeSize + 1);
			addNode.subTreeSize = 1 + smallTree.subTreeSize + ((AVLNode) addNode.getLeft()).subTreeSize;
		}
		this.treeSize = root.subTreeSize;
	}

	private AVLNode goDownLeft(AVLNode subTree, int wantedRank) { // return the leftest node in subTree with rank <=
		// wantedRank - O(logn)
		if (subTree.getLeft() == null) {
			return subTree;
		}
		while (((AVLNode) subTree.getLeft()).rank != -1 && ((AVLNode) subTree.getLeft()).rank >= wantedRank) {
			// go down keep left, to the same height as addition tree
			subTree = (AVLNode) subTree.getLeft();
		}
		return subTree;
	}

	private AVLNode goDownRight(AVLNode subTree, int wantedRank) { // return the rightest node in subTree with rank <=
		// wantedRank - O(logn)
		if (subTree.getRight() == null) {
			return subTree;
		}
		while (((AVLNode) subTree.getRight()).rank != -1 && ((AVLNode) subTree.getRight()).rank >= wantedRank) {
			// go down keep left, to the same height as addition tree
			subTree = (AVLNode) subTree.getRight();
		}
		return subTree;
	}

	/**
	 * public interface IAVLNode ! Do not delete or modify this - otherwise all
	 * tests will fail !
	 */
	public interface IAVLNode {
		public int getKey(); // returns node's key (for virtuval node return -1)

		public String getValue(); // returns node's value [info] (for virtuval node return null)

		public void setLeft(IAVLNode node); // sets left child

		public IAVLNode getLeft(); // returns left child (if there is no left child return null)

		public void setRight(IAVLNode node); // sets right child

		public IAVLNode getRight(); // returns right child (if there is no right child return null)

		public void setParent(IAVLNode node); // sets parent

		public IAVLNode getParent(); // returns the parent (if there is no parent return null)

		public boolean isRealNode(); // Returns True if this is a non-virtual AVL node

		public void setHeight(int height); // sets the height of the node

		public int getHeight(); // Returns the height of the node (-1 for virtual nodes)
	}

	/**
	 * public class AVLNode
	 *
	 * If you wish to implement classes other than AVLTree (for example AVLNode), do
	 * it in this file, not in another file. This class can and must be modified.
	 * (It must implement IAVLNode)
	 */
	public class AVLNode implements IAVLNode {

		private AVLNode leftNode;
		private AVLNode rightNode;
		private AVLNode parentNode = null;
		public int rank = -1;
		public int key;
		public String info;
		public int subTreeSize = 0;

		// Initialize Node.
		public AVLNode(int keyParam, String infoParam) {
			key = keyParam;
			info = infoParam;
			rank = 0;
			subTreeSize = 1;
			leftNode = new AVLNode();
			rightNode = new AVLNode();
			leftNode.setParent(this);
			rightNode.setParent(this);
		}

		public AVLNode() {

		}

		public int getKey() {
			return key;
		}

		public String getValue() {
			return info;
		}

		public void setLeft(IAVLNode node) {
			leftNode = (AVLNode) node;
			node.setParent(this);
		}

		public IAVLNode getLeft() {
			return leftNode;
		}

		public void setRight(IAVLNode node) {
			rightNode = (AVLNode) node;
			node.setParent(this);

		}

		public IAVLNode getRight() {
			return rightNode;
		}

		public void setParent(IAVLNode node) {
			parentNode = (AVLNode) node;
		}

		public IAVLNode getParent() {
			return parentNode;
		}

		// Returns True if this is a non-virtual AVL node
		public boolean isRealNode() {
			return rank == -1;
		}

		public void setHeight(int height) {
			rank = height;
		}

		public int getHeight() {
			return rank;
		}

		public boolean isLeaf() {
			if (leftNode.rank == -1 && rightNode.rank == -1) {
				return true;
			}
			return false;
		}
	}

}
