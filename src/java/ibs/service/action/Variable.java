/*
 * Class: Variable.java
 */

// package:
package ibs.service.action;

// imports:
import ibs.BaseObject;
import ibs.service.action.ActionConstants;


/******************************************************************************
 * The Variable holds information about one runtime-variable
 * defined in workflow or form templates. <BR/>
 *
 * @version     $Id: Variable.java,v 1.11 2007/07/24 21:27:33 kreimueller Exp $
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
        "$Id: Variable.java,v 1.11 2007/07/24 21:27:33 kreimueller Exp $";


    ///////////////////////////////////////////////
    //
    // Information that comes from the XML-definition
    //

    /**
     *  name of variable. <BR/>
     */
    private String name = ActionConstants.UNDEFINED;

    /**
     *  type of variable. <BR/>
     */
    private String type = ActionConstants.UNDEFINED;

    /**
     * max. length of the variable. <BR/>
     */
    private String length = ActionConstants.UNDEFINED;

    /**
     * description of the variable. <BR/>
     */
    private String description = ActionConstants.UNDEFINED;

    /**
     * value of the variable. <BR/>
     */
    private String value = ActionConstants.UNDEFINED;


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
        String typeLocal = type;        // variable for local assignments

        // default type is string
        if (typeLocal == null)
        {
            typeLocal = ActionConstants.VARIABLETYPE_TEXT;
        } // if

        this.name = name;
        this.type = typeLocal;
        this.length = length;
        this.description = description;
        this.value = value;
    } // Variable


    /**************************************************************************
     * Returns a copy (clone) of the Variable object. <BR/>
     *
     * @return  the copied object
     */
    public Variable getCopy ()
    {
        return new Variable (this.name, this.type, this.length, this.description, this.value);
    } // getCopy


    /**************************************************************************
     * Tells if this variable is a system-variable, e.g. #SYSVAR.name#. <BR/>
     *
     * @return  true if the variable is a system variable otherwise false.
     */
    public boolean isSystemVariable ()
    {
        // check if name holds '#SYSVAR'
        if (this.name.indexOf (ActionConstants.SYSVAR_PREFIX) == -1)
        {
            return false;
        } // if

        return true;
    } // isTypeNumber


    /**************************************************************************
     * Tells if this variable is a user-defined-variable, e.g. #VARIABLE.name#. <BR/>
     *
     * @return  true if the variable is a userdefined variable otherwise false.
     */
    public boolean isUserDefined ()
    {
        // check if name holds '#SYSVAR'
        if (this.name.indexOf (ActionConstants.VARIABLE_PREFIX) == -1)
        {
            return false;
        } // if

        return true;
    } // isTypeNumber


    /**************************************************************************
     * Tells if this variable is of type 'TEXT'. <BR/>
     *
     * @return  true if the variable is a string variable otherwise false.
     */
    public boolean isTypeText ()
    {
        return this.type.equalsIgnoreCase (ActionConstants.VARIABLETYPE_TEXT);
    } // isTypeText


    /**************************************************************************
     * Tells if this variable is of type 'NUMBER'. <BR/>
     *
     * @return  true if the variable is a number variable otherwise false.
     */
    public boolean isTypeNumber ()
    {
        return this.type.equalsIgnoreCase (ActionConstants.VARIABLETYPE_NUMBER);
    } // isTypeNumber



    /**************************************************************************
     * Returns variables name. <BR/>
     *
     * @return  the name of the variable.
     */
    public String getName ()
    {
        return this.name;
    } // getName


    /**************************************************************************
     * Returns the basic part of the variable name.
     * This is the part between the pre- and the postfix. <BR/>
     *
     * @return      the basic name of the variable
     */
    public String getBasicName ()
    {
        if (this.name == null)
        {
            return null;
        } // if

        // get the position of the dot in the variable name
        int dotPos = this.name.indexOf ('.');
        // if the name has no dot return the full name
        if (dotPos < 0)
        {
            return this.name;
        } // if

        return this.name.substring (dotPos + 1, this.name.length () - 1);
    } // getBasicName


    /**************************************************************************
     * Returns the type of the variable. <BR/>
     *
     * @return  the type of the variable
     */
    public String getType ()
    {
        return this.type;
    } // getType


    /**************************************************************************
     * Returns the value of the variable. <BR/>
     *
     * @return  the valie of the variable as string
     */
    public String getValue ()
    {
        return this.value;
    } // getValue


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
     * Sets the value for the variable. <BR/>
     *
     * @param   val     the value as string
     */
    public void setTextValue (String val)
    {
        this.value = val;
    } // setTextValue


    /**************************************************************************
     * Returns variables NUMBER-value as double. <BR/>
     *
     * @return  variables NUMBER value as double;
     *          null if variable is not set, type is not NUMBER or
     *          value cannot be transformed to a double value
     *
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
     * Returns variables length. <BR/>
     *
     * @return  the length of the variable
     *          -1 if not set or no number
     */
    public int getLength ()
    {
        try
        {
            Integer i = new Integer (this.length);
            return i.intValue ();
        } // try
        catch (NumberFormatException e)
        {
            return -1;
        } // catch NumberFormatException
    } // getName


    /**************************************************************************
     * Returns a string representation of the object. <BR/>
     *
     * @return  a string representation of the object.
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
