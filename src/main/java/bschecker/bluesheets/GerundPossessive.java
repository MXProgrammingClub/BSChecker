package main.java.bschecker.bluesheets;

import java.util.ArrayList;

import main.java.bschecker.util.Error;
import main.java.bschecker.util.ErrorList;
import main.java.bschecker.util.Tools;

/**
 * Finds errors where gerunds incorrectly lack a possessive. (13)
 * @author JeremiahDeGreeff
 */
public class GerundPossessive extends Bluesheet {
	public final int ERROR_NUMBER = 13;
	
	/**
	 * for testing purposes
	 */
	public static void main(String[] args) {
		Tools.initializeOpenNLP();
		String input = "";
		System.out.println("\ninput: " + input + "\n\n" + (new GerundPossessive().findErrors(input)).tokensToChars(0, new ArrayList<Integer>()));
	}
	
	/**
	 * default constructor
	 */
	public GerundPossessive() {
		this(true);
	}
	
	/**
	 * constructor
	 * @param CheckedWhenAnalyzed true if errors of this type should be looked for when the text is analyzed, false otherwise
	 */
	public GerundPossessive(boolean CheckedWhenAnalyzed) {
		super(CheckedWhenAnalyzed);
	}

	/**
	 * finds all errors where gerunds are not preceded by a possessive when they should be in the given paragraph
	 * known issues: catches cases where the supposed gerund is in fact a participle and is thus not an error
	 * @param line the paragraph in which to find errors
	 * @param parses a String array of the parses of each sentence of the line
	 * @return an ErrorList which for each error references start and end tokens, the bluesheet number (13), and, optionally, a note
	 */
	@Override
	protected ErrorList findErrors(String line, String[] parses) {
		String[] tokens = Tools.getTokenizer().tokenize(line);
		String[] tags = Tools.getPOSTagger().tag(tokens);
		
		ErrorList errors = new ErrorList(line, true);
		for(int i = 1; i < tokens.length; i++)
			if(tags[i].equals("VBG") && (tags[i - 1].equals("PRP") || ((tags[i - 1].equals("NN") || tags[i - 1].equals("NNS") || tags[i - 1].equals("NNP") || tags[i - 1].equals("NNPS")) && tokens[i - 1].indexOf('\'') == -1)))
					errors.add(new Error(i - 1, i, ERROR_NUMBER, true));
		
		return errors;
	}
}
