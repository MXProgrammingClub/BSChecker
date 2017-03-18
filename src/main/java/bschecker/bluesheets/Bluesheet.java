package main.java.bschecker.bluesheets;

import java.util.ArrayList;

import main.java.bschecker.util.ErrorList;
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
	 * @return an ErrorList which for each error references start and end tokens, the bluesheet number (1 - 14), and, optionally, a note
	 */
	protected abstract ErrorList findErrors(String line);
	
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
	 * @return a ErrorList which contains all the errors in the passage, referenced by character indices
	 */
	public static ErrorList findAllErrors(String text) {
		long start = System.currentTimeMillis();
		ErrorList errors = new ErrorList(text, false);
		int lineNum = 1, charOffset = 0;
		String line;
		while (charOffset < text.length()) {
			long lineStart = System.currentTimeMillis();
			line = text.substring(charOffset, charOffset + text.substring(charOffset).indexOf('\n'));
			
			System.out.println("\nAnalyzing line " + lineNum + " (characters " + charOffset + "-" + (charOffset + line.length()) + "):");
			ArrayList<Integer> removedChars = new ArrayList<Integer>();
			line = UtilityMethods.removeExtraPunctuation(line, charOffset, removedChars);
			System.out.println("\tIgnoring characters: " + removedChars);
			
			ErrorList lineErrors = new ErrorList(line, true);
			for(Bluesheets b : Bluesheets.values())
				if(b.getBluesheetObj().CheckedWhenAnalyzed){
					long bluesheetStart = System.currentTimeMillis();
					System.out.print("\tlooking for: " + b.getName() + "... ");
					ErrorList temp = b.getBluesheetObj().findErrors(line);
					lineErrors.addAll(temp);
					System.out.println(temp.size() + (temp.size() == 1 ? " Error" : " Errors") + " Found (" + ((System.currentTimeMillis() - bluesheetStart) / 1000d) + "s)");
				}
			System.out.println(lineErrors.size() + (lineErrors.size() == 1 ? " Error" : " Errors") + " Found in line " + lineNum + " (" + ((System.currentTimeMillis() - lineStart) / 1000d) + "s)");
			lineErrors.sort();
			errors.addAll(lineErrors.tokensToChars(charOffset, removedChars));

			lineNum++;
			charOffset += line.length() + removedChars.size() + 1;
		}
		System.out.println("\n\nPassage analyzed in " + ((System.currentTimeMillis() - start) / 1000d) + "s\n\n" + errors);
		
		return errors;
	}
}
