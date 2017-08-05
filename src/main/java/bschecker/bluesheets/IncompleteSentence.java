package bschecker.bluesheets;

import java.util.ArrayList;

import bschecker.util.Error;
import bschecker.util.ErrorList;
import bschecker.util.LogHelper;
import bschecker.util.Tools;
import bschecker.util.UtilityMethods;
import opennlp.tools.parser.Parse;

/**
 * Finds sentence structure errors. (2)
 * @author JeremiahDeGreeff
 */
public class IncompleteSentence extends Bluesheet {
	
	/**
	 * Finds any invalid sentence structure in a paragraph.
	 * @param line the paragraph in which to find errors
	 * @param parses a Parse array of each sentence of the line
	 * @return an ErrorList which for each Error references start token, end token, and, optionally, a note
	 */
	@Override
	protected ErrorList findErrors(String line, Parse[] parses) {
		ErrorList errors = new ErrorList(line);
		int tokenOffset = 0;
		for(Parse parse : parses) {
			if(parse.getChildCount() > 0 && parse.getChildren()[0].getType().equals("SBAR")) { //either lone dependent clause (Fragment) or run-on in form DC IC
				int[] errorTokens = UtilityMethods.getTokenRange(parse.getChildren()[0]);
				errors.add(new Error(tokenOffset + errorTokens[0], tokenOffset + errorTokens[1] - 1));
			}
			ArrayList<Parse> sParses = UtilityMethods.findParsesWithTag(parse, "S");
			for(Parse sParse : sParses) {
				LogHelper.getLogger(this).debug(sParse.getType() + ":\t" + sParse.getCoveredText());
				Parse[] siblings = sParse.getParent().getChildren();
				int siblingIndex = 0;
				for(int i = 0; i < siblings.length; i++) {
					if(siblings[i].equals(sParse)) {
						siblingIndex = i;
						break;
					}
				}
				if(siblingIndex > 0 && siblings[siblingIndex - 1].getType().equals(":")) //fragment in form DC; IC or IC; DC
					errors.add(new Error(tokenOffset + UtilityMethods.getIndexOfParse(siblings[siblingIndex - 1].getChildren()[0]), "Fragment"));
				if(siblingIndex > 1 && siblings[siblingIndex - 1].getType().equals("CC") && !siblings[siblingIndex - 2].getType().equals(",")) //run-on in form IC CC IC
					errors.add(new Error(tokenOffset + UtilityMethods.getIndexOfParse(siblings[siblingIndex - 1].getChildren()[0]), "Run-on"));
				if(siblingIndex == 0 || siblingIndex > 0 && !siblings[siblingIndex - 1].getType().equals("IN")) //can't be comma splice if first clause is dependent
					if(siblingIndex + 2 < siblings.length && siblings[siblingIndex + 1].getType().equals(",") && siblings[siblingIndex + 2].getType().equals("S")) //comma-splice
						if(siblings[siblingIndex + 2].getCoveredText().charAt(0) != '\"') //not a comma splice if comma is introducing a quote
							errors.add(new Error(tokenOffset + UtilityMethods.getIndexOfParse(siblings[siblingIndex + 1].getChildren()[0]), "Comma-Splice"));
			}
			tokenOffset += Tools.getTokenizer().tokenize(parse.getText()).length;
		}
		return errors;
	}
	
}
