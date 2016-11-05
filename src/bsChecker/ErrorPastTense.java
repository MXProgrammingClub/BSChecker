package bsChecker;

import java.util.ArrayList;

/**
 * @author Leo
 * @author tedpyne
 * @author JeremiahDeGreeff
 * Finds verbs in the past tense. (1)
 */
public class ErrorPastTense extends Error{
	private static final int ERROR_NUMBER = 1;
	private static final String[] TO_HAVE_CONJ = {"have", "has", "had", "having"};
	
	/**
	 * for testing purposes
	 */
	public static void main(String[] args) {
		Error.setupOpenNLP();
		String input = "he has died";
		System.out.println("\ninput: " + input + "\n");
		ArrayList<int[]> errors = new ErrorPastTense().findErrors(input);
		sort(errors);
		printErrors(tokensToChars(input, errors, 0), input);
	}

	/**
	 * finds all instances of past tense in the given paragraph
	 * @param line paragraph to check
	 * @return ArrayList int[3] representing errors where [0] is the beginning token index, [1] is ending token index, [2] is the type of error (1)
	 */
	@Override
	public ArrayList<int[]> findErrors(String line) {
		String tokens[] = tokenizer.tokenize(line);
		String[] tags = posTagger.tag(tokens);

		ArrayList<int[]> errors = new ArrayList<int[]>();
		for(int i = 0; i < tags.length; i++)
			if(tags[i].equals("VBD") || (tags[i].equals("VBN") && i > 0 && arrayContains(TO_HAVE_CONJ, tokens[i - 1])))
				errors.add(new int[] {i, i, ERROR_NUMBER});
		
		return errors;
	}
}