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

	public static void main(String[] args)
	{
		Error testOb = new ProgressiveTense();
		String testText = "Sensing God's desire to destroy Sodom, Abraham is negotiating for a less apocalyptic punishment";

		testOb.findErrors(testText);
	}

	@Override
	public ArrayList<int[]> findErrors(String text) {

		POSModel model = new POSModelLoader().load(new File("lib/en-pos-maxent.bin"));
		POSTaggerME tagger = new POSTaggerME(model);
		String[] toBeConj = {"be", "am", "are", "is"};
		String line;

		ObjectStream<String> lineStream = new PlainTextByLineStream(new StringReader(text));

		try {
			while ((line = lineStream.read()) != null) {
				String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE.tokenize(line);
				String[] tags = tagger.tag(whitespaceTokenizerLine);
//				POSSample token = new POSSample(whitespaceTokenizerLine, tags);

				ArrayList<Integer> errorIndices = new ArrayList<Integer>();
				for(int i = 0; i < tags.length; i++)
				{
					//finds gerunds and participles
					if(tags[i].equals("VBG"))
						errorIndices.add(i);
				}
				for(int i = 0; i < errorIndices.size(); i++)
				{
					System.out.println(errorIndices.get(i) + ": " + whitespaceTokenizerLine[errorIndices.get(i)]);
				}

				int errorNum = 0;
				String word = null;
				boolean isError = false;
				while(errorNum < errorIndices.size()) {				
					if(errorIndices.get(errorNum) == 0) {
						errorIndices.remove(errorNum);
					}
					else {
						word = whitespaceTokenizerLine[errorIndices.get(errorNum) - 1];
						System.out.println(word);
						for(int i = 0; i < 4; i++)
							if(word.equals(toBeConj[i]))
								isError = true;
						if(isError)
							errorNum++;
						else
							errorIndices.remove(errorNum);
					}
				}
				
				int[] startIndeces = new int[errorIndices.size()];
				int[] endIndeces = new int[errorIndices.size()];
				int cursor = 0, start, end;
				for(int i = 0; i < errorIndices.size(); i++)
				{
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

//				System.out.println(token.toString());
//				for(String s:whitespaceTokenizerLine)
//				{
//					System.out.println(s);
//				}
//				for(String s:tags)
//				{
//					System.out.println(s);
//				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
