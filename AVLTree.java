import java.util.Arrays;

/**
 *
 * AVLTree
 *
 * An implementation of aמ AVL Tree with
 * distinct integer keys and info.
 *
 */

public class AVLTree {
	IAVLNode root;
	IAVLNode min;
	IAVLNode max;
	int size;
	IAVLNode EXT = new AVLNode(-1, null);

	public AVLTree() {
		this.root = null;
		this.min = null;
		this.max = null;
		this.size = 0;
		this.EXT.setHeight(-1);
		this.EXT.updateSize(0);
	}

    public AVLTree(IAVLNode newRoot) { // Another Constructor
        this.root = newRoot;
        this.min = newRoot.getMin();
        this.max = newRoot.getMax();
        this.size = newRoot.getSize();
    }

  /**
   * public boolean empty()
   *
   * Returns true if and only if the tree is empty.
   *
   */
  public boolean empty() {
    return this.root == null; // Returns True if the Tree is empty from nodes. O(1).
  }

 /**
   * public String search(int k)
   *
   * Returns the info of an item with key k if it exists in the tree.
   * otherwise, returns null.
   */
  public String search(int k) { // Calls for recursive function. O(1) (Inside function is O(logn)).
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

	private IAVLNode generalSearch(int k){ // Returns parent of the desired node
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
		   if (Arrays.equals(possibleRD, rD)) {
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
	   int[][] rDParent4Promote = {{0,1}, {1,0}};
	   int[][] rDParent4Rotate = {{0,2}, {2,0}};
	   int[][] rdNode4Rotate = {{1,2}, {2,1}};
	   int[] specialJoinRDCase = {1,1};
	   IAVLNode parent = node.getParent(); // Doing most operations in relation to parent, stating the child as input because it's more convenient that determining every time whether it's left child or right child.
       while (parent != null) {
		   int[] parentRD = updateRankDifferenceInTree(parent);
		   if (!isLegalRD(parentRD)) {
			   int[] nodeRD = node.getRankDifference();
			   if (Arrays.asList(rDParent4Promote).contains(parentRD)) { // Means parent's rD is either [0,1] or [1,0]
				   promote(parent);
				   numOfOperations++;
			   } else if (Arrays.equals(parentRD, rDParent4Rotate[0]) && Arrays.equals(nodeRD, rdNode4Rotate[0])) { // First rotate case
				   rotateRight(parent);
				   demote(parent);
				   numOfOperations += 2;
			   } else if (Arrays.equals(parentRD, rDParent4Rotate[1]) && Arrays.equals(nodeRD, rdNode4Rotate[1])) { // Its symmetrical counterpart
				   rotateLeft(parent);
				   demote(parent);
				   numOfOperations += 2;
			   } else if (Arrays.equals(parentRD, rDParent4Rotate[0]) && Arrays.equals(nodeRD, rdNode4Rotate[1])) { // Second rotate case.
				   promote(node.getRight());
				   demote(node);
				   rotateLeft(node);
				   rotateRight(parent);
				   demote(parent);
				   numOfOperations += 5;
			   } else if (Arrays.equals(parentRD, rDParent4Rotate[1]) && Arrays.equals(nodeRD, rdNode4Rotate[0])) { // Its symmetrical counterpart
				   promote(node.getLeft());
				   demote(node);
				   rotateRight(node);
				   rotateLeft(parent);
				   demote(parent);
				   numOfOperations += 5;
			   }
			   else if (Arrays.equals(nodeRD, specialJoinRDCase)) { // Special case relevant only for joining trees
				   if (Arrays.equals(parentRD, rDParent4Rotate[0])) { // node is (1,1) left child to a (0,2) parent
					   rotateRight(parent);
					   promote(node);
				   }
				   else if (Arrays.equals(parentRD, rDParent4Rotate[1])) { // node is (1,1) right child to a (2,0) parent
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
   public int delete(int k)
   {
	   return 421;	// to be replaced by student code
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

	   return null; 
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
	   if (t.empty()) { // t is empty tree, just insert x to this.
		   if (this.empty()) { // Both trees are empty
			   this.root = x;
		   }
		   else { // this is not empty
			   IAVLNode newParent = this.generalSearch(x.getKey());
			   this.insertNode(newParent, x);
		   }
		   return this.root.getHeight() + 2;
	   }
	   else if (this.empty()) { // this is empty tree, just insert x to t.
		   if (t.empty()) { // Both trees are empty
			   this.root = x;
		   }
		   else { // t is not empty
			   IAVLNode newParent = t.generalSearch(x.getKey());
			   t.insertNode(newParent, x);
		   }
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
			return isRealNode() ? left : null;
		}


		public void setRight(IAVLNode node) { // Sets the right sub-tree of the given node. Every node in right should be greater then the node. O(1)
		   right = node;
		}


		public IAVLNode getRight() { // Returns the left sub-tree of the gien node or null if node is an external leaf. O(1)
			return isRealNode() ? right : null;
		}


		public void setParent(IAVLNode node) { // Sets the parent node of the given node. O(1)
		   this.parent = node;
		}


		public IAVLNode getParent() { // Returns the parent node of the given node. O(1)
			return parent;
		}


		public boolean isRealNode() { // Checks if the node is an internal node or an external one. O(1)
			return this.getKey() != -1;
		}


	    public void setHeight(int height) { // Sets the height (i.e rank) of the given node. O(1)
		  rank = height;
	    }


	    public int getHeight() { // Returns the height (i.e rank) of the given node or null if node is an external leaf. O(1)
	      return this.isRealNode() ? rank : -1;
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