package it.rabellino.toska;

import java.util.HashMap;

import org.apache.avalon.framework.component.Component;

/**
 * This is the main interface to the data model. 
 * 
 * @author <a href="gianugo@apache.org">Gianugo Rabellino</a>
 *
 */
public interface ConfigHandler extends Component {

  static final String ROLE = "it.rabellino.toska.ConfigHandler";
  
  static final String VERSION ="1.0";
  
  HashMap getKeys();
  HashMap getHosts();
  Host getHost(Host host);
  Aliases getHostAliases();
  Aliases getUserAliases();
  Aliases getKeyAliases();
  void addHostAlias(Alias alias) throws DuplicateElementException;
  void addUserAlias(Alias alias) throws DuplicateElementException;
  void addKeyAlias(Alias alias) throws DuplicateElementException;
  void addHost(Host host) throws DuplicateHostException;
  void addKey(Key key) throws DuplicateKeyException;
  void removeHost(Host host);
  void removeHostAlias(Alias alias);
  void removeHostAlias(String alias);
  void removeUserAlias(Alias alias);
  void removeUserAlias(String alias);
  void removeKeyAlias(Alias alias);
  void removeKeyAlias(String alias);
  void removeKey(String comment);
  void marshal() throws Exception;
  void unmarshal();
  void setChanged(boolean action);
  void setSCPCommand(String scpString);
  String getSCPCommand();
  boolean hasChanged();    

}
