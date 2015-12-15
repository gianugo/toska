package it.rabellino.toska.gui;

import it.rabellino.toska.Host;
import it.rabellino.toska.OpenSSH2Key;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * @author <a href="gianugo@apache.org">Gianugo Rabellino</a>
 *
 */
public class NewSSHKeyPanel extends AbstractPanel {

  private JTextField userField;
  private JTextArea keyField;
  private JCheckBox disabledBox;
  private JButton fileButton;
  private JFileChooser chooser;

  private Host host;  

  /**
   * Constructor for NewUserPanel.
   */
  public NewSSHKeyPanel() {
    this.setLayout(new BorderLayout());
    JLabel title = new JLabel("Add an OpenSSH public key");
    title.setHorizontalAlignment(SwingConstants.CENTER);
    this.add(title, BorderLayout.NORTH);
    
    JPanel containerPanel = new JPanel();
    containerPanel.setLayout(new BoxLayout(containerPanel, 
      BoxLayout.Y_AXIS));
    
    JPanel firstPanel = new JPanel();
    JPanel secondPanel = new JPanel();
    JPanel thirdPanel = new JPanel();
    JPanel fourthPanel = new JPanel();
    JLabel userLabel = new JLabel("Username: ");
    JLabel keyLabel = new JLabel("Type/paste the key here: ");
    firstPanel.setAlignmentY(SwingConstants.BOTTOM);
    secondPanel.setAlignmentY(SwingConstants.BOTTOM);
    thirdPanel.setAlignmentY(SwingConstants.TOP);
   
    userField = new JTextField(16);
    keyField = new JTextArea(8,30);
    keyField.setBorder(new BevelBorder(1));
    keyField.setLineWrap(true);
    
    firstPanel.add(userLabel);
    firstPanel.add(userField);
    secondPanel.add(keyLabel);
    thirdPanel.add(keyField);
    
    fileButton = new JButton("...or select a file");
    fileButton.addActionListener(this);
    
    fourthPanel.add(fileButton);
    
    containerPanel.add(firstPanel);
    containerPanel.add(secondPanel);
    containerPanel.add(thirdPanel);
    containerPanel.add(fourthPanel);
    
    JButton okButton =  new JButton("Add this key");
    okButton.addActionListener(this);
    this.add(containerPanel, BorderLayout.CENTER);
    this.add(okButton, BorderLayout.SOUTH);   
    
    chooser = new JFileChooser();
  }
  
  /**
   * @see ActionListener#actionPerformed(ActionEvent)
   */
  public void actionPerformed(ActionEvent arg0) {
  
    if (arg0.getSource() == fileButton) {
      chooser.setDialogTitle("Choose a key file");
      chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

      int returned = chooser.showOpenDialog(this);
      if (returned == JFileChooser.APPROVE_OPTION) {
        try { 
          File chosenFile = chooser.getSelectedFile();
          BufferedReader input = 
            new BufferedReader(new FileReader(chosenFile));
            
          String line = input.readLine();  
          
          while (line != null) {
            keyField.append(line);
            line = input.readLine();  
          }
                        
        } catch (IOException ioe) {
          logger.error("An error occurred processing the key file: " +
           ioe.getMessage());
           return;
        }
      } 
    
    
      return;
    }
  

    String user = userField.getText();
    String key  = keyField.getText();
        
    if (user.length() == 0) {
      JOptionPane.showMessageDialog(
        this, "Username is empty", "Error", 
        JOptionPane.ERROR_MESSAGE);
        return;     
    }
    
    if (key.length() == 0) {
      JOptionPane.showMessageDialog(
        this, "Key is empty", "Error", 
        JOptionPane.ERROR_MESSAGE);
        return;     
    }
    
    if (handler.getKeys().containsKey(user)) {
      JOptionPane.showMessageDialog(
        this, "A key with this username already exists", "Error", 
        JOptionPane.ERROR_MESSAGE);
        return;     
    }


    
    OpenSSH2Key newKey = new OpenSSH2Key();
    newKey.setComment(user);
    newKey.setKey(key);
    
    // Update model
    handler.getKeys().put(user, newKey);
    
    // Update view
    
    DefaultMutableTreeNode newNode = 
      new DefaultMutableTreeNode(user);
    newNode.setUserObject(newKey);
    currentNode.add(newNode);
    
    
    ((DefaultTreeModel)tree.getModel()).nodeStructureChanged(currentNode);
    logger.info("Added a key for: " + user);    
    handler.setChanged(true);    
    menuBar.modelChanged();
  }   

}
