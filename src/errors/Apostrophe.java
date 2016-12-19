package errors;

import util.UtilityMethods;

/**
 * WIP
 * Finds apostrophe errors. (8)
 * @author JeremiahDeGreeff
 */
public class Apostrophe extends Error {
	/**
	 * for testing purposes
	 */
	public static void main(String[] args) {
		UtilityMethods.setupOpenNLP();
		String input = "";
		System.out.println("\ninput: " + input + "\n");
		ErrorList errors = new Apostrophe().findErrors(input);
		errors.sort();
		errors.tokensToChars(0);
		System.out.println(errors);
	}
	
	/**
	 * constructor
	 */
	public Apostrophe() {
		super(8);
	}

	/**
	 * WIP
	 * @param line the paragraph in which to find errors
	 * @return an ErrorList of int[3] pointers to the indices of the start and end tokens of an error
	 * 			int[0], int[1] are start and end tokens of the error
	 * 			int[2] is the error number (8)
	 */
	@Override
	public ErrorList findErrors(String line) {
		ErrorList errors = new ErrorList(line, false);
		return errors;
	}
}
