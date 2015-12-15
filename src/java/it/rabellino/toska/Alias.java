package it.rabellino.toska;

import java.util.Iterator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.NoSuchElementException;

/**
 * An Alias. Will be used to create the HostAlias, UserAlias and KeyAlias.
 * It works like a name that will be expanded.
 * @author <a href="Nito@Qindel.ES">Nito Martinez</a>
 * $Id: Alias.java,v 1.1 2004/12/05 02:50:33 nito007 Exp $
 */
public class Alias {

  protected String name;
  protected HashSet elements;
  
    // Need to call the constructor with the Name of the Alias
    protected Alias() {
    }
     // Constructor for Alias
    public Alias(String n) {
	name = n;
	elements = new HashSet();
    }
    // toString
    public String toString() {
	return name;
    }
    public String getName() {
	return name;
    }
    public void setName(String s) {
	name = s;
    }
    
    /**
     * @see Object#equals(Object)
     */
    public boolean equals(Object obj) {
	if (obj instanceof Alias) {
	    Alias a = (Alias) obj;
	    return ((Alias)obj).getName().equals(this.getName());      
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
    public HashSet getElements() {
	return elements;
    }
    /*
     * Adds an elements to the Alias
     */
    public void addElement(String s) throws DuplicateElementException {
	if (elements.contains(s)) {
	    throw new DuplicateElementException("The Element "+s+" is already in the Alias "+this.toString());
	}
	elements.add(s);
    }
  public void removeElement(String s) throws NoSuchElementException {
      if (elements.remove(s) == false) {
	  throw new NoSuchElementException("The Element "+s+" does not exist in the Alias "+this.toString());
      }
  }

}
