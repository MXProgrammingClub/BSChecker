package main.java.bschecker.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.PAPX;
import org.apache.poi.hwpf.sprm.SprmBuffer;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
//import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import javafx.stage.FileChooser;

/**
 * Methods for the user to select an essay and to import the text from that file.
 * @author Julia
 */
public class TextImport {
	/**
	 * Creates a file chooser for the user to select which file to open. Must be called from an event handler.
	 * @return The selected file (null if no file is chosen).
	 */
	public static File chooseFile() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose Essay");
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Formats", "*.docx", "*.doc", "*.txt"), 
				new FileChooser.ExtensionFilter("Microsoft Word 2007", "*.docx"), new FileChooser.ExtensionFilter("Microsoft Word 1997", "*.doc"), 
				new FileChooser.ExtensionFilter("Text File", "*.txt"));
		return fileChooser.showOpenDialog(null);
	}
	
	/**
	 * Takes the selected file and returns the extracted text.
	 * @param file The user selected file.
	 * @return The text contained in the file, or null if the file is not one of the accepted types.
	 */
	public static String openFile(File file) {
		String extension = file.getName().substring(file.getName().indexOf('.'));
		if(extension.equals(".txt")) return importTxt(file);
		else if(extension.equals(".doc")) return importDoc(file);
		else if(extension.equals(".docx")) return importDocx(file);
		else return null;
	}
	
	/**
	 * Takes the selected text file and returns the extracted text.
	 * @param file The text file selected.
	 * @return The text in the text file.
	 */
	private static String importTxt(File file) {
		Scanner scan = null;
		try {
			scan = new Scanner(file);
		} catch(FileNotFoundException e) {return "";}
		
		String text = "";
		while(scan.hasNextLine())
			text += scan.nextLine() + "\n";
		
		scan.close();
		return text;
	}
	
	/**
	 * Takes the selected word 97 document and returns the extracted text.
	 * @param file The word document selected.
	 * @return The text in the document.
	 */
	private static String importDoc(File file) {
		HWPFDocument doc = null;
		try {
			doc = new HWPFDocument(new FileInputStream(file));
		} catch (IOException e) {return null;} //Should never happen
		
		String text = "";
		Range r = doc.getRange();
		for(int p = 0; p < r.numParagraphs(); p++)
			text += r.getParagraph(p).text();
		return text;
	}
	
	/**
	 * Takes the selected word 2007 document and returns the extracted text.
	 * @param file The word document selected.
	 * @return The text in the document.
	 */
	private static String importDocx(File file) {
		XWPFDocument doc = null;
		try {
			doc = new XWPFDocument(new FileInputStream(file));
		} catch (IOException e) {return null;} //Should never happen
		
		String text = "";
		List<XWPFParagraph> paragraphs = doc.getParagraphs();
		for(XWPFParagraph p: paragraphs)
			text += p.getText() + "\n";
		
		try {
			doc.close();
		} catch (IOException e){} //Should never happen
		return text;
	}
	
	/**
	 * Allows the user to create a file to save the text.
	 * @param text The text to save in the new document.
	 */
	public static void saveAs(String text) {
		FileChooser fc = new FileChooser();
		fc.setTitle("Save Essay");
		fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Microsoft Word 2007", "*.docx"), //new FileChooser.ExtensionFilter("Microsoft Word 1997", "*.doc"), 
				new FileChooser.ExtensionFilter("Text File", "*.txt"));
		File file = fc.showSaveDialog(null);
		if(file != null) {
			String extension = file.getName().substring(file.getName().indexOf('.'));
			if(extension.equals(".txt"))
				saveTxt(file, text);
//			else if(extension.equals(".doc"))
//				try {
//					FileOutputStream f = new FileOutputStream(file); 
//					POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
//					HWPFDocument doc = new HWPFDocument(fs);
//					saveDoc(file, text);
//				} catch (IOException e) {e.printStackTrace();}
			else if(extension.equals(".docx"))
				try {	
					XWPFDocument document = new XWPFDocument(); 
					FileOutputStream f = new FileOutputStream(file);  
					document.write(f);
					document.close();
					f.close();
					saveDocx(file, text);
				} catch (IOException e) {e.printStackTrace();}
		}
	}
	
	/**
	 * Saves the new text to the file.
	 * @param file The file to save to
	 * @param text The text to save.
	 */
	public static boolean saveText(File file, String text) {
		String extension = file.getName().substring(file.getName().indexOf('.'));
		if(extension.equals(".txt"))
			return saveTxt(file, text);
		else if(extension.equals(".doc"))
			return saveDoc(file, text);
		else if(extension.equals(".docx"))
			return saveDocx(file, text);
		else return false; //should never happen
	}
	
	/**
	 * Saves the new text to the txt file.
	 * @param file The file to save to
	 * @param text The text to save.
	 */
	private static boolean saveTxt(File file, String text) {
		PrintWriter output = null;
		try {
			output = new PrintWriter(file);
		} catch (FileNotFoundException e) {return false;}
		
		String[] paragraphs = text.split("\n");
		for(String paragraph: paragraphs)
			output.println(paragraph);
		output.close();
		return true;
	}
	
	/**
	 * Saves the new text to the .doc file.
	 * @param file The file to save to
	 * @param text The text to save.
	 */
	private static boolean saveDoc(File file, String text) {
		HWPFDocument doc = null;
		try {
			doc = new HWPFDocument(new FileInputStream(file));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		Range r = doc.getRange();
		for(int i = r.numParagraphs() - 1; i >= 0; i--)
			r.getParagraph(i).delete();
		
		String[] lines = text.split("\n");
		for(int i = 0; i < lines.length; i++) {
			Paragraph p = Paragraph.newParagraph(r, new PAPX(0, lines[i].length(), new SprmBuffer(0)));
			if(i == 0)
				p.insertBefore(lines[i]);
			else
				p.insertAfter(lines[i]);
		}
		
		try {
			doc.write(new FileOutputStream(file));
		} catch(IOException e) {return false;}
		return true;
	}
	
	/**
	 * Saves the new text to the .docx file.
	 * @param file The file to save to
	 * @param text The text to save.
	 */
	private static boolean saveDocx(File file, String text) {
		XWPFDocument doc = null;
		try {
			doc = new XWPFDocument(new FileInputStream(file));
		}catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		//List<XWPFParagraph> paragraphs = doc.getParagraphs();
		for(int i = doc.getBodyElements().size() - 1; i >= 0; i--)
			doc.removeBodyElement(i);
		String[] lines = text.split("\n");
		for(int i = 0; i < lines.length; i++) {
			XWPFRun r = doc.createParagraph().createRun();
			r.setText(lines[i]);
		}
		
		try {
			doc.write(new FileOutputStream(file));
			doc.close();
		} catch(IOException e) {return false;}
		return true;
	}
}