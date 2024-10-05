/*
 * Class: Tracer.java
 */

// package:
package ibs.util.trace;

// imports:
import ibs.util.trace.FileTracer;
import ibs.util.trace.TraceServer;


/******************************************************************************
 * Tracer for writing some tracing output to a file. <BR/>
 *
 * @version     $Id: Tracer.java,v 1.9 2007/07/31 19:14:01 kreimueller Exp $
 *
 * @author      Bernhard Walter (BW), 99????
 ******************************************************************************
 */
public class Tracer extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Tracer.java,v 1.9 2007/07/31 19:14:01 kreimueller Exp $";


    /**
     * The header of a tracing file. <BR/>
     * Standard: <CODE>"Number;Millis;TraceSource;Description"</CODE>
     */
    public static final String TRACE_HEADER =
        "Number;Millis;TraceSource;Description";

    /**
     * Is the file tracer enabled? <BR/>
     * Default: <CODE>false</CODE>
     */
    public static boolean isFileTracerEnabled = false;

    /**
     * Is the tracing server enabled? <BR/>
     * Default: <CODE>false</CODE>
     */
    public static boolean isTraceServerEnabled = true;

    /**
     * Counter holding the number of trace calls. <BR/>
     * Just for statistical purposes.
     */
    private static int traceCounter = 0;

    /**
     * The file tracer. <BR/>
     */
    private FileTracer fileTracer = null;

    /**
     * The trace server. <BR/>
     * This server is responsible for distributing trace messages to the
     * several trace clients.
     */
    protected TraceServer traceServer = null;


    /**************************************************************************
     * Create a new tracer object. <BR/>
     *
     * @param   name    The name of the tracer.
     *                  The file name for the tracer is built through appending
     *                  the <A HREF="#EXTENSION">EXTENSION</A> to the name.
     */
    public Tracer (String name)
    {
        if (Tracer.isFileTracerEnabled) // file tracer is enabled?
        {
            // open a new file tracer:
            this.fileTracer = new FileTracer (name, Tracer.TRACE_HEADER);
        } // if
    } // Tracer


    /**************************************************************************
     * Open the tracer. <BR/>
     */
    public void open ()
    {
        if (this.fileTracer != null)    // the file tracer exists?
        {
            // ensure that the file tracer is open:
            this.fileTracer.open ();
        } // if the file tracer exists
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
        if (this.fileTracer != null)    // the file tracer exists?
        {
            // set the name for the file tracer:
            this.fileTracer.setName (name);
        } // if the file tracer exists
    } // setName


    /**************************************************************************
     * Print an event to the trace file. <BR/>
     *
     * @param   sourceName  The name of the event source.
     * @param   message     The message to be printed.
     */
    public void print (String sourceName, String message)
    {
        ++Tracer.traceCounter;          // increment the trace counter

        // print the message:
        if (this.fileTracer != null)    // file tracer exists?
        {
            // write message to file writer:
            this.fileTracer.print (Tracer.traceCounter, sourceName, message);
        } // if file tracer exists

        if (this.traceServer != null)   // trace server exists?
        {
            // send the message to the trace server:
            this.traceServer.trace (sourceName, message, this);
        } // if trace server exists
    } // print


    /**************************************************************************
     * Print an event to the trace file. <BR/>
     * The conversion to String is done AFTER checking if a tracer exists.
     *
     * @param   sourceName  The name of the event source.
     * @param   message     The message to be printed.
     */
    public void print (String sourceName, StringBuffer message)
    {
        ++Tracer.traceCounter;          // increment the trace counter

        // print the message:
        if (this.fileTracer != null)    // file tracer exists?
        {
            // write message to file writer:
            this.fileTracer.print (Tracer.traceCounter, sourceName,
                message.toString ());
        } // if file tracer exists

        if (this.traceServer != null)   // trace server exists?
        {
            // send the message to the trace server:
            this.traceServer.trace (sourceName, message.toString (), this);
        } // if trace server exists
    } // print


    /**************************************************************************
     * Close the tracer. <BR/>
     */
    public void close ()
    {
        if (this.fileTracer != null)    // the file tracer exists?
        {
            // close the file tracer:
            this.fileTracer.close ();
        } // if the file tracer exists
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

} // class Tracer
