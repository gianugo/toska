package it.rabellino.toska.gui;

import it.rabellino.toska.DuplicateElementException;
import it.rabellino.toska.User;
import it.rabellino.toska.Alias;
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
 * @author <a href="Nito@Qindel.ES">Nito Martinez</a>
 *
 */
public class NewUserAliasPanel extends AbstractPanel {

  private JTextField userField;

  /**
   * Constructor for NewUserPanel.
   */
  public NewUserAliasPanel() {
    this.setLayout(new BorderLayout());
    JLabel title = new JLabel("Add a user alias name");
    title.setHorizontalAlignment(SwingConstants.CENTER);
    this.add(title, BorderLayout.NORTH);
    
    JPanel contentPanel = new JPanel();
    JLabel userLabel = new JLabel("New user alias name: ");
    
    contentPanel.setAlignmentY(SwingConstants.CENTER);
    userField = new JTextField(25);
    contentPanel.add(userLabel);
    contentPanel.add(userField);
    
    JButton okButton =  new JButton("Add this user alias");
    okButton.addActionListener(this);
    this.add(contentPanel, BorderLayout.CENTER);
    this.add(okButton, BorderLayout.SOUTH);
    
    
    
  }
  /**
   * @see ActionListener#actionPerformed(ActionEvent)
   */
  public void actionPerformed(ActionEvent evt) {
     String newUserAliasName = userField.getText();
     if (newUserAliasName.length() == 0) {
       JOptionPane.showMessageDialog(
        this, "Please insert a valid user alias name", "Error", 
        JOptionPane.ERROR_MESSAGE);
        return;     
     }
     
     
     Alias newUserAlias = new Alias(newUserAliasName);

     try {
	 this.handler.getUserAliases().addAlias(newUserAlias);
     } catch (DuplicateElementException e) {
	 JOptionPane.showMessageDialog(
				       this, "Duplicate user alias", "Error",
				       JOptionPane.ERROR_MESSAGE);
	 return;     
     }
     
     DefaultMutableTreeNode newUserAliasNode = 
       new DefaultMutableTreeNode(newUserAlias);
     newUserAliasNode.setUserObject(newUserAlias);
     currentNode.add(newUserAliasNode);
     ((DefaultTreeModel)tree.getModel()).nodeStructureChanged(currentNode);   
     logger.info("Added user alias " + newUserAlias.toString());
     handler.setChanged(true);
     menuBar.modelChanged();
  }

}
