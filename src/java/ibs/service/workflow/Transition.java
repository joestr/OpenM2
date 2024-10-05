/*
 * Class: Transition.java
 */

// package:
package ibs.service.workflow;

// imports:
import ibs.BaseObject;
import ibs.service.workflow.Condition;
import ibs.service.workflow.Variables;
import ibs.service.workflow.WorkflowConstants;

import java.util.Vector;


/******************************************************************************
 * The Transition holds information nextstates, conditional nextstates, etc.
 * and provides method to evaluate transition conditions. <BR/>
 *
 * @version     $Id: Transition.java,v 1.12 2007/07/24 21:27:33 kreimueller Exp $
 *
 * @author      Horst Pichler (HP), 2.10.2000
 ******************************************************************************
 */
public class Transition extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Transition.java,v 1.12 2007/07/24 21:27:33 kreimueller Exp $";


    /**
     * type of this transition. <BR/>
     * possible values:
     *      - SEQUENTIAL        only one possibility
     *      - ALTERNATIVE       nextstate depends on users choice
     *      - CONDITIONAL       nextstate depends on given conditions
     *      - UNDEFINED         will be treated as "SEQUENTIAL"
     */
    public String type = WorkflowConstants.UNDEFINED;

    /**
     * holds all nextstates.<BR> mapped to conditions by position.
     *
     */
    private Vector<String> nextStates = new Vector<String> ();

    /**
     * holds all conditions.<BR> mapped to nextstates by position.
     * condition holds null if not set (e.g. for sequential or
     * alternative transitions). the default/else branch of the
     * conditional-structure will also be initialized with null.
     *
     */
    protected Vector<Condition> conditions = new Vector<Condition> ();

    /**
     * holds the nextstate for sequences or default nextstate for
     * conditionals. <BR/>
     */
    protected String defaultNextState = WorkflowConstants.UNDEFINED;


    /**************************************************************************
     * Creates a Transition-object. <BR/>
     */
    public Transition ()
    {
        // nothing to do
    } // Transition


    /**************************************************************************
     * Creates a Transition-object. <BR/>
     *
     * @param   type    type of Transition
     */
    public Transition (String type)
    {
        this.type = type;
    } // Transition


    /**************************************************************************
     * Tells if this transition is of type 'SEQUENTIAL'. <BR/>
     *
     * @return  <CODE>true</CODE> if the transition is sequential,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean isSequential ()
    {
        if (this.type.equalsIgnoreCase (WorkflowConstants.TRANSITIONTYPE_SEQUENTIAL))
        {
            return true;
        } // if

        return false;
    } // isSequential


    /**************************************************************************
     * Tells if this transition is of type 'ALTERNATIVE'. <BR/>
     *
     * @return  <CODE>true</CODE> if the transition is an alternative,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean isAlternative ()
    {
        if (this.type.equalsIgnoreCase (WorkflowConstants.TRANSITIONTYPE_ALTERNATIVE))
        {
            return true;
        } // if

        return false;
    } // isAlternative


    /**************************************************************************
     * Tells if this transition is of type 'CONDITIONAL'. <BR/>
     *
     * @return  <CODE>true</CODE> if the transition is conditional,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean isConditional ()
    {
        if (this.type.equalsIgnoreCase (WorkflowConstants.TRANSITIONTYPE_CONDITIONAL))
        {
            return true;
        } // if

        return false;
    } // isConditional


    /**************************************************************************
     * Adds a new transition (without condition) to transitions list. <BR/>
     *
     * @param   nextState    name of next state for new transition
     *
     * @return  <CODE>true</CODE>  if transition successfully added,
     *          <CODE>false</CODE> if transition not added
     */
    public boolean addTransition (String nextState)
    {
        // differ between different types of transitions
        if (this.isSequential () || this.isConditional ())
        {
            // sequential or conditional: add default next state
            this.defaultNextState = nextState;
        } // if
        else
        {
            // alternative transition: multiple next states with
            // empty conditions.

            // add
            Condition c = new Condition ();
            this.nextStates.addElement (nextState);
            this.conditions.addElement (c);
        } // else

        // exit with success
        return true;
    } // Transition


    /**************************************************************************
     * Adds a new transition including a condition to transitions list. <BR/>
     *
     * @param   nextstate    name of nextstate for new transition
     * @param   condition    transition-condition object
     *
     * @return  <CODE>true</CODE> if transition successfully added,
     *          <CODE>false</CODE> if transition not added.
     */
    public boolean addTransition (String nextstate,
                                  Condition condition)
    {
        // add transition
        this.nextStates.addElement (nextstate);
        this.conditions.addElement (condition);

        // exit with success
        return true;
    } // Transition


    /**************************************************************************
     * Gets ONE nextstate.<BR> Eventually involved conditions will be
     * evaluated; they probably depend on the given Variables. First nextstate
     * that condition returns true will be returned (according to
     * order in definition).
     *
     * @param   variables   Set of runtime variables; needed for condition
     *                      evaluation.
     *
     * @return  The name of the next state,
     *          <CODE>null</CODE> if wrong transition type (alternative)
     *          or no transitions defined.
     */
    public String getNextState (Variables variables)
    {
        // check if transitions type is alternative
        if (this.type.equalsIgnoreCase (WorkflowConstants.TRANSITIONTYPE_ALTERNATIVE))
        {
            return null;    // more than one state required for this type!
                            // use getAllNextStates!
        } // if

        // check if transition is sequential
        if (this.type.equalsIgnoreCase (WorkflowConstants.TRANSITIONTYPE_SEQUENTIAL))
        {
            // check if default next state is set
            if (this.defaultNextState == null)
            {
                return null;
            } // if
            if (this.defaultNextState.equalsIgnoreCase (WorkflowConstants.UNDEFINED))
            {
                return null;    // exactly one state required!
            } // if

            // return next state
            return this.defaultNextState;
        } // if

        // check if transition is conditional
        if (this.type.equalsIgnoreCase (WorkflowConstants.TRANSITIONTYPE_CONDITIONAL))
        {
            // evaluate conditions and return the first TRUE next state
            return this.evaluateConditions (variables);
        } // if

        // exit with no success
        return null;
    } // getNextState


    /***************************************************************************
     * Gets ALL next states. <BR/>
     * This method should only be used when type of transition is ALTERNATIVE
     * (only in this case more than one next state is possible).
     *
     * @return Vector holds String-objects with name of next states,
     *         <CODE>null</CODE> if no transition defined.
     */
    @SuppressWarnings ("unchecked") // suppress compiler warning
    public Vector<String> getAllNextStates ()
    {
        // create list for next state-entries
        Vector<String> list = null;

        // check if any transitions defined
        if (!this.nextStates.isEmpty ())
        {
            list = (Vector<String>) this.nextStates.clone ();
        } // if

        // exit method
        return list;
    } // getNextState


    /**************************************************************************
     * Get the number of conditions which are defined for this transition. <BR/>
     *
     * @return  The number of conditions.
     */
    public int getConditionCount ()
    {
        // get the number of conditions and return the result:
        return this.conditions.size ();
    } // getConditionCount


    /**************************************************************************
     * Get a specific condition defined through its index number. <BR/>
     * The index number has to be in the range
     * <CODE>0 .. {@link #getConditionCount getConditionCount}</CODE>.
     *
     * @param   position    The position of the condition.
     * @return  The condition.
     */
    public Condition getCondition (int position)
    {
        return this.conditions.elementAt (position);
    } // getCondition


    /**************************************************************************
     * Evaluates given conditions and returns next state for first conditions
     * which results in TRUE. <BR/>
     *
     * @param   variables   set of runtime variables; needed for condition
     *                      evaluation
     *
     * @return  String      the name of the next state,
     *          <CODE>null</CODE> if no condition results in TRUE;
     *                      (in case no default-next state is given).
     */
    private String evaluateConditions (Variables variables)
    {
        Condition condition;

        // loop through all conditions and
        // check if expression is true
        for (int i = 0; i < this.conditions.size (); i++)
        {
            // get next condition
            condition = this.conditions.elementAt (i);

            // check if condition set!
            if (condition != null)
            {
                // if evaluation returns TRUE; return according next state
                if (condition.evaluateCondition (variables))
                {
                    return this.nextStates.elementAt (i);
                } // if
            } // if
        } // while

        // if no condition holds - return default next state entry
        if (this.defaultNextState != null &&
            !this.defaultNextState.equalsIgnoreCase (WorkflowConstants.UNDEFINED))
        {
            return this.defaultNextState;
        } // if

        // exit with no success
        return null;
    } // evaluateConditions


    /**************************************************************************
     * Returns a string representation of this object. <BR/>
     *
     * @return  A string representation of this object.
     */
    public String toString ()
    {
        // define string
        String str = "";
        String conditionsString = WorkflowConstants.UNDEFINED;
        String nextStatesString = WorkflowConstants.UNDEFINED;

        // set strings
        if (this.conditions != null)
        {
            conditionsString = this.conditions.toString ();
        } // if
        if (this.nextStates != null)
        {
            nextStatesString = this.nextStates.toString ();
        } // if

        // build string
        str += "type = " + this.type +
               "; defaultNextState = " + this.defaultNextState +
               "; nextStates = " + nextStatesString +
               "; conditions = " + conditionsString;

        return str;
    } // toString

} // class Transition
