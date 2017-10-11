
import java.awt.Rectangle;


public class RectNode implements Comparable<RectNode>{
	String name;
	public Rectangle rect;
	
	public RectNode(String name, int x, int y, int w, int h) {
		// TODO Auto-generated constructor stub
		this.name = name;
		this.rect = new Rectangle(x,y,w,h);
		
	}
	@Override
	public String toString(){
        return name;
    }

	@Override
	public int compareTo(RectNode other) {
		// TODO Auto-generated method stub
		
		return this.name.compareTo(other.name);
	}
	
	

}
