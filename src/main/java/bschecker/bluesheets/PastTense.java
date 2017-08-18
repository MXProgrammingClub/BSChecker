package bschecker.bluesheets;

import bschecker.util.Error;
import bschecker.util.ErrorList;
import bschecker.util.Tools;
import bschecker.util.UtilityMethods;
import opennlp.tools.parser.Parse;

/**
 * Finds verbs in the past tense. (1)
 * @author Leo
 * @author tedpyne
 * @author JeremiahDeGreeff
 */
public class PastTense extends Bluesheet {
	
	private static final String[] TO_HAVE_CONJ = {"have", "has", "had", "having"};
	
	
	/**
	 * Finds all instances of past tense in a paragraph.
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
				else if(tags[i].equals("VBN") && i > 0 && UtilityMethods.arrayContains(TO_HAVE_CONJ, tokens[i - 1]))
					sentenceErrors.add(new Error(i - 1, i)); //does not currently look past intermediary adverbs
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