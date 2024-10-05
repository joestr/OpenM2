/*
 * Class: BaseObject.java
 */

// package:
package ibs;

// imports:
import ibs.util.trace.Tracer;
import ibs.util.trace.TracerHolder;
import ibs.util.trace.TracerManager;


/******************************************************************************
 * This is a base class for objects within ibs. <BR/>
 * This class must be a major class of all object within ibs so they can use all
 * the features.
 *
 * @version     $Id: BaseObject.java,v 1.10 2007/07/17 12:11:14 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR), 000126
 ******************************************************************************
 */
public abstract class BaseObject extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: BaseObject.java,v 1.10 2007/07/17 12:11:14 kreimueller Exp $";


    /**
     * The tracer object. <BR/>
     * This tracer object represents the current tracer of this object.
     * If there is a tracer within the session info specified that one is used,
     * otherwise the globalTracer is used as actual tracer.
     */
    private Tracer p_tracer = null;

    /**
     * Holds the actual tracer info. <BR/>
     * This object is used to get the current tracer information.
     */
    private TracerHolder p_tracerHolder = null;


    /**************************************************************************
     * Set the actual session info object. <BR/>
     * The p_tracer object ist set, too.
     *
     * @param   tHolder The tracer holder object to be set.
     */
    public void setTracerHolder (TracerHolder tHolder)
    {
        // set the tracer holder property:
        this.p_tracerHolder = tHolder;

        // try to get the tracer for this property:
        if (this.p_tracerHolder != null) // tracer holder exists?
        {
            if (this.p_tracerHolder.getTracer () == null)
                                        // tracer holder has no tracer defined yet?
            {
                this.openTrace ();      // open a new tracer
            } // if tracer holder has no tracer defined yet

            // use the tracer out of the tracer holder as actual tracer:
            this.p_tracer = this.p_tracerHolder.getTracer ();
        } // if tracer holder exists
    } // setTracerHolder


    /**************************************************************************
     * Get the actual session info object. <BR/>
     *
     * @return  The actual session info object or <CODE>null</CODE> if there
     *          is none.
     */
/*
    public SessionInfo getSession ()
    {
        // get the session property and return it:
        return this.sess;
    } // getSession
*/


    /**************************************************************************
     * Get the actual tracer. <BR/>
     *
     * @return  The actual tracer or <CODE>null</CODE> if there is none.
     */
    public Tracer getTracer ()
    {
        // get the tracer property and return it:
        return this.p_tracer;
    } // getTracer


    /**************************************************************************
     * Open a new trace object. <BR/>
     */
    public void openTrace ()
    {
        // get the tracer from the tracer manager:
        this.p_tracer = TracerManager.getTracer (this.p_tracerHolder);
    } // openTrace


    /**************************************************************************
     * Close the current trace object. <BR/>
     */
    public void closeTrace ()
    {
        // close the tracer if it exists:
        if (this.p_tracer != null)        // the tracer exists?
        {
            this.p_tracer.close ();       // close it
        } // if the tracer exists
    } // closeTrace


    /**************************************************************************
     * Send a trace message to the trace object. <BR/>
     *
     * @param  msg      The trace message.
     */
    public void trace (String msg)
    {
        String sourceName = "" + this;  // the name of the trace source

        // send the message to the tracer:
        if (this.p_tracer != null)        // the tracer exists?
        {
            // tell the tracer to print the message:
            this.p_tracer.print (sourceName, msg);
        } // if the tracer exists
    } // trace


    /**************************************************************************
     * Send a trace message to the trace object. <BR/>
     *
     * @param  msg      The trace message.
     */
    public void trace (StringBuffer msg)
    {
        String sourceName = "" + this;  // the name of the trace source

        // send the message to the tracer:
        if (this.p_tracer != null)        // the tracer exists?
        {
            // tell the tracer to print the message:
            this.p_tracer.print (sourceName, msg);
        } // if the tracer exists
    } // trace


    /**************************************************************************
     * Creates and returns a copy of this object. <BR/>
     * For any object <tt>x</tt>, the following expressions will be
     * <tt>true</tt>:
     * <blockquote><pre>
     * x.clone () != x
     * x.clone ().getClass () == x.getClass ()
     * x.clone ().equals (x)
     * </pre></blockquote>
     * The object returned by this method is independent of this object (which
     * is being cloned).
     *
     * @return  A clone of this instance. <BR/>
     *
     * @exception   CloneNotSupportedException
     *              if the object's class does not support the
     *              <code>Cloneable</code> interface. Subclasses that override
     *              the <code>clone</code> method can also throw this exception
     *              to indicate that an instance cannot be cloned.
     * @exception   OutOfMemoryError
     *              if there is not enough memory.
     *
     * @see java.lang.Cloneable
     */
/*
    public Object clone ()
    {
        BaseObject obj = null;          // the new object

        try
        {
            // call corresponding method of super class:
            obj = (BaseObject) super.clone ();

            // set specific properties:
            // because the clone method of {@link java.lang.Object Object}
            // performs a shallow and not a deep copy of all existing properties
            // we have to perform the deep copy here to ensure that there are
            // no side effects.
        } // try
        catch (CloneNotSupportedException e)
        {
            return e;
        } // catch CloneNotSupportedException
        catch (OutOfMemoryError e)
        {

        } // catch OutOfMemoryError

        // return the new object:
        return obj;
    } // clone
*/

} // class BaseObject
