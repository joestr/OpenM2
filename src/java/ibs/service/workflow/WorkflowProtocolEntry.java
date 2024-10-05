/*
 * Class: WorkflowProtocolEntry
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
 * Object holds one entry of the workflow protocol. <BR/>
 *
 * @version     $Id: WorkflowProtocolEntry.java,v 1.8 2007/07/24 21:27:34 kreimueller Exp $
 *
 * @author      Horst Pichler (HP), 18.10.2000
 ******************************************************************************
 */
public class WorkflowProtocolEntry extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: WorkflowProtocolEntry.java,v 1.8 2007/07/24 21:27:34 kreimueller Exp $";


    /**
     * oid of the workflow instance. <BR/>
     */
    public OID instanceId;

    /**
     * oid of the object. <BR/>
     */
    public OID objectId;

    /**
     * oid of the object. <BR/>
     */
    public String objectName;

    /**
     * name of the current state. <BR/>
     */
    public String currentState;

    /**
     * type of the workflow operation. <BR/>
     */
    public int operationType;

    /**
     * user of the participant who performs the action (e.g. forward). <BR/>
     */
    public User fromParticipant;

    /**
     * user of the participant who receives a workflow object (or reference). <BR/>
     */
    public User toParticipant;

    /**
     * some additional comments or error text. <BR/>
     */
    public String additionalComment;


    /**************************************************************************
     * Creates a WorkflowProtocolEntry. <BR/>
     */
    public WorkflowProtocolEntry ()
    {
        //init class variables
        this.instanceId = null;
        this.objectId = null;
        this.currentState = "";
        this.operationType = WorkflowConstants.OP_UNDEFINED;
        this.fromParticipant = null;
        this.toParticipant = null;
        this.additionalComment = "";
    } // WorkflowProtocolEntry


    /**************************************************************************
     * Creates a WorkflowProtocolEntry. <BR/>
     *
     * @param   instanceId          Id of the object instance.
     * @param   objectId            The object id.
     * @param   objectName          The name of the object.
     * @param   currentState        The current workflow state.
     * @param   operationType       Type of operation.
     * @param   fromParticipant     Starting participant.
     * @param   toParticipant       Addressed participant.
     * @param   additionalComment   Additional comment.
     */
    public WorkflowProtocolEntry (OID instanceId,
                                  OID objectId,
                                  String objectName,
                                  String currentState,
                                  int operationType,
                                  User fromParticipant,
                                  User toParticipant,
                                  String additionalComment)
    {
        // init class variables:
        this.instanceId = instanceId;
        this.objectId = objectId;
        this.objectName = objectName;
        this.currentState = currentState;
        this.operationType = operationType;
        this.fromParticipant = fromParticipant;
        this.toParticipant = toParticipant;
        this.additionalComment = additionalComment;
    } // WorkflowProtocolEntry


    /**************************************************************************
     * Returns a string representation of this object. <BR/>
     *
     * @return  a string representation of this object
     */
    public String toString ()
    {
        return
            "instanceId = " + this.instanceId + "; " +
            "objectId = " + this.objectId + "; " +
            "objectName = " + this.objectName + "; " +
            "currentState = " + this.currentState + "; " +
            "operationType = " + this.operationType + "; " +
            "fromParticipant = " + this.fromParticipant.username + "; " +
            "toParticipant = " + this.toParticipant.username + "; " +
            "additionalComment = " + this.additionalComment;
    } // toString

} // class WorkflowProtocolEntry
