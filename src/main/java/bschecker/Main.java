package bschecker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

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
	
	public void start(Stage primaryStage) {
		initialize();
		
		LogHelper.getLogger(15).info("Starting the Application");
		FXMLLoader loader = new FXMLLoader(getClass().getResource("gui/GUI.fxml"));
		GUIController controller = new GUIController();
		loader.setController(controller);
		try {
			Parent root = loader.load();
			controller.setDefaultText();
			Scene scene = new Scene(root, 1000, 656);
			scene.getStylesheets().add(this.getClass().getResource("gui/application.css").toExternalForm());

			primaryStage.setTitle("BSChecker");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(IOException e) {e.printStackTrace();}
	}
	
	/**
	 * initializes various static references for the project before the application is launched
	 */
	private static void initialize() {
		LogHelper.getLogger(0).info("Beginning Initialization");
		Tools.initializeOpenNLP();
		Bluesheets.readSettings();
		QuotationForm.importVerbs();
	}
	
}
