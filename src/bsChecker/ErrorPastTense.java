package bsChecker;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

/**
 * @author
 * Finds verbs in the past tense. (1)
 */
public class ErrorPastTense extends Error{
	private static final int ERROR_NUMBER = 1;

	/**
	 * for testing purposes
	 */
	public static void main(String[] args) {
		Error.setupOpenNLP();
		String input = "At Mr Shimerda’s funeral, nature, specifically winter, acted to wear men down.";
		printErrors(new ErrorPastTense().findErrors(input), input);
	}

	@Override
	public ArrayList<int[]> findErrors(String text) {
		ArrayList<int[]> found = new ArrayList<int[]>();
		
		ObjectStream<String> lineStream = new PlainTextByLineStream(new StringReader(text));
		String line;
		try {
			int totLen = 0;
			while ((line = lineStream.read()) != null) {
				String lower = line.toLowerCase();
				String tokens[] = tokenizer.tokenize(line);
				String[] tags = posTagger.tag(tokens);

				ArrayList<Integer> index = new ArrayList<Integer>();


				for(int i = 0; i < tags.length; i++)
				{
					if(tags[i].equals("VBD")) {
						index.add(i);
					}
				}

				int leftValue = 0;
				for(int j = 0; j < index.size(); j++)
				{
					int len = tokens[index.get(j)].length();
					int nextInd = lower.indexOf(tokens[index.get(j)].toLowerCase(), leftValue);
					
					while((nextInd>0 && Character.isLetter(lower.charAt(nextInd-1))) || 
							(nextInd -1+len < lower.length() && Character.isLetter(lower.charAt(nextInd+len)))){
						leftValue = nextInd+1;
						nextInd = lower.indexOf(tokens[index.get(j)].toLowerCase(), leftValue);
						//System.out.println(leftValue);
					}
					int[] err = {totLen+nextInd, totLen+ nextInd + tokens[index.get(j)].length(), ERROR_NUMBER};
					found.add(err);

					// updates starting index
					leftValue = err[1];
				}
				totLen+=line.length()+1;

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
//		for(int i = 0; i < found.size(); i++)
//		{
//			System.out.print("Start: ");
//			System.out.println(found.get(i)[0]);
//			System.out.print("End: ");
//			System.out.println(found.get(i)[1]);
//
//			System.out.print("Substring: ");
//			System.out.println(text.substring(found.get(i)[0], (found.get(i)[1] + 1)));
//		}

		return found;
	}
}