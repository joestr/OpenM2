/*
 * Class: MenuTabTokens.java
 */

// package:
package ibs.obj.menu;

import ibs.bo.BOTokens;

// imports:


/******************************************************************************
 * Tokens for handling the menu tabs. <BR/>
 * This abstract class contains all tokens which are necessary to deal with
 * the objects delivered within this package. <BR/>
 *
 * @version     $Id: MenuTabTokens.java,v 1.7 2010/04/07 13:37:12 rburgermann Exp $
 *
 * @author Monika Eisenkolb (ME)
 ******************************************************************************
 */
public abstract class MenuTabTokens extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: MenuTabTokens.java,v 1.7 2010/04/07 13:37:12 rburgermann Exp $";

    /**
     * Name of bundle where the tokens included. <BR/>
     */
    public static String TOK_BUNDLE = BOTokens.TOK_BUNDLE;

    // text tokens
    /**
     * token for html file belonging to a special menutab
     */
    public static String ML_MENU_FILENAME = "ML_MENU_FILENAME";

    /**
     * token for m2 object belonging to a special menutab
     */
    public static String ML_ASSIGNED_TO_OBJECT = "ML_ASSIGNED_TO_OBJECT";

    /**
     * token telling where to insert the new menutab
     */
    public static String ML_MENU_TABSORT = "ML_MENU_TABSORT";

    /**
     * token for the front view of the menutab
     */
    public static String ML_MENU_FRONT = "ML_MENU_FRONT";

    /**
     * token for the back view of the menutab
     */
    public static String ML_MENU_BACK = "ML_MENU_BACK";

    /**
     * Token for levelStep. <BR/>
     */
    public static String ML_MENU_LEVELSTEP = "ML_MENU_LEVELSTEP";

    /**
     * Token for maximum levelStep. <BR/>
     */
    public static String ML_MENU_LEVELSTEPMAX = "ML_MENU_LEVELSTEPMAX";

    /**
     * text for an additional remark
     */
    public static String ML_ADDITIONALINFO = "ML_ADDITIONALINFO";

    /**
     * token for an additional remark
     */
    public static String ML_REMARK = "ML_REMARK";

} // class MenuTabTokens
