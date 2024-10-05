/*
 * Class: Receiver.java
 */

// package:
package ibs.service.workflow;

// imports:
import ibs.BaseObject;
//KR TODO: unsauber
import ibs.bo.OID;
import ibs.service.user.User;
import ibs.service.workflow.WorkflowConstants;


/******************************************************************************
 * The Receiver hold the information about a workflows RECEIVER or CC-receiver.
 * <BR/>
 *
 * @version     $Id: Receiver.java,v 1.9 2007/07/31 19:13:59 kreimueller Exp $
 *
 * @author      Horst Pichler (HP), 2.10.2000
 ******************************************************************************
 */
public class Receiver extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Receiver.java,v 1.9 2007/07/31 19:13:59 kreimueller Exp $";


    ///////////////////////////////////////////////
    //
    // Information that comes from the XML-definition
    //

    /**
     *  name of receiver. <BR/>
     */
    public String name = WorkflowConstants.UNDEFINED;

    /**
     *  path to destination container. <BR/>
     */
    public String destination =  WorkflowConstants.UNDEFINED;

    /**
     * rights of the receiver for the current state. <BR/>
     */
    public String rights = WorkflowConstants.UNDEFINED;

    /**
     * remaining rights of the receiver when leaving the state. <BR/>
     */
    public String remainRights = WorkflowConstants.UNDEFINED;


    ///////////////////////////////////////////////
    //
    // Additional information that is set from outside
    //      --> filled/set during workflow-process activities
    //

    /**
     *  Indicates if additional information has been filled/set
     */
    public boolean completed = false;

    /**
     *  user: receiver. <BR/>
     *  No DB-access in here: must be filled from outside!!!!!
     */
    public User user = null;

    /**
     *  oid of destination container. <BR/>
     *  No DB-access in here: must be filled from outside!!!!!
     */
    public OID destinationId = null;



    /**************************************************************************
     * Creates a Receiver. <BR/>
     */
    public Receiver ()
    {
        // nothing to do
    } // Receiver


    /**************************************************************************
     * Creates an Receiver. <BR/>
     *
     * @param name        name of the cc receiver
     * @param destination destination of cc receiver
     */
    public Receiver (String name, String destination)
    {
        this.name = name;
        this.destination = destination;
    } // Receiver


    /**************************************************************************
     * Returns a string representation of this object. <BR/>
     *
     * @return      a string representation of this object
     */
    public String toString ()
    {
        // declare variables
        String str = "";
        String destinationIdString = WorkflowConstants.UNDEFINED;
        String userIdString = WorkflowConstants.UNDEFINED;

        // check objects for null-values
        if (this.destinationId != null)
        {
            destinationIdString = this.destinationId.toString ();
        } // if
        if (this.user != null)
        {
            userIdString = "" + this.user.id;
        } // if

        // build string
        str += "name = " + this.name + "; " +
               "destination = " + this.destination + "; " +
               "destinationId = " + destinationIdString + "; " +
               "rights = " + this.rights + "; " +
               "remainRights = " + this.remainRights + "; " +
               "user.id = " + userIdString + "; " +
               "completed = " + this.completed;

        return str;
    } // toString

} // class Receiver
