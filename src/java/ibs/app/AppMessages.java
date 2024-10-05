/*
 * Class: AppMessages.java
 */

// package:
package ibs.app;

// imports:
import ibs.bo.BOMessages;
import ibs.bo.BOTokens;
import ibs.tech.html.IE302;
import ibs.util.UtilConstants;


/******************************************************************************
 * Messages for ibs applications. <BR/>
 * This abstract class contains all messages which are necessary to deal with
 * the classes delivered within this package. <P>
 * The messages can use tags for specific values to be inserted at runtime.
 * This tags can be replaced by the values with the
 * {@link ibs.util.Helpers#replace (String, String, String)} function.
 *
 * @version     $Id: AppMessages.java,v 1.20 2010/05/04 11:53:20 btatzmann Exp $
 *
 * @author      Klaus Reimüller (KR), 980614
 ******************************************************************************
 */
public abstract class AppMessages extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: AppMessages.java,v 1.20 2010/05/04 11:53:20 btatzmann Exp $";

    /**
     * Name of bundle where the messages are included. <BR/>
     */
    public static String MSG_BUNDLE = BOMessages.MSG_BUNDLE;

    // logout:
    /**
     * Message to be displayd when a User was logged out. <BR/>
     */
    public static String ML_MSG_LOGOUT = "ML_MSG_LOGOUT";

    // browser:
    /**
     * Browser incompatibility message. <BR/>
     * The tag <A HREF="UtilConstants.html#UtilConstants.TAG_BROWSER">UtilConstants.TAG_BROWSER</A>
     * is used to represent the browser of the user.
     * The tag <A HREF="UtilConstants.html#UtilConstants.TAG_DEFAULTBROWSER">UtilConstants.TAG_DEFAULTBROWSER</A>
     * is used to represent the name of the default browser.
     */
    public static String ML_MSG_BROWSERINCOMPATIBLE = "ML_MSG_BROWSERINCOMPATIBLE";

    
    // message header
    /**
     * Message header. <BR/>
     */
    public static String ML_MSG_MESSAGEHEADER = "ML_MSG_MESSAGEHEADER";
    
    /**
     * Default message header. <BR/>
     */
    public static String MSG_DEF_MESSAGEHEADER = "message";


    // values to be shown for boolean properties:
    /**
     * Value shown for true value of boolean property. <BR/>
     */
    public static String ML_MSG_BOOLTRUE = "ML_MSG_BOOLTRUE";

    /**
     * Value shown for false value of boolean property. <BR/>
     */
    public static String ML_MSG_BOOLFALSE = "ML_MSG_BOOLFALSE";

    /**
     * The top level object is already shown to the user. <BR/>
     * This message is used if the user wants to navigate to upper container
     * when there is none.
     */
    public static String ML_MSG_TOPLEVEL = "ML_MSG_TOPLEVEL";

    /**
     * Message to be printed if the user was not found during login. <BR/>
     */
    public static String ML_MSG_USERNOTFOUND = "ML_MSG_USERNOTFOUND";

    /**
     * Message to be printed if the password was wrong during login. <BR/>
     */
    public static String ML_MSG_PASSWORDWRONG = "ML_MSG_PASSWORDWRONG";

    /**
     * Message to be printed if the old password was not correct when
     * trying to change it to a new one. <BR/>
     */
    public static String ML_MSG_PASSWORDTOCHANGEWRONG = "ML_MSG_PASSWORDTOCHANGEWRONG";

    /**
     * Message to be printed if the user ran out. <BR/>
     */
    public static String ML_MSG_USERRANOUT = "ML_MSG_USERRANOUT";

    /**
     * Message to be printed if the login was not possible. <BR/>
     */
    public static String ML_MSG_LOGINNOTPOSSIBLE = "ML_MSG_LOGINNOTPOSSIBLE";

    /**
     * Message to be printed if the login was o.k. <BR/>
     */
    public static String ML_MSG_LOGINOK = "ML_MSG_LOGINOK";

    /**
     * Message to be printed if no recipients have been found. <BR/>
     */
    public static String ML_MSG_NORECIPIENTSFOUND = "ML_MSG_NORECIPIENTSFOUND";

    /**
     * Message to be printed if user has first login. <BR/>
     */
    public static String ML_MSG_FIRSTLOGIN = "ML_MSG_FIRSTLOGIN";

    /**
     * Message to be displayed if the password contains no letters. <BR/>
     */
    public static String ML_MSG_PASSWORDEMPTY = "ML_MSG_PASSWORDEMPTY";

    /**
     * Message to be displayed if the password confirmation was wrong. <BR/>
     */
    public static String ML_MSG_PASSWORDCONFIRMFAIL = "ML_MSG_PASSWORDCONFIRMFAIL";
    /**
     * Message to be displayd if the password change succeeded. <BR/>
     */
    public static String ML_MSG_PASSWORDCHANGED = "ML_MSG_PASSWORDCHANGED";

    /**
     * Message to be displayd if there is no layout defined. <BR/>
     */
    public static String ML_MSG_NOLAYOUTDEFINED = "ML_MSG_NOLAYOUTDEFINED";

    /**
     * Message to be displayd if the layout for the user was not found. <BR/>
     */
    public static String ML_MSG_NOLAYOUTFOUND = "ML_MSG_NOLAYOUTFOUND";

    /**
     * Message to be displayd if the object could not be distributed. <BR/>
     */
    public static String ML_MSG_COULDNOTDISTRIBUTEOBJECT = "Objekt konnte nicht verteilt werden!";

    /**
     * Message to be displayd if the objects could not be distributed. <BR/>
     */
    public static String ML_MSG_COULDNOTDISTRIBUTEOBJECTS = "ML_MSG_COULDNOTDISTRIBUTEOBJECTS";

    /**
     * Message to be displayed if an object was forwarded. <BR/>
     */
    public static String ML_MSG_OBJECTFORWARDED = "ML_MSG_OBJECTFORWARDED";

    /**
     * Message to be displayd if an object could not be forwarded. <BR/>
     */
    public static String ML_MSG_OBJECTNOTFORWARDED = "ML_MSG_OBJECTNOTFORWARDED";

    /**
     * Message to be displayd if a Cookie could not be read. <BR/>
     */
    public static String ML_MSG_COOKIENOTFOUND = "ML_MSG_COOKIENOTFOUND";

    /**
     * Message to be displayd if the data of the Cookie is invalid. <BR/>
     */
    public static String ML_MSG_COOKIEDATAINVALID = "ML_MSG_COOKIEDATAINVALID";

    /**
     * Message to be displayd if the objectPath is not given in the url of a weblink. <BR/>
     */
    public static String ML_MSG_NOPATHGIVEN = "ML_MSG_NOPATHGIVEN";

    /**
     * Message to be display if the customer name or system name is not defined
     * in the ibs_system table.
     */
    public static String ML_MSG_NOSYSTEMDOMAINGIVEN = "ML_MSG_NOSYSTEMDOMAINGIVEN";

    // message type index numbers:
    /**
     * Message type: Info. <BR/>
     */
    public static final int MST_INFO = 0;
    /**
     * Message type: Question. <BR/>
     */
    public static final int MST_QUESTION = 1;
    /**
     * Message type: Warning. <BR/>
     */
    public static final int MST_WARNING = 2;
    /**
     * Message type: Error. <BR/>
     */
    public static final int MST_ERROR = 3;
    /**
     * Message type: Debug. <BR/>
     */
    public static final int MST_DEBUG = 4;

    /**
     * header texts for the messagetypes. <BR/>
     */
    public static String[] MST_HEADERS =
    {
        BOTokens.ML_MSTHEADER_INFO,
        BOTokens.ML_MSTHEADER_QUESTION,
        BOTokens.ML_MSTHEADER_WARNING,
        BOTokens.ML_MSTHEADER_ERROR,
        BOTokens.ML_MSTHEADER_DEBUG,
    }; // MST_HEADERS

    /**
     * default header texts for the messagetypes. <BR/>
     */    
    public static String[] MST_DEF_HEADERS =
    {
        "hint",
        "question",
        "warning",
        "error",
        "debug",
    }; // MST_DEF_HEADERS

    /**
     * images for the messagetypes. <BR/>
     */
    public static final String[] MST_IMAGES =
    {
        "info.gif",
        "question.gif",
        "warning.gif",
        "error.gif",
        "debug.gif",
    }; // MST_IMAGES

} // class AppMessages
