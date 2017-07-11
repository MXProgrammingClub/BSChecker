package main.java.bschecker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.bschecker.bluesheets.Bluesheets;
import main.java.bschecker.gui.GUIController;
import main.java.bschecker.util.LogHelper;
import main.java.bschecker.util.Tools;

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
		LogHelper.getLogger(0).info("Starting the Application");
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("gui/GUI.fxml"));
			GUIController controller = new GUIController();
			loader.setController(controller);
			Parent root = loader.load();
			Scene scene = new Scene(root, 1000, 656);
			scene.getStylesheets().add(this.getClass().getResource("gui/application.css").toExternalForm());

			primaryStage.setTitle("BSChecker");
			primaryStage.setScene(scene);
			controller.setDefaultText();
			controller.loadSettings(Bluesheets.getSettings());
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
	}
	
}
