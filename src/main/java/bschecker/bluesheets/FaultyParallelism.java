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
		for(int i = 0; i < parses.length; i++){
			String[] tags = Tools.getPOSTagger().tag(Tools.getTokenizer().tokenize(parses[i].getText()));
			ArrayList<Parse> ccParses = new ArrayList<Parse>();
			for(int j = 0; j < tags.length; j++)
				if(tags[j].equals("CC"))
					ccParses.add(UtilityMethods.getParseAtToken(parses[i], j));
			for(Parse parse : ccParses)
				if(ccIsFaultyParallelism(parse))
					errors.add(new Error(UtilityMethods.getIndexOfParse(parse) + tokenOffset));
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
		Parse ccParse = parse.getParent(), ccParent = ccParse.getParent(), left = null, right = null;
		//special case: CC in CONJP e.g. "but rather"
		if(ccParent.getType().equals("CONJP")){
			ccParent = ccParent.getParent();
			ccParse = ccParse.getParent();
		}
		for(int i = 0; i < ccParent.getChildCount(); i++)
			if(ccParent.getChildren()[i].equals(ccParse)){
				left = ccParent.getChildren()[i - 1];
				//special case: CC preceded by comma e.g. 'IC, CC IC' structure
				if(left.getType().equals(","))
					left = ccParent.getChildren()[i - 2];
				
				right = ccParent.getChildren()[i + 1];
				//special case: CC followed by comma separated phrase
				//special case: CC followed by adverb e.g. "and thus"
				//special case: CC followed by possessive with intermediary noun
				if(right.getType().equals(",") || right.getType().equals("ADVP") || ccParent.getChildren().length > i + 2 && ccParent.getChildren()[i + 2].getType().equals("POS"))
					right = ccParent.getChildren()[i + 2];
			}
		
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
