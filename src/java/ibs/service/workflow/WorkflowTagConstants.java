/*
 * Class: WorkflowTagConstants.java
 */

// package:
package ibs.service.workflow;

// imports:


/******************************************************************************
 * All TAG-constants for ibs.workflow business objects. <BR/>
 * This abstract class contains all constants which are necessary to deal with
 * the tags in the workflowdefinition
 *
 * @version     $Id: WorkflowTagConstants.java,v 1.14 2008/03/07 12:38:04 btatzmann Exp $
 *
 * @author      Horst Pichler (HP), 2.10.2000
 ******************************************************************************
 */
public abstract class WorkflowTagConstants extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: WorkflowTagConstants.java,v 1.14 2008/03/07 12:38:04 btatzmann Exp $";


    ////////////////////////////////////
    //
    // XML: TAGS AND ATTRIBUTES
    //      (marked with version v2.0, if only available in 2.0
    //

    //
    // TAG: <WORKFLOW ...>
    //
    /**
     * name of workflow element. <BR/>
     */
    public static final String ELEM_WORKFLOW = "WORKFLOW";
    /**
     * [v2.0] version of the workflow-definition. <BR/>
     */
    public static final String ATTR_WORKFLOWVERSION = "VERSION";
    /**
     * [v2.0] creation-date of the workflow-definition. <BR/>
     */
    public static final String ATTR_WORKFLOWCREATED = "CREATED";
    /**
     * [v2.0] name of the workflow-definition. <BR/>
     */
    public static final String ATTR_WORKFLOWNAME = "NAME";
    /**
     * [v2.0] description for this workflow. <BR/>
     */
    public static final String ATTR_WORKFLOWDESCRIPTION = "DESCRIPTION";
    /**
     * name of current state attribute. <BR/>
     */
    public static final String ATTR_STARTSTATE = "STARTSTATE";
    /**
     * name of current state attribute. <BR/>
     */
    public static final String ATTR_WRITELOG = "WRITELOG";
    /**
     * name of current state attribute. <BR/>
     */
    public static final String ATTR_DISPLAYLOG = "DISPLAYLOG";
    /**
     * name of current state attribute. <BR/>
     */
    public static final String ATTR_CONFIRMOPERATION = "CONFIRMOPERATION";
    /**
     * name of process manager attribute. <BR/>
     */
    public static final String ATTR_PROCESSMGR = "PROCESSMANAGER";
    /**
     * name of process manager destination attribute. <BR/>
     */
    public static final String ATTR_PROCESSMGRDEST = "MANAGERDESTINATION";
    /**
     * rights for process manager on object. <BR/>
     */
    public static final String ATTR_PROCESSMGRRIGHTS = "MANAGERRIGHTS";
    /**
     * rights for starter after initiating the workflow. <BR/>
     */
    public static final String ATTR_REMAINRIGHTSSTARTER = "REMAINRIGHTSSTARTER";
    /**
     * rights for users of already existing links on object. <BR/>
     */
    public static final String ATTR_REMAINRIGHTSOTHERS = "REMAINRIGHTSOTHERS";
    /**
     * indicates if rights-settings shall be used. <BR/>
     * @deprecated option ist not supported anymore. use SETRIGHTS instead.
     */
    public static final String ATTR_IGNORERIGHTS = "IGNORERIGHTS";
    /**
     * different rights-settings. <BR/>
     */
    public static final String ATTR_SETRIGHTS = "SETRIGHTS";
    /**
     * indicates if a action should be executed after transition. <BR/>
     */
    public static final String ATTR_EXECUTEONSTART = "EXECUTEONSTART";
    /**
     * path of an object to navigate to after transition  . <BR/>
     */
    public static final String ATTR_PATHAFTERTRANSITION = "PATHAFTERTRANSITION";


    //
    // TAG: <VARIABLES>
    //

    /**
     * [v2.0] name of variables-element. <BR/>
     */
    public static final String ELEM_VARIABLES = "VARIABLES";


    //
    // [v2.0] TAG: <VARIABLE ... >
    //
    /**
     * [v2.0] name of variable-element. <BR/>
     */
    public static final String ELEM_VARIABLE = "VARIABLE";
    /**
     * [v2.0] name of variable-name element. <BR/>
     */
    public static final String ELEM_VARIABLENAME =
        WorkflowTagConstants.ATTR_WORKFLOWNAME;
    /**
     * [v2.0] name of variable-type element. <BR/>
     */
    public static final String ELEM_VARIABLETYPE = "TYPE";
    /**
     * [v2.0] name of variable-length element. <BR/>
     */
    public static final String ELEM_VARIABLELENGTH = "LENGTH";
    /**
     * [v2.0] name of variable-description element. <BR/>
     */
    public static final String ELEM_VARIABLEDESCRIPTION =
        WorkflowTagConstants.ATTR_WORKFLOWDESCRIPTION;
    /**
     * [v2.0] name of variable-description element. <BR/>
     */
    public static final String ELEM_VARIABLEVALUE = "VALUE";



    //
    // TAG: <ACTION ... >
    //
    /**
     * [v2.0] name of action element. <BR/>
     */
    public static final String ELEM_ACTION = "ACTION";
    /**
     * [v2.0] name of state's type attribute. <BR/>
     */
    public static final String ATTR_ACTIONTYPE =
        WorkflowTagConstants.ELEM_VARIABLETYPE;

    //
    // TAG: <CALL>
    //
    /**
     * [v2.0] name of call element. <BR/>
     */
    public static final String ELEM_CALL = "CALL";


    //
    // TAGS: <INPARAMS>, <OUTPARAMS>, <PARAMETER>
    //
    /**
     * [v2.0] name of inparams element. <BR/>
     */
    public static final String ELEM_INPARAMS = "INPARAMS";
    /**
     * [v2.0] name of inparams element. <BR/>
     */
    public static final String ELEM_OUTPARAMS = "OUTPARAMS";
    /**
     * [v2.0] name of parameter element. <BR/>
     */
    public static final String ELEM_PARAMETER = "PARAMETER";
    /**
     * name attribute for parameters. <BR/>
     */
    public static final String ATTR_FIELDNAME =
        WorkflowTagConstants.ATTR_WORKFLOWNAME;


    //
    // TAG: <STATE ... >
    //
    /**
     * name of state element. <BR/>
     */
    public static final String ELEM_STATE = "STATE";
    /**
     * name of state's name attribute. <BR/>
     */
    public static final String ATTR_STATENAME =
        WorkflowTagConstants.ATTR_WORKFLOWNAME;

    /**
     * name of state's type attribute. <BR/>
     */
    public static final String ATTR_STATETYPE =
        WorkflowTagConstants.ELEM_VARIABLETYPE;
    /**
     * [v2.0] description for this state. <BR/>
     */
    public static final String ATTR_STATEDESCRIPTION =
        WorkflowTagConstants.ATTR_WORKFLOWDESCRIPTION;


    //
    // TAG: <RECEIVER ... >, <CC ...>
    //
    /**
     * name of receiver element. <BR/>
     */
    public static final String ELEM_RECEIVER = "RECEIVER";
    /**
     * name of cc element. <BR/>
     */
    public static final String ELEM_CC = "CC";
    /**
     * name of destination attribute. <BR/>
     */
    public static final String ATTR_DESTINATION  = "DESTINATION";
    /**
     * rights for receiver or cc in current state. <BR/>
     */
    public static final String ATTR_RIGHTS = "RIGHTS";
    /**
     * remain rights for receiver or cc when going to next state. <BR/>
     */
    public static final String ATTR_REMAINRIGHTS = "REMAINRIGHTS";
    /**
     * attribute for selectable groups for ad-hoc states. <BR/>
     */
    public static final String ATTR_GROUPS = "GROUPS";
    /**
     * attribute for selectable users for ad-hoc states. <BR/>
     */
    public static final String ATTR_USERS = "USERS";


    //
    // TAGS: <MESSAGE ... >
    //
    /**
     * name of message element. <BR/>
     */
    public static final String ELEM_MESSAGE = "MESSAGE";


    //
    // TAG: <TRANSITION ... >
    //
    /**
     * [v2.0] name of transition element. <BR/>
     */
    public static final String ELEM_TRANSITION = "TRANSITION";
    /**
     * [v2.0] type of transition. <BR/>
     */
    public static final String ATTR_TRANSITIONTYPE =
        WorkflowTagConstants.ELEM_VARIABLETYPE;
    /**
     * [v1.0] name of nextstate element. <BR/>
     */
    public static final String ELEM_NEXTSTATE = "NEXTSTATE";
    /**
     * [v2.0] if-element for conditional-branching. <BR/>
     */
    public static final String ELEM_IF = "IF";
    /**
     * [v2.0] default condition for conditional-branching. <BR/>
     */
    public static final String ELEM_DEFAULT = "DEFAULT";
    /**
     * [v2.0] condition element for conditional-branching. <BR/>
     */
    public static final String ELEM_CONDITION = "CONDITION";
    /**
     * [v2.0] left-hand-side condition value for conditional-branching. <BR/>
     */
    public static final String ELEM_CONDITIONLHSVALUE = "LHSVALUE";
    /**
     * [v2.0] condition operator for conditional-branching. <BR/>
     */
    public static final String ELEM_CONDITIONOPERATOR = "OPERATOR";
    /**
     * [v2.0] right-hand-side condition value for conditional-branching. <BR/>
     */
    public static final String ELEM_CONDITIONRHSVALUE = "RHSVALUE";



    //
    // TAG: <NOTIFICATION ... >
    //
    /**
     * [v2.0] name notification element. <BR/>
     */
    public static final String ELEM_NOTIFICATION = "NOTIFICATION";
    /**
     * [v2.0] name of notify element. <BR/>
     */
    public static final String ELEM_NOTIFY = "NOTIFY";
    /**
     * [v2.0] name of users element. <BR/>
     */
    public static final String ELEM_NOTIFYUSERS =
        WorkflowTagConstants.ATTR_USERS;
    /**
     * [v2.0] name of users element. <BR/>
     */
    public static final String ELEM_NOTIFYGROUPS =
        WorkflowTagConstants.ATTR_GROUPS;
    /**
     * [v2.0] name of subject element. <BR/>
     */
    public static final String ELEM_NOTIFYSUBJECT = "SUBJECT";
    /**
     * [v2.0] name of content element. <BR/>
     */
    public static final String ELEM_NOTIFYCONTENT = "CONTENT";
    /**
     * [v2.0] name of activity element. <BR/>
     */
    public static final String ELEM_NOTIFYACTIVITY = "ACTIVITY";
    /**
     * [v2.0] name of description element. <BR/>
     */
    public static final String ELEM_NOTIFYDESCRIPTION =
        WorkflowTagConstants.ATTR_WORKFLOWDESCRIPTION;

    //
    // TAG: <ADDITIONALRIGHTS ... >
    //
    /**
     * [v2.0] name additionalrights element. <BR/>
     */
    public static final String ELEM_ADDITIONALRIGHTS = "ADDITIONALRIGHTS";

    /**
     * [v2.0] name groups element. <BR/>
     */
    public static final String ELEM_GROUPS = WorkflowTagConstants.ATTR_GROUPS;


    //
    // TAGS: <REGISTEROBSERVER ... >, <UNREGISTEROBSERVER ... >
    //
    /**
     * [v3.0] REGISTEROBSERVER element. <BR/>
     */
    public static final String ELEM_REGISTEROBSERVER = "REGISTEROBSERVERJOB";
    /**
     * [v3.0] UNREGISTEROBSERVER element. <BR/>
     */
    public static final String ELEM_UNREGISTEROBSERVER = "UNREGISTEROBSERVERJOB";
    /**
     * attribute: id of observerjob. <BR/>
     */
    public static final String ATTR_OBSERVERJOBID = "ID";
    /**
     * attribute: name of observerjob. <BR/>
     */
    public static final String ATTR_OBSERVERJOBNAME =
        WorkflowTagConstants.ATTR_WORKFLOWNAME;
    /**
     * attribute: name of observer. <BR/>
     */
    public static final String ATTR_OBSERVER = "OBSERVER";
    /**
     * attribute: classname of observerjob. <BR/>
     */
    public static final String ATTR_OBSERVERJOBCLASS = "CLASS";

} // class WorkflowTagConstants
