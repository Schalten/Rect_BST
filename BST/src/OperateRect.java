
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OperateRect {
	
	Map<BSTNode, ArrayList<BSTNode>> map;	
	public void regionSearch(BST head, Rectangle region) {
		for ( BSTNode rect1 : head) {
			if (region.intersects(rect1.rect.rect))
			{
				System.out.println("(" + rect1.rect.name + ", " + rect1.rect.rect.x + ", " + rect1.rect.rect.y + ", " + rect1.rect.rect.width + ", " + rect1.rect.rect.height + ")");
			}
		}		
	}
	
	public void Search(BST head, RectNode region) {
		int flag =0;
		for ( BSTNode rect1 : head) {
			//System.out.println(rect1.value() +"  my "+ region);
			if (0 == rect1.value().compareTo(region))
			{
				flag = 1;
				System.out.println("Rectangle found: (" + rect1.rect.name + ", " + rect1.rect.rect.x + ", " + rect1.rect.rect.y + ", " + rect1.rect.rect.width + ", " + rect1.rect.rect.height + ")");
			}
		}
		if (0==flag)
			System.out.println("Rectangle not found: " + region.toString());
	}
	
	public void intersections(BST head) {
		this.map = new HashMap<BSTNode, ArrayList<BSTNode>>();
		for ( BSTNode node1 : head) {
			for ( BSTNode node2 : head) {
				if (node1.value().compareTo(node2.rect) == 0) continue;
				
				if (node1.rect.rect.intersects(node2.rect.rect)) {
					if(!checkRepeat(node1, node2)) {
					String rect1 = "(" + node1.rect.name + ", " + node1.rect.rect.x + ", " + node1.rect.rect.y + ", " + node1.rect.rect.width + ", " + node1.rect.rect.height + ")";
					String rect2 = "(" + node2.rect.name + ", " + node2.rect.rect.x + ", " + node2.rect.rect.y + ", " + node2.rect.rect.width + ", " + node2.rect.rect.height + ")";
					System.out.println(rect1 + " : " + rect2);
					}
				}
			}
		}
		
	}
	
	public String remove(BST head, Rectangle rt) {
		String value = null;
		for ( BSTNode node1 : head) {
			if(node1.rect.rect.equals(rt))
				{
					value = node1.rect.name;
					break;
				}
		}
		
		return value;
	}
	
	private boolean checkRepeat(BSTNode n1, BSTNode n2) {
		ArrayList<BSTNode> temp;
		/*if (this.map.size() == 0) {
			ArrayList<BSTNode> newArr = new ArrayList<BSTNode>();
			newArr.add(n2);
			map.put(n1,newArr);
			return false;
			}*/
		if(map.containsKey(n1))
		{
			temp = map.get(n1);
			if(temp.contains(n2))
			{
				return true;
			}
			else
			{
				temp.add(n2);
				map.replace(n1, temp);
				return false;
			}
		}
		else if (map.containsKey(n2))
		{
			temp = map.get(n2);
			if(temp.contains(n1))
			{
				return true;
			}
			else
			{
				temp.add(n1);
				map.replace(n2, temp);
				return false;
			}
		}
		else
		{
			ArrayList<BSTNode> newArr = new ArrayList<BSTNode>();
			newArr.add(n2);
			map.put(n1,newArr);
			return false;
			
		}
		
	}

}
