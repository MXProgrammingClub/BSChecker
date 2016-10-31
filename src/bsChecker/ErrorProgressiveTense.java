package bsChecker;

import java.util.ArrayList;

/**
 * @author JeremiahDeGreeff
 * Finds verbs in progressive tense. (12)
 */
public class ErrorProgressiveTense extends Error {

	private static final String[] TO_BE_CONJ = {"be", "am", "are", "is"};
	private static final int ERROR_NUMBER = 12;

	/**
	 * for testing purposes
	 */
	public static void main(String[] args) {
		Error.setupOpenNLP();
		String input = "Sensing God's desire to destroy Sodom, Abraham is negotiating for a less apocalyptic punishment\nJohn is fighting Harry while eating dougnuts";
		printErrors(new ErrorProgressiveTense().findErrors(input), input);
	}

	@Override
	public ArrayList<int[]> findErrors(String line) {
		ArrayList<int[]> errors = new ArrayList<int[]>();
		String[] tokens = tokenizer.tokenize(line);
		String[] tags = posTagger.tag(tokens);

		ArrayList<Integer> errorTokenIndices = findProgressiveTense(tokens, tags);
		errors = findLoc(errorTokenIndices, line, tokens);
		
//		printErrors(errors, text);

		return errors;
	}

	/**
	 * finds all the progressive tense errors in the given line
	 * @param tokens the tokens of the line
	 * @param tags the array of the tag for each token
	 * @return the indices of each token which is an error
	 */
	private ArrayList<Integer> findProgressiveTense(String[] tokens, String[] tags) {
		//finds gerunds and participles
		ArrayList<Integer> errorTokenIndices = new ArrayList<Integer>();
		for(int i = 0; i < tags.length; i++)
		{
			if(tags[i].equals("VBG"))
				errorTokenIndices.add(i);
		}
		
//		for(int i = 0; i < errorIndices.size(); i++)
//			System.out.println(errorIndices.get(i) + ": " + tokenizerLine[errorIndices.get(i)]);

		//checks if each gerund or participle has a form of "to be" before it
		int errorNum = 0;
		String word = null;
		boolean isError;
		while(errorNum < errorTokenIndices.size()) {				
			if(errorTokenIndices.get(errorNum) == 0) {
				errorTokenIndices.remove(errorNum);
			} else {
				word = tokens[errorTokenIndices.get(errorNum) - 1];
//				System.out.println((errorIndices.get(errorNum) - 1) + ": " + word);
				isError = false;
				for(int i = 0; i < 4; i++)
				{
					if(word.equals(TO_BE_CONJ[i]))
						isError = true;
				}
				if(isError)
					errorNum++;
				else
					errorTokenIndices.remove(errorNum);
			}
		}
		return errorTokenIndices;
	}

	/**
	 * finds indices in the original text of each error and updates result to include any new errors
	 * @param errorTokenIndices the indices of the tokens of the errors that have been found
	 * @param line the original paragraph
	 * @param tokens the tokens of the paragraph
	 * @return the list of errors for this line
	 */
	private ArrayList<int[]> findLoc(ArrayList<Integer> errorTokenIndices, String line, String[] tokens) {
		ArrayList<int[]> result = new ArrayList<int[]>();
		int cursor = 0, start, end;

		for(int i = 0; i < errorTokenIndices.size(); i++) {
//			System.out.println("error found: ");
//			System.out.println("\"" + tokenizerLine[errorIndices.get(i) - 1] + " " + tokenizerLine[errorIndices.get(i)] + "\"");
			start = line.indexOf(tokens[errorTokenIndices.get(i) - 1] + " " + tokens[errorTokenIndices.get(i)], cursor);
			end = start + (tokens[errorTokenIndices.get(i) - 1] + tokens[errorTokenIndices.get(i)]).length();
			cursor = end;
			int[] error = {start, end, ERROR_NUMBER};
			result.add(error);
//			System.out.println("character indices: " + start + "-" + end);
		}

		return result;
	}
}
