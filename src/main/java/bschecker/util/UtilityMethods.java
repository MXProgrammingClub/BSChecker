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
		if(array == null)
			return false;
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
		HashMap<String, Pattern> replacements = new HashMap<String, Pattern>(3);
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
		boolean inParentheses = false;
		for(int i = 0; i < buffer.length(); i++) {
			char c = buffer.charAt(i);
			if(c == ')')
				inParentheses = false;
			if(inParentheses || c == '[' || c == ']' || c == '/')
				indices.add(startChar + i);
			if(c == '(')
				inParentheses = true;
			if(c == '.' && i + 2 < buffer.length() && buffer.charAt(i + 1) == '.' && buffer.charAt(i + 2) == '.') {
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
	 * @return the Parse of the input String
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
	 * returns the index in the sentence of the token of this parse
	 * @param parse the parse to traverse
	 * @return the index in the sentence of the token of this parse
	 * @throws IllegalArgumentException if the parse is not a token node
	 */
	public static int getIndexOfParse(Parse parse) {
		if(!parse.getType().equals(AbstractBottomUpParser.TOK_NODE)) {
			LogHelper.getLogger(17).error("Parse must be a token node.");
			throw new IllegalArgumentException("Parse must be a token node.");
		}
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
	 * a recursive method which finds all Parses below the passed Parse which have any of the the desired tags
	 * @param parse the Parse to traverse
	 * @param tags a String array of the desired tags
	 * @return an ArrayList of all Parses below the passed Parse which have any of the the desired tags
	 */
	public static ArrayList<Parse> findParsesWithTag(Parse parse, String[] tags) {
		ArrayList<Parse> result = new ArrayList<Parse>();
		if(arrayContains(tags, parse.getType()))
			result.add(parse);
		for(Parse child : parse.getChildren())
			result.addAll(findParsesWithTag(child, tags));
		return result;
	}
	
	/**
	 * determines the index of this node of the Parse among its siblings
	 * assumes that the passed Parse object is not the TOP node
	 * @param parse the Parse to traverse whose node will be indexed
	 * @return the index of this node of the Parse among its siblings
	 */
	public static int getSiblingIndex(Parse parse) {
		Parse[] siblings = parse.getParent().getChildren();
		for(int i = 0; i < siblings.length; i++)
			if(siblings[i].equals(parse))
				return i;
		return -1;
	}
	
	/**
	 * a recursive method which finds the the first token node which occurs after this node
	 * ignores any nodes whose type is included in the passed array
	 * @param parse the Parse to traverse
	 * @param ignore a String[] of types to ignore
	 * @return the Parse of the first token node which occurs after this node and whose type is not included in the passed array, null if no such node is found
	 */
	public static Parse getNextToken(Parse parse, String[] ignore) {
		if(parse.getType().equals(AbstractBottomUpParser.TOP_NODE))
			return null;
		int siblingIndex = getSiblingIndex(parse);
		if(siblingIndex + 1 < parse.getParent().getChildCount()) {
			Parse child = parse.getParent().getChildren()[siblingIndex + 1].getChildren()[0];
			while(!child.getType().equals("TK"))
				child = child.getChildren()[0];
			if(arrayContains(ignore, child.getParent().getType()))
				return getNextToken(child, ignore);
			return child;
		}
		return getNextToken(parse.getParent(), ignore);
	}
	
}
