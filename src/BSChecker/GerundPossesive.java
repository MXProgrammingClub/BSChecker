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
 * algorithms for incorrect lack of possessives with gerunds (error 12)
 */
public class GerundPossesive extends Error {

	/**
	 * for testing purposes
	 */
	public static void main(String[] args) {
		Error testOb = new GerundPossesive();
		String testText = "I saw the boy, running from danger";
		testOb.findErrors(testText);
	}

	@Override
	public ArrayList<int[]> findErrors(String text) {
		POSModel model = new POSModelLoader().load(new File("lib/en-pos-maxent.bin"));
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

				ArrayList<Integer> errorIndices = findGerundPossesive(tokenizerLine, tags);
				System.out.println();
				lineErrors.add(findLoc(errorIndices, text, tokenizerLine));
				System.out.println();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return combineLineErrors(lineErrors);
	}

	/**
	 * finds all the gerunds without possessives where there should be
	 * @param tokenizerLine the tokens of the line
	 * @param tags the array of the tag for each token
	 * @return the indices of each token which is an error
	 */
	private ArrayList<Integer> findGerundPossesive(String[] tokenizerLine, String[] tags) {
		//finds gerunds and participles
		ArrayList<Integer> errorIndices = new ArrayList<Integer>();
		for(int i = 0; i < tags.length; i++)
		{
			if(tags[i].equals("VBG"))
				errorIndices.add(i);
		}
		//prints token number with gerund or participle
		for(int i = 0; i < errorIndices.size(); i++)
			System.out.println(errorIndices.get(i) + ": " + tokenizerLine[errorIndices.get(i)]);

		//checks if each gerund or participle has a non possessive noun or pronoun before it
		int errorNum = 0;
		String word, tag;
		boolean isError;
		while(errorNum < errorIndices.size()) {				
			if(errorIndices.get(errorNum) == 0) {
				errorIndices.remove(errorNum);
			} else {
				word = tokenizerLine[errorIndices.get(errorNum) - 1];
				tag = tags[errorIndices.get(errorNum) - 1];
				System.out.println((errorIndices.get(errorNum) - 1) + ": " + word + " (" + tag + ")");
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
	 * @param errorIndices the indices of errors that have been found
	 * @param text the original text
	 * @param tokenizerLine the tokens of the text
	 * @return the list of errors for this line
	 */
	private ArrayList<int[]> findLoc(ArrayList<Integer> errorIndices, String text, String[] tokenizerLine) {
		int[] startIndeces = new int[errorIndices.size()], endIndeces = new int[errorIndices.size()];
		int cursor = 0, start, end;

		for(int i = 0; i < errorIndices.size(); i++) {
			System.out.println("error found: ");
			System.out.println("\"" + tokenizerLine[errorIndices.get(i) - 1] + " " + tokenizerLine[errorIndices.get(i)] + "\"");
			start = text.indexOf(tokenizerLine[errorIndices.get(i) - 1] + " " + tokenizerLine[errorIndices.get(i)], cursor);
			end = start + (tokenizerLine[errorIndices.get(i) - 1] + tokenizerLine[errorIndices.get(i)]).length();
			startIndeces[i] = start;
			endIndeces[i] = end;
			System.out.println("character indices: " + start + "-" + end);
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

		System.out.println("all found errors:");
		for(int i = 0; i < result.get(0).length; i++) {
			System.out.println(result.get(0)[i] + "-" + result.get(1)[i]);
		}
		return result;
	}
}
