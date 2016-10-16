/**
 * This is the class that connects the GUI with the rest of the program.
 * 
 * @author Luke Giacalone
 * @version 10/15/2016
 */

package BSChecker;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.fxmisc.richtext.StyleClassedTextArea;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;

import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;

public class GUIController {

	@FXML
	private StyleClassedTextArea essayBox;

	@FXML
	private JFXTextArea errorBox;

	@FXML
	private JFXButton buttonLeft;

	@FXML
	private JFXButton buttonRight;

	@FXML
	private JFXButton analyzeButton;

	@FXML
	private MenuItem menuOpen;

	@FXML
	private MenuItem menuSave;

	@FXML
	private MenuItem menuSaveAs;

	@FXML
	private MenuItem menuUndo;

	@FXML
	private MenuItem menuRedo;

	@FXML
	private MenuItem menuCut;

	@FXML
	private MenuItem menuCopy;

	@FXML
	private MenuItem menuPaste;

	@FXML
	private MenuItem menuSelectAll;

	@FXML
	private MenuItem menuNextError;

	@FXML
	private MenuItem menuPreviousError;

	@FXML
	private MenuItem menuAbout;

	private int currError = 0;
	private ArrayList<int[]> errors;
	private File file;
	private String clipboard = "";
	
	/**
	 * The method that will be called when the left arrow is clicked
	 */
	@FXML
	protected void leftArrowClick() {
		if(errors.size() != 0) {
			previousError();
		}
	}

	/**
	 * The method that will be called when the right arrow is clicked
	 */
	@FXML
	protected void rightArrowClick() {
		if(errors.size() != 0) {
			nextError();
		}
	}

	/**
	 * The method that will be called when the analyze button is clicked
	 */
	@FXML
	protected void analyzeButtonClick() {
		errors = new ArrayList<>();
		for(Error e: Main.ERROR_LIST) {
			//System.out.println(e.getClass().toString());
			String text = essayBox.getText();
			//text = Normalizer.normalize(text, Normalizer.Form.NFD); //normalizes to ASCII
			ArrayList<Replacement>  replacements = new ArrayList<Replacement>();
			
			// double quotation (")
		    replacements.add(new Replacement(Pattern.compile("[\u201c\u201d\u201e\u201f\u275d\u275e]"), "\""));

		    // single quotation (')
		    replacements.add(new Replacement(Pattern.compile("[\u2018\u2019\u201a\u201b\u275b\u275c]"), "'"));
		   
		    for (Replacement replacement : replacements) {
		         text = replacement.pattern.matcher(text).replaceAll(replacement.toString());
		    }
			essayBox.replaceText(text);
		    
			Alert a = new Alert(Alert.AlertType.INFORMATION);
			a.show();
			ArrayList<int[]> temp = e.findErrors(text);
			Button cancelButton = (Button) a.getDialogPane().lookupButton(a.getDialogPane().getButtonTypes().get(0));
			cancelButton.fire();
			/*for(int[] i: temp) {
				System.out.println(Arrays.toString(i));
			}*/
			errors.addAll(temp);
		}/*
		for(int i = 0; i < errors.size(); i++) {
			System.out.println(errors.get(i)[0] + "-" + errors.get(i)[1] + " (Error " + errors.get(i)[2] + ")");
		}
		*/
		Error.sort(errors); //sorts the errors based on starting index

		if(errors.size() == 0) {
			errorBox.setText("No Error Found!");
		}
		else {
			currError = 0;
			//highlight all the errors
			for(int[] location: errors) {
				essayBox.setStyleClass(location[0], location[1] + 1, "light-red");
			}

			//put first error in sentenceBox and corresponding thing in errorBox
			//System.out.println(essayBox.getText().substring(errors.get(0)[0], errors.get(0)[1]));
			displayError();
		}
	}
	
	/**
	 * Updates the selected text to the next error.
	 */
	private void nextError()
	{
		resetCurrentColor();
		currError++;
		if(currError >= errors.size())
		{
			Alert a = new Alert(Alert.AlertType.INFORMATION);
			a.setContentText("Searching from beginning of passage.");
			a.setHeaderText("Search Complete");
			a.setTitle("Notice");
			a.showAndWait();
			currError = 0;
		}
		displayError();	
	}
	
	/**
	 * Updates the selected text to the previous error.
	 */
	private void previousError()
	{
		resetCurrentColor();
		currError--;
		if(currError < 0)
		{
			Alert a = new Alert(Alert.AlertType.INFORMATION);
			a.setContentText("Searching from end of passage.");
			a.setHeaderText("Search Complete");
			a.setTitle("Notice");
			a.showAndWait();
			currError = errors.size() - 1;
		}
		displayError();
	}
	
	/**
	 * Displays the current error.
	 */
	private void displayError() {
		essayBox.positionCaret(errors.get(currError)[0]);
		essayBox.setStyleClass(errors.get(currError)[0], errors.get(currError)[1] + 1, "dark-red");
		Bluesheet b = Bluesheet.getBluesheetFromNum(errors.get(currError)[2]);
		errorBox.setText(b.getName() + "\n\n" + b.getDescription() + "\n\n" + b.getExample());
	}
	
	/**
	 * Resets the color of the current error to the lighter color
	 */
	private void resetCurrentColor() {
		essayBox.setStyleClass(errors.get(currError)[0], errors.get(currError)[1] + 1, "light-red");
	}

	/**
	 * The method that will be called when the File->Open is clicked. It takes the file and puts the contents
	 * into the essay box.
	 */
	@FXML
	protected void menuOpenClick() {
		file = TextImport.chooseFile();
		if(file == null) return;
		String text = TextImport.openFile(file);
		if(text == null) return;
		//essayBox.setText(text);
		essayBox.replaceText(text);
	}

	/**
	 * The method that will be called when the File->Save is clicked
	 */
	@FXML
	protected void menuSaveClick() {
		if(file != null)
		{
			if(!TextImport.saveText(file, essayBox.getText()))
			{
				Alert a = new Alert(Alert.AlertType.ERROR);
				a.setTitle("Saving Error");
				a.setContentText("There was an error in saving your file. It may be in use or moved from its original location.");
				a.showAndWait();
			}
		}
	}

	/**
	 * The method that will be called when the File->Save As is clicked
	 */
	@FXML
	protected void menuSaveAsClick() {
		TextImport.saveAs(essayBox.getText());
	}

	/**
	 * The method that will be called when the Edit->Undo is clicked
	 */
	@FXML
	protected void menuUndoClick() {
		/* EDIT->UNDO ACTION */
	}

	/**
	 * The method that will be called when the Edit->Redo is clicked
	 */
	@FXML
	protected void menuRedoClick() {
		/* EDIT->REDO ACTION */
	}

	/**
	 * The method that will be called when the Edit->Cut is clicked
	 */
	@FXML
	protected void menuCutClick() {
		String temp = essayBox.getSelectedText();
		if(!temp.equals(""))
		{
			clipboard = temp;
			essayBox.deleteText(essayBox.getSelection());
		}
	}

	/**
	 * The method that will be called when the Edit->Copy is clicked
	 */
	@FXML
	protected void menuCopyClick() {
		String temp = essayBox.getSelectedText();
		if(!temp.equals("")) clipboard = temp;
	}

	/**
	 * The method that will be called when the Edit->Paste is clicked
	 */
	@FXML
	protected void menuPasteClick() {
		essayBox.insertText(essayBox.getSelection().getEnd(), clipboard);
	}

	/**
	 * The method that will be called when the Edit->Select All is clicked
	 */
	@FXML
	protected void menuSelectAllClick() {
		essayBox.selectAll();
	}

	/**
	 * The method that will be called when the View->Next Error is clicked
	 */
	@FXML
	protected void menuNextErrorClick() {
		rightArrowClick();
	}

	/**
	 * The method that will be called when the View->Previous Error is clicked
	 */
	@FXML
	protected void menuPreviousErrorClick() {
		leftArrowClick();
	}

	/**
	 * The method that will be called when the Help->About is clicked
	 */
	@FXML
	protected void menuAboutClick() {
		/* HELP->ABOUT ACTION */
	}

}
