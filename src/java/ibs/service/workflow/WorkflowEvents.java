/*
 * Class: WorkflowEvents.java
 */

// package:
package ibs.service.workflow;

// imports:


/******************************************************************************
 * Events which can be called in workflow-function. <BR/>
 * This abstract class contains all events which are necessary to deal with
 * the classes delivered within the workflow-gui. <P>
 *
 * @version     $Id: WorkflowEvents.java,v 1.6 2007/07/24 21:27:33 kreimueller Exp $
 *
 * @author      Horst Pichler  (AJ), 001017
 ******************************************************************************
 */
public abstract class WorkflowEvents extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: WorkflowEvents.java,v 1.6 2007/07/24 21:27:33 kreimueller Exp $";


    // forward

    /**
     * Perform the init of a workflow. <BR/>
     */
    public static final int EVT_STARTWORKFLOW   = 1;

    /**
     * Connect a workflow to an object. <BR/>
     */
    public static final int EVT_CONNECTWORKFLOW   = 2;

    /**
     * Perform the forwarding of an object. <BR/>
     */
    public static final int EVT_FORWARD   = 3;

    /**
     * Abort a workflow. <BR/>
     */
    public static final int EVT_ABORTWORKFLOW   = 4;

    /**
     * Pause a workflow. <BR/>
     */
    public static final int EVT_PAUSEWORKFLOW   = 5;

    /**
     * Perform the finalization of a workflow. <BR/>
     */
    public static final int EVT_FINISHWORKFLOW   = 6;

    /**
     * Perform the init of a workflow. <BR/>
     */
    public static final int EVT_STARTNAMEDWORKFLOW   = 7;

    /**
     * Connect a workflow to an object. <BR/>
     */
    public static final int EVT_ADHOCSELECTIONCTRL   = 10;

    /**
     * Connect a workflow to an object. <BR/>
     */
    public static final int EVT_ADHOCSELECTIONOK   = 11;

    /**
     * Selection of alternative nextstate. <BR/>
     */
    public static final int EVT_SELECTALTERNATIVE   = 12;

} // WorkflowEvents
