package bschecker.bluesheets;

import java.util.ArrayList;

import bschecker.util.Error;
import bschecker.util.ErrorList;
import bschecker.util.Tools;
import bschecker.util.UtilityMethods;
import opennlp.tools.parser.Parse;

/**
 * Finds verbs in the passive voice. (9)
 * @author JeremiahDeGreeff
 */
public class PassiveVoice extends Bluesheet {
	
	private static final String[] TO_BE_CONJ = {"be", "am", "is", "are", "was", "were", "been", "being"};
	
	
	/**
	 * Finds all instances of passive voice in a paragraph.
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
			ArrayList<Parse> vpParses = UtilityMethods.findParsesWithTag(parse, new String[] {"VP"});
			for(Parse vpParse : vpParses)
				if(vpParse.getChildCount() > 1 && UtilityMethods.arrayContains(TO_BE_CONJ, vpParse.getChildren()[0].getCoveredText())) {
					int i = 1;
					while(vpParse.getChildren()[i].getType().equals("ADVP"))
						i++;
					if(vpParse.getChildren()[i].getType().equals("VP") && vpParse.getChildren()[i].getChildren()[0].getType().equals("VBN")) {
						int start = UtilityMethods.getIndexOfParse(vpParse.getChildren()[0].getChildren()[0]);
						sentenceErrors.add(new Error(start, start + i));
					}
				}
			UtilityMethods.removeErrorsInQuotes(sentenceErrors, parse, false);
			errors.addAllWithOffset(sentenceErrors, tokenOffset);
			tokenOffset += Tools.getTokenizer().tokenize(parse.getText()).length;
		}
		return errors;
	}
	
}
