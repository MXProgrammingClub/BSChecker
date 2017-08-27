package bschecker.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bschecker.bluesheets.Bluesheet;
import bschecker.bluesheets.Bluesheets;

public class LogHelper {
	
	private static Logger[] loggers = new Logger[19];
	
	/**
	 * initializes all the loggers used by the project
	 */
	public static void init() {
		loggers[0] = LogManager.getLogger("Init");
		for(int i = 0; i < 14; i++)
			loggers[i + 1] = LogManager.getLogger(Bluesheets.values()[i].getName());
		loggers[15] = LogManager.getLogger("Application");
		loggers[16] = LogManager.getLogger("I/O");
		loggers[17] = LogManager.getLogger("Analyze");
		loggers[18] = LogManager.getLogger("Parse");
		loggers[0].info("Loggers initialized");
	}
	
	/**
	 * returns the logger at the passed index
	 * @param number the number of the desired logger:
	 * 0 for Init
	 * 1-14 for each bluesheet
	 * 15 for Application
	 * 16 for I/O
	 * 17 for Analyze
	 * 18 for Parse
	 * @return the desired logger
	 * @throws IllegalArgumentException if number is not [0, 18]
	 */
	public static Logger getLogger(int number) {
		if(number > 18 || number < 0)
			throw new IllegalArgumentException("The passed number: " + number + " is not in the valid range: [0, 18].");
		return loggers[number];
	}
	
	/**
	 * returns the logger associated with the passed Bluesheet object
	 * @param object the Bluesheet object whose logger should be returned
	 * @return the logger corresponding to this Bluesheet
	 */
	public static Logger getLogger(Bluesheet object) {
		return loggers[Bluesheets.getNumber(object)];
	}
	
	/**
	 * prints a blank line to the log
	 */
	public static void line() {
		System.out.println();
	}
	
}
