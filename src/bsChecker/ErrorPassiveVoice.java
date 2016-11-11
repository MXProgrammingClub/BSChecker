package bsChecker;

import java.util.ArrayList;

/**
 * @author tedpyne
 * @author JeremiahDeGreeff
 * Finds verbs in the passive voice. (9)
 */
public class ErrorPassiveVoice extends Error {
	private static final int ERROR_NUMBER = 9;
	private static final String[] TO_BE_CONJ = {"be", "am", "is", "are", "was", "were", "been", "being"};
	
	/**
	 * for testing purposes
	 */
	public static void main(String[] args){
		Error.setupOpenNLP();
		String input = "This terrible Hamlet is destroyed by Claudius.";
		System.out.println("\ninput: " + input + "\n");
		ArrayList<int[]> errors = new ErrorPassiveVoice().findErrors(input);
		sort(errors);
		printErrors(tokensToChars(input, errors, 0), input);
	}

	/**
	 * finds all instances of passive voice in the given paragraph
	 * 
	 * @param line paragraph to check
	 * @return ArrayList int[3] representing errors where [0] is the beginning token index, [1] is ending token index, [2] is the type of error (9)
	 */
	@Override
	public ArrayList<int[]> findErrors(String line) {
		String tokens[] = tokenizer.tokenize(line);
		String[] tags = posTagger.tag(tokens);
		
		ArrayList<int[]> errors = new ArrayList<int[]>();
		for(int i = 1; i < tokens.length; i++)
			if(arrayContains(TO_BE_CONJ, tokens[i]) && i < tokens.length-1){
				int j = i+1;
				while(tags[j].equals("RB") && j < tokens.length) j++;
				if(tags[j].equals("VBN")){
					errors.add(new int[]{i, j, ERROR_NUMBER});
				}
			}
		/*
			if(tags[i].equals("VBN") && arrayContains(TO_BE_CONJ, tokens[i - 1]))
					errors.add(new int[]{i - 1, i, ERROR_NUMBER});*/
		
		return errors;
	}
}
