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
public class HostAliasMenu extends AbstractMenu {
    // Constructor for HostAliasMenu.
    public HostAliasMenu() {
    }
    
    /**
     * @see TreeMenu#getMenu(MutableTreeNode)
     */
    public JPopupMenu getMenu(MutableTreeNode node) {
	Alias currentHostAlias;
	currentNode = (DefaultMutableTreeNode)node;
	context.put(Constants.CURRENT_NODE, currentNode);
	Object userObject = currentNode.getUserObject();
	
	if (userObject instanceof Alias) {
	    JMenuItem addHostAliasMenu = new JMenuItem("Add a new host/HostAlias");
	    addHostAliasMenu.setActionCommand("aG:placeholder");
	    addHostAliasMenu.addActionListener(this);
	    this.add(addHostAliasMenu);
	    logger.debug("HostAliasMenu: Before checking Alias userObject");

	    if (!(currentHostAlias = (Alias)userObject).getElements().isEmpty()) {
		logger.debug("HostAliasMenu: Alias userObject is not empty");
		JMenu removeHostMenu = new JMenu("Remove Host/HostAlias");
		
		Iterator hostIterator = currentHostAlias.getElements().iterator();
		
		while (hostIterator.hasNext()) {
		    String currentHostAliasElement = (String) hostIterator.next();
		    JMenuItem currentItem = new JMenuItem(currentHostAliasElement);
		    currentItem.setActionCommand("dG:" + currentHostAliasElement);
		    currentItem.addActionListener(this);
		    removeHostMenu.add(currentItem);
		}
		this.add(new JSeparator());
		this.add(removeHostMenu);          
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
		Panel newPanel = (Panel)panelSelector.select("hostAliasElement");
		mainFrame.getSplitPane().setRightComponent((JPanel)newPanel);
	    } catch (ComponentException ce) {
		logger.error("HostAliasElement panel not found (should never happen)", ce);
		return;
	    }
	    //new Host Panel
	} else if (command.equals("dG")) {
	    int retval = JOptionPane.showConfirmDialog(null,
						       "Do you really want to delete Host/HostAlias: " + argument + "?\n" +
						       "This action can't be undone"); 
	    if (retval != JOptionPane.YES_OPTION) return;
	    
	    Alias currentHostAlias = (Alias)currentNode.getUserObject();
	    // Delete a host from model
	    currentHostAlias.removeElement(argument);
	    
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
	    logger.info("Deleted Host/HostAlias: " + argument);
	    handler.setChanged(true);
	    menuBar.modelChanged();
	}     
    }
}
