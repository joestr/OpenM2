/*
 * Class: ConfVarContainer.java
 */

// package:
package ibs.service.module;

// imports:
import ibs.io.IOHelpers;
import ibs.service.module.ConfVar;
import ibs.service.module.ModuleConstants;
import ibs.util.list.ElementContainer;
import ibs.util.list.ElementId;
import ibs.util.list.ListException;

import java.util.Iterator;


/******************************************************************************
 * Container with configuration variables. <BR/>
 *
 * @version     $Id: ConfVarContainer.java,v 1.6 2007/07/23 12:34:23 kreimueller Exp $
 *
 * @author      Klaus, 17.12.2003
 ******************************************************************************
 */
public class ConfVarContainer extends ElementContainer<ConfVar>
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ConfVarContainer.java,v 1.6 2007/07/23 12:34:23 kreimueller Exp $";



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a ConfVarContainer object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @throws  ListException
     *          An error occurred during initializing the container.
     */
    public ConfVarContainer ()
        throws ListException
    {
        // call constructor of super class:
        super ();

        // initialize the instance's properties:
    } // ConfVarContainer


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Initialize the element class. <BR/>
     * This method shall be overwritten in sub classes.
     *
     * @throws  ListException
     *          The class could not be initialized.
     *
     * @see ibs.util.list.ElementContainer#setElementClass (Class)
     */
    protected void initElementClass ()
        throws ListException
    {
        this.setElementClass (ConfVar.class);
    } // initElementClass


    /**************************************************************************
     * Check constraints for the elements within the container. <BR/>
     *
     * @throws  ListException
     *          Error when checking the constraints.
     */
    public void checkConstraints ()
        throws ListException
    {
        ConfVarContainer noDefValueVars = new ConfVarContainer ();
                                        // all variables which have no
                                        // values set and not default values
        ConfVarContainer noValueVars = new ConfVarContainer ();
                                        // all variables which have no
                                        // values set
        String message = null;          // error message
        final String methodName = "checkConstraints";

        // loop through all elements and check if its value is set:
        for (Iterator<ConfVar> iter = this.iterator (); iter.hasNext ();)
        {
            // get the actual element:
            ConfVar elem = iter.next ();

            // check if the element's value was set:
            if (!elem.isValueSet ())
            {
                // check if there exists a default value:
                if (elem.p_value != null)
                {
                    // add the variable to the elements with no values set:
                    noValueVars.add (elem);
                } // if
                else                    // no default value
                {
                    // add the variable to the elements with no default values:
                    noDefValueVars.add (elem);
                } // else no default value
            } // if
        } // for iter

        // check if there occurred an error:
        if (noValueVars.size () > 0)
        {
            message =
                "No values set for the following configuration variables;" +
                " the default values are used: " +
                noValueVars;
            IOHelpers.printWarning (methodName, this, message);
        } // if

        // check if there occurred an error:
        if (noDefValueVars.size () > 0)
        {
            message =
                "No values set for the following configuration variables: " +
                noDefValueVars;
            IOHelpers.printError (methodName, this, message);
            throw new ListException (message);
        } // if
    } // checkConstraints


    /**************************************************************************
     * Set the values for the configuration variables. <BR/>
     *
     * @param   confValues  The configuration values to be set.
     *
     * @throws  ListException
     *          Error when setting the values. <BR/>
     *          Possible reasons:
     *          The module version of a variable and a value are different.
     */
    public void setValues (ConfValueContainer confValues)
        throws ListException
    {
        ConfValue confValue = null;     // the current config value
        ConfVar confVar = null;         // the current config variable
        ConfValueContainer notFoundValues = new ConfValueContainer ();
                                        // all values which are no
                                        // defined variables
        ConfValueContainer wrongVersionValues = new ConfValueContainer ();
                                        // all values which have
                                        // the wrong module version
        String message = null;          // error message
        final String methodName = "setValues";

        // loop through all values and set each one for a variable:
        for (Iterator<ConfValue> iter = confValues.iterator (); iter.hasNext ();)
        {
            // get the actual element:
            confValue = iter.next ();

            // get the corresponding variable:
            confVar = this.get (confValue.getId ());

            // check if the configuration variable is defined:
            if (confVar != null)
            {
                // check the module version:
                if (confVar.p_moduleVersion.isMatch (confValue.p_moduleVersion,
                    ModuleConstants.MATCH_PERFECT))
                                        // same version?
                {
                    // set the value:
                    confVar.setValue (confValue.p_value);
                } // if same version
                else                    // different versions
                {
                    // add the value to the wrong version elements:
                    wrongVersionValues.add (confValue);
                } // else different versions
            } // if
            else                        // variable not defined?
            {
                // add the value to the not found elements:
                notFoundValues.add (confValue);
            } // else variable not defined
        } // for iter

        // check if there occurred an error:
        if (notFoundValues.size () > 0)
        {
            message =
                "The following configuration values are no defined variables: " +
                notFoundValues;
            IOHelpers.printWarning (methodName, this, message);
        } // if

        // check if there occurred an error:
        if (wrongVersionValues.size () > 0)
        {
            message =
                "The following configuration values have another module" +
                " version than the configuration variable definitions: " +
                wrongVersionValues;
            IOHelpers.printError (methodName, this, message);
            throw new ListException (message);
        } // if
    } // setValues


    /**************************************************************************
     * Get the value for a variable. <BR/>
     * If the variable is not defined the return value is <CODE>null</CODE>.
     *
     * @param   variableName    The name of the variable.
     *
     * @return  The value of the variable or
     *          <CODE>null</CODE> if the variable is not defined.
     */
    public String getValue (String variableName)
    {
        String variableNameLocal = variableName; // variable for local assignments
        ConfVar confVar = null;         // the variable

//IOHelpers.printMessage ("    variableNameA: " + variableName);
        // ensure that the variable prefix is not part of the variable name:
        if (variableNameLocal.indexOf (ModuleConstants.CONFVAR_PREFIX) > -1)
        {
            variableNameLocal =
                variableNameLocal.substring (ModuleConstants.CONFVAR_PREFIX.length ());
        } // if

//IOHelpers.printMessage ("    variableNameB: " + variableName);
//IOHelpers.printMessage ("    confVars: " + this.toString ());
        try
        {
            // get the variable:
            if ((confVar = this.get (new ElementId (variableNameLocal))) != null)
            {
                // return the variable value:
                return confVar.p_value;
            } // if

            // the variable was not found
            // return the error code:
            return null;
        } // try
        catch (ListException e)
        {
            // not a valid variable name
            // return the error code:
            return null;
        } // catch
    } // getValue

} // class ConfVarContainer
