/**
 * This is the class that connects the GUI with the rest of the program.
 * 
 * @author Luke Giacalone
 * @version 10/15/2016
 */

package BSChecker;

import java.io.File;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;

import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;

public class GUIController {
	
	@FXML
	private JFXTextArea essayBox;
	
	@FXML
	private JFXTextArea sentenceBox;
	
	@FXML
	private JFXTextArea errorBox;
	
	@FXML
	private JFXButton buttonLeft;
	
	@FXML
	private JFXButton buttonRight;
	
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
	
	/**
	 * The method that will be called when the left arrow is clicked
	 */
	@FXML
	protected void leftArrowClick() {
		/* LEFT ARROW ACTION */
	}
	
	/**
	 * The method that will be called when the right arrow is clicked
	 */
	@FXML
	protected void rightArrowClick() {
		/* RIGHT ARROW ACTION */
	}
	
	/**
	 * The method that will be called when the File->Open is clicked. It takes the file and puts the contents
	 * into the essay box.
	 */
	@FXML
	protected void menuOpenClick() {
		File file = TextImport.chooseFile();
		if(file == null) return;
		String text = TextImport.openFile(file);
		if(text == null) return;
		essayBox.setText(text);
	}
	
	/**
	 * The method that will be called when the File->Save is clicked
	 */
	@FXML
	protected void menuSaveClick() {
		/* FILE->SAVE ACTION */
	}
	
	/**
	 * The method that will be called when the File->Save As is clicked
	 */
	@FXML
	protected void menuSaveAsClick() {
		/* FILE->SAVEAS ACTION */
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
		/* EDIT->CUT ACTION */
	}
	
	/**
	 * The method that will be called when the Edit->Copy is clicked
	 */
	@FXML
	protected void menuCopyClick() {
		/* EDIT->COPY ACTION */
	}
	
	/**
	 * The method that will be called when the Edit->Paste is clicked
	 */
	@FXML
	protected void menuPasteClick() {
		/* EDIT->PASTE ACTION */
	}
	
	/**
	 * The method that will be called when the Edit->Select All is clicked
	 */
	@FXML
	protected void menuSelectAllClick() {
		/* EDIT->SELECTALL ACTION */
	}
	
	/**
	 * The method that will be called when the View->Next Error is clicked
	 */
	@FXML
	protected void menuNextErrorClick() {
		/* VIEW->NEXTERROR ACTION */
	}
	
	/**
	 * The method that will be called when the View->Previous Error is clicked
	 */
	@FXML
	protected void menuPreviousErrorClick() {
		/* VIEW->PREVIOUSERROR ACTION */
	}
	
	/**
	 * The method that will be called when the Help->About is clicked
	 */
	@FXML
	protected void menuAboutClick() {
		/* HELP->ABOUT ACTION */
	}
	
}
