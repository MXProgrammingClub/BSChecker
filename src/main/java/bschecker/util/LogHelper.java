package main.java.bschecker.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogHelper {
	
	private static Logger[] loggers = {
			LogManager.getLogger("Init"),
			LogManager.getLogger("Past Tense"),
			LogManager.getLogger("Fragment/Run-On/Comma-Splice"),
			LogManager.getLogger("First/Second Person"),
			LogManager.getLogger("Vague \"this\" or \"which\""),
			LogManager.getLogger("Subject-Verb Disagreement"),
			LogManager.getLogger("Pronoun Case"),
			LogManager.getLogger("Ambiguous Pronoun"),
			LogManager.getLogger("Apostrophe Error"),
			LogManager.getLogger("Passive Voice"),
			LogManager.getLogger("Dangling Participle"),
			LogManager.getLogger("Faulty Parallelism"),
			LogManager.getLogger("Progressive Tense"),
			LogManager.getLogger("Incorrect Use of Gerund/Possessive"),
			LogManager.getLogger("Quotation Error"),
			LogManager.getLogger("Analyze"),
			LogManager.getLogger("Parse")
	};
	
	public static Logger getLogger(int number) {
		return loggers[number];
	}
	
}
