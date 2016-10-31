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
		printErrors(new ErrorFirstSecondPerson().findErrors(input), input);
	}

	/**
	 * finds all first & second person errors & returns indices
	 * note: does not yet check for if the pronoun is in quotations
	 * @param line paragraph to check
	 * @return ArrayList of beginning & ending indices of errors
	 */
	@Override
	public ArrayList<int[]> findErrors(String line) {
		ArrayList<int[]> errors = new ArrayList<int[]>();
		String[] tokens = tokenizer.tokenize(line);
		int curPos=0;
		for(String token: tokens){
			if(contains(PRONOUNS,token)){
				int[] err = {line.indexOf(token,curPos), line.indexOf(token,curPos) + token.length(), ERROR_NUMBER};
				errors.add(err);
			}
			curPos = line.indexOf(token,curPos) + token.length();
		}	
		return errors;
	}

	private boolean contains(String[] tenses, String token) {
		for(String tense: tenses){
			if(tense.equalsIgnoreCase(token)) return true;
		}
		return false;
	}
}
