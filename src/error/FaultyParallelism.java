package error;

import java.util.ArrayList;

import opennlp.tools.parser.AbstractBottomUpParser;
import opennlp.tools.parser.Parse;
import opennlp.tools.util.Span;
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
		String input = "I went to the store and bought food and water.";
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
//		StringBuffer buf = new StringBuffer(line);
//		boolean autoRemove = false;
//		for(int i=0;i<buf.length();i++){
//			char c = buf.charAt(i);
//			if(c == '(' || c == ')' || c == '\"'){
//				buf.deleteCharAt(i);
//				i--;
//				autoRemove = !autoRemove;
//			} else if(autoRemove || c == '\'' || (c == ' ' && buf.charAt(i+1) == '(') || (c == '.' && buf.charAt(i-1) == ')')){
//				buf.deleteCharAt(i);
//				i--;
//			}
//		}
//		line = buf.toString();
		TokenErrorList errs = new TokenErrorList(line);
		String[] sentences = Tools.getSentenceDetector().sentDetect(line);
		int shift = 0;
		for(String sentence : sentences){
			int lineShift = 0;
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
		System.out.println(parsedText);
		int index = -1, textIndex = 0;
		while(index < parsedText.length() && parsedText.indexOf("CC",index+1) >= 0){
			index = parsedText.indexOf("CC",index+1);
			int i = index - 3, net = 0;
			boolean passedThing = false, passedV = false;
			String type1 = "", type2 = "";
			System.out.println("\n" + index + ": " + parsedText.charAt(index));
			
			while(i>=0 && !(net == 0 && passedThing)){
				if(i > 0 && !passedThing && (parsedText.charAt(i) == 'V') && (parsedText.charAt(i - 1) == '(')){
					passedThing = true;
					type1 = parsedText.substring(i,parsedText.indexOf(' ',i));
					System.out.print("(**) ");
				} else if(parsedText.charAt(i) == ')'){
					net += 1;
					System.out.print("(+1) ");
				} else if(parsedText.charAt(i) == '('){
					net -= 1;
					System.out.print("(-1) ");
				} else
					System.out.print("(==) ");
				System.out.println("index: " + i + " char: " + parsedText.charAt(i) + " net: " + net);
				i--;
			}
			System.out.println(type1);
			
			if(type1.equals("VP")){
				int start = parsedText.indexOf("VB", parsedText.indexOf('(', i) + 1);
				if(start == -1)
					start = parsedText.indexOf('(', index) + 1;
				type1 = parsedText.substring(start,parsedText.indexOf(' ', start));
			}
			
			i = parsedText.indexOf('(', index);
			net = 0;
			passedThing = false;
			while(i < parsedText.length() && !(net == 0 && passedThing)){
				if(!passedV && parsedText.charAt(i) == 'V'){
					passedV = true;
					passedThing = true;
					type2 = parsedText.substring(i,parsedText.indexOf(' ',i));
					System.out.print("(**) ");
				} else if(!passedThing && !passedV && (parsedText.substring(i, i + 2).equals("NP") || parsedText.substring(i, i + 2).equals("NN"))){
					passedThing = true;
					type2 = parsedText.substring(i,parsedText.indexOf(' ',i));
					System.out.print("(##) ");
				} else if(parsedText.charAt(i) == ')'){
					net += 1;
					System.out.print("(+1) ");
				} else if(parsedText.charAt(i) == '('){
					net -= 1;
					System.out.print("(-1) ");
				} else
					System.out.print("(==) ");
				System.out.println("index: " + i + " char: " + parsedText.charAt(i) + " net: " + net);
				i++;
			}
			System.out.println(type2);
			
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

	/**
	 * parses a String using the openNLP parser
	 * @param input the String to parse
	 * @return a String which is a parsed version of the input
	 */
	private static String parse(String input){
		Parse p = new Parse(input, new Span(0, input.length()), AbstractBottomUpParser.INC_NODE, 1, 0);
		Span[] spans = Tools.getTokenizer().tokenizePos(input);
		for(int i = 0; i < spans.length; i++) {
		      Span span = spans[i];
		      p.insert(new Parse(input, span, AbstractBottomUpParser.TOK_NODE, 0, i));
		}
		p = Tools.getParser().parse(p);
		
		StringBuffer sb = new StringBuffer(input.length()*4); //arbitrary initial size
		p.show(sb);
		return sb.toString();
	}
}
