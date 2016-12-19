package error;

import gui.Main;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.parser.Parser;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.tokenize.Tokenizer;
import util.ErrorList;
import util.UtilityMethods;

/**
 * Defines abstract class for types of grammatical errors
 * @author tedpyne
 * @author JeremiahDeGreeff
 */
public abstract class Error {
	public final int ERROR_NUMBER;
	public boolean isChecked;
	public static SentenceDetectorME sentenceDetector;
	public static Tokenizer tokenizer;
	public static NameFinderME nameFinder;
	public static POSTaggerME posTagger;
	public static Parser parser;
	
	/**
	 * for testing purposes
	 */
	public static void main(String[] args) {
		UtilityMethods.setupOpenNLP();
		String input = "I walk. The ball is round. He says: \"Hello!\"";
		ErrorList errors = new ErrorList(input, false);
		//not real errors - just for testing
		int[] error1 = {6, 12, 1};
		int[] error2 = {2, 4, 2};
		int[] error3 = {0, 1, 3};
		errors.add(error1);
		errors.add(error2);
		errors.add(error3);
		errors.sort();
		System.out.println(errors);
		errors.tokensToChars(0);
		System.out.println(errors);
	}
	
	/**
	 * creates a new Error object with the given error number
	 * @param errorNum the number (1 - 14) which represents this error
	 * @param isChecked true if errors of the given type should be looked for when the text is analyzed, false otherwise
	 */
	public Error(int errorNum, boolean isChecked) {
		ERROR_NUMBER = errorNum;
		this.isChecked = isChecked;
	}

	/**
	 * Finds errors of a specific type in the submitted text
	 * @param line the paragraph in which to find errors
	 * @return an ErrorList of int[3] pointers to the indices of the start and end tokens of an error
	 * 			int[0], int[1] are start and end tokens of the error
	 * 			int[2] is the error number (1 - 14)
	 */
	public abstract ErrorList findErrors(String line);
	
	/**
	 * changes the value of isChecked
	 */
	public void setIsChecked() {
		isChecked = !isChecked;
	}
	
	/**
	 * finds all errors within the given text
	 * all types included in ERROR_LIST who have an isChecked value of true will be checked
	 * @param text the text to search
	 * @return an ErrorList which contains all the errors in the passage
	 */
	public static ErrorList findAllErrors(String text) {
		ErrorList errors = new ErrorList(text, true);
		int lineNum = 1, charOffset = 0;
		String line;
		while (charOffset < text.length()) {
			if(text.substring(charOffset).indexOf('\n') != -1)
				line = text.substring(charOffset, charOffset + text.substring(charOffset).indexOf('\n'));
			else
				line = text.substring(charOffset);
			System.out.println("\nAnalysing line " + lineNum + " (characters " + charOffset + "-" + (charOffset + line.length()) + "):");
			ErrorList lineErrors = new ErrorList(line, false);

			for(Error e: Main.ERROR_LIST) {
				if(e.isChecked) {
					System.out.println("looking for: " + e.getClass());
					ErrorList temp = e.findErrors(line);
					lineErrors.addAll(temp);
				}
			}
			lineErrors.sort();
			lineErrors.tokensToChars(charOffset);
			errors.addAll(lineErrors);

			lineNum++;
			charOffset += line.length() + 1;
		}

		System.out.println("\n" + errors);

		return errors;
	}
}
