/*
 * Class: TracerManager.java
 */

// package:
package ibs.util.trace;

// imports:
import ibs.util.trace.Tracer;
import ibs.util.trace.TracerHolder;
import ibs.util.trace.TraceServer;


/******************************************************************************
 * Tracer for writing some tracing output to a file. <BR/>
 *
 * @version     $Id: TracerManager.java,v 1.10 2007/07/31 19:14:01 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR)  001112
 *******************************************************************************
 */
public class TracerManager extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: TracerManager.java,v 1.10 2007/07/31 19:14:01 kreimueller Exp $";


    /**
     * Is the tracing server enabled?. <BR/>
     * Default: <CODE>false</CODE>
     */
    public static boolean isTraceServerEnabled = false;

    /**
     * The trace server. <BR/>
     * This server is responsible for distributing trace messages to the
     * several trace clients.
     */
    private static TraceServer p_traceServer = null;

    /**
     * The name of the tracer when there is no tracer holder defined. <BR/>
     */
    private static final String TRACER_NOHOLDER = "NoHolderGlobal";

    /**
     * The name of the tracer when there is a holder, but no name comes from
     * the holder. <BR/>
     */
    private static final String TRACER_HOLDERWITHOUTNAME = "NoName";

    /**
     * The global tracer object. <BR/>
     * This tracer is used every time when there is no other tracer specified.
     * It is defined static for being usable throughout the application all the
     * time the java virtual machine is running.
     */
    private static Tracer p_globalTracer = null;

    /**
     * The tracer object used when there is a holder, but no tracer name. <BR/>
     * This tracer is used every time when there is a holder available, but the
     * holder does not provide a name for the tracer.
     * It is defined static for being usable throughout the application all the
     * time the java virtual machine is running.
     */
    private static Tracer p_noNameTracer = null;


    /**************************************************************************
     * Create a new tracer manager object. <BR/>
     */
    public TracerManager ()
    {
        // nothing to do
    } // TracerManager


    /**************************************************************************
     * Enable or disable the trace server. <BR/>
     *
     * @param   isEnabled   Shall the trace server be enabled or disabled?
     */
    public static void setTraceServerEnabled (boolean isEnabled)
    {
        // set corresponding property:
        TracerManager.isTraceServerEnabled = isEnabled;

        if (TracerManager.isTraceServerEnabled &&
            TracerManager.p_traceServer == null)
                                        // trace server must be started?
        {
            // start the trace server:
            TracerManager.p_traceServer = TraceServer.startTraceServer ();
        } // if trace server must be started
        else if (TracerManager.isTraceServerEnabled &&
            TracerManager.p_traceServer != null)
                                        // trace server exists and must be
                                        // started?
        {
            // ensure that the trace server is started:
            TracerManager.p_traceServer.start ();
        } // if trace server exists and must be started
        else if (!TracerManager.isTraceServerEnabled &&
            TracerManager.p_traceServer != null)
                                        // trace server must be stopped?
        {
            // stop the trace server:
            TracerManager.p_traceServer.stop ();
        } // if trace server must be stopped
    } // setTraceServerEnabled


    /**************************************************************************
     * Create a new tracer object. <BR/>
     * The method creates a new tracer object and assigns the current
     * p_traceServer to it.
     *
     * @param   name    The name for the tracer.
     *
     * @return  The tracer which was created or
     *          <CODE>null</CODE> if it was not possible to create the tracer.
     */
    private static Tracer createTracer (String name)
    {
        Tracer tracer = null;           // the found tracer

        // create the new tracer:
        tracer = new Tracer (name);

        // set the trace server:
        tracer.traceServer = TracerManager.p_traceServer;

        // return the tracer:
        return tracer;
    } // createTracer


    /**************************************************************************
     * Get a trace object. <BR/>
     * This method checks if there is already a tracer defined within the
     * session. If there is one its name is changed to the user name defined
     * within the session.
     * If there is currently no tracer defined within the session a new
     * tracer is generated depending on the user name.
     * If there is no user name defined within the session the tracer
     * <A HREF="#p_noNameTracer">p_noNameTracer</A> is used. If necessary this
     * tracer is generated on the fly.
     * If the session is null the <A HREF="#p_globalTracer">p_globalTracer</A> is
     * used. If necessary this tracer is generated on the fly.
     *
     * @param   tHolder The actual tracer holder.
     *
     * @return  The tracer.
     */
    public static Tracer getTracer (TracerHolder tHolder)
    {
        Tracer tracer = null;           // the found tracer
        String tracerName;              // the name of the tracer

        // check if there is already a tracer holder:
        if (tHolder != null)            // tracer holder exists?
        {
            // get the tracer from the tracer holder:
            tracer = tHolder.getTracer ();

            // get the name for the tracer:
            tracerName = tHolder.getTracerName ();

            // check if we have a valid name:
            if (tracerName != null && tracerName.length () > 0)
                                    // there is a name defined?
            {
                if (tracer == null || tracer == TracerManager.p_noNameTracer)
                                            // no tracer defined yet?
                {
                    // create a new tracer for that name:
                    tracer = TracerManager.createTracer (tracerName);
                    // set the tracer within the tracer holder:
                    tHolder.setTracer (tracer);
                } // if no tracer defined yet
                else                        // there is already a tracer defined
                {
                    // set the new name for the tracer:
                    tracer.setName (tracerName);
                } // else no name defined
            } // if there is a name defined
            else                    // no name defined
            {
                if (tracer != null && tracer != TracerManager.p_noNameTracer)
                                            // already a tracer defined?
                {
                    // close the old tracer:
                    tracer.close ();
                } // if already a tracer defined

                // check if the tracer for unknown name already exists:
                if (TracerManager.p_noNameTracer == null)
                                    // no tracer defined yet?
                {
                    // create a new tracer for no name:
                    TracerManager.p_noNameTracer =
                        TracerManager.createTracer (
                            TracerManager.TRACER_HOLDERWITHOUTNAME);
                } // if no tracer defined yet

                // use the noName tracer as actual tracer:
                tHolder.setTracer (TracerManager.p_noNameTracer);
            } // else no name defined

            // use the session tracer as actual tracer:
            tracer = tHolder.getTracer ();
        } // if tracer holder exists
        else                            // tracer holder does not exist
        {
            // create a new global tracer if necessary:
            if (TracerManager.p_globalTracer == null) // no global tracer defined yet?
            {
                // create the tracer:
                TracerManager.p_globalTracer =
                    TracerManager.createTracer (TracerManager.TRACER_NOHOLDER);
            } // if no global tracer defined yet

            // use the global tracer as actual tracer:
            tracer = TracerManager.p_globalTracer;
        } // else tracer holder does not exist

        // ensure that the tracer is open:
        tracer.open ();

        // return the tracer:
        return tracer;
    } // getTracer


    /**************************************************************************
     * Close the current tracer object. <BR/>
     *
     * @param   tracer  The tracer to be closed.
     */
    public static void closeTracer (Tracer tracer)
    {
        // close the tracer if it exists:
        if (tracer != null)             // the tracer exists?
        {
            tracer.close ();            // close it
        } // if
    } // closeTracer


    /**************************************************************************
     * Finalize the tracer. <BR/>
     * Called by the garbage collector when there are no more references to an
     * instance of this class. <BR/>
     * This method just closes the possibly opened output stream.
     */
    public static void stop ()
    {
        String tracerMsgHeader = "TracerManager";
        // close the open tracer:
//        close ();

        // stop the trace server:
        TracerManager.p_globalTracer.print (tracerMsgHeader, "stopping p_traceServer...");
        TracerManager.p_traceServer.stop ();
        TracerManager.p_globalTracer.print (tracerMsgHeader, "p_traceServer stopped.");
    } // stop


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
//        close ();

        // stop the trace server:
        TracerManager.p_traceServer.stop ();

        // call common finalizer:
        super.finalize ();
    } // finalize

} // class TracerManager
