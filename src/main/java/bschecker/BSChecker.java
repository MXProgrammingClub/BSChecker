package main.java.bschecker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import main.java.bschecker.bluesheets.Bluesheet;
import main.java.bschecker.bluesheets.Bluesheets;
import main.java.bschecker.bluesheets.QuotationForm;
import main.java.bschecker.util.ErrorList;
import main.java.bschecker.util.Tools;
import main.java.bschecker.util.UtilityMethods;
import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.parser.AbstractBottomUpParser;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;

/**
 * this class contains examples of how to use openNLP and can be used for testing purposes
 * to use enter your input text in the literal for the input String
 * then uncomment which ever tools you would like to run
 * 
 * this class can also be used to test bluesheet classes without launching the application
 * to do this first uncomment the lower portion of the main method
 * if the boolean values in the settings array at index n is set to true, the (n + 1)th bluesheet will be tested for
 * 
 * @author tedpyne
 * @author JeremiahDeGreeff
 */
@SuppressWarnings("unused")
public class BSChecker {
	
	public static void main(String[] args) throws InvalidFormatException, IOException{
		String input = "";
		input = UtilityMethods.replaceInvalidChars(input);
		input = UtilityMethods.removeExtraPunctuation(input, 0, new ArrayList<Integer>());
		
//		Tokenize(input);
//		SentenceDetect(input);
//		POStag(input);
//		findNames(input);
//		parse(input);
		
//		Tools.initializeOpenNLP();
//		boolean[] settings = {false, false, false, false, false, false, false, false, false, false, false, false, false, false};
//		Bluesheets.loadSettings(settings);
//		ErrorList errors = Bluesheet.findAllErrors(input);
//		System.out.println(input + "\n\n" + errors);
	}
	
	
	public static void Tokenize(String input) throws InvalidFormatException, IOException {
		InputStream is = new FileInputStream("lib/en-token.bin");
		TokenizerModel model = new TokenizerModel(is);
		is.close();
		Tokenizer tokenizer = new TokenizerME(model);
		
		String[] tokens = tokenizer.tokenize(input);
		
		for (String token : tokens)
			System.out.println(token);
	}
	
	public static void SentenceDetect(String input) throws InvalidFormatException, IOException {
		InputStream is = new FileInputStream("lib/en-sent.bin");
		SentenceModel model = new SentenceModel(is);
		is.close();
		SentenceDetectorME sdetector = new SentenceDetectorME(model);

		String[] sentences = sdetector.sentDetect(input);

		for(String sentence : sentences)
			System.out.println(sentence);
	}
	
	public static void POStag(String input) throws IOException {
		InputStream is = new FileInputStream("lib/en-token.bin");
		TokenizerModel tModel = new TokenizerModel(is);
		is.close();
		Tokenizer tokenizer = new TokenizerME(tModel);
		
		POSModel model = new POSModelLoader().load(new File("lib/en-pos-maxent.bin"));
		POSTaggerME tagger = new POSTaggerME(model);

		String[] tokens = tokenizer.tokenize(input);
		String[] tags = tagger.tag(tokens);

		POSSample sample = new POSSample(tokens, tags);
		System.out.println(sample.toString());
	}
	
	public static void findNames(String input) throws IOException {
		InputStream is = new FileInputStream("lib/en-token.bin");
		TokenizerModel tModel = new TokenizerModel(is);
		Tokenizer tokenizer = new TokenizerME(tModel);
		
		is = new FileInputStream("lib/en-ner-person.bin");
		TokenNameFinderModel model = new TokenNameFinderModel(is);
		is.close();
		NameFinderME nameFinder = new NameFinderME(model);

		String[] tokens = tokenizer.tokenize(input);
		Span[] nameSpans = nameFinder.find(tokens);

		for(Span s: nameSpans)
			System.out.println(s.toString());			
	}
	
	public static void parse(String input) throws IOException {
		InputStream is = new FileInputStream("lib/en-token.bin");
		TokenizerModel tModel = new TokenizerModel(is);
		is.close();
		Tokenizer tokenizer = new TokenizerME(tModel);
		
		is = new FileInputStream("lib/en-parser-chunking.bin");
		ParserModel pModel = new ParserModel(is);
		is.close();
		Parser parser = ParserFactory.create(pModel);
		
		//method one: simpler but not as accurate
		Parse[] topParses = ParserTool.parseLine(input, parser, 1);
		for(Parse p : topParses)
			p.show();
		
		//method two: separates punctuation and is generally more accurate
		Parse p = new Parse(input, new Span(0, input.length()), AbstractBottomUpParser.INC_NODE, 1, 0);
		Span[] spans = tokenizer.tokenizePos(input);
		for(int i = 0; i < spans.length; i++) {
		      Span span = spans[i];
		      p.insert(new Parse(input, span, AbstractBottomUpParser.TOK_NODE, 0, i));
		}
		p = parser.parse(p);
		p.show();
	}
	
}
