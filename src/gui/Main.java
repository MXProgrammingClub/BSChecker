package gui;

import error.*;
import error.Error;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.UtilityMethods;

/**
 * The main class for the BSChecker
 * 
 * @author MX Programming Club 2016
 * @version 10/15/2016
 */
public class Main extends Application {

	public final static Error[] ERROR_LIST = {
			new PastTense(true),
			new IncompleteSentence(false), //unimplemented
			new FirstSecondPerson(true),
			new VagueThisWhich(true),
			new NumberDisagreement(false), //nonfunctional
			new PronounCase(true),
			new AmbiguousPronoun(false), //over-reports
			new Apostrophe(true),
			new PassiveVoice(true),
			new DanglingModifier(false), //unimplemented
			new FaultyParallelism(false), //nonfunctional
			new ProgressiveTense(true),
			new GerundPossessive(true),
			new QuotationForm(true)
	};

	public static void main(String[] args) {
		launch(args);
	}
	
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
		} catch(Exception e) {e.printStackTrace();}
		
		UtilityMethods.setupOpenNLP();
	}
}
