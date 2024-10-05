/*
 * Class: SQLSExecDir.java
 */

// package:
package ibs.install.sql;

// imports:


/******************************************************************************
 * This class executes sql commands in a SQL Server database. <BR/>
 *
 * @version     $Id: SQLSExecDir.java,v 1.4 2007/07/31 19:13:56 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR), 021120
 ******************************************************************************
 */
public class SQLSExecDir extends DBExecDir
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: SQLSExecDir.java,v 1.4 2007/07/31 19:13:56 kreimueller Exp $";


    /**************************************************************************
     * The main method. <BR/>
     * Method for starting and executing the sql commands.
     *
     * @param   args    The command line arguments.
     */
    public static void main (String[] args)
//        throws java.io.IOException
    {
        DBExecDir.execApp (new SQLSExecDir (), args);
    } // main


    /**************************************************************************
     * Display the syntax of the program. <BR/>
     *
     * @param   args    The command line arguments.
     */
    static void syntax (String[] args)
    {
        DBExecDir.println (
            "Syntax: SQLSExecDir system library username password dirname [displaymode]\n" +
            "    SQLSExecDir .... this program\n" +
            "    system ....... the name of the computing system which contains the database\n" +
            "    library ...... the library which shall be accessed\n" +
            "    username ..... the user name for getting into the system\n" +
            "    password ..... the password for the user\n" +
            "    dirname ...... the directory which contains the sql files\n" +
            "    displaymode .. mode for displaying the statements\n" +
            "                   " + DISP_NO + " ... no display\n" +
            "                   " + DISP_ONE + " ... display first line of each statement(default)\n" +
            "                   " + DISP_FULL + " ... display the whole statement\n" +
            "\n" +
            "example:\n" +
            "SQLSExecDir dbserver m2db user password \"C:\\testdir\\\" " + DISP_FULL
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
        return new SQLSExec ();
    } // createInstance

} // class SQLSExecDir
