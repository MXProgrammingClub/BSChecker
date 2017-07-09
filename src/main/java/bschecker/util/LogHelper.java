package main.java.bschecker.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogHelper {
	
	private static Logger[] loggers = {
			LogManager.getLogger("Main")
	};
	
	public static Logger getLogger(int number) {
		return loggers[number];
	}
	
}
