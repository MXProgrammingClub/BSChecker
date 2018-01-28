package bschecker.bluesheets;

import org.apache.logging.log4j.Logger;

import bschecker.util.ErrorList;
import bschecker.util.LogHelper;
import opennlp.tools.parser.Parse;

/**
 * Defines abstract class for types of grammatical errors.
 * 
 * @author tedpyne
 * @author JeremiahDeGreeff
 * @see Bluesheets
 */
public abstract class Bluesheet {
	
	/**
	 * Finds errors of a specific type in a paragraph.
	 * 
	 * @param line the paragraph in which to find errors
	 * @param parses a Parse array of each sentence of the line
	 * @return an ErrorList which for each Error references start token, end token, and, optionally, a note
	 */
	public abstract ErrorList findErrors(String line, Parse[] parses);
	
	/**
	 * @return the logger for this Bluesheet
	 */
	protected Logger getLogger() {
		return LogHelper.getLogger(Bluesheets.getNumber(this));
	}
	
}
