package bsChecker;

import java.util.ArrayList;

import opennlp.tools.util.Span;

/**
 * @author Dalal
 * Finds ambiguous pronoun references. (7)
 */
public class ErrorAmbiguousPronoun extends Error {
	private static final int ERROR_NUMBER = 7;

	private static final String[] PRONOUNS = {"she", "her", "hers", "he", "him", "his", "it", "its", "they", "them", "their", "theirs"};

	/**
	 * for testing purposes
	 */
	public static void main (String[] args) {
		Error.setupOpenNLP();
		String input = "I saw Mike and Bob. I also saw Ted and John, but I beat him.";
		System.out.println("\ninput: " + input + "\n");
		ArrayList<int[]> errors = new ErrorPassiveVoice().findErrors(input);
		sort(errors);
		printErrors(errors, input);
	}

	/**
	 * finds all ambiguous pronoun references in the given paragraph
	 * note: not yet converted to token system
	 * @param line paragraph to check
	 * @return ArrayList int[3] representing errors where [0] is the beginning token index, [1] is ending token index, [2] is the type of error (7)
	 */
	@Override
	public ArrayList<int[]> findErrors(String line) {
		ArrayList<int[]> errors = new ArrayList<int[]>();

		String[] sentences = sentenceDetector.sentDetect(line);
		int prevNouns = 0;
		int totLen = 0;
		for(int i = 0; i < sentences.length; i++) {
			int curNouns = 0;
			int[] pnCounts = new int[PRONOUNS.length];
			String[] words = tokenizer.tokenize(sentences[i]);
			ArrayList<String> names = findName(words);
			for(String word : words) {
				int pInd = 0;
				if((pInd = pronounInd(word)) != -1) {
					if(prevNouns + curNouns > 1) {
						int[] err = {totLen + locationOf(sentences[i],PRONOUNS[pInd], pnCounts[pInd]),
								totLen + locationOf(sentences[i],PRONOUNS[pInd], pnCounts[pInd]) + word.length(), ERROR_NUMBER};
						errors.add(err);
					}
					pnCounts[pInd]++;
				}
				if(names.contains(word)){
					prevNouns=0;
					curNouns++;
				}
			}
			totLen+=sentences[i].length();
			prevNouns = curNouns;
		}


		return errors;
	}

	private int pronounInd(String word) {
		for(int i = 0; i < PRONOUNS.length; i++)
			if(word.equalsIgnoreCase(PRONOUNS[i]))
				return i;
		return -1;
	}

	public static ArrayList<String> findName(String[] words) {
		ArrayList<String> test = new ArrayList<String>();
		Span spans[] = nameFinder.find(words);
		for(Span span : spans){
			test.add(words[span.getStart()]);
		}
		return test;
	}
}
