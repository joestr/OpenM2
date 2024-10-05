/*
 * Class: TracerHolder.java
 */

// package:
package ibs.util.trace;

// imports:
import ibs.util.trace.Tracer;


/******************************************************************************
 * Implementations of this interface hold one or more tracers. <BR/>
 * The objects should just hold the tracers, but not change them.
 *
 * @version     $Id: TracerHolder.java,v 1.3 2007/07/10 09:16:40 kreimueller Exp $
 *
 * @author      Klaus, 11.10.2003
 ******************************************************************************
 */
public interface TracerHolder
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: TracerHolder.java,v 1.3 2007/07/10 09:16:40 kreimueller Exp $";



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Get the name for the tracer. <BR/>

     * @return  The tracer name.
     */
    public String getTracerName ();


    /**************************************************************************
     * Set a new tracer. <BR/>
     *
     * @param   tracer  The tracer to be set.
     */
    public void setTracer (Tracer tracer);


    /**************************************************************************
     * Set a new tracer. <BR/>
     *
     * @return  The tracer.
     */
    public Tracer getTracer ();

} // class TracerHolder
