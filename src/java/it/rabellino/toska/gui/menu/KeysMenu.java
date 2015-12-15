package it.rabellino.toska.gui.menu;

import it.rabellino.toska.Key;
import it.rabellino.toska.gui.Constants;
import it.rabellino.toska.gui.KeysAdmin;
import it.rabellino.toska.gui.KeysTree;
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
 * @author <a href="gianugo@apache.org">Gianugo Rabellino</a>
 *
 */
public class KeysMenu extends AbstractMenu {
    
  /**
   * Constructor for KeysMenu.
   */
  public KeysMenu() {
    
  }

  /**
   * @see ActionListener#actionPerformed(ActionEvent)
   */
  public void actionPerformed(ActionEvent evt) {
   
   KeysTree tree = null;
   KeysAdmin mainFrame = null;
   this.parseActionCommand(evt.getActionCommand());
   if (logger.isDebugEnabled())
     logger.debug("Performing command: " + command + 
       " with argument " + argument);
   try {
       tree = (KeysTree)context.get(Constants.KEYS_TREE);
       mainFrame = (KeysAdmin)context.get(Constants.MAIN);
       
   } catch (ContextException ce) {
       logger.error("Key tree object not found (should never happen)", ce);
   }
   
   
   if (command.equals("AK")) {
     try {
       Panel newPanel = (Panel)panelSelector.select("key");
       mainFrame.getSplitPane().setRightComponent((JPanel)newPanel);
     } catch (ComponentException ce) {
       logger.error("Key tree object not found (should never happen)", ce);
     }
   
   } else if (command.equals("DK")) {
     int retval = JOptionPane.showConfirmDialog(null,
         "Do you really want to delete key: " + argument + "?\n" +
         "This action can't be undone"); 
     if (retval != JOptionPane.YES_OPTION) return;
     
     // Remove from model
     this.handler.removeKey(argument);
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
     
     logger.info("Deleted key:" + argument);
     handler.setChanged(true);
     menuBar.modelChanged();
          

   } else if (command.equals("dK")) {
     // Disable a key  
     ((Key)this.handler.getKeys().get(argument)).disable();
     ((DefaultTreeModel)tree.getModel()).nodeStructureChanged(currentNode);
     logger.info("Disabled key:" + argument);
     handler.setChanged(true);
   } else if (command.equals("eK")) {
     // Disable a key  
     ((Key)this.handler.getKeys().get(argument)).enable();
     ((DefaultTreeModel)tree.getModel()).nodeStructureChanged(currentNode);
     logger.info("Enabled key:" + argument);
     handler.setChanged(true);
     menuBar.modelChanged();
   }  
   
    
  }

  /**
   * @see TreeMenu#getMenu(DefaultMutableTreeNode)
   */
  public JPopupMenu getMenu(MutableTreeNode node) {
    
    currentNode = (DefaultMutableTreeNode)node;
    context.put(Constants.CURRENT_NODE, currentNode);
    JMenuItem addMenu = new JMenuItem("Add a new key");
    addMenu.setActionCommand("AK:placeholder");
    this.add(addMenu);
    
    addMenu.addActionListener(this);
    
    if (!handler.getKeys().isEmpty()) {
      this.add(new JSeparator());
      JMenu disableMenu = new JMenu("Disable key");
      JMenu enableMenu = new JMenu("Enable key");
      JMenu deleteMenu = new JMenu("Delete key");
      Iterator i = handler.getKeys().values().iterator();
      
      while (i.hasNext()) {
        Key currentKey = (Key) i.next();
        String user = currentKey.getComment();
        
        if (currentKey.isDisabled()) {
          JMenuItem currentKeyMenu = new JMenuItem(user);
          currentKeyMenu.setActionCommand("eK:" + user);
          currentKeyMenu.addActionListener(this);
          enableMenu.add(currentKeyMenu);
        } else {
          JMenuItem currentKeyMenu = new JMenuItem(user);
          currentKeyMenu.setActionCommand("dK:" + user);
          currentKeyMenu.addActionListener(this);
          disableMenu.add(currentKeyMenu);      
        }  
               
        JMenuItem currentKeyMenu = new JMenuItem(user);
        currentKeyMenu.setActionCommand("DK:" + user);
        currentKeyMenu.addActionListener(this);
        deleteMenu.add(currentKeyMenu);
        
      }
      this.add(disableMenu);
      this.add(enableMenu);
      this.add(deleteMenu);
    }      
    
    return this;

  }
  

}
