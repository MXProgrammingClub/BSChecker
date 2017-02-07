package bluesheets;

import java.util.ArrayList;

import util.TokenErrorList;
import util.Tools;

/**
 * Finds apostrophe errors. (8)
 * @author tedpyne
 * @author JeremiahDeGreeff
 */
public class Apostrophe extends Bluesheet {
	public final int ERROR_NUMBER = 8;
	
	/**
	 * for testing purposes
	 */
	public static void main(String[] args) {
		Tools.initializeOpenNLP();
		String input = "";
		System.out.println("\ninput: " + input + "\n");
		TokenErrorList errors = new Apostrophe().findErrors(input);
		errors.sort();
		System.out.println(errors.tokensToChars(0, new ArrayList<Integer>()));
	}
	
	/**
	 * default constructor
	 */
	public Apostrophe() {
		this(true);
	}
	
	/**
	 * constructor
	 * @param CheckedWhenAnalyzed true if errors of this type should be looked for when the text is analyzed, false otherwise
	 */
	public Apostrophe(boolean CheckedWhenAnalyzed) {
		super(CheckedWhenAnalyzed);
	}

	/**
	 * Finds omissions of apostrophes and incorrect apostrophes in the passed line of text
	 * @param line the paragraph in which to find errors
	 * @return a TokenErrorList of int[3] elements where [0] and [1] are start and end tokens of the error and [2] is the error number (8)
	 */
	@Override
	protected TokenErrorList findErrors(String line) {
		String tokens[] = Tools.getTokenizer().tokenize(line);
		String[] tags = Tools.getPOSTagger().tag(tokens);
		
		TokenErrorList errors = new TokenErrorList(line);
		for(int i = 0; i < tokens.length; i++){
			if(tags[i].length()>2 && tags[i].substring(0,3).equals("NNS")){
				int j = i+1;
				while(tags[j].length()>1 && (tags[j].substring(0,2).equals("RB") || tags[j].substring(0,2).equals("JJ")) && j < tokens.length)
					j++;
				//If the preceding word is a noun, the tag of noun is highly likely to be in error. e.g. "the poem features cars", the tag "features"->"NNS" is incorrect
				if((i==0 || !((tags[i-1].length()>1 && tags[i-1].substring(0, 2).equals("NN")) || tags[i-1].equals("WDT"))) && tags[j].length()>1 && tags[j].substring(0,2).equals("NN"))
					errors.add(new int[]{i, j, ERROR_NUMBER});
				
				if(i+1 < tokens.length && tags[i+1].length()>2 && tags[i+1].substring(0, 3).equals("POS")){
					j = i+2;
					while((tags[j].substring(0,2).equals("RB") || tags[j].substring(0,2).equals("JJ")) && j < tokens.length)
						j++;
					if(tags[j].length()>1 && tags[j].substring(0,2).equals("VB"))
						errors.add(new int[]{i, j, ERROR_NUMBER});
				}
			}
		}
		return errors;
	}
}
