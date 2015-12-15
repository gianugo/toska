package it.rabellino.toska.gui;

import it.rabellino.toska.ConfigHandler;
import it.rabellino.toska.Host;
import it.rabellino.toska.Aliases;
import it.rabellino.toska.Alias;
import it.rabellino.toska.Key;
import it.rabellino.toska.User;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
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
public class KeysTree 
  extends 
    JTree 
  implements
    Component,
    Composable,
    Contextualizable,
    Initializable, 
    LogEnabled,
    TreeSelectionListener, 
    MouseListener {
    
  public static String ROLE = "it.rabellino.toska.gui.KeysTree";  

  private DefaultMutableTreeNode root;
  private ConfigHandler handler;
  private KeysAdmin admin;
  private DefaultContext context;
  private ComponentManager manager;
  private Logger logger;
  private PopupMenuManager popupFactory;


  /**
   * Empty constructor, as per Avalon specs
   * 
   */
  public KeysTree() {
   super(new DefaultMutableTreeNode("Toska"));

   this.putClientProperty("JTree.lineStyle", "Angled");
   // Shameless hack
   
   this.root = (DefaultMutableTreeNode)getModel().getRoot();
   this.addMouseListener(this);
   
  }

  
  public void populateTree(ConfigHandler handler) {
    this.handler = handler;
    root.removeAllChildren();

    logger.debug("Adding \"Keys\" node to root");
    DefaultMutableTreeNode keysNode = new DefaultMutableTreeNode("Keys"); 
    keysNode.setUserObject(new String("Keys"));
    logger.debug("Adding \"HostAliases\" node to root");
    DefaultMutableTreeNode hostAliasesNode = new DefaultMutableTreeNode("HostAliases");
    hostAliasesNode.setUserObject(new String("HostAliases"));
    logger.debug("Adding \"UserAliases\" node to root");
    DefaultMutableTreeNode userAliasesNode = new DefaultMutableTreeNode("UserAliases");
    userAliasesNode.setUserObject(new String("UserAliases"));
    logger.debug("Adding \"KeyAliases\" node to root");
    DefaultMutableTreeNode keyAliasesNode = new DefaultMutableTreeNode("keyAliases");
    keyAliasesNode.setUserObject(new String("KeyAliases"));
    logger.debug("Adding \"Hosts\" node to root");
    DefaultMutableTreeNode hostsNode = new DefaultMutableTreeNode("Hosts");
    hostsNode.setUserObject(new String("Hosts"));

    Iterator keysIterator;
    if (handler.getKeys() != null) {
      keysIterator = handler.getKeys().values().iterator();
    } else { 
      keysIterator = new Vector().iterator();
    }
      
    while (keysIterator.hasNext()) {
      Key userKey = (Key) keysIterator.next();
      
      DefaultMutableTreeNode userKeyNode = new DefaultMutableTreeNode(userKey);
      keysNode.add(userKeyNode);
    }
    
    Iterator hostAliasesIterator;
    if (handler.getHostAliases() != null) {
	hostAliasesIterator = handler.getHostAliases().getElements().keySet().iterator();
    } else { 
	hostAliasesIterator = new Vector().iterator();
    }
      
    while (hostAliasesIterator.hasNext()) {
	String aliasKey = (String) hostAliasesIterator.next();
	logger.debug("Processing alias "+aliasKey);
	Alias alias = handler.getHostAliases().getAlias(aliasKey);
	DefaultMutableTreeNode aliasNode = new DefaultMutableTreeNode(alias);
	Iterator hostAliasElementIterator = alias.getElements().iterator();
	while (hostAliasElementIterator.hasNext()) {
	    String hostAliasElement = (String)hostAliasElementIterator.next();
	    DefaultMutableTreeNode hostAliasElementNode = new DefaultMutableTreeNode(hostAliasElement);
	    logger.debug("adding to the hostAliasNode <"+aliasNode.toString()+"> element <"+hostAliasElementNode.toString()+">");
	    aliasNode.add(hostAliasElementNode);
	}
	hostAliasesNode.add(aliasNode);
	logger.debug("adding to the hostAliasesNode <"+hostAliasesNode.toString()+"> alias <"+aliasNode.toString()+">");
    }

    Iterator userAliasesIterator;
    if (handler.getUserAliases() != null) {
	userAliasesIterator = handler.getUserAliases().getElements().keySet().iterator();
    } else { 
	userAliasesIterator = new Vector().iterator();
    }
      
    while (userAliasesIterator.hasNext()) {
	String aliasKey = (String) userAliasesIterator.next();
	logger.debug("Processing alias "+aliasKey);
	Alias alias = handler.getUserAliases().getAlias(aliasKey);
	DefaultMutableTreeNode aliasNode = new DefaultMutableTreeNode(alias);
	Iterator userAliasElementIterator = alias.getElements().iterator();
	while (userAliasElementIterator.hasNext()) {
	    String userAliasElement = (String)userAliasElementIterator.next();
	    DefaultMutableTreeNode userAliasElementNode = new DefaultMutableTreeNode(userAliasElement);
	    logger.debug("adding to the userAliasNode <"+aliasNode.toString()+"> element <"+userAliasElementNode.toString()+">");
	    aliasNode.add(userAliasElementNode);
	}
	userAliasesNode.add(aliasNode);
	logger.debug("adding to the userAliasesNode <"+userAliasesNode.toString()+"> alias <"+aliasNode.toString()+">");
    }
    
    Iterator keyAliasesIterator;
    if (handler.getKeyAliases() != null) {
	keyAliasesIterator = handler.getKeyAliases().getElements().keySet().iterator();
    } else { 
	keyAliasesIterator = new Vector().iterator();
    }
      
    while (keyAliasesIterator.hasNext()) {
	String aliasKey = (String) keyAliasesIterator.next();
	logger.debug("Processing alias "+aliasKey);
	Alias alias = handler.getKeyAliases().getAlias(aliasKey);
	DefaultMutableTreeNode aliasNode = new DefaultMutableTreeNode(alias);
	Iterator keyAliasElementIterator = alias.getElements().iterator();
	while (keyAliasElementIterator.hasNext()) {
	    String keyAliasElement = (String)keyAliasElementIterator.next();
	    DefaultMutableTreeNode keyAliasElementNode = new DefaultMutableTreeNode(keyAliasElement);
	    logger.debug("adding to the keyAliasNode <"+aliasNode.toString()+"> element <"+keyAliasElementNode.toString()+">");
	    aliasNode.add(keyAliasElementNode);
	}
	keyAliasesNode.add(aliasNode);
	logger.debug("adding to the keyAliasesNode <"+keyAliasesNode.toString()+"> alias <"+aliasNode.toString()+">");
    }
    
    Iterator hosts;
    
    if (handler.getHosts() != null) {
      hosts = handler.getHosts().values().iterator();
    } else {
      hosts = new HashMap().values().iterator();
    }

    while (hosts.hasNext()) {
      Host host = (Host) hosts.next();
      DefaultMutableTreeNode hostNode = 
        new DefaultMutableTreeNode(host);

      Iterator users = host.getUsers().values().iterator();
      while (users.hasNext()) {
        User user = (User) users.next();
        DefaultMutableTreeNode userNode = 
          new DefaultMutableTreeNode(user);

        hostNode.add(userNode);
            
        Iterator keys = user.getKeys().values().iterator();
        while (keys.hasNext()) {
          Key key = (Key) keys.next();
          if(!key.isDisabled()) {
            DefaultMutableTreeNode keyNode = 
              new DefaultMutableTreeNode(key);
            userNode.add(keyNode);
          }
            
        }
      }
      
      hostsNode.add(hostNode);
        
    }
    root.add(hostAliasesNode);
    root.add(userAliasesNode);
    root.add(keyAliasesNode);
    root.add(keysNode);
    root.add(hostsNode);
    
    ((DefaultTreeModel)this.getModel()).nodeStructureChanged(root);

  }

  /**
   * @see TreeSelectionListener#valueChanged(TreeSelectionEvent)
   */
  public void valueChanged(TreeSelectionEvent tse) {
    DefaultMutableTreeNode node = (DefaultMutableTreeNode)this.getLastSelectedPathComponent();

    Object selected = node.getUserObject();
    
    if (selected instanceof Host) {
      Host host = (Host) selected;
      host.getName();
    }
  }
  
  public void mouseReleased(MouseEvent evt) {
    if (evt.isPopupTrigger()) {
      int x = evt.getX();
      int y = evt.getY();
      TreePath path = getPathForLocation(x, y);
      DefaultMutableTreeNode currentNode = 
        (DefaultMutableTreeNode)path.getLastPathComponent();
      //JPopupMenu menu = new JPopupMenu();
     
      
      JPopupMenu menu = popupFactory.getMenu(currentNode);         
      menu.show(this, x, y);

      
    }
  
  
  }
  
  public void mouseClicked(MouseEvent evt) {}
  
  public void mousePressed(MouseEvent evt) {
    if (evt.isPopupTrigger()) {
      int x = evt.getX();
      int y = evt.getY();
      TreePath path = getPathForLocation(x, y);
      DefaultMutableTreeNode currentNode = 
        (DefaultMutableTreeNode)path.getLastPathComponent();
      //JPopupMenu menu = new JPopupMenu();
     
      
      JPopupMenu menu = popupFactory.getMenu(currentNode);         
      menu.show(this, x, y);

      
    }
  
  
  }

  public void mouseEntered(MouseEvent evt) {}
    
  public void mouseExited(MouseEvent evt) {}

  /**
   * @see Contextualizable#contextualize(Context)
   */
  public void contextualize(Context context) throws ContextException {
    this.context = (DefaultContext)context;
    this.context.put(Constants.KEYS_TREE, this);
  }

  /**
   * @see Initializable#initialize()
   */
  public void initialize() throws Exception {
    popupFactory = (PopupMenuManager)manager.lookup(PopupMenuManager.ROLE);
    this.setCellRenderer(new KeysTreeCellRenderer());
    this.populateTree((ConfigHandler)context.get(Constants.CONFIGHANDLER));
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
  }

}
