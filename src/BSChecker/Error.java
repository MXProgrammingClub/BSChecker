package BSChecker;
import java.util.ArrayList;

import opennlp.tools.util.ObjectStream;
/**
 * 
 * @author tedpyne
 * Defines abstract class for types of grammatical errors
 */
public abstract class Error {
	/**
	 * 
	 * @param text The block of text to find errors in
	 * @return an ArrayList of int[3] pointers to the line, start and end token of the error
	 * 			int[0] is the line index,
	 * 			int[1],int[2] are start and end tokens in error
	 */
	
	public abstract ArrayList<int[]> findErrors(String text);
	
	
	
	public static int findWord(String[] tokens, String text, int start){
		for(int i = start; i < tokens.length; i++){
			String token = tokens[i];
			if(tokens[i].equalsIgnoreCase(text)) return i;
		}
		return -1;
	}
	
}
