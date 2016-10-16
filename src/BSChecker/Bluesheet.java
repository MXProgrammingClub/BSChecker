/**
 * This enum represents the bluesheet errors.
 * @author Luke Giacalone
 * @version 10/16/2016
 */

package BSChecker;

public enum Bluesheet {

	FIRST_SECOND_PERSON ("First/Second Person", "Do not use the first or second person (\"I,\" \"me,\" \"my\"; \"we,\" \"us,\" \"our\"; \"you,\" \"your) in critical writing", "(Incorrect) I think that Holden Caulfield, the hero of The Catcher in the Rye, is actually a hypocrite.\n(Correct) Holden Caulfield, the hero if the Catcher in the Rye, is actually a hypocrite. ", 3),
	PRONOUN_CASE ("Pronoun Case", "Put pronouns in the appropriate case (subjective, objective, possessive)", "(Incorrect) She is the last person who I would suspect. \n(Correct) She is the last person whom I would suspect.\n\n(Incorrect) Give the credit to she and me\n(Correct) Give the credit to her and me.", 6),
	AMBIGUOUS_PRONOUN ("Ambiguous Pronoun", "Avoid ambiguous pronouns.", "(Incorrect) Oedipus and the shepherd argue about whether he should know the truth. \n(Correct) Oedipus and the shepherd argue about whether Oedipus should know the truth.", 7),
	PASSIVE_VOICE ("Passive Voice", "Write in the active voice; avoid the passive voice.", "(Incorrect) Gulliver is taught many lessons in rational behavior. \n(Correct) The Houyhnhnms teach Gulliver many lessons in rational behavior. ", 9),
	FAULTY_PARALLELISM ("Faulty Parallelism", "Use identical grammatical forms to coordinate parallel ideas.", "(Incorrect) In a good essay the sentences are clear, concise, and hang together. \n(Correct) In a good essay, the sentences are clear, concise, and coherent.", 11),
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
