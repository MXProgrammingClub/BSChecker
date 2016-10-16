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
		ArrayList<int[]> errs = findErrors("Because I was walking, the bird ate me");
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
			for(String s: sentences)
				 parses.add(ParserTool.parseLine(s, parser, 1)[0]);
			for(Parse p: parses)
				correctParse(p);
		is.close();
		}
		catch(Exception e){
			System.out.println("ERROR");
			return null;
		}
		
		return null;
	}
	public static ArrayList<int[]> correctParse(Parse p){
		
		return null;
	}
}
