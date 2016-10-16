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
 * 
 * @author tedpyne
 * Find errors with vague "this" or "which" instances
 */
public class VagueThisWhichError extends Error {

	public static void main(String[] args){
		String test = "Hi, my name I hate this; cars are fun.";
		ArrayList<int[]> errs = new VagueThisWhichError().findErrors(test);
		for(int[] err: errs){
			System.out.println(err[0] + " " + err[1] );
			System.out.println(test.substring(err[0], err[1]));
		}
	}
	public ArrayList<int[]> findErrors(String text){
		
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
		int totLen =0;
		try {
			while ((line = lineStream.read()) != null) {
				
				String tokens[] = tokenizer.tokenize(line);
				String[] tags = tagger.tag(tokens);
				int wFound = 0, tFound = 0;
				for(int i = 0; i < tokens.length; i++){
					if(tokens[i].equalsIgnoreCase("this")){
						if(isVague(tokens,tags,i)){
							int[] err = {totLen+locationOf(line,tokens[i],tFound)-1,
									totLen+locationOf(line,tokens[i],tFound)+tokens[i].length()-1};
							found.add(err);	
						}
						tFound++;
					}
					if(tokens[i].equalsIgnoreCase("which")){
						if(i == 0 || tags[i-1].charAt(0)!='N'){
							int[] err = {totLen+locationOf(line,tokens[i],wFound)-1,
									totLen+locationOf(line,tokens[i],wFound)+tokens[i].length()-1};
							found.add(err);	
						}
						wFound++;
					}
				}
				totLen+=line.length();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return found;
	}

	private boolean isVague(String[] tokens, String[] tags, int i) {
		if(i==tokens.length-1) return true;
		for(int j = i+1; j < tokens.length; j++){
			System.out.println(tags[j]);
			if(tags[j].charAt(0)=='N') return false;
			if(tags[j].charAt(0)=='V' || tags[j].charAt(0)=='.' || tags[j].charAt(0)==':') return true;
		}
		return true;
	}
}
