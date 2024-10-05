/*
 * Class: Db2Exec.java
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
 * This class executes sql commands in a db2 database. <BR/>
 *
 * @version     $Id: Db2Exec.java,v 1.1 2007/07/31 19:14:37 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR), 021115
 ******************************************************************************
 */
public class Db2Exec extends DBExec
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Db2Exec.java,v 1.1 2007/07/31 19:14:37 kreimueller Exp $";

    // special constants:
    /**
     * Character for end of statement. <BR/>
     */
    private static final char EOSTMT = ';';

    /**
     * Token: Begin of statement. <BR/>
     */
    private static final String TOK_STMT_BEGIN = "BEGIN";


    /**************************************************************************
     * The main method. <BR/>
     * Method for starting and executing the sql commands.
     *
     * @param   args    The command line arguments.
     */
    public static void main (String[] args)
//        throws java.io.IOException
    {
        DBExec.execApp (new Db2Exec (), args);
    } // main


    /**************************************************************************
     * Display the syntax of the program. <BR/>
     *
     * @param   args    The command line arguments.
     */
    static void syntax (String[] args)
    {
        DBExec.println (null, DIConstants.LOG_NOTYPE,
            "Syntax: db2exec system library username password filename [displaymode]\n" +
            "    db2exec ...... this program\n" +
            "    system ....... the name of the AS400 system which contains the database\n" +
            "    library ...... the library which shall be accessed\n" +
            "    username ..... the user name for getting into the system\n" +
            "    password ..... the password for the user\n" +
            "    filename ..... the file which contains the sql statements\n" +
            "    displaymode .. mode for displaying the statements\n" +
            "                   " + DISP_NO + " ... no display\n" +
            "                   " + DISP_ONE + " ... display first line of each statement(default)\n" +
            "                   " + DISP_FULL + " ... display the whole statement\n" +
            "\n" +
            "example:\n" +
            "db2exec as400machine mylib user password test1.sql " + DISP_FULL
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
        String driverName = "com.ibm.as400.access.AS400JDBCDriver";
                                        // name of db driver
        String connectionString = "jdbc:as400://" + systemName + "/" + dbName +
            ";naming=sql;errors=full;" +
            "dateformat=iso;extended dynamic=false;package=JDBCEX;" +
            "package library=" + dbName;
                                        // db connection string

        // call common method for establishing the connection:
        this.connect (driverName, connectionString, systemName, dbName,
            userName, password, log);
    } // connect


    /**************************************************************************
     * Parse a sql string for statements. <BR/>
     * Separate the statements which are within one sql string.
     *
     * @param   queryStr    The sql string to be parsed.
     *
     * @return  A vector containing the several statements.
     */
/*
    private final Vector parseSqlString (String queryStr)
    {
        StringTokenizer tokenizer;      // the string tokenizer
        String token;                   // the actual token
        final int ST_START = 0;
        final int ST_QUOTE = 1;
        final int ST_SEMICOLON = 2;
        final int ST_SPACE = 3;
        final int ST_ALIAS = 4;
        int state = ST_START;
        Vector statements = new Vector (10, 10); // all statements
        StringBuffer actStmt;           // the actual statement


        tokenizer = new StringTokenizer
            (queryStr, "BEGINEND;/*-'", true);

        actStmt = new StringBuffer ();

        while (tokenizer.hasMoreTokens ())
        {
            // get the actual token:
            token = tokenizer.nextToken ();

            // append the token to the actual statement:
            actStmt.append (token);

            switch (state)
            {
                case ST_START:
                    if (token.equals (";")) // semicolon?
                    {
                        // finish the actual statement and add it to all statements:
                        statements.add (actStmt);
                        // initialize the next statement:
                        actStmt = new StringBuffer ();
                    } // if equality condition
                    else        // not recognized token
                    {
                        // nothing to do
                    } // else not recognized token
                    break;

/ *
                case ST_EQUAL:
                    if (token.equals (" ")) // empty space?
                    {
                        // nothing to do
                    } // else if empty space
                    else if (token.equals (tableAlias)) // table alias?
                    {
                        state = ST_ALIAS;
                    } // else if table alias
                    else        // not recognized token
                    {
                        // table alias not found => restart
                        state = ST_START;
                    } // else not recognized token
                    break;

                case ST_ALIAS:
                    if (token.equals (".")) // separator alias and attr.?
                    {
                        state = ST_DOT;
                    } // else if separator alias and attr.
                    else        // not recognized token
                    {
                        // dot not found => restart
                        state = ST_START;
                    } // else not recognized token
                    break;

                case ST_DOT:
                    // append the OUTER JOIN operator:
                    whereClause.append ("(+)");
                    // restart:
                    state = ST_START;
                    break;
* /
            } // switch
        } // while

        // return the result:
        return statements;
    } // parseSqlString
*/


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
     *
     * @return  The content of the file or <CODE>null</CODE> if there was an
     *          error.
     */
/*
    private Vector parseFile (String dbName, String fileName)
    {
        // other variables:
        Vector statements = new Vector (10, 10); // all statements
        BufferedReader reader = null;   // the file reader
        Vector replacements = new Vector (10, 10); // replacement strings

        replacements.add (new Replacement ("#libRef#", dbName + "."));
        replacements.add (new Replacement ("#libName#", dbName));
        replacements.add (new Replacement ("IBSDEV1", dbName));
        replacements.add (new Replacement ("IBSDEV2", dbName));
        replacements.add (new Replacement ("IBSDEV3", dbName));

        println ("____________________________________________________________");
        println ("file " + fileName + ":");
        print ("reading data from file... ");

        try
        {
            // get the file reader:
            reader = new BufferedReader (new FileReader (fileName));

            // create the tokenizer and parse the stream:
            statements = parse (new StreamTokenizer (reader), replacements);

            // close the data stream:
            reader.close ();
        } // try
        catch (IOException e)
        {
            println (e);
        } // catch

        // return the result:
        return statements;
    } // parseFile
*/


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
        int beginCounter = 0;           // number of begins - ends
        boolean finishStmt = false;     // finish the actual statement?
        boolean lineCommentStart = false; // start of line comment found?
        boolean inLineComment = false;  // currently in line comment?
        boolean isEmptyLine = false;    // is the current line empty?
        boolean beginPossible = true;   // is the begin statement possible?

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
                            isEmptyLine = false;
                            break;

                        case ' ':
                        case '\t':
                            if (!isEmptyLine) // not first character in line?
                            {
                                token = "" + (char) tokenizer.ttype;
                            } // if not first character in line
                            else        // first character
                            {
                                // don't write the character:
                                token = "";
                            } // else first character
                            beginPossible = true;
                            break;

                        case '\'':
                            token = "'" + tokenizer.sval + "'";
                            isEmptyLine = false;
                            break;

                        case InstallSqlConstants.LCOMMSTART: // possibly line comment?
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
                            // ensure that we are not within a line comment:
                            lineCommentStart = false;
                            inLineComment = false;
                            if (!isEmptyLine) // not empty line?
                            {
                                // go to next line:
                                token = "\n";
                                // the next line is now empty:
                                isEmptyLine = true;
                                beginPossible = true;
                            } // if not empty line
                            else        // empty line
                            {
                                token = "";
                            } // else empty line
                            break;

                        default:
                            token = "" + (char) tokenizer.ttype;
                            isEmptyLine = false;
                    } // switch

                    // check if we have possibly started a line comment, but not
                    // finished it:
                    if (lineCommentStart && tokenizer.ttype != InstallSqlConstants.LCOMMSTART)
                    {
                        // write the last char:
                        actStmt.append ("-");
                        // ensure that we are not in a line comment:
                        lineCommentStart = false;
                    } // if

                    switch (state)
                    {
                        case InstallSqlConstants.ST_START:
                            if (tokenizer.ttype == Db2Exec.EOSTMT) // end of statement?
                            {
                                // finish the actual statement:
                                finishStmt = true;
                                // don't write the token:
                                token = "";
                            } // if end of statement
                            else if (token.equalsIgnoreCase (Db2Exec.TOK_STMT_BEGIN) &&
                                     beginPossible) // BEGIN?
                            {
                                state = InstallSqlConstants.ST_BEGIN;
                                beginCounter++;
                            } // else if BEGIN
                            else        // not recognized token
                            {
                                // nothing to do
                            } // else not recognized token
                            break;

                        case InstallSqlConstants.ST_BEGIN:
                            if (token.equalsIgnoreCase (Db2Exec.TOK_STMT_BEGIN) &&
                                beginPossible) // BEGIN?
                            {
                                beginCounter++;
                            } // if BEGIN
                            else if (token.equalsIgnoreCase ("END")) // END?
                            {
                                state = InstallSqlConstants.ST_END;
                            } // else if END
                            else        // not recognized token
                            {
                                // nothing to do
                            } // else not recognized token
                            break;

                        case InstallSqlConstants.ST_END:
                            if (tokenizer.ttype == Db2Exec.EOSTMT) // end of statement?
                            {
                                // check if we have left the begin/end blocks:
                                if (--beginCounter == 0) // out of last block?
                                {
                                    // don't write the token:
                                    token = "";

                                    // finish the actual statement:
                                    finishStmt = true;
                                    // start again at top level:
                                    state = InstallSqlConstants.ST_START;
                                } // if out of last block
                                else    // not out of last block
                                {
                                    // wait for next end of block:
                                    state = InstallSqlConstants.ST_BEGIN;
                                } // else not out of last block
                            } // if end of statement
                            else if (tokenizer.ttype == ' ') // space character?
                            {
                                // nothing to do
                            } // else if space character
                            else            // not recognized token
                            {
                                // go back to begin state:
                                state = InstallSqlConstants.ST_BEGIN;
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
                    beginPossible =
                        tokenizer.ttype == ' ' ||
                        tokenizer.ttype == '\t' ||
                        tokenizer.ttype == StreamTokenizer.TT_EOL ||
                        tokenizer.ttype == Db2Exec.EOSTMT;
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

} // class Db2Exec
