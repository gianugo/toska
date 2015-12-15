package it.rabellino.toska.gui;

import it.rabellino.toska.Key;
import it.rabellino.toska.Host;
import it.rabellino.toska.User;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * @author <a href="gianugo@apache.org">Gianugo Rabellino</a>
 *
 */
public class KeysTreeCellRenderer extends DefaultTreeCellRenderer {
  
  private ImageIcon hostIcon;
  private ImageIcon userIcon;
  private ImageIcon keysIcon;
  private ImageIcon keyIcon;
  private ImageIcon keyDisabledIcon;
  
  /**
   * Constructor for KeysTreeCellRenderer.
   */
  public KeysTreeCellRenderer() {
  
    super();
    ClassLoader cl = KeysTreeCellRenderer.class.getClassLoader();
    hostIcon = new ImageIcon(cl.getResource("resources/host.gif"));
    userIcon = new ImageIcon(cl.getResource("resources/user.gif"));
    keysIcon  = new ImageIcon(cl.getResource("resources/users.gif"));
    keyIcon  = new ImageIcon(cl.getResource("resources/key.gif"));
    keyDisabledIcon  = new ImageIcon(cl.getResource("resources/disabledkey.gif"));

  }
  
  public Component getTreeCellRendererComponent(
                        JTree tree,
                        Object value,
                        boolean sel,
                        boolean expanded,
                        boolean leaf,
                        int row,
                        boolean hasFocus) {

     super.getTreeCellRendererComponent(
                        tree, value, sel,
                        expanded, leaf, row,
                        hasFocus);
     DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
     
     Object nodeType = node.getUserObject();                   
     if (nodeType instanceof Key) {
       if (((Key)nodeType).isDisabled())
         setIcon(keyDisabledIcon);
       else
         setIcon(keyIcon);
     } else if (nodeType instanceof User) {
       setIcon(userIcon);
     } else if (nodeType instanceof String &&
         ((String)nodeType).equals("Keys")) {
       setIcon(keysIcon);

     } else {
       setIcon(hostIcon);
     }
     
     return this;
  }

 
  

}
