/*
 * Class: DBConnector.java
 */

// package:
package ibs.tech.sql;

// imports:
//KR TODO: unsauber
import ibs.io.IOHelpers;
import ibs.tech.sql.DBActionPool;
import ibs.tech.sql.DBConf;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.util.list.ElementContainer;
import ibs.util.list.ListException;


/******************************************************************************
 * This class is used to connect to databases. <BR/>
 *
 * @version     $Id: DBConnector.java,v 1.12 2007/07/10 18:24:45 kreimueller Exp $
 *
 * @author      Klaus Reimüller, 011122
 ******************************************************************************
 */
public class DBConnector extends ElementContainer<DBActionPool>
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DBConnector.java,v 1.12 2007/07/10 18:24:45 kreimueller Exp $";


    /**
     * The owner of an action. <BR/>
     */
    public static String actionOwner = "DBConnector";

    /**
     * The connector instance. <BR/>
     */
    private static DBConnector p_dbConnector = null;

    /**
     * The default configuration which is used if there is no specific
     * configuration defined. <BR/>
     */
    public static DBConf p_defaultConf = null;

    /**
     * The action pool for the default configuration. <BR/>
     */
    private static DBActionPool p_defaultPool = null;


    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Initialize the DBConnector. <BR/>
     *
     * @throws  ListException
     *          An error occurred during initializing the container.
     */
    public static void init ()
        throws ListException
    {
        // check if there is already a connector set:
        if (DBConnector.p_dbConnector == null)  // no connector set?
        {
            // create the connector and set it:
            DBConnector.p_dbConnector = new DBConnector ();
        } // if no connector set
    } // init


    /**************************************************************************
     * Set the default db configuration to get connection. <BR/>
     * Has to be done before calling {@link #getDBConnection () getDBConnection ()}.
     *
     * @param   conf    The configuration to be set.
     */
    public static void setConfiguration (DBConf conf)
    {
        // set the configuration:
        DBConnector.p_defaultConf = conf;
    } // setConfiguration


    /**************************************************************************
     * Get the action pool for a specific configuration. <BR/>
     * If the configuration is the default configuration the default pool is
     * used. <BR/>
     * If no pool for the configuration exists a new pool is created.
     *
     * @param   conf    The configuration data for the connection.
     *
     * @return  The pool or <CODE>null</CODE> if the pool could not be
     *          determined.
     */
    public static synchronized DBActionPool getPool (DBConf conf)
    {
        DBActionPool pool = null;       // the actual action pool

        // try to find the database connector:
        if (conf == DBConnector.p_defaultConf) // default configuration?
        {
            pool = DBConnector.p_defaultPool;
        } // if default configuration
        else                            // other configuration
        {
            pool = DBConnector.p_dbConnector.find (
                conf.getDbConnectionString () + "-" + conf.getDbSid ());
        } // else other configuration

        if (pool == null)               // no connection pool available?
        {
            // check if configuration is set
            if (conf == null)           // no configuration set?
            {
                throw new RuntimeException ("configuration not set");
            } // if no configuration set

            try
            {
                // create a new connection pool and store it within the list:
                // version for Release 2.0 Beta or newer:
                pool = new DBActionPool (conf);

                // add the pool to the list:
                DBConnector.p_dbConnector.add (pool);

                // set the default pool:
                if (conf == DBConnector.p_defaultConf) // default configuration?
                {
                    // set the pool:
                    DBConnector.p_defaultPool = pool;
                } // if default configuration
            } // try
            catch (DBError e)
            {
                IOHelpers.printError ("DBConnector.getPool", e, true);
            } // catch
        } // if no connection pool available

        // return the pool:
        return pool;
    } // getPool


    /**************************************************************************
     * Gets a database connection für the default configuration. <BR/>
     *
     * @return  The action object associated with the required connection.
     *
     * @exception   DBError
     *              A database exception occurred.
     */
    public static synchronized SQLAction getDBConnection ()
        throws DBError
    {
        // call common method with default configuration
        // (should be set in Application.initGet):
        return DBConnector.getDBConnection (DBConnector.p_defaultConf);
    } // getDBConnection


    /**************************************************************************
     * Gets a database connection. <BR/>
     *
     * @param   conf    The configuration data for the connection.
     *
     * @return  The action object associated with the required connection.
     *
     * @exception   DBError
     *              A database exception occurred.
     */
    public static synchronized SQLAction getDBConnection (DBConf conf)
        throws DBError
    {
        DBActionPool pool = DBConnector.getPool (conf); // the actual action pool
/*
String stacktrace = Helpers.getStackTraceFromThrowable (new Throwable ());
String line = ExceptionUtils.getLineNumberFromStack (stacktrace);
String implclass = ExceptionUtils.getImplementationClassNameFromStack (stacktrace);
String mypackage = ExceptionUtils.getPackageInformationFromStack (stacktrace);
String method = ExceptionUtils.getMethodNameFromStack (stacktrace);
System.out.println ("getDBConnection called: from " + this.getClass () + "; method: " + method + ":"+line+"; implClass="+implclass);
*/

        try
        {
            // get a new action object associated with a connection from the pool
            // and return it:
            return pool.getAction (DBConnector.actionOwner);
        } // try
        catch (DBError e)
        {
            IOHelpers.printError ("DBConnector.getDBConnection", e, true);
            // raise the exception to the caller:
            throw e;
        } // catch
        catch (NullPointerException e)
        {
            // the pool was not determined, throw corresponding exception:
            throw new DBError ("No pool determined.");
        } // catch NullPointerException
    } // getDBConnection


    /**************************************************************************
     * Releases a database connection for the default configuration. <BR/>
     *
     * @param   action  The action object associated with the connection.
     *
     * @exception   DBError
     *              A database exception occurred.
     */
    public static synchronized void releaseDBConnection (SQLAction action)
        throws DBError
    {
        // call common method with default configuration
        // (should be set in Application.initGet):
        DBConnector.releaseDBConnection (DBConnector.p_defaultConf, action);
    } // releaseDBConnection


    /**************************************************************************
     * Releases a database connection. <BR/>
     *
     * @param   conf    The configuration data for the connection.
     * @param   action  The action object associated with the connection.
     *
     * @exception   DBError
     *              A database exception occurred.
     */
    public static synchronized void releaseDBConnection (DBConf conf,
                                                         SQLAction action)
        throws DBError
    {
        DBActionPool pool = DBConnector.getPool (conf); // the actual action pool
/*
String stacktrace = Helpers.getStackTraceFromThrowable (new Throwable ());
String line = ExceptionUtils.getLineNumberFromStack (stacktrace);
String implclass = ExceptionUtils.getImplementationClassNameFromStack (stacktrace);
String mypackage = ExceptionUtils.getPackageInformationFromStack (stacktrace);
String method = ExceptionUtils.getMethodNameFromStack (stacktrace);
System.out.println ("releaseDBConnection called: from " + this.getClass () + "; method: " + method + ":"+line+"; implClass="+implclass);
*/
/*
        // try to find the database action pool:
        pool = getPool (conf);
*/

        if (pool != null && action != null) // both pool and action exist?
        {
            try
            {
                // release the action object:
                pool.releaseAction (DBConnector.actionOwner, action);
            } // try
            catch (DBError e)
            {
                // an error occurred - show name and info
                IOHelpers.printError ("DBConnector.releaseDBConnection", e, true);
                // raise the exception to the caller:
                throw e;
            } // catch
        } // if both pool and action exist

    } // releaseDBConnection


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a DBConnector object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     * It is used to allow the handling of DBActionPools as list elements.
     *
     * @throws  ListException
     *          An error occurred in a list operation.
     */
    public DBConnector ()
        throws ListException
    {
        // call constructor of super class:
        super ();
    } // DBConnector


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

} // class DBConnector
