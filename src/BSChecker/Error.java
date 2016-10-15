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
	 * @return an ArrayList of int[3] pointers to the sentence and start and end of the error
	 * 			int[0] is index of sentences,
	 * 			int[1],int[2] are start and end of problem in string
	 */
	public abstract ArrayList<int[]> findErrors(String[] sentences);
}
