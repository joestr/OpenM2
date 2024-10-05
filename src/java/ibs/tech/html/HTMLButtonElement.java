/*
 * Class: HTMLButtonElement.java
 */

// package:
package ibs.tech.html;

// imports:
//KR TODO: unsauber
import java.util.Iterator;
import java.util.Vector;

import ibs.io.Environment;
import ibs.tech.html.BuildException;
import ibs.tech.html.Element;
import ibs.tech.html.IE302;


/******************************************************************************
 * This is the HTMLButtonElement Object, which builds a HTML-String
 * needed for a BUTTON to be displayed from the browser.
 *
 * @version     $Id: HTMLButtonElement.java,v 1.7 2011/09/27 10:52:35 btatzmann Exp $
 *
 * @author      Bernd Buchegger (BB), 20070608
 ******************************************************************************
 */
public class HTMLButtonElement extends Element
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: HTMLButtonElement.java,v 1.7 2011/09/27 10:52:35 btatzmann Exp $";

    /**
     * Constant initialElements of the Vector holding the
     * elements.
     * set to 20
     */
    protected static final int INITIAL_ELEMENTS = 20;

    /**
     * Constant incrementElements of the Vector holding the
     * elements.
     * set to 5
     */
    protected static final int INCREMENT_ELEMENTS = 5;

    /**
     * Input type: BUTTON. <BR/>
     */
    public static final String INP_BUTTON = new String ("BUTTON");
    /**
     * Input type: SUBMIT button. <BR/>
     */
    public static final String INP_SUBMIT = new String ("SUBMIT");
    /**
     * Input type: RESET button. <BR/>
     */
    public static final String INP_RESET = new String ("RESET");

    /**
     * Prefix and postfix for button texts. <BR/>
     */
    public static final String BUTTON_LABEL_BOUNDARY = IE302.HCH_NBSP +
        IE302.HCH_NBSP + IE302.HCH_NBSP + IE302.HCH_NBSP + IE302.HCH_NBSP;

    /**
     * Onclick event: submit. <BR/>
     * 
     * BT IBS-643: Within FF and IE>=9 the form submit is automatically called if the button is of TYPE=SUBMIT.
     *             So it is only necessary for browsers < IE9 to call form.submit() explicitly.
     */
    public static final String ONCLICK_SUBMIT = ";if (top.system.browser.ie && top.system.browser.version < 9 && form.onsubmit ()) {form.submit();};";

    /**
     * Onclick event: go back. <BR/>
     */
    public static final String ONCLICK_GOBACK = "top.goback (0);";


    /**
     * Specifies the defaultvalue
     * default : none (null)
    */
    public String value;

    /**
     * Specifies type of the input <BR/>
     * There are 11 types available.
     * Text, Textarea, Password, checkbox,
     * radio, submit, reset, image, file, hidden, button
     */
    public String type;

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
     * Specifies if the button is readonly.<BR/>
     */
    public boolean disabled = false;

    /**
     * Specifies if the button will be disabled on click.<BR/>
     */
    public boolean isDisabledOnClick = true;

    /**
     * The time in milliseconds the button will be disabled after click.<BR/>
     */
    public int disabledTime = 3000;

    /**
     * The elements
     */
    protected Vector<Element> elements;


    /**************************************************************************
     * Create a new instance of a HTMLButtonElement of a given type.
     * Sets parameters to default values.
     * See the variables to know their default-values.
     */
    public HTMLButtonElement ()
    {
        this.classId = null;
        this.id = null;
        this.name = null;
        this.type = null;
        this.value = null;
        this.onBlur = null;
        this.onChange = null;
        this.onClick = null;
        this.onFocus = null;
        this.onSelect = null;
        this.disabled = false;
        this.elements = null;
    } // HTMLButtonElement


    /**************************************************************************
     * Create a new instance of a HTMLButtonElement of a given type.
     * Sets parameters to default values except the name and the type.
     * See the variables to know their default-values.
     *
     * @param   pName   the name of the button (will also be set as id)
     * @param   pType   the type of the button
     */
    public HTMLButtonElement (String pName, String pType)
    {
        this.classId = null;
        this.id = pName;
        this.name = pName;
        this.type = pType;
        this.value = null;
        this.onBlur = null;
        this.onChange = null;
        this.onClick = null;
        this.onFocus = null;
        this.onSelect = null;
        this.disabled = false;
        this.elements = null;
    } // HTMLButtonElement


    /**************************************************************************
     * Adds an element to the division at the last position
     *
     * @param   elem    the element to add
     */
    public void addElement (Element elem)
    {
        if (this.elements == null)
        {
            this.elements = new Vector<Element> (
                HTMLButtonElement.INITIAL_ELEMENTS,
                HTMLButtonElement.INCREMENT_ELEMENTS);
        } // if
        this.elements.addElement (elem);
    } // addElement


    /**************************************************************************
     * Adds an element to the division at the given position
     *
     * @param   elem    the element to add
     * @param   order   position where to add the element
     */
    public void addElement (Element elem, int order)
    {
        if (this.elements == null)
        {
            this.elements = new Vector<Element> (
                HTMLButtonElement.INITIAL_ELEMENTS,
                HTMLButtonElement.INCREMENT_ELEMENTS);
        } // if
        try
        {
            this.elements.insertElementAt (elem, order);
        } // try
        catch (ArrayIndexOutOfBoundsException e)
        {
            this.elements.setSize (order);
            this.elements.insertElementAt (elem, order);
        } // catch
    } // addElement


    /**************************************************************************
     * Add a label for the button.<BR/>
     *
     * @param   label   the label to add
     */
    public void addLabel (String label)
    {
        this.addElement (new TextElement (label));
    } // addLabel


    /**************************************************************************
     * Writes the element on the browser. <BR/>
     *
     * @param   env         OutputStream
     * @param   buf         Buffer for writing the output to.
     *
     * @exception   BuildException
     *              An error occurred during building the output.
     */
    public void build (Environment env, StringBuffer buf) throws BuildException
    {
        if (this.isBrowserSupported (env))
                                        // browser is supported?
        {
            buf.append (IE302.TAG_BUTTONBEGIN);
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
            if (this.onClick != null || this.isDisabledOnClick)
            {
                StringBuffer onClickCode = new StringBuffer ();
                if (this.isDisabledOnClick)
                {
                    onClickCode.append ("this.disabled = true; setEnableTimeout (this, ")
                        .append (this.disabledTime).append (");");
                } // if (isDisabledOnClick)
                if (this.onClick != null)
                {
                    onClickCode.append (this.onClick);
                } // if (this.onClick != null)
                buf.append (IE302.TA_ONCLICK + this.inBrackets (onClickCode.toString ()));
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
            } // if
            if (this.disabled)
            {
                buf.append (IE302.TA_DISABLED + this.inBrackets (""));
            } // if
            if (this.value != null)
            {
                buf.append (IE302.TA_VALUE + this.inBrackets (this.value));
            } // if
            buf.append (IE302.TO_TAGEND);

            for (Iterator<Element> iter = this.elements.iterator (); iter.hasNext ();)
            {
                Element e = iter.next ();

                if (e != null)
                {
                    e.build (env, buf);
                } // if
            } // for iter

            buf.append (IE302.TAG_BUTTONEND + "\n");
        } // if (isBrowserSupported (env))
    } // build

} // class HTMLButtonElement
