package bschecker.bluesheets;

import java.util.ArrayList;

import bschecker.util.ErrorList;
import bschecker.util.LogHelper;
import bschecker.util.Tools;
import bschecker.util.UtilityMethods;
import opennlp.tools.parser.Parse;

/**
 * Defines abstract class for types of grammatical errors
 * @author tedpyne
 * @author JeremiahDeGreeff
 */
public abstract class Bluesheet {
	
	/**
	 * Finds errors of a specific type in a paragraph.
	 * @param line the paragraph in which to find errors
	 * @param parses a String array of the parses of each sentence of the line
	 * @return an ErrorList which for each Error references start token, end token, and, optionally, a note
	 */
	protected abstract ErrorList findErrors(String line, Parse[] parses);
	
	/**
	 * Finds all errors within the given text.
	 * All types included in {@code ERROR_LIST} which have a {@code CheckedWhenAnalyzed} value of {@code true} will be checked.
	 * Assumes that text ends with a new line character.
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
			
			System.out.println();
			LogHelper.getLogger(17).info("Analyzing line " + lineNum + " (characters " + charOffset + "-" + (charOffset + line.length()) + "):");
			ArrayList<Integer> removedChars = new ArrayList<Integer>();
			line = UtilityMethods.removeExtraPunctuation(line, charOffset, removedChars);
			LogHelper.getLogger(17).info("Ignoring characters: " + removedChars);
			
			long parseStart = System.currentTimeMillis();
			LogHelper.getLogger(18).info("Parsing line " + lineNum + "...");
			String[] sentences = Tools.getSentenceDetector().sentDetect(line);
			Parse[] parses = new Parse[sentences.length];
			for(int i = 0; i < sentences.length; i++)
				parses[i] = UtilityMethods.parse(sentences[i]);
			LogHelper.getLogger(18).info("Complete (" + ((System.currentTimeMillis() - parseStart) / 1000d) + "s)");
			
			ErrorList lineErrors = new ErrorList(line, true);
			for(Bluesheets b : Bluesheets.values())
				if(Bluesheets.isSetToAnalyze(b.getNumber())) {
					long bluesheetStart = System.currentTimeMillis();
					LogHelper.getLogger(17).info("Looking for: " + b.getName() + "...");
					ErrorList temp = b.getObject().findErrors(line, parses);
					temp.setBluesheetNumber(b.getNumber());
					lineErrors.addAll(temp);
					LogHelper.getLogger(17).info(temp.size() + (temp.size() == 1 ? " Error" : " Errors") + " Found (" + ((System.currentTimeMillis() - bluesheetStart) / 1000d) + "s)");
				}
			LogHelper.getLogger(17).info(lineErrors.size() + (lineErrors.size() == 1 ? " Error" : " Errors") + " Found in line " + lineNum + " (" + ((System.currentTimeMillis() - lineStart) / 1000d) + "s)");
			
			
			errors.addAll(lineErrors.tokensToChars(charOffset, removedChars));
			
			lineNum++;
			charOffset += line.length() + removedChars.size() + 1;
		}
		System.out.println();
		LogHelper.getLogger(17).info("Passage analyzed in " + ((System.currentTimeMillis() - start) / 1000d) + "s\n\n" + errors);
		
		return errors;
	}
	
}
