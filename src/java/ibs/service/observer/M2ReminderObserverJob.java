/*
 * Class: M2ReminderObserverJob.java
 */

// package:
package ibs.service.observer;

// imports:
import ibs.bo.BusinessObject;
import ibs.bo.IncorrectOidException;
import ibs.bo.OID;
import ibs.bo.States;
import ibs.io.Environment;
import ibs.io.IOHelpers;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.service.notification.INotificationService;
import ibs.service.notification.NotificationFailedException;
import ibs.service.notification.NotificationServiceFactory;
import ibs.service.notification.NotificationTemplate;
import ibs.service.user.User;
import ibs.tech.sql.DBConnector;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.util.Helpers;
import ibs.util.StringHelpers;
import ibs.util.UtilConstants;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;


/******************************************************************************
 * m2ReminderObserverJob starts a notification (or optional method) if
 * 'nextExecution' is reached. This class also holds methods to. <BR/>
 * <BR/>
 *
 * @version     $Id: M2ReminderObserverJob.java,v 1.3 2010/11/12 10:18:11 btatzmann Exp $
 *
 * @author      HORST PICHLER
 ******************************************************************************
 */
public class M2ReminderObserverJob extends M2ParameterObserverJob
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: M2ReminderObserverJob.java,v 1.3 2010/11/12 10:18:11 btatzmann Exp $";

    /**
     * Error message: Date value in parameter 0 was not set. <BR/>
     */
    private static final String ERRM_PARAM0_DATE_NOTSET =
        "Date in param0 not set!";



    /**************************************************************************
     * Constructor for m2ReminderObserverJob. <BR/>
     */
    public M2ReminderObserverJob ()
    {
        // nothing to do
    } // m2ReminderObserverJob


    /**************************************************************************
     * Constructor for an m2ReminderObserverJob object.<BR> Should be followed
     * by call to fetch-method.
     *
     * @param   context The context of the observer job.
     */
    public M2ReminderObserverJob (ObserverContext context)
    {
        // call constructor of super class:
        super (context);
    } // m2ReminderObserverJob


    /**************************************************************************
     * Constructor for an m2ReminderObserverJob object.<BR> Should be followed
     * by call to fetch-method.
     *
     * @param   context The observer context.
     * @param   user    The actual user.
     * @param   env     The actual environment.
     * @param   sess    The actual session object.
     * @param   app     The global application object.
     */
    public M2ReminderObserverJob (ObserverContext context, User user,
                                  Environment env, SessionInfo sess,
                                  ApplicationInfo app)
    {
        // call common constructor:
        this (context);

        // set m2 environment
        this.user = user;
        this.env = env;
        this.sess = sess;
        this.app = app;
    } // m2ReminderObserverJob


    /**************************************************************************
     * Constructor for an m2ReminderObserverJob object. <BR/>
     *
     * @param   context The context for initializing the ObserverJob.
     * @param   jdata   The job data for initializing the ObserverJob.
     */
    public M2ReminderObserverJob (ObserverContext context,
                                  M2ParameterObserverJobData jdata)
    {
        // call constructor of super class:
        super (context, jdata);
    } // m2ReminderObserverJob


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
        if (!(jdata instanceof M2ReminderObserverJobData))
        {
            throw new ObserverException ("m2ReminderObserverJob.fetch: " +
                    " Classname of given data object is no instance of m2ReminderObserverJobData." +
                    " data=" + jdata.toString ());
        } // if
        return super.register (jdata);
    } // register


    /**************************************************************************
     * Executes actions of this job: notification or optional actions defined in
     * other class.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected void executeActions () throws ObserverException
    {
        // type cast of data-object: holds
        // - paramOid1  oid of business-object. <BR/>
        // - paramOid2  oid of workflow-object (optional). <BR/>
        // - param0     string that specifies date duration. <BR/>
        //              ... date value: dd.mm.yyyy [hh:mm]. <BR/>
        //              ... duration values: 4711 h|d|w|m|y. <BR/>
        // - param1     user (s) or group (s) to notify: oids (0xddssttttiiiiiiii)
        //              and/or names. <BR/>
        // - param2     subject of notification. <BR/>
        // - param3     content of notification. <BR/>
        // - param4     description of notification. <BR/>
        // - param5     activity of notification. <BR/>
        // - param6     method (optional). <BR/>
        M2ReminderObserverJobData data = (M2ReminderObserverJobData) this.p_jdata;
        String param6 = null;

        if (data != null)
        {
            param6 = data.getParam6 ();
        } // if

        // check if default-action (=notify) or optional action
        // shall be executed.
        if (param6 == null || param6.length () == 0 || param6.equals ("null") ||
            param6.equals ("UNDEFINED"))
        {
            this.execNotification (data);
        } // if
        else
        {
            this.execOptional (data);
        } // else
    } // executeActions


    /**************************************************************************
     * Sets first execution-date according to paramDate in data-object.
     *
     * @return  The date/time of the next execution.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected Date calculateFirstExecution ()
        throws ObserverException
    {
        // calculate date from param 0
        // - param0     string that specifies date duration. <BR/>
        //              ... date value: dd.mm.yyyy [hh:mm]. <BR/>
        //              ... duration values: 4711 h|d|w|m|y. <BR/>
        Date date = null;
        String dateStr = ((M2ReminderObserverJobData) this.p_jdata).getParam0 ();
        if (dateStr == null || dateStr.length () == 0)
        {
            throw new ObserverException (M2ReminderObserverJob.ERRM_PARAM0_DATE_NOTSET);
        } // if

        // try some create conversion formats
        SimpleDateFormat df1 = new SimpleDateFormat ("dd.MM.yyyy");
        SimpleDateFormat df2 = new SimpleDateFormat ("dd.MM.yyyy HH:mm");
        df1.setTimeZone (TimeZone.getDefault ());
        df2.setTimeZone (TimeZone.getDefault ());

        // try some parsing possibilities
        try
        {
            date = df2.parse (dateStr);
        } // try
        catch (ParseException e)
        {
            // 1st format didn't work
            try
            {
                date = df1.parse (dateStr);
            } // try
            catch (ParseException e1)
            {
                date = M2ReminderObserverJob.convertDurationToDate (dateStr);
            } // catch
        } // catch

        return date;
    } // calculateFirstExecution

    /**************************************************************************
     * Converts string that specifies date-duration: 4711 mi|h|d|w|m|y. <BR/>
     *
     * @param   durationExpr    The duration as string.
     *
     * @return  The computed date/time.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected static Date convertDurationToDate (String durationExpr)
        throws ObserverException
    {
        // calculate date from param 0
        // - param0     string that specifies date duration. <BR/>
        //              ... date value: dd.mm.yyyy [hh:mm]. <BR/>
        //              ... duration values: 4711 h|d|w|m|y. <BR/>
        Date date = null;
        String dateStr = durationExpr;
        if (dateStr == null || dateStr.length () == 0)
        {
            throw new ObserverException (M2ReminderObserverJob.ERRM_PARAM0_DATE_NOTSET);
        } // if

        StringTokenizer t = new StringTokenizer (dateStr);
        try
        {
            // get 1st token: must be integer
            int duration = Integer.parseInt (t.nextToken ());
            // get 2nd toke: must be h|d|w|m|y
            String unit = t.nextToken ();
            int iUnit = -1;

            // convert duration-unit
            if (unit.equalsIgnoreCase ("h"))
            {
                iUnit = Calendar.HOUR;
            } // if
            else if (unit.equalsIgnoreCase ("mi"))
            {
                iUnit = Calendar.MINUTE;
            } // else if
            else if (unit.equalsIgnoreCase ("d"))
            {
                iUnit = Calendar.DATE;
            } // else if
            else if (unit.equalsIgnoreCase ("w"))
            {
                duration = duration * 7;    // convert to days
                iUnit = Calendar.DATE;
            } // else if
            else if (unit.equalsIgnoreCase ("m"))
            {
                iUnit = Calendar.MONTH;
            } // else if
            else if (unit.equalsIgnoreCase ("y"))
            {
                iUnit = Calendar.YEAR;
            } // else if

            // create a GregorianCalendar with default timezone and locale
            GregorianCalendar cal = new GregorianCalendar ();

            // set time and add value
            cal.setTime (new Date ());
            cal.add (iUnit, duration);

            // get date
            date = cal.getTime ();
        } // try
        catch (Exception e)
        {
            throw new ObserverException ("Could not calculate valid date from: " + dateStr +
                    "; " + e.toString ());
        } // catch Exception

        return date;
    } // convertDurationToDate


    /**************************************************************************
     * Returns null: only one execution possible.
     *
     * @return  The date/time of the next execution.
     *          <CODE>null</CODE> means: no next execution - finish job.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected Date calculateNextExecution () throws ObserverException
    {
        // only one execution!
        return null;
    } // calculateFirstExecution


    /**************************************************************************
     * Performs the notification.
     *
     * @param   data    The job data for the notification.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    private void execNotification (M2ReminderObserverJobData data)
        throws ObserverException
    {
        INotificationService ns;
        StringTokenizer tokenizer;
        String token;
        OID receiverOid = null;
        Vector<OID> receiverOids = new Vector<OID> ();
        Vector<OID> allReceiverOids = new Vector<OID> ();
        Vector<String> allReceiverNames = new Vector<String> ();

        // param1 contains one ore more user or group names or oids:
        // add user oids to vector:
        // loop through the tokens and try to get out the user or group name
        // for each token:
        tokenizer = new StringTokenizer (data.getParam1 (), ",; \t\n\r\f");
        while (tokenizer.hasMoreTokens ())
        {
            // get next token:
            token = tokenizer.nextToken ();

            if (token != null && (token = token.trim ()).length () > 0)
            {
                // check if the token is already an oid:
                if (token.length () == 18 &&
                    token.startsWith (UtilConstants.NUM_START_HEX))
                {
                    try
                    {
                        // just use the already known oid:
                        receiverOid = new OID (token);
                        allReceiverOids.addElement (receiverOid);
                    } // try
                    catch (IncorrectOidException e)
                    {
                        // print error message:
                        IOHelpers.showMessage (
                            "m2ReminderObserverJob: OID \"" + token +
                            "\" is not valid.",
                            e, this.app, this.sess, this.env, true);
                    } // catch
                } // if
                else                    // token is no oid
                {
                    // now we assume that the token is either a group name or
                    // a user name
                    // resolve the name:
                    allReceiverNames.add (token);
                } // else token is no oid
            } // if
        } // while

        // now get the receiver oids:
        receiverOids = this.getUserOids (allReceiverOids, allReceiverNames);

        // add objects oid to vector
        Vector<OID> objectOids = new Vector<OID> ();
        if (data.getOid1 () != null)
        {
            objectOids.addElement (this.bo1.oid);
        } // if

        // create notification service:
        ns = NotificationServiceFactory.getInstance (this.env).getNotificationService ();
        ns.initService (this.user, this.env, this.sess, this.app);

        // create notification template with notification-specific data:
        // - param2     subject of notification. <BR/>
        // - param3     content of notification. <BR/>
        // - param4     description of notification. <BR/>
        // - param5     description of notification. <BR/>
        NotificationTemplate templ =
                new NotificationTemplate (data.getParam2 (), data.getParam3 (),
                        data.getParam4 (), data.getParam5 ());

        // perform notification for given object to
        // given users (in param0) with created template
        try
        {
            ns.performNotification (receiverOids, objectOids, templ, false);
        } // try
        catch (NotificationFailedException exc)
        {
            throw new ObserverException ("Notification failed.");
        } // catch
    } // execNotification


    /**************************************************************************
     * Retrieve user oid (s) for given user or group names or oids. <BR/>
     * If the one of the elements is an user name or oid the oid for this user
     * is added to the vector.
     * If the element is a group name or oid all oids of all users, which are
     * members of the group, are returned.
     *
     * @param   oids    List of oids of users or groups.
     * @param   names   List of names of users or groups.
     *
     * @return  Vector with oids of users.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected Vector<OID> getUserOids (Vector<OID> oids, Vector<String> names)
        throws ObserverException
    {
        OID oid = null;
        Vector<OID> oidVector = new Vector<OID> ();
        String oidStr = null;
        SQLAction action = null;        // the action object used to access the
                                        // database
        StringBuffer oidList = new StringBuffer ();
        StringBuffer nameList = new StringBuffer ();
        String sep;
        boolean isCheckOids = false;
        boolean isCheckNames = false;

        // check if there are any oids:
        if (oids != null && oids.size () > 0)
        {
            // create comma-separated list of oids:
            sep = "";

            for (Iterator<OID> iter = oids.iterator (); iter.hasNext ();)
            {
                OID elem = iter.next ();
                oidList.append (sep).append (elem.toStringQu ());
                sep = ",";
            } // for iter

            isCheckOids = true;
        } // if

        // check if there are any names:
        if (names != null && names.size () > 0)
        {
            // create comma-separated list of names:
            nameList.append (StringHelpers.stringIteratorToStringBuffer (names.iterator (), ","));
            isCheckNames = true;
        } // if

        // check if there are any values:
        if (!isCheckOids && !isCheckNames)
        {
            //  no oid and no name, just return the empty result:
            return oidVector;
        } // if

        // select user-oids from the ibs_User table by his/her name
        // ensure that he/she is
        // - in the same domain
        // - active (in ibs_Object)
        StringBuffer queryStr = new StringBuffer ()
            .append ("SELECT DISTINCT ou.oid")
            .append (" FROM ibs_Object ou,")
            .append (" (")
                .append (" SELECT name, oid, oid AS userOid")
                .append (" FROM ibs_User")
                .append (" WHERE domainId = " + this.user.domain)
                .append (" UNION ALL")
                .append (" SELECT DISTINCT g.name, g.oid, u.oid AS userOid")
                .append (" FROM ibs_GroupUser gu, ibs_Group g, ibs_Object og,")
                    .append (" ibs_User u")
                .append (" WHERE g.oid = og.oid")
                    .append (" AND og.state = " + States.ST_ACTIVE)
                    .append (" AND g.id = gu.groupId")
                    .append (" AND gu.userId = u.id")
                    .append (" AND u.domainId = " + this.user.domain)
            .append (") userData")
            .append (" WHERE userData.userOid = ou.oid")
            .append (" AND (");

        // add oid and name selection criteria:
        sep = "";
        if (isCheckOids)
        {
            queryStr
                .append ("userData.oid IN (" + oidList + ")");
            sep = " OR ";
        } // if
        if (isCheckNames)
        {
            queryStr
                .append (sep).append ("userData.name IN (" + nameList + ")");
        } // if

        // finish query:
        queryStr
            .append (")")
            .append (" AND ou.state = " + States.ST_ACTIVE);

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = DBConnector.getDBConnection ();
            action.execute (queryStr, false);

            // get tuple out of db
            if (!action.getEOF ())
            {
                try
                {
                    // fetch additional data
                    oidStr = action.getString ("oid");
                    oid = new OID (oidStr);
                    // add the oid to the result vector:
                    oidVector.add (oid);
                } // try
                catch (IncorrectOidException e)
                {
                    throw new ObserverException ("User has incorrect oid: " +
                        oidStr);
                } // catch
            } // if
            else
            {
                throw new ObserverException (
                    "No User or Group with given names (\"" + nameList +
                    "\") and oids (\"" + oidList + "\") found." +
                    " It is also possible that a not-found group has no" +
                    " members and thus no users can be found.");
            } // else

        } // try
        catch (DBError err)
        {
            throw new ObserverException ("Error while retrieving user: " +
                err.toString ());
        } // catch
        finally
        {
            // close action and db-connection
            try
            {
                action.end ();
                DBConnector.releaseDBConnection (action);
            } // try
            catch (DBError e)
            {
                throw new ObserverException (
                    "Error while trying to getUserOidsByName: " +
                        e.getMessage () + "; Query: " + queryStr);
            } // catch
        } // finally

        // return the result:
        return oidVector;
    } // getUserOidsByName


    /**************************************************************************
     * Performs optional method in class, defined by object given in
     * oidParam1. Signature of called method must be:. <BR/>
     * void methodname ([Object]) throws Exception. <BR/>
     * The object-parameter is always the m2ReminderObserverJob-object itself.
     *
     * @param   data    The data of the job to be executed.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    private void execOptional (M2ReminderObserverJobData data)
        throws ObserverException
    {
        String methodName = "M2ReminderObserverJob.execOptional: ";

        // check initialization
        if (this.bo1 == null)
        {
            throw new ObserverException (methodName +
                    " BusinessObject not initialized.");
        } // if
        if (data == null)
        {
            throw new ObserverException (methodName +
                    " data object not set.");
        } // if

        // get class and method
        String method = data.getParam6 ();
        if (method == null || method.length () == 0)
        {
            throw new ObserverException (methodName +
                    " optional method not set.");
        } // if

        // set reminder-job as parameter for method call
        Object callInParam = this;

        // dynamically instantiate object and call method on object which
        // is given in param6
        try
        {
            // get the class of the given object
            Class<? extends BusinessObject> classOfObject = this.bo1.getClass ();
            // create array of classes that represent the in-parameters for the method
            // NOTE: only 1 in-param that must be an Object
            Class<?>[] inParamClasses = { (new Object ()).getClass ()};
            // get the method of object-class (defined by param6)
            Method callMethod = classOfObject.getMethod (method, inParamClasses);

            // inparams must be wrapped in an object-array
            Object[] inParamArray = {callInParam};

            // call the method on the given object
            callMethod.invoke (this.bo1, inParamArray);

        } // try
        catch (IllegalAccessException e)
        {
            throw new ObserverException (methodName + e.toString ());
        } // catch
        catch (NoSuchMethodException e)
        {
            throw new ObserverException (methodName + e.toString ());
        } // catch
        catch (IllegalArgumentException e)
        {
            throw new ObserverException (methodName + e.toString ());
        } // catch
        catch (InvocationTargetException e)
        {
            throw new ObserverException (methodName + "InvocationTargetException caused by \n" +
                Helpers.getStackTraceFromThrowable (e.getCause ()) + "\n");
        } // catch
        catch (Exception e)
        {
            throw new ObserverException (methodName + e.toString () +
                " caused by \n" +
                Helpers.getStackTraceFromThrowable (e.getCause ()) + "\n");
        } // catch
    } // execOptional


    /**************************************************************************
     * No retry on error: wields false.
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
        return true;
    } // retryOnError


    /**************************************************************************
     * No retry on error: wields 0.
     *
     * @return  Number of retry-attempts.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected int getInitialRetryAttempts ()
        throws ObserverException
    {
        return 5;
    } // getRetryAttempts


    /**************************************************************************
     * No checks.
     *
     * @return  <CODE>true</CODE>    if the executewhen-condition is true.
     *          <CODE>false</CODE>   if the executewhen-conition is false.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected boolean evaluateExecuteWhen () throws ObserverException
    {
        return true;
    } // evaluateExecuteWhen


    /**************************************************************************
     * No checks.
     *
     * @return  <CODE>true</CODE>    if the registerwhen-condition is true.
     *          <CODE>false</CODE>   if the registerwhen-conition is false.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected boolean evaluateRegisterWhen () throws ObserverException
    {
        return true;
    } // evaluateRegisterWhen


    /**************************************************************************
     * Check before execution:. <BR/>
     *
     * @return  <CODE>true</CODE>    If the unregisterwhen-condition is true.
     *          <CODE>false</CODE>   If the unregisterwhen-conition is false.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected boolean evaluateUnregisterWhen () throws ObserverException
    {
        return false;
    } // evaluateUnregisterWhen

} // M2ReminderObserverJob
