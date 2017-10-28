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
 * 
 * @author JeremiahDeGreeff
 */
public class IncompleteSentence extends Bluesheet {
	
	/**
	 * Finds any invalid sentence structure in a paragraph.
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
			//either lone dependent clause (Fragment) or run-on in form DC IC
			if(parse.getChildCount() > 0 && parse.getChildren()[0].getType().equals("SBAR")) {
				String errorType = testLeadingSBAR(parse);
					if(errorType != null) {
						int[] errorTokens = UtilityMethods.getTokenRange(parse.getChildren()[0]);
						sentenceErrors.add(new Error(errorTokens[0], errorTokens[1] - 1, errorType));
					}
			}
			
			ArrayList<Parse> sParses = UtilityMethods.findParsesWithTag(parse, new String[] {"S"});
			for(Parse sParse : sParses) {
				LogHelper.getLogger(this).debug(sParse.getType() + ":\t" + sParse.getCoveredText());
				Parse[] siblings = sParse.getParent().getChildren();
				int siblingIndex = UtilityMethods.getSiblingIndex(sParse);
				
				//run-on in form IC CC IC
				if(siblingIndex > 1 && siblings[siblingIndex - 1].getType().equals("CC") && !siblings[siblingIndex - 2].getType().equals(","))
					//special case: preceding semicolons will be caught as fragments
					//special case: AMBIGUOUS if follows a quote because the parsing is unreliable
					if(!siblings[siblingIndex - 2].getType().equals(":") && !siblings[siblingIndex - 2].getCoveredText().endsWith(")") && !siblings[siblingIndex - 2].getCoveredText().endsWith("\""))
						sentenceErrors.add(new Error(siblings[siblingIndex - 1].getChildren()[0], 0, "Run-on"));
				
				//comma-splice
				if(siblingIndex + 2 < siblings.length && siblings[siblingIndex + 1].getType().equals(",") && siblings[siblingIndex + 2].getType().equals("S")) {
					//special case: not a comma splice if first clause ends with a nested dependent clause
					Parse SBARparent = UtilityMethods.getParentWithTag(UtilityMethods.getPreviousToken(siblings[siblingIndex + 1], null), "SBAR");
					if(SBARparent == null || UtilityMethods.getSiblingIndex(SBARparent) == 0 || !SBARparent.getParent().getChildren()[UtilityMethods.getSiblingIndex(SBARparent) - 1].getType().equals("IN"))
						//special case: not a comma splice if first clause is dependent but contains two coordinated clauses
						if(siblingIndex == 0 || !(siblings[siblingIndex - 1].getType().equals("IN") || siblingIndex > 2 && siblings[siblingIndex - 1].getType().equals("CC") && siblings[siblingIndex - 2].getType().equals(",") && siblings[siblingIndex - 3].getType().equals("SBAR")))
							//special case: not a comma splice if comma is introducing a quote or participial phrase
							if(siblings[siblingIndex + 2].getCoveredText().charAt(0) != '\"' && !siblings[siblingIndex + 2].getChildren()[(siblings[siblingIndex + 2].getChildren()[0].getType().equals("ADVP") ? 1 : 0)].getChildren()[0].getType().equals("VBG"))
								sentenceErrors.add(new Error(siblings[siblingIndex + 1].getChildren()[0], 0, "Comma-Splice"));
				}
			}
			ArrayList<Parse> scParses = UtilityMethods.findParsesWithTag(parse, new String[] {":"});
			for(Parse scParse : scParses) {
				Parse[] siblings = scParse.getParent().getChildren();
				int siblingIndex = UtilityMethods.getSiblingIndex(scParse);
				//fragment in form DC; IC or IC; DC
				if(siblingIndex > 0 && siblings[siblingIndex - 1].getType().equals("SBAR") || siblingIndex + 1 < siblings.length && siblings[siblingIndex + 1].getType().equals("SBAR"))
					sentenceErrors.add(new Error(scParse.getChildren()[0], 0, "Fragment"));
				//fragment in form ; CC IC
				if(siblingIndex + 2 < siblings.length && siblings[siblingIndex + 1].getType().equals("CC") && siblings[siblingIndex + 2].getType().equals("S"))
					sentenceErrors.add(new Error(siblings[siblingIndex + 1].getChildren()[0], 0, "Fragment"));
			}
			UtilityMethods.removeErrorsInQuotes(sentenceErrors, parse, false);
			errors.addAllWithOffset(sentenceErrors, tokenOffset);
			tokenOffset += Tools.getTokenizer().tokenize(parse.getText()).length;
		}
		return errors;
	}
	
	/**
	 * Tests a Parse which has a leading SBAR to see if it is a fragment, run-on, or neither.
	 * 
	 * @param parse the Parse of the sentence, {@code parse.getChildren[0].getType().equals("SBAR")} should be true
	 * @return "Fragment", "Run-on", or null based on the structure of the sentence
	 */
	private String testLeadingSBAR(Parse parse) {
		Parse vpParse = null;
		for(int i = 0; i < parse.getChildren()[0].getChildren()[1].getChildCount() && vpParse == null; i++)
			if(parse.getChildren()[0].getChildren()[1].getChildren()[i].getType().equals("VP"))
				vpParse = parse.getChildren()[0].getChildren()[1].getChildren()[i];
		if(vpParse != null) {
			Parse npParse = null;
			for(int i = 0; i < vpParse.getChildCount() && npParse == null; i++)
				//clause clumped in as part of a VP (VP contains no object)
				if(vpParse.getChildren()[i].getType().equals("SBAR") || vpParse.getChildren()[i].getType().equals("S"))
					return "Run-on";
				else if(vpParse.getChildren()[i].getType().equals("NP"))
					npParse = vpParse.getChildren()[i];
			if(npParse != null)
				for(int i = 0; i < npParse.getChildCount(); i++)
					//clause not introduced by a comma clumped in as part of a NP which is object of VP
					if(npParse.getChildren()[i].getType().equals("SBAR") || npParse.getChildren()[i].getType().equals("S"))
						return "Run-on";
					//clause introduced by a comma (parsed as , NP) clumped in as part of a NP which is object of VP
					else if (npParse.getChildren()[i].getType().equals(","))
						return null;
		}
		return "Fragment";
	}
	
}
