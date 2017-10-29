package bschecker.bluesheets;

import bschecker.reference.VerbSets;
import bschecker.util.Error;
import bschecker.util.ErrorList;
import bschecker.util.Tools;
import bschecker.util.UtilityMethods;
import opennlp.tools.parser.Parse;

/**
 * Finds verbs in the past tense. (1)
 * 
 * @author Leo
 * @author tedpyne
 * @author JeremiahDeGreeff
 */
public class PastTense extends Bluesheet {
	
	/**
	 * Finds all instances of past tense in a paragraph.
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
			for(int i = 0; i < tags.length; i++)
				if(tags[i].equals("VBD"))
					sentenceErrors.add(new Error(i));
				else if(tags[i].equals("VBN")) {
					int j = i - 1;
					while(tags[j].equals("RB") && j > 0)
						j--;
					if(UtilityMethods.arrayContains(VerbSets.TO_HAVE_CONJ, tokens[j]))
						sentenceErrors.add(new Error(j, i));
				}
			for(int i = 0; i < sentenceErrors.size(); i++)
				if(UtilityMethods.parseHasParent(UtilityMethods.getParseAtToken(parse, sentenceErrors.get(i).getEndIndex()), "SBAR")) {
					sentenceErrors.remove(i);
					i--;
				}
			UtilityMethods.removeErrorsInQuotes(sentenceErrors, parse, true);
			errors.addAllWithOffset(sentenceErrors, tokenOffset);
			tokenOffset += tokens.length;
		}
		return errors;
	}
	
}