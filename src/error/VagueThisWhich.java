package error;

import java.util.ArrayList;

import util.TokenErrorList;
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
		String input = "";
		System.out.println("\ninput: " + input + "\n");
		TokenErrorList errors = new VagueThisWhich().findErrors(input);
		errors.sort();
		System.out.println(errors.tokensToChars(0, new ArrayList<Integer>()));
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
	 * @return a TokenErrorList of int[3] elements where [0] and [1] are start and end tokens of the error and [2] is the error number (4)
	 */
	@Override
	protected TokenErrorList findErrors(String line){
		String tokens[] = tokenizer.tokenize(line);
		String[] tags = posTagger.tag(tokens);
		
		TokenErrorList errors = new TokenErrorList(line);
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
