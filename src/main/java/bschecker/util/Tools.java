package bschecker.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import bschecker.reference.Paths;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinder;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.model.BaseModel;

/**
 * Manages all the openNLP tools.
 * 
 * @author JeremiahDeGreeff
 */
public enum Tools {
	
	SENTENCE_DETECTOR(SentenceDetectorME.class, SentenceModel.class, "Sentence Detector"),
	NAME_FINDER(NameFinderME.class, TokenNameFinderModel.class, "Name Finder"),
	TOKENIZER(TokenizerME.class, TokenizerModel.class, "Tokenizer"),
	POS_TAGGER(POSTaggerME.class, POSModel.class, "Part of Speech Tagger"),
	PARSER(Parser.class, ParserModel.class, "Parser");
	
	private Object tool = null;
	private final Class<?> toolClass;
	private final Class<? extends BaseModel> modelClass;
	private final String description;
	
	Tools(Class<?> toolClass, Class<? extends BaseModel> modelClass, String description) {
		this.toolClass = toolClass;
		this.modelClass = modelClass;
		this.description = description;
	}
	
	/**
	 * @return the openNLP sentenceDetector
	 */
	public static SentenceDetector getSentenceDetector() {
		return (SentenceDetectorME)SENTENCE_DETECTOR.tool;
	}
	
	/**
	 * @return the openNLP nameFinder
	 */
	public static TokenNameFinder getNameFinder() {
		return (NameFinderME)NAME_FINDER.tool;
	}
	
	/**
	 * @return the openNLP tokenizer
	 */
	public static Tokenizer getTokenizer() {
		return (TokenizerME)TOKENIZER.tool;
	}
	
	/**
	 * @return the openNLP posTagger
	 */
	public static POSTagger getPOSTagger() {
		return (POSTaggerME)POS_TAGGER.tool;
	}
	
	/**
	 * @return the openNLP parser
	 */
	public static Parser getParser() {
		return (Parser)PARSER.tool;
	}
	
	/**
	 * Initializes all the necessary OpenNLP tools.
	 */
	public static void initializeOpenNLP() {
		for(int i = 0; i < 4; i++) {
			Tools toolElement = Tools.values()[i];
			try {toolElement.tool = toolElement.toolClass.getConstructor(toolElement.modelClass).newInstance(loadModel(i));}
			catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {e.printStackTrace();}
		}
		PARSER.tool = ParserFactory.create((ParserModel)loadModel(4));
	}
	
	/**
	 * Loads an individual openNLP tool.
	 * 
	 * @param index the index of the tool in both the {@code Tools} enum and the array of Tools in {@link Paths}.
	 * @return a BaseModel which holds the loaded file
	 */
	private static BaseModel loadModel(int index) {
		Tools toolElement = Tools.values()[index];
		LogHelper.getLogger(LogHelper.INIT).info("Initializing the " + toolElement.description + "...");
		PerformanceMonitor.start("model");
		
		InputStream is = null;
		try {is = new FileInputStream(Paths.TOOLS[index]);}
		catch (IOException e) {
			LogHelper.getLogger(LogHelper.IO).fatal(toolElement.description +  "failed to load - program terminating.");
			e.printStackTrace();
			System.exit(1);
		}
		
		BaseModel model = null;
		try {model = toolElement.modelClass.getConstructor(InputStream.class).newInstance(is);}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {e.printStackTrace();}
		
		LogHelper.getLogger(LogHelper.INIT).info("Complete (" + PerformanceMonitor.stop("model") + ")");
		return model;
	}
	
}
