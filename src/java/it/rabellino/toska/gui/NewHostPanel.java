package it.rabellino.toska.gui;

import it.rabellino.toska.Host;
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
public class NewHostPanel extends AbstractPanel {

  private JTextField hostField;

  /**
   * Constructor for NewUserPanel.
   */
  public NewHostPanel() {
    this.setLayout(new BorderLayout());
    JLabel title = new JLabel("Add a host name");
    title.setHorizontalAlignment(SwingConstants.CENTER);
    this.add(title, BorderLayout.NORTH);
    
    JPanel contentPanel = new JPanel();
    JLabel hostLabel = new JLabel("New host name: ");
    
    contentPanel.setAlignmentY(SwingConstants.CENTER);
    hostField = new JTextField(25);
    contentPanel.add(hostLabel);
    contentPanel.add(hostField);
    
    JButton okButton =  new JButton("Add this host");
    okButton.addActionListener(this);
    this.add(contentPanel, BorderLayout.CENTER);
    this.add(okButton, BorderLayout.SOUTH);
    
    
    
  }
  /**
   * @see ActionListener#actionPerformed(ActionEvent)
   */
  public void actionPerformed(ActionEvent evt) {
     String newHostName = hostField.getText();
     if (newHostName.length() == 0) {
       JOptionPane.showMessageDialog(
        this, "Please insert a valid host name", "Error", 
        JOptionPane.ERROR_MESSAGE);
        return;     
     }
     
     
     Host newHost = new Host(newHostName);

     if (this.handler.getHosts().containsKey(newHost.toString())) {
       JOptionPane.showMessageDialog(
        this, "Duplicate Host", "Error",
        JOptionPane.ERROR_MESSAGE);
        return;     

     }
     this.handler.getHosts().put(newHost.toString(), newHost);
     
     DefaultMutableTreeNode newHostNode = 
       new DefaultMutableTreeNode(newHost);
     newHostNode.setUserObject(newHost);
     currentNode.add(newHostNode);
     ((DefaultTreeModel)tree.getModel()).nodeStructureChanged(currentNode);   
     logger.info("Added host " + newHost.toString());
     handler.setChanged(true);
     menuBar.modelChanged();
  }

}
