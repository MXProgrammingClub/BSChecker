package bschecker.bluesheets;

import java.util.ArrayList;

import bschecker.util.ErrorList;
import bschecker.util.LogHelper;
import bschecker.util.PerformanceMonitor;
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
	 * @param parses a Parse array of each sentence of the line
	 * @return an ErrorList which for each Error references start token, end token, and, optionally, a note
	 */
	protected abstract ErrorList findErrors(String line, Parse[] parses);
	
	/**
	 * Finds all errors within the given text.
	 * All types included in {@code ERROR_LIST} which have a {@code CheckedWhenAnalyzed} value of {@code true} will be checked.
	 * Expects text to end with a new line character.
	 * @param text the text to search
	 * @param logParses if true, all Parse trees will be logged to the console - should only be used for debugging
	 * @return a ErrorList which contains all the errors in the passage, referenced by character indices
	 */
	public static ErrorList findAllErrors(String text, boolean logParses) {
		PerformanceMonitor.start("analyze");
		if(!text.endsWith("\n"))
			text += "\n";
		ErrorList errors = new ErrorList(text, false);
		int lineNum = 1, charOffset = 0;
		String line;
		while (charOffset < text.length()) {
			PerformanceMonitor.start("line");
			line = text.substring(charOffset, charOffset + text.substring(charOffset).indexOf('\n'));
			System.out.println();
			LogHelper.getLogger(17).info("Analyzing line " + lineNum + " (characters " + charOffset + "-" + (charOffset + line.length()) + "):");
			ArrayList<Integer> removedChars = new ArrayList<Integer>();
			line = UtilityMethods.removeExtraPunctuation(line, charOffset, removedChars);
			LogHelper.getLogger(17).info("Ignoring characters: " + removedChars);
			
			PerformanceMonitor.start("parse");
			LogHelper.getLogger(18).info("Parsing line " + lineNum + "...");
			String[] sentences = UtilityMethods.separateSentences(line);
			Parse[] parses = new Parse[sentences.length];
			for(int i = 0; i < sentences.length; i++)
				parses[i] = UtilityMethods.parse(sentences[i], logParses);
			LogHelper.getLogger(18).info("Complete (" + PerformanceMonitor.stop("parse") + ")");
			
			ErrorList lineErrors = new ErrorList(line, true);
			for(Bluesheets b : Bluesheets.values())
				if(Bluesheets.isSetToAnalyze(b.getNumber())) {
					PerformanceMonitor.start("bluesheet");
					LogHelper.getLogger(17).info("Looking for: " + b.getName() + "...");
					ErrorList temp = b.getObject().findErrors(line, parses);
					temp.setBluesheetNumber(b.getNumber());
					lineErrors.addAll(temp);
					LogHelper.getLogger(17).info(temp.size() + " Error" + (temp.size() == 1 ? "" : "s") + " Found (" + PerformanceMonitor.stop("bluesheet") + ")");
				}
			LogHelper.getLogger(17).info(lineErrors.size() + " Error" + (lineErrors.size() == 1 ? "" : "s") + " Found in line " + lineNum + " (" + PerformanceMonitor.stop("line") + ")");
			
			errors.addAll(lineErrors.tokensToChars(charOffset, removedChars));
			
			lineNum++;
			charOffset += line.length() + removedChars.size() + 1;
		}
		System.out.println();
		LogHelper.getLogger(17).info("Passage analyzed in " + PerformanceMonitor.stop("analyze") + "\n\n" + errors);
		
		return errors;
	}
	
}
