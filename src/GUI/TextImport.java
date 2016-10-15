package GUI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;

import javafx.stage.FileChooser;

/**
 * Methods for the user to select an essay and to import the text from that file.
 * @author Julia
 */
public class TextImport
{	
	/**
	 * Creates a file chooser for the user to select which file to open. Must be called from an event handler.
	 * @return The selected file (null if no file is chosen).
	 */
	public static File chooseFile()
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose Essay");
		return fileChooser.showOpenDialog(null);
	}
	
	/**
	 * Takes the selected file and returns the extracted text.
	 * @param file The user selected file.
	 * @return The text contained in the file, or null if the file is not one of the accepted types.
	 */
	public static String openFile(File file)
	{
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
	public static String importTxt(File file)
	{
		Scanner scan = null;
		try
		{
			scan = new Scanner(file);
		}
		catch(FileNotFoundException e)
		{
			return "";
		}
		
		String text = "";
		while(scan.hasNextLine())
		{
			text += scan.nextLine() + "\n";
		}
		
		scan.close();
		return text;
	}
	
	/**
	 * Takes the selected word 97 document and returns the extracted text.
	 * @param file The word document selected.
	 * @return The text in the document.
	 */
	public static String importDoc(File file)
	{
		HWPFDocument doc = null;
		try
		{
			doc = new HWPFDocument(new FileInputStream(file));
		}
		catch (IOException e) //Should never happen
		{
			e.printStackTrace();
			return null;
		} 
		
		String text = "";
		Range r = doc.getRange();
		for(int p = 0; p < r.numParagraphs(); p++)
		{
			text += r.getParagraph(p).text();
		}
		return text;
	}
	
	public static String importDocx(File file)
	{
		return "";
	}
}