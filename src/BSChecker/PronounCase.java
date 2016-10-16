package BSChecker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;

import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

public class PronounCase extends Error{
	// pronoun case
	private static final String[] POSSESADJ = {"her", "his", "its", "their", "our", "my", "your", "whose"};
	private static final String[] POSSES = {"hers", "his", "its", "theirs", "ours", "mine", "yours", "whose"};
	private static final String[] OBJ = {"him", "her", "it", "them", "us", "me", "you", "whom"};
	private static final String[] SUB = {"he", "she", "it", "they", "we", "I", "you", "who"};
	private static final String[] ALLPN = {"he", "she", "it", "they", "we", "you", "his", "him", "her", "hers", "its", "their", "theirs", "them", "us", "our", "ours", "your", "yours", "who", "whose", "whom"};

	public static void main(String[] args) {
		String input = "he box is bigger than she box, but him loves its shirt";
		Error tester = new PronounCase();		
		ArrayList<int[]> found = tester.findErrors(input);
		//		for(int[] inds: found){
		//			System.out.println(inds[0] + " " + inds[1]);
		//		}
	}


	@Override
	public ArrayList<int[]> findErrors(String text) {
		// initialization: POSTagger and Tokenizer
		ArrayList<int[]> found = new ArrayList<int[]>();
		POSModel model = new POSModelLoader()	
				.load(new File("lib/en-pos-maxent.bin"));
		POSTaggerME tagger = new POSTaggerME(model);
		//System.out.println("HERE");
		InputStream is;
		TokenizerModel tModel;
		try {
			is = new FileInputStream("lib/en-token.bin");
			tModel = new TokenizerModel(is);
		} catch (FileNotFoundException e1) {
			return null;
		} catch (InvalidFormatException e) {
			return null;
		} catch (IOException e) {
			return null;
		}

		Tokenizer tokenizer = new TokenizerME(tModel);
		ObjectStream<String> lineStream = new PlainTextByLineStream(new StringReader(text));
		String line;

		// pronoun case
		try {
			while ((line = lineStream.read()) != null) 
			{

				String tokens[] = tokenizer.tokenize(line);
				String[] tags = tagger.tag(tokens);

				POSSample sample = new POSSample(tokens, tags);

				// ArrayList<Integer> index = new ArrayList<Integer>();
				ArrayList<Integer> pnIndex = new ArrayList<Integer>();
				ArrayList<Integer> errTokIndex = new ArrayList<Integer>();
				boolean hasPn = false;

				for(int i = 0; i < tokens.length; i++)
				{
					// check if pronoun exists
					//System.out.println(tokens[i]);

					for(String s: ALLPN)
					{
						if(tokens[i].equals(s))
						{
							hasPn = true;
							pnIndex.add(i);
							//							System.out.print("pnIndex: ");
							//							System.out.println(i);
						}
					}
				}

				for(int s: pnIndex)
				{
					System.out.print("pnIndex: ");
					System.out.println(s);
				}

				if(hasPn)
				{
					System.out.print("pnIndex size out of loop: ");
					System.out.println(pnIndex.size());
					//int lastTokErr = 0;
					for (int j = 0; j < pnIndex.size(); j++)
					{

						int index = pnIndex.get(j);
						if (index != (tokens.length - 1))
						{
							if(tags[index+1].equals("NN")||tags[index+1].equals("NNS")||tags[index+1].equals("NNP")||tags[index+1].equals("NNPS")
									|| (tokens[index-1].equals("of") && (tags[index-2].equals("NN")||tags[index-2].equals("NNS")||tags[index-2].equals("NNP")||tags[index-2].equals("NNPS"))))
							{
								System.out.println("Posse detected");
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

								//lastTokErr = errTokIndex.get((errTokIndex.size()-1));

								for (int s: errTokIndex)
								{
									System.out.print("poss: ");
									System.out.println(s);
								}
							}
							else if(tags[index+1].equals("VB")||tags[index+1].equals("VBD")||tags[index+1].equals("VBG")||tags[index+1].equals("VBN")||tags[index+1].equals("VBP")||tags[index+1].equals("VBZ"))
							{
								System.out.println("sub!");
								// so the pronoun should be subjective
								boolean subErr = true;
								for(String s: SUB)
								{
									if(tokens[index].equals(s))
									{
										subErr = false;
									}
								}
								System.out.println(subErr);

								if(subErr)
								{
									errTokIndex.add(index);
								}
								for (int s: errTokIndex)
								{
									System.out.print("poss + sub: ");
									System.out.println(s);
								}
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
								for (int s: errTokIndex)
								{
									System.out.print("poss + sub + obj: ");
									System.out.println(s);
								}
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

								//lastTokErr = errTokIndex.get((errTokIndex.size()-1));

								for (int s: errTokIndex)
								{
									System.out.print("poss: ");
									System.out.println(s);
								}
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
								for (int s: errTokIndex)
								{
									System.out.print("poss + sub + obj: ");
									System.out.println(s);
								}
							}
							
						}
						
					}
					
				}
				// here errTokIndex is correct and complete
				// convert errTokIndex to textIndex
				ArrayList<Integer> index = new ArrayList<Integer>();
				
				for(int j = 0; j < index.size(); j++)
				{
					int lastError = 0;
					boolean contains = text.contains(tokens[index.get(j)]);
					
					while(contains)
					{
						int[] err = {text.indexOf(tokens[index.get(j)]) + lastError,
							text.indexOf(tokens[index.get(j)]) + lastError + tokens[index.get(j)].length() - 1};
						found.add(err);

						// update last error index
						lastError = text.indexOf(tokens[index.get(j)]) + tokens[index.get(j)].length() - 1;
						
						// trims the text string
						text = text.substring(lastError);
						contains = text.contains(tokens[index.get(j)]);
					}
					
				}
			}




		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return null;



	}
}