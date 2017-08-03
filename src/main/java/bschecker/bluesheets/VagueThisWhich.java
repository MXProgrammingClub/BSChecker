package bschecker.bluesheets;

import bschecker.util.Error;
import bschecker.util.ErrorList;
import bschecker.util.Tools;
import opennlp.tools.parser.Parse;

/**
 * Finds errors with vague use of this or which. (4)
 * @author tedpyne
 */
public class VagueThisWhich extends Bluesheet {
	
	/**
	 * Finds any vague which or this in a paragraph.
	 * @param line the paragraph in which to find errors
	 * @param parses a String array of the parses of each sentence of the line
	 * @return an ErrorList which for each Error references start token, end token, and, optionally, a note
	 */
	@Override
	protected ErrorList findErrors(String line, Parse[] parses){
		String[] tokens = Tools.getTokenizer().tokenize(line);
		String[] tags = Tools.getPOSTagger().tag(tokens);
		
		ErrorList errors = new ErrorList(line);
		for(int i = 0; i < tokens.length; i++)
			if(tokens[i].equalsIgnoreCase("this") && isVagueThis(tokens, tags, i))
				errors.add(new Error(i, "Vague this"));
			else if(tokens[i].equalsIgnoreCase("which") && (i == 0 || (tags[i - 1].charAt(0) != 'N' && tags[i - 1].charAt(0) != 'I')))
				errors.add(new Error(i, "Vague which"));
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
		if(index == tokens.length - 1)
			return true;
		for(int j = index + 1; j < tokens.length; j++){
			if(tags[j].charAt(0) == 'N')
				return false;
			if(tags[j].charAt(0) == 'V' || tags[j].charAt(0) == '.' || tags[j].charAt(0) == ':')
				return true;
		}
		return true;
	}
	
}
