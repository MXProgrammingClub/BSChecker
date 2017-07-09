package main.java.bschecker.bluesheets;

import java.util.ArrayList;

import main.java.bschecker.util.Error;
import main.java.bschecker.util.ErrorList;
import main.java.bschecker.util.LogHelper;
import main.java.bschecker.util.Tools;
import main.java.bschecker.util.UtilityMethods;

/**
 * Finds errors in pronoun case. (6)
 * @author Leo
 */
public class PronounCase extends Bluesheet {
	
	public final int ERROR_NUMBER = 6;
	private static final String[] POSSESADJ = {"her", "his", "its", "their", "our", "my", "your", "whose"};
	private static final String[] POSSES = {"hers", "his", "its", "theirs", "ours", "mine", "yours", "whose"};
	private static final String[] OBJ = {"him", "her", "it", "them", "us", "me", "you", "whom"};
	private static final String[] SUBJ = {"he", "she", "it", "they", "we", "I", "you", "who"};
	private static final String[] ALLPN = {"he", "she", "it", "they", "we", "you", "his", "him", "her", "hers", "its", "their", "theirs", "them", "us", "our", "ours", "your", "yours", "who", "whose", "whom"};
	
	
	/**
	 * for testing purposes
	 */
	public static void main(String[] args) {
		Tools.initializeOpenNLP();
		String input = "As she successfully analyzes Jane.";
		System.out.println("\ninput: " + input + "\n\n" + (new PronounCase().findErrors(input)).tokensToChars(0, new ArrayList<Integer>()));
	}
	
	
	/**
	 * finds all errors in pronoun case within the paragraph
	 * @param line the paragraph in which to find errors
	 * @param parses a String array of the parses of each sentence of the line
	 * @return an ErrorList which for each error references start and end tokens, the bluesheet number (6), and, optionally, a note
	 */
	@Override
	protected ErrorList findErrors(String line, String[] parses) {
		String[] tokens = Tools.getTokenizer().tokenize(line);
		String[] tags = Tools.getPOSTagger().tag(tokens);
		
		ArrayList<Integer> pronounIndices = new ArrayList<Integer>();
		for(int i = 0; i < tokens.length; i++){
			String word = tokens[i];
			if(UtilityMethods.arrayContains(ALLPN, word))
				pronounIndices.add(i);
		}
		
		ErrorList errors = new ErrorList(line, true);
		posPronoun(pronounIndices, tokens, tags, errors);
		subjPronoun(pronounIndices, tokens, tags, errors);
		objPronoun(pronounIndices, tokens, tags, errors);	
		
		return errors;
	}
	
	/**
	 * A helper method that skips adjectives and adverbs and returns the correct nextWordIndex
	 * @param tagList the parts of speech of those tokens
	 * @param curWordIndex the index of the current word
	 * @param isForward whether look forward to the next word or look backward to previous word
	 */
	private int findNextWord(String[] tagList, int curWordIndex, boolean isForward, String[] tokenList) {
		if(isForward){
			int nextWordIndex = curWordIndex + 1;
			while(tagList[nextWordIndex].charAt(0) == 'J' || tagList[nextWordIndex].charAt(0) == 'R' || tagList[nextWordIndex].equals("VBN") || tokenList[nextWordIndex].equalsIgnoreCase("the"))
				nextWordIndex++;
			return nextWordIndex;
		} else {
			int nextWordIndex = curWordIndex - 1;
			while(tagList[nextWordIndex].charAt(0) == 'J' || tagList[nextWordIndex].charAt(0) == 'R' || tagList[nextWordIndex].equals("VBN") || tokenList[nextWordIndex].equalsIgnoreCase("the")) 
				nextWordIndex--;
			return nextWordIndex;
		}
	}
	
	/**
	 * A method that looks at pronouns that should be possessive and returns the indices of any of those that are not
	 * @param pronounIndices the indices of all pronouns to be checked
	 * @param tokenList the tokens of the paragraph
	 * @param tagList the parts of speech of those tokens
	 * @param errorTokens the ErrorList of all found errors which will be updated with any new errors that are found
	 */
	private void posPronoun(ArrayList<Integer> pronounIndices, String[] tokenList, String[] tagList, ErrorList errorTokens) {
		LogHelper.getLogger(ERROR_NUMBER).debug("Looking for Possesives in: " + pronounIndices);
		for(int element = 0; element < pronounIndices.size(); element++) 
		{
			int pronounIndex = pronounIndices.get(element);
			// assume no possessive pronouns occur at the end of the sentence
			if(pronounIndex + 1 < tokenList.length) 
			{
				int nextWordIndex = findNextWord(tagList, pronounIndex, true, tokenList);
//				//pass over adjectives and adverbs
//				while(tagList[nextWordIndex].charAt(0) == 'J' || tagList[nextWordIndex].charAt(0) == 'R') 
//				{
//					nextWordIndex++;
//				}
				//checking for a noun after the pronoun or the use of "of" as a possessive e.g. friend (noun) of his (possessive pronoun)
				if(tagList[nextWordIndex].charAt(0) == 'N' || tagList[nextWordIndex].equals("VBG") || ((pronounIndex >= 2) && (tagList[pronounIndex - 1].equals("of")) && tagList[pronounIndex - 2].charAt(0) == 'N')) {
					// so the pronoun should be possessive
					if(!(UtilityMethods.arrayContains(POSSES, tokenList[pronounIndex]) || UtilityMethods.arrayContains(POSSESADJ, tokenList[pronounIndex]))) {
						errorTokens.add(new Error(pronounIndex, ERROR_NUMBER, true, "Should be possesive pronoun."));
						LogHelper.getLogger(ERROR_NUMBER).debug("possesive error: " + tokenList[pronounIndex]);
					}
					pronounIndices.remove(element);
					element--;
				}
			}
		}
	}
	
	/**
	 * A method that looks at pronouns that should be subjective and returns the indices of any of those that are not
	 * @param pronounIndices the indices of all pronouns to be checked
	 * @param tokenList the tokens of the paragraph
	 * @param tagList the parts of speech of those tokens
	 * @param errorTokens the ErrorList of all found errors which will be updated with any new errors that are found
	 */
	private void subjPronoun(ArrayList<Integer> pronounIndices, String[] tokenList, String[] tagList, ErrorList errorTokens) {
		LogHelper.getLogger(ERROR_NUMBER).debug("Looking for Subjectives in: " + pronounIndices);
		for(int element = 0; element < pronounIndices.size(); element++) {
			int pronounIndex = pronounIndices.get(element);
			// assume no subjective pronouns occur at the end of the sentence
			if(pronounIndex + 1 < tokenList.length) 
			{
				int nextWordIndex = findNextWord(tagList, pronounIndex, true, tokenList);
				//pass over adverbs
//				while(tagList[nextWordIndex].charAt(0) == 'R' || tagList[nextWordIndex].equals("MD")) 
//				{
//					nextWordIndex++;
//				}
				// checking for a verb before the pronoun
				if(tagList[nextWordIndex].charAt(0) == 'V' && (tagList[nextWordIndex].length() < 3 || tagList[nextWordIndex].charAt(2) != 'G')) 
				{
					// when the pronoun is followed by a verb, the pronoun should be subjective
					if(!UtilityMethods.arrayContains(SUBJ, tokenList[pronounIndex])) 
					{
						errorTokens.add(new Error(pronounIndex, ERROR_NUMBER, true, "Should be subjective pronoun."));
						LogHelper.getLogger(ERROR_NUMBER).debug("subjective error: " + tokenList[pronounIndex]);
					}
					pronounIndices.remove(element);
					element--;
				}
			}	
		}
	}
	
	/**
	 * A method that looks at pronouns that should be objective and returns the indices of any of those that are not
	 * @param pronounIndices the indices of all pronouns to be checked
	 * @param tokenList the tokens of the paragraph
	 * @param tagList the parts of speech of those tokens
	 * @param errorTokens the ErrorList of all found errors which will be updated with any new errors that are found
	 */
	private void objPronoun(ArrayList<Integer> pronounIndices, String[] tokenList, String[] tagList, ErrorList errorTokens) {
		LogHelper.getLogger(ERROR_NUMBER).debug("Looking for Objectives in: " + pronounIndices);
		for(int element = 0; element < pronounIndices.size(); element++) {
			int pronounIndex = pronounIndices.get(element);
			// assume that no objective pronouns occur at the beginning of a sentence
			if (pronounIndex > 0) 
			{
				int nextWordIndex = findNextWord(tagList, pronounIndex, false, tokenList);
				if(tagList[nextWordIndex].charAt(0) == 'V' && (tagList[nextWordIndex].length() < 3 || tagList[nextWordIndex].charAt(2) != 'G')) 
				{
					// when the pronoun is preceded by a verb, the pronoun should be objective
					if(!UtilityMethods.arrayContains(OBJ, tokenList[pronounIndex])) 
					{
						errorTokens.add(new Error(pronounIndex, ERROR_NUMBER, true, "Should be objective pronoun."));
						LogHelper.getLogger(ERROR_NUMBER).debug("objective error: " + tokenList[pronounIndex]);
					}
					pronounIndices.remove(element);
					element--;
				}
			}
		}
	}
	
}