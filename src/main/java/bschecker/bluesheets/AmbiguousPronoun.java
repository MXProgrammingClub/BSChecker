package bschecker.bluesheets;

import java.util.ArrayList;

import bschecker.util.Error;
import bschecker.util.ErrorList;
import bschecker.util.Tools;
import bschecker.util.UtilityMethods;
import opennlp.tools.parser.Parse;
import opennlp.tools.util.Span;

/**
 * Finds ambiguous pronoun references. (7)
 * @author Dalal
 */
public class AmbiguousPronoun extends Bluesheet {
	
	private static final String[] PRONOUNS = {"she", "her", "hers", "herself", "he", "him", "his", "himself", "they", "them", "their", "theirs", "themselves"};
	
	
	/**
	 * Finds all ambiguous pronoun references in a paragraph.
	 * @param line the paragraph in which to find errors
	 * @param parses a Parse array of each sentence of the line
	 * @return an ErrorList which for each Error references start token, end token, and, optionally, a note
	 */
	@Override
	protected ErrorList findErrors(String line, Parse[] parses) {
		ErrorList errors = new ErrorList(line);
		int prevSentenceNouns, curSentenceNouns = 0, tokenOffset = 0, index;
		for(int i = 0; i < parses.length; i++) {
			String[] words = Tools.getTokenizer().tokenize(parses[i].getText());
			prevSentenceNouns = curSentenceNouns;
			curSentenceNouns = 0;
			ArrayList<String> names = findName(words);
			for(int j = 0; j < words.length; j++)
				if(UtilityMethods.arrayContains(PRONOUNS, words[j]) && prevSentenceNouns + curSentenceNouns >= 2)
						errors.add(new Error(tokenOffset + j));	
				else if((index = names.indexOf(words[j])) >= 0) {
					prevSentenceNouns = 0;
					curSentenceNouns++;
					names.remove(index);
				}
			tokenOffset += words.length;
		}
		return errors;
	}

	/**
	 * finds all names in the given array of words as determined by opennlp
	 * @param words the words to look through
	 * @return an ArrayList of all the words which have been recognized as names
	 */
	private static ArrayList<String> findName(String[] words) {
		ArrayList<String> names = new ArrayList<String>();
		Span spans[] = Tools.getNameFinder().find(words);
		for(Span span : spans)
			if(!names.contains(words[span.getStart()]))
				names.add(words[span.getStart()]);
		return names;
	}
	
}
