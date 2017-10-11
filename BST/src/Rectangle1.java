
import java.util.*;
import java.util.regex.Pattern;
import java.awt.Rectangle;
import java.io.*;


public class Rectangle1{

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		File inFile = null;
		if (0 < args.length) {
		   inFile = new File(args[0]);
		} else {
		   System.err.println("Invalid arguments count:" + args.length);
		   System.exit(0);
		}
		
		Parser p1 = new Parser();
		p1.read(inFile);
		List<String> cmdList = p1.getList();
		//System.out.println(cmdList);
		
		BST head = new BST();
		for (String element : cmdList) {
			
			String[] cmdParams = element.split(" ");
			int x = 0,y = 0,w = 0,h = 0;
			OperateRect operate;
			
			switch (cmdParams[0]) {
			
			case "insert":
				String name = cmdParams[1];
				x = Integer.parseInt(cmdParams[2]);
				y = Integer.parseInt(cmdParams[3]);
				w = Integer.parseInt(cmdParams[4]);
				h = Integer.parseInt(cmdParams[5]);
				Rectangle newRect = new Rectangle(x, y, w, h);
				
				if (!name.matches("^[A-Za-z][A-Za-z0-9_]*$") || w<1 || h<1 || !new Rectangle(0, 0, 1024, 1024).contains(newRect))
				{
					System.out.println("Rectangle rejected:(" +name+ ", " +x+ ", " +y+ ", " +w+ ", " +h+ ")" );
					break;
				}
				
				head.insert(new RectNode(name, x, y, w, h));
				System.out.println("Rectangle accepted:(" +name+ ", " +x+ ", " +y+ ", " +w+ ", " +h+ ")");
				break;
				
			case "remove":
				if(cmdParams.length == 2)
				{
					if (null == head.remove(new RectNode(cmdParams[1], 0, 0, 0, 0)))
						System.out.println("Rectangle rejected " + cmdParams[1]);						
				}
				else {
					x = Integer.parseInt(cmdParams[1]);
					y = Integer.parseInt(cmdParams[2]);
					w = Integer.parseInt(cmdParams[3]);
					h = Integer.parseInt(cmdParams[4]);
					
					operate = new OperateRect();
					String val = operate.remove(head, new Rectangle(x, y, w, h));
					if(null == val)
					{
						System.out.println("Rectangle rejected (" + x + ", " + y + ", " + w + ", " + h + ")" );
					}
					else
					{
						head.remove(new RectNode(val, 0, 0, 0, 0));
					}
					
				}
				break;
				
			case "search":
				/*RectNode foundNode = (RectNode)head.find( new RectNode(cmdParams[1],0,0,0,0));
				if( foundNode!= null)
				{
					System.out.println("Rectangle found: (" + foundNode.name + ", "+ foundNode.rect.x +", " + foundNode.rect.y + ", " + foundNode.rect.width + ", " + foundNode.rect.height + ")");
				}
				else
				{
					System.out.println("Rectangle not found: " + cmdParams[1]);
				}*/
				operate =new OperateRect();
				operate.Search(head, new RectNode(cmdParams[1],0,0,0,0));
				break;
				
			case "regionsearch":
				x = Integer.parseInt(cmdParams[1]);
				y = Integer.parseInt(cmdParams[2]);
				w = Integer.parseInt(cmdParams[3]);
				h = Integer.parseInt(cmdParams[4]);
				if (w<1 || h<1)
				{
					System.out.println("Rectangle rejected: (" + x +", " + y + ", " + w + ", " + h + ")");
					break;
				}
				operate =new OperateRect();
				System.out.println("Rectangles intersecting region (" + x +", " + y + ", " + w + ", " + h + "):");
				operate.regionSearch(head, new Rectangle(x, y, w, h));
				break;
				
			case "dump":
				head.printBST();
				break;
				
			case "intersections":
				operate =new OperateRect();
				System.out.println("Intersections pairs:");
				operate.intersections(head);
				break;
				
			default:
				System.out.println("Wrong Command");
								
			
			}
		}
		
		/*BufferedReader br = null;

        try {

            String sCurrentLine;

            br = new BufferedReader(new FileReader(inFile));

            while ((sCurrentLine = br.readLine()) != null) {
                //System.out.println("Here:" + sCurrentLine.trim().replaceAll("\\s+"," "));
            }
            
            BST head = new BST();
            head.printBST();
            head.insert(new RectNode("Bob", 0, 0, 10, 5));
            head.insert(new RectNode("Builder", 10, 10, 105, 50));
            head.insert(new RectNode("lavkar", 10, 10, 15, 20));
            head.insert(new RectNode("Awar", 10, 10, 15, 20));
            head.printBST();
            for(BSTNode t:head)
    		{
    			System.out.println("new code:" + t.rect);
    		}
            //head.printBST();
            head.remove(new RectNode("Bob", 0, 0, 0, 0));
            head.printBST();

        } 

        catch (IOException e) {
            e.printStackTrace();
        } 

        finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }*/

	}

}

class Parser{
	List<String> inputList;
	
	public Parser() {
		inputList = new ArrayList<String>();
	}
	
	public void read(File inFile)
	{
		BufferedReader br = null;

        try {

            String sCurrentLine, command;

            br = new BufferedReader(new FileReader(inFile));

            while ((sCurrentLine = br.readLine()) != null) {
            	command = sCurrentLine.trim().replaceAll("\\s+"," ");
                //System.out.println("Here:" + command +"abc");
                if (!command.equals(""))
                	this.inputList.add(command);                
            }
        } 

        catch (IOException e) {
            e.printStackTrace();
        } 

        finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
		
	}
	
	public List<String> getList()
	{
		return this.inputList;
	}
	
}
