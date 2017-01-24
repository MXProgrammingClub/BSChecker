package error;

import java.util.ArrayList;

import gui.Main;
import util.CharacterErrorList;
import util.TokenErrorList;
import util.UtilityMethods;

/**
 * Defines abstract class for types of grammatical errors
 * @author tedpyne
 * @author JeremiahDeGreeff
 */
public abstract class Error {
	public final int ERROR_NUMBER;
	private boolean CheckedWhenAnalyzed;
	
	/**
	 * creates a new Error object with the given error number
	 * @param errorNum the number (1 - 14) which represents this error
	 * @param CheckedWhenAnalyzed true if errors of the given type should be looked for when the text is analyzed, false otherwise
	 */
	public Error(int errorNum, boolean CheckedWhenAnalyzed) {
		ERROR_NUMBER = errorNum;
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
			
			System.out.println("\nAnalysing line " + lineNum + " (characters " + charOffset + "-" + (charOffset + line.length()) + "):");
			ArrayList<Integer> removedChars = new ArrayList<Integer>();
			line = UtilityMethods.removeExtraPunctuation(line, charOffset, removedChars);
			System.out.println("Ignoring characters: " + removedChars);
			
			TokenErrorList lineErrors = new TokenErrorList(line);
			for(Error e: Main.ERROR_LIST)
				if(e.CheckedWhenAnalyzed) {
					System.out.println("looking for: " + e.getClass());
					TokenErrorList temp = e.findErrors(line);
					lineErrors.addAll(temp);
				}
			lineErrors.sort();
			errors.addAll(lineErrors.tokensToChars(charOffset, removedChars));

			lineNum++;
			charOffset += line.length() + removedChars.size() + 1;
		}
		System.out.println("\n" + errors);
		return errors;
	}
}
