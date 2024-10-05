/*
 * Class: Action.java
 */

// package:
package ibs.service.workflow;

// imports:
import ibs.BaseObject;
import ibs.service.action.ActionParameter;
import ibs.service.workflow.Variables;
import ibs.service.workflow.WorkflowConstants;

import java.util.Iterator;
import java.util.Vector;


/******************************************************************************
 * The Action holds the information of one workflow-action. <BR/>
 *
 * @version     $Id: Action.java,v 1.11 2007/07/31 19:13:59 kreimueller Exp $
 *
 * @author      Horst Pichler (HP), 2.10.2000
 ******************************************************************************
 */
public class Action extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Action.java,v 1.11 2007/07/31 19:13:59 kreimueller Exp $";


    ///////////////////////////////////////////////
    //
    // Information that comes from the XML-definition
    // - type
    // - call
    // - inparams
    // - outparams

    //
    // TAG: <ACTION type="... >
    //
    /**
     *  type of an action element. <BR/>
     */
    public String type = WorkflowConstants.UNDEFINED;

    //
    // TAG: <CALL>
    //
    /**
     *  call-method of an action element. <BR/>
     */
    public String call = WorkflowConstants.UNDEFINED;


    //
    // TAG: <INPARAMS>
    //
    /**
     *  in-parameters of an action element. <BR/>
     */
    public Vector<ActionParameter> inParams = new Vector<ActionParameter> ();


    //
    // TAG: <OUTPARAMS>
    //
    /**
     *  out-parameters of an action element. <BR/>
     */
    public Vector<ActionParameter> outParams = new Vector<ActionParameter> ();


    /**************************************************************************
     * Creates an action-object. <BR/>
     */
    public Action ()
    {
        // nothing to do
    } // Action



    /**************************************************************************
     * returns the name/value pairs of in-params. <BR/>
     *
     * @param   variables   variables used in action
     *
     * @return      string with name/value pairs
     */
    public String valuesOfInParams (Variables variables)
    {
        return this.valuesOfParams (this.inParams, variables);
    } // valuesOfInParams


    /**************************************************************************
     * returns the name/value pairs of out-params. <BR/>
     *
     * @param   variables   variables used in action
     *
     * @return      string with name/value pairs
     */
    public String valuesOfOutParams (Variables variables)
    {
        return this.valuesOfParams (this.outParams, variables);
    } // valuesOfOutParams


    /**************************************************************************
     * returns the values of parameters as comma-separated string. <BR/>
     *
     * @param   params      the in/out params
     * @param   variables   variables used in action
     *
     * @return  string with name/value pairs.
     */
    private String valuesOfParams (Vector<ActionParameter> params,
                                   Variables variables)
    {
        String str = "[";               // return value
        String varStr = "";

        // loop through all params:
        for (Iterator<ActionParameter> iter = params.iterator (); iter.hasNext ();)
        {
            // add next entry:
            // get value of parameter
            varStr = iter.next ().getName ();
            if (varStr != null)
            {
                str += variables.replaceWithValue (varStr);
            } // if
            else
            {
                str += WorkflowConstants.UNDEFINED;
            } // else

            // switch to next:
            if (iter.hasNext ())
            {
                str += ", ";
            } // if
        } // for iter

        // exit
        return str + "]";
    } // valuesOfParams


    /**************************************************************************
     * Returns a string representation of this object. <BR/>
     *
     * @return      a string representation of this object
     */
    public String toString ()
    {
        // declare variables
        String str = "";
        String inParamsString = WorkflowConstants.UNDEFINED;
        String outParamsString = WorkflowConstants.UNDEFINED;

        // check objects for null-values
        if (this.inParams != null)
        {
            inParamsString = this.inParams.toString ();
        } // if
        if (this.outParams != null)
        {
            outParamsString = this.outParams.toString ();
        } // if

        // build string
        str += "type = " + this.type + "; " +
               "call = " + this.call + "; " +
               "inParams = " + inParamsString + "; " +
               "outParams = " + outParamsString;

        return str;
    } // toString

} // class Action
