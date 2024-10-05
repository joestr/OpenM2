/*
 * Class: State.java
 */

// package:
package ibs.service.workflow;

// imports:
import ibs.BaseObject;
import ibs.service.workflow.Receiver;
import ibs.service.workflow.Transition;
import ibs.service.workflow.WorkflowConstants;

import java.util.Vector;


/******************************************************************************
 * The State hold the information of a workflow state. <BR/>
 *
 * @version     $Id: State.java,v 1.16 2007/07/31 19:13:59 kreimueller Exp $
 *
 * @author      Horst Pichler (HP), 2.10.2000
 ******************************************************************************
 */
public class State extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: State.java,v 1.16 2007/07/31 19:13:59 kreimueller Exp $";


    ///////////////////////////////////////////////
    //
    // Information that comes from the XML-definition
    // - State-Header
    // - Receiver
    // - CC
    // - ...

    //
    // TAG: <STATE ... >
    //
    /**
     *  name of a state element. <BR/>
     */
    public String name = WorkflowConstants.UNDEFINED;
    /**
     *  description of a state element. <BR/>
     */
    public String description = WorkflowConstants.UNDEFINED;
    /**
     *  type of a state element. <BR/>
     */
    public String type = WorkflowConstants.UNDEFINED;
    /**
     * tells if a confirmation-message for this state is necessary. <BR/>
     * (overwrites value for whole workflow; only if set)
     */
    public String confirmOperation = WorkflowConstants.UNDEFINED;


    //
    // TAG: <RECEIVER ...>
    //
    /**
     *  the receiver of this state. <BR/>
     */
    public Receiver receiver = new Receiver ();
    /**
     * all selectable users for AD-HOC receiver
     */
    public String adhocUsers = WorkflowConstants.UNDEFINED;
    /**
     * all selectable groups for AD-HOC receiver
     */
    public String adhocGroups = WorkflowConstants.UNDEFINED;

    //
    // TAG: <CC ...>
    //
    /**
     *  vector of CC-receivers. <BR/>
     */
    public Vector<Receiver> ccs = new Vector<Receiver> ();

    //
    // TAG: <ACTION ... >
    //
    /**
     *  non-human actions that will be called in this state. <BR/>
     *  call-order is order in vector.
     */
    public Vector<Action> actions = new Vector<Action> ();

    //
    // TAG: <ACTION ... EXECUTEATSTART="YES">
    //
    /**
     *  non-human actions that will be called in this state
     *  immediatly at start of the state. <BR/>
     *  call-order is order in vector.
     */
    public Vector<Action> executeOnStartActions = new Vector<Action> ();


    //
    // TAG: <TRANSITION ...>, <NEXTSTATE ...>
    //
    /**
     * transition information
     * => information about possible next-states, branching conditions, ...
     */
    public Transition transition = new Transition ();

    //
    // TAG: <NOTIFICATION ...>
    //
    /**
     * notification information
     * => information about user-notification when entering this state;
     *    vector holds Notify-objects
     */
    public Vector<Notify> notification = new Vector<Notify> ();

    //
    // TAG: <MESSAGE ...>
    //
    /**
     *  message of a state element. <BR/>
     */
    public String message = WorkflowConstants.UNDEFINED;

    //
    // TAG: <REGISTEROBSERVERJOB ...>
    //
    /**
     *  ObserverJobs to register. <BR/>
     */
    public Vector<RegisterObserverJob> registerJobs =
        new Vector<RegisterObserverJob> ();

    //
    // TAG: <UNREGISTEROBSERVERJOB ...>
    //
    /**
     *  ObserverJobs to unregister. <BR/>
     */
    public Vector<RegisterObserverJob> unregisterJobs =
        new Vector<RegisterObserverJob> ();


    ///////////////////////////////////////////////
    //
    // Additional information that is set from outside
    //      --> filled/set during workflow-process activities
    //
    /**
     *  Indicates if additional information has been filled/set
     */
    public boolean stateCompleted = false;

    /**
     * indicates that this state holds information about adhocReceiver
     */
    public boolean adhocReceiver = false;


    /**************************************************************************
     * Creates a state. <BR/>
     */
    public State ()
    {
        // nothing to do
    } // State


    /**************************************************************************
     * Returns a string representation of this object. <BR/>
     *
     * @return  a string representation of this object
     */
    public String toString ()
    {
        // declare variables
        String str = "";
        String receiverString = WorkflowConstants.UNDEFINED;
        String ccsString = WorkflowConstants.UNDEFINED;
        String transitionString = WorkflowConstants.UNDEFINED;
        String actionString = WorkflowConstants.UNDEFINED;
        String registerString = WorkflowConstants.UNDEFINED;
        String unregisterString = WorkflowConstants.UNDEFINED;

        // check objects for null-values
        if (this.receiver != null)
        {
            receiverString = "" + this.receiver.toString ();
        } // if
        if (this.ccs != null)
        {
            ccsString = this.ccs.toString ();
        } // if
        if (this.transition != null)
        {
            transitionString = this.transition.toString ();
        } // if
        if (this.actions != null)
        {
            actionString = this.actions.toString ();
        } // if
        if (this.registerJobs != null)
        {
            registerString = this.registerJobs.toString ();
        } // if
        if (this.unregisterJobs != null)
        {
            unregisterString = this.unregisterJobs.toString ();
        } // if

        // build string:
        str += "name = " + this.name + "; " +
               "type = " + this.type + "; " +
               "description = " + this.description + "; " +
               "message = " + this.message + "; " +
               "receiverId = " + receiverString + "; " +
               "adHocReciever = " + this.adhocReceiver + "; " +
               "stateCompleted = " + this.stateCompleted + "; " +
               "ccs = " + ccsString +  "; " +
               "actions = " + actionString +  "; " +
               "transitions = " + transitionString + "; " +
               "registerJobs = " + registerString + "; " +
               "unregisterJobs = " + unregisterString;

        return str;
    } // toString

} // class State
