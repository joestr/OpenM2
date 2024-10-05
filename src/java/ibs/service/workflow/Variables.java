/*
 * Class: Variables.java
 */

// package:
package ibs.service.workflow;

// imports:
import ibs.BaseObject;
import ibs.service.workflow.Variable;
import ibs.service.workflow.WorkflowConstants;
import ibs.service.workflow.WorkflowHelpers;
import ibs.util.StringHelpers;

import java.util.Iterator;
import java.util.Vector;


/******************************************************************************
 * Object represents the VARIABLES tag of a workflow-definition. <BR/>
 * It holds a list of variables.
 *
 * @version     $Id: Variables.java,v 1.11 2007/07/24 21:27:33 kreimueller Exp $
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
        "$Id: Variables.java,v 1.11 2007/07/24 21:27:33 kreimueller Exp $";


    /**
     * The list of variables.<BR> Holds Variable-objects
     */
    protected Vector<Variable> list;


    /**************************************************************************
     * Constructor. <BR/>
     */
    public Variables ()
    {
        //init class variables
        this.list = new Vector<Variable> ();
    } // RightsList


    /**************************************************************************
     * Gets an entry with given name. <BR> The given name can also be a string
     * which contains the variable somewhere (like 'akldfj#VARIABLE.name#skdj').
     *
     * @param   name    Name of variable to be returned; can be set
     *                  like <CODE>"#VARIABLE.name#"</CODE> or only
     *                  <CODE>"name"</CODE>.
     *
     * @return  Entry with given name;
     *          <CODE>null</CODE> if entry with given name not found.
     */
    public Variable getEntry (String name)
    {
        String nameLocal = name;        // variable for local assignments
        // iterate through keys
        Variable variableInList;

        // check if given name contains '#VARIABLE.'
        if (WorkflowHelpers.containsIgnoreCase (nameLocal, WorkflowConstants.RUNTIME_PREFIX))
        {
            // get name without variable-marker
            nameLocal = this.extractVariablesName (nameLocal);
        } // if

        // loop through all variables:
        for (Iterator<Variable> iter = this.list.iterator (); iter.hasNext ();)
        {
            // get next entry:
            variableInList = iter.next ();

            // check if variable with given name already exists
            if (variableInList.name.equalsIgnoreCase (nameLocal))
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
     * @param   variable    A Variable object to be added.
     *
     * @return  <CODE>true</CODE> if variable could have been added;
     *          <CODE>false</CODE> if an entry same variable name already
     *          exists.
     *
     */
    public boolean addEntry (Variable variable)
    {
        // check if a variable with same name already exists
        Variable checkVar = this.getEntry (variable.name);

        if (checkVar == null)
        {
            this.list.addElement (variable);
            return true;
        } // if

        // variable with name already exists:
        return false;
    } // addEntry


    /**************************************************************************
     * Changes variable with same name in the list. If no variable with same
     * name found add new entry.<BR> Will be compared via variables name.
     *
     * @param   variable    Given variable to be changed.
     *
     * @return  <CODE>true</CODE> entry changed;
     *          <CODE>false</CODE> no entry found: entry added.
     */
    public boolean changeEntry (Variable variable)
    {
        // check if variable with same name exists
        Variable variableInList = this.getEntry (variable.name);
        if (variableInList != null)
        {
            // variable exists - exchange with given one
            this.list.removeElement (variableInList);
            this.list.addElement (variable);

            // success
            return true;
        } // changeEntry

        // not found
        return false;
    } // changeEntry


    /**************************************************************************
     * Removes variable with given name in the list. <BR> Will be compared
     * via variables name.
     *
     * @param   variable    Given variable to remove.
     *
     * @return  <CODE>true</CODE> entry removed;
     *          <CODE>false</CODE> no variable with same name found.
     */
    public boolean removeEntry (Variable variable)
    {
        // check if variable with same name exists
        Variable variableInList = this.getEntry (variable.name);
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
     * Return iterator over variables in list. <BR/>
     *
     * @return  Iterator object of variables list.
     */
    public Iterator<Variable> iterator ()
    {
        return this.list.iterator ();
    } // iterator


    /**************************************************************************
     * Replaces all occurences of variables in string with the variables
     * values. <BR/>
     * Unknown variables will be replaced with 'UNDEFINED'.
     *
     * @param   string    a string
     *
     * @return  string with variable values instead of variable names.
     */
    public String replaceWithValue (String string)
    {
        // store string in v1
        String v1 = string;

        // check if parameter set
        if (v1 == null)
        {
            return null;
        } // if

        // check if any '#VARIABLE.xxx#' can be found in given string
        if (!WorkflowHelpers.containsIgnoreCase (v1, WorkflowConstants.RUNTIME_PREFIX))
        {
            return v1;
        } // if

        // replace every variable-occurence with variable-value
        String variableString;
        Variable variable;
        while (WorkflowHelpers.containsIgnoreCase (v1, WorkflowConstants.RUNTIME_PREFIX))
        {
            // get variable
            variableString = this.extractVariable (v1);
            // check if variable exists
            variable = this.getEntry (variableString);
            if (variable != null)
            {
                // variable exists: replace with value
                v1 = StringHelpers.replace (v1, variableString, variable.value);
            } // if
            else
            {
                // variable does not exist: replace with 'UNDEFINED'
                v1 = StringHelpers.replace (v1, variableString, WorkflowConstants.UNDEFINED);
            } // else
        } // while

        // exit
        return v1;
    } // replaceWithValue


    /**************************************************************************
     * Gets name of a workflow-runtime variable hidden in a string. <BR/>
     * e.g. for 'abc#VARIABLE.hello#xyv' the value 'hello' will be returned.
     *
     * @param   string    a string
     *
     * @return  The variables name (in upper case)
     *          <CODE>null</CODE> if no variable found.
     */
    public String extractVariablesName (String string)
    {
        // check if parameter set
        if (string == null)
        {
            return null;
        } // if

        // store string in v1
        String v1 = string;

        // get position of occurence of '#VARIABLE.'
        int pos = v1.indexOf (WorkflowConstants.RUNTIME_PREFIX);
        int len = WorkflowConstants.RUNTIME_PREFIX.length ();

        // cut everything to end of '#VARIABLE.'
        v1 = v1.substring (pos + len);

        // get position of trailing '#'
        pos = v1.indexOf (WorkflowConstants.RUNTIME_POSTFIX);

        // cut everything after that position
        v1 = v1.substring (0, pos);

        // exit
        return v1;

    } // extractVariablesName


    /**************************************************************************
     * Gets 1st variable expression hidden in a string. <BR/>
     * e.g. for 'abc#VARIABLE.hello#acvdafs' the value '#VARIABLE.hello#' will
     * be returned.
     *
     * @param   string    a string
     *
     * @return  the variable (in upper case)
     *          <CODE>null</CODE> if no variable found.
     */
    public String extractVariable (String string)
    {
        // check if parameter set
        if (string == null)
        {
            return null;
        } // if

        // store string in v1
        String v1 = string;

        // get position of occurence of '#VARIABLE.'
        int pos = v1.indexOf (WorkflowConstants.RUNTIME_PREFIX);

        // cut everything to '#VARIABLE.', including the #
        v1 = v1.substring (pos + 1);

        // get position of trailing '#'
        pos = v1.indexOf (WorkflowConstants.RUNTIME_POSTFIX);

        // cut everything after that position
        v1 = v1.substring (0, pos + 1);

        // add leading '#'
        v1 = '#' + v1;

        // exit
        return v1;
    } // extractVariable


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
