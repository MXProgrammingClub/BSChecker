import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import opennlp.tools.cmdline.PerformanceMonitor;
import opennlp.tools.cmdline.postag.*;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.*;
import opennlp.tools.util.*;

/**
 * this class contains examples of how to use openNLP and can be used for testing purposes
 * @author tedpyne
 */
public class BSChecker {
	public static void main(String[] args) throws InvalidFormatException, IOException{ 
//		Tokenize();
//		SentenceDetect();
		POSTag();
//		findName();
	}

	public static void Tokenize() throws InvalidFormatException, IOException {
		InputStream is = new FileInputStream("lib/en-token.bin");

		TokenizerModel model = new TokenizerModel(is);

		Tokenizer tokenizer = new TokenizerME(model);

		String tokens[] = tokenizer.tokenize("Hi. How are you? This is Mike.");

		for (String a : tokens)
			System.out.println(a);

		is.close();
	}

	public static void SentenceDetect() throws InvalidFormatException, IOException {
		String paragraph = "Hi. How are you? This is Mike.";

		// always start with a model, a model is learned from training data
		InputStream is = new FileInputStream("lib/en-sent.bin");
		SentenceModel model = new SentenceModel(is);
		SentenceDetectorME sdetector = new SentenceDetectorME(model);

		String sentences[] = sdetector.sentDetect(paragraph);

		System.out.println(sentences[0]);
		System.out.println(sentences[1]);
		is.close();
	}

	public static void POSTag() throws IOException {
		POSModel model = new POSModelLoader().load(new File("lib/en-pos-maxent.bin"));
		PerformanceMonitor perfMon = new PerformanceMonitor(System.err, "sent");
		POSTaggerME tagger = new POSTaggerME(model);
		
		InputStream is = null;
		TokenizerModel tModel = null;
		try {is = new FileInputStream("lib/en-token.bin");}
		catch (FileNotFoundException e1) {e1.printStackTrace();}
		try {tModel = new TokenizerModel(is); }
		catch (InvalidFormatException e1) {e1.printStackTrace();}
		catch (IOException e1) {e1.printStackTrace();}
		Tokenizer tokenizer = new TokenizerME(tModel);

		String input = "The added benefits of love include";
		ObjectStream<String> lineStream = new PlainTextByLineStream(new StringReader(input));

		perfMon.start();
		String line;
		while ((line = lineStream.read()) != null) {

			String TokenizerLine[] = tokenizer.tokenize(line);
			String[] tags = tagger.tag(TokenizerLine);

			POSSample sample = new POSSample(TokenizerLine, tags);
			System.out.println(sample.toString());

			perfMon.incrementCounter();
		}
		perfMon.stopAndPrintFinalResult();
	}

	public static void findName() throws IOException {
		InputStream is = new FileInputStream("lib/en-ner-person.bin");

		TokenNameFinderModel model = new TokenNameFinderModel(is);
		is.close();

		NameFinderME nameFinder = new NameFinderME(model);

		String []sentence = new String[]{
				"Mike",
				"Smith",
				"is",
				"a",
				"good",
				"car",
				".",
				"he"
		};

		Span nameSpans[] = nameFinder.find(sentence);

		for(Span s: nameSpans)
			System.out.println(s.toString());			
	}
}