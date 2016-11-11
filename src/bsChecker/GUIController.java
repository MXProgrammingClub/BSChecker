package bsChecker;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.fxmisc.richtext.StyleClassedTextArea;

import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.MenuItem;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

/**
 * This is the class that connects the GUI with the rest of the program.
 * 
 * @author Luke Giacalone
 * @version 10/15/2016
 */

public class GUIController {

	@FXML
	private StyleClassedTextArea essayBox;

	@FXML
	private StyleClassedTextArea errorBox;

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
	private boolean opennlpIsSetUp = false;
	
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
		Dialog<ButtonType> d = new Dialog<ButtonType>();
		d.setTitle("Analyzing");
		d.setContentText("BSChecker is analyzing your essay.");
		d.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
		d.show();
		
		String text = essayBox.getText();
		ArrayList<Replacement>  replacements = new ArrayList<Replacement>();
		// double quotation (")
	    replacements.add(new Replacement(Pattern.compile("[\u201C\u201D\u201E\u201F\u275D\u275E]"), "\""));
	    // single quotation (')
	    replacements.add(new Replacement(Pattern.compile("[\u2018\u2019\u201A\u201B\u275B\u275C]"), "\'"));
	    // ellipsis (...)
	    replacements.add(new Replacement(Pattern.compile("[\u2026]"), "..."));	   
	    for (Replacement replacement : replacements) {
	         text = replacement.pattern.matcher(text).replaceAll(replacement.toString());
	    }
		essayBox.replaceText(text);
		
		if(opennlpIsSetUp == false) {
			System.out.println("setting up opennlp");
			Error.setupOpenNLP();
			opennlpIsSetUp = true;
		} else {
			System.out.println("opennlp already set up - skipping");
		}
		
		errors = new ArrayList<int[]>();
		int lineNum = 1;
		int charOffset = 0;
		String line;
		ObjectStream<String> lineStream = new PlainTextByLineStream(new StringReader(text));
		try {
			while ((line = lineStream.read()) != null) {
				System.out.println("\nAnalysing line " + lineNum + ":");
				ArrayList<int[]> lineErrors = new ArrayList<int[]>();
				
				for(Error e: Main.ERROR_LIST) {
					System.out.println("looking for: " + e.getClass());
					ArrayList<int[]> temp = e.findErrors(line);
					lineErrors.addAll(temp);
				}
				Error.sort(lineErrors);
				lineErrors = Error.tokensToChars(line, lineErrors, charOffset);
				errors.addAll(lineErrors);
				
				lineNum++;
				charOffset += line.length() + 1;
			}
		} catch (IOException e) {e.printStackTrace();}
		
		System.out.println();
		Error.printErrors(errors, text);
		
		d.close();
		
		if(errors.size() == 0) {
			errorBox.replaceText("No Error Found!");
		} else {
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
		errorBox.replaceText(b.getName() + "\n\n" + b.getDescription() + "\n\n" + b.getExample());
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
	
	/**
	 * This method sets default "empty" text for the Text Areas
	 */
	public void setDefaultText() {
		essayBox.replaceText("Insert Essay Here");
		errorBox.replaceText("No Error Selected");
	}

}