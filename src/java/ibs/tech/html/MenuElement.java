/*
 * Class: MenuElement.java
 */

// package:
package ibs.tech.html;

// imports:
//KR TODO: unsauber
import ibs.io.Environment;
import ibs.tech.html.BuildException;
import ibs.tech.html.Element;
import ibs.tech.html.ScriptElement;

import java.util.Iterator;
import java.util.Vector;


/******************************************************************************
 * This is the Menu List, which displays a Menu on the Browser
 *
 * @version     $Id: MenuElement.java,v 1.14 2007/07/31 19:13:59 kreimueller Exp $
 *
 * @author      Christine Keim (CK), 980316
 ******************************************************************************
 */
public class MenuElement extends Element
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: MenuElement.java,v 1.14 2007/07/31 19:13:59 kreimueller Exp $";


    /**
     * Specifies the number of hierarchie this element is in.
     */
    public int actlevel;

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
     * Specifies the Text to be displayed;
     */
    public String text = "";

    /**
     * Holds the data (of subparts).
     *
     */
    protected Vector<MenuElement> items = null;

    /**
     * Icon to display near the name
     * default : none (null)
     */
    public String icon = null;

    /**
     * Does the menu element have subnodes within the tree?. <BR/>
     * Default: <CODE>false</CODE>.
     */
    public boolean p_hasSubNodes = false;

    /**
     * The script generated thus far. <BR/>
     * This property is used to generate the script for the client. For each
     * MenuElement there is a bit of script code added to this script.
     */
    public ScriptElement script = null;

    /**
     * Id of upper object. <BR/>
     * This value is used for inserting the node at the correct position in
     * the tree.
     */
    public String p_upperId = null;

    /**
     * The key of the menu element. <BR/>
     * Default: set to the same value as id.
     */
    public Object p_key = null;


    /**************************************************************************
     * Creates a default Menu. <BR/>
     *
     * @param   id      ?????
     */
    public MenuElement (String id)
    {
        this.icon = null;
        this.text = id;
        this.actlevel = -1;
        this.p_key = id;
    } // MenuElement


    /**************************************************************************
     * Creates a default MenuElement. <BR/>
     *
     * @param   ptext   ?????
     * @param   id      ?????
     */
    public MenuElement (String ptext, String id)
    {
        this.icon = null;
        this.text = ptext;
        this.actlevel = -1;
        this.p_key = id;
    } // MenuElement


    /**************************************************************************
     * Creates a default MenuElement. <BR/>
     *
     * @param   ptext   ?????
     * @param   idBuf   ?????
     */
    public MenuElement (String ptext, StringBuffer idBuf)
    {
        this.icon = null;
        this.text = ptext;
        this.actlevel = -1;
        this.p_key = idBuf;
    } // MenuElement


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
    public void addElement (MenuElement item, int order)
    {
        if (this.items == null)
        {
            this.items = new Vector<MenuElement> (MenuElement.ELEMENTS_INITIAL,
                MenuElement.ELEMENTS_INCREMENT);
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
    public void addElement (MenuElement item)
    {
        if (this.items == null)
        {
            this.items = new Vector<MenuElement> (MenuElement.ELEMENTS_INITIAL,
                MenuElement.ELEMENTS_INCREMENT);
        } // if
        this.items.addElement (item);
    } // addElement


    /**************************************************************************
     * Writes the element on the browser. <BR/>
     *
     * @param   env         OutputStream
     * @param   buf     Buffer where to write the output to.
     *
     * @exception   BuildException
     *              An error occurred during building the output.
     */
    public void build (Environment env, StringBuffer buf) throws BuildException
    {
        this.script.addScript (
            new StringBuffer ("a ('")
                .append (this.id).append ("','")
                .append (this.text).append ("','")
                .append (this.p_upperId).append ("',")
                .append (this.p_hasSubNodes).append (",")
                .append (this.actlevel).append (",'")
                .append (this.icon).append ("');"));

        if (this.items != null)     // there are some elements in the
                                    // vector?
        {
            // run through all sub elements and build the code for each of
            // them:
            for (Iterator<MenuElement> iter = this.items.iterator (); iter.hasNext ();)
            {
                MenuElement e = iter.next ();
                if (e != null)
                {
                    e.actlevel = this.actlevel + 1;
                    e.script = this.script;
                    e.build (env, buf);
                } // if
            } // for iter
        } // if there are some elements in the vector
    } // build


    /**************************************************************************
     * ???. <BR/>
     *
     * @return  ???
     */
    public int getMaxLevel ()
    {
        int max = 0;
        int temp;
        if (this.items != null)
        {
            for (Iterator<MenuElement> iter = this.items.iterator (); iter.hasNext ();)
            {
                MenuElement e = iter.next ();
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

} // class MenuElement
