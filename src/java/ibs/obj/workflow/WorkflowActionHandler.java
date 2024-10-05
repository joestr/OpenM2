/*
 * Class: WorkflowActionHandler.java
 */

// package:
package ibs.obj.workflow;

// imports:
import ibs.bo.BusinessObject;
import ibs.obj.query.QueryExecutive;
import ibs.service.action.ActionParameter;
import ibs.service.workflow.Action;
import ibs.service.workflow.Variable;
import ibs.service.workflow.Variables;
import ibs.service.workflow.WorkflowConstants;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Vector;


/******************************************************************************
 * Wrapper to handle workflow actions. Calls Action-Implementations depending
 * on type of given action. <BR/>
 *
 * @version     $Id: WorkflowActionHandler.java,v 1.12 2009/07/24 18:22:04 kreimueller Exp $
 *
 * @author      Horst Pichler (HP), 2.10.2000
 ******************************************************************************
 */
public class WorkflowActionHandler extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: WorkflowActionHandler.java,v 1.12 2009/07/24 18:22:04 kreimueller Exp $";


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates an WorkflowActionHandler Object. <BR/>
     */
    public WorkflowActionHandler ()
    {
        // nothing to do
    } // constructor


    /**************************************************************************
     * Perform the given action for the given object. <BR/> Read and store
     * parameters in variable object (if needed).
     * The fixed variables ERRORCODE and ERRORMESSAGE will also be stored in
     * the given variables list (default: 0, OK).
     *
     * @param obj       forwarded object of workflow instance
     * @param action    holds the action incl. in/outparameters
     * @param variables list of variables for in/outparameters
     *
     * @return  <CODE>true</CODE>    action executed without error
     *          <CODE>false</CODE>   error occured (see ERRORCODE, ERRORMESSAGE)
     */
    protected boolean performAction (BusinessObject obj,
                                     Action action,
                                     Variables variables)
    {
        // local variables
        boolean success = false;

        // call method depending on type of action
        if (action.type.equalsIgnoreCase (WorkflowConstants.ACTIONTYPE_XPATH))
        {
            success = this.performXPathAction (obj, action, variables);
        } // if
        else if (action.type
            .equalsIgnoreCase (WorkflowConstants.ACTIONTYPE_INTERNALCALL))
        {
            success = this.performInternalCallAction (obj, action, variables);
        } // else if
        else if (action.type
            .equalsIgnoreCase (WorkflowConstants.ACTIONTYPE_QUERY))
        {
            success = this.performQueryAction (obj, action, variables);
        } // else if
/* KR 20060421 the following two conditions bodies are commented out, so I
 * dropped the complete conditions.
        else if (action.type.equalsIgnoreCase(WorkflowConstants.ACTIONTYPE_EXTERNALCALL))
            ; // success = (obj, action, variables);
        else if (action.type.equalsIgnoreCase(WorkflowConstants.ACTIONTYPE_EXPORT))
            ; // success = (obj, action, variables);
*/
        else
        {
            // unknown type of action - add errorcode, errormessage to variables
            (variables.getEntry (WorkflowConstants.VARIABLE_ERRORCODE)).value = "-1";
            (variables.getEntry (WorkflowConstants.VARIABLE_ERRORMESSAGE)).value
                = "UNKNOWN Action-TYPE: " + action.type;
        } // else

        // exit
        return success;
    } // performAction


    /**************************************************************************
     * Each occurence of #VARIABLE.xxx# in inparameters of given action will
     * be replaced with value of according action. <BR/>
     *
     * @param action    holds the action incl. in/outparameters
     * @param variables list of variables for in/outparameters
     */
    protected void replaceVariablesInParams (Action action,
                                             Variables variables)
    {
        // declare new in-parameters for action
        Vector<ActionParameter> newInParams = new Vector<ActionParameter> ();
        ActionParameter newInParam;
        ActionParameter oldInParam;
        String name = WorkflowConstants.UNDEFINED;
        String field = WorkflowConstants.UNDEFINED;

        // set variables in input-parameters (if any found)
        // iterate through in-params
        for (int i = 0; i < action.inParams.size (); i++)
        {
            // get next inParam-attributes:
            oldInParam = action.inParams.elementAt (i);
            if (oldInParam != null)
            {
                name = oldInParam.getName ();
                field = oldInParam.getQueryField ();
            } // if

            // replace variable-value inParam (if exists)
            name = variables.replaceWithValue (name);

            // init in-parameter
            newInParam = new ActionParameter (name, field);

            // add to new in-parameters
            newInParams.addElement (newInParam);
        } // for

        // store new in-parameters
        action.inParams = newInParams;
    } // replaceVariablesInparams


    /**************************************************************************
     * Perform the XPATH for the given object. <BR/>
     * Read and store parameters in variable object (if needed).
     * The fixed variables ERRORCODE and ERRORMESSAGE will also be stored in
     * the given variables list (default: 0, OK).
     *
     * @param   obj         object on with to perform the xpath action
     * @param   action      holds the action incl. in/outparameters
     * @param   variables   list of variables for in/outparameters
     *
     * @return  <CODE>true</CODE>    action executed without error
     *          <CODE>false</CODE>   error occured (see ERRORCODE, ERRORMESSAGE)
     */
    protected boolean performXPathAction (BusinessObject obj,
                                       Action action,
                                       Variables variables)
    {
        Vector<String> callOutParams = new Vector<String> ();
        String xpathExpr;
        String mappedVariable;
        ActionParameter inParam;
        ActionParameter outParam;
        Variable variable;

        // replace variables in inparams with variable-value
        // (normaly there won't be any; XPATH expression will be constant
        //  in most cases)
        this.replaceVariablesInParams (action, variables);

        // get number of in and out-params
        int numInParams = action.inParams.size ();
        int numOutParams = action.outParams.size ();
        Variable errorCode = variables.getEntry (WorkflowConstants.VARIABLE_ERRORCODE);
        Variable errorMessage = variables.getEntry (WorkflowConstants.VARIABLE_ERRORMESSAGE);

        // check if number of inparams = number of outparams
        if (numInParams != numOutParams)
        {
            // create errorcode, errormessage
            // unknown type of action - add errorcode, errormessage to variables
            errorCode.value = "-1";
            errorMessage.value = "XPATH - Number of INPARAMS must be equal to number of OUTPARAMS";
            // exit
            return false;
        } // if (numInParams != numOutParams)


        // evaluate all x-path-expressions given in inparams of action;
        // evaluated xpath-expression-results will be set in according
        // output-variables (in order of occurence)
        for (int i = 0; i < numInParams; i++)
        {
            // get next param
            inParam = action.inParams.elementAt (i);
            outParam = action.outParams.elementAt (i);

            // check if valid
            if (inParam == null || outParam == null)
            {
                // add errorcode, errormessage to variables
                errorCode.value = "-1";
                errorMessage.value = "XPATH - Parameter not valid, at position = " + 1;
                // exit
                return false;
            } // if ...

            // get x-path expression and mapped output-variable
            xpathExpr = inParam.getName ();
            mappedVariable = outParam.getName ();

            // get according variable
            variable = variables.getEntry (mappedVariable);

            // check if variable exists
            if (variable == null)
            {
                // add errorcode, errormessage to variables
                errorCode.value = "-1";
                errorMessage.value
                    = "XPATH - Variable Parameter not valid: " + mappedVariable;
                // exit
                return false;
            } // if (variable == null)

            // evaluate x-path-expression; result will be stored in
            // vector in order: (1)ERRORCODE (2)ERRORMESSAGE (3)XPATH-RESULT
            Vector<String> callInParam = new Vector<String> ();
            callInParam.addElement (xpathExpr);

            // call xpath-evaluation
            callOutParams = obj.performXPath (callInParam);

            // check if returned values are valid
            if (callOutParams == null)
            {
                // add errorcode, errormessage to variables
                errorCode.value = "-1";
                errorMessage.value
                    = "XPATH - XPath-evaluation returned null" +
                    callOutParams.toString ();
                // exit
                return false;
            } // if (callOutParams == null)

            // check errorcode and errormessage (first and second parameter)
            // check if at least 2 return-values in vector
            if (callOutParams.size () < 2)
            {
                // add errorcode, errormessage to variables
                errorCode.value = "-1";
                errorMessage.value
                    = "XPATH - No ERRORCODE or ERRORMESSAGE found: " +
                    callOutParams.toString ();
                // exit
                return false;
            } // if (callOutParams.size() < 2)

            // check errorcode - exit on error
            String callOutParam = callOutParams.elementAt (0);
            if (!callOutParam.equals ("0"))
            {
                // add errorcode, errormessage to variables
                errorCode.value = "-1";
                errorMessage.value
                    = "XPATH - Error during xpath-evaluation: " +
                    callOutParams.toString ();
                // exit
                return false;
            } // if (!callOutParam.equals("0"))

            // no error: check if 3rd parameter (result) exists.
            if (callOutParams.size () != 3)
            {
                // add errorcode, errormessage to variables
                errorCode.value = "-1";
                errorMessage.value
                    = "XPATH - No error raised, but result is missing: " +
                    callOutParams.toString ();
                // exit
                return false;
            } // if (callOutParams.size() != 3)

            //
            // get the result of the xpath-evaluation (3rd out-parameter of
            // xpath-call and store it in the according variable in the actions
            // output-parameters
            variable.value = callOutParams.elementAt (2);
            // set value in according outparam
        } // for i

        // exit
        return true;
    } // performXPathAction


    /**************************************************************************
     * Perform an INTERNALCALL. <BR/>
     * The method will be performed on the given object. <BR/>
     * The name of the method is defined in the action-objects CALL parameter.
     * <BR/>
     * The inparameters of the action will be packed into 1 vector as inparameter
     * for the method. <BR/>
     * The method of the given object must look like:<BR/>
     * <PRE>
     *      public (Vector) &lt;methodName> (Vector &lt;paramname>);
     * </PRE>
     *
     * All parameters must be of type string. The return value is a String-vector
     * which must have 2 fixed String objects on position 0 and 1: ERRORCODE and
     * ERRORMESSAGE (default: 0, OK).
     *
     * @param obj       forwarded object of workflow instance
     * @param action    holds the action incl. in/outparameters
     * @param variables list of variables for in/outparameters
     *
     * @return  <CODE>true</CODE>    action executed without error
     *          <CODE>false</CODE>   error occured (see ERRORCODE, ERRORMESSAGE)
     */
    @SuppressWarnings ("unchecked")
    protected boolean performInternalCallAction (BusinessObject obj,
                                    Action action,
                                    Variables variables)
    {
        Vector<String> callInParams = new Vector<String> ();
        Vector<String> callOutParams = new Vector<String> ();
        ActionParameter inParam;
        ActionParameter outParam;
        String inParamName;
        String callOutParam;
        Variable variable;
        Variable errorCode = variables.getEntry (WorkflowConstants.VARIABLE_ERRORCODE);
        Variable errorMessage = variables.getEntry (WorkflowConstants.VARIABLE_ERRORMESSAGE);

        // replace variables in inparams with variable-value
        this.replaceVariablesInParams (action, variables);

        // - build vector with strings as inparam for method-call
        // iterate through in-params
        for (int i = 0; i < action.inParams.size (); i++)
        {
            // get next param
            inParam = action.inParams.elementAt (i);

            // check if valid
            if (inParam == null)
            {
                // add errorcode, errormessage to variables
                errorCode.value = "-1";
                errorMessage.value
                    = "INTERNALCALL - inParameter not valid, at position " + i;
                // exit
                return false;
            } // if ...

            // get inParam value
            inParamName = inParam.getName ();

            // add string value to inParam-vector for method call
            callInParams.addElement (inParamName);
        } // for


        // dynamically instantiate object and call method on object which
        // is given in CALL-parameter of internal-call-action.
        try
        {
            // get the class of the given object
            Class<? extends BusinessObject> classOfObject = obj.getClass ();
            // create array of classes that represent the in-parameters for the method
            // NOTE: only 1 in-param that must be a vector
            Class[] inParamClasses = {(new Vector ()).getClass ()};
            // get the method of object-class which is given in CALL-parameter
            Method callMethod = classOfObject.getMethod (action.call, inParamClasses);
            // inparams must be wrapped in an object-array
            Object[] inParamArray = {callInParams}; // inParamArray

            // call the method on the given object
            Object returnObject = callMethod.invoke (obj, inParamArray);

            // convert outparams
            callOutParams = (Vector<String>) returnObject;
        } // try
        catch (IllegalAccessException e)
        {
            // add errorcode, errormessage to variables
            errorCode.value = "-1";
            errorMessage.value = "INTERNAL-CALL Exception: " + e.toString ();
            // exit
            return false;
        } // catch
        catch (NoSuchMethodException e)
        {
            // add errorcode, errormessage to variables
            errorCode.value = "-1";
            errorMessage.value = "INTERNAL-CALL Exception: " + e.toString ();
            // exit
            return false;
        } // catch
        catch (IllegalArgumentException e)
        {
            // add errorcode, errormessage to variables
            errorCode.value = "-1";
            errorMessage.value = "INTERNAL-CALL Exception: " + e.toString ();
            // exit
            return false;
        } // catch
        catch (InvocationTargetException e)
        {
            // add errorcode, errormessage to variables
            errorCode.value = "-1";
            errorMessage.value = "INTERNAL-CALL Exception: " + e.toString ();
            // exit
            return false;
        } // catch

        // get errorcode and errormessage (1st two parameters)
        // check if at least 2 return-values in vector
        if (callOutParams.size () < 2)
        {
            // add errorcode, errormessage to variables
            errorCode.value = "-1";
            errorMessage.value = "INTERNAL-CALL: Missing ERRORCODE or ERRORMESSAGE parameter ";
            // exit
            return false;
        } // if
        // check errorcode - exit on error
        callOutParam = callOutParams.elementAt (0);
        if (!callOutParam.equals ("0"))
        {
            // add errorcode, errormessage to variables
            errorCode.value = "-1";
            errorMessage.value = "INTERNAL-CALL: " + callOutParams.elementAt (1);
            // exit
            return false;
        } // if

        // iterate through calls outparams and set them in value of according
        // variable - order: must be the same order as in INTERNALCALLs outparams
        for (int i = 2; i < callOutParams.size (); i++)
        {
            // get calls next outparam
            callOutParam = callOutParams.elementAt (i);

            // get according outparam of INTERNAL-CALL
            outParam = action.outParams.elementAt (i - 2);

            // get according variable
            variable = variables.getEntry (outParam.getName ());

            // check if variable exists
            if (variable == null)
            {
                // add errorcode, errormessage to variables
                errorCode.value = "-1";
                errorMessage.value
                    = "INTERNAL-CALL - Variable not defined: " + outParam;
                // exit
                return false;
            } // if

            // set variables value: is value of method-calls outparam
            variable.value = callOutParam;
        } // for

        // exit
        return true;
    } // INTERNALCALL



    /**************************************************************************
     * Perform the QUERY call. <BR/>
     * Read and store parameters from/in variable object (if needed).
     * The fixed variables ERRORCODE and ERRORMESSAGE will also be stored in
     * the given variables list (default: 0, OK).
     *
     * @param obj       forwarded object of workflow instance
     * @param action    holds the action incl. in/outparameters
     * @param variables list of variables for in/outparameters
     *
     * @return  <CODE>true</CODE>    action executed without error
     *          <CODE>false</CODE>   error occured (see ERRORCODE, ERRORMESSAGE)
     */
    protected boolean performQueryAction (BusinessObject obj,
                             Action action,
                             Variables variables)
    {
        Variable variable;
        String value;

        // create/init query-executive
        QueryExecutive qe = new QueryExecutive ();
        qe.initObject (this.oid, this.user, this.env, this.sess, this.app);

        // add in parameters of action to query
        for (int i = 0; i < action.inParams.size (); i++)
        {
            // fetch next inParam
            ActionParameter inParam = action.inParams.elementAt (i);

            // get variable for given inParam
            variable = variables.getEntry (inParam.getName ());

            // check if variable is valid
            if (variable == null)
            {
                // add errorcode, errormessage to variables
                (variables.getEntry (WorkflowConstants.VARIABLE_ERRORCODE)).value = "-1";
                (variables.getEntry (WorkflowConstants.VARIABLE_ERRORMESSAGE)).value
                    = "QUERY - Variable not defined: " + inParam;
                // exit
                return false;
            } // if

            // get the optional query field name of the parameter.
            String fieldName = inParam.getQueryField ();
            // if the field name is not set the basic name of the
            // variable is used as the query field name.
            if (fieldName == null)
            {
                fieldName = variable.name;
            } // if

            // add input parameter
            qe.addInParameter (fieldName, variable.type, variable.value);
        } // for

        Variable errorCode = variables.getEntry (WorkflowConstants.VARIABLE_ERRORCODE);
        Variable errorMessage = variables.getEntry (WorkflowConstants.VARIABLE_ERRORMESSAGE);

        // execute query: name is given in actions tag <CALL>
        if (!qe.execute (action.call))
        {
            // add errorcode, errormessage to variables
            errorCode.value = "-1";
            errorMessage.value = "QUERY - Error when executing query: " + action.call;
            // exit
            return false;
        } // if
        // else: success - proceed

        // check if size is exactly 1
        int numRows = qe.getRowCount ();
        if (numRows == 0)
        {
            // add errorcode, errormessage to variables
            errorCode.value = "-1";
            errorMessage.value = "QUERY - No results returned: " + action.call;
            // exit
            return false;
        } // else
        else if (numRows != 1)
        {
            // add errorcode, errormessage to variables
            errorCode.value = "-1";
            errorMessage.value = "QUERY - more than 1 row returned "  + action.call;
            // exit
            return false;
        } // else

        // loop through out-variables and set values from query-result-row;
        // mapping between outparams and query-columns: name of outparam/variable;
        // value of columns will be set in according variable
        for (int i = 0; i < action.outParams.size (); i++)
        {
            // get next outparam
            ActionParameter outParam = action.outParams.elementAt (i);
            // get according variable for given outParam
            variable = variables.getEntry (outParam.getName ());
            // check if variable is valid
            if (variable == null)
            {
                // add errorcode, errormessage to variables
                errorCode.value = "-1";
                errorMessage.value = "QUERY - Variable in outparam not defined: " + outParam;
                // exit
                return false;
            } // if

            // get the optional query field name of the parameter
            String fieldName = outParam.getQueryField ();
            // if the field name is not set the basic name of the
            // variable is used as the query field name.
            if (fieldName == null)
            {
                fieldName = variable.name;
            } // if

            // get value of the query-column
            // and set it as new variable value
            value = qe.getColValue (fieldName);

            // check the variables value; if not found ==> null
            if (value == null)
            {
                // add errorcode, errormessage to variables
                errorCode.value = "-1";
                errorMessage.value = "QUERY - Column " + fieldName +  " not found in query " +
                      action.call;
                // exit
                return false;
            } // if

            // set value in variable
            variable.value = value;
        } // for

        // made it: exit
        return true;
    } // QUERYOBJECT

} // WorkflowActionHandler
