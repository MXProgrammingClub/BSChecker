/**
 * The menu bar for the GUI.
 * 
 * @author Luke Giacalone
 * @version 10/15/2016
 */

package GUI;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class BSCheckerMenu extends MenuBar {
	
	public BSCheckerMenu()  {
		Menu fileMenu = new Menu("File");
		MenuItem fileOpen = new MenuItem("Open");
		fileMenu.getItems().add(fileOpen);
		
		Menu editMenu = new Menu("Edit");
		
		this.getMenus().addAll(fileMenu, editMenu);
	}
	
}
