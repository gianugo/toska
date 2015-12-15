package it.rabellino.toska;


import java.util.HashMap;
import java.util.HashSet;
import java.util.NoSuchElementException;

/**
 * An Host. Contains Users.
 * @author <a href="gianugo@apache.org">Gianugo Rabellino</a>
 *
 */
public class Host {

  protected String name;
  protected HashMap users;
  
  /**
   * Forbid instantiation without an host name
   */
  protected Host() {
  
  }
  
  /**
   * Constructor for Host.
   */
  public Host(String name) {
    this.name = name;
    users = new HashMap();
  }

  /**
   * Gets the name.
   * @return Returns the hostname
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
   * @return Returns a ArrayList
   */
  public HashMap getUsers() {
    return users;
  }

  /**
   * Sets the keys.
   * @param keys The keys to set
   */
  public void setUsers(HashMap users) {
    this.users = users;
  }
  
  /**
   * Add a User.
   * @param user
   * @throws DuplicateUserException
   */

  public void addUser(User user) throws DuplicateUserException {
    if (users.containsKey(user.toString())) 
      throw new DuplicateUserException("User already present: " 
        + user.toString());
    users.put(user.toString(), user);    
  }
  
  
  /**
   * Remove a user.
   * @param user
   */
  public void removeUser(User user) throws NoSuchElementException {
    if (users.remove(user.toString()) == null) 
      throw new NoSuchElementException("User " + user.toString() + 
        " is not associated with this host"); 
  }
  

  /**
   * @see Object#equals(Object)
   */
  public boolean equals(Object obj) {
    if (obj instanceof Host) {
      Host host = (Host) obj;
      return ((Host)obj).getName().equals(this.getName());      
    } else if (obj instanceof String) {
      return name.equals((String)obj);
    }
    return false;
  }

  /**
   * @see Object#toString()
   */
  public String toString() {
    return this.getName();
  }

  public boolean isSimpleHost() {
    return true;
  }

  public HashSet getHosts() {
      HashSet set = new HashSet();
      set.add(this.getName());
      return set;
  }
  public HashSet getExpandedHosts() {
      return this.getHosts();
  }
}
