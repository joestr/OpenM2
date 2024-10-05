/*
 * Class: GroupElement.java
 */

// package:
package ibs.tech.html;

// imports:
//KR TODO: unsauber
import ibs.io.Environment;
import ibs.tech.html.BuildException;
import ibs.tech.html.Element;

import java.util.Iterator;
import java.util.Vector;


/******************************************************************************
 * This is the DivElement Object, which can hold more elements at a time.
 *
 * @version     $Id: GroupElement.java,v 1.8 2007/07/23 08:17:32 kreimueller Exp $
 *
 * @author      Christine Keim (CK), 980316
 ******************************************************************************
 */
public class GroupElement extends Element
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: GroupElement.java,v 1.8 2007/07/23 08:17:32 kreimueller Exp $";


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
    public Vector<Element> group;



    /**************************************************************************
     * Creates a new instance of a GroupElement
     */
    public GroupElement ()
    {
        // nothing to do
    } // GroupsElement


    /**************************************************************************
     * Adds an element to the group at the last position
     *
     * @param   elem    ?????
     */
    public void addElement (Element elem)
    {
        if (this.group == null)
        {
            this.group = new Vector<Element> (GroupElement.ELEMENTS_INITIAL,
                GroupElement.ELEMENTS_INCREMENT);
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
            this.group = new Vector<Element> (GroupElement.ELEMENTS_INITIAL,
                GroupElement.ELEMENTS_INCREMENT);
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
        if (this.group != null)
        {
            this.group.removeAllElements ();
        } // if
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
        } // there are some elements in the vector
    } // build

} // class GroupElement
