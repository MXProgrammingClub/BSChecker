package errors;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Represents a list of errors and provides methods to manipulate the list
 * @author JeremiahDeGreeff
 */
@SuppressWarnings("serial")
public class ErrorList extends ArrayList<int[]>{
	public final String TEXT;
	private boolean isCharList;
	
	/**
	 * constructor
	 * @param text the text in which the errors of this occur
	 * @param isCharList true if this ErrorList represents char indices, false if it represents token indices
	 */
	public ErrorList(String text, boolean isCharList) {
		TEXT = text;
		this.isCharList = isCharList;
	}
	
	/**
	 * returns a String representing all the errors on this ErrorList formatted based on if this ErrorList represents characters or tokens
	 */
	@Override
	public String toString() {
		if(isEmpty())
			return "No errors found!";
		
		String errors = "All found errors:\n";
		if(isCharList) {
			for(int[] error : this)
				errors += "Characters " + error[0] + "-" + error[1] + ": \"" + TEXT.substring(error[0], error[1] + 1) + "\" (error " + error[2] + ")\n";
			return errors;
		}
		for(int[] error : this)
			errors += "Tokens " + error[0] + "-" + error[1] + " (error " + error[2] + ")\n";

		return errors;
	}
	
//	public void addAll(ErrorList errors) {
//		list.addAll(errors.list);
//	}
	
	/**
	 * Sorts this list of all errors by location.
	 */
	public void sort() {
		sort(new Comparator<int[]>() {
			public int compare(int[] o1, int[] o2) {
				if(o1[0] == o2[0]) return 0;
				else if(o1[0] < o2[0]) return -1;
				else return 1;
			}
		});
	}
	
	/**
	 * changes an ErrorList which represents the tokens of errors to an ErrorList which represents the chars of errors
	 * should only be used on ErrorLists which pertain to single paragraphs
	 * @param tokenErrors an ErrorList of int[3] pointers to the tokens of each error in a single paragraph sorted by start index where int[0], int[1] are start and end tokens of the error and int[2] is the error number (1 - 14)
	 * @param startChar the beginning of this paragraph relative to the entire input
	 */
	public void tokensToChars(int startChar) {
		//does nothing if the ErrorList is already character based
		if(!isCharList) {
			String[] tokens = Error.tokenizer.tokenize(TEXT);
			boolean errorProcessed;
			int tokenIndex = 0, charIndex = 0, errorLength;

			//loop through each error
			for(int errorNum = 0; errorNum < size(); errorNum++) {
				errorProcessed = false;
				int[] curErrorTokens = new int[3], curErrorChars = new int[3];
				curErrorTokens = get(errorNum);
				curErrorChars[2] = curErrorTokens[2];

				// loop until current error is processed
				while(!errorProcessed) {
					//find next token
					while(tokens[tokenIndex].charAt(0) != TEXT.charAt(charIndex)) {
						charIndex++;
					}
					//if token is the start of the error process it
					if(tokenIndex == curErrorTokens[0]) {
						errorLength = tokens[tokenIndex].length();

						//loop through errors that include multiple tokens
						for(int i = 1; i <= curErrorTokens[1] - curErrorTokens[0]; i++) {
							//find next token
							while(tokens[tokenIndex + i].charAt(0) != TEXT.charAt(charIndex + errorLength)) {
								errorLength++;
							}
							errorLength += tokens[tokenIndex + i].length();
						}

						curErrorChars[0] = charIndex + startChar;
						curErrorChars[1] = charIndex + errorLength - 1 + startChar;
						set(errorNum, curErrorChars);

						errorProcessed = true;
					} else {
						charIndex += tokens[tokenIndex].length();
						tokenIndex++;
					}
				}
			}
			isCharList = true;
		}
	}
}
