/**
 * This is the main GUI class that compiles all the other classes into one
 * 
 * @author Luke Giacalone
 * @version 10/15/2016
 */

package GUI;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class BSChecker {
	
	private BorderPane root;
	
	public BSChecker(Stage primaryStage) {
		primaryStage.setTitle("ChemHelper");
		Button btn = new Button();
		btn.setText("Say \"Hello World\"");
		btn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("Hello World!");
			}
		});
		BSCheckerMenu menu = new BSCheckerMenu();
		root = new BorderPane();
		root.setTop(menu);
		StackPane centerPane = new StackPane();
		centerPane.getChildren().addAll(btn);
		root.setCenter(centerPane);
	}
	
	/**
	 * Returns the root pane.
	 * @return the root pane
	 */
	public Pane getRoot() {
		return root;
	}
	
}
