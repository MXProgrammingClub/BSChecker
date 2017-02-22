package main.java.bschecker.bluesheets;

import java.util.ArrayList;

import main.java.bschecker.util.CharacterErrorList;
import main.java.bschecker.util.TokenErrorList;
import main.java.bschecker.util.UtilityMethods;

/**
 * Defines abstract class for types of grammatical errors
 * @author tedpyne
 * @author JeremiahDeGreeff
 */
public abstract class Bluesheet {
	private boolean CheckedWhenAnalyzed;
	
	/**
	 * creates a new Error object with the given error number
	 * @param CheckedWhenAnalyzed true if errors of the given type should be looked for when the text is analyzed, false otherwise
	 */
	public Bluesheet(boolean CheckedWhenAnalyzed) {
		this.CheckedWhenAnalyzed = CheckedWhenAnalyzed;
	}

	/**
	 * Finds errors of a specific type in the submitted text
	 * @param line the paragraph in which to find errors
	 * @return a TokenErrorList of int[3] elements where [0] and [1] are start and end tokens of the error and [2] is the error number (1 - 14)
	 */
	protected abstract TokenErrorList findErrors(String line);
	
	/**
	 * changes the value of CheckedWhenAnalyzed
	 */
	public void setCheckedWhenAnalyzed() {
		CheckedWhenAnalyzed = !CheckedWhenAnalyzed;
	}
	
	/**
	 * finds all errors within the given text
	 * all types included in ERROR_LIST which have an CheckedWhenAnalyzed value of true will be checked
	 * assumes that text ends with a new line character
	 * @param text the text to search
	 * @return a CharacterErrorList which contains all the errors in the passage
	 */
	public static CharacterErrorList findAllErrors(String text) {
		CharacterErrorList errors = new CharacterErrorList(text);
		int lineNum = 1, charOffset = 0;
		String line;
		while (charOffset < text.length()) {
			line = text.substring(charOffset, charOffset + text.substring(charOffset).indexOf('\n'));
			
			System.out.println("\nAnalyzing line " + lineNum + " (characters " + charOffset + "-" + (charOffset + line.length()) + "):");
			ArrayList<Integer> removedChars = new ArrayList<Integer>();
			line = UtilityMethods.removeExtraPunctuation(line, charOffset, removedChars);
			System.out.println("\tIgnoring characters: " + removedChars);
			
			TokenErrorList lineErrors = new TokenErrorList(line);
			for(Bluesheets b : Bluesheets.values())
				if(b.getBluesheetObj().CheckedWhenAnalyzed) {
					System.out.print("\tlooking for: " + b.getName() + "... ");
					TokenErrorList temp = b.getBluesheetObj().findErrors(line);
					System.out.println(temp.size() + (temp.size() == 1 ? " Error" : " Errors") + " Found");
					lineErrors.addAll(temp);
				}
			System.out.println(lineErrors.size() + (lineErrors.size() == 1 ? " Error" : " Errors") + " Found in line " + lineNum);
			lineErrors.sort();
			errors.addAll(lineErrors.tokensToChars(charOffset, removedChars));

			lineNum++;
			charOffset += line.length() + removedChars.size() + 1;
		}
		System.out.println("\n\n" + errors);
		
		return errors;
	}
}
