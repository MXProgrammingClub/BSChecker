package error;

import util.ErrorList;
import util.UtilityMethods;

/**
 * Finds verbs in the passive voice. (9)
 * @author tedpyne
 * @author JeremiahDeGreeff
 */
public class PassiveVoice extends Error {
	private static final String[] TO_BE_CONJ = {"be", "am", "is", "are", "was", "were", "been", "being"};
	
	/**
	 * for testing purposes
	 */
	public static void main(String[] args){
		UtilityMethods.setupOpenNLP();
		String input = "This terrible Hamlet is destroyed by Claudius.";
		System.out.println("\ninput: " + input + "\n");
		ErrorList errors = new PassiveVoice().findErrors(input);
		errors.sort();
		errors.tokensToChars(0);
		System.out.println(errors);
	}
	
	/**
	 * constructor
	 */
	public PassiveVoice() {
		super(9);
	}

	/**
	 * finds all instances of passive voice in the given paragraph
	 * @param line the paragraph in which to find errors
	 * @return an ErrorList of int[3] pointers to the indices of the start and end tokens of an error
	 * 			int[0], int[1] are start and end tokens of the error
	 * 			int[2] is the error number (9)
	 */
	@Override
	public ErrorList findErrors(String line) {
		String tokens[] = tokenizer.tokenize(line);
		String[] tags = posTagger.tag(tokens);
		
		ErrorList errors = new ErrorList(line, false);
		for(int i = 1; i < tokens.length; i++)
			if(UtilityMethods.arrayContains(TO_BE_CONJ, tokens[i]) && i < tokens.length-1){
				int j = i+1;
				while(tags[j].equals("RB") && j < tokens.length) j++;
				if(tags[j].equals("VBN")){
					errors.add(new int[]{i, j, ERROR_NUMBER});
				}
			}
		return errors;
	}
}
