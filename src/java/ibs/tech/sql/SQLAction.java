/*
 * Class: SQLAction.java
 */

// package:
package ibs.tech.sql;

// imports:
import ibs.BaseObject;
import ibs.service.email.EMail;
import ibs.service.email.EMailManager;
import ibs.service.email.SMTPServer;
import ibs.tech.sql.DBActionException;
import ibs.tech.sql.DBConnectionException;
import ibs.tech.sql.DBParameterDefinitionException;
import ibs.tech.sql.DBParameterException;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.StoredProcedure;
import ibs.tech.sql.StoredProcedureConstants;
import ibs.util.Helpers;

import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DataTruncation;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.Vector;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.Calendar;
import java.lang.Long;
import java.lang.Math;
import java.text.SimpleDateFormat;


/******************************************************************************
 * This class implements database access actions and stored procedures. <BR/>
 * SQLAction is the main object for database calls. <BR/>
 *
 * @version     $Id: SQLAction.java,v 1.28 2012/10/17 11:57:07 gweiss Exp $
 *
 * @author      Mark Wassermann (MW)
 ******************************************************************************
 */
public class SQLAction extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: SQLAction.java,v 1.28 2012/10/17 11:57:07 gweiss Exp $";

    /**
     * Random number generator for deadlock retries. <BR/>
     */
    private static Random 			RANDOM_DEADLOCK_OFFSET = new Random ();

    /**
     * The active connection for this SQLAction object. <BR/>
     */
    private Connection          	p_connection;

    /**
     * This property contains the result of the last query. <BR/>
     */
    private boolean             	p_wasNull = false;

    /**
     * Resultset of actual open connection. <BR/>
     */
    private Statement           	p_statement;

    /**
     * Resultset of actual open connection. <BR/>
     */
    private ResultSet           	p_resultset;

    /**
     * Indicateds if next was executed. <BR/>
     */
    private boolean             	p_nextDone = false;

    /**
     * Indicates if there are more elements in the resultset. <BR/>
     */
    private boolean             	p_moreElements = false;

    /**
     * Prepared statement object of actual open connection. <BR/>
     */
    private CallableStatement   	p_callStatement;

    /**
     * The caller of the action. <BR/>
     */
    private Object              	p_caller = null;

    /**
     * Trace database operations. <BR/>
     */
    private boolean             	p_traceMode = false;

    /**
     * JDBC driver class. <BR/>
     */
    private String					p_driverclass = null;

    /**
     * Driver specific database connection string. <BR/>
     */
    private String					p_connectionString = null;

    /**
     * Database name. <BR/>
     */
    private String					p_databaseName = null;

    /**
     * User name. <BR/>
     */
    private String					p_username = null;

    /**
     * Password of database user. <BR/>
     */
    private byte[]					p_passwd = null;

    /**
     * The log for the action. <BR/>
     */
    private DBLog 					p_log = null;

    /**
     * Number of retries left if DB connection has been lost
     */
    private int						p_retriesConnLoss = 0;

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
    
    /**
     * List used as queue for storing the timestamps when DB reconnects
     * have failed. If the list is full, a message is sent.
     */
    private static LinkedBlockingDeque<Long>	p_reconnectionErrors = null;


    /**************************************************************************
     * Creates a SQLAction object. <BR/>
     *
     * @param driverclass       Name of the JDBC driverclass
     * @param connectionstring  Driverspesific connectionstring
     * @param database          Databasename
     * @param user              Databaseuser
     * @param passwd            Password of the databaseuser
     * @param loginTimeout      Logintimeout
     * @param log               The log.
     * @param	smtpServer		The SMTP server for messaging.
     * @param	mailSystem		The system mail address.
     * @param	mailAdmin		The Admin's mail address.
     *
     * @exception   DBConnectionException
     *              Error during connecting to the database.
     * @exception   DBActionException
     *              Error during trying to execute the action.
     */
    public SQLAction (String driverclass, String connectionstring,
            String database, String user, byte[] passwd,
            int loginTimeout, DBLog log, SMTPServer smtpServer,
            String mailSystem, String mailAdmin)
        throws DBConnectionException, DBActionException
    {
    	// initializing SQLAction
        this.p_resultset = null;
        this.p_callStatement = null;
        this.p_log = log;

    	// caching of database connection parameters for reconnect operations after
    	// having lost the database connection
    	this.p_driverclass = driverclass;
    	this.p_connectionString = connectionstring;
    	this.p_databaseName = database;
    	this.p_username = user;
    	this.p_passwd = passwd;

    	// caching the e-mail connection data
    	this.p_smtpServer = smtpServer;
        this.p_mailSystem = mailSystem;
        this.p_mailAdmin = mailAdmin;

    	// get the initial values for retry after connection loss from the config values
    	this.p_retriesConnLoss = SQLConstants.RETRIES_FOR_DBCONNECTION;

    	// create the DBConnection
        this.createDBConnection (driverclass, connectionstring, database, user,
				passwd);
    } // SQLAction


	/**
	 * Creates the DB connection object and stores it in the variable p_connection. <BR/>
	 * 
	 * @param driverclass				Name of the JDBC driver class
	 * @param connectionstring			Driver specific connection string
	 * @param database					Name of the database
	 * @param user						Name of the database account
	 * @param passwd					Password of the database account
	 * @throws DBConnectionException	
	 */
	private void createDBConnection(String driverclass,
			String connectionstring, String database, String user, byte[] passwd)
			throws DBConnectionException
	{

		try
        {
//DriverManager.setLogWriter (new java.io.PrintWriter (System.out));
            Class.forName (driverclass);
//println ("connectionString: " + connectionstring);
            if (connectionstring.startsWith ("jdbc:odbc:") ||
                connectionstring.startsWith ("jdbc:easysoft:"))
                                        // odbc connection?
            {
//println ("open odbc connection...");
                // open the connection:
                // there is no user and password necessary for odbc connections
                this.p_connection =
                    DriverManager.getConnection (connectionstring);
//println ("got connection.");
                this.printSqlWarnings (this.p_connection.getWarnings ());
            } // if odbc connection
            else                            // standard connection
            {
//println ("open standard jdbc connection...");
                // open the connection:
                this.p_connection = DriverManager.getConnection (
                    connectionstring, user, new String (passwd));
// println ("got connection, setting catalog...");
                this.printSqlWarnings (this.p_connection.getWarnings ());
                this.p_connection.setCatalog (database);
//println ("set catalog.");
            } // else standard connection
        } // try

        catch (ClassNotFoundException e)
        {
            throw new DBConnectionException (this, e, true);
        } // catch

        catch (SQLException e)
        {
//println ("SQLException " + e.getErrorCode () + ":" + e.getSQLState () + ":" + e.getMessage ());
            throw new DBConnectionException (this, e, true);
        } // catch

        finally
        {
//DriverManager.setLogWriter (null);
        } // finally
	} // createDBConnection


    /**************************************************************************
     * Print the warnings for a sql statement. <BR/>
     * If the warnings are chained together all warnings are printed.
     *
     * @param   warning     The initial warning (the first in the chain).
     */
    private void printSqlWarnings (SQLWarning warning)
    {
        SQLWarning warningLocal = warning; // variable for local assignments

        while (warningLocal != null)
        {
            this.println ("Warning: " + warningLocal.getMessage ());
            if (warningLocal instanceof DataTruncation)
            {
                DataTruncation d = (DataTruncation) warningLocal;
                this.println ("  Truncation error in column: " + d.getIndex ());
            } // if
            warningLocal = warningLocal.getNextWarning ();
        } // while
    } // printSqlWarnings


    /**************************************************************************
     * Print a specific text. <BR/>
     *
     * @param   text        The text to be displayed.
     */
    private void println (String text)
    {
        // display the text:
        System.out.println (text);
    } // println


    /**************************************************************************
     * Call a stored procedure. <BR/>
     * Build the stored procedures with the help of StoredProcedure and
     * Parameter classes.
     *
     * @param   storedProcedure  The stored procedure object to execute
     *
     * @return  return value if stored procedure type is VALUE
     *
     * @throws  DBActionException
     *          An exception occurred during execution of the statement.
     *
     * @see     StoredProcedure
     * @see     Parameter
     */
    public int execStoredProc (StoredProcedure storedProcedure)
        throws DBActionException
    {
        // call with deadlock-retry-value:
        return this.execStoredProc (storedProcedure, SQLConstants.RETRIES_ON_DEADLOCK);
    } // execStoredProc


    /**************************************************************************
     * Call a stored procedure. <BR/>
     * Build the stored procedures with the help of StoredProcedure and
     * Parameter classes. <BR/>
     *
     * If the db-procedure returns a deadlock, then it will be retried again
     * and again ('retriesOnDealock'-number of times). After each retry-attempt
     * the next call will be delayed some milliseconds to give the db some time
     * to finish its other activities. If the given number of retries is 0 the
     * deadlock-exeption will be thrown. <BR/>
     *
     * This behaviour demands that all procedures that can possibly be called are
     * embedded in transactions where a full rollback is possible. Otherwise
     * multiple execution of the same procedure will lead to db-inconsistencies.
     *
     * @param   storedProcedure     The stored procedure object to execute
     * @param   retriesOnDeadlock   Number of retries if deadlock occurs;
     *
     * @return  return value if stored procedure type is VALUE
     *
     * @throws  DBActionException
     *          An exception occurred during execution of the statement.
     *
     * @see     StoredProcedure
     * @see     Parameter
     */
    private int execStoredProc (StoredProcedure storedProcedure, int retriesOnDeadlock)
        throws DBActionException
    {
        long start = 0;
        long end = 0;
        int retVal = -1;
        String queryString;

        if (this.p_callStatement != null) // old statement open?
        {
            try
            {
//System.out.println ("execStoredProc before close");
                this.p_callStatement.close ();
//System.out.println ("execStoredProc after close");
            } // try
            catch (SQLException e)
            {
                throw new DBActionException (this, e , true);
            } // catch
        } // if old statement open

        if (this.p_traceMode)
        {
            System.out.println ("executing procedure: " + storedProcedure.getName ());
        } // if
        start = System.currentTimeMillis ();

        queryString = storedProcedure.buildOdbcCallString ();

        // try to create prepared statement:
        try
        {
            if (storedProcedure.getReturnType () ==
                StoredProcedureConstants.RETURN_VALUE ||
                storedProcedure.getReturnType () ==
                StoredProcedureConstants.RETURN_NOTHING)
            {
                this.p_callStatement =
                    this.p_connection.prepareCall (queryString);
                this.printSqlWarnings (this.p_callStatement.getWarnings ());
                this.p_retriesConnLoss = SQLConstants.RETRIES_FOR_DBCONNECTION;
            } // if
        } // try

        catch (SQLException e)
        {
        	String sqlState = e.getSQLState ();

        	// check if exception is a loss of database connection
        	if (sqlState != null && (sqlState.equals ("08S01") || sqlState.equals ("HY010") || sqlState.equals ("08003") ||
        			sqlState.equals ("08007") || sqlState.equals ("08900") || sqlState.equals ("HYT00") ||
               		sqlState.equals ("HYT01")))
            {
        		// try to reconnect and call the method again
        		this.retryExecStoredProc (storedProcedure, retriesOnDeadlock, e);
            } // if

        } // catch (SQLException e)

    	catch (Exception e)
    	{
       		throw new DBActionException (this, e, true);
        } // catch (Exception e)

        // try to create parameter:
        try
        {
            if (storedProcedure.getReturnType () !=
                    StoredProcedureConstants.RETURN_SET ||
                storedProcedure.getReturnType () !=
                    StoredProcedureConstants.RETURN_NOTHING)
            {
                this.setParameters (storedProcedure, this.p_callStatement);
            } // if
        } // try
        catch (DBParameterException e)
        {
            throw new DBActionException (this, e, true);
        } // catch

        // all parameters are set - now execute the prepared statement:
        try
        {
        	this.execPreparedStoredProc (this.p_callStatement, retriesOnDeadlock,
        			storedProcedure.getName (), queryString);
        } // try
        
        catch (SQLException e)
        {
        	String sqlState = e.getSQLState ();

        	// check if exception is a loss of database connection
        	if (sqlState != null && (sqlState.equals ("08S01") || sqlState.equals ("HY010") || sqlState.equals ("08003") ||
        				sqlState.equals ("08007") ||	sqlState.equals ("08900") || sqlState.equals ("08S01") || 
        				sqlState.equals ("HYT00") ||	sqlState.equals ("HYT01")))
            {
        		// try to reconnect and call the method again
        		this.retryExecStoredProc (storedProcedure, retriesOnDeadlock, e);
            } // if
        }

        // set out/return values for OUT parameters in stored procedure object
        try
        {
            // receiving result values:
            // differ between with/without resultset
            if (storedProcedure.getReturnType () ==
                    StoredProcedureConstants.RETURN_VALUE)
            {
                // transfers values from jdbc-parameters to StoredProcedure
                // Parameter objects
                this.getParameterValues (storedProcedure, this.p_callStatement);
                // set return value
                retVal = storedProcedure.getReturnValue ();
            } // if
            else if (storedProcedure.getReturnType () ==
                    StoredProcedureConstants.RETURN_NOTHING)
            {
                this.getParameterValues (storedProcedure, this.p_callStatement);
                retVal = 1;
            } // else if
        } // try
        catch (DBParameterException e)
        {
            throw new DBActionException (this, e, true);
        } // catch

        end = System.currentTimeMillis ();
        if (this.p_traceMode)
        {
            System.out.println ("time: " + (end - start) + "ms");
        } // if

        // check if the query lasted too long:
        if ((end - start) > 2000)
        {
            this.writeLog (0, "",
                "SQLAction: query needed more than 2 seconds: " +
                    (end - start) + "ms.", queryString, null);
        } // if

        // DB action succeeded
        this.p_retriesConnLoss = SQLConstants.RETRIES_FOR_DBCONNECTION;

        return retVal;
    } // execStoredProc


	/**
	 * Retry to execute a stored procedure <BR/>
	 * This method contains the exception handling code which is executed when the execution of a stored procedure
	 * could not be done because of loss of DB connection. It causes the thread sleep, the reconnect operation and
	 * calls the execStoredProc-Method again.
	 * 
	 * @param storedProcedure		The procedure which could not be executed due to loss of DB connection
	 * @param retriesOnDeadlock		Number of retries which shall be executed before giving up
	 * @param e						The SQLException which caused the loss-of-connection handling
	 * @throws DBActionException	Exception raised after all retries have failed. This exception is caught in the caller
	 * 								methods if they have to perform additional exception handling.
	 */
	private void retryExecStoredProc (StoredProcedure storedProcedure,
			int retriesOnDeadlock, SQLException e) throws DBActionException
	{
		// try to reconnect
		if (this.p_retriesConnLoss > 0)
		{
			this.recreateDBConnection ("procedure", storedProcedure.getName (), e);

			// try again to create and execute the procedure
			this.execStoredProc (storedProcedure, retriesOnDeadlock);

		} // if (this.p_retriesConnLoss > 0)

		// not successfully in reconnecting too many times
		else
		{
			this.writeLog(e.getErrorCode (), e.getSQLState (),
					"SQLAction: DB connection loss on procedure execution. " + 
							SQLConstants.RETRIES_FOR_DBCONNECTION + " done, " + this.p_retriesConnLoss +
							" left. Context = " + this.p_caller,
							storedProcedure.getName (), null);

			this.sendMessage (e);

			throw new DBActionException (this, e, true);
		} // else
	} // retryExecStoredProcedure


	/**
	 * This method handles the recreation of DB connection.<br/>
	 * Exception handling (if connection could not be recreated) is done in the calling procedure.
	 * 
	 * @param dbStatementType	The type of DB statment (e. g. query, procedure), only for writing a clear
	 * 							message to the log
	 * @param dbStatement		The name of DB statement or query string
	 * @param e					The SQLException which caused the loss-of-connection handling
	 */
	private void recreateDBConnection (String dbStatementType, String dbStatement,
			SQLException e)
	{
		// on each retry wait two times longer
		try
		{
			// to avoid negative sleep timespan (misconfiguration issue)
			int counter = SQLConstants.RETRIES_FOR_DBCONNECTION - this.p_retriesConnLoss;

			Thread.sleep (
				(int) Math.pow (2, ( (counter < 0) ? 0 : counter)) *
				SQLConstants.RETRYTIMEOFFSET_ON_DBCONNECTION_LOSS);
		} // try

		catch (InterruptedException e1)
		{
			// print the stack trace to stderr:
			e1.printStackTrace();
		} // catch (InterruptedException e1)

		// decrement counter
		this.p_retriesConnLoss --;

		if (this.p_traceMode)
		{
			System.out.println (
				"SQLAction: retry " + dbStatementType + " after loss of DB Connection; "
				+ dbStatementType + " = " +	dbStatement +
				"; retry=" + this.p_retriesConnLoss +
				"; errorcode=" + e.getErrorCode () +
				"; context=" + this.p_caller);
		} // if

		// re-create the DBConnection
		try
		{
			this.createDBConnection (this.p_driverclass, this.p_connectionString,
				this.p_databaseName, this.p_username, this.p_passwd);
		} // try

		// If the connection is not opened correctly, a new SQLException will be
		// thrown when executing the DB statement again. SQLException triggers the
		// recursion again (in the calling procedure). So there is no need for handling here.
		catch (DBConnectionException exc)
		{
			// nothing to do
		} // catch (DBConnectionException exc)
	} // recreateDBConnection


    /**************************************************************************
     * Call a stored procedure. <BR/>
     * Build the stored procedures with the help of StoredProcedure and
     * Parameter classes. <BR/>
     *
     * If the db-procedure returns a deadlock, then it will be retried again
     * and again ('retriesOnDealock'-number of times). After each retry-attempt
     * the next call will be delayed some milliseconds to give the db some time
     * to finish its other activities. If the given number of retries is 0 the
     * deadlock-exeption will be thrown. <BR/>
     *
     * This behaviour demands that all procedures that can possibly be called are
     * embedded in transactions where a full rollback is possible. Otherwise
     * multiple execution of the same procedure will lead to db-inconsistencies.
     *
     * @param   callStatement       The statement to be executed.
     * @param   retriesOnDeadlock   Number of retries if deadlock occurs.
     * @param   procName            Name of stored procedure.
     * @param   queryString         The query.
     *
     * @throws	SQLException
     * 			Error when the DB connection has been lost
     * @throws  DBActionException
     *          Other error when executing the statement.
     *
     * @see     StoredProcedure
     * @see     Parameter
     */
    private void execPreparedStoredProc (CallableStatement callStatement,
                                         int retriesOnDeadlock,
                                         String procName, String queryString)
        throws DBActionException, SQLException
    {
    	String sqlState = null;         // state of sql statement

        try
        {
            // execute the prepared statement:
            callStatement.execute ();
        } // try
        catch (SQLException e)
        {
            // get sql state:
            sqlState = e.getSQLState ();

            // check if exception is a deadlock (according to sqlstate of the
            // X/OPEN SQL 1995 standard which is used with JDBC)
            if (sqlState != null && sqlState.equals ("40001") &&
                retriesOnDeadlock > 0)
            {
                // on each retry wait a little bit (100 ms) longer
                this.sleep (SQLConstants.RETRIES_ON_DEADLOCK -
                    retriesOnDeadlock, SQLConstants.RETRYTIME_ON_DEADLOCK,
                    SQLConstants.RETRYTIMEOFFSET_ON_DEADLOCK);

                if (this.p_traceMode)
                {
                    System.out.println (
                        "SQLAction: retry procedure on deadlock; procedure=" +
                        procName + "; retry=" + retriesOnDeadlock +
                        "; errorcode=" + e.getErrorCode () +
                        "; context=" + this.p_caller);
                } // if
                this.writeLog (e.getErrorCode (), sqlState,
                    "SQLAction: retry procedure on deadlock, retriesleft=" +
                    retriesOnDeadlock + ", context=" + this.p_caller,
                    procName, null);

                this.execPreparedStoredProc (callStatement, retriesOnDeadlock - 1,
                                             procName, queryString);
            } // if
            
            else if (sqlState != null && (sqlState.equals ("08S01") || sqlState.equals ("HY010") ||
            		sqlState.equals ("08900") || sqlState.equals ("08003")) || sqlState.equals ("HYT00") ||
            		sqlState.equals ("HYT01") || sqlState.equals ("08007"))
            {	
            	throw e;
            } // else if

            else
            {
                System.out.println ("Exception SQLAction: " + e.toString () + "\n" +
                    "errorcode: " + e.getErrorCode () + "\n" +
                    "message: " + e.getMessage () + "\n" +
                    "localized message: " + e.getLocalizedMessage () + "\n" +
                    "sqlstate: " + sqlState + "\n" +
                    "stacktrace: " + e.fillInStackTrace () + "\n" +
                    "call: " + queryString);
                this.writeLog (e.getErrorCode (), sqlState,
                    e.getMessage (), queryString,
                    Helpers.getStackTraceFromThrowable (e));

                if (e.getNextException () != null)
                {
                    System.out.println ("CHAINED EXCEPTION: " + e.getNextException ().toString ());
                } // if

                throw new DBActionException (this, e, true);
            } // else ... other exception
        } // catch
    } // execPreparedStoredProc


    /**************************************************************************
     * Ends the resultset handling (for the current resultset or recordset). <BR/>
     *
     * @throws  DBActionException
     *          An error occurred when trying to close the action.
     */
    public void end () throws DBActionException
    {
        // try to close Callabale Statement
        try
        {
            if (this.p_callStatement != null)
            {
                this.p_callStatement.close ();
                this.p_callStatement = null;
            } // if
        } // try
        catch (SQLException e)
        {
            throw new DBActionException (this, e, true);
        } // catch

        // try to close resultsets and statements
        try
        {
            if (this.p_statement != null)
            {
                if (this.p_resultset != null)
                {
                    this.p_resultset.close ();
                    this.p_resultset = null;
                } // if
                this.p_statement.close ();
                this.p_statement = null;
            } // if
        } // try
        catch (SQLException e)
        {
            throw new DBActionException (this, e, true);
        } // catch
    } // end


    /**************************************************************************
     * Send the current thread to sleep. <BR/>
     *
     * @param   count   Number of units the thread shall sleep.
     *                  If count is less than <CODE>1</CODE> it is handled as
     *                  <CODE>1</CODE>.
     * @param   time    Time in milliseconds for one unit.
     *                  If <CODE>time</CODE> is <CODE>0</CODE> or less the
     *                  method just returns.
     * @param   randomOffset    Number of milli seconds to be used as random
     *                          offset. There is a random number generated which
     *                          is at most as high as the randomOffset and which
     *                          is added to the computed time.
     *                          If randomOffset is <CODE>0</CODE> no offset is
     *                          used.
     */
    private void sleep (int count, int time, int randomOffset)
    {
        int countLocal = count;         // variable for local assignments
        long timeout = 0;               // the timeout

        // check if there shall be done any waiting:
        if (time <= 0 && randomOffset == 0) // no waiting necessary?
        {
            // terminate the method:
            return;
        } // if no waiting necessary

        // ensure correct range:
        if (countLocal < 1)
        {
            countLocal = 1;
        } // if

        // compute the base timeout:
        // timout is a number between time and 2 * time computed by
        // the following formula:
        // timeout = (2 * (2^count - 1) / 2^count) * time
        countLocal = 1 << countLocal;
        timeout = (long) (time * (countLocal - 1) * 2) / (time * countLocal);

        // compute and add the offset:
        if (randomOffset != 0)
        {
            timeout += SQLAction.RANDOM_DEADLOCK_OFFSET.nextInt (randomOffset);
        } // if

        try
        {
            Thread.sleep (timeout);
        } // try
        catch (InterruptedException e)
        {
            // print the stack trace to stderr:
            e.printStackTrace ();
        } // catch
    } // sleep


    /**************************************************************************
     * Starts a transaction. <BR/>
     *
     * @throws  DBActionException
     *          An exception occurred during trying to open the transaction.
     */
    public void beginTrans ()
        throws DBActionException
    {
        // TODO: currently there is no implementation
    } // beginTrans


    /**************************************************************************
     * Commit transaction. <BR/>
     *
     * @throws  DBActionException
     *          An exception occurred during trying to commit the transaction.
     */
    public void commitTrans () throws DBActionException
    {
        // TODO: currently there is no implementation
    } // commitTrans


    /**************************************************************************
     * Rollback transaction. <BR/>
     *
     * @throws  DBActionException
     *          An exception occurred during trying to rollback the transaction.
     */
    public void rollbackTrans () throws DBActionException
    {
        // TODO: currently there is no implementation
    } // rollbackTrans


    /**************************************************************************
     * Create the prepared statement for a query. <BR/>
     *
     * @param   queryStr    The query for which to create the statement.
     *
     * @return  The prepared statement.
     *
     * @throws  SQLException
     *          If a database access error occurs.
     *
     * @deprecated  KR 20090904 Use {@link #getPreparedStatement(StringBuilder)}
     *              instead.
     */
    @Deprecated
    public PreparedStatement getPreparedStatement (StringBuffer queryStr)
        throws SQLException
    {
        // call common method:
        return this.getPreparedStatement (queryStr.toString ());
    } // getPreparedStatement


    /**************************************************************************
     * Create the prepared statement for a query. <BR/>
     *
     * @param   queryStr    The query for which to create the statement.
     *
     * @return  The prepared statement.
     *
     * @throws  SQLException
     *          If a database access error occurs.
     */
    public PreparedStatement getPreparedStatement (StringBuilder queryStr)
        throws SQLException
    {
        // call common method:
        return this.getPreparedStatement (queryStr.toString ());
    } // getPreparedStatement


    /**************************************************************************
     * Create the prepared statement for a query. <BR/>
     *
     * @param   queryStr    The query for which to create the statement.
     *
     * @return  The prepared statement.
     *
     * @throws  SQLException
     *          If a database access error occurs.
     */
    public PreparedStatement getPreparedStatement (String queryStr)
        throws SQLException
    {
        // create the statement and return it:
        return this.p_connection.prepareStatement (queryStr);
    } // getPreparedStatement


    /**************************************************************************
     * Executes a SQL statement. <BR/>
     *
     * @param   stmt    Statement to be executed.
     *
     * @return  Number of rows (= tuples) in the resultset).
     *          If the query was an action query or no resulting tuple was
     *          found the value is 0;
     *
     * @throws  DBActionException
     *          An exception occurred during statement execution.
     *
     * @see     SQLResultset#open
     */
    public int execute (SQLStatement stmt)
        throws DBActionException
    {
        try
        {
            // call common method and return the result:
            return this.execute (stmt.toValidStringBuilder (), stmt.p_isAction);
        } // try
        catch (DBQueryException e)
        {
            // chain the exception:
            throw new DBActionException (this, e, true);
        } // catch
    } // execute


    /**************************************************************************
     * Executes a SQL statement. <BR/>
     *
     * @param   strSQL  Query string to be executed.
     * @param   action  Indicates wether an action query will be performed or
     *                  not. An action query is a query which (possibly)
     *                  performs changes within the database.
     *
     * @return  Number of rows (= tuples) in the resultset).
     *          If the query was an action query or no resulting tuple was
     *          found the value is 0;
     *
     * @throws  DBActionException
     *          An exception occurred during statement execution.
     *
     * @see     SQLResultset#open
     */
    public int execute (StringBuilder strSQL, boolean action)
        throws DBActionException
    {
        // call common method and return the result:
        return this.execute (strSQL.toString (), action);
    } // execute


    /**************************************************************************
     * Executes a SQL statement. <BR/>
     *
     * @param   strSQL  Query string to be executed.
     * @param   action  Indicates wether an action query will be performed or
     *                  not. An action query is a query which (possibly)
     *                  performs changes within the database.
     *
     * @return  Number of rows (= tuples) in the resultset).
     *          If the query was an action query or no resulting tuple was
     *          found the value is 0;
     *
     * @throws  DBActionException
     *          An exception occurred during statement execution.
     *
     * @see     SQLResultset#open
     */
    public int execute (StringBuffer strSQL, boolean action)
        throws DBActionException
    {
        // call common method and return the result:
        return this.execute (strSQL.toString (), action);
    } // execute


    /**************************************************************************
     * Executes a SQL statement. <BR/>
     *
     * @param   strSQL  Query string to be executed.
     * @param   action  Indicates wether an action query will be performed or
     *                  not. An action query is a query which (possibly)
     *                  performs changes within the database.
     *
     * @return  Number of rows (= tuples) in the resultset).
     *          If the query was an action query or no resulting tuple was
     *          found the value is 0;
     *
     * @throws  DBActionException
     *          An exception occurred during statement execution.
     *
     * @see     SQLResultset#open
     */
    public int execute (String strSQL, boolean action)
        throws DBActionException
    {
        long start = 0;
        long end = 0;
        int rows = 0;

//trace ("Query: " + strSQL);
//System.out.println ("Query: " + strSQL);
        if (this.p_traceMode)
        {
            System.out.println ("executing Query: " + strSQL);
        } // if
        start = System.currentTimeMillis ();

        try
        {
            this.p_statement = this.p_connection.createStatement (
                ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            this.printSqlWarnings (this.p_statement.getWarnings ());

            if (action)
            {
                // execute the statement:
                this.execStatement (this.p_statement,
                    SQLConstants.RETRIES_ON_DEADLOCK, strSQL);
            }  // if
            else
            {
                // execute the query:
                this.p_resultset = this.execQuery (this.p_statement,
                    SQLConstants.RETRIES_ON_DEADLOCK, strSQL);

                // check if there is a resultset:
                if (this.p_resultset != null)
                {
                    if ((this.p_moreElements = this.p_resultset.next ()) == true)
                    {
                        rows = 1;
                    } // if
                    else
                    {
                        rows = 0;
                    } // else
                    this.p_nextDone = true;
                } // if
            } // else

            end = System.currentTimeMillis ();
	        if (this.p_traceMode)
	        {
	            System.out.println ("time: " + (end - start) + "ms");
	        } // if

	        // check if the query lasted too long:
	        if ((end - start) > 2000)
	        {
	            this.writeLog (0, "",
	                "SQLAction: query needed more than 2 seconds: " +
	                    (end - start) + "ms.", strSQL, null);
	        } // if ((end - start) > 2000)

            // DB action succeeded
            this.p_retriesConnLoss = SQLConstants.RETRIES_FOR_DBCONNECTION;

        } // try
        catch (SQLException e)
        {
        	// try to reconnect
        	if (this.p_retriesConnLoss > 0)
        	{
        		this.recreateDBConnection ("query", strSQL, e);

        		// try again to create and execute the SQL statement
        		this.execute (strSQL, action);

        	} // if (retriesOnConnectionLoss > 0)

        	// not successfully in reconnecting too many times
        	else
        	{
        		this.writeLog(e.getErrorCode (), e.getSQLState (),
    					"SQLAction: DB connection loss on statement execution. " + 
    					SQLConstants.RETRIES_FOR_DBCONNECTION + " done, " + this.p_retriesConnLoss +
    					" left. Context = " + this.p_caller,
    					strSQL, null);

        		end = System.currentTimeMillis ();
    	        if (this.p_traceMode)
    	        {
    	            System.out.println ("time: " + (end - start) + "ms");
    	        } // if

    	        // check if the query lasted too long:
    	        if ((end - start) > 2000)
    	        {
    	            this.writeLog (0, "",
    	                "SQLAction: query needed more than 2 seconds: " +
    	                    (end - start) + "ms.", strSQL, null);
    	        } // if ((end - start) > 2000)

        		this.sendMessage (e);

        		throw new DBActionException (this, e, true);
        	} // else

        } // catch

        return rows;
    } // execute


	/**************************************************************************
	 * Collect reconnection failures and send the system administrator an e-mail
	 * when too many failures occurred within a defined timespan <BR/>
	 * 
	 * @param e		The SQLException which caused the loss of connection handling
	 */
	private void sendMessage (SQLException e)
	{
		// timestamp only for use in loop
		long timestamp = 0;

		// current timestamp
		long currTimestamp = System.currentTimeMillis ();

		// create the list for storing when all DB reconnects have failed
		// if not already done
		synchronized (SQLAction.VERSIONINFO)
		{
			if (SQLAction.p_reconnectionErrors == null)
			{
				SQLAction.p_reconnectionErrors = new LinkedBlockingDeque<Long> (SQLConstants.THRESHOLD_OF_CONNECTION_LOSSES);
			} // if
		} // synchronized (SQLAction.p_reconnectionErrors)

		// Remove all failed reconnections which happened prior the defined saving interval
		for (Iterator<Long> iter = SQLAction.p_reconnectionErrors.iterator(); iter.hasNext ();)
		{
		    timestamp = iter.next ().longValue ();
		    if (System.currentTimeMillis() - timestamp > SQLConstants.TIMESPAN_OF_CONNECTION_LOSSES)
		    {
		    	SQLAction.p_reconnectionErrors.pollFirst ();
		    } // if
		} // for iterator

		// Clear the queue and write message to SysAdmin
		if (! SQLAction.p_reconnectionErrors.offerLast (currTimestamp))
		{
			// reset the variable anyway (for next usage of SQLAction object)
			this.p_retriesConnLoss = SQLConstants.RETRIES_FOR_DBCONNECTION;

			try
		    {
		        // create the mail
				Calendar calendar = Calendar.getInstance ();
				SimpleDateFormat dateformatter = new SimpleDateFormat ("d MMM yyy, HH:mm:ss");
				EMail mail = new EMail ();
				StringBuilder content = new StringBuilder ();

				content.append ("Message from openM2: \n\nThe database connection is closed. Reason: ");
				content.append (e.getMessage ());
				content.append ("\n\nThe connection losses occurred:");

				for (Iterator<Long> iter = SQLAction.p_reconnectionErrors.iterator (); iter.hasNext ();)
				{
					calendar.setTimeInMillis (iter.next ().longValue ());
					content.append ("\n\t at ").append (dateformatter.format (calendar.getTime ()));
				}

				calendar.setTimeInMillis (currTimestamp);
				content.append ("\n\t and at ").append (dateformatter.format (calendar.getTime ())).append (".");

				mail.setSender (this.p_mailSystem);
				mail.setReceiver (this.p_mailAdmin);
				mail.setSubject ("openM2 ERROR Notification");
				mail.setContent (content.toString ());
				EMailManager.sendMail (mail, this.p_smtpServer);

				SQLAction.p_reconnectionErrors.clear ();
		    } // try
            catch (Exception exc)
            {
                // nothing useful to do 
            } // catch
		} // if (! SQLAction.p_reconnectionErrors.offerLast (System.currentTimeMillis())
	} // sendMessage   


    /**************************************************************************
     * Execute an sql statement. <BR/>
     *
     * If the execution returns a deadlock, then it will be retried again
     * and again ('retriesOnDealock'-number of times). After each retry-attempt
     * the next call will be delayed some milliseconds to give the db some time
     * to finish its other activities. If the given number of retries is 0 the
     * deadlock-exeption will be thrown. <BR/>
     *
     * @param   statement           The statement to be executed.
     * @param   retriesOnDeadlock   Number of retries if deadlock occurs.
     * @param   queryString         The query.
     *
     * @throws	SQLException
     * 			Error when DB connection has been lost.
     * @throws  DBActionException
     *          Other error when executing the statement.
     */
    private void execStatement (Statement statement, int retriesOnDeadlock,
                                String queryString)
                                		throws DBActionException, SQLException
    {
        String sqlState = null;         // state of sql statement

        try
        {
            // execute the statement:
            statement.execute (queryString);
/*
            writeLog (0, "00000",
                "SQLAction: retry statement on deadlock, retriesleft=" +
                retriesOnDeadlock + ", context=" + this.p_caller,
                queryString, null);
*/
        } // try
        catch (SQLException e)
        {
            // get sql state:
            sqlState = e.getSQLState ();

            // check if exception is a deadlock (according to sqlstate of the
            // X/OPEN SQL 1995 standard which is used with JDBC)
            if (sqlState != null && sqlState.equals ("40001") &&
                retriesOnDeadlock > 0)
            {
                // on each retry wait a little bit (100 ms) longer
                this.sleep (
                    SQLConstants.RETRIES_ON_DEADLOCK - retriesOnDeadlock,
                    SQLConstants.RETRYTIME_ON_DEADLOCK,
                    SQLConstants.RETRYTIMEOFFSET_ON_DEADLOCK);

                if (this.p_traceMode)
                {
                    System.out.println (
                        "SQLAction: retry statement on deadlock; statement=" +
                        queryString +
                        "; retry=" + retriesOnDeadlock +
                        "; errorcode=" + e.getErrorCode () +
                        "; context=" + this.p_caller);
                } // if
                this.writeLog (e.getErrorCode (), sqlState,
                    "SQLAction: retry statement on deadlock; retriesleft=" +
                    retriesOnDeadlock + "; context=" + this.p_caller,
                    queryString, null);

                this.execStatement (statement, retriesOnDeadlock - 1, queryString);
            } // if

            // check if exception is a loss of database connection
            else if (sqlState != null && (sqlState.equals ("08S01") || sqlState.equals ("08007") ||
            		sqlState.equals ("08900") || sqlState.equals ("08003")) )
            {
            	// handle the exception in this.execute (String, boolean)
            	throw e;
            } // else if

            else                        // other exception
            {
                System.out.println ("Exception SQLAction: " + e.toString () + "\n" +
                    "errorcode: " + e.getErrorCode () + "\n" +
                    "message: " + e.getMessage () + "\n" +
                    "localized message: " + e.getLocalizedMessage () + "\n" +
                    "sqlstate: " + sqlState + "\n" +
                    "stacktrace: " + e.fillInStackTrace () + "\n" +
                    "call: " + queryString);
                this.writeLog (e.getErrorCode (), sqlState, e.getMessage (),
                    queryString, Helpers.getStackTraceFromThrowable (e));

                if (e.getNextException () != null)
                {
                    System.out.println ("CHAINED EXCEPTION: " +
                        e.getNextException ().toString ());
                } // if

                // throw the exception:
                throw new DBActionException (this, e, true);
            } // else other exception
        } // catch
    } // execStatement


    /**************************************************************************
     * Execute an sql query. <BR/>
     *
     * If the execution returns a deadlock, then it will be retried again
     * and again ('retriesOnDealock'-number of times). After each retry-attempt
     * the next call will be delayed some milliseconds to give the db some time
     * to finish its other activities. If the given number of retries is 0 the
     * deadlock-exeption will be thrown. <BR/>
     *
     * @param   statement           	The statement to be executed.
     * @param   retriesOnDeadlock   	Number of retries if deadlock occurs.
     * 									connection.
     * @param   queryString         	The query.
     *
     * @return  The resulting resultset or
     *          <CODE>null</CODE> if there occurred an error.
     *
     * @throws  DBActionException
     *          Error when executing the statement.
     * @throws	SQLException
     * 			Error caused by invalid of DB connection.
     */
    private ResultSet execQuery (Statement statement, int retriesOnDeadlock,
                                String queryString) throws DBActionException, SQLException
    {
        ResultSet resultset = null;     // the resultset of the query
        String sqlState = null;         // state of sql statement

        try
        {
            // execute the query:
            resultset = statement.executeQuery (queryString);
        } // try
        catch (SQLException e)
        {
            // get sql state:
            sqlState = e.getSQLState ();

            // check if exception is a deadlock (according to sqlstate of the
            // X/OPEN SQL 1995 standard which is used with JDBC)
            if (sqlState != null && sqlState.equals ("40001") &&
                retriesOnDeadlock > 0)
            {
                // on each retry wait a little bit (100 ms) longer
                this.sleep (
                    SQLConstants.RETRIES_ON_DEADLOCK - retriesOnDeadlock,
                    SQLConstants.RETRYTIME_ON_DEADLOCK,
                    SQLConstants.RETRYTIMEOFFSET_ON_DEADLOCK);

                if (this.p_traceMode)
                {
                    System.out.println (
                        "SQLAction: retry query on deadlock; query=" +
                        queryString +
                        "; retry=" + retriesOnDeadlock +
                        "; errorcode=" + e.getErrorCode () +
                        "; context=" + this.p_caller);
                } // if
                this.writeLog (e.getErrorCode (), sqlState,
                    "SQLAction: retry query on deadlock, retriesleft=" +
                    retriesOnDeadlock + ", context=" + this.p_caller,
                    queryString, null);

                resultset = this.execQuery (statement, retriesOnDeadlock - 1, queryString);
            } // if

            // check if exception is a loss of database connection
            else if (sqlState != null && (sqlState.equals ("08S01") || sqlState.equals ("08007") ||
            		sqlState.equals ("08900") || sqlState.equals ("08003")) )
            {
            	// handle the exception in this.execute (String, boolean)
            	throw e;
            } // else if

            // other exception
            else
            {
                System.out.println ("Exception SQLAction: " + e.toString () + "\n" +
                    "errorcode: " + e.getErrorCode () + "\n" +
                    "message: " + e.getMessage () + "\n" +
                    "localized message: " + e.getLocalizedMessage () + "\n" +
                    "sqlstate: " + sqlState + "\n" +
                    "stacktrace: " + e.fillInStackTrace () + "\n" +
                    "call: " + queryString);
                this.writeLog (e.getErrorCode (), sqlState, e.getMessage (),
                    queryString, Helpers.getStackTraceFromThrowable (e));

                if (e.getNextException () != null)
                {
                    System.out.println ("CHAINED EXCEPTION: " +
                        e.getNextException ().toString ());
                } // if

                // throw the exception:
                throw new DBActionException (this, e, true);
            } // else other exception
        } // catch

        // return the resultset:
        return resultset;
    } // execQuery


    /**************************************************************************
     * Execute a prepared update statement. <BR/>
     *
     * The statement must already have been initialized before. Also the
     * parameters must have been set. <BR/>
     *
     * @param   statement           The statement to be executed.
     * @param   retriesOnDeadlock   Number of retries if deadlock occurs.
     *
     * @return either (1) the row count for <code>INSERT</code>,
     *         <code>UPDATE</code>, or <code>DELETE</code> statements
     *         or (2) <CODE>0</CODE> for SQL statements that return nothing.
     * @throws  SQLException
     * 			Error when DB connection has been lost.
     * @throws  DBActionException
     *          Other error when executing the statement.
     */
    public int execUpdateStatement (PreparedStatement statement,
                                    int retriesOnDeadlock)
        throws DBActionException, SQLException
    {
        int rowCount = 0;               // the number of rows which were changed
        String sqlState = null;         // state of sql statement

        try
        {
            // execute the statement:
            rowCount = statement.executeUpdate ();
        } // try
        catch (SQLException e)
        {
            // get sql state:
            sqlState = e.getSQLState ();

            // check if exception is a deadlock (according to sqlstate of the
            // X/OPEN SQL 1995 standard which is used with JDBC)
            if (sqlState != null && sqlState.equals ("40001") &&
                retriesOnDeadlock > 0)
            {
                // on each retry wait a little bit (100 ms) longer
                this.sleep (
                    SQLConstants.RETRIES_ON_DEADLOCK - retriesOnDeadlock,
                    SQLConstants.RETRYTIME_ON_DEADLOCK,
                    SQLConstants.RETRYTIMEOFFSET_ON_DEADLOCK);

                if (this.p_traceMode)
                {
                    System.out.println (
                        "SQLAction: retry query on deadlock; query=" +
                        statement.toString () +
                        "; retry=" + retriesOnDeadlock +
                        "; errorcode=" + e.getErrorCode () +
                        "; context=" + this.p_caller);
                } // if
                this.writeLog (e.getErrorCode (), sqlState,
                    "SQLAction: retry query on deadlock, retriesleft=" +
                    retriesOnDeadlock + ", context=" + this.p_caller,
                    statement.toString (), null);

                rowCount = this.execUpdateStatement (statement,
                    retriesOnDeadlock - 1);
            } // if

            // check if exception is a loss of database connection
            else if (sqlState != null && (sqlState.equals ("08S01") || sqlState.equals ("08007") ||
            		sqlState.equals ("08900") || sqlState.equals ("08003")) )
            {
            	// handle the exception in caller
            	throw e;
            } // else if

            else                        // other exception
            {
                System.out.println ("Exception SQLAction: " + e.toString () + "\n" +
                    "errorcode: " + e.getErrorCode () + "\n" +
                    "message: " + e.getMessage () + "\n" +
                    "localized message: " + e.getLocalizedMessage () + "\n" +
                    "sqlstate: " + sqlState + "\n" +
                    "stacktrace: " + e.fillInStackTrace () + "\n" +
                    "call: " + statement.toString ());
                this.writeLog (e.getErrorCode (), sqlState, e.getMessage (),
                    statement.toString (), Helpers
                        .getStackTraceFromThrowable (e));

                if (e.getNextException () != null)
                {
                    System.out.println ("CHAINED EXCEPTION: " +
                        e.getNextException ().toString ());
                } // if

                // throw the exception:
                throw new DBActionException (this, e, true);
            } // else other exception
        } // catch

        // return the row count:
        return rowCount;
    } // execUpdateStatement


    /**************************************************************************
     * Execute a prepared query. <BR/>
     *
     * The statement must already have been initialized before. Also the
     * parameters must have been set. <BR/>
     *
     * @param   statement           The statement to be executed.
     * @param   retriesOnDeadlock   Number of retries if deadlock occurs.
     *
     * @return  The resulting resultset or
     *          <CODE>null</CODE> if there occurred an error.
     *
     *@throws	SQLException
     *			Error when DB connection has been lost.
     * @throws  DBActionException
     *          Other error when executing the statement.
     */
    public ResultSet execQuery (PreparedStatement statement,
                                int retriesOnDeadlock) throws DBActionException, SQLException
    {
        ResultSet resultset = null;     // the resultset of the query
        String sqlState = null;         // state of sql statement

        try
        {
            // execute the query:
            resultset = statement.executeQuery ();
        } // try
        catch (SQLException e)
        {
            // get sql state:
            sqlState = e.getSQLState ();

            // check if exception is a deadlock (according to sqlstate of the
            // X/OPEN SQL 1995 standard which is used with JDBC)
            if (sqlState != null && sqlState.equals ("40001") &&
                retriesOnDeadlock > 0)
            {
                // on each retry wait a little bit (100 ms) longer
                this.sleep (
                    SQLConstants.RETRIES_ON_DEADLOCK - retriesOnDeadlock,
                    SQLConstants.RETRYTIME_ON_DEADLOCK,
                    SQLConstants.RETRYTIMEOFFSET_ON_DEADLOCK);

                if (this.p_traceMode)
                {
                    System.out.println (
                        "SQLAction: retry query on deadlock; query=" +
                        statement.toString () +
                        "; retry=" + retriesOnDeadlock +
                        "; errorcode=" + e.getErrorCode () +
                        "; context=" + this.p_caller);
                } // if
                this.writeLog (e.getErrorCode (), sqlState,
                    "SQLAction: retry query on deadlock, retriesleft=" +
                    retriesOnDeadlock + ", context=" + this.p_caller,
                    statement.toString (), null);

                resultset = this.execQuery (statement, retriesOnDeadlock - 1);
            } // if

            // check if exception is a loss of database connection
            else if (sqlState != null && (sqlState.equals ("08S01") || sqlState.equals ("08007") ||
            		sqlState.equals ("08900") || sqlState.equals ("08003")) )
            {
            	// handle the exception in caller
            	throw e;
            } // else if

            else                        // other exception
            {
                System.out.println ("Exception SQLAction: " + e.toString () + "\n" +
                    "errorcode: " + e.getErrorCode () + "\n" +
                    "message: " + e.getMessage () + "\n" +
                    "localized message: " + e.getLocalizedMessage () + "\n" +
                    "sqlstate: " + sqlState + "\n" +
                    "stacktrace: " + e.fillInStackTrace () + "\n" +
                    "call: " + statement.toString ());
                this.writeLog (e.getErrorCode (), sqlState, e.getMessage (),
                    statement.toString (), Helpers
                        .getStackTraceFromThrowable (e));

                if (e.getNextException () != null)
                {
                    System.out.println ("CHAINED EXCEPTION: " +
                        e.getNextException ().toString ());
                } // if

                // throw the exception:
                throw new DBActionException (this, e, true);
            } // else other exception
        } // catch

        // return the resultset:
        return resultset;
    } // execQuery


    /**************************************************************************
     * Submits a batch of SQL-statements (strings) for execution. <BR/>
     *
     * @param   batch A vector of query-strings.
     *
     * @return  An array of update counts containing one element for each command
     *          in the batch. The elements of the array are ordered according to
     *          the order in which commands were added to the batch
     *
     * @throws  DBActionException
     *          An exception occurred during statement execution.
     */
    public int[] executeBatch (Vector<String> batch)
        throws DBActionException
    {
        int[] rows = {0};
        long start = 0;
        long end = 0;
        String sqlString;

        // check if vector is empty
        if (batch.size () == 0)
        {
            return rows;
        } // if

        if (this.p_traceMode)
        {
            start = System.currentTimeMillis ();
            System.out.println ("excecuting Batch: " + batch.toString ());
        } // if

        try
        {
            // create statement
        	try
        	{
        		this.p_statement = this.p_connection.createStatement ();
        	} // try

        	catch (SQLException e)
        	{
        		// try to reconnect
            	if (this.p_retriesConnLoss > 0)
            	{
            		this.recreateDBConnection ("batch", batch.toString (), e);

            		// try again to create and execute the SQL statement
            		this.executeBatch (batch);

            	} // if (retriesOnConnectionLoss > 0)

            	// not successfully in reconnecting too many times
            	else
            	{
            		this.writeLog(e.getErrorCode (), e.getSQLState (),
        					"SQLAction: DB connection loss on statement execution. " + 
        					SQLConstants.RETRIES_FOR_DBCONNECTION + " done, " + this.p_retriesConnLoss +
        					" left. Context = " + this.p_caller,
        					batch.toString (), null);

            		this.sendMessage (e);

            		throw e;
            	} // else
        	} // catch (SQLException e)

            // add strings to batch
            for (int i = 0; i < batch.size (); i++)
            {
                sqlString = batch.elementAt (i);
                this.p_statement.addBatch (sqlString);
            } // for

            // execute batch
            this.p_statement.executeBatch ();
        } // try
        catch (SQLException e)
        {
            throw new DBActionException (this, e, true);
        } // catch
        finally
        {
            try
            {
                this.p_statement.clearBatch ();
            } // try
            catch (SQLException e)
            {
                throw new DBActionException (this, e, true);
            } // catch
        } // finally

        if (this.p_traceMode)
        {
            end = System.currentTimeMillis ();
            System.out.println ("time: " + (end - start) + "ms");
        } // if

        return rows;
    } // executeBatch


    /**************************************************************************
     * Get the current result set. <BR/>
     *
     * @return  The result set of the action.
    */
    public ResultSet getResultSet ()
    {
        // get the result set and return it:
        return this.p_resultset;
    } // getResultSet


    /**************************************************************************
     * Retrieves a boolean value from a column at the current row
     * in the open resultset. <BR/>
     *
     * @param   column  The chosen column.
     *
     * @return  The value as a boolean if possible, else false. <BR/>
     *          If the value is null the next call to wasNull will return true.
     *
     * @throws  DBParameterException
     *          Either the resultset is <CODE>null</CODE> or
     *          an exception occurred during reading the value.
    */
    public boolean getBoolean (String column) throws DBParameterException
    {
        boolean retVal = false;                // return value

        if (this.p_resultset == null)
        {
            throw new DBParameterException (this, null, true);
        } // if

        try
        {
            retVal = this.p_resultset.getBoolean (column);
        } // try
        catch (SQLException e)
        {
            throw new DBParameterException (this, e, true);
        } // catch

        return retVal;
    } // getBoolean


    /**************************************************************************
     * Retrieves a byte from a column at the current row
     * in the open resultset. <BR/>
     *
     * @param   column  The chosen column.
     *
     * @return  The value as a byte if possible, else 0. <BR/>
     *          If the value is null the next call to wasNull will return true.
     *
     * @throws  DBParameterException
     *          Either the resultset is <CODE>null</CODE> or
     *          an exception occurred during reading the value.
     */
    public byte getByte (String column) throws DBParameterException
    {
        byte retVal = 0;                // return value

        if (this.p_resultset == null)
        {
            throw new DBParameterException (this, null, true);
        } // if

        try
        {
            retVal = this.p_resultset.getByte (column);
        } // try
        catch (SQLException e)
        {
            throw new DBParameterException (this, e, true);
        } // catch

        return retVal;
    } // getByte


    /**************************************************************************
     * Retrieves a binary value from a column at the current row
     * in the open resultset. <BR/>
     *
     * @param   column  The chosen column.
     *
     * @return  The value as a byte array if possible, else <CODE>null</CODE>.
     *. <BR/>
     *          If the value is <CODE>null</CODE> the next call to
     *          {@link #wasNull wasNull} will return <CODE>true</CODE>.
     *
     * @throws  DBParameterException
     *          Either the resultset is <CODE>null</CODE> or
     *          an exception occurred during reading the value.
     */
    public byte[] getBinary (String column) throws DBParameterException
    {
        byte[] retVal = null;           // return value

        if (this.p_resultset == null)
        {
            throw new DBParameterException (this, null, true);
        } // if

        try
        {
            // use binary operation to get the data:
            retVal = this.p_resultset.getBytes (column);
        } // try
        catch (SQLException e)
        {
            throw new DBParameterException (this, e, true);
        } // catch

        return retVal;
    } // getBinary


    /**************************************************************************
     * Retrieves a short from a column at the current row
     * in the open resultset. <BR/>
     *
     * @param   column  The chosen column.
     *
     * @return  The value as a short if possible, else 0. <BR/>
     *          If the value is null the next call to wasNull will return true.
     *
     * @throws  DBParameterException
     *          Either the resultset is <CODE>null</CODE> or
     *          an exception occurred during reading the value.
     */
    public short getShort (String column) throws DBParameterException
    {
        short retVal = 0;                // return value

        if (this.p_resultset == null)
        {
            throw new DBParameterException (this, null, true);
        } // if

        try
        {
            retVal = this.p_resultset.getShort (column);
        } // try
        catch (SQLException e)
        {
            throw new DBParameterException (this, e, true);
        } // catch

        return retVal;
    } // getShort


    /**************************************************************************
     * Retrieves an integer from a column at the current row
     * in the open resultset. <BR/>
     *
     * @param   column  The chosen column.
     *
     * @return  The value as an integer if possible, else 0. <BR/>
     *          If the value is null the next call to wasNull will return true.
     *
     * @throws  DBParameterException
     *          Either the resultset is <CODE>null</CODE> or
     *          an exception occurred during reading the value.
     */
    public int getInt (String column) throws DBParameterException
    {
        int retVal = 0;                // return value

        if (this.p_resultset == null)
        {
            throw new DBParameterException (this, null, true);
        } // if

        try
        {
            retVal = this.p_resultset.getInt (column);
        } // try
        catch (SQLException e)
        {
            throw new DBParameterException (this, e, true);
        } // catch

        return retVal;
    } // getInt


    /**************************************************************************
     * Retrieves a float from a column at the current row
     * in the open resultset. <BR/>
     *
     * @param   column  The chosen column.
     *
     * @return  The value as a float if possible, else 0. <BR/>
     *          If the value is null the next call to wasNull will return true.
     *
     * @throws  DBParameterException
     *          Either the resultset is <CODE>null</CODE> or
     *          an exception occurred during reading the value.
     */
    public float getFloat (String column) throws DBParameterException
    {
        float retVal = 0;                // return value

        if (this.p_resultset == null)
        {
            throw new DBParameterException (this, null, true);
        } // if

        try
        {
            retVal = this.p_resultset.getFloat (column);
        } // try
        catch (SQLException e)
        {
            throw new DBParameterException (this, e, true);
        } // catch

        return retVal;
    } // getFloat


    /**************************************************************************
     * Retrieves a double a column at the current row
     * in the open resultset. <BR/>
    *
     * @param   column  The chosen column.
     *
     * @return  The value as a double if possible, else 0. <BR/>
     *          If the value is null the next call to wasNull will return true.
     *
     * @throws  DBParameterException
     *          Either the resultset is <CODE>null</CODE> or
     *          an exception occurred during reading the value.
     */
    public double getDouble (String column) throws DBParameterException
    {
        double retVal = 0;                // return value

        if (this.p_resultset == null)
        {
            throw new DBParameterException (this, null, true);
        } // if

        try
        {
            retVal = this.p_resultset.getDouble (column);
        } // try
        catch (SQLException e)
        {
            throw new DBParameterException (this, e, true);
        } // catch

        return retVal;
    } // getDouble


    /**************************************************************************
     * Retrieves a Date value from a column at the current row
     * in the open resultset. <BR/>
     *
     * @param   column  The chosen column.
     *
     * @return  The value as a Date if possible, else null. <BR/>
     *          If the value is null the next call to wasNull will return true.
     *
     * @throws  DBParameterException
     *          Either the resultset is <CODE>null</CODE> or
     *          an exception occurred during reading the value.
     */
    public Date getDate (String column) throws DBParameterException
    {
        Date retVal = null;                // return value

        if (this.p_resultset == null)
        {
            throw new DBParameterException (this, null, true);
        } // if

        try
        {
            retVal = this.p_resultset.getTimestamp (column);
        } // try
        catch (SQLException e)
        {
            throw new DBParameterException (this, e, true);
        } // catch

        return retVal;
    } // getDate


    /**************************************************************************
     * Retrieves a Timestamp value from a column at the current row
     * in the open resultset. <BR/>
     *
     * @param   column  The chosen column.
     *
     * @return  The value as a Date if possible, else null. <BR/>
     *          If the value is null the next call to wasNull will return true.
     *
     * @throws  DBParameterException
     *          Either the resultset is <CODE>null</CODE> or
     *          an exception occurred during reading the value.
     */
    public Timestamp getTimestamp (String column)
        throws DBParameterException
    {
        java.sql.Timestamp retVal = null;                // return value

        if (this.p_resultset == null)
        {
            throw new DBParameterException (this, null, true);
        } // if

        try
        {
            retVal = this.p_resultset.getTimestamp (column);
        } // try
        catch (SQLException e)
        {
            throw new DBParameterException (this, e, true);
        } // catch

        return retVal;
    } // getTimestamp


    /**************************************************************************
     * Retrieves a currency value from a column at the current row
     * in the open resultset. <BR/>
     *
     * @param   column  The chosen column.
     *
     * @return  The value as a long if possible, else 0. <BR/>
     *          If the value is null the next call to wasNull will return true.
     *
     * @throws  DBParameterException
     *          Either the resultset is <CODE>null</CODE> or
     *          an exception occurred during reading the value.
     */
    public long getCurrency (String column) throws DBParameterException
    {
        long retVal = 0;                // return value

        if (this.p_resultset == null)
        {
            throw new DBParameterException (this, null, true);
        } // if

        try
        {
            retVal = this.p_resultset.getLong (column);
        } // try
        catch (SQLException e)
        {
            throw new DBParameterException (this, e, true);
        } // catch

        return retVal;
    } // getCurrency


    /**************************************************************************
     * Retrieves a String from a column at the current row
     * in the open resultset. <BR/>
     *
     * @param   column  The chosen column.
     *
     * @return  The value as a String if possible, else null. <BR/>
     *          If the value is null the next call to wasNull will return true.
     *
     * @throws  DBParameterException
     *          Either the resultset is <CODE>null</CODE> or
     *          an exception occurred during reading the value.
     */
    public String getString (String column) throws DBParameterException
    {
        String retVal = null;           // return value

        if (this.p_resultset == null)
        {
            throw new DBParameterException (this, null, true);
        } // if

        try
        {
            retVal = this.p_resultset.getString (column);
        } // try
        catch (SQLException e)
        {
            throw new DBParameterException (this, e, true);
        } // catch

        return retVal;
    } // getString


    /**************************************************************************
     * Retrieves a character array from a column at the current row
     * in the open resultset. <BR/>
     *
     * @param   column  The chosen column.
     *
     * @return  The value as a character array if possible, else null. <BR/>
     *          If the value is null the next call to wasNull will return true.
     *
     * @throws  DBParameterException
     *          Either the resultset is <CODE>null</CODE> or
     *          an exception occurred during reading the value.
     */
    public char[] getVarChar (String column) throws DBParameterException
    {
        String temp = this.getString (column);
        if (temp != null)
        {
            return temp.toCharArray ();
        } // if

        return null;
    } // getVarChar


    /**************************************************************************
     * Retrieves an object from a column at the current row
     * in the open resultset. <BR/>
     *
     * @param   column  The chosen column.
     *
     * @return  The value as an Object if possible, else null. <BR/>
     *          If the value is null the next call to wasNull will return true.
     *
     * @throws  DBParameterException
     *          Either the resultset is <CODE>null</CODE> or
     *          an exception occurred during reading the value.
     */
    public Object getObject (String column) throws DBParameterException
    {
        Object retVal = null;           // return value

        if (this.p_resultset == null)
        {
            throw new DBParameterException (this, null, true);
        } // if

        try
        {
            retVal = this.p_resultset.getObject (column);
        } // try
        catch (SQLException e)
        {
            throw new DBParameterException (this, e, true);
        } // catch

        return retVal;
    } // getObject


    /**************************************************************************
     * Check if the value requested by the last getXXX operation was null. <BR/>
     * This method returns true if the value was null. There can be more than
     * one call to this method which will always return the same value until
     * the next getXXX operation occurs.
     *
     * @return  true if the last value was null.
     */
    public boolean wasNull ()
    {
        return this.p_wasNull;               // return if the last getXXX was null
    } // wasNull


    /**************************************************************************
     * Check if we're at the end of the current result set. <BR/>
     *
     * @return  <CODE>true</CODE> if at the end of the result set.
     *
     * @throws  DBActionException
     *          An exception occurred during checking the end of the result set.
     */
    public boolean getEOF () throws DBActionException
    {
        if (!this.p_nextDone)
        {
            try
            {
                if (this.p_resultset != null)
                {
                    this.p_moreElements = this.p_resultset.next ();
                    this.p_nextDone = true;
                } // if
            } // try
            catch (SQLException e)
            {
                throw new DBActionException (this, e, true);
            } // catch
        } // if
        return !this.p_moreElements;
    } // getEOF


    /**************************************************************************
     * Steps forward within the result set. <BR/>
     *
     * @throws  DBActionException
     *          An exception occurred during trying to go to the next result
     *          row.
     */
    public void next () throws DBActionException
    {
        this.p_nextDone = false;
    } // next


    /**************************************************************************
     * Transfers parameters data type, direction and/or value of given stored
     * procedure to given jdbc-callable-statement. <BR/>
     *
     * @param   storedProcedure  The stored procedure object
     * @param   callStatement    The jdbc callable statement object
     *
     * @throws  DBParameterException
     *          An exception occurred while evaluating the return type or
     *          setting the parameters.
     * @throws  DBParameterDefinitionException
     *          An exception occurred during definition of parameters.
     */
    private void setParameters (StoredProcedure storedProcedure,
                                CallableStatement callStatement)
        throws DBParameterException, DBParameterDefinitionException
    {
        int paramNumber = 1;            // counter for actual jdbc parameter
        Parameter param;                // define placeholder Parameter: param

        try
        {
            // differ between stored procedures with or without return values
            if (storedProcedure.getReturnType () ==
                    StoredProcedureConstants.RETURN_VALUE)
            {
               // set counter to 1th parameter
                callStatement.registerOutParameter (paramNumber, Types.INTEGER);

                // increase currently-handled-parameter counter
                paramNumber++;
            } // if
            else if (storedProcedure.getReturnType () ==
                        StoredProcedureConstants.RETURN_NOTHING)
            {
                // ok - but do nothing
            } // else if
            else
            {
                throw new DBParameterException (this, null, true);
            } // else

            // loop through all parameters (excluding return value)
            for (int i = 0; i < storedProcedure.countParameters (); i++)
            {
                // get next parameter
                param = storedProcedure.getParameter (i);

                // set direction of parameter
                if (param.getDirection () == ParameterConstants.DIRECTION_IN)
                {
                    // differ between type
                    switch (param.getDataType ())
                    {
                        case ParameterConstants.TYPE_BOOLEAN:
                            if (param.getValueBoolean ())
                            {
                                callStatement.setInt (paramNumber, 1);
                            } // if
                            else
                            {
                                callStatement.setInt (paramNumber, 0);
                            } // else
                            break;
                        case ParameterConstants.TYPE_BYTE:
                            callStatement.setByte (paramNumber,
                                    param.getValueByte ());
                            break;
                        case ParameterConstants.TYPE_SHORT:
                            callStatement.setShort (paramNumber,
                                    param.getValueShort ());
                            break;
                        case ParameterConstants.TYPE_INTEGER:
                            callStatement.setInt (paramNumber,
                                    param.getValueInteger ());
                            break;
                        case ParameterConstants.TYPE_FLOAT:
                            callStatement.setFloat (paramNumber,
                                    param.getValueFloat ());
                            break;
                        case ParameterConstants.TYPE_DOUBLE:
                            callStatement.setDouble (paramNumber,
                                    param.getValueDouble ());
                            break;
                        case ParameterConstants.TYPE_DATE:
                            if (param.getValueDate () != null)
                            {
                                long temp = (param.getValueDate ()).getTime ();

                                java.sql.Timestamp sqldate
                                    = new java.sql.Timestamp (temp);
                                callStatement.setTimestamp (paramNumber,
                                        sqldate);
                            } // if
                            else
                            {
                                callStatement.setTimestamp (paramNumber, null);
                            } // else
                            break;
                        case ParameterConstants.TYPE_CURRENCY:
                            callStatement.setLong (paramNumber,
                                    param.getValueCurrency ());
                            break;
                        case ParameterConstants.TYPE_STRING:
                            String value = param.getValueString ();
                            if (value != null && value.length () > 0)
                            {
                                callStatement.setString (paramNumber, value);
                            } // if
                            else
                            {
                                callStatement.setString (paramNumber, " ");
                            } // else
                            break;
                        case ParameterConstants.TYPE_VARCHAR:
                            callStatement.setString(paramNumber, "" +
                                    param.getValueVarChar ().toString ());
                            break;
                        case ParameterConstants.TYPE_OBJECT:
                            callStatement.setObject (paramNumber, "" +
                                    param.getValueObject ());
                            break;
                        case ParameterConstants.TYPE_VARBYTE:
                            byte [] tempArr = param.getValueVarByte ();
                            callStatement.setBytes (paramNumber, tempArr);
                            break;
                        default:    // undefined data type
                            throw new DBParameterException (this, null, true);
                    } // switch
                } // if
                else if ((storedProcedure.getParameter (i)).getDirection () ==
                    ParameterConstants.DIRECTION_OUT)
                {
                    int type;
                    // differ between type
                    switch (param.getDataType ())
                    {
                        case ParameterConstants.TYPE_BOOLEAN:
                            type = Types.INTEGER;
                            break;
                        case ParameterConstants.TYPE_BYTE:
                            type = Types.TINYINT;
                            break;
                        case ParameterConstants.TYPE_SHORT:
                            type = Types.SMALLINT;
                            break;
                        case ParameterConstants.TYPE_INTEGER:
                            type = Types.INTEGER;
                            break;
                        case ParameterConstants.TYPE_FLOAT:
                            type = Types.FLOAT;
                            break;
                        case ParameterConstants.TYPE_DOUBLE:
                            type = Types.DOUBLE;
                            break;
                        case ParameterConstants.TYPE_DATE:
                            type = Types.TIMESTAMP;
                            break;
                        case ParameterConstants.TYPE_CURRENCY:
                            type = Types.BIGINT;
                            break;
                        case ParameterConstants.TYPE_STRING:
                            type = Types.VARCHAR;
                            break;
                        case ParameterConstants.TYPE_VARCHAR:
                            type = Types.VARCHAR;
                            break;
                        case ParameterConstants.TYPE_OBJECT:
                            type = Types.OTHER;
                            break;
                        case ParameterConstants.TYPE_VARBYTE:
                            type = Types.VARBINARY;
                            break;
                        case ParameterConstants.TYPE_TEXT:
                            type = Types.CLOB;
                            break;
                        default:    // undefined datatype
                            throw new DBParameterException (this, null, true);
                    } // switch

                    // set direction
                    callStatement.registerOutParameter (paramNumber, type);
                } // else if
                else
                {
                    throw new DBParameterException (this, null, true);
                } // else
                // increase currently-handled-parameter counter
                paramNumber++;
            } // for
        } // try
        catch (SQLException e)
        {
            throw new DBParameterException (this, e, true);
        } // catch
    } // setParameters


    /**************************************************************************
     * Transfers parameters values of the executed jdbc-callable-statement
     * back to the Parameter objects of the StoredProcedure object. <BR/>
     *
     * @param   storedProcedure  The stored procedure object
     * @param   callStatement    The jdbc callable statement object
     *
     * @throws  DBParameterException
     *          An exception occurred while getting the parameters.
     */
    private void getParameterValues (StoredProcedure storedProcedure,
                                     CallableStatement callStatement)
        throws DBParameterException
    {
        int paramNumber = 1;            // counter for  jdbc parameter
        Parameter param;                // placeholder Parameter: param

        try
        {
            // differ between stored procedures with or without return values
            if (storedProcedure.getReturnType () ==
                StoredProcedureConstants.RETURN_VALUE)
            {
                storedProcedure.setReturnValue (-1);

                // IBS-283: Necessary when using jTDS driver
                callStatement.getMoreResults();
                
                // get return value out of jdbc prepared statement and
                // set it in stored procedure object
                storedProcedure.setReturnValue (
                        callStatement.getInt (paramNumber));

                // increase currently-handled-parameter counter
                paramNumber++;
            } // if

            // loop through all out parameters (excluding return value)
            // and set values compliant to rdo-prep-statement parameters
            // out values
            for (int i = 0; i < storedProcedure.countParameters (); i++)
            {
                // get next parameter
                param = storedProcedure.getParameter (i);

                // check only OUT parameters
                if (param.getDirection () == ParameterConstants.DIRECTION_OUT)
                {
                    // differ between types - translate from rdo Variant
                    // to right stored-procedure-parameter data type
                    switch (param.getDataType ())
                    {
                        case ParameterConstants.TYPE_BOOLEAN:
                            boolean bool =
                                    callStatement.getBoolean (paramNumber);
                            if (callStatement.wasNull ())
                            {
                                param.setNull ();
                            } // if
                            else
                            {
                                param.setValue (bool);
                            } // else
                            break;
                        case ParameterConstants.TYPE_BYTE:
                            byte by = callStatement.getByte (paramNumber);
                            if (callStatement.wasNull ())
                            {
                                param.setNull ();
                            } // if
                            else
                            {
                                param.setValue (by);
                            } // else
                            break;
                        case ParameterConstants.TYPE_SHORT:
                            short sh = callStatement.getShort (paramNumber);
                            if (callStatement.wasNull ())
                            {
                                param.setNull ();
                            } // if
                            else
                            {
                                param.setValue (sh);
                            } // else
                            break;
                        case ParameterConstants.TYPE_INTEGER:
                            int in = callStatement.getInt (paramNumber);
                            if (callStatement.wasNull ())
                            {
                                param.setNull ();
                            } // if
                            else
                            {
                                param.setValue (in);
                            } // else
                            break;
                        case ParameterConstants.TYPE_FLOAT:
                            float fl = callStatement.getFloat (paramNumber);
                            if (callStatement.wasNull ())
                            {
                                param.setNull ();
                            } // if
                            else
                            {
                                param.setValue (fl);
                            } // else
                            break;
                        case ParameterConstants.TYPE_DOUBLE:
                            double doub = callStatement.getDouble (paramNumber);
                            if (callStatement.wasNull ())
                            {
                                param.setNull ();
                            } // if
                            else
                            {
                                param.setValue (doub);
                            } // else
                            break;
                        case ParameterConstants.TYPE_DATE:
                            // use timestamp instead of date! avoids loosing
                            // time information.
                            java.sql.Timestamp date =
                                    callStatement.getTimestamp (paramNumber);
                            if (callStatement.wasNull ())
                            {
                                param.setNull ();
                            } // if
                            else
                            {
                                // define timezone, calendar, ..
                                GregorianCalendar cal =
                                        new GregorianCalendar ();
                                // set date in calendar
                                cal.setTime (date);
                                // get date out of calendar
                                param.setValue (cal.getTime ());
                            } // else
                            break;
                        case ParameterConstants.TYPE_CURRENCY:
                            long lo = callStatement.getLong (paramNumber);
                            if (callStatement.wasNull ())
                            {
                                param.setNull ();
                            } // if
                            else
                            {
                                param.setValue (lo);
                            } // else
                            break;
                        case ParameterConstants.TYPE_STRING:
                            String str = callStatement.getString (paramNumber);
                            if (callStatement.wasNull ())
                            {
                                param.setNull ();
                            } // if
                            // BT/KR IBS-275 20091008:
                            // Check if the string is " " because the database driver
                            // Sprinta2000 returns this value for empty STRING fields.
                            else if (str.equals (" "))
                            {
                                param.setValue ("");
                            } // else = " "
                            else
                            {
                                param.setValue (str);
                            } // else
                            break;
                        case ParameterConstants.TYPE_VARCHAR:
                            String str2 = callStatement.getString (paramNumber);
                            if (callStatement.wasNull ())
                            {
                                param.setNull ();
                            } // if
                            else
                            {
                                param.setValue (str2.toCharArray ());
                            } // else
                            break;
                        case ParameterConstants.TYPE_OBJECT:
                            Object obj = callStatement.getObject (paramNumber);
                            if (callStatement.wasNull ())
                            {
                                param.setNull ();
                            } // if
                            else
                            {
                                param.setValue (obj);
                            } // else
                            break;
                        case ParameterConstants.TYPE_VARBYTE:
                            byte[] bytes = callStatement.getBytes (paramNumber);
                            if (callStatement.wasNull ())
                            {
                                param.setNull ();
                            } // if
                            else
                            {
                                param.setValue (bytes);
                            } // else
                            break;
                        case ParameterConstants.TYPE_TEXT:
                            // a CLOB-Object
                            Clob clob = callStatement.getClob (paramNumber);
                            if (callStatement.wasNull ())
                            {
                                param.setNull ();
                            } // if
                            else
                            {
                                String value =
                                    clob.getSubString (1, (int) clob.length ());
                                if (value.equals (" (null)"))
                                {
                                    value = " ";
                                } // if
                                param.setValue (value);
                            } // else
                            break;
                        default:
                            throw new DBParameterException (this, null, true);
                    } // switch
                } // if param.direction
                // increase currently-handled-parameter counter
                paramNumber++;
            } // for
        } // try
        catch (Exception e)
        {
            throw new DBParameterException (this, e, true);
        } // catch
    } // getParameterValues


    /**************************************************************************
     * Write the log. <BR/>
     * This method checks if there is a log set and sends the values to this
     * log.
     *
     * @param   errorcode   The internal error code.
     * @param   sqlstate    The sql state returned from the database driver.
     * @param   message     The message for the log entry.
     * @param   call        The sql call.
     * @param   stacktrace  The stack trace.
     */
    private void writeLog (int errorcode, String sqlstate, String message,
                           String call, String stacktrace)
    {
        // check if the log is set:
        if (this.p_log != null)         // log set?
        {
            this.p_log.addEntry (errorcode, sqlstate, message, call, stacktrace);
        } // if log set
    } // writeLog

} // class SQLAction
