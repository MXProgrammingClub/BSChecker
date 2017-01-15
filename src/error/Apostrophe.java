package error;

import util.ErrorList;
import util.UtilityMethods;

/**
 * WIP
 * Finds apostrophe errors. (8)
 * @author JeremiahDeGreeff
 * @author tedpyne
 */
public class Apostrophe extends Error {
	/**
	 * for testing purposes
	 */
	public static void main(String[] args) {
		UtilityMethods.setupOpenNLP();
		String input = "The poem features many flaws.";
		System.out.println("\ninput: " + input + "\n");
		ErrorList errors = new Apostrophe().findErrors(input);
		errors.sort();
		errors.tokensToChars(0);
		System.out.println(errors);
	}
	
	/**
	 * default constructor
	 */
	public Apostrophe() {
		this(true);
	}
	
	/**
	 * constructor
	 * @param isChecked true if errors of this type should be looked for when the text is analyzed, false otherwise
	 */
	public Apostrophe(boolean isChecked) {
		super(8, isChecked);
	}

	/**
	 * WIP
	 * @param line the paragraph in which to find errors
	 * @return an ErrorList of int[3] pointers to the indices of the start and end tokens of an error
	 * 			int[0], int[1] are start and end tokens of the error
	 * 			int[2] is the error number (8)
	 */
	@Override
	protected ErrorList findErrors(String line) {
		String tokens[] = tokenizer.tokenize(line);
		String[] tags = posTagger.tag(tokens);
		
		ErrorList errors = new ErrorList(line, false);
		for(int i = 0; i < tokens.length; i++){
			if(tags[i].length()>2 && tags[i].substring(0,3).equals("NNS")){
				int j = i+1;
				while(tags[j].length()>1 && (tags[j].substring(0,2).equals("RB") || tags[j].substring(0,2).equals("JJ")) && j < tokens.length)
					j++;
				if(
						(i==0 || !(tags[i-1].length()>1 && tags[i-1].substring(0, 2).equals("NN"))) &&  //If the preceeding word is a noun, the tag of noun is highly likely to be in error. e.g. "the poem features cars", the tag "features"->"NNS" is incorrect
						tags[j].length()>1 && tags[j].substring(0,2).equals("NN")){
					
					errors.add(new int[]{i, j, ERROR_NUMBER});
					System.out.println(tokens[i]);
					System.out.println(tags[j]);
				}
					
				
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
