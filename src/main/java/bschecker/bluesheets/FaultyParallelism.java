package main.java.bschecker.bluesheets;

import java.util.ArrayList;

import main.java.bschecker.util.TokenErrorList;
import main.java.bschecker.util.Tools;
import main.java.bschecker.util.UtilityMethods;

/**
 * Finds errors in Parallelism. (11)
 * @author JeremiahDeGreeff
 */
public class FaultyParallelism extends Bluesheet {
	public final int ERROR_NUMBER = 11;
	
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
		super(CheckedWhenAnalyzed);
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
	
	/**
	 * finds all Faulty Parallelism in a sentence
	 * @param sentence the sentence to search
	 * @param ccTokens the token indices of coordinating conjunctions (for returning purposes)
	 * @param tokenOffset the number of tokens which have occurred in earlier sentences (for returning purposes)
	 * @return a TokenErrorList of int[3] elements where [0] and [1] are start and end tokens of the error and [2] is the error number (11) which represent all the errors in this sentence
	 */
	private TokenErrorList findErrorsInSentence(String sentence, ArrayList<Integer> ccTokens, int tokenOffset) {
		TokenErrorList errors = new TokenErrorList(sentence);
		String parsedText = UtilityMethods.parse(sentence);
//		System.out.println("\n" + parsedText);
		int ccIndex = -1;
		for(int ccNum = 0; ccNum < ccTokens.size(); ccNum++){
			ccIndex = parsedText.indexOf("CC", ccIndex + 1);
			//catch for if posTagger identifies a CC which the parser does not
			if(ccIndex == -1)
				break;
//			System.out.println("\n" + ccIndex + "-" + (ccIndex + 1) + ": " + parsedText.charAt(ccIndex) + parsedText.charAt(ccIndex + 1));
			
			int right = ccIndex;
			int left = ccIndex - 4;
			
			//special case: CC in CONJP
			if(parsedText.substring(ccIndex - 7, ccIndex - 2).equals("CONJP")){
				right = ccIndex - 7;
				int net = -1;
				while(right <= parsedText.length() && net != 0){
					if(parsedText.charAt(right) == ')')
						net += 1;
					else if(parsedText.charAt(right) == '(')
						net -= 1;
					right++;
				}
				left = ccIndex - 8;
				for(; left >= 0 && !(parsedText.substring(left, left + 5).equals("CONJP")); left--);
				net = -1;
				while(left <= parsedText.length() && net != 0){
					if(parsedText.charAt(left) == ')')
						net += 1;
					else if(parsedText.charAt(left) == '(')
						net -= 1;
					left++;
				}
			} else {
				//special case CC followed by adverb e.g. "and thus"
				if(parsedText.substring(parsedText.indexOf('(', right) + 1, parsedText.indexOf(' ', parsedText.indexOf('(', right))).equals("ADVP") || parsedText.substring(parsedText.indexOf('(', right) + 1, parsedText.indexOf(' ', parsedText.indexOf('(', right))).equals("RB")) 
					right = parsedText.indexOf("RB", ccIndex);
				int net = 1;
				while(left >= 0 && !(net == 0 && Character.isLetter(parsedText.charAt(left + 2)))){
					if(parsedText.charAt(left) == ')')
						net += 1;
					else if(parsedText.charAt(left) == '(')
						net -= 1;
					left--;
				}
			}
			
			String rightType = parsedText.substring(parsedText.indexOf('(', right) + 1, parsedText.indexOf(' ', parsedText.indexOf('(', right)));
			String leftType = parsedText.substring(left + 2, parsedText.indexOf(' ', left + 2));			
//			System.out.print("Type to Right: \"" + rightType + "\", Type to Left: \"" + leftType + "\" -- ");
			if(!rightType.equals(leftType))
				errors.add(new int[]{ccTokens.get(ccNum) + tokenOffset, ccTokens.get(ccNum) + tokenOffset, ERROR_NUMBER});
		}
		return errors;
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
