/*
 * Class: Condition.java
 */

// package:
package ibs.service.workflow;

// imports:
import ibs.BaseObject;
import ibs.service.workflow.Variables;
import ibs.service.workflow.WorkflowConstants;


/******************************************************************************
 * The Condition-object holds the information about one condition specified
 * in a workflow-transitions IF-context.<BR> The condition holds one
 * expression of the form: lhsValue Operator rhsValue
 *
 * @version     $Id: Condition.java,v 1.7 2007/07/24 21:27:33 kreimueller Exp $
 *
 * @author      Horst Pichler (HP), 2.10.2000
 ******************************************************************************
 */
public class Condition extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Condition.java,v 1.7 2007/07/24 21:27:33 kreimueller Exp $";


    ///////////////////////////////////////////////
    //
    // Information that comes from the XML-definition
    //
    /**
     * lhs-value.<BR> this is the value on left-hand-side of the operator
     */
    public String lhsValue = WorkflowConstants.UNDEFINED;
    /**
     * operator.<BR> this is the operator that connects lhs and rhs-values
     */
    public String operator = WorkflowConstants.UNDEFINED;
    /**
     * rhs-value.<BR> this is the value on right-hand-side of the operator
     */
    public String rhsValue = WorkflowConstants.UNDEFINED;


    /**************************************************************************
     * Creates a Condition object. <BR/>
     */
    public Condition ()
    {
        // nothing to do
    } // Condition


    /**************************************************************************
     * Creates a Condition object. <BR/>
     *
     * @param   lhsValue    the left-hand-side value in the conditions-expression
     * @param   operator    the operator in the conditions-expression
     * @param   rhsValue    the right-hand-side value in the conditions-expression
     */
    public Condition (String lhsValue, String operator, String rhsValue)
    {
        // set values
        this.lhsValue = lhsValue;
        this.operator = operator;
        this.rhsValue = rhsValue;
    } // Condition


    /**************************************************************************
     * Evaluates condition with given variables. <BR/>
     *
     * @param   variables   set of runtime variables; needed for condition
     *                      evaluation
     *
     * @return  true        if condition is true
     *          false       if condition is false, or uncompatible variable-
     *                      types, or wrong condition syntax
     */
    protected boolean evaluateCondition (Variables variables)
    {
        // check if values are set
        if (this.operator == null ||
            this.lhsValue == null ||
            this.rhsValue == null)
        {
            return false;
        } // if
        // check if operator is known
        if (!(this.operator.equalsIgnoreCase (WorkflowConstants.OP_EQUAL) ||
              this.operator.equalsIgnoreCase (WorkflowConstants.OP_NOTEQUAL) ||
              this.operator.equalsIgnoreCase (WorkflowConstants.OP_LESS) ||
              this.operator.equalsIgnoreCase (WorkflowConstants.OP_GREATER) ||
              this.operator.equalsIgnoreCase (WorkflowConstants.OP_LESSEQUAL) ||
              this.operator.equalsIgnoreCase (WorkflowConstants.OP_GREATEREQUAL) ||
              this.operator.equalsIgnoreCase (WorkflowConstants.OP_CONTAINS)))
        {
            return false;
        } // if

        // init variables
        String  operator = new String (this.operator);
        String lhsText = new String (this.lhsValue);
        String rhsText = new String (this.rhsValue);
        boolean isNumber = true;
        Double  lhsNumber = new Double (0);
        Double rhsNumber = new Double (0);

        // replace all eventually existing variables in values
        lhsText = variables.replaceWithValue (lhsText);
        rhsText = variables.replaceWithValue (rhsText);

        // check if both values can be transformed to double
        try
        {
            lhsNumber = new Double (lhsText);
            rhsNumber = new Double (rhsText);
        } // try
        catch (NumberFormatException e)
        {
            // no transformation possible
            isNumber = false;
        } // catch

        // differ between different operators
        if (operator.equalsIgnoreCase (WorkflowConstants.OP_CONTAINS))
        {
            // TEXT: check if lhs contains rhs
            return lhsText.indexOf (rhsText) != -1;
        } // if
        else if (operator.equalsIgnoreCase (WorkflowConstants.OP_EQUAL))
        {
            // differ between NUMBER and TEXT types
            if (isNumber)
            {
                return lhsNumber.doubleValue () == rhsNumber.doubleValue ();
            } // if

            return lhsText.equals (rhsText);
        } // else if
        else if (operator.equalsIgnoreCase (WorkflowConstants.OP_NOTEQUAL))
        {
            // differ between NUMBER and TEXT types
            if (isNumber)
            {
                return lhsNumber.doubleValue () != rhsNumber.doubleValue ();
            } // if

            return !lhsText.equals (rhsText);
        } // else if
        else if (operator.equalsIgnoreCase (WorkflowConstants.OP_LESS))
        {
            // differ between NUMBER and TEXT types
            if (isNumber)
            {
                return lhsNumber.doubleValue () < rhsNumber.doubleValue ();
            } // if

            return lhsText.compareTo (rhsText) < 0;
        } // else if
        else if (operator.equalsIgnoreCase (WorkflowConstants.OP_GREATER))
        {
            // differ between NUMBER and TEXT types
            if (isNumber)
            {
                return lhsNumber.doubleValue () > rhsNumber.doubleValue ();
            } // if

            return lhsText.compareTo (rhsText) > 0;
        } // else if
        else if (operator.equalsIgnoreCase (WorkflowConstants.OP_LESSEQUAL))
        {
            // differ between NUMBER and TEXT types
            if (isNumber)
            {
                return lhsNumber.doubleValue () <= rhsNumber.doubleValue ();
            } // if

            return lhsText.compareTo (rhsText) < 1;
        } // else if
        else if (operator.equalsIgnoreCase (WorkflowConstants.OP_GREATEREQUAL))
        {
            // differ between NUMBER and TEXT types
            if (isNumber)
            {
                return lhsNumber.doubleValue () >= rhsNumber.doubleValue ();
            } // if

            return lhsText.compareTo (rhsText) > -1;
        } // else if

        // exit with no success
        return false;
    } // evaluateCondition


    /**************************************************************************
     * Returns a string representation of this object. <BR/>
     *
     * @return  a string representation of this object
     */
    public String toString ()
    {
        // build string
        String str = "expression = " + this.lhsValue + " " + this.operator +
            " " + this.rhsValue;
        return str;
    } // toString

} // class Condition
