package tests;

import static student.testingsupport.ReflectionSupport.getMethod;
import static student.testingsupport.ReflectionSupport.invokeEx;
import static student.testingsupport.ReflectionSupport.reloadClassForName;

import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import student.TestCase;
import student.testingsupport.StringNormalizer;
import student.testingsupport.StringNormalizer.RegexNormalizerRule;
import student.testingsupport.StringNormalizer.StandardRule;
import student.testingsupport.annotations.Hint;
import student.testingsupport.annotations.ScoringWeight;

public class BigIntersectionTest extends TestCase{

	private Class<?> Rectangle1Class;
	private Method mainMethod;
	private boolean commWorking;
	
	private final StringNormalizer ALL_LINES = new StringNormalizer(
			new RegexNormalizerRule("(?dm)[\\\\p{javaWhitespace}&&[^\\n]]*#.*$",""),
			new RegexNormalizerRule("(?dm)[\\p{javaWhitespace}&&[^\n]]+$",""),
			StringNormalizer.standardRule(StandardRule.OPT_IGNORE_BLANK_LINES));
	
	private final StringNormalizer ONE_LINE = new StringNormalizer(
			new RegexNormalizerRule("[:,()]"," "),
			StringNormalizer.standardRule(StandardRule.IGNORE_PUNCTUATION),
			StringNormalizer.standardRule(StandardRule.IGNORE_CAPITALIZATION),
			StringNormalizer.standardRule(StandardRule.IGNORE_SPACING_DIFFERENCES));
	
	public void setUp(){
		this.commWorking= true;
		try{
			this.Rectangle1Class = reloadClassForName("Rectangle1");
			this.mainMethod = getMethod(this.Rectangle1Class, "main", String[].class);
		} catch (Throwable e) {
			if(fuzzyContains(e.getMessage(), "cannot find class Rectangle1"))
				this.commWorking = false;
			else if(e instanceof Error)
				throw e;
			else throw new RuntimeException(e);
		}
	}
	
	@Hint("Please check the format and syntax of your output.")
	@ScoringWeight(6.0)
	public void testMethod() throws Exception{
		if(! this.commWorking)
			fail("Please name your driver class Rectangle1 and place it in the default package.");
		String[][] expectedOutput = this.getOutput();
		String[] input = this.getInput();
		String[] params = this.getParams();
		
		try {
			this.checkOutput(input,expectedOutput,params);
		} catch (StackOverflowError e) {
			fail("StackOverFlowError Detected. This is most likely due to infinite recursion."
					+ " Please make sure your recursive functions always terminate.");
		} catch (Exception e){
			fail("An exception occured: " + e.getLocalizedMessage());
//			fail(e.getLocalizedMessage());
		}
	}
	


	private void checkOutput(String[] input, String[][] expectedOutput, String[] params) 
			throws Exception{
		Object[]  args = {params};
		invokeEx(null, this.mainMethod, args[0]);
		
		Scanner lines = new Scanner(ALL_LINES.normalize(systemOut().getHistory()));
		
		for(int i=0; i< expectedOutput.length; i++){
			String currentCommand = this.extractCommand(input[i]);
			String prevCommand = (i>0?this.extractCommand(input[i-1]):"");
			String preceedingCommand = (i==0)? "." :
				(" when excuted after |" + prevCommand + "!.");
			if(expectedOutput[i].length >1) {
				boolean ordered = requireOrder(currentCommand);
				String[] outputLines = new String[expectedOutput[i].length];
				for(int j=0; j<expectedOutput[i].length; j++){
					if(! lines.hasNextLine()){
						fail("Command |" + currentCommand
						+ "| did not produce all the output that was expected"
						+ preceedingCommand);
					}
					outputLines[j] = lines.nextLine();
				}
				String message = ordered?compareWithOrder(outputLines,expectedOutput[i]):
					compareWithoutOrder(outputLines,expectedOutput[i]);
				message = message.replaceAll("prevCommand", prevCommand);
				message = message.replaceAll("currentCommand", currentCommand);
				message = message.replaceAll("preceedingCommand", preceedingCommand);
				assertEquals(((i>0)? message : message.split(",")[0])
									 , "success", message); 
			}
			else if(expectedOutput[i].length == 1){
				String actualLine = ONE_LINE.normalize(lines.nextLine());
				String expectedLine = ONE_LINE.normalize(expectedOutput[i][0]);
				
				assertEquals( "Command |" + currentCommand
						+ "| produced different output than was expected"
						+ preceedingCommand
						+ ((i>0)?
								", or the preceeding |"
								+ prevCommand
								+ "| generated more output than expected"
								:""	)
						, expectedLine, actualLine);
			}
			
		}
			assertFalse("Command |" + extractCommand(input[input.length-1])
			+ "| generated more output than expected, or other extraneous output caused "
			+ "the output to get out of sync with expectations."
					,lines.hasNextLine());
			lines.close();
	}

	private String compareWithoutOrder(String[] outputLines, String[] expectedOutput) {
		// TODO Auto-generated method stub
		String actualLine = ONE_LINE.normalize(outputLines[0]);
		String expectedLine = ONE_LINE.normalize(expectedOutput[0]);
		if(!actualLine.equalsIgnoreCase(expectedLine))
			return "Command |" + "currentCommand" 
			+"|, or the preceeding |"
			+ "prevCommand"
			+ "| generated more output than expected";
		
		Hashtable<String, Integer> outputLineFreqs = new Hashtable<String,Integer>();
		for(int i=1; i<expectedOutput.length; i++){
			expectedLine = ONE_LINE.normalize(expectedOutput[i]);
			if(outputLineFreqs.containsKey(expectedLine))
				outputLineFreqs.put(expectedLine, outputLineFreqs.get(expectedLine)+1);
			else
				outputLineFreqs.put(expectedLine, 1);
		}
		
		for(int i=1; i<outputLines.length; i++){
			String[] intersects = outputLines[i].split(":");
			if(intersects[0].trim().compareTo(intersects[1].trim())>0)
				outputLines[i] = intersects[1]+" : "+intersects[0];
			actualLine = ONE_LINE.normalize(outputLines[i]);
			if(!outputLineFreqs.containsKey(actualLine))
				return outputLines[i] + "is either an incorrect output of Command |"
				+ "currentCommand"
				+"|; or the command |"
				+ "currentCommand"
				+ "| didn't produce all the required output.";
//				+ " Missing outputs are |"
//				+ outputLineFreqs.keySet() + "|";
			else{
				int f = outputLineFreqs.get(actualLine);
				if(f==1)
					outputLineFreqs.remove(actualLine);
				else
					outputLineFreqs.put(actualLine, f-1);
			}
		}
		if(outputLineFreqs.isEmpty())
			return "success";
		else return "Command |" + "currentCommand |"
			+ " didn't produce all the required output.";
//			+ "should have produced the following outputs |"
//			+ outputLineFreqs.keySet() + "|";
	}

	private String compareWithOrder(String[] outputLines, String[] expectedOutput) {
		boolean rt = false;
		for(int i =0; i<outputLines.length;i++){
			String actualLine = ONE_LINE.normalize(outputLines[i]);
			String expectedLine = ONE_LINE.normalize(expectedOutput[i]);
//			if(!actualLine.equalsIgnoreCase(expectedLine))
			if(!fuzzyContains(expectedLine,actualLine))
				return 
//				"Expected |" + expectedLine 
//				+ "| here. "
				"Command |" + "currentCommand" 
				+ ((i>0)? "| produced different output than was expected"
				+ "preceedingCommand" : 
				"|, or the preceeding |"
				+ "prevCommand"
				+ "| generated more output than expected");
			if(fuzzyContains(actualLine,"node has depth 0"))
				rt = !rt;
		}
		if (rt) return "success";
		else return "Inconsistent Tree with zero or even number of roots";
	}
	
	private boolean requireOrder(String command) {
		if(command.equals("dump")) return true;
		return false;
	}

	private String extractCommand(String input) {
		String pattern = "(insert|remove|dump|search|regionsearch|intersections)\\s*.*";	
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(input);
		String command = "";
		if(m.find())
			command = m.group(1);
		else
			return "Unknown command type, please report this to the GTA.";
		
		return command;
	}

	private String[] getInput() {
		// TODO Auto-generated method stub
		return new String[]{
				"insert rand27 50 50 100 70",
				"insert val8 1 150 149 60",
				"insert rect8 61 56 2 5",
				"insert rand45 50 180 100 70", 
				"insert rect1 34 2 1 4",
				"insert val2 220 100 50 20",
				"insert r3 200 120  150 100",
				"insert rand46 561 434 277 401",
				"insert rand47 250 150 100 50",
				"insert r3 483 274 63 19",
				"insert rand48 50 50 100 70",
				"insert r3 1 5 21 3",
				"insert rand49 311 575 562 173",
				"insert rand50 596 549 421 157",
				"insert virtualrec1 700 250 1 1",
				"insert val9 24 23 4 1",
				"insert XYZ 423 643 23 300",
				"insert xyz 450 123 6 1",
				"insert rand 81 818 15 12",
				"insert rect9 7 71 34 51",
				"insert rect9 73 4 12 45",
				"insert xyz 241 320 494 124",
				"intersections"
		};
	}

	private String[][] getOutput() {
		// TODO Auto-generated method stub
		return new String[][]{
			{"Rectangle accepted:(rand27, 50, 50, 100, 70)"},
			{"Rectangle accepted:(val8, 1, 150, 149, 60)"},
			{"Rectangle accepted:(rect8, 61, 56, 2, 5)"},
			{"Rectangle accepted:(rand45, 50, 180, 100, 70)"},
			{"Rectangle accepted:(rect1, 34, 2, 1, 4)"},
			{"Rectangle accepted:(val2, 220, 100, 50, 20)"},
			{"Rectangle accepted:(r3, 200, 120, 150, 100)"},
			{"Rectangle accepted:(rand46, 561, 434, 277, 401)"},
			{"Rectangle accepted:(rand47, 250, 150, 100, 50)"},
			{"Rectangle accepted:(r3, 483, 274, 63, 19)"},
			{"Rectangle accepted:(rand48, 50, 50, 100, 70)"},
			{"Rectangle accepted:(r3, 1, 5, 21, 3)"},
			{"Rectangle accepted:(rand49, 311, 575, 562, 173)"},
			{"Rectangle accepted:(rand50, 596, 549, 421, 157)"},
			{"Rectangle accepted:(virtualrec1, 700, 250, 1, 1)"},
			{"Rectangle accepted:(val9, 24, 23, 4, 1)"},
			{"Rectangle accepted:(XYZ, 423, 643, 23, 300)"},
			{"Rectangle accepted:(xyz, 450, 123, 6, 1)"},
			{"Rectangle accepted:(rand, 81, 818, 15, 12)"},
			{"Rectangle accepted:(rect9, 7, 71, 34, 51)"},
			{"Rectangle accepted:(rect9, 73, 4, 12, 45)"},
			{"Rectangle accepted:(xyz, 241, 320, 494, 124)"},
			{"Intersections pairs:",
				"(rand45, 50, 180, 100, 70) : (val8, 1, 150, 149, 60)",
				"(r3, 200, 120, 150, 100) : (rand47, 250, 150, 100, 50)",
				"(rand27, 50, 50, 100, 70) : (rect8, 61, 56, 2, 5)",
				"(rand46, 561, 434, 277, 401) : (rand49, 311, 575, 562, 173)",
				"(rand46, 561, 434, 277, 401) : (rand50, 596, 549, 421, 157)",
				"(rand46, 561, 434, 277, 401) : (xyz, 241, 320, 494, 124)",
				"(rand27, 50, 50, 100, 70) : (rand48, 50, 50, 100, 70)",
				"(rand48, 50, 50, 100, 70) : (rect8, 61, 56, 2, 5)",
				"(rand49, 311, 575, 562, 173) : (rand50, 596, 549, 421, 157)",
				"(XYZ, 423, 643, 23, 300) : (rand49, 311, 575, 562, 173)"}
		};
	}

	private String[] getParams() {
		// TODO Auto-generated method stub
		return new String[]{"BigIntersectionTest.txt"};
	}

//	public boolean containsNodeInfo(String actualOutputLine, String[] nodeInfo) throws Exception {
//		// TODO Auto-generated method stub
//		
//		if(numVisited == 0)
//			visited = new boolean[nodeInfo.length];
//		
//		for (int i = 0; i < nodeInfo.length; i++) {
//			String[] fragmentComponents = nodeInfo[i].split("\\s");
//			String regx1 = fragmentComponents[0] + ".+" + fragmentComponents[1];
//			String regx2 = fragmentComponents[1] + ".+" + fragmentComponents[0];
//			String pattern = regx1 + "|" + regx2 ;
//			Pattern r = Pattern.compile(pattern);
//			Matcher m = r.matcher(actualOutputLine);
//			if(m.find()){
//				if(visited[i])
//					continue;
//				numVisited++;
//				visited[i]=true;
//				
//				if(numVisited == nodeInfo.length){
//					for(int j=0; j<visited.length; j++)
//						if(visited[j])
//							throw new Exception("You forgot to report the intersection of"
//									+ " |" + nodeInfo[j].replaceFirst("\\s", " with ")
//									+ "|");
////						TestCase.assertTrue("You forgot to report the intersection of |" +
////								nodeInfo[j].replaceFirst("\\s", " with ") + "|"
////								,visited[j]);
//					numVisited=0;
//				}
//				return true;
//			}
//		}
//		return false;
//	}

}
