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
public class NewUserAliasElementPanel extends AbstractPanel {
    private JTextField userAliasElement;
    //private Alias alias;

    /**
     * Constructor for NewUserPanel.
     */
    public NewUserAliasElementPanel() {
	this.setLayout(new BorderLayout());
	JLabel title = new JLabel("Add a user/user alias name");
	title.setHorizontalAlignment(SwingConstants.CENTER);
	this.add(title, BorderLayout.NORTH);
	
	JPanel contentPanel = new JPanel();
	JLabel userAliasElementLabel = new JLabel("New user/user alias name: ");
	
	contentPanel.setAlignmentY(SwingConstants.CENTER);
	userAliasElement = new JTextField(25);
	contentPanel.add(userAliasElementLabel);
	contentPanel.add(userAliasElement);
	
	JButton okButton =  new JButton("Add this user/user alias");
	okButton.addActionListener(this);
	this.add(contentPanel, BorderLayout.CENTER);
	this.add(okButton, BorderLayout.SOUTH);    
    }
    /**
     * @see ActionListener#actionPerformed(ActionEvent)
     */
    public void actionPerformed(ActionEvent evt) {
	Alias currentUserAlias = (Alias) currentNode.getUserObject();
	String newUserAliasElementName = userAliasElement.getText();
	if (newUserAliasElementName.length() == 0) {
	    JOptionPane.showMessageDialog(this, "Please insert a valid user/user alias name", "Error", 
					  JOptionPane.ERROR_MESSAGE);
	    return;     
	}

	try {
	    //	 this.handler.getUserAliases().getAlias(newUserAliasElement).addElement(newUserAliasElementName);
	    currentUserAlias.addElement(newUserAliasElementName);
	    logger.debug("Adding to userAlias "+currentUserAlias.toString()+ " element "+newUserAliasElementName);
	} catch (DuplicateElementException e) {
	    JOptionPane.showMessageDialog(this, "Duplicate user alias", "Error", JOptionPane.ERROR_MESSAGE);
	    return;     
	}
	
	DefaultMutableTreeNode newUserAliasElementNode = 
	    new DefaultMutableTreeNode(newUserAliasElementName);
	//	    new DefaultMutableTreeNode(newUserAliasElement);
	newUserAliasElementNode.setUserObject(newUserAliasElementName);
	currentNode.add(newUserAliasElementNode);
	((DefaultTreeModel)tree.getModel()).nodeStructureChanged(currentNode);   
	logger.info("Added user alias Element " + newUserAliasElementName);
	handler.setChanged(true);
	menuBar.modelChanged();
    }   
}
