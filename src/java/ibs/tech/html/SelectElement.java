/*
 * Class: SelectElement.java
 */

// package:
package ibs.tech.html;

// imports:
//KR TODO: unsauber
import ibs.io.Environment;
import ibs.tech.html.BuildException;
import ibs.tech.html.Element;
import ibs.tech.html.IE302;

import java.util.Iterator;
import java.util.Vector;


/******************************************************************************
 * This is the SelectElement Object, which builds a HTML-String
 * needed for a Selectionbox to be displayed
 *
 * @version     $Id: SelectElement.java,v 1.17 2009/07/24 08:36:38 kreimueller Exp $
 *
 * @author      Christine Keim (CK), 980316
 ******************************************************************************
 */
public class SelectElement extends Element
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: SelectElement.java,v 1.17 2009/07/24 08:36:38 kreimueller Exp $";


    /**
     * Constant initialElements of the Vector holding the
     * elements.
     * set to 20
     */
    protected static final int ELEMENTS_INITIAL = 20;

    /**
     * Constant incrementElements of the Vector holding the
     * elements.
     * set to 5
     */
    protected static final int ELEMENTS_INCREMENT = 5;

    /**
     * Specifies how many rows are displayed
     * default : 1
     */
    public int size;

    /**
     * Specifies if multiple selection is allowed
     * default : false
     */
    public boolean multiple;

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
     * Specifies the event to occur when the user double clicks in the element
     * default : none (null)
     */
    public String onDblClick;

    /**
     * Specifies a CSS style.
     * default : none (null)
     */
    public String style;

    /**
     * Option value not selected. <BR/>
     */
    private static final String OP_NOT_SELECTED = "false";
    /**
     * Option value was selected. <BR/>
     */
    private static final String OP_SELECTED = "true";

    /**
     * the options to display (with the info selected or not)
     */
    public Vector<String[]> options;


    /**************************************************************************
     * Creates a new instance of a SelectElement with the given name.
     *
     * @param   pName   ?????
     */
    public SelectElement (String pName)
    {
        this.id = null;
        this.name = pName;
        this.options = null;
        this.multiple = false;
        this.size = 1;
        this.onBlur = null;
        this.onChange = null;
        this.onClick = null;
        this.onFocus = null;
        this.onSelect = null;
    } // SelectElement


    /**************************************************************************
     * Creates a new instance of a SelectElement with multipleselect option
     *
     * @param   pName           ?????
     * @param   multipleAllowed ?????
     */
    public SelectElement (String pName, boolean multipleAllowed)
    {
        this.name = pName;
        this.id = null;
        this.options = null;
        this.multiple = multipleAllowed;
        this.size = 1;
        this.onBlur = null;
        this.onChange = null;
        this.onClick = null;
        this.onFocus = null;
        this.onSelect = null;

    } // SelectElement


    /**************************************************************************
     * Creates a new instance of a SelectElement with multipleselect option
     *
     * @param   pName           ?????
     * @param   pId           ?????
     * @param   multipleAllowed ?????
     */
    public SelectElement (String pName, String pId, boolean multipleAllowed)
    {
        this.name = pName;
        this.id = pId;
        this.options = null;
        this.multiple = multipleAllowed;
        this.size = 1;
        this.onBlur = null;
        this.onChange = null;
        this.onClick = null;
        this.onFocus = null;
        this.onSelect = null;

    } // SelectElement


    /**************************************************************************
     * Adds an option to the selectbox. <BR/>
     * If the boolean selected is true, then the added option is selected.
     *
     * @param   option      ?????
     * @param   selected    ?????
     * @param   value       ?????
     */
    public void addOption (String option, String value, boolean selected)
    {
        if (this.options == null)
        {
            this.options = new Vector<String[]> (SelectElement.ELEMENTS_INITIAL,
                SelectElement.ELEMENTS_INCREMENT);
        } // if
        String[] op = new String[3];
        op[0] = option;
        op[1] = value;
        op[2] = "" + selected;
        this.options.addElement (op);
    } // addOption


    /**************************************************************************
     * Adds an option to the selectbox. <BR/>
     *
     * @param   option  ?????
     * @param   value   ?????
     */
    public void addOption (String option, String value)
    {
        if (this.options == null)
        {
            this.options = new Vector<String[]> (SelectElement.ELEMENTS_INITIAL,
                SelectElement.ELEMENTS_INCREMENT);
        } // if
        String[] op = new String[3];
        op[0] = option;
        op[1] = value;
        op[2] = SelectElement.OP_NOT_SELECTED;
        this.options.addElement (op);
    } // addOption


    /**************************************************************************
     * Change the selected Option. <BR/>
     * Depending which of the parameter is set,
     * the array is search and the corresponding Option
     * (either comparing the name or the value)
     * is set to selected. All others are set to
     * not selected.
     *
     * @param   name    Name of the selection to select
     * @param   value   Value
     */
    public void changeSelected (String name, String value)
    {
        // if no value and no name is provided
        // then the action is not done
        if (name == null && value == null)
        {
            return;
        } // if

        // there are some elements in the vector
        for (Iterator<String[]> iter = this.options.iterator (); iter.hasNext ();)
        {
            String[] e = iter.next ();
            if (e != null)
            {
                if (name != null)
                {
                    // the name is the same
                    // set this option to selected
                    if (name.equalsIgnoreCase (e[0]))
                    {
                        e[2] = SelectElement.OP_SELECTED;
                    } // if
                    else
                    {
                        // set the option to not selected
                        e[2] = SelectElement.OP_NOT_SELECTED;
                    } // else
                } // if
                else
                {
                    // the value is the same
                    // set this option to selected
                    if (value.equalsIgnoreCase (e[1]))
                    {
                        e[2] = SelectElement.OP_SELECTED;
                    } // if
                    else
                    {
                        // set the option to not selected
                        e[2] = SelectElement.OP_NOT_SELECTED;
                    } // else
                } // else
            } // if
        } // for iter
    } // changeSelected


    /**************************************************************************
     * Clears the element.
     */
    public void clear ()
    {
        this.options = null;
    } // clear


    /**************************************************************************
     * Writes the element on the browser. <BR/>
     *
     * @param   env     OutputStream
     * @param   buf     Buffer where to write the output to.
     *
     * @exception   BuildException
     *              An error occurred during building the output.
     */
    public void build (Environment env, StringBuffer buf) throws BuildException
    {
        if (this.isBrowserSupported (env)) // browser is supported?
        {
            buf.append (IE302.TAG_SELECTBEGIN);
            if (this.name != null)
            {
                buf.append (IE302.TA_NAME + this.inBrackets (this.name));
            } // if
            if (this.id != null)
            {
                buf.append (IE302.TA_ID + this.inBrackets (this.id));
            } // if
            if (this.title != null)
            {
                buf.append (IE302.TA_TITLE + this.inBrackets (this.title));
            } // if
            if (this.multiple)
            {
                buf.append (IE302.TO_MULTIPLE);
            } // if
            buf.append (IE302.TA_SIZE + this.inBrackets ("" + this.size));

            // add events
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
            if (this.onDblClick != null)
            {
                buf.append (IE302.TA_ONDBLCLICK + this.inBrackets (this.onDblClick));
            } // if

            if (this.style != null)
            {
                buf.append (IE302.TA_STYLE + this.inBrackets (this.style));
            } // if

            buf.append (IE302.TO_TAGEND);

            if (this.options != null)   // there are some elements in the vector
            {
                for (Iterator<String[]> iter = this.options.iterator (); iter.hasNext ();)
                {
                    String[] e = iter.next ();
                    if (e != null)
                    {
                        buf.append (IE302.TAG_OPTIONBEGIN);
                        buf.append (IE302.TA_VALUE + this.inBrackets (e[1]));
                        if (e[2].equalsIgnoreCase (SelectElement.OP_SELECTED))
                        {
                            buf.append (IE302.TO_SELECTED);
                        } // if
                        buf.append (IE302.TO_TAGEND);
                        buf.append (e[0]);
                        buf.append (IE302.TAG_OPTIONEND);
                    } // if
                } // for iter
            } // if there are some elements in the vector
            buf.append (IE302.TAG_SELECTEND + "\n");
        } // if
    } // build


    /**************************************************************************
     * Creates a string which represents the element also the ability to add an
     * Java Script String at onChange. <BR/>
     * Is similar to build-Method. <BR/>
     *
     * @param   onChange    the javaScript for onChange
     *
     * @return  a selection list as a string.
     */
    public String getSelectElementString (String onChange)
    {
        String selectionList = "";      // return value of this method

        selectionList += IE302.TAG_SELECTBEGIN;
        if (this.name != null)
        {
            selectionList += IE302.TA_NAME + this.name;
        } // if

        if (this.id != null)
        {
            selectionList += IE302.TA_ID + this.id;
        } // if

        if (this.title != null)
        {
            selectionList += IE302.TA_TITLE + this.title;
        } // if

        if (this.multiple)
        {
            selectionList += IE302.TO_MULTIPLE;
        } // if

        selectionList += IE302.TA_SIZE + "" + this.size;

        if (onChange != null && onChange.length () > 0)
        {
            selectionList += IE302.TA_ONCHANGE + onChange;
        } // if

        selectionList += IE302.TO_TAGEND;

        if (this.options != null)       // there are some elements in the vector?
        {
            for (Iterator<String[]> iter = this.options.iterator (); iter.hasNext ();)
            {
                String[] e = iter.next ();
                if (e != null)
                {
                    selectionList += IE302.TAG_OPTIONBEGIN;
                    selectionList += IE302.TA_VALUE + e[1];
                    if (e[2].equalsIgnoreCase (SelectElement.OP_SELECTED))
                    {
                        selectionList += IE302.TO_SELECTED;
                    } // if
                    selectionList += IE302.TO_TAGEND;
                    selectionList += e[0];
                } // if
            } // for iter
        } // if there are some elements in the vector
        selectionList += IE302.TAG_SELECTEND;

        return selectionList;
    } // getSelectElementString

} // class SelectElement
