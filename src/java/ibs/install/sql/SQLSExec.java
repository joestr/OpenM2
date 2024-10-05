/*
 * Class: SQLSExec.java
 */

// package:
package ibs.install.sql;

// imports:
import ibs.di.DIConstants;
import ibs.di.Log_01;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.sql.SQLException;
import java.util.Vector;


/******************************************************************************
 * This class executes sql commands in a SQL Server database. <BR/>
 *
 * @version     $Id: SQLSExec.java,v 1.6 2007/07/31 19:13:56 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR), 021115
 ******************************************************************************
 */
public class SQLSExec extends DBExec
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: SQLSExec.java,v 1.6 2007/07/31 19:13:56 kreimueller Exp $";


    /**************************************************************************
     * The main method. <BR/>
     * Method for starting and executing the sql commands.
     *
     * @param   args    The command line arguments.
     */
    public static void main (String[] args)
//        throws java.io.IOException
    {
        DBExec.execApp (new SQLSExec (), args);
    } // main


    /**************************************************************************
     * Display the syntax of the program. <BR/>
     *
     * @param   args    The command line arguments.
     */
    static void syntax (String[] args)
    {
        DBExec.println (null, DIConstants.LOG_NOTYPE,
            "Syntax: SQLSExec system database username password filename [displaymode]\n" +
            "    SQLSExec ..... this program\n" +
            "    system ....... the name of the system which contains the database\n" +
            "    database ..... the database which shall be accessed\n" +
            "    username ..... the user name for getting into the system\n" +
            "    password ..... the password for the user\n" +
            "    filename ..... the file which contains the sql statements\n" +
            "    displaymode .. mode for displaying the statements\n" +
            "                   " + DISP_NO + " ... no display\n" +
            "                   " + DISP_ONE + " ... display first line of each statement(default)\n" +
            "                   " + DISP_FULL + " ... display the whole statement\n" +
            "\n" +
            "example:\n" +
            "SQLSExec myMachine mydb user password test1.sql " + DISP_FULL
        );
    } // syntax


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
        String driverName = "com.inet.tds.TdsDriver"; // name of db driver
        String connectionString = "jdbc:inetdae7a:" + systemName + ":1433";
                                        // db connection string

        // call common method for establishing the connection:
        this.connect (driverName, connectionString, systemName, dbName,
            userName, password, log);
    } // connect


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
        String token;                   // the actual token
        // other variables:
        int state = InstallSqlConstants.ST_START;
        Vector<String> statements = new Vector<String> (10, 10); // all statements
        StringBuffer actStmt;           // the actual statement
/*
        int beginCounter = 0;           // number of begins - ends
*/
        boolean finishStmt = false;     // finish the actual statement?
        boolean lineCommentStart = false; // start of line comment found?
        boolean inLineComment = false;  // currently in line comment?
        boolean isEmptyLine = false;    // is the current line empty?
/*
        boolean beginPossible = true;   // is the begin statement possible?
*/
        // special constants:
//        final char EOSTMTSTART = 'G';   // start character of end of statement

        DBExec.print (log, DIConstants.LOG_ENTRY, "parsing data... ");

        try
        {
            // initialize the tokenizer:
            // set ordinary characters which shall not be treated in a special
            // way:
/*
            tokenizer.ordinaryChar (' '); // space character    0x20
            tokenizer.ordinaryChar ('-'); // minus character    0x2D
            tokenizer.ordinaryChar ('.'); // dot character      0x2E
            tokenizer.ordinaryChar ('/'); // slash character    0x2F
            tokenizer.ordinaryChar (':'); // colon character    0x3A
            tokenizer.ordinaryChar ('_'); // underscore character 0x5F
            tokenizer.ordinaryChars (' ', '~'); // all characters from
                                        // 0x20 until 0x7E
*/
            tokenizer.ordinaryChar ('\t'); // special character  TAB
            tokenizer.ordinaryChar ('¦'); // special character  0x7F
            tokenizer.ordinaryChars (' ', '&'); //  !"#$%&      0x20 .. 0x26
            tokenizer.ordinaryChars ('(', '/'); // ()*+,-./     0x28 .. 0x2F
            tokenizer.ordinaryChars ('0', '9'); // digits       0x30 .. 0x39
            tokenizer.ordinaryChars ('<', '?'); // <=>?         0x3C .. 0x3F
            tokenizer.ordinaryChars ('[', '`'); // [\]^_`       0x5B .. 0x60
            tokenizer.ordinaryChars ('{', '~'); // {|}~         0x7B .. 0x7E

            // characters which are now recognized as special characters:
            // ' 0x27  39
            // : 0x3A  58
            // ; 0x3B  59
            // @ 0x40  64
            // A .. Z
            // a .. z

            // set special character handling:
            tokenizer.eolIsSignificant (true);
            tokenizer.quoteChar ('\'');
            tokenizer.slashStarComments (true);

            // initialize the first statement:
            actStmt = new StringBuffer ();
            isEmptyLine = true;

            // loop through all found tokens:
            while (tokenizer.nextToken () != StreamTokenizer.TT_EOF)
            {
                if (!inLineComment || tokenizer.ttype == StreamTokenizer.TT_EOL)
                                        // currently not in line comment?
                {
                    // get the actual token:
                    switch (tokenizer.ttype)
                    {
                        case StreamTokenizer.TT_WORD:
                            token = tokenizer.sval;
//System.out.println (actStmt + "|word." + token);
                            isEmptyLine = false;
                            break;

                        case ' ':
                        case '\t':
//System.out.println (actStmt + "|space or tab." + tokenizer.ttype);
                            if (!isEmptyLine) // not first character in line?
                            {
                                token = "" + (char) tokenizer.ttype;
                            } // if not first character in line
                            else        // first character
                            {
                                // don't write the character:
                                token = "";
                            } // else first character
//                            beginPossible = true;
                            break;

                        case '\'':
                            token = "'" + tokenizer.sval + "'";
//System.out.println (actStmt + "|quote." + token);
                            isEmptyLine = false;
                            break;

                        case InstallSqlConstants.LCOMMSTART: // possibly line comment?
//System.out.println (actStmt + "|LCOMMSTART." + LCOMMSTART);
                            // check if we have already started a line comment:
                            if (!lineCommentStart) // currently not started?
                            {
                                // start new line comment:
                                lineCommentStart = true;
                            } // if currently not started
                            else        // line comment already started
                            {
                                // not in start of line comment:
                                lineCommentStart = false;
                                // remember that we are in a line comment:
                                inLineComment = true;
                            } // else line comment already started
                            // don't write the token:
                            token = "";
                            break;

                        case StreamTokenizer.TT_EOL: // end of line?
//System.out.println (actStmt + "|EOL.");
                            // ensure that we are not within a line comment:
                            if (lineCommentStart) // line comment started?
                            {
                                // write the comment start character:
                                actStmt.append (InstallSqlConstants.LCOMMSTART);
                                // set the state variable:
                                lineCommentStart = false;
                            } // if line comment started

                            inLineComment = false;
                            if (!isEmptyLine) // not empty line?
                            {
                                // go to next line:
                                token = "\n";
                                // the next line is now empty:
                                isEmptyLine = true;
//                                beginPossible = true;
                            } // if not empty line
                            else        // empty line
                            {
                                token = "";
                            } // else empty line
                            break;

                        default:
//System.out.println (actStmt + "|unknown char." + tokenizer.ttype);
                            token = "" + (char) tokenizer.ttype;
                            isEmptyLine = false;
                    } // switch

                    // check if we have possibly started a line comment, but not
                    // finished it:
                    if (lineCommentStart && tokenizer.ttype != InstallSqlConstants.LCOMMSTART)
                    {
                        // write the last char:
                        actStmt.append (InstallSqlConstants.LCOMMSTART);
                        // ensure that we are not in a line comment:
                        lineCommentStart = false;
                    } // if

                    switch (state)
                    {
                        case InstallSqlConstants.ST_START:
                            if (token.equalsIgnoreCase ("GO")) // end of statement?
                            {
                                // finish the actual statement:
                                finishStmt = true;
                                // don't write the token:
                                token = "";
                            } // if end of statement
                            else        // not recognized token
                            {
                                // nothing to do
                            } // else not recognized token
                            break;

                        default: // nothing to do
                    } // switch

                    // check if the actual statement shall be finished:
                    if (finishStmt)         // finish the actual statement?
                    {
                        // finish the actual statement and add it to all statements:
                        this.finishStmt (statements, actStmt.toString (), replacements);

                        // initialize the next statement:
                        actStmt = new StringBuffer ();
                        // don't finish the new statement:
                        finishStmt = false;
                    } // if finish the actual statement

                    // append the token to the actual statement:
                    actStmt.append (token);

                    // check if the next token is allowed to be BEGIN:
/*
                    beginPossible =
                        tokenizer.ttype == ' ' ||
                        tokenizer.ttype == '\t' ||
                        tokenizer.ttype == StreamTokenizer.TT_EOL ||
                        token.equalsIgnoreCase ("GO");
*/
                } // if currently not in line comment
            } // while

            // finish the actual statement and add it to all statements:
            this.finishStmt (statements, actStmt.toString (), replacements);

            DBExec.println (log, DIConstants.LOG_ENTRY, "" +
                statements.size () + " statements found.");
        } // try
        catch (IOException e)
        {
            // no result found:
            statements = null;

            DBExec.println (log, e);
        } // catch

/*
        for (int i = 0; i < statements.size (); i++)
        {
            println (statements.elementAt (i).toString ());
            println ("*****************************************");
        } // for
*/

        // return the result:
        return statements;
    } // parse

} // class SQLSExec
