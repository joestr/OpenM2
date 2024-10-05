/*
 * Class: UtilConstants.java
 */

// package:
package ibs.util;

// imports:


/******************************************************************************
 * Constants for intranet business solutions utilities. <BR/>
 * This abstract class contains all constants which are necessary to deal with
 * the utilities delivered within this package.
 *
 * @version     $Id: UtilConstants.java,v 1.33 2010/05/18 09:42:02 btatzmann Exp $
 *
 * @author      Klaus Reimüller (KR), 980330
 ******************************************************************************
 */
public abstract class UtilConstants extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: UtilConstants.java,v 1.33 2010/05/18 09:42:02 btatzmann Exp $";


    /**
     * Sign to identify hexadecimal numbers. <BR/>
     */
    public static final String NUM_START_HEX = "0x";

    /**
     * Sign to identify hexadecimal numbers (upper case version). <BR/>
     */
    public static final String NUM_START_HEX_UPPER =
        UtilConstants.NUM_START_HEX.toUpperCase ();

    // some tags which are used within strings as placeholders:
    /**
     * Oid of an object. <BR/>
     */
    public static final String TAG_OID = "<oid>";

    /**
     * Name of an object. <BR/>
     */
    public static final String TAG_NAME = "<name>";

    /**
     * Quoted name of an object. <BR/>
     */
    public static final String TAG_QUOTEDNAME = "\'" + UtilConstants.TAG_NAME + "\'";

    /**
     * Other name of an object. <BR/>
     */
    public static final String TAG_NAME2 = "<name2>";

    /**
     * Again another name of an object. <BR/>
     */
    public static final String TAG_NAME3 = "<name3>";

    /**
     * Again another name of an object. <BR/>
     */
    public static final String TAG_NAME4 = "<name4>";

    /**
     * Number of elements. <BR/>
     */
    public static final String TAG_NUMBER = "<number>";

    /**
     * Actual browser of user. <BR/>
     */
    public static final String TAG_BROWSER = "<browser>";

    /**
     * Default browser. <BR/>
     */
    public static final String TAG_DEFAULTBROWSER = "<defaultbrowser>";

    /**
     * Exception. <BR/>
     */
    public static final String TAG_EXCEPTION = "<exception>";

    /**
     * Actual operation. <BR/>
     */
    public static final String TAG_OPERATION = "<op>";

    // representation formats:
    /**
     * Standard representation format. <BR/>
     */
    public static final int REP_STANDARD        = 1;
    /**
     * Catalog representation format. <BR/>
     */
    public static final int REP_CATALOG        = 2;

    /**
     * ISO3 country code. <BR/>
     * DEU = Deutschland.
     */
    public static final String LOCALE_COUNTRY = "DE";

    /**
     * ISO3 language code. <BR/>
     * deu = deutsch.
     */
    public static final String LOCALE_LANGUAGE = "de";

    // HTTP header methods
    /**
     * Post a form content. <BR/>
     */
    public static final String HTTP_POST = "POST";

    /**
     * Get a document. <BR/>
     */
    public static final String HTTP_GET = "GET";

    // return constants for business object

    // query properties
    /**
     * Action query flag. <BR/>
     */
    public static final boolean QRY_ACTION          = true;

    /**
     * Problems during performing a query. <BR/>
     */
    public static final int QRY_NOT_OK               = 0;

    /**
     * No problems during performing a query. <BR/>
     */
    public static final int QRY_OK                  = 1;

    /**
     * User has not sufficient rights to perform query. <BR/>
     */
    public static final int QRY_INSUFFICIENTRIGHTS  = 2;

    /**
     * The required object was not found within the database. <BR/>
     */
    public static final int QRY_OBJECTNOTFOUND  = 3;

    /**
     * There where more objects found, than could be handled. <BR/>
     */
    public static final int QRY_TOOMANYROWS = 5;

    /**
     * The password was wrong during login. <BR/>
     */
    public static final int QRY_PASSWORDWRONG  = 11;

    /**
     * The object exists already within the database. <BR/>
     */
    public static final int QRY_ALREADY_EXISTS  = 21;

    /**
     * The function didn't affect all possible Objects. <BR/>
     */
    public static final int QRY_NOTALLAFFECTED  = 31;

    /**
     * The object which was required within the database is not longer valid.
     * <BR/>
     */
    public static final int QRY_NOTVALID = 41;

    /**
     * The name of the user ia already given within the database in this domain. <BR/>
     */
    public static final int QRY_ALREADY_EXISTS_NAME  = 51;

    /**
     * An object which should be deleted still has dependent objects.
     */
    public static final int QRY_DEPENDENT_OBJECT_EXISTS = 61;

    /**
     * BOOLEAN value for true in SQL queries. <BR/>
     */
    public static final int QRY_TRUE = 1;

    /**
     * BOOLEAN value for true in SQL queries. <BR/>
     */
    public static final int QRY_FALSE = 0;

    /**
     * Action query flag. <BR/>
     */
    public static final boolean ACTION_QUERY = true;

    /**
     * Form-field relation operator: none. <BR/>
     */
    public static final int FF_REL_UNKNOWN = -1;

    /**
     * Form-field relation operator: equal. <BR/>
     */
    public static final int FF_REL_EQUAL = 0;

    /**
     * Form-field relation operator: not equal. <BR/>
     */
    public static final int FF_REL_NOTEQUAL = 1;

    /**
     * Form-field relation operator: greater. <BR/>
     */
    public static final int FF_REL_GREATER = 2;

    /**
     * Form-field relation operator: lower. <BR/>
     */
    public static final int FF_REL_LOWER = 3;

    /**
     * Form-field relation operator: greater equal. <BR/>
     */
    public static final int FF_REL_GREATEREQUAL = 4;

    /**
     * Form-field relation operator: lower equal. <BR/>
     */
    public static final int FF_REL_LOWEREQUAL = 5;

    /**
     * Form-field relation operator: equalIgnoreCase. <BR/>
     */
    public static final int FF_REL_EQUALIGNORECASE = 6;

    /**
     * Replacement tag for the Base-TAG in the welcome page. <BR/>
     */
    public static final String TAG_BASE = "<%base%>";

    /**
     * Replacement tag for the BrowserDir-TAG in the welcome page. <BR/>
     */
    public static final String TAG_BROWSERDIR = "<%browserdir%>";

    /**
     * Replacement tag for the News-message in the welcome page. <BR/>
     */
    public static final String TAG_NEWS = "<%news%>";

    /**
     * Replacement tag for the Inbox-message in the welcome page. <BR/>
     */
    public static final String TAG_INBOX = "<%inbox%>";

    /**
     * Replacement tag for the username in the welcome page. <BR/>
     * Also used for user name input field in login page.
     */
    public static final String TAG_USERNAME = "<%username%>";

    /**
     * Replacement tag for the username label in login page.
     */
    public static final String TAG_USERNAME_LABEL = "<%USERNAME_LABEL%>";

    /**
     * Replacement tag for the password label in login page.
     */
    public static final String TAG_PASSWORD_LABEL = "<%PASSWORD_LABEL%>";

    /**
     * Replacement tag for the domain label in login page.
     */
    public static final String TAG_DOMAIN_LABEL = "<%DOMAIN_LABEL%>";
    
    /**
     * Replacement tag for the message within the login info page.
     */
    public static final String TAG_LOGIN_INFO = "<%LOGIN_INFO%>";
    
    /**
     * Replacement tag for the login page header.
     */
    public static final String TAG_LOGIN_PAGE_HEADER = "<%LOGIN_PAGE_HEADER%>";

    /**
     * Replacement tag for the ok button label.
     */
    public static final String TAG_OK_BUTTON = "<%OK_BUTTON%>";

    /**
     * Replacement tag for the cancel button label.
     */
    public static final String TAG_CANCEL_BUTTON = "<%CANCEL_BUTTON%>";
    
    /**
     * Replacement tag for the password in the login page. <BR/>
     */
    public static final String TAG_PASSWORD = "<%password%>";

    /**
     * Replacement tag for the fullname of the user in the welcome page. <BR/>
     */
    public static final String TAG_USERFULLNAME = "<%userfullname%>";

    /**
     * Replacement tag for the lastlogin-message in the welcome page. <BR/>
     */
    public static final String TAG_LASTLOGIN = "<%lastlogin%>";

    /**
     * Replacement tag for the action url of an user dialog. <BR/>
     */
    public static final String TAG_FORMURL = "<%formurl%>";

    /**
     * Replacement tag for the function field of a form. <BR/>
     */
    public static final String TAG_FCT = "<%fct%>";

    /**
     * Replacement tag for the login domains selection box. <BR/>
     */
    public static final String TAG_LOGINDOMAINS = "<%logindomains%>";

    /**
     * Replacement tag for the login info include file. <BR/>
     */
    public static final String TAG_LOGININFO = "<%logininfo%>";

    /**
     * Replacement tag for the sysInfo include file.
     */
    public static final String TAG_SYSINFO = "<%sysinfo%>";

    /**
     * Replacement tag for the custom includes in an include file.
     */
    public static final String TAG_INCLUDES = "<%includes%>";

    /**
     * Replacement tag for the change pwd include file.
     */
    public static final String TAG_CHANGEPWD = "<%changepwd%>";

    /**
     * Replacement tag for part 1 of the login info text.
     */
    public static final String TAG_CHANGEPWD_INFO_TEXT_1 = "<%logininfo1%>";

    /**
     * Replacement tag for part 2 of the login info text.
     */
    public static final String TAG_CHANGEPWD_INFO_TEXT_2 = "<%logininfo2%>";

    /**
     * Replacement tag for yes.
     */
    public static final String TAG_YES = "<%yes%>";

    /**
     * Replacement tag for no.
     */
    public static final String TAG_NO = "<%no%>";

    /**
     * Replacement tag for installation date label.
     */
    public static final String TAG_INSTALLATION_DATE_LABEL = "<%installed%>";

    /**
     * Replacement tag for installation language label.
     */
    public static final String TAG_INSTALLATION_LANGUAGE_LABEL = "<%language%>";
    
    /**
     * Replacement tag for the mli text declaration in the mli client texts file. <BR/>
     */
    public static final String TAG_MLITEXTS = "<%mlitexts%>";

    /**
     * Replacement tag for no layout defined info. <BR/>
     */
    public static final String TAG_NO_LAYOUT_DEF_FOR_USER = "<%NO_LAYOUT_DEF_FOR_USER%>";

    /**
     * Replacement tag for no layout found info. <BR/>
     */
    public static final String TAG_NO_LAYOUT_FOUND_FOR_USER = "<%NO_LAYOUT_FOUND_FOR_USER%>";

    /**
     * Replacement tag for please contact info. <BR/>
     */
    public static final String TAG_PLEASE_CONTACT = "<%PLEASE_CONTACT%>";
    
    /**
     * Currency token: ATS. <BR/>
     */
    public static String TOK_CURRENCY_ATS = "ATS";
    /**
     * Currency token: EURO. <BR/>
     */
    public static String TOK_CURRENCY_EUR = "Euro";
    /**
     * Currency token: D-Mark. <BR/>
     */
    public static String TOK_CURRENCY_DM = "DM";

    /**
     * Currency tokens. <BR/>
     */
    public static String[] TOK_CURRENCIES =
    {
        UtilConstants.TOK_CURRENCY_ATS,
        UtilConstants.TOK_CURRENCY_EUR,
        UtilConstants.TOK_CURRENCY_DM,
    };

    /**
     * Constant for orderings: none. <BR/>
     */
    public static final int ORDER_NONE  = -1;
    /**
     * Constant for orderings: ascending. <BR/>
     */
    public static final int ORDER_ASC   = 0;
    /**
     * Constant for orderings: descending. <BR/>
     */
    public static final int ORDER_DESC  = 1;

} // class UtilConstants
