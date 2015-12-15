package it.rabellino.toska.gui.menu;

import it.rabellino.toska.Host;
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
public class HostsMenu extends AbstractMenu {

  /**
   * Constructor for HostsMenu.
   */
  public HostsMenu() {

  }

  /**
   * @see TreeMenu#getMenu(MutableTreeNode)
   */
  public JPopupMenu getMenu(MutableTreeNode node) {
    currentNode = (DefaultMutableTreeNode)node;
    context.put(Constants.CURRENT_NODE, currentNode);
    Object userObject = currentNode.getUserObject();
    
    if (userObject.equals("Hosts")) {
       JMenuItem addHostMenu = new JMenuItem("Add new host");
       addHostMenu.setActionCommand("ah:placeholder");
       addHostMenu.addActionListener(this);
       this.add(addHostMenu);
       
       if (!handler.getHosts().isEmpty()) {
           // Build the host list
           JMenu removeHostMenu = new JMenu("Remove host");
           
           Iterator hostIterator = handler.getHosts().values().iterator();
           
           while (hostIterator.hasNext()) {
              Host currentHost = (Host) hostIterator.next();
              JMenuItem currentItem = new JMenuItem(currentHost.getName());
              currentItem.setActionCommand("dh:" + currentHost.getName());
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
    KeysAdmin mainFrame = null;
    parseActionCommand(evt.getActionCommand());
    if (command.equals("ah")) {
      try {
         mainFrame = (KeysAdmin)context.get(Constants.MAIN); 
         Panel newPanel = (Panel)panelSelector.select("host");
         mainFrame.getSplitPane().setRightComponent((JPanel)newPanel);
      } catch (ComponentException ce) {
         logger.error("Host panel not found (should never happen)", ce);
      } catch (ContextException ce) {
         logger.error("Main window not found (should never happen)", ce);
      }
    } else if (command.equals("dh")) {

     int retval = JOptionPane.showConfirmDialog(null,
         "Do you really want to delete host: " + argument + "?\n" +
         "This action can't be undone"); 
      if (retval != JOptionPane.YES_OPTION) return;

      
      // Remove from model
      handler.getHosts().remove(argument);
      
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
     
     logger.info("Deleted host:" + argument);
     handler.setChanged(true);
     menuBar.modelChanged();

      
      
    
    }

  }

}
