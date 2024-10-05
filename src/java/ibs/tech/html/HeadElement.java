/*
 * Class: HeadElement.java
 */

// package:
package ibs.tech.html;

// imports:
//KR TODO: unsauber
import ibs.io.Environment;
import ibs.tech.html.BuildException;
import ibs.tech.html.Element;
import ibs.tech.html.IE302;
import ibs.tech.html.ScriptElement;

import java.util.Iterator;
import java.util.Vector;


/*****************************************************************************
 * This is the HeadElement Object, which builds a HTML-String
 * needed for the Head to be constructed
 *
 * @version     $Id: HeadElement.java,v 1.10 2007/07/23 08:17:32 kreimueller Exp $
 *
 * @author      Christine Keim (CK), 980318
 ******************************************************************************
 */
public class HeadElement extends Element
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: HeadElement.java,v 1.10 2007/07/23 08:17:32 kreimueller Exp $";


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
     * The base of the page
     * default: none (null)
     */
    public String base = null;

    /**
     * The base of the page as javascript code. <BR/>
     * This script is always at the first position in the head element.
     * default: none (null)
     */
    public ScriptElement baseScript = null;

    /**
     * The elements in the head. <BR/>
     */
    Vector<Element> elements = null;



    /**************************************************************************
     * Creates a new instance of a HeadElement.
     */
    public HeadElement ()
    {
        this.elements = null;
        this.title = null;
        this.base = null;
    } // HeadElement


    /**************************************************************************
     * Creates a new instance of a HeadElement with the given title. <BR/>
     *
     * @param pTitle    .....Title of the Page
     */
    public HeadElement (String pTitle)
    {
        this.elements = null;
        this.title = pTitle;
        this.base = null;
    } // HeadElement


    /**************************************************************************
     * Adds an element to the Head at the given position. <BR/>
     *
     * @param   str     ?????
     * @param   order   ?????
     *
     * @deprecated 20070723 This method is never used.
     */
    public void addElement (String str, int order)
    {
        TextElement elem = new TextElement (str);
        if (this.elements == null)
        {
            this.elements = new Vector<Element> (HeadElement.ELEMENTS_INITIAL,
                HeadElement.ELEMENTS_INCREMENT);
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
     * Adds an Element to the Head at the last position. <BR/>
     *
     * @param   elem    ?????
     */
    public void addElement (Element elem)
    {
        if (this.elements == null)
        {
            this.elements = new Vector<Element> (HeadElement.ELEMENTS_INITIAL,
                HeadElement.ELEMENTS_INCREMENT);
            this.elements.addElement (elem);
        } // if
        else
        {
            this.elements.addElement (elem);
        } // else
    } // addElement


    /**************************************************************************
     * Clears the element.
     */
    public void clear ()
    {
        this.elements = null;
        this.title = null;
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
            buf.append (IE302.TAG_HEADBEGIN);
            if (this.base != null)
            {
                buf.append ("<BASE HREF=\"" + this.base + "\">");
            } // if
            else if (this.baseScript != null)
            {
                this.baseScript.build (env, buf);
            } // else if
            if (this.title != null)
            {
                buf.append (IE302.TAG_TITLEBEGIN + this.title + IE302.TAG_TITLEEND);
            } // if

            if (this.elements != null)  // there are some elements in the vector?
            {
                for (Iterator<Element> iter = this.elements.iterator ();
                     iter.hasNext ();)
                {
                    Element e = iter.next ();

                    if (e != null)
                    {
                        e.build (env, buf);
                    } // if
                } // for iter
            } // if there are some elements in the vector

            buf.append (IE302.TAG_HEADEND + "\n");
        } // if browser is supported
    } // build

} // class HeadElement
