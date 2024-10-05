package ibs.service.notification;

import ibs.bo.OID;
import ibs.di.XMLViewer_01;
import ibs.io.Environment;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.service.user.User;

import java.util.Vector;

public interface INotificationService
{

    /*************************************************************************
     * Initializes a Function. <BR/>
     *
     * The user object is also stored in a specific
     * property of this object to make sure that the user's context can be used
     * for getting his/her rights. <BR/>
     * {@link #env env} is initialized to the provided object. <BR/>
     * {@link #sess sess} is initialized to the provided object. <BR/>
     * {@link #app app} is initialized to the provided object. <BR/>
     *
     * @param   aUser   Object representing the user.
     * @param   aEnv    The actual environment.
     * @param   aSess   The actual session info object.
     * @param   aApp    The global application info object.
     */
    public void initService (User aUser, Environment aEnv, SessionInfo aSess,
            ApplicationInfo aApp); // initFunction


    /**************************************************************************
     * The oids of the receivers who could not be notified at all
     * are returned. <BR/>
     *
     * @return  A string with the receiver oids.
     */
    public String getFailedReceiverOids (); // getFailedReceiverOids


    /**************************************************************************
     * The names of the receivers who could not be notified at all
     * are returned. <BR/>
     *
     * @return  A string with the receiver names concatenated with ";".
     */
    public String getFailedReceiverUserNames (); // getFailedReceiverUserNames


    /**************************************************************************
     * Send a notification via email. <BR/>
     * Note that this is a public wrapper method for the
     * {@link #emailNotification (NotificationTemplate, Vector, String, String)
     * emailNotification ()} method. <BR/>
     *
     * @param   template            The notification template that holds the
     *                              settings for the notification.
     * @param   distributeObjects   A Vector containing the oids of the objects
     *                              to distribute.
     * @param   sender              The email address of the sender.
     * @param   receiver            The email address of the receiver.
     *
     * @return  <CODE>true</CODE> if notification was done sucessfully.
     */
    public boolean emailNotification (NotificationTemplate template,
            Vector<OID> distributeObjects, String sender, String receiver); // emailNotification


    /**************************************************************************
     * After setting the receivers, the distributeObjects and the
     * notifyTemplate the different methods that are performing the notification
     * are called. The kind of notification depends on the userprofile and
     * notification data of a single receiver.
     * If the notification failed an exception is thrown. <BR/>
     *
     * @param   receiverOids    Vector of receiver oids.
     * @param   oid             Oid of an object.
     * @param   template        NotificationTemplate holding the attributes.
     * @param   createOutboxEntry Shall an outbox entry be created, too?
     *
     * @throws  NotificationFailedException
     *          If an error occurred.
     */
    public void performNotification (Vector<String []> receiverOids, OID oid,
            NotificationTemplate template, boolean createOutboxEntry)
            throws NotificationFailedException; //  performNotification


    /***************************************************************************
     * After setting the receivers, the distributeObjects and the
     * notifyTemplate the different methods that are performing the notification
     * are called.
     * The kind of notification depends on the userprofile and
     * notification data of a single receiver.
     * If the notification failed an exception is thrown. <BR/>
     *
     * @param   receiverOids    Vector of receiver oids
     * @param   objectOids      array of object oids
     * @param   template        NotificationTemplate holding the attributes
     * @param   createOutboxEntry Shall an outbox entry be created, too?
     *
     * @throws NotificationFailedException
     *                            if an error occurred
     */
    public void performNotification (Vector<String []> receiverOids,
            OID [] objectOids, NotificationTemplate template,
            boolean createOutboxEntry) throws NotificationFailedException; // performNotification


    /**************************************************************************
     * After setting the receivers, the distributeObjects and the
     * notifyTemplate the different methods that are performing the notification
     * are called.
     * The kind of notification depends on the userprofile and
     * notification data of a single receiver.
     * If the notification failed an exception is thrown. <BR/>
     *
     * @param   receiverOids    Vector of receiver oids
     * @param   objectOids      Vector of object oids
     * @param   template        NotificationTemplate holding the attributes
     * @param   createOutboxEntry Shall an outbox entry be created, too?
     *
     * @throws  NotificationFailedException
     *          if an error occurred
     */
    public void performNotification (Vector<OID> receiverOids,
            Vector<OID> objectOids, NotificationTemplate template,
            boolean createOutboxEntry) throws NotificationFailedException; // performNotification


    /**************************************************************************
     * Performs distribution of an object. <BR/>
     *
     * @param   oid         Object id of the required business object.
     * @param   receiver    oid of a single receiver.
     * @param   subject     Subject of message to be distributed.
     * @param   description Description of the message to be distributed.
     * @param   activities  Activities attached to the message to be
     *                      distributed.
     * @param   freeze      Shall the object be frozen after distribution.
     * @param   createOutboxEntry Shall an outbox entry be created, too?
     *
     * @return  The OID of the ReceivedObject which was created.
     */
    public OID objectDistribute (OID oid, OID receiver, String subject,
            String description, String activities, boolean freeze,
            boolean createOutboxEntry); // objectDistribute


    /**************************************************************************
     * Create the ReceivedObject entry in inbox. <BR/>
     *
     * @param   distributedOid  Oid of the object which was distributed.
     * @param   distributedType Type of distributed object.
     * @param   distributedTypeName Name of type.
     * @param   receiver    oid of a single receiver.
     * @param   sentObjectOid Oid of SentObject entry.
     * @param   subject     Subject of message to be distributed.
     * @param   description Description of the message to be distributed.
     * @param   activities  Activities attached to the message to be
     *                      distributed.
     *
     * @return  The OID of the ReceivedObject which was created.
     */
    public OID createReceivedObject (OID distributedOid, int distributedType,
            String distributedTypeName, OID receiver, OID sentObjectOid,
            String subject, String description, String activities); // createReceivedObject
    
    
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
    public void updateDistributedObjectName (String subject, String activity,
    		XMLViewer_01 distributedObject);

}
