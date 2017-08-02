package bschecker.bluesheets;

import java.util.ArrayList;

import bschecker.util.Error;
import bschecker.util.ErrorList;
import bschecker.util.LogHelper;
import bschecker.util.Tools;
import bschecker.util.UtilityMethods;
import opennlp.tools.parser.Parse;

/**
 * Finds sentence structure errors. (2)
 * @author JeremiahDeGreeff
 */
public class IncompleteSentence extends Bluesheet {
	
	/**
	 * Finds any invalid sentence structure in a paragraph.
	 * @param line the paragraph in which to find errors
	 * @param parses a String array of the parses of each sentence of the line
	 * @return an ErrorList which for each Error references start token, end token, and, optionally, a note
	 */
	@Override
	protected ErrorList findErrors(String line, Parse[] parses) {
		ErrorList errors = new ErrorList(line);
		int tokenOffset = 0;
		
		//temporary
		String[] parseStrings = new String[parses.length];
		for(int i = 0; i < parses.length; i++){
			StringBuffer sb = new StringBuffer(parses[i].getText().length() * 4);
			parses[i].show(sb);
			parseStrings[i] = sb.toString();
		}
			
		for(int i = 0; i < parses.length; i++){
			int length = Tools.getTokenizer().tokenize(parses[i].getText()).length;
			errors.addAll(findErrorsInSentence(line, parseStrings[i], tokenOffset, length));
			tokenOffset += length;
		}
		return errors;
	}
	
	/**
	 * finds any issues in the structure of a sentence
	 * @param line the full line which the sentence is a part of (for returning purposes)
	 * @param parse the string representation of the parse of the sentence
	 * @param tokenOffset the number of tokens which have occurred in earlier sentences (for returning purposes)
	 * @param length the number of tokens in this sentence (for returning purposes)
	 * @return an ErrorList which for each error in this sentence references start and end tokens, the bluesheet number (11), and, optionally, a note
	 */
	private ErrorList findErrorsInSentence(String line, String parse, int tokenOffset, int length) {
		ErrorList errors = new ErrorList(line);
		ArrayList<String> tags = UtilityMethods.listParseTags(parse);
		LogHelper.getLogger(this).debug(parse);
		LogHelper.getLogger(this).debug(tags);
		
		if(tags.get(1).equals("SBAR")) //either lone dependent clause (Fragment) or run-on in form DC IC
			errors.add(new Error(tokenOffset, tokenOffset + length - 1));
		int sIndex = 0;
		int wIndex = 0;
		for(int i = 0; i < tags.size(); i++){
			if(UtilityMethods.arrayContains(Tools.WORD_LEVEL_TAGS, tags.get(i)))
				wIndex++;
			if(tags.get(i).equals(":") && !tags.get(i + 1).equals("S")) //fragment in form DC; IC or IC; DC
				errors.add(new Error(tokenOffset + wIndex - 1, "Fragment"));
			else if(tags.get(i).equals("CC") && tags.get(i + 1).equals("S") && !tags.get(i - 1).equals(",")) //run-on in form IC CC IC
				errors.add(new Error(tokenOffset + wIndex - 1, "Run-on"));
			else if(tags.get(i).equals("S")){ //potential for comma-splice in form IC, IC
				sIndex += parse.substring(sIndex + 1).indexOf("(S ") + 2;
				//catch for the case where this clause is in fact dependent despite the parser thinking otherwise
				int leftParen = sIndex + 2;
				boolean isIndependant = true;
				while(parse.charAt(leftParen) == '('){
					if(parse.substring(leftParen + 1, leftParen + 2).equals(",") || parse.substring(leftParen + 1, leftParen + 3).equals("RB") || parse.substring(leftParen + 1, leftParen + 3).equals("PP") || parse.substring(leftParen + 1, leftParen + 5).equals("ADVP"))
						leftParen = parse.indexOf('(', leftParen + 1);
					else{
						if(parse.substring(leftParen + 1, leftParen + 3).equals("IN"))
							isIndependant = false;
						break;
					}
				}
				if(isIndependant){
					//find the end of this S
					int net = -1, j = sIndex, tagsPassed = 0;
					boolean inTag = false;
					while(net != 0 && j < parse.length()){
						if(parse.charAt(j) == ')'){
							net++;
							inTag = false;
						}
						else if(parse.charAt(j) == '(')
							net--;
						else if(!inTag && parse.charAt(j) != ' '){
							inTag = true;
							tagsPassed++;
						}
						j++;
					}
					//if S followed by NP most likely a comma splice
					if(j + 6 < parse.length() && parse.charAt(j + 1) == ',' && parse.substring(j + 7, j + 9).equals("NP")){
						int k = j + 9;
						while(parse.charAt(k + 1) == '(')
							k = parse.indexOf(' ', k + 1);
						//not a comma splice if comma is introducing a quote
						if(parse.charAt(k + 1) != '"')
							errors.add(new Error(tokenOffset + wIndex + tagsPassed, "Comma-Splice"));
					}
				}
			}
		}
		return errors;
	}
	
}
