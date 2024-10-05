/*
 * Class: CssConstants.java
 */

// package:
package ibs.app;

// imports:


/******************************************************************************
 * Constants for ibs applications. <BR/>
 * This abstract class contains all constants which are necessary to deal with
 * the classes delivered within this package. <P>
 * Contains system defined object types with IDs from 0x0011 to 0x00FF.<BR/>
 * Format of object types: <B>ddsstttv</B><BR/>
 * <UL>
 * <LI><B>dd</B> ... domain id (01 .. FF)
 * <LI><B>ss</B> ... server id (01 .. FF)
 * <LI><B>ttt</B> .. type id (001 .. FFF)
 * <LI><B>v</B> .... version id (0 .. F)
 * </UL><BR/>
 * <B>ttt</B> has the following ranges depending on its use:<BR/>
 * <UL>
 * <LI>001 .. 00F: system defined object types
 * <LI>010 .. 0FF: base object types
 * <LI>110 .. FFF: derived object types.
 * </UL><P>
 *
 * @version     $Id: CssConstants.java,v 1.7 2011/08/08 14:10:58 btatzmann Exp $
 *
 * @author      Klaus Reimüller (KR), 20060120
 ******************************************************************************
 */
public abstract class CssConstants extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: CssConstants.java,v 1.7 2011/08/08 14:10:58 btatzmann Exp $";


    /**
     * The name of the class in css for description. <BR/>
     */
    public static final String CLASS_LISTDESCRIPTION = "listdescription";

    /**
     * The name of the class in css for listheader. <BR/>
     */
    public static final String CLASS_LISTHEADER = "listheader";

    /**
     * The name of the class in css for header. <BR/>
     */
    public static final String CLASS_HEADER = "header";

    /**
     * The name of the class in css for header. <BR/>
     */
    public static final String CLASS_HEADERNUM = "headernum";

    /**
     * The name of the class in css for list. <BR/>
     */
    public static final String CLASS_LIST = "list";

    /**
     * The name of the class in css for sub entries table. <BR/>
     */
    public static final String CLASS_LIST_ENTRIES_TABLE = "entries";

    /**
     * The name of the class in css for an open sub entries table. <BR/>
     */
    public static final String CLASS_SUB_ENTRIES_TABLE_OPEN = "subEntriesState_open";

    /**
     * The name of the class in css for a closed sub entries table. <BR/>
     */
    public static final String CLASS_SUB_ENTRIES_TABLE_CLOSED = "subEntriesState_closed";

    /**
     * text in a container - content-view wich must be seen better than
     * the other text. <BR/>
     */
    public static final String CLASS_LISTCONTENTSPECIAL = "listcontspecial";

    /**
     * The name of the class in css for column icon. <BR/>
     */
    public static final String CLASS_COLICON = "icon";

    /**
     * The name of the class in css for column name. <BR/>
     */
    public static final String CLASS_COLNAME = CssConstants.CLASS_NAME;

    /**
     * The name of the class in css for column name. <BR/>
     */
    public static final String CLASS_COLOWNER = "owner";

    /**
     * The name of the class in css for column name. <BR/>
     */
    public static final String CLASS_COLLASTCHANGED = "lastchanged";

    /**
     * The name of the class in css for name. <BR/>
     */
    public static final String CLASS_NAME = "name";

    /**
     * The name of the class in css for value. <BR/>
     */
    public static final String CLASS_VALUE = "value";

    /**
     * The name of the class in css for info-table. <BR/>
     */
    public static final String CLASS_INFO = "info";

    /**
     * The name of the class in css for welcome-table. <BR/>
     */
    public static final String CLASS_WELCOME = "welcome";

    /**
     * The name of the class in css for selections. <BR/>
     */
    public static final String CLASS_SELECT = "select";

    /**
     * The name of the class in css for rightslegend. <BR/>
     */
    public static final String CLASS_LEGEND = "legend";

    /**
     * The name of the class in css for footer. <BR/>
     */
    public static final String CLASS_FOOTER = "footer";

    /**
     * The name of the class in css for rightscolumn. <BR/>
     */
    public static final String CLASS_COLRIGHT = "right";

    /**
     * The name of the class in css for rightscolumn. <BR/>
     */
    public static final String CLASS_COLRIGHTDESCRIPTION = "rightdescr";

    /**
     * The name of the class in css for rights td. <BR/>
     */
    public static final String CLASS_TDRIGHT = "right";

    /**
     * The name of the class in css for rights description td. <BR/>
     */
    public static final String CLASS_TDRIGHTDESCRIPTION = "rightdescr";

    /**
     * The name of the class in css for messages. <BR/>
     */
    public static final String CLASS_MESSAGE = "message";

    /**
     * The name of the class in css for messageicon. <BR/>
     */
    public static final String CLASS_MSGHEADER = "msgheader";

    /**
     * The name of the class in css for the div of the body. <BR/>
     */
    public static final String CLASS_BODY = "body";

    /**
     * The name of the class in css for addresses. <BR/>
     */
    public static final String CLASS_ADDRESS = "address";

    /**
     * The name of the class in css for addresses. <BR/>
     */
    public static final String CLASS_SIGNATURE = "signature";

    /**
     * The name of the class in css for the newsmessage in the welcomescreen. <BR/>
     */
    public static final String CLASS_NEWS = "news";

    /**
     * The name of the class in css for the inboxmessage in the welcomescreen. <BR/>
     */
    public static final String CLASS_INBOX = "inbox";

    /**
     * The name of the class in css for the usermessage in the welcomescreen. <BR/>
     */
    public static final String CLASS_USER = "user";

    /**
     * The name of the class in css for the loginmessage in the welcomescreen. <BR/>
     */
    public static final String CLASS_LOGIN = "login";

    /**
     * stylesheet-Class for the References-Table
     */
    public static final String CLASS_REFS = "references";

    /**
     * stylesheet-Class for the Path in the header
     */
    public static final String CLASS_PATH = "path";

    /**
     * stylesheet-Class for the separator of the path in rhe header
     */
    public static final String CLASS_PATHSEPARATOR = "pathsep";

    /**
     * stylesheet-Class for the separator of the path in rhe header
     */
    public static final String CLASS_PATHOBJECT = "pathobj";

    /**
     * stylesheet-Class for the description in the list of NewsContainer
     */
    public static final String CLASS_ELEMENTDESCRIPTION = "elemdescr";


    // css styles for field types:
    /**
     * CSS class for field type BOOLEAN. <BR/>
     */
    public static final String CLASS_BOOLEAN = "boolean";

    /**
     * CSS class for field type BOOLEAN with empty option. <BR/>
     */
    public static final String CLASS_BOOLEANEMPTY = "booleanempty";

    /**
     * CSS class for field type INTEGER. <BR/>
     */
    public static final String CLASS_INTEGER = "integer";

    /**
     * CSS class for field type NUMBER. <BR/>
     */
    public static final String CLASS_NUMBER = "number";

    /**
     * CSS class for field type DATE. <BR/>
     */
    public static final String CLASS_DATE = "date";

    /**
     * CSS class for field type TIME. <BR/>
     */
    public static final String CLASS_TIME = "time";

    /**
     * CSS class for field type MONEY. <BR/>
     */
    public static final String CLASS_MONEY = "money";

    /**
     * CSS class for field type EMAIL. <BR/>
     */
    public static final String CLASS_EMAIL = "email";

    /**
     * CSS class for field type URL. <BR/>
     */
    public static final String CLASS_URL = "url";

    /**
     * CSS class for field type DESCRIPTION. <BR/>
     */
    public static final String CLASS_DESCRIPTION = "description";

    /**
     * CSS class for field type TEXT. <BR/>
     */
    public static final String CLASS_TEXT = "text";

    /**
     * CSS class for field type TEXTAREA. <BR/>
     */
    public static final String CLASS_TEXTAREA = "textarea";

    /**
     * CSS class for field type HTMLTEXT. <BR/>
     */
    public static final String CLASS_HTMLTEXT = "htmltext";

    /**
     * CSS class for field type RADIO button. <BR/>
     */
    public static final String CLASS_RADIO = "radio";

    /**
     * CSS class for field type FILE. <BR/>
     */
    public static final String CLASS_FILE = "file";

    /**
     * CSS class for field type IMAGE. <BR/>
     */
    public static final String CLASS_IMAGE = "image";

    /**
     * CSS class for field type PICTURE. <BR/>
     */
    public static final String CLASS_PICTURE = "picture";

    /**
     * CSS class for field type THUMBNAIL. <BR/>
     */
    public static final String CLASS_THUMBNAIL = "thumbnail";

    /**
     * CSS class for field type IMPORTFILE. <BR/>
     */
    public static final String CLASS_IMPORTFILE = "importfile";


    // other CSS constants:
    /**
     * CSS class for invisible link. <BR/>
     */
    public static final String CLASS_INVISLINK = "invisLink";

    /**
     * CSS class for file button. <BR/>
     */
    public static final String CLASS_BUTTONFILE = "filebutton";

    /**
     * CSS class for submit button. <BR/>
     */
    public static final String CLASS_BUTTONSUBMIT = "submit";

    /**
     * CSS class for cancel button. <BR/>
     */
    public static final String CLASS_BUTTONCANCEL = "cancel";

    /**
     * The name of the class in css for hr footer. <BR/>
     */
    public static final String CLASS_FOOTER_HR = "footerHr";

    /**
     * CSS class for buttons. <BR/>
     */
    public static final String CLASS_BUTTONS = "buttons";
    
    /**
     * CSS class for actions. <BR/>
     */
    public static final String CLASS_ACTIONS = "actions";
} // class CssConstants