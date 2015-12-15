package it.rabellino.toska.gui;

import it.rabellino.toska.ConfigHandler;
import java.awt.event.ActionEvent;

import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.ComponentSelector;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;

/**
 * @author <a href="gianugo@apache.org">Gianugo Rabellino</a>
 *
 */
public abstract class AbstractPanel 
  extends
    JPanel
  implements 
    Panel, 
    Contextualizable,
    LogEnabled {
    
  protected Context context;
  protected ComponentManager manager;
  protected ComponentSelector panelSelector;
  protected ConfigHandler handler;
  protected Logger logger;
  protected String command;
  protected String argument;
  protected DefaultMutableTreeNode currentNode;
  protected KeysAdmin mainFrame;
  protected KeysTree tree;
  protected KeysMenuBar menuBar;
  
  

  /**
   * @see ActionListener#actionPerformed(ActionEvent)
   */
  public void actionPerformed(ActionEvent evt) {
    
  }

  /**
   * @see Contextualizable#contextualize(Context)
   */
  public void contextualize(Context context) throws ContextException {
    this.context = context;
    this.handler = (ConfigHandler)context.get(Constants.CONFIGHANDLER);
    this.mainFrame = (KeysAdmin)context.get(Constants.MAIN);
    this.currentNode = (DefaultMutableTreeNode)context.get(Constants.CURRENT_NODE);
    this.tree = (KeysTree)context.get(Constants.KEYS_TREE);
    this.menuBar = (KeysMenuBar)context.get(Constants.MENUBAR);
  }
  
  
  /**
   * @see LogEnabled#enableLogging(Logger)
   */
  public void enableLogging(Logger logger) {
    this.logger = logger;
  }
    

  /**
   * @see Composable#compose(ComponentManager)
   */
  public void compose(ComponentManager manager) throws ComponentException {
    this.manager = manager;
    this.panelSelector = 
      (ComponentSelector)manager.lookup(Panel.ROLE + "Selector");  
  }

}
