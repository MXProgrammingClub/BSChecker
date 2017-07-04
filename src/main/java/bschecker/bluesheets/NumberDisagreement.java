package main.java.bschecker.bluesheets;

import java.util.ArrayList;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import main.java.bschecker.util.Error;
import main.java.bschecker.util.ErrorList;
import main.java.bschecker.util.SentenceTree;
import main.java.bschecker.util.Tools;
import main.java.bschecker.util.UtilityMethods;

/**
 * WIP
 * Finds errors with verbs which don't agree in number with their subjects
 * and pronouns which don't agree in number with their antecedents. (5)
 * @author
 */
@SuppressWarnings("unused")
public class NumberDisagreement extends Bluesheet {
	public final int ERROR_NUMBER = 5;
	
	/**
	 * for testing purposes
	 */
	public static void main(String[] args){
		Tools.initializeOpenNLP();
		String input = "";
		System.out.println("\ninput: " + input + "\n\n" + (new NumberDisagreement().findErrors(input)).tokensToChars(0, new ArrayList<Integer>()));
	}
	
	/**
	 * finds errors in number disagreement in the given paragraph
	 * @param line the paragraph in which to find errors
	 * @param parses a String array of the parses of each sentence of the line
	 * @return an ErrorList which for each error references start and end tokens, the bluesheet number (5), and, optionally, a note
	 */
	@Override
	protected ErrorList findErrors(String line, String[] parses){
//		String sentences[] = Tools.getSentenceDetector().sentDetect(line);
//		ArrayList<Parse> parses = new ArrayList<Parse>();
//		ArrayList<int[]> arr = new ArrayList<int[]>();
//		for(String s: sentences){
//			ParserTool.parseLine(s.substring(0, s.length()-1), Tools.getParser(), 1)[0].show();
//			correctParse(ParserTool.parseLine(s.substring(0,s.length()-1), Tools.getParser(), 1)[0]);
//			arr.addAll(correctParse(ParserTool.parseLine(s.substring(0,s.length()-1), UtilityMethods.getParser(), 1)[0]));
//		}
		return null;
	}
//	public static ArrayList<int[]> correctParse(Parse p){
//		System.out.println(p);
//		SentenceTree head = new SentenceTree(null,p.getChildren()[0]);
//		SentenceTree tree = head.fix();
//		return new ArrayList<int[]>();
//	}
}
