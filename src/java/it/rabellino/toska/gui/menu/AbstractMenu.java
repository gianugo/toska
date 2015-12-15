package it.rabellino.toska.gui.menu;

import it.rabellino.toska.ConfigHandler;
import it.rabellino.toska.gui.Constants;
import it.rabellino.toska.gui.KeysAdmin;
import it.rabellino.toska.gui.KeysMenuBar;
import it.rabellino.toska.gui.KeysTree;
import it.rabellino.toska.gui.Panel;
import java.awt.event.ActionEvent;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.ComponentSelector;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;

/**
 * @author <a href="gianugo@apache.org">Gianugo Rabellino</a>
 *
 */
public abstract class AbstractMenu
  extends JPopupMenu
  implements TreeMenu, Contextualizable, LogEnabled {

  protected DefaultContext context;
  protected Logger logger;
  protected ComponentManager manager;
  protected ComponentSelector panelSelector;
  protected ConfigHandler handler;
  protected String command;
  protected String argument;
  protected DefaultMutableTreeNode currentNode;
  protected KeysMenuBar menuBar;
  protected KeysTree tree;
  protected KeysAdmin mainFrame;

  /**
   * Constructor for KeysMenu.
   */
  public AbstractMenu() {
    super("Key actions");
  }

  /**
   * @see ActionListener#actionPerformed(ActionEvent)
   */
  public void actionPerformed(ActionEvent evt) {

  }

  /**
   * @see Contextualizable#contextualize(Context)
   */
  public void contextualize(Context context) throws ContextException {
    this.context = (DefaultContext)context;
    this.handler = (ConfigHandler) context.get(Constants.CONFIGHANDLER);
    this.tree = (KeysTree) context.get(Constants.KEYS_TREE);
    this.mainFrame = (KeysAdmin) context.get(Constants.MAIN);
    this.menuBar = (KeysMenuBar)context.get(Constants.MENUBAR);
  }

  
  /**
   * @see Composable#compose(ComponentManager)
   */
  public void compose(ComponentManager manager) throws ComponentException {
    this.manager = manager;
    this.panelSelector = 
      (ComponentSelector)manager.lookup(Panel.ROLE + "Selector");  
  }

  /**
   * @see LogEnabled#enableLogging(Logger)
   */
  public void enableLogging(Logger logger) {
    this.logger = logger;
  }

  /**
   * @see TreeMenu#getMenu(DefaultMutableTreeNode)
   */
  public abstract JPopupMenu getMenu(MutableTreeNode node);

  /**
   * Parse and tokenize the command.
   * @param command The command to be parsed.
   */
  public void parseActionCommand(String command) {
    StringTokenizer st = new StringTokenizer(command, ":");

    try {
      this.command = st.nextToken();
      this.argument = st.nextToken();

    } catch (NoSuchElementException nse) {
      JOptionPane.showMessageDialog(
        this,
        "Bad command or argument",
        "Error",
        JOptionPane.ERROR_MESSAGE);
      return;
    }
  }

}
