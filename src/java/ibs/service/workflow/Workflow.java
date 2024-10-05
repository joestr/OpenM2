/*
 * Class: Workflow.java
 */

// package:
package ibs.service.workflow;

// imports:
import ibs.BaseObject;
import ibs.service.workflow.Receiver;
import ibs.service.workflow.State;
import ibs.service.workflow.Variables;
import ibs.service.workflow.WorkflowConstants;

import java.util.Vector;


/******************************************************************************
 * Java representation of workflow definition. <BR/>
 *
 * Holds the following data:
 * 1. Description of the workflow
 *      - workflow header data
 *      - states (including receivers, ...)
 *      - variables ...
 * 2. Methods to instantiate/access attributes like
 *      - states, variables, ...
 *
 * @version     $Id: Workflow.java,v 1.13 2007/07/31 19:13:59 kreimueller Exp $
 *
 * @author      Horst Pichler (HP), 2.10.2000
 ******************************************************************************
 */
public class Workflow extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Workflow.java,v 1.13 2007/07/31 19:13:59 kreimueller Exp $";


    //////////////////////////////////////////////////////////////////////////
    //
    // Representation of the workflow-definitions nodes, tags, attributes, ...
    //

    //
    // TAG: <Workflow ...>
    //
    /**
     * version of the workflow-definition. <BR/>
     */
    public String version = WorkflowConstants.UNDEFINED;
    /**
     * creation-date of the workflow-definition. <BR/>
     */
    public String created = WorkflowConstants.UNDEFINED;
    /**
     * name of the workflow. <BR/>
     */
    public String name = WorkflowConstants.UNDEFINED;
     /**
     * description of this workflow. <BR/>
     */
    public String description = WorkflowConstants.UNDEFINED;
     /**
     * write workflow-operations in log file?. <BR/>
     * DEFAULT: YES (true)
     */
    public String writeLogValue = WorkflowConstants.YES;
    /**
     * write workflow-operations in log file?. <BR/>
     * DEFAULT: YES (true)
     */
    public boolean writeLog = true;
    /**
     * shall the log be displayed during workflow-operation?. <BR/>
     * DEFAULT: YES (=true)
     */
    public String displayLogValue = WorkflowConstants.YES;
    /**
     * shall the log be displayed during workflow-operation?. <BR/>
     * DEFAULT: YES (=true)
     */
    public boolean displayLog = true;
    /**
     * shall an alert-box be displayed after workflow-operation?. <BR/>
     * DEFAULT: YES (=true)
     * (overwritten by confirmation-value of state; if set)
     */
    public String confirmOperationValue = WorkflowConstants.YES;
    /**
     * shall an alert-box be displayed after workflow-operation?. <BR/>
     * DEFAULT: YES (=true)
     */
    public boolean confirmOperation = true;


    /**
     * indicates the ignorerights-settings . <BR/>
     * DEFAULT: NO
     * @deprecated option ist not supported anymore. use setRights instead.

     */
    public boolean ignoreRights = false;

    /**
     * indicates the ignorerights-settings . <BR/>
     * DEFAULT: NO
     */
    public int setRights = WorkflowConstants.SETRIGHTS_DEFAULT;

    /**
     * the path to go to after a transition. <BR/>
     * DEFAULT: go to container of the object
     */
    public String pathAfterTransition = "";

    /**
     * the instances process manager. <BR/>
     * No DB-access in here: must be filled from outside!!!!!
     */
    public Receiver processMgr = new Receiver ();
    /**
     * the instances starter. <BR/>
     * No DB-access in here: must be filled from outside!!!!!
     */
    public Receiver starter = new Receiver ();
    /**
     * the instances current owner. <BR/>
     * No DB-access in here: must be filled from outside!!!!!
     */
    public Receiver currentOwner = new Receiver ();


    /**
     * remaining rights for all other users when initializing workflow. <BR/>
     * DEFAULT: NONE
     */
    public String remainRightsOthers = WorkflowConstants.UNDEFINED;
    /**
     * name of the start state. <BR/>
     */
    public String startStateName = WorkflowConstants.UNDEFINED;
    /**
     * list of names of possible end-states. <BR/>
     * definition of endstate: states where nexstate is END or END-NOCONFIRM
     */
//    public String endStateName = WorkflowConstants.UNDEFINED;
    public Vector<String> endStates = new Vector<String> ();


    //
    // Representation of the workflow-nodes: TAGs <STATE ...>, <VARIABLES ...>
    //
    /**
     * list of run time variables used in the workflow. <BR/>
     */
    public Variables variables = new Variables ();
    /**
     * list of states. <BR/>
     */
    public Vector<State> stateList = new Vector<State> ();


    ///////////////////////////////////////////////
    //
    // Additional information that is set from outside
    //      --> filled/set during workflow-process activities
    //
   /**
     *  Indicates if additional information has been filled/set
     */
    public boolean definitionCompleted = false;


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates the workflow-object. <BR/>
     */
    public Workflow ()
    {
        // nothing to do
    } // constructor


    /**************************************************************************
     * Get state with given name from the stateList. <BR/>
     *
     * @param   stateName   The state name for which to get the state object.
     *
     * @return  the state with the given name,
     *          <CODE>null</CODE> if there occurred an error when retrieving
     *          the state (or state not found).
     */
    public State getState (String stateName)
    {
        State aState = null;

        // get all states defined
        for (int i = 0; i < this.stateList.size (); i++)
        {
            // get the state with index i from the list
            aState = this.stateList.elementAt (i);

            // if the state with index i is the current state then
            // exit method
            if (aState.name.equalsIgnoreCase (stateName))
            {
                return aState;
            } // if
        } // for

        // loop done; state not found; return no state
        return null;
    } // getState


    /**************************************************************************
     * Checks if given state is an end state of this workflow. <BR/>
     *
     * @param stateName     name of the state that shall be checked
     *
     * @return true     the given state is an end state
     *         false    the given state is no end state or not defined
     */
    public boolean isEndState (String stateName)
    {
        String aState;

        // check if list of end-states is valid
        if (this.endStates == null)
        {
            return false;
        } // if

        // loop through all end states and check for equality
        for (int i = 0; i < this.endStates.size (); i++)
        {
            // get the state with index i from the list
            aState = this.endStates.elementAt (i);

            // check if valid
            if (aState != null)
            {
                // check for equality with given state name
                if (aState.equalsIgnoreCase (stateName))
                {
                    return true;
                } // if
            } // if
        } // for

        // loop done; state not found; exit
        return false;
    } // getState


    /**************************************************************************
     * Returns a string representation of this object. <BR/>
     *
     * @return  A string representation of this object.
     */
    public String toString ()
    {
        String str = "";
        String endStatesString = WorkflowConstants.UNDEFINED;

        if (this.endStates != null)
        {
            endStatesString = this.endStates.toString ();
        } // if

        str += "name = " + this.name +
               "; description = " + this.description +
               "; version = " + this.version +
               "; created = " + this.created +
               "; writeLog = " + this.writeLog +
               "; writeLogValue = " + this.writeLogValue +
               "; displayLog = " + this.displayLog +
               "; displayLogValue = " + this.displayLogValue +
               "; confirmOperation = " + this.confirmOperation +
               "; confirmOperationValue = " + this.confirmOperationValue +
               "; ignoreRights = " + this.ignoreRights +
               "; startStateName = " + this.startStateName +
               "; endStates = " + endStatesString +
               "; remainRightsOthers = " + this.remainRightsOthers +
               "; definitionCompleted = " + this.definitionCompleted +
               "; processMgr = " + this.processMgr.toString () +
               "; starter = " + this.starter +
               "; currentOwner = " + this.currentOwner +
               "; stateList = " + this.stateList.toString () +
               "; variables = " + this.variables.toString ();

        return str;
    } // toString

} // class Workflow
