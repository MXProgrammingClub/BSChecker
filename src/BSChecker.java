import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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
 * @author JeremiahDeGreeff
 */
public class BSChecker {
	public static void main(String[] args) throws InvalidFormatException, IOException{
		String input = "";
		Tokenize(input);
		SentenceDetect(input);
		POSTag(input);
		findName(input);
	}

	public static void Tokenize(String input) throws InvalidFormatException, IOException {
		InputStream is = new FileInputStream("lib/en-token.bin");
		TokenizerModel model = new TokenizerModel(is);
		is.close();
		Tokenizer tokenizer = new TokenizerME(model);
		
		String tokens[] = tokenizer.tokenize(input);
		
		for (String a : tokens)
			System.out.println(a);
	}

	public static void SentenceDetect(String paragraph) throws InvalidFormatException, IOException {
		InputStream is = new FileInputStream("lib/en-sent.bin");
		SentenceModel model = new SentenceModel(is);
		is.close();
		SentenceDetectorME sdetector = new SentenceDetectorME(model);

		String sentences[] = sdetector.sentDetect(paragraph);

		System.out.println(sentences[0]);
		System.out.println(sentences[1]);
	}

	public static void POSTag(String input) throws IOException {
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

	public static void findName(String input) throws IOException {
		InputStream is = new FileInputStream("lib/en-token.bin");
		TokenizerModel tModel = new TokenizerModel(is);
		Tokenizer tokenizer = new TokenizerME(tModel);
		
		is = new FileInputStream("lib/en-ner-person.bin");
		TokenNameFinderModel model = new TokenNameFinderModel(is);
		is.close();
		NameFinderME nameFinder = new NameFinderME(model);

		String[] tokens = tokenizer.tokenize(input);
		Span nameSpans[] = nameFinder.find(tokens);

		for(Span s: nameSpans)
			System.out.println(s.toString());			
	}
}