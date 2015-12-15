package it.rabellino.toska;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;

/**
 * @version 	1.0
 * @author <a href="mailto:Nito@Qindel.ES">Nito Martinez</a>
 */
public class FileDistribution implements LogEnabled {
    
    private String sourceDir = "/tmp";
    private String targetDir = "/tmp";
    private ConfigHandler handler;
    private Logger logger;

    public void setSourceDir(String path) {
	this.sourceDir = path;
    }
    
    public void setTargetDir(String target) {
	this.targetDir = target;
    }
    
    public void setConfigHandler(ConfigHandler handler) {
	this.handler = handler;
    }
  
    
    public void scpFiles() throws ScpException {
	
	Iterator hosts = handler.getHosts().values().iterator();
	Aliases hostAliases = handler.getHostAliases();
	Aliases userAliases = handler.getUserAliases();

	String sourceFiles = "";
	while (hosts.hasNext()) {
	    //Host host = (Host) hosts.next();
	    Host nonExpandedHost = (Host) hosts.next();
	    Iterator expandedHostsIterator = hostAliases.getExpandedAlias(nonExpandedHost.getName()).iterator();
	    while (expandedHostsIterator.hasNext()) { 
		String hostName = (String)expandedHostsIterator.next();
		logger.debug("Non expanded host: "+nonExpandedHost.getName()+ " expanded host: "+hostName);

		// getExpandedHosts here
		Iterator users = nonExpandedHost.getUsers().values().iterator();
		while (users.hasNext()) {
		    User nonExpandedUser = (User) users.next();
		    Iterator expandedUsersIterator = userAliases.getExpandedAlias(nonExpandedUser.getName()).iterator();
		    while (expandedUsersIterator.hasNext()) {
			String userName = (String) expandedUsersIterator.next();
			//User user = (User) users.next();
			String sourceFile = sourceDir + File.separator +
			    hostName + File.separator + 
			    userName;
			File userFile = new File(sourceFile);
			
			if (!userFile.exists()) {
			    System.err.println("A supposed created file during the deployment is" +
					       " no longer there(?): <" + sourceFile + ">"); 
			    throw new ScpException("Source file <" + sourceFile + 
						   "> does not exists");
			}
			sourceFiles += " " + sourceFile;
			System.gc(); // This is to clear the references to old userFile. Out of file descriptors error
		    }
		    String target = hostName + ":" + targetDir;
		    doScp(sourceFiles, target);
		}  
	    }
	}
    }
  
    public void enableLogging(Logger logger) {
	this.logger = logger;
    }
    /*
     * doSCP
     * Input: 
     * - source. The source string in the same way as it is done in the normal scp command
     *           Usually something like "host/*" or "host/key"
     * - target. The target string in the same way as scp accepts it. 
     *           In this environment usually "host:/etc/ssh2/keys"
     *
     * Dependencies. scp has to be in the default PATH
     */
    private void doScp(String source, String target) throws ScpException {
	Process p;
	Runtime r;
	
	String commandLine = handler.getSCPCommand() + source + " " + target;
	
	try {
	    r = Runtime.getRuntime();
	    p = r.exec(commandLine);
	    p.waitFor();
	    if (p.exitValue() == 0) {
		System.out.println("Transferred " + source + " to " + target);
	    } else {
		throw new ScpException("Error transferring " + source + " to " + target 
				       + ". The command line was: <" + commandLine + ">");
	    }
	} catch (SecurityException e) {
	    throw new ScpException("SecurityException: executing <" + commandLine + "> :" + e);
	} catch (IOException e) {
	    throw new ScpException("IOException: executing <" + commandLine + "> :" + e);
	} catch (InterruptedException e) {
	    throw new ScpException("InterruptedException: problem during wait while executing <" + 
				   commandLine + "> :" + e);
	}
    }
}
