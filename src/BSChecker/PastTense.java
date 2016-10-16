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
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

public class PastTense extends Error{
	private static final int ERROR_NUMBER = 1;
	
	public static void main(String[] args) {
		String input = "He walked, I walk, and she ran; therefore, I walked.";
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
		
		InputStream is;
		TokenizerModel tModel;
		try {
			is = new FileInputStream("lib/en-token.bin");
			tModel = new TokenizerModel(is);
		} catch (FileNotFoundException e1) {
			return null;
		} catch (InvalidFormatException e) {
			return null;
		} catch (IOException e) {
			return null;
		}

		Tokenizer tokenizer = new TokenizerME(tModel);
		ObjectStream<String> lineStream = new PlainTextByLineStream(new StringReader(text));
		String line;

		try {
			while ((line = lineStream.read()) != null) {

				String tokens[] = tokenizer.tokenize(line);
				String[] tags = tagger.tag(tokens);

				POSSample sample = new POSSample(tokens, tags);

				ArrayList<Integer> index = new ArrayList<Integer>();


				for(int i = 0; i < tags.length; i++)
				{
					if(tags[i].equals("VBD")){
						index.add(i);
					}
				}
				
				int leftValue = 0;
				for(int j = 0; j < index.size(); j++)
				{
					int[] err = {text.indexOf(tokens[index.get(j)], leftValue),
							text.indexOf(tokens[index.get(j)], leftValue) + tokens[index.get(j)].length() - 1,
							ERROR_NUMBER};
					found.add(err);

					// updates starting index
					leftValue = err[1];
					}
					
				}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// print final result
				for(int i = 0; i < found.size(); i++)
				{
						System.out.print("Start: ");
						System.out.println(found.get(i)[0]);
						System.out.print("End: ");
						System.out.println(found.get(i)[1]);
						
						System.out.print("Substring: ");
						System.out.println(text.substring(found.get(i)[0], (found.get(i)[1] + 1)));
				}
		
		return found;
	}
}