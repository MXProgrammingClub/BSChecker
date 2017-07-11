package bschecker.reference;

/**
 * A simple class to store the paths of any resources needed by the project in one place
 * @author JeremiahDeGreeff
 */
public class Paths {
	
	public static final String SAYING_VERBS = "bin/SayingVerbs.txt";
	public static final String SETTINGS = "Settings.txt";
	
	//opennlp tools models
	public static final String SENTENCE_DETECTOR = "lib/en-sent.bin";
	public static final String NAME_FINDER = "lib/en-ner-person.bin";
	public static final String TOKENIZER = "lib/en-token.bin";
	public static final String POS_TAGGER = "lib/en-pos-maxent.bin";
	public static final String PARSER = "lib/en-parser-chunking.bin";
}
