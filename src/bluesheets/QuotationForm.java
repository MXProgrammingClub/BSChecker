package bluesheets;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

import util.TokenErrorList;
import util.Tools;
import util.UtilityMethods;

/**
 * Finds errors in quotation form. (14)
 * @author Julia
 * @author JeremiahDeGreeff
 */
public class QuotationForm extends Bluesheet {
	public final int ERROR_NUMBER = 14;
	private static final String FILE_NAME = "SayingVerbs.txt"; //the location of the list of verbs of saying or thinking
	private static final HashSet<String> VERB_SET = importVerbs(); //the set of verbs of saying or thinking
	private static final String[] PUNCTUATION1 = {".", ","};
	private static final String[] PUNCTUATION2 = {":", ";"};
	
	/**
	 * Imports the list of words of saying or thinking.
	 * @return The set of words.
	 */
	private static HashSet<String> importVerbs() {
		HashSet<String> verbs = new HashSet<String>();
		Scanner scan = null;
			try {scan = new Scanner(new File(FILE_NAME));}
			catch (FileNotFoundException e) {e.printStackTrace();}
		while(scan.hasNext()) {
			verbs.add(scan.nextLine());
		}
		return verbs;
	}
	
	/**
	 * for testing purposes
	 */
	public static void main (String[] args) {
		Tools.initializeOpenNLP();
		String input = "";
		System.out.println("\ninput: " + input + "\n");
		TokenErrorList errors = new QuotationForm().findErrors(input);
		errors.sort();
		System.out.println(errors.tokensToChars(0, new ArrayList<Integer>()));
	}
	
	/**
	 * default constructor
	 */
	public QuotationForm() {
		this(true);
	}
	
	/**
	 * constructor
	 * @param CheckedWhenAnalyzed true if errors of this type should be looked for when the text is analyzed, false otherwise
	 */
	public QuotationForm(boolean CheckedWhenAnalyzed) {
		super(CheckedWhenAnalyzed);
	}

	/**
	 * finds all errors with quotation form in the given paragraph
	 * known issues: doesn't see a preceding verb if other words between it and the quote
	 * @param line the paragraph in which to find errors
	 * @return a TokenErrorList of int[3] elements where [0] and [1] are start and end tokens of the error and [2] is the error number (14)
	 */
	@Override
	protected TokenErrorList findErrors(String line) {
		String tokens[] = Tools.getTokenizer().tokenize(line);
		TokenErrorList errors = new TokenErrorList(line);
		for(int i = 0; i < tokens.length; i++)
			if(tokens[i].contains("\"")) { //finds opening quotation
				int start = i + 1;
				if(tokens[i].substring(tokens[i].indexOf("\"") + 1).contains("\"")) //checks if opening and closing quotations are on the same token
					start = i;
				for(int j = start; j < tokens.length; j++)
					if(tokens[j].contains("\"")) { //finds closing quotation
						int errorFront = findErrorsFront(tokens, i, j);
						int errorBack = findErrorsBack(tokens, i, j);
						if(errorFront == 1 || errorFront == 2 || errorFront == 3)
							errors.add(new int[] {i - 1, i - 1, ERROR_NUMBER});
						if(errorBack == 1 || errorBack == 3)
							errors.add(new int[] {j - 1, j - 1, ERROR_NUMBER});
						if(errorBack == 2)
							errors.add(new int[] {j + 1, j + 1, ERROR_NUMBER});
						i = j;
						break;
					}
			}
		return errors;
	}

	/**
	 * Finds errors in running in the quotation.
	 * @param tokens The tokens from the nlp tokenizer.
	 * @param start The starting index of the quotation.
	 * @param end The ending index of the quotation
	 * @return 0 if no error, 1 if semicolon that should be a comma, 2 if comma that should be no punctuation, 3 if no punctuation that should be a comma
	 */
	private int findErrorsFront(String[] tokens, int start, int end)
	{
		if(start > 0 && tokens[start - 1].equals(":"))
			if(start > 1 && VERB_SET.contains(tokens[start - 2]))
				return 1; //error if there is a semicolon before and the word before it is a verb
		else if(start > 0 && tokens[start - 1].equals(","))
			if(start > 1 && !VERB_SET.contains(tokens[start - 2]))
				return 2; //error if there is a comma before and the word before it is not a verb
		else
			if(start > 0 && VERB_SET.contains(tokens[start - 1]))
				return 3; //error if there is a not punctuation before and the word before is a verb
		return 0;
	}

	/**
	 * Finds errors in citing the quotation.
	 * @param tokens The tokens from the nlp tokenizer.
	 * @param start The starting index of the quotation.
	 * @param end The ending index of the quotation
	 * @return 0 if no error, 1 if cited with punctuation incorrectly inside, 2 if not cited with punctuation incorrectly outside, 3 if not cited and punctuation incorrectly inside
	 */
	private int findErrorsBack(String[] tokens, int start, int end) {
		if(tokens[end].contains("(") || end + 1 < tokens.length && tokens[end + 1].contains("("))
			if(end > start && UtilityMethods.arrayContains(PUNCTUATION1, tokens[end - 1]) || UtilityMethods.arrayContains(PUNCTUATION2, tokens[end - 1]))
				return 1; //error if cited and punctuation inside
		else {
			if(end + 1 < tokens.length && UtilityMethods.arrayContains(PUNCTUATION1, tokens[end + 1]))
				return 2; //error if not cited and period/comma outside
			if(end > start && UtilityMethods.arrayContains(PUNCTUATION2, tokens[end - 1]))
				return 3; //error if not cited and colon/semicolon inside
		} return 0;
	}
}