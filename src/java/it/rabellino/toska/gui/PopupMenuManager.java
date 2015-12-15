package it.rabellino.toska.gui;

import it.rabellino.toska.ConfigHandler;
import it.rabellino.toska.Host;
import it.rabellino.toska.Alias;
import it.rabellino.toska.Key;
import it.rabellino.toska.User;
import it.rabellino.toska.gui.menu.TreeMenu;
import java.util.Iterator;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.ComponentSelector;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;


/**
 * A poor's man pseudo-singleton class to obtain 
 * dynamically a popup menu given the context.
 * 
 * @author <a href="gianugo@apache.org">Gianugo Rabellino</a>
 *
 */
public class PopupMenuManager 
  implements 
    Component, 
    Composable, 
    Contextualizable, 
    LogEnabled {

  public static final String ROLE = "it.rabellino.toska.gui.PopupMenuManager";
  private Context context;
  private ComponentManager manager;
  private ComponentSelector selector;
  private Logger logger;
  
  private static PopupMenuManager m_factory = null;
  private JPopupMenu m_menu;
  
  public PopupMenuManager() {
     m_menu = new JPopupMenu(); 
  }
      
  /* public static synchronized PopupMenuManager getInstance() {
    if (m_factory == null) 
      m_factory = new PopupMenuManager();
      
    return m_factory;  
  }
  */
  
  public JPopupMenu getMenu(DefaultMutableTreeNode node) {
      DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)node.getParent();
      //TreeNode parentNode = node.getParent();
      Object userObject = node.getUserObject();
      Object nodeParentObject = null;
      if (parentNode != null) {
	  nodeParentObject = parentNode.getUserObject();
      } else {
	  logger.error("getMenu: parentNode is null for objectNode. Should never happen");
      }
      logger.debug("getMenu: userObject is of class "+userObject.getClass().getName()+
		   " and the class of the parentObject is of class "+nodeParentObject.getClass().getName());
  
    
      try {
	  if  (userObject instanceof String && 
	       ((String)userObject).equals("Keys")) {
	      m_menu = ((TreeMenu)selector.select("keys")).getMenu(node);
	      
	  } else if (userObject instanceof Key && 
		     ((DefaultMutableTreeNode)node.getParent()).getUserObject().equals("Keys")) {
	      m_menu = ((TreeMenu)selector.select("key")).getMenu(node);
	  } else if (userObject instanceof String && 
		     ((String)userObject).equals("HostAliases")) {
	      m_menu = ((TreeMenu)selector.select("hostAliases")).getMenu(node);         
	  } else if (userObject instanceof String && 
		     ((String)userObject).equals("UserAliases")) {
	      m_menu = ((TreeMenu)selector.select("userAliases")).getMenu(node);         
	  } else if (userObject instanceof String && 
		     ((String)userObject).equals("KeyAliases")) {
	      m_menu = ((TreeMenu)selector.select("keyAliases")).getMenu(node);         
	  } else if (userObject instanceof Alias && 
		     nodeParentObject instanceof String &&
		     ((String)nodeParentObject).equals("HostAliases")) {
	      m_menu = ((TreeMenu)selector.select("hostAlias")).getMenu(node);         
	  } else if (userObject instanceof Alias && 
		     nodeParentObject instanceof String &&
		     ((String)nodeParentObject).equals("UserAliases")) {
	      m_menu = ((TreeMenu)selector.select("userAlias")).getMenu(node);         
	  } else if (userObject instanceof Alias && 
		     nodeParentObject instanceof String &&
		     ((String)nodeParentObject).equals("KeyAliases")) {
	      m_menu = ((TreeMenu)selector.select("keyAlias")).getMenu(node);         
	  } else if (userObject instanceof String && 
		     ((String)userObject).equals("Hosts")) {
	      m_menu = ((TreeMenu)selector.select("hosts")).getMenu(node);         
	  } else if (userObject instanceof Host &&
		     nodeParentObject instanceof String &&
		     ((String)nodeParentObject).equals("Hosts")) {
	      m_menu = ((TreeMenu)selector.select("host")).getMenu(node);
	  } else if (userObject instanceof User) {
	      m_menu = ((TreeMenu)selector.select("user")).getMenu(node);
	  } else {  
	      m_menu = ((TreeMenu)selector.select("nop")).getMenu(node);
	  }
	  
	  
      } catch (ComponentException ce) {
	  logger.error("Error in generating menu: " + ce.getMessage(), ce);
	  
      }
      return m_menu;
  }

  /**
   * @see Composable#compose(ComponentManager)
   */
  public void compose(ComponentManager manager) throws ComponentException {
    this.manager = manager;
    this.selector = (ComponentSelector)manager.lookup(TreeMenu.ROLE + "Selector"); 

  }

  /**
   * @see Contextualizable#contextualize(Context)
   */
  public void contextualize(Context context) throws ContextException {
    this.context = context;
  }

  /**
   * @see LogEnabled#enableLogging(Logger)
   */
  public void enableLogging(Logger logger) {
    this.logger = logger;
  }

}
