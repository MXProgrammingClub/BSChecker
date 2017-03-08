package main.java.bschecker.bluesheets;

import java.util.ArrayList;

import main.java.bschecker.util.Error;
import main.java.bschecker.util.ErrorList;
import main.java.bschecker.util.Tools;
import main.java.bschecker.util.UtilityMethods;

/**
 * WIP
 * Finds sentence structure errors. (2)
 * @author JeremiahDeGreeff
 */
@SuppressWarnings("unused")
public class IncompleteSentence extends Bluesheet {
	public final int ERROR_NUMBER = 2;
	
	/**
	 * for testing purposes
	 */
	public static void main(String[] args) {
		Tools.initializeOpenNLP();
		String input = "";
		System.out.println("\ninput: " + input + "\n\n" + (new IncompleteSentence().findErrors(input)).tokensToChars(0, new ArrayList<Integer>()));
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
	 * @return an ErrorList which for each error references start and end tokens, the bluesheet number (1 - 14), and, optionally, a note
	 */
	@Override
	protected ErrorList findErrors(String line) {
		ErrorList errors = new ErrorList(line, true);
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
	 * @return an ErrorList which for each error in this sentence references start and end tokens, the bluesheet number (11), and, optionally, a note
	 */
	private ErrorList findErrorsInSentence(String sentence, int tokenOffset) {
		ErrorList errors = new ErrorList(sentence, true);
		String parsedText = UtilityMethods.parse(sentence);
		System.out.println("\n" + parsedText);
		
		
		return errors;
	}
}
