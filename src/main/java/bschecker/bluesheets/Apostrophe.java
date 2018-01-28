package bschecker.bluesheets;

import bschecker.util.Error;
import bschecker.util.ErrorList;
import bschecker.util.Tools;
import opennlp.tools.parser.Parse;

/**
 * Finds apostrophe errors. (8)
 * 
 * @author tedpyne
 * @author JeremiahDeGreeff
 */
public class Apostrophe extends Bluesheet {
	
	/**
	 * Finds omissions and incorrect uses of apostrophes in a paragraph.
	 * 
	 * @param line the paragraph in which to find errors
	 * @param parses a Parse array of each sentence of the line
	 * @return an ErrorList which for each Error references start token, end token, and, optionally, a note
	 */
	@Override
	public ErrorList findErrors(String line, Parse[] parses) {
		ErrorList errors = new ErrorList(line);
		String[] tags = Tools.getPOSTagger().tag(Tools.getTokenizer().tokenize(line));
		for(int i = 0; i < tags.length; i++) {
			if(tags[i].length() > 2 && tags[i].substring(0, 3).equals("NNS")) {
				int j = i + 1;
				while(tags[j].length() > 1 && (tags[j].substring(0, 2).equals("RB") || tags[j].substring(0, 2).equals("JJ")) && j < tags.length)
					j++;
				//If the preceding word is a noun, the tag of noun is highly likely to be in error. e.g. "the poem features cars", the tag "features"->"NNS" is incorrect
				if((i == 0 || !((tags[i - 1].length() > 1 && tags[i - 1].substring(0, 2).equals("NN")) || tags[i - 1].equals("WDT"))) && tags[j].length() > 1 && tags[j].substring(0, 2).equals("NN"))
					errors.add(new Error(i, j));
				
				if(i + 1 < tags.length && tags[i + 1].length() > 2 && tags[i + 1].substring(0, 3).equals("POS")){
					j = i + 2;
					while(tags[j].length() > 1 && (tags[j].substring(0,2).equals("RB") || tags[j].substring(0, 2).equals("JJ")) && j < tags.length)
						j++;
					if(tags[j].length()>1 && tags[j].substring(0, 2).equals("VB"))
						errors.add(new Error(i, j));
				}
			}
		}
		return errors;
	}
	
}
