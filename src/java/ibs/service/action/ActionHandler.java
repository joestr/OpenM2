/*
 * Class: ActionHandler.java
 */

// package:
package ibs.service.action;

// imports:
//KR TODO: unsauber
import ibs.bo.BusinessObject;
//KR TODO: unsauber
import ibs.obj.query.QueryExecutive;
import ibs.service.action.Action;
import ibs.service.action.ActionConstants;
import ibs.service.action.ActionException;
import ibs.service.action.ActionParameter;
import ibs.service.action.Variable;
import ibs.service.action.Variables;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Vector;


/******************************************************************************
 * Wrapper to handle actions in workflows and forms.
 * Calls Action-Implementations depending on type of given action. <BR/>
 *
 * @version     $Id: ActionHandler.java,v 1.8 2010/04/07 13:37:12 rburgermann Exp $
 *
 * @author      Horst Pichler (HP), 2.10.2000
 ******************************************************************************
 */
public class ActionHandler extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ActionHandler.java,v 1.8 2010/04/07 13:37:12 rburgermann Exp $";

    /**
     * Error message prefix: internal call exception. <BR/>
     */
    private static final String ERRP_INTERNALCALL = "INTERNAL-CALL Exception: ";



    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates an ActionHandler Object. <BR/>
     */
    public ActionHandler ()
    {
        // nothing to do
    } // ActionHandler


    /**************************************************************************
     * Perform the given action for the given object. <BR> Read and store
     * parameters in variable object (if needed).
     * The fixed variables ERRORCODE and ERRORMESSAGE will also be stored in
     * the given variables list (default: 0, OK).
     *
     * @param   obj         forwarded object of workflow instance
     * @param   action      holds the action incl. in/outparameters
     * @param   variables   list of variables for in/outparameters
     *
     * @throws  ActionException
     *          An error occurred.
     */
    public void performAction (BusinessObject obj,
                               Action action,
                               Variables variables) throws ActionException
    {
        // call method depending on type of action
        if (action.type.equalsIgnoreCase (ActionConstants.ACTIONTYPE_XPATH))
        {
            this.performXPathAction (obj, action, variables);
        } // if
        else if (action.type.equalsIgnoreCase (ActionConstants.ACTIONTYPE_INTERNALCALL))
        {
            this.performInternalCallAction (obj, action, variables);
        } // else if
        else if (action.type.equalsIgnoreCase (ActionConstants.ACTIONTYPE_QUERY))
        {
            this.performQueryAction (obj, action, variables);
        } // if
/* KR 20060421  The bodies of the following two action types are empty. So I
 *              commented them out.
        else if (action.type.equalsIgnoreCase(ActionConstants.ACTIONTYPE_EXTERNALCALL))
            ;   // not yet implemented
        else if (action.type.equalsIgnoreCase(ActionConstants.ACTIONTYPE_EXPORT))
            ;   // not yet implemented
*/
        else
        {
            // unknown type of action - add errorcode, errormessage to variables
            throw new ActionException ("UNKNOWN Action-TYPE: " + action.type);
        } // else
    } // performAction


    /**************************************************************************
     * Each occurence of #VARIABLE.xxx# in inparameters of given action will
     * be replaced with value of according action. <BR/>
     *
     * @param action    holds the action incl. in/outparameters
     * @param variables list of variables for in/outparameters
     *
     * @throws  ActionException
     *          An error occurred.
     */
    protected void replaceVariablesInParams (Action action, Variables variables)
        throws ActionException
    {
        // declare new in-parameters for action
        Vector<ActionParameter> newInParams = new Vector<ActionParameter> ();
        ActionParameter newInParam;
        ActionParameter oldInParam;
        String name = ActionConstants.UNDEFINED;
        String field = ActionConstants.UNDEFINED;

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
            name =
                variables.replaceWithValue (name, this.getEnv ());

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
     * @throws  ActionException
     *          An error occurred.
     */
    protected void performXPathAction (BusinessObject obj, Action action,
                                       Variables variables)
        throws ActionException
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

        // check if number of inparams = number of outparams
        if (numInParams != numOutParams)
        {
            // in/out parameters mismatch
            throw new ActionException ("XPATH - Number of INPARAMS must be equal to number of OUTPARAMS");
        } // if (numInParams != numOutParams)


        // evaluate all x-path-expressions given in inparams of action;
        // evaluated xpath-expression-results will be set in according
        // output-variables (in order of occurrence)
        for (int i = 0; i < numInParams; i++)
        {
            // get next param
            inParam = action.inParams.elementAt (i);
            outParam = action.outParams.elementAt (i);

            // check if valid
            if (inParam == null || outParam == null)
            {
                throw new ActionException ("XPATH - Parameter not valid");
            } // if ...

            // get x-path expression and mapped output-variable
            xpathExpr = inParam.getName ();
            mappedVariable = outParam.getName ();

            // get according variable
            variable = variables.getEntry (mappedVariable);

            // check if variable exists
            if (variable == null)
            {
                // unknown variable
                throw new ActionException ("XPATH - Variable not defined: " + mappedVariable);
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
                throw new ActionException ("XPATH - XPath-evaluation returned null");
            } // if (callOutParams == null)

            // check error code and error message (first and second parameter)
            // check if at least 2 return-values in vector
            if (callOutParams.size () < 2)
            {
                throw new ActionException (
                    "XPATH - No ERRORCODE or ERRORMESSAGE found: " +
                    callOutParams.toString ());
            } // if (callOutParams.size() < 2)

            // check errorcode - exit on error
            String callOutParam = callOutParams.elementAt (0);
            if (!callOutParam.equals ("0"))
            {
                throw new ActionException (
                    "XPATH - Error during xpath-evaluation: " +
                    callOutParams.toString ());
            } // if (!callOutParam.equals("0"))

            // no error: check if 3rd parameter (result) exists.
            if (callOutParams.size () != 3)
            {
                throw new ActionException (
                    "XPATH - No error raised, but result is missing: " +
                    callOutParams.toString ());
            } // if (callOutParams.size() != 3)

            //
            // get the result of the xpath-evaluation (3rd out-parameter of
            // xpath-call and store it in the according variable in the actions
            // output-parameters
            variable.setTextValue (callOutParams.elementAt (2));
            // set value in according outparam
        } // for i
    } // performXPathAction


    /**************************************************************************
     * Perform an INTERNALCALL. <BR/>
     * The method will be performed on the given object. <BR/>
     * The name of the method is defined in the action-objects CALL parameter. <BR/>
     * The inparameters of the action will be packed into 1 vector as inparameter
     * for the method. <BR/>
     * The method of the given object must look like:<BR/><BR/>
     *      public (Vector) &lt;methodName> (Vector &lt;paramname>);<BR/><BR/>
     *
     * All parameters must be of type string. The return value is a String-vector
     * which must have 2 fixed String objects on position 0 and 1: ERRORCODE and
     * ERRORMESSAGE (default: 0, OK).
     *
     * @param   obj         forwarded object of workflow instance
     * @param   action      holds the action incl. in/outparameters
     * @param   variables   list of variables for in/outparameters
     *
     * @throws  ActionException
     *          An error occurred.
     */
    protected void performInternalCallAction (
                                BusinessObject obj,
                                Action action,
                                Variables variables) throws ActionException
    {
        Vector<String> callInParams = new Vector<String> ();
        Vector<String> callOutParams = new Vector<String> ();
        ActionParameter inParam;
        ActionParameter outParam;
        String inParamName;
        String callOutParam;
        Variable variable;

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
                throw new ActionException ("XPATH - inParameter not valid");
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
            @SuppressWarnings ("unchecked") // suppress compiler warning
            Class[] inParamClasses1 = {(new Vector<String> ()).getClass ()};
            @SuppressWarnings ("unchecked") // suppress compiler warning
            Class<Vector<String>>[] inParamClasses = inParamClasses1;
            // get the method of object-class which is given in CALL-parameter
            Method callMethod = classOfObject.getMethod (action.call, inParamClasses);

            // inparams must be wrapped in an object-array
            Object[] inParamArray = {callInParams};

            // call the method on the given object
            Object returnObject = callMethod.invoke (obj, inParamArray);

            // convert outparams
            @SuppressWarnings ("unchecked") // suppress compiler warning
            Vector<String> callOutParams1 = (Vector<String>) returnObject;
            callOutParams = callOutParams1;
        } // try
        catch (IllegalAccessException e)
        {
            throw new ActionException (ActionHandler.ERRP_INTERNALCALL + e.toString ());
        } // catch
        catch (NoSuchMethodException e)
        {
            throw new ActionException (ActionHandler.ERRP_INTERNALCALL + e.toString ());
        } // catch
        catch (IllegalArgumentException e)
        {
            throw new ActionException (ActionHandler.ERRP_INTERNALCALL + e.toString ());
        } // catch
        catch (InvocationTargetException e)
        {
            throw new ActionException (ActionHandler.ERRP_INTERNALCALL + e.toString ());
        } // catch

        // get error code and error message (1st two parameters)
        // check if at least 2 return-values in vector
        if (callOutParams.size () < 2)
        {
            throw new ActionException (
                ActionHandler.ERRP_INTERNALCALL + "Missing ERRORCODE or ERRORMESSAGE parameter ");
        } // if
        // check error code - exit on error
        callOutParam = callOutParams.elementAt (0);
        if (!callOutParam.equals ("0"))
        {
            throw new ActionException (
                ActionHandler.ERRP_INTERNALCALL + callOutParams.elementAt (1));
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
                throw new ActionException (
                    "INTERNAL-CALL - Variable not defined: " + outParam);
            } // if

            // set variables value: is value of method-calls outparam
            variable.setTextValue (callOutParam);
        } // for
    } // performInternalCallAction


    /**************************************************************************
     * Perform the QUERY call. <BR> Read and store parameters from/in
     * variable object (if needed).
     * The fixed variables ERRORCODE and ERRORMESSAGE will also be stored in
     * the given variables list (default: 0, OK).
     *
     * @param obj       forwarded object of workflow instance
     * @param action    holds the action incl. in/outparameters
     * @param variables list of variables for in/outparameters
     *
     * @throws  ActionException
     *          An error occurred.
     */
    protected void performQueryAction (
                            BusinessObject obj,
                            Action action,
                            Variables variables) throws ActionException
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
                throw new ActionException (
                    "QUERY - Variable not defined: " + inParam);
            } // if

            // get the optional query field name of the parameter.
            String fieldName = inParam.getQueryField ();
            // if the field name is not set the basic name of the
            // variable is used as the query field name.
            if (fieldName == null)
            {
                fieldName = variable.getBasicName ();
            } // if

            // add input parameter
            qe.addInParameter (fieldName, variable.getType (), variable.getValue ());
        } // for

        // execute query: name is given in actions tag <CALL>
        if (!qe.execute (action.call))
        {
            throw new ActionException (
                "QUERY - Error when executing query: " + action.call);
        } // if
        // else: success - proceed

        // check if size is exactly 1
        int numRows = qe.getRowCount ();
        if (numRows == 0)
        {
            throw new ActionException (
                "QUERY - No results returned: " + action.call);
        } // else
        else if (numRows != 1)
        {
            throw new ActionException (
                "QUERY - more than 1 row returned "  + action.call);
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
                throw new ActionException (
                    "QUERY - Variable in outparam not defined: " + outParam);
            } // if

            // get the optional query field name of the parameter
            String fieldName = outParam.getQueryField ();
            // if the field name is not set the basic name of the
            // variable is used as the query field name.
            if (fieldName == null)
            {
                fieldName = variable.getBasicName ();
            } // if

            // get value of the query-column
            // and set it as new variable value
            value = qe.getColValue (fieldName);

            // check the variables value; if not found ==> null
            if (value == null)
            {
                throw new ActionException (
                    "QUERY - Column " + fieldName +
                    " not found in query " + action.call);
            } // if

            // set value in variable
            variable.setTextValue (value);
        } // for
    } // performQueryAction

} // ActionHandler
