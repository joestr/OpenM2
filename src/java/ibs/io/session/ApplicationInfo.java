/*
 * Class: ApplicationInfo.java
 */

// package:
package ibs.io.session;

// imports:
//TODO: unsauber
import ibs.app.system.System;
import ibs.io.servlet.ApplicationInitializationException;
import ibs.io.session.IApplicationInitializer;
import ibs.io.session.SessionInfo;
import ibs.service.reporting.IReportingEngine;
import ibs.tech.html.ButtonBarElement;
import ibs.util.list.ListException;
import ibs.util.trace.TracerManager;

import java.util.Hashtable;


/******************************************************************************
 * This is the ApplicationInfo Object, which holds all
 * application-relevant information, especially Transactions and Values.
 *
 * @version     $Id: ApplicationInfo.java,v 1.26 2008/09/17 16:37:13 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR), 990111
 ******************************************************************************
 */
public class ApplicationInfo extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ApplicationInfo.java,v 1.26 2008/09/17 16:37:13 kreimueller Exp $";


    /**
      * The pool which manages the connections to the repository database. <BR/>
      */
//    public Encapsulator dbEncapsulator = null;

    /**
     * Property containing information regarding the available Layouts. <BR/>
     */
    public Object layouts = null;

    /**
     * Contains the last several objects any of the users worked with. <BR/>
     */
    public Object cache;

    /**
     * New cache for business objects. <BR/>
     */
/*
    public BusinessObject3Cache cache3 = new BusinessObject3Cache ();
*/

    /**
     * Interface to all customizing - systemqueries. <BR/>
     */
    public Object queryPool = null;

    /**
     * Pool containing all connections to external
     * applications of m2 users. <BR/>
     */
    public Object apiConPool = null;

    /**
     * Number o concurrent user connections. <BR/>
     */
    public int connections = 0;

    /**
     * The configuration info about the application read from the configuration
     * files. <BR/>
     */
    public Object configuration = null;

    /**
     * Contains all temporary stored sessionInfo-objects for
     * changing from and secure mode into an insecure one. <BR/>
     */
    public Hashtable<String, SessionInfo> sessioninfoTable =
        new Hashtable<String, SessionInfo> ();

    /**
     * Contains the error message if an error occurred while
     * reading the configuration-file. <BR/>
     */
    public StringBuffer configErrors = null;

    /**
     * All system information. <BR/>
     */
    public System p_system = null;

    /**
     * The observer loader. <BR/>
     */
    public Object p_observerLoader = null;

    /**
     * The application initializer object. <BR/>
     */
    public IApplicationInitializer p_appInitializer = null;

    /**
     * All knwon buttons. <BR/>
     */
    public ButtonBarElement p_buttons = null;

    /**
     * Is the restart of the application possible? <BR/>
     */
    public boolean p_restartPossible = true;

    /**
     * Any reporting engine. <BR/>
     */
    public IReportingEngine reportingEngine = null;


    /**************************************************************************
     * Create a new instance representing the Information about the Application. <BR/>
     * The <A HREF="#trans">trans</A> (transaction Array) and the
     * <A HREF="#variables">variables</A> (variable Array) is set to <CODE>null</CODE>.
     * The <A HREF="#lastid">lastid</A> counter is set to <CODE>0</CODE>.
     *
     * @param   basePath    The base path of the application.
     *
     * @throws  ApplicationInitializationException
     *          An error occurred during the initialization.
     */
    public ApplicationInfo (String basePath)
        throws ApplicationInitializationException
    {
        // nothing to do
        // specialized objects like cache, etc. have to be initialized outside
        // of this class to avoid dependencies
        try
        {
            this.p_system = new System (basePath);
        } // try
        catch (ListException e)
        {
            throw new ApplicationInitializationException (e);
        } // catch
    } // ApplicationInfo


    /**************************************************************************
     * Inserts an 'old' Sessioninfo object into a hashtable for later use
     * of its data. <BR/>
     *
     * @param   key     The key to identify it.
     * @param   sess    The Sessioninfo object.
     *
     * @return  <CODE>0</CODE> if inserting was successful. <BR/>
     *          <CODE>1</CODE> if inserting failed.
     */
    public int insertSessioninfoTable (String key, SessionInfo sess)
    {
        // store the sessioninfo-object given into a hashtable
        Object val = this.sessioninfoTable.put (key, sess);

        if (val == null)                  // saving was not successfull
        {
            return -1;
        } // if saving was not successfull

        // saving was successfull
        return 0;
    } // insertSessioninfoTable


    /**************************************************************************
     * Retrieves an 'old' Sessioninfo object from a hashtable with the key
     * given. <BR/>
     *
     * @param   key     the key to identify the entry.
     *
     * @return  The Sessioninfo object if there was one, or
     *          <CODE>null</CODE> if no Sessioninfo object was found
     */
    public SessionInfo getSessioninfoTable (String key)
    {
        // find the sessioninfo-object with the key given in the hashtable
        Object val = this.sessioninfoTable.get (key);

        if (val == null)                  // no sessioninfo-object found
        {
            return null;
        } // if no sessioninfo-object found

        // sessioninfo-object found
        return (SessionInfo) val;
    } // getSessioninfoTable


    /**************************************************************************
     * Deletes an 'old' Sessioninfo object from a hashtable with the key
     * given. <BR/>
     *
     * @param   key     The key to identify the entry.
     *
     * @return  <CODE>0</CODE> deleting was successful,
     *          <CODE>1</CODE> deleting was not successful
     */
    public int deleteSessioninfoTable (String key)
    {
        // deletes the sessioninfo-object with the key given
        // from the hashtable
        Object val = this.sessioninfoTable.remove (key);

        if (val == null)                // deleting was not successful
        {
            return -1;
        } // if deleting was not successful

        // deleting was successful
        return 0;
    } // deleteSessioninfoTable


    /**************************************************************************
     * Get the reporting engine. <BR/>
     *
     * @return the reportingEngine
     */
    public IReportingEngine getReportingEngine ()
    {
        return this.reportingEngine;
    } // getReportingEngine


    /**************************************************************************
     * Set the reporting Engine. <BR/>
     *
     * @param reportingEngine the reportingEngine to set
     */
    public void setReportingEngine (IReportingEngine reportingEngine)
    {
        this.reportingEngine = reportingEngine;
    } // setReportingEngine


    /**************************************************************************
     * Reset the reporting Engine. <BR/>
     */
    public void resetReportingEngine ()
    {
        this.reportingEngine = null;
    } // resetReportingEngine


    /**************************************************************************
     * Check if a reporting engine is present. <BR/>
     *
     * @return <code>true</code> is a reporting engine is present or
     *         <code>false</code> otherwise
     */
    public boolean hasReportingEngine ()
    {
        return this.reportingEngine != null;
    } // hasReportingEngine


    /***************************************************************************
     * Finalize the application info. <BR/> Called by the garbage collector when
     * there are no more references to an instance of this class. <BR/>
     * This method just stops the TracerManager and calls the common finalizer.
     *
     * @throws  Throwable the <code>Exception</code> raised by this method.
     */
    protected void finalize () throws Throwable
    {
        // stop the tracer manager:
        TracerManager.stop ();

        // call common method:
        super.finalize ();
    } // finalize

} // class ApplicationInfo
