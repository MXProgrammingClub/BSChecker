package BSChecker;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

public class FaultyParallelismError extends Error{
	public static void main(String[] args){
		String passage = "She spent the day visiting all the tourist shops and watched the children on the beach, and then she went back to the hotel for a late lunch. Mr. Hirsch hates children and eating french fries.";
		ArrayList<int[]> errs = new FaultyParallelismError().findErrors(passage);
		for(int[] arr: errs)
			System.out.println("(" + arr[0] + "," + arr[1] + "): " + passage.substring(arr[0], arr[1]));
	}
	@Override
	public ArrayList<int[]> findErrors(String text){
		// TODO Auto-generated method stub
		ArrayList<int[]> errs = new ArrayList<int[]>();
		String[] sentences = null;
		try{
		sentences = sentenceDetect(text.toLowerCase());
		}
		catch(Exception e){
			System.out.println("ERROR");
			return null;
		}
		int shift = 0;
		for(String line: sentences){
			ArrayList<int[]> errors = findErrorsInLine(line.substring(0, line.length()-1));
			for(int[] err: errors){
				int[] newErr = {err[0]+shift,err[1]+shift};
				errs.add(newErr);
			}
			shift += line.length()+1;
		}
		return errs;
	}
	public ArrayList<int[]> findErrorsInLine(String text){
	ArrayList<int[]> errors = new ArrayList<int[]>();
	String parsedText = null;
	try{
		parsedText = parse(text);
	}
	catch(Exception e){
			System.out.println("ERROR");
	}
	int index = -1;
	int textIndex = 0;
	while(index < parsedText.length() && parsedText.indexOf("CC",index+1) >= 0){
		index = parsedText.indexOf("CC",index+1);
		int net = 0;
		boolean first = true;
		int i = index-2;
		boolean passedThing = false,passedV = false;
		String type1 = "",type2 = "";
		for(;i>=0 && parsedText.charAt(i) != ')';i--);
		for(;(i>=0 && !(net == 0 && passedThing)) || first ;i--){
			
			if(parsedText.charAt(i) == ')'){
				net += 1;
				first = false;
			}
			if(parsedText.charAt(i) == '('){
				net -= 1;
				first = false;
			}
			if(!passedThing && (parsedText.charAt(i) == 'V')){
				passedThing = true;
				type1 = parsedText.substring(i,parsedText.indexOf(' ',i));
			}
		}
		int beginIndex = i;
		if(type1.equals("VP")){
			int start = parsedText.indexOf("VB",parsedText.indexOf('(',beginIndex)+1);
			if(start == -1)
				start = parsedText.indexOf('(',index)+1;
			type1 = parsedText.substring(start,parsedText.indexOf(' ',start));
		}
		i = parsedText.indexOf(')',index)+1;
		first = true;
		net = 0;
		passedThing = false;
		for(;(i<parsedText.length() && !(net == 0 && passedThing)) || first;i++){
			if(parsedText.charAt(i) == ')'){
				net += 1;
				first = false;
			}
			if(parsedText.charAt(i) == '('){
				net -= 1;
				first = false;
			}
			if(!passedV && parsedText.charAt(i) == 'V'){
				passedV = true;
				passedThing = true;
				type2 = parsedText.substring(i,parsedText.indexOf(' ',i));
			}
			if(!passedThing && !passedV && (parsedText.substring(i,i+2).equals("NP") || parsedText.substring(i,i+2).equals("NN"))){
				passedThing = true;
				type2 = parsedText.substring(i,parsedText.indexOf(' ',i));
			}
		}
		int endIndex = i;
		if(type2.equals("VP")){
			int start = parsedText.indexOf("VB",parsedText.indexOf('(',index)+1);
			if(start == -1)
				start = parsedText.indexOf('(',index)+1;
			type2 = parsedText.substring(start,parsedText.indexOf(' ',start));
			endIndex = parsedText.indexOf(')',start)+1;
		}
		String conjunction = parsedText.substring(parsedText.indexOf(' ',index)+1,parsedText.indexOf(')',index));
		int newTextIndex = text.indexOf(conjunction,textIndex);
		int[] err = {newTextIndex,text.indexOf(' ', newTextIndex)};
		if(!type1.equals(type2)){
			errors.add(err);
		}
		textIndex = err[1];
	}
	return errors;
	}
	public static String parse(String input) throws InvalidFormatException, IOException {
		// http://sourceforge.net/apps/mediawiki/opennlp/index.php?title=Parser#Training_Tool
		InputStream is = new FileInputStream("lib/en-parser-chunking.bin");
	 
		ParserModel model = new ParserModel(is);
	 
		Parser parser = ParserFactory.create(model);
		Parse topParses[] = ParserTool.parseLine(input, parser, 1);
		is.close();
		StringBuffer sb = new StringBuffer(input.length()*4);
		topParses[0].show(sb);
		return sb.toString();
		/*
		 * (TOP (S (NP (NN Programcreek) ) (VP (VBZ is) (NP (DT a) (ADJP (RB
		 * very) (JJ huge) (CC and) (JJ useful) ) ) ) (. website.) ) )
		 */
	}
	public static String[] sentenceDetect(String text) throws InvalidFormatException, IOException {
		// always start with a model, a model is learned from training data
		InputStream is = new FileInputStream("lib/en-sent.bin");
		SentenceModel model = new SentenceModel(is);
		SentenceDetectorME sdetector = new SentenceDetectorME(model);
		String sentences[] = sdetector.sentDetect(text);
		is.close();
		return sentences;
	}
}
