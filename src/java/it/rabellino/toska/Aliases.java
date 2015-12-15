package it.rabellino.toska;

import java.util.Iterator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.NoSuchElementException;
import it.rabellino.toska.Alias;

/**
 * A haslist of aliases. This class will hold a number of Alias objects
 * and you can get the expanded list of the objects if necessary.
 * @author <a href="Nito@Qindel.ES">Nito Martinez</a>
 * $Id: Aliases.java,v 1.2 2004/12/08 23:00:13 nito007 Exp $
 */
public class Aliases {
    
    protected String name;
    protected HashMap aliases;
  
    // Need to call the constructor with the Name of the Alias
    protected Aliases() {
    }
     // Constructor for Aliases
    public Aliases(String n) {
	name = n;
	aliases = new HashMap();
    }
    // toString
    public String toString() {
	return name;
    }
    public void setName(String s) {
	name = s;
    }
    public String getName() {
	return name;
    }
    
    /**
     * @see Object#equals(Object)
     */
    public boolean equals(Object obj) {
	if (obj instanceof Aliases) {
	    Aliases a = (Aliases) obj;
	    return ((Aliases)obj).getName().equals(this.getName());      
	} else if (obj instanceof String) {
	    return name.equals((String)obj);
	}
	return false;
    }

    /*
     * returns the non expanded list of elements that belong to this Alias
     * that is if an Alias is part of another Alias then the Alias
     * elements are returned and not the expanded elements that are inside;
     */
    public HashMap getElements() {
	return aliases;
    }
    /*
     * Adds an element to the Alias
     */
    public void addAlias(Alias a) throws DuplicateElementException {
	if (aliases.containsKey(a.getName())) {
	    throw new DuplicateElementException("The Alias "+a.toString()+" is already in the Aliases "+this.toString());
	}
	aliases.put(a.getName(), a);
    }
    public Alias getAlias(String s) throws NoSuchElementException {
	if (aliases.containsKey(s)) {
	    return (Alias)aliases.get(s) ;
	}
	throw new NoSuchElementException("The Alias "+s+" does not exist in the Aliases "+this.toString());
	
    }
    public Alias getAlias(Alias a) throws NoSuchElementException {
	return getAlias(a.getName());
    }
    public boolean isAlias(String s) {
	return aliases.containsKey(s);
    }
    public boolean isAlias(Alias a) {
	return isAlias(a.getName());
    }

    public void removeAlias(String s) throws NoSuchElementException {
	if (aliases.remove(s) == null) {
	    throw new NoSuchElementException("The Alias "+s+" does not exist in the Aliases "+this.toString());
	}
    }
    public void removeAlias(Alias a) throws NoSuchElementException {
	removeAlias(a.getName());
    }

    // Returns the expansion of an alias
    public HashSet getExpandedAlias(String s) {
	Iterator i;
	String element;
	HashSet resultSet = getExpandedAliasWithElements(s);
	HashSet returnResultSet = new HashSet();

	// Delete all the elements that are Aliases
	i = resultSet.iterator();
	while (i.hasNext()) {
	    element = (String)i.next();
	    if (!isAlias(element)) {
		returnResultSet.add(element);
	    }
	}
	return returnResultSet;
    }

    /*
     * This will try expand the name of the Alias provided.
     * The algorithm works as follows:
     * 1) if the string is not an alias then return the string and we are done
     * 2) if the string is an alias set the resultSet as the expansion of the alias (as strings) +
     *    the name of the Alias
     * 3) expand all the aliases found in the resultset (but not recursively only once).
     * 4) Repeat the step 3 until there the size of the resultSet doesn't change.
     */
    private HashSet getExpandedAliasWithElements(String s) {
	HashSet resultSet = new HashSet();
	Iterator resultSetIterator, aliasElementIterator;
	String resultSetElement, aliasElement;
	
	if (!isAlias(s)) {
	    resultSet.add(s);
	    return resultSet;
	}
	
	HashSet newResultSet = getAlias(s).getElements();
	while (resultSet.size() != newResultSet.size()) {
	    resultSet = (HashSet) newResultSet.clone();
	    resultSetIterator = resultSet.iterator();
	    // Now expand each element if it is an alias
	    while (resultSetIterator.hasNext()) {
		resultSetElement = (String)resultSetIterator.next();
		if (!isAlias(resultSetElement))
		    continue;
		
		aliasElementIterator = getAlias(resultSetElement).getElements().iterator();
	      // Add all the elements of the alias to the new resultSet
		while (aliasElementIterator.hasNext()) {
		    aliasElement = (String)aliasElementIterator.next();
		    newResultSet.add(aliasElement);
		}
	    }
	}
	return resultSet;
    }
}
