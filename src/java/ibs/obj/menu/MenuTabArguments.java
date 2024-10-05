/*
 * Class: MenuTabArguments.java
 */

// package:
package ibs.obj.menu;

// imports:


/******************************************************************************
 * Arguments for handling the menutabs. <BR/>
 * This abstract class contains all arguments which are necessary to deal with
 * the classes delivered within this package. <P>
 *
 * @version     $Id: MenuTabArguments.java,v 1.5 2009/07/24 07:56:40 kreimueller Exp $
 *
 * @author      Monika Eisenkolb (ME)
 ******************************************************************************
 */
public abstract class MenuTabArguments extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: MenuTabArguments.java,v 1.5 2009/07/24 07:56:40 kreimueller Exp $";


    /**
     * argument for the filename. <BR/>
     */
    public static final String ARG_MENU_FILENAME   = "filename";

    /**
     * argument for the objectname. <BR/>
     */
    public static final String ARG_MENU_OBJECTNAME   = "objectname";

    /**
     * argument for the tabsort. <BR/>
     */
    public static final String ARG_MENU_TABSORT   = "tabsort";

    /**
     * argument for the front view of the tab. <BR/>
     */
    public static final String ARG_MENU_FRONT   = "front";

    /**
     * argument for the back view of the tab. <BR/>
     */
    public static final String ARG_MENU_BACK   = "back";

    /**
     * Argument for the levelStep of the tab. <BR/>
     */
    public static final String ARG_MENU_LEVELSTEP = "lstep";

    /**
     * Argument for the maximum levelStep of the tab. <BR/>
     */
    public static final String ARG_MENU_LEVELSTEPMAX = "lstepm";

    /**
     * argument for the oid of the chosen object. <BR/>
     */
    public static final String ARG_MENU_OBJECTOID   = "objectoid";

    /**
     * argumentname for the linked object. <BR/>
     */
    public static final String ARG_LINKED_TO = "Linked";

    /**
     * oid extension: indicates the oid field. <BR/>
     */
    public static final String ARG_OID_EXTENSION  = "_OID";

    /**
     * Argument name for remark. <BR/>
     */
    public static final String ARG_REMARK  = "REM";

} // class MenuTabArguments
