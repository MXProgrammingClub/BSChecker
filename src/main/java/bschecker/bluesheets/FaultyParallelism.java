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
	
	private final String[][] tagGroups = {{"NN", "NNS", "NNP", "NNPS"}, {"S", "SBAR"}, {"POS", "PRP$"}, {"IN", "RB"}};
	
	
	/**
	 * Finds any instances of faulty parallelism in a paragraph.
	 * @param line the paragraph in which to find errors
	 * @param parses a String array of the parses of each sentence of the line
	 * @return an ErrorList which for each Error references start token, end token, and, optionally, a note
	 */
	@Override
	protected ErrorList findErrors(String line, Parse[] parses) {
		ErrorList errors = new ErrorList(line);
		String[] sentences = Tools.getSentenceDetector().sentDetect(line);
		int tokenOffset = 0;
		
		//temporary
		String[] parseStrings = new String[parses.length];
		for(int i = 0; i < parses.length; i++) {
			StringBuffer sb = new StringBuffer(sentences[i].length() * 4);
			parses[i].show(sb);
			parseStrings[i] = sb.toString();
		}
		
		for(int i = 0; i < sentences.length; i++){
			errors.addAll(findErrorsInSentence(line, parseStrings[i], UtilityMethods.findTokenTags(sentences[i], "CC"), tokenOffset));
			tokenOffset += Tools.getTokenizer().tokenize(sentences[i]).length;
		}
		return errors;
	}
	
	/**
	 * finds any Faulty Parallelism in a sentence
	 * @param line the full line which the sentence is a part of (for returning purposes)
	 * @param parse the string representation of the parse of the sentence
	 * @param ccTokens the token indices of coordinating conjunctions (for returning purposes)
	 * @param tokenOffset the number of tokens which have occurred in earlier sentences (for returning purposes)
	 * @return an ErrorList which for each error in this sentence references start and end tokens, the bluesheet number (11), and, optionally, a note
	 */
	private ErrorList findErrorsInSentence(String line, String parse, ArrayList<Integer> ccTokens, int tokenOffset) {
		ErrorList errors = new ErrorList(line);
		String simplifiedParse = UtilityMethods.simplifyParse(parse);
		LogHelper.getLogger(this).debug(parse);
		LogHelper.getLogger(this).debug(simplifiedParse);
		int ccIndex = -1;
		for(int ccNum = 0; ccNum < ccTokens.size(); ccNum++){
			ccIndex = simplifiedParse.indexOf("CC", ccIndex + 1);
			//catch for if the posTagger identifies a CC which the parser does not
			if(ccIndex == -1)
				break;
			
			int[] rightResult = findRight(simplifiedParse, ccIndex);
			int right = rightResult[0], left = findLeft(simplifiedParse, ccIndex, rightResult[1]);
			String rightType = simplifiedParse.substring(right, (simplifiedParse.indexOf(' ', right) < simplifiedParse.indexOf(')', right) && simplifiedParse.indexOf(' ', right) != -1) ? simplifiedParse.indexOf(' ', right) : simplifiedParse.indexOf(')', right));
			String leftType = simplifiedParse.substring(left, (simplifiedParse.indexOf(' ', left) < simplifiedParse.indexOf(')', left)) ? simplifiedParse.indexOf(' ', left) : simplifiedParse.indexOf(')', left));
			LogHelper.getLogger(this).debug("Type to Left: \"" + leftType + "\" (" + left + "-" + (left + leftType.length()) + ") -- Type to Right: \"" + rightType + "\" (" + right + "-" + (right + rightType.length()) + ")");
			if(!leftType.equals(rightType)){
				boolean error = true;
				for(String[] group : tagGroups)
					if(UtilityMethods.arrayContains(group, leftType) && UtilityMethods.arrayContains(group, rightType)){
						error = false;
						break;
					}
				if(error){
					LogHelper.getLogger(this).debug("Error!");
					errors.add(new Error(ccTokens.get(ccNum) + tokenOffset));
				}
			}
		}
		return errors;
	}
	
	/**
	 * a private helper method which finds the index of the tag to the right of a coordinating conjunction
	 * @param simplifiedParse a simplified parse String representation of the sentence structure
	 * @param ccIndex the starting index of the particular coordinating conjunction within that simplified parse
	 * @return and int[] with [0] being the starting index of the tag to the right and [1] being the offset to begin looking for the tag to the left (normally 4)
	 */
	private int[] findRight(String simplifiedParse, int ccIndex) {
		int right = simplifiedParse.indexOf('(', ccIndex) + 1, left = 4;
		
		//special case: CC in CONJP e.g. "but rather"
		if(simplifiedParse.substring(ccIndex - 7, ccIndex - 2).equals("CONJP")){
			right = ccIndex - 7;
			int net = -1;
			while(right <= simplifiedParse.length() && net != 0){
				if(simplifiedParse.charAt(right) == ')')
					net++;
				else if(simplifiedParse.charAt(right) == '(')
					net--;
				right++;
			}
			right += 2; //move from the character before the open parenthesis to the first character of the desired token
			left = 11;
		}
		
		String rightTag = simplifiedParse.substring(right, (simplifiedParse.indexOf(' ', right) < simplifiedParse.indexOf(')', right) && simplifiedParse.indexOf(' ', right) != -1) ? simplifiedParse.indexOf(' ', right) : simplifiedParse.indexOf(')', right));
		//special case: move past a comma
		if(simplifiedParse.charAt(right) == ',')
			right += 4;
		//special case: CC followed by adverb e.g. "and thus" but leave alone if part of a correlative conjunction e.g. "whether or not"
		if(rightTag.equals("ADVP") || (rightTag.equals("RB") && !simplifiedParse.substring(ccIndex - 5, ccIndex - 3).equals("IN")))
			right = simplifiedParse.indexOf('(', simplifiedParse.indexOf("RB", ccIndex)) + 1;
		//special case: if the following tag is POS, that should be the right tag
		if(simplifiedParse.indexOf('(', right) != -1 && simplifiedParse.substring(simplifiedParse.indexOf('(', right) + 1, simplifiedParse.indexOf('(', right) + 4).equals("POS"))
			right = simplifiedParse.indexOf('(', right) + 1;
		
		return new int[]{right, left};
	}
	
	/**
	 * a private helper method which finds the index of the tag to the left of a coordinating conjunction
	 * @param simplifiedParse a simplified parse String representation of the sentence structure
	 * @param ccIndex the starting index of the particular coordinating conjunction within that simplified parse
	 * @param offset the offset from ccIndex to begin looking for the left tag (usually 4)
	 * @return the starting index of the tag to the left
	 */
	private int findLeft(String simplifiedParse, int ccIndex, int offset) {
		int left = ccIndex - offset;
		int net = 1;
		while(left >= 0 && !(net == 0 && Character.isLetter(simplifiedParse.charAt(left + 2)))){
			if(simplifiedParse.charAt(left) == ')')
				net++;
			else if(simplifiedParse.charAt(left) == '(')
				net--;
			left--;
		}
		left += 2; //move from the character before the open parenthesis to the first character of the desired token
		
		return left;
	}
}
