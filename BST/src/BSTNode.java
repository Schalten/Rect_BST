

public class BSTNode {
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

}
