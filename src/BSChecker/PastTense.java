package BSChecker;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

public class PastTense extends Error{
	public static void main(String[] args) {
		String input = "Hamlet walked to the store, I walked to the mall.";
		Error tester = new PastTense();		
		ArrayList<int[]> found = tester.findErrors(input);
		for(int[] inds: found){
			System.out.println(inds[0] + " " + inds[1]);
		}
	}


	@Override
	public ArrayList<int[]> findErrors(String text) {

		ArrayList<int[]> found = new ArrayList<int[]>();
		POSModel model = new POSModelLoader()	
				.load(new File("lib/en-pos-maxent.bin"));
		POSTaggerME tagger = new POSTaggerME(model);

		ObjectStream<String> lineStream = new PlainTextByLineStream(new StringReader(text));
		String line;

		try {
			while ((line = lineStream.read()) != null) {

				String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE
						.tokenize(line);
				String[] tags = tagger.tag(whitespaceTokenizerLine);

				POSSample sample = new POSSample(whitespaceTokenizerLine, tags);

				ArrayList<Integer> index = new ArrayList<Integer>();


				for(int i = 0; i < tags.length; i++)
				{
					if(tags[i].equals("VBD")){
						index.add(i);
					}
				}
				
				
				for(int j = 0; j < index.size(); j++)
				{
					int lastError = 0;
					boolean contains = text.contains(whitespaceTokenizerLine[index.get(j)]);
					
					while(contains)
					{
						int[] err = {text.indexOf(whitespaceTokenizerLine[index.get(j)]) + lastError,
							text.indexOf(whitespaceTokenizerLine[index.get(j)]) + lastError + whitespaceTokenizerLine[index.get(j)].length() - 1};
						found.add(err);

						// update last error index
						lastError = text.indexOf(whitespaceTokenizerLine[index.get(j)]) + whitespaceTokenizerLine[index.get(j)].length() - 1;
						
						// trims the text string
						text = text.substring(lastError);
						contains = text.contains(whitespaceTokenizerLine[index.get(j)]);
					}
					
				}
				//System.out.println(output);	
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return found;
	}
}