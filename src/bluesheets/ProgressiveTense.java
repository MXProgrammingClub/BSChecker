package bluesheets;

import java.util.ArrayList;

import util.TokenErrorList;
import util.Tools;
import util.UtilityMethods;

/**
 * Finds verbs in progressive tense. (12)
 * @author JeremiahDeGreeff
 */
public class ProgressiveTense extends Bluesheet {
	public final int ERROR_NUMBER = 12;
	private static final String[] TO_BE_CONJ = {"be", "am", "is", "are", "was", "were", "been"};

	/**
	 * for testing purposes
	 */
	public static void main(String[] args) {
		Tools.initializeOpenNLP();
		String input = "";
		System.out.println("\ninput: " + input + "\n");
		TokenErrorList errors = new ProgressiveTense().findErrors(input);
		errors.sort();
		System.out.println(errors.tokensToChars(0, new ArrayList<Integer>()));
	}
	
	/**
	 * default constructor
	 */
	public ProgressiveTense() {
		this(true);
	}
	
	/**
	 * constructor
	 * @param CheckedWhenAnalyzed true if errors of this type should be looked for when the text is analyzed, false otherwise
	 */
	public ProgressiveTense(boolean CheckedWhenAnalyzed) {
		super(CheckedWhenAnalyzed);
	}

	/**
	 * finds all instances of progressive tense in the given paragraph
	 * @param line the paragraph in which to find errors
	 * @return a TokenErrorList of int[3] elements where [0] and [1] are start and end tokens of the error and [2] is the error number (12)
	 */
	@Override
	protected TokenErrorList findErrors(String line) {
		String[] tokens = Tools.getTokenizer().tokenize(line);
		String[] tags = Tools.getPOSTagger().tag(tokens);
		
		TokenErrorList errors = new TokenErrorList(line);
		for(int i = 1; i < tokens.length; i++)
			if(UtilityMethods.arrayContains(TO_BE_CONJ, tokens[i]) && i != tokens.length-1){
				int j = i+1;
				while(tags[j].equals("RB") && j < tokens.length) j++;
				if(tags[j].equals("VBG")){
					errors.add(new int[]{i, j, ERROR_NUMBER});
				}
			}
		return errors;
	}
}
