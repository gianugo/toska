package it.rabellino.toska;

/**
 * This class implements the OpenSSH v.2 key format
 * 
 * @author <a href="gianugo@apache.org">Gianugo Rabellino</a>
 */

public class OpenSSH2Key implements Key {

  protected String comment;
  protected String key;
  protected String options;
  protected boolean disabled;

  /**
   * Constructor for OpenSSH2Key.
   */
  public OpenSSH2Key() {

  }
  
  /**
   * Full constructor.
   * @param comment
   * @param key
   * @param options
   */
  public OpenSSH2Key(String comment, String key, String options) {
    this.comment = comment;
    this.key = key;
    this.options = options;
  }

  /**
   * Gets the comment.
   * @return Returns a String
   */
  public String getComment() {
    return comment;
  }

  /**
   * Gets the key.
   * @return Returns a String
   */
  public String getKey() {
    return key;
  }

  /**
   * Gets the options.
   * @return Returns a String
   */
  public String getOptions() {
    return options;
  }

  /**
   * Sets the comment.
   * @param comment The comment to set
   */
  public void setComment(String comment) {
    this.comment = comment;
  }

  /**
   * Sets the key.
   * @param key The key to set
   */
  public void setKey(String key) {
    this.key = key;
  }

  /**
   * Sets the options.
   * @param options The options to set
   */
  public void setOptions(String options) {
    this.options = options;
  }

  /**
   * Gets the key status.
   * @return Returns a boolean
   */
  public boolean isDisabled() {
    return disabled;
  }

  /**
   * Disable this key.
   * 
   */
  public void disable() {
    this.disabled = true;
  }
  
  /**
   * Enable this key.
   * 
   */
  public void enable() {
    this.disabled = false;
  }
  

  /**
   * @see Object#toString()
   */
  public String toString() {
    return comment;
  }

  /**
   * @see Object#equals(Object)
   */
  public boolean equals(Object o) {
    return this.comment.equals(o.toString());
  }

}
