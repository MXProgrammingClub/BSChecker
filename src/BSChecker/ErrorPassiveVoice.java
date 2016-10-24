package BSChecker;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

/**
 * @author tedpyne
 * Return instances of present forms of "to be" + past participles
 */
public class ErrorPassiveVoice extends Error {
	private static final int ERROR_NUMBER = 9;

//	/**
//	 * for testing purposes
//	 */
//	public static void main(String[] args){
//		Error.setupOpenNLP();
//		String test = "This terrible Hamlet is destroyed by Claudius.";
//		ArrayList<int[]> errs = new ErrorPassiveVoice().findErrors(test);
//		for(int[] err: errs){
//			System.out.println(err[0] + " " + err[1] );
//			System.out.println(test.substring(err[0], err[1]));
//		}
//	}

	@Override
	public ArrayList<int[]> findErrors(String text) {
		ArrayList<int[]> found = new ArrayList<int[]>();
		int totLen = 0;
		
		ObjectStream<String> lineStream = new PlainTextByLineStream(new StringReader(text));
		String line;
		try {
			while ((line = lineStream.read()) != null) {
				String tokens[] = tokenizer.tokenize(line);
				String[] tags = posTagger.tag(tokens);
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
				totLen+=line.length()+1;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return found;
	}
}
