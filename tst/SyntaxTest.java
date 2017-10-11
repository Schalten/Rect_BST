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

public class SyntaxTest extends TestCase{

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
				"insert r_r -1 -20 3 4",
				"insert rec 7 -8 1 3",
				"insert virtual_rec0 1 1 0 0",
				"insert virtual_REC0 0 0 11 0",
				"insert inExistRec_0 1 1 -1 -2",
				"intersections",
				"dump",
				"search r_r",
				"remove r_r",
				"remove 1 1 0 0",
				"regionsearch -5 -5 20 20 "
		};
	}

	private String[][] getOutput() {
		// TODO Auto-generated method stub
		return new String[][]{
			{"Rectangle rejected:(r_r, -1, -20, 3, 4)"},
			{"Rectangle rejected:(rec, 7, -8, 1, 3)"},
			{"Rectangle rejected:(virtual_rec0, 1, 1, 0, 0)"},
			{"Rectangle rejected:(virtual_REC0, 0, 0, 11, 0)"},
			{"Rectangle rejected:(inExistRec_0, 1, 1, -1, -2)"},
			{"Intersections pairs:"},
			{"BST dump:", "Node has depth 0, Value (null)", "BST size is: 0"},
			{"Rectangle not found: r_r"},
			{"Rectangle rejected r_r"},
			{"Rectangle rejected (1, 1, 0, 0)"},
			{"Rectangles intersecting region (-5, -5, 20, 20):"}
		};
	}

	private String[] getParams() {
		// TODO Auto-generated method stub
		return new String[]{"SyntaxTest.txt"};
	}
//
//	public boolean containsNodeInfo(String actualOutputLine, String[] nodeInfo) throws Exception {
//		String fragment = nodeInfo[0];
//		String[] fragmentComponents = fragment.split("\\s");
//		String regx1 = fragmentComponents[0] + ".+" + fragmentComponents[1];
//		String regx2 = fragmentComponents[1] + ".+" + fragmentComponents[0];
//		String pattern = regx1 + "|" + regx2 ;
//		Pattern r = Pattern.compile(pattern);
//		Matcher m = r.matcher(actualOutputLine);
//		return m.find();
//	}

}
