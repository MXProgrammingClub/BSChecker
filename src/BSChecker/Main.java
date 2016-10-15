/**
 * The main class for the BSChecker/AutoHirsch
 * 
 * @author MX Programming Club 2016
 * @version 10/15/2016
 */

package BSChecker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("GUI.fxml"));
			loader.setController(new GUIController());
			Parent root = loader.load();
	        Scene scene = new Scene(root, 1000, 650);
	    
	        primaryStage.setTitle("AutoHirsch");
	        primaryStage.setScene(scene);
	        primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}

