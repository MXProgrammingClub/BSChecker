package main.java.bschecker.bluesheets;

import java.util.ArrayList;

import main.java.bschecker.util.TokenErrorList;
import main.java.bschecker.util.Tools;
import main.java.bschecker.util.UtilityMethods;

/**
 * WIP
 * Finds sentence structure errors. (2)
 * @author JeremiahDeGreeff
 */
public class IncompleteSentence extends Bluesheet {
	public final int ERROR_NUMBER = 2;
	
	/**
	 * for testing purposes
	 */
	public static void main(String[] args) {
		Tools.initializeOpenNLP();
		String input = "";
		System.out.println("\ninput: " + input + "\n");
		TokenErrorList errors = new IncompleteSentence().findErrors(input);
		errors.sort();
		System.out.println(errors.tokensToChars(0, new ArrayList<Integer>()));
	}
	
	/**
	 * default constructor
	 */
	public IncompleteSentence() {
		this(true);
	}
	
	/**
	 * constructor
	 * @param CheckedWhenAnalyzed true if errors of this type should be looked for when the text is analyzed, false otherwise
	 */
	public IncompleteSentence(boolean CheckedWhenAnalyzed) {
		super(CheckedWhenAnalyzed);
	}

	/**
	 * @param line the paragraph in which to find errors
	 * @return a TokenErrorList of int[3] elements where [0] and [1] are start and end tokens of the error and [2] is the error number (2)
	 */
	@Override
	protected TokenErrorList findErrors(String line) {
		TokenErrorList errors = new TokenErrorList(line);
		String[] sentences = Tools.getSentenceDetector().sentDetect(line);
		int tokenOffset = 0;
		for(String sentence : sentences){
			errors.addAll(findErrorsInSentence(sentence, tokenOffset));
			tokenOffset += Tools.getTokenizer().tokenize(sentence).length;
		}
		return errors;
	}
	
	/**
	 * WIP
	 * finds any issues in the structure of a sentence
	 * @param sentence the sentence to examine
	 * @param tokenOffset the number of tokens which have occurred in earlier sentences (for returning purposes)
	 * @return a TokenErrorList of int[3] elements where [0] and [1] are start and end tokens of the error and [2] is the error number (2) which represents all the errors in this sentence
	 */
	private TokenErrorList findErrorsInSentence(String sentence, int tokenOffset) {
		TokenErrorList errors = new TokenErrorList(sentence);
		String parsedText = UtilityMethods.parse(sentence);
		System.out.println("\n" + parsedText);
		
		
		return errors;
	}
}
