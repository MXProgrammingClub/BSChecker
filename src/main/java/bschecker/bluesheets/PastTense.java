package main.java.bschecker.bluesheets;

import java.util.ArrayList;

import main.java.bschecker.util.Error;
import main.java.bschecker.util.ErrorList;
import main.java.bschecker.util.Tools;
import main.java.bschecker.util.UtilityMethods;

/**
 * Finds verbs in the past tense. (1)
 * @author Leo
 * @author tedpyne
 * @author JeremiahDeGreeff
 */
public class PastTense extends Bluesheet {
	public final int ERROR_NUMBER = 1;
	private static final String[] TO_HAVE_CONJ = {"have", "has", "had", "having"};
	
	/**
	 * for testing purposes
	 */
	public static void main(String[] args) {
		Tools.initializeOpenNLP();
		String input = "";
		System.out.println("\ninput: " + input + "\n\n" + (new PastTense().findErrors(input)).tokensToChars(0, new ArrayList<Integer>()));
	}
	
	/**
	 * default constructor
	 */
	public PastTense() {
		this(true);
	}

	/**
	 * constructor
	 * @param checkedWhenAnalyzed true if errors of this type should be looked for when the text is analyzed, false otherwise
	 */
	public PastTense(boolean checkedWhenAnalyzed) {
		super(checkedWhenAnalyzed);
	}
	
	/**
	 * finds all instances of past tense in the given paragraph
	 * @param line the paragraph in which to find errors
	 * @param parses a String array of the parses of each sentence of the line
	 * @return an ErrorList which for each error references start and end tokens, the bluesheet number (1), and, optionally, a note
	 */
	@Override
	protected ErrorList findErrors(String line, String[] parses) {
		ErrorList errors = new ErrorList(line, true);
		String sentences[] = Tools.getSentenceDetector().sentDetect(line);
		int tokenOffset = 0;
		for(int i = 0; i < sentences.length; i++){
			String tokens[] = Tools.getTokenizer().tokenize(sentences[i]);
			String[] tags = Tools.getPOSTagger().tag(tokens);
			ErrorList sentenceErrors = new ErrorList(sentences[i], true);
			boolean inQuote = false, inIntroducedQuote = false;
			for(int j = 0; j < tags.length; j++){
				if(tokens[j].contains("\"")) {
					inIntroducedQuote = !inQuote && j > 0 && (tokens[j - 1].equals(",") || tokens[j - 1].equals(":"));
					inQuote = !inQuote;
				}
				if(!inIntroducedQuote && tags[j].equals("VBD"))
					sentenceErrors.add(new Error(j, ERROR_NUMBER, true));
				if(!inIntroducedQuote && tags[j].equals("VBN") && j > 0 && UtilityMethods.arrayContains(TO_HAVE_CONJ, tokens[j - 1]))
					sentenceErrors.add(new Error(j - 1, j, ERROR_NUMBER, true)); //does not currently look past intermediary adverbs
			}
			int[] errorTokens = new int[sentenceErrors.size()];
			for(int j = 0; j < sentenceErrors.size(); j++)
				errorTokens[j] = sentenceErrors.get(j).getEndIndex();
			boolean[] inSBAR = UtilityMethods.tokensInsideTag(errorTokens, parses[i], "SBAR");
			for(int j = 0; j < sentenceErrors.size(); j++)
				if(!inSBAR[j])
					errors.add(new Error(sentenceErrors.get(j).getStartIndex() + tokenOffset, sentenceErrors.get(j).getEndIndex() + tokenOffset, ERROR_NUMBER, true));
			tokenOffset += tokens.length;
		}
		return errors;
	}
}