/*
 * Class: ReiterItemElement.java
 */

// package:
package ibs.tech.html;

// imports:
//KR TODO: unsauber
import ibs.bo.OID;
//KR TODO: unsauber
import ibs.io.Environment;
import ibs.tech.html.BuildException;
import ibs.tech.html.Element;
import ibs.tech.html.Font;
import ibs.tech.html.IE302;
import ibs.tech.html.LinkElement;
import ibs.tech.html.TextElement;

import java.util.Iterator;
import java.util.Vector;


/******************************************************************************
 * This is the ???????????, which displays a ?????????? on the Browser. <BR/>
 *
 * @version     $Id: ReiterItemElement.java,v 1.9 2007/07/23 08:17:32 kreimueller Exp $
 *
 * @author      Christine Keim (CK), 980328
 ******************************************************************************
 */
public class ReiterItemElement extends Element
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ReiterItemElement.java,v 1.9 2007/07/23 08:17:32 kreimueller Exp $";


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
     * Specifies the font of the written text.
     * default: none (null)
     */
    public Font font;

    /**
     * url of application
     * default: actual base of the url
     */
    public String url;

    /**
     * target
     */
    public String target;

    /**
     * urls to open when tab is activated
     * default: none (null)
     */
    protected Vector<String[]> urls;

    /**
     * text of the ReiterItem
     */
    public String text;

    /**
     * Needed to activate/deactivate tab
     */
    public int reiterId;

    /**
     * Class id used if the tab item is active. <BR/>
     */
    public String activeClassId = null;

    /**
     * Class id used if the tab item is inactive. <BR/>
     */
    public String inactiveClassId = null;

    /**
     * Argument name, where the reiterId is put.
     */
    public String argName;

    /**
     * Is the tab active? (which means, not clickable)
     */
    public boolean active;

    /**
     * image width
     */
    public String imageWidth;

    /**
     * image height
     */
    public String imageHeight;

    /**
     * Image to display when active (on background)
     */
    public String activeImage;

    /**
     * Image to display when inactive (on background)
     */
    public String inactiveImage;

    /**
     * Actual Rights of the user
     *
     */
    public int actualRights;

    /**
     * required rights to display the tab
     *
     */
    public int necessaryRights;

    /**
     * Is Tab visible?
     *
     */
    public boolean isVisible;

    /**
     * Oid of the Tab
     *
     */
    public OID oid;


    /**************************************************************************
     * Is id member of the given array?. <BR/>
     *
     * @param   id      The id to search for.
     * @param   ids     The array in which to search.
     *
     * @return  <CODE>true</CODE> if the id is part of the array,
     *          <CODE>false</CODE> otherwise.
     */
    protected boolean member (int id, int[] ids)
    {
        if (ids == null)
        {
            return false;
        } // if
        for (int i = 0; i < ids.length; i++)
        {
            if (id == ids[i])
            {
                return true;
            } // if
        } // for i
        return false;
    } // member


    /**************************************************************************
     * Is text member of the given array?. <BR/>
     *
     * @param   text    The text to search for.
     * @param   texts   The array in which to search.
     *
     * @return  <CODE>true</CODE> if the text is part of the array,
     *          <CODE>false</CODE> otherwise.
     */
    protected boolean member (String text, String[] texts)
    {
        if ((texts == null) || (text == null))
        {
            return false;
        } // if
        for (int i = 0; i < texts.length; i++)
        {
            if (text.equalsIgnoreCase (texts[i]))
            {
                return true;
            } // if
        } // for i
        return false;
    } // member


    /**************************************************************************
     * Creates a default ReiterItem. <BR/>
     *
     * @param   argname     The name of the argument.
     * @param   activeImg   Image for the active tab..
     * @param   inactiveImg Image for the inactive tab.
     */
    public ReiterItemElement (String argname, String activeImg,
        String inactiveImg)
    {
        this.font = null;
        this.argName = argname;
        this.reiterId = -1;
        this.text = null;
        this.active = false;
        this.imageWidth = null;
        this.imageHeight = null;
        this.activeImage = activeImg;
        this.inactiveImage = inactiveImg;
        this.urls = null;
        this.url = null;
        this.target = null;
        this.actualRights = -1;
        this.necessaryRights = -1;
        this.isVisible = true;
    } // ReiterItemElement


    /**************************************************************************
     * Creates a default ReiterItem. <BR/>
     *
     * @param   argname     The argument name.
     */
    public ReiterItemElement (String argname)
    {
        this.font = null;
        this.argName = argname;
        this.reiterId = -1;
        this.text = null;
        this.active = false;
        this.imageWidth = null;
        this.imageHeight = null;
        this.activeImage = null;
        this.inactiveImage = null;
        this.urls = null;
        this.url = null;
        this.target = null;
        this.actualRights = -1;
        this.necessaryRights = -1;
        this.isVisible = true;
    } // ReiterItemElement


    /**************************************************************************
     * Set the state of this item to active. <BR/>
     */
    public void setActive ()
    {
        this.active = true;
        this.classId = this.activeClassId;
    } // setActive


    /**************************************************************************
     * Set the state of this item to inactive. <BR/>
     */
    public void setInactive ()
    {
        this.active = false;
        this.classId = this.inactiveClassId;
    } // setInactive


    /**************************************************************************
     * dummy, does nothing
     *
     * @param   pId     ?????
     */
    public void changeStatus (int pId)
    {
        if (this.reiterId == pId)
        {
            this.setActive ();
        } // if
        else
        {
            this.setInactive ();
        } // else
    } // changeStatus


    /**************************************************************************
     * Changes the state of this tab to active if the provided text equals to
     * the text of the tab, to inactive otherwise.
     *
     * @param   pText       Text to compare the tab's text to.
     */
    public void changeStatus (String pText)
    {
        if (this.text.equalsIgnoreCase (pText))
        {
            this.setActive ();
        } // if
        else
        {
            this.setInactive ();
        } // else
    } // changeStatus


    /**************************************************************************
     * Changes the state of this tab to visible if the id is member of
     * the provided id-array of the tab, to invisible otherwise.
     *
     * @param   ids     List of tabs which shall be set to visible.
     */
    public void setVisible (int[] ids)
    {
        if (this.member (this.reiterId, ids))
        {
            this.isVisible = true;
        } // if
        else
        {
            this.isVisible = false;
        } // else
    } // setVisible


    /**************************************************************************
     * Changes the state of this tab to visible if the text equals to
     * the provided text-array of the tab, to invisible otherwise.
     *
     * @param   pText       Text to compare the tab's text to.
     */
    public void setVisible (String[] pText)
    {
        if (this.member (this.text, pText))
        {
            this.isVisible = true;
        } // if
        else
        {
            this.isVisible = false;
        } // else
    } // setVisible


    /**************************************************************************
     * Adds a URL to call when Reiter is activated
     *
     * @param   urlToAdd    ???
     */
    public void addUrl (String urlToAdd)
    {
        if (this.urls == null)
        {
            this.urls = new Vector<String[]> (
                ReiterItemElement.ELEMENTS_INITIAL,
                ReiterItemElement.ELEMENTS_INCREMENT);
        } // if
        String[] temp = new String[2];
        temp[0] = urlToAdd;
        temp[1] = null;
        this.urls.addElement (temp);
    } // addUrl


    /**************************************************************************
     * Adds a URL to call when Reiter is activated (with target)
     *
     * @param   urlToAdd    ???
     * @param   target      ???
     */
    public void addUrl (String urlToAdd, String target)
    {
        if (this.urls == null)
        {
            this.urls = new Vector<String[]> (
                ReiterItemElement.ELEMENTS_INITIAL,
                ReiterItemElement.ELEMENTS_INCREMENT);
        } // if
        String[] temp = new String[2];
        temp[0] = urlToAdd;
        temp[1] = target;
        this.urls.addElement (temp);
    } // addUrl


    /**************************************************************************
     * Get the final URL constructed of all URLs which shall be called from
     * this tab. <BR/>
     *
     * @return  The final URL.
     */
    public String getFinalUrl ()
    {
        String finalUrl = "";
        if (this.urls != null)               // there are some elements in the
                                        // vector?
        {
            for (Iterator<String[]> iter = this.urls.iterator (); iter.hasNext ();)
            {
                String[] e = iter.next ();
                if (e != null)
                {
                    finalUrl += IE302.JS_OPENBEGIN;
                    if (e[1] != null)
                    {
                        finalUrl += e[0];
                        finalUrl += IE302.JS_OPENBETWEEN;
                        finalUrl += e[1];
                    } // if
                    else
                    {
                        finalUrl += e[0];
                    } // else
                    finalUrl += IE302.JS_OPENEND;
                } // if
            } // for iter
        } // if there are some elements in the vector

        return finalUrl;                // return the constructed url
    } // getFinalUrl


    /******************************************************************************
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
            buf.append (IE302.TAG_TABLECELLBEGIN);
            buf.append (IE302.TA_VALIGN + this.inBrackets ("TOP"));
            buf.append (IE302.TA_ALIGN + this.inBrackets ("CENTER"));
            if (this.imageWidth != null)
            {
                buf.append (IE302.TA_WIDTH + this.inBrackets (this.imageWidth));
            } // if
            if (this.imageHeight != null)
            {
                buf.append (IE302.TA_HEIGHT + this.inBrackets (this.imageHeight));
            } // if
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
            if (this.active)
            {
                if (this.activeImage != null)
                {
                    buf.append (IE302.TA_BGIMAGE + this.inBrackets (this.activeImage));
                } // if
            } // if
            else
            {
                if (this.inactiveImage != null)
                {
                    buf.append (IE302.TA_BGIMAGE + this.inBrackets (this.inactiveImage));
                } // if
            } // else
            buf.append (IE302.TO_TAGEND);
            TextElement temp;
            if (this.text != null)
            {
                temp = new TextElement (this.text, this.font);
            } // if
            else
            {
                temp = new TextElement ("" + this.reiterId, this.font);
            } // else
            if (this.active)
            {
                buf.append (IE302.TAG_FONTBEGIN + " SIZE=-1>" + IE302.HCH_NBSP +
                    IE302.TAG_NEWLINE + IE302.TAG_FONTEND);
                temp.build (env, buf);
            } // if
            else
            {
                buf.append (IE302.TAG_FONTBEGIN + " SIZE=-3>" + IE302.HCH_NBSP +
                    IE302.TAG_NEWLINE + IE302.TAG_FONTEND);
                String finalUrl = IE302.JS_BEGIN +
                    IE302.JS_OPENBEGIN +
                    this.url + "&" + this.argName + "=" + this.reiterId +
                    IE302.JS_OPENBETWEEN +
                    this.target +
                    IE302.JS_OPENEND +
                    this.getFinalUrl ();

                LinkElement temp2 = new LinkElement (temp, finalUrl);
                temp2.build (env, buf);
            } // else
            buf.append (IE302.TAG_TABLECELLEND);
        } // if
    } // build

} // class ReiterItemElement
