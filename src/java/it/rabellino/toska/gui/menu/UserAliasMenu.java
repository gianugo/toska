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
public class UserAliasMenu extends AbstractMenu {
    // Constructor for UserAliasMenu.
    public UserAliasMenu() {
    }
    
    /**
     * @see TreeMenu#getMenu(MutableTreeNode)
     */
    public JPopupMenu getMenu(MutableTreeNode node) {
	Alias currentUserAlias;
	currentNode = (DefaultMutableTreeNode)node;
	context.put(Constants.CURRENT_NODE, currentNode);
	Object userObject = currentNode.getUserObject();
	
	if (userObject instanceof Alias) {
	    JMenuItem addUserAliasMenu = new JMenuItem("Add a new user/UserAlias");
	    addUserAliasMenu.setActionCommand("aG:placeholder");
	    addUserAliasMenu.addActionListener(this);
	    this.add(addUserAliasMenu);
	    logger.debug("UserAliasMenu: Before checking Alias userObject");

	    if (!(currentUserAlias = (Alias)userObject).getElements().isEmpty()) {
		logger.debug("UserAliasMenu: Alias userObject is not empty");
		JMenu removeUserMenu = new JMenu("Remove User/UserAlias");
		
		Iterator userIterator = currentUserAlias.getElements().iterator();
		
		while (userIterator.hasNext()) {
		    String currentUserAliasElement = (String) userIterator.next();
		    JMenuItem currentItem = new JMenuItem(currentUserAliasElement);
		    currentItem.setActionCommand("dG:" + currentUserAliasElement);
		    currentItem.addActionListener(this);
		    removeUserMenu.add(currentItem);
		}
		this.add(new JSeparator());
		this.add(removeUserMenu);          
	    }   
	}
	return this;          
    }
    
    /**
     * @see ActionListener#actionPerformed(ActionEvent)
     */
    public void actionPerformed(ActionEvent evt) {
	parseActionCommand(evt.getActionCommand());
	
	if (command.equals("aG")) {
	    try {
		Panel newPanel = (Panel)panelSelector.select("userAliasElement");
		mainFrame.getSplitPane().setRightComponent((JPanel)newPanel);
	    } catch (ComponentException ce) {
		logger.error("UserAliasElement panel not found (should never happen)", ce);
		return;
	    }
	    //new User Panel
	} else if (command.equals("dG")) {
	    int retval = JOptionPane.showConfirmDialog(null,
						       "Do you really want to delete User/UserAlias: " + argument + "?\n" +
						       "This action can't be undone"); 
	    if (retval != JOptionPane.YES_OPTION) return;
	    
	    Alias currentUserAlias = (Alias)currentNode.getUserObject();
	    // Delete a user from model
	    currentUserAlias.removeElement(argument);
	    
	    // And from view
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
	    logger.info("Deleted User/UserAlias: " + argument);
	    handler.setChanged(true);
	    menuBar.modelChanged();
	}     
    }
}
