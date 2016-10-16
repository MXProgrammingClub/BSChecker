/**
 * This enum represents the bluesheet errors.
 * @author Luke Giacalone
 * @version 10/16/2016
 */

package BSChecker;

public enum Bluesheet {

	PAST_TENSE ("Past Tense", "Use the present tense in writing about a literary work.", "(Incorrect) Macbeth hastened home to tell his wife of the king's approach.\n(Correct) Macbeth hastens home to tell his wife of the king's approach.", 1),
	COMPLETE_SENTENCE ("Fragment/Run-On/Comma-Splice", "Write complete, corrext sentences.", "(Incorrect) Macbeth murders King Duncan for many reasons. One being his desire for power.\n(Correct) Macbeth murders King Duncan for many reasons, one being his desire for power.\n\n(Incorrect) Huck Finn's father is an abusive parent he kidnaps his son, holds him prisoner, and nearly kills him in a drunken fit. \n(Correct) Huck Finn's father is an abusive parent who kidnaps his son, holds him prisoner, and nearly kills him in a drunken fit.\nOR\nHuck Finn's father is an abusive parent: he kidnaps his son, holds him prisoner, and nearly\nkills him in a drunken fit.\n\n(Incorrect) Homer seems fond of Eumaios, he addresses him familiarly as \"my swineherd.\"\n(Correct) Homer seems fond of Eumaios; he addresses him familiarly as \"my swineherd.\"", 2),
	FIRST_SECOND_PERSON ("First/Second Person", "Do not use the first or second person (\"I,\" \"me,\" \"my\"; \"we,\" \"us,\" \"our\"; \"you,\" \"your) in critical writing", "(Incorrect) I think that Holden Caulfield, the hero of The Catcher in the Rye, is actually a hypocrite.\n(Correct) Holden Caulfield, the hero if the Catcher in the Rye, is actually a hypocrite. ", 3),
	VAGUE_THIS_WHICH ("Vague \"this\" or \"which\"", "Do not use \"this\" or \"which\" to refer to a clause.", "(Incorrect) In Dr. Seuss's Horton Hears a Who, Horton the elephant says that he hears a voice. This causes his friends to accuse him of being insane. \n(Correct) In Dr. Seuss's Horton Hears a Who, Horton the elephant says that he hears a voice. This claim causes his friends to accuse him of being insane.\n\n(Incorrect) Nick finds Daisy's voice thrilling, which helps him to sympathize with Gatsby's love for her. \n(Correct) Nick finds Daisy's voice thrilling, a feeling which helps him to sympathize with Gatsby's love for her.", 4),
	SUBJECT_VERB_AGREEMENT ("Subject-Verb Agreement", "A subject and verb must agree in number (sungular, plural). A pronoun must agree in number (singular, plural) with its antecedent.", "(Incorrect) The diverging roads in Frost's poem represents alternate choices or destinies. \n(Correct) The diverging roads in Frost's poem represent alternate choices or destinies.\n\n(Incorrect) A person should be able to defend their principles.\n(Correct) A person should be able to defend her principles.\nOR\nPeople should be able to defend their principles. ", 5),
	PRONOUN_CASE ("Pronoun Case", "Put pronouns in the appropriate case (subjective, objective, possessive)", "(Incorrect) She is the last person who I would suspect. \n(Correct) She is the last person whom I would suspect.\n\n(Incorrect) Give the credit to she and me\n(Correct) Give the credit to her and me.", 6),
	AMBIGUOUS_PRONOUN ("Ambiguous Pronoun", "Avoid ambiguous pronouns.", "(Incorrect) Oedipus and the shepherd argue about whether he should know the truth. \n(Correct) Oedipus and the shepherd argue about whether Oedipus should know the truth.", 7),
	APOSTROPHE_ERROR ("Apostrophe Error", "Use an apostrophe to indicate possession, not to indicate that a noun is plural. Distinguish properly between its and it's.", "(Incorrect) Longbourn is Elizabeth Bennets home. \n(Correct) Longbourn is Elizabeth Bennet's home.\n\n(Incorrect) The Bennet's are an eccentric family.(Correct) The Bennets are an eccentric family. ", 8),
	PASSIVE_VOICE ("Passive Voice", "Write in the active voice; avoid the passive voice.", "(Incorrect) Gulliver is taught many lessons in rational behavior. \n(Correct) The Houyhnhnms teach Gulliver many lessons in rational behavior. ", 9),
	DANGLING_PARTICIPLE ("Dangling Participle", "Avoid dangling modifiers.", "(Incorrect) Seeing his bloodstained hands, Macbeth's reaction is horrified dismay. \n(Correct) Seeing his bloodstained hands, Macbeth reacts with horrified dismay. ", 10),
	FAULTY_PARALLELISM ("Faulty Parallelism", "Use identical grammatical forms to coordinate parallel ideas.", "(Incorrect) In a good essay the sentences are clear, concise, and hang together. \n(Correct) In a good essay, the sentences are clear, concise, and coherent.", 11),
	PROGRESSIVE_TENSE ("Progressive Tense", "Avoid progressive tenses.", "(Incorrect) Sensing God's desire to destroy Sodom, Abraham is negotiating for a less apocalyptic punishment. \n(Correct) Sensing God's desire to destroy Sodom, Abraham negotiates for a less apocalyptic punishment. ", 12),
	GERUNDS ("Incorrect Use of Gerund/Possessive", "Recognize gerunds and use possessives accordingly", "(Incorrect) Elizabeth is grateful for him loving her so well. \n(Correct) Elizabeth is grateful for his loving her so well. ", 13),
	QUOTATION ("Quotation Error", "Malformed Quotation and/or Citation", "Punctuation goes inside the quotations.\nCitations go outside the quotations.\nUse commas to introduce a quote preceeded by a verb of saying or thinking.", 14);

	private final String name;
	private final String description;
	private final String example;
	private final int number;

	Bluesheet(String name, String description, String example, int number) {
		this.name = name;
		this.description = description;
		this.example = example;
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

	public int getNumber() {
		return number;
	}

	/**
	 * Based on the number passed in, returns the corresponding bluesheet.
	 * @param num The number of the bluesheet.
	 * @return The bluesheet with the number num.
	 */
	public static Bluesheet getBluesheetFromNum(int num) {
		for(Bluesheet b: Bluesheet.values()) {
			if(b.getNumber() == num) {
				return b;
			}
		}
		return null;
	}

}
