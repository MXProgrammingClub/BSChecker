package main.java.bschecker.bluesheets;

import main.java.bschecker.util.Error;
import main.java.bschecker.util.ErrorList;
import main.java.bschecker.util.Tools;
import main.java.bschecker.util.UtilityMethods;

/**
 * Finds verbs in the passive voice. (9)
 * @author tedpyne
 * @author JeremiahDeGreeff
 */
public class PassiveVoice extends Bluesheet {
	
	public final int ERROR_NUMBER = 9;
	private static final String[] TO_BE_CONJ = {"be", "am", "is", "are", "was", "were", "been", "being"};
	
	
	/**
	 * finds all instances of passive voice in the given paragraph
	 * @param line the paragraph in which to find errors
	 * @param parses a String array of the parses of each sentence of the line
	 * @return an ErrorList which for each error references start and end tokens, the bluesheet number (9), and, optionally, a note
	 */
	@Override
	protected ErrorList findErrors(String line, String[] parses) {
		String tokens[] = Tools.getTokenizer().tokenize(line);
		String[] tags = Tools.getPOSTagger().tag(tokens);
		
		ErrorList errors = new ErrorList(line, true);
		boolean inQuote = false;
		for(int i = 1; i < tokens.length; i++){
			if(tokens[i].contains("\""))
				inQuote = !inQuote;
			if(!inQuote && UtilityMethods.arrayContains(TO_BE_CONJ, tokens[i]) && i < tokens.length - 1){
				int j = i + 1;
				while(tags[j].equals("RB") && j < tokens.length)
					j++;
				if(tags[j].equals("VBN"))
					errors.add(new Error(i, j, ERROR_NUMBER, true));
			}
		}
		return errors;
	}
	
}
