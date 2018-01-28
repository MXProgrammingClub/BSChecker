package bschecker;

import javafx.application.Application;
import javafx.stage.Stage;

import bschecker.application.GUIController;
import bschecker.util.LogHelper;
import bschecker.util.TaskManager;

/**
 * The main class for the BSChecker.
 * 
 * @author MX Programming Club 2016-2017
 */
public class BSChecker extends Application {
	
	/**
	 * The controller for this Application's GUI.
	 */
	private GUIController controller;
	
	/**
	 * Initializes various static references for the project before the application is launched.
	 */
	public void init() {
		TaskManager.runInit(this);
	}
	
	public void start(Stage primaryStage) {
		LogHelper.getLogger(LogHelper.INIT).info("Starting the Application");
		controller = new GUIController(primaryStage);
	}
	
	/**
	 * The method to be called when the init task has succeeded.
	 */
	public void onInitSucceeded() {
		controller.onInitSucceeded();
	}
	
}
