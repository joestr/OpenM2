/*
 * Class: AppConstants.java
 */

// package:
package ibs.app;

// imports:
import ibs.util.UtilConstants;


/******************************************************************************
 * Constants for ibs applications. <BR/>
 * This abstract class contains all constants which are necessary to deal with
 * the classes delivered within this package. <P>
 * Contains system defined object types with IDs from 0x0011 to 0x00FF. <BR/>
 * Format of object types: <B>ddsstttv</B>. <BR/>
 * <UL>
 * <LI><B>dd</B> ... domain id (01 .. FF)
 * <LI><B>ss</B> ... server id (01 .. FF)
 * <LI><B>ttt</B> .. type id (001 .. FFF)
 * <LI><B>v</B> .... version id (0 .. F)
 * </UL>. <BR/>
 * <B>ttt</B> has the following ranges depending on its use: <BR/>
 * <UL>
 * <LI>001 .. 00F: system defined object types
 * <LI>010 .. 0FF: base object types
 * <LI>110 .. FFF: derived object types.
 * </UL><P>
 *
 * @version     $Id: AppConstants.java,v 1.43 2010/04/20 09:05:01 btatzmann Exp $
 *
 * @author      Klaus Reimüller (KR), 980204
 ******************************************************************************
 */
public abstract class AppConstants extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: AppConstants.java,v 1.43 2010/04/20 09:05:01 btatzmann Exp $";


    /**
     * Copy type: CUT. <BR/>
     */
    public static final short CT_CUT = 1;

    /**
     * Copy type: COPY. <BR/>
     */
    public static final short CT_COPY = 2;


    /**
     * Name of common font. <BR/>
     */
    public static final String FONT = "Arial, Tahoma, Verdana, sans-serif";

    /**
     * Name of font for names. <BR/>
     */
    public static final String FONT_NAME = AppConstants.FONT;

    /**
     * Name of font for values. <BR/>
     */
    public static final String FONT_VALUE = AppConstants.FONT;

    /**
     * Name of font for captions. <BR/>
     */
    public static final String FONT_CAPTION = AppConstants.FONT;

    /**
     * Name of font for tab bars. <BR/>
     */
    public static final String FONT_TABBAR = AppConstants.FONT;

    /**
     * Name of font for  number of list elements. <BR/>
     */
    public static final String FONT_ELEMENTS = AppConstants.FONT;

    /**
     * Name of font for list headers. <BR/>
     */
    public static final String FONT_HEADER = AppConstants.FONT;

    /**
     * Common font size. <BR/>
     */
    public static final int FONTSIZE = 2;

    /**
     * Font size used for names. <BR/>
     */
    public static final int FONTSIZE_NAME = AppConstants.FONTSIZE;

    /**
     * Font size used for values. <BR/>
     */
    public static final int FONTSIZE_VALUE = AppConstants.FONTSIZE;

    /**
     * Font size used for captions. <BR/>
     */
    public static final int FONTSIZE_CAPTION = AppConstants.FONTSIZE + 1;

    /**
     * Font size used for tab bars. <BR/>
     */
    public static final int FONTSIZE_TABBAR = AppConstants.FONTSIZE;

    /**
     * Font size used for number of list elements. <BR/>
     */
    public static final int FONTSIZE_ELEMENTS = AppConstants.FONTSIZE_CAPTION - 1;

    /**
     * Font size used for list header. <BR/>
     */
    public static final int FONTSIZE_HEADER = AppConstants.FONTSIZE;


    /**
     * Form field extension for path info in DT_FILE types. <BR/>
     */
    public static final String DT_FILE_PATH_EXT = new String ("_path");

    /**
     * Form field extension for web path info in DT_FILE types. <BR/>
     */
    public static final String DT_WWW_PATH_EXT = new String ("_wwwp");

    /**
     * The default user name. <BR/>
     */
    public static final String DEFAULT_USERNAME = "";

    /**
     * The password corresponding to the
     * <A HREF="#DEFAULT_USERNAME">DEFAULT_USERNAME</A>.
     */
    public static final String DEFAULT_PASSWORD = "";

    /**
     * The number of elements stored within the cache. <BR/>
     */
    public static final int CACHE_SIZE = 5;

    /**
     * Style sheet file for button bar. <BR/>
     */
    public static final String CSS_BUTTONS = "styleButtonBar.css";

    /**
     * Style sheet file for tab bar. <BR/>
     */
    public static final String CSS_TABS = "styleTabBar.css";

    /**
     * Style sheet file for sheet. <BR/>
     */
    public static final String CSS_SHEET = "styleSheet.css";

    /**
     * Style sheet file for list. <BR/>
     */
    public static final String CSS_LIST = "styleList.css";

    /**
     * Style sheet file for menu bar. <BR/>
     */
    public static final String CSS_MENU = "styleMenuBar.css";

    /**
     * Style sheet file for nav bar. <BR/>
     */
    public static final String CSS_NAVTABS = "styleNavBar.css";

    /**
     * Style sheet file for printing sheet. <BR/>
     */
    public static final String CSS_SHEETPRINT = "stylePrintSheet.css";

    /**
     * Style sheet file for header. <BR/>
     */
    public static final String CSS_HEADER = "styleHeader.css";

    /**
     * Style sheet file for footer. <BR/>
     */
    public static final String CSS_FOOTER = "styleFooter.css";

    /**
     * Style sheet file for order. <BR/>
     */
    public static final String CSS_SHEETORDER = "styleOrder.css";

    /**
     * Style sheet file for collection. <BR/>
     */
    public static final String CSS_SHEETCOLLECTION = "styleCollection.css";

    /**
     * Style sheet file for order. <BR/>
     */
    public static final String CSS_PRODUCTCATALOG = "styleCatalog.css";

    /**
     * Style sheet file for calendar. <BR/>
     */
    public static final String CSS_CALENDAR = "styleCalendar.css";

    /**
     * Style sheet file for message. <BR/>
     */
    public static final String CSS_MESSAGE = "styleMessage.css";

    /**
     * Style sheet file for references. <BR/>
     */
    public static final String CSS_REFERENCES = "styleRefs.css";

    /**
     * javascript-file for button bar. <BR/>
     */
    public static final String JS_BUTTONS = "scriptButtonBar.js";

    /**
     * javascript-file for tab bar. <BR/>
     */
    public static final String JS_TABS = "scriptTabBar.js";

    /**
     * javascript-file for sheet. <BR/>
     */
    public static final String JS_SHEET = "scriptSheet.js";

    /**
     * javascript-file for list. <BR/>
     */
    public static final String JS_LIST = "scriptList.js";

    /**
     * javascript-file for menubar. <BR/>
     */
    public static final String JS_MENU = "scriptMenuBar.js";

    /**
     * javascript-file for nav tab bar. <BR/>
     */
    public static final String JS_NAVTABS = "scriptNavBar.js";

    /**
     * javascript-file for header. <BR/>
     */
    public static final String JS_HEADER = "scriptHeader.js";

    /**
     * javascript-file for scriptOrder. <BR/>
     */
    public static final String JS_SHEETORDER = "scriptOrder.js";

    /**
     * javascript-file for scriptCollection. <BR/>
     */
    public static final String JS_SHEETCOLLECTION = "scriptCollection.js";

    /**
     * javascript-file for catalog. <BR/>
     */
    public static final String JS_PRODUCTCATALOG = "scriptCatalog.js";

    /**
     * javascript-file for scriptCollection. <BR/>
     */
    public static final String JS_CALENDAR = "scriptCalendar.js";

    /**
     * javascript-file for message. <BR/>
     */
    public static final String JS_MESSAGE = "scriptMessage.js";

    /**
     * Name of the frameset-file. <BR/>
     */
    public static final String FILE_FRAMESET = "frameset.html";

    /**
     * Name of the buttons-file. <BR/>
     */
    public static final String FILE_BUTTONS = "buttons.html";
    
    /**
     * Name for an empty page
     */
    public static final String FILE_EMPTYPAGE = new String ("empty.htm");


    // operating system identifiers:
    /**
     * Operating system identifier for Windows NT. <BR/>
     */
    public static final String OS_NT = "NT";

    /**
     * Operating system identifier for LINUX. <BR/>
     */
    public static final String OS_LINUX = "LINUX";

    /**
     * Operating system identifier for UNIX. <BR/>
     */
    public static final String OS_UNIX = "UNIX";


//  WINDOWS CONSTANTS

    /**
     * Carrige return line feed. <BR/>
     */
    public static final String WC_CRLF = "\015\012";


//  UNICODES

    /**
     * Unicode: Quote. <BR/>
     */
    public static final String UC_QUOTE = "$#39";

    /**
     * Unicode: Comma. <BR/>
     */
    public static final String UC_COMMA = "$#44";


    /**
     * server side include file for welcome page. <BR/>
     */
    public static final String SSI_WELCOME = "welcome.htm";

    /**
     * server side include file for multilang client texts. <BR/>
     */
    public static final String SSI_MLICLIENTTEXTS = "mlitexts.htm";
    
    /**
     * redirect for welcomeframeset. <BR/>
     */
    public static final String SSI_WELCOMEFRAMESET = "welcomeframeset.htm";

    /**
     * server side include file for layout defined message. <BR/>
     */
    public static final String SSI_NOLAYOUTDEFINED = "nolayoutdefined.htm";

    /**
     * server side include file for layout not found message. <BR/>
     */
    public static final String SSI_NOLAYOUTFOUND = "nolayoutfound.htm";

    /**
     * server side include file for password change. <BR/>
     */
    public static final String SSI_CHANGEPWD = "changepwd.htm";

    /**
     * server side include file for additional login information. <BR/>
     */
    public static final String SSI_LOGININFO = "logininfo.htm";

    /**
     * Server side include file for the login dialog. <BR/>
     * The file may include some tags:
     * {@link UtilConstants#TAG_BASE UtilConstants.TAG_BASE},
     * {@link UtilConstants#TAG_FORMURL UtilConstants.TAG_FORMURL},
     * {@link UtilConstants#TAG_FCT UtilConstants.TAG_FCT},
     * {@link UtilConstants#TAG_USERNAME UtilConstants.TAG_USERNAME},
     * {@link UtilConstants#TAG_PASSWORD UtilConstants.TAG_PASSWORD},
     * {@link UtilConstants#TAG_LOGINDOMAINS UtilConstants.TAG_LOGINDOMAINS},
     * {@link UtilConstants#TAG_LOGININFO UtilConstants.TAG_LOGININFO} and
     * {@link UtilConstants#TAG_SYSINFO UtilConstants.TAG_SYSINFO}.
     */
    public static final String SSI_LOGINDIALOG = "logindialog.htm";

    /**
     * Server side include file for system information. <BR/>
     */
    public static final String SSI_SYSINFO = "sysInfo.htm";

    /**
     * direction of the radiobuttons in change-form
     */
    public static final int DIR_HORIZONTAL = 111;

    /**
     * direction of the radiobuttons in change-form
     */
    public static final int DIR_VERTICAL = 222;

    /**
     * JavaScript for showing Buttons in NavBar
     */
    public static final String CALL_SHOWNAVITEM = "top.showNavItem ('" + UtilConstants.TAG_NAME + "',\'" + UtilConstants.TAG_NAME2 + "\', this.document);";

    /**
     * The name of the customer name tuple in the table ibs_system.
     */
    public static final String CUSTOMER_NAME = "CUSTOMER_NAME";

    /**
     * The name of the system name tuple in the table ibs_system.
     */
    public static final String SYSTEM_NAME = "SYSTEM_NAME";

    /**
     * Show the linked object in searchlist. <BR/>
     */
    public static final int SHOWSEARCHEDOBJECTS = 3;

    /**
     * Extlogin invalid user.
     */
    public static final String EXTLOGIN_INVALID_USER = "INVALID_USER";

    /**
     * Extlogin invalid password.
     */
    public static final String EXTLOGIN_INVALID_PWD = "INVALID_PWD";

} // class AppConstants
