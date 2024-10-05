/*
 * Class: MenuRootElement.java
 */

// package:
package ibs.tech.html;

// imports:
//KR TODO: unsauber
import ibs.io.Environment;
import ibs.tech.html.BuildException;
import ibs.tech.html.ScriptElement;

import java.util.Iterator;
import java.util.Vector;


/******************************************************************************
 * This is the Menu List, which displays a Menu on the Browser
 *
 * @version     $Id: MenuRootElement.java,v 1.6 2007/07/24 21:27:02 kreimueller Exp $
 *
 * @author      Christine Keim (CK), 980316
 ******************************************************************************
 */
public class MenuRootElement extends MenuElement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: MenuRootElement.java,v 1.6 2007/07/24 21:27:02 kreimueller Exp $";


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
     * Maximum hierarchy levels.
     */
    public int maxlevels = 0;

    /**
     * Starting level within hierarchy. <BR/>
     * Default: <CODE>1</CODE>
     */
    public int p_startLevel = 1;

    /**
     * The script generated thus far. <BR/>
     * This property is used to generate the script for the client. For each
     * MenuRootElement there is a bit of script code added to this script.
     */
    public ScriptElement script = null;

    /**
     * Id of root object in menu tree. <BR/>
     * The root object is the object below which the actual tree shall be added.
     * If <CODE>null</CODE> this is the whole tree.
     * Default: <CODE>null</CODE>.
     */
    public String p_rootObjId = null;

    /**
     * Menu mode: Nothing to do. <BR/>
     */
    public static final int MMODE_NONE = 0;

    /**
     * Menu mode: Create/Add nodes. <BR/>
     * This is the default mode.
     */
    public static final int MMODE_CREATE = 1;

    /**
     * Menu mode: Synchronize nodes. <BR/>
     */
    public static final int MMODE_SYNC = 2;

    /**
     * Mode of menu bar. <BR/>
     * Can be one of {@link #MMODE_CREATE} or {@link #MMODE_SYNC}. <BR/>
     * Default: {@link #MMODE_CREATE}
     */
    public int p_mode = MenuRootElement.MMODE_CREATE;

    /**
     * Duration for getting the data for the menu out of the database. <BR/>
     */
    public long p_durationDatabaseQuery = 0;

    /**
     * Duration for creating the java menu structure. <BR/>
     */
    public long p_durationStructureCreation = 0;

    /**
     * End javascript duration measurement. <BR/>
     */
    private static final String JS_ENDDURATION =
        "top.v_durations.end (durationId);\n";


    /**************************************************************************
     * Creates a default Menu. <BR/>
     *
     * @param   id      ?????
     */
    public MenuRootElement (String id)
    {
        super (id);

        this.icon = null;
        this.id = id;
        this.text = id;
    } // MenuRootElement


    /**************************************************************************
     * Creates a default MenuRootElement. <BR/>
     *
     * @param   ptext   ?????
     * @param   id      ?????
     */
    public MenuRootElement (String ptext, String id)
    {
        super (ptext, id);

        this.icon = null;
        this.id = id;
        this.text = ptext;
    } // MenuRootElement


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
     * @param   item    Element to be added.
     * @param   order   Position at which to set the element.
     */
    public void addElement (MenuElement item, int order)
    {
        if (this.items == null)
        {
            this.items = new Vector<MenuElement> (
                MenuRootElement.ELEMENTS_INITIAL,
                MenuRootElement.ELEMENTS_INCREMENT);
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
     * @param   item    Element to be added.
     */
    public void addElement (MenuElement item)
    {
        if (this.items == null)
        {
            this.items = new Vector<MenuElement> (
                MenuRootElement.ELEMENTS_INITIAL,
                MenuRootElement.ELEMENTS_INCREMENT);
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
        long start = 0;                 // start time of measured duration
        long end = 0;                   // end time of measured duration

        start = System.currentTimeMillis ();

        this.script = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);
        // create the basic script code:
        this.script.addScript (
            IE302.TAG_COMMENTBEGIN + "\n" +
            "top.v_durations.set (\"getting data out of database\", " +
                this.p_durationDatabaseQuery + ");" + "\n" +
            "top.v_durations.set (\"generating java menu structure\", " +
                this.p_durationStructureCreation + ");" + "\n" +
            // declare the local variable:
            "var v_menuBar = null;\n" +

            // create function for registering a node of the menu bar:
            "function a (k,n,p,c,l,i)\n" +
            "{");

        // handle menu mode:
        switch (this.p_mode)
        {
            case MenuRootElement.MMODE_CREATE:
                this.script
                    .addScript ("top.scripts.hier_createNodeAtLevel (v_menuBar,l,k,n,p,c,l,i,true);");
                break;

            case MenuRootElement.MMODE_SYNC:
                this.script
                    .addScript ("top.scripts.hier_syncNode (v_menuBar,l,k,n,p,c,l,i,true);");
                break;

            default: // nothing to do
        } // switch (this.p_mode)

        this.script.addScript (
            "} // a\n" +

            // create function for loading the menu bars:
            "function loadMenuBar ()\n" +
            "{\n" +
            "var durationId = top.v_durations.start (\"displaying menu bar\");" + "\n" +
            "var navBarTab = top.scripts.actNavBar.get ('" + this.id + "');" + "\n" +
            // get actual tree:
            "v_menuBar = navBarTab.menuBar;");

        // check if we are bulding a complete or a partial tree:
        if (this.p_rootObjId != null)
        {
            // ensure that the tree is positioned at the root element:
            this.script.addScript (
                "var rootNode = v_menuBar.get ('" + this.p_rootObjId + "');" + "\n" +
                "v_menuBar.setActNode (rootNode);");
        } // if

        // check if we are synchronizing the tree:
        if (this.p_mode == MenuRootElement.MMODE_SYNC)
        {
            // start tree synchronization:
            this.script.addScript (
                "v_menuBar.startSynchronization ();");
        } // if

        this.script.addScript (
            MenuRootElement.JS_ENDDURATION +
            "durationId = top.v_durations.start (\"building menu bar JavaScript structure\");");

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
                    e.actlevel = this.p_startLevel;
                    e.script = this.script;
                    e.build (env, buf);
                } // if
            } // for iter
        } // if there are some elements in the vector

        // check if we are bulding a complete or a partial tree:
        if (this.p_rootObjId != null)
        {
            this.script.addScript (
                "rootNode.show ();");
        } // if

        // create final script code:
        this.script.addScript (
            // compute duration for getting data from server:
            MenuRootElement.JS_ENDDURATION +
            "v_menuBar.loadingFinished ();\n" +
            "} // loadMenuBar\n" + // loadMenuBar

            // place the call to load the menu bar within the call queue:
            "top.tryCall (top.getFrameName (window) + '.loadMenuBar ()');\n" +
            IE302.JS_COMMENT + IE302.TAG_COMMENTEND + "\n");

        // build the script code:
        this.script.build (env, buf);

        end = System.currentTimeMillis ();

        ScriptElement script = new ScriptElement (ScriptElement.LANG_JAVASCRIPT, ScriptElement.TYPE_JAVASCRIPT);
        script.addScript (
            IE302.TAG_COMMENTBEGIN + "\n" +
            "top.v_durations.set (\"generating javascript\", " +
                (end - start) + ");" + "\n" +
            IE302.JS_COMMENT + "alert (top.v_durations.toString ());\n" +
            IE302.JS_COMMENT + IE302.TAG_COMMENTEND + "\n");
        // build the script code:
        script.build (env, buf);
    } // build

} // class MenuRootElement
