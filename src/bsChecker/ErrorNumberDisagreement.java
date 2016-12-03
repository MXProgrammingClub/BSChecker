package bsChecker;

import java.util.ArrayList;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;

/**
 * WIP
 * Finds errors with verbs which don't agree in number with their subjects
 * and pronouns which don't agree in number with their antecedents. (5)
 * @author
 */
@SuppressWarnings("unused")
public class ErrorNumberDisagreement extends Error {
	/**
	 * for testing purposes
	 */
	public static void main(String[] args){
		Error.setupOpenNLP();
		String input = "They eat the man. It is delicious. It are enjoyable. To eat men is enjoyable.";
		System.out.println("\ninput: " + input + "\n");
		ArrayList<int[]> errors = new ErrorNumberDisagreement().findErrors(input);
		sort(errors);
		printErrors(tokensToChars(input, errors, 0), input);
	}
	
	/**
	 * constructor
	 */
	public ErrorNumberDisagreement() {
		super(5);
	}
	
	@Override
	public ArrayList<int[]> findErrors(String line){
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
