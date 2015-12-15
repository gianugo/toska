package it.rabellino.toska;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;
import java.util.HashMap;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;

/**
 * @version 	1.0
 * @author <a href="mailto:rabellino@ksolutions.it">Gianugo Rabellino</a>
 */
public class FileCreator implements LogEnabled {
  
  private String path = "/tmp";
  private ConfigHandler handler;
  private Logger logger;

  public void setPath(String path) {
    this.path = path;
  }
  
  public void setConfigHandler(ConfigHandler handler) {
    this.handler = handler;
  }
  
  
  public void writeFiles() {
    
    Iterator hosts = handler.getHosts().values().iterator();
    Aliases hostAliases = handler.getHostAliases();
    Aliases userAliases = handler.getUserAliases();
    Aliases keyAliases = handler.getKeyAliases();
    HashMap keyHashMap = handler.getKeys();
    while (hosts.hasNext()) {
	Host nonExpandedHost = (Host) hosts.next();
	Iterator expandedHostsIterator = hostAliases.getExpandedAlias(nonExpandedHost.getName()).iterator();
	while (expandedHostsIterator.hasNext()) { 
	    String hostName = (String)expandedHostsIterator.next();
	    logger.debug("Non expanded host: "+nonExpandedHost.getName()+ " expanded host: "+hostName);
	    Iterator users = nonExpandedHost.getUsers().values().iterator();
	    while (users.hasNext()) {
		User nonExpandedUser = (User) users.next();
		Iterator expandedUsersIterator = userAliases.getExpandedAlias(nonExpandedUser.getName()).iterator();
		while (expandedUsersIterator.hasNext()) {
		    String userName = (String) expandedUsersIterator.next();
		    //User user = (User) users.next();
		    File userFile = new File(path + File.separator +
					     hostName + File.separator + 
					     userName);
		    
		    if (!userFile.exists())
			userFile.getParentFile().mkdirs();
		    
		    try {
			userFile.createNewFile();  
			FileOutputStream fos = new FileOutputStream(userFile);
			
			Iterator keys = nonExpandedUser.getKeys().values().iterator();
			
			while (keys.hasNext()) {
			    Key nonExpandedKey = (Key) keys.next();
			    Iterator expandedKeysIterator = keyAliases.getExpandedAlias(nonExpandedKey.getComment()).iterator();
			    while (expandedKeysIterator.hasNext()) {
				String keyName = (String) expandedKeysIterator.next();
				Key key = (Key) keyHashMap.get(keyName);
				if (key != null) {
				    fos.write(key.getKey().getBytes());
				    fos.write("\n".getBytes());
				} else {
				    logger.error("Error: Cannot get key for:"+keyName);
				}
			    }
			}
			fos.close();
		    } catch (IOException e) {
			System.err.println("Unable to create file: " +
					   userFile.toString() +
					   e.getMessage());
		    }	    	
		}
	    }  
	}  
    }
  }
  
  public void enableLogging(Logger logger) {
    this.logger = logger;
  }
  public static void main(String args[]) {
    FileCreator fc = new FileCreator();
    fc.setPath("C:\\tmp");
    XMLConfigHandler handler = new XMLConfigHandler(
      new InputSource(args[0]));
    fc.setConfigHandler(handler);
    fc.writeFiles();     		    
  }    


}
