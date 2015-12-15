package it.rabellino.toska.gui.menu;

import java.awt.event.ActionEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.MutableTreeNode;
import org.apache.avalon.framework.component.Component;

/**
 * @author <a href="gianugo@apache.org">Gianugo Rabellino</a>
 *
 */
public class NopMenu 
  extends
    AbstractMenu
  implements 
    TreeMenu,
    Component {

  /**
   * Constructor for NopMenu.
   */
  public NopMenu() {
   
  }

  /**
   * @see TreeMenu#getMenu(DefaultMutableTreeNode)
   */
  public JPopupMenu getMenu(MutableTreeNode node) {
    JMenuItem menu = new JMenuItem("No actions here");
    this.add(menu);
    //this.setVisible(true);
    return this;  
  }

  /**
   * @see ActionListener#actionPerformed(ActionEvent)
   */
  public void actionPerformed(ActionEvent arg0) {
  }

}
