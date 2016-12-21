package error;

import java.util.ArrayList;

import util.ErrorList;
import util.UtilityMethods;

/**
 * Finds errors in pronoun case. (6)
 * @author Leo
 */
public class PronounCase extends Error {
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
		UtilityMethods.setupOpenNLP();
		String input = "However, he died and instead of adapting political systems from he apple, he died.";
		System.out.println("\ninput: " + input + "\n");
		ErrorList errors = new PronounCase().findErrors(input);
		errors.sort();
		errors.tokensToChars(0);
		System.out.println(errors);
	}
	
	/**
	 * default constructor
	 */
	public PronounCase() {
		this(true);
	}

	/**
	 * constructor
	 * @param isChecked true if errors of this type should be looked for when the text is analyzed, false otherwise
	 */
	public PronounCase(boolean isChecked) {
		super(6, isChecked);
	}

	/**
	 * finds all errors in pronoun case within the paragraph
	 * @param line the paragraph in which to find errors
	 * @return an ErrorList of int[3] pointers to the indices of the start and end tokens of an error
	 * 			int[0], int[1] are start and end tokens of the error
	 * 			int[2] is the error number (6)
	 */
	@Override
	protected ErrorList findErrors(String line) {
		String[] tokens = tokenizer.tokenize(line);
		String[] tags = posTagger.tag(tokens);
		ArrayList<Integer> pronounIndices = new ArrayList<Integer>();
		for(int i = 0; i < tokens.length; i++) {
			String word = tokens[i];
			if(UtilityMethods.arrayContains(ALLPN, word))
				pronounIndices.add(i);
		}
		ErrorList errors = new ErrorList(line, false);
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
	 * @param errorIndices the list of all found errors which will be updated with any new errors that are found
	 */
	private void posPronoun(ArrayList<Integer> pronounIndices, String[] tokenList, String[] tagList, ErrorList errorIndices) {
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
						errorIndices.add(new int[] {index, index, ERROR_NUMBER});
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
	 * @param errorIndices the list of all found errors which will be updated with any new errors that are found
	 */
	private void subjPronoun(ArrayList<Integer> pronounIndices, String[] tokenList, String[] tagList, ErrorList errorIndices) {
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
						errorIndices.add(new int[] {index, index, ERROR_NUMBER});
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
	 * @param errorIndices the list of all found errors which will be updated with any new errors that are found
	 */
	private void objPronoun(ArrayList<Integer> pronounIndices, String[] tokenList, String[] tagList, ErrorList errorIndices) {
//		System.out.println("Looking for Objectives in: " + pronounIndices);
		for(int element = 0; element < pronounIndices.size(); element++) {
			int index = pronounIndices.get(element);
			if (index > 0) {
				int previousWordIndex = index - 1;
				// checking for a verb before the pronoun
				if(tagList[previousWordIndex].charAt(0) == 'V') {
					// so the pronoun should be objective
					if(!UtilityMethods.arrayContains(OBJ, tokenList[index])) {
						errorIndices.add(new int[] {index, index, ERROR_NUMBER});
//						System.out.println("subjective error: " + tokenList[index]);
					}
					pronounIndices.remove(element);
					element--;
				}
			}
		}
	}
}