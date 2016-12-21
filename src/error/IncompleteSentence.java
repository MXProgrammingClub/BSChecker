package error;

import util.ErrorList;
import util.UtilityMethods;

/**
 * WIP
 * Finds sentence structure errors. (2)
 * @author JeremiahDeGreeff
 */
public class IncompleteSentence extends Error {
	/**
	 * for testing purposes
	 */
	public static void main(String[] args) {
		UtilityMethods.setupOpenNLP();
		String input = "";
		System.out.println("\ninput: " + input + "\n");
		ErrorList errors = new IncompleteSentence().findErrors(input);
		errors.sort();
		errors.tokensToChars(0);
		System.out.println(errors);
	}
	
	/**
	 * default constructor
	 */
	public IncompleteSentence() {
		this(true);
	}
	
	/**
	 * constructor
	 * @param isChecked true if errors of this type should be looked for when the text is analyzed, false otherwise
	 */
	public IncompleteSentence(boolean isChecked) {
		super(2, isChecked);
	}

	/**
	 * WIP
	 * @param line the paragraph in which to find errors
	 * @return an ErrorList of int[3] pointers to the indices of the start and end tokens of an error
	 * 			int[0], int[1] are start and end tokens of the error
	 * 			int[2] is the error number (2)
	 */
	@Override
	protected ErrorList findErrors(String line) {
		ErrorList errors = new ErrorList(line, false);
		return errors;
	}
}
