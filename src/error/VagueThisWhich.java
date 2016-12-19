package error;

import util.ErrorList;
import util.UtilityMethods;

/**
 * Finds errors with vague use of this or which. (4)
 * @author tedpyne
 */
public class VagueThisWhich extends Error {
	/**
	 * for testing purposes
	 */
	public static void main(String[] args){
		UtilityMethods.setupOpenNLP();
		String input = "Hi, my name I hate this; cars are fun.";
		System.out.println("\ninput: " + input + "\n");
		ErrorList errors = new VagueThisWhich().findErrors(input);
		errors.sort();
		errors.tokensToChars(0);
		System.out.println(errors);
	}
	
	/**
	 * default constructor
	 */
	public VagueThisWhich() {
		this(true);
	}
	
	/**
	 * constructor
	 * @param isChecked true if errors of this type should be looked for when the text is analyzed, false otherwise
	 */
	public VagueThisWhich(boolean isChecked) {
		super(4, isChecked);
	}
	
	/**
	 * finds any vague which or this in the given paragraph
	 * @param line the paragraph in which to find errors
	 * @return an ErrorList of int[3] pointers to the indices of the start and end tokens of an error
	 * 			int[0], int[1] are start and end tokens of the error
	 * 			int[2] is the error number (4)
	 */
	@Override
	public ErrorList findErrors(String line){
		String tokens[] = tokenizer.tokenize(line);
		String[] tags = posTagger.tag(tokens);
		
		ErrorList errors = new ErrorList(line, false);
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
