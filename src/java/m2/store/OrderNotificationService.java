/*
 * Class: OrderNotificationService.java
 */

// package:
package m2.store;

// imports:
import ibs.bo.OID;
import ibs.bo.Operations;
import ibs.bo.States;
import ibs.service.email.EMail;
import ibs.service.email.EMailManager;
import ibs.service.notification.NotificationFailedException;
import ibs.service.notification.NotificationService;
import ibs.service.notification.NotificationTemplate;
import ibs.util.NoAccessException;

import java.util.Vector;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;


/******************************************************************************
 * The NotificationService performs the notification. Notification is possible
 * m2 internally, by email and additionally by sms. The kind of notification can
 * be chosen by a single receiver in the userprofile. The names and the oids
 * of the receivers who could not been notified at all are saved. <BR/>
 *
 * @version     $Id: OrderNotificationService.java,v 1.6 2007/07/31 19:14:02 kreimueller Exp $
 *
 * @author      Monika Eisenkolb (ME)
 ******************************************************************************
 */
public class OrderNotificationService extends NotificationService
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: OrderNotificationService.java,v 1.6 2007/07/31 19:14:02 kreimueller Exp $";


    /**
     * Is this the first distribution of the order or was the order already
     * distributed and there is an additional distribution performed? <BR/>
     */
    private boolean p_firstDistribution = true;


    /**************************************************************************
     * For every receiver a query to the database is done to get the desired
     * kind of notification and the needed adress data.Then the different methods
     * for the special notification are called. <BR/>
     *
     * @param   receivers               The receivers who should receive the
     *                                  notification.
     * @param   allowedNotifications    The allowed notification kinds.
     *                                  (sum of the notification kind values)
     *
     * @return  <CODE>true</CODE> if notification was successful.
     */
    protected boolean doNotification (Vector<OID> receivers, int allowedNotifications)
    {
        boolean res = true;             // return value of the function
        Vector<OID> orderResponsibleReceivers = null; // order responsibles

        // check order state:
        if (!this.p_firstDistribution)
        {
            res = super.doNotification (this.receivers, NOT_ALL, true);
        } // if (!flag)

        // get the order object:
        Order_01 orderObj = new Order_01 ();
        orderObj = (Order_01) this.getObject (this.oid);

        // set order responsibles:
        orderResponsibleReceivers = new Vector<OID> ();
        orderResponsibleReceivers.addElement (orderObj.orderResponsibleOid);
        res = super.doNotification (orderResponsibleReceivers, NOT_M2, true);

        // change processState of order only if order was
        // discarded before distributing

        // only do this for the orderResponsible!
        if (res)
        // if everything was all right and object is order
        // -> change state
        {
            // notification only for orderResponsible:
            boolean oldValue = false;       // saving the catalog.notifyByEmail value
            UserNotificationData userData = null; // the user notification data

            // get data of orders catalog:
            Catalog_01 catalog = orderObj.getOrderCatalogObject ();
            oldValue = catalog.notifyByEmail;

            userData = this.getUserNotificationData (orderObj.orderResponsibleOid);

            // if required the notification is done per email
            if ((userData.getNotificationKind () & NOT_EMAIL) == NOT_EMAIL)
            {
                catalog.notifyByEmail = true;

                // email address of order responsible
                String orderRespMail = "";
                // set orderRespMail
                orderRespMail = orderObj.eMailOfOrderResponsible;
                // create EMail object
                EMail mail = new EMail ();
                // subject
                mail.setSubject (catalog.subject);
                // content
                String weblink = "";
                // add a web link to the content
                weblink = this.getWeblink (orderObj.oid, true);
                mail.setContent (catalog.content + "\n\n" + weblink);
                // receiver
                mail.setReceiver (orderRespMail);
                // sender
                mail.setSender (orderObj.eMailOfCurrentUser);

                try
                {
                    EMailManager.sendMail (mail);
                } // try
                catch (AddressException e)
                {
                    // should not occur
                    throw new RuntimeException (e);
                } // catch
                catch (MessagingException e)
                {
                    // should not occur
                    throw new RuntimeException (e);
                } // catch
            } // if (catalog.notifyByEmail)

            // change the value to the old value
            catalog.notifyByEmail = oldValue;

            // change processState of order only if order was
            // discarded before distributing

            orderObj.retrieveProcessState ();

            if (orderObj.processState == States.PST_DISCARDED)
            {
                orderObj.processState = States.PST_ORDERED;
                orderObj.changeProcessState (Operations.OP_CHANGEPROCSTATE +
                                             orderObj.processState);

                try
                {
                    orderObj.actualizeOrderDate ();
                } // try
                catch (NoAccessException ex)
                {
                    // should not occur
                    throw new RuntimeException (ex);
                } // catch
                // start the order export
                orderObj.exportOrder ();
            } // if (obj.processState == States.PST_DISCARDED)
        } // if (res)

        // returning the result
        return res;
    } // doNotification


    /**************************************************************************
     * at time important interface for orders
     * After setting the receivers, the distributeObjects and the
     * notifyTemplate the different methods that are performing the notification
     * are called. The kind of notification depends on the userprofile and
     * notification data of a single receiver.
     * If the notification failed an exception is thrown. <BR/>
     *
     * @param   receiverOids    Vector of receiver oids.
     * @param   oid             oid of an object.
     * @param   template        NotificationTemplate holding the attributes.
     * @param   flag            <CODE>true</CODE> if an order is distributed right away.
     *                          <CODE>false</CODE> if an order was discarded first.
     *
     * @throws  NotificationFailedException
     *          If an error occurred.
     */
    public void performNotification (Vector<String[]> receiverOids, OID oid,
        NotificationTemplate template, boolean flag)
        throws NotificationFailedException
    {
        // set the flag in a global property:
        this.p_firstDistribution = flag;

        // call corresponding method of super class:
        super.performNotification (receiverOids, oid, template, true);
    } //  performNotification

} // class OrderNotificationService
