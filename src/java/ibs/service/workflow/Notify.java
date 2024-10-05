/*
 * Class: Notify.java
 */

// package:
package ibs.service.workflow;

// imports:
import ibs.BaseObject;
import ibs.bo.OID;
import ibs.service.workflow.WorkflowConstants;

import java.util.Vector;


/******************************************************************************
 * The Notify-object holds the information about one notification-action. <BR/>
 * This includes the users who shall be notified and the notification-messages.
 *
 * @version     $Id: Notify.java,v 1.7 2008/03/07 12:38:04 btatzmann Exp $
 *
 * @author      Horst Pichler (HP), 2.10.2000
 ******************************************************************************
 */
public class Notify extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Notify.java,v 1.7 2008/03/07 12:38:04 btatzmann Exp $";


    ///////////////////////////////////////////////
    //
    // Information that comes from the XML-definition
    //
    /**
     * list of users; user names stored in one comma-separated string. <BR/>
     */
    public String users = WorkflowConstants.UNDEFINED;
    /**
     * list of groups; group names stored in one comma-separated string. <BR/>
     */
    public String groups = WorkflowConstants.UNDEFINED;
    /**
     *  the subject of the message. <BR/>
     */
    public String subject = WorkflowConstants.UNDEFINED;
    /**
     *  the content of the message. <BR/>
     */
    public String content = WorkflowConstants.UNDEFINED;
    /**
     *  the description of the message. <BR/>
     */
    public String description = WorkflowConstants.UNDEFINED;
    /**
     *  the activtiy defined for the message. <BR/>
     */
    public String activity = WorkflowConstants.UNDEFINED;


    ///////////////////////////////////////////////
    //
    // Additional information that is set from outside
    //      --> filled/set during workflow-process activities
    //
    /**
     * list of user-oids; oid-mapping to list of users. <BR/>
     */
    public Vector<OID> userOIDs = new Vector<OID> ();
    /**
     * list of group-oids; oid-mapping to list of users. <BR/>
     */
    public Vector<OID> groupOIDs = new Vector<OID> ();

    /**************************************************************************
     * Creates a Notify-object. <BR/>
     */
    public Notify ()
    {
        // nothing to do
    } // Receiver

    /**************************************************************************
     * Returns a string representation of this object. <BR/>
     *
     * @return      a string representation of this object
     */
    public String toString ()
    {
        // build string
        String str =
            "users = " + this.users.toString () +
            ";groups = " + this.groups +
            ";subject = " + this.subject +
            ";content = " + this.content +
            ";activity = " + this.activity +
            ";description = " + this.description +
            ";userOIDs = " + this.userOIDs.toString ();
        return str;
    } // toString

} // class Notify
