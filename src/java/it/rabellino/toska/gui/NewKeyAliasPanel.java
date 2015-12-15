package it.rabellino.toska.gui;

import it.rabellino.toska.DuplicateElementException;
import it.rabellino.toska.Key;
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
public class NewKeyAliasPanel extends AbstractPanel {

  private JTextField keyField;

  /**
   * Constructor for NewUserPanel.
   */
  public NewKeyAliasPanel() {
    this.setLayout(new BorderLayout());
    JLabel title = new JLabel("Add a key alias name");
    title.setHorizontalAlignment(SwingConstants.CENTER);
    this.add(title, BorderLayout.NORTH);
    
    JPanel contentPanel = new JPanel();
    JLabel keyLabel = new JLabel("New key alias name: ");
    
    contentPanel.setAlignmentY(SwingConstants.CENTER);
    keyField = new JTextField(25);
    contentPanel.add(keyLabel);
    contentPanel.add(keyField);
    
    JButton okButton =  new JButton("Add this key alias");
    okButton.addActionListener(this);
    this.add(contentPanel, BorderLayout.CENTER);
    this.add(okButton, BorderLayout.SOUTH);
    
    
    
  }
  /**
   * @see ActionListener#actionPerformed(ActionEvent)
   */
  public void actionPerformed(ActionEvent evt) {
     String newKeyAliasName = keyField.getText();
     if (newKeyAliasName.length() == 0) {
       JOptionPane.showMessageDialog(
        this, "Please insert a valid key alias name", "Error", 
        JOptionPane.ERROR_MESSAGE);
        return;     
     }
     
     
     Alias newKeyAlias = new Alias(newKeyAliasName);

     try {
	 this.handler.getKeyAliases().addAlias(newKeyAlias);
	 // Add here a Key
	 //....................
     } catch (DuplicateElementException e) {
	 JOptionPane.showMessageDialog(
				       this, "Duplicate key alias", "Error",
				       JOptionPane.ERROR_MESSAGE);
	 return;     
     }
     
     DefaultMutableTreeNode newKeyAliasNode = 
       new DefaultMutableTreeNode(newKeyAlias);
     newKeyAliasNode.setUserObject(newKeyAlias);
     currentNode.add(newKeyAliasNode);
     ((DefaultTreeModel)tree.getModel()).nodeStructureChanged(currentNode);   
     logger.info("Added key alias " + newKeyAlias.toString());
     handler.setChanged(true);
     menuBar.modelChanged();
  }

}
