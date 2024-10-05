/*
 * Class: DiscElement.java
 */

// package:
package ibs.tech.html;

// imports:
//KR TODO: unsauber
import ibs.io.Environment;
import ibs.io.HtmlConstants;
import ibs.tech.html.BuildException;
import ibs.tech.html.Element;
import ibs.tech.html.Font;
import ibs.tech.html.GroupElement;
import ibs.tech.html.IE302;
import ibs.tech.html.ImageElement;
import ibs.tech.html.LinkElement;
import ibs.tech.html.RowElement;
import ibs.tech.html.TextElement;

import java.util.Iterator;
import java.util.Vector;


/******************************************************************************
 * This is the Menu List, which displays a Menu on the Browser
 *
 * @version     $Id: DiscElement.java,v 1.10 2007/07/24 21:27:02 kreimueller Exp $
 *
 * @author      Christine Keim (CK), 980316
 ******************************************************************************
 */
public class DiscElement extends Element
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DiscElement.java,v 1.10 2007/07/24 21:27:02 kreimueller Exp $";


    /**
     * Specifies the width of the table.
     * default : 100%
     */
    public String tablewidth;

    /**
     * Specifies the header of the table
     * default : null (none)
     */
    public RowElement header;

    /**
     * Specifies the width of the spaces.
     * default : 15
     */
    public String width;

    /**
     * Specifies the color of the row.
     * default : none (null)
     */
    public String bgcolor;

    /**
     * Specifies the font of the written text.
     */
    public Font font;

    /**
     * Specifies the number of hierarchies possible maximum.
     */
    public int maxlevels;

    /**
     * Specifies the number of hierarchie this element is in.
     */
    public int actlevel;

    /**
     * Argumentname, where the discId is put.
     */
    public String argName;

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
     * Specifies the menuid of the menu;
     */
    public String discid;

    /**
     * Specifies the Text to be displayed;
     * default : discid
     */
    public String text;

    /**
     * Specifies the other Texts to be displayed right of the discussion;
     * default : none (null)
     */
    public Element[] moreToShow;

    /**
     * Specifies the other Texts to be displayed left of the discussion;
     * default : none (null)
     */
    public Element[] moreToShowLeft;

    /**
     * menu opened?
     * default : no (false)
     */
    public boolean open;

    /**
     * url when clicking on image
     * default : get.asp
     */
    public String url2;

    /**
     * url when clicking on text
     * default : none (null)
     */
    public String url;

    /**
     * target of the link
     * default : null
     */
    public String target;

    /**
     * Icon when Menu is open
     * default : none (null)
     */
    public String openSrc;

    /**
     * Icon when Menu is close
     * default : none (null)
     */
    public String closeSrc;

    /**
     * Icon when no elements are in the discussion
     * default : none (null)
     */
    public String disabledSrc;

    /**
     * Holds the data (of subparts).
     *
     */
    protected Vector<DiscElement> items;

    /**
     * Icon to display near the name
     * default : none (null)
     */
    public String icon;

    /**
     * Sets the classIds of each column
     */
    public String[] classIds;

    /**
     * Sets the alignment of each column
     */
    public String[] alignments;

    /**
     * Standard width of disc element. <BR/>
     */
    private static final String STANDARD_WIDTH = "15";

    /**
     * Result of open operation: true. <BR/>
     */
    public static final String OP_TRUE = "true";
    /**
     * Result of open operation: false. <BR/>
     */
    public static final String OP_FALSE = "false";
    /**
     * Result of open operation: not found. <BR/>
     */
    public static final String OP_NOTFOUND = "not_found";



    /**************************************************************************
     * Creates a default Menu. <BR/>
     *
     * @param   id      ?????
     */
    public DiscElement (String id)
    {
        this.open = false;
        this.openSrc = null;
        this.closeSrc = null;
        this.discid = id;
        this.text = this.discid;
        this.moreToShow = null;
        this.url2 = null;
        this.url = null;
        this.target = null;
        this.maxlevels = -1;
        this.actlevel = -1;
        this.width = DiscElement.STANDARD_WIDTH;
        this.tablewidth = HtmlConstants.TAV_FULLWIDTH;
        this.argName = null;
        this.bgcolor = null;
        this.moreToShowLeft = null;
        this.header = null;
        this.disabledSrc = null;
        this.icon = null;
        this.alignments = null;
    } // DiscElement


    /**************************************************************************
     * Creates a default Discussion. <BR/>
     *
     * @param   ptext   ?????
     * @param   id      ?????
     */
    public DiscElement (String ptext, String id)
    {
        this.open = false;
        this.openSrc = null;
        this.closeSrc = null;
        this.discid = id;
        this.text = ptext;
        this.moreToShow = null;
        this.url2 = null;
        this.url = null;
        this.target = null;
        this.maxlevels = -1;
        this.actlevel = -1;
        this.width = DiscElement.STANDARD_WIDTH;
        this.tablewidth = HtmlConstants.TAV_FULLWIDTH;
        this.argName = null;
        this.bgcolor = null;
        this.moreToShowLeft = null;
        this.header = null;
        this.disabledSrc = null;
        this.icon = null;
        this.alignments = null;
    } // DiscElement


    /**************************************************************************
     * Clears the element. <BR/>
     * Sets the whole menu empty.
     */
    public void clear ()
    {
        this.items = null;
    } // clear


    /**************************************************************************
     * Adds a MenuItem at the actual depth
     *
     * @param   item    ?????
     * @param   order   ?????
     */
    public void addElement (DiscElement item, int order)
    {
        if (this.items == null)
        {
            this.items = new Vector<DiscElement> (DiscElement.ELEMENTS_INITIAL,
                DiscElement.ELEMENTS_INCREMENT);
        } // if
        try
        {
            this.items.insertElementAt (item, order);
        } // try
        catch (ArrayIndexOutOfBoundsException e)
        {
            this.items.setSize (order);
            this.items.insertElementAt (item, order);
        } // catch
    } // addElement


    /**************************************************************************
     * Adds a MenuItem at the actual depth
     *
     * @param   item    ?????
     */
    public void addElement (DiscElement item)
    {
        if (this.items == null)
        {
            this.items = new Vector<DiscElement> (DiscElement.ELEMENTS_INITIAL,
                DiscElement.ELEMENTS_INCREMENT);
        } // if
        this.items.addElement (item);
    } // addElement


    /**************************************************************************
     * Changes the status of the menuelement
     *
     * @param   pId     ?????
     *
     * @return  <CODE>true</CODE> if the status of this element was changed,
     *          <CODE>false</CODE> if the status of a sub element was changed
     *          or nothing was changed.
     */
    public boolean changeStatus (String pId)
    {
        if (this.discid.equalsIgnoreCase (pId))
        {
            this.open = !this.open;
            return true;
        } // if

        if (this.items != null)         // there are some elements in the vector?
        {
            for (Iterator<DiscElement> iter = this.items.iterator (); iter.hasNext ();)
            {
                DiscElement e = iter.next ();

                if (e != null)
                {
                    if (e.changeStatus (pId))
                    {
                        break;
                    } // if
                } // if
            } // for iter
        } // if there are some elements in the vector
        return false;
    } // changeStatus


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
            if (this.actlevel == 0)
            {
                this.buildFirstLevel (env, buf);
            } // if
            else
            {
                this.buildHigherLevel (env, buf);
            } // else

            if (this.open)
            {
                if (this.items != null) // there are some elements in the vector?
                {
                    for (Iterator<DiscElement> iter = this.items.iterator (); iter.hasNext ();)
                    {
                        DiscElement e = iter.next ();

                        if (e != null)
                        {
                            e.maxlevels = this.maxlevels;
                            e.actlevel = this.actlevel + 1;
                            e.build (env, buf);
                        } // if
                    } // for iter
                } // if there are some elements in the vector
            } // if open
            if (this.actlevel == 0)
            {
                buf.append (IE302.TAG_TABLEBODYEND);
                buf.append (IE302.MENU_END);
            } // if
        } // if browser is supported
    } // build


    /**************************************************************************
     * Build output for the element on the first level. <BR/>
     *
     * @param   env     OutputStream
     * @param   buf     Buffer where to write the output to.
     *
     * @exception   BuildException
     *              An error occurred during building the output.
     */
    public void buildFirstLevel (Environment env, StringBuffer buf) throws BuildException
    {
        buf.append (IE302.MENU_BEGIN);
        buf.append (IE302.TA_FRAMETYPE + this.inBrackets ("VOID"));
        buf.append (IE302.TA_WIDTH + this.inBrackets (this.tablewidth));
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

        int temp = 0;
        if ((this.moreToShow != null) || (this.moreToShowLeft != null))
        {
            if (this.moreToShowLeft != null)
            {
                /* HACK */
                if (this.moreToShowLeft.length == 1)
                {
                    buf.append (IE302.TAG_COLBEGIN);
                    if ((this.classIds != null) && (this.classIds[0] != null))
                    {
                        buf.append (IE302.TA_CLASSID + this.inBrackets (this.classIds[0]));
                    } // if
                    if ((this.alignments != null) && (this.alignments[0] != null))
                    {
                        buf.append (IE302.TA_ALIGN + this.inBrackets (this.alignments[0]));
                    } // if
                    buf.append (IE302.TO_TAGEND);
                    buf.append (IE302.TAG_COLBEGIN2);
                    if ((this.classIds != null) && (this.classIds[1] != null))
                    {
                        buf.append (IE302.TA_CLASSID + this.inBrackets (this.classIds[1]));
                    } // if
                    if ((this.alignments != null) && (this.alignments[1] != null))
                    {
                        buf.append (IE302.TA_ALIGN + this.inBrackets (this.alignments[1]));
                    } // if
                    buf.append (IE302.TO_TAGEND);
                    buf.append (IE302.TAG_COLEND);
                    temp = 1;
                } // if
                else
                {
                    for (int i = 0; i < this.moreToShowLeft.length - 1; i++)
                    {
                        buf.append (IE302.TAG_COLBEGIN);
                        if ((this.classIds != null) && (this.classIds[i] != null))
                        {
                            buf.append (IE302.TA_CLASSID + this.inBrackets (this.classIds[i]));
                        } // if
                        if ((this.alignments != null) && (this.alignments[i] != null))
                        {
                            buf.append (IE302.TA_ALIGN + this.inBrackets (this.alignments[i]));
                        } // if
                        buf.append (IE302.TO_TAGEND);
                        buf.append (IE302.TAG_COLEND);
                        temp = i;
                    } // for
                    buf.append (IE302.TAG_COLBEGIN);
                    ++temp;
                    if ((this.classIds != null) && (this.classIds[temp] != null))
                    {
                        buf.append (IE302.TA_CLASSID + this.inBrackets (this.classIds[temp]));
                    } // if
                    if ((this.alignments != null) && (this.alignments[temp] != null))
                    {
                        buf.append (IE302.TA_ALIGN + this.inBrackets (this.alignments[temp]));
                    } // if
                    buf.append (IE302.TO_TAGEND);
                    buf.append (IE302.TAG_COLEND);
                } // else
            } // if
            else
            {
                buf.append (IE302.TAG_COLBEGIN);
                if ((this.classIds != null) && (this.classIds[0] != null))
                {
                    buf.append (IE302.TA_CLASSID + this.inBrackets (this.classIds[0]));
                } // if
                if ((this.alignments != null) && (this.alignments[0] != null))
                {
                    buf.append (IE302.TA_ALIGN + this.inBrackets (this.alignments[0]));
                } // if
                buf.append (IE302.TO_TAGEND);
                buf.append (IE302.TAG_COLEND);
            } // else

            if (this.moreToShow != null)
            {
                for (int i = 0; i < this.moreToShow.length; i++)
                {
                    buf.append (IE302.TAG_COLBEGIN);
                    ++temp;
                    if ((this.classIds != null) && (this.classIds[temp] != null))
                    {
                        buf.append (IE302.TA_CLASSID + this.inBrackets (this.classIds[temp]));
                    } // if
                    if ((this.alignments != null) && (this.alignments[temp] != null))
                    {
                        buf.append (IE302.TA_ALIGN + this.inBrackets (this.alignments[temp]));
                    } // if
                    buf.append (IE302.TO_TAGEND);
                    buf.append (IE302.TAG_COLEND);
                } // for
            } // if

            buf.append (IE302.TAG_TABLEHEADBEGIN);
            if (this.header != null)
            {
                this.header.build (env, buf);
            } // if
            buf.append (IE302.TAG_TABLEHEADEND);
        } // if
        buf.append (IE302.TAG_TABLEBODYBEGIN);
    } // buildFirstLevel


    /**************************************************************************
     * Build output for the element on any higher level. <BR/>
     *
     * @param   env     OutputStream
     * @param   buf     Buffer where to write the output to.
     *
     * @exception   BuildException
     *              An error occurred during building the output.
     */
    public void buildHigherLevel (Environment env, StringBuffer buf) throws BuildException
    {
        buf.append (IE302.TAG_TABLEROWBEGIN);
        if (this.bgcolor != null)
        {
            buf.append (IE302.TA_BGCOLOR + this.inBrackets (this.bgcolor));
        } // if
        if (this.classId != null)
        {
            buf.append (IE302.TA_CLASSID + this.inBrackets (this.classId));
        } // if

        Element e;
        buf.append (IE302.TO_TAGEND);
        if (this.moreToShowLeft != null)
        {
            for (int i = 0; i < this.moreToShowLeft.length; i++)
            {
                buf.append (IE302.TAG_TABLECELLBEGIN);
                buf.append (IE302.TO_TAGEND);
                e = this.moreToShowLeft[i];
                e.build (env, buf);
                buf.append (IE302.TAG_TABLECELLEND);
            } // for
        } // if

        ImageElement temp = null;
        buf.append (IE302.TAG_TABLECELLBEGIN);
        buf.append (IE302.TA_VALIGN + this.inBrackets ("MIDDLE"));
        buf.append (IE302.TA_NOWRAP);
        buf.append (IE302.TO_TAGEND);

        GroupElement group1 = new GroupElement ();
        if (this.actlevel != 1)
        {
            for (int i = 0; i < this.actlevel - 1; i++)
            {
                temp = new ImageElement (this.disabledSrc);
                group1.addElement (temp);
            } // for
        } // if

        if (this.open)
        {
            if (this.openSrc != null)
            {
                temp = new ImageElement (this.openSrc);

                if (this.items != null)
                {
                    LinkElement le = new LinkElement (temp, this.url2 +
                        "&" + this.argName + "=" + this.discid);
                    group1.addElement (le);
                } // if
                else
                {
                    temp = new ImageElement (this.disabledSrc);
                    group1.addElement (temp);
                } // else
            } // if
        } // if open
        else
        {
            if (this.closeSrc != null)
            {
                temp = new ImageElement (this.closeSrc);
                if (this.items != null)
                {
                    LinkElement le = new LinkElement (temp, this.url2 +
                        "&" + this.argName + "=" + this.discid);
                    group1.addElement (le);
                } // if
                else
                {
                    temp = new ImageElement (this.disabledSrc);
                    group1.addElement (temp);
                } // else
            } // if
        } // else
        group1.build (env, buf);

        GroupElement group = new GroupElement ();
        if (this.icon != null)
        {
            group.addElement (new ImageElement (this.icon));
        } // if
        else
        {
            temp = new ImageElement (this.disabledSrc);
            group.addElement (temp);
        } // else

        TextElement menuItem = new TextElement (this.text, this.font);
        group.addElement (menuItem);

        if (this.url != null)
        {
            LinkElement le = new LinkElement (group, this.url);
            if (this.target != null)
            {
                le.target = this.target;
            } // if
            le.build (env, buf);
        } // if
        else
        {
            menuItem.build (env, buf);
        } // else

        buf.append (IE302.TAG_TABLECELLEND);

        if (this.moreToShow != null)
        {
            for (int i = 0; i < this.moreToShow.length; i++)
            {
                buf.append (IE302.TAG_TABLECELLBEGIN);
                buf.append (IE302.TA_NOWRAP);
//                        buf.append (IE302.TA_WIDTH + this.inBrackets (width2 + "%"));
                buf.append (IE302.TO_TAGEND);
                e = this.moreToShow[i];
                e.build (env, buf);
                buf.append (IE302.TAG_TABLECELLEND);
            } // for
        } // if
        buf.append (IE302.TAG_TABLEROWEND);
    } // buildHigherLevel


    /**************************************************************************
     * This method ... <BR/>
     *
     * @return  ???
     */
    public int getMaxLevel ()
    {
        int max = 0;
        int temp;

        if (this.items != null)
        {
            for (Iterator<DiscElement> iter = this.items.iterator (); iter.hasNext ();)
            {
                DiscElement e = iter.next ();

                if (e != null)
                {
                    temp = e.getMaxLevel ();
                    if (temp > max)
                    {
                        max = temp;
                    } // if
                } // if
            } // for iter
        } // if

        max++;
        return max;
    } // getMaxLevel


    /**************************************************************************
     * This method ... <BR/>
     *
     * @param   oid ???
     *
     * @return  ???
     */
    public String open (String oid)
    {
        if (oid.equalsIgnoreCase (this.discid))
        {
            if (this.open)
            {
                return DiscElement.OP_TRUE;
            } // if

            return DiscElement.OP_FALSE;
        } // if

        if (this.items != null)
        {
            for (Iterator<DiscElement> iter = this.items.iterator (); iter.hasNext ();)
            {
                DiscElement e = iter.next ();

                if (e != null)
                {
                    String temp = e.open (oid);
                    if (!temp.equalsIgnoreCase (DiscElement.OP_NOTFOUND))
                    {
                        return temp;
                    } // if
                } // if
            } // for iter
        } // if
        return DiscElement.OP_NOTFOUND;
    } // open

} // class DiscElement
