package bschecker.bluesheets;

import bschecker.util.ErrorList;
import opennlp.tools.parser.Parse;

/**
 * WIP
 * Finds dangling modifiers. (10)
 * @author JeremiahDeGreeff
 */
public class DanglingModifier extends Bluesheet {
	
	/**
	 * WIP
	 * @param line the paragraph in which to find errors
	 * @param parses a String array of the parses of each sentence of the line
	 * @return an ErrorList which for each error references start and end tokens, the bluesheet number (10), and, optionally, a note
	 */
	@Override
	protected ErrorList findErrors(String line, Parse[] parses) {
		ErrorList errors = new ErrorList(line);
		return errors;
	}
	
}
