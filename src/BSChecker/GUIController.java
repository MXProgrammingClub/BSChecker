/**
 * This is the class that connects the GUI with the rest of the program.
 * 
 * @author Luke Giacalone
 * @version 10/15/2016
 */

package BSChecker;

import java.io.File;

import java.util.ArrayList;

import org.fxmisc.richtext.StyleClassedTextArea;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;

import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;

public class GUIController {
	
	@FXML
	private StyleClassedTextArea essayBox;
	
	@FXML
	private JFXTextArea sentenceBox;
	
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
	/**
	 * The method that will be called when the left arrow is clicked
	 */
	@FXML
	protected void leftArrowClick() {
		if(errors.size() != 0) {
			currError = (currError - 1 + errors.size()) % errors.size();
			System.out.println(errors.size());
			Bluesheet b = Bluesheet.getBluesheetFromNum(errors.get(currError)[2]);
			errorBox.setText(b.getName() + "\n\n" + b.getDescription());
		}
	}
	
	/**
	 * The method that will be called when the right arrow is clicked
	 */
	@FXML
	protected void rightArrowClick() {
		if(errors.size() != 0) {
			currError = (currError + 1) % errors.size();
			Bluesheet b = Bluesheet.getBluesheetFromNum(errors.get(currError)[2]);
			errorBox.setText(b.getName() + "\n\n" + b.getDescription());
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
			ArrayList<int[]> temp = e.findErrors(essayBox.getText());
			/*for(int[] i: temp) {
				System.out.println(Arrays.toString(i));
			}*/
			errors.addAll(temp);
		}
		System.out.println(errors);
		
		Error.sort(errors); //sorts the errors based on starting index
		
		if(errors.size() == 0) {
			sentenceBox.setText("No Errors Found!");
			errorBox.setText("No Error Found!");
		}
		else {
			currError = 0;
			//highlight all the errors
			for(int[] location: errors) {
				essayBox.setStyleClass(location[0], location[1] + 1, "red");
			}
			
			//put first error in sentenceBox and corresponding thing in errorBox
			//System.out.println(essayBox.getText().substring(errors.get(0)[0], errors.get(0)[1]));
			Bluesheet b = Bluesheet.getBluesheetFromNum(errors.get(0)[2]);
			errorBox.setText(b.getName() + "\n\n" + b.getDescription());
		}
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
				//popup error message
			}
		}
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
