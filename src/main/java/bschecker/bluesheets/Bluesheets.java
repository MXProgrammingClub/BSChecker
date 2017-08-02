package bschecker.bluesheets;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import bschecker.reference.Paths;
import bschecker.util.LogHelper;

/**
* This enum represents the bluesheet errors.
* It also stores the settings indicating which bluesheets should be checked for and provides static methods to change these settings.
* The settings are stored externally in the file indicated by {@code Paths.SETTINGS}.
* @author Luke Giacalone
* @author JeremiahDeGreeff
*/
public enum Bluesheets {
	
	PAST_TENSE (1, new PastTense(), "Past Tense",
			"Use the present tense in writing about a literary work.",
			"(Incorrect) Macbeth hastened home to tell his wife of the king's approach.\n(Correct) Macbeth hastens home to tell his wife of the king's approach.",
			Availability.AVAILABLE),
	COMPLETE_SENTENCE (2, new IncompleteSentence(), "Fragment/Run-On/Comma-Splice",
			"Write complete, corrext sentences.",
			"(Incorrect) Macbeth murders King Duncan for many reasons. One being his desire for power.\n(Correct) Macbeth murders King Duncan for many reasons, one being his desire for power.\n\n(Incorrect) Huck Finn's father is an abusive parent he kidnaps his son, holds him prisoner, and nearly kills him in a drunken fit. \n(Correct) Huck Finn's father is an abusive parent who kidnaps his son, holds him prisoner, and nearly kills him in a drunken fit.\nOR\nHuck Finn's father is an abusive parent: he kidnaps his son, holds him prisoner, and nearly\nkills him in a drunken fit.\n\n(Incorrect) Homer seems fond of Eumaios, he addresses him familiarly as \"my swineherd.\"\n(Correct) Homer seems fond of Eumaios; he addresses him familiarly as \"my swineherd.\"",
			Availability.AVAILABLE),
	FIRST_SECOND_PERSON (3, new FirstSecondPerson(), "First/Second Person",
			"Do not use the first or second person (\"I,\" \"me,\" \"my\"; \"we,\" \"us,\" \"our\"; \"you,\" \"your) in critical writing",
			"(Incorrect) I think that Holden Caulfield, the hero of The Catcher in the Rye, is actually a hypocrite.\n(Correct) Holden Caulfield, the hero if the Catcher in the Rye, is actually a hypocrite. ",
			Availability.AVAILABLE),
	VAGUE_THIS_WHICH (4, new VagueThisWhich(), "Vague \"this\" or \"which\"",
			"Do not use \"this\" or \"which\" to refer to a clause.",
			"(Incorrect) In Dr. Seuss's Horton Hears a Who, Horton the elephant says that he hears a voice. This causes his friends to accuse him of being insane. \n(Correct) In Dr. Seuss's Horton Hears a Who, Horton the elephant says that he hears a voice. This claim causes his friends to accuse him of being insane.\n\n(Incorrect) Nick finds Daisy's voice thrilling, which helps him to sympathize with Gatsby's love for her. \n(Correct) Nick finds Daisy's voice thrilling, a feeling which helps him to sympathize with Gatsby's love for her.",
			Availability.AVAILABLE),
	SUBJECT_VERB_DISAGREEMENT (5, new NumberDisagreement(), "Subject-Verb Disagreement",
			"A subject and verb must agree in number (sungular, plural). A pronoun must agree in number (singular, plural) with its antecedent.",
			"(Incorrect) The diverging roads in Frost's poem represents alternate choices or destinies. \n(Correct) The diverging roads in Frost's poem represent alternate choices or destinies.\n\n(Incorrect) A person should be able to defend their principles.\n(Correct) A person should be able to defend her principles.\nOR\nPeople should be able to defend their principles. ",
			Availability.UNAVAILABLE),
	PRONOUN_CASE (6, new PronounCase(), "Pronoun Case",
			"Put pronouns in the appropriate case (subjective, objective, possessive)",
			"(Incorrect) She is the last person who I would suspect. \n(Correct) She is the last person whom I would suspect.\n\n(Incorrect) Give the credit to she and me\n(Correct) Give the credit to her and me.",
			Availability.INACCURATE),
	AMBIGUOUS_PRONOUN (7, new AmbiguousPronoun(), "Ambiguous Pronoun",
			"Avoid ambiguous pronouns.",
			"(Incorrect) Oedipus and the shepherd argue about whether he should know the truth. \n(Correct) Oedipus and the shepherd argue about whether Oedipus should know the truth.",
			Availability.INACCURATE),
	APOSTROPHE_ERROR (8, new Apostrophe(), "Apostrophe Error",
			"Use an apostrophe to indicate possession, not to indicate that a noun is plural. Distinguish properly between its and it's.",
			"(Incorrect) Longbourn is Elizabeth Bennets home. \n(Correct) Longbourn is Elizabeth Bennet's home.\n\n(Incorrect) The Bennet's are an eccentric family.(Correct) The Bennets are an eccentric family. ",
			Availability.AVAILABLE),
	PASSIVE_VOICE (9, new PassiveVoice(), "Passive Voice",
			"Write in the active voice; avoid the passive voice.",
			"(Incorrect) Gulliver is taught many lessons in rational behavior. \n(Correct) The Houyhnhnms teach Gulliver many lessons in rational behavior. ",
			Availability.AVAILABLE),
	DANGLING_PARTICIPLE (10, new DanglingModifier(), "Dangling Participle",
			"Avoid dangling modifiers.",
			"(Incorrect) Seeing his bloodstained hands, Macbeth's reaction is horrified dismay. \n(Correct) Seeing his bloodstained hands, Macbeth reacts with horrified dismay. ",
			Availability.UNAVAILABLE),
	FAULTY_PARALLELISM (11, new FaultyParallelism(), "Faulty Parallelism",
			"Use identical grammatical forms to coordinate parallel ideas.",
			"(Incorrect) In a good essay the sentences are clear, concise, and hang together. \n(Correct) In a good essay, the sentences are clear, concise, and coherent.",
			Availability.AVAILABLE),
	PROGRESSIVE_TENSE (12, new ProgressiveTense(), "Progressive Tense",
			"Avoid progressive tenses.",
			"(Incorrect) Sensing God's desire to destroy Sodom, Abraham is negotiating for a less apocalyptic punishment. \n(Correct) Sensing God's desire to destroy Sodom, Abraham negotiates for a less apocalyptic punishment. ",
			Availability.AVAILABLE),
	GERUNDS (13, new GerundPossessive(), "Incorrect Use of Gerund/Possessive",
			"Recognize gerunds and use possessives accordingly",
			"(Incorrect) Elizabeth is grateful for him loving her so well. \n(Correct) Elizabeth is grateful for his loving her so well. ",
			Availability.AVAILABLE),
	QUOTATION (14, new QuotationForm(), "Quotation Error",
			"Malformed Quotation and/or Citation",
			"Punctuation goes inside the quotations.\nCitations go outside the quotations.\nUse commas to introduce a quote preceeded by a verb of saying or thinking.",
			Availability.AVAILABLE);
	
	
	private static boolean[] defaultSettings;
	private static boolean[] settings = new boolean[14];
	
	private final int NUMBER;
	private final Bluesheet OBJECT;
	private final String NAME;
	private final String DESCRIPTION;
	private final String EXAMPLE;
	private final String AVAILABILITY_WARNING;
	
	
	Bluesheets(int number, Bluesheet object, String name, String description, String example, Availability availability) {
		NUMBER = number;
		OBJECT = object;
		NAME = name;
		DESCRIPTION = description;
		EXAMPLE = example;
		AVAILABILITY_WARNING = availability.description == null ? null : availability.description.replace("BluesheetName", name);
	}
	
	
	public int getNumber() {
		return NUMBER;
	}
	
	public Bluesheet getObject() {
		return OBJECT;
	}
	
	public String getName() {
		return NAME;
	}

	public String getDescription() {
		return DESCRIPTION;
	}
	
	public String getExample() {
		return EXAMPLE;
	}

	public String getAvailabilityWarning() {
		return AVAILABILITY_WARNING;
	}
	
	public static boolean[] getSettings() {
		return settings;
	}
	
	public static boolean[] getDefaultSettings() {
		if(defaultSettings == null)
			generateDefaultSettings();
		return defaultSettings;
	}
	
	/**
	 * Based on the number passed in, returns whether or not the bluesheet with that number should be tested.
	 * @param number The number of the bluesheet
	 * @return true if that bluesheet should be tested, false otherwise
	 * @throws IllegalArgumentException if number is not [1, 14]
	 */
	public static boolean isSetToAnalyze(int number) {
		if(number > 14 || number < 1) {
			LogHelper.getLogger(0).error("Passed number is invalid - it must be in range [1, 14].");
			throw new IllegalArgumentException("The passed number: " + number + " is not in the valid range: [1, 14].");
		}
		return settings[number - 1];
	}

	/**
	 * Based on the number passed in, returns the corresponding element.
	 * @param number The number of the element.
	 * @return The element with the number number.
	 * @throws IllegalArgumentException if number is not [1, 14].
	 */
	public static Bluesheets getBluesheetFromNumber(int number) {
		for(Bluesheets b: Bluesheets.values())
			if(b.NUMBER == number)
				return b;
		throw new IllegalArgumentException("The passed number: " + number + " is not in the valid range: [1, 14].");
	}
	
	/**
	 * returns the number corresponding to the passed Bluesheet object
	 * @param object the object whose number will be returned
	 * @return the number of the object [1, 14]
	 * @throws IllegalArgumentException if object is does not belong to any of the values in this enum
	 */
	public static int getNumber(Bluesheet object) {
		for(Bluesheets b: Bluesheets.values())
			if(b.OBJECT == object)
				return b.NUMBER;
		throw new IllegalArgumentException("The passed Bluesheet object was not found.");
	}
	
	/**
	 * Generates the default settings based on the stated availability of each element in the Enum.
	 */
	private static void generateDefaultSettings() {
		LogHelper.getLogger(0).info("Generating default settings");
		defaultSettings = new boolean[14];
		for(int i = 0; i < settings.length; i++)
			defaultSettings[i] = Bluesheets.values()[i].AVAILABILITY_WARNING == null;
	}
	
	/**
	 * Reads the settings from the file indicated by {@code Paths.SETTINGS} and loads them into the settings array.
	 * If the file is longer than 14 lines, only the first 14 will be read.
	 * If the file is not found or is less than 14 lines, the default settings will be written and loaded.
	 */
	public static void readSettings() {
		LogHelper.getLogger(16).info("Reading settings from " + Paths.SETTINGS);
		boolean successful = true;
		try {
			Scanner scan = new Scanner(new File(Paths.SETTINGS));
			LogHelper.getLogger(16).info("File found");
			for(int i = 0; i < settings.length; i++)
				if(scan.hasNextBoolean())
					settings[i] = scan.nextBoolean();
				else {
					LogHelper.getLogger(16).warn("File contains less than 14 booleans. Default Settings will be used instead.");
					successful = false;
					break;
				}
			scan.close();
		} catch (FileNotFoundException e) {
			LogHelper.getLogger(16).warn("File not found. Default Settings will be used instead.");
			successful = false;
		}
		if(successful)
			LogHelper.getLogger(16).info("Settings read: " + Arrays.toString(settings));
		else {
			writeSettings(getDefaultSettings());
			readSettings();
		}
	}
	
	/**
	 * Creates a settings file and writes the passed boolean[] of settings into it.
	 * If the file already exists it will be overwritten.
	 * @param writeSettings a boolean[] which must be length 14 which will be written to the settings file
	 */
	private static void writeSettings(boolean[] writeSettings) {
		LogHelper.getLogger(16).info("Writing settings to " + Paths.SETTINGS);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(Paths.SETTINGS));
			for(boolean setting : writeSettings)
				writer.write(setting == true ? "true\n" : "false\n");
			LogHelper.getLogger(16).info("Settings written: " + Arrays.toString(writeSettings));
			writer.close();
		} catch (IOException e) {LogHelper.getLogger(16).error("Opening the file for writting failed.");}
	}
	
	/**
	 * Reverses the setting for a given bluesheet.
	 * @param number the number corresponding to the bluesheet whose setting will be reversed [1 - 14]
	 * @throws IllegalArgumentException if number is not [1, 14]
	 */
	public static void reverseSetting(int number) {
		if(number > 14 || number < 1)
			throw new IllegalArgumentException("The passed number: " + number + " is not in the valid range: [1, 14].");
		LogHelper.getLogger(15).info("Updating setting for bluesheet #" + number);
		settings[number - 1] = !settings[number - 1];
		writeSettings(settings);
	}
	
	/**
	 * loads settings without altering the settings.txt file
	 * @param loadSettings a boolean[] which holds the settings to be loaded
	 */
	public static void loadSettings(boolean[] loadSettings) {
		LogHelper.getLogger(0).info("Loading settings: " + Arrays.toString(loadSettings));
		settings = loadSettings;
	}
	
	
	/**
	 * A simple Enum which represents possible availability states for a bluesheet class.
	 * @author JeremiahDeGreeff
	 */
	private static enum Availability {
		
		AVAILABLE(null),
		INACCURATE("Testing for BluesheetName currently has poor accuracy. Be cautious with trusting results at this time."),
		UNAVAILABLE("Testing for BluesheetName is currently unavailable. It will not be tested when the text is analyzed.");
		
		private final String description;
		
		Availability(String description) {
			this.description = description;
		}
	}
	
}
