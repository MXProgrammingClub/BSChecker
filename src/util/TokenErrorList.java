package util;

import error.Error;

@SuppressWarnings("serial")
/**
 * An object which represents a list of errors referenced by token indices
 * Should have int[3] elements where [0] and [1] are start and end tokens of the error and [2] is the error number (1 - 14)
 * @author JeremiahDeGreeff
 */
public class TokenErrorList extends ErrorList {
	/**
	 * constructor
	 * @param text the text in which the errors of this TokenErrorList occur
	 */
	public TokenErrorList(String text) {
		super(text);
	}
	
	/**
	 * returns a String representing all the errors on this ErrorList and their tokens
	 */
	@Override
	public String toString() {
		if(isEmpty())
			return "No errors found!";
		String errors = "All found errors (" + size() + " total):\n";
		for(int[] error : this)
			errors += "Tokens " + error[0] + "-" + error[1] + " (error " + error[2] + ")\n";
		return errors;
	}
	
	/**
	 * generates a CharacterErrorList based upon this TokenErrorList
	 * should only be used on ErrorLists which pertain to single paragraphs
	 * @param startChar the beginning of this paragraph relative to the entire input
	 * @return a CharacterErrorList which represents the same errors as this TokenErrorList
	 */
	public CharacterErrorList tokensToChars(int startChar) {
		String[] tokens = Error.tokenizer.tokenize(TEXT);
		boolean errorProcessed;
		int tokenIndex = 0, charIndex = 0, errorLength;
		CharacterErrorList charErrorList = new CharacterErrorList(TEXT);

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
