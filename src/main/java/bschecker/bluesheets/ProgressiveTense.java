package bschecker.bluesheets;

import bschecker.reference.VerbSets;
import bschecker.util.Error;
import bschecker.util.ErrorList;
import bschecker.util.Tools;
import bschecker.util.UtilityMethods;
import opennlp.tools.parser.Parse;

/**
 * Finds verbs in progressive tense. (12)
 * 
 * @author JeremiahDeGreeff
 */
public class ProgressiveTense extends Bluesheet {
	
	/**
	 * Finds all instances of progressive tense in a paragraph.
	 * 
	 * @param line the paragraph in which to find errors
	 * @param parses a Parse array of each sentence of the line
	 * @return an ErrorList which for each Error references start token, end token, and, optionally, a note
	 */
	@Override
	protected ErrorList findErrors(String line, Parse[] parses) {
		ErrorList errors = new ErrorList(line);
		int tokenOffset = 0;
		for(Parse parse : parses) {
			String sentence = parse.getText();
			ErrorList sentenceErrors = new ErrorList(sentence);
			String[] tokens = Tools.getTokenizer().tokenize(sentence);
			String[] tags = Tools.getPOSTagger().tag(tokens);
			for(int i = 1; i < tokens.length; i++)
				if(UtilityMethods.arrayContains(VerbSets.TO_BE_CONJ, tokens[i]) && i != tokens.length - 1) {
					int j = i + 1;
					while(tags[j].equals("RB") && j < tokens.length) j++;
					if(tags[j].equals("VBG"))
						sentenceErrors.add(new Error(i, j));
				}
			UtilityMethods.removeErrorsInQuotes(sentenceErrors, parse, true);
			errors.addAllWithOffset(sentenceErrors, tokenOffset);
			tokenOffset += tokens.length;
		}
		return errors;
	}
	
}
