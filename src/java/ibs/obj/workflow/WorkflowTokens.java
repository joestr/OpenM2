/*
 * Class: WorkflowTokens.java
 */

// package:
package ibs.obj.workflow;

// imports:


/******************************************************************************
 * Tokens for ibs.workflow business objects. <BR/>
 * This abstract class contains all tokens which are necessary to deal with
 * the business objects delivered within this package.
 *
 * @version     $Id: WorkflowTokens.java,v 1.8 2010/04/07 13:37:09 rburgermann Exp $
 *
 * @author      Horst Pichler (HP), 2.10.2000
 ******************************************************************************
 */
public abstract class WorkflowTokens extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: WorkflowTokens.java,v 1.8 2010/04/07 13:37:09 rburgermann Exp $";

    /**
     * Name of bundle where the tokens included. <BR/>
     */
    public static String TOK_BUNDLE = "ibs_ibsworkflow_tokens";

    // tokens when viewing object Workflow_01
    /**
     * object name (forward). <BR/>
     */
    public static String ML_FORWARD_OBJECT_NAME = "ML_FORWARD_OBJECT_NAME";

    /**
     * template name. <BR/>
     */
    public static String ML_TEMPLATE_NAME = "ML_TEMPLATE_NAME";

    /**
     * startdate. <BR/>
     */
    public static String ML_STARTDATE = "ML_STARTDATE";

    /**
     * enddate. <BR/>
     */
    public static String ML_ENDDATE = "ML_ENDDATE";

    /**
     * state (wfmc). <BR/>
     */
    public static String ML_WORKFLOW_STATE = "ML_WORKFLOW_STATE";

    /**
     * workflowstate (m2-state name). <BR/>
     */
    public static String ML_CURRENT_STATE = "ML_CURRENT_STATE";

    /**
     * workflowstate (m2-state name). <BR/>
     */
    public static String ML_PROC_MGR_NAME = "ML_PROC_MGR_NAME";

    /**
     * workflowstate (m2-state name). <BR/>
     */
    public static String ML_STARTER_NAME = "ML_STARTER_NAME";

    /**
     * workflowstate (m2-state name). <BR/>
     */
    public static String ML_CURRENT_OWNER_NAME = "ML_CURRENT_OWNER_NAME";

    /**
     * log file. <BR/>
     */
    public static String ML_LOGFILE = "ML_LOGFILE";


    //
    // other tokens
    //

    /**
     * Header of output when finishing Workflow on an object. <BR/>
     */
    public static String ML_FINISHING_HEADER = "ML_FINISHING_HEADER";

    /**
     * Header for list of  receivers when forwarding an object. <BR/>
     */
    public static String ML_FORWARD_RECEIVER = "ML_FORWARD_RECEIVER";

    /**
     * Header for list of cc-receivers when forwarding an object. <BR/>
     */
    public static String ML_FORWARD_CC = "ML_FORWARD_CC";

// ----- NEW

    /**
     * workflowstate (m2-state name). <BR/>
     */
    public static String ML_PREVIOUS_STATE = "ML_PREVIOUS_STATE";

    /**
     * Header of output when forwarding an object. <BR/>
     */
    public static String ML_FORWARD_HEADER = "ML_FORWARD_HEADER";

    /**
     * Header for list of cc-receivers when forwarding an object. <BR/>
     */
    public static String ML_ALTERNATIVENEXTSTATE = "ML_ALTERNATIVENEXTSTATE";

    /**
     * Header of output when finishing Workflow on an object. <BR/>
     */
    public static String ML_ALTERNATIVE_HEADER = "ML_ALTERNATIVE_HEADER";

} // class WorkflowTokens
