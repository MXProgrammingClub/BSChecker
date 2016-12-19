package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import error.Error;
import opennlp.tools.cmdline.PerformanceMonitor;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
//import opennlp.tools.parser.ParserFactory;
//import opennlp.tools.parser.ParserModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;

/**
 * A class which contains many useful static methods for the project
 * @author JeremiahDeGreeff
 */
public class UtilityMethods {
	/**
	 * for testing purposes
	 */
	public static void main(String[] args) {
		setupOpenNLP();
	}
	
	/**
	 * Initializes all the necessary OpenNLP tools
	 */
	public static void setupOpenNLP() {
		PerformanceMonitor perfMon = new PerformanceMonitor(System.err, "tools");
		InputStream is = null;
		System.out.println("Setting up opennlp:\n");
		perfMon.start();

		System.out.println("Setting up the Sentence Detector");
		SentenceModel sModel = null;
		try {is = new FileInputStream("lib/en-sent.bin");}
		catch (FileNotFoundException e1) {e1.printStackTrace();}
		try {sModel = new SentenceModel(is);}
		catch (InvalidFormatException e1) {e1.printStackTrace();}
		catch (IOException e1) {e1.printStackTrace();}
		perfMon.incrementCounter();
		
		System.out.println("Setting up the Name Finder");
		TokenNameFinderModel nModel = null;
		try {is = new FileInputStream("lib/en-ner-person.bin");}
		catch (FileNotFoundException e1) {e1.printStackTrace();}
		try {nModel = new TokenNameFinderModel(is);}
		catch (InvalidFormatException e1) {e1.printStackTrace();}
		catch (IOException e1) {e1.printStackTrace();}
		perfMon.incrementCounter();
		
		System.out.println("Setting up the Tokenizer");
		TokenizerModel tModel = null;
		try {is = new FileInputStream("lib/en-token.bin");}
		catch (FileNotFoundException e1) {e1.printStackTrace();}
		try {tModel = new TokenizerModel(is); }
		catch (InvalidFormatException e1) {e1.printStackTrace();}
		catch (IOException e1) {e1.printStackTrace();}
		perfMon.incrementCounter();
		
		System.out.println("Setting up the Part of Speech Tagger");
		POSModel posModel = new POSModelLoader().load(new File("lib/en-pos-maxent.bin"));
		perfMon.incrementCounter();

//		System.out.println("Setting up the Parser");
//		ParserModel pModel = null;
//		try {is = new FileInputStream("lib/en-parser-chunking.bin");}
//		catch (FileNotFoundException e1) {e1.printStackTrace();}
//		try {pModel = new ParserModel(is); }
//		catch (InvalidFormatException e1) {e1.printStackTrace();}
//		catch (IOException e1) {e1.printStackTrace();}
//		perfMon.incrementCounter();

		Error.sentenceDetector = new SentenceDetectorME(sModel);
		Error.nameFinder = new NameFinderME(nModel);
		Error.tokenizer = new TokenizerME(tModel);
		Error.posTagger = new POSTaggerME(posModel);
//		Error.parser = ParserFactory.create(pModel);

		try {is.close();}
		catch (IOException e) {e.printStackTrace();}
		perfMon.stopAndPrintFinalResult();
		System.out.println("Set up complete!\n");
	}
	
	/**
	 * returns whether or not a string can be found in an array of strings
	 * @param array the array to check
	 * @param word the string to look for
	 * @return true if found, false otherwise
	 */
	public static boolean arrayContains(String[] array, String word) {
		for(String item : array)
			if(word.equalsIgnoreCase(item))
				return true;
		return false;
	}
}
