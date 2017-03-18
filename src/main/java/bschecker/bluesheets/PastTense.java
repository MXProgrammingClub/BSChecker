package main.java.bschecker.bluesheets;

import java.util.ArrayList;

import main.java.bschecker.util.Error;
import main.java.bschecker.util.ErrorList;
import main.java.bschecker.util.Tools;

/**
 * Finds verbs in the past tense. (1)
 * @author Leo
 * @author tedpyne
 * @author JeremiahDeGreeff
 */
public class PastTense extends Bluesheet {
	public final int ERROR_NUMBER = 1;
	
	/**
	 * for testing purposes
	 */
	public static void main(String[] args) {
		Tools.initializeOpenNLP();
		String input = "";
		System.out.println("\ninput: " + input + "\n\n" + (new PastTense().findErrors(input)).tokensToChars(0, new ArrayList<Integer>()));
	}
	
	/**
	 * default constructor
	 */
	public PastTense() {
		this(true);
	}

	/**
	 * constructor
	 * @param CheckedWhenAnalyzed true if errors of this type should be looked for when the text is analyzed, false otherwise
	 */
	public PastTense(boolean CheckedWhenAnalyzed) {
		super(CheckedWhenAnalyzed);
	}
	
	/**
	 * finds all instances of past tense in the given paragraph
	 * @param line the paragraph in which to find errors
	 * @param parses a String array of the parses of each sentence of the line
	 * @return an ErrorList which for each error references start and end tokens, the bluesheet number (1), and, optionally, a note
	 */
	@Override
	protected ErrorList findErrors(String line, String[] parses) {
		String tokens[] = Tools.getTokenizer().tokenize(line);
		String[] tags = Tools.getPOSTagger().tag(tokens);

		boolean inQuote = false, inIntroducedQuote = false;
		ErrorList errors = new ErrorList(line, true);
		for(int i = 0; i < tags.length; i++) {
			if(tokens[i].contains("\"")) {
				inIntroducedQuote = (!inQuote && i > 0 && (tokens[i - 1].equals(",") || tokens[i - 1].equals(":"))) ? true : false;
				inQuote = !inQuote;
			}
			if(!inIntroducedQuote && tags[i].equals("VBD"))
				errors.add(new Error(i, ERROR_NUMBER, true));
		}
		
		return errors;
	}
}