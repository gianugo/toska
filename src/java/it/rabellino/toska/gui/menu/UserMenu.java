package it.rabellino.toska.gui.menu;

import it.rabellino.toska.DuplicateKeyForUserException;
import it.rabellino.toska.Host;
import it.rabellino.toska.Key;
import it.rabellino.toska.User;
import it.rabellino.toska.Alias;
import it.rabellino.toska.KeyAlias;
import it.rabellino.toska.gui.Constants;
import it.rabellino.toska.gui.KeysAdmin;
import it.rabellino.toska.gui.Panel;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Iterator;
import java.lang.Object;

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
 * @author <a href="gianugo@apache.org">Gianugo Rabellino</a>
 *
 */
public class UserMenu extends AbstractMenu {
    
    private User currentUser;
    /**
     * Constructor for HostsMenu.
     */
    public UserMenu() {
	
    }
    
    /**
     * @see TreeMenu#getMenu(MutableTreeNode)
     */
    public JPopupMenu getMenu(MutableTreeNode node) {
	currentNode = (DefaultMutableTreeNode)node;
	context.put(Constants.CURRENT_NODE, currentNode);
	Object userObject = currentNode.getUserObject();
	
	if (userObject instanceof User) {
	    currentUser = (User)userObject;
	    
	    JMenu addMenu = new JMenu("Add a key");
	    
	    Iterator newKeyIterator = handler.getKeys().values().iterator();
	    
	    while (newKeyIterator.hasNext()) {
		Key currentKey = (Key) newKeyIterator.next();
		if (!currentUser.getKeys().containsKey(currentKey.getComment())) {
		    JMenuItem newKeyMenu = new JMenuItem(currentKey.getComment());
		    newKeyMenu.setActionCommand("ak:" + currentKey.getComment());
		    newKeyMenu.addActionListener(this);
		    addMenu.add(newKeyMenu);
		}
	    }
	    Iterator newKeyAliasesIterator = handler.getKeyAliases().getElements().keySet().iterator();
	    while (newKeyAliasesIterator.hasNext()) {
		String keyAliasString = (String) newKeyAliasesIterator.next();
		if (!currentUser.getKeys().containsKey(keyAliasString)) {
		    JMenuItem newKeyAliasMenu = new JMenuItem("Alias:"+keyAliasString);
		    newKeyAliasMenu.setActionCommand("aka:" + keyAliasString);
		    newKeyAliasMenu.addActionListener(this);
		    addMenu.add(newKeyAliasMenu);
		}
	    }
            /*
	     * Here I need to add more Key Aliases, perhaps also a different action Command
	     */
	    this.add(addMenu);
	    
	    if (!(currentUser = (User)userObject).getKeys().isEmpty()) {
		JMenu removeUserMenu = new JMenu("Remove a key");
		
		Iterator userIterator = currentUser.getKeys().values().iterator();
		
		while (userIterator.hasNext()) {
		    Object k = (Object) userIterator.next();
		    Key currentKey = (Key) k;
		    String aliasPrefix = "";
		    try {
			if (k.getClass().isAssignableFrom(Class.forName("it.rabellino.toska.KeyAlias"))) {
			    aliasPrefix = "Alias:";
			}
		    } catch (java.lang.ClassNotFoundException e) {
			System.err.println("Class " + e.toString() + " not found");
		    }
		    JMenuItem currentItem = new JMenuItem(aliasPrefix + currentKey.getComment());
		    currentItem.setActionCommand("dk:" + currentKey.getComment());
		    currentItem.addActionListener(this);
		    removeUserMenu.add(currentItem);
		}
		/*
		 * Here I need to delete more Key Aliases, perhaps also a different action Command
		 */
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
	
        
	if (command.equals("ak")) {
	    
	    Key newKey = (Key)handler.getKeys().get(argument);
	    
	    // Update model
	    try {
		currentUser.addKey(newKey);
	    } catch (DuplicateKeyForUserException dkfe) {
		logger.error("Key " + newKey + " is already defined for this user");
		return;
	    }
	    
	    DefaultMutableTreeNode newNode = 
		new DefaultMutableTreeNode(newKey);
	    
	    currentNode.add(newNode);
	    logger.info("Added key: " + argument);
	    handler.setChanged(true);
	    menuBar.modelChanged();
	    
	    ((DefaultTreeModel)tree.getModel()).nodeStructureChanged(currentNode);
	    
	    return;
	} else if (command.equals("aka")) {
	    
	    KeyAlias newKeyAlias = new KeyAlias(argument);
	    
	    // Update model
	    try {
		currentUser.addKey(newKeyAlias);
	    } catch (DuplicateKeyForUserException dkfe) {
		logger.error("KeyAlias " + newKeyAlias + " is already defined for this user");
		return;
	    }
	    
	    DefaultMutableTreeNode newNode = 
		new DefaultMutableTreeNode(newKeyAlias);
	    
	    currentNode.add(newNode);
	    logger.info("Added keyAlias: " + argument);
	    handler.setChanged(true);
	    menuBar.modelChanged();
	    
	    ((DefaultTreeModel)tree.getModel()).nodeStructureChanged(currentNode);
	    
	    return;
	} else if (command.equals("dk")) {
	    int retval = JOptionPane.showConfirmDialog(null,
						       "Do you really want to delete key: " + argument + "?\n" +
						       "This action can't be undone"); 
	    if (retval != JOptionPane.YES_OPTION) return;
	    
	    // Remove from  model
	    currentUser.getKeys().remove(argument);
	    
	    
	    // Remove from view
	    
	    Enumeration e = currentNode.children();
	    
	    while (e.hasMoreElements()) {
		DefaultMutableTreeNode element = 
		    (DefaultMutableTreeNode) e.nextElement();
		if (element.getUserObject().equals(argument)) {
		    int index = element.getParent().getIndex(element);
		    ((DefaultMutableTreeNode)element.getParent()).remove(index); 
		    
		    ((DefaultTreeModel)tree.getModel()).nodeStructureChanged(currentNode);
		}  
	    }    
	    
	    logger.info("Deleted key: " + argument);
	    handler.setChanged(true);
	    menuBar.modelChanged();
	}   
    }     
}
