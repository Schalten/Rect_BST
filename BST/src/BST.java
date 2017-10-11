import java.util.Iterator;
import java.util.Stack;

public class BST implements Iterable<BSTNode>{
	
	private BSTNode root; // Root of the BST
	  private int nodecount; // Number of nodes in the BST
	  private int depth;

	  // constructor
	  BST() { root = null; nodecount = 0; depth = 0;}

	  // Reinitialize tree
	  public void clear() { root = null; nodecount = 0; depth = 0;}

	  // Insert a record into the tree.
	  // Records can be anything, but they must be Comparable
	  // e: The record to insert.
	  public void insert(Comparable e) {
	    root = inserthelp(root, e);
	    nodecount++;
	  }

	  // Remove a record from the tree
	  // key: The key value of record to remove
	  // Returns the record removed, null if there is none.
	  public Comparable remove(Comparable key) {
	    Comparable temp = findhelp(root, key); // First find it
	    if (temp != null) {
	      root = removehelp(root, key); // Now remove it
	      nodecount--;
	    }
	    return temp;
	  }

	  // Return the record with key value k, null if none exists
	  // key: The key value to find
	  public Comparable find(Comparable key) { return findhelp(root, key); }

	  // Return the number of records in the dictionary
	  public int size() { return nodecount; }
	  
	  public void printBST()
	  {
		  depth = 0;
		  System.out.println("BST dump:");//printVisit(this.root);
		  printhelp(this.root);
		  if (this.root == null) System.out.println("Node has depth " + 0 + ", Value (" + null + ")");
		  System.out.println("BST size is: " + this.nodecount);
	  }
	  
	  private void printhelp(BSTNode rt) {
		    if (rt == null) {depth--;return;}
		    depth++;
		    printhelp(rt.left());
		    printVisit(rt);
		    depth++;
		    printhelp(rt.right());
		    depth--;
		  }
	  
	  private void printVisit(BSTNode value) {
		// TODO Auto-generated method stub
		  System.out.println("Node has depth " + depth + ", Value (" + value.rect.name + ", " + value.rect.rect.x + ", " + value.rect.rect.y + ", " + value.rect.rect.width + ", " + value.rect.rect.height +")");
		
	}

	private BSTNode inserthelp(BSTNode rt, Comparable e) {
			if (rt == null) return new BSTNode(e);
			if(rt.value().compareTo(e) >= 0)
			{
				rt.setLeft(inserthelp(rt.left(),e));
			}
			else
			{
				rt.setRight(inserthelp(rt.right(), e));
			}
			return rt;
		}
	  
	  private Comparable findhelp(BSTNode rt, Comparable key) {
			if (rt == null) return null;
			if(rt.value().compareTo(key) >0)
			{
				return findhelp(rt.left(),key);
			}
			else if (rt.value().compareTo(key)== 0)
			{
				return rt.value();
			}
			else return findhelp(rt.right(), key);
		}
	  
	  private BSTNode removehelp(BSTNode rt, Comparable key) {
		  if(rt == null) return null;
		  if(rt.value().compareTo(key) > 0)
			  rt.setLeft(removehelp(rt.left(), key));
		  else if(rt.value().compareTo(key) < 0)
			  rt.setRight(removehelp(rt.right(), key));
		  else {
			  //Found it
			  if (rt.left() == null) return rt.right();
			  else if (rt.right() == null) return rt.left();
			  else {
				  //to children
				  BSTNode temp =  getmax(rt.left());
				  rt.setValue(temp.value());
				  rt.setLeft(deletemax(rt.left()));
			  }
		  }
		  return rt;
	  }
	  
	  private BSTNode deletemax(BSTNode rt) {
		// TODO Auto-generated method stub
		  if (rt.right() == null) return rt.left();
		  rt.setRight(deletemax (rt.right()));
		  return rt;
	}

	private BSTNode getmax(BSTNode rt) {
		  if (rt.right() == null) return rt;
		  return getmax(rt.right());
	  }

	@Override
	public Iterator<BSTNode> iterator() {
		// TODO Auto-generated method stub
		return new BSTIterator(this.root);
	}
	
	class BSTIterator implements Iterator{
		private BSTNode tree;
		Stack<BSTNode> st;
		private void populateStack(Stack<BSTNode> st,BSTNode root)
		{
			 if(root==null) return;
			populateStack(st,root.right());
			st.push(root);
			populateStack(st,root.left());
 		}
		private BSTIterator(BSTNode tree){
			this.tree = tree;
			st=new Stack<BSTNode>();
			populateStack(st,this.tree);
		}
		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return !st.isEmpty();
		}

		@Override
		public BSTNode next() {
			// TODO Auto-generated method stub
			return st.pop();
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub
			
		}
		
	}

}

/*class BSTNode{
	private BSTNode left = null;
	private BSTNode right = null;
	RectNode rect = null;
	
	public BSTNode(Comparable rt)
	{
		this.rect = (RectNode) rt;
	}
	
	public void setValue(Comparable value) {
		// TODO Auto-generated method stub
		this.rect = (RectNode) value;
	}

	public void setRight(BSTNode newNode) {
		// TODO Auto-generated method stub
		this.right = newNode;
		
	}

	public void setLeft(BSTNode newNode) {
		// TODO Auto-generated method stub
		this.left = newNode;
		
	}

	public BSTNode right() {
		// TODO Auto-generated method stub
		return this.right;
	}

	public BSTNode left() {
		// TODO Auto-generated method stub
		return this.left;
	}

	public Comparable value()
	{
		return this.rect;
	}
	
		
}	*/