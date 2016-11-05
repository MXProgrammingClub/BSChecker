package bsChecker;

import java.util.ArrayList;

/**
 * @author tedpyne
 * Finds errors with vague use of this or which. (4)
 */
public class ErrorVagueThisWhich extends Error {
	private static final int ERROR_NUMBER = 4;

	/**
	 * for testing purposes
	 */
	public static void main(String[] args){
		Error.setupOpenNLP();
		String input = "Hi, my name I hate this; cars are fun.";
		System.out.println("\ninput: " + input + "\n");
		ArrayList<int[]> errors = new ErrorVagueThisWhich().findErrors(input);
		sort(errors);
		printErrors(tokensToChars(input, errors, 0), input);
	}
	
	/**
	 * finds any vague which or this in the given paragraph
	 * @param line paragraph to check
	 * @return ArrayList int[3] representing errors where [0] is the beginning token index, [1] is ending token index, [2] is the type of error (4)
	 */
	@Override
	public ArrayList<int[]> findErrors(String line){
		String tokens[] = tokenizer.tokenize(line);
		String[] tags = posTagger.tag(tokens);
		
		ArrayList<int[]> errors = new ArrayList<int[]>();
		for(int i = 0; i < tokens.length; i++)
			if(	(tokens[i].equalsIgnoreCase("this") && isVagueThis(tokens,tags,i)) ||
				(tokens[i].equalsIgnoreCase("which") && (i == 0 || (tags[i-1].charAt(0)!='N' && tags[i-1].charAt(0)!='I'))))
				errors.add(new int[]{i, i, ERROR_NUMBER});
		
		return errors;
	}

	/**
	 * loops through tokens after "this" until a noun or verb is found
	 * @param tokens the tokens to look through
	 * @param tags the tags of those tokens
	 * @param index the index to start looking from
	 * @return true if followed by verb and is thus vague, false if followed by noun and is thus not vague
	 */
	private boolean isVagueThis(String[] tokens, String[] tags, int index) {
		if(index==tokens.length-1) return true;
		for(int j = index+1; j < tokens.length; j++){
			if(tags[j].charAt(0)=='N') return false;
			if(tags[j].charAt(0)=='V' || tags[j].charAt(0)=='.' || tags[j].charAt(0)==':') return true;
		}
		return true;
	}
}
