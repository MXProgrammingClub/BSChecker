package bschecker.bluesheets;

import bschecker.util.Error;
import bschecker.util.ErrorList;
import bschecker.util.Tools;
import bschecker.util.UtilityMethods;

/**
 * Finds verbs in the past tense. (1)
 * @author Leo
 * @author tedpyne
 * @author JeremiahDeGreeff
 */
public class PastTense extends Bluesheet {
	
	private static final String[] TO_HAVE_CONJ = {"have", "has", "had", "having"};
	
	
	/**
	 * finds all instances of past tense in the given paragraph
	 * @param line the paragraph in which to find errors
	 * @param parses a String array of the parses of each sentence of the line
	 * @return an ErrorList which for each error references start and end tokens, the bluesheet number (1), and, optionally, a note
	 */
	@Override
	protected ErrorList findErrors(String line, String[] parses) {
		ErrorList errors = new ErrorList(line);
		String sentences[] = Tools.getSentenceDetector().sentDetect(line);
		int tokenOffset = 0;
		for(int i = 0; i < sentences.length; i++){
			String tokens[] = Tools.getTokenizer().tokenize(sentences[i]);
			String[] tags = Tools.getPOSTagger().tag(tokens);
			ErrorList sentenceErrors = new ErrorList(sentences[i]);
			boolean inQuote = false, inIntroducedQuote = false;
			for(int j = 0; j < tags.length; j++){
				if(tokens[j].contains("\"")) {
					inIntroducedQuote = !inQuote && j > 0 && (tokens[j - 1].equals(",") || tokens[j - 1].equals(":"));
					inQuote = !inQuote;
				}
				if(!inIntroducedQuote && tags[j].equals("VBD"))
					sentenceErrors.add(new Error(j));
				if(!inIntroducedQuote && tags[j].equals("VBN") && j > 0 && UtilityMethods.arrayContains(TO_HAVE_CONJ, tokens[j - 1]))
					sentenceErrors.add(new Error(j - 1, j)); //does not currently look past intermediary adverbs
			}
			int[] errorTokens = new int[sentenceErrors.size()];
			for(int j = 0; j < sentenceErrors.size(); j++)
				errorTokens[j] = sentenceErrors.get(j).getEndIndex();
			boolean[] inSBAR = UtilityMethods.tokensInsideTag(errorTokens, parses[i], "SBAR");
			for(int j = 0; j < sentenceErrors.size(); j++)
				if(!inSBAR[j])
					errors.add(new Error(sentenceErrors.get(j).getStartIndex() + tokenOffset, sentenceErrors.get(j).getEndIndex() + tokenOffset));
			tokenOffset += tokens.length;
		}
		return errors;
	}
	
}