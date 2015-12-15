package it.rabellino.toska;

import it.rabellino.toska.gui.Constants;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * @version 	1.0
 * @author		<a href="mailto:rabellino@ksolutions.it">Gianugo Rabellino</a>
 */
public class XMLConfigHandler 
    implements 
	ConfigHandler, 
	ContentHandler, 
	ErrorHandler,
	Component,
	Configurable,
	Contextualizable,
	LogEnabled,
	Initializable {
  
    static String ROLE = ConfigHandler.ROLE + "/XML";
    
    protected DefaultContext context;
    protected Configuration configuration;
    protected Logger logger;
    
    private HashMap keys;
    private HashMap hosts;
    private Aliases hostAliases;
    private Aliases userAliases;
    private Aliases keyAliases;
    private Aliases aliases;
    private Alias alias;
    private String aliasElement;
    private User user = null;
    private Host host;
    private boolean hasChanged = false;
    private String scpString;
  
    public XMLConfigHandler() {
    }
  
    public XMLConfigHandler(InputSource source) {
	try {
	    SAXParserFactory factory = SAXParserFactory.newInstance();
	    factory.setValidating(false);
	    XMLReader reader = factory.newSAXParser().getXMLReader();
	    reader.setContentHandler(this);
	    reader.setErrorHandler(this);
	    reader.parse(source);
	} catch (Exception e) {
	    e.printStackTrace(System.err);    
	}
    }
  
    public void enableLogging(Logger logger) {
	this.logger = logger;
    }
    
    public HashMap getKeys() {
	return keys;  
    }
  
    public HashMap getHosts() {
	return hosts;
    }
    
    public Aliases getHostAliases() {
	return hostAliases;
    }

    public Aliases getUserAliases() {
	return userAliases;
    }

    public Aliases getKeyAliases() {
	return keyAliases;
    }

    public void addHost(Host host) throws DuplicateHostException {
	if (hosts.containsKey(host.toString()))
	    throw new DuplicateHostException("Host " + host.toString() +
					     "already exists");
	hosts.put(host.toString(), host);
    }

    public void addHostAlias(Alias alias) throws DuplicateElementException {
	hostAliases.addAlias(alias);
    }
        	
    public void addUserAlias(Alias alias) throws DuplicateElementException {
	userAliases.addAlias(alias);
    }

    public void addKeyAlias(Alias alias) throws DuplicateElementException {
	keyAliases.addAlias(alias);
    }
        	
    public void addKey(Key key) throws DuplicateKeyException {
	if (keys.containsKey(key.toString()))
	    throw new DuplicateKeyException("Key " + key.toString() +
					    "already exists");
	keys.put(key.toString(), key);
    }      
    /*
     * @see ContentHandler#startElement(String, String, String, Attributes)
     */
    public void startElement(String nsUri, String localName, String qName, Attributes atts)
	throws SAXException {
	if (qName.equals("keys")) {
	    keys = new HashMap();
	} else if (qName.equals("key")) {
	    OpenSSH2Key key = new OpenSSH2Key();
	    key.setComment(atts.getValue("user"));
	    key.setKey(atts.getValue("value"));
	    if (atts.getValue("disabled") != null) {
		key.disable();
	    }
	    this.logger.info("Adding a key for " + key.toString());
	    keys.put(key.toString(), key);
	} else if (qName.equals("hostAliases")) {
	    hostAliases = new Aliases("hostAliases");
	    aliases = hostAliases;
	} else if (qName.equals("userAliases")) {
	    userAliases = new Aliases("userAliases");
	    aliases = userAliases;
	} else if (qName.equals("keyAliases")) {
	    keyAliases = new Aliases("keyAliases");
	    aliases = keyAliases;
	} else if (qName.equals("alias")) {
	    alias = new Alias(atts.getValue("name"));
	    try {
		aliases.addAlias(alias);
	    } catch (DuplicateElementException e) {
		this.fatalError(new SAXParseException("Duplicated alias: " + alias.toString(), null));
	    }
	} else if (qName.equals("element")) {
	    aliasElement = atts.getValue("name");
	    try {
		alias.addElement(aliasElement);
	    } catch (DuplicateElementException e) {
		this.fatalError(new SAXParseException("Duplicated alias element: " + aliasElement, null));
	    }
	} else if (qName.equals("hosts")) {
	    hosts = new HashMap();
	} else if (qName.equals("host")) {
	    host = new Host(atts.getValue("name"));
	    this.logger.info("Adding host " + host.toString());
	  hosts.put(host.toString(), host);
	} else if (qName.equals("user")) {
	    user = new User(atts.getValue("name"));
	    try {
		host.addUser(user);                 
	    } catch (DuplicateUserException e) {
		this.fatalError(
				new SAXParseException("Duplicated user: " + user, null));
	    }
	    this.logger.info("Adding user " + user.toString() +
			     " to host " + host.toString());
	} else if (qName.equals("ssh-key")) {
	    String name = atts.getValue("user");
	    Key key = null;
	    if (keyAliases.isAlias(name)) {
		key = new KeyAlias(name);
	    } else if (keys.containsKey(name)) {
		key = (Key) keys.get(name);
	    }
	    if (key != null) {
		try {
		    user.addKey(key);
		    this.logger.info("Adding key " + key.getComment() +
				 " to user " + user.toString());
		} catch (DuplicateKeyForUserException e) {
		    this.fatalError(new SAXParseException("Duplicated key: " + key.getComment(), null));             
		}     
	    } else {
		throw new SAXException("Error, key " + name + 
				       " is not references in the key database");
	    }
	} else if (qName.equals("scpString")) {
	    String command = atts.getValue("command");
	    this.logger.info("scp command string is: "+command);
	    this.scpString = command;
	}
    }
    
    /*
     * @see ErrorHandler#warning(SAXParseException)
     */
    public void warning(SAXParseException spe) throws SAXException {
	logger.error("Warning, parsing problems with XML data file: " +
		     spe.getMessage());	
    }
    
    /*
     * @see ErrorHandler#error(SAXParseException)
     */
    public void error(SAXParseException spe) throws SAXException {
	logger.error("Error, parsing problems with XML data file: " +
		     spe.getMessage());
	System.err.println("Giving up...");  
	System.exit(1);  	
    }

    /*
     * @see ErrorHandler#fatalError(SAXParseException)
     */
    public void fatalError(SAXParseException spe) throws SAXException {
	logger.error("Fatal error, parsing problems with XML data file: " +
		     spe.getMessage());	
	logger.error("Giving up...");  
	System.exit(1);  	
    }
    
    
    public void marshal() throws Exception {  
	InputStream ist = (InputStream)context.get(Constants.DATASTREAM);
	InputSource is = new InputSource(ist);
	logger.debug("Inputsource is " + is.toString());
	SAXParserFactory factory = SAXParserFactory.newInstance();
	// factory.setValidating(true);
	XMLReader reader = factory.newSAXParser().getXMLReader();
	reader.setContentHandler(this);
	reader.setErrorHandler(this);
	reader.parse(is);
	this.hasChanged = true;
    }    

    public void unmarshal() {
	DocumentBuilder builder = null;
	try {
	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    builder = dbf.newDocumentBuilder();
	} catch (Exception e) {
	    e.printStackTrace(System.err);
	}
	Document doc = builder.newDocument();      
	Node rootNode = doc.getDocumentElement();
	Element sshconfig = doc.createElement("ssh-config");
	Element scpStringElement = doc.createElement("scpString");
	Element keysElement = doc.createElement("keys");
	Element hostAliasesElement = doc.createElement("hostAliases");
	Element userAliasesElement = doc.createElement("userAliases");
	Element keyAliasesElement = doc.createElement("keyAliases");
	Element hostsElement = doc.createElement("hosts");
	doc.appendChild(sshconfig);
	sshconfig.appendChild(scpStringElement);
	sshconfig.appendChild(keysElement);
	sshconfig.appendChild(hostAliasesElement);
	sshconfig.appendChild(userAliasesElement);
	sshconfig.appendChild(keyAliasesElement);
	sshconfig.appendChild(hostsElement);
	
	scpStringElement.setAttribute("command", this.scpString);
	
	Iterator keysIterator = keys.values().iterator();
	while (keysIterator.hasNext()) {
	    Key mykey = (Key) keysIterator.next();
	    Element keyElement = doc.createElement("key");
	    keyElement.setAttribute("user", mykey.getComment());
	    keyElement.setAttribute("value", mykey.getKey());
	    if (mykey.isDisabled()) {
		keyElement.setAttribute("disabled", "true");
	    }
	    keysElement.appendChild(keyElement);
	}
	
	Iterator hostAliasesIterator = hostAliases.getElements().keySet().iterator();
	while (hostAliasesIterator.hasNext()) {
	    Alias hostAlias = hostAliases.getAlias((String) hostAliasesIterator.next());
	    Element hostAliasElement = doc.createElement("alias");
	    hostAliasElement.setAttribute("name", hostAlias.getName());
	    hostAliasesElement.appendChild(hostAliasElement);
	  
	    Iterator hostAliasElementIterator = hostAlias.getElements().iterator();
	    while (hostAliasElementIterator.hasNext()) {
		String hostAliasElementString = (String) hostAliasElementIterator.next();
		Element hostAliasElementStringElement = doc.createElement("element");
		hostAliasElementStringElement.setAttribute("name", hostAliasElementString);
		hostAliasElement.appendChild(hostAliasElementStringElement);
	    }
	}

	Iterator userAliasesIterator = userAliases.getElements().keySet().iterator();
	while (userAliasesIterator.hasNext()) {
	    Alias userAlias = userAliases.getAlias((String) userAliasesIterator.next());
	    Element userAliasElement = doc.createElement("alias");
	    userAliasElement.setAttribute("name", userAlias.getName());
	    userAliasesElement.appendChild(userAliasElement);
	  
	    Iterator userAliasElementIterator = userAlias.getElements().iterator();
	    while (userAliasElementIterator.hasNext()) {
		String userAliasElementString = (String) userAliasElementIterator.next();
		Element userAliasElementStringElement = doc.createElement("element");
		userAliasElementStringElement.setAttribute("name", userAliasElementString);
		userAliasElement.appendChild(userAliasElementStringElement);
	    }
	}

	Iterator keyAliasesIterator = keyAliases.getElements().keySet().iterator();
	while (keyAliasesIterator.hasNext()) {
	    Alias keyAlias = keyAliases.getAlias((String) keyAliasesIterator.next());
	    Element keyAliasElement = doc.createElement("alias");
	    keyAliasElement.setAttribute("name", keyAlias.getName());
	    keyAliasesElement.appendChild(keyAliasElement);
	  
	    Iterator keyAliasElementIterator = keyAlias.getElements().iterator();
	    while (keyAliasElementIterator.hasNext()) {
		String keyAliasElementString = (String) keyAliasElementIterator.next();
		Element keyAliasElementStringElement = doc.createElement("element");
		keyAliasElementStringElement.setAttribute("name", keyAliasElementString);
		keyAliasElement.appendChild(keyAliasElementStringElement);
	    }
	}
	
	Iterator hostsIterator = hosts.values().iterator();
	while (hostsIterator.hasNext()) {
	    Host myhost = (Host) hostsIterator.next();
	    Element hostElement = doc.createElement("host");
	    hostElement.setAttribute("name", myhost.getName());
	    hostsElement.appendChild(hostElement);
	    
	    Iterator usersIterator = myhost.getUsers().values().iterator();
	    while (usersIterator.hasNext()) {
		User myuser = (User) usersIterator.next();
		Element userElement = doc.createElement("user");
		userElement.setAttribute("name", myuser.getName());
		hostElement.appendChild(userElement);
		
		Iterator userKeysIterator = myuser.getKeys().values().iterator();
		while (userKeysIterator.hasNext()) {
		    Key myUserKey = (Key) userKeysIterator.next();
		    Element userKey = doc.createElement("ssh-key");
		    userKey.setAttribute("user", myUserKey.getComment());
		    userElement.appendChild(userKey);
		}
	    }
	}
	
	// Finished marshalling, serialize
	TransformerFactory tfactory = TransformerFactory.newInstance();
	try {
	    Transformer transformer = tfactory.newTransformer();
	    DOMSource source = new DOMSource(doc);
	    File out = (File)context.get(Constants.DATASINK);
	    StreamResult result = new StreamResult(out);
	    transformer.transform(source, result);
	} catch (TransformerException e) {
	    e.printStackTrace(System.err);
	} catch (ContextException ce) {
	}
	this.hasChanged = false;
    }
    
    public void removeKey(String comment) {
	if (keys.remove(comment) == null)
	    throw new NoSuchElementException("Key " + comment +
					     " is not present"); 
        
	Iterator hostIterator = hosts.values().iterator();
	
	while (hostIterator.hasNext()) {
	    Host currentHost = (Host) hostIterator.next();
	    Iterator userIterator = currentHost.getUsers().values().iterator();
	    while (userIterator.hasNext()) {
		User currentUser = (User) userIterator.next();
		currentUser.getKeys().remove(comment);
	    }
	}        
    }       
    
    public void removeHostAlias(Alias alias) {
	hostAliases.removeAlias(alias);
    }       
    public void removeUserAlias(Alias alias) {
	userAliases.removeAlias(alias);
    }       
    public void removeKeyAlias(Alias alias) {
	keyAliases.removeAlias(alias);
    }       
    public void removeHostAlias(String alias) {
	hostAliases.removeAlias(alias);
    }       
    public void removeUserAlias(String alias) {
	userAliases.removeAlias(alias);
    }       
    public void removeKeyAlias(String alias) {
	keyAliases.removeAlias(alias);
    }       
    public void removeHost(Host host) {
	if (hosts.remove(host.toString()) == null)
	    throw new NoSuchElementException("Host " + host.toString() +
					     " is not present");         
    }       
    
    
    /*
     * @see ContentHandler#setDocumentLocator(Locator)
     */
    public void setDocumentLocator(Locator arg0) {
    }
    
    /*
     * @see ContentHandler#startDocument()
     */
    public void startDocument() throws SAXException {
	
    }  
    
    /*
     * @see ContentHandler#endDocument()
     */
    public void endDocument() throws SAXException {
    }
    
    /*
     * @see ContentHandler#startPrefixMapping(String, String)
     */
    public void startPrefixMapping(String arg0, String arg1) throws SAXException {
    }

    /*
     * @see ContentHandler#endPrefixMapping(String)
     */
    public void endPrefixMapping(String arg0) throws SAXException {
    }
    
    /*
     * @see ContentHandler#endElement(String, String, String)
     */
    public void endElement(String arg0, String arg1, String arg2)
	throws SAXException {
    }
    
    /*
     * @see ContentHandler#characters(char[], int, int)
     */
    public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
    }
    
    /*
     * @see ContentHandler#ignorableWhitespace(char[], int, int)
     */
    public void ignorableWhitespace(char[] arg0, int arg1, int arg2)
	throws SAXException {
    }
    
    /*
     * @see ContentHandler#processingInstruction(String, String)
     */
    public void processingInstruction(String arg0, String arg1)
	throws SAXException {
    }
    
    /*
     * @see ContentHandler#skippedEntity(String)
     */
    public void skippedEntity(String arg0) throws SAXException {
    }
    
    
    
    /**
     * @see Configurable#configure(Configuration)
     */
    public void configure(Configuration configuration) throws ConfigurationException {
	this.configuration = configuration;
    }
    
    /**
     * @see Contextualizable#contextualize(Context)
     */
    public void contextualize(Context context) throws ContextException {
	this.context = (DefaultContext)context;  
    }
    
    /**
     * @see Initializable#initialize()
     */
    public void initialize() throws Exception {
	ClassLoader cl = XMLConfigHandler.class.getClassLoader();
	
        
	InputStream stream =  
	    cl.getResourceAsStream("resources/empty.xml");
	
	InputSource source = new InputSource(stream);
	try {
	    
	    SAXParserFactory factory = SAXParserFactory.newInstance();
	    factory.setValidating(false);
	    XMLReader reader = factory.newSAXParser().getXMLReader();
	    reader.setContentHandler(this);
	    reader.setErrorHandler(this);
	    reader.parse(source);
	    
	} catch (Exception e) {
	    e.printStackTrace(System.err);    
	}
    }


    /**
     * @see ConfigHandler#getHost(Host)
     */
    public Host getHost(Host host) {
	return (Host)hosts.get(host.getName());
    }


    public void setSCPCommand(String scpString) {
	this.scpString = scpString;
    }

    public String getSCPCommand() {
	return scpString;
    }

    public void setChanged(boolean arg) {
	this.hasChanged = arg;
	
    }
    
    public boolean hasChanged() {
	return hasChanged;
    }

}
