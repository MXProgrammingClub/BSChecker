package main.java.bschecker.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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
import opennlp.tools.util.model.BaseModel;

/**
 * A class which holds all the openNLP tools
 * @author JeremiahDeGreeff
 */
public class Tools {
	
	public static final String[] CLAUSE_LEVEL_TAGS = {"S", "SBAR", "SBARQ", "SINV", "SQ"};
	public static final String[] PHRASE_LEVEL_TAGS = {"ADJP", "ADVP", "CONJP", "FRAG", "INTJ", "LST", "NAC", "NP", "NX", "PP", "PRN", "PRT", "QP", "RRC", "UCP", "VP", "WHADJP", "WHAVP", "WHNP", "WHPP", "X"};
	public static final String[] WORD_LEVEL_TAGS = {"CC", "CD", "DT", "EX", "FW", "IN", "JJ", "JJR", "JJS", "LS", "MD", "NN", "NNS", "NNP", "NNPS", "PDT", "POS", "PRP", "PRP$", "RB", "RBR", "RBS", "RP", "SYM", "TO", "UH", "VB", "VBD", "VBG", "VBN", "VBP", "VBZ", "WDT", "WP", "WP$", "WRB", ".", ",", ":"};
	
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
		System.out.println("Setting up opennlp tools:");
		long start = System.currentTimeMillis();

		System.out.print("\tInitializing the Sentence Detector... ");
		sentenceDetector = new SentenceDetectorME((SentenceModel)loadModel('s', "lib/en-sent.bin"));
		
		System.out.print("\tInitializing the Name Finder... ");
		nameFinder = new NameFinderME((TokenNameFinderModel)loadModel('n', "lib/en-ner-person.bin"));
		
		System.out.print("\tInitializing the Tokenizer... ");
		tokenizer = new TokenizerME((TokenizerModel)loadModel('t', "lib/en-token.bin"));
		
		System.out.print("\tInitializing the Part of Speech Tagger... ");
		posTagger = new POSTaggerME((POSModel)loadModel('o', "lib/en-pos-maxent.bin"));
		
		System.out.print("\tInitializing the Parser... ");
		parser = ParserFactory.create((ParserModel)loadModel('p', "lib/en-parser-chunking.bin"));

		System.out.println("Setup completed in " + ((System.currentTimeMillis() - start) / 1000d) + "s");
	}
	
	/**
	 * private helper method for loading individual openNLP tools
	 * @param tool a character from ['s', 'n', 't', 'o', 'p'] which corresponds to a tool
	 * @param file the file which contains the model to me loaded
	 * @return a BaseModel which holds the loaded file
	 */
	private static BaseModel loadModel(char tool, String file) {
		long start = System.currentTimeMillis();
		InputStream is = null;
		BaseModel model = null;
		try {is = new FileInputStream(file);}
		catch (FileNotFoundException e) {e.printStackTrace();}
		try {model = 
				tool == 's' ? new SentenceModel(is): 
				tool == 'n' ? new TokenNameFinderModel(is):
				tool == 't' ? new TokenizerModel(is):
				tool == 'o' ? new POSModel(is):
				tool == 'p' ? new ParserModel(is): null;}
		catch (InvalidFormatException e) {e.printStackTrace();}
		catch (IOException e) {e.printStackTrace();}
		System.out.println("Complete (" + ((System.currentTimeMillis() - start) / 1000d) + "s)");
		return model;
	}
}
