package main.java.bschecker.bluesheets;

import java.util.ArrayList;

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
	 * for testing purposes
	 */
	public static void main(String[] args){
		Tools.initializeOpenNLP();
		String input = "";
		System.out.println("\ninput: " + input + "\n\n" + (new PassiveVoice().findErrors(input)).tokensToChars(0, new ArrayList<Integer>()));
	}
	
	/**
	 * default constructor
	 */
	public PassiveVoice() {
		this(true);
	}
	
	/**
	 * constructor
	 * @param checkedWhenAnalyzed true if errors of this type should be looked for when the text is analyzed, false otherwise
	 */
	public PassiveVoice(boolean checkedWhenAnalyzed) {
		super(checkedWhenAnalyzed);
	}

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
		for(int i = 1; i < tokens.length; i++)
			if(UtilityMethods.arrayContains(TO_BE_CONJ, tokens[i]) && i < tokens.length-1){
				int j = i+1;
				while(tags[j].equals("RB") && j < tokens.length) j++;
				if(tags[j].equals("VBN")){
					errors.add(new Error(i, j, ERROR_NUMBER, true));
				}
			}
		return errors;
	}
}
