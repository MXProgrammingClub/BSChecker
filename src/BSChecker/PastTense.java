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
		String input = "She died and he was sad.";
		Error tester = new PastTense();		
		tester.findErrors(input);
	}


	@Override
	public ArrayList<int[]> findErrors(String text) {

		POSModel model = new POSModelLoader()	
				.load(new File("lib/en-pos-maxent.bin"));
		POSTaggerME tagger = new POSTaggerME(model);

		ObjectStream<String> lineStream = new PlainTextByLineStream(new StringReader(text));
		String line;
		String output;

		try {
			while ((line = lineStream.read()) != null) {

				String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE
						.tokenize(line);
				String[] tags = tagger.tag(whitespaceTokenizerLine);

				POSSample sample = new POSSample(whitespaceTokenizerLine, tags);

				for(String s:whitespaceTokenizerLine)
				{
					System.out.println(s);
				}
				for(String s:tags)
				{
					System.out.println(s);
				}


				ArrayList<Integer> index = new ArrayList<Integer>();

				ArrayList<Integer> errorStart = new ArrayList<Integer>();
				
				ArrayList<Integer> errorEnd = new ArrayList<Integer>();


				for(int i = 0; i < tags.length; i++)
				{
					if(tags[i].equals("VBD")){
						index.add(i);
					}
				}
				
				
				// System.out.println(index);


				for(int j = 0; j < index.size(); j++)
				{
					errorStart.add(text.indexOf(whitespaceTokenizerLine[index.get(j)]));
					errorEnd.add(text.indexOf(whitespaceTokenizerLine[index.get(j)]) + whitespaceTokenizerLine[index.get(j)].length() - 1);
				}
				
//				System.out.println(text.indexOf(whitespaceTokenizerLine[1]));
				
				for (int i:errorStart)
				{
					System.out.println("Start");
					System.out.println(i);
				}
				
				for (int i:errorEnd)
				{
					System.out.println("End");
					System.out.println(i);
				}




				output = sample.toString();
				//System.out.println(output);	
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return null;
	}
}