/*
 * Class: EMailMessages.java
 */

// package:
package ibs.service.email;

import ibs.bo.BOMessages;

// imports:


/******************************************************************************
 * Messages for the ibs.util.email EMailManager object. <BR/>
 * This abstract class contains all tokens which are necessary to deal with
 * the objects delivered within this package.
 *
 * @version     $Id: EMailMessages.java,v 1.7 2010/04/07 13:37:16 rburgermann Exp $
 *
 * @author      Monika Eisenkolb (ME) 001127
 ******************************************************************************
 */
public abstract class EMailMessages extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: EMailMessages.java,v 1.7 2010/04/07 13:37:16 rburgermann Exp $";

    /**
     * Name of bundle where the messages are included. <BR/>
     */
    public static String MSG_BUNDLE = BOMessages.MSG_BUNDLE;

    // elements
    /**
     * If no email receiver was found this message is shown. <BR/>
     */
    public static String ML_MSG_NORECEIVER = "ML_MSG_NORECEIVER";

    /**
     * If an incorrect receiver address occurred this message is shown. <BR/>
     */
    public static String ML_MSG_INCORRECTRECEIVER = "ML_MSG_INCORRECTRECEIVER";

    /**
     * If an incorrect sender address occurred this message is shown. <BR/>
     */
    public static String ML_MSG_INCORRECTSENDER = "ML_MSG_INCORRECTSENDER";

} // class EMailMessages
