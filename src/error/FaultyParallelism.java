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
		TokenErrorList errors = new TokenErrorList(line);
		String[] sentences = Tools.getSentenceDetector().sentDetect(line);
		int tokenOffset = 0;
		for(String sentence : sentences){
			errors.addAll(findErrorsInSentence(sentence, findCCTokens(sentence), tokenOffset));
			tokenOffset += Tools.getTokenizer().tokenize(sentence).length;
		}
		return errors;
	}
	
	private TokenErrorList findErrorsInSentence(String sentence, ArrayList<Integer> ccTokens, int tokenOffset) {
		TokenErrorList errors = new TokenErrorList(sentence);
		String parsedText = parse(sentence);
		System.out.println(parsedText);
		int index = -1;
		for(int ccNum = 0; ccNum < ccTokens.size(); ccNum++){
			index = parsedText.indexOf("CC", index+1);
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
			
			if(!type1.equals(type2))
				errors.add(new int[]{ccTokens.get(ccNum), ccTokens.get(ccNum), ERROR_NUMBER});
		}
		return errors;
	}

	/**
	 * parses a String using the openNLP parser
	 * @param input the String to parse
	 * @return a String which is a parsed version of the input
	 */
	private static String parse(String input) {
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
	
	/**
	 * finds coordinating conjunctions in a String
	 * @param input the String to search
	 * @return an ArrayList of Integers which represent the indices of tokens which are coordinating conjunctions
	 */
	private static ArrayList<Integer> findCCTokens(String input) {
		String[] tokens = Tools.getTokenizer().tokenize(input);
		String[] tags = Tools.getPOSTagger().tag(tokens);
		
		ArrayList<Integer> ccIndices = new ArrayList<Integer>();
		for(int i = 0; i < tags.length; i++)
			if(tags[i].equals("CC"))
				ccIndices.add(i);
		return ccIndices;
	}
}
