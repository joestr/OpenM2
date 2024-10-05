/*
 * Class: NotificationMessages.java
 */

// package:
package ibs.service.notification;

import ibs.bo.BOMessages;

// imports:


/*******************************************************************************
 * Messages for the ibs.util.notification NotificationService object. <BR/>
 * This abstract class contains all tokens which are necessary to deal with
 * the objects delivered within this package.
 *
 * @version     $Id: NotificationMessages.java,v 1.6 2010/04/07 13:37:17 rburgermann Exp $
 *
 * @author      Monika Eisenkolb (ME) 001127
 *******************************************************************************
 */
public abstract class NotificationMessages extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: NotificationMessages.java,v 1.6 2010/04/07 13:37:17 rburgermann Exp $";

    /**
     * Name of bundle where the messages are included. <BR/>
     */
    public static String MSG_BUNDLE = BOMessages.MSG_BUNDLE;

    // elements
    /**
     * The notification failed. <BR/>
     */
    public static String ML_MSG_NOTIFICATIONFAILED = "ML_MSG_NOTIFICATIONFAILED";

    /**
     * fixed m2 message for the email subject. <BR/>
     */
    public static String ML_MSG_EMAILSMSSUBJECT = "ML_MSG_EMAILSMSSUBJECT";

    /**
     * fixed subject text. <BR/>
     */
    public static String ML_MSG_NOTIFICATIONSUBJECT = "ML_MSG_NOTIFICATIONSUBJECT";

    /**
     * fixed request text. <BR/>
     */
    public static String ML_MSG_NOTIFICATIONREQUEST = "ML_MSG_NOTIFICATIONREQUEST";

    /**
     * fixed remark text. <BR/>
     */
    public static String ML_MSG_NOTIFICATIONREMARK = "ML_MSG_NOTIFICATIONREMARK";

} // class NotificationMessages
