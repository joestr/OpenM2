/*
 * Class: m2ParameterObserverJob.java
 */

// package:
package ibs.service.observer;

// imports:
import ibs.io.Environment;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.service.observer.M2ObserverJob;
import ibs.service.observer.M2ParameterObserverJobData;
import ibs.service.observer.ObserverContext;
import ibs.service.observer.ObserverException;
import ibs.service.observer.ObserverJobData;
import ibs.service.user.User;


/******************************************************************************
 * This class.... <BR/>
 *
 * @version     $Id: M2ParameterObserverJob.java,v 1.1 2007/07/24 21:28:12 kreimueller Exp $
 *
 * @author      hpichler
 ******************************************************************************
 */
public abstract class M2ParameterObserverJob extends M2ObserverJob
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: M2ParameterObserverJob.java,v 1.1 2007/07/24 21:28:12 kreimueller Exp $";


    /**************************************************************************
     * Constructor for an m2ParameterObserverJob object. <BR/>
     */
    public M2ParameterObserverJob ()
    {
        // nothing to do
    } // m2ParameterObserverJob


    /**************************************************************************
     * Constructor for an m2ParameterObserverJob object.<BR> Should be followed by
     * call to fetch-method.
     *
     * @param   context The context of the observer job.
     */
    public M2ParameterObserverJob (ObserverContext context)
    {
        // call constructor of super class:
        super (context);
    } // m2ParameterObserverJob


    /**************************************************************************
     * Constructor for an m2ParameterObserverJob object.<BR> Should be followed by
     * call to fetch-method.
     *
     * @param   context The observer context.
     * @param   user    The actual user.
     * @param   env     The actual environment.
     * @param   sess    The actual session object.
     * @param   app     The global application object.
     */
    public M2ParameterObserverJob (ObserverContext context, User user, Environment env,
                                   SessionInfo sess, ApplicationInfo app)
    {
        // call common constructor:
        this (context);

        // set m2 environment
        this.user = user;
        this.env = env;
        this.sess = sess;
        this.app = app;
    } // m2ParameterObserverJob


    /**************************************************************************
     * Constructor for an m2ParameterObserverJob object. <BR/>
     *
     * @param   context The context for initializing the ObserverJob.
     * @param   jdata   The job data for initializing the ObserverJob.
     */
    public M2ParameterObserverJob (ObserverContext context,
                                   M2ParameterObserverJobData jdata)
    {
        // call constructor of super class:
        super (context, jdata);
    } // m2ParameterObserverJob


    /**************************************************************************
     * Initializes m2ParameterObserverJob.
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
        if (!(jdata instanceof M2ParameterObserverJobData))
        {
            throw new ObserverException ("m2ParameterObserverJob.fetch: " +
                    " Classname of given data object is no instance of m2ParameterObserverJobData." +
                    " data=" + jdata.toString ());
        } // if

        // call super method:
        return super.register (jdata);
    } // register


    /**************************************************************************
     * Shows m2-GUI-Form for job-registration/change.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected void showForm ()
        throws ObserverException
    {
        throw new ObserverException ("m2ParameterObserverJob.showForm: Has to be implemented in subclasses.");
    } // showForm

} // m2ParameterObserverJob
