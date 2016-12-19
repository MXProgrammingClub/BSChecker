package error;

import util.ErrorList;
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
		ErrorList errors = new PastTense().findErrors(input);
		errors.sort();
		errors.tokensToChars(0);
		System.out.println(errors);
	}
	
	/**
	 * default constructor
	 */
	public PastTense() {
		this(true);
	}

	/**
	 * constructor
	 * @param isChecked true if errors of this type should be looked for when the text is analyzed, false otherwise
	 */
	public PastTense(boolean isChecked) {
		super(1, isChecked);
	}
	
	/**
	 * finds all instances of past tense in the given paragraph
	 * known issues: runs into problems with ']' being interpreted as a past tense verb
	 * @param line the paragraph in which to find errors
	 * @return an ErrorList of int[3] pointers to the indices of the start and end tokens of an error
	 * 			int[0], int[1] are start and end tokens of the error
	 * 			int[2] is the error number (1)
	 */
	@Override
	public ErrorList findErrors(String line) {
		String tokens[] = tokenizer.tokenize(line);
		String[] tags = posTagger.tag(tokens);

		boolean inQuote = false, inIntroducedQuote = false;
		ErrorList errors = new ErrorList(line, false);
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