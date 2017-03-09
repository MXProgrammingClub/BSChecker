package main.java.bschecker.bluesheets;

import java.util.ArrayList;

import main.java.bschecker.util.Error;
import main.java.bschecker.util.ErrorList;
import main.java.bschecker.util.Tools;
import main.java.bschecker.util.UtilityMethods;

/**
 * WIP
 * Finds sentence structure errors. (2)
 * @author JeremiahDeGreeff
 */
public class IncompleteSentence extends Bluesheet {
	public final int ERROR_NUMBER = 2;
	
	/**
	 * for testing purposes
	 */
	public static void main(String[] args) {
		Tools.initializeOpenNLP();
		String input = "Even after the third apparition further assures the near invulnerability of Macbeth's kingship, Macbeth is still not content as he desires even greater security, so he announces to the witches, \"my heart  throbs to know one thing\"  and proceeds to inquire as to whether or not Banquo's line could be a threat to his sovereignty.";
		System.out.println("\ninput: " + input + "\n\n" + (new IncompleteSentence().findErrors(input)).tokensToChars(0, new ArrayList<Integer>()));
	}
	
	/**
	 * default constructor
	 */
	public IncompleteSentence() {
		this(true);
	}
	
	/**
	 * constructor
	 * @param CheckedWhenAnalyzed true if errors of this type should be looked for when the text is analyzed, false otherwise
	 */
	public IncompleteSentence(boolean CheckedWhenAnalyzed) {
		super(CheckedWhenAnalyzed);
	}

	/**
	 * @param line the paragraph in which to find errors
	 * @return an ErrorList which for each error references start and end tokens, the bluesheet number (1 - 14), and, optionally, a note
	 */
	@Override
	protected ErrorList findErrors(String line) {
		ErrorList errors = new ErrorList(line, true);
		String[] sentences = Tools.getSentenceDetector().sentDetect(line);
		int tokenOffset = 0;
		for(String sentence : sentences){
			int length = Tools.getTokenizer().tokenize(sentence).length;
			errors.addAll(findErrorsInSentence(sentence, tokenOffset, length));
			tokenOffset += length;
		}
		return errors;
	}
	
	/**
	 * WIP
	 * finds any issues in the structure of a sentence
	 * @param sentence the sentence to examine
	 * @param tokenOffset the number of tokens which have occurred in earlier sentences (for returning purposes)
	 * @param length the number of tokens in this sentence (for returning purposes)
	 * @return an ErrorList which for each error in this sentence references start and end tokens, the bluesheet number (11), and, optionally, a note
	 */
	private ErrorList findErrorsInSentence(String sentence, int tokenOffset, int length) {
		ErrorList errors = new ErrorList(sentence, true);
		String parsedText = UtilityMethods.parse(sentence);
		String simplifiedParse = UtilityMethods.simplifyParse(parsedText);
		ArrayList<String> tags = UtilityMethods.listParseTags(parsedText);
		System.out.println("\n" + sentence + "\n" + parsedText + "\n" + simplifiedParse + "\n" + tags);
		
		if(tags.get(1).equals("SBAR")) //either lone dependent clause or run-on in form DC IC
			errors.add(new Error(tokenOffset, tokenOffset + length - 1, ERROR_NUMBER, true));
		int sIndex = 0;
		for(int i = 0; i < tags.size(); i++){
			if(tags.get(i).equals(":") && !tags.get(i + 1).equals("S")) //fragment in form DC; IC or IC; DC
				errors.add(new Error(tokenOffset, tokenOffset + length - 1, ERROR_NUMBER, true, "Fragment"));
			else if(tags.get(i).equals("CC") && tags.get(i + 1).equals("S") && !tags.get(i - 1).equals(",")) //run-on in form IC CC IC
				errors.add(new Error(tokenOffset, tokenOffset + length - 1, ERROR_NUMBER, true, "Run-on"));
			else if(tags.get(i).equals("S")){
				sIndex += parsedText.substring(sIndex + 1).indexOf("(S ") + 2;
				int net = -1, j = sIndex;
				while(net != 0 && j < parsedText.length()){
					if(parsedText.charAt(j) == ')')
						net++;
					else if(parsedText.charAt(j) == '(')
						net--;
					j++;
				}
				if(j + 6 < parsedText.length() && parsedText.charAt(j + 1) == ',' && parsedText.substring(j + 7, j + 9).equals("NP")){
					int k = j + 9;
					while(parsedText.charAt(k + 1) == '(')
						k = parsedText.indexOf(' ', k + 1);
					if(parsedText.charAt(k + 1) != '"')
						errors.add(new Error(tokenOffset, tokenOffset + length - 1, ERROR_NUMBER, true, "Comma Splice"));
				}
			}
		}
		
		return errors;
	}
}
