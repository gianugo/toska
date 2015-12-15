package it.rabellino.toska.gui;

import it.rabellino.toska.DuplicateUserException;
import it.rabellino.toska.Host;
import it.rabellino.toska.User;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * @author <a href="gianugo@apache.org">Gianugo Rabellino</a>
 *
 */
public class NewUserPanel extends AbstractPanel  {

  private JTextField userField;

  private Host host;
  private Constants context;
    
  /**
   * Constructor for NewUserPanel.
   */
  public NewUserPanel() {
    super();

    this.setLayout(new BorderLayout());
    JLabel title = new JLabel("Add a username");
    title.setHorizontalAlignment(SwingConstants.CENTER);
    this.add(title, BorderLayout.NORTH);
    
    JPanel contentPanel = new JPanel();
    JLabel userLabel = new JLabel("Username: ");
    
    contentPanel.setAlignmentY(SwingConstants.CENTER);
    userField = new JTextField(16);
    contentPanel.add(userLabel);
    contentPanel.add(userField);
    
    JButton okButton =  new JButton("Add this user");
    okButton.addActionListener(this);
    this.add(contentPanel, BorderLayout.CENTER);
    this.add(okButton, BorderLayout.SOUTH);
    
        
  }
  
  /**
   * @see ActionListener#actionPerformed(ActionEvent)
   */
  public void actionPerformed(ActionEvent arg0) {
     
     if (userField.getText().length() == 0) {
       JOptionPane.showMessageDialog(
        this, "Please insert a valid Unix username", "Error", 
        JOptionPane.ERROR_MESSAGE);
        return;     
     }
     
     host = (Host)currentNode.getUserObject();
     
     // Update model
     User newUser = new User(userField.getText());
     try {
       this.host.addUser(newUser);
     } catch (DuplicateUserException e) {
       JOptionPane.showMessageDialog(
        this, "The username already exists, delete it first", 
          "Error", JOptionPane.ERROR_MESSAGE);
       return;          
     }
     
     // Update view
        
     DefaultMutableTreeNode newNode = 
       new DefaultMutableTreeNode(newUser);
     newNode.setUserObject(newUser);
     currentNode.add(newNode);
    
    
     ((DefaultTreeModel)tree.getModel()).nodeStructureChanged(currentNode);
     
     logger.info("Added user: " + newUser.getName());
     handler.setChanged(true);    
     menuBar.modelChanged();

  }   

}
