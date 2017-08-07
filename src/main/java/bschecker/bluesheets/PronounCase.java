package bschecker.bluesheets;

import java.util.ArrayList;

import bschecker.util.Error;
import bschecker.util.ErrorList;
import bschecker.util.Tools;
import bschecker.util.UtilityMethods;
import opennlp.tools.parser.Parse;

/**
 * Finds errors in pronoun case. (6)
 * @author Leo
 * @author JeremiahDeGreeff
 */
public class PronounCase extends Bluesheet {
	
	private static final String[] SUBJ = {"I", "you", "he", "she", "it", "we", "they", "who"};
	private static final String[] OBJ = {"me", "you", "him", "her", "it", "us", "them", "whom"};
	
	/**
	 * Finds all errors in pronoun case in a paragraph.
	 * @param line the paragraph in which to find errors
	 * @param parses a Parse array of each sentence of the line
	 * @return an ErrorList which for each Error references start token, end token, and, optionally, a note
	 */
	@Override
	protected ErrorList findErrors(String line, Parse[] parses) {
		ErrorList errors = new ErrorList(line);
		int tokenOffset = 0;
		for(Parse parse: parses) {
			ArrayList<Parse> pronounParses = UtilityMethods.findParsesWithTag(parse, new String[] {"PRP", "PRP$"});
			
			for(Parse pronounParse : pronounParses) {
				Parse[] siblings = pronounParse.getParent().getChildren();
				int siblingIndex = UtilityMethods.getSiblingIndex(pronounParse);
				String[] tags = new String[siblings.length - siblingIndex - 1];
				for(int i = 0; i < tags.length; i++)
					tags[i] = siblings[siblingIndex + 1 + i].getType().length() > 1 ? siblings[siblingIndex + 1 + i].getType().substring(0, 2) : siblings[siblingIndex + 1 + i].getType();
				if(UtilityMethods.arrayContains(tags, "NN")) {
					if(!pronounParse.getType().equals("PRP$"))
						errors.add(new Error(UtilityMethods.getIndexOfParse(pronounParse.getChildren()[0]) + tokenOffset, "Should be possessive pronoun."));
				} else if(siblingIndex + 1 == siblings.length && !pronounParse.getParent().getParent().getType().equals("VP")) {
					if(!UtilityMethods.arrayContains(SUBJ, pronounParse.getCoveredText()))
						errors.add(new Error(UtilityMethods.getIndexOfParse(pronounParse.getChildren()[0]) + tokenOffset, "Should be subjective pronoun."));
				} else if(pronounParse.getParent().getParent().getType().equals("VP")) {
					if(!UtilityMethods.arrayContains(OBJ, pronounParse.getCoveredText()))
						errors.add(new Error(UtilityMethods.getIndexOfParse(pronounParse.getChildren()[0]) + tokenOffset, "Should be objective pronoun."));
				}
			}
			
			tokenOffset += Tools.getTokenizer().tokenize(parse.getText()).length;
		}
		
		return errors;
	}
	
}