package bschecker.bluesheets;

import bschecker.util.Error;
import bschecker.util.ErrorList;
import bschecker.util.Tools;
import bschecker.util.UtilityMethods;
import opennlp.tools.parser.Parse;

/**
 * Finds verbs in the passive voice. (9)
 * @author tedpyne
 * @author JeremiahDeGreeff
 */
public class PassiveVoice extends Bluesheet {
	
	private static final String[] TO_BE_CONJ = {"be", "am", "is", "are", "was", "were", "been", "being"};
	
	
	/**
	 * Finds all instances of passive voice in a paragraph.
	 * @param line the paragraph in which to find errors
	 * @param parses a String array of the parses of each sentence of the line
	 * @return an ErrorList which for each Error references start token, end token, and, optionally, a note
	 */
	@Override
	protected ErrorList findErrors(String line, Parse[] parses) {
		String tokens[] = Tools.getTokenizer().tokenize(line);
		String[] tags = Tools.getPOSTagger().tag(tokens);
		
		ErrorList errors = new ErrorList(line);
		boolean inQuote = false;
		for(int i = 1; i < tokens.length; i++){
			if(tokens[i].contains("\""))
				inQuote = !inQuote;
			if(!inQuote && UtilityMethods.arrayContains(TO_BE_CONJ, tokens[i]) && i < tokens.length - 1){
				int j = i + 1;
				while(tags[j].equals("RB") && j < tokens.length)
					j++;
				if(tags[j].equals("VBN"))
					errors.add(new Error(i, j));
			}
		}
		return errors;
	}
	
}
