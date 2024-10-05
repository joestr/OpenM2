/*
 * Class: DBActionPool.java
 */

// package:
package ibs.tech.sql;

// imports:
import ibs.service.email.SMTPServer;
import ibs.util.list.Element;

import java.util.Stack;


/******************************************************************************
 * Connectionpool for JDBC Databases.
 *
 * @version     $Id: DBActionPool.java,v 1.12 2012/09/18 14:47:49 btatzmann Exp $
 *
 * @author      Mark Wassermann (MW)
 ******************************************************************************
 */
public class DBActionPool extends Element
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DBActionPool.java,v 1.12 2012/09/18 14:47:49 btatzmann Exp $";


    /**
     * Tracing message to be displayed for the number of actions in the pool.
     * <BR/>
     */
    private static final String MSG_NUM_ACTIONS = " Actions are in the pool. ";

    /**
     * An array containing the actions of the connection pool. <BR/>
     */
    private Stack<SQLAction> p_actionPool = null;

    /**
     * JDBC driver class. <BR/>
     */
    private String          p_driverClass = null;

    /**
     * JDBC connection string. <BR/>
     */
    private String          p_connectionString = null;

    /**
     * Database name. <BR/>
     */
    private String          p_database = null;

    /**
     * Database user. <BR/>
     */
    private String          p_user = null;

    /**
     * Password of database user. <BR/>
     */
    private byte[]          p_passwd = null;

    /**
     * Login timeout. <BR/>
     */
    private int             p_loginTimeout = 0;

    /**
     * Number of connection initialized at startup. <BR/>
     */
    private int             p_openActions = 0;

    /**
     * Number of connections initialized at startup. <BR/>
     */
    private int             p_startSize = 5;

    /**
     * Maximum number of connections in the pool. <BR/>
     */
    private int             p_holdSize = 50;

    /**
     * Maximum Connections to be used. <BR/>
     */
    private int             p_maxSize = 100;

    /**
     * Trace database operations. <BR/>
     */
    private boolean         p_traceMode = false;

    /**
     * Number of requests to the DBActionPool. <BR/>
     */
    private int             p_request = 0;

    /**
     * The log for the action pool. <BR/>
     */
    private DBLog 			p_log = null;

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


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a new connection pool. <BR/>
     *
     * @param   driverClass     Name of the JDBC driver class.
     * @param   connectionString JDBC connectionstring.
     * @param   database        Name of the database.
     * @param   user            Name of database user.
     * @param   passwd          Password of database user.
     * @param   loginTimeout    Login time out.
     * @param   queryTimeout    Query time out.
     * @param   log             The log for sql logging.
     * @param	smtpServer		The SMTP server for messaging.
     * @param	mailSystem		The system mail address.
     * @param	mailAdmin		The Admin's mail address.
     *
     * @throws  DBActionPoolException
     *          The action pool is full. No more connections possible.
     * @throws  DBConnectionException
     *          Error during connecting to the database.
     * @throws  DBActionException
     *          Error during trying to execute the action.
     */
    public DBActionPool (String driverClass, String connectionString,
        String database, String user, byte[] passwd,
        int loginTimeout, int queryTimeout, DBLog log,
        SMTPServer smtpServer, String mailSystem, String mailAdmin)
        throws DBActionPoolException, DBConnectionException, DBActionException
    {
        // call constructor of super class:
        super (0, connectionString + "-" + database);

        // initialize the properties:
        this.initialize (driverClass, connectionString, database, user, passwd,
                    loginTimeout, queryTimeout, log, smtpServer, mailSystem,
                    mailAdmin);
    } // DBActionPool


    /**************************************************************************
     * Creates a new connectionpool. <BR/>
     *
     * @param   conf        The configuration values.
     *
     * @throws  DBActionPoolException
     *          The start size for the pool is <CODE>0</CODE> or less.
     * @throws  DBConnectionException
     *          Error during connecting to the database.
     * @throws  DBActionException
     *          Error during trying to execute the action.
     */
    public DBActionPool (DBConf conf)
        throws DBActionPoolException, DBConnectionException, DBActionException
    {
        // call constructor of super class:
        super (0, conf.getDbConnectionString () + "-" + conf.getDbSid ());

        // initialize the properties:
        this.initialize (
            conf.getDbJdbcDriverClass (),
            conf.getDbConnectionString (),
            conf.getDbSid (),
            conf.getDbUserName (),
            conf.getDbPassword (),
            conf.getDbLoginTimeout (),
            conf.getDbQueryTimeout (),
            conf.getDbLog (),
        	conf.getSmtpServer (),
        	conf.getMailSystem (),
        	conf.getMailAdmin ());
    } // DBActionPool


    /**************************************************************************
     * Creates a new connectionpool. <BR/>
     *
     * @param   driverClass     Name of the JDBC driver class.
     * @param   connectionString JDBC connectionstring.
     * @param   database        Name of the database.
     * @param   user            Name of database user.
     * @param   passwd          Password of database user.
     * @param   loginTimeout    Login time out.
     * @param   queryTimeout    Query time out.
     * @param   log             The log for sql logging.
     * @param	smtpServer		The SMTP server for messaging.
     * @param	mailSystem		The system mail address.
     * @param	mailAdmin		The Admin's mail address.
     *
     * @throws  DBActionPoolException
     *          The start size for the pool is <CODE>0</CODE> or less.
     * @throws  DBConnectionException
     *          Error during connecting to the database.
     * @throws  DBActionException
     *          Error during trying to execute the action.
     */
    public void initialize (String driverClass, String connectionString,
        String database, String user, byte[] passwd,
        int loginTimeout, int queryTimeout, DBLog log,
        SMTPServer smtpServer, String mailSystem, String mailAdmin)
        throws DBActionPoolException, DBConnectionException, DBActionException
    {
        int i = 0;                      // counter

        // setting up connection data:
        this.p_driverClass = driverClass;
        this.p_connectionString = connectionString;
        this.p_database = database;
        this.p_user = user;
        this.p_passwd = passwd;
        this.p_loginTimeout = loginTimeout;
        this.p_log = log;
        this.p_smtpServer = smtpServer;
        this.p_mailSystem = mailSystem;
        this.p_mailAdmin = mailAdmin;

        // creating the pool:
        if (this.p_startSize > 0)
        {
            this.p_actionPool = new Stack<SQLAction> ();

            for (i = 0; i < this.p_startSize; i++)
            {
                this.p_actionPool.push (this.createAction ());
            } // for
        } // if
        else
        {
            // configuration error:
            throw new DBActionPoolException (this, null, true);
        } // else
    } // initialize


    ///////////////////////////////////////////////////////////////////////////
    // methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Create a new action/connection object in the pool. <BR/>
     *
     * @return  The newly created action object. <BR/>
     *
     * @throws  DBConnectionException
     *          Error during connecting to the database.
     * @throws  DBActionException
     *          Error during trying to execute the action.
     */
    private SQLAction createAction ()
        throws DBConnectionException, DBActionException
    {
        SQLAction action = new SQLAction (this.p_driverClass,
                this.p_connectionString, this.p_database, this.p_user,
                this.p_passwd, this.p_loginTimeout, this.p_log,
                this.p_smtpServer, this.p_mailSystem, this.p_mailAdmin);
        return action;
    } // createAction


    /**************************************************************************
     * Get an action object out of the pool. <BR/>
     *
     * @param   caller  The caller object of this method.
     *
     * @return  The action object to be used.
     *
     * @throws  DBActionPoolException
     *          The action pool is full. No more connections possible.
     * @throws  DBConnectionException
     *          Error during connecting to the database.
     * @throws  DBActionException
     *          Any exception within the action.
     */
    public synchronized SQLAction getAction (Object caller)
        throws DBActionPoolException, DBConnectionException, DBActionException
    {
        SQLAction action = null;

        // check actionpool for avalable actions
        if (!this.p_actionPool.empty ())
        {
            // take an avalable action from the pool
            action = this.p_actionPool.pop ();
        } // if
        else
        {
            // if there is no action in the pool
            if (this.p_actionPool.size () <= this.p_maxSize)
            {
                // create a new action
                action = this.createAction ();
            } // if
            else
            {
                // throw an error if maximum actions are reached
                throw new DBActionPoolException (this, null, true);
            } // else
        } // else
        this.p_openActions++;

        // tracemode:
        if (this.p_traceMode)
        {
            this.p_request++;
            System.out.println (" (" + this.p_actionPool.size () + ")" +
                DBActionPool.MSG_NUM_ACTIONS + this.p_openActions +
                " Actions are open. - getAction No.: " +
                this.p_request + "\t" + caller);
        } // if

        return action;
    } // getAction


    /**************************************************************************
     * Release an action. <BR/>
     *
     * @param   caller  The caller object of this method.
     * @param   action  The action to be released.
     *
     * @throws  DBConnectionException
     *          Error during connecting to the database.
     */
    public synchronized void releaseAction (Object caller, SQLAction action)
        throws DBConnectionException
    {
        // Close action
        try
        {
            action.end ();
        } // try
        catch (DBActionException e)
        {
            throw new DBConnectionException (this, e, true);
        } // catch DBActionException
        finally
        {
            if (this.p_actionPool.size () <= this.p_holdSize)
            {
                // put action back into the pool:
                this.p_actionPool.push (action);
            } // if
            else
            {
                // destroy connection:
                // nothing to do
            } // else
            this.p_openActions--;
        } // finally

        // tracing output:
        if (this.p_traceMode)
        {
            System.out.println (" (" + this.p_actionPool.size () + ")" +
                DBActionPool.MSG_NUM_ACTIONS + this.p_openActions +
                " Actions are open. - releaseAction\t\t" + caller);
        } // if
    } // releaseAction


    /**************************************************************************
     * Gets the thre status of the tracemode. <BR/>
     *
     * @return  true if tracemode is active
     */
    public boolean getTraceMode ()
    {
        return this.p_traceMode;
    } // getTraceMode


    /**************************************************************************
     * Sets the tracemode on. <BR/>
     */
    public void setTraceModeOn ()
    {
        this.p_traceMode = true;
    } // setTraceModeOn


    /**************************************************************************
     * Sets the tracemode on. <BR/>
     */
    public void setTraceModeOff ()
    {
        this.p_traceMode = false;
    } // setTraceModeOff

} // class DBActionPool
