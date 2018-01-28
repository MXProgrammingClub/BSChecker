package bschecker.reference;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import bschecker.bluesheets.Bluesheets;
import bschecker.util.LogHelper;

/**
 * Stores the settings indicating which bluesheets should be checked for and provides static methods to change these settings.
 * The settings are stored externally in the settings file indicated by {@link bschecker.reference.Paths}.
 * 
 * @author JeremiahDeGreeff
 */
public class Settings {
	
	private static boolean[] defaultSettings;
	private static boolean[] settings = new boolean[14];
	
	/**
	 * @return the settings array which indicates which bluesheets should be checked when text is annalyzed
	 */
	public static boolean[] getSettings() {
		return settings;
	}
	
	/**
	 * If the default settings have not already been cached, they will be generated.
	 * 
	 * @return the default settings as determined by the stated availability of each element in {@link Bluesheets}
	 */
	private static boolean[] getDefaultSettings() {
		if(defaultSettings == null)
			generateDefaultSettings();
		return defaultSettings;
	}
	
	/**
	 * Generates the default settings array based on the stated availability of each element in {@link Bluesheets} and caches it.
	 */
	private static void generateDefaultSettings() {
		LogHelper.getLogger(LogHelper.INIT).info("Generating default settings");
		defaultSettings = new boolean[14];
		for(int i = 0; i < settings.length; i++)
			defaultSettings[i] = Bluesheets.values()[i].getAvailabilityWarning() == null;
	}
	
	/**
	 * Returns whether or not the bluesheet corresponding to the passed number should be tested.
	 * 
	 * @param number The number of the bluesheet
	 * @return true if that bluesheet should be tested, false otherwise
	 * @throws IllegalArgumentException if number is not [1, 14]
	 */
	public static boolean isSetToAnalyze(int number) {
		if(number > 14 || number < 1)
			throw new IllegalArgumentException("The passed number: " + number + " is not in the valid range: [1, 14].");
		return Settings.getSettings()[number - 1];
	}
	
	/**
	 * Reads the settings from the file indicated in {@link bschecker.reference.Paths} and loads them into the settings array.
	 * If the file is longer than 14 lines, only the first 14 will be read.
	 * If the file is not found or is less than 14 lines, the default settings will be written and loaded.
	 */
	public static void readSettings() {
		LogHelper.getLogger(LogHelper.IO).info("Reading settings from " + Paths.SETTINGS);
		boolean successful = true;
		try {
			Scanner scan = new Scanner(new File(Paths.SETTINGS));
			LogHelper.getLogger(LogHelper.IO).info("File found");
			for(int i = 0; i < settings.length; i++)
				if(scan.hasNextBoolean())
					settings[i] = scan.nextBoolean();
				else {
					LogHelper.getLogger(LogHelper.IO).warn("File contains less than 14 booleans. Default Settings will be used instead.");
					successful = false;
					break;
				}
			scan.close();
		} catch (FileNotFoundException e) {
			LogHelper.getLogger(LogHelper.IO).warn("File not found. Default Settings will be used instead.");
			successful = false;
		}
		if(successful)
			LogHelper.getLogger(LogHelper.IO).info("Settings read: " + Arrays.toString(settings));
		else {
			writeDefaultSettings();
		}
	}
	
	/**
	 * Creates a settings file at the location indicated in {@link bschecker.reference.Paths} and writes the default settings into it.
	 * If the file already exists its contents will be overwritten.
	 * Once the settings have been written they will be read.
	 */
	public static void writeDefaultSettings() {
		LogHelper.getLogger(LogHelper.IO).info("Writing default settings");
		writeSettings(getDefaultSettings());
		readSettings();
	}
	
	/**
	 * Creates a settings file at the location indicated in {@link bschecker.reference.Paths} and writes the passed boolean[] of settings into it.
	 * If the file already exists its contents will be overwritten.
	 * 
	 * @param writeSettings a boolean[] which must be length 14 which will be written to the settings file
	 */
	private static void writeSettings(boolean[] writeSettings) {
		LogHelper.getLogger(LogHelper.ANALYZE).info("Writing settings to " + Paths.SETTINGS);
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
	 * Writes the change to the settings file indicated in {@link bschecker.reference.Paths}.
	 * 
	 * @param number the number corresponding to the bluesheet whose setting will be reversed [1 - 14]
	 * @throws IllegalArgumentException if number is not [1, 14]
	 */
	public static void reverseSetting(int number) {
		if(number > 14 || number < 1)
			throw new IllegalArgumentException("The passed number: " + number + " is not in the valid range: [1, 14].");
		LogHelper.getLogger(LogHelper.APPLICATION).info("Updating setting for bluesheet #" + number);
		settings[number - 1] = !settings[number - 1];
		writeSettings(settings);
	}
	
	/**
	 * Loads settings without altering the settings file indicated in {@link bschecker.reference.Paths}.
	 * 
	 * @param loadSettings a boolean[] which holds the settings to be loaded
	 */
	public static void loadSettings(boolean[] loadSettings) {
		LogHelper.getLogger(LogHelper.INIT).info("Loading settings: " + Arrays.toString(loadSettings));
		settings = loadSettings;
	}
	
}
