package BSChecker;

import java.util.ArrayList;

/**
 * @author Dalal
 * Finds (& print out locations of) first & second person -> BS error #3
 */
public class ErrorFirstSecondPerson extends Error {
	private static final int ERROR_NUMBER = 3;

	private static final String[] PRONOUNS = {"I","me", "my", "we", "us", "our", "you", "your"};

//	/**
//	 * for testing purposes
//	 */
//	public static void main (String[] args) {
//		Error.setupOpenNLP();
//		String test = "Hi. How are you? This my car.";
//		ArrayList<int[]> errors = new ErrorFirstSecondPerson().findErrors(test);
//		for (int[] error : errors)
//			System.out.println(test.substring(error[0], error[1]));//error[0] + " " + error[1] + " " + error[2]);
//	}

	/**
	 * finds all first & second person errors & returns indices
	 * note: does not yet check for if the pronoun is in quotations
	 * @param text block of text to check
	 * @return ArrayList of beginning & ending indices of errors
	 */
	@Override
	public ArrayList<int[]> findErrors(String text) {
		ArrayList<int[]> errors = new ArrayList<int[]>();
		String[] tokens = tokenizer.tokenize(text);
		int curPos=0;
		for(String token: tokens){
			if(contains(PRONOUNS,token)){
				int[] err = {text.indexOf(token,curPos), text.indexOf(token,curPos) + token.length(), ERROR_NUMBER};
				errors.add(err);
			}
			curPos = text.indexOf(token,curPos) + token.length();
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
