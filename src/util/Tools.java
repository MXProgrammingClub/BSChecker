package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.cmdline.PerformanceMonitor;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.parser.Parser;
//import opennlp.tools.parser.ParserFactory;
//import opennlp.tools.parser.ParserModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;

/**
 * A class which holds all the openNLP tools
 * @author JeremiahDeGreeff
 */
public class Tools {
	private static SentenceDetectorME sentenceDetector;
	private static NameFinderME nameFinder;
	private static Tokenizer tokenizer;
	private static POSTaggerME posTagger;
	private static Parser parser;
	
	/**
	 * accessor method for the openNLP sentenceDetector
	 * @return the openNLP sentenceDetector as initialized by the setupOpenNLP() method
	 */
	public static SentenceDetectorME getSentenceDetector() {
		return sentenceDetector;
	}
	
	/**
	 * accessor method for the openNLP nameFinder
	 * @return the openNLP nameFinder as initialized by the setupOpenNLP() method
	 */
	public static NameFinderME getNameFinder() {
		return nameFinder;
	}
	
	/**
	 * accessor method for the openNLP tokenizer
	 * @return the openNLP tokenizer as initialized by the setupOpenNLP() method
	 */
	public static Tokenizer getTokenizer() {
		return tokenizer;
	}
	
	/**
	 * accessor method for the openNLP posTagger
	 * @return the openNLP posTagger as initialized by the setupOpenNLP() method
	 */
	public static POSTaggerME getPOSTagger() {
		return posTagger;
	}
	
	/**
	 * accessor method for the openNLP parser
	 * @return the openNLP parser as initialized by the setupOpenNLP() method
	 */
	public static Parser getParser() {
		return parser;
	}
	
	/**
	 * Initializes all the necessary OpenNLP tools
	 */
	public static void initializeOpenNLP() {
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

		sentenceDetector = new SentenceDetectorME(sModel);
		nameFinder = new NameFinderME(nModel);
		tokenizer = new TokenizerME(tModel);
		posTagger = new POSTaggerME(posModel);
//		parser = ParserFactory.create(pModel);

		try {is.close();}
		catch (IOException e) {e.printStackTrace();}
		perfMon.stopAndPrintFinalResult();
		System.out.println("Set up complete!\n");
	}
}
