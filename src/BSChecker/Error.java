package BSChecker;
import java.util.ArrayList;
/**
 * 
 * @author tedpyne
 * Defines abstract class for types of grammatical errors
 */
public abstract class Error {
	/**
	 * @param text The block of text to find errors in
	 * @return an ArrayList of int[2] pointers to the start and end indices of the roor in the submitted text
	 * 			int[0],int[1] are start and end tokens in error
	 */
	public abstract ArrayList<int[]> findErrors(String text);
	
	/**
	 * 
	 * @param line The text to search through
	 * @param string The word to find in the text
	 * @param found The number of occurrences already found
	 * @return The location of the n+1th instance
	 */
	public static int locationOf(String line, String string, int found) {
		int loc = 0;
		for(int i = 0; i <= found; i++){
			loc = line.indexOf(string,loc) +1;
		}
		return loc;
	}
	
}
