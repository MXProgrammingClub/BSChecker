package BSChecker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;

import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

/**
 * @author JeremiahDeGreeff
 * algorithms for progressive tense (error 12)
 */
public class ProgressiveTense extends Error {

	private static final String[] TO_BE_CONJ = {"be", "am", "are", "is"};
	private static final int ERROR_NUMBER = 12;

	/**
	 * for testing purposes
	 */
	public static void main(String[] args) {
		Error testOb = new ProgressiveTense();
		String testText = "Sensing God's desire to destroy Sodom, Abraham is negotiating for a less apocalyptic punishment\nJohn is fighting Harry while eating dougnuts";
		testOb.findErrors(testText,null);
	}

	@Override
	public ArrayList<int[]> findErrors(String text,POSModel model) {
		//POSModel model = new POSModelLoader().load(new File("lib/en-pos-maxent.bin"));
		TokenizerModel tModel = null;

		InputStream is;
		try {
			is = new FileInputStream("lib/en-token.bin");
			try {
				tModel = new TokenizerModel(is);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		Tokenizer tokenizer = new TokenizerME(tModel);
		POSTaggerME tagger = new POSTaggerME(model);

		ArrayList<ArrayList<int[]>> lineErrors = new ArrayList<ArrayList<int[]>>();
		String line;

		ObjectStream<String> lineStream = new PlainTextByLineStream(new StringReader(text));

		try {
			while ((line = lineStream.read()) != null) {
				String[] tokenizerLine = tokenizer.tokenize(line);
				String[] tags = tagger.tag(tokenizerLine);

				ArrayList<Integer> errorIndices = findProgressiveTense(tokenizerLine, tags);
				//				System.out.println();
				lineErrors.add(findLoc(errorIndices, text, tokenizerLine));
				//				System.out.println();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return combineLineErrors(lineErrors);
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
		//prints token number with gerund or participle
		//		for(int i = 0; i < errorIndices.size(); i++)
		//		System.out.println(errorIndices.get(i) + ": " + tokenizerLine[errorIndices.get(i)]);

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
			int[] error = {start, end};
			result.add(error);
			//			System.out.println("character indices: " + start + "-" + end);
		}

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
			numErrors += lineErrors.get(line).size();

		ArrayList<int[]> result = new ArrayList<int[]>();

		for(int line = 0; line < lineErrors.size(); line++)
			for(int lineErrorNum = 0; lineErrorNum < lineErrors.get(line).size(); lineErrorNum++) {
				int[] errorIndices = {lineErrors.get(line).get(lineErrorNum)[0], lineErrors.get(line).get(lineErrorNum)[1], ERROR_NUMBER};
				result.add(errorIndices);
			}

		if(numErrors > 0) {
			//			System.out.println("all found errors:");
			//			for(int i = 0; i < result.size(); i++) {
			//				System.out.println(result.get(i)[0] + "-" + result.get(i)[1] + " (error " + result.get(i)[2] + ")");
			//			}
		}

		return result;
	}
}
