package errors;

import java.util.ArrayList;

import opennlp.tools.util.Span;
import util.UtilityMethods;

/**
 * Finds ambiguous pronoun references. (7)
 * @author Dalal
 */
public class AmbiguousPronoun extends Error {
	private static final String[] PRONOUNS = {"she", "her", "hers", "herself", "he", "him", "his", "himself", "they", "them", "their", "theirs", "themselves"};

	/**
	 * for testing purposes
	 */
	public static void main (String[] args) {
		UtilityMethods.setupOpenNLP();
		String input = "Consequently, Bob threw the ball to himself";
		System.out.println("\ninput: " + input + "\n");
		ErrorList errors = new AmbiguousPronoun().findErrors(input);
		errors.sort();
		errors.tokensToChars(0);
		System.out.println(errors);
	}
	
	/**
	 * constructor
	 */
	public AmbiguousPronoun() {
		super(7);
	}

	/**
	 * finds all ambiguous pronoun references in the given paragraph
	 * known issues: does not distinguish between genders, does not look for anything other than names
	 * @param line the paragraph in which to find errors
	 * @return an ErrorList of int[3] pointers to the indices of the start and end tokens of an error
	 * 			int[0], int[1] are start and end tokens of the error
	 * 			int[2] is the error number (7)
	 */
	@Override
	public ErrorList findErrors(String line) {
		String[] sentences = sentenceDetector.sentDetect(line);
		ErrorList errors = new ErrorList(line, false);
		int prevSentenceNouns, curSentenceNouns = 0, tokenOffset = 0, index;
		for(int i = 0; i < sentences.length; i++) {
			String[] words = tokenizer.tokenize(sentences[i]);
			prevSentenceNouns = curSentenceNouns;
			curSentenceNouns = 0;
			ArrayList<String> names = findName(words);
			for(int j = 0; j < words.length; j++)
				if(UtilityMethods.arrayContains(PRONOUNS, words[j]) && prevSentenceNouns + curSentenceNouns >= 2)
						errors.add(new int[] {tokenOffset + j, tokenOffset + j, ERROR_NUMBER});	
				else if((index = names.indexOf(words[j])) >= 0) {
					prevSentenceNouns = 0;
					curSentenceNouns++;
					names.remove(index);
				}
			tokenOffset += words.length;
		}
		return errors;
	}

	/**
	 * finds all names in the given array of words as determined by opennlp
	 * @param words the words to look through
	 * @return an ArrayList of all the words which have been recognized as names
	 */
	private static ArrayList<String> findName(String[] words) {
		ArrayList<String> names = new ArrayList<String>();
		Span spans[] = nameFinder.find(words);
		for(Span span : spans)
			if(!names.contains(words[span.getStart()]))
				names.add(words[span.getStart()]);
		return names;
	}
}
