/**
 *
 * AVLTree
 *
 * An implementation of aמ AVL Tree with
 * distinct integer keys and info.
 *
 */

public class AVLTree {
	private IAVLNode root;
	private IAVLNode min;
	private IAVLNode max;
	private int size;
	private IAVLNode EXT = new AVLNode(-1, null);

	public AVLTree() {
		this.root = null;
		this.min = null;
		this.max = null;
		this.size = 0;
		this.EXT.setHeight(-1);
		this.EXT.updateSize(0);
	}

	/** New constructor
	 *
	 * @param newRoot newRoot
	 *
	 * @post New tree's root's parent is now null and not the previous parent (if existed).
	 */
    public AVLTree(IAVLNode newRoot) { // Another Constructor
        this.root = newRoot;
        this.min = newRoot.getMin();
        this.max = newRoot.getMax();
        this.size = newRoot.getSize();
		root.setParent(null);
    }

  /**
   * public boolean empty()
   *
   * Returns true if and only if the tree is empty.
   *
   */
  public boolean empty() {
    return this.root == null || !this.root.isRealNode(); // Returns True if the Tree is empty from nodes. O(1).
  }

 /**
   * public String search(int k)
   *
   * Returns the info of an item with key k if it exists in the tree.
   * otherwise, returns null.
   */
  public String search(int k) { // Calls for Iterative function. O(1) (Inside function is O(logn)). O(logn) total.
	  IAVLNode resultNode = generalSearch(k);
	  if (resultNode == null) {
		  return null;
	  }
	  if (resultNode.getKey() < k) {
		  return resultNode.getRight().getValue();
	  }
	  else if (resultNode.getKey() == k) {
		  return resultNode.getValue();
	  }
	  else {
		  return resultNode.getLeft().getValue();
	  }
  }

	private IAVLNode generalSearch(int k) { // Returns parent of the desired node, or Node itself if it's in the tree. O(logn)
		IAVLNode currNode = this.root;
		if (currNode == null) { // Means tree is empty
			return null; // What we chose to return
		}
		while (currNode.getHeight() > 0) { // While we're not looking at a leaf
			if (currNode.getKey() == k) { // There's already a node with key k
				return currNode;
			}
			else if (k < currNode.getKey()) { // Go left (if possible)
				if (currNode.getLeft().isRealNode()) {
					currNode = currNode.getLeft();
				}
				else {
					return currNode;
				}
			}
			else { // Go right (if possible)
				if (currNode.getRight().isRealNode()) {
					currNode = currNode.getRight();
				}
				else {
					return currNode;
				}
			}
		}
		return currNode;
	}

	/**
   * public int insert(int k, String i)
   *
   * Inserts an item with key k and info i to the AVL tree.
   * The tree must remain valid, i.e. keep its invariants.
   * Returns the number of re-balancing operations, or 0 if no re-balancing operations were necessary.
   * A promotion/rotation counts as one re-balance operation, double-rotation is counted as 2.
   * Returns -1 if an item with key k already exists in the tree.
   */
   public int insert(int k, String i) { // Searching the location to insert the node and rebalancing the tree, including updating all fields of nodes in the path from root to the inserted node.
	   IAVLNode newNode = new AVLNode(k, i);
	   IAVLNode newParent = generalSearch(k);
	   if (newParent == null) { // Means tree is empty, and we need to initialize root
		   this.root = newNode;
		   root.setParent(null);
		   root.setLeft(EXT); root.setRight(EXT);
		   updateFields(root);
		   return 0;
	   }
	   if (newParent.getKey() == k) { // There's already a node with key k in the tree
		   return -1;
	   }
	   insertNode(newParent, newNode);
	   int numOfMoves = 1; // Promoting inside insertNode therefore there's already one balancing operation
	   numOfMoves += rebalanceInsert(newParent); // We already took care of newParent-newNode edge. now for the rest of the tree
       updateTreeFields();
	  return numOfMoves;
   }

   public void insertNode(IAVLNode parent, IAVLNode node) {
	   if (parent.getKey() > node.getKey()) { // Means child is a LEFT child
		   parent.setLeft(node);
	   }
	   else { // Means child is a RIGHT child. CANNOT be equal.
		   parent.setRight(node);
	   }
	   node.setParent(parent);
	   node.setLeft(EXT);
	   node.setRight(EXT);
	   updateFields(node);
       updateFields(parent);
   }

   private boolean isLegalRD(int[] rD) { // Checks if a given rank Difference is legal according to AVL definition. O(1)
	   int[][] legalRD = {{1,1}, {1,2}, {2,1}};
	   for(int[] possibleRD : legalRD) {
		   if (rD[0] == possibleRD[0] && rD[1] == possibleRD[1]) {
			   return true;
		   }
	   }
	   return false;
   }

   private void updateFields(IAVLNode node) { // Updates all fields of the node. O(1)
	   node.updateMin();
	   node.updateMax();
	   node.updateSize(updateSizeInTree(node));
	   node.updateBalanceFactor();
	   node.setHeight(Math.max(node.getLeft().getHeight(), node.getRight().getHeight()) + 1); // Updates rank
	   node.updateRankDifference(updateRankDifferenceInTree(node));
   }

   private void updateTreeFields() {
	   this.min = root.getMin();
       this.max = root.getMax();
       this.size = root.getSize();
   }

   private int updateSizeInTree(IAVLNode node) { // O(1)
	   int inputSize = 0;
	   if (node.getKey() != -1) {
		   inputSize = node.getLeft().getSize() + node.getRight().getSize() + 1;
	   }
	   return inputSize;
   }

   private int[] updateRankDifferenceInTree(IAVLNode node) { // O(1)
	   int[] rankDifference = new int[2];
	   if (node.getKey() != -1) { // Means node is not EXT
		   rankDifference[0] = node.getHeight() - node.getLeft().getHeight();
		   rankDifference[1] = node.getHeight() - node.getRight().getHeight();
	   }
	   return rankDifference;
   }

   private int rebalanceInsert(IAVLNode node) { // Rebalance from this node and go up. O(logn)
	   int numOfOperations = 0;
	   IAVLNode parent = node.getParent(); // Doing most operations in relation to parent, stating the child as input because it's more convenient that determining every time whether it's left child or right child.
       while (parent != null) {
		   int[] parentRD = updateRankDifferenceInTree(parent);
		   if (!isLegalRD(parentRD)) {
			   int[] nodeRD = node.getRankDifference();
			   if ((parentRD[0] == 0 && parentRD[1] == 1) || (parentRD[0] == 1 && parentRD[1] == 0)) { // Means parent's rD is either [0,1] or [1,0]
				   promote(parent);
				   numOfOperations++;
			   } else if ((parentRD[0] == 0 && parentRD[1] == 2) && (nodeRD[0] == 1 && nodeRD[1] == 2)) { // Parent is (0,2) and node is (1,2)
				   rotateRight(parent);
				   demote(parent);
				   numOfOperations += 2;
			   } else if ((parentRD[0] == 2 && parentRD[1] == 0) && (nodeRD[0] == 2 && nodeRD[1] == 1)) { // Its symmetrical counterpart: Parent (2,0), node (2,1)
				   rotateLeft(parent);
				   demote(parent);
				   numOfOperations += 2;
			   } else if ((parentRD[0] == 0 && parentRD[1] == 2) && (nodeRD[0] == 2 && nodeRD[1] == 1)) { // Parent is (0,2) and node is (2,1)
				   promote(node.getRight());
				   demote(node);
				   rotateLeft(node);
				   rotateRight(parent);
				   demote(parent);
				   numOfOperations += 5;
			   } else if ((parentRD[0] == 2 && parentRD[1] == 0) && (nodeRD[0] == 1 && nodeRD[1] == 2)) { // Its symmetrical counterpart:  Parent (2,0), node (1,2)
				   promote(node.getLeft());
				   demote(node);
				   rotateRight(node);
				   rotateLeft(parent);
				   demote(parent);
				   numOfOperations += 5;
			   }
			   else if (nodeRD[0] == 1 && nodeRD[1] == 1) { // Special case relevant only for joining trees. node is (1,1)
				   if (parentRD[0] == 0 && parentRD[1] == 2) { // node is (1,1) left child to a (0,2) parent
					   rotateRight(parent);
					   promote(node);
				   }
				   else if (parentRD[0] == 2 && parentRD[1] == 0) { // node is (1,1) right child to a (2,0) parent
					   rotateLeft(parent);
					   promote(node);
				   }
			   }
		   }
		   updateFields(parent);
		   node = parent;
		   parent = parent.getParent();
	   }
	   return numOfOperations;
   }

   private void rotateRight(IAVLNode parent) { // Mind that we rotate parent with its left child O(1)
	   IAVLNode node = parent.getLeft();
	   IAVLNode grandpa = parent.getParent();
	   IAVLNode rightChild = node.getRight();
	   node.setParent(grandpa);
	   if (grandpa == null) { // Means parent is the root!
		   this.root = node;
	   }
	   else { // Parent is not root
		   if (grandpa.getKey() < parent.getKey()) { // Means parent is pop's right child
			   grandpa.setRight(node);
		   } else { // Parent is pop's left child (can't be equal)
			   grandpa.setLeft(node);
		   }
	   }
	   parent.setLeft(rightChild);
	   rightChild.setParent(parent);
	   node.setRight(parent);
	   parent.setParent(node);
   }

   private void rotateLeft(IAVLNode parent) { // Mind that we rotate parent with its right child. O(1)
	   IAVLNode node = parent.getRight();
	   IAVLNode grandma = parent.getParent();
	   IAVLNode leftChild = node.getLeft();
	   node.setParent(grandma);
	   if (grandma == null) { // Parent is the root
		   this.root = node;
	   }
	   else {
		   if (grandma.getKey() < parent.getKey()) { // Means parent is mah's right child
			   grandma.setRight(node);
		   } else { // Parent is mah's right child (can't be equal)
			   grandma.setLeft(node);
		   }
	   }
	   parent.setRight(leftChild);
	   leftChild.setParent(parent);
	   node.setLeft(parent);
	   parent.setParent(node);
   }

   private void promote(IAVLNode node) { // O(1)
	   node.setHeight(node.getHeight() + 1);
   }

   private void demote(IAVLNode node) { // O(1)
	   node.setHeight(node.getHeight() - 1);
   }

  /**
   * public int delete(int k)
   *
   * Deletes an item with key k from the binary tree, if it is there.
   * The tree must remain valid, i.e. keep its invariants.
   * Returns the number of re-balancing operations, or 0 if no re-balancing operations were necessary.
   * A promotion/rotation counts as one re-balance operation, double-rotation is counted as 2.
   * Returns -1 if an item with key k was not found in the tree.
   */
   public int delete(int k) // O(logn)
   {
	   IAVLNode deletedNode = generalSearch(k);
	   int numOfOps = 0;
	   if (deletedNode == null) {
		   return -1;
	   }
	   else {
		   if (isBinary(deletedNode)) { // Node is binary
			   binSuccessor(deletedNode); // Find its successor and replace it!
			   // Node is now either unary or a leaf.
			   // Bear in mind that current tree is not necessarily a BST nor AVL tree! But we'll fix it
		   }
		   if (isLeaf(deletedNode)) {
			   IAVLNode parent = deletedNode.getParent();
			   deleteALeaf(deletedNode);
			   if (empty()) { // Means we just deleted the root and tree is now empty - no rebalance steps required
				   return 0;
			   }
				   updateFields(parent);
				   int[] parentRD = parent.getRankDifference();
				   if (parentRD[0] == 2 && parentRD[1] == 2) { // Special case from other rebalance cases that parent became a leaf
					   demote(parent);
					   numOfOps++;
				   }
				   if (parent.getLeft().isRealNode()) {
					   numOfOps += rebalanceDelete(parent.getLeft());
				   }
				   else if (parent.getRight().isRealNode()) {
					   numOfOps += rebalanceDelete(parent.getRight());
				   }
		   }
		   else if (isUnary(deletedNode)) {
			   IAVLNode child = deletedNode.getLeft().isRealNode() ? deletedNode.getLeft() : deletedNode.getRight(); // Get the deleted unary node child, either one.
			   deleteUnary(deletedNode);
			   numOfOps += rebalanceDelete(child); // Start from child and go up
		   }
	   }
	   numOfOps += rebalanceRoot();
	   return numOfOps;
   }

   private boolean isBinary(IAVLNode node) { // O(1)
	   return node.getRight().isRealNode() && node.getLeft().isRealNode();
   }

   private boolean isUnary(IAVLNode node) { // O(1)
	   return node.getRight().isRealNode() ^ node.getLeft().isRealNode();
   }

   private boolean isLeaf(IAVLNode node) { // O(1)
	   return !node.getRight().isRealNode() && !node.getLeft().isRealNode();
   }


   public void binSuccessor(IAVLNode node) { // x is a binary node and Search(x) != null. O(logn)
	   IAVLNode succs = node.getRight();
	   while (succs.getHeight() != -1) {
		   if (succs.getLeft().getHeight() != -1) {
			   succs = succs.getLeft();
		   }
		   else {
			   break;
		   }
	   }
	   if (succs.isRealNode()) {
		   switchNodes(node, succs);
	   }
	   updateFields(node);
	   updateFields(succs);
	   updateTreeFields();
   }

   private void switchNodes(IAVLNode node, IAVLNode succs) { // Switch places of a node and its successor. O(1)
	   IAVLNode parent = node.getParent();
	   IAVLNode nodeLeft = node.getLeft(); // The nodes Left child
	   IAVLNode nodeRight = node.getRight(); // nodeRight may be succs itself
	   IAVLNode succsParent = succs.getParent(); // succsParent may be node itself
	   IAVLNode succsRight = succs.getRight(); // may be EXT
	   /* general switches relevant for all cases: */
	   succs.setLeft(nodeLeft);
	   nodeLeft.setParent(succs); // Both ways
	   node.setRight(succsRight);
	   if (succsRight.isRealNode()) {
		   succsRight.setParent(node); // Both ways, only if succsRight is NOT EXT
	   }
	   succs.setParent(parent);
	   if (parent != null) { // Means node is not the root!
		   if (parent.getLeft().getKey() == node.getKey()) { // Node is parents left child
			   parent.setLeft(succs); // Both ways
		   } else { // Node is parents right child
			   parent.setRight(succs);
		   }
	   }
	   else { // Node IS the root (but not anymore - now succs is the root!)
		   this.root = succs;
	   }
	   /* Assignment differ for special cases: */
	   if (nodeRight.getKey() == succs.getKey()) { // Node is succs parent
		   succs.setRight(node);
		   node.setParent(succs);
	   }
	   else  { // Node is not succs parent
		   node.setParent(succsParent);
		   succsParent.setLeft(node);
		   succs.setRight(nodeRight);
		   nodeRight.setParent(succs);
	   }
	   node.setLeft(EXT);
   } // Result is NOT BST nor AVL but invariant is maintained as the node is deleted immediately after the switch

	private void deleteALeaf(IAVLNode node) { // Deletes a leaf from tree. O(1)
	   IAVLNode parent = node.getParent();
	   node.setParent(null);
		if (node.getKey() != this.root.getKey()) { // Means deleting an inside leaf, Tree won't remain empty
			if (node.getKey() == parent.getLeft().getKey()) { // node was a left child
				parent.setLeft(EXT);
			} else { // node was a right child
				parent.setRight(EXT);
			}
		}
		else { // Else is ok, delete the node and get an empty tree
			this.root = null;
		}
	   node.setLeft(null);
	   node.setRight(null);
   }

   private void deleteUnary(IAVLNode node) { // Deletes an unary node from tree. O(1)
	   IAVLNode parent = node.getParent();
	   node.setParent(null);
	   if (node.getLeft().isRealNode()) { // node had a left child and right was EXT
		   IAVLNode left = node.getLeft();
		   setParentChild(node, parent, left);
	   }
	   else { // node had a right child and left was EXT
		   IAVLNode right = node.getRight();
		   setParentChild(node, parent, right);
	   }
	   node.setLeft(null);
	   node.setRight(null);
   }

	private void setParentChild(IAVLNode node, IAVLNode parent, IAVLNode child) {
		if (node.getKey() == root.getKey()) { // Means we're deleting the root
			this.root = child;
		}
		else { // Not deleting the root
			if (node.getKey() == parent.getLeft().getKey()) { // node was a left child
				parent.setLeft(child);
			} else { // node was a right child
				parent.setRight(child);
			}
		}
		child.setParent(parent);
	}

	private int rebalanceDelete(IAVLNode node) { // Rebalances the tree after node deletion. O(logn)
	   IAVLNode parent = node.getParent();
	   int numOfOps = 0;
	   while (parent != null) {
		   updateFields(parent);
		   int[] parentRD = parent.getRankDifference();
		   if (parentRD[0] == 2 && parentRD[1] == 2) { // Parent is a (2,2) node
			   demote(parent);
			   numOfOps++;
		   }
		   else if (parentRD[0] == 3 && parentRD[1] == 1) { // Parent is a (3,1) node, 3 cases
			   IAVLNode rightChild = parent.getRight(); // Get the right child, which wasn't in the deletion route
			   int[] rightRD = rightChild.getRankDifference();
			   if (rightRD[0] == 1 && rightRD[1] == 1) { // Right child is a (1,1) node
				   rotateLeft(parent);
				   demote(parent);
				   promote(rightChild);
				   numOfOps += 3;
			   }
			   else if (rightRD[0] == 1 && rightRD[1] == 2) { // Right child is a (1,2) node
				   IAVLNode leftRightChild = rightChild.getLeft();
				   rotateRight(node);
				   rotateLeft(parent);
				   promote(leftRightChild);
				   demote(node);
				   demote(parent); demote(parent); // Double demote, counts as 2 operations
				   numOfOps += 6;
			   }
			   else if (rightRD[0] == 2 && rightRD[1] == 1) { // Right child is a (2,1) node
				   rotateLeft(parent);
				   demote(parent); demote(parent); // Double demote
				   numOfOps += 3;
			   }
		   }
		   else if (parentRD[0] == 1 && parentRD[1] == 3) { // Parent is a (3,1) node, all symmetrical cases to the above
			   IAVLNode leftChild = parent.getLeft(); // Get the left child, which wasn't in the deletion route
			   int[] leftRD = leftChild.getRankDifference();
			   if (leftRD[0] == 1 && leftRD[1] == 1) { // Left child is a (1,1) node
				   rotateRight(parent);
				   demote(parent);
				   promote(leftChild);
				   numOfOps += 3;
			   }
			   else if (leftRD[0] == 2 && leftRD[1] == 1) { // Left child is a (2,1) node
				   IAVLNode rightLeftChild = leftChild.getRight();
				   rotateLeft(node);
				   rotateRight(parent);
				   promote(rightLeftChild);
				   demote(node);
				   demote(parent); demote(parent); // Double demote
				   numOfOps += 6;
			   }
			   else if (leftRD[0] == 1 && leftRD[1] == 2) { // Left child is a (1,2) node
				   rotateRight(parent);
				   demote(parent); demote(parent); // Double demote
				   numOfOps += 3;
			   }
		   }
		   updateFields(parent);
		   node = parent;
		   parent = parent.getParent();
	   }
	   return numOfOps;
   }

   private int rebalanceRoot() { // In special cases the root is not rebalanced. O(1)
	   int[] rootRD = this.root.getRankDifference();
	   if (!isLegalRD(rootRD)) { // Check if these operations are even necessary
		   if (rootRD[0] == 2 && rootRD[1] == 2) { // Root is a (2,2) node
			   demote(root);
			   return 1;
		   }
		   else if (rootRD[0] == 1 && rootRD[1] == 3) { // Root is a (3,1) node. 3 cases.
			   IAVLNode left = root.getLeft();
			   return rebalanceDelete(left); // Calls for rebalanceDelete on left node to ensure rebalancing. Two iterations TOTAL
		   }
		   else if (rootRD[0] == 3 && rootRD[1] == 1) { // Symmetrical cases
			   IAVLNode right = root.getRight();
			   return rebalanceDelete(right); // Same here, two iterations TOTAL.
		   }
	   }
	   return 0;
   }

   /**
    * public String min()
    *
    * Returns the info of the item with the smallest key in the tree,
    * or null if the tree is empty.
    */
   public String min()
   {
	   return !empty() ? this.root.getMin().getValue() : null; // Returns min key node's info or null if tree is empty. O(1)
   }

   /**
    * public String max()
    *
    * Returns the info of the item with the largest key in the tree,
    * or null if the tree is empty.
    */
   public String max()
   {
	   return !empty() ? this.root.getMax().getValue() : null; // Returns max key node's info or null if tree is empty. O(1)
   }

  /**
   * public int[] keysToArray()
   *
   * Returns a sorted array which contains all keys in the tree,
   * or an empty array if the tree is empty.
   */
  public int[] keysToArray()
  {
        return new int[33]; // to be replaced by student code
  }

  /**
   * public String[] infoToArray()
   *
   * Returns an array which contains all info in the tree,
   * sorted by their respective keys,
   * or an empty array if the tree is empty.
   */
  public String[] infoToArray()
  {
        return new String[55]; // to be replaced by student code
  }

   /**
    * public int size()
    *
    * Returns the number of nodes in the tree.
    */
   public int size()
   {
	   return this.root.getSize(); // Returns num of nodes in tree. O(1)
   }
   
   /**
    * public int getRoot()
    *
    * Returns the root AVL node, or null if the tree is empty
    */
   public IAVLNode getRoot()
   {
	   return this.root; // Returns the root of the tree. O(1)
   }

   /**
    * public AVLTree[] split(int x)
    *
    * splits the tree into 2 trees according to the key x. 
    * Returns an array [t1, t2] with two AVL trees. keys(t1) < x < keys(t2).
    * 
	* precondition: search(x) != null (i.e. you can also assume that the tree is not empty)
    * postcondition: none
    */   
   public AVLTree[] split(int x)
   {
	   IAVLNode xNode = generalSearch(x);
	   if (xNode == null) {
		   return null;
	   }
	   IAVLNode parent = xNode.getParent();
	   AVLTree Tsmall = new AVLTree(xNode.getLeft());
	   AVLTree Tbig = new AVLTree(xNode.getRight());
	   while (parent != null) {
		   IAVLNode gramps = parent.getParent();
		   if (parent.getKey() < x) { // x was a right child
			   parent.setRight(EXT); // replace with virtual node to update fields
			   updateFields(parent);
			   AVLTree jointTree = new AVLTree(parent.getLeft());
			   Tsmall.join(parent, jointTree); // Join to the smaller keys tree
			   Tsmall = Tsmall.root.getHeight() > jointTree.root.getHeight() ? Tsmall : jointTree;
		   }
		   else { // x was a left child
			   parent.setLeft(EXT); // replace with virtual node to update fields
			   updateFields(parent);
			   AVLTree jointTree = new AVLTree(parent.getRight());
			   Tbig.join(parent, jointTree); // Join to the bigger keys tree
			   Tbig = Tbig.root.getHeight() > jointTree.root.getHeight() ? Tbig : jointTree;
		   }
		   parent = gramps;
	   }
	   return new AVLTree[]{Tsmall, Tbig};
   }
   
   /**
    * public int join(IAVLNode x, AVLTree t)
    *
    * joins t and x with the tree. 	
    * Returns the complexity of the operation (|tree.rank - t.rank| + 1).
	*
	* precondition: keys(t) < x < keys() or keys(t) > x > keys(). t/tree might be empty (rank = -1).
    * postcondition: none
    */   
   public int join(IAVLNode x, AVLTree t) // Joins a given AVL tree t and a node x to this tree. O(log)
   {
	   if (t == null) {
		   t = new AVLTree(); // Make t an empty tree
	   }
	   if (t.empty()) { // t is empty tree, just insert x to this.
		   if (this.empty()) { // Both trees are empty
			   this.root = x;
		   }
		   else { // this is not empty
			   IAVLNode newParent = this.generalSearch(x.getKey());
			   this.insertNode(newParent, x);
		   }
		   updateFields(x);
		   this.rebalanceInsert(x);
		   updateTreeFields();
		   return this.root.getHeight() + 2;
	   }
	   else if (this.empty()) { // this is empty tree, just insert x to t.
			   IAVLNode newParent = t.generalSearch(x.getKey());
			   t.insertNode(newParent, x);
		   t.updateFields(x);
		   t.rebalanceInsert(x);
		   t.updateTreeFields();
		   return t.root.getHeight() + 2;
	   }
	   if (t.root.getHeight() > this.root.getHeight()) {
		   if (t.min.getKey() > this.max.getKey()) {
			   return joinRight(t, this, x);
		   }
		   else {
			   return joinLeft(t, this, x);
		   }
	   }
	   else {
		   if (t.root.getKey() > this.root.getKey()) {
			   return joinLeft(this,t,x);
		   }
		   else {
			   return joinRight(this, t, x);
		   }
	   }
   }

   private int joinRight(AVLTree big, AVLTree small, IAVLNode x) { //
	   IAVLNode joinPoint = big.root;
	   int bigRank = big.root.getHeight();
	   int smallRank = small.root.getHeight();
	   while (joinPoint.getHeight() > smallRank) {
		   if (joinPoint.getLeft().isRealNode()) {
			   joinPoint = joinPoint.getLeft();
		   }
		   else {
			   joinPoint = joinPoint.getRight();
		   }
	   }
	   x.setLeft(small.root);
	   x.setRight(joinPoint);
	   x.setParent(joinPoint.getParent());
	   small.root.setParent(x);
	   joinPoint.getParent().setLeft(x);
	   joinPoint.setParent(x);
	   updateFields(x);
	   rebalanceInsert(x);
	   return (bigRank - smallRank + 1);
   }

   private int joinLeft(AVLTree big, AVLTree small, IAVLNode x) {
	   IAVLNode joinPoint = big.root;
	   int bigRank = big.root.getHeight();
	   int smallRank = small.root.getHeight();
	   while (joinPoint.getHeight() > smallRank) {
		   if (joinPoint.getRight().isRealNode()) {
			   joinPoint = joinPoint.getRight();
		   }
		   else {
			   joinPoint = joinPoint.getLeft();
		   }
	   }
	   x.setRight(small.root);
	   x.setLeft(joinPoint);
	   x.setParent(joinPoint.getParent());
	   small.root.setParent(x);
	   joinPoint.getParent().setRight(x);
	   joinPoint.setParent(x);
	   updateFields(x);
	   rebalanceInsert(x);
	   return (bigRank - smallRank + 1);
   }

	/** 
	 * public interface IAVLNode
	 * ! Do not delete or modify this - otherwise all tests will fail !
	 */
	public interface IAVLNode{	
		public int getKey(); // Returns node's key (for virtual node return -1).
		public String getValue(); // Returns node's value [info], for virtual node returns null.
		public void setLeft(IAVLNode node); // Sets left child.
		public IAVLNode getLeft(); // Returns left child, if there is no left child returns null.
		public void setRight(IAVLNode node); // Sets right child.
		public IAVLNode getRight(); // Returns right child, if there is no right child return null.
		public void setParent(IAVLNode node); // Sets parent.
		public IAVLNode getParent(); // Returns the parent, if there is no parent return null.
		public boolean isRealNode(); // Returns True if this is a non-virtual AVL node.
    	public void setHeight(int height); // Sets the height of the node.
    	public int getHeight(); // Returns the height of the node (-1 for virtual nodes).
		public void updateRankDifference(int[] insertedRankDifference);
		public int[] getRankDifference();
		public void updateBalanceFactor();
		public int getBalanceFactor();
		public void updateSize(int inputSize);
		public int getSize();
		public void updateMin();
		public IAVLNode getMin();
		public void updateMax();
		public IAVLNode getMax();
	}

   /** 
    * public class AVLNode
    *
    * If you wish to implement classes other than AVLTree
    * (for example AVLNode), do it in this file, not in another file. 
    * 
    * This class can and MUST be modified (It must implement IAVLNode).
    */
  public class AVLNode implements IAVLNode {
	  private int key;
	  private String info;
	  private int rank;
	  private IAVLNode left;
	  private IAVLNode right;
	  private IAVLNode parent; // Gender fluid
	  private int[] rankDifference;
	  private int bF;
	  private int size;
	  private IAVLNode min;
	  private IAVLNode max;


	   public AVLNode(int key, String info) {
		   this.key = key;
		   this.info = info;
		   this.rank = -1;
		   this.left = null;
		   this.right = null;
		   this.parent = null;
		   this.rankDifference = new int[2];
		   this.bF = 0;
		   this.size = 1;
		   this.min = this;
		   this.max = this;
	   }


		public int getKey() { // Returns node's key or -1 if node is an external leaf. O(1)
			return Math.max(this.key, -1);
		}


		public String getValue() { // Returns node's info or null if node is an external leaf. O(1)
			return isRealNode() ? info : null;
		}


		public void setLeft(IAVLNode node) { // Sets the left sub-tree of the given node. Every node in left should be smaller the the node. O(1)
		   this.left = node;
		}


		public IAVLNode getLeft() { // Returns the left sub-tree of the given node or null if node is an external leaf. O(1)
			return this.left != null ? left : null;
		}


		public void setRight(IAVLNode node) { // Sets the right sub-tree of the given node. Every node in right should be greater then the node. O(1)
		   right = node;
		}


		public IAVLNode getRight() { // Returns the left sub-tree of the gien node or null if node is an external leaf. O(1)
			return this.right != null ? right : null;
		}


		public void setParent(IAVLNode node) { // Sets the parent node of the given node. O(1)
		   this.parent = node;
		}


		public IAVLNode getParent() { // Returns the parent node of the given node. O(1)
			return parent;
		}


		public boolean isRealNode() { // Checks if the node is an internal node or an external one. O(1)
			return (this.getHeight() != -1);
		}


	    public void setHeight(int height) { // Sets the height (i.e rank) of the given node. O(1)
		  rank = height;
	    }


	    public int getHeight() { // Returns the height (i.e. rank) of the given node or null if node is an external leaf. O(1)
	      return this.rank;
	    }


		public void updateRankDifference(int[] insertedRankDifference) { // Calculates rank differences between the node and its sub-trees.
		   this.rankDifference = insertedRankDifference;
		}

		public int[] getRankDifference() {
		   return rankDifference;
		}

		public void updateBalanceFactor() {
		   bF = this.key != -1 ? (this.left.getHeight() - this.right.getHeight()) : -1;
		}

		public int getBalanceFactor() {
		   return bF;
		}

		public void updateSize(int inputSize) {
			this.size = inputSize;
	   }

		public int getSize() {
		   return this.size;
		}

		public void updateMin() {
		   this.min = this.left != EXT ? this.left.getMin() : this;
		}

		public IAVLNode getMin() {
		   return this.min;
		}

		public void updateMax() {
		   this.max = this.right != EXT ? this.right.getMax() : this;
		}

		public IAVLNode getMax() {
		   return this.max;
		}

  }

}