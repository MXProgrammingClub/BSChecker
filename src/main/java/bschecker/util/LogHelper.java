package bschecker.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bschecker.bluesheets.Bluesheets;

/**
 * Manages all loggers used by the project.
 * 
 * @author JeremiahDeGreeff
 */
public class LogHelper {
	
	private static Logger[] loggers = new Logger[19];
	
	public static final int INIT = 0, APPLICATION = 15, IO = 16, ANALYZE = 17, PARSE = 18;
	
	/**
	 * Initializes all the loggers used by the project.
	 */
	public static void init() {
		loggers[INIT] = LogManager.getLogger("Init");
		for(int i = 0; i < Bluesheets.values().length; i++)
			loggers[i + 1] = LogManager.getLogger(Bluesheets.values()[i].getName());
		loggers[APPLICATION] = LogManager.getLogger("Application");
		loggers[IO] = LogManager.getLogger("I/O");
		loggers[ANALYZE] = LogManager.getLogger("Analyze");
		loggers[PARSE] = LogManager.getLogger("Parse");
		
		loggers[0].info("Loggers initialized");
	}
	
	/**
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
	 * Prints a blank line to the console.
	 */
	public static void line() {
		System.out.println();
	}
	
}
