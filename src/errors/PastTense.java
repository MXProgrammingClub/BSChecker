package errors;

import java.util.ArrayList;

import util.UtilityMethods;

/**
 * Finds verbs in the past tense. (1)
 * @author Leo
 * @author tedpyne
 * @author JeremiahDeGreeff
 */
public class PastTense extends Error {
	//private static final String[] TO_HAVE_CONJ = {"have", "has", "had", "having"};
	
	/**
	 * for testing purposes
	 */
	public static void main(String[] args) {
		UtilityMethods.setupOpenNLP();
		String input = "he has died";
		System.out.println("\ninput: " + input + "\n");
		ArrayList<int[]> errors = new PastTense().findErrors(input);
		sort(errors);
		printErrors(tokensToChars(input, errors, 0), input);
	}

	/**
	 * constructor
	 */
	public PastTense() {
		super(1);
	}
	
	/**
	 * finds all instances of past tense in the given paragraph
	 * known issues: runs into problems with ']' being interpreted as a past tense verb
	 * @param line paragraph to check
	 * @return ArrayList int[3] representing errors where [0] is the beginning token index, [1] is ending token index, [2] is the type of error (1)
	 */
	@Override
	public ArrayList<int[]> findErrors(String line) {
		String tokens[] = tokenizer.tokenize(line);
		String[] tags = posTagger.tag(tokens);

		boolean inQuote = false, inIntroducedQuote = false;
		ArrayList<int[]> errors = new ArrayList<int[]>();
		for(int i = 0; i < tags.length; i++) {
			if(tokens[i].contains("\"")) {
				if(!inQuote && i > 0 && (tokens[i - 1].equals(",") || tokens[i - 1].equals(":")))
					inIntroducedQuote = true;
				else
					inIntroducedQuote = false;
				inQuote = !inQuote;
			}
			if(!inIntroducedQuote && tags[i].equals("VBD")) //|| (tags[i].equals("VBN") && i > 0 && arrayContains(TO_HAVE_CONJ, tokens[i - 1])))
				errors.add(new int[] {i, i, ERROR_NUMBER});
		}
		
		return errors;
	}
}