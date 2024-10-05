/*
 * Class: GeneralFunctionHandler.java
 */

// package:
package ibs.app.func;

//imports:
import ibs.app.AppFunctions;
import ibs.app.func.IFunctionHandler;
import ibs.bo.Buttons;
import ibs.bo.OID;
import ibs.service.list.XMLElement;
import ibs.tech.html.ButtonBarElement;
import ibs.tech.html.ButtonElement;
import ibs.tech.xml.XMLHelpers;
import ibs.util.list.ElementId;
import ibs.util.list.IElementId;
import ibs.util.list.ListException;

import java.io.File;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


/******************************************************************************
 * This class is the super class of all function handlers. <BR/>
 *
 * @version     $Id: GeneralFunctionHandler.java,v 1.15 2007/07/31 19:13:52 kreimueller Exp $
 *
 * @author      Klaus, 15.11.2003
 ******************************************************************************
 */
public class GeneralFunctionHandler extends XMLElement
    implements IFunctionHandler
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: GeneralFunctionHandler.java,v 1.15 2007/07/31 19:13:52 kreimueller Exp $";


    /**
     * The minimum function number handled by the function handler. <BR/>
     */
    private int p_minFunc = 0;

    /**
     * The maximum function number handled by the function handler. <BR/>
     */
    private int p_maxFunc = 0;

    /**
     * The function handler to be replaced by this one. <BR/>
     */
    public IElementId p_replacedId = null;



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a GeneralFunctionHandler object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   id      Id of the element.
     * @param   name    The element's name.
     */
    public GeneralFunctionHandler (int id, String name)
    {
        // call constructor of super class:
        this (new ElementId (id), name);

        // initialize the other instance properties:
    } // GeneralFunctionHandler


    /**************************************************************************
     * Creates a GeneralFunctionHandler object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   id      Id of the element.
     * @param   name    The element's name.
     */
    public GeneralFunctionHandler (IElementId id, String name)
    {
        // call constructor of super class:
        super (id, name);

        // initialize the other instance properties:
    } // GeneralFunctionHandler


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Evaluate the function to be performed. <BR/>
     * This method can be used if there shall be another function evaluated
     * within the evaluation of one function.
     *
     * @param   function    The function to be performed.
     * @param   oid         The oid of the object on which to perform the
     *                      function.
     * @param   origValues  Values of original function.
     *                      Necessary to get session and environment.
     *
     * @return  Function to be performed. <BR/>
     *          <CODE>AppFunctions.FCT_NOFUNCTION</CODE> if there is no function
     *          or the function was already performed.
     */
    protected int evalFunction (int function, OID oid, FunctionValues origValues)
    {
        FunctionValues values;          // the values for the function handlers

        try
        {
            // get the function values:
            values = (FunctionValues) origValues.clone ();
            values.setOid (oid);
            values.p_function = function;
            // get the object without reading the parameters:
            if (oid != null)
            {
                values.getObject (oid, false);
            } // if

            return values.getFunctionCache ().evalFunction (function, values);
        } // try
        catch (CloneNotSupportedException e)
        {
            return function;
        } // catch
    } // evalFunction


    /**************************************************************************
     * Evaluate the function to be performed. <BR/>
     *
     * @param   function    The function to be performed.
     * @param   values      The values for the function handler.
     *
     * @return  Function to be performed after this method. <BR/>
     *          {@link ibs.app.AppFunctions#FCT_NOFUNCTION AppFunctions.FCT_NOFUNCTION}
     *          if there is no function or the function was already performed.
     */
    public int evalFunction (int function, FunctionValues values)
    {
        int resultFunction = AppFunctions.FCT_NOFUNCTION;
                                        // the resulting function
        switch (function)               // perform function
        {
            case 999999999:             // special function
//System.out.println (this.getClass ().getName () + ": function found: " + function);
                break;

            default:                    // unknown function
                resultFunction = function; // function was not performed
/* code for sub classes:
                resultFunction = super.evalFunction (function);
                                        // evaluate function in super class
*/
        } // switch function

        // return which function shall be performed after this method:
        return resultFunction;
    } // evalFunction


    /**************************************************************************
     * Set the function number range handled by the function handler. <BR/>
     * If minFunc > maxFunc the values are changed.
     *
     * @param   minFunc     The minimum function number.
     * @param   maxFunc     The minimum function number.
     *
     * @throws  ListException
     *          One of the functions is not valid (&lt; 1).
     */
    public void setFunctionRange (int minFunc, int maxFunc)
        throws ListException
    {
        // check if the values are valid:
        if (minFunc > 0 && maxFunc > 0) // values valid?
        {
            // check if the values are set correctly:
            if (minFunc <= maxFunc)
            {
                // set the values as given:
                this.p_minFunc = minFunc;
                this.p_maxFunc = maxFunc;
            } // if
            else
            {
                // set the changed values:
                this.p_minFunc = maxFunc;
                this.p_maxFunc = minFunc;
            } // else
        } // if values valid
        else                            // values not valid
        {
            String startString = "Function handler " + this.getName () + ": ";
            String minFuncStr = "minFunc (" + minFunc + ")";
            String maxFuncStr = "maxFunc (" + maxFunc + ")";
            String msg = "out of range";
            String msgSing = "is " + msg;

            // check kind of invalidity and create correct error message:
            if (minFunc <= 0 && maxFunc <= 0)
            {
                throw new ListException (startString + minFuncStr +
                    " and " + maxFuncStr + " are " + msg);
            } // if
            else if (minFunc <= 0)
            {
                throw new ListException (startString + minFuncStr + msgSing);
            } // if
            else if (maxFunc <= 0)
            {
                throw new ListException (startString + maxFuncStr + msgSing);
            } // if
        } // else values not valid
    } // setFunctionRange


    /**************************************************************************
     * Set the replacement value. <BR/>
     *
     * @param   replace The value.
     */
    public void setReplacedId (IElementId replace)
    {
        this.p_replacedId = replace;
    } // setReplace


    /**************************************************************************
     * Get the minimum function number handled by the function handler. <BR/>
     *
     * @return  The function number.
     */
    public int getMinFunction ()
    {
        return this.p_minFunc;
    } // getMinFunction


    /**************************************************************************
     * Get the maximum function number handled by the function handler. <BR/>
     *
     * @return  The function number.
     */
    public int getMaxFunction ()
    {
        return this.p_maxFunc;
    } // getMaxFunction


    /**************************************************************************
     * Get the replacement value. <BR/>
     *
     * @return  The replace value.
     */
    public IElementId getReplacedId ()
    {
        return this.p_replacedId;
    } // getReplace


    /**************************************************************************
     * Returns the string representation of this object. <BR/>
     * The id and the name are concatenated to create a string
     * representation according to "id,name,fullName,minFunc,maxFunc,replace".
     *
     * @return  String represention of the object.
     */
    public String toString ()
    {
        // compute the string and return it:
        return super.toString () + "," +
            this.p_minFunc + "," + this.p_maxFunc +
            ((this.p_replacedId != null) ? (",repl=\"" + this.p_replacedId + "\"") : "");
    } // toString


    /**************************************************************************
     * Get the element type specific data out of the actual element data. <BR/>
     * This method is used to get all values of one element out of the
     * result set. <BR/>
     * <B>example:</B>. <BR/>
     * <PRE>
     * NamedNodeMap attributes = elemData.getAttributes ();
     * this.p_value = XMLHelpers.getAttributeValue (attributes, "itemName");
     * </PRE>
     *
     * @param   elemData    The data for the element.
     * @param   dataFile    The file which contains the data.
     *
     * @throws  ListException
     *          An error occurred during parsing the element.
     */
    public void setProperties (Node elemData, File dataFile)
        throws ListException
    {
        NamedNodeMap attributes = elemData.getAttributes ();
//        NamedNodeMap moduleAttributes = null; // attributes of module

//        moduleAttributes = elemData.getParentNode ().getParentNode ().getAttributes ();

        this.init (new ElementId (XMLHelpers.getAttributeValue (attributes, "id")),
            XMLHelpers.getAttributeValue (attributes, "name"));
        this.setFunctionRange (
            XMLHelpers.getAttributeValueInt (attributes, "minfunc"),
            XMLHelpers.getAttributeValueInt (attributes, "maxfunc"));

        try
        {
            this.setReplacedId (
                new ElementId (XMLHelpers.getAttributeValue (attributes, "replace")));
        } // try
        catch (ListException e)
        {
            // this value is optional, so set default value:
            this.setReplacedId (null);
        } // catch
//        this.p_moduleId = XMLHelpers.getAttributeValue (moduleAttributes, "id");
//        this.p_moduleVersion = XMLHelpers.getAttributeValue (moduleAttributes, "version");
    } // setProperties


    /**************************************************************************
     * Sets one button within a button bar. <BR/>
     *
     * @param   buttonBar       The ButtonBarElement Object where the button
     *                          shall be added to.
     * @param   buttonNo        Unique id of the button.
     * @param   function        Function to be executed when clicking on the
     *                          button.
     * @param   objectRights    Necessary rights on the object for the
     *                          button to be displayed.
     * @param   containerRights Necessary rights on the container for the
     *                          button to be displayed.
     */
    protected void setButton (ButtonBarElement buttonBar, int buttonNo,
                              String function, int objectRights, int containerRights)
    {
        buttonBar.addElement (new ButtonElement (
            buttonNo, Buttons.BTN_NAMES[buttonNo],
            Buttons.BTN_IMAGESACTIVE[buttonNo],
            Buttons.BTN_IMAGESINACTIVE[buttonNo],
            Buttons.BTN_DESCRIPTION[buttonNo],
            function, objectRights, containerRights));
    } // setButton


    /**************************************************************************
     * Sets one button within a button bar. <BR/>
     *
     * @param   buttonBar       The ButtonBarElement Object where the button
     *                          shall be added to.
     * @param   buttonNo        Unique id of the button.
     * @param   name            The name of the button.
     * @param   description     The description of the button.
     * @param   function        Function to be executed when clicking on the
     *                          button.
     * @param   objectRights    Necessary rights on the object for the
     *                          button to be displayed.
     * @param   containerRights Necessary rights on the container for the
     *                          button to be displayed.
     */
    protected void setButton (ButtonBarElement buttonBar, int buttonNo,
                              String name, String description, String function,
                              int objectRights, int containerRights)
    {
        buttonBar.addElement (new ButtonElement (buttonNo, name, null, null,
                                                 description, function,
                                                 objectRights, containerRights));
    } // setButton


    /**************************************************************************
     * Sets all possible buttons and constructs a buttonBar element. <BR/>
     *
     * @param   buttonBar   a ButtonBarElement Object where the buttons shall
     *                      be added to.
     *
     * @see #setButton (ButtonBarElement, int, String, int, int)
     */
    public void setButtons (ButtonBarElement buttonBar)
    {
        // nothing to do here
        // to be overwritten in sub classes
    } // setButtons


    /**************************************************************************
     * Sets the dependencies of the files filled. <BR/>
     */
    public void setDependentProperties ()
    {
        // nothing to do here
        // to be overwritten in sub classes
    } // setDependentProperties

} // class GeneralFunctionHandler
