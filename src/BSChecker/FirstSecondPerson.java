/**
 * 
 */
package BSChecker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

/**
 * @author Dalal
 * Find (& correct?) first/second person -> BS error #3
 */
public class FirstSecondPerson extends Error {
	
	
	public static void main (String[] args) {
		ArrayList<int[]> errors = new FirstSecondPerson().findErrors("Hi. How are you? This is Mike.");
		for (int[] error : errors)
			System.out.println(error[0] + " " + error[1]);
	}
	
	/* (non-Javadoc)
	 * @see BSChecker.Error#findErrors(java.lang.String)
	 */
	@Override
	public ArrayList<int[]> findErrors(String text) {
		//check for quotation
		ArrayList<int[]> errors = new ArrayList<int[]>();
		String[] tenses = {"I","me", "my", "we", "us", "our", "you", "your"};
		String[] tokens = Tokenize(text);
		
		for (String tense : tenses) {
			int index = 0;
			while (index < text.length()) {
				int errIndex = text.indexOf(tense, index);
				int errEndIndex = errIndex + tense.length();
				if (errIndex < -1) {
					int[] error = {errIndex, errEndIndex};
					errors.add(error);
					index = errEndIndex;
				}
			}
		}
		
		return errors;
	}
	
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
