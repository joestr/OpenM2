/*
 * Class: m2ObserverJob.java
 */

// package:
package ibs.service.observer;

// imports:
import ibs.bo.BusinessObject;
import ibs.bo.OID;
import ibs.io.Environment;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.service.observer.M2ObserverJobData;
import ibs.service.observer.M2ObserverObjectHandler;
import ibs.service.observer.ObserverContext;
import ibs.service.observer.ObserverException;
import ibs.service.observer.ObserverJob;
import ibs.service.observer.ObserverJobData;
import ibs.service.user.User;
import ibs.util.GeneralException;


/******************************************************************************
 * This class.... <BR/>
 *
 * @version     $Id: M2ObserverJob.java,v 1.1 2007/07/24 21:28:12 kreimueller Exp $
 *
 * @author      hpichler
 ******************************************************************************
 */
public abstract class M2ObserverJob extends ObserverJob
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: M2ObserverJob.java,v 1.1 2007/07/24 21:28:12 kreimueller Exp $";


    /**
     * Holds 1st m2-BusinessObject.
     */
    protected BusinessObject bo1 = null;
    /**
     * Holds 2nd m2-BusinessObject
     */
    protected BusinessObject bo2 = null;

    //
    // m2 environment
    //
    /**
     * Holds current m2-user.
     */
    protected User user = null;
    /**
     * Holds m2-environment.
     */
    protected Environment env = null;
    /**
     * Holds m2-Sessioninfo.
     */
    protected SessionInfo sess = null;
    /**
     * Holds m2-Applicationinfo app.
     */
    protected ApplicationInfo app = null;


    /**************************************************************************
     * Constructor for an m2ObserverJob object. <BR/>
     */
    public M2ObserverJob ()
    {
        // nothing to do
    } // m2ObserverJob


    /**************************************************************************
     * Constructor for an m2ObserverJob object.<BR> Should be followed by
     * call to fetch-method.
     *
     * @param   context The context of the observer job.
     */
    public M2ObserverJob (ObserverContext context)
    {
        // call constructor of super class:
        super (context);
    } // m2ObserverJob


    /**************************************************************************
     * Constructor for an m2ObserverJob object.<BR> Should be followed by
     * call to fetch-method.
     *
     * @param   context The observer context.
     * @param   user    The actual user.
     * @param   env     The actual environment.
     * @param   sess    The actual session object.
     * @param   app     The global application object.
     */
    public M2ObserverJob (ObserverContext context, User user, Environment env,
                         SessionInfo sess, ApplicationInfo app)
    {
        // call common constructor:
        this (context);

        // set m2 environment
        this.user = user;
        this.env = env;
        this.sess = sess;
        this.app = app;
    } // ObserverJob


    /**************************************************************************
     * Constructor for an m2ObserverJob object. <BR/>
     *
     * @param   context The context for initializing the ObserverJob.
     * @param   jdata   The job data for initializing the ObserverJob.
     */
    public M2ObserverJob (ObserverContext context, M2ObserverJobData jdata)
    {
        // call constructor of super class:
        super (context, jdata);
    } // ObserverJob


    /**************************************************************************
     * Initializes a m2ObserverJobObject.
     *
     * @param   context The observer context.
     * @param   user    The actual user.
     * @param   env     The actual environment.
     * @param   sess    The actual session object.
     * @param   app     The global application object.
     */
    public void init (ObserverContext context, User user, Environment env,
                      SessionInfo sess, ApplicationInfo app)
    {
        this.p_context = context;

        // set m2 environment
        this.user = user;
        this.env = env;
        this.sess = sess;
        this.app = app;
    } // init


    /**************************************************************************
     * Load data for ObserverJob with given id. <BR/>
     *
     * @param   id  The id of the observer job.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    public void fetch (int id) throws ObserverException
    {
        // call super implementation:
        super.fetch (id);

        this.loadm2Objects ((M2ObserverJobData) this.p_jdata);
    } // fetch


    /**************************************************************************
     * Register ObserverJob with given id.
     *
     * @param   jdata   Object holding the ObserverJobs data.
     *
     * @return  Unique id of registered ObserverJob.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected int register (ObserverJobData jdata)
        throws ObserverException
    {
        // check instance type
        if (!(jdata instanceof M2ObserverJobData))
        {
            throw new ObserverException ("m2ObserverJob.fetch: " +
                    " Classname of given data object is no instance of m2ObserverJobData." +
                    " data=" + jdata.toString ());
        } // if

        this.loadm2Objects ((M2ObserverJobData) jdata);
        return super.register (jdata);
    } // register


    /**************************************************************************
     * Loads m2 context objects.<BR> Objects will be loaded with internal
     * system user to avoid permission-problems.
     *
     * @param   jdata   The job data used for object loading.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected void loadm2Objects (M2ObserverJobData jdata)
        throws ObserverException
    {
        OID oid = null;

        try
        {
            // instantiate object handler
            M2ObserverObjectHandler handler =
                    new M2ObserverObjectHandler (this.user,  this.env,  this.sess,  this.app);

            // get internal m2 system user (=administrator in lack of system-user-concept)
            User systemUser = handler.getSystemUser ();

            // check if system user could be found
            if (systemUser == null)
            {
                throw new Exception ("System-user not found.");
            } // if

            // get 1st business object
            oid = jdata.getOid1 ();
            if (oid != null && !oid.isEmpty ())
            {
                this.bo1 = handler.fetchObject (oid, systemUser);
            } // if

            // get 2nd business object
            oid = jdata.getOid2 ();
            if (oid != null && !oid.isEmpty ())
            {
                this.bo2 = handler.fetchObject (oid, systemUser);
            } // if
        } // finally
        catch (GeneralException e)
        {
            ObserverException oex = new ObserverException ("Error while loading m2 context-objects: " + e.toString ());
            // chain the exception that caused the problem
            oex.initCause (e);
            throw oex;
        } // catch
        catch (Exception e)
        {
            ObserverException oex = new ObserverException ("Exception occurred while loading m2 context-objects: " + e.toString ());
            // chain the exception that caused the problem
            oex.initCause (e);
            throw oex;
        } // catch
    } // loadm2Objects

} // m2ObserverJob
