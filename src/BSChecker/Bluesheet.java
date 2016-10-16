/**
 * This enum represents the bluesheet errors.
 * @author Luke Giacalone
 * @version 10/15/2016
 */

package BSChecker;

public enum Bluesheet {

	PAST_TENSE ("Past Tense", "Use the present tense in writing about a literary work.", 1),
	COMPLETE_SENTENCE ("Fragment/Run-On/Comma-Splice", "Write complete, corrext sentences.", 2),
	FIRST_SECOND_PERSON ("First/Second Person", "Do not use the first or second person (\"I,\" \"me,\" \"my\"; \"we,\" \"us,\" \"our\"; \"you,\" \"your) in critical writing", 3),
	VAGUE_THIS_WHICH ("Vague \"this\" or \"which\"", "Do not use \"this\" or \"which\" to refer to a clause.", 4),
	SUBJECT_VERB_AGREEMENT ("Subject-Verb Agreement", "A subject and verb must agree in number (sungular, plural). A pronoun must agree in number (singular, plural) with its antecedent.", 5),
	PRONOUN_CASE ("Pronoun Case", "Put pronouns in the appropriate case (subjective, objective, possessive)", 6),
	AMBIGUOUS_PRONOUN ("Ambiguous Pronoun", "Avoid ambiguous pronouns.", 7),
	APOSTROPHE_ERROR ("Apostrophe Error", "Use an apostrophe to indicate possession, not to indicate that a noun is plural. Distinguish properly between its and it's.", 8),
	PASSIVE_VOICE ("Passive Voice", "Write in the active voice; avoid the passive voice.", 9),
	DANGLING_PARTICIPLE ("Dangling Participle", "Avoid dangling modifiers.", 10),
	FAULTY_PARALLELISM ("Faulty Parallelism", "Use identical grammatical forms to coordinate parallel ideas.", 11),
	PROGRESSIVE_TENSE ("Progressive Tense", "Avoid progressive tenses.", 12),
	GERUNDS ("Incorrect Use of Gerund/Possessive", "Recognize gerunds and use possessives accordingly", 13),
	QUOTATION ("Quotation Error", "Malformed Quotation and/or Citation", 14);

	private final String name;
	private final String description;
	private final int number;

	Bluesheet(String name, String description, int number) {
		this.name = name;
		this.description = description;
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
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
