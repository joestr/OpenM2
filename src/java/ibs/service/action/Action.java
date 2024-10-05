/*
 * Class: Action.java
 */

// package:
package ibs.service.action;

// imports:
import ibs.BaseObject;
import ibs.io.Environment;
import ibs.service.action.ActionConstants;
import ibs.service.action.ActionException;
import ibs.service.action.ActionParameter;
import ibs.service.action.Variables;

import java.util.Iterator;
import java.util.Vector;


/******************************************************************************
 * The Action holds the information of one xml-action. <BR/>
 *
 * @version     $Id: Action.java,v 1.11 2010/04/07 13:37:12 rburgermann Exp $
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
        "$Id: Action.java,v 1.11 2010/04/07 13:37:12 rburgermann Exp $";


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
    public String type = ActionConstants.UNDEFINED;

    //
    // TAG: <CALL>
    //
    /**
     *  call-method of an action element. <BR/>
     */
    public String call = ActionConstants.UNDEFINED;


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
     * Returns a copy (clone) of the Action object.
     *
     * @return  The copy.
     */
    public Action getCopy ()
    {
        Action clone = new Action ();
        clone.type = this.type;
        clone.call = this.call;

        // clone the inPrams vector
        for (int i = 0; i < this.inParams.size (); i++)
        {
            ActionParameter param = this.inParams.elementAt (i);
            clone.inParams.addElement (param.getCopy ());
        } // for i

        // clone the outPrams vector
        for (int i = 0; i < this.outParams.size (); i++)
        {
            ActionParameter param = this.outParams.elementAt (i);
            clone.outParams.addElement (param.getCopy ());
        } // for i

        return clone;
    } // getCopy


    /**************************************************************************
     * returns the name/value pairs of in-params. <BR/>
     *
     * @param   variables   variables used in action
     * @param   env         The current environment
     *
     * @return  string with name/value pairs.
     */
    public String valuesOfInParams (Variables variables, Environment env)
    {
        return this.valuesOfParams (this.inParams, variables, env);
    } // valuesOfInParams


    /**************************************************************************
     * Returns the name/value pairs of out-params. <BR/>
     *
     * @param   variables   variables used in action
     * @param   env         The current environment
     *
     * @return  string with name/value pairs.
     */
    public String valuesOfOutParams (Variables variables, Environment env)
    {
        return this.valuesOfParams (this.outParams, variables, env);
    } // valuesOfOutParams


    /**************************************************************************
     * Returns the values of parameters as comma-separated string. <BR/>
     *
     * @param   params      the in/out params
     * @param   variables   variables used in action
     * @param   env         The current enviroment
     *
     * @return  string with name/value pairs
     */
    private String valuesOfParams (Vector<ActionParameter> params, Variables variables, Environment env)
    {
        StringBuffer str = new StringBuffer ("[");
        String varStr = null;

        // loop through all params:
        for (Iterator<ActionParameter> iter = params.iterator (); iter.hasNext ();)
        {
            // add next entry:
            try
            {
                // get value of parameter:
                varStr = iter.next ().getName ();

                if (varStr != null)
                {
                    varStr = variables.replaceWithValue (varStr, env);
                } // if
                else
                {
                    varStr = ActionConstants.UNDEFINED;
                } // else

                str.append (varStr);
            } // try
            catch (ActionException e)
            {
                str.append (ActionConstants.UNDEFINED);
            } // catch

            if (iter.hasNext ())
            {
                str.append (", ");
            } // if
        } // for iter

        // exit
        return str + "]";
    } // valuesOfParams


    /**************************************************************************
     * Returns a string representation of this object. <BR/>
     *
     * @return  A string representation of this object.
     */
    public String toString ()
    {
        // declare variables
        String str = "";
        String inParamsString = ActionConstants.UNDEFINED;
        String outParamsString = ActionConstants.UNDEFINED;

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
