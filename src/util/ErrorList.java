package util;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * An object which represents a list of errors
 * @author JeremiahDeGreeff
 */
@SuppressWarnings("serial")
public abstract class ErrorList extends ArrayList<int[]>{
	public final String TEXT;
	
	/**
	 * constructor
	 * @param text the text in which the errors of this ErrorList occur
	 */
	public ErrorList(String text) {
		TEXT = text;
	}
	
	@Override
	public abstract String toString();
	
	/**
	 * Sorts this list of all errors by location.
	 */
	public void sort() {
		sort(new Comparator<int[]>() {
			public int compare(int[] o1, int[] o2) {
				if(o1[0] == o2[0]) return 0;
				else if(o1[0] < o2[0]) return -1;
				else return 1;
			}
		});
	}
}
