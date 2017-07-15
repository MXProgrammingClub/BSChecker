package bschecker.util;


/**
 * an object which represents a blue sheet error
 * the object contains the start and end indices of the error, the type of error (1 - 14) and an optional note which provides more information about the specific error
 * @author JeremiahDeGreeff
 */
public class Error{
	
	private int startIndex;
	private int endIndex;
	private final int BLUESHEET_NUMBER;
	private final boolean IS_TOKEN_BASED;
	private String note;
	
	
	/**
	 * Constructor
	 * @param startIndex the starting index of the error
	 * @param endIndex the ending index of the error
	 * @param bluesheetNum the number of the bluesheet which the error violates
	 * @param isTokenBased true if the indicies of this error are based on tokens, false if based on characters
	 * @param note a note to be attached to the error
	 */
	public Error(int startIndex, int endIndex, int bluesheetNum, boolean isTokenBased, String note) {
		if(startIndex > endIndex){
			LogHelper.getLogger(17).error("WARNING: invalid error object created");
			this.startIndex = -1;
			this.endIndex = -1;
		}else{
			this.startIndex = startIndex;
			this.endIndex = endIndex;
		}
		BLUESHEET_NUMBER = bluesheetNum;
		IS_TOKEN_BASED = isTokenBased;
		this.note = note;
	}
	
	/**
	 * Constructor
	 * @param startIndex the starting index of the error
	 * @param endIndex the ending index of the error
	 * @param bluesheetNum the number of the bluesheet which the error violates
	 * @param isTokenBased true if the indicies of this error are based on tokens, false if based on characters
	 */
	public Error(int startIndex, int endIndex, int bluesheetNum, boolean isTokenBased) {
		this(startIndex, endIndex, bluesheetNum, isTokenBased, "");
	}
	
	/**
	 * Constructor
	 * @param index the index of the error (starting and ending)
	 * @param bluesheetNum the number of the bluesheet which the error violates
	 * @param isTokenBased true if the indicies of this error are based on tokens, false if based on characters
	 * @param note a note to be attached to the error
	 */
	public Error(int index, int bluesheetNum, boolean isTokenBased, String note) {
		this(index, index, bluesheetNum, isTokenBased, note);
	}
	
	/**
	 * Constructor
	 * @param index the index of the error (starting and ending)
	 * @param bluesheetNum the number of the bluesheet which the error violates
	 * @param isTokenBased true if the indicies of this error are based on tokens, false if based on characters
	 */
	public Error(int index, int bluesheetNum, boolean isTokenBased) {
		this(index, index, bluesheetNum, isTokenBased, "");
	}
	
	
	/**
	 * accessor method for the starting index of the error
	 * @return the starting index of the error
	 */
	public int getStartIndex() {
		return startIndex;
	}
	
	/**
	 * accessor method for the ending index of the error
	 * @return the ending index of the error
	 */
	public int getEndIndex() {
		return endIndex;
	}
	
	/**
	 * accessor method for the the number of the corresponding bluesheet
	 * @return the bluesheet number
	 */
	public int getBluesheetNumber() {
		return BLUESHEET_NUMBER;
	}
	
	/**
	 * accessor method for whether this error object's indices are based on tokens or characters
	 * @return true if tokens, false if characters
	 */
	public boolean isTokenBased() {
		return IS_TOKEN_BASED;
	}
	
	/**
	 * accessor method for the note attached to this error
	 * @return the note attached to this error
	 */
	public String getNote() {
		return note;
	}
	
	/**
	 * a mutator method for the starting index of the error
	 * @param startIndex the new starting index
	 */
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}
	
	/**
	 * a mutator method for the ending index of the error
	 * @param endIndex the new ending index
	 */
	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}
	
	/**
	 * a mutator method for the note of an error
	 * @param note the new note
	 */
	public void setNote(String note) {
		this.note = note;
	}
	
}
