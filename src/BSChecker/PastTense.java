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
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

public class PastTense extends Error{
	private static final int ERROR_NUMBER = 1;

	public static void main(String[] args) {
		String input = "When the cold-weary party sets out to bury the coffin, nature fights back, cutting their faces with a “fine, icy snow which cut [their] faces like a sand blast” (p. 75).";
		Error tester = new PastTense();		
		ArrayList<int[]> found = tester.findErrors(input);
		for(int[] inds: found){
		System.out.println(input.substring(inds[0],inds[1]));
		}
	}

	@Override
	public ArrayList<int[]> findErrors(String text) {
		String lower = text.toLowerCase();

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
					int len = tokens[index.get(j)].length();
					int nextInd = lower.indexOf(tokens[index.get(j)].toLowerCase(), leftValue);
					
					while((nextInd>0 && Character.isLetter(lower.charAt(nextInd-1))) || 
							(nextInd -1+len < lower.length() && Character.isLetter(lower.charAt(nextInd+len)))){
						leftValue = nextInd+1;
						nextInd = lower.indexOf(tokens[index.get(j)].toLowerCase(), leftValue);
						//System.out.println(lower.charAt(nextInd-1));
					}
					int[] err = {nextInd,
							nextInd + tokens[index.get(j)].length(),
							ERROR_NUMBER};
					found.add(err);

					// updates starting index
					leftValue = err[1];
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// print final result
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