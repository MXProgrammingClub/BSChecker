package bschecker.bluesheets;

import bschecker.util.Error;
import bschecker.util.ErrorList;
import bschecker.util.Tools;
import bschecker.util.UtilityMethods;
import opennlp.tools.parser.Parse;

/**
 * Finds verbs in progressive tense. (12)
 * @author JeremiahDeGreeff
 */
public class ProgressiveTense extends Bluesheet {
	
	private static final String[] TO_BE_CONJ = {"be", "am", "is", "are", "was", "were", "been"};
	
	
	/**
	 * Finds all instances of progressive tense in a paragraph.
	 * @param line the paragraph in which to find errors
	 * @param parses a Parse array of each sentence of the line
	 * @return an ErrorList which for each Error references start token, end token, and, optionally, a note
	 */
	@Override
	protected ErrorList findErrors(String line, Parse[] parses) {
		String[] tokens = Tools.getTokenizer().tokenize(line);
		String[] tags = Tools.getPOSTagger().tag(tokens);
		
		ErrorList errors = new ErrorList(line);
		boolean inQuote = false, inIntroducedQuote = false;
		for(int i = 1; i < tokens.length; i++) {
			if(tokens[i].contains("\"")) {
				inIntroducedQuote = !inQuote && i > 0 && (tokens[i - 1].equals(",") || tokens[i - 1].equals(":"));
				inQuote = !inQuote;
			}
			if(!inIntroducedQuote && UtilityMethods.arrayContains(TO_BE_CONJ, tokens[i]) && i != tokens.length - 1) {
				int j = i + 1;
				while(tags[j].equals("RB") && j < tokens.length) j++;
				if(tags[j].equals("VBG"))
					errors.add(new Error(i, j));
			}
		}
		return errors;
	}
	
}
