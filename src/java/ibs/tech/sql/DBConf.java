/**
 * Class: DBConf.java
 */

// package:
package ibs.tech.sql;

// imports:
//KR TODO: unsauber
import ibs.di.DataElement;
//KR TODO: unsauber
import ibs.di.ValueDataElement;
//KR TODO: unsauber
import ibs.ml.MultilingualTextProvider;
import ibs.service.conf.AConfigurationContainer;
import ibs.service.email.SMTPServer;
import ibs.tech.sql.DBConfConstants;
import ibs.tech.sql.SQLConstants;
import ibs.util.DateTimeHelpers;
import ibs.util.UtilExceptions;
import ibs.util.file.FileHelpers;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Vector;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/******************************************************************************
 * Configuration for database. <BR/>
 *
 * @version     $Id: DBConf.java,v 1.16 2012/09/18 14:47:50 btatzmann Exp $
 *
 * @author      Bernd Martin (BM) Oct 22, 2001
 ******************************************************************************
 */
public class DBConf extends AConfigurationContainer
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DBConf.java,v 1.16 2012/09/18 14:47:50 btatzmann Exp $";


    /**
     * driver class. <BR/>
     */
    protected String jdbcDriverClass = "";

    /**
     * connection string. <BR/>
     */
    protected String connectionString = "";

    /**
     * Name of server, where database is located. <BR/>
     */
    protected String serverName = null;

    /**
     * Name of database (sid) to access. <BR/>
     */
    protected String sid = null;

    /**
     * DB user's name. <BR/>
     */
    protected String userName = null;

    /**
     * DB user's password. <BR/>
     */
    protected byte[] password = null;

    /**
     * Maximum time to wait for completion of login (given in seconds). <BR/>
     */
    protected int loginTimeout = 0;

    /**
     * Maximum time to wait for completion of a query (given in seconds). <BR/>
     */
    protected int queryTimeout = 0;

    /**
     * Name of dbms used. <BR/>
     */
    protected String type = null;

    /**
     * Type of connection to database. <BR/>
     */
    protected String connectionType = null;

    /**
     * Number of statically open connections from m2 to the database. <BR/>
     */
    protected int regularSize = 1;

    /**
     * Number of statically open + dynamically opened connections from m2
     * to the database. <BR/>
     */
    protected int maxSize = 5;

    /**
     * The directory for writing the logs. <BR/>
     */
    private String p_logDir = null;

    /**
     * The log for the db configuration. <BR/>
     */
    private DBLog p_log = null;

    /**
     * The name of the directory where the database specific scripts are stored.
     * <BR/>
     */
    private String p_dbDir = SQLConstants.DBDIR_UNDEF;

    // E-Mail:
    /**
     * The SMTP server which is used to send e-mail messages. <BR/>
     * Default: <CODE>null</CODE>
     */
    private SMTPServer p_smtpServer = null;

    /**
     * The system's mail address which is used to send e-mail messages
     * components.
     * Default: <CODE>null</CODE>
     */
    private String p_mailSystem = null;

    /**
     * The system administrator's mail address.
     * Default: <CODE>null</CODE>
     */
    private String p_mailAdmin = null;


    /**************************************************************************
     * Name of server, where database is located. <BR/>
     *
     * @return  The name of the database server.
     */
    public String getDbServerName ()
    {
        return this.serverName;
    } //  getDbServerName


    /**************************************************************************
     * Name of server, where database is located. <BR/>
     *
     * @param   name    The name of the database server.
     */
    public void setDbServerName (String name)
    {
        this.serverName = name;
    } // setDbServerName


    /**************************************************************************
     * Name of database to access. <BR/>
     *
     * @return  The name of the database.
     */
    public String getDbSid ()
    {
        return this.sid;
    } // getDbName


    /**************************************************************************
     * Name of database to access. <BR/>
     *
     * @param   name    The name of the database.
     */
    public void setDbSid (String name)
    {
        this.sid = name;
    } // setDbName


    /**************************************************************************
     * DB user's name. <BR/>
     *
     * @return  The name of the user used to log in.
     */
    public String getDbUserName ()
    {
        return this.userName;
    } // getDbUserName


    /**************************************************************************
     * DB user's name. <BR/>
     *
     * @param   name    The name of the user used to log in.
     */
    public void setDbUserName (String name)
    {
        this.userName = name;
    } // setDbUserName


    /**************************************************************************
     * DB user's password. <BR/>
     *
     * @param   password    The password.
     */
    public void setDbPassword (byte[] password)
    {
        this.password = password;
    } // setDbPassword


    /**************************************************************************
     * DB user's password. <BR/>
     *
     * @return  The password.
     */
    public byte[] getDbPassword ()
    {
        return this.password;
    } // setDbPassword


    /**************************************************************************
     * Connectionstring to database. <BR/>
     *
     * @return  The connection string.
     */
    public String getDbConnectionString ()
    {
        return this.connectionString;
    } // getConnectionString


    /**************************************************************************
     * Connectionstring to database. <BR/>
     *
     * @param   connectionString    The connection string.
     */
    public void setDbConnectionString (String connectionString)
    {
        this.connectionString = connectionString;
    } // setConnectionString


    /**************************************************************************
     * The class for the JDBC driver.
     *
     * @return  The jdbc driver class.
     */
    public String getDbJdbcDriverClass ()
    {
        return this.jdbcDriverClass;
    } // getJDBCDriverClass


    /**************************************************************************
     * The class for the JDBC driver.
     *
     * @param   className   The jdbc driver class.
     */
    public void setDbJdbcDriverClass (String className)
    {
        this.jdbcDriverClass = className;
    } // setJDBCDriverClass


    /**************************************************************************
     * Maximum time to wait for completion of login (given in seconds). <BR/>
     *
     * @return  The login timeout.
     */
    public int getDbLoginTimeout ()
    {
        return this.loginTimeout;
    } // getDbLoginTimeout


    /**************************************************************************
     * Maximum time to wait for completion of login (given in seconds). <BR/>
     *
     * @param   seconds The login timeout.
     */
    public void setDbLoginTimeout (int seconds)
    {
        this.loginTimeout = seconds;
    } // setDbLoginTimeout


    /**************************************************************************
     * Maximum time to wait for completion of a query (given in seconds). <BR/>
     *
     * @return  The query timeout.
     */
    public int getDbQueryTimeout ()
    {
        return this.queryTimeout;
    } // getDbQueryTimeout


    /**************************************************************************
     * Maximum time to wait for completion of a query (given in seconds). <BR/>
     *
     * @param   seconds The query timeout.
     */
    public void setDbQueryTimeout (int seconds)
    {
        this.queryTimeout = seconds;
    } // setDbQueryTimeout


    /**************************************************************************
     * Name of dbms used. <BR/>
     *
     * @return  The dbms type.
     */
    public String getDbType ()
    {
        return this.type;
    } // getDbType


    /**************************************************************************
     * Name of dbms used. <BR/>
     *
     * @param   dbms    The dbms type.
     */
    public void setDbType (String dbms)
    {
        this.type = dbms;

        // compute database directory from database type:
        if (this.type.equals (SQLConstants.DB_UNDEF_STR))
        {
            this.p_dbDir = SQLConstants.DBDIR_UNDEF;
        } // if
        else if (this.type.equals (SQLConstants.DB_MSSQL_STR))
        {
            this.p_dbDir = SQLConstants.DBDIR_MSSQL;
        } // if
        else if (this.type.equals (SQLConstants.DB_ORACLE_STR))
        {
            this.p_dbDir = SQLConstants.DBDIR_ORACLE;
        } // if
        else if (this.type.equals (SQLConstants.DB_DB2_STR))
        {
            this.p_dbDir = SQLConstants.DBDIR_DB2;
        } // if
    } // setDbType


    /**************************************************************************
     * Get the log. <BR/>
     *
     * @return  The log.
     *          <CODE>null</CODE> if no log is set.
     */
    public DBLog getDbLog ()
    {
        return this.p_log;
    } // getDbLog


    /**************************************************************************
     * Set log. <BR/>
     *
     * @param   log The log  to be set.
     */
    public void setDbLog (DBLog log)
    {
        this.p_log = log;
    } // setDbLog


    /**************************************************************************
     * Set log directory. <BR/>
     *
     * @param   logDir  The log directory to be set.
     */
    public void setDbLogDir (String logDir)
    {
        this.p_logDir = logDir;

        // check if the database configuration was already read:
        if (this.sid != null && this.sid.length () > 0 &&
            this.userName != null && this.userName.length () > 0)
        {
            // open the log:
            this.createDbLog ();
        } // if
    } // setDbLogDir


    /**************************************************************************
     * This method gets the dbDir. <BR/>
     *
     * @return Returns the dbDir.
     */
    public String getDbDir ()
    {
        //get the property value and return the result:
        return this.p_dbDir;
    } // getDbDir


    /**************************************************************************
     * This method sets the dbDir. <BR/>
     *
     * @param dbDir The dbDir to set.
     */
    public void setDbDir (String dbDir)
    {
        //set the property value:
        this.p_dbDir = dbDir;
    } // setDbDir


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
     * @return	The actual value.
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
     * @return	The actual value.
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
     * Get the value of a specific database configuration field. <BR/>
     *
     * @param   f       The field for which to get the configuration value.
     *
     * @return  The value of the field as object.
     *
     * @throws  IllegalAccessException
     *          If the underlying field is inaccessible.
     */
    protected Object getFieldValue (Field f) throws IllegalAccessException
    {
        return f.get (this);
    } // getFieldValue


    /**************************************************************************
     * Get all declared fields for the database configuration. <BR/>
     *
     * @return  The declared fields. Empty array if there where no fields found.
     */
    protected Field[] getDeclaredFields ()
    {
        Field[] fields = this.getClass ().getDeclaredFields ();
        Vector<Field> v = new Vector<Field> ();

        for (int i = 0; i < Array.getLength (fields); i++)
        {
            if (!fields[i].getName ().equalsIgnoreCase (DBConfConstants.TOK_PASSWORD))
            {
                v.add (fields[i]);
            } // if field to display
        } // for

        // convert the vector into an array:
        Field[] ret = v.toArray (new Field [v.size ()]);

        return ret;
    } // getDeclaredFields


    /**************************************************************************
     * Only the first node will be set for the database settings.
     * The other nodes are ignored. <BR/>
     *
     * @param dbs The nodelist which contains the database settings. Only the
     *            first node will be taken.
     */
    public void setDBConfiguration (NodeList dbs)
    {
        String mustBeSet = " must be set in the configuration!";
        String isNotSet = " is not set in the configuration!";
        Node db = dbs.item (0);
        NamedNodeMap attributes = db.getAttributes ();

        // set jdbc driver class for database
        if (attributes.getNamedItem (DBConfConstants.TOK_JDBCDRIVERCLASS) != null)
        {
            this.jdbcDriverClass = attributes.getNamedItem (
                DBConfConstants.TOK_JDBCDRIVERCLASS).getNodeValue ();
        } // if JDBC driverclass set
        else
        {
            this.addErrorMessage (DBConfConstants.TOK_JDBCDRIVERCLASS + " (" +
                DBConfConstants.TOK_DATABASE + ")" + mustBeSet);
        } // else

        // set jdbc driver class for database
        if (attributes.getNamedItem (DBConfConstants.TOK_JDBCDCONNECTIONSTRING) != null)
        {
            this.connectionString = attributes.getNamedItem (
                DBConfConstants.TOK_JDBCDCONNECTIONSTRING).getNodeValue ();
        } // if JDBC connection string set
        else
        {
            this.addErrorMessage (DBConfConstants.TOK_JDBCDCONNECTIONSTRING + " (" +
                DBConfConstants.TOK_DATABASE + ")" + mustBeSet);
        } // else

        // set type for database
        if (attributes.getNamedItem (DBConfConstants.TOK_DBTYPE) != null)
        {
            String tmpValue =
                attributes.getNamedItem (DBConfConstants.TOK_DBTYPE).getNodeValue ();

            // map db-type string (from configuration) to internally used
            // int-values MS SQL 6.5?
            if (tmpValue.equalsIgnoreCase (SQLConstants.DB_MSSQL_STR))
                                        // database is MSSQL?
            {
                SQLConstants.DB_TYPE = SQLConstants.DB_MSSQL;
            } // if database is MSSQL
            else if (tmpValue.equalsIgnoreCase (SQLConstants.DB_ORACLE_STR))
                                        // database is oracle?
            {
                SQLConstants.DB_TYPE = SQLConstants.DB_ORACLE;
            } // else if database is oracle
            else if (tmpValue.equalsIgnoreCase (SQLConstants.DB_DB2_STR))
                                        // database is set to DB2?
            {
                SQLConstants.DB_TYPE = SQLConstants.DB_DB2;
            } // else if database is set to DB2
            else                        // no database set
            {
                SQLConstants.DB_TYPE = SQLConstants.DB_UNDEF;
                // TODO RB: Call  
                //          MultilingualTextProvider.getMessage (UtilExceptions.EXC_BUNDLE,
                //              UtilExceptions.ML_E_CONFIGURATIONERRORDATABASETYPENOTDEFINED, env)
                //          to get the text in the correct language
                this.addErrorMessage (UtilExceptions.ML_E_CONFIGURATIONERRORDATABASETYPENOTDEFINED);
            } // else no database set

            if (SQLConstants.DB_TYPE != SQLConstants.DB_UNDEF)
            {
                this.setDbType (tmpValue);
            } // if valid datatype set
        } // if type set
        else
        {
            this.addErrorMessage (DBConfConstants.TOK_DBTYPE + " (" +
                DBConfConstants.TOK_DATABASE + ")" + mustBeSet);
        } // else

        // set sid for database
        if (attributes.getNamedItem (DBConfConstants.TOK_SID) != null)
        {
            this.sid = attributes.getNamedItem (DBConfConstants.TOK_SID)
                .getNodeValue ();
        } // if sid set
        else
        {
            this.addErrorMessage (DBConfConstants.TOK_SID + mustBeSet);
        } // else

        // set user for database
        if (attributes.getNamedItem (DBConfConstants.TOK_USER) != null)
        {
            this.userName = attributes.getNamedItem (DBConfConstants.TOK_USER)
                .getNodeValue ();
        } // if sid set
        else
        {
            this.addErrorMessage (DBConfConstants.TOK_USER + mustBeSet);
        } // else

        // set password for database
        if (attributes.getNamedItem (DBConfConstants.TOK_PASSWORD) != null)
        {
            this.password = attributes.getNamedItem (
                DBConfConstants.TOK_PASSWORD).getNodeValue ().getBytes ();
        } // if sid set
        else
        {
            this.addErrorMessage (DBConfConstants.TOK_PASSWORD + mustBeSet);
        } // else

        // set logintimeout for database
        if (attributes.getNamedItem (DBConfConstants.TOK_DBLOGINTIMEOUT) != null)
        {
            try
            {
                this.loginTimeout = Integer.parseInt (attributes.getNamedItem
                    (DBConfConstants.TOK_DBLOGINTIMEOUT).getNodeValue ());
            } // try
            catch (NumberFormatException e)
            {
                this.addErrorMessage ("Invalid format for " +
                    DBConfConstants.TOK_DBLOGINTIMEOUT +
                    ". Set to default value " +
                    DBConfConstants.DBLOGINTIMEOUT_DEFAULT + ".");
                this.loginTimeout = DBConfConstants.DBLOGINTIMEOUT_DEFAULT;
            } // catch
        } // if sid set
        else
        {
            this.loginTimeout = DBConfConstants.DBLOGINTIMEOUT_DEFAULT;
        } // else

        // set querytimeout for database
        if (attributes.getNamedItem (DBConfConstants.TOK_DBQUERYTIMEOUT) != null)
        {
            try
            {
                this.loginTimeout = Integer.parseInt (attributes.getNamedItem (
                    DBConfConstants.TOK_DBQUERYTIMEOUT).getNodeValue ());
            } // try
            catch (NumberFormatException e)
            {
                this.addErrorMessage ("Invalid format for " +
                     DBConfConstants.TOK_DBQUERYTIMEOUT +
                     ". Set to default value " +
                     DBConfConstants.TOK_DBQUERYTIMEOUT + ".");
                this.queryTimeout = DBConfConstants.DBQUERYTIMEOUT_DEFAULT;
            } // catch
        } // if sid set
        else
        {
            this.queryTimeout = DBConfConstants.DBQUERYTIMEOUT_DEFAULT;
        } // else

        // set log directory:
        if (attributes.getNamedItem (DBConfConstants.TOK_LOGDIR) != null)
        {
            this.p_logDir = attributes
                .getNamedItem (DBConfConstants.TOK_LOGDIR).getNodeValue ();
        } // if sid set
        else
        {
            this.addErrorMessage (DBConfConstants.TOK_LOGDIR + isNotSet);
        } // else

        // open the log:
        this.createDbLog ();
    } // setDbConfiguration


    /**************************************************************************
     * Only the first node will be set for the database settings.
     * The other nodes are ignored. <BR/>
     *
     * @param   dataElement The DOM tree which contains the database settings.
     *                      Only the first node will be taken.
     * @param   setGlobalValues Should the global values (for the default
     *                      repository database) be set, too?
     */
    public void setDBConfiguration (DataElement dataElement,
                                    boolean setGlobalValues)
    {
        String mustBeSet = " must be set in the database configuration!";
        String isNotSet = " is not set in the database configuration!";
        ValueDataElement value = null;  // the actual value


        // set jdbc driver class for database:
        if ((value = dataElement.getValueElement (DBConfConstants.TOK_JDBCDRIVERCLASS)) != null)
        {
            this.jdbcDriverClass = value.value;
        } // if JDBC driverclass set
        else
        {
            this.addErrorMessage (DBConfConstants.TOK_JDBCDRIVERCLASS + " (" +
                DBConfConstants.TOK_DATABASE + ")" + mustBeSet);
        } // else

        // set jdbc driver class for database
        if ((value = dataElement.getValueElement (DBConfConstants.TOK_JDBCDCONNECTIONSTRING)) != null)
        {
            this.connectionString = value.value;
        } // if JDBC connection string set
        else
        {
            this.addErrorMessage (DBConfConstants.TOK_JDBCDCONNECTIONSTRING + " (" +
                DBConfConstants.TOK_DATABASE + ")" + mustBeSet);
        } // else

        // set type for database
        if ((value = dataElement.getValueElement (DBConfConstants.TOK_DBTYPE)) != null)
        {
            String tmpValue = value.value;

            // check if the global value shall be set:
            if (setGlobalValues)        // set the global values?
            {
                // map db-type string (from configuration) to internally used
                // int-values MS SQL 6.5?
                if (tmpValue.equalsIgnoreCase (SQLConstants.DB_MSSQL_STR))
                                        // database is MSSQL?
                {
                    SQLConstants.DB_TYPE = SQLConstants.DB_MSSQL;
                } // if database is MSSQL
                else if (tmpValue.equalsIgnoreCase (SQLConstants.DB_ORACLE_STR))
                                        // database is oracle?
                {
                    SQLConstants.DB_TYPE = SQLConstants.DB_ORACLE;
                } // else if database is oracle
                else if (tmpValue.equalsIgnoreCase (SQLConstants.DB_DB2_STR))
                                        // database is set to DB2?
                {
                    SQLConstants.DB_TYPE = SQLConstants.DB_DB2;
                } // else if database is set to DB2
                else                    // no database set
                {
                    SQLConstants.DB_TYPE = SQLConstants.DB_UNDEF;
                } // else no database set
            } // if set the global values

            if (SQLConstants.DB_TYPE != SQLConstants.DB_UNDEF)
            {
                this.setDbType (tmpValue);
            } // if valid datatype set
            else                        // database type not correctly defined
            {
                // TODO RB: Call  
                //          MultilingualTextProvider.getMessage (UtilExceptions.EXC_BUNDLE,
                //              UtilExceptions.ML_E_CONFIGURATIONERRORDATABASETYPENOTDEFINED, env)
                //          to get the text in the correct language
                this.addErrorMessage (UtilExceptions.ML_E_CONFIGURATIONERRORDATABASETYPENOTDEFINED);
            } // else database type not correctly defined
        } // if type set
        else
        {
            this.addErrorMessage (DBConfConstants.TOK_DBTYPE + " (" +
                DBConfConstants.TOK_DATABASE + ")" + mustBeSet);
        } // else

        // set sid for database
        if ((value = dataElement.getValueElement (DBConfConstants.TOK_SID)) != null)
        {
            this.sid = value.value;
        } // if sid set
        else
        {
            this.addErrorMessage (DBConfConstants.TOK_SID + mustBeSet);
        } // else

        // set user for database
        if ((value = dataElement.getValueElement (DBConfConstants.TOK_USER)) != null)
        {
            this.userName = value.value;
        } // if sid set
        else
        {
            this.addErrorMessage (DBConfConstants.TOK_USER + mustBeSet);
        } // else

        // set password for database
        if ((value = dataElement.getValueElement (DBConfConstants.TOK_PASSWORD)) != null)
        {
            this.password = value.value.getBytes ();
        } // if sid set
        else
        {
            this.addErrorMessage (DBConfConstants.TOK_PASSWORD + mustBeSet);
        } // else

        // set logintimeout for database
        if ((value = dataElement.getValueElement (DBConfConstants.TOK_DBLOGINTIMEOUT)) != null)
        {
            try
            {
                this.loginTimeout = Integer.parseInt (value.value);
            } // try
            catch (NumberFormatException e)
            {
                this.loginTimeout = DBConfConstants.DBLOGINTIMEOUT_DEFAULT;
                this.addErrorMessage ("Invalid format for " +
                                 DBConfConstants.TOK_DBLOGINTIMEOUT +
                                 ". Set to default value " +
                                 DBConfConstants.DBLOGINTIMEOUT_DEFAULT + ".");
            } // catch
        } // if sid set
        else
        {
            this.loginTimeout = DBConfConstants.DBLOGINTIMEOUT_DEFAULT;
        } // else

        // set querytimeout for database
        if ((value = dataElement.getValueElement (DBConfConstants.TOK_DBQUERYTIMEOUT)) != null)
        {
            try
            {
                this.loginTimeout = Integer.parseInt (value.value);
            } // try
            catch (NumberFormatException e)
            {
                this.queryTimeout = DBConfConstants.DBQUERYTIMEOUT_DEFAULT;
                this.addErrorMessage ("Invalid format for " +
                                 DBConfConstants.TOK_DBQUERYTIMEOUT +
                                 ". Set to default value " +
                                 DBConfConstants.TOK_DBQUERYTIMEOUT + ".");
            } // catch
        } // if sid set
        else
        {
            this.queryTimeout = DBConfConstants.DBQUERYTIMEOUT_DEFAULT;
        } // else

        // set log directory:
        if ((value = dataElement.getValueElement (DBConfConstants.TOK_LOGDIR)) != null)
        {
            this.p_logDir = value.value;
        } // if sid set
        else
        {
            this.addErrorMessage (DBConfConstants.TOK_LOGDIR + isNotSet);
        } // else

        // open the log:
        this.createDbLog ();

        // display the error messages:
        if (this.errors != null)
        {
            System.out.println (this.getErrors ());
        } // if
    } // setDbConfiguration


    /**************************************************************************
     * Create the log. <BR/>
     */
    private void createDbLog ()
    {
        if (this.p_logDir != null && this.p_logDir.length () > 0)
        {
            // create and store the log:
            String logFileName = this.p_logDir +
                FileHelpers.getUniqueFileName (this.p_logDir,
                    this.sid + "." + this.userName + "." +
                    DateTimeHelpers.getTimestamp () + ".csv");
            this.p_log = new DBLog (logFileName);
        } // if
    } // createDbLog


    /**************************************************************************
     * If rrors occurred while reading the configuration file then they will
     * be returned with this method, otherwise null is returned. <BR/>
     *
     * @return  This method returns the errorstring (s) or <CODE>null</CODE> if
     *          no errors occurred.
     */
    public StringBuffer getErrors ()
    {
        return this.errors;
    } // getErrors


    /**************************************************************************
     * Return the string representation of the Configuration. <BR/>
     * This method just concatenates the most important configuration properties
     * and creates a String out of them.
     *
     * @return  String representation of Configuration.
     */
    public String toString ()
    {
        String s = super.toString ();
        return "\nDatabase:\n" + s;
    } // toString

} // DBConf
