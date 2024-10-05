/*
 * Created by IntelliJ IDEA.
 * User: hpichler
 * Date: 22.08.2002
 * Time: 10:47:27
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */

// package:
package ibs.service.observer;

// imports:
import ibs.BaseObject;
import ibs.io.Environment;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.service.user.User;


/******************************************************************************
 * m2ObserverService provides the call-interface from m2-objects to the
 * observer (loader)-functionality.
 *
 * @version     $Id: M2ObserverService.java,v 1.1 2007/07/24 21:28:12 kreimueller Exp $
 *
 * @author      HORST PICHLER, 22.08.2002
 ******************************************************************************
 */
public class M2ObserverService extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: M2ObserverService.java,v 1.1 2007/07/24 21:28:12 kreimueller Exp $";


    //
    // m2 environment
    //
    /**
     * Holds current m2-user.
     */
    private User user = null;
    /**
     * Holds m2-environment.
     */
    private Environment env = null;
    /**
     * Holds m2-Sessioninfo.
     */
    private SessionInfo sess = null;
    /**
     * Holds m2-Applicationinfo app.
     */
    private ApplicationInfo app = null;


    /**************************************************************************
     * Constructor for the m2ObserverServiceObject. <BR/>
     *
     * @param   user    The actual user.
     * @param   env     The actual environment.
     * @param   sess    The actual session info object.
     * @param   app     The global application info.
     */
    public M2ObserverService (User user, Environment env,
                              SessionInfo sess, ApplicationInfo app)
    {
        // set m2 environment
        this.user = user;
        this.env = env;
        this.sess = sess;
        this.app = app;
    } // m2ObserverService


    /**************************************************************************
     * Returns observer-context object for observer with given name. <BR/>
     *
     * @param   observerName    The name of the observer for which to get the
     *                          context.
     *
     * @return  The observer context.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    public final synchronized ObserverContext getObserverContext (
                                                                  String observerName)
        throws ObserverException
    {
        return this.getObserverLoader ().getObserverContext (observerName);
    } // getObserverContext


    /**************************************************************************
     * Registers given ObserverJob for given Observer. Unique id
     * (for given observer) will be returned.<BR> Encapsulation-method
     * to hide underlying tiers.
     *
     * @param   observerName    The name of the observer for which the job
     *                          shall be registered.
     * @param   jdata           The observer job to be registered.
     *
     * @return  The id of the new observer job.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    public final synchronized int registerObserverJob (String observerName,
                                                       M2ObserverJobData jdata)
        throws ObserverException
    {
        this.checkInitialization ("registerObserverJob");
        if (observerName == null || observerName.length () == 0)
        {
            throw new ObserverException ("m2ObserverService.registerObserverJob: " +
                "Observer-name not initialized.");
        } // if
        if (jdata == null)
        {
            throw new ObserverException ("m2ObserverService.registerObserverJob: " +
                "ObserverJobData not initialized.");
        } // if

        M2ObserverJob oj = null;
        ObserverContext c = null;
        int id = 0;

        // get/set observer-context
        c = this.getObserverLoader ().getObserverContext (observerName);
        if (jdata.getContext () == null)
        {
            jdata.setContext (c);
        } // if

        // check if unique-constraint for observer-job will be violated
        id = jdata.determineUniqueIdNonFinished ();
        if (id != 0)
        {
            throw new ObserverException ("m2ObserverService.registerObserverJob: " +
                    "ObserverJob with given data is not unique - could not register." +
                    ". data=" + jdata.toString ());
        } // if

        // create observer job - according to class given in jdata
        try
        {
            @SuppressWarnings ("unchecked") // suppress compiler warning
            Class<? extends M2ObserverJob> cl =
                (Class<? extends M2ObserverJob>) Class.forName (jdata.getClassName ());
            oj = cl.newInstance ();
            oj.init (c, this.user, this.env, this.sess, this.app);
            id = oj.register (c, jdata);
        } // catch
        catch (ClassNotFoundException e)
        {
            throw new ObserverException ("m2ObserverService.registerObserverJob: " +
                    e.toString () +
                    ". data=" + jdata.toString ());
        } // catch
        catch (InstantiationException e)
        {
            throw new ObserverException ("m2ObserverService.registerObserverJob: " +
                    e.toString () + ". data=" + jdata.toString ());
        } // catch
        catch (IllegalAccessException e)
        {
            throw new ObserverException ("m2ObserverService.registerObserverJob: " +
                    e.toString () + ". data=" + jdata.toString ());
        } // catch

        return id;
    } // registerObserverJob


    /**************************************************************************
     * Unregisters ObserverJob for given Observer. Job will be determined by
     * data given in jdata. If id is not set the
     * methodObserverJobData.determineUniqueId () will be called. <BR/>
     * Already finished jobs will not be unregistered.
     *
     * @param   observerName    The name of the observer for which the job
     *                          shall be unregistered.
     * @param   jdata           The observer job to be unregistered.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    public final synchronized void unregisterObserverJob (
                                                          String observerName,
                                                          M2ObserverJobData jdata)
        throws ObserverException
    {
        this.checkInitialization ("unregisterObserverJob");
        if (observerName == null || observerName.length () == 0)
        {
            throw new ObserverException ("m2ObserverService.unregisterObserverJob:" +
                " Observer-name not initialized.");
        } // if
        if (jdata == null)
        {
            throw new ObserverException ("m2ObserverService.unregisterObserverJob:" +
                " ObserverJobData not initialized.");
        } // if

        ObserverContext c = null;
        int id = 0;

        // get/set observer-context and data
        c = this.getObserverLoader ().getObserverContext (observerName);
        if (jdata.getContext () == null)
        {
            jdata.setContext (c);
        } // if


        // check if id is set
        id = jdata.getId ();
        if (id < 0)
        {
            // check if unique-constraint for observer-job will be violated
            id = jdata.determineUniqueId ();
            if (id < 0)
            {
                throw new ObserverException ("m2ObserverService.registerObserverJob: " +
                        "ObserverJob with given data is not-unique (constraint violated). Could not unregister" +
                        ". data=" + jdata.toString ());
            } // if
        } // if

        // unregister observerjob - according to class given in jdata
        try
        {
            @SuppressWarnings ("unchecked") // suppress compiler warning
            Class<? extends M2ObserverJob> cl =
                (Class<? extends M2ObserverJob>) Class.forName (jdata.getClassName ());
            M2ObserverJob oj = cl.newInstance ();
            oj.init (c, this.user, this.env,  this.sess,  this.app);
            oj.fetch (id);
            // check state: only unregister if state <> finished
            if (oj.getJdata ().getState () != ObserverJob.STATE_FINISHED)
            {
                oj.unregister ();
            } // if
        } // catch
        catch (ClassNotFoundException e)
        {
            throw new ObserverException ("m2ObserverService.unregisterObserverJob: " +
                    e.toString () +
                    ". data=" + jdata.toString ());
        } // catch
        catch (InstantiationException e)
        {
            throw new ObserverException ("m2ObserverService.unregisterObserverJob: " +
                    e.toString () + ". data=" + jdata.toString ());
        } // catch
        catch (IllegalAccessException e)
        {
            throw new ObserverException ("m2ObserverService.unregisterObserverJob: " +
                    e.toString () + ". data=" + jdata.toString ());
        } // catch
    } // unregisterObserverJob


    /**************************************************************************
     * Unregisters ObserverJob for given Observer. Job will be determined by
     * id .<BR> Already finished jobs will not be unregistered.
     *
     * @param   observerName    The name of the observer for which the job
     *                          shall be registered.
     * @param   id              The id of the observer job to be registered.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    public final synchronized void unregisterObserverJob (String observerName,
                                                          int id)
        throws ObserverException
    {
        this.checkInitialization ("unregisterObserverJob");
        if (observerName == null || observerName.length () == 0)
        {
            throw new ObserverException ("m2ObserverService.unregisterObserverJob:" +
                " Observer-name not initialized.");
        } // if

        M2ObserverJobData jData = this.fetchObserverJobData (observerName, id);
        this.unregisterObserverJob (observerName, jData);
    } // unregisterObserverJob


    /**************************************************************************
     * Executes ObserverJob for given Observer. Job will be determined by given
     * id and observer context. <BR/>
     * Encapsulation-method to hide underlying tiers.
     *
     * @param   observerName    The name of the observer for which the job
     *                          shall be executed.
     * @param   id              The id of the job to be executed.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    public final synchronized void executeObserverJob (String observerName,
                                                       int id)
        throws ObserverException
    {
        this.checkInitialization ("executeObserverJob");
        M2ObserverJob oj = null;
        ObserverJobData jdata = null;
        ObserverContext c = null;

        // get observer-context
        c = this.getObserverLoader ().getObserverContext (observerName);

        // get jobs base data
        jdata = new ObserverJobData (c);
        jdata.load (id);

        // create m2ObserverJob - according to class given in jdata
        try
        {
            @SuppressWarnings ("unchecked") // suppress compiler warning
            Class<? extends M2ObserverJob> cl =
                (Class<? extends M2ObserverJob>) Class.forName (jdata.getClassName ());
            oj = cl.newInstance ();
            oj.init (c, this.user, this.env,  this.sess,  this.app);
        } // catch
        catch (ClassCastException e)
        {
            throw new ObserverException (
                "m2ObserverService.executeObserverJob: " +
                    "Could not create observer job. " +
                    " Classname of given job is no instance of m2ObserverJob." +
                    " " + e.toString () + "; id=" + id + "; context=" +
                    c.toString ());
        } // catch
        catch (ClassNotFoundException e)
        {
            throw new ObserverException ("m2ObserverService.executeObserverJob: " +
                "Could not create observer job. " +
                e.toString () + " id=" + id + "; context=" + c.toString ());
        } // catch
        catch (InstantiationException e)
        {
            throw new ObserverException ("m2ObserverService.executeObserverJob: " +
                "Could not create observer job. " +
                e.toString () + " id=" + id + "; context=" + c.toString ());
        } // catch
        catch (IllegalAccessException e)
        {
            throw new ObserverException ("m2ObserverService.executeObserverJob: " +
                    "Could not create observer job. " +
                    e.toString () + " id=" + id + "; context=" + c.toString ());
        } // catch


        // try to fetch the data neccessary to execute the job
        try
        {
//
//          u.u. HIER NUR oj.fetchAdditional -> spart Zugriffe
//
            oj.fetch (id);                       // load additional + basedata
        } // try
        catch (Exception e)
        {
            // check if there was a NoAccessException in the exception chain
            // in that case the job should not be terminated
            // because the observer just did not have access to the object
            Throwable cause = e;
            boolean isFound = false;
            while (!isFound && cause != null)
            {
                isFound = cause instanceof ibs.util.NoAccessException;
                cause = cause.getCause ();
            } // while (cause != null && (! isFound))
            // if there was no NoAccessException a real error
            // occurred while fetching the object
            // in that case terminate the observer job
            if (!isFound)
            {
                try
                {
                    // state of the job must be set terminated
                    oj.p_jdata.setState (ObserverJob.STATE_TERMINATED);
                    // save jobs data
                    oj.p_jdata.update ();
                } // try
                catch (Exception err)
                {
                    throw new ObserverException ("m2ObserverService.executeObserverJob: " +
                        "Could not update ObserverJobs-data while fetching observer job data. Job terminated." +
                        " execution error: " + e.getMessage () +
                        " update error: " + err.getMessage () +
                        " job data:" + oj.p_jdata.toString ());
                } // catch
            } // if (! isFound)
            throw new ObserverException ("m2ObserverService.executeObserverJob: " +
                "Could not fetch observer job data. Job terminated. " +
                e.toString () + " id=" + id + "; context=" + c.toString ());
        } // catch

        // now execute the job
        try
        {
            oj.execute ();
        } // try
        catch (Exception e)
        {
            throw new ObserverException ("m2ObserverService.executeObserverJob:" +
                "\nCould not execute observer job with id=" + id +
                "\n\r==> observer context: " + c.toString () +
                "\n\r==> job data: " + oj.p_jdata.toString () +
                "\n\rproblem report:\n" + e.toString ());
        } // catch (Exception e)

    } // executeObserverJob


    /**************************************************************************
     * Fetch unique ObserverJobs data based by id. <BR/>
     * Encapsulation-method to hide underlying tiers.
     *
     * @param   observerName    The name of the observer for which the job
     *                          data shall be fetched.
     * @param   id              The id of the job.
     *
     * @return  The observer job data.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    public final synchronized M2ObserverJobData fetchObserverJobData (
                                                                      String observerName,
                                                                      int id)
        throws ObserverException
    {
        this.checkInitialization ("fetchObserverJobData");
        M2ObserverJobData jdata = null;
        ObserverJobData jdata0 = null;
        ObserverContext c = null;

        // get observer-context
        c = this.getObserverLoader ().getObserverContext (observerName);

        // get jobs base data
        jdata0 = new ObserverJobData (c);
        jdata0.load (id);

        // create m2ObserverJob - according to class given in jdata
        try
        {
            @SuppressWarnings ("unchecked") // suppress compiler warning
            Class<? extends M2ObserverJobData> cl =
                (Class<? extends M2ObserverJobData>) Class.forName (jdata0.getClassName () + "Data");
            jdata = cl.newInstance ();
            jdata.init (c);
            jdata.load (id);                       // load additional + basedata
        } // catch
        catch (ClassCastException e)
        {
            throw new ObserverException ("m2ObserverService.fetchObserverJobData: " +
                    " Classname of given job is no instance of m2ObserverJob. " +
                    e.toString () + "; id=" + id + "; context=" + c.toString ());
        } // catch
        catch (ClassNotFoundException e)
        {
            throw new ObserverException ("m2ObserverService.fetchObserverJobData: " +
                    e.toString () + " id=" + id + "; context=" + c.toString ());
        } // catch
        catch (InstantiationException e)
        {
            throw new ObserverException ("m2ObserverService.fetchObserverJobData: " +
                    e.toString () + " id=" + id + "; context=" + c.toString ());
        } // catch
        catch (IllegalAccessException e)
        {
            throw new ObserverException ("m2ObserverService.fetchObserverJobData: " +
                    e.toString () + " id=" + id + "; context=" + c.toString ());
        } // catch

        return jdata;
    } // fetchObserverJobData


/*

==> not possible because job-class is unknown

    / **************************************************************************
     * Checks for existence of job with given id on given observer. <BR/>
     *
     * @returns true  ... job exists
     *          false ... job doesnt exist
     * /
    public final synchronized boolean exists (String observerName, int id) throws ObserverException
    {
        this.checkInitialization ("registerObserverJob");
        if (observerName == null || observerName.length () == 0)
            throw new ObserverException ("m2ObserverService.exists: " +
                "Observer-name not initialized.");
        if (id < 1)
            throw new ObserverException ("m2ObserverService.exists: " +
                "invalid id: " + id);

        Class cl = null;
        m2ObserverJob oj = null;
        ObserverContext c = null;

        c = this.getObserverLoader ().getObserverContext (observerName);
        m2ObserverJob job = new m2ObserverJob (c, this.user, this.env, this.sess, this.app);
        return job.exists (id);
    } // existsObserverJob
*/


    /**************************************************************************
     * Checks for existence of job. Unique Job is determined by given
     * data-object. No data will be set if job exists. <BR/>
     *
     * @param   observerName    The name of the observer for which the job
     *                          shall be checked.
     * @param   jdata           The observer job which shall be existent.
     *
     * @return  <CODE>>0</CODE> ... unique job-id
     *          <CODE>0</CODE>  ... no job found
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    public int exists (String observerName, ObserverJobData jdata)
        throws ObserverException
    {
        this.checkInitialization ("registerObserverJob");
        if (observerName == null || observerName.length () == 0)
        {
            throw new ObserverException ("m2ObserverService.exists: " +
                "Observer-name not initialized.");
        } // if
        if (jdata == null)
        {
            throw new ObserverException ("m2ObserverService.exists:" +
                " ObserverJobData not initialized.");
        } // if

        M2ObserverJob oj = null;
        ObserverContext c = null;

        // get/set observer-context
        c = this.getObserverLoader ().getObserverContext (observerName);

        // create observerjob - according to class given in jdata
        try
        {
            @SuppressWarnings ("unchecked") // suppress compiler warning
            Class<? extends M2ObserverJob> cl =
                (Class<? extends M2ObserverJob>) Class.forName (jdata.getClassName ());
            oj = cl.newInstance ();
            oj.init (c, this.user, this.env, this.sess, this.app);
        } // catch
        catch (ClassNotFoundException e)
        {
            throw new ObserverException ("m2ObserverService.exists: " +
                    e.toString () +
                    ". data=" + jdata.toString ());
        } // catch
        catch (InstantiationException e)
        {
            throw new ObserverException ("m2ObserverService.exists: " +
                    e.toString () + ". data=" + jdata.toString ());
        } // catch
        catch (IllegalAccessException e)
        {
            throw new ObserverException ("m2ObserverService.exists: " +
                    e.toString () + ". data=" + jdata.toString ());
        } // catch

        return oj.exists (jdata);
    } // existsObserverJob


    /**************************************************************************
     * Checks for given operation if needed objects are initialized . <BR/>
     *
     * @param   op      The operation to be checked.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    private final void checkInitialization (String op)
        throws ObserverException
    {
        if (this.user == null)
        {
            throw new ObserverException ("Error during m2ObserverService." + op + ": " +
                "User undefined. ");
        } // if
        if (this.env == null)
        {
            throw new ObserverException ("Error during m2ObserverService." + op + ": " +
                "Environment undefined. ");
        } // if
        if (this.sess == null)
        {
            throw new ObserverException ("Error during m2ObserverService." + op + ": " +
                "Session undefined. ");
        } // if
        if (this.app == null)
        {
            throw new ObserverException ("Error during m2ObserverService." + op + ": " +
                "Application undefined. ");
        } // if
        if (this.getObserverLoader () == null)
        {
            throw new ObserverException ("Error during m2ObserverService." + op + ": " +
                "ObserverLoader undefined. ");
        } // if
    } // checkInitialization


    /**************************************************************************
     * Get the observer loader. <BR/>
     *
     * @return  The observer loader.
     */
    protected ObserverLoader getObserverLoader ()
    {
        return (ObserverLoader) this.app.p_observerLoader;
    } // getObserverLoader

} // m2ObserverService
