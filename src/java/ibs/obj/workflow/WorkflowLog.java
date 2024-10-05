/*
 * Class: WorkflowLog.java
 */

// package:
package ibs.obj.workflow;

// imports:
import ibs.di.LogElement;
import ibs.di.Log_01;
import ibs.io.IOHelpers;
import ibs.util.file.FileHelpers;

import java.io.FileWriter;
import java.io.IOException;


/******************************************************************************
 * Extends the ibs.di.Log_01 object. <BR/>
 *
 * @version     $Id: WorkflowLog.java,v 1.12 2009/07/24 18:22:05 kreimueller Exp $
 *
 * @author      Horst Pichler, 6.11.2000
 ******************************************************************************
 */
public class WorkflowLog extends Log_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: WorkflowLog.java,v 1.12 2009/07/24 18:22:05 kreimueller Exp $";


    /**
     * flag that indicates if display-flag is frozen. <BR/>
     */
    private boolean freezeDisplayLogFlag = false;

    /**
     * flag that indicates if append-flag is frozen. <BR/>
     */
    private boolean freezeAppendLogFlag = false;


    /**************************************************************************
     * Creates an WorkflowLog Object. . <BR/>
     */
    public WorkflowLog ()
    {
        //init class variables
        super ();
    } // WorkflowLog


    /**************************************************************************
     * Overwrites super - does nothing!. <BR/>
     */
    public void getParameters ()
    {
        // nothing to do
    } // getParameters


    /**************************************************************************
     * Overwrites super - calls initLog("UNDEFINED", "UNDEFINED"). <BR/>
     *
     * @return  <CODE>true</CODE> if the log was initialized successfully,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean initLog ()
    {
        return this.initLog (null, null);
    } // initLog


    /**************************************************************************
     * Initializes the log. In case we have to write the log a fileWriter
     * will be initialized and the log header will be written to the log.
     * If the append log option is not activated a unique filename will
     * be created for the log file. <BR/>
     * Overwrites super.
     *
     * @param   logFilePath Path to the log file.
     * @param   logFileName Name of the log file.
     *
     * @return  <CODE>true</CODE> if the log was initialized successfully,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean initLog (String logFilePath, String logFileName)
    {
        // set filename and path
        this.fileName = logFileName;
        this.path = FileHelpers.addEndingFileSeparator (logFilePath);

        // reset initial workflow configuration of log
        // (necessary flags will be set later, from outside)
        this.isWriteLog = false;
        this.isAppendLog = false;
        this.isDisplayLog = true;

        // exit method
        return true;
    }   // initLog


    /**************************************************************************
     * Sets the log configuration for the workflow component. <BR/>
     *
     * @param   isDisplayLog           sets display-log flag
     * @param   isAppendLog            sets append-log  flag
     */
    public void configureForWorkflow (boolean isDisplayLog,
                                      boolean isAppendLog)
    {
        // log configuration for workflow-service
        this.isWriteLog = false;

        // change flag only if it is not frozen
        if (!this.freezeDisplayLogFlag)
        {
            this.isDisplayLog = isDisplayLog;   // log shall be displayed
        } // if

        // change flag only if it is not frozen
        if (!this.freezeAppendLogFlag)
        {
            this.isAppendLog = isAppendLog;
        } // if
    } // configureForWorkflow


    /**************************************************************************
     * Sets the log configuration. <BR/>
     *
     * @param   isDisplayLog           sets display-log flag
     * @param   freezeDisplayLogFlag   indicates if further changes of
     *                                 the displayLog-flags are allowed
     */
    public void configureDisplayFlag (boolean isDisplayLog,
                                      boolean freezeDisplayLogFlag)
    {
        // set configuration
        this.isDisplayLog = isDisplayLog;
        this.freezeDisplayLogFlag = freezeDisplayLogFlag;
    } // configureDisplayFlag


    /**************************************************************************
     * Sets the log configuration. <BR/>
     *
     * @param   isAppendLog           sets append-log flag
     * @param   freezeAppendLogFlag   indicates if further changes of
     *                                 the appendLog-flags are allowed
     */
    public void configureAppendFlag (boolean isAppendLog,
                                     boolean freezeAppendLogFlag)
    {
        // set configuration
        this.isAppendLog = isAppendLog;
        this.freezeAppendLogFlag = freezeAppendLogFlag;
    } // configureAppendFlag


    /**************************************************************************
     * Overwrites super. <BR/>
     *
     * @return  The header of the log.
     */
    protected String getLogHeader ()
    {
        return "";
    } // getLogHeader


    /**************************************************************************
     * Add a log entry to the logElement vector. <BR/>
     *
     * @param logElement    the logElement object to be added
     * @param isDisplay     flag wheather to print the log entry to the env
     */
    public void add (LogElement logElement, boolean isDisplay)
    {
        // add the element to the vector
        this.logElements.addElement (logElement);

        // display element - if flag is set
        if (isDisplay)
        {
            this.displayElement (logElement);
        } // if

        // write element to file
        this.writeElement (logElement);
    } // add


    /**************************************************************************
     * Display a log entry, but only if the display-flag is set. <BR/>
     *
     * @param logElement    the logElement object to be added
     */
    public void displayElement (LogElement logElement)
    {
        // check whether to write the log entry imitiatly to ethe environment
        if (this.isDisplayLog)
        {
            // write the text of the log entry to the environment
            if (this.isGenerateHtml)
            {
                this.env.write (
                    "<DIV ALIGN=\"LEFT\"><LI>" + logElement.toString () +
                    "</LI></DIV>");
            } // if
            else
            {
                this.env.write (logElement.toString () + "\r\n");
            } // else
        } // if (isWriteToEnv)
    } // displayElement


    /**************************************************************************
     * Write a log entry to the log-file. <BR/>
     *
     * @param logElement    the logElement object to be added
     */
    public void writeElement (LogElement logElement)
    {
        // check if we need to write the log
        if (this.isWriteLog || this.isAppendLog)
        {
            String logText = logElement.toString () + "\r\n";
            try
            {
                this.logFileWriter
                    = new FileWriter (this.path + this.fileName, true);
                this.logFileWriter.write (logText, 0, logText.length ());
                this.logFileWriter.close ();
            } // try
            catch (IOException e)
            {
                // could not write the log, display error message:
                IOHelpers.showMessage (
                    "WorkflowLog.writeElement: Could not write the log.",
                    e, this.app, this.sess, this.env, true);
            } // catch
        } // if
    } // writeElement


    /**************************************************************************
     * Appends existing elements in log-vector to the log-file. <BR/>
     */
    public void appendExisting ()
    {
        // loop through vector and write existing elements
        for (int i = 0; i < this.logElements.size (); i++)
        {
            this.writeElement (this.logElements.elementAt (i));
        } // for i
    } // appendExisting


    /**************************************************************************
     * Display existing elements in log-vector. <BR/>
     */
    public void displayExisting ()
    {
         // loop through vector and write existing elements
        for (int i = 0; i < this.logElements.size (); i++)
        {
            this.displayElement (this.logElements.elementAt (i));
        } // for i
    } // displayExisting

} // class WorkflowLog
