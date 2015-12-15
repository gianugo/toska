package it.rabellino.toska.gui;

import it.rabellino.toska.ConfigHandler;
import it.rabellino.toska.XMLConfigHandler;
import java.awt.BorderLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.avalon.excalibur.component.DefaultRoleManager;
import org.apache.avalon.excalibur.component.ExcaliburComponentManager;
import org.apache.avalon.excalibur.logger.LogKitLoggerManager;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;

import org.apache.log.Hierarchy;
import org.apache.log.Priority;
import org.apache.log.format.PatternFormatter;
import org.apache.log.output.io.StreamTarget;


import org.xml.sax.InputSource;


/**
 * This is the main class for the GUI version of Toska.
 * 
 * @author <a href="gianugo@apache.org">Gianugo Rabellino</a>
 *
 */

public class KeysAdmin 
  extends JFrame  
  implements 
    Component, 
    Composable,
    Configurable,
    Contextualizable,
    Initializable, 
    LogEnabled {


  public static final String APP_NAME = "Toska";
  public static final String APP_FULLNAME = " Tool for OpenSsh Key Administration";
  public static final String APP_VERSION = "0.5";
  
  protected Logger logger;
  protected ExcaliburComponentManager manager;
  protected Configuration config;
  protected DefaultContext context;
  
  // Logger kludge. Avalon people, usque tandem?
  
  protected org.apache.log.Logger oldLogger;
  
  private ConfigHandler handler;
  private DefaultMutableTreeNode root = new DefaultMutableTreeNode("Configuration");
  private KeysTree keysTree;
  private JSplitPane splitPane;
  private TextAreaOutputStream taos;
  
  
  public KeysAdmin() {
        
  }
  

    
  public void compose(ComponentManager manager) {
    this.manager = (ExcaliburComponentManager)manager;  
  }
  
  public void configure(Configuration config) {
    this.config = config; 
  }
  

  
  public void enableLogging(Logger logger) {
    this.logger = logger;
  
  }
  
  public void open(File fileName) {
    try {      
       context.put(Constants.DATASTREAM,
            new FileInputStream(fileName));
      ((XMLConfigHandler)handler).marshal(); 
      KeysMenuBar menuBar = (KeysMenuBar)context.get(Constants.MENUBAR);
      menuBar.getSaveMenu().setEnabled(true);
      menuBar.getDeployMenu().setEnabled(true);
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Problem opening " + 
         fileName.getName() + ": " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      e.printStackTrace(System.err);
      return;   
    }
          
    keysTree.populateTree(handler);

  }  
  
  public ConfigHandler getConfigHandler() {
    return handler;
  }    

  public void setConfigHandler(ConfigHandler handler) {
    if (this.handler.hasChanged()) {
      int retval = JOptionPane.showConfirmDialog(null,
        "Some data have been changed:\n" +
        "Proceed anyway?"); 
      if (retval != JOptionPane.YES_OPTION) return;
    }
    this.handler = handler;
    context.put(Constants.CONFIGHANDLER, handler);
    keysTree.populateTree(handler);
  }
  
  public void populateTree() {
    keysTree.populateTree(handler);
  }  
 

  public void save(File toFile) {
    this.context.put(Constants.DATASINK, toFile);
    ((XMLConfigHandler)handler).unmarshal();
    logger.info("Succesfully saved: " + toFile.toString());
  }
  
    /** Initialize the application */
  public void initialize() {

    this.initLogger(); 

    DefaultRoleManager drm = new DefaultRoleManager();
    try {
      DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();

      InputStream is = 
          Thread.currentThread().getContextClassLoader().getResource("it/rabellino/toska/toska.roles").openStream();

      Configuration roleConfig = builder.build(is);
      drm.setLogger(oldLogger);
      //drm.enableLogging(logger);

      drm.configure(roleConfig);
      
      
      ((ExcaliburComponentManager)manager).setLogger(oldLogger);
      
      manager.setRoleManager(drm);
      
      manager.configure(config);
      manager.initialize();

  
      handler = (ConfigHandler)manager.lookup(ConfigHandler.ROLE);
      context.put(Constants.CONFIGHANDLER, handler);
      // Main window
      this.getContentPane().setLayout(new BorderLayout());
      this.setTitle(APP_FULLNAME); 
      ClassLoader cl = KeysAdmin.class.getClassLoader();    
      this.setIconImage(new ImageIcon(cl.getResource("resources/toska16.jpg")).getImage());   
      
      // Log window
      this.getContentPane().add(taos.getPane(), BorderLayout.SOUTH);
      
      // Menu bar
      KeysMenuBar menuBar = (KeysMenuBar)manager.lookup(KeysMenuBar.ROLE);
      this.getContentPane().add(menuBar, BorderLayout.NORTH);
      menuBar.getSaveMenu().setEnabled(false);
      menuBar.getDeployMenu().setEnabled(false);
      menuBar.getDistributeMenu().setEnabled(false);
       
      
      // Configuration tree
      keysTree = (KeysTree)manager.lookup(KeysTree.ROLE);

      //Main split pane      
      
      splitPane = new JSplitPane();
      
      splitPane.setLeftComponent(keysTree);
      splitPane.setRightComponent(new JLabel(new ImageIcon(cl.getResource("resources/toska.jpg"))));
      splitPane.setDividerLocation(250);   
      
      this.getContentPane().add(splitPane, BorderLayout.CENTER);
      this.getContentPane().setSize(800, 500);
      this.setSize(800,500);
      this.setVisible(true);        

    }  catch (Exception e) { 
      this.logger.error("Error initializing the application: ", e);
      e.printStackTrace(System.err);
    }
 
  }  


  public void initLogger() { 
    taos = new TextAreaOutputStream();
    try {
      final Hierarchy hierarchy = new Hierarchy();
      PatternFormatter formatter = 
        new PatternFormatter("[%-10.10{category}]: %{message}\n");
      
      hierarchy.setDefaultLogTarget(
        new StreamTarget(taos, formatter));
      hierarchy.setDefaultPriority(Priority.ERROR);  
      oldLogger = hierarchy.getLoggerFor("toska");  
      
      
      final ClassLoader cl = KeysAdmin.class.getClassLoader();
      final InputStream is = 
        cl.getResourceAsStream("it/rabellino/toska/logkit.xconf");
      
      final DefaultConfigurationBuilder builder =
        new DefaultConfigurationBuilder();
      Configuration config = builder.build(is);          
  
      final LogKitLoggerManager lkManager = 
        new LogKitLoggerManager(hierarchy);
 
      DefaultContext context = new DefaultContext();
      
      context.put("console", taos);
      
      lkManager.contextualize(context);
      lkManager.configure(config);
      
 
      
      Logger logger = lkManager.getLoggerForCategory("toska");
      this.enableLogging(logger);
    } catch (Exception e) {
      System.err.println("Fatal error: could not configure the application" +
        e.getMessage());
      e.printStackTrace(System.err);
      System.exit(1);
    }
    
            
  }  
  
 
  /**
   * @see Contextualizable#contextualize(Context)
   */
  public void contextualize(Context context) throws ContextException {
    this.context = (DefaultContext)context;
    this.context.put(Constants.MAIN, this);
  }

   
  /**
   * Gets the splitPane so that components can update it.
   * @return Returns the JSplitPane
   */
  public JSplitPane getSplitPane() {
    return splitPane;
  }

  public static void main(String[] args) {
  
    ClassLoader cl = KeysAdmin.class.getClassLoader();    
    ImageIcon splashicon = new ImageIcon(cl.getResource("resources/toska.jpg"));   
  
    SplashScreen splash = new SplashScreen(splashicon, null, 7500);
    
    ExcaliburComponentManager manager = new ExcaliburComponentManager();
    DefaultContext context = new DefaultContext();
    
    manager.contextualize(context);
    KeysAdmin admin = new KeysAdmin();
    admin.compose(manager);
    
    
    DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();

    try {

      InputStream is = cl.getResourceAsStream("resources/toska.xconf");


      Configuration toskaConfig = builder.build(is);
      
      admin.configure(toskaConfig);
      admin.contextualize(context);
      admin.initialize();

    } catch (Exception e) { 
      e.printStackTrace(System.err);

    }
  }

}
