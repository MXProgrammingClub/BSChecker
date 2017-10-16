package bschecker;

import javafx.application.Application;
import javafx.stage.Stage;

import bschecker.application.GUIController;
import bschecker.reference.VerbSet;
import bschecker.reference.Settings;
import bschecker.util.LogHelper;
import bschecker.util.PerformanceMonitor;
import bschecker.util.Tools;

/**
 * The main class for the BSChecker.
 * 
 * @author MX Programming Club 2016-2017
 */
public class Main extends Application {
	
	/**
	 * Initializes various static references for the project before the application is launched.
	 */
	public void init() {
		PerformanceMonitor.start("init");
		LogHelper.init();
		LogHelper.line();
		LogHelper.getLogger(0).info("Beginning Initialization...");
		LogHelper.line();
		Tools.initializeOpenNLP();
		LogHelper.line();
		Settings.readSettings();
		LogHelper.line();
		VerbSet.importVerbs();
		LogHelper.line();
		LogHelper.getLogger(0).info("Initialization Completed in " + PerformanceMonitor.stop("init"));
		LogHelper.line();
	}
	
	public void start(Stage primaryStage) {
		LogHelper.getLogger(15).info("Starting the Application");
		new GUIController(primaryStage);
	}
	
}
