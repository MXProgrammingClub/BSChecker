package bschecker.application;

import java.io.IOException;

import com.jfoenix.controls.JFXProgressBar;

import bschecker.reference.Paths;
import bschecker.util.LogHelper;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Creates a progress dialog to be used when analyzing an essay.
 * 
 * @author JeremiahDeGreeff
 */
public class ProgressDialogController {
	
	@FXML
	private JFXProgressBar progressBar;
	
	private final Stage stage = new Stage();;
	
	public ProgressDialogController() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(Paths.PROGRESS_DIALOG_FXML));
		loader.setController(this);
		
		stage.setTitle("BSChecker is analyzing your essay.");
		stage.setResizable(false);
		try {stage.setScene(new Scene(loader.load(), 300, 100));}
		catch (IOException e) {
			LogHelper.getLogger(LogHelper.ANALYZE).error("ProgressDialog Failed to load");
			e.printStackTrace();
		}
	}
	
	/**
	 * Activates the progress bar.
	 * 
	 * @param task the Task whose progress will be displayed
	 */
	public void activateProgressBar(Task<?> task) {
		progressBar.progressProperty().bind(task.progressProperty());
		stage.setOnCloseRequest(event -> {task.cancel();});
		stage.show();
	}
	
	/**
	 * Closes the dialog.
	 */
	public void close() {
		stage.close();
	}
	
}
