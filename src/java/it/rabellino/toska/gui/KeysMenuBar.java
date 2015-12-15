package it.rabellino.toska.gui;

import it.rabellino.toska.FileCreator;
import it.rabellino.toska.FileDistribution;
import it.rabellino.toska.ConfigHandler;
import it.rabellino.toska.ScpException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

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
 * @author rabellino
 *
 * 
 */
public class KeysMenuBar 
  extends 
    JMenuBar
  implements
    // MenuBar,
    ActionListener,
    Component,
    Composable,
    Contextualizable,
    Initializable,
    LogEnabled {
    
  public static final String ROLE = "it.rabellino.toska.gui.MenuBar";
  
  private File chosenFile;
  private KeysAdmin admin;
  private JFileChooser chooser = new JFileChooser();
  private JMenu fileMenu;
  private JMenuItem newMenu;
  private JMenuItem openMenu;
  private JMenuItem saveMenu;
  private JMenuItem deployMenu;
  private JMenuItem distributeMenu; // Should be probably Merged with deployMenu
  private ComponentManager manager;
  private DefaultContext context;
  private Logger logger;
  private String deployPath; // Should be probably in the global config

  public KeysMenuBar() {
  
  }
  
  /**
   * @see ActionListener#actionPerformed(ActionEvent)
   */
  public void actionPerformed(ActionEvent event) {

    if (event.getSource().equals(newMenu)) {
      try {
        ConfigHandler newHandler = 
           (ConfigHandler)manager.lookup(ConfigHandler.ROLE);
        admin.setConfigHandler(newHandler);
      } catch (ComponentException ce) {
        logger.error("Unable to create a new configuration", ce);
      }
    }

    
    if (event.getActionCommand().equals("Open")) {          
      chooser.setDialogTitle("Choose a key file");
      chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

      int returned = chooser.showOpenDialog(this);
      if (returned == JFileChooser.APPROVE_OPTION) {
         
        File chosenFile = chooser.getSelectedFile();
        this.logger.debug("Parsing " + chosenFile.toString());
        try {

          admin.open(chooser.getSelectedFile());
        } catch (Exception e) {
          logger.debug("Problem!", e);
        
        }
      } 
    } else if (event.getActionCommand().equals("Save")) {
      chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      chooser.setDialogTitle("Save a key file");
      int returned = chooser.showSaveDialog(this);
      if (returned == JFileChooser.APPROVE_OPTION) {
        admin.save(chooser.getSelectedFile());
      }  
    } else if (event.getActionCommand().equals("Deploy")) {
      File deployFile;
      chooser.setDialogTitle("Choose a directory to deploy");
      chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

      int returned = chooser.showOpenDialog(this);
      if (returned == JFileChooser.APPROVE_OPTION) {
        deployFile = chooser.getSelectedFile();
        
        if (!deployFile.isDirectory()) {
          JOptionPane.showMessageDialog(this, 
            "Not a directory", "Error", 
             JOptionPane.ERROR_MESSAGE);
          return;           
        }
        deployPath = deployFile.getAbsolutePath();
        FileCreator creator = new FileCreator();
	creator.enableLogging(logger);
        creator.setPath(deployPath);
        creator.setConfigHandler(admin.getConfigHandler());
        creator.writeFiles();
        logger.info("Successfully deployed configuration to: " +
          deployFile.toString());
	this.distributeMenu.setEnabled(true);
      }  
    
    } else if (event.getActionCommand().equals("Distribute")) {
      File targetDir;
      chooser.setDialogTitle("Choose a target directory in all the boxes");
      chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

      int returned = chooser.showOpenDialog(this);
      if (returned == JFileChooser.APPROVE_OPTION) {
        targetDir = chooser.getSelectedFile();
        
        FileDistribution distribution = new FileDistribution();
	distribution.enableLogging(logger);
        distribution.setSourceDir(deployPath); // This was assigned during the Deploy. See above
	distribution.setTargetDir(targetDir.getAbsolutePath());
        distribution.setConfigHandler(admin.getConfigHandler());
	try {
	    distribution.scpFiles();
	    logger.info("Successfully deployed configuration to: " +
			targetDir.toString());
	} catch (ScpException e) {
	    logger.error(e.toString());
	}
      }  
	
    }
  }
    
  /**
   * Gets the deployMenu.
   * @return Returns a JMenuItem
   */
  public JMenuItem getDistributeMenu() {
    return distributeMenu;
  }

  /**
   * Gets the deployMenu.
   * @return Returns a JMenuItem
   */
  public JMenuItem getDeployMenu() {
    return deployMenu;
  }

  /**
   * Gets the openMenu.
   * @return Returns a JMenuItem
   */
  public JMenuItem getOpenMenu() {
    return openMenu;
  }

  /**
   * Gets the saveMenu.
   * @return Returns a JMenuItem
   */
  public JMenuItem getSaveMenu() {
    return saveMenu;
  }


  /**
   * @see Contextualizable#contextualize(Context)
   */
  public void contextualize(Context context) throws ContextException {
    this.context = (DefaultContext)context;
    this.context.put(Constants.MENUBAR, this);
  
  }

  public void compose(ComponentManager manager) {
    this.manager = manager;
  }

  /**
   * @see Initializable#initialize()
   */
  public void initialize() throws Exception {
  
    this.admin = (KeysAdmin) context.get(Constants.MAIN);
    fileMenu = new JMenu("File");
    fileMenu.setMnemonic('f');
    fileMenu.addActionListener(this);
    newMenu = new JMenuItem("New");
    newMenu.addActionListener(this);
    newMenu.setMnemonic('n');
    openMenu = new JMenuItem("Open");
    openMenu.addActionListener(this);
    openMenu.setMnemonic('o');
    saveMenu = new JMenuItem("Save");
    saveMenu.addActionListener(this);
    saveMenu.setMnemonic('s');
    deployMenu = new JMenuItem("Deploy to filesystem");
    deployMenu.setMnemonic('d');
    deployMenu.setActionCommand("Deploy");
    deployMenu.addActionListener(this);
    distributeMenu = new JMenuItem("Scp Keys to boxes");
    distributeMenu.setMnemonic('s');
    distributeMenu.setActionCommand("Distribute");
    distributeMenu.addActionListener(this);
    JMenuItem exitMenu = new JMenuItem("Exit");
    exitMenu.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent a) {
        if (admin.getConfigHandler().hasChanged()) {
          int retval = JOptionPane.showConfirmDialog(null,
            "Some data have been changed:\n" +
            "Are you sure you want to exit?"); 
          if (retval != JOptionPane.YES_OPTION) return;
        }
        System.exit(0);
      }
    });  
    exitMenu.setMnemonic('e');
    fileMenu.add(newMenu);
    fileMenu.add(openMenu);
    fileMenu.add(saveMenu);
    fileMenu.add(deployMenu);
    fileMenu.add(distributeMenu);
    fileMenu.add(exitMenu);     
    this.add(fileMenu);
  }

  /**
   * @see LogEnabled#enableLogging(Logger)
   */
  public void enableLogging(Logger logger) {
    this.logger = logger;
  }
  
  public void modelChanged() {
    this.saveMenu.setEnabled(true);
    this.deployMenu.setEnabled(true);
  }

}
