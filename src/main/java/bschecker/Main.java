package bschecker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import bschecker.bluesheets.Bluesheets;
import bschecker.gui.GUIController;
import bschecker.reference.Paths;
import bschecker.reference.Reference;
import bschecker.util.LogHelper;
import bschecker.util.PerformanceMonitor;
import bschecker.util.Tools;

/**
 * The main class for the BSChecker
 * 
 * @author MX Programming Club 2016-2017
 */
public class Main extends Application {
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public void start(Stage primaryStage) {
		initialize();
		LogHelper.getLogger(15).info("Starting the Application");
		FXMLLoader loader = new FXMLLoader(getClass().getResource("gui/GUI.fxml"));
		GUIController controller = new GUIController();
		loader.setController(controller);
		Parent root;
		try {root = loader.load();}
		catch(IOException e) {
			LogHelper.getLogger(15).fatal("Application failed to load - program terminating.");
			e.printStackTrace();
			return;
		}
		Scene scene = new Scene(root, 1000, 656);
		scene.getStylesheets().add(this.getClass().getResource(Paths.APPLICATION_STYLESHEET).toExternalForm());
		
		controller.setDefaultText();
		controller.loadSettings();
		
		primaryStage.setTitle("BSChecker");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	/**
	 * initializes various static references for the project before the application is launched
	 */
	protected static void initialize() {
		PerformanceMonitor.start("init");
		LogHelper.init();
		System.out.println();
		LogHelper.getLogger(0).info("Beginning Initialization...");
		System.out.println();
		Tools.initializeOpenNLP();
		System.out.println();
		Bluesheets.readSettings();
		System.out.println();
		Reference.importVerbs();
		System.out.println();
		LogHelper.getLogger(0).info("Initialization Completed in " + PerformanceMonitor.stop("init"));
		System.out.println();
	}
	
}
