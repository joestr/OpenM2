/*
 * Class: ActionVariables
 */

// package:
package ibs.service.action;

// imports:
import ibs.BaseObject;
//KR TODO: unsauber
import ibs.app.UserInfo;
//KR TODO: unsauber
import ibs.bo.BusinessObject;
//KR TODO: unsauber
import ibs.bo.OID;
import ibs.io.Environment;
import ibs.ml.MultilingualTextProvider;
import ibs.service.action.ActionConstants;
import ibs.service.action.ActionException;
import ibs.service.action.ActionMessages;
import ibs.service.action.Variable;
import ibs.util.DateTimeHelpers;

import java.util.Date;
import java.util.Iterator;
import java.util.Vector;


/******************************************************************************
 * Object represents the VARIABLES tag of a workflow or form definition. <BR/>
 * It holds a list of variables.
 *
 * @version     $Id: Variables.java,v 1.25 2010/04/07 13:37:12 rburgermann Exp $
 *
 * @author      Horst Pichler (HP), 18.10.2000
 ******************************************************************************
 */
public class Variables extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Variables.java,v 1.25 2010/04/07 13:37:12 rburgermann Exp $";


    /**
     * The list of variables.<BR> Holds Variable-objects
     */
    private Vector<Variable> list;

    /**
     * Variable length: name length. <BR/>
     */
    private static final String VAR_LENGTH_NAME = "63";
    /**
     * Variable length: id. <BR/>
     */
    private static final String VAR_LENGTH_ID = "10";
    /**
     * Variable length: date. <BR/>
     */
    private static final String VAR_LENGTH_DATE = Variables.VAR_LENGTH_ID;
    /**
     * Variable length: time. <BR/>
     */
    private static final String VAR_LENGTH_TIME = "5";
    /**
     * Variable length: date + time. <BR/>
     */
    private static final String VAR_LENGTH_DATETIME = "15";
    /**
     * Variable length: oid. <BR/>
     */
    private static final String VAR_LENGTH_OID = "16";


    /**************************************************************************
     * Constructor. <BR/>
     */
    public Variables ()
    {
        // init class variables
        this.list = new Vector<Variable> ();
    } // Variables


    /**************************************************************************
     * Returns a copy (clone) of the Variables object.
     *
     * @return  The Variables copy.
     */
    public Variables getCopy ()
    {
        Variables clone = new Variables ();
        // clone the Variable objects in the vector
        for (int i = 0; i < this.list.size (); i++)
        {
            Variable v = this.list.elementAt (i);
            clone.list.addElement (v.getCopy ());
        } // for i
        return clone;
    } // getCopy


    /**************************************************************************
     * Adds all system variables to the variable list. <BR/>
     *
     * @param   obj ???
     *
     * @throws  ActionException
     *          An error occurred while adding the system variables.
     */
    public void addSysVars (BusinessObject obj) throws ActionException
    {
        // add all predefined system variables

        try
        {
            // add the full user name
            this.addEntry (new Variable (
                ActionConstants.SYSVAR_PREFIX +
                ActionConstants.SYSVAR_USERFULLNAME +
                ActionConstants.VARIABLE_POSTFIX,
                ActionConstants.VARIABLETYPE_TEXT,
                Variables.VAR_LENGTH_NAME,
                "",
                ((UserInfo) obj.sess.userInfo).getUser ().fullname), obj.getEnv ());

            // add the user name
            this.addEntry (new Variable (
                ActionConstants.SYSVAR_PREFIX +
                ActionConstants.SYSVAR_USERNAME +
                ActionConstants.VARIABLE_POSTFIX,
                ActionConstants.VARIABLETYPE_TEXT,
                Variables.VAR_LENGTH_NAME,
                "",
                ((UserInfo) obj.sess.userInfo).getUser ().actUsername), obj.getEnv ());

            // add the user id
            this.addEntry (new Variable (
                ActionConstants.SYSVAR_PREFIX +
                ActionConstants.SYSVAR_USERID +
                ActionConstants.VARIABLE_POSTFIX,
                ActionConstants.VARIABLETYPE_NUMBER,
                Variables.VAR_LENGTH_ID,
                "",
                "" + ((UserInfo) obj.sess.userInfo).getUser ().id), obj.getEnv ());

            // add the user oid:
            this.addEntry (new Variable (
                ActionConstants.SYSVAR_PREFIX +
                ActionConstants.SYSVAR_USEROID +
                ActionConstants.VARIABLE_POSTFIX,
                ActionConstants.VARIABLETYPE_NUMBER,
                Variables.VAR_LENGTH_OID,
                "",
                "" + ((UserInfo) obj.sess.userInfo).getUser ().oid), obj.getEnv ());

            // add the extended user data oid:
            this.addEntry (new Variable (
                ActionConstants.SYSVAR_PREFIX +
                ActionConstants.SYSVAR_EXTENDEDUSERDATAOID +
                ActionConstants.VARIABLE_POSTFIX,
                ActionConstants.VARIABLETYPE_NUMBER,
                Variables.VAR_LENGTH_OID,
                "",
                "" + ((UserInfo) obj.sess.userInfo).getExtendedUserData ().p_oid), obj.getEnv ());

            // add the current datetime
            this.addEntry (new Variable (
                ActionConstants.SYSVAR_PREFIX +
                ActionConstants.SYSVAR_DATE +
                ActionConstants.SYSVAR_TIME +
                ActionConstants.VARIABLE_POSTFIX,
                ActionConstants.VARIABLETYPE_TEXT,
                Variables.VAR_LENGTH_DATETIME,
                "",
                DateTimeHelpers.dateTimeToString (new Date ())), obj.getEnv ());

            // add the current date
            this.addEntry (new Variable (
                ActionConstants.SYSVAR_PREFIX +
                ActionConstants.SYSVAR_DATE +
                ActionConstants.VARIABLE_POSTFIX,
                ActionConstants.VARIABLETYPE_TEXT,
                Variables.VAR_LENGTH_DATE,
                "",
                DateTimeHelpers.dateToString (new Date ())), obj.getEnv ());

            // add the current time
            this.addEntry (new Variable (
                ActionConstants.SYSVAR_PREFIX +
                ActionConstants.SYSVAR_TIME +
                ActionConstants.VARIABLE_POSTFIX,
                ActionConstants.VARIABLETYPE_TEXT,
                Variables.VAR_LENGTH_TIME,
                "",
                DateTimeHelpers.timeToString (new Date ())), obj.getEnv ());

            // for object specific values an object must be set!
            if (obj != null)
            {
                this.addEntry (new Variable (
                    ActionConstants.SYSVAR_PREFIX +
                    ActionConstants.SYSVAR_OID +
                    ActionConstants.VARIABLE_POSTFIX,
                    ActionConstants.VARIABLETYPE_TEXT,
                    Variables.VAR_LENGTH_OID,
                    "",
                    obj.oid != null ? obj.oid.toString () : OID.EMPTYOID), obj.getEnv ());

                this.addEntry (new Variable (
                    ActionConstants.SYSVAR_PREFIX +
                    ActionConstants.SYSVAR_CONTAINERID +
                    ActionConstants.VARIABLE_POSTFIX,
                    ActionConstants.VARIABLETYPE_TEXT,
                    Variables.VAR_LENGTH_OID,
                    "",
                    obj.containerId != null ? obj.containerId.toString () : OID.EMPTYOID), obj.getEnv ());

    /* TODO BB050829: containerOid2 not yet supported in Java only in DB
                this.addEntry (new Variable (
                    ActionConstants.SYSVAR_PREFIX +
                    ActionConstants.SYSVAR_CONTAINEROID2 +
                    ActionConstants.VARIABLE_POSTFIX,
                    ActionConstants.VARIABLETYPE_TEXT,
                    Variables.VAR_LENGTH_OID,
                    "",
                    (obj.containerOid2 != null ? obj.containerOid2.toString (): OID.EMPTYOID)), obj.getEnv ());
    */
                // add the typename of the business object:
                this.addEntry (new Variable (
                    ActionConstants.SYSVAR_PREFIX +
                    ActionConstants.SYSVAR_TYPENAME +
                    ActionConstants.VARIABLE_POSTFIX,
                    ActionConstants.VARIABLETYPE_TEXT,
                    Variables.VAR_LENGTH_NAME,
                    "",
                    obj.typeName), obj.getEnv ());

                // type object available?
                if (obj.typeObj != null)
                {
                    // add the typecode of the business object
                    this.addEntry (new Variable (
                        ActionConstants.SYSVAR_PREFIX +
                        ActionConstants.SYSVAR_TYPECODE +
                        ActionConstants.VARIABLE_POSTFIX,
                        ActionConstants.VARIABLETYPE_TEXT,
                        Variables.VAR_LENGTH_NAME,
                        "",
                        obj.typeObj.getCode ()), obj.getEnv ());
                } // if (obj.typeObj != null)
            } // if (obj != null)
        } // try
        catch (ActionException e)
        {
            // nothing to do, just means that a variable already exists
        } // catch
    } // addSysVars


    /**************************************************************************
     * Gets an entry with given name. <BR/>
     * The given name must reference a variable, for example
     * '#VARIABLE.name#' or '#SYSVAR.name#'.
     *
     * @param   name    name of variable to be returned
     *
     * @return  Variable      entry with given name
     *          null          entry with given name not found
     */
    public Variable getEntry (String name)
    {
        // loop through all variables:
        for (Iterator<Variable> iter = this.list.iterator (); iter.hasNext ();)
        {
            // get next entry:
            Variable variableInList = iter.next ();

            // check if variable with given name already exists
            if (variableInList.getName ().equalsIgnoreCase (name))
            {
                // return value of this entry
                return variableInList;
            } // if
        } // for iter

        // no variable with given name found
        return null;
    } // getEntry


    /**************************************************************************
     * Adds given Variable-object to list. <BR/>
     *
     * @param   variable    A Variable object to add.
     * @param   env         The current environment
     *
     * @throws  ActionException
     *          The variable already exists.
     */
    public void addEntry (Variable variable, Environment env) throws ActionException
    {
        // check if a variable with same name already exists
        if (this.getEntry (variable.getName ()) != null)
        {
            // variable with name already exists
            throw new ActionException (
                MultilingualTextProvider.getMessage (ActionMessages.MSG_BUNDLE,
                    ActionMessages.ML_MSG_VARIABLE_ALREADY_DEFINED, env) +
                " (" + variable.getName () + ")");
        } // if (this.getEntry (variable.name) != null)

        this.list.addElement (variable);
    } // addEntry


    /**************************************************************************
     * Changes variable with same name in the list. If no variable with same
     * name found add new entry.<BR> Will be compared via variables name.
     *
     * @param   variable    Given variable to change.
     *
     * @return  <CODE>true</CODE>  entry changed.
     *          <CODE>false</CODE> no entry found: entry added.
     */
    public boolean changeEntry (Variable variable)
    {
        // check if variable with same name exists
        Variable variableInList = this.getEntry (variable.getName ());
        if (variableInList != null)
        {
            // variable exists - exchange with given one
            this.list.removeElement (variableInList);
            this.list.addElement (variable);

            // success
            return true;
        } // changeEntry

        // variable does not exist - add given one
        this.list.addElement (variable);
        // not found
        return false;
    } // changeEntry


    /**************************************************************************
     * Removes variable with given name in the list. <BR> Will be compared
     * via variables name.
     *
     * @param   variable    Given variable to remove.
     *
     * @return  <CODE>true</CODE>  entry removed.
     *          <CODE>false</CODE> no variable with same name found.
     */
    public boolean removeEntry (Variable variable)
    {
        // check if variable with same name exists
        Variable variableInList = this.getEntry (variable.getName ());
        if (variableInList != null)
        {
            // variable exists - remove
            this.list.removeElement (variableInList);

            // success
            return true;
        } // changeEntry

        // not found
        return false;
    } // changeEntry


    /**************************************************************************
     * Replaces occurences of variables in string with the variables value. <BR/>
     *
     * @param   str     a string
     * @param   env     The current environment
     *
     * @return  string with variable values instead of variable names
     *
     * @throws  ActionException
     *          if referenced variable is not found or an invalid variable
     *          reference (syntax error) is found.
     */
    public String replaceWithValue (String str, Environment env) throws ActionException
    {
        // check if parameter set
        if (str == null)
        {
            return null;
        } // if

        StringBuffer res = new StringBuffer ();

        // scan the entire string for variable references (#SYSVAR.xxx# or #VARIABLE.xxx#)
        // and replace the reference by the value of the variable
        int len = str.length ();
        int pos = 0;

        while (pos >= 0 && pos < len)
        {
            // get the index of the first occurrence of the '#SYSVAR.' token
            int idxSysVar = str.indexOf (ActionConstants.SYSVAR_PREFIX, pos);
            // get the index of the first occurrence of the '#VARIABLE.' token
            int idxVariable = str.indexOf (ActionConstants.VARIABLE_PREFIX, pos);

            int idx = idxSysVar;
            // determinate witch of the two token comes first
            // and set the index to this token.
            if (idxSysVar < 0 || (idxVariable >= 0 && idxVariable < idxSysVar))
            {
                idx = idxVariable;
            } // if #SYSVAR comes after #VARIABLE

            // if a #SYSVAR or #VARIABLE token was found
            // replace it by the value
            if (idx >= 0)
            {
                // append the part before the token to the result buffer.
                res.append (str.substring (pos, idx));
                // get the index of the tailing '#' character
                int idx2 = str.indexOf (ActionConstants.VARIABLE_POSTFIX, idx + 1);
                if (idx2 >= 0)
                {
                    // get the name of the variable
                    String varName = str.substring (idx, idx2 + 1);
                    // get the variable from the variable list
                    Variable var = this.getEntry (varName);
                    // if the variable was not found report an error
                    if (var == null)
                    {
                        throw new ActionException (
                            MultilingualTextProvider.getMessage (ActionMessages.MSG_BUNDLE,
                                ActionMessages.ML_MSG_UNDEFINED_VARIABLE, env)
                             + " (" + varName + ")");
                    } // if (var == null)
                    // append the value of the variable to the result buffer
                    res.append (var.getValue ());
                    // continue after the variable found
                    idx = idx2 + 1;
                } // if (idx2 >= 0)
                else
                {
                    // no tailing '#' character found
                    // -> incorrect variable reference, stop replacement
                    throw new ActionException (
                        MultilingualTextProvider.getMessage (ActionMessages.MSG_BUNDLE,
                            ActionMessages.ML_MSG_INCORRECT_VARIABLE, env)
                        + " (" + str + ")");
                } // else if (idx2 >= 0)
            } // if (idx >= 0)

            // if no variable reference was found
            if (idx < 0)
            {
                // append the rest of the value string to the result buffer
                res.append (str.substring (pos));
            } // if (idx < 0)

            // set the new position index
            pos = idx;
        } // while (pos >= 0 && pos < len)

        return res.toString ();
    } // replaceWithValue


    /**************************************************************************
     * Replaces occurences of variables in string with the variables value. <BR/>
     *
     * @param   str     a string
     * @param   env     The current environment
     *
     * @return  string with variable values instead of variable names
     *
     * @throws  ActionException
     *          if referenced variable is not found or an invalid variable
     *          reference (syntax error) is found.
     */
    public StringBuffer replaceWithValue (StringBuffer str, Environment env) throws ActionException
    {
        // check if parameter set
        if (str == null)
        {
            return null;
        } // if

        StringBuffer res = new StringBuffer ();

        // scan the entire string for variable references (#SYSVAR.xxx# or #VARIABLE.xxx#)
        // and replace the reference by the value of the variable
        int len = str.length ();
        int pos = 0;

        while (pos >= 0 && pos < len)
        {
            // get the index of the first occurrence of the '#SYSVAR.' token
            int idxSysVar = str.toString ().indexOf (ActionConstants.SYSVAR_PREFIX, pos);
            // get the index of the first occurrence of the '#VARIABLE.' token
            int idxVariable = str.toString ().indexOf (ActionConstants.VARIABLE_PREFIX, pos);
/*
// KR HACK: This part is not used because of backwards compatibility to
// Java 1.3 which does not support the StringBuffer.indexOf () method.
            // get the index of the first occurrence of the '#SYSVAR.' token
            int idxSysVar = str.indexOf (ActionConstants.SYSVAR_PREFIX, pos);
            // get the index of the first occurrence of the '#VARIABLE.' token
            int idxVariable = str.indexOf (ActionConstants.VARIABLE_PREFIX, pos);
*/

            int idx = idxSysVar;
            // determinate witch of the two token comes first
            // and set the index to this token.
            if (idx < 0 || (idxVariable >= 0 && idxVariable < idx))
            {
                idx = idxVariable;
            } // if #SYSVAR comes after #VARIABLE

            // if a #SYSVAR or #VARIABLE token was found
            // replace it with the value
            if (idx >= 0)
            {
                // append the part before the token to the result buffer.
                res.append (str.substring (pos, idx));
                // get the index of the tailing '#' character
                int idx2 = str.toString ().indexOf (ActionConstants.VARIABLE_POSTFIX, idx + 1);
/*
// KR HACK: This part is not used because of backwards compatibility to
// Java 1.3 which does not support the StringBuffer.indexOf () method.
                int idx2 = str.indexOf (ActionConstants.VARIABLE_POSTFIX, idx + 1);
*/
                if (idx2 >= 0)
                {
                    // get the name of the variable
                    String varName = str.substring (idx, idx2 + 1);
                    // get the variable from the variable list
                    Variable var = this.getEntry (varName);
                    // if the variable was not found report an error
                    if (var == null)
                    {
                        throw new ActionException (
                            MultilingualTextProvider.getMessage (ActionMessages.MSG_BUNDLE,
                                ActionMessages.ML_MSG_UNDEFINED_VARIABLE, env)
                            + " (" + varName + ")");
                    } // if (var == null)
                    // append the value of the variable to the result buffer
                    res.append (var.getValue ());
                    // continue after the variable found
                    idx = idx2 + 1;
                } // if (idx2 >= 0)
                else
                {
                    // no tailing '#' character found
                    // -> incorrect variable reference, stop replacement
                    throw new ActionException (
                        MultilingualTextProvider.getMessage (ActionMessages.MSG_BUNDLE,
                            ActionMessages.ML_MSG_INCORRECT_VARIABLE, env) +
                        " (" + str.substring (idx) + ")");
                } // else if (idx2 >= 0)
            } // if (idx >= 0)

            // if no variable reference was found
            if (idx < 0)
            {
                // append the rest of the value string to the result buffer
                res.append (str.substring (pos));
            } // if (idx < 0)

            // set the new position index
            pos = idx;
        } // while (pos >= 0 && pos < len)

        return res;
    } // replaceWithValue


    /**************************************************************************
     * Checks if given string1 contains given string2, case will be ignored. <BR/>
     *
     * @param   string1   a string
     * @param   string2   the other string
     *
     * @return  <CODE>true</CODE>    if string1 contains string1 (ignore case)
     *          <CODE>false</CODE>   otherwise
     */
    public static boolean containsIgnoreCase (String string1, String string2)
    {
        // check if parameters set
        if (string1 == null || string2 == null)
        {
            return false;
        } // if

        // create upper case versions of both strings
        String s1 = string1.toUpperCase ();
        String s2 = string2.toUpperCase ();

        // check if s1 in s2; indexOf returns -1 if not substring
        return s1.indexOf (s2) != -1;
    } // containsIgnoreCase


    /**************************************************************************
     * Returns a string representation of this object. <BR/>
     *
     * @return  A string representation of this object.
     */
    public String toString ()
    {
        return this.list.toString ();
    } // toString

} // class Variables
