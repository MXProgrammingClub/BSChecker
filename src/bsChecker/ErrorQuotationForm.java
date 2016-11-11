package bsChecker;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

/**
 * @author Julia
 * Finds errors in quotation form. (14)
 */
public class ErrorQuotationForm extends Error {
	private static final int ERROR_NUMBER = 14;	
	private static final String FILE_NAME = "SayingVerbs.txt"; //the location of the list of verbs of saying or thinking
	private static final HashSet<String> VERB_SET = importVerbs(); //the set of verbs of saying or thinking
	private static final String[] PUNCTUATION1 = {".", ","};
	private static final String[] PUNCTUATION2 = {":", ";"};
	
	/**
	 * Imports the list of words of saying or thinking.
	 * @return The set of words.
	 */
	private static HashSet<String> importVerbs()
	{
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
		Error.setupOpenNLP();
		String input = "he says \"hi\"; he says, \"hi\"(1), he says: \"hi\".";
		System.out.println("\ninput: " + input + "\n");
		ArrayList<int[]> errors = new ErrorQuotationForm().findErrors(input);
		sort(errors);
		printErrors(tokensToChars(input, errors, 0), input);
	}

	/**
	 * finds all errors with quotation form in the given paragraph
	 * known issues: none
	 * @param line paragraph to check
	 * @return ArrayList int[3] representing errors where [0] is the beginning token index, [1] is ending token index, [2] is the type of error (9)
	 */
	@Override
	public ArrayList<int[]> findErrors(String line)
	{
		String tokens[] = tokenizer.tokenize(line);
		ArrayList<int[]> errors = new ArrayList<int[]>();
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
		if(start > 0 && tokens[start - 1].equals(":")) {
			if(start > 1 && VERB_SET.contains(tokens[start - 2]))
				return 1; //error if there is a semicolon before and the word before it is a verb
		} else if(start > 0 && tokens[start - 1].equals(",")) {
			if(start > 1 && !VERB_SET.contains(tokens[start - 2]))
				return 2; //error if there is a comma before and the word before it is not a verb
		} else {
			if(start > 0 && VERB_SET.contains(tokens[start - 1]))
				return 3; //error if there is a not punctuation before and the word before is a verb
		} return 0;
	}

	/**
	 * Finds errors in citing the quotation.
	 * @param tokens The tokens from the nlp tokenizer.
	 * @param start The starting index of the quotation.
	 * @param end The ending index of the quotation
	 * @return 0 if no error, 1 if cited with punctuation incorrectly inside, 2 if not cited with punctuation incorrectly outside, 3 if not cited and punctuation incorrectly inside
	 */
	private int findErrorsBack(String[] tokens, int start, int end) {
		if(tokens[end].contains("(") || end + 1 < tokens.length && tokens[end + 1].contains("(")) {
			if(end > start && arrayContains(PUNCTUATION1, tokens[end - 1]) || arrayContains(PUNCTUATION2, tokens[end - 1]))
				return 1; //error if cited and punctuation inside
		} else {
			if(end + 1 < tokens.length && arrayContains(PUNCTUATION1, tokens[end + 1]))
				return 2; //error if not cited and period/comma outside
			if(end > start && arrayContains(PUNCTUATION2, tokens[end - 1]))
				return 3; //error if not cited and colon/semicolon inside
		} return 0;
	}
}