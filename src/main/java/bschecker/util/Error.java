package bschecker.util;

import opennlp.tools.parser.Parse;

/**
 * An immutable instance of a bluesheet error.
 * Includes the start and end indices (token or character) of the error,
 * the number of the violated bluesheet [1 - 14],
 * and, optionally, a note which provides more information about the specific Error.
 * 
 * @author JeremiahDeGreeff
 */
public class Error {
	
	protected final boolean isTokenBased;
	private final int startIndex;
	private final int endIndex;
	private final int bluesheetNumber;
	private final String note;
	
	
	/**
	 * @param startIndex the starting index of the error
	 * @param endIndex the ending index of the error
	 * @param bluesheetNumber the number of the bluesheet [1, 14] which the error violates
	 * @param isTokenBased true if the indicies of this error are based on tokens, false if based on characters
	 * @param note a note to be attached to the error
	 * @throws IllegalArgumentException if startIndex > endIndex
	 */
	protected Error(boolean isTokenBased, int startIndex, int endIndex, int bluesheetNumber, String note) {
		if(startIndex > endIndex)
			throw new IllegalArgumentException("StartIndex must occur before EndIndex.");
		this.isTokenBased = isTokenBased;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.bluesheetNumber = bluesheetNumber;
		this.note = note;
	}
	
	/**
	 * Assumes that this Error is indexed based on tokens.
	 * 
	 * @param startIndex the starting index of the error
	 * @param endIndex the ending index of the error
	 * @param note a note to be attached to the error
	 * @throws IllegalArgumentException if startIndex > endIndex
	 */
	public Error(int startIndex, int endIndex, String note) {
		this(true, startIndex, endIndex, -1, note);
	}
	
	/**
	 * Assumes that this Error is indexed based on tokens.
	 * 
	 * @param startIndex the starting index of the error
	 * @param endIndex the ending index of the error
	 * @throws IllegalArgumentException if startIndex > endIndex
	 */
	public Error(int startIndex, int endIndex) {
		this(true, startIndex, endIndex, -1, "");
	}
	
	/**
	 * Assumes that this Error is indexed based on tokens
	 * 
	 * @param index the index of the error (starting and ending)
	 * @param note a note to be attached to the error
	 */
	public Error(int index, String note) {
		this(true, index, index, -1, note);
	}
	
	/**
	 * Assumes that this Error is indexed based on tokens
	 * 
	 * @param index the index of the error (starting and ending)
	 */
	public Error(int index) {
		this(true, index, index, -1, "");
	}
	
	/**
	 * For internal use only.
	 * 
	 * @param range the start and end tokens of the error within the sentence
	 * @param tokenOffset where this sentence starts relative to the entire paragraph
	 * @param note a note to be attached to the error
	 */
	private Error(int[] range, int tokenOffset, String note) {
		this(true, range[0] + tokenOffset, range[1] - 1 + tokenOffset, -1, note);
	}
	
	/**
	 * @param parse a Parse that covers the error
	 * @param tokenOffset where this sentence starts relative to the entire paragraph
	 * @param note a note to be attached to the error
	 */
	public Error(Parse parse, int tokenOffset, String note) {
		this(UtilityMethods.getTokenRange(parse), tokenOffset, note);
	}
	
	/**
	 * @param parse a Parse that covers the error
	 * @param tokenOffset where this sentence starts relative to the entire paragraph
	 */
	public Error(Parse parse, int tokenOffset) {
		this(parse, tokenOffset, "");
	}
	
	
	/**
	 * @return the starting index of the error
	 */
	public int getStartIndex() {
		return startIndex;
	}
	
	/**
	 * @return the ending index of the error
	 */
	public int getEndIndex() {
		return endIndex;
	}
	
	/**
	 * @return the bluesheet number
	 */
	public int getBluesheetNumber() {
		return bluesheetNumber;
	}
	
	/**
	 * @return the note attached to this error
	 */
	public String getNote() {
		return note;
	}
	
}
