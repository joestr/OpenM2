/*
 * Class: NotifyMessages.java
 */

// package:
package ibs.bo;

// imports:


/******************************************************************************
 * Messages for the NotifyFunction. <BR/>
 *
 * @version     $Id: NotifyMessages.java,v 1.7 2010/04/07 13:37:08 rburgermann Exp $
 *
 * @author      Monika Eisenkolb (ME), 010123
 ******************************************************************************
 */
public abstract class NotifyMessages extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: NotifyMessages.java,v 1.7 2010/04/07 13:37:08 rburgermann Exp $";

    /**
     * Name of bundle where the messages are included. <BR/>
     */
    public static String MSG_BUNDLE = BOMessages.MSG_BUNDLE;

    // elements
    /**
     * The message tells that the NotifyFunction was sucessfull. <BR/>
     */
    public static String ML_MSG_FUNCTIONSUCCEEDED = "ML_MSG_FUNCTIONSUCCEEDED";

    /**
     * static text for receivers who could not be notified at all. <BR/>
     */
    public static String ML_MSG_FAILEDRECEIVERUSERNAMES = "ML_MSG_FAILEDRECEIVERUSERNAMES";

    /**
     * static text if there were no receivers selected. <BR/>
     */
    public static String ML_MSG_NORECEIVERS = "ML_MSG_NORECEIVERS";

} // class NotifyMessages
