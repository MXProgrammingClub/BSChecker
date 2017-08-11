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
	
	private static final String[] SUBJECTIVE = {"I", "you", "he", "she", "it", "we", "they"};
	private static final String[] OBJECTIVE = {"me", "you", "him", "her", "it", "us", "them"};
	private static final String[] POSSESSIVE = {"my", "your", "his", "her", "its", "our", "their", "whose"};
	private static final String[] IGNORE = {"myself", "yourself", "himself", "herself", "itself", "ourselves", "themselves"};
	
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
			ArrayList<Parse> pronounParses = UtilityMethods.findParsesWithTag(parse, new String[] {"PRP", "PRP$", "WP$"});
			for(Parse pronounParse : pronounParses) {
				if(!UtilityMethods.arrayContains(IGNORE, pronounParse.getCoveredText())) {
					Parse[] siblings = pronounParse.getParent().getChildren();
					int siblingIndex = UtilityMethods.getSiblingIndex(pronounParse);
					String[] tags = new String[siblings.length - siblingIndex - 1];
					for(int i = 0; i < tags.length; i++) {
						tags[i] = siblings[siblingIndex + 1 + i].getType().length() > 1 ? siblings[siblingIndex + 1 + i].getType().substring(0, 2) : siblings[siblingIndex + 1 + i].getType();
					}
					if(UtilityMethods.arrayContains(tags, "NN")) {
						if(!UtilityMethods.arrayContains(POSSESSIVE, pronounParse.getCoveredText().replaceAll("\"", "")))
							errors.add(new Error(UtilityMethods.getIndexOfParse(pronounParse.getChildren()[0]) + tokenOffset, "Should be possessive pronoun."));
					} else if(pronounParse.getParent().getParent().getType().equals("VP") || pronounParse.getParent().getParent().getType().equals("PP") || pronounParse.getParent().getParent().getParent().getType().equals("VP") && pronounParse.getParent().getParent().getParent().getChildren()[UtilityMethods.getSiblingIndex(pronounParse.getParent().getParent()) - 1].getType().charAt(0) == 'V') {
						if(!UtilityMethods.arrayContains(OBJECTIVE, pronounParse.getCoveredText().replaceAll("\"", "")))
							errors.add(new Error(UtilityMethods.getIndexOfParse(pronounParse.getChildren()[0]) + tokenOffset, "Should be objective pronoun."));
					} else if(siblingIndex + 1 == siblings.length && pronounParse.getParent().getParent().getType().equals("S")) {
						if(!UtilityMethods.arrayContains(SUBJECTIVE, pronounParse.getCoveredText().replaceAll("\"", "")))
							errors.add(new Error(UtilityMethods.getIndexOfParse(pronounParse.getChildren()[0]) + tokenOffset, "Should be subjective pronoun."));
					}
				}
			}
			
			ArrayList<Parse> relativeParses = UtilityMethods.findParsesWithTag(parse, new String[] {"WP"});
			for(Parse relativeParse : relativeParses) {
				System.out.println(relativeParse.getParent().getParent().getChildren()[1].getChildren()[0].getType());
				if(relativeParse.getParent().getParent().getChildren()[1].getChildren()[0].getType().equals("NP")) {
					if(!relativeParse.getCoveredText().replaceAll("\"", "").equals("whom"))
						errors.add(new Error(UtilityMethods.getIndexOfParse(relativeParse.getChildren()[0]) + tokenOffset, "Should be objective pronoun."));
				} else if(relativeParse.getParent().getParent().getChildren()[1].getChildren()[0].getType().equals("VP"))
					if(!relativeParse.getCoveredText().replaceAll("\"", "").equals("who"))
						errors.add(new Error(UtilityMethods.getIndexOfParse(relativeParse.getChildren()[0]) + tokenOffset, "Should be subjective pronoun."));
			}
			
			tokenOffset += Tools.getTokenizer().tokenize(parse.getText()).length;
		}
		
		return errors;
	}
	
}