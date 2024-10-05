/*
 * Class: WorkflowMessages.java
 */

// package:
package ibs.service.workflow;

// imports:


/******************************************************************************
 * Messages for ibs applications. <BR/>
 * This abstract class contains all messages which are necessary to deal with
 * the classes delivered within this package. <P>
 * The messages can use tags for specific values to be inserted at runtime.
 * This tags can be replaced by the values with the
 * {@link ibs.util.Helpers#replace (String, String, String)} function.
 *
 * @version     $Id: WorkflowMessages.java,v 1.15 2010/04/07 13:37:16 rburgermann Exp $
 *
 * @author      Horst Pichler (HP), 2.10.2000
 ******************************************************************************
 */
public abstract class WorkflowMessages extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: WorkflowMessages.java,v 1.15 2010/04/07 13:37:16 rburgermann Exp $";

    /**
     * Name of bundle where the messages are included. <BR/>
     */
    public static String MSG_BUNDLE = "ibs_ibsworkflow_messages";

    // header of object properties:

    /**
     * Message to be displayed if a workflow started. <BR/>
     */
    public static String ML_MSG_WORKFLOW_STARTED = "ML_MSG_WORKFLOW_STARTED";

    /**
     * Message to be displayed if an workflow creation & instantiation failed. <BR/>
     */
    public static String ML_MSG_CREATION_FAILED = "ML_MSG_CREATION_FAILED";

    /**
     * Message to be displayed if an object was forwarded. <BR/>
     */
    public static String ML_MSG_OBJECT_FORWARDED = "ML_MSG_OBJECT_FORWARDED";

    /**
     * Message to be displayd if an object could not be forwarded. <BR/>
     */
    public static String ML_MSG_OBJECT_NOT_FORWARDED = "ML_MSG_OBJECT_NOT_FORWARDED";

    /**
     * Message to be displayd if objects (multiple forward) could not be forwarded. <BR/>
     */
    public static String ML_MSG_OBJECTS_NOT_FORWARDED = "ML_MSG_OBJECTS_NOT_FORWARDED";

    /**
     * Message to be displayed if a workflow could be finished. <BR/>
     */
    public static String ML_MSG_WORKFLOW_FINISHED = "ML_MSG_WORKFLOW_FINISHED";

    /**
     * Message to be displayd if a workflow could not be finished scucessfully. <BR/>
     */
    public static String ML_MSG_WORKFLOW_FINISHED_ERROR = "ML_MSG_WORKFLOW_FINISHED_ERROR";

    /**
     * Message to be displayed if a workflow was aborted. <BR/>
     */
    public static String ML_MSG_WORKFLOW_ABORTED = "ML_MSG_WORKFLOW_ABORTED";

    /**
     * Message to be displayed if a workflow could not be aborted. <BR/>
     */
    public static String ML_MSG_WORKFLOW_ABORTED_ERROR = "ML_MSG_WORKFLOW_ABORTED_ERROR";

    /**
     * Message to be displayed if no workflow templates where found. <BR/>
     */
    public static String ML_MSG_NO_TEMPLATES_AVAILABLE = "ML_MSG_NO_TEMPLATES_AVAILABLE";

    /**
     * Message to be displayed if there occurred an error when reading the template file
     * or creating the dom-tree. <BR/>
     */
    public static String ML_MSG_DOMERROR_OR_FILENOTFOUND = "ML_MSG_DOMERROR_OR_FILENOTFOUND";

    /**
     * Message to be displayed if there should be a template, but none found. <BR/>
     */
    public static String ML_MSG_PARSE_ERROR = "ML_MSG_PARSE_ERROR";

    /**
     * Message to be displayed if there the given receiver is no user in the system. <BR/>
     */
    public static String ML_MSG_RECEIVER_NOT_FOUND = "ML_MSG_RECEIVER_NOT_FOUND";

    /**
     * Message to be displayed if there the given receiver container is no object in the system. <BR/>
     */
    public static String ML_MSG_RECEIVERDEST_NOT_FOUND = "ML_MSG_RECEIVERDEST_NOT_FOUND";

    /**
     * Message to be displayed if a reference could not be created. <BR/>
     */
    public static String ML_MSG_CREATELINK_FAILED = "ML_MSG_CREATELINK_FAILED";

    /**
     * Message to be displayed if the owner could not be changed. <BR/>
     */
    public static String ML_MSG_CHANGE_FAILED = "ML_MSG_CHANGE_FAILED";

    /**
     * Message to be displayed if the owner could not be changed. <BR/>
     */
    public static String ML_MSG_CHANGEOWNER_FAILED = "ML_MSG_CHANGEOWNER_FAILED";

    /**
     * Message to be displayed if the owner could not be changed. <BR/>
     */
    public static String ML_MSG_SETRIGHTS_FAILED = "ML_MSG_SETRIGHTS_FAILED";

    /**
     * Message to be displayed if the reference for the process manager could not be created. <BR/>
     */
    public static String ML_MSG_FORWARD_MOVE_FAILED = "ML_MSG_FORWARD_MOVE_FAILED";

    /**
     * Message to be displayed if error in definition. <BR/>
     */
    public static String ML_MSG_GENERAL_DEFINITION_ERROR = "ML_MSG_GENERAL_DEFINITION_ERROR";

    /**
     * Message to be displayed if start state has not been found. <BR/>
     */
    public static String ML_MSG_NO_START_STATE = "ML_MSG_NO_START_STATE";

    /**
     * Message to be displayed if start state has not been defined. <BR/>
     */
    public static String ML_MSG_START_STATE_UNDEF = "ML_MSG_START_STATE_UNDEF";

    /**
     * Message to be displayed if end state has not been defined. <BR/>
     */
    public static String ML_MSG_END_STATE_UNDEF = "ML_MSG_END_STATE_UNDEF";

    /**
     * Message to be displayed if the current state is not set or undefined. <BR/>
     */
    public static String ML_MSG_STATE_NOT_FOUND = "ML_MSG_STATE_NOT_FOUND";

    /**
     * Message to be displayed if the next state of the workflow is not set or undefined. <BR/>
     */
    public static String ML_MSG_NO_NEXT_STATE = "ML_MSG_NO_NEXT_STATE";

    /**
     * General Message to be displayed if workflow control-data inconsistencies. <BR/>
     */
    public static String ML_MSG_INCONSISTENCY_GENERAL = "ML_MSG_INCONSISTENCY_GENERAL";

    /**
     * Message to be displayed if workflow control-data inconsistencies:
     * previous-state = current-State. <BR/>
     */
    public static String ML_MSG_INCONSISTENCY_EQUALSTATES = "ML_MSG_INCONSISTENCY_EQUALSTATES";

    /**
     * Message to be displayed if workflow control-data inconsistencies:
     * last-State must be = current-State. <BR/>
     */
    public static String ML_MSG_INCONSISTENCY_LASTSTATEMANDATORY = "ML_MSG_INCONSISTENCY_LASTSTATEMANDATORY";

    /**
     * Message to be displayed if rights-alias unknown. <BR/>
     */
    public static String ML_MSG_UNKNOWN_RIGHTS_ENTRY = "ML_MSG_UNKNOWN_RIGHTS_ENTRY";

    /**
     * Message to be displayed if no adhoc-receiver selected. <BR/>
     */
    public static String ML_MSG_NOADHOCRECEIVER = "ML_MSG_NOADHOCRECEIVER";

    /**
     * Message to be displayed if no alternative next-states found. <BR/>
     */
    public static String ML_MSG_NO_ALTERNATIVES_FOUND = "ML_MSG_NO_ALTERNATIVES_FOUND";

    /**
     * Message to be displayed if used variable is not defined. <BR/>
     */
    public static String ML_MSG_UNKNOWN_VARIABLE = "ML_MSG_UNKNOWN_VARIABLE";

    /**
     * Message that is displayed if the user wants to forward an object but there are no
     * workflow templates available. <BR/>
     */
    public static String ML_MSG_NOTEMPLATESAVAILABLE = "ML_MSG_NOTEMPLATESAVAILABLE";

    /**
     * Message to be displayed if the forward object can't be found. <BR/>
     */
    public static String ML_MSG_FORWARDOBJECT_NOTFOUND = "ML_MSG_FORWARDOBJECT_NOTFOUND";

    /**
     * Message to be displayed if there should be a template, but none found. <BR/>
     */
    public static String ML_MSG_WORKFLOWTEMPLATE_NOTFOUND = "ML_MSG_WORKFLOWTEMPLATE_NOTFOUND";

    /**
     * Message to be displayed if there should be a Workflow_01, but none found. <BR/>
     */
    public static String ML_MSG_NO_WORKFLOWINSTANCE_FOUND = "ML_MSG_NO_WORKFLOWINSTANCE_FOUND";

    /**
     * Message to be displayed for each workflow-operation. <BR/>
     */
    public static String ML_MSG_WORKFLOW_OPERATION_STARTED = "ML_MSG_WORKFLOW_OPERATION_STARTED";

    /**
     * Message to be displayed if there the given user is no m2 user. <BR/>
     */
    public static String ML_MSG_USER_NOT_FOUND = "ML_MSG_USER_NOT_FOUND";

    /**
     * Message to be displayed if there the given group is no m2 group. <BR/>
     */
    public static String ML_MSG_GROUP_NOT_FOUND = "ML_MSG_GROUP_NOT_FOUND";

    /**
     * Message to be displayed if there the given user is no m2 user. <BR/>
     */
    public static String ML_MSG_SYSTEMUSER_NOT_FOUND = "ML_MSG_SYSTEMUSER_NOT_FOUND";

    /**
     * Message to be displayed if there the given destination is no object in the system. <BR/>
     */
    public static String ML_MSG_DEST_NOT_FOUND = "ML_MSG_DEST_NOT_FOUND";

    /**
     * Message to be displayed if variable exists in definition and not in db (or vice-versa). <BR/>
     */
    public static String ML_MSG_VARIABLEMAPPING_ERROR = "ML_MSG_VARIABLEMAPPING_ERROR";

    /**
     * Message when some users could not be notified. <BR/>
     */
    public static String ML_MSG_NOTIFY_FAILED = "ML_MSG_NOTIFY_FAILED";

    /**
     * Message when workflow-template shall be changed and is used by active workflow-instances. <BR/>
     */
    public static String ML_MSG_WARNING_TEMPLATEINUSE = "ML_MSG_WARNING_TEMPLATEINUSE";

} // class WorkflowMessages
