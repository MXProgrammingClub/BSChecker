package bschecker.bluesheets;

import bschecker.util.Error;
import bschecker.util.ErrorList;
import bschecker.util.Tools;

/**
 * Finds apostrophe errors. (8)
 * @author tedpyne
 * @author JeremiahDeGreeff
 */
public class Apostrophe extends Bluesheet {
	
	public final int ERROR_NUMBER = 8;
	
	
	/**
	 * Finds omissions and incorrect uses of apostrophes in the passed line of text
	 * @param line the paragraph in which to find errors
	 * @param parses a String array of the parses of each sentence of the line
	 * @return an ErrorList which for each error references start and end tokens, the bluesheet number (8), and, optionally, a note
	 */
	@Override
	protected ErrorList findErrors(String line, String[] parses) {
		String tokens[] = Tools.getTokenizer().tokenize(line);
		String[] tags = Tools.getPOSTagger().tag(tokens);
		
		ErrorList errors = new ErrorList(line, true);
		for(int i = 0; i < tokens.length; i++){
			if(tags[i].length()>2 && tags[i].substring(0,3).equals("NNS")){
				int j = i+1;
				while(tags[j].length()>1 && (tags[j].substring(0,2).equals("RB") || tags[j].substring(0,2).equals("JJ")) && j < tokens.length)
					j++;
				//If the preceding word is a noun, the tag of noun is highly likely to be in error. e.g. "the poem features cars", the tag "features"->"NNS" is incorrect
				if((i==0 || !((tags[i-1].length()>1 && tags[i-1].substring(0, 2).equals("NN")) || tags[i-1].equals("WDT"))) && tags[j].length()>1 && tags[j].substring(0,2).equals("NN"))
					errors.add(new Error(i, j, ERROR_NUMBER, true));
				
				if(i+1 < tokens.length && tags[i+1].length()>2 && tags[i+1].substring(0, 3).equals("POS")){
					j = i+2;
					while(tags[j].length() > 1 && (tags[j].substring(0,2).equals("RB") || tags[j].substring(0,2).equals("JJ")) && j < tokens.length)
						j++;
					if(tags[j].length()>1 && tags[j].substring(0,2).equals("VB"))
						errors.add(new Error(i, j, ERROR_NUMBER, true));
				}
			}
		}
		return errors;
	}
	
}
