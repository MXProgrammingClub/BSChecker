package bsChecker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;

import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;

/**
 * @author tedpyne
 * @author JeremiahDeGreeff
 * Defines abstract class for types of grammatical errors
 */
public abstract class Error {
	public static SentenceDetectorME sentenceDetector;
	public static Tokenizer tokenizer;
	public static NameFinderME nameFinder;
	public static POSTaggerME posTagger;
	public static Parser parser;

	/**
	 * Initializes all the necessary OpenNLP tools
	 */
	public static void setupOpenNLP()
	{
		InputStream is = null;

		//SentenceDetector
		SentenceModel sModel = null;
		try {is = new FileInputStream("lib/en-sent.bin");}
		catch (FileNotFoundException e1) {e1.printStackTrace();}
		try {sModel = new SentenceModel(is);}
		catch (InvalidFormatException e1) {e1.printStackTrace();}
		catch (IOException e1) {e1.printStackTrace();}

		//NameFinder
		TokenNameFinderModel nModel = null;
		try {is = new FileInputStream("lib/en-ner-person.bin");}
		catch (FileNotFoundException e1) {e1.printStackTrace();}
		try {nModel = new TokenNameFinderModel(is);}
		catch (InvalidFormatException e1) {e1.printStackTrace();}
		catch (IOException e1) {e1.printStackTrace();}

		//Tokenizer
		TokenizerModel tModel = null;
		try {is = new FileInputStream("lib/en-token.bin");}
		catch (FileNotFoundException e1) {e1.printStackTrace();}
		try {tModel = new TokenizerModel(is); }
		catch (InvalidFormatException e1) {e1.printStackTrace();}
		catch (IOException e1) {e1.printStackTrace();}

		//POSTagger
		POSModel posModel = new POSModelLoader().load(new File("lib/en-pos-maxent.bin"));

		//Parser
		ParserModel pModel = null;
		try {is = new FileInputStream("lib/en-parser-chunking.bin");}
		catch (FileNotFoundException e1) {e1.printStackTrace();}
		try {pModel = new ParserModel(is); }
		catch (InvalidFormatException e1) {e1.printStackTrace();}
		catch (IOException e1) {e1.printStackTrace();}

		sentenceDetector = new SentenceDetectorME(sModel);
		nameFinder = new NameFinderME(nModel);
		tokenizer = new TokenizerME(tModel);
		posTagger = new POSTaggerME(posModel);
		parser = ParserFactory.create(pModel);

		try {is.close();}
		catch (IOException e) {e.printStackTrace();}
	}

	/**
	 * Finds errors of a specific type in the submitted text
	 * @param text the block of text in which to find errors
	 * @return an ArrayList of int[3] pointers to the indices of the start and end characters of an error
	 * 			int[0], int[1] are start and end characters of the error
	 * 			int[2] is the error number (1 - 14)
	 */
	public abstract ArrayList<int[]> findErrors(String text);

	/**
	 * 
	 * @param line The text to search through
	 * @param string The word to find in the text
	 * @param found The number of occurrences already found
	 * @return The location of the n+1th instance
	 */
	public static int locationOf(String line, String string, int found) {
		int loc = 0;
		for(int i = 0; i <= found; i++){
			loc = line.indexOf(string,loc) +1;
		}
		return loc;
	}

	/**
	 * Sorts the list of all errors by location.
	 * @param list All the located errors 
	 */
	public static void sort(ArrayList<int[]> list)
	{
		list.sort(new Comparator<int[]>()
		{
			public int compare(int[] o1, int[] o2)
			{
				if(o1[0] == o2[0]) return 0;
				else if(o1[0] < o2[0]) return -1;
				else return 1;
			}
		});
	}

	/**
	 * prints all errors to console in a way that is easy to understand for testing purposes
	 * @param errors list of errors to be printed
	 */
	public static void printErrors(ArrayList<int[]> errors, String text)
	{
		if(errors.size() > 0) {
			System.out.println("all found errors:");
			for(int i = 0; i < errors.size(); i++) {
				System.out.println(errors.get(i)[0] + "-" + errors.get(i)[1] + ": \"" + text.substring(errors.get(i)[0], errors.get(i)[1] + 1) + "\" (error " + errors.get(i)[2] + ")");
			}
		}
	}
}
