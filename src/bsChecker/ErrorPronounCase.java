package bsChecker;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

/**
 * 
 * @author Leo
 * Finds errors in pronoun case. (6)
 */
public class ErrorPronounCase extends Error{
	private static final int ERROR_NUMBER = 6;
	
	// pronoun cases
	private static final String[] POSSESADJ = {"her", "his", "its", "their", "our", "my", "your", "whose"};
	private static final String[] POSSES = {"hers", "his", "its", "theirs", "ours", "mine", "yours", "whose"};
	private static final String[] OBJ = {"him", "her", "it", "them", "us", "me", "you", "whom"};
	private static final String[] SUBJ = {"he", "she", "it", "they", "we", "I", "you", "who"};
	private static final String[] ALLPN = {"he", "she", "it", "they", "we", "you", "his", "him", "her", "hers", "its", "their", "theirs", "them", "us", "our", "ours", "your", "yours", "who", "whose", "whom"};

	/**
	 * for testing purposes
	 */
	public static void main(String[] args) {
		Error.setupOpenNLP();
		String input = "However, he died and instead of adapting political systems from he apple, he died.";
		System.out.println("\ninput: " + input + "\n");
		ArrayList<int[]> errors = new ErrorPronounCase().findErrors(input);
		sort(errors);
		printErrors(tokensToChars(input, errors, 0), input);
	}


	@Override
	public ArrayList<int[]> findErrors(String line) {
		String[] tokens = tokenizer.tokenize(line);
		String[] tags = posTagger.tag(tokens);
		ArrayList<Integer> pronounIndices = new ArrayList<Integer>();
		for(int i = 0; i < tokens.length; i++)
		{
			String word = tokens[i];
			if(arrayContains(ALLPN, word))
				pronounIndices.add(i);
		}
		ArrayList<int[]> errors = new ArrayList<int[]>();
		possesPronoun(pronounIndices, tokens, tags, errors);
		//subjPronoun(pronounIndices, tokens, tags, errors);
		//objPronoun(pronounIndices, tokens, tags, errors);	
		
		return errors;
	}
	
	
	private void possesPronoun(ArrayList<Integer> pronounIndices, String[] tokenList, String[] tagList, ArrayList<int[]> errorIndices)
	{
		System.out.println("Running");
		System.out.println(pronounIndices);
		for(int element = 0; element < pronounIndices.size(); element++)
		{
			int index = pronounIndices.get(element);
			System.out.println("This is index");
			System.out.println(index);
			int nextWordIndex = index + 1;
			while(tagList[nextWordIndex].equals("JJ") || tagList[nextWordIndex].equals("JJR") || tagList[nextWordIndex].equals("JJS") || tagList[nextWordIndex].equals("RB") || tagList[nextWordIndex].equals("RBR")|| tagList[nextWordIndex].equals("RBS"))
			{								
				nextWordIndex++;
			}
			
			if(tagList[nextWordIndex].equals("NN")
			|| tagList[nextWordIndex].equals("NNS")
			|| tagList[nextWordIndex].equals("NNP")
			|| tagList[nextWordIndex].equals("NNPS")
			|| ((index >= 2) && (tagList[index-1].equals("of")) && (tagList[index-2].equals("NN") || tagList[index-2].equals("NNS") || tagList[index-2].equals("NNP") || tagList[index-2].equals("NNPS"))))
			// e.g. friend (noun) of his (possessive pronoun)
			{
	  			System.out.println("Possessive pronoun detected");
				// so the pronoun should be possessive
				if(!tokenList[index].equals("whom"))
				{
					boolean possErr = true;
					for(String pronoun: POSSES)
					{
						if(tokenList[index].equals(pronoun))
						{
							possErr = false;
						}
					}
					for(String pronoun: POSSESADJ)
					{
						if(tokenList[index].equals(pronoun))
						{
							possErr = false;
						}
					}
					if(possErr)
					{
						errorIndices.add(new int[] {index, index, ERROR_NUMBER});
					}
				}

				
			}
			for (int[] s: errorIndices)
			{
				System.out.print("pos: ");
				System.out.println(s[0]);
			}
		}
	}
	
	private void subjPronoun(ArrayList<Integer> pronounIndices, String[] tokenList, String[] tagList, ArrayList<int[]> errorIndices)
	{
		System.out.println("Running");
		System.out.println(pronounIndices);
		for(int element = 0; element < pronounIndices.size(); element++)
		{
			int index = pronounIndices.get(element);
			System.out.println("This is index ");
			System.out.println(index);
			if (index - 1 >= 0)
			{
				int previousWordIndex = index - 1;
				
				if(tagList[previousWordIndex].equals("VB")
				|| tagList[previousWordIndex].equals("VBD")
				|| tagList[previousWordIndex].equals("VBG")
				|| tagList[previousWordIndex].equals("VBN")
				|| tagList[previousWordIndex].equals("VBP")
				|| tagList[previousWordIndex].equals("VBZ"))
				// checking for a verb before the pronoun
				{
		  			System.out.println("Subjective pronoun detected");
					// so the pronoun should be subjective
					
						boolean subjErr = true;
						for(String pronoun: SUBJ)
						{
							if(tokenList[index].equals(pronoun))
							{
								subjErr = false;
							}
						}
						if(subjErr)
						{
							errorIndices.add(new int[] {index, index, ERROR_NUMBER});
						}
					}
			}
			for (int[] s: errorIndices)
			{
				System.out.print("subj: ");
				System.out.println(s[0]);
			}
		}
	}
	
	private void objPronoun(ArrayList<Integer> pronounIndices, String[] tokenList, String[] tagList, ArrayList<int[]> errorIndices)
	{
		System.out.println("Running");
		System.out.println(pronounIndices);
		for(int element = 0; element < pronounIndices.size(); element++)
		{
			int index = pronounIndices.get(element);
			System.out.println("This is index ");
			System.out.println(index);
			if (index + 1 < tokenList.length)
			{
				int nextWordIndex = index - 1;
				
				if(tagList[nextWordIndex].equals("VB")
				|| tagList[nextWordIndex].equals("VBD")
				|| tagList[nextWordIndex].equals("VBG")
				|| tagList[nextWordIndex].equals("VBN")
				|| tagList[nextWordIndex].equals("VBP")
				|| tagList[nextWordIndex].equals("VBZ"))
				// checking for a verb after the pronoun
				{
		  			System.out.println("Objective pronoun detected");
					// so the pronoun should be objective
					
						boolean objErr = true;
						for(String pronoun: SUBJ)
						{
							if(tokenList[index].equals(pronoun))
							{
								objErr = false;
							}
						}
						if(objErr)
						{
							errorIndices.add(new int[] {index, index, ERROR_NUMBER});
						}
					}
			}
			for (int[] s: errorIndices)
			{
				System.out.print("obj: ");
				System.out.println(s[0]);
			}
		}
	}
	
	
	
	
	public ArrayList<int[]> findErrorsOld(String text) {
		ArrayList<int[]> found = new ArrayList<int[]>();
		
		ObjectStream<String> lineStream = new PlainTextByLineStream(new StringReader(text.toLowerCase()));
		String line;

		// pronoun case
		try {
			while ((line = lineStream.read()) != null) 
			{
				String[] tokens = tokenizer.tokenize(line);
				String[] tags = posTagger.tag(tokens);

				// ArrayList<Integer> index = new ArrayList<Integer>();
				ArrayList<Integer> pnIndex = new ArrayList<Integer>();
				ArrayList<Integer> errTokIndex = new ArrayList<Integer>();
				boolean hasPn = false;

				for(int i = 0; i < tokens.length; i++)
				{
					// check if pronoun exists
//					System.out.println(tokens[i]);

					for(String s: ALLPN)
					{
						if(tokens[i].equals(s))
						{
							hasPn = true;
							pnIndex.add(i);
						}
					}
				}

//				for(int s: pnIndex)
//				{
//					System.out.print("pnIndex: ");
//					System.out.println(s);
//				}

				if(hasPn)
				{
					for (int j = 0; j < pnIndex.size(); j++)
					{

						int index = pnIndex.get(j);
						if (index != (tokens.length - 1))
						{
							int relIndex = index;
							while(tags[relIndex + 1].equals("JJ") || tags[relIndex + 1].equals("JJR") || tags[relIndex + 1].equals("JJS") || tags[relIndex + 1].equals("RB") || tags[relIndex + 1].equals("RBR")|| tags[relIndex + 1].equals("RBS")){								
								relIndex++;
							}

							if(tags[relIndex+1].equals("NN")||tags[relIndex+1].equals("NNS")||tags[relIndex+1].equals("NNP")||tags[relIndex+1].equals("NNPS")
									|| ((index >= 2) && (tokens[index-1].equals("of")) && (tags[index-2].equals("NN") || tags[index-2].equals("NNS") || tags[index-2].equals("NNP") || tags[index-2].equals("NNPS"))))
							{
//								System.out.println("pos detected");
								// so the pronoun should be possessive
								if(!tokens[index].equals("whom"))
								{
									boolean possErr = true;
									for(String s: POSSES)
									{
										if(tokens[index].equals(s))
										{
											possErr = false;
										}
									}
									for(String s: POSSESADJ)
									{
										if(tokens[index].equals(s))
										{
											possErr = false;
										}
									}

									if(possErr)
									{
										errTokIndex.add(index);
									}
								}

//								for (int s: errTokIndex)
//								{
//									System.out.print("pos: ");
//									System.out.println(s);
//								}
							}
							else if(tags[relIndex+1].equals("VB")||tags[relIndex+1].equals("VBD")||tags[relIndex+1].equals("VBG")||tags[relIndex+1].equals("VBN")||tags[relIndex+1].equals("VBP")||tags[relIndex+1].equals("VBZ"))
							{
//								System.out.println("sub detected");
								// so the pronoun should be subjective
								if(tokens[index].equals("whose") || tokens[index].equals("whom"))
								{
									errTokIndex.add(index);
								}
								else
								{
									boolean subErr = true;
									for(String s: SUB)
									{
										if(tokens[index].equals(s))
										{
											subErr = false;
										}
									}

									if(subErr)
									{
										errTokIndex.add(index);
									}
								}
//								for (int s: errTokIndex)
//								{
//									System.out.print("poss & sub: ");
//									System.out.println(s);
//								}
							}
							else if(index > 0 && (tags[index-1].equals("VB") || tags[index-1].equals("VBD") || tags[index-1].equals("VBG") || tags[index-1].equals("VBN") || tags[index-1].equals("VBP") || tags[index-1].equals("VBZ")))
							{
//								System.out.println("obj detected");
								// so the pronoun should be objective
								boolean objErr = true;
								for(String s: OBJ)
								{
									if(tokens[index].equals(s))
									{
										objErr = false;
									}
								}
								if(objErr)
								{
									errTokIndex.add(index);
								}
//								for (int s: errTokIndex)
//								{
//									System.out.print("poss & sub & obj: ");
//									System.out.println(s);
//								}
							}
						}
					else
					{
						// special: when pronoun is the last word in the paragraph
						if(tokens[index-1].equals("of") && (tags[index-2].equals("NN")||tags[index-2].equals("NNS")||tags[index-2].equals("NNP")||tags[index-2].equals("NNPS")))
						{
							// so the pronoun should be possessive
							boolean possErr = true;
							for(String s: POSSES)
							{
								if(tokens[index].equals(s))
								{
									possErr = false;
								}
							}
							for(String s: POSSESADJ)
							{
								if(tokens[index].equals(s))
								{
									possErr = false;
								}
							}

							if(possErr)
							{
								errTokIndex.add(index);
							}

//							for (int s: errTokIndex)
//							{
//								System.out.print("poss: ");
//								System.out.println(s);
//							}
						}
						else if(tags[index-1].equals("VB")||tags[index-1].equals("VBD")||tags[index-1].equals("VBG")||tags[index-1].equals("VBN")||tags[index-1].equals("VBP")||tags[index-1].equals("VBZ"))
						{
							// so the pronoun should be objective
							boolean objErr = true;
							for(String s: OBJ)
							{
								if(tokens[index].equals(s))
								{
									objErr = false;
								}
							}
							if(objErr)
							{
								errTokIndex.add(index);
							}
//							for (int s: errTokIndex)
//							{
//								System.out.print("poss + sub + obj: ");
//								System.out.println(s);
//							}
						}

					}
				}
				String[] nextWord = new String[errTokIndex.size()];
				for(int i = 0; i < errTokIndex.size(); i++) {
					nextWord[i] = tokens[errTokIndex.get(i) + 1];
//					System.out.println(tokens[errTokIndex.get(i)]);
//					System.out.println(nextWord[i]);
				}
				// here errTokIndex is correct and complete
				// convert errTokIndex to textIndex
				int leftValue = 0, startCharIndex, endCharIndex;
				for(int j = 0; j < errTokIndex.size(); j++)
				{
//					System.out.println(j);
					startCharIndex = text.toLowerCase().indexOf(tokens[errTokIndex.get(j)], leftValue);
					endCharIndex = startCharIndex + tokens[errTokIndex.get(j)].length() - 1;

//					System.out.println(startCharIndex);
//					System.out.println(endCharIndex);
//					System.out.println(text.substring(endCharIndex + 2, endCharIndex + nextWord[j].length() + 2));
//					System.out.println(nextWord[j]);
//					He, who is big, is dead, killed he, who is small.

					if((startCharIndex == 0) || (startCharIndex >= 0 && text.toLowerCase().charAt(startCharIndex - 1) == ' ')
							&& (text.charAt(endCharIndex + 1) != 's' && text.charAt(endCharIndex + 1) != 'm')
							&& text.substring(endCharIndex + 2, endCharIndex + nextWord[j].length() + 2).equals(nextWord[j]))
					{
//						System.out.println(errTokIndex.get(j));
						int[] err = {startCharIndex, endCharIndex, ERROR_NUMBER};
						found.add(err);

						// updates starting index
						leftValue = endCharIndex;
					}
					else
					{
						leftValue = endCharIndex;
						j--;
					}

				}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// print final result
		//		for(int i = 0; i < found.size(); i++)
		//		{
		//			System.out.print("Start: ");
		//			System.out.println(found.get(i)[0]);
		//			System.out.print("End: ");
		//			System.out.println(found.get(i)[1]);
		//
		//			System.out.print("Substring: ");
		//			System.out.println(text.toLowerCase().substring(found.get(i)[0], (found.get(i)[1] + 1)));
		//		}

		return found;
	}
}