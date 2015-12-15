package it.rabellino.toska;

import java.util.HashMap;
import java.util.NoSuchElementException;

/**
 * A class representing a user. Contains key references.
 * 
 * @author <a href="gianugo@apache.org">Gianugo Rabellino</a>
 *
 */
public class User {

  protected String name;
  protected HashMap keys;

  /**
   * Constructor for User.
   */
  public User(String name) {
    this.name=name;  
    keys = new HashMap();
  }

  /**
   * Gets the name.
   * @return Returns a String
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name.
   * @param name The name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the keys.
   * @return Returns a HashMap
   */
  public HashMap getKeys() {
    return keys;
  }

  /**
   * Sets the keys.
   * @param keys The keys to set
   */
  public void setKeys(HashMap keys) {
    this.keys = keys;
  }
  
  /**
   * Adds a key.
   * @param key
   * @throws DuplicateKeyException
   */
  public void addKey(Key key) throws DuplicateKeyForUserException {
    if (keys.containsKey(key))
      throw new DuplicateKeyForUserException("Key already exists for this user: " +
        key.toString());
    keys.put(key.toString(), key);          
  }
  
  public void removeKey(Key key) throws NoSuchElementException {
    if (keys.remove(key.toString()) == null)
      throw new NoSuchElementException("Not existing key for this user" +
        key.toString());
  }

  /**
   * @see Object#equals(Object)
   */
  public boolean equals(Object obj) {
    if (obj instanceof User) 
      return ((User)obj).getName().equals(this.getName());      
    else if (obj instanceof String)
      return this.getName().equals((String)obj);
      
    return false;
  }

  /**
   * @see Object#toString()
   */
  public String toString() {
    return this.getName();
  }
}
