package bschecker.bluesheets;

/**
* Enumerates all of the bluesheet errors.
* 
* @author Luke Giacalone
* @author JeremiahDeGreeff
*/
public enum Bluesheets {
	
	PAST_TENSE (1, new PastTense(), "Past Tense",
			"Use the present tense in writing about a literary work.",
			"(Incorrect) Macbeth hastened home to tell his wife of the king's approach.\n(Correct) Macbeth hastens home to tell his wife of the king's approach.",
			Availability.AVAILABLE),
	INCOMPLETE_SENTENCE (2, new IncompleteSentence(), "Fragment/Run-On/Comma-Splice",
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
			Availability.AVAILABLE),
	AMBIGUOUS_PRONOUN (7, new AmbiguousPronoun(), "Ambiguous Pronoun",
			"Avoid ambiguous pronouns.",
			"(Incorrect) Oedipus and the shepherd argue about whether he should know the truth. \n(Correct) Oedipus and the shepherd argue about whether Oedipus should know the truth.",
			Availability.UNAVAILABLE),
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
	QUOTATION_FORM (14, new QuotationForm(), "Quotation Error",
			"Malformed Quotation and/or Citation",
			"Punctuation goes inside the quotations.\nCitations go outside the quotations.\nUse commas to introduce a quote preceeded by a verb of saying or thinking.",
			Availability.AVAILABLE);
	
	private final int number;
	private final Bluesheet object;
	private final String name;
	private final String description;
	private final String example;
	private final String availibilityWarning;
	private final boolean isRunnable;
	
	Bluesheets(int number, Bluesheet object, String name, String description, String example, Availability availability) {
		this.number = number;
		this.object = object;
		this.name = name;
		this.description = description;
		this.example = example;
		availibilityWarning = availability.description == null ? null : availability.description.replace("BluesheetName", name);
		isRunnable = availability.isRunnable;
	}
	
	public int getNumber() {
		return number;
	}
	
	public Bluesheet getObject() {
		return object;
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

	public String getAvailabilityWarning() {
		return availibilityWarning;
	}
	
	public boolean isRunnable() {
		return isRunnable;
	}

	/**
	 * Returns the element corresponding to the passed number.
	 * 
	 * @param number The number of the element.
	 * @return The element with the number number.
	 * @throws IllegalArgumentException if number is not [1, 14].
	 */
	public static Bluesheets getBluesheetFromNumber(int number) {
		if(number < 1 || number > 14)
			throw new IllegalArgumentException("The passed number: " + number + " is not in the valid range: [1, 14].");
		return values()[number - 1];
	}
	
	/**
	 * Returns the number corresponding to the passed Bluesheet object.
	 * 
	 * @param object the object whose number will be returned
	 * @return the number of the object [1, 14]
	 * @throws IllegalArgumentException if object is does not belong to any of the values in this enum
	 */
	public static int getNumber(Bluesheet object) {
		for(Bluesheets b : values())
			if(b.object == object)
				return b.number;
		throw new IllegalArgumentException("The passed Bluesheet object was not found.");
	}
	
	/**
	 * Represents possible availability states for a bluesheet class.
	 * 
	 * @author JeremiahDeGreeff
	 */
	private static enum Availability {
		
		AVAILABLE(null, true),
		INACCURATE("Testing for BluesheetName currently has poor accuracy. Be cautious with trusting results at this time.", true),
		UNAVAILABLE("Testing for BluesheetName is currently unavailable. It cannot be tested at this time.", false);
		
		private final String description;
		private final boolean isRunnable;
		
		Availability(String description, boolean isRunnable) {
			this.description = description;
			this.isRunnable = isRunnable;
		}
		
	}
	
}
