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
	 * @return an ArrayList of int[2] pointers to the start and end token of the error
	 * 			int[0],int[1] are start and end tokens in error
	 */
	
	public abstract ArrayList<int[]> findErrors(String text);
	
	
	
	public static int findWord(String[] tokens, String text, int start){
		for(int i = start; i < tokens.length; i++){
			String token = tokens[i];
			if(tokens[i].equalsIgnoreCase(text)) return i;
		}
		return -1;
	}
	
	public static int locationOf(String line, String string, int found) {
		int loc = 0;
		for(int i = 0; i <= found; i++){
			loc = line.indexOf(string,loc) +1;
		}
		return loc;
	}
	
}
