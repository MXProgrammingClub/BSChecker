package BSChecker;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

/**
 * @author JeremiahDeGreeff
 * algorithms for progressive tense (error 12)
 */
public class ErrorProgressiveTense extends Error {

	private static final String[] TO_BE_CONJ = {"be", "am", "are", "is"};
	private static final int ERROR_NUMBER = 12;

//	/**
//	 * for testing purposes
//	 */
//	public static void main(String[] args) {
//		Error.setupOpenNLP();
//		Error testOb = new ErrorProgressiveTense();
//		String test = "Sensing God's desire to destroy Sodom, Abraham is negotiating for a less apocalyptic punishment\nJohn is fighting Harry while eating dougnuts";
//		testOb.findErrors(test);
//	}

	@Override
	public ArrayList<int[]> findErrors(String text) {
		ArrayList<int[]> Errors = new ArrayList<int[]>();
		String line;

		ObjectStream<String> lineStream = new PlainTextByLineStream(new StringReader(text));

		try {
			while ((line = lineStream.read()) != null) {
				String[] tokenizerLine = tokenizer.tokenize(line);
				String[] tags = posTagger.tag(tokenizerLine);

				ArrayList<Integer> errorIndices = findProgressiveTense(tokenizerLine, tags);
				//				System.out.println();
				Errors.addAll(findLoc(errorIndices, text, tokenizerLine));
				//				System.out.println();
			}
		} catch (IOException e) {e.printStackTrace();}

//		if(Errors.size() > 0) {
//			System.out.println("all found errors:");
//			for(int i = 0; i < Errors.size(); i++) {
//				System.out.println(Errors.get(i)[0] + "-" + Errors.get(i)[1] + " (error " + Errors.get(i)[2] + ")");
//			}
//		}
		
		return Errors;
	}

	/**
	 * finds all the progressive tense errors in the given line
	 * @param tokenizerLine the tokens of the line
	 * @param tags the array of the tag for each token
	 * @return the indices of each token which is an error
	 */
	private ArrayList<Integer> findProgressiveTense(String[] tokenizerLine, String[] tags) {
		//finds gerunds and participles
		ArrayList<Integer> errorIndices = new ArrayList<Integer>();
		for(int i = 0; i < tags.length; i++)
		{
			if(tags[i].equals("VBG"))
				errorIndices.add(i);
		}
		
//		for(int i = 0; i < errorIndices.size(); i++)
//			System.out.println(errorIndices.get(i) + ": " + tokenizerLine[errorIndices.get(i)]);

		//checks if each gerund or participle has a form of "to be" before it
		int errorNum = 0;
		String word = null;
		boolean isError;
		while(errorNum < errorIndices.size()) {				
			if(errorIndices.get(errorNum) == 0) {
				errorIndices.remove(errorNum);
			} else {
				word = tokenizerLine[errorIndices.get(errorNum) - 1];
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
					errorIndices.remove(errorNum);
			}
		}

		return errorIndices;
	}

	/**
	 * finds indices in the original text of each error and updates result to include any new errors
	 * @param errorIndices the indices of errors that have been found
	 * @param text the original text
	 * @param tokenizerLine the tokens of the text
	 * @return the list of errors for this line
	 */
	private ArrayList<int[]> findLoc(ArrayList<Integer> errorIndices, String text, String[] tokenizerLine) {
		ArrayList<int[]> result = new ArrayList<int[]>();
		int cursor = 0, start, end;

		for(int i = 0; i < errorIndices.size(); i++) {
//			System.out.println("error found: ");
//			System.out.println("\"" + tokenizerLine[errorIndices.get(i) - 1] + " " + tokenizerLine[errorIndices.get(i)] + "\"");
			start = text.indexOf(tokenizerLine[errorIndices.get(i) - 1] + " " + tokenizerLine[errorIndices.get(i)], cursor);
			end = start + (tokenizerLine[errorIndices.get(i) - 1] + tokenizerLine[errorIndices.get(i)]).length();
			cursor = end;
			int[] error = {start, end, ERROR_NUMBER};
			result.add(error);
//			System.out.println("character indices: " + start + "-" + end);
		}

		return result;
	}
}
