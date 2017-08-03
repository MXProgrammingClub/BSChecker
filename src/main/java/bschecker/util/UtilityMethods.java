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
	 * Removes extra punctuation from the passed text.
	 * @param line the text to remove punctuation from
	 * @param startChar where this line starts relative to an entire passage
	 * @param indices an ArrayList of Integers which represent the indices of any characters which are removed by the method
	 * @return a String which is the same line without the extra punctuation
	 */
	public static String removeExtraPunctuation(String line, int startChar, ArrayList<Integer> indices) {
		StringBuffer buffer = new StringBuffer(line);
		boolean inParens = false;
		for(int i = 0; i < buffer.length(); i++){
			char c = buffer.charAt(i);
			if(c == '('){
				inParens = true;
				indices.add(startChar + i - 1); //remove space before parentheses
			}
			if(inParens || c == '[' || c == ']' || c == '/')
				indices.add(startChar + i);
			if(c == ')')
				inParens = false;
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
	 * Finds words which are a particular part of speech within in a String.
	 * @param input the String to search
	 * @param tag the openNLP tag for the desired part of speech
	 * @return an ArrayList of Integers which represent the indices of tokens which are the desired part of speech
	 */
	public static ArrayList<Integer> findTokenTags(String input, String tag) {
		String[] tokens = Tools.getTokenizer().tokenize(input);
		String[] tags = Tools.getPOSTagger().tag(tokens);
		
		ArrayList<Integer> indices = new ArrayList<Integer>();
		for(int i = 0; i < tags.length; i++)
			if(tags[i].equals(tag))
				indices.add(i);
		return indices;
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
		
		p.showCodeTree();
		
		return p;
	}
	
	/**
	 * Removes the words from a string representation of a parse to make traversing it simpler.
	 * @param parse the original string representation of the parse
	 * @return a String which represents the same parse as the original String but without any words
	 */
	public static String simplifyParse(String parse) {
		StringBuffer simplifiedParse = new StringBuffer(parse.length()/2); //arbitrary initial size
		for(int i = 0; i < parse.length(); i++){
			if(parse.charAt(i) == '('){
				if(i != 0 && parse.charAt(i + 1) != '.' && parse.charAt(i + 1) != ',' && parse.charAt(i + 1) != ':')
					simplifiedParse.append(" ");
				simplifiedParse.append(parse.substring(i, i + parse.substring(i).indexOf(' ')));
				i += parse.substring(i).indexOf(' ');
			}else if(parse.charAt(i) == ')')
				simplifiedParse.append(')');
		}
		return simplifiedParse.toString();
	}
	
	/**
	 * Lists all clause, phrase, and word tags which are found in a particular String representation of a parse.
	 * @param parse the String representation of the parse
	 * @return an ArrayList of Strings which are each tag of the parse in order
	 */
	public static ArrayList<String> listParseTags(String parse) {
		ArrayList<String> tags = new ArrayList<String>();
		int leftParen = 0;
		while(leftParen < parse.length()){
			tags.add(parse.substring(leftParen + 1, leftParen + parse.substring(leftParen).indexOf(' ')));
			if(parse.substring(leftParen + 1).indexOf('(') == -1)
				break;
			leftParen += parse.substring(leftParen + 1).indexOf('(') + 1;
		}
		return tags;
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
	 * a recursive method which returns true if the passed Parse occurs within a particular tag
	 * @param parse the Parse to search
	 * @param tag the tag to search for
	 * @return true if the passed Parse occurs within the specified tag, false otherwise
	 */
	public static boolean parseHasParent(Parse parse, String tag) {
		return !parse.getType().equals(AbstractBottomUpParser.TOP_NODE) && (parse.getParent().getType().equals(tag) || parseHasParent(parse.getParent(), tag));
	}
	
}
