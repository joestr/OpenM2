/*
 * Class: WorkflowInstanceInformation
 */

// package:
package ibs.service.workflow;

// imports:
import ibs.BaseObject;
//KR TODO: unsauber
import ibs.bo.OID;


/******************************************************************************
 * Object information about one workflow instance. <BR/>
 * Only used as return-record in BusinessObject.
 *
 * @version     $Id: WorkflowInstanceInformation.java,v 1.6 2007/07/24 21:27:34 kreimueller Exp $
 *
 * @author      Horst Pichler (HP), 18.10.2000
 ******************************************************************************
 */
public class WorkflowInstanceInformation extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: WorkflowInstanceInformation.java,v 1.6 2007/07/24 21:27:34 kreimueller Exp $";


    /**
     * The instance OID
     */
    public OID instanceId;

    /**
     * The OID of the object
     */
    public OID objectId;

    /**
     * The current state
     */
    public String currentState;

    /**
     * The workflow state
     */
    public String workflowState;

    /**
     * The currentOwner (of this workflow-step)
     */
    public int currentOwnerId;

    /**
     * The processManager
     */
    public int processManagerId;

    /**
     * The starter
     */
    public int starterId;


    /**************************************************************************
     * Creates a RightsList. <BR/>
     */
    public WorkflowInstanceInformation ()
    {
        // nothing to do
    } // WorkflowInstanceInformation

} // class WorkflowInstanceInformation
