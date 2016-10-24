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
	private static final String[] PUNCTUATION = {".", ",", ":", ";", "?", "!"};

	/**
	 * Imports the list of words of saying or thinking.
	 * @return The set of words.
	 */
	private static HashSet<String> importVerbs()
	{
		HashSet<String> verbs = new HashSet<String>();
		Scanner scan = null;
		try
		{
			scan = new Scanner(new File(FILE_NAME));
		} catch (FileNotFoundException e){} //Won't happen

		while(scan.hasNext())
		{
			verbs.add(scan.nextLine());
		}
		return verbs;
	}
	
	/**
	 * for testing purposes
	 */
	public static void main (String[] args) {
		Error.setupOpenNLP();
		String input = "";
		printErrors(new ErrorQuotationForm().findErrors(input), input);
	}

	@Override
	public ArrayList<int[]> findErrors(String text)
	{
		ArrayList<int[]> errors = new ArrayList<int[]>();

		String tokens[] = tokenizer.tokenize(text);
//		System.out.println(Arrays.toString(tokens));
		for(int i = 0, count = 0; i < tokens.length; i++)
		{
			if(tokens[i].contains("\"")) //finds opening quotation
			{
				for(int j = i + 1; j < tokens.length; j++)
				{
					if(tokens[j].contains("\"")) //finds ending quotation
					{
						if(findErrorsFront(tokens, i, j))
						{
							int loc = Error.locationOf(text, "\"", count);
							if(text.charAt(loc - 3) == ',') errors.add(new int[]{loc - 3, loc - 3, ERROR_NUMBER});
							else errors.add(new int[]{text.lastIndexOf(' ', loc - 3) + 1, loc - 1, ERROR_NUMBER});
						}
						if(findErrorsBack(tokens, i, j))
						{
							int loc = Error.locationOf(text, "\"", count + 1);
							errors.add(new int[]{loc, loc, ERROR_NUMBER});
						}
						i = j;
						count += 2;
						break;
					}
				}
			}
		}
		return errors;
	}

	/**
	 * Finds errors in running in the quotation.
	 * @param tokens The tokens from the nlp tokenizer.
	 * @param start The starting index of the quotation.
	 * @param end The ending index of the quotation
	 * @return true if an error was found, false otherwise.
	 */
	private boolean findErrorsFront(String[] tokens, int start, int end)
	{
		if(tokens[start - 1].equals(":")) return false; //there is no issue associated with running in a quotation with a colon
		else if(tokens[start - 1].equals(",")) //if there is a comma before, the word before should be a verb
		{
			if(VERB_SET.contains(tokens[start - 2])) return false;
			else return true;
		}
		else //if there is a word before, it should not be a verb
		{
			if(VERB_SET.contains(tokens[start - 1])) return true;
			else return false;
		}
	}

	/**
	 * Finds errors in citing the quotation.
	 * @param tokens The tokens from the nlp tokenizer.
	 * @param start The starting index of the quotation.
	 * @param end The ending index of the quotation
	 * @return true if an error is found, false otherwise
	 */
	private boolean findErrorsBack(String[] tokens, int start, int end)
	{
		if(tokens[end].contains("(") || end + 1 < tokens.length && tokens[end + 1].contains("("))
		{
			if(isPunctuation(tokens[end - 1])) return true; //error if cited and punctuation inside
			else return false;
		}
		else if(end + 1 < tokens.length && isPunctuation(tokens[end + 1])) return true; //error if not cited and punctuation outside
		else return false;
	}

	/**
	 * Returns whether the given string is in the array.
	 * @param str The possible punctuation.
	 * @return Whether str is punctuation.
	 */
	private boolean isPunctuation(String str)
	{
		for(String punc: PUNCTUATION)
		{
			if(str.equals(punc)) return true;
		}
		return false;
	}
}