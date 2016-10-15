package GUI;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

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
	
	public static String importDoc(File file)
	{
		return "";
	}
	
	public static String importDocx(File file)
	{
		return "";
	}
}