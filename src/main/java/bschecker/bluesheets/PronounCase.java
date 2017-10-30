package bschecker.bluesheets;

import bschecker.reference.VerbSets;
import bschecker.util.Error;
import bschecker.util.ErrorList;
import bschecker.util.LogHelper;
import bschecker.util.Tools;
import bschecker.util.UtilityMethods;
import opennlp.tools.parser.AbstractBottomUpParser;
import opennlp.tools.parser.Parse;

/**
 * Finds errors in pronoun case. (6)
 * 
 * @author Leo
 * @author JeremiahDeGreeff
 */
public class PronounCase extends Bluesheet {
	
	/**
	 * Finds all errors in pronoun case in a paragraph.
	 * 
	 * @param line the paragraph in which to find errors
	 * @param parses a Parse array of each sentence of the line
	 * @return an ErrorList which for each Error references start token, end token, and, optionally, a note
	 */
	@Override
	protected ErrorList findErrors(String line, Parse[] parses) {
		ErrorList errors = new ErrorList(line);
		int tokenOffset = 0;
		for(Parse parse: parses) {
			for(Types type : Types.values())
				for(Parse pronounParse : UtilityMethods.findParsesWithTag(parse, type.TAGS))
					if(!UtilityMethods.arrayContains(type.IGNORE, pronounParse.getCoveredText())) {
						Cases pronounCase = type == Types.PERSONAL ? getCorrectPersonalCase(pronounParse) : getCorrectRelativeCase(pronounParse);
						if(pronounCase == Cases.UNDETERMINED)
							LogHelper.getLogger(this).warn("Undetermined " + type.toString().toLowerCase() + " pronoun case - token: \"" + pronounParse.getCoveredText() + "\"");
						else {
							String pronoun = pronounParse.getCoveredText().replaceAll("\"", "");
							if(type == Types.PERSONAL ? !UtilityMethods.arrayContains(pronounCase.PRONOUNS, pronoun) : !pronoun.equals(pronounCase.RELATIVE))
								errors.add(new Error(pronounParse.getChildren()[0], tokenOffset, "Should be " + pronounCase.toString().toLowerCase() + " pronoun."));
						}
					}
			tokenOffset += Tools.getTokenizer().tokenize(parse.getText()).length;
		}
		
		return errors;
	}
	
	/**
	 * Determines which case a personal pronoun should be based on its Parse.
	 * 
	 * @param personalParse a Parse whose node is the pronoun to check
	 * @return the element of {@link Cases} which corresponds to the correct case of the pronoun
	 */
	private static Cases getCorrectPersonalCase(Parse personalParse) {
		Parse[] siblings = personalParse.getParent().getChildren();
		int siblingIndex = UtilityMethods.getSiblingIndex(personalParse);
		
		String[] tags = new String[siblings.length - siblingIndex - 1];
		for(int i = 0; i < tags.length; i++)
			tags[i] = siblings[siblingIndex + 1 + i].getCoveredText().equals("\"") ? "" : siblings[siblingIndex + 1 + i].getType().length() > 1 ? siblings[siblingIndex + 1 + i].getType().substring(0, 2) : siblings[siblingIndex + 1 + i].getType();
		Parse nextTokenIgnoreAdverbsAdjectives = UtilityMethods.getNextToken(personalParse, new String[]{"RB", "JJ"});
		if(UtilityMethods.arrayContains(tags, "NN")
				|| nextTokenIgnoreAdverbsAdjectives != null && nextTokenIgnoreAdverbsAdjectives.getParent().getType().equals("VBG"))
			return Cases.POSSESSIVE;
		
		Parse nextToken = UtilityMethods.getNextToken(personalParse, null);
		if(personalParse.getParent().getParent().getType().equals("VP")
				|| personalParse.getParent().getParent().getType().equals("PP")
				|| nextToken != null && nextToken.getParent().getType().equals("TO")
				|| personalParse.getParent().getParent().getParent().getType().equals("VP") && personalParse.getParent().getParent().getParent().getChildren()[UtilityMethods.getSiblingIndex(personalParse.getParent().getParent()) - 1].getType().charAt(0) == 'V'
				|| !personalParse.getParent().getParent().getParent().getType().equals(AbstractBottomUpParser.TOP_NODE) && personalParse.getParent().getParent().getParent().getParent().getType().equals("VP") && personalParse.getParent().getParent().getParent().getType().equals("SBAR") && personalParse.getParent().getParent().getParent().getSpan().getStart() == personalParse.getSpan().getStart() && !VerbSets.getSayingVerbs().contains(personalParse.getParent().getParent().getParent().getParent().getChildren()[0].getCoveredText()))
			return Cases.OBJECTIVE;
		
		Parse parent = personalParse.getParent();
		while(parent.getType().equals("NP"))
			parent = parent.getParent();
		if(parent.getType().equals("S")
				|| UtilityMethods.getNextNode(personalParse.getChildren()[0]).getType().equals("VP"))
			return Cases.SUBJECTIVE;
		if(parent.getType().equals("PP"))
			return Cases.OBJECTIVE;
		
		return Cases.UNDETERMINED;
	}
	
	/**
	 * Determines which case a relative pronoun should be based on its Parse.
	 * 
	 * @param relativeParse a Parse whose node is the relative pronoun to check
	 * @return the element of {@link Cases} which corresponds to the correct case of the pronoun
	 */
	private static Cases getCorrectRelativeCase(Parse relativeParse) {
		Parse following = relativeParse.getParent().getParent().getChildren()[1].getChildren()[0];
		if(following.getType().equals("ADVP"))
			following = following.getParent().getChildren()[1];
		else if(following.getType().equals("S"))
			following = following.getChildren()[0];
		Parse next = UtilityMethods.getNextSibling(following, new String[] {"ADVP"});
		return following.getType().equals("VP") || following.getType().equals("NP") && next != null && next.getType().equals("VP") && UtilityMethods.arrayContains(VerbSets.TO_BE_CONJ, next.getCoveredText()) ? Cases.SUBJECTIVE : following.getType().equals("NP") ? Cases.OBJECTIVE : Cases.UNDETERMINED;
	}
	
	/**
	 * Represents the different cases of pronouns.
	 * 
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
	
	/**
	 * Represents the different types of pronouns which this class checks for.
	 * 
	 * @author JeremiahDeGreeff
	 */
	private static enum Types {
		
		PERSONAL(new String[] {"myself", "yourself", "himself", "herself", "itself", "one", "oneself", "ourselves", "themselves", "mine", "yours", "hers", "ours", "theirs"}, new String[] {"PRP", "PRP$", "WP$"}),
		RELATIVE(new String[] {"what", "that", "\""}, new String[] {"WP"});
		
		protected final String[] IGNORE;
		protected final String[] TAGS;
		
		Types(String[] ignore, String[] tags) {
			IGNORE = ignore;
			TAGS = tags;
		}
		
	}
	
}