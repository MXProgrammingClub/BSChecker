package error;

import java.util.ArrayList;

import util.TokenErrorList;
import util.UtilityMethods;

/**
 * Finds errors where gerunds incorrectly lack a possessive. (13)
 * @author JeremiahDeGreeff
 */
public class GerundPossessive extends Error {
	/**
	 * for testing purposes
	 */
	public static void main(String[] args) {
		UtilityMethods.setupOpenNLP();
		String input = "";
		System.out.println("\ninput: " + input + "\n");
		TokenErrorList errors = new GerundPossessive().findErrors(input);
		errors.sort();
		System.out.println(errors.tokensToChars(0, new ArrayList<Integer>()));
	}
	
	/**
	 * default constructor
	 */
	public GerundPossessive() {
		this(true);
	}
	
	/**
	 * constructor
	 * @param CheckedWhenAnalyzed true if errors of this type should be looked for when the text is analyzed, false otherwise
	 */
	public GerundPossessive(boolean CheckedWhenAnalyzed) {
		super(13, CheckedWhenAnalyzed);
	}

	/**
	 * finds all errors where gerunds are not preceded by a possessive when they should be in the given paragraph
	 * known issues: catches cases where the supposed gerund is in fact a participle and is thus not an error
	 * @param line the paragraph in which to find errors
	 * @return a TokenErrorList of int[3] elements where [0] and [1] are start and end tokens of the error and [2] is the error number (13)
	 */
	@Override
	protected TokenErrorList findErrors(String line) {
		String[] tokens = UtilityMethods.getTokenizer().tokenize(line);
		String[] tags = UtilityMethods.getPOSTagger().tag(tokens);
		
		TokenErrorList errors = new TokenErrorList(line);
		for(int i = 1; i < tokens.length; i++)
			if(tags[i].equals("VBG") && (tags[i - 1].equals("PRP") || ((tags[i - 1].equals("NN") || tags[i - 1].equals("NNS") || tags[i - 1].equals("NNP") || tags[i - 1].equals("NNPS")) && tokens[i - 1].indexOf('\'') == -1)))
					errors.add(new int[]{i - 1, i, ERROR_NUMBER});
		
		return errors;
	}
}
