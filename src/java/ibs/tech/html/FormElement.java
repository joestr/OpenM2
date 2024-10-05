/*
 * Class: FormElement.java
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
 * This is the FormElement Object, which builds a HTML-String
 * needed for a Form to be displayed
 *
 * @version     $Id: FormElement.java,v 1.8 2007/07/23 08:17:32 kreimueller Exp $
 *
 * @author      Christine Keim (CK), 980316
 ******************************************************************************
 */
public class FormElement extends Element
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: FormElement.java,v 1.8 2007/07/23 08:17:32 kreimueller Exp $";


    /**
     * Constant ELEMENTS_INITIAL of the Vector holding the
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
     * Specifies the url to go.
     */
    public String action;

    /**
     * Specifies the encriptiontype (for upload)
     * default: none (null)
     */
    public String enctype;

    /**
     * Specifies the framename
     * default : none (null)
     */
    public String target;

    /**
     * Specififies the method... (post or get)
     */
    public String method;

    /**
     * the elements to display
     */
    protected Vector<Element> elements;

    /**
     * Specifies the JavaScript-Code to use on Submit
     * default : none (null)
     */
    public String onSubmit;


    /**************************************************************************
     * Creates a new instance of a FormElement with the given URL, method and
     * target
     * Sets parameters to default values except the action, the method and the
     * target.
     * See the variables to know their default-values.
     *
     * @param   pAction URL or JavaScript to do
     * @param   pMethod Post or Get ?
     * @param   pTarget Targetframe
     */
    public FormElement (String pAction, String pMethod, String pTarget)
    {
        this.classId = null;
        this.id = null;
        this.name = null;
        this.action = pAction;
        this.method = pMethod;
        this.target = pTarget;
        this.elements = null;
        this.onSubmit = null;
        this.enctype = null;
    } // FormElement


    /**************************************************************************
     * Creates a new instance of a FormElement
     *
     * @param   pAction URL or JavaScript to do
     * @param   pMethod Post or Get ?
     */
    public FormElement (String pAction, String pMethod)
    {
        this.classId = null;
        this.id = null;
        this.name = null;
        this.action = pAction;
        this.method = pMethod;
        this.target = null;
        this.elements = null;
        this.onSubmit = null;
        this.enctype = null;
    } // FormElement


    /**************************************************************************
     * Adds an element to the Form at the given position. <BR/>
     *
     * @param   elem    ?????
     * @param   order   ?????
     */
    public void addElement (Element elem, int order)
    {
        if (this.elements == null)
        {
            this.elements = new Vector<Element> (FormElement.ELEMENTS_INITIAL,
                FormElement.ELEMENTS_INCREMENT);
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
     * Adds an Element to the Form at the last position. <BR/>
     *
     * @param   elem    ?????
     */
    public void addElement (Element elem)
    {
        if (this.elements == null)
        {
            this.elements = new Vector<Element> (FormElement.ELEMENTS_INITIAL,
                FormElement.ELEMENTS_INCREMENT);
        } // if
        this.elements.addElement (elem);
    } // addElement


    /**************************************************************************
     * Clears the element.
     */
    public void clear ()
    {
        this.elements = null;
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
            buf.append (IE302.TAG_FORMBEGIN);
            buf.append (IE302.TA_ACTION + this.inBrackets (this.action));
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
            if (this.enctype != null)
            {
                buf.append (IE302.TA_ENCTYPE + this.inBrackets (this.enctype));
            } // if
            if (this.target != null)
            {
                buf.append (IE302.TA_TARGET + this.inBrackets (this.target));
            } // if
            buf.append (IE302.TA_METHOD + this.inBrackets (this.method));
            if (this.onSubmit != null)
            {
                buf.append (IE302.TA_ONSUBMIT + this.inBrackets (this.onSubmit));
            } // if
            buf.append (IE302.TO_TAGEND);
            if (this.elements != null)       // there are some elements in the vector
            {
                for (Iterator<Element> iter = this.elements.iterator (); iter.hasNext ();)
                {
                    Element e = iter.next ();

                    if (e != null)
                    {
                        e.build (env, buf);
                    } // if
                } // for iter
            } // if
            buf.append (IE302.TAG_FORMEND + "\n");
        } // if browser is supported
    } // build

} // class FormElement
