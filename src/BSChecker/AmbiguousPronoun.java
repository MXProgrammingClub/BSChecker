/**
 * 
 */
package BSChecker;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;

/**
 * @author Dalal
 * Finds (& print out locations of) ambiguous pronoun -> BS error #7
 */
public class AmbiguousPronoun extends Error {
	
	public static void main (String[] args) {
		System.out.println("test");
		ArrayList<int[]> errors = new AmbiguousPronoun().findErrors("Mike and Bob. He runs.");
		for (int[] error : errors)
			System.out.println(error[0] + " " + error[1]);
	}

	/* (non-Javadoc)
	 * @see BSChecker.Error#findErrors(java.lang.String)
	 */
	@Override
	public ArrayList<int[]> findErrors(String text) {
		ArrayList<int[]> errors = new ArrayList<int[]>();
		ArrayList<Span[]> names = findName(Tokenize(SentenceDetect(text)));
		ArrayList<String[]> words = Tokenize(SentenceDetect(text));
		
		for (int i = 0; i < names.size(); i++) {
			String[] sentence = words.get(i);
			Span[] nouns = names.get(i);
			int length = nouns.length;
			String[] pronouns = {"she", "her", "he", "him", "it", "they", "them"};
			if (length > 1) {
				int endIndex = nouns[length - 1].getEnd();
				
				//ARRAYINDEXOUTOFBOUNDSEXCEPTION HERE
				String noun = sentence[endIndex-1];
				
				int nounIndex = text.indexOf(noun);
				for (String pronoun : pronouns) {
					int index = text.indexOf(pronoun, nounIndex);
					if (index > -1) {
						errors.add(new int[]{index, index + pronoun.length()});
					}
				}
			}
		}
		
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
	
	public static ArrayList<String[]> Tokenize(String[] sentences) {
		try {
			InputStream is = new FileInputStream("lib/en-token.bin");
		 
			TokenizerModel model = new TokenizerModel(is);
		 
			Tokenizer tokenizer = new TokenizerME(model);
			
			ArrayList<String[]> words = new ArrayList<String[]>();
			
			for (String sentence : sentences) {			
				words.add(tokenizer.tokenize(sentence));
			}
			
			return words;
		}
		catch(IOException e) {
			return null;
		}
	}
	
	
	public static ArrayList<Span[]> findName(ArrayList<String[]> sentences) {
		try {
			InputStream is = new FileInputStream("lib/en-ner-person.bin");
		 
			TokenNameFinderModel model = new TokenNameFinderModel(is);
			is.close();
		 
			NameFinderME nameFinder = new NameFinderME(model);
			
			ArrayList<Span[]> names = new ArrayList<Span[]>();
			
			for (String[] sentence : sentences) {
				names.add(nameFinder.find(sentence));
			}
			
			return names;
		}
		catch(IOException e) {
			return null;
		}
	}
}
