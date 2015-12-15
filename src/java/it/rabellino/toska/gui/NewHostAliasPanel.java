package it.rabellino.toska.gui;

import it.rabellino.toska.DuplicateElementException;
import it.rabellino.toska.Host;
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
public class NewHostAliasPanel extends AbstractPanel {

  private JTextField hostField;

  /**
   * Constructor for NewUserPanel.
   */
  public NewHostAliasPanel() {
    this.setLayout(new BorderLayout());
    JLabel title = new JLabel("Add a host alias name");
    title.setHorizontalAlignment(SwingConstants.CENTER);
    this.add(title, BorderLayout.NORTH);
    
    JPanel contentPanel = new JPanel();
    JLabel hostLabel = new JLabel("New host alias name: ");
    
    contentPanel.setAlignmentY(SwingConstants.CENTER);
    hostField = new JTextField(25);
    contentPanel.add(hostLabel);
    contentPanel.add(hostField);
    
    JButton okButton =  new JButton("Add this host alias");
    okButton.addActionListener(this);
    this.add(contentPanel, BorderLayout.CENTER);
    this.add(okButton, BorderLayout.SOUTH);
    
    
    
  }
  /**
   * @see ActionListener#actionPerformed(ActionEvent)
   */
  public void actionPerformed(ActionEvent evt) {
     String newHostAliasName = hostField.getText();
     if (newHostAliasName.length() == 0) {
       JOptionPane.showMessageDialog(
        this, "Please insert a valid host alias name", "Error", 
        JOptionPane.ERROR_MESSAGE);
        return;     
     }
     
     
     Alias newHostAlias = new Alias(newHostAliasName);

     try {
	 this.handler.getHostAliases().addAlias(newHostAlias);
     } catch (DuplicateElementException e) {
	 JOptionPane.showMessageDialog(
				       this, "Duplicate host alias", "Error",
				       JOptionPane.ERROR_MESSAGE);
	 return;     
     }
     
     DefaultMutableTreeNode newHostAliasNode = 
       new DefaultMutableTreeNode(newHostAlias);
     newHostAliasNode.setUserObject(newHostAlias);
     currentNode.add(newHostAliasNode);
     ((DefaultTreeModel)tree.getModel()).nodeStructureChanged(currentNode);   
     logger.info("Added host alias " + newHostAlias.toString());
     handler.setChanged(true);
     menuBar.modelChanged();
  }

}
