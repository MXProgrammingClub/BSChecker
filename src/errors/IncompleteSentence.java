package errors;

import java.util.ArrayList;

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
		ArrayList<int[]> errors = new IncompleteSentence().findErrors(input);
		sort(errors);
		printErrors(tokensToChars(input, errors, 0), input);
	}
	
	/**
	 * constructor
	 */
	public IncompleteSentence() {
		super(2);
	}

	/**
	 * WIP
	 * @param line paragraph to check
	 * @return ArrayList int[3] representing errors where [0] is the beginning token index, [1] is ending token index, [2] is the type of error (12)
	 */
	@Override
	public ArrayList<int[]> findErrors(String line) {
		ArrayList<int[]> errors = new ArrayList<int[]>();
		return errors;
	}
}
