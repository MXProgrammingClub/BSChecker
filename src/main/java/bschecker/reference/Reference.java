package bschecker.reference;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;

import bschecker.util.LogHelper;
import bschecker.util.PerformanceMonitor;

/**
 * Stores the list of verbs of saying or thinking to be used by the project.
 * 
 * @author JeremiahDeGreeff
 */
public class Reference {
	
	private static HashSet<String> verbSet;
	
	/**
	 * Imports the list of verbs of saying or thinking.
	 */
	public static void importVerbs() {
		if(verbSet != null) {
			LogHelper.getLogger(0).warn("Verb Set has already been initialized - skipping");
			return;
		}
		PerformanceMonitor.start("verbs");
		LogHelper.getLogger(0).info("Loading verbs of saying or thinking...");
		verbSet = new HashSet<String>();
		Scanner scan = null;
			try {scan = new Scanner(new File(Paths.SAYING_VERBS));}
			catch (FileNotFoundException e) {e.printStackTrace();}
		while(scan.hasNext()) {
			verbSet.add(scan.nextLine());
		}
		LogHelper.getLogger(0).info("Complete (" + PerformanceMonitor.stop("verbs") + ")");
	}
	
	/**
	 * @return the list of verbs of saying or thinking
	 */
	public static HashSet<String> getVerbSet() {
		return verbSet;
	}
	
}
