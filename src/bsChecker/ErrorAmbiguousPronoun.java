package bsChecker;

import java.util.ArrayList;

import opennlp.tools.util.Span;

/**
 * @author Dalal
 * Finds ambiguous pronoun references. (7)
 */
public class ErrorAmbiguousPronoun extends Error {
	private static final int ERROR_NUMBER = 7;
	private static final String[] PRONOUNS = {"she", "her", "hers", "herself", "he", "him", "his", "himself", "they", "them", "their", "theirs", "themselves"};

	/**
	 * for testing purposes
	 */
	public static void main (String[] args) {
		Error.setupOpenNLP();
		String input = "Consequently, Bob threw the ball to himself";
		System.out.println("\ninput: " + input + "\n");
		ArrayList<int[]> errors = new ErrorAmbiguousPronoun().findErrors(input);
		sort(errors);
		printErrors(tokensToChars(input, errors, 0), input);
	}

	/**
	 * finds all ambiguous pronoun references in the given paragraph
	 * known issues: does not distinguish between genders, does not look for anything other than names
	 * @param line paragraph to check
	 * @return ArrayList int[3] representing errors where [0] is the beginning token index, [1] is ending token index, [2] is the type of error (7)
	 */
	@Override
	public ArrayList<int[]> findErrors(String line) {
		String[] sentences = sentenceDetector.sentDetect(line);
		ArrayList<int[]> errors = new ArrayList<int[]>();
		int prevSentenceNouns, curSentenceNouns = 0, tokenOffset = 0, index;
		for(int i = 0; i < sentences.length; i++) {
			String[] words = tokenizer.tokenize(sentences[i]);
			prevSentenceNouns = curSentenceNouns;
			curSentenceNouns = 0;
			ArrayList<String> names = findName(words);
			for(int j = 0; j < words.length; j++)
				if(arrayContains(PRONOUNS, words[j]) && prevSentenceNouns + curSentenceNouns >= 2)
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
