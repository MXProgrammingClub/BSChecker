/**
 * 
 */
package BSChecker;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

/**
 * @author Dalal
 * Finds (& prints out locations of) incomplete sentences -> BS error #2
 */
public class IncompleteSentences extends Error {

	public static void main (String[] args) {
		ArrayList<int[]> errors = new IncompleteSentences().findErrors("Hi. How are. you? This is Mike.");
		for (int[] error : errors)
			System.out.println(error[0] + " " + error[1] + " " + error[2]);
	}

	@Override
	public ArrayList<int[]> findErrors(String text) {
		ArrayList<int[]> errors = new ArrayList<int[]>();
		
		
		return errors;
	}
	
	//SentenceDetector text first?
	public static String[] SentenceDetect(String text) {
		try {
			// always start with a model, a model is learned from training data
			InputStream is = new FileInputStream("en-sent.bin");
			SentenceModel model = new SentenceModel(is);
			SentenceDetectorME sdetector = new SentenceDetectorME(model);
			is.close();
			
			return sdetector.sentDetect(paragraph);
		}
		catch (IOException e) {
			return null;
		}
}

}
