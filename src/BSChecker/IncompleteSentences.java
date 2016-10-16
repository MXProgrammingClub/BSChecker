/**
 * 
 */
package BSChecker;

import java.util.ArrayList;

/**
 * @author Dalal
 * Finds (& prints out locations of) incomplete sentences -> BS error #2
 */
public class IncompleteSentences extends Error {

	public static void main (String[] args) {
		ArrayList<int[]> errors = new IncompleteSentences().findErrors("Hi. How are you? This is Mike.");
		for (int[] error : errors)
			System.out.println(error[0] + " " + error[1] + " " + error[2]);
	}
	
	/* (non-Javadoc)
	 * @see BSChecker.Error#findErrors(java.lang.String)
	 */
	@Override
	public ArrayList<int[]> findErrors(String text) {
		ArrayList<int[]> errors = new ArrayList<int[]>();
		
		return errors;
	}

}
