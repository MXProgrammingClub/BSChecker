import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.tokenize.*;
import opennlp.tools.util.*;
public class BSChecker {
	public static void main(String[] args) throws InvalidFormatException, IOException {
			InputStream is = new FileInputStream("en-token.bin");
		 
			TokenizerModel model = new TokenizerModel(is);
		 
			Tokenizer tokenizer = new TokenizerME(model);
		 
			String tokens[] = tokenizer.tokenize("Hi. How are you? This is Mike.");
		 
			for (String a : tokens)
				System.out.println(a);
		 
			is.close();
		}
	}
