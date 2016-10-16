package BSChecker;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

public class VerbAgreementError {
	public static void main(String[] args){
		ArrayList<int[]> errs = findErrors("They eat the man. It is delicious. It are enjoyable. To eat men is enjoyable.");
	}
	public static ArrayList<int[]> findErrors(String text){
		try{
			InputStream is = new FileInputStream("lib/en-sent.bin");
			SentenceModel model = new SentenceModel(is);
			SentenceDetectorME sdetector = new SentenceDetectorME(model);
			String sentences[] = sdetector.sentDetect(text);
			is = new FileInputStream("lib/en-parser-chunking.bin");
			ParserModel parseModel = new ParserModel(is);
			Parser parser = ParserFactory.create(parseModel);
			ArrayList<Parse> parses = new ArrayList<Parse>();
			ArrayList<int[]> arr = new ArrayList<int[]>();
			for(String s: sentences){
				ParserTool.parseLine(s.substring(0, s.length()-1), parser, 1)[0].show();
				arr.addAll(correctParse(ParserTool.parseLine(s.substring(0,s.length()-1), parser, 1)[0]));
			}
		is.close();
		}
		catch(Exception e){
			System.out.println("ERROR");
			return null;
		}
		return null;
	}
	public static ArrayList<int[]> correctParse(Parse p){
		SentenceTree head = new SentenceTree(null,p.getChildren()[0]);
		SentenceTree tree = head.fix();
		return new ArrayList<int[]>();
	}
}
