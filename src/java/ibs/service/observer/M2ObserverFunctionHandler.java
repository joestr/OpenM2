/*
 * Class: mM2ObserverFunctionHandler.java
 */

// package:
package ibs.service.observer;

// imports:
import ibs.bo.BusinessObject;
import ibs.bo.OID;
import ibs.io.IOHelpers;
import ibs.obj.func.IbsFunction;
import ibs.service.observer.M2ObserverArguments;
import ibs.service.observer.M2ObserverEvents;
import ibs.service.observer.M2ObserverJobRepresentation;
import ibs.service.observer.M2ObserverService;
import ibs.service.observer.M2ParameterObserverJob;
import ibs.service.observer.M2ParameterObserverJobData;
import ibs.service.observer.M2ReminderObserverJob;
import ibs.service.observer.ObserverConstants;
import ibs.service.observer.ObserverContext;
import ibs.service.observer.ObserverException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


/******************************************************************************
 * This class.... <BR/>
 *
 * @version     $Id: M2ObserverFunctionHandler.java,v 1.1 2007/07/24 21:28:12 kreimueller Exp $
 *
 * @author      hpichler
 ******************************************************************************
 */
public class M2ObserverFunctionHandler extends IbsFunction
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: M2ObserverFunctionHandler.java,v 1.1 2007/07/24 21:28:12 kreimueller Exp $";


    /**
     * Object to perform observer-operations. <BR/>
     */
    M2ObserverService observerService = null;


    /**************************************************************************
     * This constructor creates a new instance of the class IbsFunction.
     * <BR/>
     */
    public M2ObserverFunctionHandler ()
    {
        // init specifics of actual class:
        this.initClassSpecifics ();
    } // m2ObserverFunctionHandler


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // nothing to do
    } // initClassSpecifics


    ///////////////// control flow
    //
    //

    /**************************************************************************
     * mainmethod = sequence control of this function. Read environment
     * parameters and call functions. <BR/>
     *
     * @throws  Exception
     *          An error occurred.
     */
    public void handleEvent () throws Exception
    {
        // get current event
        int event = this.getEvent ();

        // check which event was requested
        switch (event)
        {
            case M2ObserverEvents.EVT_EXECOBSERVERJOB:  // execute given job
                // get the params
                String obs = this.env.getStringParam (M2ObserverArguments.ARG_OBS);
                int jobId = this.env.getIntParam (M2ObserverArguments.ARG_JOBID);

                // generate observer objects and initiate execution
                M2ObserverService os = new M2ObserverService (this.user, this.env, this.sess, this.app);
                os.executeObserverJob (obs, jobId);

                break;

            case M2ObserverEvents.EVT_GUISHOWPARAMETERJOB:          // shows parameter-job
            case M2ObserverEvents.EVT_GUISHOWPARAMETERJOBFORM:      // shows registration form for parameter-job
            case M2ObserverEvents.EVT_GUIREGISTERPARAMETERJOB:      // registers/changes given job
            case M2ObserverEvents.EVT_GUIUNREGISTERPARAMETERJOB:    // unregisters given job
                this.handleParameterJob (event);
                break;

            default:
                throw new ObserverException (this.getClass ().getName () +
                    ": Unknown event " + event);
        } // switch
    } // handleEvent


    /**************************************************************************
     * Handle parameter job event. <BR/>
     *
     * @param   event   The event to be handled.
     *
     * @throws  Exception
     *          An error occurred.
     */
    public void handleParameterJob (int event) throws Exception
    {
        // get parameters from environment
        OID paramOid1;
        OID paramOid2;
        String param1 = null;
        String param2 = null;
        String param3 = null;
        String param4 = null;
        String param5 = null;
        String param6 = null;
        String param7 = null;
        String param8 = null;
        String param9 = null;
        String param0 = null;
        String param0Duration = null;
        String param0Unit = null;
        Date   param0Date = null;

        String observer = this.env.getStringParam (M2ObserverArguments.ARG_OBS);
        int jobId = this.env.getIntParam (M2ObserverArguments.ARG_JOBID);
        String jobClass = this.env.getStringParam (M2ObserverArguments.ARG_JOBCLASS);
        OID oid = this.env.getOidParam ("oid");
        paramOid1 = oid; // must be equal to oid
        paramOid2 = this.env.getOidParam ("paramOid2");
        param0Date = this.env.getDateTimeParam ("param0Date");
        param0Duration = this.env.getStringParam ("param0Duration");
        param0Unit = this.env.getStringParam ("param0Unit");
        param1 = this.env.getStringParam ("param1");
        param2 = this.env.getStringParam ("param2");
        param3 = this.env.getStringParam ("param3");
        param4 = this.env.getStringParam ("param4");
        param5 = this.env.getStringParam ("param5");
        param6 = this.env.getStringParam ("param6");
        param7 = this.env.getStringParam ("param7");
        param8 = this.env.getStringParam ("param8");
        param9 = this.env.getStringParam ("param9");

        // check/set parameters
        if (oid == null || oid.isEmpty ())
            // 1st oid must be given
        {
            throw new ObserverException ("Missing URL-Parameter: oid1");
        } // if
        if (jobClass == null || jobClass.length () == 0)
            // job class must be given
        {
            throw new ObserverException ("Missing URL-Parameter: jclass");
        } // if
        if (observer == null || observer.length () == 0)
        {
            // set standard observer:
            observer = ObserverConstants.STANDARD_OBSERVER;
        } // if
        if (jobClass.indexOf ('.') < 0)
        {
            // add standard package:
            jobClass = "ibs.observer." + jobClass;
        } // if

        // init service - get observercontext
        M2ObserverService os = new M2ObserverService (this.user, this.env, this.sess, this.app);
        ObserverContext context = os.getObserverContext (observer);

        // create job instance: class specified by jobClass; initialize it
        @SuppressWarnings ("unchecked") // suppress compiler warning
        Class<? extends M2ParameterObserverJob> cl = (Class<? extends M2ParameterObserverJob>) Class.forName (jobClass);
        M2ParameterObserverJob job = cl.newInstance ();
        job.init (context, this.user, this.env, this.sess, this.app);

        // create jobdata instance: class specified by jobClass; initialize it
        @SuppressWarnings ("unchecked") // suppress compiler warning
        Class<? extends M2ParameterObserverJobData> c2 = (Class<? extends M2ParameterObserverJobData>) Class.forName (jobClass + "Data");
        M2ParameterObserverJobData jData = c2.newInstance ();

        // check if id is given
        if (jobId > 0)
        {
            // job exists - load by id
            job.fetch (jobId);   // load job

            // check if given oid equals parameter oid1
            BusinessObject bo1 = job.bo1;
            if (bo1 != null)
            {
                if (!bo1.oid.equals (oid))
                {
                    throw new ObserverException (
                        "Context object bo1 is not object specified in parameter: " +
                            oid.toString ());
                } // if
            } // if
            else
            {
                throw new ObserverException (
                    "Could not load context object bo1.");
            } // else
        } // if
        else
        {
            // set param1: current users name
            param1 = this.user.username;

            // set jdata to identify job
            jData.init (context, jobClass, "m2Reminder", paramOid1, paramOid2, param0,
                    param1, param2, param3, param4, param5, param6, param7, param8, param9);

            // check if job exists
            jobId = os.exists (observer, jData);

            if (jobId > 0)              // fetch data
            {
                job.fetch (jobId);
            } // if
            else                        // initialize job
            {
                job.setJdata (jData);
                job.loadm2Objects (jData);
            } // else
        } // else

        // create representation object
        @SuppressWarnings ("unchecked") // suppress compiler warning
        Class<? extends M2ObserverJobRepresentation> c3 =
            (Class<? extends M2ObserverJobRepresentation>)
            Class.forName (jobClass + "Representation");
        M2ObserverJobRepresentation repObj = c3.newInstance ();
        repObj.initObject (OID.getEmptyOid (), this.user, this.env, this.sess, this.app);
        repObj.job = job;

        // differ different actions
        if (event == M2ObserverEvents.EVT_GUISHOWPARAMETERJOB ||
                event == M2ObserverEvents.EVT_GUISHOWPARAMETERJOBFORM)
        {
            if (jobId > 0)
            {
                repObj.showInfo (0);
            } // if
            else
            {
                repObj.showChangeForm (0);
            } // else
        } // if
        else if (event == M2ObserverEvents.EVT_GUIREGISTERPARAMETERJOB)
        {
            // check if param0 is given as duration-expression -> convert
            if (param0Duration != null && param0Duration.length () > 0)
            {
                param0Date = M2ReminderObserverJob.convertDurationToDate (param0Duration + " " + param0Unit);
            } // if

            // check if param0 is given as date
            if (param0Date == null)
            {
                IOHelpers.showMessage ("Sie müssen das Erinnerungsdatum definieren!",
                                       this.app, this.sess, this.env);
                repObj.showChangeForm (0);
                return;
            } // if

            // convert param0Date to storable string
            SimpleDateFormat df = new SimpleDateFormat ("dd.MM.yyyy HH:mm");
            df.setTimeZone (TimeZone.getDefault ());
            param0 = df.format (param0Date);

            // check if job already existed: change-action ==> unregister old job
            if (jobId > 0)
            {
                os.unregisterObserverJob (observer, jobId);
            } // if

            // create jobdata instance: class specified by jobClass; initialize it
            @SuppressWarnings ("unchecked") // suppress compiler warning
            Class<M2ParameterObserverJobData> c4 = (Class<M2ParameterObserverJobData>) Class.forName (jobClass + "Data");
            jData = c4.newInstance ();
            jData.init (context, jobClass, "m2Reminder", paramOid1, paramOid2, param0,
                    param1, param2, param3, param4, param5, param6, param7, param8, param9);

            // register
            os.registerObserverJob (observer, jData);

            // show information
            repObj.job.setJdata (jData);
            repObj.showInfo (0);

        } // else if
        else if (event == M2ObserverEvents.EVT_GUIUNREGISTERPARAMETERJOB)
        {
            os.unregisterObserverJob (observer, jobId);

            // show object
            job.bo1.show (0);
        } // else if
    } // handleParameterJob

} // M2ObserverFunctionHandler
