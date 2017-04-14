package main.java.bschecker.bluesheets;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

import main.java.bschecker.util.Error;
import main.java.bschecker.util.ErrorList;
import main.java.bschecker.util.Tools;
import main.java.bschecker.util.UtilityMethods;

/**
 * Finds errors in quotation form. (14)
 * @author Julia
 * @author JeremiahDeGreeff
 */
public class QuotationForm extends Bluesheet {
	public final int ERROR_NUMBER = 14;
	private static final String FILE_NAME = "bin/resources/SayingVerbs.txt"; //the location of the list of verbs of saying or thinking
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
		System.out.println("\ninput: " + input + "\n\n" + (new QuotationForm().findErrors(input)).tokensToChars(0, new ArrayList<Integer>()));
	}
	
	/**
	 * default constructor
	 */
	public QuotationForm() {
		this(true);
	}
	
	/**
	 * constructor
	 * @param checkedWhenAnalyzed true if errors of this type should be looked for when the text is analyzed, false otherwise
	 */
	public QuotationForm(boolean checkedWhenAnalyzed) {
		super(checkedWhenAnalyzed);
	}

	/**
	 * finds all errors with quotation form in the given paragraph
	 * @param line the paragraph in which to find errors
	 * @param parses a String array of the parses of each sentence of the line
	 * @return an ErrorList which for each error references start and end tokens, the bluesheet number (14), and, optionally, a note
	 */
	@Override
	protected ErrorList findErrors(String line, String[] parses) {
		String tokens[] = Tools.getTokenizer().tokenize(line);
		ErrorList errors = new ErrorList(line, true);
		for(int i = 0; i < tokens.length; i++)
			if(tokens[i].contains("\"")) { //finds opening quotation
				int start = i + 1;
				if(tokens[i].substring(tokens[i].indexOf("\"") + 1).contains("\"")) //checks if opening and closing quotations are on the same token
					start = i;
				for(int j = start; j < tokens.length; j++)
					if(tokens[j].contains("\"")) { //finds closing quotation
						int errorFront = findErrorsFront(tokens, i, j);
						int errorBack = findErrorsBack(tokens, i, j);
						if(errorFront == 1)
							errors.add(new Error(i - 1, ERROR_NUMBER, true, "Do not introduce a quote with a colon after a verb of saying or thinking."));
						if(errorFront == 2)
							errors.add(new Error(i - 1, ERROR_NUMBER, true, "Do not introduce a quote with a comma without a verb of saying or thinking."));
						if(errorFront == 3)
							errors.add(new Error(i - 1, ERROR_NUMBER, true, "Do not introduce a quote with a verb of saying or thinking and no comma."));
						if(errorBack == 1)
							errors.add(new Error(j - 1, ERROR_NUMBER, true, "Do not put periods, commas, colons, or semicolons inside a quote which is sited."));
						if(errorBack == 2)
							errors.add(new Error(j + 1, ERROR_NUMBER, true, "Do not put periods or commas outside a quote which is not cited."));
						if(errorBack == 3)
							errors.add(new Error(j - 1, ERROR_NUMBER, true, "Do not put colons or semicolons inside a quote."));
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
				return 1; //error if there is a colon before and the word before it is a verb
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