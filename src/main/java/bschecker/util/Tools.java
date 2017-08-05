package bschecker.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import bschecker.reference.Paths;
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
import opennlp.tools.util.model.BaseModel;

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
		PerformanceMonitor.start("tools");
		LogHelper.getLogger(0).info("Initializing opennlp tools...");
		
		sentenceDetector = new SentenceDetectorME((SentenceModel)loadModel('s', Paths.SENTENCE_DETECTOR));
		nameFinder = new NameFinderME((TokenNameFinderModel)loadModel('n', Paths.NAME_FINDER));
		tokenizer = new TokenizerME((TokenizerModel)loadModel('t', Paths.TOKENIZER));
		posTagger = new POSTaggerME((POSModel)loadModel('o', Paths.POS_TAGGER));
		parser = ParserFactory.create((ParserModel)loadModel('p', Paths.PARSER));
		
		LogHelper.getLogger(0).info("Initialization of opennlp tools completed in " + PerformanceMonitor.stop("tools"));
	}
	
	/**
	 * private helper method for loading individual openNLP tools
	 * @param tool a character from ['s', 'n', 't', 'o', 'p'] which corresponds to a tool
	 * @param file the file which contains the model to me loaded
	 * @return a BaseModel which holds the loaded file
	 */
	private static BaseModel loadModel(char tool, String file) {
		PerformanceMonitor.start("model");
		LogHelper.getLogger(0).info("Initializing the " +
				(tool == 's' ? "Sentence Detector" :
					tool == 'n' ? "Name Finder" :
						tool == 't' ? "Tokenizer" :
							tool == 'o' ? "Part of Speech Tagger" :
								tool == 'p' ? "Parser" :
						"") + " ...");
		InputStream is = null;
		BaseModel model = null;
		try {is = new FileInputStream(file);}
		catch (FileNotFoundException e) {e.printStackTrace();}
		try {model = 
				tool == 's' ? new SentenceModel(is): 
					tool == 'n' ? new TokenNameFinderModel(is):
						tool == 't' ? new TokenizerModel(is):
							tool == 'o' ? new POSModel(is):
								tool == 'p' ? new ParserModel(is):
									null;}
		catch (IOException e) {e.printStackTrace();}
		LogHelper.getLogger(0).info("Complete (" + PerformanceMonitor.stop("model") + ")");
		return model;
	}
}
