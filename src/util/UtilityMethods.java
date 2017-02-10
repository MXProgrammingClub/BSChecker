package util;

import java.util.ArrayList;
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
	 * returns whether or not a string can be found in an array of strings
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
	 * replaces unicode characters with their ascii equivalents
	 * @param text the text that has characters to be changed
	 * @return the same text with the appropriate character changes
	 */
	public static String replaceInvalidChars(String text) {
		ArrayList<Replacement>  replacements = new ArrayList<Replacement>();
		// double quotation (")
	    replacements.add(new Replacement(Pattern.compile("[\u201C\u201D\u201E\u201F\u275D\u275E]"), "\""));
	    // single quotation (')
	    replacements.add(new Replacement(Pattern.compile("[\u2018\u2019\u201A\u201B\u275B\u275C]"), "\'"));
	    // ellipsis (...)
	    replacements.add(new Replacement(Pattern.compile("[\u2026]"), "..."));
	    for (Replacement replacement : replacements)
	         text = replacement.pattern.matcher(text).replaceAll(replacement.toString());
	    return text;
	}
	
	/**
	 * removes extra punctuation from the passed text
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
		}
		for(int j = 0; j < indices.size(); j++)
			buffer.deleteCharAt(indices.get(j) - j - startChar);
		return buffer.toString();
	}
	
	/**
	 * parses a String using the openNLP parser
	 * @param input the String to parse
	 * @return a String which is a parsed version of the input
	 */
	public static String parse(String input) {
		Parse p = new Parse(input, new Span(0, input.length()), AbstractBottomUpParser.INC_NODE, 1, 0);
		Span[] spans = Tools.getTokenizer().tokenizePos(input);
		for(int i = 0; i < spans.length; i++) {
		      Span span = spans[i];
		      p.insert(new Parse(input, span, AbstractBottomUpParser.TOK_NODE, 0, i));
		}
		p = Tools.getParser().parse(p);
		
		StringBuffer sb = new StringBuffer(input.length()*4); //arbitrary initial size
		p.show(sb);
		return sb.toString();
	}
}
