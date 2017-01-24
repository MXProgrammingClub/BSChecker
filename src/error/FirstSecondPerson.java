package error;

import java.util.ArrayList;

import util.TokenErrorList;
import util.UtilityMethods;

/**
 * Finds uses of first and second person. (3)
 * @author Dalal
 * @author JeremiahDeGreeff
 */
public class FirstSecondPerson extends Error {
	private static final String[] PRONOUNS = {"I","me", "my", "we", "us", "our", "you", "your"};

	/**
	 * for testing purposes
	 */
	public static void main (String[] args) {
		UtilityMethods.setupOpenNLP();
		String input = "";
		System.out.println("\ninput: " + input + "\n");
		TokenErrorList errors = new FirstSecondPerson().findErrors(input);
		errors.sort();
		System.out.println(errors.tokensToChars(0, new ArrayList<Integer>()));
	}
	
	/**
	 * default constructor
	 */
	public FirstSecondPerson() {
		this(true);
	}
	
	/**
	 * constructor
	 * @param CheckedWhenAnalyzed true if errors of this type should be looked for when the text is analyzed, false otherwise
	 */
	public FirstSecondPerson(boolean CheckedWhenAnalyzed) {
		super(3, CheckedWhenAnalyzed);
	}

	/**
	 * finds all instances of first or second person in the given paragraph
	 * @param line the paragraph in which to find errors
	 * @return a TokenErrorList of int[3] elements where [0] and [1] are start and end tokens of the error and [2] is the error number (3)
	 */
	@Override
	protected TokenErrorList findErrors(String line) {
		String[] tokens = UtilityMethods.getTokenizer().tokenize(line);
		
		boolean inQuote = false, inIntroducedQuote = false;
		TokenErrorList errors = new TokenErrorList(line);
		for(int i = 0; i < tokens.length; i++) {
			if(tokens[i].contains("\"")) {
				if(!inQuote && i > 0 && (tokens[i - 1].equals(",") || tokens[i - 1].equals(":")))
					inIntroducedQuote = true;
				else
					inIntroducedQuote = false;
				inQuote = !inQuote;
			}
			if(!inIntroducedQuote && UtilityMethods.arrayContains(PRONOUNS, tokens[i]))
				errors.add(new int[]{i, i, ERROR_NUMBER});
		}
		
		return errors;
	}
}
