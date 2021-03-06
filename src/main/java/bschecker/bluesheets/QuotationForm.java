package bschecker.bluesheets;

import bschecker.reference.VerbSets;
import bschecker.util.Error;
import bschecker.util.ErrorList;
import bschecker.util.Tools;
import bschecker.util.UtilityMethods;
import opennlp.tools.parser.Parse;

/**
 * Finds errors in quotation form. (14)
 * 
 * @author Julia
 * @author JeremiahDeGreeff
 * @see VerbSets
 */
public class QuotationForm extends Bluesheet {
	
	private static final String[][] PUNCTUATION = {{".", ","}, {":", ";"}};

	/**
	 * Finds all errors with quotation form in a paragraph.
	 * 
	 * @param line the paragraph in which to find errors
	 * @param parses a Parse array of each sentence of the line
	 * @return an ErrorList which for each Error references start token, end token, and, optionally, a note
	 */
	@Override
	public ErrorList findErrors(String line, Parse[] parses) {
		ErrorList errors = new ErrorList(line);
		String tokens[] = Tools.getTokenizer().tokenize(line);
		for(int i = 0; i < tokens.length; i++)
			if(tokens[i].contains("\"")) { //finds opening quotation
				int start = i + 1;
				if(tokens[i].substring(tokens[i].indexOf("\"") + 1).contains("\"")) //checks if opening and closing quotations are on the same token
					start = i;
				for(int j = start; j < tokens.length; j++)
					if(tokens[j].contains("\"")) { //finds closing quotation
						ErrorTypes errorFront = findErrorsFront(tokens, i, j);
						if(!(errorFront == ErrorTypes.NO_ERROR))
							errors.add(new Error(i - 1, errorFront.DESCRIPTION));
						ErrorTypes errorBack = findErrorsBack(tokens, i, j);
						if(!(errorBack == ErrorTypes.NO_ERROR))
							errors.add(new Error(errorBack == ErrorTypes.PUNCTUATION_OUTSIDE ? j + 1 : j - 1, errorBack.DESCRIPTION));
						i = j;
						break;
					}
			}
		return errors;
	}

	/**
	 * Finds errors in running in the quotation.
	 * 
	 * @param tokens The tokens from the nlp tokenizer.
	 * @param start The starting index of the quotation.
	 * @param end The ending index of the quotation
	 * @return the type of any error which is found
	 */
	private ErrorTypes findErrorsFront(String[] tokens, int start, int end) {
		if(start > 0 && tokens[start - 1].equals(":")) {
			if(start > 1 && VerbSets.getSayingVerbs().contains(tokens[start - 2]))
				return ErrorTypes.INVALID_COLON; //error if there is a colon before and the word before it is a verb
		} else if(start > 0 && tokens[start - 1].equals(",")) {
			if(start > 1 && !VerbSets.getSayingVerbs().contains(tokens[start - 2]))
				return ErrorTypes.INVALID_COMMA; //error if there is a comma before and the word before it is not a verb
		} else
			if(start > 0 && VerbSets.getSayingVerbs().contains(tokens[start - 1]) && start + 1 < tokens.length && !tokens[start + 1].equalsIgnoreCase("that"))
				return ErrorTypes.NEEDS_COMMA; //error if there is not a punctuation before and the word before is a verb unless the first word of the quote is that
		return ErrorTypes.NO_ERROR;
	}

	/**
	 * Finds errors in citing the quotation.
	 * 
	 * @param tokens The tokens from the nlp tokenizer.
	 * @param start The starting index of the quotation.
	 * @param end The ending index of the quotation
	 * @return the type of any error which is found
	 */
	private ErrorTypes findErrorsBack(String[] tokens, int start, int end) {
		if(tokens[end].contains("(") || end + 1 < tokens.length && tokens[end + 1].contains("(")) {
			if(end > start && UtilityMethods.arrayContains(PUNCTUATION[0], tokens[end - 1]) || UtilityMethods.arrayContains(PUNCTUATION[1], tokens[end - 1]))
				return ErrorTypes.PUNCTUATION_INSIDE_CITED; //error if cited and punctuation inside
		} else {
			if(end + 1 < tokens.length && UtilityMethods.arrayContains(PUNCTUATION[0], tokens[end + 1]))
				return ErrorTypes.PUNCTUATION_OUTSIDE; //error if not cited and period/comma outside
			if(end > start && UtilityMethods.arrayContains(PUNCTUATION[1], tokens[end - 1]))
				return ErrorTypes.PUNCTUATION_INSIDE; //error if not cited and colon/semicolon inside
		} return ErrorTypes.NO_ERROR;
	}
	
	/**
	 * Defines possible types of quotation form errors.
	 * 
	 * @author JeremiahDeGreeff
	 */
	private enum ErrorTypes {
		NO_ERROR(null),
		INVALID_COLON("Do not introduce a quote with a colon after a verb of saying or thinking."),
		INVALID_COMMA("Do not introduce a quote with a comma without a verb of saying or thinking."),
		NEEDS_COMMA("Do not introduce a quote with a verb of saying or thinking and no comma."),
		PUNCTUATION_INSIDE_CITED("Do not put periods, commas, colons, or semicolons inside a quote which is cited."),
		PUNCTUATION_OUTSIDE("Do not put periods or commas outside a quote which is not cited."),
		PUNCTUATION_INSIDE("Do not put colons or semicolons inside a quote which is not cited.");
		
		public final String DESCRIPTION;
		
		ErrorTypes(String description) {
			DESCRIPTION = description;
		}
	}
	
}
