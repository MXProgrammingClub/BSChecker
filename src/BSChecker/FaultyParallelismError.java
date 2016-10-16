package BSChecker;

import java.io.FileInputStream;
import java.util.Scanner;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.InvalidFormatException;

public class FaultyParallelismError extends Error{
	public static void main(String[] args){
		Scanner scan = new Scanner(System.in);
		System.out.println("passage: ");
		String passage = scan.nextLine();
		scan.close();
		ArrayList<int[]> errs = new FaultyParallelismError().findErrors(passage);
		for(int[] arr: errs)
			System.out.println("(" + arr[0] + "," + arr[1] + "): " + passage.substring(arr[0], arr[1]));
	}
	@Override
	public ArrayList<int[]> findErrors(String text){
		// TODO Auto-generated method stub
		String startText = text;
		text = text.replace('\u201D', '\"');
		text = text.replace('\u201C', '\"');
		text = text.replace('\u2018','\'');
		text = text.replace('\u2019','\'');
		text = text.replace(':', '.');
		text = text.replace(';', '.');
		StringBuffer buf = new StringBuffer(text);
		boolean autoRemove = false;
		for(int i=0;i<buf.length();i++){
			char c = buf.charAt(i);
			if(c == '(' || c == ')' || c == '\"'){
				buf.deleteCharAt(i);
				i--;
				autoRemove = !autoRemove;
			}
			else if(autoRemove || c == '\'' || (c == ' ' && buf.charAt(i+1) == '(') || (c == '.' && buf.charAt(i-1) == ')')){
				buf.deleteCharAt(i);
				i--;
			}
		}
		text = buf.toString();
		System.out.println(text);
		ArrayList<int[]> errs = new ArrayList<int[]>();
		String[] sentences = null;
		try{
		sentences = sentenceDetect(text);
		}
		catch(Exception e){
			System.out.println("ERROR");
			return null;
		}
		int shift = 0;
		for(String line: sentences){
			int lineShift = 0;
			line.replace(".", "");
			System.out.println(line);
			ArrayList<int[]> errors = findErrorsInLine(line.substring(0, line.length()));
			for(int[] err: errors){
				String conjunction = line.substring(err[0],err[1]);
				int[] newErr = {startText.indexOf(conjunction,lineShift + shift),startText.indexOf(conjunction, lineShift + shift) + conjunction.length(),11};
				errs.add(newErr);
				lineShift = newErr[1]+1 - shift;
				System.out.println(startText.substring(newErr[0],newErr[1]));
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
		if(type2.equals("VP")){
			int start = parsedText.indexOf("VB",parsedText.indexOf('(',index)+1);
			if(start == -1)
				start = parsedText.indexOf('(',index)+1;
			type2 = parsedText.substring(start,parsedText.indexOf(' ',start));
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
