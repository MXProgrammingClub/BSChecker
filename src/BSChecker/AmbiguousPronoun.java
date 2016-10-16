/**
 * 
 */
package BSChecker;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

/**
 * @author Dalal
 * Finds (& print out locations of) ambiguous pronoun -> BS error #7
 */
public class AmbiguousPronoun extends Error {
	private final static String[] PRONOUNS = {"she", "her", "he", "him", "it", "they", "them"};

	private static final int ERROR_NUMBER = 7;

	public static void main (String[] args) {
		String test = "I saw Mike and Bob. I also saw Ted and John, but I beat him.";
		ArrayList<int[]> errors = new AmbiguousPronoun().findErrors(test);
//		for (int[] error : errors){
//			System.out.println(error[0] + " " + error[1] + " " + error[2]);
//		}

	}

	@Override
	public ArrayList<int[]> findErrors(String text,POSModel model) {
		ArrayList<int[]> errors = new ArrayList<int[]>();

		String[] sentences = SentenceDetect(text);
		int prevNouns=0;
		int totLen = 0;
		for(int i = 0; i < sentences.length; i++){
			int curNouns = 0;
			int[] pnCounts = new int[PRONOUNS.length];
			String[] words = Tokenize(sentences[i]);
			ArrayList<String> names = findName(words);
			for(String word: words){
				int pInd = 0;
				if((pInd = pronounInd(word))!=-1){
					if(prevNouns+curNouns>1){
						int[] err = {totLen + locationOf(sentences[i],PRONOUNS[pInd], pnCounts[pInd]),
								totLen + locationOf(sentences[i],PRONOUNS[pInd], pnCounts[pInd]) + word.length(),ERROR_NUMBER};
						errors.add(err);
					}
					pnCounts[pInd]++;
				}
				if(names.contains(word)){
					prevNouns=0;
					curNouns++;
				}
			}
			totLen+=sentences[i].length();
			prevNouns = curNouns;
		}


		return errors;
	}

	private int pronounInd(String word) {
		for(int i = 0; i < PRONOUNS.length; i++){
			if(word.equalsIgnoreCase(PRONOUNS[i])) return i;
		}
		return -1;
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

	public static String[] Tokenize(String sentence) {
		try {
			InputStream is = new FileInputStream("lib/en-token.bin");

			TokenizerModel model = new TokenizerModel(is);

			Tokenizer tokenizer = new TokenizerME(model);

			return tokenizer.tokenize(sentence);
		}
		catch(IOException e) {
			return null;
		}
	}


	public static ArrayList<String> findName(String[] words) {
		try {
			InputStream is = new FileInputStream("lib/en-ner-person.bin");

			TokenNameFinderModel model = new TokenNameFinderModel(is);
			is.close();

			NameFinderME nameFinder = new NameFinderME(model);

			ArrayList<String> test = new ArrayList<String>();
			Span spans[] = nameFinder.find(words);
			for(Span span : spans){
				test.add(words[span.getStart()]);
			}

			return test;
		}
		catch(IOException e) {
			return null;
		}
	}
}
