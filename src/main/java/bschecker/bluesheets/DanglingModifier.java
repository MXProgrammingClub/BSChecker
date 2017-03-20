package main.java.bschecker.bluesheets;

import java.util.ArrayList;

import main.java.bschecker.util.Error;
import main.java.bschecker.util.ErrorList;
import main.java.bschecker.util.Tools;

/**
 * WIP
 * Finds dangling modifiers. (10)
 * @author JeremiahDeGreeff
 */
@SuppressWarnings("unused")
public class DanglingModifier extends Bluesheet {
	public final int ERROR_NUMBER = 10;
	
	/**
	 * for testing purposes
	 */
	public static void main(String[] args) {
		Tools.initializeOpenNLP();
		String input = "";
		System.out.println("\ninput: " + input + "\n");
		ErrorList errors = new DanglingModifier().findErrors(input);
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
	 * @param checkedWhenAnalyzed true if errors of this type should be looked for when the text is analyzed, false otherwise
	 */
	public DanglingModifier(boolean checkedWhenAnalyzed) {
		super(checkedWhenAnalyzed);
	}

	/**
	 * WIP
	 * @param line the paragraph in which to find errors
	 * @param parses a String array of the parses of each sentence of the line
	 * @return an ErrorList which for each error references start and end tokens, the bluesheet number (10), and, optionally, a note
	 */
	@Override
	protected ErrorList findErrors(String line, String[] parses) {
		ErrorList errors = new ErrorList(line, true);
		return errors;
	}
}
