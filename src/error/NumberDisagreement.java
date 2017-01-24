package error;

import java.util.ArrayList;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import util.SentenceTree;
import util.TokenErrorList;
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
		TokenErrorList errors = new NumberDisagreement().findErrors(input);
		errors.sort();
		System.out.println(errors.tokensToChars(0, new ArrayList<Integer>()));
	}
	
	/**
	 * default constructor
	 */
	public NumberDisagreement() {
		this(true);
	}
	
	/**
	 * constructor
	 * @param CheckedWhenAnalyzed true if errors of this type should be looked for when the text is analyzed, false otherwise
	 */
	public NumberDisagreement(boolean CheckedWhenAnalyzed) {
		super(5, CheckedWhenAnalyzed);
	}
	
	/**
	 * finds errors in number disagreement in the given paragraph
	 * @param line the paragraph in which to find errors
	 * @return a TokenErrorList of int[3] elements where [0] and [1] are start and end tokens of the error and [2] is the error number (5)
	 */
	@Override
	protected TokenErrorList findErrors(String line){
		String sentences[] = UtilityMethods.getSentenceDetector().sentDetect(line);
		ArrayList<Parse> parses = new ArrayList<Parse>();
		ArrayList<int[]> arr = new ArrayList<int[]>();
		for(String s: sentences){
			ParserTool.parseLine(s.substring(0, s.length()-1), UtilityMethods.getParser(), 1)[0].show();
			correctParse(ParserTool.parseLine(s.substring(0,s.length()-1), UtilityMethods.getParser(), 1)[0]);
//			arr.addAll(correctParse(ParserTool.parseLine(s.substring(0,s.length()-1), UtilityMethods.getParser(), 1)[0]));
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
