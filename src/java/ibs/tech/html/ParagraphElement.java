/*
 Class: ParagraphElement.java
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
 * This is the ParagraphElement Object, which can hold more elements
 * at a time
 *
 * @version     $Id: ParagraphElement.java,v 1.8 2007/07/23 08:17:32 kreimueller Exp $
 *
 * @author      Christine Keim (CK), 980605
 ******************************************************************************
 */
public class ParagraphElement extends Element
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ParagraphElement.java,v 1.8 2007/07/23 08:17:32 kreimueller Exp $";


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
     * The groups
     */
    protected Vector<Element> group;

    /**
     * Alignment of the text in the paragraph
     * default : none (null)
     */
    public String alignment;

    /**
     * Is the Paragraph used as a container?
     * default : true
     */
    public boolean endtag;


    /**************************************************************************
     * Creates a new instance of a ParagraphElement
     */
    public ParagraphElement ()
    {
        this.alignment = null;
        this.endtag = true;
    } // ParagraphElement


    /**************************************************************************
     * Creates a new instance of a ParagraphElement with the givenalignment
     *
     * @param   al      ???
     */
    public ParagraphElement (String al)
    {
        this.alignment = al;
        this.endtag = true;
    } // ParagraphElement


    /**************************************************************************
     * Adds an element to the Paragraph at the last position
     *
     * @param   elem    ?????
     */
    public void addElement (Element elem)
    {
        if (this.group == null)
        {
            this.group = new Vector<Element> (
                ParagraphElement.ELEMENTS_INITIAL,
                ParagraphElement.ELEMENTS_INCREMENT);
        } // if
        this.group.addElement (elem);
    } // addElement


    /**************************************************************************
     * Adds an element to the group at the given position
     *
     * @param   elem    ?????
     * @param   order   ?????
     */
    public void addElement (Element elem, int order)
    {
        if (this.group == null)
        {
            this.group = new Vector<Element> (
                ParagraphElement.ELEMENTS_INITIAL,
                ParagraphElement.ELEMENTS_INCREMENT);
        } // if
        try
        {
            this.group.insertElementAt (elem, order);
        } // try
        catch (ArrayIndexOutOfBoundsException e)
        {
            this.group.setSize (order);
            this.group.insertElementAt (elem, order);
        } // catch
    } // addElement


    /**************************************************************************
     * Adds an element to the group at the given position
     */
    public void clear ()
    {
        this.group = null;
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
        if (!this.endtag)
        {
            buf.append (IE302.TAG_PBEGIN);
            if (this.alignment != null)
            {
                buf.append (IE302.TA_ALIGN + this.inBrackets (this.alignment));
            } // if
            buf.append (IE302.TO_TAGEND);
            return;
        } // if

        buf.append (IE302.TAG_PBEGIN);
        if (this.alignment != null)
        {
            buf.append (IE302.TA_ALIGN + this.inBrackets (this.alignment));
        } // if
        buf.append (IE302.TO_TAGEND);

        if (this.group != null)         // there are some elements in the vector?
        {
            for (Iterator<Element> iter = this.group.iterator (); iter.hasNext ();)
            {
                Element e = iter.next ();
                if (e != null)
                {
                    e.build (env, buf);
                } // if
            } // for iter
        } // if
        buf.append (IE302.TAG_PEND);
    } // build

} // class ParagraphElement
