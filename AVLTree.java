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
	private final IAVLNode EXT = new AVLNode(-1, null);

	public AVLTree() { // Default constructor, will produce an empty tree.
		this.root = null;
		this.min = null;
		this.max = null;
		this.size = 0;
		this.EXT.setHeight(-1);
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
  public boolean empty() { // Returns True iff the Tree is empty from nodes. O(1).
    return this.root == null || !this.root.isRealNode();
  }

 /**
   * public String search(int k)
   *
   * Returns the info of an item with key k if it exists in the tree.
   * otherwise, returns null.
   */
  public String search(int k) { // Calls for Iterative function. O(1) (Inside function is O(logn)). O(logn) total.
	  IAVLNode resultNode = generalSearch(k);
	  if (resultNode == null || resultNode.getKey() != k) { // Means node not found
		  return null;
	  }
	  else { // Got the node! Get its value >:O !!
		  return resultNode.getValue();
	  }
  }

	private IAVLNode generalSearch(int k) { // Returns parent of the desired node for insertion, or Node itself if it's in the tree. O(logn)
		IAVLNode currNode = this.root;
		if (empty()) { // Means tree is empty
			return null; // What we chose to return
		}
		while (currNode.getHeight() > 0) { // While we're not looking at a leaf
			if (currNode.getKey() == k) { // There's already a node with key k
				return currNode; // For search
			}
			else if (k < currNode.getKey()) { // Go left (if possible)
				if (currNode.getLeft().isRealNode()) {
					currNode = currNode.getLeft();
				}
				else { // Can't go left anymore, should insert here
					return currNode;
				}
			}
			else { // Go right (if possible)
				if (currNode.getRight().isRealNode()) {
					currNode = currNode.getRight();
				}
				else { // Can't go right anymore, should insert here
					return currNode;
				}
			}
		}
		return currNode; // Should never reach here.
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
   public int insert(int k, String i) { // Searching the location to insert the node and rebalancing the tree, including updating all fields of nodes in the path from root to the inserted node. O(logn)
	   IAVLNode newNode = new AVLNode(k, i);
	   IAVLNode newParent = generalSearch(k);
	   if (newParent == null) { // Means tree is empty, and we need to initialize root
		   this.root = newNode;
		   root.setParent(null);
		   root.setLeft(EXT); root.setRight(EXT);
		   updateFields(root);
		   updateTreeFields();
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

   public void insertNode(IAVLNode parent, IAVLNode node) { // Inserting node to parent. O(1)
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
	   node.updateSize();
	   node.setHeight(Math.max(node.getLeft().getHeight(), node.getRight().getHeight()) + 1); // Updates rank
	   node.updateRankDifference(updateRankDifferenceInTree(node));
   }

   private void updateTreeFields() { // O(1)
	   this.min = root.getMin();
       this.max = root.getMax();
       this.size = root.getSize();
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
	   updateFields(node);
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
					   numOfOperations += 2;
				   }
				   else if (parentRD[0] == 2 && parentRD[1] == 0) { // node is (1,1) right child to a (2,0) parent
					   rotateLeft(parent);
					   promote(node);
					   numOfOperations +=2;
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
	   if (rightChild != null && rightChild.isRealNode()) {
		   rightChild.setParent(parent);
		   updateFields(rightChild);
	   }
	   if (node.isRealNode()) {
		   node.setParent(grandpa);
		   parent.setLeft(rightChild);
		   node.setRight(parent);
	   }
	   parent.setParent(node);
	   updateFields(parent);
	   updateFields(node);
   }

   private void rotateLeft(IAVLNode parent) { // Mind that we rotate parent with its right child. O(1)
	   IAVLNode node = parent.getRight();
	   IAVLNode grandma = parent.getParent();
	   IAVLNode leftChild = node.getLeft();
	   if (grandma == null) { // Parent is the root
		   this.root = node;
	   }
	   else { // grandma != null - parent is not the root
		   if (grandma.getKey() < parent.getKey()) { // Means parent is mah's right child
			   grandma.setRight(node);
		   } else { // Parent is mah's right child (can't be equal)
			   grandma.setLeft(node);
		   }
	   }
	   if (leftChild != null && leftChild.isRealNode()) {
		   leftChild.setParent(parent);
		   updateFields(leftChild);
	   }
	   if (node.isRealNode()) {
		   node.setParent(grandma);
		   parent.setRight(leftChild);
		   node.setLeft(parent);
	   }
	   parent.setParent(node);
	   updateFields(parent);
	   updateFields(node);
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
	   if (deletedNode == null || deletedNode.getKey() != k) {
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
			   numOfOps += rebalanceDelete(parent);
			   if (parent.getLeft().isRealNode()) { // To start rebalance from child, not from parent - might cover some edge cases
				   numOfOps += rebalanceDelete(parent.getLeft()) - 1; 
			   }
			   else if (parent.getRight().isRealNode()) { // In case parent doesn't have a left child.
				   numOfOps += rebalanceDelete(parent.getRight()) - 1;
			   }
		   }
		   else if (isUnary(deletedNode)) {
			   IAVLNode child = deletedNode.getLeft().isRealNode() ? deletedNode.getLeft() : deletedNode.getRight(); // Get the deleted unary node child, either one.
			   deleteUnary(deletedNode);
			   numOfOps += rebalanceDelete(child); // Start from child and go up
		   }
	   }
	   numOfOps += rebalanceRoot();
	   updateFields(this.root);
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

   private void deleteUnary(IAVLNode node) { // Deletes a unary node from tree. O(1)
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

	private void setParentChild(IAVLNode node, IAVLNode parent, IAVLNode child) { // Sets the parent's new child. O(1)
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
		updateFields(child);
	}

	private int rebalanceDelete(IAVLNode node) { // Rebalances the tree after node deletion. O(logn)
	   updateFields(node);
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
				   rotateRight(rightChild);
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
				   rotateLeft(leftChild);
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
		   updateFields(node);
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
   public String min() // O(1)
   {
	   return !empty() ? this.root.getMin().getValue() : null; // Returns min key node's info or null if tree is empty. O(1)
   }

   /**
    * public String max()
    *
    * Returns the info of the item with the largest key in the tree,
    * or null if the tree is empty.
    */
   public String max() // O(1)
   {
	   return !empty() ? this.root.getMax().getValue() : null; // Returns max key node's info or null if tree is empty. O(1)
   }

  /**
   * public int[] keysToArray()
   *
   * Returns a sorted array which contains all keys in the tree,
   * or an empty array if the tree is empty.
   */
  public int[] keysToArray() // This one is O(1), but it calls for a recursive function of O(n) and therefore O(n) total.
  {
	  if (empty()) { // If tree is empty - return an empty array just like they wanted
		  return new int[]{};
	  }
	  int[] arrayOfKeys = new int[this.root.getSize()]; // Initializing array
	  int[] index = new int[]{0}; // This array will serve as index.
	  return keysToArrayRec(this.root, arrayOfKeys, index); // Calls for a recursive function
  }

  private int[] keysToArrayRec(IAVLNode node, int[] array, int[] index) { // Travels in order and adds to the array. O(n)
	  if (node.isRealNode()) {
		  keysToArrayRec(node.getLeft(), array, index);
		  array[index[0]++] = node.getKey();
		  keysToArrayRec(node.getRight(), array, index);
	  }
	  return array;
  }

  /**
   * public String[] infoToArray()
   *
   * Returns an array which contains all info in the tree,
   * sorted by their respective keys,
   * or an empty array if the tree is empty.
   */
  public String[] infoToArray() // Same as keysToArray - O(n)
  {
	  if (empty()) { // If tree is empty - return an empty array just like they wanted
		  return new String[]{};
	  }
	  String[] arrayOfInfo = new String[this.root.getSize()]; // Initializing array
	  int[] index = new int[]{0}; // This array will serve as index.
	  return infoToArrayRec(this.root, arrayOfInfo, index); // Calls for a recursive function
  }

  private String[] infoToArrayRec(IAVLNode node, String[] array, int[] index) { // Travels in order and adds to the array. O(n)
	  if (node.isRealNode()) {
		  infoToArrayRec(node.getLeft(), array, index);
		  array[index[0]++] = node.getValue();
		  infoToArrayRec(node.getRight(), array, index);
	  }
	  return array;
  }

   /**
    * public int size()
    *
    * Returns the number of nodes in the tree.
    */
   public int size() // O(1)
   {
       if (empty()) {
           return 0;
       }
	   return this.root.getSize(); // Returns num of nodes in tree. O(1)
   }
   
   /**
    * public int getRoot()
    *
    * Returns the root AVL node, or null if the tree is empty
    */
   public IAVLNode getRoot() // O(1)
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
   public AVLTree[] split(int x) // O(logn)
   {
	   IAVLNode xNode = generalSearch(x);
	   if (xNode == null) { // x wasn't found. Shouldn't reach here, was made just to calm IntelliJ down :)
		   return null;
	   }
	   IAVLNode parent = xNode.getParent();
	   AVLTree Tsmall = new AVLTree(xNode.getLeft()); // Tsmall initialized to be xNode's left sub-tree
	   AVLTree Tbig = new AVLTree(xNode.getRight()); // Tbig initialized to be xNode's right sub-tree
	   while (parent != null) {
		   IAVLNode gramps = parent.getParent();
		   if (parent.getKey() < x) { // x was a right child
			   parent.setRight(EXT); // replace with virtual node to update fields
			   updateFields(parent);
			   AVLTree jointTree = new AVLTree(parent.getLeft()); // Get the parents left sub-tree
			   Tsmall.join(parent, jointTree); // Join to the smaller keys tree
			   Tsmall.root = Tsmall.root.getHeight() > jointTree.root.getHeight() ? Tsmall.root : jointTree.root; // Assigns Tsmall to be the bigger (by rank) tree of both
		   }
		   else { // x was a left child
			   parent.setLeft(EXT); // replace with virtual node to update fields
			   updateFields(parent);
			   AVLTree jointTree = new AVLTree(parent.getRight()); // Get the parents right sub-tree
			   Tbig.join(parent, jointTree); // Join to the bigger keys tree
			   Tbig.root = Tbig.root.getHeight() >= jointTree.root.getHeight() ? Tbig.root : jointTree.root; // Assigns Tbig to be the bigger (by rank) tree of both
		   }
		   parent = gramps; // go UP
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
   public int join(IAVLNode x, AVLTree t) // Joins a given AVL tree t and a node x to this tree. O(|rank(this) - rank(t)| + 1)
   {
	   if (t == null) {
		   t = new AVLTree(); // Make t an empty tree
	   }
	   x.setParent(null);
	   x.setLeft(EXT); x.setRight(EXT);
	   updateFields(x);
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
	   } // Both trees aren't empty
	   if (t.root.getHeight() > this.root.getHeight()) { // Always join small tree (by rank) to big tree (by rank)
		   if (t.min.getKey() > this.max.getKey()) {
			   return t.joinRight(this, x);
		   }
		   else {
			   return t.joinLeft(this, x);
		   }
	   }
	   else {
		   if (t.root.getKey() > this.root.getKey()) {
			   return this.joinLeft(t,x);
		   }
		   else {
			   return this.joinRight(t, x);
		   }
	   }
   }

   private int joinRight(AVLTree small, IAVLNode x) { // Joins x and the tree with the smaller rank with this (the tree with a greater rank). O(|rank(this) - rank(t)| + 1)
	   IAVLNode joinPoint = this.root;
	   int bigRank = this.root.getHeight();
	   int smallRank = small.root.getHeight();
	   while (joinPoint.getHeight() > smallRank && joinPoint.getLeft().isRealNode()) { // Looking for the first node x with rank(x) <= small.rank()
		   joinPoint = joinPoint.getLeft();
	   }
	   if (joinPoint.getHeight() <= smallRank) {
		   x.setRight(joinPoint);
		   if (joinPoint.getParent() != null && joinPoint.getParent().getKey() != x.getKey()) { // Might cover special cases in split
			   x.setParent(joinPoint.getParent());
			   joinPoint.getParent().setLeft(x);
		   }
		   joinPoint.setParent(x);
	   }
	   else { // joinPoints rank is greater than smallRank!! Happens when joinPoint's chi.d is EXT!
		   x.setParent(joinPoint);
		   joinPoint.setLeft(x);
		   x.setRight(EXT);
	   }
	   x.setLeft(small.root);
	   small.root.setParent(x);
	   updateFields(x);
	   if (x.getHeight() > bigRank) { // New root
		   this.root = x;
		   x.setParent(null);
	   }
	   rebalanceInsert(x);
	   updateTreeFields();
	   return (bigRank - smallRank + 1);
   }

   private int joinLeft(AVLTree small, IAVLNode x) { // Same goes here. O(|rank(this) - rank(t)| + 1)
	   IAVLNode joinPoint = this.root;
	   int bigRank = this.root.getHeight();
	   int smallRank = small.root.getHeight();
	   while (joinPoint.getHeight() > smallRank && joinPoint.getRight().isRealNode()) { // Finds the first node x in big tree in which rank(x) <= rank(small_tree)
		   joinPoint = joinPoint.getRight();
	   }
	   if (joinPoint.getHeight() <= smallRank) {
		   x.setLeft(joinPoint);
		   if (joinPoint.getParent() != null && joinPoint.getParent().getKey() != x.getKey()) { // Might cover special cases in split
			   x.setParent(joinPoint.getParent());
			   joinPoint.getParent().setRight(x);
		   }
		   joinPoint.setParent(x);
	   }
	   else {
		   x.setParent(joinPoint);
		   joinPoint.setRight(x);
		   x.setLeft(EXT);
	   }
	   x.setRight(small.root);
	   small.root.setParent(x);
	   updateFields(x);
	   if (x.getHeight() > bigRank) { // New root
		   this.root = x;
		   x.setParent(null);
	   }
	   rebalanceInsert(x);
	   updateTreeFields();
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
		public void updateSize();
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
		   this.size = key > -1 ? 1 : 0;
		   this.min = this;
		   this.max = this;
	   }


		public int getKey() { // Returns node's key or -1 if node is an external leaf. O(1)
			return Math.max(this.key, -1);
		}


		public String getValue() { // Returns node's info or null if node is an external leaf. O(1)
			return isRealNode() ? info : null;
		}


		public void setLeft(IAVLNode node) { // Sets the left sub-tree of the given node. Every node in left should be smaller than the node. O(1)
		   this.left = node;
		}


		public IAVLNode getLeft() { // Returns the left sub-tree of the given node or null if node is an external leaf. O(1)
			return this.left != null ? left : null;
		}


		public void setRight(IAVLNode node) { // Sets the right sub-tree of the given node. Every node in right should be greater than the node. O(1)
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


	    public void setHeight(int height) { // Sets the height (i.e. rank) of the given node. O(1)
		  rank = height;
	    }


	    public int getHeight() { // Returns the height (i.e. rank) of the given node or null if node is an external leaf. O(1)
	      return this.rank;
	    }


		public void updateRankDifference(int[] insertedRankDifference) { // Calculates rank differences between the node and its sub-trees. O(1)
		   this.rankDifference = insertedRankDifference;
		}

		public int[] getRankDifference() { // Gets rank difference of node. O(1)
		   return rankDifference;
		}

		public void updateSize() { // Recalculates the size of the sub-tree with node as its root. O(1)
		   this.size = this.left.getSize() + this.right.getSize() + 1;
		}

		public int getSize() { // Returns the size of this sub-tree. O(1)
		   return this.size;
		}

		public void updateMin() { // Recalculates the minimum of this sub-tree. O(1)
		   this.min = this.left.isRealNode() ? this.left.getMin() : this;
		}

		public IAVLNode getMin() { // Returns the node with the minimum key of this sub-tree. O(1)
		   return this.min;
		}

		public void updateMax() { // Recalculates the maximum of this sub-tree. O(1)
		   this.max = this.right.isRealNode() ? this.right.getMax() : this;
		}

		public IAVLNode getMax() { // Returns the node with the maximum key of this sub-tree. O(1)
		   return this.max;
		}

  }

}
