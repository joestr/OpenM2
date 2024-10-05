/*
 * Class: InputElement.java
 */

// package:
package ibs.tech.html;

// imports:
//KR TODO: unsauber
import ibs.io.Environment;
import ibs.tech.html.BuildException;
import ibs.tech.html.Element;
import ibs.tech.html.IE302;
import ibs.tech.html.TextAreaElement;
import ibs.util.FormFieldRestriction;


/******************************************************************************
 * This is the InputElement Object, which builds a HTML-String
 * needed for a Input to be displayed from the browser.
 *
 * @version     $Id: InputElement.java,v 1.10 2007/07/31 19:13:59 kreimueller Exp $
 *
 * @author      Christine Keim (CK), 980316
 ******************************************************************************
 */
public class InputElement extends Element
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: InputElement.java,v 1.10 2007/07/31 19:13:59 kreimueller Exp $";


    /**
     * Input type: BUTTON. <BR/>
     */
    public static final String INP_BUTTON    =  new String ("BUTTON");
    /**
     * Input type: TEXT. <BR/>
     */
    public static final String INP_TEXT      =  new String ("TEXT");
    /**
     * Input type: TEXTAREA. <BR/>
     */
    public static final String INP_TEXTAREA  =  new String ("TEXTAREA");
    /**
     * Input type: PASSWORD. <BR/>
     */
    public static final String INP_PASSWORD  =  new String ("PASSWORD");
    /**
     * Input type: CHECKBOX. <BR/>
     */
    public static final String INP_CHECKBOX  =  new String ("CHECKBOX");
    /**
     * Input type: RADIO button. <BR/>
     */
    public static final String INP_RADIO     =  new String ("RADIO");
    /**
     * Input type: SUBMIT button. <BR/>
     */
    public static final String INP_SUBMIT    =  new String ("SUBMIT");
    /**
     * Input type: RESET button. <BR/>
     */
    public static final String INP_RESET     =  new String ("RESET");
    /**
     * Input type: FILE. <BR/>
     */
    public static final String INP_FILE      =  new String ("FILE");
    /**
     * Input type: HIDDEN. <BR/>
     */
    public static final String INP_HIDDEN    =  new String ("HIDDEN");
    /**
     * Input type: IMAGE. <BR/>
     */
    public static final String INP_IMAGE     =  new String ("IMAGE");

    /**
     * Specifies the defaultvalue
     * default : none (null)
    */
    public String value;

    /**
     * Specifies type of the input. <BR/>
     * There are 11 types available.
     * Text, Textarea, Password, checkbox,
     * radio, submit, reset, image, file, hidden, button
     */
    public String type;

    /**
     * Only for checkboxes.
     * default : false
     */
    public boolean checked;

    /**
     * Only for text and password and textarea
     * default : none (null)
     */
    public int size;

    /**
     * Only for textarea
     * default : 1
     */
    public int lines;

    /**
     * Only for text and password and textarea,
     * default : no maximal length
     */
    public int maxlength;

    /**
     * Source for the image, if the inputelement is a image
     * default : none (null)
     */
    public String src;

    /**
     * Only for textarea,
     * default : no wrapping
     */
    public String wrap;

    /**
     * Specifies the event to occur when the element loses focus.
     * default : none (null)
     */
    public String onBlur;

    /**
     * Specifies the event to occur when the values of the element is changed.
     * default : none (null)
     */
    public String onChange;

    /**
     * Specifies the event to occur when the mouse is clicked on the element.
     * default : none (null)
     */
    public String onClick;

    /**
     * Specifies the event to occur when the cursor focus is on the element.
     * default : none (null)
     */
    public String onFocus;

    /**
     * Specifies the event to occur when the element is selected.
     * default : none (null)
     */
    public String onSelect;

    /**
     * Specifies if the input field is readonly. <BR/>
     */
    public boolean readonly = false;


    /**************************************************************************
     * Create a new instance of a InputElement of a given type.
     * Sets parameters to default values except the name and the type.
     * See the variables to know their default-values.
     *
     * @param   pName   ?????
     * @param   pType   ?????
     */
    public InputElement (String pName, String pType)
    {
        this.classId = null;
        this.id = null;
        this.name = pName;
        this.type = pType;
        this.value = null;
        this.checked = false;
        this.src = null;
        this.maxlength = -1;
        this.lines = -1;
        this.size = -1;
        this.wrap = null;
        this.onBlur = null;
        this.onChange = null;
        this.onClick = null;
        this.onFocus = null;
        this.onSelect = null;
    } // InputElement


    /**************************************************************************
     * Create a new instance of a InputElement with a given type with a
     * given value.
     * Sets parameters to default values except the name, the type and the value.
     * See the variables to know their default-values.
     *
     * @param   pName   ?????
     * @param   pType   ?????
     * @param   pValue  ?????
     */
    public InputElement (String pName, String pType, String pValue)
    {
        this.classId = null;
        this.id = null;
        this.name = pName;
        this.type = pType;
        this.value = pValue;
        this.checked = false;
        this.src = null;
        this.maxlength = -1;
        this.lines = -1;
        this.size = -1;
        this.wrap = null;
        this.onBlur = null;
        this.onChange = null;
        this.onClick = null;
        this.onFocus = null;
        this.onSelect = null;
    } // InputElement


    /***************************************************************************
     * Set size of input field. <BR/>
     * The method checks whether a restriction is set.
     * For both displaySize and maxLength: if there is a value set in the
     * restriction this value is used. Otherwise the corresponding parameter
     * (displaySize or maxLength) is used.
     *
     * @param   restriction The restriction to be checked.
     * @param   displaySize The default display size.
     * @param   maxLength   The default length.
     */
    public void setSize (final FormFieldRestriction restriction,
                         final int displaySize, final int maxLength)
    {
        // set view-size of input field:
        if (restriction != null && restriction.viewLength > 0)
        {
            // set length:
            this.size = restriction.viewLength;
        } // if
        else
        {
            // set default value:
            this.size = displaySize;
        } // else

        // set maximum size of input field content:
        if (restriction != null && restriction.maxLength > 0)
        {
            this.maxlength = restriction.maxLength;
        } // if
        else
        {
            this.maxlength = maxLength;
        } // else
    } // setSize


    /**************************************************************************
     * Writes the element on the browser. <BR/>
     *
     * @param   env         OutputStream
     * @param   buf         The buffer to be filled with the build result.
     *
     * @exception   BuildException
     *              An error occurred during building the output.
     */
    public void build (Environment env, StringBuffer buf) throws BuildException
    {
        if (this.isBrowserSupported (env)) // browser is supported?
        {
            if (this.type.equalsIgnoreCase (InputElement.INP_TEXTAREA) &&
                this.wrap != null)
            {
                TextAreaElement temp =
                    new TextAreaElement (this.name, this.value);
                temp.id = this.id;
                temp.classId = this.classId;
                temp.rows = this.lines;
                temp.cols = this.size;
                temp.wrap = this.wrap;
                temp.maxlength = this.maxlength;
                temp.build (env, buf);
            } // if
            else
            {
                buf.append (IE302.TAG_INPUTBEGIN);
                if (this.name != null)
                {
                    buf.append (IE302.TA_NAME + this.inBrackets (this.name));
                } // if
                if (this.id != null)
                {
                    buf.append (IE302.TA_ID + this.inBrackets (this.id));
                } // if
                if (this.classId != null)
                {
                    buf.append (IE302.TA_CLASSID + this.inBrackets (this.classId));
                } // if
                if (this.title != null)
                {
                    buf.append (IE302.TA_TITLE + this.inBrackets (this.title));
                } // if

                if (this.onBlur != null)
                {
                    buf.append (IE302.TA_ONBLUR + this.inBrackets (this.onBlur));
                } // if
                if (this.onChange != null)
                {
                    buf.append (IE302.TA_ONCHANGE + this.inBrackets (this.onChange));
                } // if
                if (this.onClick != null)
                {
                    buf.append (IE302.TA_ONCLICK + this.inBrackets (this.onClick));
                } // if
                if (this.onFocus != null)
                {
                    buf.append (IE302.TA_ONFOCUS + this.inBrackets (this.onFocus));
                } // if
                if (this.onSelect != null)
                {
                    buf.append (IE302.TA_ONSELECT + this.inBrackets (this.onSelect));
                } // if

                if (this.type != null)
                {
                    buf.append (IE302.TA_TYPE + this.inBrackets (this.type));
                    if ((this.type.equalsIgnoreCase (InputElement.INP_RADIO)) ||
                        (this.type.equalsIgnoreCase (InputElement.INP_CHECKBOX)))
                    {
                        if (this.checked)
                        {
                            buf.append (IE302.TO_CHECKED);
                        } // if
                    } // if
                    if ((this.type.equalsIgnoreCase (InputElement.INP_TEXT)) ||
                        (this.type.equalsIgnoreCase (InputElement.INP_PASSWORD)) ||
                        (this.type.equalsIgnoreCase (InputElement.INP_TEXTAREA)))
                    {
                        if (this.maxlength != -1)
                        {
                            buf.append (IE302.TA_MAXLENGTH +
                                this.inBrackets ("" + this.maxlength));
                        } // if
                        if (this.type.equalsIgnoreCase (InputElement.INP_TEXT) ||
                            this.type.equalsIgnoreCase (InputElement.INP_PASSWORD))
                        {
                            if (this.size != -1)
                            {
                                buf.append (IE302.TA_SIZE +
                                    this.inBrackets ("" + this.size));
                            } // if
                        } // if
                        if (this.type.equalsIgnoreCase (InputElement.INP_TEXTAREA))
                        {
                            if (this.size != -1)
                            {
                                buf.append (IE302.TA_SIZE +
                                    this.inBrackets (this.size + "," + this.lines));
                            } // if
                        } // if
                        if (this.readonly)
                        {
                            buf.append (IE302.TA_READONLY + this.inBrackets (""));
                        } // if
                    } // if
                    if (this.type.equalsIgnoreCase (InputElement.INP_IMAGE))
                    {
                        buf.append (IE302.TA_SRC + this.inBrackets (this.src));
                    } // if
                } // if
                if (this.value != null)
                {
                    buf.append (IE302.TA_VALUE + this.inBrackets (this.value));
                } // if
                buf.append (IE302.TO_TAGEND);
            } // else
        } // if
    } // build

} // class InputElement
