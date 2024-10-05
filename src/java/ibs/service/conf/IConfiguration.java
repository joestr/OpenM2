/*
 * Class: Configuration.java
 */

// package:
package ibs.service.conf;

// imports:
import ibs.io.Environment;
import ibs.service.conf.ConfigurationException;
import ibs.service.conf.ConfigurationServer;
import ibs.service.conf.ServerRecord;
import ibs.service.email.SMTPServer;
import ibs.service.module.ConfVarContainer;

import java.io.FileNotFoundException;
import java.io.IOException;


/******************************************************************************
 * This interface defines the configuration. <BR/>
 *
 * @version     $Id: IConfiguration.java,v 1.14 2012/09/18 14:47:50 btatzmann Exp $
 *
 * @author      Bernd Martin (BM) 011018
 ******************************************************************************
 */
public interface IConfiguration
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: IConfiguration.java,v 1.14 2012/09/18 14:47:50 btatzmann Exp $";


    /**
     * Constants for undefined timeout values. <BR/>
     */
    public static final int UNDEFINED_QUERYTIMEOUT  = -1;

    /**
     * ???
     */
    public static final int UNDEFINED_LOGINTIMEOUT  = -1;


    /**************************************************************************
     * Set the configuration path. <BR/>
     * This methods ensures that the path ends with a file separator.
     *
     * @param   configPath  The configuration path.
     */
    public void setConfigPath (String configPath);


    /**************************************************************************
     * Get the configuration path. <BR/>
     *
     * @return  The path of the configuration directory.
     */
    public String getConfigPath ();


    /**************************************************************************
     * Reads the configuration from the given path.
     *
     * @return  <CODE>true</CODE> if no error occurred,
     *          <CODE>false</CODE> if an error occurred while reading
     *          the configuration file.
     *
     * @throws  FileNotFoundException
     *          The configuration file was not found.
     * @throws  IOException
     *          Exception during accessing the file.
     * @throws  ConfigurationException
     *          Something in the configuration is wrong.
     */
    public boolean readConfig ()
        throws FileNotFoundException, IOException, ConfigurationException;


    // database configuration:
    /**************************************************************************
     * Name of server, where database is located. <BR/>
     *
     * @return  The name of the database server.
     */
    public String getDbServerName ();


    /**************************************************************************
     * Name of server, where database is located. <BR/>
     *
     * @param   name    The name of the database server.
     */
    public void setDbServerName (String name);


    /**************************************************************************
     * Name of database to access. <BR/>
     *
     * @return  The SID of the database.
     */
    public String getDbSid ();


    /**************************************************************************
     * Name of database (sid) to access. <BR/>
     *
     * @param   name    The SID of the database.
     */
    public void setDbSid (String name);


    /**************************************************************************
     * DB user's name. <BR/>
     *
     * @return  The user name for accessing the database.
     */
    public String getDbUserName ();


    /**************************************************************************
     * DB user's name. <BR/>
     *
     * @param   name    The user name for accessing the database.
     */
    public void setDbUserName (String name);


    /**************************************************************************
     * DB user's password. <BR/>
     *
     * @return   The password for the database user.
     */
    public byte[] getDbPassword ();


    /**************************************************************************
     * DB user's password. <BR/>
     *
     * @param   password    The password for the database user.
     */
    public void setDbPassword (byte[] password);


    /**************************************************************************
     * Connectionstring to database. <BR/>
     *
     * @return   The database connection string.
     */
    public String getDbConnectionString ();


    /**************************************************************************
     * Connectionstring to database. <BR/>
     *
     * @param   connectionString    The database connection string.
     */
    public void setDbConnectionString (String connectionString);


    /**************************************************************************
     * The class for the JDBC driver.
     *
     * @return  The JDBC driver class.
     */
    public String getDbJdbcDriverClass ();


    /**************************************************************************
     * The class for the JDBC driver.
     *
     * @param   className   The JDBC driver class.
     */
    public void setDbJdbcDriverClass (String className);


    /**************************************************************************
     * Maximum time to wait for completion of login (given in seconds). <BR/>
     *
     * @return  The login timeout for the database.
     */
    public int getDbLoginTimeout ();


    /**************************************************************************
     * Maximum time to wait for completion of login (given in seconds). <BR/>
     *
     * @param   seconds The login timeout for the database.
     */
    public void setDbLoginTimeout (int seconds);


    /**************************************************************************
     * Maximum time to wait for completion of a query (given in seconds). <BR/>
     *
     * @return  The query timeout for the database.
     */
    public int getDbQueryTimeout ();


    /**************************************************************************
     * Maximum time to wait for completion of a query (given in seconds). <BR/>
     *
     * @param   seconds The query timeout for the database.
     */
    public void setDbQueryTimeout (int seconds);


    /**************************************************************************
     * Type of db used. <BR/>
     *
     * @return  The database type.
     */
    public String getDbType ();


    /**************************************************************************
     * Type of dbms used. <BR/>
     *
     * @param   dbms    The database type.
     */
    public void setDbType (String dbms);


    // application server and SSL server configurations
    /**************************************************************************
     * Vector of all possible servers. <BR/>
     *
     * @return  All available configuration servers.
     */
    public ConfigurationServer getConfigurationServers ();


    /**************************************************************************
     * Vector of all possible servers. <BR/>
     *
     * @param   server  The new server to be set.
     */
    public void setConfigurationServer (ServerRecord server);


    // server side includes (SSI):
    /**************************************************************************
     * Defines the URL where the ServerSideIncludes can be found. <BR/>
     *
     * @return  URL for server side includes.
     */
    public String getSsiurl ();


    /**************************************************************************
     * Defines the URL where the ServerSideIncludes can be found. <BR/>
     *
     * @param   url     URL for server side includes.
     */
    public void setSsiurl (String url);


    // tracer:
    /**************************************************************************
     * Is the trace enabled?. <BR/>
     *
     * @return  <CODE>true</CODE> if the tracer is active,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean getTracerActive ();


    /**************************************************************************
     * Set the trace enabled/disabled?. <BR/>
     *
     * @param   bool    The value to which to set the tracer's state.
     */
    public void setTracerActive (boolean bool);


    /**************************************************************************
     * The directory where the tracing files shall be written. <BR/>
     *
     * @return  The tracer path.
     */
    public String getTracerPath ();


    /**************************************************************************
     * The directory where the tracing files shall be written. <BR/>
     *
     * @param   path    The tracer path.
     */
    public void getTracerPath (String path);


    /**************************************************************************
     * The port of the trace server. <BR/>
     * <CODE>-1</CODE> means no specific port.
     *
     * @return  The port of the trace server.
     */
    public int getTraceServerPort ();


    /**************************************************************************
     * The port of the trace server. <BR/>
     *
     * @param   port    The port of the trace server.
     */
    public void setTraceServerPort (int port);


    /**************************************************************************
     * The name of the trace server. <BR/>
     *
     * @return  The name of the trace server.
     */
    public String getTraceServerName ();


    /**************************************************************************
     * The name of the trace server. <BR/>
     *
     * @param   serverName  The name of the trace server.
     */
    public void getTraceServerName (String serverName);


    /**************************************************************************
     * This password is used to authenticate the tracer clients. <BR/>
     * It is read from the configuration file ibssystem.cfg. <BR/>
     * Default:
     *      <CODE>ibs.util.trace.TracerConstants.TRACERPASSWORD_DEFAULT</CODE>
     *
     * @return  The tracer password.
     */
    public byte[] getTracerPassword ();


    /**************************************************************************
     * This password is used to authenticate the tracer clients. <BR/>
     * It is read from the configuration file ibssystem.cfg. <BR/>
     * Default:
     *      <CODE>ibs.util.trace.TracerConstants.TRACERPASSWORD_DEFAULT</CODE>
     *
     * @param   password    The tracer password.
     */
    public void setTracerPassword (byte[] password);


    // E-Mail:
    /**************************************************************************
     * The SMTP server which is used to send e-mail messages. <BR/>
     *
     * @return  The name of the SMTP server.
     */
    public SMTPServer getSmtpServer ();


    /**************************************************************************
     * The SMTP server which is used to send e-mail messages. <BR/>
     *
     * @param   smtpServer  The SMTP server.
     */
    public void setSmtpServer (SMTPServer smtpServer);


    /**************************************************************************
     * The system's mail address. <BR/>
     * It is used for sending e-mails from basic system components.
     * 
     * @return	The actual value.
     */
    public String getMailSystem ();


    /**************************************************************************
     * Set system's mail address. <BR/>
     *
     * @param   mailSystem  The value to be set.
     */
    public void setMailSystem (String mailSystem);


    /**************************************************************************
     * The system administrator's mail address. <BR/>
     * It is used for sending e-mails from basic system components.
     * 
     * @return	The actual value.
     */
    public String getMailAdmin ();


    /**************************************************************************
     * Set system administrator's mail address. <BR/>
     *
     * @param   mailAdmin  The value to be set.
     */
    public void setMailAdmin (String mailAdmin);


    /**************************************************************************
     * Returns the flag if the wizard login is activated. <BR/>
     *
     * @return  <CODE>true</CODE> if the login wizard is activated,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean getWizardLogin ();


    /**************************************************************************
     * Sets the flag for wizard login. <BR/>
     *
     * @param   value   The value to which to set the login wizard flag.
     */
    public void setWizardLogin (boolean value);


    // webdav configuration:
    /**************************************************************************
     * get webdav url. <BR/>
     *
     * @param    environment    The current environment.
     *
     * @return  The webdav url.
     */
    public String getWebDavURL (Environment environment);


    /**************************************************************************
     * set webdav url. <BR/>
     *
     * @param   webdavurl   The webdav url.
     */
    public void setWebDavURL (String webdavurl);


    /**************************************************************************
     * get webdav path. <BR/>
     *
     * @return  The webdav path.
     */
    public String getWebDavPath ();


    /**************************************************************************
     * set webdav path. <BR/>
     *
     * @param   webdavpath  The webdav path.
     */
    public void setWebDavPath (String webdavpath);


    /**************************************************************************
     * Get configuration variables. <BR/>
     *
     * @return  The coniguration variable container.
     */
    public ConfVarContainer getConfVars ();


    /***************************************************************************
     * Get valid NT domains. <BR/>
     *
     * @return  The NT domains.
     */
    public String getNTDomains ();


    /**************************************************************************
     * Check if errors occurred while reading the configuration file. <BR/>
     *
     * @return The errors occurred while reading the configuration file
     *         concatenated together as a string.
     */
    public StringBuffer getErrors ();

} // interface IConfiguration
