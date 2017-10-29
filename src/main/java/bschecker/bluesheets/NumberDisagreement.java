package bschecker.bluesheets;

import bschecker.util.ErrorList;
import opennlp.tools.parser.Parse;

/**
 * WIP
 * Finds errors with verbs which don't agree in number with their subjects and pronouns which don't agree in number with their antecedents. (5)
 * 
 * @author JeremiahDeGreeff
 */
public class NumberDisagreement extends Bluesheet {
	
	/**
	 * WIP
	 * Finds any number disagreement in a paragraph.
	 * 
	 * @param line the paragraph in which to find errors
	 * @param parses a Parse array of each sentence of the line
	 * @return an ErrorList which for each Error references start token, end token, and, optionally, a note
	 */
	@Override
	protected ErrorList findErrors(String line, Parse[] parses){
		ErrorList errors = new ErrorList(line);
		return errors;
	}
	
}
