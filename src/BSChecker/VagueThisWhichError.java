package BSChecker;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;

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
		ArrayList<int[]> errs = new VagueThisWhichError().findErrors(new PlainTextByLineStream(new StringReader("Hi, my name is slim shady. This is fun!")));
		for(int[] err: errs){
			System.out.println(err[0] + " " + err[1] + " " + err[2]);
		}
	}
	public ArrayList<int[]> findErrors(String text){
		ArrayList<int[]> found = new ArrayList<int[]>();
		
		POSModel model = new POSModelLoader().load(new File("lib/en-pos-maxent.bin"));
		POSTaggerME tagger = new POSTaggerME(model);
		ObjectStream<String> lineStream = new PlainTextByLineStream(new StringReader(text));
		String line;
		int lineNum = 0;
		try {
			while ((line = lineStream.read()) != null) {

				String tokens[] = WhitespaceTokenizer.INSTANCE.tokenize(line);
				String[] tags = tagger.tag(tokens);
				int last = 0;
				while((last = Error.findWord(tokens, "this", last))!=-1){
					if(last == tokens.length-1 || tags[last+1].charAt(0)!='N'){
						//Error has occured!
						int[] err = {lineNum,last,last};
						found.add(err);
					}
					last++;
				}
				lineNum++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return found;
	}
	
	public ArrayList<int[]> findErrors(ObjectStream<String> lines){
		ArrayList<int[]> found = new ArrayList<int[]>();
		
		POSModel model = new POSModelLoader().load(new File("lib/en-pos-maxent.bin"));
		POSTaggerME tagger = new POSTaggerME(model);
		String line;
		int lineNum = 0;
		try {
			while ((line = lines.read()) != null) {

				String tokens[] = WhitespaceTokenizer.INSTANCE.tokenize(line);
				String[] tags = tagger.tag(tokens);
				int last = 0;
				while((last = Error.findWord(tokens, "this", last))!=-1){
					if(last == tokens.length-1 || tags[last+1].charAt(0)!='N'){
						//Error has occured!
						int[] err = {lineNum,last,last};
						found.add(err);
					}
					last++;
				}
				lineNum++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return found;
	}
}
