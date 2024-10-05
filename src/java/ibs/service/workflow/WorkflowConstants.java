/*
 * Class: WorkflowConstants.java
 */

// package:
package ibs.service.workflow;

// import:
import java.io.File;


/******************************************************************************
 * Constants for ibs.workflow business objects. <BR/>
 * This abstract class contains all tokens which are necessary to deal with
 * the business objects delivered within this package.
 *
 * @version     $Id: WorkflowConstants.java,v 1.11 2007/07/24 21:27:33 kreimueller Exp $
 *
 * @author      Horst Pichler (HP), 2.10.2000
 ******************************************************************************
 */
public abstract class WorkflowConstants extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: WorkflowConstants.java,v 1.11 2007/07/24 21:27:33 kreimueller Exp $";


    //
    // PATH
    //

    /**
     * path to log files: system
     */
    public static final String LOG_ABSPATH_PREFIX = "workflow" +
                                                    File.separator +
                                                    "log" +
                                                    File.separator;

    /**
     * path to log files: web
     */
    public static final String LOG_WEBPATH_PREFIX = "workflow/log/";


    // common tokens:
    /**
     * No rights. <BR/>
     */
    private static final String RIGHTS_NONE = "NONE";
    /**
     * All rights. <BR/>
     */
    private static final String RIGHTS_ALL = "ALL";
    /**
     * Read rights. <BR/>
     */
    private static final String RIGHTS_READ = "READ";

    //
    // RIGHTS, STATE, OPERATION CONSTANTS
    //

    // WORKFLOW OPERATIONS:
    /**
     * Workflow operation: undefined. <BR/>
     */
    public static final int OP_UNDEFINED            = 0;
    /**
     * Workflow operation: send to receiver. <BR/>
     */
    public static final int OP_SENTTORECEIVER       = 1;
    /**
     * Workflow operation: send to cc user (s). <BR/>
     */
    public static final int OP_SENTTOCC             = 2;
    /**
     * Workflow operation: send to application. <BR/>
     */
    public static final int OP_SENTTOAPPLICATION    = 3;
    /**
     * Workflow operation: start workflow. <BR/>
     */
    public static final int OP_START                = 10;
    /**
     * Workflow operation: complete workflow. <BR/>
     */
    public static final int OP_COMPLETE             = 11;
    /**
     * Workflow operation: abort workflow. <BR/>
     */
    public static final int OP_ABORT                = 12;
    /**
     * Workflow operation: terminate workflow. <BR/>
     */
    public static final int OP_TERMINATE            = 13;


    // WORKFLOW RIGHTS ALIASES
    /**
     * Workflow rights alias: NONE. <BR/>
     */
    public static final String RIGHTS_ALIAS_NONE =
        WorkflowConstants.RIGHTS_NONE;
    /**
     * Workflow rights alias: READ rights. <BR/>
     */
    public static final String RIGHTS_ALIAS_READ =
        WorkflowConstants.RIGHTS_READ;
    /**
     * Workflow rights alias: CREATE rights. <BR/>
     */
    public static final String RIGHTS_ALIAS_CREATE    = "CREATE";
    /**
     * Workflow rights alias: CHANGE rights. <BR/>
     */
    public static final String RIGHTS_ALIAS_CHANGE    = "CHANGE";
    /**
     * Workflow rights alias: CHANGE and DELETE rights. <BR/>
     */
    public static final String RIGHTS_ALIAS_CHANGEDELETE = "CHANGEDELETE";
    /**
     * Workflow rights alias: ALL rights. <BR/>
     */
    public static final String RIGHTS_ALIAS_ALL = WorkflowConstants.RIGHTS_ALL;


    // WORKFLOW RIGHTS - DEFAULTS RIGHTS FOR TAGS:
    /**
     * Default workflow rights for tags: manage rights. <BR/>
     */
    public static final String DEFAULT_MANAGERRIGHTS           =
        WorkflowConstants.RIGHTS_READ;
    /**
     * Default workflow rights for tags: remain rights for starter. <BR/>
     */
    public static final String DEFAULT_REMAINRIGHTSSTARTER     =
        WorkflowConstants.RIGHTS_READ;
    /**
     * Default workflow rights for tags: remaining rights for others. <BR/>
     */
    public static final String DEFAULT_REMAINRIGHTSOTHERS      =
        WorkflowConstants.RIGHTS_NONE;
    /**
     * Default workflow rights for tags: receiver rights. <BR/>
     */
    public static final String DEFAULT_RECEIVERRIGHTS          =
        WorkflowConstants.RIGHTS_ALL;
    /**
     * Default workflow rights for tags: remaining rights for receiver. <BR/>
     */
    public static final String DEFAULT_RECEIVERREMAINRIGHTS    =
        WorkflowConstants.RIGHTS_READ;
    /**
     * Default workflow rights for tags: rights for cc user (s). <BR/>
     */
    public static final String DEFAULT_CCRIGHTS                =
        WorkflowConstants.RIGHTS_READ;
    /**
     * Default workflow rights for tags: remaining rights for cc user (s). <BR/>
     */
    public static final String DEFAULT_CCREMAINRIGHTS          =
        WorkflowConstants.RIGHTS_NONE;

    // WFMC WORKFLOW STATES (WFMC = Workflow Management Coalition)
    // The following states are a not in the WfMC standard:
    // - 'open.running.lastState'
    /**
     * Workflow state according to WfMC: OPEN. <BR/>
     */
    public static final String STATE_OPEN                          = "open";
    /**
     * Workflow state according to WfMC: OPEN + RUNNING. <BR/>
     */
    public static final String STATE_OPEN_RUNNING                  = "open.running";
    /**
     * Workflow state: OPEN + RUNNING + LASTSTATEREACHED. <BR/>
     * This state is not part of the WfMC standard.
     */
    public static final String STATE_OPEN_RUNNING_LASTSTATE        = "open.running.lastStateReached";
    /**
     * Workflow state according to WfMC: OPEN + NOTRUNNING. <BR/>
     */
    public static final String STATE_OPEN_NOTRUNNING               = "open.notRunning";
    /**
     * Workflow state according to WfMC: OPEN + NOTRUNNING + NOTSTARTED. <BR/>
     */
    public static final String STATE_OPEN_NOTRUNNING_NOTSTARTED    = "open.notRunning.notStarted";
    /**
     * Workflow state according to WfMC: OPEN + NOTRUNNING + SUSPENDED. <BR/>
     */
    public static final String STATE_OPEN_NOTRUNNING_SUSPENDED     = "open.notRunning.suspended";
    /**
     * Workflow state according to WfMC: CLOSED. <BR/>
     */
    public static final String STATE_CLOSED                        = "closed";
    /**
     * Workflow state according to WfMC: CLOSED + ABORTED. <BR/>
     */
    public static final String STATE_CLOSED_ABORTED                = "closed.aborted";
    /**
     * Workflow state according to WfMC: CLOSED + TERMINATED. <BR/>
     */
    public static final String STATE_CLOSED_TERMINATED             = "closed.terminated";
    /**
     * Workflow state according to WfMC: CLOSED. <BR/>
     */
    public static final String STATE_CLOSED_COMPLETED              = "closed.completed";

    /**
     * COMMON STATE: UNDEFINED
     */
    public static final String STATE_UNDEFINED = WorkflowConstants.UNDEFINED;


    //
    // STRING CONSTANTS
    //

    /**
     * text for yes
     */
    public static final String YES = "YES";

    /**
     * text for NO
     */
    public static final String NO = "NO";

    // Text tokens for SETRIGHTS entries:
    /**
     * Text token for SETRIGHTS entries: NONE. <BR/>
     */
    public static final String TOK_SETRIGHTS_NONE       =
        WorkflowConstants.RIGHTS_NONE;
    /**
     * Text token for SETRIGHTS entries: DEFAULT. <BR/>
     */
    public static final String TOK_SETRIGHTS_DEFAULT    = "DEFAULT";
    /**
     * Text token for SETRIGHTS entries: INHERIT. <BR/>
     */
    public static final String TOK_SETRIGHTS_INHERIT    = "INHERIT";

    // setrights options:
    /**
     * SETRIGHTS option: NONE. <BR/>
     */
    public static final int SETRIGHTS_NONE       = 1;
    /**
     * SETRIGHTS option: DEFAULT. <BR/>
     */
    public static final int SETRIGHTS_DEFAULT    = 0;
    /**
     * SETRIGHTS option: INHERIT. <BR/>
     */
    public static final int SETRIGHTS_INHERIT    = 2;


    /**
     * common text for undefined entries
     */
    public static final String UNDEFINED = "UNDEFINED";

    // Text for statetype-entries:
    /**
     * Text for statetype-entries: NODE. <BR/>
     */
    public static final String STATETYPE_NODE          = "NODE";
    /**
     * Text for statetype-entries: HUMAN. <BR/>
     */
    public static final String STATETYPE_HUMAN         = "HUMAN";
    /**
     * Text for statetype-entries: APPLICATION. <BR/>
     */
    public static final String STATETYPE_APPLICATION   = "APPLICATION";

    // Text for transition-type-entries:
    /**
     * Text for transition-type-entries: SEQUENTIAL. <BR/>
     */
    public static final String TRANSITIONTYPE_SEQUENTIAL  = "SEQUENTIAL";
    /**
     * Text for transition-type-entries: ALTERNATIVE. <BR/>
     */
    public static final String TRANSITIONTYPE_ALTERNATIVE = "ALTERNATIVE";
    /**
     * Text for transition-type-entries: CONDITIONAL. <BR/>
     */
    public static final String TRANSITIONTYPE_CONDITIONAL = "CONDITIONAL";

    // Text for action-type-entries:
    /**
     * Text for action-type-entries: XPATH. <BR/>
     */
    public static final String ACTIONTYPE_XPATH        = "XPATH";
    /**
     * Text for action-type-entries: QUERY. <BR/>
     */
    public static final String ACTIONTYPE_QUERY        = "QUERY";
    /**
     * Text for action-type-entries: INTERNALCALL. <BR/>
     */
    public static final String ACTIONTYPE_INTERNALCALL = "INTERNALCALL";
    /**
     * Text for action-type-entries: EXTERNALCALL. <BR/>
     */
    public static final String ACTIONTYPE_EXTERNALCALL = "EXTERNALCALL";
    /**
     * Text for action-type-entries: EXPORT. <BR/>
     */
    public static final String ACTIONTYPE_EXPORT       = "EXPORT";

    // Workflow Variable types:
    /**
     * Workflow Variable type: TEXT. <BR/>
     */
    public static final String VARIABLETYPE_TEXT          = "TEXT";
    /**
     * Workflow Variable type: NUMBER. <BR/>
     */
    public static final String VARIABLETYPE_NUMBER        = "NUMBER";

    // Workflow Variable types-length:
    /**
     * Workflow Variable types-length: TEXT. <BR/>
     */
    public static final String VARIABLETYPELENGTH_TEXT          = "255";
    /**
     * Workflow Variable types-length: TEXT. <BR/>
     */
    public static final String VARIABLETYPELENGTH_NUMBER        = "16";

    // Workflow conditional operators:
    /**
     * Workflow conditional operator: EQUAL. <BR/>
     */
    public static final String OP_EQUAL            = "EQUAL";
    /**
     * Workflow conditional operator: NOTEQUAL. <BR/>
     */
    public static final String OP_NOTEQUAL         = "NOTEQUAL";
    /**
     * Workflow conditional operator: LESS. <BR/>
     */
    public static final String OP_LESS             = "LESS";
    /**
     * Workflow conditional operator: GREATER. <BR/>
     */
    public static final String OP_GREATER          = "GREATER";
    /**
     * Workflow conditional operator: LESSEQUAL. <BR/>
     */
    public static final String OP_LESSEQUAL        = "LESSEQUAL";
    /**
     * Workflow conditional operator: GREATEREQUAL. <BR/>
     */
    public static final String OP_GREATEREQUAL     = "GREATEREQUAL";
    /**
     * Workflow conditional operator: CONTAINS. <BR/>
     */
    public static final String OP_CONTAINS         = "CONTAINS";


    // Workflow error part:
    /**
     * Workflow error part: ERRORCODE. <BR/>
     */
    public static final String VARIABLE_ERRORCODE          = "ERRORCODE";
    /**
     * Workflow error part: ERRORMESSAGE. <BR/>
     */
    public static final String VARIABLE_ERRORMESSAGE       = "ERRORMESSAGE";


    /**
     * Text for nextstate-ELEMENT in last states definition
     */
    public static final String LASTSTATEENTRY = "END";

    /**
     * Text for nextstate-ELEMENT in last states definition
     * if automatic finalization shall be performed
     */
    public static final String LASTSTATEENTRY_NOCONFIRM = "END-NOCONFIRM";

    // Text for xml-definition versions:
    /**
     * Text for xml-definition version: 1.0. <BR/>
     */
    public static final String VERSION10 = "V1.0";
    /**
     * Text for xml-definition version: 2.0. <BR/>
     */
    public static final String VERSION20 = "V2.0";

    //
    // RUNTIMTE VARIABLES & PLACEHOLDERS
    //

    /**
     * Prefix for runtime variables
     */
    public static final String RUNTIME_PREFIX = "#VARIABLE.";

    /**
     * Postfix for runtime variables
     */
    public static final String RUNTIME_POSTFIX = "#";

    /**
     * Runtime variable for user who starts the workflow
     */
    public static final String RUNTIME_STARTER = "#STARTER#";

    /**
     * Runtime variable for user who starts the workflow
     */
    public static final String RUNTIME_STARTERCONTAINER = "#STARTERCONTAINER#";

    /**
     * Placeholder for AD-HOC-selected users
     */
    public static final String RUNTIME_ADHOC = "#AD-HOC#";

    /**
     * Runtime variable for user who is processmanager of workflow
     */
    public static final String RUNTIME_PROCESSMANAGER = "#PROCESSMANAGER#";

    /**
     * Runtime variable for destination-container of user who is
     * processmanager of workflow
     */
    public static final String RUNTIME_PROCESSMANAGERCONTAINER = "#PROCESSMANAGERCONTAINER#";

    /**
     * Runtime variable for receiver of object
     */
    public static final String RUNTIME_RECEIVER = "#RECEIVER#";

    /**
     * Runtime variable for all cc-receivers of object
     */
    public static final String RUNTIME_CCS = "#CCS#";

    /**
     * Runtime variable for current owner of object in workflow
     */
//    public static final String RUNTIME_CURRENTOWNER = "#CURRENTOWNER#";

    /**
     * Runtime variable for container where object currently resides.
     */
//    public static final String RUNTIME_CURRENTCONTAINER = "#CURRENTCONTAINER#";

} // class WorkflowConstants
