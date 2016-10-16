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

/**
 * @author tedpyne
 * Return instances of present forms of "to be" + past participles
 */
public class PassiveVoiceError extends Error {
	private static final int ERROR_NUMBER = 9;

	public static void main(String[] args){
		String test = "This terrible Hamlet is destroyed by Claudius.";
		ArrayList<int[]> errs = new PassiveVoiceError().findErrors(test);
//		for(int[] err: errs){
//			System.out.println(err[0] + " " + err[1] );
//			System.out.println(test.substring(err[0], err[1]));
//		}
	}

	@Override		//Find forms of "to be" followed by past participle
	public ArrayList<int[]> findErrors(String text) {

		ArrayList<int[]> found = new ArrayList<int[]>();
		POSModel model = new POSModelLoader().load(new File("lib/en-pos-maxent.bin"));
		ObjectStream<String> lineStream = new PlainTextByLineStream(new StringReader(text));

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
		POSTaggerME tagger = new POSTaggerME(model);
		String line;
		int totLen = 0;
		try {
			while ((line = lineStream.read()) != null) {

				String tokens[] = tokenizer.tokenize(line);
				String[] tags = tagger.tag(tokens);
				int isFound = 0, areFound=0;
				for(int i = 0; i < tokens.length; i++){
					if(tokens[i].equalsIgnoreCase("is")){
						if(i!=tokens.length-1 && tags[i+1].equals("VBN")){
							int[] err = {totLen + locationOf(line," is ",isFound), totLen + locationOf(line," is ",isFound) + tokens[i].length()+1+tokens[i+1].length(),ERROR_NUMBER};
							found.add(err);
						}
						isFound++;
					}
					if(tokens[i].equalsIgnoreCase("are")){
						if(i!=tokens.length-1 && tags[i+1].equals("VBN")){
							int[] err = {totLen + locationOf(line," are ",areFound), totLen + locationOf(line," are ",areFound) + tokens[i].length()+1+tokens[i+1].length(),ERROR_NUMBER};
							found.add(err);
						}
						areFound++;
					}
				}
				totLen+=line.length();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return found;
	}
}
