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
public class KeyAliasMenu extends AbstractMenu {
    // Constructor for KeyAliasMenu.
    public KeyAliasMenu() {
    }
    
    /**
     * @see TreeMenu#getMenu(MutableTreeNode)
     */
    public JPopupMenu getMenu(MutableTreeNode node) {
	Alias currentKeyAlias;
	currentNode = (DefaultMutableTreeNode)node;
	context.put(Constants.CURRENT_NODE, currentNode);
	Object keyObject = currentNode.getUserObject();
	
	if (keyObject instanceof Alias) {
	    JMenuItem addKeyAliasMenu = new JMenuItem("Add a new key/KeyAlias");
	    addKeyAliasMenu.setActionCommand("aG:placeholder");
	    addKeyAliasMenu.addActionListener(this);
	    this.add(addKeyAliasMenu);
	    logger.debug("KeyAliasMenu: Before checking Alias userObject");

	    if (!(currentKeyAlias = (Alias)keyObject).getElements().isEmpty()) {
		logger.debug("KeyAliasMenu: Alias keyObject is not empty");
		JMenu removeKeyMenu = new JMenu("Remove Key/KeyAlias");
		
		Iterator keyIterator = currentKeyAlias.getElements().iterator();
		
		while (keyIterator.hasNext()) {
		    String currentKeyAliasElement = (String) keyIterator.next();
		    JMenuItem currentItem = new JMenuItem(currentKeyAliasElement);
		    currentItem.setActionCommand("dG:" + currentKeyAliasElement);
		    currentItem.addActionListener(this);
		    removeKeyMenu.add(currentItem);
		}
		this.add(new JSeparator());
		this.add(removeKeyMenu);          
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
		Panel newPanel = (Panel)panelSelector.select("keyAliasElement");
		mainFrame.getSplitPane().setRightComponent((JPanel)newPanel);
	    } catch (ComponentException ce) {
		logger.error("KeyAliasElement panel not found (should never happen)", ce);
		return;
	    }
	    //new Key Panel
	} else if (command.equals("dG")) {
	    int retval = JOptionPane.showConfirmDialog(null,
						       "Do you really want to delete Key/KeyAlias: " + argument + "?\n" +
						       "This action can't be undone"); 
	    if (retval != JOptionPane.YES_OPTION) return;
	    
	    Alias currentKeyAlias = (Alias)currentNode.getUserObject();
	    // Delete a key from model
	    currentKeyAlias.removeElement(argument);
	    
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
	    logger.info("Deleted Key/KeyAlias: " + argument);
	    handler.setChanged(true);
	    menuBar.modelChanged();
	}     
    }
}
