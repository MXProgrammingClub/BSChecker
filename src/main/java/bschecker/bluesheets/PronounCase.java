package bschecker.bluesheets;

import java.util.ArrayList;

import bschecker.reference.Reference;
import bschecker.util.Error;
import bschecker.util.ErrorList;
import bschecker.util.LogHelper;
import bschecker.util.Tools;
import bschecker.util.UtilityMethods;
import opennlp.tools.parser.AbstractBottomUpParser;
import opennlp.tools.parser.Parse;

/**
 * Finds errors in pronoun case. (6)
 * @author Leo
 * @author JeremiahDeGreeff
 */
public class PronounCase extends Bluesheet {
	
	private static final String[] IGNORE = {"myself", "yourself", "himself", "herself", "itself", "ourselves", "themselves", "one", "oneself"};
	private static final String[] RELATIVE_IGNORE = {"what"};
	
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
			for(Parse pronounParse : pronounParses)
				if(!UtilityMethods.arrayContains(IGNORE, pronounParse.getCoveredText())) {
					Cases pronounCase = getCorrectCase(pronounParse);
					if(pronounCase == Cases.UNDETERMINED)
						LogHelper.getLogger(this).warn("Undetermined personal pronoun case");
					else if(!UtilityMethods.arrayContains(pronounCase.PRONOUNS, pronounParse.getCoveredText().replaceAll("\"", "")))
						errors.add(new Error(pronounParse.getChildren()[0], tokenOffset, "Should be " + pronounCase.toString().toLowerCase() + " pronoun."));
				}
			
			ArrayList<Parse> relativeParses = UtilityMethods.findParsesWithTag(parse, new String[] {"WP"});
			for(Parse relativeParse : relativeParses)
				if(!UtilityMethods.arrayContains(RELATIVE_IGNORE, relativeParse.getCoveredText())) {
					String type = relativeParse.getParent().getParent().getChildren()[1].getChildren()[0].getType();
					Cases relativeCase = type.equals("NP") ? Cases.OBJECTIVE : type.equals("VP") ? Cases.SUBJECTIVE : Cases.UNDETERMINED;
					if(relativeCase == Cases.UNDETERMINED)
						LogHelper.getLogger(this).warn("Undetermined relative pronoun case");
					else if(!relativeParse.getCoveredText().replaceAll("\"", "").equals(relativeCase.RELATIVE))
						errors.add(new Error(relativeParse.getChildren()[0], tokenOffset, "Should be " + relativeCase.toString().toLowerCase() + " pronoun."));
				}
			
			tokenOffset += Tools.getTokenizer().tokenize(parse.getText()).length;
		}
		
		return errors;
	}
	
	/**
	 * determines which case a pronoun should be based on its Parse
	 * @param pronounParse a Parse whose node is the pronoun to check
	 * @return the element of the Cases enum which corresponds to the correct case of the pronoun
	 */
	private static Cases getCorrectCase(Parse pronounParse) {
		Parse[] siblings = pronounParse.getParent().getChildren();
		int siblingIndex = UtilityMethods.getSiblingIndex(pronounParse);
		String[] tags = new String[siblings.length - siblingIndex - 1];
		for(int i = 0; i < tags.length; i++)
			tags[i] = siblings[siblingIndex + 1 + i].getCoveredText().equals("\"") ? "" : siblings[siblingIndex + 1 + i].getType().length() > 1 ? siblings[siblingIndex + 1 + i].getType().substring(0, 2) : siblings[siblingIndex + 1 + i].getType();
		Parse nextTokenIgnoreAdverbs = UtilityMethods.getNextToken(pronounParse, new String[]{"RB"});
		if(UtilityMethods.arrayContains(tags, "NN")
			|| nextTokenIgnoreAdverbs != null && nextTokenIgnoreAdverbs.getParent().getType().equals("VBG"))
			return Cases.POSSESSIVE;
		
		Parse nextToken = UtilityMethods.getNextToken(pronounParse, null);
		if(pronounParse.getParent().getParent().getType().equals("VP")
		|| pronounParse.getParent().getParent().getType().equals("PP")
		|| nextToken != null && nextToken.getParent().getType().equals("TO")
		|| pronounParse.getParent().getParent().getParent().getType().equals("VP") && pronounParse.getParent().getParent().getParent().getChildren()[UtilityMethods.getSiblingIndex(pronounParse.getParent().getParent()) - 1].getType().charAt(0) == 'V'
		|| !pronounParse.getParent().getParent().getParent().getType().equals(AbstractBottomUpParser.TOP_NODE) && pronounParse.getParent().getParent().getParent().getParent().getType().equals("VP") && pronounParse.getParent().getParent().getParent().getType().equals("SBAR") && pronounParse.getParent().getParent().getParent().getSpan().getStart() == pronounParse.getSpan().getStart() && !Reference.getVerbSet().contains(pronounParse.getParent().getParent().getParent().getParent().getChildren()[0].getCoveredText()))
			return Cases.OBJECTIVE;
		
		Parse parent = pronounParse.getParent();
		while(parent.getType().equals("NP"))
			parent = parent.getParent();
		if(parent.getType().equals("S")
		|| UtilityMethods.getNextNode(pronounParse.getChildren()[0]).getType().equals("VP"))
			return Cases.SUBJECTIVE;
		if(parent.getType().equals("PP"))
			return Cases.OBJECTIVE;
		
		return Cases.UNDETERMINED;
	}
	
	/**
	 * an enum which represents the different cases of pronouns
	 * @author JeremiahDeGreeff
	 */
	private static enum Cases {
		
		UNDETERMINED(null, null),
		SUBJECTIVE(new String[]{"I", "you", "he", "she", "it", "we", "they"}, "who"),
		OBJECTIVE(new String[]{"me", "you", "him", "her", "it", "us", "them"}, "whom"),
		POSSESSIVE(new String[]{"my", "your", "his", "her", "its", "our", "their", "whose"}, null);
		
		protected final String[] PRONOUNS;
		protected final String RELATIVE;
		
		Cases(String[] pronouns, String relative) {
			PRONOUNS = pronouns;
			RELATIVE = relative;
		}
		
	}
	
}