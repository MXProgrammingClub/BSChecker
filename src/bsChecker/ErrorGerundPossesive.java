package bsChecker;

import java.util.ArrayList;

/**
 * @author JeremiahDeGreeff
 * Finds errors where gerunds incorrectly lack a possessive. (13)
 */
public class ErrorGerundPossesive extends Error {
	private static final int ERROR_NUMBER = 13;

	/**
	 * for testing purposes
	 */
	public static void main(String[] args) {
		Error.setupOpenNLP();
		String input = "Elizabeth is grateful for him loving her so well.";
		printErrors(new ErrorGerundPossesive().findErrors(input), input);
	}

	@Override
	public ArrayList<int[]> findErrors(String line) {
		ArrayList<int[]> errors = new ArrayList<int[]>();
		String[] tokens = tokenizer.tokenize(line);
		String[] tags = posTagger.tag(tokens);

		ArrayList<Integer> errorTokenIndices = findGerundPossesive(tokens, tags);
		errors = findLoc(errorTokenIndices, line, tokens);

//		printErrors(errors, text);
		
		return errors;
	}

	/**
	 * finds all the gerunds without possessives where there should be
	 * @param tokens the tokens of the line
	 * @param tags the array of the tag for each token
	 * @return the indices of each token which is an error
	 */
	private ArrayList<Integer> findGerundPossesive(String[] tokens, String[] tags) {
		//finds gerunds and participles
		ArrayList<Integer> errorIndices = new ArrayList<Integer>();
		for(int i = 0; i < tags.length; i++)
		{
			if(tags[i].equals("VBG"))
				errorIndices.add(i);
		}
		//prints token number with gerund or participle
		//for(int i = 0; i < errorIndices.size(); i++)
		//	System.out.println(errorIndices.get(i) + ": " + tokenizerLine[errorIndices.get(i)]);

		//checks if each gerund or participle has a non possessive noun or pronoun before it
		int errorNum = 0;
		String word, tag;
		boolean isError;
		while(errorNum < errorIndices.size()) {				
			if(errorIndices.get(errorNum) == 0) {
				errorIndices.remove(errorNum);
			} else {
				word = tokens[errorIndices.get(errorNum) - 1];
				tag = tags[errorIndices.get(errorNum) - 1];
				//System.out.println((errorIndices.get(errorNum) - 1) + ": " + word + " (" + tag + ")");
				isError = false;
				//Warning: will catch some cases which are not errors
				for(int i = 0; i < 4; i++)
				{
					if(tag.equals("PRP"))
						isError = true;
					if(tag.equals("NN") || tag.equals("NNS") || tag.equals("NNP") || tag.equals("NNPS"))
						if(word.indexOf('\'') == -1)
							isError = true;
				}
				if(isError)
					errorNum++;
				else
					errorIndices.remove(errorNum);
			}
		}

		return errorIndices;
	}

	/**
	 * finds indices in the original text of each error and updates result to include any new errors
	 * @param errorTokenIndices the indices of errors that have been found
	 * @param line the original paragraph
	 * @param tokens the tokens of the paragraph
	 * @return the list of errors for this line
	 */
	private ArrayList<int[]> findLoc(ArrayList<Integer> errorTokenIndices, String line, String[] tokens) {
		ArrayList<int[]> result = new ArrayList<int[]>();
		int cursor = 0, start, end;

		for(int i = 0; i < errorTokenIndices.size(); i++) {
			//System.out.println("error found: ");
			//System.out.println("\"" + tokenizerLine[errorIndices.get(i) - 1] + " " + tokenizerLine[errorIndices.get(i)] + "\"");
			start = line.indexOf(tokens[errorTokenIndices.get(i) - 1] + " " + tokens[errorTokenIndices.get(i)], cursor);
			end = start + (tokens[errorTokenIndices.get(i) - 1] + tokens[errorTokenIndices.get(i)]).length();
			cursor = end;
			int[] error = {start, end, ERROR_NUMBER};
			result.add(error);
			//System.out.println("character indices: " + start + "-" + end);
		}

		return result;
	}
}
