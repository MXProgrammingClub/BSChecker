package bschecker.bluesheets;

import java.util.ArrayList;

import bschecker.util.Error;
import bschecker.util.ErrorList;
import bschecker.util.LogHelper;
import bschecker.util.Tools;
import bschecker.util.UtilityMethods;
import opennlp.tools.parser.Parse;

/**
 * Finds errors in Parallelism. (11)
 * @author JeremiahDeGreeff
 */
public class FaultyParallelism extends Bluesheet {
	
	private final String[][] tagGroups = {{"NN", "NNS", "NNP", "NNPS"}, {"S", "SBAR", "NP"}, {"POS", "PRP$"}, {"VP", "VB", "VBD", "VBP", "VBZ"}};
	
	/**
	 * Finds any instances of faulty parallelism in a paragraph.
	 * @param line the paragraph in which to find errors
	 * @param parses a Parse array of each sentence of the line
	 * @return an ErrorList which for each Error references start token, end token, and, optionally, a note
	 */
	@Override
	protected ErrorList findErrors(String line, Parse[] parses) {
		ErrorList errors = new ErrorList(line);
		int tokenOffset = 0;
		for(Parse parse : parses) {
			String[] tags = Tools.getPOSTagger().tag(Tools.getTokenizer().tokenize(parse.getText()));
			ArrayList<Parse> ccParses = new ArrayList<Parse>();
			for(int i = 0; i < tags.length; i++)
				if(tags[i].equals("CC"))
					ccParses.add(UtilityMethods.getParseAtToken(parse, i));
			for(Parse ccParse : ccParses)
				if(!ccIsValid(ccParse))
					errors.add(new Error(ccParse, tokenOffset));
			tokenOffset += tags.length;
		}
		return errors;
	}
	
	/**
	 * tests whether or not a coordinating conjunction is part of a faulty parallelism
	 * @param parse the Parse with the CC node
	 * @return false if there is faulty parallelism, true otherwise
	 */
	private boolean ccIsValid(Parse parse) {
		Parse ccParse = parse.getParent();
		//special case: CC in CONJP e.g. "but rather"
		if(ccParse.getParent().getType().equals("CONJP"))
			ccParse = ccParse.getParent();
		
		Parse[] siblings = ccParse.getParent().getChildren();
		int siblingIndex = UtilityMethods.getSiblingIndex(ccParse);
		//this catches exceptions generally caused by parsing errors - NOTE: these cases may in fact be errors, but with the current implementation they are AMBIGUOUS
		if(siblingIndex == 0 || siblingIndex == siblings.length - 1)
			return false;
		ArrayList<Parse> parallels = new ArrayList<Parse>();
		parallels.add(siblings[siblingIndex - 1]);
		parallels.add(siblings[siblingIndex + 1]);
		
		//special case: correlative conjunction e.g. "whether or not" - parsed as IN CC RB - must be checked here because afterwards RB are ignored
		if(parallels.get(0).getType().equals("IN") && parallels.get(1).getType().equals("RB"))
			return false;
		
		//special case: CC followed by comma separated phrase
		//special case: CC followed by adverb e.g. "and thus"
		if(parallels.get(1).getType().equals(",") || parallels.get(1).getType().equals("ADVP") || parallels.get(1).getType().equals("RB"))
			if(siblingIndex + 2 < siblings.length)
				parallels.set(1, siblings[siblingIndex + 2]);
		//this is an exception which will generally only come up with bad sentence structure that generates an unusual parse
			else
				parallels.set(1, UtilityMethods.getNextNode(UtilityMethods.getNextToken(parallels.get(1), null)));
		
		//special case: CC preceded by comma e.g. 'IC, CC IC' structure
		//special case: CC preceded by semicolon (not valid sentence structure)
		if(parallels.get(0).getType().equals(",")  && (siblings[siblingIndex - 2].getType().equals("S") || siblings[siblingIndex - 2].getType().equals("SBAR")) || parallels.get(0).getType().equals(":"))
			parallels.set(0, siblings[siblingIndex - 2]);
		
		//list with three or more elements - assumes the use of the Oxford comma
		if(parallels.get(0).getType().equals(",")) {
			for(int cursorIndex = UtilityMethods.getSiblingIndex(parallels.get(0)); cursorIndex > 0 && siblings[cursorIndex].getType().equals(","); cursorIndex -= 2) {
				ArrayList<Parse> temp = new ArrayList<Parse>(), commaParses = UtilityMethods.findParsesWithTag(siblings[cursorIndex - 1], new String[] {","});
				if(commaParses.size() == 0)
					temp.add(siblings[cursorIndex - 1]);
				//comma within what appears to be the previous element - most likely an error (doesn't support complex, semicolon separated lists which contain commas within elements)
				else
					for(int i = 0; i < commaParses.size(); i++) {
						int commaSiblingIndex = UtilityMethods.getSiblingIndex(commaParses.get(i));
						temp.add(commaParses.get(i).getParent().getChildren()[commaSiblingIndex - 1]);
						if(i + 1 == commaParses.size())
							temp.add(commaParses.get(i).getParent().getChildren()[commaSiblingIndex + 1]);
					}
				parallels.addAll(1, temp);
			}
			//remove original left entry
			parallels.remove(0);
		}
		
		return parallelsAreValid(parallels, siblingIndex + 2 < siblings.length && siblings[siblingIndex + 2].getType().equals("POS"));
	}
	
	/**
	 * determines whether or not a given ArrayList of parse are valid as parallel structures
	 * @param parallels an ArrayList of the Parses which are used in parallel
	 * @param rightIsPosessive true if the word to the right of the CC is a possessive noun
	 * @return true if the the parallels are valid, false otherwise
	 */
	private boolean parallelsAreValid(ArrayList<Parse> parallels, boolean rightIsPossessive) {
		String[] parallelTypes = new String[parallels.size()];
		String debug = "left: \"";
		for(int i = 0; i < parallelTypes.length; i++) {
			parallelTypes[i] = parallels.get(i).getType();
			if(i + 1 < parallelTypes.length)
				debug += parallelTypes[i] + (i + 2 < parallelTypes.length ? "\", \"" : "\" ");
		}
		LogHelper.getLogger(this).debug(debug + "right: \"" + parallelTypes[parallelTypes.length - 1] + "\"");
		
		for(int i = 0; i < parallelTypes.length; i++) {
			if(i + 1 == parallelTypes.length)
				return true;
			if(!parallelTypes[parallelTypes.length - 1].equals(parallelTypes[i]))
				break;
		}
		//any noun is considered the same
		//S, SBAR, and NP are considered the same (not looking for sentence structure)
		//possessive nouns and possessive pronouns are considered the same
		//VP and any verbs are considered the same
		for(String[] group : tagGroups)
			if(UtilityMethods.arrayContains(group, parallelTypes[parallelTypes.length - 1]))
				for(int i = 0; i < parallelTypes.length; i++) {
					if(i + 1 == parallelTypes.length)
						return true;
					if(!UtilityMethods.arrayContains(group, parallelTypes[i]))
						break;
				}
		//special case: CC followed by possessive with intermediary noun - must be checked here because compound nouns which are collectively possessive are marked only on the final noun
		if(rightIsPossessive)
			for(int i = 0; i < parallelTypes.length; i++) {
				if(i + 1 == parallelTypes.length)
					return true;
				if(!parallelTypes[i].equals("PRP$"))
					break;
			}
		
		return false;
	}
	
}
