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

	public AVLTree {
		this.root = null;
		this.min = null;
		this.max = null;
		this.size = 0;
		this.EXT.setHeight(-1);
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
	return searchRec(k, this.root);
  }

  private String searchRec(int k, IAVLNode node) { // Returns the value of a node with key k, or null if key k is not in tree. O(logn).
	  if (node == EXT) {
		  return null;
	  }
	  else if (k == node.getKey()) {
		  return node.getValue();
	  }
	  else {
		  if (k < node.getKey()) {
			  return searchRec(k, node.getLeft());
		  }
		  else if (k > node.getKey()) {
			  return searchRec(k, node.getRight());
		  }
	  }
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
   public int insert(int k, String i) {
	  return 420;	// to be replaced by student code
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
	   return !empty() ? this.min.getValue() : null; // Returns min key node's info or null if tree is empty. O(1)
   }

   /**
    * public String max()
    *
    * Returns the info of the item with the largest key in the tree,
    * or null if the tree is empty.
    */
   public String max()
   {
	   return !empty() ? this.max.getValue() : null; // Returns max key node's info or null if tree is empty. O(1)
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
	   return this.size; // Returns num of nodes in tree. O(1)
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
   public int join(IAVLNode x, AVLTree t)
   {
	   return -1;
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
		public int updateRankDifference(int[] rankDifference);
		public int updateBallanceFactor(int bF);
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
	  private AVLNode left;
	  private AVLNode right;
	  private AVLNode parent; // Gender fluid
	  private int[] rankDifference;
	  private int bF;


	   public AVLNode(int key, String info) {
		   this.key = key;
		   this.info = info;
		   this.rank;
		   this.left;
		   this.right;
		   this.parent;
		   this.rankDifference = new int[2];
		   this.bF;
	   }


		public int getKey() { // Returns node's key or -1 if node is an external leaf. O(1)
			return isRealNode() ? AVLNode.key : -1;
		}


		public String getValue() { // Returns node's info or null if node is an external leaf. O(1)
			return isRealNode() ? AVLNode.info : null;
		}


		public void setLeft(IAVLNode node) { // Sets the left sub-tree of the given node. Every node in left should be smaller the the node. O(1)
		   AVLNode.left = node;
		}


		public IAVLNode getLeft() { // Returns the left sub-tree of the given node or null if node is an external leaf. O(1)
			return isRealNode() ? AVLNode.left : null;
		}


		public void setRight(IAVLNode node) { // Sets the right sub-tree of the given node. Every node in right should be greater then the node. O(1)
		   AVLNode.right = right;
		}


		public IAVLNode getRight() { // Returns the left sub-tree of the gien node or null if node is an external leaf. O(1)
			return isRealNode() ? AVLNode.right : null;
		}


		public void setParent(IAVLNode node) { // Sets the parent node of the given node. O(1)
		   AVLNode.parent = node;
		}


		public IAVLNode getParent() { // Returns the parent node of the given node. O(1)
			return AVLNode.parent;
		}


		public boolean isRealNode() { // Checks if the node is an internal node or an external one. O(1)
			return getHeight() != -1;
		}


	    public void setHeight(int height) { // Sets the height (i.e rank) of the given node. O(1)
		  AVLNode.rank = height;
	    }


	    public int getHeight() { // Returns the height (i.e rank) of the given node or null if node is an external leaf. O(1)
	      return isRealNode() ? AVLNode.rank : -1;
	    }


		public int[] updateRankDifference(int[] rankDifference) { // Calculates rank differences between the node and it's sub-trees.
			rankDifference[0] = this.rank - this.left.rank;
			rankDifference[1] = this.rank - this.right.rank;
		}

		public int updateBallanceFactor(int bF) {
		   bF = this.left.rank - this.right.rank;
		   return bF;
		}

  }

}