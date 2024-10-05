/*
 * Class: BodyElement.java
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
 * This is the BodyElement Object, which builds a HTML-String
 * needed for the Body to be displayed
 *
 * @version     $Id: BodyElement.java,v 1.8 2007/07/23 08:17:32 kreimueller Exp $
 *
 * @author      Christine Keim (CK), 980318
 ******************************************************************************
 */
public class BodyElement extends Element
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: BodyElement.java,v 1.8 2007/07/23 08:17:32 kreimueller Exp $";


    /**
     * Constant ELEMENTS_INITIAL of the Vector holding the
     * elements.
     * set to 20
     */
    protected static final int ELEMENTS_INITIAL = 20;

    /**
     * Constant ELEMENTS_INCREMENT of the Vector holding the
     * elements.
     * set to 5
     */
    protected static final int ELEMENTS_INCREMENT = 5;

    /**
     * Framedefinition in Body?
     * (then the body tag should not be set.)
     * default: <CODE>false</CODE>
     */
    public boolean frameset;

    /**
     * Backgroundimage
     * default: none (<CODE>null</CODE>)
     *
     * @deprecated  This should be done via stylesheet (CSS).
     */
    public String bgimage;

    /**
     * the elements in the body of the page.
     */
    protected Vector<Element> elements;

    /**
     * Specifies the JavaScript-Code to use when the page finishes loading.
     */
    public String onLoad;

    /**
     * Specifies the JavaScript-Code to use when the page is left.
     */
    public String onUnload;



    /**************************************************************************
     * Creates a new instance of a BodyElement.
     * Sets parameters to default values. See the variables to know
     * their default-values.
     */
    public BodyElement ()
    {
        this.elements = null;
        this.frameset = false;
        this.bgimage = null;
        this.onLoad = null;
    } // BodyElement


    /**************************************************************************
     * Adds an element to the Body at the given position. <BR/>
     *
     * @param   str     Element to add
     * @param   order   position where to add element
     */
    public void addElement (String str, int order)
    {
        TextElement elem = new TextElement (str);
        if (this.elements == null)
        {
            this.elements = new Vector<Element> (BodyElement.ELEMENTS_INITIAL,
                BodyElement.ELEMENTS_INCREMENT);
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
     * Adds an Element to the Body at the last position. <BR/>
     *
     * @param   elem    Element to add
     */
    public void addElement (Element elem)
    {
        if (this.elements == null)
        {
            this.elements = new Vector<Element> (BodyElement.ELEMENTS_INITIAL,
                BodyElement.ELEMENTS_INCREMENT);
        } // if
        this.elements.addElement (elem);
    } // addElement


    /**************************************************************************
     * Clears the element.
     * Sets the variables to their default-values.
     */
    public void clear ()
    {
        this.elements = null;
        this.frameset = false;
        this.bgimage = null;
        this.onLoad = null;
    } // clear


    /**************************************************************************
     * Writes the element on the browser. <BR/>
     *
     * @param   env     OutputStream
     * @param   buf     Buffer to write the output to.
     *
     * @exception   BuildException
     *              An error occurred during building the output.
     */
    public void build (Environment env, StringBuffer buf) throws BuildException
    {
        if (this.isBrowserSupported (env))
                                        // browser is supported?
        {
            if (!this.frameset)
            {
                buf.append (IE302.TAG_BODYBEGIN);
                if (this.bgimage != null)
                {
                    buf.append (IE302.TA_BGIMAGE + this.inBrackets (this.bgimage));
                } // if
                if (this.onLoad != null)
                {
                    buf.append (IE302.TA_ONLOAD + this.inBrackets (this.onLoad));
                } // if
                if (this.onUnload != null)
                {
                    buf.append (IE302.TA_ONUNLOAD + this.inBrackets (this.onUnload));
                } // if
                buf.append (IE302.TO_TAGEND + "\n");
// ---------------------------
/*
                if (env.getBrowser ().equalsIgnoreCase (IOConstants.NS4))
                    buf.append ("Testbetrieb " + IE302.TAG_NEWLINE + IE302.TAG_NEWLINE);
*/
            } // if
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
            if (!this.frameset)
            {
                buf.append (IE302.TAG_BODYEND + "\n");
            } // if
        } // if
    } // build

} // class BodyElement
