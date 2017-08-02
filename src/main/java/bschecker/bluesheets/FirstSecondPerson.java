package bschecker.bluesheets;

import bschecker.util.Error;
import bschecker.util.ErrorList;
import bschecker.util.Tools;
import bschecker.util.UtilityMethods;
import opennlp.tools.parser.Parse;

/**
 * Finds uses of first and second person. (3)
 * @author Dalal
 * @author JeremiahDeGreeff
 */
public class FirstSecondPerson extends Bluesheet {
	
	private static final String[] FIRST_PERSON = {"I","me", "my", "mine", "we", "us", "our", "ours"};
	private static final String[] SECOND_PERSON = {"you", "your", "yours"};
	
	
	/**
	 * Finds all instances of first or second person in a paragraph.
	 * @param line the paragraph in which to find errors
	 * @param parses a String array of the parses of each sentence of the line
	 * @return an ErrorList which for each Error references start token, end token, and, optionally, a note
	 */
	@Override
	protected ErrorList findErrors(String line, Parse[] parses) {
		String[] tokens = Tools.getTokenizer().tokenize(line);
		
		boolean inQuote = false, inIntroducedQuote = false;
		ErrorList errors = new ErrorList(line);
		for(int i = 0; i < tokens.length; i++) {
			if(tokens[i].contains("\"")) {
				inIntroducedQuote = (!inQuote && i > 0 && (tokens[i - 1].equals(",") || tokens[i - 1].equals(":"))) ? true : false;
				inQuote = !inQuote;
			}
			if(!inIntroducedQuote && UtilityMethods.arrayContains(FIRST_PERSON, tokens[i]))
				errors.add(new Error(i, "First Person"));
			if(!inIntroducedQuote && UtilityMethods.arrayContains(SECOND_PERSON, tokens[i]))
				errors.add(new Error(i, "Second Person"));
		}
		
		return errors;
	}
}
