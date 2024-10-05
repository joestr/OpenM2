/*
 * Class: DBLog.java
 */

// package:
package ibs.tech.sql;

//imports:
import ibs.util.DateTimeHelpers;
import ibs.util.StringHelpers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/******************************************************************************
 * This class is responsible for storing the database log messages. <BR/>
 *
 * @version     $Id: DBLog.java,v 1.5 2007/07/24 21:27:02 kreimueller Exp $
 *
 * @author      Klaus, 06.08.2004
 ******************************************************************************
 */
public class DBLog extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DBLog.java,v 1.5 2007/07/24 21:27:02 kreimueller Exp $";

    /**
     * The file. <BR/>
     */
    private File p_file = null;

    /**
     * The file writer. <BR/>
     */
    private FileWriter p_writer = null;


    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a DBLog object. <BR/>
     * The log is automatically opened. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   fileName    The path and name of the log file.
     */
    public DBLog (String fileName)
    {
        super ();

        // initialize the instance:
        this.initialize (fileName);
    } // DBLog



    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Open the log. <BR/>
     */
    public void open ()
    {
        // open the writer:
        this.openWriter ();
    } // close


    /**************************************************************************
     * Close the log. <BR/>
     */
    public void close ()
    {
        // close the writer:
        this.closeWriter ();
    } // close


    /**************************************************************************
     * Initializes the log. <BR/>
     *
     * @param   fileName    The path and name of the log file.
     */
    private void initialize (String fileName)
    {
        FileWriter writer = null;       // the current writer

        // create the file object:
        this.p_file = new File (fileName);

        // open the file and write the header:
        writer = this.openWriter ();

        try
        {
            writer.write ("time;errorcode;sqlstate;message;call;stacktrace\r\n");
        } // try
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace ();
        } // catch

        // close the writer:
        this.closeWriter ();
    } // initialize


    /**************************************************************************
     * Open the log file for writing. <BR/>
     *
     * @return  The file writer.
     *          <CODE>null</CODE> if there occurred an error.
     */
    private FileWriter openWriter ()
    {
        if (this.p_writer == null)      // writer currently not open?
        {
            // first check if the directory already exists:
            if (!this.p_file.getParentFile ().exists ())
            {
                // create the directory and all necessary directories above:
                this.p_file.getParentFile ().mkdirs ();
            } // if

            try
            {
                // open the file and write the header:
                this.p_writer = new FileWriter (this.p_file, true);
            } // try
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace ();
            } // catch
        } // if writer currently not open

        return this.p_writer;
    } // openWriter


    /**************************************************************************
     * Close the log file for writing. <BR/>
     */
    private void closeWriter ()
    {
        if (this.p_writer != null)      // writer currently open?
        {
            try
            {
                // close the writer:
                this.p_writer.close ();
                // drop the instance:
                this.p_writer = null;
            } // try
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace ();
            } // catch
        } // if writer currently open
    } // closeWriter


    /**************************************************************************
     * Add an entry to the log. <BR/>
     * Remark: None of the parameters is allowed to have semicolons
     * (<CODE>';'</CODE>) in it! If they are there they are converted to commas
     * (<CODE>','</CODE>). <BR/>
     * Linebreaks (<CODE>'\n'</CODE>) are converted to spaces
     * (<CODE>" "</CODE>);
     *
     * @param   errorcode   The internal error code.
     * @param   sqlstate    The sql state returned from the database driver.
     * @param   message     The message for the log entry.
     * @param   call        The sql call.
     * @param   stacktrace  The stack trace.
     */
    public void addEntry (int errorcode, String sqlstate, String message,
                          String call, String stacktrace)
    {
        // write the entry to the file:
        this.writeEntry (errorcode, sqlstate, message, call, stacktrace);
    } // addEntry


    /**************************************************************************
     * Write the entry to the file. <BR/>
     *
     * @param   errorcode   The internal error code.
     * @param   sqlstate    The sql state returned from the database driver.
     * @param   message     The message for the log entry.
     * @param   call        The sql call.
     * @param   stacktrace  The stack trace.
     */
    private void writeEntry (int errorcode, String sqlstate, String message,
                             String call, String stacktrace)
    {
        FileWriter writer = null;       // the writer

        // open the file and write the entry:
        writer = this.openWriter ();

        try
        {
            writer.write (
                DateTimeHelpers.getTimestamp () + ";" +
                errorcode + ";" +
                this.replaceSpecialCharacters (sqlstate) + ";" +
                this.replaceSpecialCharacters (message) + ";" +
                this.replaceSpecialCharacters (call) + ";" +
                this.replaceSpecialCharacters (stacktrace) +
                "\r\n");
        } // try
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace ();
        } // catch

        // close the writer:
        this.closeWriter ();
    } // writeEntry


    /**************************************************************************
     * Write the entry to the file. <BR/>
     *
     * @param   origStr The string in which to replace the special characters.
     *
     * @return  The string with the replaced characters.
     */
    private String replaceSpecialCharacters (String origStr)
    {
        return
            StringHelpers.replaceChars (origStr,
                new char[] {';', '\r', '\n'},
                new char[] {',', ' ', ' '});
    } // replaceSpecialCharacters


    /**************************************************************************
     * Called by the garbage collector on an object when garbage collection
     * determines that there are no more references to the object. <BR/>
     * See {@link java.lang.Object#finalize ()} for more details.
     *
     * @throws Throwable the <code>Exception</code> raised by this method
     */
    protected void finalize () throws Throwable
    {
        // ensure that the file writer is closed:
        this.closeWriter ();

        // TODO Auto-generated method stub
        super.finalize ();
    } // finalize

} // class DBLog
