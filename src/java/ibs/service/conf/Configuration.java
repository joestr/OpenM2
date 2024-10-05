/*
 * Class: Configuration.java
 */

// package:
package ibs.service.conf;

// imports:
import ibs.io.Environment;
import ibs.io.IOHelpers;
import ibs.service.email.EMailManager;
import ibs.service.email.SMTPServer;
import ibs.service.module.ConfVarContainer;
import ibs.tech.sql.DBConf;
import ibs.util.Helpers;
import ibs.util.UtilExceptions;
import ibs.util.file.FileHelpers;
import ibs.util.list.ListException;
import ibs.util.trace.FileTracer;
import ibs.util.trace.Tracer;
import ibs.util.trace.TracerConstants;
import ibs.util.trace.TracerManager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/******************************************************************************
 * This class defines the configuration properties. <BR/>
 *
 * @version     $Id: Configuration.java,v 1.24 2012/09/18 14:47:50 btatzmann Exp $
 *
 * @author      Bernd Martin (BM) 011018
 ******************************************************************************
 */
public class Configuration extends AConfigurationContainer
    implements IConfiguration
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Configuration.java,v 1.24 2012/09/18 14:47:50 btatzmann Exp $";


    // database configuration:
    /**
     * DB Configuration. <BR/>
     */
    private DBConf p_dbConf = new DBConf ();

    /**
     * WebDav Configuration. <BR/>
     */
    private WebDavConf p_webdavConf = new WebDavConf ();

    /**
     * Tracer Configuration. <BR/>
     */
    private TracerConf p_tracerConf = new TracerConf ();

    // application server and p_ssl server configurations
    /**
     * Vector of all possible servers. <BR/>
     */
    private ConfigurationServer p_configurationServers = new ConfigurationServer ();


    // server side includes (SSI):
    /**
     * Defines the URL where the ServerSideIncludes can be found. <BR/>
     */
    private String p_ssiurl = ConfigurationConstants.SSIURL_DEFAULT;

    // E-Mail:
    /**
     * The standard mail server which is used to send e-mail messages. <BR/>
     * Default: <CODE>null</CODE>
     */
    private SMTPServer p_smtpServer = null;

    /**
     * The system's mail address which is used to send e-mail messages from basic system
     * components.
     * Default: <CODE>null</CODE>
     */
    private String p_mailSystem = null;

    /**
     * The system administrator's mail address.
     * Default: <CODE>null</CODE>
     */
    private String p_mailAdmin = null;

    /**
     * The flag for wizard login. <BR/>
     */
    private boolean p_wizardLogin = false;

    /**
     * Valid domains for an ntlm authentication. <BR/>
     */
    private String p_validNTDomains = "";

    // private properties used at runtime:
    /**
     * The input file for reading the config. <BR/>
     */
    private File p_configFile = null;

    /**
     * The configuration path. <BR/>
     * This path denotes the directory where the configuration files reside.
     */
    private String p_configPath = "";

    /**
     * The configuration variables and their values. <BR/>
     */
    private ConfVarContainer p_confVars = null;



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a Configuration object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   configPath  The path of the folder which contains the
     *                      configuration files.
     *
     * @throws  ConfigurationException
     *          There occurred an error during initializing the configuration.
     */
    public Configuration (String configPath)
        throws ConfigurationException
    {
        try
        {
            // set properties:
            this.p_confVars = new ConfVarContainer ();
        } // try
        catch (ListException e)
        {
            throw new ConfigurationException (e);
        } // catch

        // init configuration:
        this.setConfigPath (configPath);
    } // Configuration


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Get the database configuration object. <BR/>
     *
     * @return  The database configuration object or <CODE>null</CODE> if it is
     *          not set.
     */
    public DBConf getDbConf ()
    {
        return this.p_dbConf;
    } // getDbConf


    /**************************************************************************
     * Set the configuration path. <BR/>
     * This methods ensures that the path ends with a file separator.
     *
     * @param   configPath  The configuration path.
     */
    public void setConfigPath (String configPath)
    {
        String configPathLocal = configPath; // variable for local assignments

        // ensure that there is a file separator at the end of the path:
        if (!configPathLocal.endsWith (File.separator))
                                        // no separator found?
        {
            // add the separator:
            configPathLocal += File.separator;
        } // if no separator found

        // set the property value:
        this.p_configPath = configPathLocal;
    } // setConfigPath


    /**************************************************************************
     * Get the configuration path. <BR/>
     *
     * @return  The path of the configuration directory.
     */
    public String getConfigPath ()
    {
        // get the property value and return the result:
        return this.p_configPath;
    } // getConfigPath


    /**************************************************************************
     * Read the ibsSystem configuration file. <BR/>
     *
     * @return  <CODE>true</CODE> if no error occurred and the operation
     *          successfully finished.
     *          <CODE>false</CODE> in case of configuration errors.
     */
    public boolean readConfig ()
    {
        String fileName;                // the name of the configuration file

        try
        {
            if (this.p_configPath != null) // there was a path defined?
            {
                // create the fully qualified file name:
                fileName = this.p_configPath + ConfigurationConstants.FILE_IBSSYSTEM;

                // check if the file exists:
                if (FileHelpers.exists (fileName)) // file exists?
                {
                    File configFile = new File (fileName);

                    // read the configuration files:
                    this.readSystemConf (configFile);
                    // set dependent properties:
                    this.setDependentProperties ();
                } // if file exists
                else
                {
                    // TODO RB: Call  
                    //          MultilingualTextProvider.getMessage (UtilExceptions.EXC_BUNDLE,
                    //              UtilExceptions.ML_E_CONFIGURATIONERRORFILENOTFOUND, env)
                    //          to get the text in the correct language
                    this.addErrorMessage (UtilExceptions.ML_E_CONFIGURATIONERRORFILENOTFOUND);
                } // else
            } // if there was a path defined
            else
            {
                // TODO RB: Call  
                //          MultilingualTextProvider.getMessage (UtilExceptions.EXC_BUNDLE,
                //              UtilExceptions.ML_E_CONFIGURATIONERRORFILENOTFOUND, env)
                //          to get the text in the correct language
                this.addErrorMessage (UtilExceptions.ML_E_CONFIGURATIONERRORFILENOTFOUND);
            } // else no path defined
        } // try
        catch (ParserConfigurationException e)
        {
            String message = "ParserConfigurationException: " +
                Helpers.getStackTraceFromThrowable (e);
            this.addErrorMessage (message);
        } // catch
        catch (SAXException e)
        {
            String message =
                "SAXException: " +
                Helpers.getStackTraceFromThrowable (e);
            this.addErrorMessage (message);
        } // catch
        catch (IOException e)
        {
            // TODO RB: Call  
            //          MultilingualTextProvider.getMessage (UtilExceptions.EXC_BUNDLE,
            //              UtilExceptions.ML_E_CONFIGURATIONERRORIOERROR, env)
            //          to get the text in the correct language
            String message = UtilExceptions.ML_E_CONFIGURATIONERRORIOERROR +
                "IOException: " + Helpers.getStackTraceFromThrowable (e);
            this.addErrorMessage (message);
        } // catch

        return this.errors == null;
    } // getConfig


    /**************************************************************************
     * Read the ibsSystem configuration file. <BR/>
     *
     * @param   configFile  The config file.
     *
     * @throws  ParserConfigurationException
     *          Error during building the document builder.
     * @throws  IOException
     *          Could not access the configuration file.
     * @throws  SAXException
     *          There occurred an error during parsing the file.
     */
    private void readSystemConf (File configFile)
        throws ParserConfigurationException, IOException, SAXException
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance ();
        DocumentBuilder db = dbf.newDocumentBuilder ();
        Document doc = db.parse (configFile);
        Element elem = doc.getDocumentElement ();

        if (elem.getNodeName () != null &&
            elem.getNodeName ().equalsIgnoreCase (ConfigurationConstants.TOK_ROOT))
        {
            this.setConfigurationProperties (elem);
            this.setDependentProperties ();
        } // if
        else
        {
            String message = "ConfigurationException: No " +
                ConfigurationConstants.TOK_ROOT +
                " found in " + ConfigurationConstants.FILE_IBSSYSTEM + ".";
            this.addErrorMessage (message);
        } // else
    } // readSystemConf


    /**************************************************************************
     * ???
     *
     * @param   elem    ???
     */
    private void setConfigurationProperties (Element elem)
    {
        this.setServersConfiguration (elem
            .getElementsByTagName (ConfigurationConstants.TOK_SERVER));
        this.p_dbConf.setDBConfiguration (elem
            .getElementsByTagName (ConfigurationConstants.TOK_DATABASE));
        this.p_tracerConf.setTracerConfiguration (elem
            .getElementsByTagName (ConfigurationConstants.TOK_TRACESERVER));
        this.setSsiConfiguration (elem
            .getElementsByTagName (ConfigurationConstants.TOK_SSIURL));
        this.setSmtpConfiguration (elem
            .getElementsByTagName (ConfigurationConstants.TOK_SMTPSERVER));
        this.setSystemMailAddress (elem
            .getElementsByTagName (ConfigurationConstants.TOK_MAILSYSTEM));
        this.setSystemAdministratorMailAddress (elem
            .getElementsByTagName (ConfigurationConstants.TOK_MAILADMIN));
        this.p_webdavConf.setWebDavConfiguration (elem
            .getElementsByTagName (ConfigurationConstants.TOK_WEBDAV));
        this.setAuthenticationConfiguration (elem
            .getElementsByTagName (ConfigurationConstants.TOK_AUTHENTICATION));

        this.p_dbConf.setSmtpServer (this.p_smtpServer);
        this.p_dbConf.setMailSystem (this.p_mailSystem);
        this.p_dbConf.setMailAdmin (this.p_mailAdmin);
    } // setConfigurationProperties


    /**************************************************************************
     * Only the first node will be set for the ssi url settings.
     * The other nodes are ignored. <BR/>
     *
     * @param dbs The nodelist which contains the tracer settings. Only the
     *            first node will be taken.
     */
    private void setSsiConfiguration (NodeList dbs)
    {
        Node db = dbs.item (0);
        NamedNodeMap attributes = db.getAttributes ();

        // set trace server name class for database
        if (attributes.getNamedItem (ConfigurationConstants.TOK_VALUE) != null)
        {
            this.p_ssiurl = attributes.getNamedItem (
                ConfigurationConstants.TOK_VALUE).getNodeValue ();
        } // if ssiurl set
        else
        {
            // TODO RB: Call  
            //          MultilingualTextProvider.getMessage (UtilExceptions.EXC_BUNDLE,
            //              UtilExceptions.ML_E_CONFIGURATIONERRORNOSSIURL,
            //              new String[] {ConfigurationConstants.FILE_IBSSYSTEM, 
            //                  ConfigurationConstants.TOK_SSIURL}, env)
            //          to get the text in the correct language
            this.addErrorMessage (UtilExceptions.ML_E_CONFIGURATIONERRORNOSSIURL +
                ConfigurationConstants.FILE_IBSSYSTEM + 
                ConfigurationConstants.TOK_SSIURL);
        } // else
    } // getSsiConfiguration


    /***************************************************************************
     * Set the SMTP server name. <BR/>
     * Only the first node will be set for the authentication settings. The
     * other nodes are ignored. <BR/>
     *
     * @param dbs The nodelist which contains the SMTP server name. Only the
     *            first node will be taken.
     */
    private void setSmtpConfiguration (NodeList dbs)
    {
        Node db = dbs.item (0);
        Node nodeAccount, nodePassword = null;
        NamedNodeMap attributes = db.getAttributes ();

        if (attributes.getNamedItem (ConfigurationConstants.TOK_NAME) != null)
        {
            this.p_smtpServer = new SMTPServer (
                attributes.getNamedItem (ConfigurationConstants.TOK_NAME).getNodeValue ());

            nodeAccount = attributes.getNamedItem (ConfigurationConstants.TOK_ACCOUNT);
            nodePassword = attributes.getNamedItem (ConfigurationConstants.TOK_PASSWORD);
            
            // check if an account has been set
            if(nodeAccount != null && !nodeAccount.getNodeValue ().isEmpty ())
            {
                // set the account 
                this.p_smtpServer.setAccountName (nodeAccount.getNodeValue ());
            } // if

            // check if a password has been set
            if(nodePassword != null && !nodePassword.getNodeValue ().isEmpty ())
            {
                // set the password 
                this.p_smtpServer.setPassword (nodePassword.getNodeValue ());
            } // if           
        } // if
        else
        {
            this.addErrorMessage ("SMTP Server not given, no Notification possible.");
        } // else
    } // setSmtpConfiguration
    
    
    /***************************************************************************
     * Set the system's mail address. <BR/>
     * Only the first node will be set for the system's mail address.
     * The other nodes are ignored. <BR/>
     *
     * @param dbs The nodelist which contains the system's mail address. Only the
     *            first node will be taken.
     */
    private void setSystemMailAddress (NodeList dbs)
    {
        Node db = dbs.item (0);
        NamedNodeMap attributes = db.getAttributes ();

        if (attributes.getNamedItem (ConfigurationConstants.TOK_NAME) != null)
        {
            this.p_mailSystem = attributes.getNamedItem (
                ConfigurationConstants.TOK_NAME).getNodeValue ();
        } // if
        else
        {
            this.addErrorMessage (
                "System's mail address not given, no Notification possible.");
        } // else
    } // setSystemMailAddress
    
    
    /***************************************************************************
     * Set the system administrators mail address. <BR/>
     * Only the first node will be set for the system administrator's mail address.
     * The other nodes are ignored. <BR/>
     *
     * @param dbs The nodelist which contains the system administrator's mail
     *          address. Only the first node will be taken.
     */
    private void setSystemAdministratorMailAddress (NodeList dbs)
    {
        Node db = dbs.item (0);
        NamedNodeMap attributes = db.getAttributes ();

        if (attributes.getNamedItem (ConfigurationConstants.TOK_NAME) != null)
        {
            this.p_mailAdmin = attributes.getNamedItem (
                ConfigurationConstants.TOK_NAME).getNodeValue ();
        } // if
        else
        {
            this.addErrorMessage (
                "System administrator's mail address not given, no Notification possible.");
        } // else
    } // setSystemMailAddress


    /***************************************************************************
     * Set the authentication settings. <BR/>
     * Only the first node will be set for the authentication settings.
     * The other nodes are ignored. <BR/>
     *
     * @param dbs The nodelist which contains the autentication settings. Only the
     *            first node will be taken.
     */
    private void setAuthenticationConfiguration (NodeList dbs)
    {
        // check if the authentication has been set:
        if (dbs != null)                // authentication set?
        {
            Node db = dbs.item (0);
            if (db != null)             // authentication node not empty?
            {
                NamedNodeMap attributes = db.getAttributes ();
                Node ntlmNode =
                    attributes.getNamedItem (ConfigurationConstants.TOK_NT_DOMAINS);
                // try to set the ntlm setting
                if (ntlmNode != null)
                {
                    this.p_validNTDomains = "," + ntlmNode.getNodeValue () + ",";
                } // if ssiurl set
            } // if authentication node not empty
        } // if authentication set
    } // setAuthenticationConfiguration


    /**************************************************************************
     * This method loops through all nodes which contain server configuration
     * settings. <BR/>
     *
     * @param servers The nodelist of server nodes
     */
    private void setServersConfiguration (NodeList servers)
    {
        // loop through server nodes
        for (int i = 0; i < servers.getLength (); i++)
        {
            this.setServerConfiguration (servers.item (i));
        } // for

        if (this.p_configurationServers.size () <= 0)
        {
            // if no configuration servers given, return an appropriate error message
            // TODO RB: Call  
            //          MultilingualTextProvider.getMessage (UtilExceptions.EXC_BUNDLE,
            //              UtilExceptions.ML_E_NOSERVERSCONFIGURED,
            //              new String[] {ConfigurationConstants.FILE_IBSSYSTEM, env)
            //          to get the text in the correct language
            this.addErrorMessage (UtilExceptions.ML_E_NOSERVERSCONFIGURED + 
                ConfigurationConstants.FILE_IBSSYSTEM);
        } // if configuration servers set
    } // setServerConfiguration


    /**************************************************************************
     * This method adds one server configuartion to the configuration settings
     * if the settings are valid. <BR/>
     *
     * @param server One node which contains a server configuration
     */
    private void setServerConfiguration (Node server)
    {
        NamedNodeMap attributes = server.getAttributes ();
        String appServer = null;
        String sslServer = null;
        String appServerPort = null;
        String sslServerPort = null;
        String ssl = null;

        appServer = this.getAttributeValue (attributes, ConfigurationConstants.TOK_NAME);
        appServerPort = this.getAttributeValue (attributes, ConfigurationConstants.TOK_PORT);
        sslServer = this.getAttributeValue (attributes, ConfigurationConstants.TOK_SSLNAME);
        sslServerPort = this.getAttributeValue (attributes, ConfigurationConstants.TOK_SSLPORT);
        ssl = this.getAttributeValue (attributes, ConfigurationConstants.TOK_SSL);

        ServerRecord s =
            new ServerRecord (appServer, sslServer, appServerPort, sslServerPort, ssl);

        this.p_configurationServers.addServerRecord (s);
    } // setServerConfiguration


    /**************************************************************************
     * Get the value of one attribute. <BR/>
     *
     * @param   attributes  The list of attributes.
     * @param   name        The name of the attribute.
     *
     * @return  The value of the attribute.
     *          <CODE>null</CODE> if the attribute does not exist.
     */
    protected String getAttributeValue (NamedNodeMap attributes, String name)
    {
        Node node = attributes.getNamedItem (name); // the node

        // check if the attribute exists:
        if (node != null)               // the attribute exists?
        {
            // get the value and return it:
            return node.getNodeValue ();
        } // if the attribute exists

        // return the default value:
        return null;
    } // getAttributeValue


    /**************************************************************************
     * Set properties which depend on properties of the configuration. <BR/>
     */
    protected void setDependentProperties ()
    {
        TracerConstants.traceServer = this.p_tracerConf.serverName;
        TracerConstants.traceServerPort = this.p_tracerConf.serverPort;
        TracerConstants.password = this.p_tracerConf.password;
        Tracer.isFileTracerEnabled = this.p_tracerConf.active;
        TracerConstants.maxMessages = this.p_tracerConf.maxMessages;
        FileTracer.debugDirectory = this.p_tracerConf.path;
        TracerManager.setTraceServerEnabled (this.p_tracerConf.active);
        EMailManager.setStandardMailServer (this.p_smtpServer);
    } // setDependentProperties


    // database configuration:
    /**************************************************************************
     * Name of server where database is located. <BR/>
     *
     * @return  The actual value.
     */
    public String getDbServerName ()
    {
        return this.p_dbConf.getDbServerName ();
    } //  getDbServerName


    /**************************************************************************
     * Name of server, where database is located. <BR/>
     *
     * @param   name    The value to be set.
     */
    public void setDbServerName (String name)
    {
        this.p_dbConf.setDbServerName (name);
    } // setDbServerName


    /**************************************************************************
     * Name of database to access. <BR/>
     *
     * @return  The actual value.
     */
    public String getDbSid ()
    {
        return this.p_dbConf.getDbSid ();
    } // getDbName


    /**************************************************************************
     * Name of database to access. <BR/>
     *
     * @param   name    The value to be set.
     */
    public void setDbSid (String name)
    {
        this.p_dbConf.setDbSid (name);
    } // setDbName


    /**************************************************************************
     * DB user's name. <BR/>
     *
     * @return  The actual value.
     */
    public String getDbUserName ()
    {
        return this.p_dbConf.getDbUserName ();
    } // getDbUserName


    /**************************************************************************
     * DB user's name. <BR/>
     *
     * @param   name    The value to be set.
     */
    public void setDbUserName (String name)
    {
        this.p_dbConf.setDbUserName (name);
    } // setDbUserName


    /**************************************************************************
     * DB user's password. <BR/>
     *
     * @param   password    The value to be set.
     */
    public void setDbPassword (byte[] password)
    {
        this.p_dbConf.setDbPassword (password);
    } // setDbPassword


    /**************************************************************************
     * DB user's password. <BR/>
     *
     * @return  The actual value.
     */
    public byte[] getDbPassword ()
    {
        return this.p_dbConf.getDbPassword ();
    } // setDbPassword


    /**************************************************************************
     * Connection string to database. <BR/>
     *
     * @return  The actual value.
     */
    public String getDbConnectionString ()
    {
        return this.p_dbConf.getDbConnectionString ();
    } // getConnectionString


    /**************************************************************************
     * Connection string to database. <BR/>
     *
     * @param   connectionString    The value to be set.
     */
    public void setDbConnectionString (String connectionString)
    {
        this.p_dbConf.setDbConnectionString (connectionString);
    } // setConnectionString


    /**************************************************************************
     * The class for the JDBC driver.
     *
     * @return  The actual value.
     */
    public String getDbJdbcDriverClass ()
    {
        return this.p_dbConf.getDbJdbcDriverClass ();
    } // getJDBCDriverClass


    /**************************************************************************
     * The class for the JDBC driver.
     *
     * @param   className   The value to be set.
     */
    public void setDbJdbcDriverClass (String className)
    {
        this.p_dbConf.setDbJdbcDriverClass (className);
    } // setJDBCDriverClass


    /**************************************************************************
     * Maximum time to wait for completion of login (given in seconds). <BR/>
     *
     * @return  The actual value.
     */
    public int getDbLoginTimeout ()
    {
        return this.p_dbConf.getDbLoginTimeout ();
    } // getDbLoginTimeout


    /**************************************************************************
     * Maximum time to wait for completion of login (given in seconds). <BR/>
     *
     * @param   seconds The value to be set.
     */
    public void setDbLoginTimeout (int seconds)
    {
        this.p_dbConf.setDbLoginTimeout (seconds);
    } // setDbLoginTimeout


    /**************************************************************************
     * Maximum time to wait for completion of a query (given in seconds). <BR/>
     *
     * @return  The actual value.
     */
    public int getDbQueryTimeout ()
    {
        return this.p_dbConf.getDbQueryTimeout ();
    } // getDbQueryTimeout


    /**************************************************************************
     * Maximum time to wait for completion of a query (given in seconds). <BR/>
     *
     * @param   seconds The value to be set.
     */
    public void setDbQueryTimeout (int seconds)
    {
        this.p_dbConf.setDbQueryTimeout (seconds);
    } // setDbQueryTimeout


    /**************************************************************************
     * Name of dbms used. <BR/>
     *
     * @return  The actual value.
     */
    public String getDbType ()
    {
        return this.p_dbConf.getDbType ();
    } // getDbType


    /**************************************************************************
     * Name of dbms used. <BR/>
     *
     * @param   dbms    The value to be set.
     */
    public void setDbType (String dbms)
    {
        this.p_dbConf.setDbType (dbms);
    } // setDbType


    // application server and p_ssl server configurations
    /**************************************************************************
     * Vector of all possible servers. <BR/>
     *
     * @return  The actual value.
     */
    public ConfigurationServer getConfigurationServers ()
    {
        return this.p_configurationServers;
    } // getConfigurationServers


    /**************************************************************************
     * Vector of all possible servers. <BR/>
     *
     * @param   server  The value to be set.
     */
    public void setConfigurationServer (ServerRecord server)
    {
        this.p_configurationServers.addServerRecord (server);
    } // setConfigurationServer


    // server side includes (SSI):
    /**************************************************************************
     * Defines the URL where the ServerSideIncludes can be found. <BR/>
     *
     * @return  The actual value.
     */
    public String getSsiurl ()
    {
        return this.p_ssiurl;
    } //getSsiurl


    /**************************************************************************
     * Defines the URL where the ServerSideIncludes can be found. <BR/>
     *
     * @param   url The value to be set.
     */
    public void setSsiurl (String url)
    {
        this.p_ssiurl = url;
    } // setSsiurl


    // tracer:
    /**************************************************************************
     * Is the trace enabled?. <BR/>
     * Default: <CODE>false</CODE>
     *
     * @return  The actual value.
     */
    public boolean getTracerActive ()
    {
        return this.p_tracerConf.active;
    } // getTracerActive


    /**************************************************************************
     * Set the trace enabled/disabled?. <BR/>
     * Default: <CODE>false</CODE>
     *
     * @param   bool    The value to be set.
     */
    public void setTracerActive (boolean bool)
    {
        this.p_tracerConf.active = bool;
    } // setTracerActive


    /**************************************************************************
     * The directory where the tracing files shall be written. <BR/>
     * Default: <CODE>"c:\Inetpub\wwwroot\m2\debug\"</CODE>
     *
     * @return  The actual value.
     */
    public String getTracerPath ()
    {
        return this.p_tracerConf.path;
    } // getTracerPath


    /**************************************************************************
     * The directory where the tracing files shall be written. <BR/>
     * Default: <CODE>"c:\Inetpub\wwwroot\m2\debug\"</CODE>
     *
     * @param   path    The value to be set.
     */
    public void getTracerPath (String path)
    {
        this.p_tracerConf.path = path;
    } // getTracerPath


    /**************************************************************************
     * The port of the trace server. <BR/>
     * Default: <CODE>-1</CODE>
     *
     * @return  The actual value.
     */
    public int getTraceServerPort ()
    {
        return this.p_tracerConf.serverPort;
    } // getTraceServerPort


    /**************************************************************************
     * The port of the trace server. <BR/>
     * Default: <CODE>-1</CODE>
     *
     * @param   port    The value to be set.
     */
    public void setTraceServerPort (int port)
    {
        this.p_tracerConf.serverPort = port;
    } // setTraceServerPort


    /**************************************************************************
     * The name of the trace server. <BR/>
     *
     * @return  The actual value.
     */
    public String getTraceServerName ()
    {
        return this.p_tracerConf.serverName;
    } // getTraceServer


    /**************************************************************************
     * The name of the trace server. <BR/>
     *
     * @param   serverName  The value to be set.
     */
    public void getTraceServerName (String serverName)
    {
        this.p_tracerConf.serverName = serverName;
    } // getTraceServerName


    /**************************************************************************
     * This password is used to authenticate the tracer clients. <BR/>
     * It is read from the configuration file ibssystem.cfg. <BR/>
     * Default:
     *      <CODE>ibs.util.trace.TracerConstants.TRACERPASSWORD_DEFAULT</CODE>
     *
     * @return  The actual value.
     */
    public byte[] getTracerPassword ()
    {
        return this.p_tracerConf.password;
    } // getTracerPassword


    /**************************************************************************
     * This password is used to authenticate the tracer clients. <BR/>
     * It is read from the configuration file ibssystem.cfg. <BR/>
     * Default:
     *      <CODE>ibs.util.trace.TracerConstants.TRACERPASSWORD_DEFAULT</CODE>
     *
     * @param   password    The value to be set.
     */
    public void setTracerPassword (byte[] password)
    {
        this.p_tracerConf.password = password;
    } // setTracerPassword


    /**************************************************************************
     * The SMTP server which is used to send e-mail messages. <BR/>
     * Default: <CODE>null</CODE>
     *
     * @return  The actual value.
     */
    public SMTPServer getSmtpServer ()
    {
        return this.p_smtpServer;
    } // getSmtpServer


    /**************************************************************************
     * Set name of SMTP server. <BR/>
     *
     * @param   serverName  The value to be set.
     */
    public void setSmtpServer (SMTPServer smtpServer)
    {
        this.p_smtpServer = smtpServer;
    } // setSmtpServer


    /**************************************************************************
     * The system's mail address. <BR/>
     * It is used for sending e-mails from basic system components.
     * 
     * @return  The actual value.
     */
    public String getMailSystem ()
    {
        return this.p_mailSystem;
    } // getMailSystem


    /**************************************************************************
     * Set system's mail address. <BR/>
     *
     * @param   mailSystem  The value to be set.
     */
    public void setMailSystem (String mailSystem)
    {
        this.p_mailSystem = mailSystem;
    } // setMailSystem


    /**************************************************************************
     * The system administrator's mail address. <BR/>
     * It is used for sending e-mails from basic system components.
     * 
     * @return  The actual value.
     */
    public String getMailAdmin ()
    {
        return this.p_mailAdmin;
    } // getMailAdmin


    /**************************************************************************
     * Set system administrator's mail address. <BR/>
     *
     * @param   mailAdmin  The value to be set.
     */
    public void setMailAdmin (String mailAdmin)
    {
        this.p_mailAdmin = mailAdmin;
    } // setMailAdmin


    /**************************************************************************
     * The port of the trace server. <BR/>
     *
     * @return  The actual value.
     */
    public boolean getWizardLogin ()
    {
        return this.p_wizardLogin;
    } // getTraceServerPort


    /**************************************************************************
     * The port of the trace server. <BR/>
     *
     * @param   value   The value to be set.
     */
    public void setWizardLogin (boolean value)
    {
        this.p_wizardLogin = value;
    } // setTraceServerPort


    // webdav configuration:
    /**************************************************************************
     * get webdav url. <BR/>
     *
     * @param    environment    The current environment.
     *
     * @return  The actual value.
     */
    public String getWebDavURL (Environment environment)
    {
        // IBS-127 create the full url from the relative webdavurl
        return Helpers.createUrlString (environment.getServerName () + "/" +
            this.p_webdavConf.webdavurl);
    } //  getWebDavURL


    /**************************************************************************
     * set webdav url. <BR/>
     *
     * @param   webdavUrl   The value to be set.
     */
    public void setWebDavURL (String webdavUrl)
    {
        String webdavUrlLocal = webdavUrl; // variable for local assignments

        // IBS-127 webdavurl is now a relative path
        // remove leading path separators
        while (webdavUrlLocal.indexOf ("/") == 0)
        {
            webdavUrlLocal = webdavUrlLocal.substring (1, webdavUrlLocal.length ());
        } // while

        this.p_webdavConf.webdavurl = webdavUrlLocal;
    } // setWebDavURL


    /**************************************************************************
     * get webdav path. <BR/>
     *
     * @return  The actual value.
     */
    public String getWebDavPath ()
    {
        return this.p_webdavConf.webdavpath;
    } //  getWebDavPath


    /**************************************************************************
     * set webdav path. <BR/>
     *
     * @param   webdavpath  The value to be set.
     */
    public void setWebDavPath (String webdavpath)
    {
        this.p_webdavConf.webdavpath = webdavpath;
    } // setWebDavPath


    /**************************************************************************
     * Get configuration variables. <BR/>
     *
     * @return  The coniguration variable container.
     */
    public ConfVarContainer getConfVars ()
    {
        return this.p_confVars;
    } // getConfVars


    /***************************************************************************
     * Get valid NT domains. <BR/>
     *
     * @return  The actual value.
     */
    public String getNTDomains ()
    {
        return this.p_validNTDomains;
    } // getNTDomains


    /***************************************************************************
     * Set the valid NT domains. <BR/>
     *
     * @param   validDomains    The NT domains to be set.
     */
    public void setNTDomains (String validDomains)
    {
        this.p_validNTDomains = validDomains;
    } // setNTDomains


    /**************************************************************************
     * If errors occurred while reading the configuration file then they will
     * be returned with this method, otherwise null is returned. <BR/>
     *
     * @return This method returns the errorstring(s) or null if no
     *         errors occurred.
     */
    public StringBuffer getErrors ()
    {
        return this.errors;
    } // hasErrors


    /**************************************************************************
     * Returns the configuration file object. <BR/>
     *
     * @return This method returns the configuration file object.
     */
    public File getConfigFile ()
    {
        return this.p_configFile;
    } // hasErrors


    /**************************************************************************
     * Get the value of a specific field. <BR/>
     *
     * @param   f       The field.
     *
     * @return  The value of the field as object.
     *
     * @throws  IllegalAccessException
     *          There is no access to the field.
     */
    protected Object getFieldValue (Field f) throws IllegalAccessException
    {
        return f.get (this);
    } // getFieldValue


    /**************************************************************************
     * Get all fields which are declared within this container. <BR/>
     *
     * @return  The fields.
     */
    protected Field[] getDeclaredFields ()
    {
        Field[] fields = this.getClass ().getDeclaredFields ();
        Vector<Field> v = new Vector<Field> ();

        // loop through elements and search only for elements to print to.
        // store them temporarily into a vector
        for (int i = 0; i < Array.getLength (fields); i++)
        {
            // those fields are excluded from the list to show to
            if (!fields[i].getName ().equalsIgnoreCase ("errors") &&
                !fields[i].getName ().equalsIgnoreCase ("configFile") &&
                !fields[i].getName ().equalsIgnoreCase (ConfigurationConstants.TOK_PASSWORD))
            {
                v.add (fields[i]);
            } // if field to display
        } // for

        Field[] ret = new Field [v.size ()];

        for (int i = 0; i < ret.length; i++)
        {
            ret[i] = v.elementAt (i);
        } // for

        return ret;
    } // getDeclaredFields


    /**************************************************************************
     * A main method to run the configuration out of context. <BR/>
     *
     * @param   args    The arguments.
     */
    public static void main (String[] args)
    {
        try
        {
            Configuration c = new Configuration (args[0]);
            if (!c.readConfig ())
            {
                IOHelpers.printMessage ("errors occurred: " + c.errors);
            } // if
            else
            {
                IOHelpers.printMessage ("read config, out = " + c.toString ());
            } // else
        } // try
        catch (ConfigurationException e)
        {
            IOHelpers.printError ("Error in Configuration.main", e, true);
        } // catch

        System.out.println ("main done.");
        System.exit (0);
    } // main

} // class Configuration
