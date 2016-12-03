package bsChecker;

import java.util.ArrayList;

/**
 * Finds uses of first and second person. (3)
 * @author Dalal
 * @author JeremiahDeGreeff
 */
public class ErrorFirstSecondPerson extends Error {
	private static final String[] PRONOUNS = {"I","me", "my", "we", "us", "our", "you", "your"};

	/**
	 * for testing purposes
	 */
	public static void main (String[] args) {
		Error.setupOpenNLP();
		String input = "He said, \"I am happy.\"";
		System.out.println("\ninput: " + input + "\n");
		ArrayList<int[]> errors = new ErrorFirstSecondPerson().findErrors(input);
		sort(errors);
		printErrors(tokensToChars(input, errors, 0), input);
	}
	
	/**
	 * constructor
	 */
	public ErrorFirstSecondPerson() {
		super(3);
	}

	/**
	 * finds all instances of first or second person in the given paragraph
	 * @param line paragraph to check
	 * @return ArrayList int[3] representing errors where [0] is the beginning token index, [1] is ending token index, [2] is the type of error (3)
	 */
	@Override
	public ArrayList<int[]> findErrors(String line) {
		String[] tokens = tokenizer.tokenize(line);
		
		boolean inQuote = false, inIntroducedQuote = false;
		ArrayList<int[]> errors = new ArrayList<int[]>();
		for(int i = 0; i < tokens.length; i++) {
			if(tokens[i].contains("\"")) {
				if(!inQuote && i > 0 && (tokens[i - 1].equals(",") || tokens[i - 1].equals(":")))
					inIntroducedQuote = true;
				else
					inIntroducedQuote = false;
				inQuote = !inQuote;
			}
			if(!inIntroducedQuote && arrayContains(PRONOUNS, tokens[i]))
				errors.add(new int[]{i, i, ERROR_NUMBER});
		}
		
		return errors;
	}
}
