package error;

import util.ErrorList;
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
		ErrorList errors = new GerundPossessive().findErrors(input);
		errors.sort();
		errors.tokensToChars(0);
		System.out.println(errors);
	}
	
	/**
	 * default constructor
	 */
	public GerundPossessive() {
		this(true);
	}
	
	/**
	 * constructor
	 * @param isChecked true if errors of this type should be looked for when the text is analyzed, false otherwise
	 */
	public GerundPossessive(boolean isChecked) {
		super(13, isChecked);
	}

	/**
	 * finds all errors where gerunds are not preceded by a possessive when they should be in the given paragraph
	 * known issues: catches cases where the supposed gerund is in fact a participle and is thus not an error
	 * @param line the paragraph in which to find errors
	 * @return an ErrorList of int[3] pointers to the indices of the start and end tokens of an error
	 * 			int[0], int[1] are start and end tokens of the error
	 * 			int[2] is the error number (13)
	 */
	@Override
	protected ErrorList findErrors(String line) {
		String[] tokens = tokenizer.tokenize(line);
		String[] tags = posTagger.tag(tokens);
		
		ErrorList errors = new ErrorList(line, false);
		for(int i = 1; i < tokens.length; i++)
			if(tags[i].equals("VBG") && (tags[i - 1].equals("PRP") || ((tags[i - 1].equals("NN") || tags[i - 1].equals("NNS") || tags[i - 1].equals("NNP") || tags[i - 1].equals("NNPS")) && tokens[i - 1].indexOf('\'') == -1)))
					errors.add(new int[]{i - 1, i, ERROR_NUMBER});
		
		return errors;
	}
}
