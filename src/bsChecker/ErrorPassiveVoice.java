package bsChecker;

import java.util.ArrayList;

/**
 * @author tedpyne
 * Finds verbs in the passive voice. (9)
 */
public class ErrorPassiveVoice extends Error {
	private static final int ERROR_NUMBER = 9;

	/**
	 * for testing purposes
	 */
	public static void main(String[] args){
		Error.setupOpenNLP();
		String input = "This terrible Hamlet is destroyed by Claudius.";
		printErrors(new ErrorPassiveVoice().findErrors(input), input);
	}

	@Override
	public ArrayList<int[]> findErrors(String line) {
		ArrayList<int[]> found = new ArrayList<int[]>();
		int totLen = 0;
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
		return found;
	}
}
