package util;

@SuppressWarnings("serial")
/**
 * An object which represents a list of errors referenced by character indices
 * Should have int[3] elements where [0] and [1] are start and end characters of the error and [2] is the error number (1 - 14)
 * @author JeremiahDeGreeff
 */
public class CharacterErrorList extends ErrorList {
	/**
	 * constructor
	 * @param text the text in which the errors of this CharacterErrorList occur
	 */
	public CharacterErrorList(String text) {
		super(text);
	}
	
	/**
	 * returns a String representing all the errors on this ErrorList and their characters
	 */
	@Override
	public String toString() {
		if(isEmpty())
			return "No errors found!";
		String errors = "All found errors (" + size() + " total):\n";
		for(int[] error : this)
			errors += "Characters " + error[0] + "-" + error[1] + ": \"" + TEXT.substring(error[0], error[1] + 1) + "\" (error " + error[2] + ")\n";
		return errors;
	}
}
