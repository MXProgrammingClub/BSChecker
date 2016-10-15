/**
 * The menu bar for the GUI.
 * 
 * @author Luke Giacalone
 * @version 10/15/2016
 */

package GUI;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public class BSCheckerMenu extends MenuBar {
	
	public BSCheckerMenu()  {
		Menu fileMenu = new Menu("File");
		MenuItem fileOpen = new MenuItem("Open");
		fileOpen.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN));
		fileOpen.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent arg0)
			{
				File file = TextImport.chooseFile();
				if(file == null) return;
				String text = TextImport.openFile(file);
				if(text == null) return;
			}
		});
		fileMenu.getItems().add(fileOpen);
		
		Menu editMenu = new Menu("Edit");
		
		this.getMenus().addAll(fileMenu, editMenu);
	}
	
}
