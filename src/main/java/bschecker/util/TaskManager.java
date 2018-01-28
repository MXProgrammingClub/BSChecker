package bschecker.util;

import java.util.ArrayList;

import bschecker.BSChecker;
import bschecker.application.GUIController;
import bschecker.application.ProgressDialogController;
import bschecker.bluesheets.Bluesheets;
import bschecker.reference.Settings;
import bschecker.reference.VerbSets;
import javafx.concurrent.Task;
import opennlp.tools.parser.Parse;

/**
 * A class that manages tasks that need to run on threads other than the JavaFX application thread.
 * 
 * @author JeremiahDeGreeff
 */
public class TaskManager {
	
	/**
	 * Initializes various static references for the project using a Task on a separate thread.
	 * 
	 * @param source the Application which requested the init task
	 */
	public static void runInit(final BSChecker source) {
		LogHelper.init();
		LogHelper.line();
		Task<Boolean> init = new Task<Boolean>() {
			@Override
			protected Boolean call() {
				LogHelper.getLogger(LogHelper.INIT).info("Starting initialization task...");
				PerformanceMonitor.start("init");
				LogHelper.line();
				
				LogHelper.getLogger(LogHelper.INIT).info("Loading settings...");
				PerformanceMonitor.start("settings");
				Settings.readSettings();
				LogHelper.getLogger(LogHelper.INIT).info("Complete (" + PerformanceMonitor.stop("settings") + ")");
				LogHelper.line();
				
				LogHelper.getLogger(LogHelper.INIT).info("Loading verbs of saying or thinking...");
				PerformanceMonitor.start("verbs");
				VerbSets.importSayingVerbs();
				LogHelper.getLogger(LogHelper.INIT).info("Complete (" + PerformanceMonitor.stop("verbs") + ")");
				LogHelper.line();
				
				LogHelper.getLogger(LogHelper.INIT).info("Initializing openNLP tools...");
				PerformanceMonitor.start("tools");
				Tools.initializeOpenNLP();
				LogHelper.getLogger(LogHelper.INIT).info("Initialization of openNLP tools completed in " + PerformanceMonitor.stop("tools"));
				LogHelper.line();
				
				LogHelper.getLogger(LogHelper.INIT).info("Initialization task completed in " + PerformanceMonitor.stop("init"));
				LogHelper.line();
				return true;
			}
		};
		
		init.setOnSucceeded(event -> {source.onInitSucceeded();});
		
		Thread thread = new Thread(init, "Init");
		thread.start();
	}
	
	/**
	 * Runs the analysis of the passed text using a Task on a separate thread.
	 * All Bluesheets referenced in {@link #Settings} with a value of {@code true} will be checked.
	 * 
	 * @param source the GUIController which requested the analyze task
	 * @param text the text to analyze
	 * @param logParses if true, parses will be logged
	 */
	public static void runAnalyze(final GUIController source, final String text, final boolean logParses) {
		Task<ErrorList> analyze = new AnalyzeTask(text);
		
		ProgressDialogController dialog = new ProgressDialogController();
		dialog.activateProgressBar(analyze);
		
		analyze.setOnRunning(event -> {source.onAnalyzeRunning();});
		analyze.setOnSucceeded(event -> {dialog.close(); source.onAnalyzeSucceeded(analyze.getValue());});
		analyze.setOnCancelled(event -> {source.onAnalyzeCancelled();});
		
		Thread thread = new Thread(analyze, "Analyze");
		thread.start();
	}
	
	/**
	 * Class for analysis tasks.
	 * 
	 * @author JeremiahDeGreeff
	 */
	private static class AnalyzeTask extends Task<ErrorList> {
		private String text;
		private double workDone = -1, totalWork;
		
		private AnalyzeTask(String text) {
			super();
			this.text = text;
		}
		
		/**
		 * Analyzes the text of this AnalyzeTask.
		 */
		@Override
		protected ErrorList call() {
			LogHelper.getLogger(LogHelper.ANALYZE).info("Starting essay analysis task...");
			LogHelper.line();
			PerformanceMonitor.start("analyze");
			
			String fullText = text + (text.endsWith("\n") ? "" : "\n");
			int bluesheets = 0;
			for(Bluesheets b : Bluesheets.values())
				if(Settings.isSetToAnalyze(b.getNumber()))
					bluesheets++;
			totalWork = UtilityMethods.countOccurences(fullText, "\n") * (bluesheets + 1);
			IncrementProgress();
			
			ErrorList errors = analyze(fullText, this);
			
			LogHelper.getLogger(LogHelper.ANALYZE).info("Essay analysis task completed in " + PerformanceMonitor.stop("analyze") + ":");
			LogHelper.getLogger(LogHelper.ANALYZE).info(errors);
			return errors;
		}
		
		/**
		 * Increments the progress bar on this task's dialog.
		 */
		private void IncrementProgress() {
			this.updateProgress(++workDone, totalWork);
		}
	}
	
	/**
	 * Analyzes the passed text, finding all errors within it.
	 * All Bluesheets referenced in {@link #Settings} with a value of {@code true} will be checked.
	 * 
	 * @param text the text to analyze
	 * @param task the AnalyzeTask this analysis is associated with, null if not run from the application
	 * @return an ErrorList which contains all the errors in the passage, referenced by character indices
	 */
	public static ErrorList analyze(String text, AnalyzeTask task) {
		ErrorList errors = new ErrorList(text, false);
		int lineNum = 1, charOffset = 0;
		while (charOffset < text.length()) {
			PerformanceMonitor.start("line");
			String line = text.substring(charOffset, charOffset + text.substring(charOffset).indexOf('\n'));
			LogHelper.getLogger(LogHelper.ANALYZE).info("Analyzing line " + lineNum + " (characters " + charOffset + "-" + (charOffset + line.length()) + "):");
			
			PerformanceMonitor.start("parse");
			ArrayList<Integer> removedChars = new ArrayList<Integer>();
			String[] linePointer = new String[] {line};
			Parse[] parses = UtilityMethods.parseLine(linePointer, task == null, lineNum, charOffset, removedChars);
			line = linePointer[0];
			LogHelper.getLogger(LogHelper.PARSE).info("Complete (" + PerformanceMonitor.stop("parse") + ")");
			if(task != null) task.IncrementProgress();
			
			ErrorList lineErrors = new ErrorList(line, true);
			for(Bluesheets b : Bluesheets.values())
				if(Settings.isSetToAnalyze(b.getNumber())) {
					PerformanceMonitor.start("bluesheet");
					LogHelper.getLogger(LogHelper.ANALYZE).info("Looking for: " + b.getName() + "...");
					ErrorList bluesheetErrors = b.getObject().findErrors(line, parses);
					bluesheetErrors.setBluesheetNumber(b.getNumber());
					lineErrors.addAll(bluesheetErrors);
					LogHelper.getLogger(LogHelper.ANALYZE).info(bluesheetErrors.size() + " Error" + (bluesheetErrors.size() == 1 ? "" : "s") + " Found (" + PerformanceMonitor.stop("bluesheet") + ")");
					if(task != null) task.IncrementProgress();
				}
			LogHelper.getLogger(LogHelper.ANALYZE).info(lineErrors.size() + " Error" + (lineErrors.size() == 1 ? "" : "s") + " Found in line " + lineNum + " (" + PerformanceMonitor.stop("line") + ")");
			LogHelper.line();
			
			errors.addAll(lineErrors.tokensToChars(charOffset, removedChars));
			
			lineNum++;
			charOffset += line.length() + removedChars.size() + 1;
		}
		return errors;
	}
	
}
