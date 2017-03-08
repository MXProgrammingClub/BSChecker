package main.java.bschecker.bluesheets;

import java.util.ArrayList;

import main.java.bschecker.util.Error;
import main.java.bschecker.util.ErrorList;
import main.java.bschecker.util.Tools;
import main.java.bschecker.util.UtilityMethods;
import opennlp.tools.util.Span;

/**
 * Finds ambiguous pronoun references. (7)
 * @author Dalal
 */
public class AmbiguousPronoun extends Bluesheet {
	public final int ERROR_NUMBER = 7;
	private static final String[] PRONOUNS = {"she", "her", "hers", "herself", "he", "him", "his", "himself", "they", "them", "their", "theirs", "themselves"};

	/**
	 * for testing purposes
	 */
	public static void main (String[] args) {
		Tools.initializeOpenNLP();
		String input = "";
		System.out.println("\ninput: " + input + "\n\n" + (new AmbiguousPronoun().findErrors(input)).tokensToChars(0, new ArrayList<Integer>()));
	}
	
	/**
	 * default constructor
	 */
	public AmbiguousPronoun() {
		this(true);
	}
	
	/**
	 * constructor
	 * @param CheckedWhenAnalyzed true if errors of this type should be looked for when the text is analyzed, false otherwise
	 */
	public AmbiguousPronoun(boolean CheckedWhenAnalyzed) {
		super(CheckedWhenAnalyzed);
	}

	/**
	 * finds all ambiguous pronoun references in the given paragraph
	 * known issues: does not distinguish between genders, does not look for anything other than names
	 * @param line the paragraph in which to find errors
	 * @return an ErrorList which for each error references start and end tokens, the bluesheet number (7), and, optionally, a note
	 */
	@Override
	protected ErrorList findErrors(String line) {
		String[] sentences = Tools.getSentenceDetector().sentDetect(line);
		ErrorList errors = new ErrorList(line, true);
		int prevSentenceNouns, curSentenceNouns = 0, tokenOffset = 0, index;
		for(int i = 0; i < sentences.length; i++) {
			String[] words = Tools.getTokenizer().tokenize(sentences[i]);
			prevSentenceNouns = curSentenceNouns;
			curSentenceNouns = 0;
			ArrayList<String> names = findName(words);
			for(int j = 0; j < words.length; j++)
				if(UtilityMethods.arrayContains(PRONOUNS, words[j]) && prevSentenceNouns + curSentenceNouns >= 2)
						errors.add(new Error(tokenOffset + j, ERROR_NUMBER, true));	
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
		Span spans[] = Tools.getNameFinder().find(words);
		for(Span span : spans)
			if(!names.contains(words[span.getStart()]))
				names.add(words[span.getStart()]);
		return names;
	}
}
