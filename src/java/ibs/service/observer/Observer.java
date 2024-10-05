/*
 * Created by IntelliJ IDEA.
 * User: Horsti
 * Date: 10.07.2002
 * Time: 12:53:47
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */

// package:
package ibs.service.observer;

// imports:
import ibs.service.email.EMail;
import ibs.service.email.EMailManager;
import ibs.util.Helpers;
import ibs.util.file.FileHelpers;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;


/******************************************************************************
 * An Observer is a thread, that periodically checks its ObserverJobs and
 * starts their execution if their next cycle is due.
 *
 * @version     $Id: Observer.java,v 1.11 2012/09/18 14:47:50 btatzmann Exp $
 *
 * @author      HORST PICHLER, 10.07.2002
 ******************************************************************************
 */
public class Observer extends Thread
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Observer.java,v 1.11 2012/09/18 14:47:50 btatzmann Exp $";


    /**
     * Holds the configuration for this observer.
     */
    private ObserverConfiguration p_config = null;
    /**
     * Holds context-data for this observer. Needed to create ObserverJobs.
     */
    private ObserverContext p_context = null;
    /**
     * Holds ordered set of ObserverJobs, which shall be executed.
     */
    private TreeSet<ObserverJobData> p_jobDataQueue = null;
    /**
     * Indicates if thread runs in test-mode. If this thread runs in
     * testmode the main-loop will be executed p_testCycle times
     */
    private boolean p_testMode = false;
    /**
     * Defines the number of testcycles for this observer. Will
     * only be needed in testMode.
     */
    private int p_testCycles = ObserverConstants.UNDEFINED_INTEGER;
    /**
     * Indicates if observer should stop - set from outside!
     */
    protected boolean p_shouldIStop = false;
    /**
     * Indicates if thread runs in trace-mode: some information
     * (start, stop, job-execution, ...) will be printed to system.out.
     */
    private boolean p_trace = false;
    /**
     * Indicates if thread runs in debug-mode: a lot of information
     * (config, job-details, execution-details, ...) will be printed to system.out.
     */
    private boolean p_debug = false;
    /**
     * Indicates if thread runs in log-mode: a lot of information
     * (config, job-details, execution-details, ...) will be printed to
     * configured log-file.
     */
    private boolean p_log = false;
    /**
     * Indicates if error-notification is enabled.
     */
    private boolean p_notify = false;
    /**
     * Name of the log file.
     */
    private String p_logfilename = null;

    /**
     * Name of uninitialized observer. <BR/>
     */
    private static final String NAME_NOTINIT = "uninitialized";

    /**
     * Error prefix. <BR/>
     */
    private static final String ERRP = "* Error: ";

    /**
     * Log message: something is pending. <BR/>
     */
    private static final String LOGM_PENDING = " ... ";


    /**************************************************************************
     * Constructor for an Observer object. <BR/>
     */
    public Observer ()
    {
        this.setName (Observer.NAME_NOTINIT);
        this.log ("Creation with constructor Observer ().", true);
    } // Observer


    /**************************************************************************
     * Constructor for an Observer object. A value for testCycles > 0
     * indicates that thread runs in test-mode. In testmode main-loop will
     * be executed only testCycles times (not eternally).
     *
     * @param   config      The configuration object.
     * @param   testCycles  The number of test cycles.
     */
    protected Observer (ObserverConfiguration config, int testCycles)
    {
        if (testCycles > 0)
        {
            this.p_testMode = true;
            this.p_testCycles = testCycles;
        } // if
        this.init (config);
        this.log ("Creation with constructor Observer (config,testCycles.)", true);
        this.log ("* testCycles=" + testCycles, true);
    } // Observer


    /**************************************************************************
     * Initializer for an Observer object. <BR/>
     *
     * @param   config      The configuration object.
     */
    protected final void init (ObserverConfiguration config)
    {
        String n = null;
        this.setName (Observer.NAME_NOTINIT);

        // get and set some configuration values
        this.p_config = config;
        if (this.p_config != null)
        {
            // name of observer
            n = this.p_config.getName ();
            if (n != null)
            {
                this.setName (n);
            } // if

            // echo enabled?
            String e = this.p_config.getEcho ();
            if (e != null)
            {
                if (e.equals (ObserverConfiguration.ECHO_TRACE))
                {
                    this.p_trace = true;
                    this.p_debug = false;
                } // if
                else if (e.equals (ObserverConfiguration.ECHO_DEBUG))
                {
                    this.p_trace = false;
                    this.p_debug = true;
                } // else if
            } // if

            // file logging enabled?
            if (this.p_config.getLog ())
            {
                if (this.p_config.getLogDir () != null)
                {
                    // create log-directory-name
                    this.p_logfilename = this.p_config.getLogDir () + File.separator +
                            this.getName ();
                    // create log-directory for this observer (if it does not exist)
                    if (FileHelpers.makeDir (this.p_logfilename, true))
                    {
                        // enable logging and set logfile
                        this.p_log = true;
                        this.p_logfilename += File.separator + new Date ().getTime () + ".log";
                    } // if
                } // if
            } // if

            // notification enabled?
            if (this.p_config.getNotify ())
            {
                // check if necessary values do exist
                if (this.p_config.getMailServer () != null &&
                    this.p_config.getMailSender () != null &&
                    this.p_config.getMailReceiver () != null)
                {
                    this.p_notify = true;
                } // if
            } // if
        } // if
        this.setDaemon (true);
        this.log ("Configuration with config=" + config.toString (), true);
    } // Observer


    /**************************************************************************
     * Gets configuration object.
     *
     * @return  The configuration object.
     */
    protected ObserverConfiguration getConfig ()
    {
        return this.p_config;
    } // getConfig


    /**************************************************************************
     * Sets number of testcycles for this Thread. <BR/>
     * Will only be needed in testMode.
     *
     * @param   cycles  The number of test cycles.
     */
    protected final void setTestCycle (int cycles)
    {
        this.p_testCycles = cycles;
    } // setTestCycle


    /**************************************************************************
     * Set if the observer should stop. <BR/>
     *
     * @param   shouldIStop <CODE>true</CODE> if the observer should stop,
     *                      <CODE>false</CODE> otherwise.
     */
    protected void setShouldIStop (boolean shouldIStop)
    {
        this.p_shouldIStop = shouldIStop;
    } // setShouldIStop


    /**************************************************************************
     * Threads run method. <BR/>
     */
    public final void run ()
    {
        this.log ("Starting observer '" + this.getName () + "'" +
            Observer.LOGM_PENDING, false);

        // check initialization
        if (this.p_config == null)
        {
            this.error ("Error during run: Configuration not initialized.", null);
        } // if

        // initialize
        try
        {
            this.p_context = this.p_config.createObserverContext ();
            this.log ("Context created.", true);

            this.checkAndCreateDataStructure ();
            this.log ("Datastructures checked.", true);

            this.adjustObserverJobData ();
            this.log ("Observerjobdata checked.", true);

        } // try
        catch (Exception e)
        {
            // hardcore error - try to notifiy administrator
            // log error
            this.error ("ERROR during observer startup. Observer '" + this.getName () + "' stopped. ", e);
            return;
        } // catch

        // run forever: get, test and execute the jobs
        this.log ("Run observer '" + this.getName () + "'" +
            Observer.LOGM_PENDING, false);
        while (true)
        {
            try
            {
                // check if this observer should finish execution
                if (this.shouldIStop ())
                {
                    return;
                } // if

                // load executable jobs
                this.loadObserverJobData ();

                // check if this observer should finish execution
                if (this.shouldIStop ())
                {
                    return;
                } // if

                // check if any jobs in queue: execute jobs which are due
                if (this.p_jobDataQueue.size () > 0)
                {
                    this.executeObserverJobs ();
                } // if

                // check if this observer should finish execution
                if (this.shouldIStop ())
                {
                    return;
                } // if

                // exit if thread runs in testmode and defined number of cycles reached
                if (this.p_testMode)
                {
                    if (--this.p_testCycles <= 0)
                    {
                        this.log ("Stopped Observer '" + this.getName () +
                                "' due)to start-mode with limited cycles).", false);
                        return;
                    } // if
                } // if

                // check if this observer should finish execution
                if (this.shouldIStop ())
                {
                    return;
                } // if

                //
                // wait for a defined time span
/*
                this.sleep (p_config.getRefresh ());
*/
                // ==> split refresh in timeslices of 10-seconds each
                // ==> this is necessary to avoid long waiting-time when signaling stop!
                int t = this.p_config.getRefresh ();
                int c = 1;
                // ignore refresh under 10 seconds
                if (t > 10000)
                {
                    c = t / 10000;
                } // if
                else
                {
                    c = 1;
                } // else

                // sleep; wake up every in 10seconds; check if 'stop' signaled; etc.
                for (int i = 0; i < c; i++)
                {
                    if (this.shouldIStop ())
                    {
                        return;
                    } // if
                    Thread.sleep (10000);
                } // for

            } // try
            catch (Exception e)
            {
                this.error ("ERROR in THREAD observer '" + this.getName () + "'.", e);
                return;
            } // catch
        } // while
    } // run


    /**************************************************************************
     * Checks the datastructure, if not exists: create it. <BR/>
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    private final void checkAndCreateDataStructure () throws ObserverException
    {
        // base structure
        ObserverJobData ojd = new ObserverJobData (this.p_context);
        try
        {
            ojd.checkStructure ();
        } // try
        catch (ObserverException e)
        {
            this.log ("Basedatastructures for observer '" + this.getName () +
                "' do not exist, try to create them" + Observer.LOGM_PENDING,
                false);
            // structure does not exist - try to create it
            ojd.createStructure ();
            this.log (Observer.LOGM_PENDING + "datastructures for observer '" +
                this.getName () + "' created.\n", false);
        } // catch

        // additional structures for configured observerjobs
        String name = null;

        for (Iterator<String> iter = this.p_config.getObserverJobClasses ().iterator ();
             iter.hasNext ();)
        {
            name = iter.next ();
            //  create observerjob-object of given type and set values
            try
            {
                if (name.indexOf ('.') < 0)
                {
                    name = this.getClass ().getPackage () + name;
                } // if
/*
                    "ibs.observer." + name;
*/
                name = name + "Data";
                @SuppressWarnings ("unchecked") // suppress compiler warning
                Class<? extends ObserverJobData> cl =
                    (Class<? extends ObserverJobData>) Class.forName (name);
                ojd = cl.newInstance ();
                ojd.setContext (this.p_context);
                ojd.checkAdditionalStructure ();
            } // catch
            catch (Exception e)
            {
                this.log ("Additional datastructures of '" + name +
                    "' for observer '" + this.getName () +
                    "' do not exist, try to create them" +
                    Observer.LOGM_PENDING, false);
                try
                {
                    // structure does not exist - try to create it
                    ojd.createAdditionalStructure ();
                    this.log (Observer.LOGM_PENDING +
                        "additional datastructures of '" + name +
                        "' for observer '" + this.getName () + "' created.\n",
                        false);
                } // try
                catch (ObserverException err1)
                {
                    this.error (
                        ".. could not creaet additional datastructures of '" +
                            name + "' for observer '" + this.getName () +
                            "'.\n", err1);
                    // proceed!
                    this.log ("\n", false);
                } // try
            } // catch
        } // for iter
    } // checkAndCreateDataStructure


    /**************************************************************************
     * Loads base data of all ObserverJobs, which are potentially executable.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    private final void loadObserverJobData () throws ObserverException
    {
        ObserverJobData ojd = new ObserverJobData (this.p_context);
        this.p_jobDataQueue = ojd.loadBaseDataOfExecuteableJobs ();
        this.log (this.p_jobDataQueue.size () + " job (s) loaded.", true);
        this.log ("job (s)=" + this.p_jobDataQueue.toString (), true);
    } // loadObserverJobData


    /**************************************************************************
     * On observer-startup only: checks job-list for jobs with state
     * ACTIVE. <BR/>
     * For every job with state 'ACTIVE' the following actions will be set,
     * according to the jobs retryOnError-behaviour: <BR/>
     * (a) retryOnError=true: log/error/notification and reset jobs state
     * to 'WAITING', job will be executed (again) on next observer-cycle. <BR/>
     * (b) retryOnError=false: log/error/notification and reset jobs state
     * to ' TERMINATED', job will not be executed on next observer-cycle.
     *
     * Note: state 'ACTIVE' means that the job is currently executing which
     * is impossible on observer startup.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    private final void adjustObserverJobData () throws ObserverException
    {
        // check initialization
        this.checkInitialization ("adjustObserverJobData");

        // load data of all jobs which are marked as 'ACTIVE'
        ObserverJobData ojd = new ObserverJobData (this.p_context);
        TreeSet<ObserverJobData> activeJobsData =
            new TreeSet<ObserverJobData> (ojd.loadBaseDataOfActiveJobs ());

        if (activeJobsData.size () == 0)
        {
            return;
        } // if

        this.log (activeJobsData.size () + " active job (s) loaded.", true);
        this.log ("active job (s)=" + activeJobsData.toString (), true);

        // loop through the active-job-queue
        String errorMsg = "State-refresh for 'active' jobs on observer startup:\n\n";
        ObserverJobData jobData = null;
        Iterator<ObserverJobData> it = activeJobsData.iterator ();
        while (it.hasNext ())
        {
            // get next (active) state
            jobData = it.next ();

            // reset state of job according to retryOnError-behaviour
            if (jobData.getRetryOnError ())
            {
                jobData.setState (ObserverJob.STATE_WAITING);
                errorMsg += "* changed jobs state to 'waiting': " + jobData.toString () + "\n\n";
            } // if
            else
            {
                jobData.setState (ObserverJob.STATE_TERMINATED);
                errorMsg += "* changed jobs state to 'terminated': " + jobData.toString () + "\n\n";
            } // if

            // save new state
            jobData.update ();

        } // while

        // indicate error
        this.error (errorMsg, null);
    } // checkObserverJobData

    /**************************************************************************
     * Loops through job queue and starts execution of jobs that are due. <BR/>
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    private final void executeObserverJobs () throws ObserverException
    {
        ObserverJobData jobData = null;
        ObserverJob job = null;
        Iterator<ObserverJobData> it = null;
        String errStr = "An error occurred during observer-job execution;\n\n";
        boolean erroroccurred = false;

        // check initialization
        this.checkInitialization ("executeObserverJobs");
        if (this.p_jobDataQueue == null)
        {
            throw new ObserverException ("Error during executeObserverJobs:" +
                " Jobqueue not initialized.");
        } // if

        // check if queue has any entries
        if (this.p_jobDataQueue.size () <= 0)
        {
            return;
        } // if

        // loop through the queue and call execution-routine
        // until nextDate of job is after current Date
        // (p_jobqueue sort order is based on nextExecution-Date)
        it = this.p_jobDataQueue.iterator ();
        while (it.hasNext ())
        {
            // get next
            jobData = it.next ();
            // check next data - only jobs with past next execution-time will be
            // executed
            if (jobData.getNextExecution ().before (new Date ()))
            {
                //  create observer job object of given type and set values
                try
                {
                    @SuppressWarnings ("unchecked") // suppress compiler warning
                    Class<? extends ObserverJob> cl = (Class<? extends ObserverJob>) Class
                        .forName (jobData.getClassName ());
                    job = cl.newInstance ();
                    job.setContext (this.p_context);
                    job.setJdata (jobData);

                    // execute job
                    this.executeObserverJob (job);

                    this.log ("Job executed successfully: name=" + jobData.getName () +
                            "id=" + jobData.getId () + ".", false);
                } // catch
                //
                // NOTE: Do not throw exceptions to next level, because this would
                //       prevent the execution of the remaining executable jobs in
                //       the list. Just report an error and proceed with next job.
                //
                catch (ObserverException e)
                {
//                    errStr += Observer.ERRP + e.toString () + "\n" +
                    errStr += Observer.ERRP +
                        Helpers.getStackTraceFromThrowable (e) + "\n" +
                        "==> job=" + jobData.toString () + "\n\n";
                    erroroccurred = true;
                } // catch
                catch (ClassNotFoundException e)
                {
                    errStr += Observer.ERRP + e.toString () + "\n" +
                        "==> job=" + jobData.toString () + "\n\n";
                    erroroccurred = true;
                } // catch
                catch (InstantiationException e)
                {
                    errStr += Observer.ERRP + e.toString () + "\n" +
                        "==> job=" + jobData.toString () + "\n\n";
                    erroroccurred = true;
                } // catch
                catch (IllegalAccessException e)
                {
                    errStr += Observer.ERRP + e.toString () + "\n" +
                        "==> job=" + jobData.toString () + "\n\n";
                    erroroccurred = true;
                } // catch
                catch (Exception e)
                {
                    errStr += Observer.ERRP + Helpers.getStackTraceFromThrowable (e) + "\n" +
                        "==> job=" + jobData.toString () + "\n\n";
                    erroroccurred = true;
                } // catch
            } // if
        } // while

        // check error-state
        if (erroroccurred)
        {
            // log error and notify: one message for all errors
            this.error (errStr, null);
        } // if
    } // executeObserverJobs


    /**************************************************************************
     * Executes given ObserverJob. <BR/>
     *
     * @param   job     The job to be executed.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected void executeObserverJob (ObserverJob job) throws ObserverException
    {
        this.log ("Executing job: id=" + job.getJdata ().getName () + ", " + job.getJdata ().getName () + ".", true);
        //
        // for m2Observer overwrite this method and implement the
        // call-interface to the webserver!
        //

        // refetch jobs data and execute
        job.refetch ();
        job.execute ();
    } // executeObserverJob


    /**************************************************************************
     * Checks if shouldIStop has been signaled and logs messages. <BR/>
     *
     * @return  <CODE>true</CODE> if the observer should stop,
     *          <CODE>false</CODE> otherwise.
     */
    protected final boolean shouldIStop ()
    {
        // check if this observer should finish execution
        if (this.p_shouldIStop)
        {
            this.log ("Stopped thread Observer (signaled from outside).", false);
            return true;
        } // if

        return false;
    } // shouldIStop


    /**************************************************************************
     * Prints given error-message to System.out, to logfile if logging is
     * enabled and also sends an email if notification is enabled.
     *
     * @param   msg     The message for the error.
     * @param   e       The chained exception.
     */
    protected void error (String msg, Exception e)
    {
        String errStr = "**** ERROR: " + msg;
        // add exception details
        if (e != null)
        {
            errStr += e.toString ();
        } // if

        // write error
        System.out.println (">>> " + new Date ().toString () + " " +
            this.getClass ().getName () + " [" + this.getName () + "]: " +
            errStr + "\n");

        // write to logfile?
        if (this.p_log)
        {
            this.logToFile (errStr);
        } // if

        // send notification
        if (this.p_notify)
        {
            this.notifyAdmin (errStr);
        } // if
    } // error


    /**************************************************************************
     * Prints and logs the given message. If parameter debug=true, then the
     * given message will only be printed if observer runs in debug. <BR/>
     *
     * @param   msg     The message to be logged.
     * @param   debug   <CODE>true</CODE> if in debugging mode,
     *                  <CODE>false</CODE> otherwise.
     */
    protected void log (String msg, boolean debug)
    {
        // check possible print-states for system.out
        if (this.p_debug  || (this.p_trace && !debug))
        {
            String d = new Date ().toString ();
            System.out.println (">>> " + d + " " + this.getClass ().getName () +
                " [" + this.getName () + "]: " + msg);
        } // if

        // check possible print-states for log-file
        if (this.p_log && ((this.p_debug && debug) || (!this.p_debug && !debug)))
        {
            this.logToFile (msg);
        } // if
    } // log


    /**************************************************************************
     * Logs the given message to file.
     *
     * @param   msg     The message to be logged.
     */
    protected void logToFile (String msg)
    {
        // file logging enabled?
        if (!this.p_log)
        {
            return;
        } // if

        try
        {
            String logMsg = ">>> " + new Date ().toString () + ": " + msg + "\n";
            FileWriter fw = new FileWriter (this.p_logfilename, true);
            fw.write (logMsg);
            fw.close ();
        } // try
        catch (Exception e)
        {
            String errMsg = ">>> " + new Date ().toString () + " " + this.getClass ().getName () +
                    " [" + this.getName () + "]: " + ": ERROR while writing to logfile, because: " +
                    e.toString () + ". Could not write message: " + msg;
            System.out.println (errMsg);
        } // catch
    } // logToFile


    /**************************************************************************
     * Sends email with given message to configured server/receiver.
     *
     * @param   msg     The message for the notification.
     */
    protected void notifyAdmin (String msg)
    {
        // notification enabled?
        if (!this.p_notify)
        {
            return;
        } // if

        try
        {
            // create the mail
            EMail mail = new EMail ();
            mail.setReceiver (this.p_config.getMailReceiver ());
            mail.setSender (this.p_config.getMailSender ());
            mail.setReceiver (this.p_config.getMailReceiver ());
            mail.setSubject (this.p_config.getMailSubject ());
            mail.setContent ("Message from observer '" + this.getName () + "'\n\n" + msg);
            EMailManager.sendMail (mail, this.p_config.getMailServer ());
        } // try
        catch (Exception e)
        {
            String errMsg = ">>> " + new Date ().toString () + " " + this.getClass ().getName () +
                    " [" + this.getName () + "]: " + ": ERROR while sending notification, because: " +
                    e.toString () + ". Could not send message: " + msg;
            System.out.println (errMsg);
        } // catch
    } // notifiyAdmin


    /**************************************************************************
     * Checks for given operation if needed objects are initialized . <BR/>
     *
     * @param   op      The operation to be checked.
     *
     * @throws  ObserverException
     *          Not all needed objects are initialized.
     */
    private final void checkInitialization (String op)
        throws ObserverException
    {
        if (this.p_config == null)
        {
            throw new ObserverException ("Error during " + op + ":" +
                " Configuration not initialized.");
        } // if

        if (this.p_context == null)
        {
            throw new ObserverException ("Error during " + op + ":" +
                " Context not initialized.");
        } // if
    } // checkInitialization

} // Observer
