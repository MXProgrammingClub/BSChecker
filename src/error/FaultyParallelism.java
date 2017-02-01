package error;

import java.util.ArrayList;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import util.TokenErrorList;
import util.Tools;

/**
 * WIP
 * Finds errors in Parallelism. (11)
 * @author
 */
public class FaultyParallelism extends Error {
	/**
	 * for testing purposes
	 */
	public static void main(String[] args) {
		Tools.initializeOpenNLP();
		String input = "";
		System.out.println("\ninput: " + input + "\n");
		TokenErrorList errors = new FaultyParallelism().findErrors(input);
		errors.sort();
		System.out.println(errors.tokensToChars(0, new ArrayList<Integer>()));
	}
	
	/**
	 * default constructor
	 */
	public FaultyParallelism() {
		this(true);
	}
	
	/**
	 * constructor
	 * @param CheckedWhenAnalyzed true if errors of this type should be looked for when the text is analyzed, false otherwise
	 */
	public FaultyParallelism(boolean CheckedWhenAnalyzed) {
		super(11, CheckedWhenAnalyzed);
	}
	
	/**
	 * finds instances of faulty parallelism in the given paragraph
	 * @param line the paragraph in which to find errors
	 * @return a TokenErrorList of int[3] elements where [0] and [1] are start and end tokens of the error and [2] is the error number (11)
	 */
	@Override
	protected TokenErrorList findErrors(String line) {
		String startText = line;
		line = line.replace(':', '.');
		line = line.replace(';', '.');
		StringBuffer buf = new StringBuffer(line);
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
		line = buf.toString();
		TokenErrorList errs = new TokenErrorList(line);
		String[] sentences = Tools.getSentenceDetector().sentDetect(line);
		int shift = 0;
		for(String sentence : sentences){
			int lineShift = 0;
			sentence.replace(".", "");
			TokenErrorList errors = findErrorsInSentence(sentence);
			for(int[] err: errors){
				String conjunction = sentence.substring(err[0],err[1]);
				int[] newErr = {startText.indexOf(conjunction,lineShift + shift),startText.indexOf(conjunction, lineShift + shift) + conjunction.length(),ERROR_NUMBER};
				errs.add(newErr);
				lineShift = newErr[1]+1 - shift;
			}
			shift += sentence.length()+1;
		}
		return errs;
	}
	private TokenErrorList findErrorsInSentence(String sentence) {
		TokenErrorList errors = new TokenErrorList(sentence);
		String parsedText = parse(sentence);
		int index = -1, textIndex = 0;
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
			int newTextIndex = sentence.indexOf(conjunction,textIndex);
			int[] err = {newTextIndex,sentence.indexOf(' ', newTextIndex)};
			if(!type1.equals(type2)){
				errors.add(err);
			}
			textIndex = err[1];
		}
		return errors;
	}

	private static String parse(String input){
		Parse topParses[] = ParserTool.parseLine(input, Tools.getParser(), 1);
		StringBuffer sb = new StringBuffer(input.length()*4);
		topParses[0].show(sb);
		return sb.toString();
	}
}
