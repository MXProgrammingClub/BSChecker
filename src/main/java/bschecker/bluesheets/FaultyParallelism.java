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
	
	private final String[][] tagGroups = {{"NN", "NNS", "NNP", "NNPS"}, {"S", "SBAR"}, {"POS", "PRP$"}};
	
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
				if(ccIsFaultyParallelism(ccParse))
					errors.add(new Error(ccParse, tokenOffset));
			tokenOffset += tags.length;
		}
		return errors;
	}
	
	/**
	 * tests whether or not a coordinating conjunction is part of a faulty parallelism
	 * @param parse the parse from the CC
	 * @return true if there is faulty parallelism, false otherwise
	 */
	private boolean ccIsFaultyParallelism(Parse parse) {
		Parse ccParse = parse.getParent(), left = null, right = null;
		//special case: CC in CONJP e.g. "but rather"
		if(ccParse.getParent().getType().equals("CONJP"))
			ccParse = ccParse.getParent();
		
		Parse[] siblings = ccParse.getParent().getChildren();
		int siblingIndex = UtilityMethods.getSiblingIndex(ccParse);
		//this catches exceptions generally caused by parsing errors - NOTE: these cases may in fact be errors, but with the current implementation they are ambiguous
		if(siblingIndex == 0 || siblingIndex == siblings.length - 1)
			return false;
		left = siblings[siblingIndex - 1];
		//special case: CC preceded by comma e.g. 'IC, CC IC' structure
		if(left.getType().equals(","))
			left = siblings[siblingIndex - 2];
		right = siblings[siblingIndex + 1];
		//special case: CC followed by comma separated phrase
		//special case: CC followed by adverb e.g. "and thus"
		//special case: CC followed by possessive with intermediary noun
		if(right.getType().equals(",") || right.getType().equals("ADVP") || siblings.length > siblingIndex + 2 && siblings[siblingIndex + 2].getType().equals("POS"))
			right = siblings[siblingIndex + 2];
		
		String leftType = left.getType(), rightType = right.getType();
		LogHelper.getLogger(this).debug("Type to left: " + leftType + "\t" + "Type to right: " + rightType);
		//special case: correlative conjunction e.g. "whether or not" - parsed as IN CC RB
		if(leftType.equals(rightType) || leftType.equals("IN") && rightType.equals("RB"))
			return false;
		//any noun is considered the same
		//S and SBAR are considered the same (not looking for sentence structure)
		//possessive nouns and possessive pronouns are considered the same
		for(String[] group : tagGroups)
			if(UtilityMethods.arrayContains(group, leftType) && UtilityMethods.arrayContains(group, rightType))
				return false;
		
		return true;
	}
	
}
