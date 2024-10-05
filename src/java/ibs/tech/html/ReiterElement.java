/*
 * Class: ReiterElement.java
 */

// package:
package ibs.tech.html;

// imports:
//KR TODO: unsauber
import ibs.io.Environment;
import ibs.tech.html.BuildException;
import ibs.tech.html.Element;
import ibs.tech.html.Font;
import ibs.tech.html.IE302;
import ibs.tech.html.ReiterItemElement;

import java.util.Iterator;
import java.util.Vector;


/******************************************************************************
 * This is the Reiter List, which displays Reiter on the Browser. <BR/>
 *
 * @version     $Id: ReiterElement.java,v 1.9 2007/07/23 08:17:32 kreimueller Exp $
 *
 * @author      Christine Keim (CK), 980316
 ******************************************************************************
 */
public class ReiterElement extends Element
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ReiterElement.java,v 1.9 2007/07/23 08:17:32 kreimueller Exp $";


    /**
     * Specifies the font of the written text.
     */
    public Font font;

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
     * Holds the tab data.
     *
     */
    protected Vector<ReiterItemElement> reiter;

    /**
     * String for class id if active.
     *
     */
    public String active;

    /**
     * String for class id if inactive.
     *
     */
    public String inactive;


    /**************************************************************************
     * Creates a default tab list. <BR/>
     */
    public ReiterElement ()
    {
        this.reiter = null;
        this.font = null;
    } // ReiterElement


    /**************************************************************************
     * Clears the element. <BR/>
     * Sets the whole tab list empty.
     */
    public void clear ()
    {
        this.reiter = null;
    } // clear


    /**************************************************************************
     * Adds a ReiterItem at the actual depth
     *
     * @param   reiterItem  ?????
     * @param   order       ?????
     */
    public void addElement (ReiterItemElement reiterItem, int order)
    {
        reiterItem.reiterId = order;
        if (this.reiter == null)
        {
            this.reiter = new Vector<ReiterItemElement> (
                ReiterElement.ELEMENTS_INITIAL,
                ReiterElement.ELEMENTS_INCREMENT);
        } // if
        try
        {
            this.reiter.insertElementAt (reiterItem, order);
        } // try
        catch (ArrayIndexOutOfBoundsException e)
        {
            this.reiter.setSize (order);
            this.reiter.insertElementAt (reiterItem, order);
        } // catch
    } // addElement


    /**************************************************************************
     * Adds a ReiterItem at the actual depth
     *
     * @param   reiterItem  ?????
     */
    public void addElement (ReiterItemElement reiterItem)
    {
        if (this.reiter == null)
        {
            this.reiter = new Vector<ReiterItemElement> (
                ReiterElement.ELEMENTS_INITIAL,
                ReiterElement.ELEMENTS_INCREMENT);
        } // if

        this.reiter.addElement (reiterItem);
        int temp = this.reiter.indexOf (reiterItem);
        reiterItem.reiterId = temp;
    } // addElement


    /**************************************************************************
     * Sets a reiterItem active and all others inactive.
     *
     * @param   pId     ?????
     */
    public void changeStatus (int pId)
    {
        if (this.reiter != null)        // there are some elements in the vector?
        {
            for (Iterator<ReiterItemElement> iter = this.reiter.iterator (); iter.hasNext ();)
            {
                ReiterItemElement e = iter.next ();
                if (e != null)
                {
                    e.changeStatus (pId);
                } // if
            } // for iter
        } // if there are some elements in the vector
    } // changeStatus


    /**************************************************************************
     * Sets a reiterItem active and all others inactive. <BR/>
     * The item to be set active is identified by its text which must be equal
     * to the text parameter of this method.
     *
     * @param   pText       Text to compare the tab's text to.
     */
    public void changeStatus (String pText)
    {
        if (this.reiter != null)        // there are some elements in the vector?
        {
            for (Iterator<ReiterItemElement> iter = this.reiter.iterator (); iter.hasNext ();)
            {
                ReiterItemElement e = iter.next ();
                if (e != null)
                {
                    e.changeStatus (pText);
                } // if
            } // for iter
        } // if there are some elements in the vector
    } // changeStatus


    /**************************************************************************
     * Gets the ReiterItemElement with the given tabid.
     *
     * @param   pId     ?????
     *
     * @return  ???
     */
    public ReiterItemElement getTab (int pId)
    {
        ReiterItemElement element = null;
        if (this.reiter != null)        // there are some elements in the vector?
        {
            for (Iterator<ReiterItemElement> iter = this.reiter.iterator (); iter.hasNext ();)
            {
                ReiterItemElement e = iter.next ();
                if (e.reiterId == pId)
                {
                    return e;
                } // if
            } // for iter
        } // if there are some elements in the vector
        return element;
    } // getTab


    /**************************************************************************
     * Gets the ReiterItemElement with the given tab text.
     *
     * @param   pText       Text to compare the tab's text to.
     *
     * @return  The constructed ReiterItemElement.
     */
    public ReiterItemElement getTab (String pText)
    {
        if (this.reiter != null)        // there are some elements in the vector?
        {
            for (Iterator<ReiterItemElement> iter = this.reiter.iterator (); iter.hasNext ();)
            {
                ReiterItemElement e = iter.next ();
                if (e.text.equalsIgnoreCase (pText))
                {
                    return e;
                } // if
            } // for iter
        } // if there are some elements in the vector

        // no corresponding element found:
        return null;
    } // getTab


    /**************************************************************************
     * Gets the url of the reiterItemElement with the given tabid.
     *
     * @return  ???
     */
    public ReiterItemElement getActiveTab ()
    {
        if (this.reiter != null)        // there are some elements in the vector?
        {
            for (Iterator<ReiterItemElement> iter = this.reiter.iterator (); iter.hasNext ();)
            {
                ReiterItemElement e = iter.next ();
                if (e.active)
                {
                    return e;
                } // if
            } // for iter
        } // if there are some elements in the vector

        return null;
    } // ReiterItemElement


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
        if (this.isBrowserSupported (env))
                                        // browser is supported?
        {
            buf.append (IE302.MENU_BEGIN);
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
            buf.append (IE302.TO_TAGEND);

            if (this.reiter != null)    // there are some elements in the vector?
            {
                buf.append (IE302.TAG_TABLEROWBEGIN);
                buf.append (IE302.TO_TAGEND);
                for (Iterator<ReiterItemElement> iter = this.reiter.iterator (); iter.hasNext ();)
                {
                    ReiterItemElement e = iter.next ();
                    if (this.font != null)
                    {
                        e.font = this.font;
                    } // if
                    if (e.isVisible)
                    {
                        if ((e.actualRights & e.necessaryRights) == e.necessaryRights)
                        {
                            e.build (env, buf);
                        } // if
                    } // if
                } // for iter
                buf.append (IE302.TAG_TABLEROWEND);
            } // if there are some elements in the vector
            buf.append (IE302.MENU_END);
        } // if
    } // build

} // class ReiterElement
