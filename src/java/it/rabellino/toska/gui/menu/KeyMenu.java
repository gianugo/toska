package it.rabellino.toska.gui.menu;

import it.rabellino.toska.Key;
import it.rabellino.toska.gui.Constants;
import it.rabellino.toska.gui.KeysAdmin;
import it.rabellino.toska.gui.KeysTree;
import java.awt.event.ActionEvent;
import java.util.Enumeration;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import org.apache.avalon.framework.context.ContextException;

/**
 * @author <a href="gianugo@apache.org">Gianugo Rabellino</a>
 *
 */
public class KeyMenu extends AbstractMenu {

  private Key currentKey;
    
  /**
   * Constructor for KeysMenu.
   */
  public KeyMenu() {
    
  }

  /**
   * @see ActionListener#actionPerformed(ActionEvent)
   */
  public void actionPerformed(ActionEvent evt) {
   
   KeysTree tree = null;
   KeysAdmin mainFrame = null;
   this.parseActionCommand(evt.getActionCommand());
   try {
       tree = (KeysTree)context.get(Constants.KEYS_TREE);
       mainFrame = (KeysAdmin)context.get(Constants.MAIN);
       
   } catch (ContextException ce) {
       logger.error("Key tree object not found (should never happen)", ce);
   }
   
   
   if (command.equals("DK")) {
     // Remove from model
     int retval = JOptionPane.showConfirmDialog(null,
         "Do you really want to delete key: " + argument + "?\n" +
         "This action can't be undone"); 
     if (retval != JOptionPane.YES_OPTION) return;

     this.handler.removeKey(argument);
     TreeNode parent = currentNode.getParent();
     Enumeration e = parent.children();

     // Remove from view
     while (e.hasMoreElements()) {
       DefaultMutableTreeNode element = 
         (DefaultMutableTreeNode) e.nextElement();
       if (element.getUserObject().equals(argument)) {
         int index = element.getParent().getIndex(element);
         ((DefaultMutableTreeNode)element.getParent()).remove(index); 
       
         ((DefaultTreeModel)tree.getModel()).nodeStructureChanged(parent);
       }  
     }    
     
     logger.info("Deleted key:" + argument);
     handler.setChanged(true);
     menuBar.modelChanged();
          
   } else if (command.equals("dK")) {
     // Disable a key  
     ((Key)this.handler.getKeys().get(argument)).disable();
     ((DefaultTreeModel)tree.getModel()).nodeStructureChanged(currentNode.getParent());
     logger.info("Disabled key:" + argument);
     handler.setChanged(true);
     menuBar.modelChanged();
   } else if (command.equals("eK")) {
     // Disable a key  
     ((Key)this.handler.getKeys().get(argument)).enable();
     ((DefaultTreeModel)tree.getModel()).nodeStructureChanged(currentNode.getParent());
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
    Object userObject = currentNode.getUserObject();
    
    if (userObject instanceof Key) {
      currentKey = (Key)userObject;
      if (currentKey.isDisabled()) {
        JMenuItem enableMenu = new JMenuItem("Enable key");
        enableMenu.setActionCommand("eK:" + currentKey.getComment());
        enableMenu.addActionListener(this);
        this.add(enableMenu);
      } else {
        JMenuItem disableMenu = new JMenuItem("Disable key");
        disableMenu.setActionCommand("dK:" + currentKey.getComment());
        disableMenu.addActionListener(this);
        this.add(disableMenu);
      }   
      JMenuItem deleteMenu = new JMenuItem("Delete key");
      deleteMenu.setActionCommand("DK:" + currentKey.getComment());
      deleteMenu.addActionListener(this);
      this.add(deleteMenu);
    }      
    
    return this;

  }
  

}
