import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import opennlp.tools.cmdline.PerformanceMonitor;
import opennlp.tools.cmdline.postag.*;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.*;
import opennlp.tools.util.*;
public class BSChecker {
	public static void main(String[] args) throws InvalidFormatException, IOException{ 
	Tokenize();
	SentenceDetect();
	POSTag();
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
	InputStream is = new FileInputStream("en-sent.bin");
	SentenceModel model = new SentenceModel(is);
	SentenceDetectorME sdetector = new SentenceDetectorME(model);

	String sentences[] = sdetector.sentDetect(paragraph);

	System.out.println(sentences[0]);
	System.out.println(sentences[1]);
	is.close();
}
public static void POSTag() throws IOException {
	POSModel model = new POSModelLoader()	
		.load(new File("en-pos-maxent.bin"));
	PerformanceMonitor perfMon = new PerformanceMonitor(System.err, "sent");
	POSTaggerME tagger = new POSTaggerME(model);
 
	String input = "Hi. How are you? This is Mike.";
	ObjectStream<String> lineStream = new PlainTextByLineStream(
			new StringReader(input));
 
	perfMon.start();
	String line;
	while ((line = lineStream.read()) != null) {
 
		String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE
				.tokenize(line);
		String[] tags = tagger.tag(whitespaceTokenizerLine);
 
		POSSample sample = new POSSample(whitespaceTokenizerLine, tags);
		System.out.println(sample.toString());
 
		perfMon.incrementCounter();
	}
	perfMon.stopAndPrintFinalResult();
}
}