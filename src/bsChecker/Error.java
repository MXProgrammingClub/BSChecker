package bsChecker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;

import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;

/**
 * @author tedpyne
 * @author JeremiahDeGreeff
 * Defines abstract class for types of grammatical errors
 */
public abstract class Error {
	public static SentenceDetectorME sentenceDetector;
	public static Tokenizer tokenizer;
	public static NameFinderME nameFinder;
	public static POSTaggerME posTagger;
	public static Parser parser;

	public static void main(String[] args) {
		setupOpenNLP();
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
	 * Initializes all the necessary OpenNLP tools
	 */
	public static void setupOpenNLP() {
		InputStream is = null;

		//SentenceDetector
		SentenceModel sModel = null;
		try {is = new FileInputStream("lib/en-sent.bin");}
		catch (FileNotFoundException e1) {e1.printStackTrace();}
		try {sModel = new SentenceModel(is);}
		catch (InvalidFormatException e1) {e1.printStackTrace();}
		catch (IOException e1) {e1.printStackTrace();}

		//NameFinder
		TokenNameFinderModel nModel = null;
		try {is = new FileInputStream("lib/en-ner-person.bin");}
		catch (FileNotFoundException e1) {e1.printStackTrace();}
		try {nModel = new TokenNameFinderModel(is);}
		catch (InvalidFormatException e1) {e1.printStackTrace();}
		catch (IOException e1) {e1.printStackTrace();}

		//Tokenizer
		TokenizerModel tModel = null;
		try {is = new FileInputStream("lib/en-token.bin");}
		catch (FileNotFoundException e1) {e1.printStackTrace();}
		try {tModel = new TokenizerModel(is); }
		catch (InvalidFormatException e1) {e1.printStackTrace();}
		catch (IOException e1) {e1.printStackTrace();}

		//POSTagger
		POSModel posModel = new POSModelLoader().load(new File("lib/en-pos-maxent.bin"));

		//Parser
		ParserModel pModel = null;
		try {is = new FileInputStream("lib/en-parser-chunking.bin");}
		catch (FileNotFoundException e1) {e1.printStackTrace();}
		try {pModel = new ParserModel(is); }
		catch (InvalidFormatException e1) {e1.printStackTrace();}
		catch (IOException e1) {e1.printStackTrace();}

		sentenceDetector = new SentenceDetectorME(sModel);
		nameFinder = new NameFinderME(nModel);
		tokenizer = new TokenizerME(tModel);
		posTagger = new POSTaggerME(posModel);
		parser = ParserFactory.create(pModel);

		try {is.close();}
		catch (IOException e) {e.printStackTrace();}
	}

	/**
	 * Finds errors of a specific type in the submitted text
	 * @param line the paragraph in which to find errors
	 * @return an ArrayList of int[3] pointers to the indices of the start and end characters of an error
	 * 			int[0], int[1] are start and end characters of the error
	 * 			int[2] is the error number (1 - 14)
	 */
	public abstract ArrayList<int[]> findErrors(String line);

	/**
	 * 
	 * @param line The text to search through
	 * @param string The word to find in the text
	 * @param found The number of occurrences already found
	 * @return The location of the n+1th instance
	 */
	public static int locationOf(String line, String string, int found) {
		int loc = 0;
		for(int i = 0; i <= found; i++){
			loc = line.indexOf(string,loc) +1;
		}
		return loc;
	}

	/**
	 * Sorts the list of all errors by location.
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

					tokenIndex += curErrorTokens[1] - curErrorTokens[0] + 1;
					charIndex += errorLength;

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
