package bschecker.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import bschecker.reference.Paths;
import javafx.stage.FileChooser;

/**
 * Allows the user to select an essay and to import the text from that file.
 * 
 * @author Julia
 * @author JeremiahDeGreeff
 */
public class TextImport {
	
	/**
	 * Creates a file chooser for the user to select which file to open. Must be called from an event handler.
	 * 
	 * @return The selected file (null if no file is chosen).
	 */
	public static File chooseFile() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose Essay");
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("All Formats", "*.docx", "*.doc", "*.txt"), 
				new FileChooser.ExtensionFilter("Microsoft Word 2007", "*.docx"),
				new FileChooser.ExtensionFilter("Microsoft Word 1997", "*.doc"), 
				new FileChooser.ExtensionFilter("Text File", "*.txt"));
		return fileChooser.showOpenDialog(null);
	}
	
	/**
	 * Takes the selected file and returns the extracted text.
	 * 
	 * @param file The user selected file.
	 * @return The text contained in the file, or null if the file is not one of the accepted types.
	 */
	public static String openFile(File file) {
		switch(file.getName().substring(file.getName().indexOf('.'))) {
		case ".docx": return importDocx(file);
		case ".doc": return importDoc(file);
		case ".txt": return importTxt(file);
		default:
			LogHelper.getLogger(LogHelper.IO).error("Invalid file");
			return null;
		}
	}
	
	/**
	 * Takes the selected word 2007 document and returns the extracted text.
	 * 
	 * @param file The word document selected.
	 * @return The text in the document.
	 */
	private static String importDocx(File file) {
		XWPFDocument doc = null;
		try {
			doc = new XWPFDocument(new FileInputStream(file));
			String text = "";
			List<XWPFParagraph> paragraphs = doc.getParagraphs();
			for(XWPFParagraph p: paragraphs)
				text += p.getText() + "\n";
			doc.close();
			return text;
		}
		catch (IOException e) {
			LogHelper.getLogger(LogHelper.IO).error("Failed to open file.");
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Takes the selected word 97 document and returns the extracted text.
	 * 
	 * @param file The word document selected.
	 * @return The text in the document.
	 */
	private static String importDoc(File file) {
		HWPFDocument doc = null;
		try {
			doc = new HWPFDocument(new FileInputStream(file));
			String text = "";
			Range r = doc.getRange();
			for(int p = 0; p < r.numParagraphs(); p++)
				text += r.getParagraph(p).text();
			doc.close();
			return text;
		}
		catch (IOException e) {
			LogHelper.getLogger(LogHelper.IO).error("Failed to open file.");
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Takes the selected text file and returns the extracted text.
	 * 
	 * @param file The text file selected.
	 * @return The text in the text file.
	 */
	private static String importTxt(File file) {
		Scanner scan = null;
		try {
			scan = new Scanner(file);
			String text = "";
			while(scan.hasNextLine())
				text += scan.nextLine() + "\n";
			scan.close();
			return text;
		}
		catch(FileNotFoundException e) {
			LogHelper.getLogger(LogHelper.IO).error("Failed to open file.");
			e.printStackTrace();
			return null;
		}
	}
	
	
	/**
	 * Allows the user to create a file to save the text.
	 * 
	 * @param text The text to save in the new document.
	 */
	public static File saveAs(String text) {
		FileChooser fc = new FileChooser();
		fc.setTitle("Save Essay");
		fc.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("Microsoft Word 2007", "*.docx"),
				new FileChooser.ExtensionFilter("Microsoft Word 1997", "*.doc"),
				new FileChooser.ExtensionFilter("Text File", "*.txt"));
		File file = fc.showSaveDialog(null);
		if(file != null)
			switch(file.getName().substring(file.getName().indexOf('.'))) {
			case ".docx":
				try {
					XWPFDocument document = new XWPFDocument(); 
					FileOutputStream f = new FileOutputStream(file);  
					document.write(f);
					document.close();
					if(saveDocx(file, text))
						LogHelper.getLogger(LogHelper.IO).info("text successfully saved as " + file.getName());
				} catch (IOException e) {
					LogHelper.getLogger(LogHelper.IO).error("Failed to save file.");
					e.printStackTrace();
				}
				break;
			case ".doc":
				try {
					createEmptyDoc(file);
					if(saveDoc(file, text))
						LogHelper.getLogger(LogHelper.IO).info("text successfully saved as " + file.getName());
				} catch (IOException e) {
					LogHelper.getLogger(LogHelper.IO).error("Failed to save file.");
					e.printStackTrace();
				}	
				break;
			case ".txt":
				if(saveTxt(file, text))
					LogHelper.getLogger(LogHelper.IO).info("text successfully saved as " + file.getName());
				break;
			}
		return file;
	}
	
	/**
	 * Creates an empty .doc file at the specified file path.
	 * 
	 * @param file the location where this file will be created
	 * @throws IOException if the creation fails for any reason
	 */
	private static void createEmptyDoc(File file) throws IOException {
		POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(Paths.EMPTY_DOC));
		HWPFDocument document = new HWPFDocument(fs);
		FileOutputStream f = new FileOutputStream(file);
		document.write(f);
		document.close();
	}
	
	/**
	 * Saves the new text to the file.
	 * 
	 * @param file The file to save to
	 * @param text The text to save.
	 */
	public static boolean saveText(File file, String text) {
		switch(file.getName().substring(file.getName().indexOf('.'))) {
		case ".docx": return saveDocx(file, text);
		case ".doc":
			try {createEmptyDoc(file);}
			catch (IOException e) {
				LogHelper.getLogger(LogHelper.IO).error("Failed to save file.");
				e.printStackTrace();
			}
			return saveDoc(file, text);
		case ".txt": return saveTxt(file, text);
		default: return false;
		}
	}
	
	/**
	 * Saves the new text to the .docx file.
	 * 
	 * @param file The file to save to
	 * @param text The text to save.
	 */
	private static boolean saveDocx(File file, String text) {
		XWPFDocument doc = null;
		try {
			doc = new XWPFDocument(new FileInputStream(file));
			for(int i = doc.getBodyElements().size() - 1; i >= 0; i--)
				doc.removeBodyElement(i);
			String[] lines = text.split("\n");
			for(int i = 0; i < lines.length; i++) {
				XWPFRun r = doc.createParagraph().createRun();
				r.setText(lines[i]);
			}
			doc.write(new FileOutputStream(file));
			doc.close();
			return true;
		}
		catch (IOException e) {
			LogHelper.getLogger(LogHelper.IO).error("Failed to save file.");
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Saves the new text to the .doc file.
	 * 
	 * @param file The file to save to
	 * @param text The text to save.
	 */
	private static boolean saveDoc(File file, String text) {
		HWPFDocument doc = null;
		try {
			doc = new HWPFDocument(new FileInputStream(file));
			Range r = doc.getRange();
			r.insertBefore(text.replaceAll("\n", "\r"));
			doc.write(new FileOutputStream(file));
			doc.close();
			return true;
		}
		catch (IOException e) {
			LogHelper.getLogger(LogHelper.IO).error("Failed to save file.");
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Saves the new text to the txt file.
	 * 
	 * @param file The file to save to
	 * @param text The text to save.
	 */
	private static boolean saveTxt(File file, String text) {
		PrintWriter output = null;
		try {output = new PrintWriter(file);}
		catch (FileNotFoundException e) {
			LogHelper.getLogger(LogHelper.IO).error("Failed to save file - the file could not be found.");
			e.printStackTrace();
			return false;
		}
		
		String[] paragraphs = text.split("\n");
		for(String paragraph: paragraphs)
			output.println(paragraph);
		output.close();
		return true;
	}
	
}
