/*
 * Class: Variable.java
 */

// package:
package ibs.service.workflow;

// imports:
import ibs.BaseObject;
import ibs.service.workflow.WorkflowConstants;


/******************************************************************************
 * The Variable holds information about one workflow runtime-variable. <BR/>
 *
 * @version     $Id: Variable.java,v 1.11 2007/07/31 19:13:59 kreimueller Exp $
 *
 * @author      Horst Pichler (HP), 2.10.2000
 ******************************************************************************
 */
public class Variable extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Variable.java,v 1.11 2007/07/31 19:13:59 kreimueller Exp $";


    ///////////////////////////////////////////////
    //
    // Information that comes from the XML-definition
    //

    /**
     *  name of variable. <BR/>
     */
    public String name = WorkflowConstants.UNDEFINED;

    /**
     *  type of variable. <BR/>
     */
    public String type = WorkflowConstants.UNDEFINED;

    /**
     * max. length of the variable. <BR/>
     */
    public String length = WorkflowConstants.UNDEFINED;

    /**
     * description of the variable. <BR/>
     */
    public String description = WorkflowConstants.UNDEFINED;

    /**
     * value of the variable. <BR/>
     */
    public String value = WorkflowConstants.UNDEFINED;


    /**************************************************************************
     * Creates a Variable. <BR/>
     */
    public Variable ()
    {
        // nothing to do
    } // Variable


    /**************************************************************************
     * Creates an Variable. <BR/>
     *
     * @param name          name of variable
     * @param type          type of variable
     * @param length        length of variable
     * @param description   description of variable
     * @param value         value of variable
     */
    public Variable (String name, String type, String length,
                     String description, String value)
    {
        this.name = name;
        this.type = type;
        this.length = length;
        this.description = description;
        this.value = value;
    } // Variable


    /**************************************************************************
     * Tells if this variable is of type 'TEXT'. <BR/>
     *
     * @return  <CODE>true</CODE> if the variable is a TEXT type,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean isTypeText ()
    {
        if (this.type.equalsIgnoreCase (WorkflowConstants.VARIABLETYPE_TEXT))
        {
            return true;
        } // if

        return false;
    } // isTypeText


    /**************************************************************************
     * Tells if this variable is of type 'NUMBER'. <BR/>
     *
     * @return  <CODE>true</CODE> if the variable is a NUMBER type,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean isTypeNumber ()
    {
        if (this.type.equalsIgnoreCase (WorkflowConstants.VARIABLETYPE_NUMBER))
        {
            return true;
        } // if

        return false;
    } // isTypeNumber


    /**************************************************************************
     * Returns variables TEXT-value as string. <BR/>
     *
     * @return  variables TEXT value as string
     *          null if variable is not set or type is not TEXT
     */
    public String getTextValue ()
    {
        if (this.isTypeText () && this.value != null)
        {
            return this.value;
        } // if

        return null;
    } // getTextValue


    /**************************************************************************
     * Returns variables NUMBER-value as double. <BR/>
     *
     * @return  variables NUMBER value as double;
     *          null if variable is not set, type is not NUMBER or
     *          value cannot be transformed to a double value
     */
    public Double getNumberValue ()
    {
        if (this.isTypeNumber () && this.value != null)
        {
            try
            {
                Double d = new Double (this.value);
                return d;
            } // try
            catch (NumberFormatException e)
            {
                return null;
            } // catch
        } // if

        return null;
    } // getNumberValue


    /**************************************************************************
     * Returns a string representation of this object. <BR/>
     *
     * @return  a string representation of this object
     */
    public String toString ()
    {
        // declare variables
        String str = "";

        // build string
        str += "name = " + this.name + "; " +
               "type = " + this.type + "; " +
               "length = " + this.length + "; " +
               "description = " + this.description + "; " +
               "value = " + this.value;

        return str;
    } // toString

} // class Variable
