/*
 * Class: BOPathConstants.java
 */

// package:
package ibs.bo;

// imports:
import ibs.io.IOConstants;

import java.io.File;


/******************************************************************************
 * Constants for path operations. <BR/>
 *
 * @version     $Id: BOPathConstants.java,v 1.20 2010/04/14 13:04:51 btatzmann Exp $
 *
 * @author      Klaus, 15.10.2003
 ******************************************************************************
 */
public abstract class BOPathConstants extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: BOPathConstants.java,v 1.20 2010/04/14 13:04:51 btatzmann Exp $";


    /**
     * Path where the images reside. <BR/>
     */
    public static final String PATH_IMAGES = "images/";

    /**
     * Path where the global images reside. <BR/>
     */
    public static final String PATH_GLOBAL =
        BOPathConstants.PATH_IMAGES + "global/";

    /**
     * Path where the arrows reside. <BR/>
     */
    public static final String PATH_ARROWS =
        BOPathConstants.PATH_IMAGES + "arrows/";

    /**
     * Path where the backgrounds reside. <BR/>
     */
    public static final String PATH_BACKGROUNDS =
        BOPathConstants.PATH_IMAGES + "backgrounds/";

    /**
     * Path where the menu images reside. <BR/>
     */
    public static final String PATH_MENU =
        BOPathConstants.PATH_IMAGES + "menu/";

    /**
     * Path where the discussion images reside. <BR/>
     */
    public static final String PATH_DISC =
        BOPathConstants.PATH_IMAGES + "disc/";

    /**
     * Path where the button images reside. <BR/>
     */
    public static final String PATH_BUTTONS =
        BOPathConstants.PATH_IMAGES + "buttons/";

    /**
     * Path where the tab images reside. <BR/>
     */
    public static final String PATH_TABS =
        BOPathConstants.PATH_IMAGES + "tabs/";

    /**
     * Path where the object icons reside. <BR/>
     */
    public static final String PATH_OBJECTICONS =
        BOPathConstants.PATH_IMAGES + "objectIcons/";

    /**
     * Path where the message icons reside. <BR/>
     */
    public static final String PATH_MESSAGEICONS =
        BOPathConstants.PATH_IMAGES + "msgIcons/";

    /**
     * Path where the arrow icons reside. <BR/>
     */
    public static final String PATH_ARROWICONS = BOPathConstants.PATH_ARROWS;

    /**
     * Path where the help files reside. <BR/>
     */
    public static final String PATH_HELP = "help/";


    /**
     * Path where the configuration files reside. <BR/>
     */
    public static String PATH_CONF = "conf/";

    /**
     * Name of log directory. <BR/>
     */
    public static String PATHN_LOGS = "logs";
    /**
     * Path where the log files reside. <BR/>
     */
    public static String PATH_LOGS = BOPathConstants.PATHN_LOGS + "/";

    /**
     * Path where the sql log files reside. <BR/>
     */
    public static String PATH_SQLLOGS = BOPathConstants.PATH_LOGS + "sql/";

    /**
     * Name of application directory. <BR/>
     */
    public static final String PATHN_APP = "app";
    /**
     * Path where the application layout files reside. <BR/>
     */
    public static String PATH_APP = BOPathConstants.PATHN_APP + "/";

    /**
     * Path where the application image files reside. <BR/>
     */
    public static String PATH_APPIMAGES =
        BOPathConstants.PATH_APP + BOPathConstants.PATH_IMAGES;

    /**
     * PATH where the pages to include are located. <BR/>
     */
    public static final String PATH_INCLUDE = "include/";

    /**
     * Path where the include files reside. <BR/>
     */
    public static String PATH_APPINCLUDE =
        BOPathConstants.PATH_APP + BOPathConstants.PATH_INCLUDE;

    /**
     * Path where the different layouts are stored. <BR/>
     */
    public static final String PATH_LAYOUT = "layouts/";

    /**
     * Path where the layouts reside. <BR/>
     */
    public static String PATH_APPLAYOUTS =
        BOPathConstants.PATH_APP + BOPathConstants.PATH_LAYOUT;

    /**
     * Path where the reports are stored. <BR/>
     */
    public static final String PATH_REPORT = "reports/";

    /**
     * Path where the reports reside. <BR/>
     */
    public static String PATH_APPREPORTS =
        BOPathConstants.PATH_APP + BOPathConstants.PATH_REPORT;

    /**
     * Path where the layouts reside. <BR/>
     */
    public static String PATH_APPFORMCFG =
        BOPathConstants.PATH_APP + "formcfg/";

    /**
     * Name of installation directory. <BR/>
     */
    public static String PATHN_INSTALL = "install";

    /**
     * Name of default installation pakage directory. <BR/>
     */
    public static String PATHN_DEFAULTPACKAGE = "xml";

    /**
     * Path with installation files. <BR/>
     */
    public static String PATH_INSTALL =
        BOPathConstants.PATH_APP + BOPathConstants.PATHN_INSTALL + "/";

    /**
     * Path where the script files reside. <BR/>
     */
    public static String PATH_SCRIPTS = BOPathConstants.PATH_APP + "scripts/";

    /**
     * Path where the xslt style sheets reside. <BR/>
     */
    public static String PATH_XSLT = BOPathConstants.PATH_APP + "stylesheets/";

    /**
     * Path where the translator files reside. <BR/>
     */
    public static String PATH_TRANS = BOPathConstants.PATH_APP + "trans/";

    /**
     * Path where the library files reside. <BR/>
     */
    public static String PATH_LIB = "WEB-INF/lib/";


    /**
     * Path for upload. <BR/>
     */
    public static final String PATH_UPLOAD = "/m2/";

    /**
     * Path where the upload application can be found. <BR/>
     * This is a path relativ to <A HREF="#PATH_APP">PATH_APP</A>
     */
    public static final String PATH_UPLOADAPPLICATIONDIR = "upload/";

    /**
     * Path for document template translation log files. <BR/>
     */
    public static final String PATH_TYPELOGS = "typeLogs/";

    /**
     * Path for uploaded files. <BR/>
     */
    public static final String PATH_UPLOAD_FILES =
        BOPathConstants.PATH_UPLOADAPPLICATIONDIR + "files/";

    /**
     * Path for uploaded importfiles. <BR/>
     */
    public static final String PATH_UPLOAD_IMPORTFILES =
        BOPathConstants.PATH_UPLOADAPPLICATIONDIR + "importfiles/";

    /**
     * Path for uploaded images. <BR/>
     */
    public static final String PATH_UPLOAD_IMAGES =
        BOPathConstants.PATH_UPLOADAPPLICATIONDIR + BOPathConstants.PATH_IMAGES;

    /**
     * Path for uploaded pictures (needed in component m2.store). <BR/>
     */
    public static final String PATH_UPLOAD_PICTURES =
        BOPathConstants.PATH_UPLOADAPPLICATIONDIR + "productPictures/";

    /**
     * Path for uploaded pictures (needed in component m2.store). <BR/>
     */
    public static final String PATH_UPLOAD_THUMBS =
        BOPathConstants.PATH_UPLOADAPPLICATIONDIR + "productThumbs/";

    /**
     * Add to absolute m2 base upload path for the document template
     * translation log files. <BR/>
     */
    public static final String PATH_ABS_TYPELOGS = "typeLogs" + File.separator;

    /**
     * Add to absolute m2 base path for uploaded files. <BR/>
     */
    public static final String PATH_UPLOAD_ABS = "upload" + File.separator;

    /**
     * Add to absolute m2 base upload path for uploaded files. <BR/>
     * Absolute path is stored in table ibs_system.
     */
    public static final String PATH_UPLOAD_ABS_FILES =
        BOPathConstants.PATH_UPLOAD_ABS + "files" + File.separator;

    /**
     * Add to absolute m2 base temporary path for upload-files. <BR/>
     */
    public static final String PATH_UPLOAD_ABS_FILES_TEMP =
        BOPathConstants.PATH_UPLOAD_ABS + "temp" + File.separator;
    /**
     * Add to absolute m2 base upload path for uploaded import files. <BR/>
     * Absolute path is stored in table ibs_system.
     */
    public static final String PATH_UPLOAD_ABS_IMPORTFILES =
        BOPathConstants.PATH_UPLOAD_ABS + "importfiles" + File.separator;

    /**
     * Add to absolute m2 base upload path for uploaded images. <BR/>
     * Absolute path is stored in table ibs_system.
     */
    public static final String PATH_UPLOAD_ABS_IMAGES =
        BOPathConstants.PATH_UPLOAD_ABS + "images" + File.separator;

    /**
     * Add to absolute m2 base upload path for uploaded pictures. <BR/>
     * Absolute path is stored in table ibs_system.
     */
    public static final String PATH_UPLOAD_ABS_PICTURES =
        BOPathConstants.PATH_UPLOAD_ABS + "productPictures" + File.separator;

    /**
     * Add to absolute m2 base upload path for uploaded thumb nails. <BR/>
     * Absolute path is stored in table ibs_system.
     */
    public static final String PATH_UPLOAD_ABS_THUMBS =
        BOPathConstants.PATH_UPLOAD_ABS + "productThumbs" + File.separator;

    /**
     * Prefix for HTTP URLS. <BR/>
     * @deprecated Use {@link ibs.io.IOConstants.URL_HTTP} instead (KR 20070709).
     */
    @Deprecated
    public static final String PATH_HTTPPREFIX = IOConstants.URL_HTTP;

    /**
     * Prefix for HTTPS URLS. <BR/>
     * @deprecated Use {@link ibs.io.IOConstants.URL_HTTPS} instead (KR 20070709).
     */
    @Deprecated
    public static final String PATH_HTTPSPREFIX = IOConstants.URL_HTTPS;

    /**
     * Prefix for FTP URLs. <BR/>
     * @deprecated Use {@link ibs.io.IOConstants.URL_FTP} instead (KR 20070709).
     */
    @Deprecated
    public static final String PATH_FTPPREFIX = IOConstants.URL_FTP;


    /**
     * Path for the images of the buttons. <BR/>
     */
    public static final String PATH_IMAGE_BUTTONS =
        BOPathConstants.PATH_BUTTONS;

    /**
     * Path for the images of the tabs. <BR/>
     */
    public static final String PATH_IMAGE_TABS = BOPathConstants.PATH_TABS;

    /**
     * Path for the images of the sheet. <BR/>
     */
    public static final String PATH_IMAGE_SHEET =
        BOPathConstants.PATH_IMAGES + "sheet/";

    /**
     * Path for the images of the list. <BR/>
     */
    public static final String PATH_IMAGE_LIST =
        BOPathConstants.PATH_IMAGES + "list/";

    /**
     * Path for the images of the menu. <BR/>
     */
    public static final String PATH_IMAGE_MENU = BOPathConstants.PATH_MENU;

    /**
     * Path for the images of the header. <BR/>
     */
    public static final String PATH_IMAGE_HEADER =
        BOPathConstants.PATH_IMAGES + "header/";

    /**
     * Path for the images of the order. <BR/>
     */
    public static final String PATH_IMAGE_SHEETORDER =
        BOPathConstants.PATH_IMAGES + "product/";

    /**
     * Path for the images of the sheetCollection. <BR/>
     */
    public static final String PATH_IMAGE_SHEETCOLLECTION =
        BOPathConstants.PATH_IMAGES + BOPathConstants.PATH_IMAGE_SHEETORDER;

    /**
     * Path for the images of the catalog. <BR/>
     */
    public static final String PATH_IMAGE_PRODUCTCATALOG =
        BOPathConstants.PATH_IMAGES + "catalog/";

    /**
     * Path for the images of the buttons. <BR/>
     */
    public static final String PATH_IMAGE_CALENDAR =
        BOPathConstants.PATH_IMAGES + "calendar/";

    /**
     * Path for the images of the messages. <BR/>
     */
    public static final String PATH_IMAGE_MESSAGE =
        BOPathConstants.PATH_IMAGES + "messages/";


    /**
     * Add to absolute m2 base path for application files. <BR/>
     */
    public static final String PATH_ABS_APP =
        BOPathConstants.PATHN_APP + File.separator;

    /**
     * Directory for installation files. <BR/>
     */
    public static final String PATH_ABS_INSTALL =
        BOPathConstants.PATHN_INSTALL + File.separator;

    /**
     * Add to absolute m2 base path for installation files. <BR/>
     */
    public static final String PATH_ABS_APPINSTALL =
        BOPathConstants.PATH_ABS_APP + BOPathConstants.PATH_ABS_INSTALL;

    /**
     * Add to absolute m2 base path for the type translation files. <BR/>
     */
    public static final String PATH_ABS_TYPETRANSLATORS =
        BOPathConstants.PATH_ABS_APP + "typeTranslators" + File.separator;

    /**
     * Path where the temporary files are stored. <BR/>
     */
    public static final String PATH_TEMP = "temp";

    /**
     * Path where language dependent files are stored. <BR/>
     */
    public static final String PATH_LANG = "lang";
    
    /**
     * Path where the deployed resource bundles reside. <BR/>
     */
    public static String PATH_RESOURCE_BUNDLES = BOPathConstants.PATH_APP +
        BOPathConstants.PATH_LANG + "/rb/";
    
    /**
     * Path where the preloaded client side multilang html files reside. <BR/>
     */
    public static String PATH_MLI_CLIENT_TEXTS = BOPathConstants.PATH_LANG + "/html/";
    
    /**
     * Protocol 'file:'. <BR/>
     */
    public static String PROTOCOL_FILE = "file:";
} // class BOPathConstants
