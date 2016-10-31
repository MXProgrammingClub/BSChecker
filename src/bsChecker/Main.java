package bsChecker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The main class for the BSChecker
 * 
 * @author MX Programming Club 2016
 * @version 10/15/2016
 */
public class Main extends Application {

	public final static Error[] ERROR_LIST = {
			new ErrorPastTense(),
//			new ErrorIncompleteSentence(),
			new ErrorFirstSecondPerson(),
			new ErrorVagueThisWhich(),
//			new ErrorNumberDisagreement(),
//			new ErrorPronounCase(),
			new ErrorAmbiguousPronoun(),
//			new ErrorApostrophe(),
			new ErrorPassiveVoice(),
//			new ErrorDanglingModifier(),
//			new ErrorFaultyParallelism(),
			new ErrorProgressiveTense(),
			new ErrorGerundPossesive(),
			new ErrorQuotationForm()
	};


	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("GUI.fxml"));
			GUIController controller = new GUIController();
			loader.setController(controller);
			Parent root = loader.load();
			Scene scene = new Scene(root, 1000, 650);
			scene.getStylesheets().add(this.getClass().getResource("application.css").toExternalForm());

			primaryStage.setTitle("BSChecker");
			primaryStage.setScene(scene);
			controller.setDefaultText();
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}

