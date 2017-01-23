package error;

import gui.Main;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.parser.Parser;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.tokenize.Tokenizer;
import util.CharacterErrorList;
import util.TokenErrorList;

/**
 * Defines abstract class for types of grammatical errors
 * @author tedpyne
 * @author JeremiahDeGreeff
 */
public abstract class Error {
	public final int ERROR_NUMBER;
	private boolean isChecked;
	public static SentenceDetectorME sentenceDetector;
	public static Tokenizer tokenizer;
	public static NameFinderME nameFinder;
	public static POSTaggerME posTagger;
	public static Parser parser;
	
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
	 * @return a TokenErrorList of int[3] elements where [0] and [1] are start and end tokens of the error and [2] is the error number (1 - 14)
	 */
	protected abstract TokenErrorList findErrors(String line);
	
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
	public static CharacterErrorList findAllErrors(String text) {
		CharacterErrorList errors = new CharacterErrorList(text);
		int lineNum = 1, charOffset = 0;
		String line;
		while (charOffset < text.length()) {
			if(text.substring(charOffset).indexOf('\n') != -1)
				line = text.substring(charOffset, charOffset + text.substring(charOffset).indexOf('\n'));
			else
				line = text.substring(charOffset);
			System.out.println("\nAnalysing line " + lineNum + " (characters " + charOffset + "-" + (charOffset + line.length()) + "):");
			TokenErrorList lineErrors = new TokenErrorList(line);

			for(Error e: Main.ERROR_LIST)
				if(e.isChecked) {
					System.out.println("looking for: " + e.getClass());
					TokenErrorList temp = e.findErrors(line);
					lineErrors.addAll(temp);
				}
			lineErrors.sort();
			errors.addAll(lineErrors.tokensToChars(charOffset));

			lineNum++;
			charOffset += line.length() + 1;
		}

		System.out.println("\n" + errors);

		return errors;
	}
}
