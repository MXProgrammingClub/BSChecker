package BSChecker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

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
	private static String importTxt(File file)
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
	private static String importDoc(File file)
	{
		HWPFDocument doc = null;
		try
		{
			doc = new HWPFDocument(new FileInputStream(file));
		}
		catch (IOException e) //Should never happen
		{
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
	
	/**
	 * Takes the selected word 2007 document and returns the extracted text.
	 * @param file The word document selected.
	 * @return The text in the document.
	 */
	private static String importDocx(File file)
	{
		XWPFDocument doc = null;
		try
		{
			doc = new XWPFDocument(new FileInputStream(file));
		}
		catch (IOException e) //Should never happen
		{
			return null;
		} 
		
		String text = "";
		List<XWPFParagraph> paragraphs = doc.getParagraphs();
		for(XWPFParagraph p: paragraphs)
		{
			text += p.getText() + "\n";
		}
		try
		{
			doc.close();
		} catch (IOException e){} //should never happen
		return text;
	}
	
	/**
	 * Saves the new text to the file.
	 * @param file The file to save to
	 * @param text The text to save.
	 */
	public static void saveText(File file, String text)
	{
		String extension = file.getName().substring(file.getName().indexOf('.'));
		if(extension.equals(".txt"))
		{
			saveTxt(file, text);
		}
		else if(extension.equals(".doc"))
		{
			
		}
		else if(extension.equals(".docx"))
		{
			
		}
		else return; //should never happen
	}
	
	/**
	 * Saves the new text to the txt file.
	 * @param file The file to save to
	 * @param text The text to save.
	 */
	private static void saveTxt(File file, String text)
	{
		PrintWriter output = null;
		try
		{
			output = new PrintWriter(file);
		} catch (FileNotFoundException e){} //shouldn't happen
		String[] paragraphs = text.split("\n");
		for(String paragraph: paragraphs)
		{
			output.println(paragraph);
		}
		output.close();
	}
}