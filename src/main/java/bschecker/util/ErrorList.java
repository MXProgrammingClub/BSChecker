package bschecker.util;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * An object which represents a list of errors.
 * 
 * @author JeremiahDeGreeff
 */
@SuppressWarnings("serial")
public class ErrorList extends ArrayList<Error> {
	
	private final String TEXT;
	private final boolean IS_TOKEN_BASED;
	
	
	/**
	 * @param text the text in which the errors of this ErrorList occur
	 * @param isTokenBased true if the errors in this list are stored based on token indices, false if on character indices
	 */
	public ErrorList(String text, boolean isTokenBased) {
		TEXT = text;
		IS_TOKEN_BASED = isTokenBased;
	}
	
	/**
	 * Assumes that the ErrorList contains Errors which are indexed based on tokens.
	 * 
	 * @param text the text in which the errors of this ErrorList occur
	 */
	public ErrorList(String text) {
		this(text, true);
	}
	
	/**
	 * @return the text in which the errors of this ErrorList occur
	 */
	public String getText() {
		return TEXT;
	}
	
	@Override
	public boolean add(Error e) {
		if(this.IS_TOKEN_BASED != e.IS_TOKEN_BASED) {
			LogHelper.getLogger(17).error("Cannot add errors of different types to same ErrorList!");
			return false;
		}
		return super.add(e);
	}
	
	@Override
	public void add(int i, Error e) {
		if(this.IS_TOKEN_BASED != e.IS_TOKEN_BASED)
			LogHelper.getLogger(17).error("Cannot add errors of different types to same ErrorList!");
		super.add(i, e);
	}
	
	@Override
	public Error set(int i, Error e) {
		if(this.IS_TOKEN_BASED != e.IS_TOKEN_BASED) {
			LogHelper.getLogger(17).error("Cannot add errors of different types to same ErrorList!");
			return null;
		}
		return super.set(i, e);
	}
	
	/**
	 * Adds all items of the passed ErrorList to this ErrorList with an offset.
	 * 
	 * @param l the ErrorList whose elements will be added
	 * @param offset an offset to be applied to all the indices of all Errors in the added list
	 */
	public void addAllWithOffset(ErrorList l, int offset) {
		if(this.IS_TOKEN_BASED != l.IS_TOKEN_BASED) {
			LogHelper.getLogger(17).error("Cannot combine ErrorLists of different types");
			return;
		}
		for(Error e : l)
			this.add(new Error(e.IS_TOKEN_BASED, e.getStartIndex() + offset, e.getEndIndex() + offset, e.getBluesheetNumber(), e.getNote()));
	}
	
	/**
	 * Sorts this list by location.
	 */
	private void sort() {
		sort(new Comparator<Error>() {
			public int compare(Error o1, Error o2) {
				return o1.getStartIndex() > o2.getStartIndex() ? 1 : o1.getStartIndex() < o2.getStartIndex() ? -1 : 0;
				}
			});
	}
	
	/**
	 * @return a String representation of this ErrorList with differing formatting based upon whether it is token based or character based.
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
	 * Creates a new ErrorList whose Errors' indices are based on characters rather than tokens.
	 * Should only be used on ErrorLists which pertain to single paragraphs.
	 * 
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
		for(Error e : this) {
			errorProcessed = false;

			// loop until current error is processed
			while(!errorProcessed) {
				ignoredInside = 0;
				
				//find next token
				while(tokens[tokenIndex].charAt(0) != TEXT.charAt(charIndex)) {
					charIndex++;
				}
				//if token is the start of the error process it
				if(tokenIndex == e.getStartIndex()) {
					errorIndex = charIndex;
					errorLength = tokens[tokenIndex].length();

					//loop through errors that include multiple tokens
					for(int i = 1; i <= e.getEndIndex() - e.getStartIndex(); i++) {
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
					
					charErrorList.add(new Error(false, startChar + numIgnored + errorIndex, startChar + numIgnored + ignoredInside + errorIndex + errorLength - 1, e.getBluesheetNumber(), e.getNote()));
					errorProcessed = true;
				} else {
					charIndex += tokens[tokenIndex].length();
					tokenIndex++;
				}
			}
		}
		return charErrorList;
	}
	
	/**
	 * Sets the bluesheet number for all elements of this ErrorList
	 * 
	 * @param bluesheetNumber the number to be set
	 */
	public void setBluesheetNumber(int bluesheetNumber) {
		for(int i = 0; i < size(); i++) {
			Error old = get(i);
			if(old.getBluesheetNumber() == -1)
				set(i, new Error(old.IS_TOKEN_BASED, old.getStartIndex(), old.getEndIndex(), bluesheetNumber, old.getNote()));
		}
	}
	
}
