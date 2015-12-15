package it.rabellino.toska.gui;

import it.rabellino.toska.DuplicateElementException;
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
public class NewHostAliasElementPanel extends AbstractPanel {
    private JTextField hostAliasElement;
    //private Alias alias;

    /**
     * Constructor for NewUserPanel.
     */
    public NewHostAliasElementPanel() {
	this.setLayout(new BorderLayout());
	JLabel title = new JLabel("Add a host/host alias name");
	title.setHorizontalAlignment(SwingConstants.CENTER);
	this.add(title, BorderLayout.NORTH);
	
	JPanel contentPanel = new JPanel();
	JLabel hostAliasElementLabel = new JLabel("New host/host alias name: ");
	
	contentPanel.setAlignmentY(SwingConstants.CENTER);
	hostAliasElement = new JTextField(25);
	contentPanel.add(hostAliasElementLabel);
	contentPanel.add(hostAliasElement);
	
	JButton okButton =  new JButton("Add this host/host alias");
	okButton.addActionListener(this);
	this.add(contentPanel, BorderLayout.CENTER);
	this.add(okButton, BorderLayout.SOUTH);    
    }
    /**
     * @see ActionListener#actionPerformed(ActionEvent)
     */
    public void actionPerformed(ActionEvent evt) {
	Alias currentHostAlias = (Alias) currentNode.getUserObject();
	String newHostAliasElementName = hostAliasElement.getText();
	if (newHostAliasElementName.length() == 0) {
	    JOptionPane.showMessageDialog(this, "Please insert a valid host/host alias name", "Error", 
					  JOptionPane.ERROR_MESSAGE);
	    return;     
	}

	try {
	    //	 this.handler.getHostAliases().getAlias(newHostAliasElement).addElement(newHostAliasElementName);
	    currentHostAlias.addElement(newHostAliasElementName);
	    logger.debug("Adding to hostAlias "+currentHostAlias.toString()+ " element "+newHostAliasElementName);
	} catch (DuplicateElementException e) {
	    JOptionPane.showMessageDialog(this, "Duplicate host alias", "Error", JOptionPane.ERROR_MESSAGE);
	    return;     
	}
	
	DefaultMutableTreeNode newHostAliasElementNode = 
	    new DefaultMutableTreeNode(newHostAliasElementName);
	//	    new DefaultMutableTreeNode(newHostAliasElement);
	newHostAliasElementNode.setUserObject(newHostAliasElementName);
	currentNode.add(newHostAliasElementNode);
	((DefaultTreeModel)tree.getModel()).nodeStructureChanged(currentNode);   
	logger.info("Added host alias Element " + newHostAliasElementName);
	handler.setChanged(true);
	menuBar.modelChanged();
    }   
}
