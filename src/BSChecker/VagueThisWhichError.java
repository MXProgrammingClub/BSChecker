package BSChecker;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import opennlp.tools.cmdline.PerformanceMonitor;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

public class VagueThisWhichError extends Error {

	public static void main(String[] args){
		System.out.println(new VagueThisWhichError().findErrors("Hi, my name is slim shady. I own a car!"));
	}
	public ArrayList<int[]> findErrors(String text){
		ArrayList<int[]> found = new ArrayList<int[]>();
		POSModel model = new POSModelLoader().load(new File("lib/en-pos-maxent.bin"));
		POSTaggerME tagger = new POSTaggerME(model);

		String input = "Hi. How are you? This is Mike.";
		ObjectStream<String> lineStream = new PlainTextByLineStream(new StringReader(text));

		String line;
		try {
			while ((line = lineStream.read()) != null) {

				String tokens[] = WhitespaceTokenizer.INSTANCE.tokenize(line);
				String[] tags = tagger.tag(tokens);
				line.indexOf("this")
				
				POSSample sample = new POSSample(tokens, tags);
				sample.
				System.out.println(sample);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return found;
	}

}
