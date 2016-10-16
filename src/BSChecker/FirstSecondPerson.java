/**
 * 
 */
package BSChecker;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;

import opennlp.tools.postag.POSModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

/**
 * @author Dalal
 * Finds (& print out locations of) first & second person -> BS error #3
 */
public class FirstSecondPerson extends Error {
	private static final int ERROR_NUMBER = 3;

	/**
	 * main method
	 * currently testing random String
	 */
	public static void main (String[] args) {
		ArrayList<int[]> errors = new FirstSecondPerson().findErrors("Hi. How are you? This is Mike.", null);
//		for (int[] error : errors)
//			System.out.println(error[0] + " " + error[1] + " " + error[2]);
	}

	/**
	 * finds all first & second person errors & returns indices
	 * @param text block of text to check
	 * @return ArrayList of beginning & ending indices of errors
	 */
	@Override
	public ArrayList<int[]> findErrors(String text, POSModel model) {
		//check for quotation
		ArrayList<int[]> errors = new ArrayList<int[]>();
		String[] tenses = {"I","me", "my", "we", "us", "our", "you", "your"};
		String[] tokens = Tokenize(text);

		for (String tense : tenses) {
			int index = 0;
			int textIndex = 0;
			while (index < tokens.length) {
				int textIndexEnd = textIndex + tense.length();
				if ((tokens[index]).equals(tense)) {
					int[] error = {textIndex, textIndexEnd, ERROR_NUMBER};
					errors.add(error);
				}
				textIndex = textIndexEnd;
				index++;
			}
		}		
		return errors;
	}

	/**
	 * breaks text into tokens and returns
	 * @param text block of text to Tolkenize
	 * @return String[] array of String tokens
	 */
	public static String[] Tokenize(String text) {
		try {
			InputStream is = new FileInputStream("lib/en-token.bin");

			TokenizerModel model = new TokenizerModel(is);

			Tokenizer tokenizer = new TokenizerME(model);

			return tokenizer.tokenize(text);
		}
		catch(IOException e)
		{
			return null;
		}
	}

}
