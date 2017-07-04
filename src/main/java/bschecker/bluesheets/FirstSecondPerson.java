package main.java.bschecker.bluesheets;

import java.util.ArrayList;

import main.java.bschecker.util.Error;
import main.java.bschecker.util.ErrorList;
import main.java.bschecker.util.Tools;
import main.java.bschecker.util.UtilityMethods;

/**
 * Finds uses of first and second person. (3)
 * @author Dalal
 * @author JeremiahDeGreeff
 */
public class FirstSecondPerson extends Bluesheet {
	public final int ERROR_NUMBER = 3;
	private static final String[] FIRST_PERSON = {"I","me", "my", "mine", "we", "us", "our", "ours"};
	private static final String[] SECOND_PERSON = {"you", "your", "yours"};

	/**
	 * for testing purposes
	 */
	public static void main (String[] args) {
		Tools.initializeOpenNLP();
		String input = "";
		System.out.println("\ninput: " + input + "\n\n" + (new FirstSecondPerson().findErrors(input)).tokensToChars(0, new ArrayList<Integer>()));
	}

	/**
	 * finds all instances of first or second person in the given paragraph
	 * @param line the paragraph in which to find errors
	 * @param parses a String array of the parses of each sentence of the line
	 * @return an ErrorList which for each error references start and end tokens, the bluesheet number (3), and, optionally, a note
	 */
	@Override
	protected ErrorList findErrors(String line, String[] parses) {
		String[] tokens = Tools.getTokenizer().tokenize(line);
		
		boolean inQuote = false, inIntroducedQuote = false;
		ErrorList errors = new ErrorList(line, true);
		for(int i = 0; i < tokens.length; i++) {
			if(tokens[i].contains("\"")) {
				inIntroducedQuote = (!inQuote && i > 0 && (tokens[i - 1].equals(",") || tokens[i - 1].equals(":"))) ? true : false;
				inQuote = !inQuote;
			}
			if(!inIntroducedQuote && UtilityMethods.arrayContains(FIRST_PERSON, tokens[i]))
				errors.add(new Error(i, ERROR_NUMBER, true, "First Person"));
			if(!inIntroducedQuote && UtilityMethods.arrayContains(SECOND_PERSON, tokens[i]))
				errors.add(new Error(i, ERROR_NUMBER, true, "Second Person"));
		}
		
		return errors;
	}
}
