package it.rabellino.toska;

import org.apache.avalon.framework.component.Component;

/**
 * A generic key representation
 * 
 * @author <a href="gianugo@apache.org">Gianugo Rabellino</a>
 *
 */
public interface Key extends Component {
  static final String ROLE = "it.rabellino.toska.Key";
  
  String getComment();
  String getKey();
  String getOptions();
  void setComment(String comment);
  void setKey(String key);
  void setOptions(String options);
  void enable();
  void disable();
  boolean isDisabled();
  

}
