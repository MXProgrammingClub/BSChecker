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
 * This class stores the settings indicating which bluesheets should be checked for and provides static methods to change these settings.
 * The settings are stored externally in the file indicated by {@code Paths.SETTINGS}.
 * @author JeremiahDeGreeff
 */
public class Settings {
	
	private static boolean[] defaultSettings;
	private static boolean[] settings = new boolean[14];
	
	public static boolean[] getSettings() {
		return settings;
	}
	
	private static boolean[] getDefaultSettings() {
		if(defaultSettings == null)
			generateDefaultSettings();
		return defaultSettings;
	}
	
	/**
	 * Generates the default settings based on the stated availability of each element in the Enum and caches them.
	 */
	private static void generateDefaultSettings() {
		LogHelper.getLogger(0).info("Generating default settings");
		defaultSettings = new boolean[14];
		for(int i = 0; i < settings.length; i++)
			defaultSettings[i] = Bluesheets.values()[i].getAvailabilityWarning() == null;
	}
	
	/**
	 * Based on the number passed in, returns whether or not the bluesheet with that number should be tested.
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
			writeDefaultSettings();
		}
	}
	
	/**
	 * Creates a settings file and writes the default settings into it.
	 * If the file already exists it will be overwritten.
	 * Once the settings have been written they will be read.
	 */
	public static void writeDefaultSettings() {
		LogHelper.getLogger(16).info("Writing default settings");
		writeSettings(getDefaultSettings());
		readSettings();
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
	
}
