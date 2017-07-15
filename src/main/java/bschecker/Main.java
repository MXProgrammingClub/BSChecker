package bschecker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import bschecker.bluesheets.Bluesheets;
import bschecker.bluesheets.QuotationForm;
import bschecker.gui.GUIController;
import bschecker.util.LogHelper;
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
	
	private static GUIController controller;
	
	public void start(Stage primaryStage) {
		LogHelper.getLogger(15).info("Starting the Application");
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("gui/GUI.fxml"));
			controller = new GUIController();
			loader.setController(controller);
			Parent root = loader.load();
			Scene scene = new Scene(root, 1000, 656);
			scene.getStylesheets().add(this.getClass().getResource("gui/application.css").toExternalForm());

			primaryStage.setTitle("BSChecker");
			primaryStage.setScene(scene);
			controller.setDefaultText();
			primaryStage.show();
		} catch(Exception e) {e.printStackTrace();}
		initialize();
	}
	
	/**
	 * initializes various static references for the project
	 */
	private static void initialize() {
		LogHelper.getLogger(0).info("Beginning Initialization");
		Tools.initializeOpenNLP();
		Bluesheets.readSettings();
		controller.loadSettings(Bluesheets.getSettings());
		QuotationForm.importVerbs();
	}
	
}
