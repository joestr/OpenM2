/*
 * Class: DBExec.java
 */

// package:
package ibs.install.sql;

// imports:
import ibs.di.DIConstants;
import ibs.di.Log_01;
import ibs.tech.sql.DBConf;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.sql.Connection;
import java.sql.DataTruncation;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.Vector;


/******************************************************************************
 * This class executes sql commands in a database. <BR/>
 *
 * @version     $Id: DBExec.java,v 1.7 2007/07/31 19:13:56 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR), 031002
 ******************************************************************************
 */
public class DBExec extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DBExec.java,v 1.7 2007/07/31 19:13:56 kreimueller Exp $";

    /**
     * Prefix for output messages. <BR/>
     * Prefix used to sign specific output messages.
     */
    private static final String OUTPUT_PREFIX = "    ==> ";

    /**
     * Display mode for displaying no statements. <BR/>
     */
    static final String DISP_NO = "dispno";

    /**
     * Display mode for displaying just the first line of statements. <BR/>
     */
    static final String DISP_ONE = "dispone";

    /**
     * Display mode for displaying the whole statements. <BR/>
     */
    static final String DISP_FULL = "dispfull";


    /**
     * The database connection. <BR/>
     */
    Connection p_conn = null;

    /**
     * Error message: error during connecting to database. <BR/>
     */
    private static final String ERRM_DBCONNECT =
        "error during connecting to database.";
    /**
     * Error message: execution of statement aborted. <BR/>
     */
    private static final String ERRM_EXECABORT =
        " Execution of statement aborted.";


    /**************************************************************************
     * The main method. <BR/>
     * Method for starting and executing the sql commands.
     *
     * @param   args    The command line arguments.
     */
    public static void main (String[] args)
//        throws java.io.IOException
    {
        DBExec.execApp (new DBExec (), args);
    } // main


    /**************************************************************************
     * The main method. <BR/>
     * Method for starting and executing the sql commands.
     *
     * @param   executor    The instance which is used for executing the
     *                      commands.
     * @param   args        The command line arguments.
     */
    public static final void execApp (DBExec executor, String[] args)
    {
        String dispMode = DBExec.DISP_ONE; // display mode
        boolean failure = false;        // was there a failure?

        // check the syntax:
        if (args.length >= 5 && args.length <= 6) // correct number of arguments?
        {
            if (args.length >= 6)       // display mode set?
            {
                dispMode = args[5];

                // check if display mode is valid:
                if (!dispMode.equals (DBExec.DISP_NO) && !dispMode.equals (DBExec.DISP_ONE) &&
                    !dispMode.equals (DBExec.DISP_FULL)) // not valid display mode?
                {
                    failure = true;
                } // if not valid display mode
            } // if display mode set

            // check if there was a failure:
            if (!failure)               // no failure?
            {
                // execute the statements of the file:
                executor.exec (args[0], args[1], args[2], args[3], args[4],
                    dispMode, null);
            } // if no failure
        } // if correct number of arguments
        else                            // wrong syntax in call
        {
            failure = true;
        } // else wrong syntax in call

        if (failure)                    // there was a failure?
        {
            // display the correct syntax:
            DBExec.syntax (args);
        } // if there was a failure
    } // execApp


    /**************************************************************************
     * Display the syntax of the program. <BR/>
     *
     * @param   args    The command line arguments.
     */
    static void syntax (String[] args)
    {
        DBExec.println (null, DIConstants.LOG_NOTYPE,
            "Syntax: DBExec system database username password filename [displaymode]\n" +
            "    DBExec ....... this program\n" +
            "    system ....... the name of the computing system which contains the database\n" +
            "    database ..... the database which shall be accessed\n" +
            "    username ..... the user name for getting into the database\n" +
            "    password ..... the password for the user\n" +
            "    filename ..... the file which contains the sql statements\n" +
            "    displaymode .. mode for displaying the statements\n" +
            "                   " + DBExec.DISP_NO + " ... no display\n" +
            "                   " + DBExec.DISP_ONE + " ... display first line of each statement(default)\n" +
            "                   " + DBExec.DISP_FULL + " ... display the whole statement\n" +
            "\n" +
            "example:\n" +
            "DBExec dbserver m2db user password test1.sql " + DBExec.DISP_FULL
        );
    } // syntax


    /**************************************************************************
     * The main method. <BR/>
     * Method for starting and executing the sql commands. <BR/>
     * This method establishes a connection and executes the file on this
     * connection.
     *
     * @param   systemName  The name of the system on which the database resides.
     * @param   dbName      The name of the database to connect to.
     * @param   userName    User name for connecting to the database.
     * @param   password    Password for the user.
     * @param   fileName    The name of the file to be read.
     * @param   dispMode    The display mode.
     * @param   log         The log to write output to.
     */
    public final void exec (String systemName, String dbName, String userName,
                            String password, String fileName, String dispMode,
                            Log_01 log)
    {
        try
        {
            // connect to the database:
            this.connect (systemName, dbName, userName, password, log);

            // execute the statements:
            this.exec (dbName, fileName, dispMode, log);
        } // try
        catch (SQLException e)
        {
            DBExec.println (log, DIConstants.LOG_ERROR, DBExec.ERRM_DBCONNECT +
                DBExec.ERRM_EXECABORT);
            DBExec.println (log, e);
//            e.printStackTrace ();
        } // catch
        finally
        {
            // disconnect from the database:
            this.disconnect (log);
        } // finally
    } // exec


    /**************************************************************************
     * The main method. <BR/>
     * Method for starting and executing the sql commands. <BR/>
     * This method establishes a connection and executes the file on this
     * connection.
     *
     * @param   dbConf      Configuration of database access.
     * @param   fileName    The name of the file to be read.
     * @param   dispMode    The display mode.
     * @param   log         The log to write output to.
     */
    public final void exec (DBConf dbConf, String fileName, String dispMode,
                            Log_01 log)
    {
        try
        {
            // connect to the database:
            this.connect (dbConf, log);

            // execute the statements:
            this.exec (dbConf.getDbSid (), fileName, dispMode, log);
        } // try
        catch (SQLException e)
        {
            DBExec.println (log, DIConstants.LOG_ERROR, DBExec.ERRM_DBCONNECT +
                DBExec.ERRM_EXECABORT);
            DBExec.println (log, e);
//            e.printStackTrace ();
        } // catch
        finally
        {
            // disconnect from the database:
            this.disconnect (log);
        } // finally
    } // exec


    /**************************************************************************
     * Method for starting and executing the sql commands. <BR/>
     * This method presumes that there is already a connection established with
     * {@link #connect(String, String, String, String, Log_01) connect}.
     *
     * @param   dbName      The name of the database to connect to.
     * @param   fileName    The name of the file to be read.
     * @param   dispMode    The display mode.
     */
    protected final void exec (String dbName, String fileName, String dispMode)
    {
        // call common method:
        this.exec (dbName, fileName, dispMode, null);
    } // exec


    /**************************************************************************
     * Method for starting and executing the sql commands. <BR/>
     * This method presumes that there is already a connection established with
     * {@link #connect(String, String, String, String, Log_01) connect}.
     *
     * @param   dbName      The name of the database to connect to.
     * @param   fileName    The name of the file to be read.
     * @param   dispMode    The display mode.
     * @param   log         The log to write output to.
     */
    protected final void exec (String dbName, String fileName, String dispMode,
                               Log_01 log)
    {
        Vector<String> statements;      // all statements

        // read the content of the file:
        statements = this.parseFile (dbName, fileName, log);
        // execute the statements:
        this.execSql (this.p_conn, statements, dispMode, log);
    } // exec


    /**************************************************************************
     * Connect to the database. <BR/>
     * This method tries to connect to the specified database. <BR/>
     * The property {@link #p_conn p_conn} is set to the actual connection.
     * If there was an error {@link #p_conn p_conn} is set to <CODE>null</CODE>.
     *
     * @param   systemName  The name of the system on which the database resides.
     * @param   dbName      The name of the database to connect to.
     * @param   userName    User name for connecting to the database.
     * @param   password    Password for the user.
     * @param   log         The log to write output to.
     *
     * @throws  SQLException
     *          An exception occurred during connecting.
     */
    protected void connect (String systemName, String dbName,
                            String userName, String password, Log_01 log)
        throws SQLException
    {
        // method has to be implemented in subclasses
    } // connect


    /**************************************************************************
     * Connect to the database. <BR/>
     * This method tries to connect to the specified database. <BR/>
     * The property {@link #p_conn p_conn} is set to the actual connection.
     * If there was an error {@link #p_conn p_conn} is set to <CODE>null</CODE>.
     *
     * @param   driverName  The database driver, i.e. the fullz qualified class
     *                      name.
     * @param   connectionString A database url of the form
     *                  <CODE>jdbc:<EM>subprotocol</EM>:<EM>subname</EM></CODE>
     * @param   systemName  The name of the system on which the database resides.
     * @param   dbName      The name of the database to connect to.
     * @param   userName    User name for connecting to the database.
     * @param   password    Password for the user.
     * @param   log         The log to write output to.
     *
     * @throws  SQLException
     *          An exception occurred during connecting.
     */
    protected void connect (String driverName, String connectionString,
                            String systemName, String dbName,
                            String userName, String password, Log_01 log)
        throws SQLException
    {
        DBExec.print (log, DIConstants.LOG_ENTRY,
               "connecting to " + systemName + "/" + dbName + " with " +
               userName + "... ");

        try
        {
            try
            {
                // get the driver manager:
                Class.forName (driverName);

                // open the connection:
                this.p_conn = DriverManager.getConnection (connectionString,
                    userName, password);
                // print possible sql warnings:
                this.printSqlWarnings (log, this.p_conn.getWarnings ());
                // got connection, setting catalog:
//println ("got connection, setting catalog...");
                this.p_conn.setCatalog (dbName);
            } // try
            catch (ClassNotFoundException e)
            {
                throw new SQLException ("SQL driver class " + driverName + " not found!");
            } // catch

            DBExec.println (log, DIConstants.LOG_ENTRY, "connection established.");
        } // try
        catch (SQLException e)
        {
            DBExec.println (log, e);    // print exception
            this.p_conn = null;         // drop connection data
            throw e;                    // throw the exception
        } // catch SQLException
    } // connect


    /**************************************************************************
     * Connect to the database. <BR/>
     * This method tries to connect to the specified database. <BR/>
     * The property {@link #p_conn p_conn} is set to the actual connection.
     * If there was an error {@link #p_conn p_conn} is set to <CODE>null</CODE>.
     *
     * @param   conf        Database configuration.
     * @param   log         The log to write output to.
     *
     * @throws  SQLException
     *          An exception occurred during connecting.
     */
    protected void connect (DBConf conf, Log_01 log)
        throws SQLException
    {
        // call common method:
        this.connect (conf.getDbJdbcDriverClass (),
                      conf.getDbConnectionString (),
                      conf.getDbServerName (), conf.getDbSid (),
                      conf.getDbUserName (),
                      new String (conf.getDbPassword ()), log);
    } // connect


    /**************************************************************************
     * Disconnect from the database. <BR/>
     * The property {@link #p_conn p_conn} is set to <CODE>null</CODE>.
     *
     * @param   log         The log to write output to.
     */
    protected void disconnect (Log_01 log)
    {
        DBExec.print (log, DIConstants.LOG_ENTRY, "disconnecting... ");

        // close the connection:
        if (this.p_conn != null)        // connection defined?
        {
            try
            {
                this.p_conn.close ();
                this.p_conn = null;
                DBExec.println (log, DIConstants.LOG_ENTRY, "disconnected.");
            } // try
            catch (SQLException e)
            {
                DBExec.println (log, e);
            } // catch SQLException
        } // if connection defined
        else                            // no connection found
        {
            DBExec.println (log, DIConstants.LOG_WARNING, "no connection found.");
        } // else no connection found
    } // disconnect


    /**************************************************************************
     * Execute a list of sql statements. <BR/>
     * These statements are Strings which are enumerated in a vector.
     *
     * @param   conn        The connection through which to execute the
     *                      statements.
     * @param   statements  A vector containing the statements.
     * @param   dispMode    The display mode.
     * @param   log         The log to write output to.
     */
    protected final void execSql (Connection conn, Vector<String> statements,
                                  String dispMode, Log_01 log)
    {
        DBExec.println (log, DIConstants.LOG_ENTRY, "\n" +
            DBExec.OUTPUT_PREFIX + "executing the statements...\n");

        if (statements == null)         // no statements defined?
        {
            DBExec.println (log, DIConstants.LOG_ENTRY, DBExec.OUTPUT_PREFIX +
                "no statements to execute.");
        } // if no statements defined
        else                            // there is at least one statement
        {
            // execute the statements:
            for (int i = 0; i < statements.size (); i++)
            {
                this.execSql (conn, statements.elementAt (i), dispMode, log);
            } // for
//            execSql (conn, "CREATE TABLE kr_test1 (id INT, id2 INT)");
//            execSql (conn, "DROP TABLE kr_test1");
        } // else there is at least one statement

        DBExec.println (log, DIConstants.LOG_ENTRY,
            DBExec.OUTPUT_PREFIX + "statements finished.\n");
    } // execSql


    /**************************************************************************
     * Execute a sql statement. <BR/>
     *
     * @param   conn        The connection through which to execute the
     *                      statement.
     * @param   queryStr    The sql statement to be executed.
     * @param   dispMode    The display mode.
     * @param   log         The log to write output to.
     */
    protected final void execSql (Connection conn, String queryStr,
                                  String dispMode, Log_01 log)
    {
        Statement stmt = null;          // the statement object
        boolean bool = false;           // result of statement execution
        int pos = 0;                    // position within string
        String finishLine = "";         // finish line to be printed after
                                        // statement execution

        // display the statement:
        if (dispMode.equals (DBExec.DISP_ONE)) // display first line?
        {
            // search for end of line:
            if ((pos = queryStr.indexOf ('\n')) > 0) // end of line found?
            {
                // display the first line:
                DBExec.println (log, DIConstants.LOG_ENTRY, queryStr.substring (0, pos) + "...");
            } // if end of line found
            else                            // no end of line
            {
                // display the whole statement:
                DBExec.println (log, DIConstants.LOG_ENTRY, queryStr);
            } // else no end of line
        } // if
        else if (dispMode.equals (DBExec.DISP_FULL)) // display whole statement?
        {
            DBExec.println (log, DIConstants.LOG_ENTRY, queryStr);
            finishLine = "\n";
        } // else if display whole statement

        try
        {
            // get the statement:
            stmt = conn.createStatement ();
            // execute the statement:
//            result = stmt.executeUpdate (queryStr);
            bool = stmt.execute (queryStr);

            // check if there is a result:
            if (bool)                   // there is a ResultSet?
            {
                DBExec.println (log, DIConstants.LOG_WARNING,
                    DBExec.OUTPUT_PREFIX + "statement executed with results." +
                         finishLine);
                // display the results:
                this.showResults (stmt);
            } // if there is a ResultSet
            else                        // no ResultSet
            {
                DBExec.println (log, DIConstants.LOG_ENTRY,
                    DBExec.OUTPUT_PREFIX + "statement executed with no results." +
                         finishLine);
            } // else no ResultSet
        } // try
        catch (SQLException e)
        {
            DBExec.println (log, e);
//            e.printStackTrace ();
            DBExec.print (log, DIConstants.LOG_ERROR, finishLine);
        } // catch SQLException
    } // execSql


    /**************************************************************************
     * Display the results of the execution of a sql statement. <BR/>
     *
     * @param   stmt        The statement which was already executed.
     */
    protected void showResults (Statement stmt)
    {
//        ResultSet results = stmt.getResultSet (); // the result set

        // loop through all tuples of the result set and displey them:
/*
        for (results.next (); result != null; result = results.next ()) // there exists one further tuple
        {

        } // for
*/
    } // showResults


    /**************************************************************************
     * Read the content of a file and parse it. <BR/>
     * This method reads the content of a file and parses it in a db2 like
     * manner. This means that the file is separated in parts from which each
     * represents one statement.
     * Comments (both <CODE>/* ... &#42;/</CODE> and <CODE>-- ...</CODE>) and
     * empty lines are left out of the result.
     *
     * @param   dbName      The name of the database to connect to.
     * @param   fileName    The name of the file to be read.
     * @param   log         The log to write output to.
     *
     * @return  The content of the file or <CODE>null</CODE> if there was an
     *          error.
     */
    private final Vector<String> parseFile (String dbName, String fileName, Log_01 log)
    {
        // other variables:
        Vector<String> statements = new Vector<String> (10, 10); // all statements
        BufferedReader reader = null;   // the file reader
        // replacement strings
        Vector<Replacement> replacements = new Vector<Replacement> (10, 10);

        replacements.add (new Replacement ("#libRef#", dbName + "."));
        replacements.add (new Replacement ("#libName#", dbName));
        replacements.add (new Replacement ("IBSDEV1", dbName));
        replacements.add (new Replacement ("IBSDEV2", dbName));
        replacements.add (new Replacement ("IBSDEV3", dbName));

        DBExec.println (log, DIConstants.LOG_ENTRY, "____________________________________________________________");
        DBExec.println (log, DIConstants.LOG_ENTRY, "file " + fileName + ":");
        DBExec.print (log, DIConstants.LOG_ENTRY, "reading data from file... ");

        try
        {
            // get the file reader:
            reader = new BufferedReader (new FileReader (fileName));

            // create the tokenizer and parse the stream:
            statements = this.parse (new StreamTokenizer (reader), replacements, log);

            // close the data stream:
            reader.close ();
        } // try
        catch (IOException e)
        {
            DBExec.println (log, e);
        } // catch

        // return the result:
        return statements;
    } // parseFile


    /**************************************************************************
     * Parse a data stream. <BR/>
     * This method parses a data stream in a db2 like manner. This means that
     * the stream is separated in parts from which each represents one statement.
     * <BR/>
     * Comments (both <CODE>/* ... &#42;/</CODE> and <CODE>-- ...</CODE>) and
     * empty lines are left out of the result.
     *
     * @param   tokenizer   The stream tokenizer.
     * @param   replacements Strings to be replaced by others in the resulting
     *                      statements.
     * @param   log         The log to write output to.
     *
     * @return  The parsed data stream or <CODE>null</CODE> if there was an
     *          error.
     */
    Vector<String> parse (StreamTokenizer tokenizer,
                          Vector<Replacement> replacements, Log_01 log)
    {
        // method has to be implemented in subclasses
        return null;
    } // parse


    /**************************************************************************
     * Finish one statement and add it to the vector of several statements. <BR/>
     * If the statement is not valid - i.e. it's empty or just consisting of
     * statement separators - it is not added.
     *
     * @param   statements  The vector to which to add the statement.
     * @param   actStmt     The actual statement to be finished.
     * @param   replacements Strings to be replaced by others in the resulting
     *                      statements.
     */
    final void finishStmt (Vector<String> statements, String actStmt,
                           Vector<Replacement> replacements)
    {
        // drop leading and trailing spaces:
        String actStmtLocal = actStmt.trim (); // variable for local assignments
        Replacement actReplacement;     // the actual replacement

        // drop trailing semicolons:
        while (actStmtLocal.endsWith (";"))
        {
            actStmtLocal = actStmtLocal.substring (0, actStmtLocal.length () - 1);
        } // while

        // check if there is a statement to be handled:
        if (actStmtLocal.length () > 0)      // at least one valid character?
        {
            // replace the special strings:
            // loop through all elements of the replacement vector and perform
            // the replacement for each one.
            for (int i = 0; i < replacements.size (); i++)
                                        // there exists one further element
            {
                // get the actual replacement:
                actReplacement = replacements.elementAt (i);
                // perform the replacement on the actual statement:
                actStmtLocal = actReplacement.replace (actStmtLocal);
            } // for

            // add the statement to the statement vector:
            if (actStmtLocal.length () > 0)      // at least one valid character?
            {
                statements.add (actStmtLocal);
            } // if at least one valid character
        } // if at least one valid character
    } // finishStmt


    /**************************************************************************
     * Read the content of a file. <BR/>
     *
     * @param   fileName    The name of the file to be read.
     *
     * @return  The content of the file or <CODE>null</CODE> if there was an
     *          error.
     */
/*
    private static final String readFile (String fileName)
    {
        // read the file and return the result:
        return FileHelpers.getContent (fileName);
    } // readFile
*/


    /**************************************************************************
     * Write a message to the log. <BR/>
     * The method ensures that the message is displayed if the log type is
     * ERROR. Otherwise it is not displayed.
     *
     * @param   log     The log to write the message to.
     * @param   logType The type of the log (DIMessages.LOG_xxx).
     * @param   msg     The message to be displayed.
     */
    protected static final void printLog (Log_01 log, int logType, String msg)
    {
        boolean isDisplayLog = false;   // original value of log

        // check if we have a log:
        if (log != null)
        {
            // ensure that the message is displayed according to the log type:
            isDisplayLog = log.isDisplayLog;
            log.isDisplayLog = logType == DIConstants.LOG_ERROR;
            // add the message to the log:
            log.add (logType, msg);
            // reset the isDisplayLog value of the log to the original value:
            log.isDisplayLog = isDisplayLog;
        } // if
    } // printLog


    /**************************************************************************
     * Display a message. <BR/>
     *
     * @param   log     The log to write the message to.
     * @param   logType The type of the log (DIMessages.LOG_xxx).
     * @param   msg     The message to be displayed.
     */
    protected static final void print (Log_01 log, int logType, String msg)
    {
        System.out.print (msg);
        DBExec.printLog (log, logType, msg);
    } // print


    /**************************************************************************
     * Display a line message. <BR/>
     *
     * @param   log     The log to write the message to.
     * @param   logType The type of the log (DIMessages.LOG_xxx).
     * @param   msg     The message to be displayed.
     */
    protected static final void println (Log_01 log, int logType, String msg)
    {
        System.out.println (msg);
        DBExec.printLog (log, logType, msg);
    } // println


    /**************************************************************************
     * Display an error message. <BR/>
     *
     * @param   log     The log to write the message to.
     * @param   error   Error for which to display the message
     */
    protected static final void println (Log_01 log, Exception error)
    {
        System.out.println (DBExec.OUTPUT_PREFIX + error);
//        error.printStackTrace ();
        DBExec.printLog (log, DIConstants.LOG_ERROR, DBExec.OUTPUT_PREFIX + error);
    } // println


    /**************************************************************************
     * Display an error message. <BR/>
     *
     * @param   log     The log to write the message to.
     * @param   error   Error for which to display the message
     */
    protected static final void println (Log_01 log, SQLException error)
    {
        String msg = DBExec.OUTPUT_PREFIX + "SQL state " +
            error.getSQLState () + " - " + error;

        System.out.println (msg);
        DBExec.printLog (log, DIConstants.LOG_ERROR, msg);

        SQLException e = error.getNextException ();
        if (e != null)
        {
            DBExec.println (log, e);
        } // if
//        error.printStackTrace ();
    } // println


    /**************************************************************************
     * Print the warnings for a sql statement. <BR/>
     * If the warnings are chained together all warnings are printed.
     *
     * @param   log     The log to write the message to.
     * @param   warning The initial warning (the first in the chain).
     */
    protected void printSqlWarnings (Log_01 log, SQLWarning warning)
    {
        SQLWarning warningLocal = warning; // variable for local assignments

        while (warningLocal != null)
        {
            DBExec.println (log, DIConstants.LOG_ENTRY, "Warning: " + warningLocal.getMessage ());
            if (warningLocal instanceof DataTruncation)
            {
                DataTruncation d = (DataTruncation) warningLocal;
                DBExec.println (log, DIConstants.LOG_ERROR, "  Truncation error in column: " + d.getIndex ());
            } // if
            warningLocal = warningLocal.getNextWarning ();
        } // while
    } // printSqlWarnings


    /******************************************************************************
     * This class is used for doing replacements within strings. <BR/>
     *
     * @version     $Id: DBExec.java,v 1.7 2007/07/31 19:13:56 kreimueller Exp $
     *
     * @author      Klaus, ??.01.2003
     ******************************************************************************
     */
    class Replacement extends Object
    {
        /**
         * String for replacement. <BR/>
         * This is the string which has to be replaced by a new string.
         */
        public String p_origStr;

        /**
         * Replacement string. <BR/>
         * This is the string which replaces the original string.
         */
        public String p_newStr;


        /**************************************************************************
         * Create a new instance of this class. <BR/>
         * This method creates a new instance and initializes it. <BR/>
         *
         * @param   origStr The original string to be replaced.
         * @param   newStr  The new string which replaces the original one.
         */
        public Replacement (String origStr, String newStr)
        {
            // set the corresponding properties:
            this.p_origStr = origStr;
            this.p_newStr = newStr;
        } // ReplacementString


        /**************************************************************************
         * Take one string and replace all occurrences of the original string
         * with the new string. <BR/>
         * The original string is not changed.
         *
         * @param   majorStr    The string in which to perform the replacement.
         *
         * @return  String in which all occurrences of origStr are replaced by
         *          newStr.
         */
        public String replace (String majorStr)
        {
            int oldPos = 0;             // actual position in old major string
            int pos = 0;                // actual found position
            String str = "";            // new String

            while ((pos = majorStr.indexOf (this.p_origStr, oldPos)) >= 0)
            {
                str += majorStr.substring (oldPos, pos) + this.p_newStr;
                oldPos = pos + this.p_origStr.length ();
            } // while

            // concatenate the rest of the major string not used yet:
            str += majorStr.substring (oldPos);

            return str;                 // return the new string
        } // replace
    } // class Replacement

} // class DBExec
