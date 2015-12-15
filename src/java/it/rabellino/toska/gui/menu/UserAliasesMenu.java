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
public class UserAliasesMenu extends AbstractMenu {
    // Constructor for UserAliasesMenu.
    public UserAliasesMenu() {
    }

    /**
     * @see TreeMenu#getMenu(MutableTreeNode)
     */
    public JPopupMenu getMenu(MutableTreeNode node) {
	currentNode = (DefaultMutableTreeNode)node;
	context.put(Constants.CURRENT_NODE, currentNode);
	Object userObject = currentNode.getUserObject();
	
	if (userObject.equals("UserAliases")) {
	    JMenuItem addUserAliasMenu = new JMenuItem("Add new user alias");
	    addUserAliasMenu.setActionCommand("aH:placeholder");
	    addUserAliasMenu.addActionListener(this);
	    this.add(addUserAliasMenu);
	    logger.debug("UseraAliasesMenu: before checking the getElements.isEmpty");
	    
	    if (!handler.getUserAliases().getElements().isEmpty()) {
		logger.debug("UseraAliasesMenu: checking the getElements.isEmpty");
		// Build the UserAlias list
		JMenu removeUserAliasMenu = new JMenu("Remove user alias");
           
		Iterator userAliasIterator = handler.getUserAliases().getElements().keySet().iterator();
		
		while (userAliasIterator.hasNext()) {
		    Alias currentUserAlias =  handler.getUserAliases().getAlias((String) userAliasIterator.next());
		    JMenuItem currentItem = new JMenuItem(currentUserAlias.getName());
		    currentItem.setActionCommand("dH:" + currentUserAlias.getName());
		    currentItem.addActionListener(this);
		    removeUserAliasMenu.add(currentItem);
		}
		this.add(new JSeparator());
		this.add(removeUserAliasMenu);
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
         Panel newPanel = (Panel)panelSelector.select("userAlias");
         mainFrame.getSplitPane().setRightComponent((JPanel)newPanel);
      } catch (ComponentException ce) {
         logger.error("UserAlias panel not found (should never happen)", ce);
      } catch (ContextException ce) {
         logger.error("Main window not found (should never happen)", ce);
      }
    } else if (command.equals("dH")) {

     int retval = JOptionPane.showConfirmDialog(null,
         "Do you really want to delete user alias: " + argument + "?\n" +
         "This action can't be undone"); 
      if (retval != JOptionPane.YES_OPTION) return;

      
      // Remove from model
      handler.getUserAliases().removeAlias(argument);
      
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
     
     logger.info("Deleted user alias:" + argument);
     handler.setChanged(true);
     menuBar.modelChanged();
    
    }

  }

}
