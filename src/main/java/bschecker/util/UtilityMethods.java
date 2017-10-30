package bschecker.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;

import opennlp.tools.parser.AbstractBottomUpParser;
import opennlp.tools.parser.Parse;
import opennlp.tools.util.Span;

/**
 * A class which contains many useful static methods for the project.
 * 
 * @author JeremiahDeGreeff
 */
public class UtilityMethods {
	
	/**
	 * Returns whether or not a string can be found in an array of strings.
	 * 
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
	 * Counts the number of occurrences of a String within another String.
	 * 
	 * @param text the String to search
	 * @param target the String to search for
	 * @return the number of occurrences of target within text
	 */
	public static int countOccurences(String text, String target) {
		return text == null || target == null || !text.contains(target) ? 0 : 1 + countOccurences(text.substring(text.indexOf(target) + 1), target);
	}
	
	/**
	 * Replaces unicode characters with their ascii equivalents.
	 * 
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
	 * Parses a paragraph and removes any extra punctuation from it.
	 * Also handles parsing failures.
	 * 
	 * @param linePointer a pointer to the line to parse where linePointer[0] is the line - allows for changes to line to be accessed after method call
	 * @param logParses if true, all Parse trees will be logged to the console - should only be used for debugging
	 * @param lineNum the number of this line within the entire passage - used only for reporting parsing failures
	 * @param charOffset the number of characters in the entire passage before the start of this line
	 * @param removedChars an Integer ArrayList (probably empty) to which the location of any extra punctuation will be added
	 * @return an array of the Parses of this line
	 */
	public static Parse[] parseLine(String[] linePointer, boolean logParses, int lineNum, int charOffset, ArrayList<Integer> removedChars) {
		linePointer[0] = removeExtraPunctuation(linePointer[0], charOffset, removedChars);
		LogHelper.getLogger(18).info("Ignoring characters: " + rangeFormat(removedChars));
		
		String[] sentences = separateSentences(linePointer[0]);
		Parse[] parses = new Parse[sentences.length];
		ArrayList<int[]> incompleteRanges = new ArrayList<int[]>();
		for(int i = 0; i < sentences.length; i++) {
			parses[i] = parse(sentences[i], logParses);
			if(!parses[i].complete()) {
				LogHelper.getLogger(18).error("Warning: parsing failure in sentence " + (i + 1) + " of line " + lineNum + " - this sentence will not be analyzed!"); //not zero-indexed
				int characterOffset = 0;
				for(int j = 0; j < i; j++)
					characterOffset += sentences[j] == null ? 0 : sentences[j].length();
				incompleteRanges.add(new int[] {characterOffset, characterOffset + sentences[i].length()});
				sentences[i] = null;
				parses[i] = null;
			}
		}
		
		if(!incompleteRanges.isEmpty()) {
			parses = copyParsesWithRemoval(parses, incompleteRanges.size());
			linePointer[0] = removeAdditionalRanges(linePointer[0], removedChars, incompleteRanges);
			LogHelper.getLogger(18).info("Now ignoring characters: " + rangeFormat(removedChars));
		}
		
		return parses;
	}
	
	/**
	 * Removes extra punctuation from the passed text that are found in quotations or parentheses.
	 * Updates the passed ArrayList to include the indices of any removed characters in order.
	 * 
	 * @param line the text to remove punctuation from
	 * @param startChar where this line starts relative to an entire passage
	 * @param indices an ArrayList of Integers which represent the indices of any characters which are removed by the method
	 * @return a String which is the same line without the extra punctuation
	 */
	private static String removeExtraPunctuation(String line, int startChar, ArrayList<Integer> indices) {
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
	 * Removes ranges of characters from a line and adds their indices to an ArrayList.
	 * Assumes that the ranges are measured with the indices already ignored not being accounted for.
	 * Expects all ranges to be int[2] where the first number is less than the second.
	 * Expects the ranges to be sorted and not overlapping
	 * 
	 * @param line the line to remove ranges of characters from
	 * @param indices the existing ArrayList of indices
	 * @param newRanges the int[2] ranges of indices to add, indexed without account for those already in the ArrayList
	 * @return line with the specified ranges removed
	 */
	private static String removeAdditionalRanges(String line, ArrayList<Integer> indices, ArrayList<int[]> newRanges) {
		int offset = 0;
		for(int i = 0; i < newRanges.size(); i++) {
			line = line.substring(0, newRanges.get(i)[0]) + line.substring(newRanges.get(i)[1] + 1);
			while(indices.get(offset) <= newRanges.get(i)[0] + offset)
				offset++;
			int insideOffset = 0;
			while(offset + insideOffset < indices.size() && indices.get(offset + insideOffset) <= newRanges.get(i)[1] + offset + insideOffset)
				insideOffset++;
			for(int j = offset; j < offset + insideOffset; j++)
				indices.remove(offset);
			for(int j = offset; j <= offset + insideOffset + newRanges.get(i)[1] - newRanges.get(i)[0]; j++)
				indices.add(j, newRanges.get(i)[0] + j);
			offset += insideOffset + newRanges.get(i)[1] - newRanges.get(i)[0];
			for(int j = i + 1; j < newRanges.size(); j++) {
				newRanges.get(j)[0] -= (newRanges.get(i)[1] - newRanges.get(i)[0]);
				newRanges.get(j)[1] -= (newRanges.get(i)[1] - newRanges.get(i)[0]);
			}
		}
		return line;
	}
	
	/**
	 * Formats the string representation of an ArrayList of Integers to condense ranges of numbers.
	 * Expects all elements in the ArrayList to be in order and unique.
	 * 
	 * @param indices the ArrayList of Integers to format
	 * @return a formated string representing this ArrayList
	 */
	private static String rangeFormat(ArrayList<Integer> indices) {
		StringBuffer buffer = new StringBuffer(indices.size() * 2); //arbitrary initial size
		buffer.append('[');
		for(int i = 0; i < indices.size(); i++) {
			buffer.append(indices.get(i));
			int j = i;
			while(j + 1 < indices.size() && indices.get(j) + 1 == indices.get(j + 1))
				j++;
			String separator = j + 1 == indices.size() ? "" : ", ";
			buffer.append(i == j ? separator : "-" + indices.get(j) + separator);
			i = j;
		}
		buffer.append(']');
		return buffer.toString();
	}
	
	/**
	 * Uses the opennlp SentenceDetector to separate a paragraph into sentences.
	 * Tries to combine sentences which are separated due to having periods within quotes.
	 * 
	 * @param line the paragraph to separate
	 * @return a String[] of the sentences in the paragraph
	 */
	private static String[] separateSentences(String line) {
		ArrayList<String> sentences = new ArrayList<String>();
		sentences.addAll(Arrays.asList(Tools.getSentenceDetector().sentDetect(line)));
		boolean previousSentenceOdd = false;
		for(int i = 0; i < sentences.size(); i++) {
			boolean evenQuotations = true;
			int cursor = 0;
			while(sentences.get(i).substring(cursor).contains("\"")) {
				evenQuotations = !evenQuotations;
				cursor += sentences.get(i).substring(cursor).indexOf('\"') + 1;
			}
			if(!evenQuotations)
				if(previousSentenceOdd) {
					sentences.set(i - 1, sentences.get(i - 1) + " " + sentences.get(i));
					sentences.remove(i);
					previousSentenceOdd = false;
					i--;
				} else
					previousSentenceOdd = true;
		}
		return sentences.toArray(new String[sentences.size()]);
	}
	
	/**
	 * Parses a String using the openNLP parser.
	 * 
	 * @param input the String to parse
	 * @param logParse if true, all Parse trees will be logged to the console - should only be used for debugging
	 * @return the Parse of the input String
	 */
	private static Parse parse(String input, boolean logParse) {
		LogHelper.getLogger(18).debug(input);
		Parse p = new Parse(input, new Span(0, input.length()), AbstractBottomUpParser.INC_NODE, 1, 0);
		Span[] spans = Tools.getTokenizer().tokenizePos(input);
		for(int i = 0; i < spans.length; i++) {
		      Span span = spans[i];
		      p.insert(new Parse(input, span, AbstractBottomUpParser.TOK_NODE, 0, i));
		}
		p = Tools.getParser().parse(p);
		if(logParse)
			p.showCodeTree();
		return p;
	}
	
	/**
	 * Removes null parses from an array
	 * 
	 * @param parses the initial array
	 * @param removeCount the number of Parses to be removed
	 * @return a Parse[] with all null elements removed
	 */
	private static Parse[] copyParsesWithRemoval(Parse[] parses, int removeCount) {
		Parse[] copyParses = new Parse[parses.length - removeCount];
		int offset = 0;
		for(int i = 0; i < copyParses.length; i++) {
			while(parses[i + offset] == null)
				offset++;
			copyParses[i] = parses[i + offset];
		}
		return copyParses;
	}
	
	/**
	 * Returns the Parse object of the token at the passed index.
	 * 
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
	 * Counts the number of tokens in a parse.
	 * If the count reaches the passed threshold, the threshold value will be returned, otherwise the count will be returned.
	 * 
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
	 * Returns the index in the sentence of the token a parse.
	 * 
	 * @param parse the parse to traverse
	 * @return the index in the sentence of the token the parse
	 * @throws IllegalArgumentException if the parse is not a token node
	 */
	public static int getIndexOfParse(Parse parse) {
		if(!parse.getType().equals(AbstractBottomUpParser.TOK_NODE))
			throw new IllegalArgumentException("Parse must be a token node.");
		return countPreceedingTokens(parse);
	}
	
	/**
	 * Counts the number of tokens which occur before the node a parse.
	 * 
	 * @param parse the parse to traverse
	 * @return the number of tokens which occur before the node the parse
	 */
	private static int countPreceedingTokens(Parse parse) {
		if(parse.getType().equals(AbstractBottomUpParser.TOP_NODE))
			return 0;
		int count = 0;
		for(Parse sibling : parse.getParent().getChildren()) {
			if(sibling.equals(parse))
				break;
			count += countTokens(sibling, Integer.MAX_VALUE);
		}
		count += countPreceedingTokens(parse.getParent());
		return count;
	}
	
	/**
	 * Determines the range of tokens within the full parse that occur after this node.
	 * Note that the returned values are inclusive, exclusive,
	 * 
	 * @param parse the Parse whose tokens will be represented
	 * @return an int[2] with {start token, end token}
	 */
	public static int[] getTokenRange(Parse parse) {
		int start = countPreceedingTokens(parse), end = start + countTokens(parse, Integer.MAX_VALUE);
		return new int[]{start, end};
	}
	
	/**
	 * Returns whether or not the passed Parse has an ancestor with a particular tag.
	 * 
	 * @param parse the Parse to search
	 * @param tag the tag to search for
	 * @return true if the passed Parse has an ancestor with a node with the specified tag, false otherwise
	 */
	public static boolean parseHasParent(Parse parse, String tag) {
		return parse != null && !parse.getType().equals(AbstractBottomUpParser.TOP_NODE) && (parse.getParent().getType().equals(tag) || parseHasParent(parse.getParent(), tag));
	}
	
	/**
	 * Returns the nearest ancestor of the passed Parse with a particular tag.
	 * 
	 * @param parse the Parse to search
	 * @param tag the tag to search for
	 * @return the nearest ancestor of the passed Parse with the specified tag, null if no such ancestor exists
	 */
	public static Parse getParentWithTag(Parse parse, String tag) {
		return parse == null || parse.getType().equals(AbstractBottomUpParser.TOP_NODE) ? null : parse.getParent().getType().equals(tag) ? parse.getParent() : getParentWithTag(parse.getParent(), tag);
	}
	
	/**
	 * Finds all Parses below the passed Parse which have any of the the desired tags.
	 * 
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
	 * Determines the index of this node of the Parse among its siblings.
	 * Assumes that the passed Parse object is not the TOP node.
	 * 
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
	 * Finds the the first token node which occurs before this node.
	 * Ignores any nodes whose type is included in the passed array.
	 * 
	 * @param parse the Parse to traverse
	 * @param ignore a String[] of types to ignore
	 * @return the Parse of the first token node which occurs before this node and whose type is not included in the passed array, null if no such node is found
	 */
	public static Parse getPreviousToken(Parse parse, String[] ignore) {
		if(parse.getType().equals(AbstractBottomUpParser.TOP_NODE))
			return null;
		int siblingIndex = getSiblingIndex(parse);
		if(siblingIndex > 0) {
			Parse cursor = parse.getParent().getChildren()[siblingIndex - 1];
			do cursor = cursor.getChildren()[cursor.getChildCount() - 1]; while(!cursor.getType().equals(AbstractBottomUpParser.TOK_NODE));
			if(arrayContains(ignore, cursor.getParent().getType()))
				return getPreviousToken(cursor, ignore);
			return cursor;
		}
		return getPreviousToken(parse.getParent(), ignore);
	}
	
	/**
	 * Finds the the first token node which occurs after this node.
	 * Ignores any nodes whose type is included in the passed array.
	 * 
	 * @param parse the Parse to traverse
	 * @param ignore a String[] of types to ignore
	 * @return the Parse of the first token node which occurs after this node and whose type is not included in the passed array, null if no such node is found
	 */
	public static Parse getNextToken(Parse parse, String[] ignore) {
		if(parse.getType().equals(AbstractBottomUpParser.TOP_NODE))
			return null;
		int siblingIndex = getSiblingIndex(parse);
		if(siblingIndex + 1 < parse.getParent().getChildCount()) {
			Parse cursor = parse.getParent().getChildren()[siblingIndex + 1];
			do cursor = cursor.getChildren()[0]; while(!cursor.getType().equals(AbstractBottomUpParser.TOK_NODE));
			if(arrayContains(ignore, cursor.getParent().getType()))
				return getNextToken(cursor, ignore);
			return cursor;
		}
		return getNextToken(parse.getParent(), ignore);
	}
	
	/**
	 * Finds the next node in the Parse.
	 * 
	 * @param parse the Parse which determines the starting node
	 * @return the next node in the Parse, null if this is the last node
	 */
	public static Parse getNextNode(Parse parse) {
		if(parse.getChildCount() != 0)
			return parse.getChildren()[0];
		Parse temp = parse;
		while(!temp.getType().equals(AbstractBottomUpParser.TOP_NODE)) {
			int siblingIndex = getSiblingIndex(temp);
			temp = temp.getParent();
			if(siblingIndex + 1 < temp.getChildCount())
				return temp.getChildren()[siblingIndex + 1];
		}
		return null;
	}
	
	/**
	 * Finds the next node in the Parse that is a sibling of this node.
	 * Ignores any nodes whose type is included in the passed array.
	 * 
	 * @param parse the Parse to traverse
	 * @param ignore a String[] of types to ignore
	 * @return the Parse of the next sibling which is not ignored, null if no such node is found
	 */
	public static Parse getNextSibling(Parse parse, String[] ignore) {
		Parse[] siblings = parse.getParent().getChildren();
		int siblingIndex = getSiblingIndex(parse) + 1;
		while(siblingIndex < siblings.length && arrayContains(ignore, siblings[siblingIndex].getType()))
			siblingIndex++;
		return siblingIndex < siblings.length ? siblings[siblingIndex] : null;
	}
	
	/**
	 * Removes Errors from an ErrorList which occur inside of quotes.
	 * 
	 * @param errors the ErrorList to be examined which covers <em>only one</em> sentence
	 * @param parse the Parse of the same sentence
	 * @param introducedOnly if true, only errors inside of introduced quotes will be removed
	 */
	public static void removeErrorsInQuotes(ErrorList errors, Parse parse, boolean introducedOnly) {
		if(!errors.getText().equals(parse.getText()))
			LogHelper.getLogger(17).warn("ErrorList text and Parse text do not match - results will likely be inaccurate");
		String text = parse.getText();
		ArrayList<Span> quotes = new ArrayList<Span>();
		int cursor = 0;
		while(text.substring(cursor).contains("\"")) {
			int start = cursor + text.indexOf('\"');
			if(!(start + 1 < text.length() && text.substring(start + 1).contains("\""))) {
				LogHelper.getLogger(17).warn("Uneven number of quotation marks");
				break;
			}
			cursor = start + 2 + text.substring(start + 1).indexOf('\"');
			quotes.add(new Span(start, cursor));
		}
		if(introducedOnly)
			for(int i = 0; i < quotes.size(); i++) {
				String preceeding = text.substring(0, quotes.get(i).getStart()).trim();
				if(!(preceeding.endsWith(",") || preceeding.endsWith(":"))) {
					quotes.remove(i);
					i--;
				}
			}
		
		for(int i = 0; i < errors.size(); i++) {
			Error error = errors.get(i);
			Parse errorParse = getParseAtToken(parse, error.getStartIndex());
			if(error.getStartIndex() != error.getEndIndex()) {
				LogHelper.getLogger(17).debug("Finding common parent");
				errorParse = errorParse.getCommonParent(getParseAtToken(parse, error.getEndIndex()));
			}
			for(Span quote : quotes)
				if(quote.intersects(errorParse.getSpan())) {
					LogHelper.getLogger(17).debug("Removing Error in quotes: " + errorParse.getCoveredText() + " (" + errors.get(i).getStartIndex() + "-" + errors.get(i).getEndIndex() + ")");
					errors.remove(i);
					i--;
				}
		}
	}
	
}
