/*
 * Class: NotificationService.java
 */

// package:
package ibs.service.notification;

// imports:
//KR TODO: unsauber
import ibs.app.AppFunctions;
import ibs.bo.BOArguments;
import ibs.bo.BOConstants;
import ibs.bo.BOHelpers;
import ibs.bo.BOMessages;
import ibs.bo.BusinessObject;
import ibs.bo.IncorrectOidException;
import ibs.bo.OID;
import ibs.bo.ObjectClassNotFoundException;
import ibs.bo.ObjectInitializeException;
import ibs.bo.ObjectNotFoundException;
import ibs.bo.Operations;
import ibs.bo.type.Type;
import ibs.bo.type.TypeConstants;
import ibs.bo.type.TypeNotFoundException;
import ibs.di.DIConstants;
import ibs.di.XMLViewer_01;
import ibs.io.Environment;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.io.Ssl;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.wsp.ReceivedObject_01;
import ibs.obj.wsp.Recipient_01;
import ibs.obj.wsp.SentObject_01;
import ibs.service.conf.ServerRecord;
import ibs.service.email.EMail;
import ibs.service.email.EMailConstants;
import ibs.service.email.EMailManager;
import ibs.service.user.User;
import ibs.tech.http.HttpArguments;
import ibs.tech.sql.DBError;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.SQLQueryConstants;
import ibs.tech.sql.SelectQuery;
import ibs.tech.sql.StoredProcedure;
import ibs.tech.sql.StoredProcedureConstants;
import ibs.util.NameAlreadyGivenException;
import ibs.util.NoAccessException;

import java.io.File;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;


/******************************************************************************
 * The NotificationService performs the notification. Notification is possible
 * m2 internally, by email and additionally by sms. The kind of notification can
 * be chosen by a single receiver in the userprofile. The names and the oids
 * of the receivers who could not been notified at all are saved. <BR/>
 *
 * @version     $Id: NotificationService.java,v 1.31 2013/01/16 16:14:14 btatzmann Exp $
 *
 * @author      Monika Eisenkolb (ME)
 ******************************************************************************
 */
public class NotificationService extends BusinessObject implements INotificationService
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: NotificationService.java,v 1.31 2013/01/16 16:14:14 btatzmann Exp $";


    /**
     * No notification. <BR/>
     */
    protected static final int NOT_NONE = 0;

    /**
     * Notification kind m2. <BR/>
     */
    protected static final int NOT_M2 = 1;

    /**
     * Notification kind email. <BR/>
     */
    protected static final int NOT_EMAIL = 2;

    /**
     * Notification kind sms. <BR/>
     */
    protected static final int NOT_SMS = 4;

    /**
     * All existing notification kinds. <BR/>
     */
    protected static final int NOT_ALL = NotificationService.NOT_M2 |
        NotificationService.NOT_EMAIL | NotificationService.NOT_SMS;

    /**
     * all receivers who will be notified. <BR/>
     */
    protected Vector<OID> receivers = null;

    /**
     * all objects which will be distributed. <BR/>
     */
    private Vector<OID> distributeObjects = null;

    /**
     * a template holding important attributes for performing
     * the notification. <BR/>
     */
    private NotificationTemplate notifyTemplate = null;

    /**
     * the email address of the user. <BR/>
     */
    private String userEmail = "";

    /**
     * holding the user names of the receivers who could not be notified
     * at all. <BR/>
     */
    private String errorStringUserNames = "";

    /**
     * holding the user oids of the receivers who could not be notified
     * at all. <BR/>
     */
    private String errorStringUserOids = "";



    /**************************************************************************
     * Trivial constructor. <BR/>
     */
    protected NotificationService ()
    {
        // nothing to do
    } // NotificationService


    /***************************************************************************
     * @see ibs.service.notification.INotificationService#initService(ibs.service.user.User, ibs.io.Environment, ibs.io.session.SessionInfo, ibs.io.session.ApplicationInfo)
     */
    public void initService (User aUser, Environment aEnv,
                            SessionInfo aSess, ApplicationInfo aApp)
    {
        // set id of function
        //this.id = aId;
        // set the instance's public/protected properties:
        this.setEnv (aEnv);
        this.app = aApp;
        this.sess = aSess;
        this.user = aUser;               // set the user

    } // initFunction


    /***************************************************************************
     * @see ibs.service.notification.INotificationService#getFailedReceiverOids()
     */
    public String getFailedReceiverOids ()
    {
        // cutting the last delimiter
        if (this.errorStringUserOids.length () == 0)
        {
            return this.errorStringUserOids;
        } // if

        return this.errorStringUserOids.substring (0, this.errorStringUserOids
            .length () - 1);
    } // getFailedReceiverOids


    /***************************************************************************
     * @see ibs.service.notification.INotificationService#getFailedReceiverUserNames()
     */
    public String getFailedReceiverUserNames ()
    {
        // cutting the last delimiter
        if (this.errorStringUserNames.length () == 0)
        {
            return this.errorStringUserNames;
        } // if

        return this.errorStringUserNames.substring (0,
            this.errorStringUserNames.length () - 1);
    } // getFailedReceiverUserNames


    /**************************************************************************
     * For every receiver a query to the database is done to get the desired
     * kind of notification and the needed adress data.Then the different methods
     * for the special notification are called. <BR/>
     *
     * @param   receivers               The receivers who should receive the
     *                                  notification.
     * @param   allowedNotifications    The allowed notification kinds.
     *                                  (sum of the notification kind values)
     * @param   createOutboxEntry Shall an outbox entry be created, too?
     *
     * @return  <CODE>true</CODE> if notification was successful.
     */
    protected boolean doNotification (Vector<OID> receivers,
                                      int allowedNotifications,
                                      boolean createOutboxEntry)
    {
        // true if one receiver was notified successfully
        // return values for the special notifications
        boolean result = false;
        boolean result1 = false;
        boolean result2 = false;
        boolean res = true;             // return value of the function
        OID singleReceiver = null;      // oid of a single user
        UserNotificationData userData = null; // data for a user notification

        // get the user email address:
        userData = this.getUserNotificationData (this.getUser ().oid);
        // check if we got the user data:
        if (userData != null)           // got the user data?
        {
            // set the userEmail, user = sender
            this.userEmail = userData.p_email;

            if (this.userEmail == null || this.userEmail.trim ().length () == 0)
            {
                // set standard sender address:
                this.userEmail =
                    userData.p_name + "@" + this.app.p_system.p_systemName;
            } // if

            // all chosen receivers will be notified
            for (Enumeration<OID> e = receivers.elements (); e.hasMoreElements ();)
            {
                // get a single receiver
                singleReceiver = e.nextElement ();

                // get the notification data for a receiver
                userData = this.getUserNotificationData (singleReceiver);
                // check if we have valid user data:
                if (userData != null && userData.p_name != null &&
                    userData.p_name.length () > 0)
                {
                    // ensure allowed notification kinds:
                    userData.p_notificationKind &= allowedNotifications;

                    // the notification is done depending on the chosen kind
                    switch (userData.p_notificationKind)
                    {
                        case NotificationService.NOT_M2:
                            // m2-internally
                            result = this.m2Notification (userData, createOutboxEntry);
//                            setErrorStrings (result, singleReceiver);
                            res = res && result;
                            break;

                        case NotificationService.NOT_EMAIL:
                            // by email
                            result = this.emailNotification (userData);
//                            setErrorStrings (result, singleReceiver);
                            res = res && result;
                            break;

                        case NotificationService.NOT_M2 | NotificationService.NOT_EMAIL:
                            // m2-internally and by email
                            result1 = this.m2Notification (userData, createOutboxEntry);
                            result2 = this.emailNotification (userData);

                            // check if the receiver could be notified:
                            if (!(result1 || result2))
                                            // receiver could not be notified?
                            {
  //                              setErrorStrings (false, singleReceiver);
                                result = false;
                            } // if receiver could not be notified
                            else
                            {
                                result = true;
                            } // else

                            res = res && result;
                            break;

                        default:
                            res = false;
                    } // switch (userData.p_notificationKind)

                    // performing notification per sms additionally:
                    if (userData.p_sendSms)
                    {
                        // currently there is no error handling if a sms
                        // notification fails
                        result = this.smsNotification (userData);
                    } // if
                } // if (userData != null && userData.p_name != null &&
                else    // else
                {
                    // BB TODO: this means that the user that has been set as
                    // receiver was not valid and we could not get any data
                    // This case should better be treated with an exception
                } // else
            } // for e

            // returning the result:
            return res;
        } // if got the user data

        // got no user data
        // this should never happen!
        // BB TODO: an exception would be the better solution
        return false;
    } // doNotification


    /**************************************************************************
     * A m2-interior notification is performed like the usual distributing.
     * The marked objects are distributed to the chosen receivers. <BR/>
     *
     * @param   userData    Data of a receiver.
     * @param   createOutboxEntry Shall an outbox entry be created, too?
     *
     * @return  <CODE>true</CODE> if notification was successful,
     *          <CODE>false</CODE> otherwise.
     */
    private boolean m2Notification (UserNotificationData userData,
                                    boolean createOutboxEntry)
    {
        boolean single = false;         // just one single object?
        OID receivedObjectOid = null;   // oid of the received object
        OID oid = null;                 // the oid of the distributed object

        // how many objects are chosen for distributing?
        if (this.distributeObjects == null ||
            this.distributeObjects.size () == 0) // no object to distribute?
        {
            // use single object distribution:
            single = true;

            // set dummy oid:
            oid = OID.getEmptyOid ();
        } // if no object to distribute
        else if (this.distributeObjects.size () == 1) // just one object?
        {
            // use single object distribution:
            single = true;

            // get the oid:
            oid = this.distributeObjects.firstElement ();
        } // if just one object

        if (single)                     // there is only one element?
        {
            receivedObjectOid =
                this.performM2Notification (oid, userData.p_oid, createOutboxEntry);

            // executed distribute - clear the old receivers list
            this.sess.receivers = new Vector<String[]> ();
        } // if there is only one element
        else                            // more objects
        {
            for (Iterator<OID> iter = this.distributeObjects.iterator ();
                 iter.hasNext ();)
            {
                receivedObjectOid = this.performM2Notification (iter.next (),
                    userData.p_oid, createOutboxEntry);
            } // for iter
        } // else more objects

        // true is returned if distributing the objects was ok otherwise false
        if (receivedObjectOid != null)
        {
            return true;
        } // if

        // notification failed
        // add the error message:
        this.setErrorMessage (userData, "inbox notification failed!");
        return false;
    } // m2Notification


    /**************************************************************************
     * Perform the notification of one object in m2. <BR/>
     *
     * @param   distributedOid  The oid of the distributed object.
     * @param   singleReceiver  Oid of the receiver.
     * @param   createOutboxEntry Shall an outbox entry be created, too?
     *
     * @return  The oid of the received object entry.
     */
    private OID performM2Notification (OID distributedOid, OID singleReceiver,
                                       boolean createOutboxEntry)
    {
        // perform the distribution and return the result:
        return this.objectDistribute (
                            distributedOid,
                            singleReceiver,
                            this.notifyTemplate.getSubject (),
                            this.notifyTemplate.getDescription (),
                            this.notifyTemplate.getActivities (),
                            this.env.getBoolParam (BOArguments.ARG_FREEZE) ==
                            IOConstants.BOOLPARAM_TRUE,
                            createOutboxEntry);
    } // performM2Notification


    /***************************************************************************
     * @see ibs.service.notification.INotificationService#emailNotification(ibs.service.notification.NotificationTemplate, java.util.Vector, java.lang.String, java.lang.String)
     */
    public boolean emailNotification (NotificationTemplate template,
                                      Vector<OID> distributeObjects,
                                      String sender, String receiver)
    {
        UserNotificationData userData = null; // data for a user notification

        // integrity checks:
        if (receiver != null && receiver.length () > 0 &&
            template != null && distributeObjects != null)
        {
            // set the parameters:
            this.userEmail = sender;
            this.notifyTemplate = template;
            this.distributeObjects = distributeObjects;

            // get the notification data for a receiver:
            userData = new UserNotificationData ();
            userData.p_name = "";
            userData.p_oid = OID.getEmptyOid ();
            userData.p_notificationKind = NotificationService.NOT_EMAIL;
            userData.p_sendSms = false;
            userData.p_email = receiver;
            userData.p_smsemail = null;

            // and send the email:
            return this.emailNotification (userData);
        } // if (receiver != null && (!receiver.equals ("")) && ...

        // invalid settings
        return false;
    } // emailNotification


    /**************************************************************************
     * Notification by email is performed. An EMail object is created
     *
     * If the receiver for every object an weblink is added in the email
     * content.
     * the EMailManager is called that is handling the sending of an email. <BR/>
     *
     * @param   userData    The data of the current notification user.
     *
     * @return  <CODE>true</CODE> if notification was done successfully.
     */
    private boolean emailNotification (UserNotificationData userData)
    {
        boolean result = true;          // return value

        // fill the email object with the data from the notification template
        EMail mail = new EMail ();

        // set the sender
        mail.setSender (this.userEmail);
        // set the receiver
        mail.setReceiver (userData.p_email);

        String content = "";
        // set the m2 message
        content = 
            MultilingualTextProvider.getMessage (NotificationMessages.MSG_BUNDLE,
                NotificationMessages.ML_MSG_NOTIFICATIONSUBJECT, env)
            + " " + this.notifyTemplate.getSubject () + "\n" +
            MultilingualTextProvider.getMessage (NotificationMessages.MSG_BUNDLE,
                NotificationMessages.ML_MSG_NOTIFICATIONREQUEST, env)
            + " " + this.notifyTemplate.getActivities () + "\n" +
            MultilingualTextProvider.getMessage (NotificationMessages.MSG_BUNDLE,
                NotificationMessages.ML_MSG_NOTIFICATIONREMARK, env)
            + " " + this.notifyTemplate.getDescription () + "\n\n";

        // if weblinks are required for each object one is built
        // requirements changed -> always with weblinks
        if (true)                       // (this.addWeblink)
        {
            for (Enumeration<OID> e = this.distributeObjects.elements ();
                 e.hasMoreElements ();)
            {
                content += this.getWeblink (e.nextElement (), true) + "\n\n";
            } // for
        } // if

        if (this.notifyTemplate.getEmailContentType () == EMailConstants.CONTENT_TYPE_TEXT_HTML)
        {
            mail.setContentType ("text/html");
        } // if
        
        // set content and subject of the email
        mail.setContent (this.getEmailContent ());
        mail.setSubject (
            MultilingualTextProvider.getMessage (NotificationMessages.MSG_BUNDLE,
                NotificationMessages.ML_MSG_EMAILSMSSUBJECT, env) +
            this.notifyTemplate.getSubject ());

        // add attachments
        if (this.notifyTemplate.attachments != null)
        {
            for (Enumeration<File> e = this.notifyTemplate.attachments.elements ();
                 e.hasMoreElements ();)
            {
                mail.addAttachment (e.nextElement ());
            } // for
        } // if

        // try to notify
        try
        {   // EMailmanager handles sending an email
            EMailManager.sendMail (mail);
        } // try
        catch (AddressException e)
        {
//System.err.println (e.getMessage ());
            this.setErrorMessage (userData, e.toString ());
            result = false;
        } // catch
        catch (MessagingException e)
        {
//System.err.println (e.getMessage ());
            this.setErrorMessage (userData, e.toString ());
            result = false;
        } // catch

        // returning the result
        return result;

    } // emailNotification
    
    
    /**
     * Computes the content for sending notification emails based
     * on the information witin the notify template. <BR/>
     *
     * @return
     */
    protected String getEmailContent ()
    {
        StringBuilder content = new StringBuilder ();
        
        String lineSeparator;
        String boldStartTag = "", boldEndTag = "";
        
        if (this.notifyTemplate.getEmailContentType () == EMailConstants.CONTENT_TYPE_TEXT_HTML)
        {
            lineSeparator = "<BR/>";
            boldStartTag = "<B>";
            boldEndTag = "</B>";
            
            content.append (DIConstants.STANDARD_EMAIL_HEADER);
            content.append ("<FONT FACE=\"Verdana,Arial,sans-serif\" SIZE=\"2\">");
        } // if
        else
        {
            lineSeparator = "\n";
        } // else
        
        // set the m2 message
        content.
            append (boldStartTag).
            append (MultilingualTextProvider.getMessage (NotificationMessages.MSG_BUNDLE,
                NotificationMessages.ML_MSG_NOTIFICATIONSUBJECT, env)).
            append (boldEndTag).
            append (" ").append (this.notifyTemplate.getSubject ()).append (lineSeparator).
            append (boldStartTag).
            append (MultilingualTextProvider.getMessage (NotificationMessages.MSG_BUNDLE,
                NotificationMessages.ML_MSG_NOTIFICATIONREQUEST, env)).   
            append (boldEndTag).
            append (" ").append (this.notifyTemplate.getActivities ()).append (lineSeparator).
            append (boldStartTag).append (MultilingualTextProvider.getMessage (NotificationMessages.MSG_BUNDLE,
                NotificationMessages.ML_MSG_NOTIFICATIONREMARK, env)).append (boldEndTag).
            append (" ").append (this.notifyTemplate.getDescription ()).
            append (lineSeparator).
            append (lineSeparator);

        // if weblinks are required for each object one is built
        // requirements changed -> always with weblinks
        if (true)                       // (this.addWeblink)
        {
            for (Enumeration<OID> e = this.distributeObjects.elements ();
                 e.hasMoreElements ();)
            {
                content.append (this.getWeblink (e.nextElement (), true)).
                        append (lineSeparator).
                        append (lineSeparator);
            } // for
        } // if
        
        if (this.notifyTemplate.getContent () != null && !this.notifyTemplate.getContent ().trim ().isEmpty ())
        {
            content.append (this.notifyTemplate.getContent ()).append (lineSeparator).append (lineSeparator);
        } // if
        
        if (this.notifyTemplate.getEmailContentType () == EMailConstants.CONTENT_TYPE_TEXT_HTML)
        {
            content.append ("</FONT>");
            content.append (lineSeparator).append (lineSeparator).append (DIConstants.STANDARD_EMAIL_FOOTER);
        } // if
        
        return content.toString ();
    } // getEmailContent


    /**************************************************************************
     * A notification by sms is done. <BR/>
     *
     * @param   userData    The data of the current notification user.
     *
     * @return  <CODE>true</CODE> if the notification was successful.
     */
    private boolean smsNotification (UserNotificationData userData)
    {
        boolean result = true;
        // fill the email object with the data from the notification template
        EMail mail = new EMail ();

        // set sender and receiver
        mail.setSender (this.userEmail);
        mail.setReceiver (userData.p_smsemail);

        String content = "";
        // set the text message
        content = 
            MultilingualTextProvider.getMessage (NotificationMessages.MSG_BUNDLE,
                NotificationMessages.ML_MSG_NOTIFICATIONSUBJECT, env) +
            this.notifyTemplate.getSubject () + "\n" +
            MultilingualTextProvider.getMessage (NotificationMessages.MSG_BUNDLE,
                NotificationMessages.ML_MSG_NOTIFICATIONREQUEST, env) +
            this.notifyTemplate.getActivities ();

        // set the mail parameter
        mail.setContent (content);
        mail.setSubject (
            MultilingualTextProvider.getMessage (NotificationMessages.MSG_BUNDLE,
                NotificationMessages.ML_MSG_EMAILSMSSUBJECT, env));

        // try to notify
        try
        {
            // EMailmanager handles sending an email
            EMailManager.sendMail (mail);
        } // try
        catch (AddressException e)
        {
            this.setErrorMessage (userData, e.toString ());
            result = false;
        } // catch
        catch (MessagingException e)
        {
            this.setErrorMessage (userData, e.toString ());
            result = false;
        } // catch

        // returning the result
        return result;

    } // smsNotification


    /***************************************************************************
     * @see ibs.service.notification.INotificationService#performNotification(java.util.Vector, ibs.bo.OID, ibs.service.notification.NotificationTemplate, boolean)
     */
    public void performNotification (Vector<String[]> receiverOids, OID oid,
                                     NotificationTemplate template,
                                     boolean createOutboxEntry)
        throws NotificationFailedException
    {
        // set the receivers
        this.receivers = new Vector<OID> ();

        String st = "";
        OID stoid = null;
        for (Iterator<String[]> iter = receiverOids.iterator (); iter.hasNext ();)
        {
            String[] t = iter.next ();

            if (t != null)
            {
                st = t[1];
                try
                {
                    stoid = new OID (st);
                } // try
                catch (IncorrectOidException e)
                {
                // no valid oid => show corresponding message:
                // showIncorrectOidMessage (oidStr);
                } // catch
            } // if
            this.receivers.addElement (stoid);
        } // for iter

        // set the distributeObjects
        this.distributeObjects = new Vector<OID> ();
        this.distributeObjects.addElement (oid);

/* KR 20031014 doesn't make sense
        this.notifyTemplate = new NotificationTemplate ();
*/
        this.notifyTemplate = template;

        boolean result = false;         // return value
        // calling the methods who are performing the notification
        result = this.doNotification (this.receivers,
            NotificationService.NOT_ALL, createOutboxEntry);

        if (!result)                    // no success?
        {
            throw new NotificationFailedException (
                MultilingualTextProvider.getMessage (NotificationMessages.MSG_BUNDLE,
                    NotificationMessages.ML_MSG_NOTIFICATIONFAILED, env)
                + " [" + this.getFailedReceiverUserNames () + "]");
        } // if no success
    } //  performNotification


    /***************************************************************************
     * @see ibs.service.notification.INotificationService#performNotification(java.util.Vector, ibs.bo.OID[], ibs.service.notification.NotificationTemplate, boolean)
     */
    public void performNotification (Vector<String[]> receiverOids, OID[] objectOids,
        NotificationTemplate template, boolean createOutboxEntry)
        throws NotificationFailedException
    {
        // set the receivers
        this.receivers = new Vector<OID> ();

        String st = "";
        OID    stoid = null;
        for (Enumeration<String[]> e = receiverOids.elements (); e.hasMoreElements ();)
        {
            String[] t = e.nextElement ();

            if (t != null)
            {
                st = t[1];
                try
                {
                    stoid = new OID (st);
                } // try
                catch (IncorrectOidException ex)
                {
                    // nothing to do
                } // catch
            } // if

            this.receivers.addElement (stoid);
        } // for

        // set the distributeObjects
        this.distributeObjects = new Vector<OID> ();
        int i;
        for (i = 0; i < objectOids.length; i++)
        {
            this.distributeObjects.addElement (objectOids[i]);
        } // for

        // set the NotificationTemplate object
        this.notifyTemplate = new NotificationTemplate ();
        this.notifyTemplate = template;

        boolean result = false;                                 //return value
        // calling the methods which are performing the notification
        result = this.doNotification (this.receivers,
            NotificationService.NOT_ALL, createOutboxEntry);

        if (!result)                    // no success?
        {
            throw new NotificationFailedException (
                MultilingualTextProvider.getMessage (NotificationMessages.MSG_BUNDLE,
                    NotificationMessages.ML_MSG_NOTIFICATIONFAILED, env)
                + " [" + this.getFailedReceiverUserNames () + "]");
        } // if no success
    } // performNotification


    /***************************************************************************
     * @see ibs.service.notification.INotificationService#performNotification(java.util.Vector, java.util.Vector, ibs.service.notification.NotificationTemplate, boolean)
     */
    public void performNotification (Vector<OID> receiverOids,
                                     Vector<OID> objectOids,
                                     NotificationTemplate template,
                                     boolean createOutboxEntry)
        throws NotificationFailedException
    {
        // set the receivers
        this.receivers = new Vector<OID> ();
        for (Enumeration<OID> e = receiverOids.elements (); e.hasMoreElements ();)
        {
            this.receivers.addElement (e.nextElement ());
        } // for
        // set the distributeObjects
        this.distributeObjects = new Vector<OID> ();
        for (Enumeration<OID> e = objectOids.elements (); e.hasMoreElements ();)
        {
            this.distributeObjects.addElement (e.nextElement ());
        } // for

        // set the NotificationTemplate object
        this.notifyTemplate = template;

        boolean result = false;                                    // return value
        // calling the methods which are performing the notification
        result = this.doNotification (this.receivers,
            NotificationService.NOT_ALL, createOutboxEntry);

        if (!result)                    // no success?
        {
            throw new NotificationFailedException (
                MultilingualTextProvider.getMessage (NotificationMessages.MSG_BUNDLE,
                    NotificationMessages.ML_MSG_NOTIFICATIONFAILED, env)
                + " [" + this.getFailedReceiverUserNames () + "]");
        } // if no success
    } // performNotification


    /**************************************************************************
     * special interface for ordering.
     * An emailnotification to the person who is responsible for the orderings
     * is done. <BR/>
     *
     * @param   receiverOids    Vector of receiver oids
     * @param   objectOids      Vector of object oids
     * @param   template        NotificationTemplate holding the attributes
     * @param   order           set if service called from an ordering
     *
     * @throws  NotificationFailedException
     *          if an error occurred.
     */
    /*
    public void performNotification (Vector receiverOids, Vector objectOids,
        NotificationTemplate template, boolean order) throws NotificationFailedException
    {
    // set the receivers
    this.receivers = new Vector ();
    for (Enumeration e = receiverOids.elements (); e.hasMoreElements ();)
    {
        this.receivers.addElement (e.nextElement ());
    } // for

    // set the distributeObjects
    this.distributeObjects = new Vector ();
    for (Enumeration e = objectOids.elements (); e.hasMoreElements ();)
    {
        this.distributeObjects.addElement (e.nextElement ());
    } // for

    // set the NotificationTemplate object
    this.notifyTemplate = new NotificationTemplate ();
    this.notifyTemplate = template;

    boolean result = false;             // return value

    OID singleReceiver = null;        // oid of a receiver
    getUserData ();                 // get the user email address

    // all chosen receivers will be notified
    for (Enumeration e = this.receivers.elements (); e.hasMoreElements ();)
    {
        singleReceiver = (OID) (e.nextElement ());
        // get the notification data for a receiver
        getReceiverData (singleReceiver);
        // emailnotification is done
        result = emailNotification ();
    } // for

    if (!result)
    { // no success
        throw (new NotificationFailedException (NotificationMessages.MSG_NOTIFICATIONFAILED));
    } // if
    } // performNotification
    */


    /***************************************************************************
     * @see ibs.service.notification.INotificationService#objectDistribute(ibs.bo.OID, ibs.bo.OID, java.lang.String, java.lang.String, java.lang.String, boolean, boolean)
     */
    public OID objectDistribute (OID oid, OID receiver, String subject,
                                 String description, String activities,
                                 boolean freeze, boolean createOutboxEntry)
    {
        Recipient_01 recipient = null;
        SentObject_01 sentObject = null;
        OID receiverEntryOid = null;
        OID sentObjectOid = null;
        OID recipientOid = null;
        OID receivedObjectOid = null;
        OID distributedOid = oid;       // the oid of the distributed object
        int distributedType = TypeConstants.TYPE_NOTYPE; // the object's type
        String distributedTypeName = 
                MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_UNKNOWN, env); // the type name
        boolean physicalObj = false;       // was the object found?

        if (oid.isEmpty ())             // the oid is empty?
        {
            distributedOid = null;
            // there exists no object, but it's o.k.
            physicalObj = true;
        } // if the oid is empty
        // get the distributed object:
        else if (!oid.isTemp ())        // physical oid?
        {
            // set the values:
            Type curType = this.getTypeCache ().getType (oid.tVersionId);
            distributedType = curType.getTVersionId ();
            distributedTypeName = curType.getName ();
            // the object was found:
            physicalObj = true;
        } // if object exists

        // check if the object was found:
        if (physicalObj)                   // found the object?
        {
            // check if the entry in the outbox shall be created:
            if (createOutboxEntry)      // outbox entry shall be created?
            {
                // create an sentobject entry in the outbox
                if ((sentObject = (SentObject_01) this
                    .getNewObject (TypeConstants.TC_SentObject)) != null)
                {
/* KR not necessary because already done in BusinessObject.initialize
                    // set type within oid:
                    sentObject.type = this.getTypeCache ().getTVersionId (TypeConstants.TC_SentObject);
                    // init the type of the SentObject
*/
                    // set container id:
                    sentObject.containerId = OID.getEmptyOid ();
                    // set properties of inbox object
                    sentObject.name = subject;
                    sentObject.description = description;
                    sentObject.distributeId = distributedOid;
                    // name of object will be will be added in stored procedure
                    // sentObject.distributeName = "";
                    sentObject.distributeType = distributedType;
                    sentObject.distributeTypeName = distributedTypeName;
                    sentObject.activities = activities;
                    sentObject.freeze = freeze;
                    // create the new object and make it persistent in the db:
                    sentObjectOid = sentObject.forceStore ();
//                    sentObjectOid = sentObject.store (this.representationForm);
                    if (sentObjectOid != null) // object was created, oid returned?
                    {
                        // create the recipient object
                        receiverEntryOid = receiver; // (OID) (receivers.nextElement ());
                        if ((recipient = (Recipient_01) this
                            .getNewObject (TypeConstants.TC_Recipient)) != null)
                        {
/* KR not necessary because already done in BusinessObject.initialize
                            // set type within oid:
                            recipient.oid.type = recipient.type;
                            // type version id of recipient_01
                            recipient.type = this.getTypeCache ().getTVersionId (TypeConstants.TC_Recipient);
*/
                            // dummy value
                            // should be fullname of recipient name
                            recipient.name = sentObject.name;
                            // set container id:
                            // will be redirected in the stored procedure
                            // to the related recipients container
                            recipient.containerId = OID.getEmptyOid ();
                            recipient.setIsTab (false);
                            recipient.description = description;
                            // create an entry in the outbox receivers list
                            recipient.recipientId = receiverEntryOid;
                            recipient.sentObjectId = sentObjectOid;
                            // make object persistent in the db
                            // store performs a create (performCreateData)
                            // and a change (performChangeData)
                            recipientOid = recipient.forceStore ();
//                            recipientOid = recipient.store (this.representationForm);

                            if (recipientOid != null)
                                            // object was created, oid returned?
                            {
                                // nothing to do
                            } // if object was created, oid returned
                            else                    // object not created
                            {
                                // show corresponding message:
                                IOHelpers.showMessage (
                                    MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                                    BOMessages.ML_MSG_OBJECTNOTCREATED, this.env),
                                    this.app, this.sess, this.env);
                            } // else object not created
                        } // if
                        else                            // didn't get object
                        {
                            IOHelpers.showMessage (
                                MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                                BOMessages.ML_MSG_OBJECTNOTFOUND, this.env),
                                this.app, this.sess, this.env);
                        } // else
                    } // if object was created, oid returned
                    else                    // object not created
                    {
                        // show corresponding message:
                        IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                            BOMessages.ML_MSG_OBJECTNOTCREATED, this.env),
                            this.app, this.sess, this.env);
                    } // else object not created
                } // if
                else                            // didn't get object
                {
                    IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                            BOMessages.ML_MSG_OBJECTNOTFOUND, this.env),
                            this.app, this.sess, this.env);
                } // else
            } // if outbox entry shall be created

            // create received object entry in inbox:
            receivedObjectOid = this.createReceivedObject (distributedOid,
                distributedType, distributedTypeName, receiver, sentObjectOid,
                subject, description, activities);
        } // if found the object
        else                            // didn't get object
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_OBJECTNOTFOUND, this.env),
                    this.app, this.sess, this.env);
        } // else didn't get object

        // return the oid of the sentobject created or null
        return receivedObjectOid;
    } // objectDistribute


    /***************************************************************************
     * @see ibs.service.notification.INotificationService#createReceivedObject(ibs.bo.OID, int, java.lang.String, ibs.bo.OID, ibs.bo.OID, java.lang.String, java.lang.String, java.lang.String)
     */
    public OID createReceivedObject (OID distributedOid, int distributedType,
                                     String distributedTypeName, OID receiver,
                                     OID sentObjectOid, String subject,
                                     String description, String activities)
    {
        ReceivedObject_01 receivedObject = null;
        OID receivedObjectOid = null;

        // create the ReceivedObject entry in the inbox:
        if ((receivedObject = (ReceivedObject_01) this.getNewObject (
            TypeConstants.TC_ReceivedObject, false)) != null)
        {
            // set properties of inbox object
            receivedObject.name = subject;
            receivedObject.description = description;
//                receivedObject.recipientContainerId = ???;
            receivedObject.setReceiverOid (receiver);
            receivedObject.distributedId = distributedOid;
            // name of object will be added in stored procedure
            // receivedObject.distributedName = "";
            // receivedObject.distributedIcon = "";
            receivedObject.distributedType = distributedType;
            receivedObject.distributedTypeName = distributedTypeName;
            receivedObject.activities = activities;
            receivedObject.setSentObjectOid (sentObjectOid);
//                receivedObject.senderFullName = ???;
            // set container id:
//                receivedObject.computeContainerOid ();
//                receivedObject.containerId = OID.getEmptyOid ();
            // create the new object and make it persistent in the db:
            receivedObjectOid = receivedObject.forceStore ();
        } // if
        else                            // didn't get object
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_OBJECTNOTFOUND, this.env),
                    this.app, this.sess, this.env);
        } // else

        // return the oid of the sentobject created or null
        return receivedObjectOid;
    } // createReceivedObject


    /**************************************************************************
     * Get data of distributed object. <BR/>
     *
     * @param   oidStr  String representation of object id of the required
     *                  business object.
     *
     * @return  The generated object. <BR/>
     *          null if the object could not be generated.
     */
    protected BusinessObject getObject (String oidStr)
    {
        try
        {
            // create the oid and get the corresponding object:
            return this.getObject (new OID (oidStr));
        } // try
        catch (IncorrectOidException e)
        {
            // no valid oid => show corresponding message:
            this.showIncorrectOidMessage (oidStr);
        } // catch
        catch (Exception e)
        {
            // any exception => show a message:
            IOHelpers.showMessage (e.toString (),
                this.app, this.sess, this.env);
        } // catch
        return null;                    // return default value
    } // getObject


    /**************************************************************************
     * Get data for distributet object. <BR/>
     *
     * @param   oid     Object id of the required business object.
     *
     * @return  The generated object. <BR/>
     *          null if the object could not be generated.
     */
    protected BusinessObject getObject (OID oid)
    {
        BusinessObject obj = null;      // the business object

        if (oid != null)                // valid oid?
        {
            try
            {
                obj = this.getObjectCache ().fetchObject (oid, this.user, this.sess, this.env, true);
            } // try
            catch (ObjectNotFoundException e)
            {
                // check if a real object should be found or a temporary object:
                if (oid.isTemp ())      // temporary object?
                {
                    // no problem - work as if the object where found:
                } // if temporary object
                else                    // real object
                {
                    // show corresponding error message:
                    IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                            BOMessages.ML_MSG_OBJECTNOTFOUND, this.env),
                            this.app, this.sess, this.env);
                } // else
            } // catch
            catch (TypeNotFoundException e)
            {
                // show corresponding error message:
                IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            } // catch
            catch (ObjectClassNotFoundException e)
            {
                // show corresponding error message:
                IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            } // catch
            catch (ObjectInitializeException e)
            {
                // show corresponding error message:
                IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            } // catch
            catch (Exception e)
            {
                IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            } // catch
        } // if valid oid
        else                            // no valid oid
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_NOOID, this.env),
                    this.app, this.sess, this.env);
        } // else no valid oid

        return obj;                     // return the created object
    } // getObject


    /**************************************************************************
     * Get a new BusinessObject of a certain object type. <BR/>
     *
     * @param   type    Type of the object to instantiate.
     *
     * @return  The new BusinessObject instance or
     *          <CODE>null</CODE> if something went wrong.
     */
    public BusinessObject getNewObject (int type)
    {
        // create new BusinessObject from the type:
        BusinessObject obj = this.getObject (new OID (Type.createTVersionId (type), 0));

        return obj;                     // return the found object
    } // getNewObject


    /**************************************************************************
     * Get a new BusinessObject of a certain object type. <BR/>
     * The object type is specified through the unique tpye code.
     *
     * @param   typeCode    The unique type code.
     *
     * @return  The new BusinessObject instance or
     *          <CODE>null</CODE> if something went wrong.
     */
    public BusinessObject getNewObject (String typeCode)
    {
        // call common method and return the result:
        return this.getNewObject (typeCode, true);
    } // getNewObject


    /**************************************************************************
     * Get a new BusinessObject of a certain object type. <BR/>
     * The object type is specified through the unique tpye code.
     *
     * @param   typeCode    The unique type code.
     * @param   getParameters   Read the parameters?
     *
     * @return  The new BusinessObject instance or
     *          <CODE>null</CODE> if something went wrong.
     */
    public BusinessObject getNewObject (String typeCode, boolean getParameters)
    {
        BusinessObject obj = null;      // the business object

        if (typeCode != null)           // valid type code?
        {
            try
            {
                obj = this.getObjectCache ().fetchNewObject
                    (typeCode, this.user, this.sess, this.env, getParameters);
            } // try
            catch (TypeNotFoundException e)
            {
                // show corresponding error message:
                IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                        BOMessages.ML_MSG_TYPENOTFOUND, this.env),
                        this.app, this.sess, this.env);
            } // catch
            catch (ObjectClassNotFoundException e)
            {
                // show corresponding error message:
                IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                        BOMessages.ML_MSG_CLASSNOTFOUND, this.env),
                        this.app, this.sess, this.env);
            } // catch
            catch (ObjectInitializeException e)
            {
                // show corresponding error message:
                IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            } // catch
            catch (Exception e)
            {
                IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            } // catch
        } // if valid type
        else                            // no valid type
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_NOOID, this.env),
                    this.app, this.sess, this.env);
        } // else no valid oid

        return obj;                     // return the found object
    } // getNewObject


    /**************************************************************************
     * Returns the weblink to an specific object.
     * Contents parameter path in every case (ASP or SERVLET)
     *
     * DRAFTVERSION - weblink does not work in private-tab
     *
     * @param   oid             the oid of the object
     * @param   isExternal      false if weblink is used in existing m2 - session
     *                          true if weblink is used in mail or something like that
     *
     * @return  The weblink.
     */
    protected String getWeblink (OID oid, boolean isExternal)
    {
        String weblink = "";

        // building the weblink string
        weblink = IOConstants.URL_HTTP +
            ((ServerRecord) this.sess.actServerConfiguration).getApplicationServer () +
            ":" +
            ((ServerRecord) this.sess.actServerConfiguration).getApplicationServerPort () +
            this.env.getBaseURL () +
            // path
            HttpArguments.ARG_BEGIN + BOArguments.ARG_PATH +
            HttpArguments.ARG_ASSIGN + this.sess.home + "app/" +
            // dom
            HttpArguments.createArg (BOArguments.ARG_DOMAIN, this.user.domain) +
            // fct
            HttpArguments.createArg (BOArguments.ARG_FUNCTION, AppFunctions.FCT_WEBLINK) +
            // opath
            HttpArguments.createArg (BOArguments.ARG_OID, "" + oid.toString ());

        if (isExternal)
        {
            weblink = weblink + HttpArguments.createArg (
                BOArguments.ARG_LOADFRAME, true);
        } // internal

        if (((ServerRecord) this.sess.actServerConfiguration).getSsl ())
        { // if ssl is enabled
            return Ssl.getSecureUrl (weblink, this.sess);
        } // if

        // ssl is not enabled
        return Ssl.getNonSecureUrl (weblink, this.sess);
    } // getWeblink


    /**************************************************************************
     * The p_User_01$getNotificationData on the database is performed and
     * the important attributes (userprofile and notification address data)
     * for the NotificationService are set. <BR/>
     *
     * @param   oid     The oid of an user.
     *
     * @return  The data for the notification of an user.
     */
    protected UserNotificationData getUserNotificationData (OID oid)
    {
        UserNotificationData userData = null;
                                        // the notification data for the user

        // check if we have the oid of an existing object or of a virtual
        // object:
        // (a virtual object is an object which has no representation in the
        // object repository and is just created for some specific temporary
        // use)
        if (oid != null)           // existing object?
        {
            // create the stored procedure call:
            StoredProcedure sp = new StoredProcedure(
                    "p_User_01$getNotificationData",
                    StoredProcedureConstants.RETURN_VALUE);

            // parameter definitions:
            // must be in right sequence (like SQL stored procedure def.)
            // input parameters:
            // oid
            BOHelpers.addInParameter (sp, oid);

            // output parameters:
            // user name
            Parameter userNameOutParam = sp.addOutParameter (ParameterConstants.TYPE_STRING);
            // notificationKind
            Parameter notificationKindOutParam = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);
            // sendSms
            Parameter sendSmsOutParam = sp.addOutParameter (ParameterConstants.TYPE_BOOLEAN);
            // addWeblink
            Parameter addWeblinkOutParam = sp.addOutParameter (ParameterConstants.TYPE_BOOLEAN);
            // email
            Parameter emailOutParam = sp.addOutParameter (ParameterConstants.TYPE_STRING);
            // smsemail
            Parameter smsemailOutParam = sp.addOutParameter (ParameterConstants.TYPE_STRING);

            try
            {
                // perform the function call:
                BOHelpers.performCallFunctionData (sp, this.env);
            } // try
            catch (NoAccessException e)
            {
                // should not occur, display error message:
                IOHelpers.showMessage ("error when getting notification data",
                    e, this.app, this.sess, this.env, true);
            } // catch

            // set the attributes - get them out of parameters
            userData = new UserNotificationData ();
            userData.p_oid = oid;
            userData.p_name = userNameOutParam.getValueString ();
            userData.p_notificationKind = notificationKindOutParam.getValueInteger ();
            userData.p_sendSms = sendSmsOutParam.getValueBoolean ();
/*
            userData.p_addWeblink = addWeblinkOutParam.getValueBoolean ();
*/
            // addWeblink is not used                        
            userData.p_email = emailOutParam.getValueString ();
            userData.p_smsemail = smsemailOutParam.getValueString ();
        } // if existing object

        // return the result:
        return userData;
    } // getDataBaseData


    /**************************************************************************
     * If an error occurred during the notification the username and the
     * oid of the receiver who could not be notified at all
     * are saved. <BR/>
     *
     * @param   result          false if an error occurred
     * @param   oid             the oid of a receiver
     */
/*
    private void setErrorStrings (boolean result, OID oid)
    {
        if (!result)
            { // if no success username and oid of the receiver are saved
                this.errorStringUserNames += this.name + BOConstants.DELIMITER
                                             + " ";
                this.errorStringUserOids += oid.toString () +
                                            BOConstants.DELIMITER + " ";
            } // if
    } // setErrorStrings
*/


    /**************************************************************************
     * If an error occurred during the notification the username and the
     * oid of the receiver who could not be notified at all
     * are saved. <BR/>
     *
     * @param   userData    The receiver data of the notification.
     * @param   errorMsg    The error message.
     */
    private void setErrorMessage (UserNotificationData userData, String errorMsg)
    {
        String errorMsgLocal = errorMsg; // variable for local assignments
        // note that the " must be replaced with ' and \n by ' ' in case,
        // the message is used within javascript. this is the case when
        // using the distribution function
        errorMsgLocal = errorMsgLocal.replace ('\n', ' ');
        errorMsgLocal = errorMsgLocal.replace ('"', '\'');
        this.errorStringUserNames += userData.p_name + " [" +
            errorMsgLocal.replace ('\n', ' ') + "]" + BOConstants.DELIMITER + " ";
        this.errorStringUserOids += userData.p_oid + BOConstants.DELIMITER + " ";
    } // setErrorMessage


    /**************************************************************************
     * The method updates the name of a ReceivedObject_01 instance. It is primarily
     * used when the name of the corresponding XMLViewer_01 object has changed.
     * <BR/> 
     * 
     * @param subject				The subject under which the object has been sent (child
     *								node of the notify node in the workflow)
     * @param activity				The activity the receiver shall perform (child node
     * 								of the notify node in the workflow)
     * @param distributedObject		The business object which has been distributed by
     * 								the notification service
     */
    public void updateDistributedObjectName (String subject, String activity, XMLViewer_01 distributedObject)
    {  	
    	// Part of the select statement
    	StringBuilder whereClause;
    	// The database access actions
    	SQLAction action = null;
    	// The vector of query results
    	Vector<OID> receivedObjectOIDs = null;

    	StringBuilder selectClause = new StringBuilder ("ro.oid");
    	StringBuilder fromClause = new StringBuilder ("ibs_ReceivedObject_01 ro, ibs_object o");
    	whereClause = new StringBuilder ("o.name = N'").append (subject).append ("'");
    	whereClause.append (SQLQueryConstants.QL_AND);
    	whereClause.append ("o.state = 2");
    	whereClause.append (SQLQueryConstants.QL_AND);
    	whereClause.append ("ro.activities = N'").append (activity).append ("'");
    	whereClause.append (SQLQueryConstants.QL_AND);
    	whereClause.append ("ro.distributedId = ").append (distributedObject.oid.toString());
    	whereClause.append (SQLQueryConstants.QL_AND);
    	whereClause.append ("ro.oid = o.oid");

    	SelectQuery query = new SelectQuery (selectClause, fromClause, whereClause, null, null, null);

    	try {
    		// get the DB connection and execute the query
    		action = BOHelpers.getDBConnection (this.env);
    		int rowCount = action.execute (query);

    		if (rowCount > 0)
    		{
    			receivedObjectOIDs = new Vector<OID> ();

    			while ( !action.getEOF () )
    			{
    				// receive the query results
    				receivedObjectOIDs.add(SQLHelpers.getQuOidValue (action, "oid"));
    				action.next ();
    			} //while ( !action.getEOF () )
    		} // if (rowCount > 0)

    		action.end ();
    	} // try
    	catch (DBError e)
        {
            IOHelpers.showMessage (e, this.env, true);
        } // catch
        finally
        {
            BOHelpers.releaseDBConnection (action, this.env);
        } // finally

    	if (receivedObjectOIDs != null)
    	{
    		// the object which shall be changed
    		ReceivedObject_01 receivedObj;

    		for (Iterator<OID> iter = receivedObjectOIDs.iterator (); iter.hasNext ();)
    		{
    			receivedObj = (ReceivedObject_01) BOHelpers.getObject (iter.next (), distributedObject.getEnv(), false, false);

    			if(receivedObj != null)
    			{
    				// set the new name
    				receivedObj.distributedName = distributedObject.name;

    				// update the object on data base
    				try
    				{
						receivedObj.performChangeData (Operations.OP_CHANGE);
					} // try
    				catch (NoAccessException e)
    				{
						IOHelpers.showMessage (e, this.env, true);
					} // catch (NoAccessException e)
    				catch (NameAlreadyGivenException e)
    				{
    					IOHelpers.showMessage (e, this.env, true);
					} // catch (NameAlreadyGivenException e)

    			} //if (receivedObj != null)
    		} // for (Iterator<OID> iter = receivedObjectOIDs.iterator (); iter.hasNext ();)
    	} // if (receivedObjectOIDs != null)

    } // updateDistributedObjectName


    /**************************************************************************
     * This class holds all data which are necessary for user notification.
     * <BR/>
     *
     * @version     $Id: NotificationService.java,v 1.31 2013/01/16 16:14:14 btatzmann Exp $
     *
     * @author      Klaus, 30.10.2003
     **************************************************************************
     */
    protected class UserNotificationData extends Object
    {
        /**
         * Kind of notification. <BR/>
         * This value is a sum of the several allowed notification kinds:
         * 0 = no notification, 1 = m2, 2 = email, 1+2 = m2 + email... <BR/>
         */
        int p_notificationKind = NotificationService.NOT_M2;

        /**
         * <CODE>true</CODE> if notification by sms is required. <BR/>
         */
        boolean p_sendSms = false;

        /**
         * The oid of the actual receiver. <BR/>
         */
        OID p_oid = null;

        /**
         * The username of the actual receiver. <BR/>
         */
        String p_name = "";

        /**
         * The emailaddress of the actual receiver. <BR/>
         */
        String p_email = "";

        /**
         * The emailaddress of the actual receiver for sending a sms. <BR/>
         */
        String p_smsemail = "";


        /**************************************************************************
         * Get the notification kind and return the value. <BR/>
         *
         * @return  The value.
         */
        public int getNotificationKind ()
        {
            // get the property value and return the result:
            return this.p_notificationKind;
        } // getNotificationKind
        
        
        /**
         * Setter method for p_email.
         *
         * @param pEmail
         */
        public void setEmail (String pEmail)
        {
            p_email = pEmail;
        } // setEmail

    } // class UserNotificationData

} // class NotificationService
