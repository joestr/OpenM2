/*
 * Class: DBExecDir.java
 */

// package:
package ibs.install.sql;

// imports:
import java.io.File;
import java.io.FilenameFilter;
import java.sql.SQLException;


/******************************************************************************
 * This class executes sql commands in a db2 database. <BR/>
 *
 * @version     $Id: DBExecDir.java,v 1.6 2007/07/31 19:13:56 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR), 031002
 ******************************************************************************
 */
public class DBExecDir extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DBExecDir.java,v 1.6 2007/07/31 19:13:56 kreimueller Exp $";

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


    /**************************************************************************
     * The main method. <BR/>
     * Method for starting and executing the sql commands.
     *
     * @param   args    The command line arguments.
     */
    public static void main (String[] args)
//        throws java.io.IOException
    {
        DBExecDir.execApp (new DBExecDir (), args);
    } // main


    /**************************************************************************
     * The main method. <BR/>
     * Method for starting and executing the sql commands.
     *
     * @param   executor    The instance which is used for executing the
     *                      commands.
     * @param   args        The command line arguments.
     */
    public static final void execApp (DBExecDir executor, String[] args)
//        throws java.io.IOException
    {
        String dispMode = DBExecDir.DISP_ONE; // display mode
        boolean failure = false;        // was there a failure?

        // check the syntax:
        if (args.length >= 5 && args.length <= 6) // correct number of arguments?
        {
            if (args.length >= 6)       // display mode set?
            {
                dispMode = args[5];

                // check if display mode is valid:
                if (!dispMode.equals (DBExecDir.DISP_NO) && !dispMode.equals (DBExecDir.DISP_ONE) &&
                    !dispMode.equals (DBExecDir.DISP_FULL)) // not valid display mode?
                {
                    failure = true;
                } // if not valid display mode
            } // if display mode set

            // check if there was a failure:
            if (!failure)               // no failure?
            {
                // execute the files of the directory:
                executor
                    .exec (args[0], args[1], args[2], args[3], args[4], dispMode);
            } // if no failure
        } // if correct number of arguments
        else                            // wrong syntax in call
        {
            failure = true;
        } // else wrong syntax in call

        if (failure)                    // there was a failure?
        {
            // display the correct syntax:
            DBExecDir.syntax (args);
        } // if there was a failure
    } // main


    /**************************************************************************
     * Display the syntax of the program. <BR/>
     *
     * @param   args    The command line arguments.
     */
    static void syntax (String[] args)
    {
        DBExecDir.println (
            "Syntax: DBExecDir system library username password dirname [displaymode]\n" +
            "    DBExecDir .... this program\n" +
            "    system ....... the name of the computing system which contains the database\n" +
            "    library ...... the library which shall be accessed\n" +
            "    username ..... the user name for getting into the system\n" +
            "    password ..... the password for the user\n" +
            "    dirname ...... the directory which contains the sql files\n" +
            "    displaymode .. mode for displaying the statements\n" +
            "                   " + DBExecDir.DISP_NO + " ... no display\n" +
            "                   " + DBExecDir.DISP_ONE + " ... display first line of each statement(default)\n" +
            "                   " + DBExecDir.DISP_FULL + " ... display the whole statement\n" +
            "\n" +
            "example:\n" +
            "DBExecDir dbserver m2db user password \"C:\\testdir\\\" " + DBExecDir.DISP_FULL
        );
    } // syntax


    /**************************************************************************
     * Create a new instance for execution. <BR/>
     *
     * @return  The newly created instance
     */
    DBExec createFileExecutive ()
    {
        // create the new instance and return it:
        return new DBExec ();
    } // createInstance


    /**************************************************************************
     * Method for starting and executing the sql commands.
     *
     * @param   systemName  The name of the system on which the database resides.
     * @param   dbName      The name of the database to connect to.
     * @param   userName    User name for connecting to the database.
     * @param   password    Password for the user.
     * @param   dirName     The name of the directory which contains the sql
     *                      files.
     * @param   dispMode    The display mode.
     */
    protected final void exec (String systemName, String dbName, String userName,
                                String password, String dirName, String dispMode)
    {
        File dir = new File (dirName);  // the directory
        File[] files;                   // the files
        SQLFilenameFilter filter = new DBExecDir.SQLFilenameFilter ();
        DBExec fileExecutive = this.createFileExecutive ();
                                        // instance to execute the files

        DBExecDir.println ("");
        DBExecDir.println ("============================================================");
        DBExecDir.println ("executing sql files in directory \"" + dirName + "\"");

        if (dir.exists ())
        {
            // get the directory entries:
            files = dir.listFiles (filter);

            try
            {
                // establish the database connection:
                fileExecutive.connect (systemName, dbName, userName, password,
                    null);

                // loop through all files and execute each of them:
                for (int i = 0; i < files.length; i++) // there exists another file
                {
                    // execute the actual file:
                    fileExecutive.exec (dbName, files[i].getAbsolutePath (), dispMode);
                } // for
            } // try
            catch (SQLException e)
            {
                DBExecDir.println ("error during connecting ");
                DBExecDir.println (e);
//                e.printStackTrace ();
            } // catch
            finally
            {
                // disconnect from the database:
                fileExecutive.disconnect (null);
            } // finally
        } // if
        else                            // directory does not exist
        {
            DBExecDir.println ("WARNING: directory \"" + dirName + "\" does not exist.");
        } // else directory does not exist
    } // exec


    /**************************************************************************
     * Display a message. <BR/>
     *
     * @param   msg     The message to be displayed.
     */
    protected static final void print (String msg)
    {
        System.out.print (msg);
    } // print


    /**************************************************************************
     * Display a line message. <BR/>
     *
     * @param   msg     The message to be displayed.
     */
    protected static final void println (String msg)
    {
        System.out.println (msg);
    } // println


    /**************************************************************************
     * Display an error message. <BR/>
     *
     * @param   error   Error for which to display the message
     */
    protected static final void println (Exception error)
    {
        System.out.println (DBExecDir.OUTPUT_PREFIX + error);
//        error.printStackTrace ();
    } // println



    /******************************************************************************
     * Filter for reading SQL files. <BR/>
     *
     * @version     $Id: DBExecDir.java,v 1.6 2007/07/31 19:13:56 kreimueller Exp $
     *
     * @author      Klaus, ??.01.2003
     ******************************************************************************
     */
    class SQLFilenameFilter implements FilenameFilter
    {
        /**************************************************************************
         * Tests if a specified file should be included in a file list. <BR/>
         *
         * @param   dir     The directory in which the file was found.
         * @param   name    The name of the file.
         *
         * @return  <CODE>true</CODE> if and only if the name should be
         *          included in the file list; <CODE>false</CODE> otherwise.
         */
        public boolean accept (File dir, String name)
        {
            return name.endsWith (".sql");
        } // accept
    } // class SQLFilenameFilter

} // class DBExecDir
