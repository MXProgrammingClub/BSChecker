package bschecker.application;

import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Creates a progress dialog to be used when analyzing an essay
 * @author JeremiahDeGreeff
 */
public class ProgressDialog {
	
	private final Stage stage;
	private final ProgressBar bar = new ProgressBar(0);
	
	public ProgressDialog() {
		stage = new Stage();
		stage.setTitle("BSChecker is analyzing your essay.");
		stage.setResizable(false);
		stage.initModality(Modality.APPLICATION_MODAL);
		
		final HBox hb = new HBox();
		hb.setAlignment(Pos.CENTER);
		hb.getChildren().addAll(bar);
		
		Scene scene = new Scene(hb, 300, 50);
		stage.setScene(scene);
		
	}
	
	/**
	 * activates the progress bar 
	 * @param task
	 */
	public void activateProgressBar(Task<?> task)  {
		bar.progressProperty().bind(task.progressProperty());
		stage.setOnCloseRequest(event -> {
			task.cancel();
		});
		stage.show();
	}
	
	/**
	 * closes the dialog
	 */
	public void close() {
		stage.close();
	}
	
}
