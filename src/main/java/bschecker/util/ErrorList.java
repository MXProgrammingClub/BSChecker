package bschecker.util;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * An object which represents a list of errors
 * @author JeremiahDeGreeff
 */
@SuppressWarnings("serial")
public class ErrorList extends ArrayList<Error>{
	
	private final String TEXT;
	private final boolean IS_TOKEN_BASED;
	
	
	/**
	 * constructor
	 * @param text the text in which the errors of this ErrorList occur
	 * @param isTokenBased true if the errors in this list are stored based on token indices, false if on character indices
	 */
	public ErrorList(String text, boolean isTokenBased) {
		TEXT = text;
		IS_TOKEN_BASED = isTokenBased;
	}
	
	
	@Override
	public boolean add(Error e) {
		if(IS_TOKEN_BASED != e.isTokenBased()){
			LogHelper.getLogger(17).error("Cannot add errors of different types to same ErrorList!");
			return false;
		}
		return super.add(e);
	}
	
	@Override
	public void add(int i, Error e) {
		if(IS_TOKEN_BASED != e.isTokenBased())
			LogHelper.getLogger(17).error("Cannot add errors of different types to same ErrorList!");
		super.add(i, e);
	}
	
	@Override
	public Error set(int i, Error e) {
		if(IS_TOKEN_BASED != e.isTokenBased()){
			LogHelper.getLogger(17).error("Cannot add errors of different types to same ErrorList!");
			return null;
		}
		return super.set(i, e);
	}
	
	/**
	 * Sorts this list by location.
	 */
	public void sort() {
		sort(new Comparator<Error>() {public int compare(Error o1, Error o2) {return o1.getStartIndex() > o2.getStartIndex() ? 1 : o1.getStartIndex() < o2.getStartIndex() ? -1 : 0;}});
	}
	
	/**
	 * Returns a String representation of this ErrorList with differing formatting based upon whether it is token based or character based.
	 */
	@Override
	public String toString() {
		if(isEmpty())
			return "No errors found!";
		String string = "All found errors (" + size() + " total):\n";
		if(IS_TOKEN_BASED)
			for(Error error : this) {
				string += "Tokens " + error.getStartIndex() + "-" + error.getEndIndex() + " (bluesheet " + error.getBluesheetNumber() + ")";
				if(!error.getNote().equals(""))
					string += " -- " + error.getNote();
				string += "\n";
			}
		else
			for(Error error : this){
				string += "Characters " + error.getStartIndex() + "-" + error.getEndIndex() + ": \"" + TEXT.substring(error.getStartIndex(), error.getEndIndex() + 1) + "\" (bluesheet " + error.getBluesheetNumber() + ")";
				if(!error.getNote().equals(""))
					string += " -- " + error.getNote();
				string += "\n";
			}
		return string;
	}
	
	
	/**
	 * Creates a new error list whose errors' indices are based on characters rather than tokens.
	 * Should only be used on ErrorLists which pertain to single paragraphs.
	 * @param startChar the beginning of this paragraph relative to the entire input
	 * @param ignoredChars indices of chars which have been removed from {@codeTEXT}
	 * @return an ErrorList which represents the same errors as this ErrorList but is based on characters rather than tokens
	 */
	public ErrorList tokensToChars(int startChar, ArrayList<Integer> ignoredChars) {
		if(!IS_TOKEN_BASED) {
			LogHelper.getLogger(17).error("This ErrorList is already character based!");
			return this;
		}
		String[] tokens = Tools.getTokenizer().tokenize(TEXT);
		boolean errorProcessed;
		int tokenIndex = 0, charIndex = 0, numIgnored = 0, ignoredInside, errorIndex, errorLength;
		ErrorList charErrorList = new ErrorList(TEXT, false);

		//loop through each error
		this.sort();
		for(int errorNum = 0; errorNum < size(); errorNum++) {
			errorProcessed = false;
			Error curErrorTokens = get(errorNum);
			Error curErrorChars = new Error(-1, -1, curErrorTokens.getBluesheetNumber(), false, curErrorTokens.getNote());

			// loop until current error is processed
			while(!errorProcessed) {
				ignoredInside = 0;
				
				//find next token
				while(tokens[tokenIndex].charAt(0) != TEXT.charAt(charIndex)) {
					charIndex++;
				}
				//if token is the start of the error process it
				if(tokenIndex == curErrorTokens.getStartIndex()) {
					errorIndex = charIndex;
					errorLength = tokens[tokenIndex].length();

					//loop through errors that include multiple tokens
					for(int i = 1; i <= curErrorTokens.getEndIndex() - curErrorTokens.getStartIndex(); i++) {
						//find next token
						while(tokens[tokenIndex + i].charAt(0) != TEXT.charAt(errorIndex + errorLength))
							errorLength++;
						errorLength += tokens[tokenIndex + i].length();
					}
					//don't include quotation marks as the first character of an error
					if(TEXT.charAt(errorIndex) == '"'){
						errorIndex++;
						errorLength--;
					}
					//account for ignored characters
					while(numIgnored < ignoredChars.size() && ignoredChars.get(numIgnored) <= startChar + numIgnored + errorIndex)
						numIgnored++;
					//account for ignored characters between the start and end indices
					while(numIgnored + ignoredInside < ignoredChars.size() && ignoredChars.get(numIgnored + ignoredInside) <= startChar + numIgnored + errorIndex + errorLength - 1)
						ignoredInside++;
					
					curErrorChars.setStartIndex(startChar + numIgnored + errorIndex);
					curErrorChars.setEndIndex(startChar + numIgnored + ignoredInside + errorIndex + errorLength - 1);
					charErrorList.add(curErrorChars);
					
					errorProcessed = true;
				} else {
					charIndex += tokens[tokenIndex].length();
					tokenIndex++;
				}
			}
		}
		return charErrorList;
	}
	
}
