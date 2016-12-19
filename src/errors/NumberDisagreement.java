package errors;

import java.util.ArrayList;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import util.ErrorList;
import util.SentenceTree;
import util.UtilityMethods;

/**
 * WIP
 * Finds errors with verbs which don't agree in number with their subjects
 * and pronouns which don't agree in number with their antecedents. (5)
 * @author
 */
@SuppressWarnings("unused")
public class NumberDisagreement extends Error {
	/**
	 * for testing purposes
	 */
	public static void main(String[] args){
		UtilityMethods.setupOpenNLP();
		String input = "They eat the man. It is delicious. It are enjoyable. To eat men is enjoyable.";
		System.out.println("\ninput: " + input + "\n");
		ErrorList errors = new NumberDisagreement().findErrors(input);
		errors.sort();
		errors.tokensToChars(0);
		System.out.println(errors);
	}
	
	/**
	 * constructor
	 */
	public NumberDisagreement() {
		super(5);
	}
	/**
	 * finds errors in number disagreement in the given paragraph
	 * @param line the paragraph in which to find errors
	 * @return an ErrorList of int[3] pointers to the indices of the start and end tokens of an error
	 * 			int[0], int[1] are start and end tokens of the error
	 * 			int[2] is the error number (1 - 14)
	 */
	@Override
	public ErrorList findErrors(String line){
		String sentences[] = sentenceDetector.sentDetect(line);
		ArrayList<Parse> parses = new ArrayList<Parse>();
		ArrayList<int[]> arr = new ArrayList<int[]>();
		for(String s: sentences){
			ParserTool.parseLine(s.substring(0, s.length()-1), parser, 1)[0].show();
			correctParse(ParserTool.parseLine(s.substring(0,s.length()-1), parser, 1)[0]);
//			arr.addAll(correctParse(ParserTool.parseLine(s.substring(0,s.length()-1), parser, 1)[0]));
		}
		return null;
	}
	public static ArrayList<int[]> correctParse(Parse p){
//		System.out.println(p);
		SentenceTree head = new SentenceTree(null,p.getChildren()[0]);
		SentenceTree tree = head.fix();
		return new ArrayList<int[]>();
	}
}
