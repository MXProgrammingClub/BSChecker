package BSChecker;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

/**
 * @author JeremiahDeGreeff
 * algorithms for progressive tense (error 12)
 */
public class ProgressiveTense extends Error {

	static final String[] TO_BE_CONJ = {"be", "am", "are", "is"};
	
	/**
	 * for testing purposes only
	 */
	public static void main(String[] args) {
		Error testOb = new ProgressiveTense();
		String testText = "Sensing God's desire to destroy Sodom, Abraham is negotiating for a less apocalyptic punishment\nSensing God's desire to destroy Sodom, Abraham is negotiating for a less apocalyptic punishment";
		testOb.findErrors(testText);
	}

	@Override
	public ArrayList<int[]> findErrors(String text) {
		POSModel model = new POSModelLoader().load(new File("lib/en-pos-maxent.bin"));
		POSTaggerME tagger = new POSTaggerME(model);
		ArrayList<ArrayList<int[]>> lineErrors = new ArrayList<ArrayList<int[]>>();
		String line;

		ObjectStream<String> lineStream = new PlainTextByLineStream(new StringReader(text));

		try {
			while ((line = lineStream.read()) != null) {
				String[] whitespaceTokenizerLine = WhitespaceTokenizer.INSTANCE.tokenize(line);
				String[] tags = tagger.tag(whitespaceTokenizerLine);
				
				ArrayList<Integer> errorIndices = findProgressiveTense(whitespaceTokenizerLine, tags);
				lineErrors.add(findLoc(errorIndices, text, whitespaceTokenizerLine));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return combineLineErrors(lineErrors);
	}
	
	/**
	 * finds all the progressive tense errors in the given line
	 * @param whitespaceTokenizerLine the tokens of the line
	 * @param tags the array of the tag for each token
	 * @return the indices of each token which is an error
	 */
	private ArrayList<Integer> findProgressiveTense(String[] whitespaceTokenizerLine, String[] tags) {
		//finds gerunds and participles
		ArrayList<Integer> errorIndices = new ArrayList<Integer>();
		for(int i = 0; i < tags.length; i++)
		{
			if(tags[i].equals("VBG"))
				errorIndices.add(i);
		}
		//prints token number with gerund or participle
		for(int i = 0; i < errorIndices.size(); i++)
			System.out.println(errorIndices.get(i) + ": " + whitespaceTokenizerLine[errorIndices.get(i)]);

		
		//checks if each gerund or participle has a form of "to be" before it
		int errorNum = 0;
		String word = null;
		boolean isError = false;
		while(errorNum < errorIndices.size()) {				
			if(errorIndices.get(errorNum) == 0) {
				errorIndices.remove(errorNum);
			} else {
				word = whitespaceTokenizerLine[errorIndices.get(errorNum) - 1];
				System.out.println(word);
				for(int i = 0; i < 4; i++)
					if(word.equals(TO_BE_CONJ[i]))
						isError = true;
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
	 * @param whitespaceTokenizerLine the tokens of the text
	 * @return the list of errors for this line
	 */
	private ArrayList<int[]> findLoc(ArrayList<Integer> errorIndices, String text, String[] whitespaceTokenizerLine) {
		int[] startIndeces = new int[errorIndices.size()], endIndeces = new int[errorIndices.size()];
		int cursor = 0, start, end;
		
		for(int i = 0; i < errorIndices.size(); i++) {
			System.out.println("Error Found");
			System.out.println(whitespaceTokenizerLine[errorIndices.get(i) - 1] + " " + whitespaceTokenizerLine[errorIndices.get(i)]);
			start = text.indexOf(whitespaceTokenizerLine[errorIndices.get(i) - 1] + " " + whitespaceTokenizerLine[errorIndices.get(i)], cursor);
			end = start + text.length();
			startIndeces[i] = start;
			endIndeces[i] = end;
		}
		
		ArrayList<int[]> result = new ArrayList<int[]>();
		result.add(startIndeces);
		result.add(endIndeces);
		
		return result;
	}
	
	/**
	 * combines the errors of each line into one ArrayList
	 * @param lineErrors the errors from each line
	 * @return all of the errors in the text
	 */
	private ArrayList<int[]> combineLineErrors(ArrayList<ArrayList<int[]>> lineErrors) {
		int numErrors = 0;
		for(int line = 0; line < lineErrors.size(); line++)
			numErrors += lineErrors.get(line).get(0).length;
		
		int[] startIndeces = new int[numErrors], endIndeces = new int[numErrors];
		
		int errorNum = 0;
		for(int line = 0; line < lineErrors.size(); line++)
			for(int lineErrorNum = 0; lineErrorNum < lineErrors.get(line).get(0).length; lineErrorNum++) {
				startIndeces[errorNum] = lineErrors.get(line).get(0)[lineErrorNum];
				endIndeces[errorNum] = lineErrors.get(line).get(1)[lineErrorNum];
				errorNum++;
			}
		
		ArrayList<int[]> result = new ArrayList<int[]>();
		result.add(startIndeces);
		result.add(endIndeces);
		
		return result;
	}

}
