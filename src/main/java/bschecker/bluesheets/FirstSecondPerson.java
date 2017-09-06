package bschecker.bluesheets;

import bschecker.util.Error;
import bschecker.util.ErrorList;
import bschecker.util.Tools;
import bschecker.util.UtilityMethods;
import opennlp.tools.parser.Parse;

/**
 * Finds uses of first and second person. (3)
 * 
 * @author Dalal
 * @author JeremiahDeGreeff
 */
public class FirstSecondPerson extends Bluesheet {
	
	private static final String[] FIRST_PERSON = {"I","me", "my", "mine", "we", "us", "our", "ours"};
	private static final String[] SECOND_PERSON = {"you", "your", "yours"};
	
	
	/**
	 * Finds all instances of first or second person in a paragraph.
	 * 
	 * @param line the paragraph in which to find errors
	 * @param parses a Parse array of each sentence of the line
	 * @return an ErrorList which for each Error references start token, end token, and, optionally, a note
	 */
	@Override
	protected ErrorList findErrors(String line, Parse[] parses) {
		ErrorList errors = new ErrorList(line);
		int tokenOffset = 0;
		for(Parse parse : parses) {
			String sentence = parse.getText();
			ErrorList sentenceErrors = new ErrorList(sentence);
			String[] tokens = Tools.getTokenizer().tokenize(sentence);
			for(int i = 0; i < tokens.length; i++)
				if(UtilityMethods.arrayContains(FIRST_PERSON, tokens[i]))
					sentenceErrors.add(new Error(i, "First Person"));
				else if(UtilityMethods.arrayContains(SECOND_PERSON, tokens[i]))
					sentenceErrors.add(new Error(i, "Second Person"));
			UtilityMethods.removeErrorsInQuotes(sentenceErrors, parse, true);
			errors.addAllWithOffset(sentenceErrors, tokenOffset);
			tokenOffset += tokens.length;
		}
		return errors;
	}
}
