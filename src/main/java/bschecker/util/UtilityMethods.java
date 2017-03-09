package main.java.bschecker.util;

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
		}
		for(int j = 0; j < indices.size(); j++)
			buffer.deleteCharAt(indices.get(j) - j - startChar);
		return buffer.toString();
	}
	
	/**
	 * finds words which are a particular part of speech within in a String
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
	
	/**
	 * removes the words from a string representation of a parse to make traversing it simpler
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
	 * lists all clause, phrase, and word tags which are found in a particular String representation of a parse
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
}
