package bluesheets;

import java.util.ArrayList;

import util.TokenErrorList;
import util.Tools;
//import util.UtilityMethods;

/**
 * Finds verbs in the past tense. (1)
 * @author Leo
 * @author tedpyne
 * @author JeremiahDeGreeff
 */
public class PastTense extends Bluesheet {
	public final int ERROR_NUMBER = 1;
	//private static final String[] TO_HAVE_CONJ = {"have", "has", "had", "having"};
	
	/**
	 * for testing purposes
	 */
	public static void main(String[] args) {
		Tools.initializeOpenNLP();
		String input = "";
		System.out.println("\ninput: " + input + "\n");
		TokenErrorList errors = new PastTense().findErrors(input);
		errors.sort();
		System.out.println(errors.tokensToChars(0, new ArrayList<Integer>()));
	}
	
	/**
	 * default constructor
	 */
	public PastTense() {
		this(true);
	}

	/**
	 * constructor
	 * @param CheckedWhenAnalyzed true if errors of this type should be looked for when the text is analyzed, false otherwise
	 */
	public PastTense(boolean CheckedWhenAnalyzed) {
		super(CheckedWhenAnalyzed);
	}
	
	/**
	 * finds all instances of past tense in the given paragraph
	 * @param line the paragraph in which to find errors
	 * @return a TokenErrorList of int[3] elements where [0] and [1] are start and end tokens of the error and [2] is the error number (1)
	 */
	@Override
	protected TokenErrorList findErrors(String line) {
		String tokens[] = Tools.getTokenizer().tokenize(line);
		String[] tags = Tools.getPOSTagger().tag(tokens);

		boolean inQuote = false, inIntroducedQuote = false;
		TokenErrorList errors = new TokenErrorList(line);
		for(int i = 0; i < tags.length; i++) {
			if(tokens[i].contains("\"")) {
				inIntroducedQuote = (!inQuote && i > 0 && (tokens[i - 1].equals(",") || tokens[i - 1].equals(":"))) ? true : false;
				inQuote = !inQuote;
			}
			if(!inIntroducedQuote && tags[i].equals("VBD")) //|| (tags[i].equals("VBN") && i > 0 && arrayContains(TO_HAVE_CONJ, tokens[i - 1])))
				errors.add(new int[] {i, i, ERROR_NUMBER});
		}
		
		return errors;
	}
}