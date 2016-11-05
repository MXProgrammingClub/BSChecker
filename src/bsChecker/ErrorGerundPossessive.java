package bsChecker;

import java.util.ArrayList;

/**
 * @author JeremiahDeGreeff
 * Finds errors where gerunds incorrectly lack a possessive. (13)
 */
public class ErrorGerundPossessive extends Error {
	private static final int ERROR_NUMBER = 13;

	/**
	 * for testing purposes
	 */
	public static void main(String[] args) {
		Error.setupOpenNLP();
		String input = "Elizabeth is grateful for him loving her so well.";
		System.out.println("\ninput: " + input + "\n");
		ArrayList<int[]> errors = new ErrorGerundPossessive().findErrors(input);
		sort(errors);
		printErrors(tokensToChars(input, errors, 0), input);
	}

	/**
	 * finds all errors where gerunds are not preceded by a possessive when they should be in the given paragraph
	 * note: catches many cases which are not errors
	 * @param line paragraph to check
	 * @return ArrayList int[3] representing errors where [0] is the beginning token index, [1] is ending token index, [2] is the type of error (13)
	 */
	@Override
	public ArrayList<int[]> findErrors(String line) {
		String[] tokens = tokenizer.tokenize(line);
		String[] tags = posTagger.tag(tokens);
		
		ArrayList<int[]> errors = new ArrayList<int[]>();
		for(int i = 1; i < tokens.length; i++)
			if(tags[i].equals("VBG") && (tags[i - 1].equals("PRP") || ((tags[i - 1].equals("NN") || tags[i - 1].equals("NNS") || tags[i - 1].equals("NNP") || tags[i - 1].equals("NNPS")) && tokens[i - 1].indexOf('\'') == -1)))
					errors.add(new int[]{i - 1, i, ERROR_NUMBER});
		
		return errors;
	}
}
