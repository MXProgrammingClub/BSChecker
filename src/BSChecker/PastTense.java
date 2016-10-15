package BSChecker;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

public class PastTense extends Error{
	public static void main(String[] args) {
		String input = "Hello, I was Leo Appleseed.";
		Error tester = new PastTense();		
		tester.findErrors(input);
	}
	
	
	@Override
	public ArrayList<int[]> findErrors(String text) {
		
		POSModel model = new POSModelLoader()	
				.load(new File("lib/en-pos-maxent.bin"));
		POSTaggerME tagger = new POSTaggerME(model);
		
		ObjectStream<String> lineStream = new PlainTextByLineStream(new StringReader(text));
		String line;
		String output;
		
		try {
			while ((line = lineStream.read()) != null) {

				String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE
						.tokenize(line);
				String[] tags = tagger.tag(whitespaceTokenizerLine);
				
				POSSample sample = new POSSample(whitespaceTokenizerLine, tags);
				//System.out.println(sample.toString());
				output = sample.toString();
				System.out.println(output);
				
				
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return null;
	}
}