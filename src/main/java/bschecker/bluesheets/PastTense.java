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
		for(int i = 0; i < parses.length; i++) {
			String sentence = parses[i].getText();
			String[] tokens = Tools.getTokenizer().tokenize(sentence);
			String[] tags = Tools.getPOSTagger().tag(tokens);
			ErrorList sentenceErrors = new ErrorList(sentence);
			boolean inQuote = false, inIntroducedQuote = false;
			for(int j = 0; j < tags.length; j++) {
				if(tokens[j].contains("\"")) {
					inIntroducedQuote = !inQuote && j > 0 && (tokens[j - 1].equals(",") || tokens[j - 1].equals(":"));
					inQuote = !inQuote;
				}
				if(!inIntroducedQuote && tags[j].equals("VBD"))
					sentenceErrors.add(new Error(j));
				if(!inIntroducedQuote && tags[j].equals("VBN") && j > 0 && UtilityMethods.arrayContains(TO_HAVE_CONJ, tokens[j - 1]))
					sentenceErrors.add(new Error(j - 1, j)); //does not currently look past intermediary adverbs
			}
			for(int j = 0; j < sentenceErrors.size(); j++)
				if(!UtilityMethods.parseHasParent(UtilityMethods.getParseAtToken(parses[i], sentenceErrors.get(j).getEndIndex()), "SBAR"))
					errors.add(new Error(sentenceErrors.get(j).getStartIndex() + tokenOffset, sentenceErrors.get(j).getEndIndex() + tokenOffset));
			tokenOffset += tokens.length;
		}
		return errors;
	}
	
}