package bschecker.reference;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;

import bschecker.util.LogHelper;
import bschecker.util.PerformanceMonitor;

/**
 * Stores the list of verbs of saying or thinking to be used by the project.
 * Also stores static arrays of commonly used verb conjugations.
 * 
 * @author JeremiahDeGreeff
 */
public class VerbSets {
	
	public static final String[] TO_BE_CONJ = {"be", "am", "is", "are", "was", "were", "will be", "been", "has been", "have been", "had been", "will have been", "being", "is being", "are being", "were being"};
	public static final String[] TO_HAVE_CONJ = {"have", "has", "had", "having"};
	
	private static HashSet<String> SayingVerbs;
	
	/**
	 * @return the list of verbs of saying or thinking
	 */
	public static HashSet<String> getSayingVerbs() {
		return SayingVerbs;
	}
	
	/**
	 * Imports the list of verbs of saying or thinking.
	 */
	public static void importSayingVerbs() {
		if(SayingVerbs != null) {
			LogHelper.getLogger(0).warn("Verb Set has already been initialized - skipping");
			return;
		}
		PerformanceMonitor.start("verbs");
		LogHelper.getLogger(0).info("Loading verbs of saying or thinking...");
		SayingVerbs = new HashSet<String>();
		Scanner scan = null;
			try {scan = new Scanner(new File(Paths.SAYING_VERBS));}
			catch (FileNotFoundException e) {e.printStackTrace();}
		while(scan.hasNext()) {
			SayingVerbs.add(scan.nextLine());
		}
		LogHelper.getLogger(0).info("Complete (" + PerformanceMonitor.stop("verbs") + ")");
	}
	
}
