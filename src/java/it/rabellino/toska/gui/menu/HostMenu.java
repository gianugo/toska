package it.rabellino.toska.gui.menu;

import it.rabellino.toska.Host;
import it.rabellino.toska.User;
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
 * @author <a href="gianugo@apache.org">Gianugo Rabellino</a>
 *
 */
public class HostMenu extends AbstractMenu {

  /**
   * Constructor for HostsMenu.
   */
  public HostMenu() {

  }

  /**
   * @see TreeMenu#getMenu(MutableTreeNode)
   */
  public JPopupMenu getMenu(MutableTreeNode node) {
    currentNode = (DefaultMutableTreeNode)node;
    context.put(Constants.CURRENT_NODE, currentNode);
    Object userObject = currentNode.getUserObject();
    Host currentHost;
    
    if (userObject instanceof Host) {
       JMenuItem addHostMenu = new JMenuItem("Add a new user");
       addHostMenu.setActionCommand("au:placeholder");
       addHostMenu.addActionListener(this);
       this.add(addHostMenu);
       if (!(currentHost = (Host)userObject).getUsers().isEmpty()) {
         JMenu removeUserMenu = new JMenu("Remove user");
           
         Iterator userIterator = currentHost.getUsers().values().iterator();
           
         while (userIterator.hasNext()) {
           User currentUser = (User) userIterator.next();
           JMenuItem currentItem = new JMenuItem(currentUser.getName());
           currentItem.setActionCommand("du:" + currentUser.getName());
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
    
    if (command.equals("au")) {
      try {
        Panel newPanel = (Panel)panelSelector.select("user");
        mainFrame.getSplitPane().setRightComponent((JPanel)newPanel);
      } catch (ComponentException ce) {
        logger.error("User panel not found (should never happen)", ce);
        return;
      }

      
       //new User Panel
    } else if (command.equals("du")) {
      
      int retval = JOptionPane.showConfirmDialog(null,
         "Do you really want to delete user: " + argument + "?\n" +
         "This action can't be undone"); 
      if (retval != JOptionPane.YES_OPTION) return;

      Host currentHost = (Host)currentNode.getUserObject();
      // Delete a user from model
      currentHost.getUsers().remove(argument);
      
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
     
     logger.info("Deleted user: " + argument);
     handler.setChanged(true);
     menuBar.modelChanged();
   }     
    
  }

}
