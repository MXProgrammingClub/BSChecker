package bschecker.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import opennlp.tools.parser.AbstractBottomUpParser;
import opennlp.tools.parser.Parse;
import opennlp.tools.util.Span;

/**
 * A class which contains many useful static methods for the project
 * @author JeremiahDeGreeff
 */
public class UtilityMethods {
	
	/**
	 * Returns whether or not a string can be found in an array of strings.
	 * @param array the array to check
	 * @param word the string to look for
	 * @return true if found, false otherwise
	 */
	public static boolean arrayContains(String[] array, String word) {
		for(String element : array)
			if(element.equalsIgnoreCase(word))
				return true;
		return false;
	}
	
	/**
	 * Replaces unicode characters with their ascii equivalents.
	 * @param text the text that has characters to be changed
	 * @return the same text with the appropriate character changes
	 */
	public static String replaceInvalidChars(String text) {
		HashMap<String, Pattern> replacements = new HashMap<String, Pattern>();
		// double quotation (")
	    replacements.put("\"", Pattern.compile("[\u201C\u201D\u201E\u201F\u275D\u275E]"));
	    // single quotation (')
	    replacements.put("\'", Pattern.compile("[\u2018\u2019\u201A\u201B\u275B\u275C]"));
	    // ellipsis (...)
	    replacements.put("...", Pattern.compile("[\u2026]"));
	    for (String replacement : replacements.keySet())
	         text = replacements.get(replacement).matcher(text).replaceAll(replacement);
	    return text;
	}
	
	/**
	 * Removes extra punctuation from the passed text that are found in quotations.
	 * @param line the text to remove punctuation from
	 * @param startChar where this line starts relative to an entire passage
	 * @param indices an ArrayList of Integers which represent the indices of any characters which are removed by the method
	 * @return a String which is the same line without the extra punctuation
	 */
	public static String removeExtraPunctuation(String line, int startChar, ArrayList<Integer> indices) {
		StringBuffer buffer = new StringBuffer(line);
		for(int i = 0; i < buffer.length(); i++){
			char c = buffer.charAt(i);
			if(c == '[' || c == ']' || c == '/')
				indices.add(startChar + i);
			if(c == '.' && i + 2 < buffer.length() && buffer.charAt(i + 1) == '.' && buffer.charAt(i + 2) == '.'){
				indices.add(startChar + i);
				indices.add(startChar + i + 1);
				indices.add(startChar + i + 2);
				i += 2;
			}
		}
		for(int j = 0; j < indices.size(); j++)
			buffer.deleteCharAt(indices.get(j) - j - startChar);
		return buffer.toString();
	}
	
	/**
	 * Parses a String using the openNLP parser.
	 * @param input the String to parse
	 * @return a String which is a parsed version of the input
	 */
	public static Parse parse(String input) {
		Parse p = new Parse(input, new Span(0, input.length()), AbstractBottomUpParser.INC_NODE, 1, 0);
		Span[] spans = Tools.getTokenizer().tokenizePos(input);
		for(int i = 0; i < spans.length; i++) {
		      Span span = spans[i];
		      p.insert(new Parse(input, span, AbstractBottomUpParser.TOK_NODE, 0, i));
		}
		p = Tools.getParser().parse(p);
//		p.showCodeTree();
		return p;
	}
	
	/**
	 * a recursive method which returns the Parse object of the token at the passed index
	 * @param parse the Parse to search
	 * @param target the index of the token - must be zero-indexed
	 * @return the Parse for the indicated token, null if the target exceeds the number of tokens in the passed Parse
	 */
	public static Parse getParseAtToken(Parse parse, int target) {
		for(Parse child : parse.getChildren()) {
			int count = countTokens(child, target + 1);
			if(count == target + 1)
				return getParseAtToken(child, target);
			else
				target -= count;
		}
		return target == 0 ? parse : null;
	}
	
	/**
	 * a recursive method which counts the number of tokens in a parse
	 * if the count reaches the passed threshold the threshold value will be returned
	 * otherwise the count will be returned
	 * @param parse the parse to traverse
	 * @param threshold the target which the count will be compared to
	 * @return the number of tokens in the parse as long as it does not exceed the threshold
	 */
	private static int countTokens(Parse parse, int threshold) {
		int count = parse.getType().equals(AbstractBottomUpParser.TOK_NODE) ? 1 : 0;
		for(Parse child : parse.getChildren()) {
			count += child.getType().equals(AbstractBottomUpParser.TOK_NODE) ? 1 : countTokens(child, threshold - count);
			if(count == threshold)
				return threshold;
		}
		return count;
	}
	
	/**
	 * returns the index in the sentence of the token of this parse if it is a token node
	 * @param parse the parse to traverse
	 * @return the index in the sentence of the token of this parse if it is a token node, -1 otherwise
	 */
	public static int getIndexOfParse(Parse parse) {
		if(!parse.getType().equals(AbstractBottomUpParser.TOK_NODE))
			return -1;
		return countPreceedingTokens(parse);
	}
	
	/**
	 * a recursive method that counts the number of tokens which occur before the node of this parse
	 * @param parse the parse to traverse
	 * @return the number of tokens which occur before the node of this parse
	 */
	private static int countPreceedingTokens(Parse parse) {
		if(parse.getType().equals(AbstractBottomUpParser.TOP_NODE))
			return 0;
		int count = 0;
		for(Parse sibling : parse.getParent().getChildren()){
			if(sibling.equals(parse))
				break;
			count += countTokens(sibling, Integer.MAX_VALUE);
		}
		count += countPreceedingTokens(parse.getParent());
		return count;
	}
	
	/**
	 * determines the range of tokens within the full parse that occur after this node
	 * note that the returned values are inclusive, exclusive
	 * @param parse the Parse whose tokens will be represented
	 * @return an int[2] with {start token, end token}
	 */
	public static int[] getTokenRange(Parse parse) {
		int start = countPreceedingTokens(parse), end = start + countTokens(parse, Integer.MAX_VALUE);
		return new int[]{start, end};
	}
	
	/**
	 * a recursive method which returns true if the passed Parse occurs within a particular tag
	 * @param parse the Parse to search
	 * @param tag the tag to search for
	 * @return true if the passed Parse occurs within the specified tag, false otherwise
	 */
	public static boolean parseHasParent(Parse parse, String tag) {
		return !parse.getType().equals(AbstractBottomUpParser.TOP_NODE) && (parse.getParent().getType().equals(tag) || parseHasParent(parse.getParent(), tag));
	}
	
	/**
	 * a recursive method which will find all Parses below the passed Parse which have the desired tag
	 * @param parse the Parse to traverse
	 * @param tag the desired tag
	 * @return an ArrayList of all Parses below the passed Parse which have the desired tag
	 */
	public static ArrayList<Parse> findParsesWithTag(Parse parse, String tag) {
		ArrayList<Parse> result = new ArrayList<Parse>();
		if(parse.getType().equals(tag))
			result.add(parse);
		for(Parse child : parse.getChildren())
			result.addAll(findParsesWithTag(child, tag));
		return result;
	}
	
}
