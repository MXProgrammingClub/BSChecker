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
	 * 			int[0],int[1] are start and end of problem in string
	 */
	public abstract ArrayList<int[]> findErrors(String text);
	
	public static int findWord(String[] tokens, String text, int start){
		for(int i = start; i < tokens.length; i++){
			if(tokens[i].equals(text)) return i;
		}
		return -1;
	}
}
