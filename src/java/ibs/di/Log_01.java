/*
 * Class: Log_01.java
 */

// package:
package ibs.di;

// imports:
//KR TODO: unsauber
import ibs.bo.BusinessObject;
//KR TODO: unsauber
import ibs.bo.Datatypes;
//KR TODO: unsauber
import ibs.bo.OID;
import ibs.di.DIArguments;
import ibs.di.DIConstants;
import ibs.di.DIHelpers;
import ibs.di.DITokens;
import ibs.di.LogElement;
//KR TODO: unsauber
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
import ibs.tech.html.GroupElement;
import ibs.tech.html.InputElement;
import ibs.tech.html.NewLineElement;
import ibs.tech.html.TableElement;
import ibs.util.DateTimeHelpers;
import ibs.util.file.FileHelpers;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;


/******************************************************************************
 * The Log_01 object holds the log of an import or an export operation. <BR/>
 *
 * @version     $Id: Log_01.java,v 1.29 2010/04/07 13:37:06 rburgermann Exp $
 *
 * @author      Buchegger Bernd (BB), 990128
 ******************************************************************************
 */
public class Log_01 extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Log_01.java,v 1.29 2010/04/07 13:37:06 rburgermann Exp $";


    /**
     * Absolute base path of the m2 system.<BR/>
     */
    protected String m2AbsBasePath = "";

    /**
     * Vector that holds all log entries.<BR/>
     */
    protected Vector<LogElement> logElements;

    /**
     * Vector that holds all error log entries.<BR/>
     */
    protected Vector<LogElement> errorLogElements;

    /**
     * Vector that holds all warning log entries.<BR/>
     */
    protected Vector<LogElement> warningLogElements;

    /**
     * Path of the logfile.<BR/>
     */
    protected String path = "";

    /**
     * Name of the logfile. <BR/>
     */
    protected String fileName = "";

    /**
     * flag to write to log. <BR/>
     */
    public boolean isWriteLog = false;

    /**
     * flag to display the log. <BR/>
     */
    public boolean isDisplayLog = true;

    /**
     * flag to append the log. <BR/>
     */
    public boolean isAppendLog = false;

    /**
     * flag to generate HTML output otherwise generate text output.<BR/>
     */
    public boolean isGenerateHtml = true;

    /**
     * start date of the process this log is generating the protocol for.<BR/>
     */
    public Date processStartDate = null;

    /**
     * end date of the process this log is generating the protocol for.<BR/>
     */
    public Date processEndDate = null;

    /**
     * fileWriter to write the log
     */
    public FileWriter logFileWriter;


    /**************************************************************************
     * Creates an Log_01 Object. . <BR/>
     */
    public Log_01 ()
    {
        //init class variables
        this.logElements = new Vector<LogElement> ();
        this.errorLogElements = new Vector<LogElement> ();
        this.warningLogElements = new Vector<LogElement> ();
        this.path = "";
        this.fileName = "";
    } // Log_01


    /**************************************************************************
     * Sets the m2AbsbasePath. This is the absolute file path to the m2
     * system directories and is stored the in session.
     *
     * @param m2AbsBasePath     the m2AbsBasePath
     */
    public void setM2AbsBasePath (String m2AbsBasePath)
    {
        this.m2AbsBasePath = m2AbsBasePath;
    } // setM2AbsBasePath


    /**************************************************************************
     * Sets the name of the logfile. <BR/>
     *
     * @param fileName  name of the logfile
     */
    public void setFileName (String fileName)
    {
        this.fileName = fileName;
    } // setFileName


    /**************************************************************************
     * Sets the path of the log file. <BR/>
     *
     * @param   path    Path and name of the log file.
     */
    public void setPath (String path)
    {
        this.path = FileHelpers.addEndingFileSeparator (path);
    } // setFilePath


    /**************************************************************************
     * Getter method the name of the logfile. <BR/>
     *
     * @return  The name of the log file.
     */
    public String getFileName ()
    {
        return this.fileName;
    } // getFileName


    /**************************************************************************
     * Getter method for the path of the logfile. <BR/>
     *
     * @return the path of the logfile
     */
    public String getPath ()
    {
        return this.path;
    } // getPath


    /**************************************************************************
     * Getter method for the vector with all log entries. <BR/>
     *
     * @return the vector with all log entries
     */
    public Vector<LogElement> getLogElements ()
    {
        return this.logElements;
    } // getLogElements


    /**************************************************************************
     * Getter method for the vector with all error log entries. <BR/>
     *
     * @return the vector with all error log entries
     */
    public Vector<LogElement> getErrorLogElements ()
    {
        return this.errorLogElements;
    } // getErrorLogElements


    /**************************************************************************
     * Getter method for the vector with all warning log entries. <BR/>
     *
     * @return the vector with all warning log entries
     */
    public Vector<LogElement> getWarningLogElements ()
    {
        return this.warningLogElements;
    } // getWarningLogElements


    /**************************************************************************
     * Returns true if the log does not contain any error or warning
     * entries. <BR/>
     *
     * @return true if the log does not container any error or warning
     */
    public boolean isErrorfree ()
    {
        return this.errorLogElements.size () == 0 && this.warningLogElements.size () == 0;
    } // isErrorfree


    /**************************************************************************
     * Returns true if the log does contain any errors. <BR/>
     *
     * @return true if the log does contain any errors
     */
    public boolean hasErrors ()
    {
        return this.errorLogElements.size () > 0;
    } // hasErrors


    /**************************************************************************
     * Returns the number of errors in the log. <BR/>
     *
     * @return the number of errors in the log
     */
    public int getErrorsCount ()
    {
        return this.errorLogElements.size ();
    } // getErrorsCount


    /**************************************************************************
     * Returns true if the log does contain any warnings. <BR/>
     *
     * @return true if the log does contain any warnings
     */
    public boolean hasWarnings ()
    {
        return this.warningLogElements.size () > 0;
    } // hasWarnings


    /**************************************************************************
     * Returns the number of warnings in the log. <BR/>
     *
     * @return the number of warnings in the log
     */
    public int getWarningsCount ()
    {
        return this.warningLogElements.size ();
    } // getWarningsCount


    /**************************************************************************
     * Gets the parameters for the log from the environment.<BR/>
     */
    public void getParameters ()
    {
        String str;
        int num;

        // check if we have to write the log
        num = this.env.getBoolParam (DIArguments.ARG_WRITELOGFILE);
        if (num == IOConstants.BOOLPARAM_TRUE)
        {
            this.isWriteLog = true;
        } // if
        else if (num == IOConstants.BOOLPARAM_FALSE)
        {
            this.isWriteLog = false;
        } // else if
        else
        {
            this.isWriteLog = false;
        } // else

        // check if we have to display the log
        num = this.env.getBoolParam (DIArguments.ARG_DISPLAYLOGFILE);
        if (num == IOConstants.BOOLPARAM_TRUE)
        {
            this.isDisplayLog = true;
        } // if
        else if (num == IOConstants.BOOLPARAM_FALSE)
        {
            this.isDisplayLog = false;
        } // else if
        else
        {
            this.isDisplayLog = true;
        } // else

        // check if we have to append the log
        num = this.env.getBoolParam (DIArguments.ARG_APPENDLOGFILE);
        if (num == IOConstants.BOOLPARAM_TRUE)
        {
            this.isAppendLog = true;
        } // if
        else if (num == IOConstants.BOOLPARAM_FALSE)
        {
            this.isAppendLog = false;
        } // else if
        else
        {
            this.isAppendLog = false;
        } // else

        // get the log path
        str = this.env.getStringParam (DIArguments.ARG_LOGFILEPATH);
        if (str == null || str.length () == 0)
        {
            this.setPath (this.m2AbsBasePath + DIConstants.PATH_IMPORTLOG);
        } // if
        else
        {
            this.setPath (str);
        } // else

        // get logfile name
        str = this.env.getStringParam (DIArguments.ARG_LOGFILENAME);
        if (str == null || str.length () == 0)
        {
            this.setFileName (DIConstants.PATH_IMPORTLOGFILENAME);
        } // if
        else
        {
            this.setFileName (str);
        } // else
    } // getParameters


    /**************************************************************************
     * Displays the log form. <BR/>
     *
     * @param   table   The table layout element to which the form properties
     *                  shall be added.
     */
    public void showFormProperties (TableElement table)
    {
        InputElement input;
        GroupElement gel;

        // show fields for logging preferences
        // show checkbox for display the log
        this.showFormProperty (table, DIArguments.ARG_DISPLAYLOGFILE,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_DISPLAYLOGFILE, env),
            Datatypes.DT_BOOL, "" + this.isDisplayLog);
        // show checkbox for writing the log
        this.showFormProperty (table, DIArguments.ARG_WRITELOGFILE,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_WRITELOGFILE, env),
            Datatypes.DT_BOOL, "" + this.isWriteLog);
        // show field for name of the log file
        this.showFormProperty (table, DIArguments.ARG_LOGFILENAME,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_LOGFILENAME, env),
            Datatypes.DT_TEXT, this.fileName);
        // show field for name of the log file path
        gel = new GroupElement ();
        input = new InputElement (DIArguments.ARG_LOGFILEPATH,
                                  InputElement.INP_TEXT, this.path);
        input.size = 50;
        input.maxlength = 128;
        gel.addElement (input);
        this.showFormProperty (table, MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
            DITokens.ML_LOGFILEPATH, env), gel);
        // show checkbox for append the log
        this.showFormProperty (table, DIArguments.ARG_APPENDLOGFILE,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_APPENDLOGFILE, env),
            Datatypes.DT_BOOL, "" + this.isAppendLog);
    } // showFormProperties


    /**************************************************************************
     * Displays the log settings.<BR/>
     *
     * @param   table   the table element to add the log settings to
     */
    public void showProperties (TableElement table)
    {
        // check if log has been written
        if (this.isWriteLog)
        {
            // display log file name
            this.showProperty (table, DIArguments.ARG_LOGFILENAME,
                MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                    DITokens.ML_LOGFILENAME, env),
                Datatypes.DT_TEXT, this.getFileName ());
            // display log path
            this.showProperty (table, DIArguments.ARG_LOGFILEPATH,
                MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                    DITokens.ML_LOGFILEPATH, env),
                Datatypes.DT_TEXT, this.path);
            // display append log flag
            this.showProperty (table, DIArguments.ARG_APPENDLOGFILE,
                MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                    DITokens.ML_APPENDLOGFILE, env),
                Datatypes.DT_BOOL, "" + this.isAppendLog);
        } // if (this.isWriteLog)
        else // log should not be written
        {
            this.showProperty (table, DIArguments.ARG_WRITELOGFILE,
                MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                    DITokens.ML_WRITELOGFILE, env),
                Datatypes.DT_BOOL, "" + false);
        } // else log should not be written
    } // showProperties


    /**************************************************************************
     * Add a log entry to the logElement vector. <BR/>
     *
     * @param   type    Type of log entry.
     * @param   oid     Oid of object in the log entry.
     * @param   text    Text of log entry.
     */
    public void add (int type, OID oid, String text)
    {
        LogElement logElement = new LogElement (type, oid, text);
        // add the element to the vector
        this.add (logElement, this.isDisplayLog);
    } // add


    /**************************************************************************
     * Add a log entry to the logElement vector. <BR/>
     *
     * @param   type    Type of log entry.
     * @param   text    Text of log entry.
     */
    public void add (int type, String text)
    {
        LogElement logElement = new LogElement (type, null, text);
        // add the element to the vector
        this.add (logElement, this.isDisplayLog);
    } // add


    /**************************************************************************
     * Add a log entry to the logElement vector. <BR/>
     *
     * @param   type        Type of log entry.
     * @param   oid         Oid of object in the log entry.
     * @param   text        Text of log entry.
     * @param   isDisplay   Flag wheather to print the log entry to the env.
     */
    public void add (int type, OID oid, String text, boolean isDisplay)
    {
        LogElement logElement = new LogElement (type, oid, text);
        // add the element to the vector
        this.add (logElement, isDisplay);
    } // add


    /**************************************************************************
     * Add a log entry to the logElement vector. <BR/>
     *
     * @param   type        Type of log entry.
     * @param   text        Text of log entry.
     * @param   isDisplay   Flag wheather to print the log entry to the env.
     */
    public void add (int type, String text, boolean isDisplay)
    {
        LogElement logElement = new LogElement (type, null, text);
        // add the element to the vector
        this.add (logElement, isDisplay);
    } // add


    /**************************************************************************
     * Add a log entry to the logElement vector. <BR/>
     *
     * @param   logElement  The logElement object to be added.
     * @param   isDisplay   Flag wheather to print the log entry to the env.
     */
    public void add (LogElement logElement, boolean isDisplay)
    {
        // add the element to the vector of all log entries
        this.logElements.addElement (logElement);
        // check if the log entry type is error
        if (logElement.type == DIConstants.LOG_ERROR)
        {
            // add the log entry to the vector of all errors
            this.errorLogElements.addElement (logElement);
        } // if (logElement.type == DIConstants.LOG_ERROR)
        // check if the log entry type is error
        if (logElement.type == DIConstants.LOG_WARNING)
        {
            // add the log entry to the vector of all warnings
            this.warningLogElements.addElement (logElement);
        } // if (logElement.type == DIConstants.LOG_ERROR)

        // should the entry be displayed?
        if (isDisplay)
        {
            // should html output be generated?
            if (this.isGenerateHtml)
            {
                this.env.write ("<DIV ALIGN=\"LEFT\"><LI>" +
                    logElement.toHTMLString () + "</LI></DIV>");
            } // if
            else
            {
                this.env.write (logElement.toString () + "\r\n");
            } // else
        } // if (isDisplay)

        // check if we need to write the entry to the log file
        if (this.isWriteLog && this.logFileWriter != null)
        {
            String logText = logElement.toString () + "\r\n";
            try
            {
                this.logFileWriter  = new FileWriter (this.path + this.fileName, true);
                this.logFileWriter.write (logText, 0, logText.length ());
                this.logFileWriter.close ();
            } // try
            catch (IOException e)
            {
                IOHelpers.showMessage ("could not write the log!",
                    e, this.app, this.sess, this.env, true);
            } // catch (IOException e)
        } // if (this.isWriteLog && this.logFileWriter != null)
    } // add


    /**************************************************************************
     * Prints all entries of the log and generate HTML output.<BR/>
     *
     * @return a table that contains all entries in the log
     */
    public GroupElement print ()
    {
        GroupElement group = new GroupElement ();

        LogElement entry;
        // get the entries
        // check if we have any entries
        if (this.logElements != null)
        {
            // loop through the log entries:
            for (Iterator<LogElement> iter = this.logElements.iterator (); iter.hasNext ();)
            {
                entry = iter.next ();
                group.addElement (entry.print ());
                group.addElement (new NewLineElement ());
            } // for iter
        } // if (this.logElements != null)
        return group;
    } // print


    /***************************************************************************
     * Genenerate a string with all log entries. <BR/>
     *
     * @return a string containing all entries of the log.
     */
    public String toString ()
    {
        return this.toString (this.logElements);
    } // toString


    /**************************************************************************
     * Genenerate a string with all error log entries. <BR/>
     *
     * @return a string containing all entries of the log.
     */
    public String errorsToString ()
    {
        return this.toString (this.errorLogElements);
    } // errorsToString


    /**************************************************************************
     * Genenerate a string with all warning log entries. <BR/>
     *
     * @return a string containing all entries of the log.
     */
    public String warningsToString ()
    {
        return this.toString (this.warningLogElements);
    } // warningsToString


    /**************************************************************************
     * Genenerate a string with entries in a log vector. <BR/>
     *
     * @param   logVector     the vector that holds the log entries
     *
     * @return a string containing all entries of the log.
     */
    public String toString (Vector<LogElement> logVector)
    {
        String logStr = "";
        LogElement entry;

        // check if we have any entries
        if (logVector != null && logVector.size () > 0)
        {
            // get the entries:
            // loop through the log entries:
            for (Iterator<LogElement> iter = logVector.iterator (); iter.hasNext ();)
            {
                entry = iter.next ();
                logStr += entry.toString () + "\r\n";
            } // for iter
        } // if (this.logElements != null)
        return logStr;
    } // toString


    /**************************************************************************
     * Initializes the log. In case we have to write the log a fileWriter
     * will be initialized and the log header will be written to the log.
     * If the append log option is not activated a unique filename will
     * be created for the log file. <BR/>
     *
     * @return  <CODE>true</CODE> if the logging was successful,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean initLog ()
    {
        // first check if we need to write the log
        if (this.isWriteLog)
        {
            String logText;
            try
            {
                // check if we need to append the log to an existing log file
                if (this.isAppendLog)
                {
                    // append the file ending for logFiles
                    this.fileName = this.addLogExtension (this.fileName);
                    this.logFileWriter = new FileWriter (this.path + this.fileName, true);
                } // if (this.isAppendLog)
                else    // create a new log
                {
                    // append the actual date
                    this.fileName += "_" + DateTimeHelpers.getTimestamp ();
                    // append the file extension for logFiles
                    this.fileName = this.addLogExtension (this.fileName);
                    // make sure it is a unique filename
                    this.fileName = FileHelpers.getUniqueFileName (this.path, this.fileName);
                    // create the fileWriter
                    this.logFileWriter = new FileWriter (this.path + this.fileName, false);
                } // else create a new log
                // add the log header and the log footer
                logText = this.getLogHeader ();
                // write the log header first
                this.logFileWriter.write (logText, 0, logText.length ());
                this.logFileWriter.close ();
                return true;
            } // try
            catch (IOException e)
            {
                return false;
            } // catch
        } // if (this.isWriteLog)

        // log does not need to be written
        return true;
    }   // initLog


    /**************************************************************************
     * Closes the log. In case we had to write the log the log footer will
     * be written and the fileWriter will be closed. <BR/>
     *
     * @return  <CODE>true</CODE> if the log was closed successfully,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean closeLog ()
    {
        // check first if we had to write the log
        if (this.isWriteLog)
        {
            String logText;
            try
            {
                this.logFileWriter  = new FileWriter (this.path + this.fileName, true);
                logText = this.getLogFooter ();
                // write the log header first
                this.logFileWriter.write (logText, 0, logText.length ());
                this.logFileWriter.close ();
                return true;
            } // try
            catch (IOException e)
            {
                return false;
            } // catch
        } // if (this.isWriteLog)

        // we did not write the log
        return true;
    } // closeLog


    /**************************************************************************
     * Writes a log to the filesystem. Differentiates between appending the
     * log to an existing log file or creating a new one. <BR/>
     *
     * @return true if log could have been successfully written or false otherwise
     */
    public boolean writeLog ()
    {
        if (this.isAppendLog)
        {
            return this.appendLogFile ();
        } // if (isAppend)

        // create a new log file
        return this.createLogFile ();
    } // performWriteLog


    /**************************************************************************
     * Adds a log to an existing log file. If logfile does not exist yet
     * it will be created. <BR/>
     *
     * @return true if log file could be appended/created or false otherwise
     */
    public boolean appendLogFile ()
    {
        String logText;
        FileWriter fw;
        try
        {
            // append the file ending for logFiles
            this.fileName = this.addLogExtension (this.fileName);
            fw = new FileWriter (this.path + this.fileName, true);
            // create a string that holds the log text
            logText = this.toString ();
            // add the log header and the log footer
            logText = this.getLogHeader () + logText + this.getLogFooter ();
            // write the log text
            fw.write (logText, 0, logText.length ());
            // close the stream
            fw.close ();
            return true;
        } // try
        catch (IOException e)
        {
            return false;
        } // catch
    } // appendLogFile


    /**************************************************************************
     * Creates a log file in the file system. Ensures that the filename ist
     * unique by adding counter numbers to the beginning of the filename.<BR/>
     *
     * @return  the filename used for the log file or null if file could not have
     *          been written
     */
    public boolean createLogFile ()
    {
        String logText;
        FileWriter fw;
        try
        {
            // append the actual date
            this.fileName += DIHelpers.getDateString ();
            // append the file extension for logFiles
            this.fileName = this.addLogExtension (this.fileName);
            // make sure it is a unique filename
            this.fileName = FileHelpers.getUniqueFileName (this.path, this.fileName);
            // create the fileWriter
            fw = new FileWriter (this.path + this.fileName, false);
            // create a string that holds the log text
            logText = this.toString ();
            // add the log header and the log footer
            logText = this.getLogHeader () + logText + this.getLogFooter ();
            // write the text
            fw.write (logText, 0, logText.length ());
            // close the stream
            fw.close ();
            return true;
        } // try
        catch (IOException e)
        {
            return false;
        } // catch
    } // createLogFile


    /**************************************************************************
     * Add a .log extension to a fileName if filename does not already have
     * one. <BR/>
     *
     * @param   fileName    The name of the file which has to be extended
     *                      with the log extension.
     *
     * @return  The fileName with the .log as extension.
     */
    protected String addLogExtension (String fileName)
    {
        if (fileName.endsWith (DIConstants.LOGFILE_EXTENSION))
        {
            return fileName;
        } // if

        return fileName + DIConstants.LOGFILE_EXTENSION;
    } // addLogExtension


    /**************************************************************************
     * Creates a log header. <BR/>
     *
     * @return  The header for the log.
     */
    protected String getLogHeader ()
    {
        return "------------------------------------------------\r\n" +
               "- LOG " + DateTimeHelpers.dateTimeToString (new Date ()) + "\r\n" +
               "------------------------------------------------\r\n";
    } // getLogHeader


    /**************************************************************************
     * Creates a log footer. <BR/>
     *
     * @return  The footer for the log.
     */
    protected String getLogFooter ()
    {
        return "\r\n";
    } // getLogFooter

} // class Log_01
