package bschecker.bluesheets;

import bschecker.reference.VerbSets;
import bschecker.util.Error;
import bschecker.util.ErrorList;
import bschecker.util.Tools;
import bschecker.util.UtilityMethods;
import opennlp.tools.parser.Parse;

/**
 * Finds verbs in the passive voice. (9)
 * 
 * @author JeremiahDeGreeff
 */
public class PassiveVoice extends Bluesheet {
	
	/**
	 * Finds all instances of passive voice in a paragraph.
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
			ErrorList sentenceErrors = new ErrorList(parse.getText());
			for(Parse vpParse : UtilityMethods.findParsesWithTag(parse, new String[] {"VP"}))
				if(vpParse.getChildCount() > 1 && UtilityMethods.arrayContains(VerbSets.TO_BE_CONJ, vpParse.getChildren()[0].getCoveredText())) {
					int i = 1, tokenCount = 1;
					while(i < vpParse.getChildCount() && vpParse.getChildren()[i].getType().equals("ADVP")) {
						tokenCount += vpParse.getChildren()[i].getChildCount();
						i++;
					}
					if(i < vpParse.getChildCount() && vpParse.getChildren()[i].getType().equals("VP") && vpParse.getChildren()[i].getChildren()[0].getType().equals("VBN")) {
						int start = UtilityMethods.getIndexOfParse(vpParse.getChildren()[0].getChildren()[0]);
						sentenceErrors.add(new Error(start, start + tokenCount));
					}
				}
			UtilityMethods.removeErrorsInQuotes(sentenceErrors, parse, false);
			errors.addAllWithOffset(sentenceErrors, tokenOffset);
			tokenOffset += Tools.getTokenizer().tokenize(parse.getText()).length;
		}
		return errors;
	}
	
}
