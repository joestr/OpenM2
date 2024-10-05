/*
 * Class: FileTracer.java
 */

// package:
package ibs.util.trace;

// imports:
import ibs.util.DateTimeHelpers;
import ibs.util.trace.TracerConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;


/******************************************************************************
 * Tracer for writing some tracing output to a file. <BR/>
 *
 * @version     $Id: FileTracer.java,v 1.11 2007/07/31 19:14:01 kreimueller Exp $
 *
 * @author      Bernhard Walter (BW)  99????
 * @author      Klaus Reimüller (KR)  001112
 ******************************************************************************
 */
public class FileTracer extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: FileTracer.java,v 1.11 2007/07/31 19:14:01 kreimueller Exp $";


    /**
     * The directory where the tracing files shall be written. <BR/>
     * Default: <CODE>"c:\Inetpub\wwwroot\m2\debug\"</CODE>
     */
    public static String debugDirectory = TracerConstants.TRACERPATH_DEFAULT;

    /**
     * Extension for tracing files. <BR/>
     * Default: <CODE>".csv"</CODE>
     */
    public static String extension = ".csv";

    /**
     * Shall the file be appended?. <BR/>
     * If the tracer is opened and there exists already a file with this name
     * this property tells whether the file shall be replaced or the new trace
     * shall be appended to the existing one. <BR/>
     * Default: <CODE>false</CODE>
     */
    public static boolean append = false;

    /**
     * Name of tracer for writing output. <BR/>
     */
    public static final String TRACER_NAME = "Tracer";

    /**
     * The name of the tracer. <BR/>
     */
    public String name = null;

    /**
     * The header which is written first to the file. <BR/>
     */
    public String traceHeader = null;

    /**
     * The output stream which is used to write the data to the file. <BR/>
     */
    private FileOutputStream fileOs = null;

    /**
     * The trace writer. <BR/>
     */
    private PrintWriter writer = null;

    /**
     * Is the tracer currently open?. <BR/>
     */
    private boolean isOpen = false;

    /**
     * The start time of the tracing. <BR/>
     */
    private Date startTime;


    /**************************************************************************
     * Create a new file tracer object. <BR/>
     *
     * @param   name        The name of the tracer.
     *                      The file name for the tracer is built through
     *                      appending the {@link #extension} to the name.
     * @param   traceHeader The header to be used for the trace file.
     */
    public FileTracer (String name, String traceHeader)
    {
        this.name = name;               // set the tracer's name
        this.traceHeader = traceHeader; // the header of the trace file
    } // FileTracer


    /**************************************************************************
     * Open the tracer. <BR/>
     */
    public void open ()
    {
        // check if the tracer is currently open:
        if (!this.isOpen)               // the tracer is not open?
        {
            try
            {
                // ensure that there is a file separator at the end of the
                // path:
                if (!FileTracer.debugDirectory.endsWith (File.separator))
                {
                    FileTracer.debugDirectory += File.separator;
                } // if

                // get the output stream:
                this.fileOs = new FileOutputStream (FileTracer.debugDirectory +
                    this.name + FileTracer.extension, FileTracer.append);
                // get the trace writer:
                this.writer = new PrintWriter (this.fileOs);
                // get the starting time:
                this.startTime = new Date ();
                // now the trace is open:
                this.isOpen = true;
                // write the header:
                this.writer.println (this.traceHeader);

                // make the first output to the file:
                this.print (0, FileTracer.TRACER_NAME + " " + this.name,
                       "Start at " + DateTimeHelpers.dateTimeToString (this.startTime));
            } // try
            catch (IOException e)
            {
                // display the error message:
                System.err.println (e);
            } // catch
        } // if the tracer is not open
    } // open


    /**************************************************************************
     * Set the name for the tracer. <BR/>
     * The tracer checks if the old name and the new name are the same. If
     * this is the case nothing is done.
     * If the names differ, the file for the old name is closed and a new file
     * for the new name is created.
     * After this operation the tracer has the same state as before (open or
     * closed).
     *
     * @param   name    The name of the tracer.
     *                  The file name for the tracer is built through appending
     *                  the <A HREF="#EXTENSION">EXTENSION</A> to the name.
     */
    public void setName (String name)
    {
        boolean oldIsOpen = this.isOpen; // store the current state

        // check if there is an old tracer with another name than the new one:
        if (this.name != null && !this.name.equalsIgnoreCase (name))
                                        // an old tracer is open?
        {
            // close the old tracer:
            this.close ();

            // inititialize the name:
            this.name = null;
        } // if an old tracer is open

        // check if there is a name:
        if (this.name == null)          // currently no name?
        {
            // set the new name:
            this.name = name;

            // ensure the state of the tracer:
            if (oldIsOpen)              // the tracer shall be open?
            {
                // open the tracer:
                this.open ();
            } // if the tracer shall be open
        } // if currently no name
    } // setName


    /**************************************************************************
     * Print an event to the trace file. <BR/>
     *
     * @param   number      Number of message.
     * @param   sourceName  The name of the event source.
     * @param   message     The message to be printed.
     */
    public void print (int number, String sourceName, String message)
    {
        long millis = 0;

        // check if the trace already exists:
        if (this.writer != null)
        {
            Date current = new Date (); // the actual date/time
//            String counterStr = "";
//            String millisStr = "";
            String fullMessage = "";

            // check if the start time is already initialized:
            if (this.startTime != null)
            {
                millis = current.getTime () - this.startTime.getTime ();
            } // if

            // print the number:
            fullMessage +=
                ((number >= 100000) ? "" + (number / 100000) : " ") +
                ((number >= 10000) ? "" + (number / 10000 % 10) : " ") +
                ((number >= 1000) ? "" + (number / 1000 % 10) : " ") +
                ((number >= 100) ? "" + (number / 100 % 10) : " ") +
                ((number >= 10) ? "" + (number / 10 % 10) : " ") +
                "" + (number % 10);

            // print the time:
            fullMessage += "\t" +
                ((millis >= 100000) ? "" + (millis / 100000) : " ") +
                ((millis >= 10000) ? "" + (millis / 10000 % 10) : " ") +
                ((millis >= 1000) ? "" + (millis / 1000 % 10) : " ") +
                ((millis >= 100) ? "" + (millis / 100 % 10) : " ") +
                ((millis >= 10) ? "" + (millis / 10 % 10) : " ") +
                "" + (millis % 10);

            // print the message:
            fullMessage += "\t" + sourceName + "\t" + message;
            this.writer.print (fullMessage);

            // store the new start time:
            this.startTime = current;
            // go to next line:
            this.writer.println ();
            // flush the text to the file:
            this.writer.flush ();
        } // if
    } // print


    /**************************************************************************
     * Close the tracer. <BR/>
     */
    public void close ()
    {
        if (this.writer != null)
        {
            try
            {
                // make the last output to the file:
                this.print (0, FileTracer.TRACER_NAME + " " + this.name,
                       "End at " + DateTimeHelpers.dateTimeToString (new Date ()) + "\n");

                this.writer.flush ();
                this.isOpen = false;
                this.writer.close ();
                this.fileOs.close ();

                // ensure that both the writer and the file connector are
                // released:
                this.writer = null;
                this.fileOs = null;
            } // try
            catch (IOException e)
            {
                // display the error:
                System.err.println (e);
            } // catch
        } // if
    } // close


    /**************************************************************************
     * Finalize the tracer. <BR/>
     * Called by the garbage collector when there are no more references to an
     * instance of this class. <BR/>
     * This method just closes the possibly opened output stream.
     *
     * @throws Throwable the <code>Exception</code> raised by this method
     */
    protected void finalize () throws Throwable
    {
        // close the open tracer:
        this.close ();

        // call common finalizer:
        super.finalize ();
    } // finalize

} // class FileTracer
