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
public class NewKeyAliasElementPanel extends AbstractPanel {
    private JTextField keyAliasElement;
    //private Alias alias;

    /**
     * Constructor for NewUserPanel.
     */
    public NewKeyAliasElementPanel() {
	this.setLayout(new BorderLayout());
	JLabel title = new JLabel("Add a key/key alias name");
	title.setHorizontalAlignment(SwingConstants.CENTER);
	this.add(title, BorderLayout.NORTH);
	
	JPanel contentPanel = new JPanel();
	JLabel keyAliasElementLabel = new JLabel("New key/key alias name: ");
	
	contentPanel.setAlignmentY(SwingConstants.CENTER);
	keyAliasElement = new JTextField(25);
	contentPanel.add(keyAliasElementLabel);
	contentPanel.add(keyAliasElement);
	
	JButton okButton =  new JButton("Add this key/key alias");
	okButton.addActionListener(this);
	this.add(contentPanel, BorderLayout.CENTER);
	this.add(okButton, BorderLayout.SOUTH);    
    }
    /**
     * @see ActionListener#actionPerformed(ActionEvent)
     */
    public void actionPerformed(ActionEvent evt) {
	Alias currentKeyAlias = (Alias) currentNode.getUserObject();
	String newKeyAliasElementName = keyAliasElement.getText();
	if (newKeyAliasElementName.length() == 0) {
	    JOptionPane.showMessageDialog(this, "Please insert a valid key/key alias name", "Error", 
					  JOptionPane.ERROR_MESSAGE);
	    return;     
	}

	try {
	    //	 this.handler.getKeyAliases().getAlias(newKeyAliasElement).addElement(newKeyAliasElementName);
	    currentKeyAlias.addElement(newKeyAliasElementName);
	    logger.debug("Adding to keyAlias "+currentKeyAlias.toString()+ " element "+newKeyAliasElementName);
	} catch (DuplicateElementException e) {
	    JOptionPane.showMessageDialog(this, "Duplicate key alias", "Error", JOptionPane.ERROR_MESSAGE);
	    return;     
	}
	
	DefaultMutableTreeNode newKeyAliasElementNode = 
	    new DefaultMutableTreeNode(newKeyAliasElementName);
	//	    new DefaultMutableTreeNode(newKeyAliasElement);
	newKeyAliasElementNode.setUserObject(newKeyAliasElementName);
	currentNode.add(newKeyAliasElementNode);
	((DefaultTreeModel)tree.getModel()).nodeStructureChanged(currentNode);   
	logger.info("Added key alias Element " + newKeyAliasElementName);
	handler.setChanged(true);
	menuBar.modelChanged();
    }   
}
