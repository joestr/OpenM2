/*
 * Class: SpanElement.java
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
 * This is the SpanElement, which displays a Span-tag in the browser
 *
 * @version     $Id: SpanElement.java,v 1.10 2008/09/17 16:01:06 kreimueller Exp $
 *
 * @author      Christine Keim (CK), 990118
 ******************************************************************************
 */
public class SpanElement extends Element
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: SpanElement.java,v 1.10 2008/09/17 16:01:06 kreimueller Exp $";

    /**
     * Specifies a CSS style.
     * default : none (null)
     */
    public String style;

    /**
     * Constant initialElements of the Vector holding the
     * elements.
     * set to 20
     */
    protected static final int ELEMENTS_INITIAL = 5;

    /**
     * Constant incrementElements of the Vector holding the
     * elements.
     * set to 5
     */
    protected static final int ELEMENTS_INCREMENT = 2;

    /**
     * Holds the data (of subparts).
     *
     */
    protected Vector<Element> items;


    /**************************************************************************
     * Creates a default SpanElement. <BR/>
     */
    public SpanElement ()
    {
        this.items = null;
    } // SpanElement


    /**************************************************************************
     * Clears the element. <BR/>
     * Sets the whole menu empty.
     */
    public void clear ()
    {
        this.items = null;
    } // clear


    /**************************************************************************
     * Adds a item to the spanElement.
     *
     * @param   pItem   Element to add.
     * @param   order   The order of the item.
     */
    public void addElement (Element pItem, int order)
    {
        if (this.items == null)
        {
            this.items = new Vector<Element> (SpanElement.ELEMENTS_INITIAL,
                SpanElement.ELEMENTS_INCREMENT);
        } // if
        try
        {
            this.items.insertElementAt (pItem, order);
        } // try
        catch (ArrayIndexOutOfBoundsException e)
        {
            this.items.setSize (order);
            this.items.insertElementAt (pItem, order);
        } // catch ArrayIndexOutOfBoundsException
    } // addElement


    /**************************************************************************
     * Adds a MenuItem at the actual depth
     *
     * @param   item    Element to add
     */
    public void addElement (Element item)
    {
        if (this.items == null)
        {
            this.items = new Vector<Element> (SpanElement.ELEMENTS_INITIAL,
                SpanElement.ELEMENTS_INCREMENT);
        } // if
        this.items.addElement (item);
    } // addElement


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
            buf.append (IE302.TAG_SPANBEGIN);
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
            if (this.style != null)
            {
                buf.append (IE302.TA_STYLE + this.inBrackets (this.style));
            } // if

            buf.append (IE302.TO_TAGEND);

            if (this.items != null)     // there are some elements in the vector
            {
                for (Iterator<Element> iter = this.items.iterator (); iter.hasNext ();)
                {
                    Element e = iter.next ();
                    if (e != null)
                    {
                        e.build (env, buf);
                    } // if
                } // for iter
            } // if there are some elements in the vector
            buf.append (IE302.TAG_SPANEND);
        } // isBrowserSupported
    } // build

} // class SpanElement
