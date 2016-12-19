package errors;

import util.ErrorList;
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
		String input = "He said, \"I am happy.\"";
		System.out.println("\ninput: " + input + "\n");
		ErrorList errors = new FirstSecondPerson().findErrors(input);
		errors.sort();
		errors.tokensToChars(0);
		System.out.println(errors);
	}
	
	/**
	 * constructor
	 */
	public FirstSecondPerson() {
		super(3);
	}

	/**
	 * finds all instances of first or second person in the given paragraph
	 * @param line the paragraph in which to find errors
	 * @return an ErrorList of int[3] pointers to the indices of the start and end tokens of an error
	 * 			int[0], int[1] are start and end tokens of the error
	 * 			int[2] is the error number (3)
	 */
	@Override
	public ErrorList findErrors(String line) {
		String[] tokens = tokenizer.tokenize(line);
		
		boolean inQuote = false, inIntroducedQuote = false;
		ErrorList errors = new ErrorList(line, false);
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
