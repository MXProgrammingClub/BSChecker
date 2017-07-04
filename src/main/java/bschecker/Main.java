package main.java.bschecker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.bschecker.bluesheets.Bluesheet.Bluesheets;
import main.java.bschecker.gui.GUIController;
import main.java.bschecker.util.Tools;

/**
 * The main class for the BSChecker
 * 
 * @author MX Programming Club 2016
 * @version 10/15/2016
 */
public class Main extends Application {
	public static void main(String[] args) {
		Tools.initializeOpenNLP();
		Bluesheets.readSettings();
		launch(args);
	}
	
	public void start(Stage primaryStage) {
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
			primaryStage.show();
		} catch(Exception e) {e.printStackTrace();}
	}
}
