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
public class HostAliasesMenu extends AbstractMenu {
    // Constructor for HostAliasesMenu.
    public HostAliasesMenu() {
    }

    /**
     * @see TreeMenu#getMenu(MutableTreeNode)
     */
    public JPopupMenu getMenu(MutableTreeNode node) {
	currentNode = (DefaultMutableTreeNode)node;
	context.put(Constants.CURRENT_NODE, currentNode);
	Object userObject = currentNode.getUserObject();
	
	if (userObject.equals("HostAliases")) {
	    JMenuItem addHostAliasMenu = new JMenuItem("Add new host alias");
	    addHostAliasMenu.setActionCommand("aH:placeholder");
	    addHostAliasMenu.addActionListener(this);
	    this.add(addHostAliasMenu);
	    logger.debug("HostaAliasesMenu: before checking the getElements.isEmpty");
	    
	    if (!handler.getHostAliases().getElements().isEmpty()) {
		logger.debug("HostaAliasesMenu: checking the getElements.isEmpty");
		// Build the HostAlias list
		JMenu removeHostAliasMenu = new JMenu("Remove host alias");
           
		Iterator hostAliasIterator = handler.getHostAliases().getElements().keySet().iterator();
		
		while (hostAliasIterator.hasNext()) {
		    Alias currentHostAlias =  handler.getHostAliases().getAlias((String) hostAliasIterator.next());
		    JMenuItem currentItem = new JMenuItem(currentHostAlias.getName());
		    currentItem.setActionCommand("dH:" + currentHostAlias.getName());
		    currentItem.addActionListener(this);
		    removeHostAliasMenu.add(currentItem);
		}
		this.add(new JSeparator());
		this.add(removeHostAliasMenu);
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
         Panel newPanel = (Panel)panelSelector.select("hostAlias");
         mainFrame.getSplitPane().setRightComponent((JPanel)newPanel);
      } catch (ComponentException ce) {
         logger.error("HostAlias panel not found (should never happen)", ce);
      } catch (ContextException ce) {
         logger.error("Main window not found (should never happen)", ce);
      }
    } else if (command.equals("dH")) {

     int retval = JOptionPane.showConfirmDialog(null,
         "Do you really want to delete host alias: " + argument + "?\n" +
         "This action can't be undone"); 
      if (retval != JOptionPane.YES_OPTION) return;

      
      // Remove from model
      handler.getHostAliases().removeAlias(argument);
      
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
     
     logger.info("Deleted host alias:" + argument);
     handler.setChanged(true);
     menuBar.modelChanged();
    
    }

  }

}
