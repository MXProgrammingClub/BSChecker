package bsChecker;

import java.util.ArrayList;

/**
 * @author Dalal
 * Finds uses of first and second person. (3)
 */
public class ErrorFirstSecondPerson extends Error {
	private static final int ERROR_NUMBER = 3;
	private static final String[] PRONOUNS = {"I","me", "my", "we", "us", "our", "you", "your"};

	/**
	 * for testing purposes
	 */
	public static void main (String[] args) {
		Error.setupOpenNLP();
		String input = "Hi. How are you? This my car.";
		System.out.println("\ninput: " + input + "\n");
		ArrayList<int[]> errors = new ErrorFirstSecondPerson().findErrors(input);
		sort(errors);
		printErrors(tokensToChars(input, errors, 0), input);
	}

	/**
	 * finds all instances of first or second person in the given paragraph
	 * note: does not check to see if the pronoun is in quotations
	 * @param line paragraph to check
	 * @return ArrayList int[3] representing errors where [0] is the beginning token index, [1] is ending token index, [2] is the type of error (3)
	 */
	@Override
	public ArrayList<int[]> findErrors(String line) {
		String[] tokens = tokenizer.tokenize(line);
		
		ArrayList<int[]> errors = new ArrayList<int[]>();
		for(int i = 0; i < tokens.length; i++)
			if(arrayContains(PRONOUNS, tokens[i]))
				errors.add(new int[]{i, i, ERROR_NUMBER});
		
		return errors;
	}
}
