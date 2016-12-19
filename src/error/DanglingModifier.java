package error;

import util.ErrorList;
import util.UtilityMethods;

/**
 * WIP
 * Finds dangling modifiers. (10)
 * @author JeremiahDeGreeff
 */
public class DanglingModifier extends Error {
	/**
	 * for testing purposes
	 */
	public static void main(String[] args) {
		UtilityMethods.setupOpenNLP();
		String input = "";
		System.out.println("\ninput: " + input + "\n");
		ErrorList errors = new DanglingModifier().findErrors(input);
		errors.sort();
		errors.tokensToChars(0);
		System.out.println(errors);
	}
	
	/**
	 * constructor
	 */
	public DanglingModifier() {
		super(10);
	}

	/**
	 * WIP
	 * @param line the paragraph in which to find errors
	 * @return an ErrorList of int[3] pointers to the indices of the start and end tokens of an error
	 * 			int[0], int[1] are start and end tokens of the error
	 * 			int[2] is the error number (10)
	 */
	@Override
	public ErrorList findErrors(String line) {
		ErrorList errors = new ErrorList(line, false);
		return errors;
	}
}
