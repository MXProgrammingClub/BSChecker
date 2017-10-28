package bschecker.bluesheets;

import bschecker.util.Error;
import bschecker.util.ErrorList;
import bschecker.util.Tools;
import bschecker.util.UtilityMethods;
import opennlp.tools.parser.Parse;

/**
 * Finds errors with vague use of this or which. (4)
 * 
 * @author tedpyne
 */
public class VagueThisWhich extends Bluesheet {
	
	/**
	 * Finds any vague which or this in a paragraph.
	 * 
	 * @param line the paragraph in which to find errors
	 * @param parses a Parse array of each sentence of the line
	 * @return an ErrorList which for each Error references start token, end token, and, optionally, a note
	 */
	@Override
	protected ErrorList findErrors(String line, Parse[] parses) {
		String[] tokens = Tools.getTokenizer().tokenize(line);
		String[] tags = Tools.getPOSTagger().tag(tokens);
		
		ErrorList errors = new ErrorList(line);
		for(int i = 0; i < tokens.length; i++)
			if(tokens[i].equalsIgnoreCase("this") && isVagueThis(tags, i))
				errors.add(new Error(i, "Vague this"));
			else if(tokens[i].equalsIgnoreCase("which") && isVagueWhich(tags, i))
				errors.add(new Error(i, "Vague which"));
		return errors;
	}

	/**
	 * Loops through tokens after "this" until a noun or verb is found.
	 * 
	 * @param tags the tags of the tokens in the sentence
	 * @param index the index to start looking from
	 * @return true if followed by verb and is thus vague, false if followed by noun and is thus not vague
	 */
	private boolean isVagueThis(String[] tags, int index) {
		while(index < tags.length) {
			if(tags[index].charAt(0) == 'N')
				return false;
			else if(tags[index].charAt(0) == 'V' || tags[index].charAt(0) == '.' || tags[index].charAt(0) == ':')
				return true;
			index++;
		}
		return true;
	}
	
	/**
	 * Loops through any ignorable tokens before "which" to find a noun.
	 * 
	 * @param tags the tags of the tokens in the sentence
	 * @param index the index to start looking from
	 * @return false if preceded by a noun, true otherwise
	 */
	private boolean isVagueWhich(String[] tags, int index) {
		if(index == 0)
			return true;
		do index--; while(UtilityMethods.arrayContains(new String[] {"-RRB-", "-LRB-", "''"}, tags[index]));
		return tags[index].charAt(0) != 'N';
	}
	
}
