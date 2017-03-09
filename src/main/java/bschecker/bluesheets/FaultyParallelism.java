package main.java.bschecker.bluesheets;

import java.util.ArrayList;

import main.java.bschecker.util.Error;
import main.java.bschecker.util.ErrorList;
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
		System.out.println("\ninput: " + input + "\n\n" + (new FaultyParallelism().findErrors(input)).tokensToChars(0, new ArrayList<Integer>()));
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
	 * @return an ErrorList which for each error references start and end tokens, the bluesheet number (11), and, optionally, a note
	 */
	@Override
	protected ErrorList findErrors(String line) {
		ErrorList errors = new ErrorList(line, true);
		String[] sentences = Tools.getSentenceDetector().sentDetect(line);
		int tokenOffset = 0;
		for(String sentence : sentences){
			errors.addAll(findErrorsInSentence(sentence, UtilityMethods.findTokenTags(sentence, "CC"), tokenOffset));
			tokenOffset += Tools.getTokenizer().tokenize(sentence).length;
		}
		return errors;
	}
	
	/**
	 * finds all Faulty Parallelism in a sentence
	 * @param sentence the sentence to search
	 * @param ccTokens the token indices of coordinating conjunctions (for returning purposes)
	 * @param tokenOffset the number of tokens which have occurred in earlier sentences (for returning purposes)
	 * @return an ErrorList which for each error in this sentence references start and end tokens, the bluesheet number (11), and, optionally, a note
	 */
	private ErrorList findErrorsInSentence(String sentence, ArrayList<Integer> ccTokens, int tokenOffset) {
		ErrorList errors = new ErrorList(sentence, true);
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
						net++;
					else if(parsedText.charAt(right) == '(')
						net--;
					right++;
				}
				left = ccIndex - 8;
				for(; left >= 0 && !(parsedText.substring(left, left + 5).equals("CONJP")); left--);
				net = -1;
				while(left <= parsedText.length() && net != 0){
					if(parsedText.charAt(left) == ')')
						net++;
					else if(parsedText.charAt(left) == '(')
						net--;
					left++;
				}
			} else {
				//special case CC followed by adverb e.g. "and thus"
				if(parsedText.substring(parsedText.indexOf('(', right) + 1, parsedText.indexOf(' ', parsedText.indexOf('(', right))).equals("ADVP") || parsedText.substring(parsedText.indexOf('(', right) + 1, parsedText.indexOf(' ', parsedText.indexOf('(', right))).equals("RB")) 
					right = parsedText.indexOf("RB", ccIndex);
				int net = 1;
				while(left >= 0 && !(net == 0 && Character.isLetter(parsedText.charAt(left + 2)))){
					if(parsedText.charAt(left) == ')')
						net++;
					else if(parsedText.charAt(left) == '(')
						net--;
					left--;
				}
			}
			
			String rightType = parsedText.substring(parsedText.indexOf('(', right) + 1, parsedText.indexOf(' ', parsedText.indexOf('(', right)));
			String leftType = parsedText.substring(left + 2, parsedText.indexOf(' ', left + 2));			
//			System.out.print("Type to Right: \"" + rightType + "\", Type to Left: \"" + leftType + "\" -- ");
			if(!rightType.equals(leftType))
				errors.add(new Error(ccTokens.get(ccNum) + tokenOffset, ERROR_NUMBER, true));
		}
		return errors;
	}
}
