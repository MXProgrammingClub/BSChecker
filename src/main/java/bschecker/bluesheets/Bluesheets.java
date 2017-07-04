package main.java.bschecker.bluesheets;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

/**
* This enum represents the bluesheet errors.
* @author Luke Giacalone
* @author JeremiahDeGreeff
*/
public enum Bluesheets {
	
	PAST_TENSE ("Past Tense",
			"Use the present tense in writing about a literary work.",
			"(Incorrect) Macbeth hastened home to tell his wife of the king's approach.\n(Correct) Macbeth hastens home to tell his wife of the king's approach.",
			new PastTense(), 1),
	COMPLETE_SENTENCE ("Fragment/Run-On/Comma-Splice",
			"Write complete, corrext sentences.",
			"(Incorrect) Macbeth murders King Duncan for many reasons. One being his desire for power.\n(Correct) Macbeth murders King Duncan for many reasons, one being his desire for power.\n\n(Incorrect) Huck Finn's father is an abusive parent he kidnaps his son, holds him prisoner, and nearly kills him in a drunken fit. \n(Correct) Huck Finn's father is an abusive parent who kidnaps his son, holds him prisoner, and nearly kills him in a drunken fit.\nOR\nHuck Finn's father is an abusive parent: he kidnaps his son, holds him prisoner, and nearly\nkills him in a drunken fit.\n\n(Incorrect) Homer seems fond of Eumaios, he addresses him familiarly as \"my swineherd.\"\n(Correct) Homer seems fond of Eumaios; he addresses him familiarly as \"my swineherd.\"",
			new IncompleteSentence(), 2),
	FIRST_SECOND_PERSON ("First/Second Person",
			"Do not use the first or second person (\"I,\" \"me,\" \"my\"; \"we,\" \"us,\" \"our\"; \"you,\" \"your) in critical writing",
			"(Incorrect) I think that Holden Caulfield, the hero of The Catcher in the Rye, is actually a hypocrite.\n(Correct) Holden Caulfield, the hero if the Catcher in the Rye, is actually a hypocrite. ",
			new FirstSecondPerson(), 3),
	VAGUE_THIS_WHICH ("Vague \"this\" or \"which\"",
			"Do not use \"this\" or \"which\" to refer to a clause.",
			"(Incorrect) In Dr. Seuss's Horton Hears a Who, Horton the elephant says that he hears a voice. This causes his friends to accuse him of being insane. \n(Correct) In Dr. Seuss's Horton Hears a Who, Horton the elephant says that he hears a voice. This claim causes his friends to accuse him of being insane.\n\n(Incorrect) Nick finds Daisy's voice thrilling, which helps him to sympathize with Gatsby's love for her. \n(Correct) Nick finds Daisy's voice thrilling, a feeling which helps him to sympathize with Gatsby's love for her.",
			new VagueThisWhich(), 4),
	SUBJECT_VERB_DISAGREEMENT ("Subject-Verb Disagreement",
			"A subject and verb must agree in number (sungular, plural). A pronoun must agree in number (singular, plural) with its antecedent.",
			"(Incorrect) The diverging roads in Frost's poem represents alternate choices or destinies. \n(Correct) The diverging roads in Frost's poem represent alternate choices or destinies.\n\n(Incorrect) A person should be able to defend their principles.\n(Correct) A person should be able to defend her principles.\nOR\nPeople should be able to defend their principles. ",
			new NumberDisagreement(), 5),
	PRONOUN_CASE ("Pronoun Case",
			"Put pronouns in the appropriate case (subjective, objective, possessive)",
			"(Incorrect) She is the last person who I would suspect. \n(Correct) She is the last person whom I would suspect.\n\n(Incorrect) Give the credit to she and me\n(Correct) Give the credit to her and me.",
			new PronounCase(), 6),
	AMBIGUOUS_PRONOUN ("Ambiguous Pronoun",
			"Avoid ambiguous pronouns.",
			"(Incorrect) Oedipus and the shepherd argue about whether he should know the truth. \n(Correct) Oedipus and the shepherd argue about whether Oedipus should know the truth.",
			new AmbiguousPronoun(), 7),
	APOSTROPHE_ERROR ("Apostrophe Error",
			"Use an apostrophe to indicate possession, not to indicate that a noun is plural. Distinguish properly between its and it's.",
			"(Incorrect) Longbourn is Elizabeth Bennets home. \n(Correct) Longbourn is Elizabeth Bennet's home.\n\n(Incorrect) The Bennet's are an eccentric family.(Correct) The Bennets are an eccentric family. ",
			new Apostrophe(), 8),
	PASSIVE_VOICE ("Passive Voice",
			"Write in the active voice; avoid the passive voice.",
			"(Incorrect) Gulliver is taught many lessons in rational behavior. \n(Correct) The Houyhnhnms teach Gulliver many lessons in rational behavior. ",
			new PassiveVoice(), 9),
	DANGLING_PARTICIPLE ("Dangling Participle",
			"Avoid dangling modifiers.",
			"(Incorrect) Seeing his bloodstained hands, Macbeth's reaction is horrified dismay. \n(Correct) Seeing his bloodstained hands, Macbeth reacts with horrified dismay. ",
			new DanglingModifier(), 10),
	FAULTY_PARALLELISM ("Faulty Parallelism",
			"Use identical grammatical forms to coordinate parallel ideas.",
			"(Incorrect) In a good essay the sentences are clear, concise, and hang together. \n(Correct) In a good essay, the sentences are clear, concise, and coherent.",
			new FaultyParallelism(), 11),
	PROGRESSIVE_TENSE ("Progressive Tense",
			"Avoid progressive tenses.",
			"(Incorrect) Sensing God's desire to destroy Sodom, Abraham is negotiating for a less apocalyptic punishment. \n(Correct) Sensing God's desire to destroy Sodom, Abraham negotiates for a less apocalyptic punishment. ",
			new ProgressiveTense(), 12),
	GERUNDS ("Incorrect Use of Gerund/Possessive",
			"Recognize gerunds and use possessives accordingly",
			"(Incorrect) Elizabeth is grateful for him loving her so well. \n(Correct) Elizabeth is grateful for his loving her so well. ",
			new GerundPossessive(), 13),
	QUOTATION ("Quotation Error",
			"Malformed Quotation and/or Citation",
			"Punctuation goes inside the quotations.\nCitations go outside the quotations.\nUse commas to introduce a quote preceeded by a verb of saying or thinking.",
			new QuotationForm(), 14);
	
	
	private static final String SETTINGS_FILE_PATH = "bin/resources/Settings.txt";
	private static final boolean[] DEFAULT_SETTINGS = {false, false, false, false, false, false, false, false, false, false, false, false, false, false};
	private static boolean[] settings = new boolean[14];
	
	private final String name;
	private final String description;
	private final String example;
	private final Bluesheet object;
	private final int number;
	
	
	Bluesheets(String name, String description, String example, Bluesheet object, int number) {
		this.name = name;
		this.description = description;
		this.example = example;
		this.object = object;
		this.number = number;
	}
	
	
	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
	
	public String getExample() {
		return example;
	}

	public Bluesheet getObject() {
		return object;
	}
	
	public int getNumber() {
		return number;
	}
	
	public static boolean[] getSettings() {
		return settings;
	}
	
	/**
	 * Based on the number passed in, returns whether or not the bluesheet with that number should be tested
	 * @param number The number of the bluesheet
	 * @return true if that bluesheet should be tested, false otherwise
	 */
	public static boolean isSetToAnalyze(int number) {
		return settings[number - 1];
	}

	/**
	 * Based on the number passed in, returns the corresponding element.
	 * @param num The number of the element.
	 * @return The element with the number num.
	 */
	public static Bluesheets getBluesheetFromNum(int num) {
		for(Bluesheets b: Bluesheets.values())
			if(b.getNumber() == num)
				return b;
		return null;
	}
	
	
	/**
	 * reads the settings from the settings.txt file and saves them to the settings array
	 */
	public static void readSettings() {
		System.out.println("Reading settings from " + SETTINGS_FILE_PATH);
		Scanner scan = null;
		try {
			scan = new Scanner(new File(SETTINGS_FILE_PATH));
			System.out.println("\tFile found");
			for(int i = 0; i < settings.length && scan.hasNextBoolean(); i++)
				settings[i] = scan.nextBoolean();
			System.out.println("\tSettings read: " + Arrays.toString(settings));
			scan.close();
		} catch (FileNotFoundException e) {
			System.out.println("\tFile not found");
			writeSettings(DEFAULT_SETTINGS);
			readSettings();
		}
	}
	
	/**
	 * reverses the setting for a given bluesheet
	 * @param number the number corresponding to the bluesheet whose setting will be reversed (1 - 14)
	 */
	public static void reverseSetting(int number) {
		if(number > 14 || number < 1)
			System.out.println("Invalid bluesheet: #" + number);
		else {
			System.out.println("Updating setting for bluesheet #" + number);
			settings[number - 1] = !settings[number - 1];
			writeSettings(settings);
		}
	}
	
	/**
	 * creates a settings.txt file and writes the passed settings into it
	 */
	public static void writeSettings(boolean[] writeSettings) {
		System.out.println("Writing settings to " + SETTINGS_FILE_PATH);
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(SETTINGS_FILE_PATH));
			for(boolean setting : writeSettings)
				writer.write(setting == true ? "true\n" : "false\n");
			System.out.println("\tSettings written: " + Arrays.toString(writeSettings));
			writer.close();
		} catch (IOException e) {e.printStackTrace();}
	}
}
