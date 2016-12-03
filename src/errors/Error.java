package errors;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Comparator;

import gui.Main;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.parser.Parser;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import util.UtilityMethods;

/**
 * Defines abstract class for types of grammatical errors
 * Provides static utility methods for manipulatng arrays of errors
 * @author tedpyne
 * @author JeremiahDeGreeff
 */
public abstract class Error {
	public final int ERROR_NUMBER;
	public static SentenceDetectorME sentenceDetector;
	public static Tokenizer tokenizer;
	public static NameFinderME nameFinder;
	public static POSTaggerME posTagger;
	public static Parser parser;
	
	/**
	 * for testing purposes
	 */
	public static void main(String[] args) {
		UtilityMethods.setupOpenNLP();
		String input = "I walk. The ball is round. He says: \"Hello!\"";
		ArrayList<int[]> errorTokens = new ArrayList<int[]>();
		int[] error1 = {6, 12, 1};
		int[] error2 = {2, 4, 2};
		int[] error3 = {0, 1, 3};
		errorTokens.add(error1);
		errorTokens.add(error2);
		errorTokens.add(error3);
		sort(errorTokens);
		printErrors(tokensToChars(input, errorTokens, 0), input);
	}
	
	/**
	 * default constructor which should not be called
	 */
	public Error() {
		this(0);
	}
	
	/**
	 * creates a new Error object with the given error number
	 * @param errorNum the number (1 - 14) which represents this error. 0 is invalid error
	 */
	public Error(int errorNum) {
		ERROR_NUMBER = errorNum;
	}

	/**
	 * Finds errors of a specific type in the submitted text
	 * @param line the paragraph in which to find errors
	 * @return an ArrayList of int[3] pointers to the indices of the start and end characters of an error
	 * 			int[0], int[1] are start and end characters of the error
	 * 			int[2] is the error number (1 - 14)
	 */
	public abstract ArrayList<int[]> findErrors(String line);
	
	public static ArrayList<int[]> findAllErrors(String text) {
		ArrayList<int[]> errors = new ArrayList<int[]>();
		int lineNum = 1, charOffset = 0;
		String line;
		ObjectStream<String> lineStream = new PlainTextByLineStream(new StringReader(text));
		try {
			while ((line = lineStream.read()) != null) {
				System.out.println("\nAnalysing line " + lineNum + ":");
				ArrayList<int[]> lineErrors = new ArrayList<int[]>();
				
				for(Error e: Main.ERROR_LIST) {
					System.out.println("looking for: " + e.getClass());
					ArrayList<int[]> temp = e.findErrors(line);
					lineErrors.addAll(temp);
				}
				sort(lineErrors);
				lineErrors = tokensToChars(line, lineErrors, charOffset);
				errors.addAll(lineErrors);
				
				lineNum++;
				charOffset += line.length() + 1;
			}
		} catch (IOException e) {e.printStackTrace();}
		
		System.out.println();
		printErrors(errors, text);
		
		return errors;
	}

	/**
	 * Sorts a list of all errors by location.
	 * @param list All the located errors 
	 */
	public static void sort(ArrayList<int[]> list) {
		list.sort(new Comparator<int[]>()
		{
			public int compare(int[] o1, int[] o2)
			{
				if(o1[0] == o2[0]) return 0;
				else if(o1[0] < o2[0]) return -1;
				else return 1;
			}
		});
	}

	/**
	 * prints all errors to console in a way that is easy to understand for testing purposes
	 * @param text the passage in which the errors occur
	 * @param errors list of errors to be printed
	 */
	public static void printErrors(ArrayList<int[]> errors, String text) {
		if(errors.size() > 0) {
			System.out.println("all found errors:");
			for(int i = 0; i < errors.size(); i++) {
				System.out.println(errors.get(i)[0] + "-" + errors.get(i)[1] + ": \"" + text.substring(errors.get(i)[0], errors.get(i)[1] + 1) + "\" (error " + errors.get(i)[2] + ")");
			}
		} else {
			System.out.println("no errors found!");
		}
	}
	
	/**
	 * converts int[3] which represent the tokens of an error to int[3] which represent the characters of an error
	 * @param line the single paragraph in which the errors occur
	 * @param tokenErrors an ArrayList of int[3] pointers to the tokens of each error in this paragraph sorted by start index where int[0], int[1] are start and end tokens of the error and int[2] is the error number (1 - 14)
	 * @param the beginning of this paragraph relative to the entire input for returning purposes
	 * @return an ArrayList of int[3] pointers to the character indices of each error in this paragraph sorted by start index where int[0], int[1] are start and end characters of the error and int[2] is the error number (1 - 14)	
	 */
	public static ArrayList<int[]> tokensToChars(String line, ArrayList<int[]> errorTokens, int startChar) {
		ArrayList<int[]> errorChars = new ArrayList<int[]>();
		String[] tokens = tokenizer.tokenize(line);
		boolean errorProcessed;
		int tokenIndex = 0, charIndex = 0, errorLength;

		//loop through each error
		for(int errorNum = 0; errorNum < errorTokens.size(); errorNum++) {
			errorProcessed = false;
			int[] curErrorTokens = new int[3], curErrorChars = new int[3];
			curErrorTokens = errorTokens.get(errorNum);
			curErrorChars[2] = curErrorTokens[2];

			// loop until current error is processed
			while(!errorProcessed) {
				//find next token
				while(tokens[tokenIndex].charAt(0) != line.charAt(charIndex)) {
					charIndex++;
				}
				//if token is the start of the error process it
				if(tokenIndex == curErrorTokens[0]) {
					errorLength = tokens[tokenIndex].length();

					//loop through errors that include multiple tokens
					for(int i = 1; i <= curErrorTokens[1] - curErrorTokens[0]; i++) {
						//find next token
						while(tokens[tokenIndex + i].charAt(0) != line.charAt(charIndex + errorLength)) {
							errorLength++;
						}
						errorLength += tokens[tokenIndex + i].length();
					}

					curErrorChars[0] = charIndex + startChar;
					curErrorChars[1] = charIndex + errorLength - 1 + startChar;
					errorChars.add(curErrorChars);

					errorProcessed = true;
				} else {
					charIndex += tokens[tokenIndex].length();
					tokenIndex++;
				}
			}
		}
		return errorChars;
	}
}
