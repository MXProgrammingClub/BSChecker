package error;

import java.io.IOException;
import java.io.StringReader;

import gui.Main;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.parser.Parser;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import util.ErrorList;
import util.UtilityMethods;

/**
 * Defines abstract class for types of grammatical errors
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
		ErrorList errors = new ErrorList(input, false);
		int[] error1 = {6, 12, 1};
		int[] error2 = {2, 4, 2};
		int[] error3 = {0, 1, 3};
		errors.add(error1);
		errors.add(error2);
		errors.add(error3);
		errors.sort();
		System.out.println(errors);
		errors.tokensToChars(0);
		System.out.println(errors);
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
	 * @return an ErrorList of int[3] pointers to the indices of the start and end tokens of an error
	 * 			int[0], int[1] are start and end tokens of the error
	 * 			int[2] is the error number (1 - 14)
	 */
	public abstract ErrorList findErrors(String line);
	
	public static ErrorList findAllErrors(String text) {
		ErrorList errors = new ErrorList(text, true);
		int lineNum = 1, charOffset = 0;
		String line;
		ObjectStream<String> lineStream = new PlainTextByLineStream(new StringReader(text));
		try {
			while ((line = lineStream.read()) != null) {
				System.out.println("\nAnalysing line " + lineNum + ":");
				ErrorList lineErrors = new ErrorList(line, false);
				
				for(Error e: Main.ERROR_LIST) {
					System.out.println("looking for: " + e.getClass());
					ErrorList temp = e.findErrors(line);
					lineErrors.addAll(temp);
				}
				lineErrors.sort();
				lineErrors.tokensToChars(charOffset);
				errors.addAll(lineErrors);
				
				lineNum++;
				charOffset += line.length() + 1;
			}
		} catch (IOException e) {e.printStackTrace();}
		
		System.out.println("\n" + errors);
		
		return errors;
	}
}
