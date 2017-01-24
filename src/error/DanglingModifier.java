package error;

import java.util.ArrayList;

import util.TokenErrorList;
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
		TokenErrorList errors = new DanglingModifier().findErrors(input);
		errors.sort();
		System.out.println(errors.tokensToChars(0, new ArrayList<Integer>()));
	}
	
	/**
	 * default constructor
	 */
	public DanglingModifier() {
		this(true);
	}
	
	/**
	 * constructor
	 * @param isChecked true if errors of this type should be looked for when the text is analyzed, false otherwise
	 */
	public DanglingModifier(boolean isChecked) {
		super(10, isChecked);
	}

	/**
	 * WIP
	 * @param line the paragraph in which to find errors
	 * @return a TokenErrorList of int[3] elements where [0] and [1] are start and end tokens of the error and [2] is the error number (10)
	 */
	@Override
	protected TokenErrorList findErrors(String line) {
		TokenErrorList errors = new TokenErrorList(line);
		return errors;
	}
}
