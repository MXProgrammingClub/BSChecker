package main.java.bschecker.bluesheets;

import java.util.ArrayList;

import main.java.bschecker.util.Error;
import main.java.bschecker.util.ErrorList;
import main.java.bschecker.util.Tools;
import main.java.bschecker.util.UtilityMethods;

/**
 * Finds errors in pronoun case. (6)
 * @author Leo
 */
public class PronounCase extends Bluesheet {
	public final int ERROR_NUMBER = 6;
	// arrays for various pronoun cases
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
		String input = "";
		System.out.println("\ninput: " + input + "\n\n" + (new PronounCase().findErrors(input)).tokensToChars(0, new ArrayList<Integer>()));
	}
	
	/**
	 * default constructor
	 */
	public PronounCase() {
		this(true);
	}

	/**
	 * constructor
	 * @param CheckedWhenAnalyzed true if errors of this type should be looked for when the text is analyzed, false otherwise
	 */
	public PronounCase(boolean CheckedWhenAnalyzed) {
		super(CheckedWhenAnalyzed);
	}

	/**
	 * finds all errors in pronoun case within the paragraph
	 * @param line the paragraph in which to find errors
	 * @return an ErrorList which for each error references start and end tokens, the bluesheet number (6), and, optionally, a note
	 */
	@Override
	protected ErrorList findErrors(String line) {
		String[] tokens = Tools.getTokenizer().tokenize(line);
		String[] tags = Tools.getPOSTagger().tag(tokens);
		
		ArrayList<Integer> pronounIndices = new ArrayList<Integer>();
		for(int i = 0; i < tokens.length; i++) {
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
	 * A method that looks at pronouns that should be possessive and returns the indices of any of those that are not
	 * @param pronounIndices the indices of all pronouns to be checked
	 * @param tokenList the tokens of the paragraph
	 * @param tagList the parts of speech of those tokens
	 * @param errorTokens the ErrorList of all found errors which will be updated with any new errors that are found
	 */
	private void posPronoun(ArrayList<Integer> pronounIndices, String[] tokenList, String[] tagList, ErrorList errorTokens) {
//		System.out.println("Looking for Possesives in: " + pronounIndices);
		for(int element = 0; element < pronounIndices.size(); element++) {
			int index = pronounIndices.get(element);
			if(index + 1 < tokenList.length) {
				int nextWordIndex = index + 1;
				//pass over adjectives and adverbs
				while(tagList[nextWordIndex].charAt(0) == 'J' || tagList[nextWordIndex].charAt(0) == 'R') {
					nextWordIndex++;
				}
				//checking for a noun after the pronoun or the use of "of" as a possessive e.g. friend (noun) of his (possessive pronoun)
				if(tagList[nextWordIndex].charAt(0) == 'N' || ((index >= 2) && (tagList[index - 1].equals("of")) && tagList[index - 2].charAt(0) == 'N')) {
					// so the pronoun should be possessive
					if(!(UtilityMethods.arrayContains(POSSES, tokenList[index]) || UtilityMethods.arrayContains(POSSESADJ, tokenList[index]))) {
						errorTokens.add(new Error(index, ERROR_NUMBER, true));
//						System.out.println("possesive error: " + tokenList[index]);
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
//		System.out.println("Looking for Subjectives in: " + pronounIndices);
		for(int element = 0; element < pronounIndices.size(); element++) {
			int index = pronounIndices.get(element);
			if(index + 1 < tokenList.length) {
				int nextWordIndex = index + 1;
				//pass over adverbs
				while(tagList[nextWordIndex].charAt(0) == 'R' || tagList[nextWordIndex].equals("MD")) {
					nextWordIndex++;
				}
				// checking for a verb before the pronoun
				if(tagList[nextWordIndex].charAt(0) == 'V') {
					// so the pronoun should be subjective
					if(!UtilityMethods.arrayContains(SUBJ, tokenList[index])) {
						errorTokens.add(new Error(index, ERROR_NUMBER, true));
//						System.out.println("subjective error: " + tokenList[index]);
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
//		System.out.println("Looking for Objectives in: " + pronounIndices);
		for(int element = 0; element < pronounIndices.size(); element++) {
			int index = pronounIndices.get(element);
			if (index > 0) {
				int previousWordIndex = index - 1;
				// checking for a verb before the pronoun
				if(tagList[previousWordIndex].charAt(0) == 'V') {
					// so the pronoun should be objective
					if(!UtilityMethods.arrayContains(OBJ, tokenList[index])) {
						errorTokens.add(new Error(index, ERROR_NUMBER, true));
//						System.out.println("subjective error: " + tokenList[index]);
					}
					pronounIndices.remove(element);
					element--;
				}
			}
		}
	}
}