package bschecker.util;

import opennlp.tools.parser.Parse;

/**
 * an object which represents a blue sheet error
 * the object contains the start and end indices of the error, the type of error [1 - 14] and an optional note which provides more information about the specific error
 * @author JeremiahDeGreeff
 */
public class Error {
	
	protected final boolean IS_TOKEN_BASED;
	private final int START_INDEX;
	private final int END_INDEX;
	private int bluesheetNumber;
	private final String NOTE;
	
	
	/**
	 * constructor
	 * @param startIndex the starting index of the error
	 * @param endIndex the ending index of the error
	 * @param bluesheetNum the number of the bluesheet [1, 14] which the error violates
	 * @param isTokenBased true if the indicies of this error are based on tokens, false if based on characters
	 * @param note a note to be attached to the error
	 * @throws IllegalArgumentException if startIndex > endIndex
	 */
	protected Error(boolean isTokenBased, int startIndex, int endIndex, int bluesheetNum, String note) {
		if(startIndex > endIndex)
			throw new IllegalArgumentException("StartIndex must occur before EndIndex.");
		IS_TOKEN_BASED = isTokenBased;
		START_INDEX = startIndex;
		END_INDEX = endIndex;
		this.bluesheetNumber = bluesheetNum;
		NOTE = note;
	}
	
	/**
	 * constructor
	 * assumes that the Error is indexed based on tokens
	 * @param startIndex the starting index of the error
	 * @param endIndex the ending index of the error
	 * @param note a note to be attached to the error
	 * @throws IllegalArgumentException if startIndex > endIndex
	 */
	public Error(int startIndex, int endIndex, String note) {
		this(true, startIndex, endIndex, -1, note);
	}
	
	/**
	 * constructor
	 * assumes that the Error is indexed based on tokens
	 * @param startIndex the starting index of the error
	 * @param endIndex the ending index of the error
	 * @throws IllegalArgumentException if startIndex > endIndex
	 */
	public Error(int startIndex, int endIndex) {
		this(true, startIndex, endIndex, -1, "");
	}
	
	/**
	 * constructor
	 * assumes that the Error is indexed based on tokens
	 * @param index the index of the error (starting and ending)
	 * @param note a note to be attached to the error
	 */
	public Error(int index, String note) {
		this(true, index, index, -1, note);
	}
	
	/**
	 * constructor
	 * assumes that the Error is indexed based on tokens
	 * @param index the index of the error (starting and ending)
	 */
	public Error(int index) {
		this(true, index, index, -1, "");
	}
	
	/**
	 * internal constructor
	 * @param range the start and end tokens of the error within the sentence
	 * @param tokenOffset where this sentence starts relative to the entire paragraph
	 * @param note a note to be attached to the error
	 */
	private Error(int[] range, int tokenOffset, String note) {
		this(true, range[0] + tokenOffset, range[1] - 1 + tokenOffset, -1, note);
	}
	
	/**
	 * constructor
	 * @param parse a Parse that covers the error
	 * @param tokenOffset where this sentence starts relative to the entire paragraph
	 * @param note a note to be attached to the error
	 */
	public Error(Parse parse, int tokenOffset, String note) {
		this(UtilityMethods.getTokenRange(parse), tokenOffset, note);
	}
	
	/**
	 * constructor
	 * @param parse a Parse that covers the error
	 * @param tokenOffset where this sentence starts relative to the entire paragraph
	 */
	public Error(Parse parse, int tokenOffset) {
		this(parse, tokenOffset, "");
	}
	
	
	/**
	 * accessor method for the starting index of the error
	 * @return the starting index of the error
	 */
	public int getStartIndex() {
		return START_INDEX;
	}
	
	/**
	 * accessor method for the ending index of the error
	 * @return the ending index of the error
	 */
	public int getEndIndex() {
		return END_INDEX;
	}
	
	/**
	 * accessor method for the the number of the corresponding bluesheet
	 * @return the bluesheet number
	 */
	public int getBluesheetNumber() {
		return bluesheetNumber;
	}
	
	/**
	 * accessor method for the note attached to this error
	 * @return the note attached to this error
	 */
	public String getNote() {
		return NOTE;
	}
	
	/**
	 * a mutator method for the number of the bluesheet
	 * @param bluesheetNumber the number to be set
	 */
	protected void setBluesheetNumber(int bluesheetNumber) {
		this.bluesheetNumber = bluesheetNumber;
	}
	
}
