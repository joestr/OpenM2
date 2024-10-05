/*
 * Class: DivElement.java
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
 * This is the DivElement Object, which can hold more elements
 * at a time
 *
 * @version     $Id: DivElement.java,v 1.8 2007/07/23 08:17:32 kreimueller Exp $
 *
 * @author      Christine Keim (CK), 980316
 ******************************************************************************
 */
public class DivElement extends Element
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DivElement.java,v 1.8 2007/07/23 08:17:32 kreimueller Exp $";


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
     * The elements
     */
    protected Vector<Element> elements;

    /**
     * Alignment
     * default : none (null)
     */
    public String alignment;


    /**************************************************************************
     * Creates a new instance of a DivElement
     */
    public DivElement ()
    {
        this.elements = null;
        this.alignment = null;
    } // DivElement


    /**************************************************************************
     * Adds an element to the division at the last position
     *
     * @param   elem    ?????
     */
    public void addElement (Element elem)
    {
        if (this.elements == null)
        {
            this.elements = new Vector<Element> (DivElement.ELEMENTS_INITIAL,
                DivElement.ELEMENTS_INCREMENT);
        } // if
        this.elements.addElement (elem);
    } // addElement


    /**************************************************************************
     * Adds an element to the division at the given position
     *
     * @param   elem    ?????
     * @param   order   ?????
     */
    public void addElement (Element elem, int order)
    {
        if (this.elements == null)
        {
            this.elements = new Vector<Element> (DivElement.ELEMENTS_INITIAL,
                DivElement.ELEMENTS_INCREMENT);
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
     * Adds an element to the group at the given position
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
        if (this.elements != null)      // there are some elements in the vector?
        {
            buf.append (IE302.TAG_DIVBEGIN);
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
            if (this.alignment != null)
            {
                buf.append (IE302.TA_ALIGN + this.inBrackets (this.alignment));
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
            buf.append (IE302.TAG_DIVEND);
        } // if there are some elements in the vector
    } // build

} // class DivElement
