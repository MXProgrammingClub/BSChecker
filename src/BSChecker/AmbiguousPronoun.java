/**
 * 
 */
package BSChecker;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;

/**
 * @author Dalal
 * Finds (& print out locations of) ambiguous pronoun -> BS error #7
 */
public class AmbiguousPronoun extends Error {
	
	public static void main (String[] args) {
		ArrayList<int[]> errors = new AmbiguousPronoun().findErrors("Hi. How are you? This is Mike.");
		for (int[] error : errors)
			System.out.println(error[0] + " " + error[1]);
	}

	/* (non-Javadoc)
	 * @see BSChecker.Error#findErrors(java.lang.String)
	 */
	@Override
	public ArrayList<int[]> findErrors(String text) {
		ArrayList<int[]> errors = new ArrayList<int[]>();
		Span[] names = findName(SentenceDetect(text));
		
		
		
		return errors;
	}
	
	public static String[] SentenceDetect(String text) {
		try {
			// always start with a model, a model is learned from training data
			InputStream is = new FileInputStream("lib/en-sent.bin");
			SentenceModel model = new SentenceModel(is);
			SentenceDetectorME sdetector = new SentenceDetectorME(model);
			is.close();
			
			return sdetector.sentDetect(text);
		}
		catch(IOException e) {
			return null;
		}
	}
	
	public static Span[] findName(String[] sentences) {
		try {
			InputStream is = new FileInputStream("lib/en-ner-person.bin");
		 
			TokenNameFinderModel model = new TokenNameFinderModel(is);
			is.close();
		 
			NameFinderME nameFinder = new NameFinderME(model);
		 
			return nameFinder.find(sentences);
		}
		catch(IOException e) {
			return null;
		}
	}

}
