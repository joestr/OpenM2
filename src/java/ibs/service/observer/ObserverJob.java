/*
 * Class: ObserverJob.java
 */

// package:
package ibs.service.observer;

// imports:
import ibs.BaseObject;
import ibs.service.observer.ObserverConstants;
import ibs.service.observer.ObserverContext;
import ibs.service.observer.ObserverException;
import ibs.service.observer.ObserverJobData;
import ibs.util.Helpers;

import java.util.Date;


/******************************************************************************
 * An ObserverJob defines the jobs, which shall run at defined times executing
 * defined actions. It provides the methods to register/unregister
 * and execute these jobs.
 *
 * @version     $Id: ObserverJob.java,v 1.6 2007/07/24 21:27:33 kreimueller Exp $
 *
 * @author      HORST PICHLER
 ******************************************************************************
 */
public abstract class ObserverJob extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ObserverJob.java,v 1.6 2007/07/24 21:27:33 kreimueller Exp $";


    //
    // ObserverJob State constants: possible states of an observer job.
    //
    /**
     * State: unknown or not defined.
     */
    public static final int STATE_UNDEFINED    = 0;
    /**
     * State: created/registered, but never executed (until now).
     */
    public static final int STATE_CREATED      = 1;
    /**
     * State: currently active.
     */
    public static final int STATE_ACTIVE       = 2;
    /**
     * State: executed at least once, waiting for next execution-cycle.
     */
    public static final int STATE_WAITING      = 3;
    /**
     * State: execution paused; no execution until state is changed
     */
    public static final int STATE_PAUSED        = 4;
    /**
     * State: 'finished' execution with error
     */
    public static final int STATE_ERROR        = 5;
    /**
     * State: terminated/canceled during execution (can occur after STATE_ACTIVE).
     */
    public static final int STATE_TERMINATED        = 6;
    /**
     * State: finished, after normal execution (s) without errors.
     */
    public static final int STATE_FINISHED     = 7;
    /**
     * State: unregistered.
     */
    public static final int STATE_UNREGISTERED    = 8;

    //
    // Persistent properties for observerjobs - stored in db.
    //
    /**
     * Context object that holds ObserverJob data.
     */
    protected ObserverJobData p_jdata = null;

    //
    // Non persistent properties.
    //
    /**
     * Context object that holds data about observer.
     */
    protected ObserverContext p_context = null;

    /**
     * The hash code. <BR/>
     */
    private int p_hashCode = Integer.MIN_VALUE;



    /**************************************************************************
     * Constructor for an ObserverJob object. <BR/>
     */
    public ObserverJob ()
    {
        // nothing to do
    } // ObserverJob


    /**************************************************************************
     * Constructor for an ObserverJob object. <BR/>
     * Should be followed by call to fetch-method.
     *
     * @param   context The context for initializing the ObserverJob.
     */
    public ObserverJob (ObserverContext context)
    {
        this.init (context);
    } // ObserverJob


    /**************************************************************************
     * Constructor for an ObserverJob object. <BR/>
     *
     * @param   context The context for initializing the ObserverJob.
     * @param   jdata   The job data for initializing the ObserverJob.
     */
    public ObserverJob (ObserverContext context, ObserverJobData jdata)
    {
        this.p_jdata = jdata;
        this.init (context);
    } // ObserverJob


    /**************************************************************************
     * Initialization-activities on ObserverJob creation. <BR/>
     *
     * @param   context The context for initializing the ObserverJob.
     */
    private void init (ObserverContext context)
    {
        // set observer-context for this job
        this.p_context = context;

// create the log object
// this.logObject = new Log_01 ();
// this.logObject.initObject (oid, user, env, sess, app);
// this.logObject.isDisplayLog = false;
// this.logObject.isWriteLog = false;
    } // init


    ///////////////////////////////////////////////////////////////////////////
    //
    // getters
    //
    //

    /**************************************************************************
     * This method ... <BR/>
     *
     * @return  The job data.
     */
    public final ObserverJobData getJdata ()
    {
        return this.p_jdata;
    } // getJdata


    /**************************************************************************
     * This method ... <BR/>
     *
     * @return  The context.
     */
    public final ObserverContext getContext ()
    {
        return this.p_context;
    } // getContext


    ///////////////////////////////////////////////////////////////////////////
    //
    // setters
    //
    //

    /**************************************************************************
     * This method ... <BR/>
     *
     * @param   jdata The job data to be set.
     */
    protected final void setJdata (ObserverJobData jdata)
    {
        this.p_jdata = jdata;
    } // setJdata


    /**************************************************************************
     * This method ... <BR/>
     *
     * @param   context   The context to be set.
     */
    protected final void setContext (ObserverContext context)
    {
        this.p_context = context;
    } // setContext


    ///////////////////////////////////////////////////////////////////////////
    //
    // "abstract" methods
    //
    //
    /**************************************************************************
     * Activities of ObserverJob. <BR/>
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected abstract void executeActions () throws ObserverException;
    {
        // whatever you want to do
    } // executeActions


    /**************************************************************************
     * Checks execute-conditions for this job.<BR> If this method returns
     * false the given job will not be executed.
     *
     * @return  <CODE>true</CODE>    if the executewhen-condition is true.
     *          <CODE>false</CODE>   if the executewhen-conition is false.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected abstract boolean evaluateExecuteWhen () throws ObserverException;


    /**************************************************************************
     * Checks register-conditions for this job.<BR> If this method returns
     * false the given job will not be registered.
     *
     * @return  <CODE>true</CODE>    if the registerwhen-condition is true.
     *          <CODE>false</CODE>   if the registerwhen-conition is false.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected abstract boolean evaluateRegisterWhen () throws ObserverException;


    /**************************************************************************
     * Checks unregister-conditions for this job.<BR> If this method returns
     * true the given job will be unregistered.
     *
     * @return  <CODE>true</CODE>    If the unregisterwhen-condition is true.
     *          <CODE>false</CODE>   If the unregisterwhen-conition is false.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected abstract boolean evaluateUnregisterWhen () throws ObserverException;


    /**************************************************************************
     * Calculates the first execution date of this ObserverJob. <BR/>
     *
     * @return  The date/time of the next execution.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected abstract Date calculateFirstExecution () throws ObserverException;
    {
        // nothing to do
    } // calculateFirstExecution


    /**************************************************************************
     * Calculates the first execution date of this ObserverJob. <BR/>
     *
     * @return  The date/time of the next execution.
     *          <CODE>null</CODE> means: no next execution - finish job.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected abstract Date calculateNextExecution () throws ObserverException;


    /**************************************************************************
     * Tells if jobs of this class shall retry execution on next
     * observer cycle if an error occurred. <BR> retryOnError=true avoids that
     * jobs can be set to STATE_ERROR. Will only be called on job-registration.
     *
     * @return  <CODE>true</CODE> to retry execution on next observer-cycle,
     *          <CODE>false</CODE> otherwise.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected boolean getInitialRetryOnError ()
        throws ObserverException
    {
        return false;
    } // retryOnError


    /**************************************************************************
     * Returns number of retries for job. Will only be used if retryOnError
     * yields 'true'.<BR> After an error during job-execution this value
     * will be reduced by one. Will only be called on job-registration.
     *
     * @return  Number of retry-attempts.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected int getInitialRetryAttempts ()
        throws ObserverException
    {
        return 0;
    } // getRetryAttempts



    ///////////////////////////////////////////////////////////////////////////
    //
    // interface methods
    //
    //
    /**************************************************************************
     * Load data for ObserverJob with given id. <BR/>
     *
     * @param   id      The id of the observer job to be fetched.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    public void fetch (int id) throws ObserverException
    {
        if (this.p_context == null)
        {
            throw new ObserverException ("Error during fetch ObserverJob." +
                " Context undefined. ");
        } // if

        // create job-data: class specified by className + "Data"-extension
        try
        {
            @SuppressWarnings ("unchecked") // suppress compiler warning
            Class<? extends ObserverJobData> cl =
                (Class<? extends ObserverJobData>) Class.forName (this.getClass ().getName () + "Data");
            this.p_jdata = cl.newInstance ();
            this.p_jdata.init (this.p_context);
        } // try
        catch (Exception e)
        {
            throw new ObserverException ("Error during fetch ObserverJob: " + e.toString ());
        } // catch

        this.p_jdata.load (id);
    } // load


    /**************************************************************************
     * Reload data for this ObserverJob. <BR/>
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected void refetch () throws ObserverException
    {
        // check pre-conditions
        this.checkInitialization ("refetch");
        if (this.p_jdata.getId () <= 0)
        {
            throw new ObserverException ("Error during refetching of ObserverJob: " +
                "Id must be given." +
                " data=" + this.getJdata ().toString ());
        } // if

        // call method to load base data
        this.p_jdata.load (this.p_jdata.getId ());
    } // load


    /**************************************************************************
     * Checks for existence of job with given id. No data will be set if
     * job exists. <BR/>
     *
     * @param   id      The if of the observer job.
     *
     * @return  <CODE>true</CODE>  if the job exists,
     *          <CODE>false</CODE> if the job does not exist.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected boolean exists (int id) throws ObserverException
    {
        if (this.p_context == null)
        {
            throw new ObserverException ("Error during exists ObserverJob." +
                " Context undefined. ");
        } // if

        // create job-data: class specified by className + "Data"-extension
        try
        {
            @SuppressWarnings ("unchecked") // suppress compiler warning
            Class<? extends ObserverJobData> cl =
                (Class<? extends ObserverJobData>) Class.forName (this.getClass ().getName () + "Data");
            this.p_jdata = cl.newInstance ();
            this.p_jdata.init (this.p_context);
        } // try
        catch (Exception e)
        {
            throw new ObserverException ("Error during fetch ObserverJob: " + e.toString ());
        } // catch

        return this.p_jdata.exists (id);
    } // exists


    /**************************************************************************
     * Checks for existence of job. Unique Job is determined by given
     * data-object. No data will be set if job exists. <BR/>
     *
     * @param   jdata   The job data to be checked.
     *
     * @return  <CODE>>0</CODE> unique job-id,
     *          <CODE>0</CODE>  no job found.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected int exists (ObserverJobData jdata) throws ObserverException
    {
        if (this.p_context == null)
        {
            throw new ObserverException ("Error during exists ObserverJob." +
                " Context undefined. ");
        } // if
        if (jdata == null)
        {
            throw new ObserverException ("Error during exists ObserverJob." +
                " ObserverJobData-object undefined. ");
        } // if

        int id = -1;

        // check if unique job exists
        id = jdata.determineUniqueIdNonFinished ();
        if (id < 0)
        {
            throw new ObserverException ("m2ObserverService.registerObserverJob: " +
                    "ObserverJob with given data is not-unique (constraint violated). Could not unregister" +
                    ". data=" + jdata.toString ());
        } // if

        return id;
    } // exists


    /**************************************************************************
     * Creates and registers (makes persistent) an ObserverJob. <BR/>
     *
     * @param   context Object holding the Observers context data.
     * @param   jdata   Object holding the ObserverJobs data.
     *
     * @return  Unique id of registered ObserverJob.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected final int register (ObserverContext context, ObserverJobData jdata)
        throws ObserverException
    {
        this.setContext (context);
        return this.register (jdata);
    } // register


    /**************************************************************************
     * Creates and registers (makes persistent) an ObserverJob. <BR/>
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
        // set jdata
        this.setJdata (jdata);

        // check pre-conditions
        this.checkInitialization ("register");
        if (this.p_jdata.getId () > 0)
        {
            throw new ObserverException (
                "Error during register ObserverJob: " +
                    "Already set id is not allowed in data-object - it will be created. " +
                    "Use another Constructor; e.g. ObserverJobData (context,name). " +
                    "data=" + this.getJdata ().toString ());
        } // if
        if (this.p_jdata.getName () == null ||
            this.p_jdata.getName ().length () == 0)
        {
            throw new ObserverException ("Error during register ObserverJob: " +
                "Name must be given in data-object. " +
                "data=" + this.getJdata ().toString ());
        } // if

        // check if additional structure exists: create if not!
        // base structure MUST exist (created on observer.start)
        try
        {
            this.p_jdata.checkAdditionalStructure ();
        } // try
        catch (ObserverException e)
        {
            // structure does not exist - try to create it
            this.p_jdata.createAdditionalStructure ();
//
// log-entry
//
        } // catch

        //
        // set jobs base-data
        //
        // create rest of data
        this.p_jdata.setClassName (this.getClass ().getName ());
        this.p_jdata.setCreated (new Date ());
        // calculate and set date for first execution
        this.p_jdata.setNextExecution (this.calculateFirstExecution ());
        this.p_jdata.setPrevExecution (null);
        this.p_jdata.setExecutionCount (0);
        this.p_jdata.setState (ObserverJob.STATE_CREATED);
        this.p_jdata.setRetryOnError (this.getInitialRetryOnError ());
        this.p_jdata.setRetryAttempts (this.getInitialRetryAttempts ());

        // check if registration should be performed
        if (!this.evaluateRegisterWhen ())
        {
            throw new ObserverException ("Could not register ObserverJob, because register-condition returned 'false'." +
                " data=" + this.p_jdata.toString ());
        } // if

        this.p_jdata.create ();
        return this.p_jdata.getId ();
    } // register


    /**************************************************************************
     * Unregisters an ObserverJob. <BR/>
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected final void unregister ()
        throws ObserverException
    {
        // check pre-conditions
        this.checkInitialization ("unregister");
        if (this.p_jdata.getId () <= 0)
        {
            throw new ObserverException ("Error during unregistration of ObserverJob: " +
                "Id must be given." +
                " data=" + this.getJdata ().toString ());
        } // if

        //
        // update jobs base-data
        //
        this.p_jdata.setNextExecution (null);
        this.p_jdata.setState (ObserverJob.STATE_UNREGISTERED);

        this.p_jdata.update ();
    } // register


    /**************************************************************************
     * Check all conditions and execute the actions of this ObserverJob. <BR/>
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected final void execute () throws ObserverException
    {
// this.log.open ();
        // check pre-conditions
        this.checkInitialization ("execute");
        if (this.p_jdata.getClass () == null ||
            this.p_jdata.getId () <= ObserverConstants.UNDEFINED_INTEGER ||
            this.p_jdata.getName () == null ||
            this.p_jdata.getCreated () == null ||
            this.p_jdata.getState () == ObserverJob.STATE_UNDEFINED ||
            this.p_jdata.getExecutionCount () <= ObserverConstants.UNDEFINED_INTEGER)
        {
            throw new ObserverException ("Error while executing ObserverJob, data not complete.");
        } // if

        // check if jobs state is valid (for execution)
        // ObserverJob.undefined, active, created, error, waiting, finished,
        int st = this.p_jdata.getState ();
        if (st != ObserverJob.STATE_CREATED && st != ObserverJob.STATE_WAITING)
        {
            throw new ObserverException ("Error during execution of ObserverJob." +
                "State of job must be 'created' or 'waiting'.");
        } // if

        //
        // check if job shall be unregistered
        //
        if (this.evaluateUnregisterWhen ())
        {
            this.unregister ();
            return;
// logging notwendig!
/*
==> exception nicht sinnvoll: stille intelligenz besser!
            throw new ObserverException ("Could not execute ObserverJob, because unregister-condition returned 'true'." +
                " Job has been unregistered before execution." +
                " data=" + this.p_jdata.toString ());
*/
        } // if

        // check additional execution-conditions
        if (!this.evaluateExecuteWhen ())
        {
            return;
// logging notwendig!
/*
==> exception nicht sinnvoll: stille intelligenz besser!
            throw new ObserverException ("Could not execute ObserverJob, because execute-condition returned 'false'." +
                " Job will probably be executed on next observer-cycle." +
                " data=" + this.p_jdata.toString ());
*/
        } // if

        // save state/date of job - indicate that it currently executes
        try
        {
            this.p_jdata.setState (ObserverJob.STATE_ACTIVE);
            this.p_jdata.setPrevExecution (new Date ());
            this.p_jdata.update ();
        } // try
        catch (ObserverException e)
        {
            throw new ObserverException (
                "Could not set ObserverJobs state to 'active'." + "\nerror: " +
                    e.getMessage ());
        } // catch

        // execute actions of job
        try
        {
            this.executeActions ();
        } // try
        catch (Exception e)
        {
            // error occurred
            // set state/date according to retryOnError-behaviour and number
            // of remaining retryAttempts
            if (this.p_jdata.getRetryOnError () && (this.p_jdata.getRetryAttempts () > 0))
            {
                // retryOnError=true:
                // - job will be executed againg on next observer-cycle
                // --> set state to waiting
                // --> decrease number of retry-attempts
                this.p_jdata.setState (ObserverJob.STATE_WAITING);
                this.p_jdata.setRetryAttempts (this.p_jdata.getRetryAttempts () - 1);
            } // if
            else
            {
                // retryOnError=false:
                // - job will not be executed on next observer-cycle
                // --> set state to error
                // --> set datetime of this execution-attempt to 'now'
                this.p_jdata.setState (ObserverJob.STATE_ERROR);
            } // else

            // save jobs data
            try
            {
                this.p_jdata.update ();
            } // try
            catch (Exception err)
            {
                throw new ObserverException ("Could not update ObserverJobs-data while handling error in action-sequence." +
                    "\nexecution error: " + e.getMessage () +
                    "\nupdate error: " + err.getMessage ());
            } // catch

            throw new ObserverException (
                "Could not execute ObserverJob, due to error in action-sequence." +
                "\nerror: " + e.getMessage ());
        } // catch

        // execution finished without errors: update state, dates, ...
        try
        {
            // check date+time of next execution
            this.p_jdata.setNextExecution (this.calculateNextExecution ());
            // set state: depends if there is a next execution.
            if (this.p_jdata.getNextExecution () != null)
            {
                this.p_jdata.setState (ObserverJob.STATE_WAITING);
            } // if
            else
            {
                this.p_jdata.setState (ObserverJob.STATE_FINISHED);
            } // else
            // increment execution-counter
            this.p_jdata.setExecutionCount (this.p_jdata.getExecutionCount () + 1);
        } // try
        catch (Exception e)
        {
            throw new ObserverException ("Could not set ObserverJobs-data after execution of ObserverJob." +
                "\nerror: " + e.getMessage ());
        } // catch

        // save jobs data
        try
        {
            this.p_jdata.update ();
        } // try
        catch (Exception e)
        {
            throw new ObserverException ("Could not update ObserverJobs-data after execution of ObserverJob." +
                "\nerror: " + e.getMessage ());
        } // catch
    } // execute


    /**************************************************************************
     * Checks for given operation if needed objects are initialized . <BR/>
     *
     * @param   op      The operation.
     * @throws  ObserverException
     *          An error occurred.
     */
    private final void checkInitialization (String op)
        throws ObserverException
    {
        if (this.p_context == null)
        {
            throw new ObserverException ("Error during " + op + " ObserverJob." +
                "Context undefined. ");
        } // if
        if (this.p_jdata == null)
        {
            throw new ObserverException ("Error during " + op + " ObserverJob." +
                "Data-object undefined.");
        } // if
        if (!this.p_context.equals (this.p_jdata.getContext ()))
        {
            throw new ObserverException ("Error during " + op + " ObserverJob." +
                "Context mismatch between object and data-object." +
                " data=" + this.getJdata ().toString ());
        } // if
    } // checkInitialization


    /**************************************************************************
     * Returns a string representation of the object. <BR/>
     *
     * @return  a string representation of the object.
     */
    public String toString ()
    {
        return "[p_context=" + this.p_context + "; p_jdata=" + this.p_jdata + "]";
    } // toString


    /**************************************************************************
     * Returns true if given object equals this object. <BR/>
     * Tests: p_context, p_jdata
     *
     * @param   obj     The object to be checked for euqality.
     *
     * @return  <CODE>true</CODE> if the objects are equal,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean equals (ObserverJob obj)
    {
        boolean bContext = true;
        boolean bJData = true;

        // check null value
        if (obj == null)
        {
            return false;
        } // if
        // check class equality
        if (!this.getClass ().getName ().equals (obj.getClass ().getName ()))
        {
            return false;
        } // if

        // test equality of sub objects (including null-pointer tests)
        if (this.p_context == null || obj.getContext () == null)
        {
            bContext = this.p_context == obj.getContext ();
        } // if
        else
        {
            bContext = this.p_context.equals (obj.getContext ());
        } // else

        if (this.p_jdata == null || obj.getJdata () == null)
        {
            bJData = this.p_jdata == obj.getJdata ();
        } // if
        else
        {
            bJData = this.p_jdata.equals (obj.getJdata ());
        } // else

        // check property-equality
        if (bContext && bJData)
        {
            return true;
        } // if

        return false;
    } // equals


    /**************************************************************************
     * Returns a hash code value for the object. <BR/>
     *
     * @return  A hash code value for this object.
     */
    public int hashCode ()
    {
        // check if a valid hash code was set:
        if (this.p_hashCode == Integer.MIN_VALUE)
        {
            // compute hash code out of context hash code and observer job
            // hash code:
            this.p_hashCode = Helpers.sumHashCodes (this.p_context.hashCode (),
                this.p_jdata.hashCode ());
        } // if

        // return the result:
        return this.p_hashCode;
    } // hashCode

} // ObserverJob
