package BSChecker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;

import opennlp.tools.cmdline.PerformanceMonitor;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.sentdetect.SentenceSampleStream;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

public class VagueThisWhichError extends Error {

	public static void main(String[] args){
		ArrayList<int[]> errs = new VagueThisWhichError().findErrors("Hi, my name is slim shady. Which is fun!");
		for(int[] err: errs){
			System.out.println(err[0] + " " + err[1] );
			System.out.println("Hi, my name is slim shady. Which is fun!".substring(21));
		}
	}
	/*public ArrayList<int[]> findErrors(String text){
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
						//This error has occured!
						int[] err = {lineNum,last,last};
						found.add(err);
					}
					
					last++;
				}
				last = 0;
				while((last=Error.findWord(tokens, "which", last))!=-1){
					System.out.println("Which found!");
					if(last == 0 || tags[last-1].charAt(0)!='N'){
						//Which error has occured!
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
	}*/
	
	public ArrayList<int[]> findErrors(String text){
		
		ArrayList<int[]> found = new ArrayList<int[]>();
		POSModel model = new POSModelLoader().load(new File("lib/en-pos-maxent.bin"));
		ObjectStream<String> lineStream = new PlainTextByLineStream(new StringReader(text));
		
		POSTaggerME tagger = new POSTaggerME(model);
		String line;
		int totLen =0;
		try {
			while ((line = lineStream.read()) != null) {
				String tokens[] = WhitespaceTokenizer.INSTANCE.tokenize(line);
				String[] tags = tagger.tag(tokens);
				int lineLen = 0;
				for(int i = 0; i < tokens.length; i++){
					if(tokens[i].equalsIgnoreCase("this")){
						if(i == tokens.length-1 || tags[i+1].charAt(0)!='N'){
							int[] err = {totLen+lineLen,totLen+lineLen};
							found.add(err);
						}
					}
					if(tokens[i].equalsIgnoreCase("which")){
						if(i == 0 || tags[i-1].charAt(0)!='N' || endOfSentence(tokens[i-1])){
							int[] err = {totLen+lineLen,totLen+lineLen};
							found.add(err);
						}
					}
					lineLen+=tokens[i].length();
				}
				/*int last = 0;
				System.out.println(line);
				while((last = Error.findWord(tokens, "this", last))!=-1){
					if(last == tokens.length-1 || tags[last+1].charAt(0)!='N'){
						//This error has occured!
						int[] err = {lineNum,last,last};
						found.add(err);
					}
					
					last++;
				}
				last = 0;
				while((last=Error.findWord(tokens, "which", last))!=-1){
					System.out.println("Which found, " + tokens[last-1]);
					if(last == 0 || tags[last-1].charAt(0)!='N' || endOfSentence(tokens[last-1])){
						//Which error has occured!
						int[] err = {lineNum,last,last};
						found.add(err);
					}
					last++;
				}*/
				totLen+=line.length();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return found;
	}
	public static boolean endOfSentence(String word) {
		char fChar = word.charAt(word.length()-1);
		return fChar == ';' || fChar =='.'||fChar=='?'||fChar=='!';
	}
}
