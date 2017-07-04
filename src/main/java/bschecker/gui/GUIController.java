package main.java.bschecker.gui;

import java.io.File;

import org.fxmisc.richtext.StyleClassedTextArea;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import main.java.bschecker.bluesheets.Bluesheet;
import main.java.bschecker.bluesheets.Bluesheets;
import main.java.bschecker.util.Error;
import main.java.bschecker.util.ErrorList;
import main.java.bschecker.util.TextImport;
import main.java.bschecker.util.UtilityMethods;

/**
 * This is the class that connects the GUI with the rest of the program.
 * 
 * @author Luke Giacalone
 * @author JeremiahDeGreeff
*/

public class GUIController {

	@FXML
	private StyleClassedTextArea essayBox;

	@FXML
	private StyleClassedTextArea errorBox;
	
	@FXML
	private StyleClassedTextArea noteBox;

	/**
	 * The method that will be called when the analyze button is clicked
	 */
	@FXML
	protected void analyzeButtonClick() {
		Dialog<ButtonType> d = new Dialog<ButtonType>();
		d.setTitle("Analyzing");
		d.setContentText("BSChecker is analyzing your essay.");
		d.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
		d.show();
		
		String text = essayBox.getText();
		essayBox.setStyleClass(0, essayBox.getLength(), null);
		
		text = UtilityMethods.replaceInvalidChars(text);
		if(text.charAt(text.length() - 1) != '\n')
			text += "\n";
		essayBox.replaceText(text);
		
		errors = Bluesheet.findAllErrors(text);
		
		d.close();
		
		if(errors.size() == 0)
			errorBox.replaceText("No Error Found!");
		else {
			currError = 0;
			//highlight all the errors
			for(Error error : errors)
				essayBox.setStyleClass(error.getStartIndex(), error.getEndIndex() + 1, "light-red");
			//put first error in sentenceBox and corresponding thing in errorBox
			displayError();
		}
	}
	
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
	 * The method that will be called when the File->Open is clicked. It takes the file and puts the contents
	 * into the essay box.
	 */
	@FXML
	protected void menuOpenClick() {
		file = TextImport.chooseFile();
		if(file == null)
			return;
		String text = TextImport.openFile(file);
		if(text == null)
			return;
		essayBox.replaceText(text);
	}

	/**
	 * The method that will be called when the File->Save is clicked
	 */
	@FXML
	protected void menuSaveClick() {
		if(file != null && !TextImport.saveText(file, essayBox.getText())) {
			Alert a = new Alert(Alert.AlertType.ERROR);
			a.setTitle("Saving Error");
			a.setContentText("There was an error in saving your file. It may be in use or moved from its original location.");
			a.showAndWait();
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
		if(!temp.equals("")) {
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
		if(!temp.equals(""))
			clipboard = temp;
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
	protected void menuSelectAllClick() {essayBox.selectAll();}

	/**
	 * The method that will be called when the View->Next Error is clicked
	 */
	@FXML
	protected void menuNextErrorClick() {rightArrowClick();}

	/**
	 * The method that will be called when the View->Previous Error is clicked
	 */
	@FXML
	protected void menuPreviousErrorClick() {leftArrowClick();}
	
	/**
	 * The method that will be called when the Bluesheets->Past Tense (1) is clicked
	 */
	@FXML
	protected void menuBluesheet1Click() {Bluesheets.reverseSetting(1);}
	
	/**
	 * The method that will be called when the Bluesheets->Incomplete Sentence (2) is clicked
	 */
	@FXML
	protected void menuBluesheet2Click() {Bluesheets.reverseSetting(2);}
	
	/**
	 * The method that will be called when the Bluesheets->First/Second Person (3) is clicked
	 */
	@FXML
	protected void menuBluesheet3Click() {Bluesheets.reverseSetting(3);}
	
	/**
	 * The method that will be called when the Bluesheets->Vague This/Which (4) is clicked
	 */
	@FXML
	protected void menuBluesheet4Click() {Bluesheets.reverseSetting(4);}
	
	/**
	 * The method that will be called when the Bluesheets->Subject-Verb Disagreement (5) is clicked
	 */
	@FXML
	protected void menuBluesheet5Click() {Bluesheets.reverseSetting(5);}
	
	/**
	 * The method that will be called when the Bluesheets->Pronoun Case (6) is clicked
	 */
	@FXML
	protected void menuBluesheet6Click() {Bluesheets.reverseSetting(6);}
	
	/**
	 * The method that will be called when the Bluesheets->Ambiguous Pronoun (7) is clicked
	 */
	@FXML
	protected void menuBluesheet7Click() {Bluesheets.reverseSetting(7);}
	
	/**
	 * The method that will be called when the Bluesheets->Apostrophe Error (8) is clicked
	 */
	@FXML
	protected void menuBluesheet8Click() {Bluesheets.reverseSetting(8);}
	
	/**
	 * The method that will be called when the Bluesheets->Passive Voice (9) is clicked
	 */
	@FXML
	protected void menuBluesheet9Click() {Bluesheets.reverseSetting(9);}
	
	/**
	 * The method that will be called when the Bluesheets->Dangling Modifier (10) is clicked
	 */
	@FXML
	protected void menuBluesheet10Click() {Bluesheets.reverseSetting(10);}
	
	/**
	 * The method that will be called when the Bluesheets->Faulty Parallelism (11) is clicked
	 */
	@FXML
	protected void menuBluesheet11Click() {Bluesheets.reverseSetting(11);}
	
	/**
	 * The method that will be called when the Bluesheets->Progressive Tense (12) is clicked
	 */
	@FXML
	protected void menuBluesheet12Click() {Bluesheets.reverseSetting(12);}
	
	/**
	 * The method that will be called when the Bluesheets->Gerund Possesive (13) is clicked
	 */
	@FXML
	protected void menuBluesheet13Click() {Bluesheets.reverseSetting(13);}
	
	/**
	 * The method that will be called when the Bluesheets->Quotation Form (14) is clicked
	 */
	@FXML
	protected void menuBluesheet14Click() {Bluesheets.reverseSetting(14);}

	/**
	 * The method that will be called when the Help->About is clicked
	 */
	@FXML
	protected void menuAboutClick() {
		/* HELP->ABOUT ACTION */
	}
	
	private int currError = 0;
	private ErrorList errors;
	private File file;
	private String clipboard = "";
	
	/**
	 * This method sets default "empty" text for the Text Areas
	 */
	public void setDefaultText() {
		essayBox.replaceText("Insert Essay Here");
		errorBox.replaceText("No Error Selected");
		noteBox.replaceText("No Error Selected");
	}
	
	/**
	 * Updates the selected text to the next error.
	 */
	private void nextError() {
		resetCurrentColor();
		currError++;
		if(currError >= errors.size()) {
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
	private void previousError() {
		resetCurrentColor();
		currError--;
		if(currError < 0) {
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
		essayBox.positionCaret(errors.get(currError).getStartIndex());
		essayBox.setStyleClass(errors.get(currError).getStartIndex(), errors.get(currError).getEndIndex() + 1, "dark-red");
		Bluesheets b = Bluesheets.getBluesheetFromNum(errors.get(currError).getBluesheetNumber());
		errorBox.replaceText(b.getName() + "\n\n" + b.getDescription() + "\n\n" + b.getExample());
		noteBox.replaceText(errors.get(currError).getNote().equals("") ? "No note was found for this error." : errors.get(currError).getNote());
	}
	
	/**
	 * Resets the color of the current error to the lighter color
	 */
	private void resetCurrentColor() {
		essayBox.setStyleClass(errors.get(currError).getStartIndex(), errors.get(currError).getEndIndex() + 1, "light-red");
	}
}
