/**
 * The main class for the BSChecker/AutoHirsch
 * 
 * @author MX Programming Club 2016
 * @version 10/15/2016
 */

package BSChecker;

import GUI.BSChecker;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			BSChecker bsc = new BSChecker(primaryStage);
			Scene scene = new Scene(bsc.getRoot(), 500, 600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
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

