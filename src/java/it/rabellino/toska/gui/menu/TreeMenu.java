package it.rabellino.toska.gui.menu;

import java.awt.event.ActionListener;

import javax.swing.JPopupMenu;
import javax.swing.tree.MutableTreeNode;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.Composable;

/**
 * The common interface for menu components
 * 
 *  @author <a href="gianugo@apache.org">Gianugo Rabellino</a>
 *
 */
public interface TreeMenu extends 
  Component, 
  Composable,
  ActionListener {
  
  static final String ROLE = "it.rabellino.toska.gui.menu.TreeMenu";
  
  JPopupMenu getMenu(MutableTreeNode node);
  

}
