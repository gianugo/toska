package it.rabellino.toska.gui.menu;

import it.rabellino.toska.Aliases;
import it.rabellino.toska.Alias;
import it.rabellino.toska.gui.Constants;
import it.rabellino.toska.gui.KeysAdmin;
import it.rabellino.toska.gui.Panel;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.context.ContextException;

/**
 * @author <a href="Nito@Qindel.ES">Nito Martinez</a>
 *
 */
public class KeyAliasesMenu extends AbstractMenu {
    // Constructor for KeyAliasesMenu.
    public KeyAliasesMenu() {
    }

    /**
     * @see TreeMenu#getMenu(MutableTreeNode)
     */
    public JPopupMenu getMenu(MutableTreeNode node) {
	currentNode = (DefaultMutableTreeNode)node;
	context.put(Constants.CURRENT_NODE, currentNode);
	Object keyObject = currentNode.getUserObject();
	
	if (keyObject.equals("KeyAliases")) {
	    JMenuItem addKeyAliasMenu = new JMenuItem("Add new key alias");
	    addKeyAliasMenu.setActionCommand("aH:placeholder");
	    addKeyAliasMenu.addActionListener(this);
	    this.add(addKeyAliasMenu);
	    logger.debug("KeyaAliasesMenu: before checking the getElements.isEmpty");
	    
	    if (!handler.getKeyAliases().getElements().isEmpty()) {
		logger.debug("KeyaAliasesMenu: checking the getElements.isEmpty");
		// Build the KeyAlias list
		JMenu removeKeyAliasMenu = new JMenu("Remove key alias");
           
		Iterator keyAliasIterator = handler.getKeyAliases().getElements().keySet().iterator();
		
		while (keyAliasIterator.hasNext()) {
		    Alias currentKeyAlias =  handler.getKeyAliases().getAlias((String) keyAliasIterator.next());
		    JMenuItem currentItem = new JMenuItem(currentKeyAlias.getName());
		    currentItem.setActionCommand("dH:" + currentKeyAlias.getName());
		    currentItem.addActionListener(this);
		    removeKeyAliasMenu.add(currentItem);
		}
		this.add(new JSeparator());
		this.add(removeKeyAliasMenu);
	    }
	}
	
	return this;          
    }

  /**
   * @see ActionListener#actionPerformed(ActionEvent)
   */
  public void actionPerformed(ActionEvent evt) {
    KeysAdmin mainFrame = null;
    parseActionCommand(evt.getActionCommand());
    if (command.equals("aH")) {
      try {
         mainFrame = (KeysAdmin)context.get(Constants.MAIN); 
         Panel newPanel = (Panel)panelSelector.select("keyAlias");
         mainFrame.getSplitPane().setRightComponent((JPanel)newPanel);
      } catch (ComponentException ce) {
         logger.error("KeyAlias panel not found (should never happen)", ce);
      } catch (ContextException ce) {
         logger.error("Main window not found (should never happen)", ce);
      }
    } else if (command.equals("dH")) {

     int retval = JOptionPane.showConfirmDialog(null,
         "Do you really want to delete key alias: " + argument + "?\n" +
         "This action can't be undone"); 
      if (retval != JOptionPane.YES_OPTION) return;

      
      // Remove from model
      handler.getKeyAliases().removeAlias(argument);
      
      Enumeration e = currentNode.children();

      // Remove from view
      while (e.hasMoreElements()) {
        DefaultMutableTreeNode element = 
          (DefaultMutableTreeNode) e.nextElement();
        if (element.getUserObject().equals(argument)) {
          int index = element.getParent().getIndex(element);
          ((DefaultMutableTreeNode)element.getParent()).remove(index); 
       
          ((DefaultTreeModel)tree.getModel()).nodeStructureChanged(currentNode);
        }  
      }    
     
     logger.info("Deleted key alias:" + argument);
     handler.setChanged(true);
     menuBar.modelChanged();
    
    }

  }

}
