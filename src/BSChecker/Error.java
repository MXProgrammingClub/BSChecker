package BSChecker;
import java.util.ArrayList;
/**
 * 
 * @author tedpyne
 * Defines abstract class for types of grammatical errors
 */
public abstract class Error {
	/**
	 * 
	 * @param text The block of text to find errors in
	 * @return an ArrayList of int[2] pointers to the start and end of the error
	 */
	public abstract ArrayList<int[]> findErrors(String text);
}
