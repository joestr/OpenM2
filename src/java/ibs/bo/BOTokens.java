/*
 * Class: BOTokens.java
 */

// package:
package ibs.bo;

// imports:
//KR TODO: unsauber


/******************************************************************************
 * Tokens for business objects. <BR/>
 * This abstract class contains all tokens which are necessary to deal with
 * the business objects delivered within this package.
 *
 * @version     $Id: BOTokens.java,v 1.64 2013/01/15 14:48:28 rburgermann Exp $
 *
 * @author      Klaus Reimüller (KR), 980429
 ******************************************************************************
 */
public abstract class BOTokens extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: BOTokens.java,v 1.64 2013/01/15 14:48:28 rburgermann Exp $";

    /**
     * Name of bundle where the tokens included. <BR/>
     */
    public static String TOK_BUNDLE = "ibs_ibsbase_tokens";

    // text tokens:
    /**
     * OID an object. <BR/>
     */
    public static String ML_OID = "ML_OID";

    /**
     * Name of an object. <BR/>
     */
    public static String ML_NAME = "ML_NAME";

    /**
     * Name of the type of an object. <BR/>
     */
    public static String ML_TYPENAME = "ML_TYPENAME";

    /**
     * Owner of an object. <BR/>
     */
    public static String ML_OWNER = "ML_OWNER";

    /**
     *  ownerName ordering. <BR/>
     */
    public static String ML_LOGTYPE = "ML_LOGTYPE";

    /**
     *  ownerName ordering. <BR/>
     */
    public static String ML_LOGPARTOF = "ML_LOGPARTOF";

    /**
     * User and date of creation. <BR/>
     */
    public static String ML_CREATED = "ML_CREATED";

    /**
     * Creator of an object. <BR/>
     */
    public static String ML_CREATOR = "ML_CREATOR";

    /**
     * Date of creation. <BR/>
     */
    public static String ML_CREATIONDATE = "ML_CREATIONDATE";

    /**
     * User and date of last change. <BR/>
     */
    public static String ML_CHANGED = "ML_CHANGED";

    /**
     * Changer of an object. <BR/>
     */
    public static String ML_CHANGER = "ML_CHANGER";

    /**
     * Date of last change. <BR/>
     */
    public static String ML_LASTCHANGED = "ML_LASTCHANGED";

    /**
     * Type of an object. <BR/>
     */
    public static String ML_READDATE = "ML_READDATE";

    /**
     * Type of an object. <BR/>
     */
    public static String ML_FILTER = "ML_FILTER";

    /**
     * Type of an object. <BR/>
     */
    public static String ML_RECIPIENTENNAME = "ML_RECIPIENTENNAME";

    /**
     * Type of an object. <BR/>
     */
    public static String ML_TYPE = "ML_TYPE";

    /**
     * Description of an object. <BR/>
     */
    public static String ML_DESCRIPTION = "ML_DESCRIPTION";

    /**
     * Connection selector. <BR/>
     */
    public static String ML_SELECT = "ML_SELECT";

    /**
     *  The file size. <BR/>
     */
    public static String ML_FILESIZE = "ML_FILESIZE";

    /**
     *  The Hyperlink to a HTML-Page. <BR/>
     */
    public static String ML_HYPERLINK = "ML_HYPERLINK";

    /**
     *  A reference to a file defined with the path and the filename. <BR/>
     */
    public static String ML_FILE = "ML_FILE";

    /**
     *  A reference to a file defined with the path and the filename. <BR/>
     * This file is a translator to transform a old xml-template to a new one.
     */
    public static String ML_TRANSLATOR = "ML_TRANSLATOR";

    /**
     *  A reference to a file defined with the path and the filename. <BR/>
     */
    public static String ML_MASTERFILE = "ML_MASTERFILE";

    /**
     *  A reference to a file defined with the path and the filename. <BR/>
     */
    public static String ML_REALCOPYKIND = "ML_REALCOPYKIND";

    /**
     *  A reference to a file defined with the path and the filename. <BR/>
     */
    public static String ML_NAMEOFOBJECT = "ML_NAMEOFOBJECT";

    /**
     *  A reference to a file defined with the path and the filename. <BR/>
     */
    public static String ML_ACTION = "ML_ACTION";

    /**
     *  A reference to a file defined with the path and the filename. <BR/>
     */
    public static String ML_ACTIONDATE = "ML_ACTIONDATE";

    /**
     *  A reference to a file defined with the path and the filename. <BR/>
     */
    public static String ML_FIELDNAME = "ML_FIELDNAME";

    /**
     *  A reference to a file defined with the path and the filename. <BR/>
     */
    public static String ML_OLD_VALUE = "ML_OLD_VALUE";

    /**
     *  A reference to a file defined with the path and the filename. <BR/>
     */
    public static String ML_VALUE = "ML_VALUE";

    /**
     *  The Position of the recipient. <BR/>
     */
    public static String ML_RECIPIENTPOSITION    = "ML_RECIPIENTPOSITION";

    /**
     *  The Emailadress of the recipient. <BR/>
     */
    public static String ML_RECIPIENTEMAIL       = "ML_RECIPIENTEMAIL";

    /**
     *  The title of the recipient. <BR/>
     */
    public static String ML_RECIPIENTTITLE       = "ML_RECIPIENTTITLE";

    /**
     *  The organisation of the recipient. <BR/>
     */
    public static String ML_RECIPIENTCOMPANY     = "ML_RECIPIENTCOMPANY";

    /**
     *  A reference to a file defined with the path and the filename. <BR/>
     */
    public static String ML_DISTRIBUTENAME = "ML_DISTRIBUTENAME";

    /**
     * Name  of an object we have distributed. <BR/>
     */
    public static String ML_OBJECTNAME = BOTokens.ML_DISTRIBUTENAME;
    
    /**
     *  A reference to a file defined with the path and the filename. <BR/>
     */
    public static String ML_SENTDATE = "ML_SENTDATE";

    /**
     *  A reference to a file defined with the path and the filename. <BR/>
     */
    public static String ML_RECIPIENTS = "ML_RECIPIENTS";

    /**
     *  A reference to a file defined with the path and the filename. <BR/>
     */
    public static String ML_SOURCENAME = "ML_SOURCENAME";

    /**
     *  source of an attachment. <BR/>
     */
    public static String ML_ATTACHMENTSOURCE = "ML_ATTACHMENTSOURCE";

    /**
     *  A reference to a file defined with the path and the filename. <BR/>
     */
    public static String ML_ATTACHMENTTYPE = "ML_ATTACHMENTTYPE";

    /**
     * Argument used to paste as link or dublicate. <BR/>
     */
    public static String ML_DAYSSELECTED  = "ML_DAYSSELECTED";

    /**
     * Argument used to paste as link or dublicate. <BR/>
     */
    public static String ML_FROMTOSELECTED  = "ML_FROMTOSELECTED";

    /**
     * Argument used to paste as link or dublicate. <BR/>
     */
    public static String ML_TYPESELECTED  = "ML_TYPESELECTED";

    /**
     * Argument used to  as link or dublicate. <BR/>
     */
    public static String ML_NUMBEROFDAYS  = "ML_NUMBEROFDAYS";

    /**
     * Argument to read the selected startshowdate. <BR/>
     */
    public static String ML_STARTSHOWDATE  = "ML_STARTSHOWDATE";

    /**
     * Argument to read the selected endshowdate. <BR/>
     */
    public static String ML_ENDSHOWDATE  = "ML_ENDSHOWDATE";

    /**
     * Argument to read the selected Type of BusinessObject. <BR/>
     */
    public static String ML_SELECTEDTYPE  = "ML_SELECTEDTYPE";

    /**
     * Argument to read the selected Type of BusinessObject. <BR/>
     */
    public static String ML_RECIPIENTSELECTED  = "ML_RECIPIENTSELECTED";

    /**
     * Argument to read the selected Type of BusinessObject. <BR/>
     */
    public static String ML_RECIPIENTNAME  = "ML_RECIPIENTNAME";

    /**
     * Argument to read if the user want to do RealCopy or make just a reference. <BR/>
     */
    public static String ML_REFERENCEKIND  = "ML_REFERENCEKIND";

    /**
     * Argument to read if the user want to do RealCopy or make just a reference. <BR/>
     */
    public static String ML_RECIPIENTLIST  = BOTokens.ML_RECIPIENTS;

    /**
     * AttachmentReiter for BusinessObject Document . <BR/>
     */
    public static String ML_ATTACHMENTS  = "ML_ATTACHMENTS";
    
    /**
     * AttachmentReiter for BusinessObject Document . <BR/>
     */
    public static String ML_ATTACHMENTCONTAINER  = BOTokens.ML_ATTACHMENTS;

    /**
     * Freeze for distributeinterface. <BR/>
     */
    public static String ML_FREEZE = "ML_FREEZE";

    /**
     * Freeze for distributeinterface. <BR/>
     */
    public static String ML_SUPPLIER = "ML_SUPPLIER";

    /**
     *  A reference to a file defined with the path and the filename. <BR/>
     */
    public static String ML_PATH = "ML_PATH";

    /**
     *  A reference to a file defined with the path and the filename. <BR/>
     */
    public static String ML_MASTERDEFINED = "ML_MASTERDEFINED";

    /**
     * Valid-until date of an object. <BR/>
     */
    public static String ML_VALIDUNTIL = "ML_VALIDUNTIL";

    /**
     * Users. Used in object distribution form. <BR/>
     */
    public static String ML_USERS = "ML_USERS";

    /**
     * Groups.Used in object distribution form. <BR/>
     */
    public static String ML_GROUPS = "ML_GROUPS";

    /**
     * Receivers.Used in object distribution form. <BR/>
     */
    public static String ML_RECEIVERS = BOTokens.ML_RECIPIENTS;

    /**
     * PostIt. Used to comment the reason for the object distribution. <BR/>
     */
    public static String ML_POSTIT = "ML_POSTIT";

    /**
     * Explanation text for the cleaning expired objects form. <BR/>
     */
    public static String ML_CLEANHINT = "ML_CLEANHINT";

    /**
     * Caption in clean expired objects form. <BR/>
     */
    public static String ML_FUNCTIONCLEAN = "ML_FUNCTIONCLEAN";

    /**
     * Caption in notification form. <BR/>
     */
    public static String ML_NOTIFY = "ML_NOTIFY";

    /**
     * Caption in search form. <BR/>
     */
    public static String ML_FUNCTIONSEARCH = "ML_FUNCTIONSEARCH";

    /**
     * Name of linked object. <BR/>
     */
    public static String ML_LINKEDOBJECT = "ML_LINKEDOBJECT";

    /**
     * Name of linked objects. <BR/>
     */
    public static String ML_LINKEDOBJECTS = "ML_LINKEDOBJECTS";

    /**
     * Name of business object. <BR/>
     */
    public static String ML_BUSINESSOBJECT = "ML_BUSINESSOBJECT";

    /**
     * Pasting a business object. <BR/>
     */
    public static String ML_FUNCTIONPASTE = "ML_FUNCTIONPASTE";

    /**
     * Logging in. <BR/>
     */
    public static String ML_LOGIN = "ML_LOGIN";

    /**
     * Change the users password. <BR/>
     */
    public static String ML_CHANGEPASSWORD = "ML_CHANGEPASSWORD";

    /**
     * The old password. <BR/>
     */
    public static String ML_OLDPASSWORD = "ML_OLDPASSWORD";

    /**
     * The new password. <BR/>
     */
    public static String ML_NEWPASSWORD = "ML_NEWPASSWORD";

    /**
     * The new password confirmation. <BR/>
     */
    public static String ML_CONFIRMPASSWORD = "ML_CONFIRMPASSWORD";

    /**
     * Login page header text. <BR/>
     */
    public static String ML_LOGIN_PAGE_HEADER = "ML_LOGIN_PAGE_HEADER";
    
    /**
     * Username. <BR/>
     */
    public static String ML_USERNAME = "ML_USERNAME";

    /**
     * Password. <BR/>
     */
    public static String ML_PASSWORD = "ML_PASSWORD";

    /**
     * Password validation. <BR/>
     */
    public static String ML_PASSWORD2 = "ML_PASSWORD2";

    /**
     * Change password. <BR/>
     */
    public static String ML_CHANGE_PASSWORD = "ML_CHANGE_PASSWORD";

    /**
     * Domain. <BR/>
     */
    public static String ML_DOMAIN = "ML_DOMAIN";

    /**
     * The scheme of a domain. <BR/>
     */
    public static String ML_DOMAINSCHEME = "ML_DOMAINSCHEME";

    /**
     * The home page path of a domain, i.e. the path where the domain can be
     * found via url. <BR/>
     */
    public static String ML_HOMEPAGEPATH = "ML_HOMEPAGEPATH";

    /**
     *  name for QUERYSELECTIONBOX datatype. <BR/>
     */
    public static String ML_DTQUERYSELECTIONBOX = "ML_DTQUERYSELECTIONBOX";

    /**
     *  name for SELECTIONBOX datatype. <BR/>
     */
    public static String ML_DTSELECTIONBOX = "ML_DTSELECTIONBOX";

    /**
     * This domain scheme property tells whether a domain with this scheme
     * has a catalog management. <BR/>
     */
    public static String ML_HASCATALOGMMT = "ML_HASCATALOGMMT";

    /**
     * This domain scheme property tells whether a domain with this scheme
     * has a data interchange component. <BR/>
     */
    public static String ML_HASDATAINTERCHANGE = "ML_HASDATAINTERCHANGE";

    /**
     * This domain scheme property contains the procedure which is used
     * to create the workspace of an user within a domain having this
     * scheme. <BR/>
     */
    public static String ML_WORKSPACEPROC = "ML_WORKSPACEPROC";

    /**
     * This domain scheme property contains the number of domains which use
     * the current scheme. <BR/>
     */
    public static String ML_NUMBEROFDOMAINS = "ML_NUMBEROFDOMAINS";

    /**
     * Searchresult. <BR/>
     */
    public static String ML_SEARCHRESULT = "ML_SEARCHRESULT";

    /**
     * Upload. <BR/>
     */
    public static String ML_UPLOAD = "ML_UPLOAD";

    /**
     * Upload: upper frame. <BR/>
     */
    public static String ML_UPLOAD_UP = "ML_UPLOAD_UP";

    /**
     * Upload: lower frame. <BR/>
     */
    public static String ML_UPLOAD_DOWN = "ML_UPLOAD_DOWN";

    /**
     * Property of type representing the object's id. <BR/>
     */
    public static String ML_IDPROPERTY = "ML_IDPROPERTY";

    /**
     * Superior type. <BR/>
     */
    public static String ML_SUPERTYPE = "ML_SUPERTYPE";

    /**
     * Is the type a container? <BR/>
     */
    public static String ML_ISCONTAINER = "ML_ISCONTAINER";

    /**
     * Code of the type, type version, method, ... <BR/>
     */
    public static String ML_CODE = "ML_CODE";

    /**
     * The next property. <BR/>
     */
    public static String ML_NEXTPROPERTY = "ML_NEXTPROPERTY";

    /**
     * Actual version. <BR/>
     */
    public static String ML_ACTVERSION = "ML_ACTVERSION";

    /**
     * Sender. <BR/>
     */
    public static String ML_SENDER = "ML_SENDER";

    /**
     * Street. <BR/>
     */
    public static String ML_STREET = "ML_STREET";

    /**
     * zip. <BR/>
     */
    public static String ML_ZIP = "ML_ZIP";

    /**
     * mailbox. <BR/>
     */
    public static String ML_MAILBOX = "ML_MAILBOX";

    /**
     * country. <BR/>
     */
    public static String ML_COUNTRY = "ML_COUNTRY";

    /**
     * tel. <BR/>
     */
    public static String ML_TEL = "ML_TEL";

    /**
     * fax. <BR/>
     */
    public static String ML_FAX = "ML_FAX";

    /**
     * email. <BR/>
     */
    public static String ML_EMAIL = "ML_EMAIL";

    /**
     * homepage. <BR/>
     */
    public static String ML_HOMEPAGE = "ML_HOMEPAGE";

    /**
     * subject. <BR/>
     */
    public static String ML_SUBJECT = "ML_SUBJECT";

    /**
     *  A reference to a file defined with the path and the filename. <BR/>
     */
    public static String ML_SENTACTION = BOTokens.ML_SUBJECT;

    
    /**
     * activities. <BR/>
     */
    public static String ML_ACTIVITIES = "ML_ACTIVITIES";

    /**
     * everybody. <BR/>
     */
    public static String ML_EVERYBODY = "ML_EVERYBODY";

    /**
     * Show information of object. <BR/>
     */
    public static String ML_INFO = "ML_INFO";

    /**
     *  received. <BR/>
     */
    public static String ML_RECEIVED = "ML_RECEIVED";

    /**
     *  object recieved. <BR/>
     */
    public static String ML_OBJECTRECEIVED = "ML_OBJECTRECEIVED";

    /**
     *  received. <BR/>
     */
    public static String ML_RIGHTS = "ML_RIGHTS";

    /**
     *  Person/Group/Role. <BR/>
     */
    public static String ML_PGR = "ML_PGR";

    /**
     *  set filter. <BR/>
     */
    public static String ML_SETFILTER        = "ML_SETFILTER";

    /**
     *  substring string match. <BR/>
     */
    public static String ML_MATCHSUBSTRING   = "ML_MATCHSUBSTRING";

    /**
     *  exact string match. <BR/>
     */
    public static String ML_MATCHEXACT       = "ML_MATCHEXACT";

    /**
     *  soundex string match. <BR/>
     */
    public static String ML_MATCHSOUNDEX     = "ML_MATCHSOUNDEX";

    /**
     *  number match. <BR/>
     */
    public static String ML_MATCHGREATER     = "ML_MATCHGREATER";

    /**
     *  number match. <BR/>
     */
    public static String ML_MATCHLESS        = "ML_MATCHLESS";

    /**
     *  number match. <BR/>
     */
    public static String ML_MATCHGREATEREQUAL = "ML_MATCHGREATEREQUAL";

    /**
     *  number match. <BR/>
     */
    public static String ML_MATCHLESSEQUAL   = "ML_MATCHLESSEQUAL";

    /**
     *  you are. <BR/>
     */
    public static String ML_YOUARE           = "ML_YOUARE";

    /**
     *  interval for news. <BR/>
     */
    public static String ML_NEWSINTERVAL     = "ML_NEWSINTERVAL";

    /**
     *  interval for news. <BR/>
     */
    public static String ML_DAYS             = "ML_DAYS";

    /**
     *  fullname. <BR/>
     */
    public static String ML_FULLNAME = "ML_FULLNAME";

    /**
     *  fullname. <BR/>
     */
    public static String ML_AKTEURNAME = "ML_AKTEURNAME";

    /**
     *  aktiv. <BR/>
     */
    public static String ML_STACTIVE = "ML_STACTIVE";

    /**
     *  inaktiv. <BR/>
     */
    public static String ML_STINACTIVE = "ML_STINACTIVE";

    /**
     *  unknown. <BR/>
     */
    public static String ML_STUNKNOWN = "ML_STUNKNOWN";

    /**
     *  name for UNKNOWN datatype. <BR/>
     */
    public static String ML_DTUNKNOWN = "ML_DTUNKNOWN";

    /**
     *  name for BOOLEAN datatype. <BR/>
     */
    public static String ML_DTBOOL = "ML_DTBOOL";

    /**
     *  name for INTEGER datatype. <BR/>
     */
    public static String ML_DTINTEGER = "ML_DTINTEGER";

    /**
     *  name for INTEGERRANGE datatype. <BR/>
     */
    public static String ML_DTINTEGERRANGE = "ML_DTINTEGERRANGE";

    /**
     *  name for TEXT datatype. <BR/>
     */
    public static String ML_DTTEXT = "ML_DTTEXT";

    /**
     *  name for TEXTAREA datatype. <BR/>
     */
    public static String ML_DTTEXTAREA = "ML_DTTEXTAREA";

    /**
     *  name for DATE datatype. <BR/>
     */
    public static String ML_DTDATE = "ML_DTDATE";

    /**
     *  name for TIME datatype. <BR/>
     */
    public static String ML_DTTIME = "ML_DTTIME";

    /**
     *  name for DATETME datatype. <BR/>
     */
    public static String ML_DTDATETIME = "ML_DTDATETIME";

    /**
     *  name for DATERANGE datatype. <BR/>
     */
    public static String ML_DTDATERANGE = "ML_DTDATERANGE";

    /**
     *  name for RADIO datatype. <BR/>
     */
    public static String ML_DTRADIO = "ML_DTRADIO";

    /**
     *  name for SEPARATOR datatype. <BR/>
     */
    public static String ML_DTSEPARATOR = "ML_DTSEPARATOR";

    /**
     *  name for SELECT datatype. <BR/>
     */
    public static String ML_DTSELECT = "ML_DTSELECT";

    /**
     *  name for TYPEWITHALL datatype. <BR/>
     */
    public static String ML_DTTYPEWITHALL = "ML_DTTYPEWITHALL";

    /**
     *  name for TYPE datatype. <BR/>
     */
    public static String ML_DTTYPE = "ML_DTTYPE";

    /**
     *  name for FILE datatype. <BR/>
     */
    public static String ML_DTFILE = BOTokens.ML_FILE;

    /**
     *  name for IMPORTFILE datatype. <BR/>
     */
    public static String ML_DTIMPORTFILE = "ML_DTIMPORTFILE";

    /**
     *  name for USER datatype. <BR/>
     */
    public static String ML_DTUSER = BOTokens.ML_USERS;

    /**
     *  name for USERDATE datatype. <BR/>
     */
    public static String ML_DTUSERDATE = "ML_DTUSERDATE";

    /**
     *  name for LINK datatype. <BR/>
     */
    public static String ML_DTLINK = "ML_DTLINK";

    /**
     *  name for SEARCHTEXT datatype. <BR/>
     */
    public static String ML_DTDESCRIPTION = BOTokens.ML_DESCRIPTION;

    /**
     *  name for SEARCHTEXT datatype. <BR/>
     */
    public static String ML_DTSEARCHTEXT = "ML_DTSEARCHTEXT";

    /**
     *  name for search button of SEARCHTEXT datatype. <BR/>
     */
    public static String ML_DTSEARCHTEXTSEARCH = BOTokens.ML_FUNCTIONSEARCH;

    /**
     *  name for HIDDEN datatype. <BR/>
     */
    public static String ML_DTHIDDEN = "ML_DTHIDDEN";

    /**
     *  name for HIDDEN datatype. <BR/>
     */
    public static String ML_DTEMAIL = BOTokens.ML_EMAIL;

    /**
     *  name for URL datatype. <BR/>
     */
    public static String ML_DTURL = "ML_DTURL";

    /**
     *  name for NAME datatype. <BR/>
     */
    public static String ML_DTNAME = BOTokens.ML_NAME;

    /**
     *  name for PASSOWRD datatype. <BR/>
     */
    public static String ML_DTPASSWORD = "ML_DTPASSWORD";

    /**
     * time limit for newslist. <BR/>
     */
    public static String ML_NEWSTIMELIMIT = "ML_NEWSTIMELIMIT";

    /**
     * flag to show only unread messages in newslist. <BR/>
     */
    public static String ML_NEWSSHOWONLYUNREAD = "ML_NEWSSHOWONLYUNREAD";

    /**
     * flag to use timelimit in outbox. <BR/>
     */
    public static String ML_OUTBOXUSETIMELIMIT = "ML_OUTBOXUSETIMELIMIT";

    /**
     * timelimit in outbox. <BR/>
     */
    public static String ML_OUTBOXTIMELIMIT = "ML_OUTBOXTIMELIMIT";

    /**
     * flag to use timeframe in outbox. <BR/>
     */
    public static String ML_OUTBOXUSETIMEFRAME = "ML_OUTBOXUSETIMEFRAME";

    /**
     * timeframe in outbox. <BR/>
     */
    public static String ML_OUTBOXTIMEFRAME = "ML_OUTBOXTIMEFRAME";

    /**
     * flag to use no filter in outbox. <BR/>
     */
    public static String ML_OUTBOXUSENOFILTER = "ML_OUTBOXUSENOFILTER";

    /**
     * flag to show complete object attributes. <BR/>
     */
    public static String ML_SHOWEXTENDEDATTRIBUTES = "ML_SHOWEXTENDEDATTRIBUTES";

    /**
     * flag to save profile in a Cookie at login. <BR/>
     */
    public static String ML_SAVEPROFILE = "ML_SAVEPROFILE";

    /**
     * flag to show files in a separate window. <BR/>
     */
    public static String ML_SHOWFILESINWINDOWS = "ML_SHOWFILESINWINDOWS";

    /**
     * date of last login. <BR/>
     */
    public static String ML_LASTLOGIN = "ML_LASTLOGIN";
    
    /**
     * sys info label for date of installation. <BR/>
     */
    public static String ML_INSTALLTION_DATE = "ML_INSTALLTION_DATE";

    /**
     * sys info label for installation language. <BR/>
     */
    public static String ML_INSTALLTION_LANGUAGE = "ML_INSTALLTION_LANGUAGE";
    
    /**
     * is users browsers MSIE302. <BR/>
     */
    public static String ML_ISBROWSER_MSI302 = "ML_ISBROWSER_MSI302";

    /**
     * max entries beeing displayed in a list. <BR/>
     */
    public static String ML_DISPLAYMAXENTRIES = "ML_DISPLAYMAXENTRIES";

    /**
     * unlimited. <BR/>
     */
    public static String ML_UNLIMITED = "ML_UNLIMITED";

    /**
     * has been deleted. <BR/>
     */
    public static String ML_HASBEENDELETED = "ML_HASBEENDELETED";

    /**
     * could not be deleted. <BR/>
     */
    public static String ML_COULDNOTBEDELETED = "ML_COULDNOTBEDELETED";

    /**
     * restrict to max. results. <BR/>
     */
    public static String ML_MAXRESULT = "ML_MAXRESULT";

    /**
     * restrict to max. results token 2. <BR/>
     */
    public static String ML_MAXRESULT2 = "ML_MAXRESULT2";

    /**
     * state of an object. <BR/>
     */
    public static String ML_STATE = "ML_STATE";

    /**
     * Token for history function. <BR/>
     */
    public static String ML_GOTO = "ML_GOTO";

    /**
     * Token for history function. <BR/>
     */
    public static String ML_ACTOBJECT = "ML_ACTOBJECT";

    /**
     * Token for the ftp proxy field which may be used for connecting
     * to the internet via FTP. <BR/>
     */
    public static String ML_FTPPROXY = "ML_FTPPROXY";

    /**
     * Token for the ftp proxy bypass field which may be used for
     * explicitly connecting directly to some sites via FTP. <BR/>
     */
    public static String ML_FTPPROXYBYPASS = "ML_FTPPROXYBYPASS";

    /**
     * token for show a goto news button in welcome page. <BR/>
     */
    public static String ML_SHOWNEWS = "ML_SHOWNEWS";

    /**
     * token for show a goto inbox button in welcome page. <BR/>
     */
    public static String ML_SHOWINBOX = "ML_SHOWINBOX";

    /**
     * token for news. <BR/>
     */
    public static String ML_NEWS = "ML_NEWS";

    /**
     * token for number of entries in news.<BR/>
     */
    public static String ML_NEWSENTRIES = BOTokens.ML_NEWS;

    /**
     * token for one entry in news. <BR/>
     */
    public static String ML_NEWSENTRY = "ML_NEWSENTRY";

    /**
     * token for no news. <BR/>
     */
    public static String ML_NONEWS = "ML_NONEWS";

    /**
     *  token for "unread messages in inbox" in welcome page. <BR/>
     */
    public static String ML_UNREADMESSAGES   = "ML_UNREADMESSAGES";

    /**
     *  token for "unread message in inbox" in welcome page. <BR/>
     */
    public static String ML_UNREADMESSAGE   = "ML_UNREADMESSAGE";

    /**
     * token for no messages in inbix. <BR/>
     */
    public static String ML_NOMESSAGES = "ML_NOMESSAGES";

    /**
     * token for "the last" for "the last x days" in welcome page. <BR/>
     */
    public static String ML_THELAST = "ML_THELAST";

    /**
     * token for "days" in "the last x days" in welcome page. <BR/>
     */
    public static String ML_LASTDAYS = "ML_LASTDAYS";

    /**
     * token for info text part 1 in change password htm. <BR/>
     */
    public static String ML_CHANGE_PWD_INFO_1 = "ML_CHANGE_PWD_INFO_1";

    /**
     * token for info text part 2 in change password htm. <BR/>
     */
    public static String ML_CHANGE_PWD_INFO_2 = "ML_CHANGE_PWD_INFO_2";

    /**
     * token for yes button. <BR/>
     */
    public static String ML_BUTTON_YES = "ML_BUTTON_YES";

    /**
     * token for no button. <BR/>
     */
    public static String ML_BUTTON_NO = "ML_BUTTON_NO";

    /**
     * token for location. <BR/>
     */
    public static String ML_LOCATION = "ML_LOCATION";
    
    /**
     * town. <BR/>
     */
    public static String ML_TOWN = BOTokens.ML_LOCATION;

    /**
     * token for the layout.<BR/>
     */
    public static String ML_LAYOUT = "ML_LAYOUT";

    /**
     * token for the locale.<BR/>
     */
    public static String ML_LOCALE = "ML_LOCALE";

    /**
     * token for the Language.<BR/>
     */
    public static String ML_LOCALE_LANGUAGE = "ML_LOCALE_LANGUAGE";

    /**
     * token for the Country.<BR/>
     */
    public static String ML_LOCALE_COUNTRY = "ML_LOCALE_COUNTRY";
    
    /**
     * token for the is default field.<BR/>
     */
    public static String ML_IS_DEFAULT = "ML_IS_DEFAULT";
    
    /**
     * token for searchcontents
     */
    public static String ML_SEARCHCONTENT = "ML_SEARCHCONTENT";

    /**
     * token for the header of the selectionlist.<BR/>
     */
    public static String ML_SELHEADERSELECT = "ML_SELHEADERSELECT";

    /**
     * token for the header of the selectionlist.<BR/>
     */
    public static String ML_SELHEADERDELETE = "ML_SELHEADERDELETE";

   /**
     * Token for the headers of selectionlists for multiple operations:
     * Copy elements. <BR/>
     */
    public static String ML_SELHEADERCOPY       = "ML_SELHEADERCOPY";
    /**
     * Token for the headers of selectionlists for multiple operations:
     * Cut elements. <BR/>
     */
    public static String ML_SELHEADERCUT        = "ML_SELHEADERCUT";
    /**
     * Token for the headers of selectionlists for multiple operations:
     * Paste elements. <BR/>
     */
    public static String ML_SELHEADERPASTE      = "ML_SELHEADERPASTE";
    /**
     * Token for the headers of selectionlists for multiple operations:
     * Distribute elements. <BR/>
     */
    public static String ML_SELHEADERDISTRIBUTE = "ML_SELHEADERDISTRIBUTE";
    /**
     * Token for the headers of selectionlists for multiple operations:
     * Paste links elements. <BR/>
     */
    public static String ML_SELHEADERPASTELINK  = "ML_SELHEADERPASTELINK";
    /**
     * Token for the headers of selectionlists for multiple operations:
     * Export elements. <BR/>
     */
    public static String ML_SELHEADEREXPORT     = "ML_SELHEADEREXPORT";

    /**
     * token for the header of a singleselectioncontainer.<BR/>
     */
    public static String ML_SINGLESELHEADER = "ML_SINGLESELHEADER";

    /**
     * Token for the short menu for new objects: Save and New. <BR/>
     */
    public static String ML_SAVE_AND_NEW = "ML_SAVE_AND_NEW";
    /**
     * Token for the short menu for new objects: Save and Back. <BR/>
     */
    public static String ML_SAVE_AND_BACK = "ML_SAVE_AND_BACK";
    /**
     * Token for the short menu for new objects: Save and to Object. <BR/>
     */
    public static String ML_SAVE_AND_TO_OBJECT = "ML_SAVE_AND_TO_OBJECT";

// PROCESSSTATES
    /**
     * Token for the possible process states of an object: No process state. <BR/>
     */
    public static String ML_PST_NONE = "ML_PST_NONE";
    /**
     * Token for the possible process states of an object: Executing. <BR/>
     */
    public static String ML_PST_EXECUTING = "ML_PST_EXECUTING";
    /**
     * Token for the possible process states of an object: Not approved. <BR/>
     */
    public static String ML_PST_NOTAPPROVED = "ML_PST_NOTAPPROVED";
    /**
     * Token for the possible process states of an object: Approved. <BR/>
     */
    public static String ML_PST_APPROVED = "ML_PST_APPROVED";
    /**
     * Token for the possible process states of an object: Archived. <BR/>
     */
    public static String ML_PST_STORED = "ML_PST_STORED";
    /**
     * Token for the possible process states of an object: Open. <BR/>
     */
    public static String ML_PST_OPEN = "ML_PST_OPEN";
    /**
     * Token for the possible process states of an object: Rejected. <BR/>
     */
    public static String ML_PST_REJECTED = "ML_PST_REJECTED";

// ORDER PROCESSSTATES
    /**
     * Token for the specific process states for an order: Ordered. <BR/>
     */
    public static String ML_PST_ORDERED = "ML_PST_ORDERED";
    /**
     * Token for the specific process states for an order: Delivered. <BR/>
     */
    public static String ML_PST_DELIVERED = "ML_PST_DELIVERED";
    /**
     * Token for the specific process states for an order: Discarded. <BR/>
     */
    public static String ML_PST_DISCARDED = "ML_PST_DISCARDED";
    /**
     * Token for the specific process states for an order: Completed. <BR/>
     */
    public static String ML_PST_COMPLETED = "ML_PST_COMPLETED";

    /**
     * token for the header of the selectionlist.<BR/>
     */
    public static String ML_DTHIERARCHY = "ML_DTHIERARCHY";

    /**
     * token for the button to mark all.<BR/>
     */
    public static String ML_MARK = "ML_MARK";

    /**
     * token for the button to unmark all.<BR/>
     */
    public static String ML_UNMARK = "ML_UNMARK";

    /**
     * token for the button to invert selection.<BR/>
     */
    public static String ML_INVERTMARK = "ML_INVERTMARK";

    /**
     * token for the header of the selectionlist.<BR/>
     */
    public static String ML_INNEWS = "ML_INNEWS";

    /**
     * token for the header of the reference list.<BR/>
     */
    public static String ML_SEETOO = "ML_SEETOO";

    /**
     * token for the header of the generate translator form. <BR/>
     */
    public static String ML_GENERATETRANSLATOR = "ML_GENERATETRANSLATOR";

    /**
     * use rightaliases. <BR/>
     */
    public static String ML_USERIGHTALIASES = "ML_USERIGHTALIASES";

    /**
     * use rightaliases. <BR/>
     */
    public static String ML_TO_RIGHTS = "ML_TO_RIGHTS";

    /**
     * use rightaliases. <BR/>
     */
    public static String ML_GOAL = "ML_GOAL";

    /**
     * workspaces name.<BR/>
     */
    public static String ML_WORKSPACES = "ML_WORKSPACES";

    /**
     * text for  " (left field)".<BR/>
     */
    public static String ML_LEFTFIELD = "ML_LEFTFIELD";

    /**
     * text for " (right field)".<BR/>
     */
    public static String ML_RIGHTFIELD = "ML_RIGHTFIELD";

    /**
     * not read text. <BR/>
     */
    public static String ML_NOTREAD = "ML_NOTREAD";

    /**
     * Token for the content of an entry. <BR/>
     */
    public static String ML_CONTENT   = "ML_CONTENT";

    /**
     * please revise text. <BR/>
     */
    public static String ML_ACT_PLEASE_REVISE = "ML_ACT_PLEASE_REVISE";

    /**
     * please complement text. <BR/>
     */
    public static String ML_ACT_PLEASE_COMPLEMENT = "ML_ACT_PLEASE_COMPLEMENT";

    /**
     * please publish text. <BR/>
     */
    public static String ML_ACT_PLEASE_PUBLISH = "ML_ACT_PLEASE_PUBLISH";

    /**
     * please take note of text. <BR/>
     */
    public static String ML_ACT_PLEASE_TAKE_NOTE_OF = "ML_ACT_PLEASE_TAKE_NOTE_OF";

    /**
     * urgent revision text. <BR/>
     */
    public static String ML_ACT_URGENT_REVISION = "ML_ACT_URGENT_REVISION";

    /**
     * please edit text. <BR/>
     */
    public static String ML_ACT_PLEASE_EDIT = "ML_ACT_PLEASE_EDIT";

    /**
     * please correct text. <BR/>
     */
    public static String ML_ACT_PLEASE_CORRECT = "ML_ACT_PLEASE_CORRECT";

    /**
     * please approve text. <BR/>
     */
    public static String ML_ACT_PLEASE_APPROVE = "ML_ACT_PLEASE_APPROVE";

    /**
     * for presentation text. <BR/>
     */
    public static String ML_ACT_FOR_PRESENTATION = "ML_ACT_FOR_PRESENTATION";

    /**
     * chief only
     */
    public static String ML_ACT_CHIEF_ONLY = "ML_ACT_CHIEF_ONLY";

    /**
     * activities array
     */
    public static String[] ML_ACTIVITIES_ARRAY =
    {
        BOTokens.ML_ACT_PLEASE_REVISE,
        BOTokens.ML_ACT_PLEASE_COMPLEMENT,
        BOTokens.ML_ACT_PLEASE_PUBLISH,
        BOTokens.ML_ACT_PLEASE_TAKE_NOTE_OF,
        BOTokens.ML_ACT_URGENT_REVISION,
        BOTokens.ML_ACT_PLEASE_EDIT,
        BOTokens.ML_ACT_PLEASE_CORRECT,
        BOTokens.ML_ACT_PLEASE_APPROVE,
        BOTokens.ML_ACT_FOR_PRESENTATION,
        BOTokens.ML_ACT_CHIEF_ONLY,
    }; // ML_ACTIVITIES_ARRAY


    /**
     * Message type header text: message type INFO. <BR/>
     * Used in appMessages to construct the message type header array.
     */
    public static String ML_MSTHEADER_INFO = "ML_MSTHEADER_INFO";
    /**
     * Message type header text: message type QUESTION. <BR/>
     * Used in appMessages to construct the message type header array.
     */
    public static String ML_MSTHEADER_QUESTION = "ML_MSTHEADER_QUESTION";
    /**
     * Message type header text: message type WARNING. <BR/>
     * Used in appMessages to construct the message type header array.
     */
    public static String ML_MSTHEADER_WARNING = "ML_MSTHEADER_WARNING";
    /**
     * Message type header text: message type ERROR. <BR/>
     * Used in appMessages to construct the message type header array.
     */
    public static String ML_MSTHEADER_ERROR = "ML_MSTHEADER_ERROR";
    /**
     * Message type header text: message type DEBUG. <BR/>
     * Used in appMessages to construct the message type header array.
     */
    public static String ML_MSTHEADER_DEBUG = "ML_MSTHEADER_DEBUG";


    /**
     * Token with the name of the privatsection. <BR/>
     */
    public static String ML_PRIV_SECTION = "ML_PRIV_SECTION";

    /**
     * Explanation for the previous line within form.<BR/>
     * There is no text necessary.
     */
    public static String ML_EXPLANATION = "ML_EXPLANATION";

    /**
     * Token checkedOut. <BR/>
     */
    public static String ML_CHECKEDOUT = "ML_CHECKEDOUT";

    /**
     * Default prefix for name (name = 'Beilage zu' + name)
     * of new attachments . <BR/>
     */
    public static String ML_ATTACHMENT_FOR = "ML_ATTACHMENT_FOR";

    /**
     *  Token for frames for editing a domain. It is set
     *  where the selection has to be done if the connection should
     *  in secure mode or not. <BR/>
     */
    public static String ML_DOMAINSSLREQUIRED = "ML_DOMAINSSLREQUIRED";

    /**
     *  This token is added to a domain-name in the login-form if the domain
     *  requires the secure mode. <BR/>
     */
    public static String ML_DOMAINSSLINDICATOR = "ML_DOMAINSSLINDICATOR";

    /**
     *  The name which is shown if no domain is available.
     */
    public static String ML_NODOMAINNAME = "ML_NODOMAINNAME";

    /**
     *  The token for the views of statecontainer. <BR/>
     */
    public static String ML_NOWORKFLOWSTATE = BOTokens.ML_PST_NONE;

    /**
     * The token for the status of the workflow. <BR/>
     */
    public static String ML_WORKFLOWSTATE = BOTokens.ML_STATE;

    /**
     * The token for the date of the last change at the workflow. <BR/>
     */
    public static String ML_STATECHANGEDATE = BOTokens.ML_CHANGED;

    /**
     * Token for the master version. <BR/>
     */
    public static String ML_MASTERVERSION = "ML_MASTERVERSION";


    // ************************ BUTTONTOKENS *******************************

    // specific buttons:
    /**
     * Button used for editing an object. <BR/>
     */
    public static String ML_BUTTONEDIT = "ML_BUTTONEDIT";

    /**
     * Button used for deleting an object. <BR/>
     */
    public static String ML_BUTTONDELETE = "ML_BUTTONDELETE";

    /**
     * Button used for cutting an object. <BR/>
     */
    public static String ML_BUTTONCUT = "ML_BUTTONCUT";

    /**
     * Button used for copying an object. <BR/>
     */
    public static String ML_BUTTONCOPY = "ML_BUTTONCOPY";

    /**
     * Button used for pasting an object. <BR/>
     */
    public static String ML_BUTTONPASTE = "ML_BUTTONPASTE";

    /**
     * Button used for distributing an object. <BR/>
     */
    public static String ML_BUTTONDISTRIBUTE = "ML_BUTTONDISTRIBUTE";
    
    /**
     * Caption in distribute objects form. <BR/>
     */
    public static String ML_FUNCTIONDISTRIBUTE = BOTokens.ML_BUTTONDISTRIBUTE;

    /**
     * Button used for cleaning an object. <BR/>
     */
    public static String ML_BUTTONCLEAN = "ML_BUTTONCLEAN";

    /**
     * Button used for assigning an object. <BR/>
     */
    public static String ML_BUTTONASSIGN = "ML_BUTTONASSIGN";

    /**
     * Button used for going to an object. <BR/>
     */
    public static String ML_BUTTONGOTO = "ML_BUTTONGOTO";

    /**
     * Button used for searching for an object. <BR/>
     */
    public static String ML_BUTTONSEARCH = "ML_BUTTONSEARCH";

    /**
     * Button used for showing help. <BR/>
     */
    public static String ML_BUTTONHELP = "ML_BUTTONHELP";

    /**
     * Button used for adding an answer to an object. <BR/>
     */
    public static String ML_BUTTONANSWER = "ML_BUTTONANSWER";

    /**
     * Button for submitting a form. <BR/>
     */
    public static String ML_BUTTONOK = "ML_BUTTONOK";

    /**
     * Button for cancelling a form. <BR/>
     */
    public static String ML_BUTTONCANCEL = "ML_BUTTONCANCEL";

    /**
     * Button for logging in. <BR/>
     */
    public static String ML_BUTTONLOGIN = BOTokens.ML_LOGIN;

    /**
     * Button for ordering = adding a product to the shopping cart. <BR/>
     */
    public static String ML_BUTTONSHOPPINGCART = "ML_BUTTONSHOPPINGCART";

    /**
     * Button to display the container content as selectionlist to delete objects. <BR/>
     */
    public static String ML_BUTTONLISTDELETE = "ML_BUTTONLISTDELETE";

    /**
     * Button for make a reference of a marked object. <BR/>
     */
    public static String ML_BUTTONREFERENCE = "ML_BUTTONREFERENCE";

    /**
     * Button to announce user to a term. <BR/>
     */
    public static String ML_BUTTONANNOUNCE = "ML_BUTTONANNOUNCE";

    /**
     * Button to "un" - announce user to a term. <BR/>
     */
    public static String ML_BUTTONUNANNOUNCE = "ML_BUTTONUNANNOUNCE";

    /**
     * Button to change the user's password. <BR/>
     */
    public static String ML_BUTTONCHANGEPASSWORD = BOTokens.ML_CHANGEPASSWORD;

    /**
     * Button to change the user's password. <BR/>
     */
    public static String ML_BUTTONDELETEENTRIES = "ML_BUTTONDELETEENTRIES";

    /**
     * Button for ordering = adding a product to the shopping cart. <BR/>
     */
    public static String ML_BUTTONORDER = "ML_BUTTONORDER";

    /**
     * Button used for editing a rightsObject. <BR/>
     */
    public static String ML_BUTTONRIGHTSEDIT = BOTokens.ML_BUTTONEDIT;

    /**
     * Button used for deleting a rightsObject. <BR/>
     */
    public static String ML_BUTTONRIGHTSDELETE = BOTokens.ML_BUTTONDELETE;

    /**
     * Button used for setting rights to all subObjects. <BR/>
     */
    public static String ML_BUTTONSETRIGHTSREC = "ML_BUTTONSETRIGHTSREC";

    /**
     * Button used for setting rights to all subObjects. <BR/>
     */
    public static String ML_BUTTONCLEANLOG = "ML_BUTTONCLEANLOG";

    /**
     * Button for ordering = adding a product to the shopping cart. <BR/>
     */
    public static String ML_BUTTONSENDORDER = "ML_BUTTONSENDORDER";

    /**
     * Button to announce person (no system user) to a term. <BR/>
     */
    public static String ML_BUTTONANNOUNCE_OTHER = "ML_BUTTONANNOUNCE_OTHER";

    /**
     * Button to show announcements delete list. <BR/>
     */
    public static String ML_BUTTONUNANNOUNCE_LIST = "ML_BUTTONUNANNOUNCE_LIST";

    /**
     * Button to show a print window. <BR/>
     */
    public static String ML_BUTTONPRINT = "ML_BUTTONPRINT";

    /**
     * Button to show a print window. <BR/>
     */
    public static String ML_BUTTONGOTOCONTAINER = "ML_BUTTONGOTOCONTAINER";

    /**
     * Button to show a print window. <BR/>
     */
    public static String ML_BUTTONBACK = "ML_BUTTONBACK";
    
    /**
     * Button used for adding an object. <BR/>
     */
    public static String ML_BUTTONNEW = "ML_BUTTONNEW";

    /**
     * Button to add new Rights. <BR/>
     */
    public static String ML_BUTTONRIGHTSNEW = BOTokens.ML_BUTTONNEW;

    /**
     * Button used for setting rights to all subObjects. <BR/>
     */
    public static String ML_BUTTONLISTDELETERIGHTS =
        BOTokens.ML_BUTTONLISTDELETE;

    /**
     * Button used for import. <BR/>
     */
    public static String ML_BUTTONIMPORT = "ML_BUTTONIMPORT";

    /**
     * Button used to activate the settings, for example the Layout for the userprofile. <BR/>
     */
    public static String ML_BUTTONACTIVATE = "ML_BUTTONACTIVATE";

    /**
     * Button used for adding a topic. <BR/>
     */
    public static String ML_BUTTONTOPICNEW = "ML_BUTTONTOPICNEW";

    /**
     * Button used for export. <BR/>
     */
    public static String ML_BUTTONEXPORT = "ML_BUTTONEXPORT";

    /**
     * Button used for change the orderstate. <BR/>
     */
    public static String ML_BUTTONCHANGEORDERSTATE = BOTokens.ML_STATE;

    /**
     * Button to display the container content as changeform. <BR/>
     */
    public static String ML_BUTTONLISTCHANGE = BOTokens.ML_BUTTONEDIT;

    /**
     * Button to display the container content as changeform for the
     * RightsContainer. <BR/>
     */
    public static String ML_BUTTONASSIGNRIGHTS = BOTokens.ML_BUTTONASSIGN;

    /**
    * Button to display the container content as selectionlist to copy objects.<BR/>
    */
    public static String ML_BUTTONLIST_COPY = "ML_BUTTONLIST_COPY";

    /**
    * Button to display the container content as selectionlist to copy objects.<BR/>
    */
    public static String ML_BUTTONLIST_CUT = "ML_BUTTONLIST_CUT";

    /**
     * Button to check out an object. <BR/>
     */
    public static String ML_BUTTONCHECKOUT = "ML_BUTTONCHECKOUT";

    /**
     * Button to check in an object. <BR/>
     */
    public static String ML_BUTTONCHECKIN = "ML_BUTTONCHECKIN";

    /**
     * Button to check in an object. <BR/>
     */
    public static String ML_BUTTONEDITBEFORECHECKIN =
        BOTokens.ML_BUTTONCHECKIN;

    /**
     * Button to check in an object from a container and editing it first. <BR/>
     */
    public static String ML_BUTTONEDITBEFORECHECKINCONTAINER =
        BOTokens.ML_BUTTONCHECKIN;

    /**
     * Button to check in an object. <BR/>
     */
    public static String ML_BUTTONCHECKOUTCONTAINER =
        BOTokens.ML_BUTTONCHECKOUT;

    /**
     * Button to check in an object. <BR/>
     */
    public static String ML_BUTTONCHECKINCONTAINER =
        BOTokens.ML_BUTTONCHECKIN;

    /**
     * Button to publish an object. <BR/>
     */
    public static String ML_BUTTONPUBLISH = "ML_BUTTONPUBLISH";

    /**
    * Button to forward objects.<BR/>
    */
    public static String ML_BUTTONFORWARD = "ML_BUTTONFORWARD";

    /**
    * Button to start workflow.<BR/>
    */
    public static String ML_BUTTONSTARTWORKFLOW = "ML_BUTTONSTARTWORKFLOW";

    /**
    * Button to finish workflow.<BR/>
    */
    public static String ML_BUTTONSFINISHWORKFLOW = "ML_BUTTONSFINISHWORKFLOW";

    /**
    * Button to abort workflow.<BR/>
    */
    public static String ML_BUTTONSABORTWORKFLOW = "ML_BUTTONSABORTWORKFLOW";

    /**
    * Button to generate a a new translator.<BR/>
    */
    public static String ML_BUTTONGENERATETRANSLATOR = "ML_BUTTONGENERATETRANSLATOR";

    /**
     * Button used to call query for search and create link. <BR/>
     */
    public static String ML_BUTTONSEARCHANDREFERENCE = "ML_BUTTONSEARCHANDREFERENCE";

    /**
     * Goto container of external objects (used in exchange integration). <BR/>
     */
    public static String ML_BUTTONGOTOCONTAINEREXT =
        BOTokens.ML_BUTTONGOTOCONTAINER;

    /**
     * new external object (used for exchange integration). <BR/>
     */
    public static String ML_BUTTONNEWEXT = BOTokens.ML_BUTTONNEW;

    /**
     * new external object (used for exchange integration). <BR/>
     */
    public static final String ML_BUTTONWEBDAVCHECKOUT = "ML_BUTTONWEBDAVCHECKOUT";

    /**
     * new external object (used for exchange integration). <BR/>
     */
    public static final String ML_BUTTONWEBDAVCHECKIN = "ML_BUTTONWEBDAVCHECKIN";

    /**
     * Button used for call query for new and create link. <BR/>
     */
    public static String ML_BUTTONNEWANDREFERENCE = "ML_BUTTONNEWANDREFERENCE";

     /**
     * header for PersonSearchContainer_01 if the container is used to show linked person
     */
    public static String ML_NOTIFICATION_KIND = "ML_NOTIFICATION_KIND";

     /**
     * header for PersonSearchContainer_01 if the container is used to show linked person
     */
    public static String ML_NOTIFICATION_SMSADDITIVE = "ML_NOTIFICATION_SMSADDITIVE";

     /**
     * label for the In-fan notification
     */
    public static String ML_NOTIFICATION_IN_BASKET = "ML_NOTIFICATION_IN_BASKET";

     /**
     * label for the Email notification
     */
    public static String ML_NOTIFICATION_EMAIL = BOTokens.ML_EMAIL;

    /**
     * container for the notification-selections
     */
    public static String[] ML_NOTIFIKATION_SELECTION =
    {
        BOTokens.ML_NOTIFICATION_IN_BASKET,
        BOTokens.ML_NOTIFICATION_EMAIL,
    };

    /**
     * add a weblink to email ?
     */
    public static String ML_ADD_HYPERLINK = "ML_ADD_HYPERLINK";

    /**
     * Button to show to undelete list. <BR/>
     */
    public static String ML_BUTTONSUNDELETE = "ML_BUTTONSUNDELETE";

    /**
     * Button to show to undelete list. <BR/>
     */
    public static String ML_SELHEADERUNDELETE = BOTokens.ML_BUTTONSUNDELETE;

    /**
     * Number of references. <BR/>
     * This property is used as header for list columns in which there is the
     * number of references.
     */
    public static String ML_REFCOUNT = "ML_REFCOUNT";

    /**
     * Used Workspacetemplate for user. <BR/>
     */
    public static String ML_WORKSPACETEMPLATE = "ML_WORKSPACETEMPLATE";

    /**
     * Is this the default element of a list? <BR/>
     */
    public static String ML_ISDEFAULT = "ML_ISDEFAULT";


    /**
     * Token: selected.<BR/>
     */
    public static String ML_SELECTED = "ML_SELECTED";

    /**
     * Token: tooltip for usage of a multiselectionbox.<BR/>
     */
    public static String ML_TIP_USE_MULTISELECTION = "ML_TIP_USE_MULTISELECTION";
    
    /**
     * Token: Standard format for date values. <BR/>
     */
    public static final String ML_STANDARDDATEFORMAT = "ML_STANDARDDATEFORMAT";

    /**
     * Token: Standard format for time values. <BR/>
     */
    public static final String ML_STANDARDTIMEFORMAT = "ML_STANDARDTIMEFORMAT";


//    /********************************************************************
//     * Set the dependent properties. <BR/>
//     * properties from this and other files (initialized Arrays)
//     * that are build of the Attributes of this File
//     */
//    
//    commented out by gw
//    reason: empty function
//    
//    public static void setDependentProperties ()
//    {
        // TODO RB: Remove this part after all parts are migrated to MLI usage
/*
        BOTokens.ML_ACTIVITIES_ARRAY[0] = BOTokens.ML_ACT_PLEASE_REVISE;
        BOTokens.ML_ACTIVITIES_ARRAY[1] = BOTokens.ML_ACT_PLEASE_COMPLEMENT;
        BOTokens.ML_ACTIVITIES_ARRAY[2] = BOTokens.ML_ACT_PLEASE_PUBLISH;
        BOTokens.ML_ACTIVITIES_ARRAY[3] = BOTokens.ML_ACT_PLEASE_TAKE_NOTE_OF;
        BOTokens.ML_ACTIVITIES_ARRAY[4] = BOTokens.ML_ACT_URGENT_REVISION;
        BOTokens.ML_ACTIVITIES_ARRAY[5] = BOTokens.ML_ACT_PLEASE_EDIT;
        BOTokens.ML_ACTIVITIES_ARRAY[6] = BOTokens.ML_ACT_PLEASE_CORRECT;
        BOTokens.ML_ACTIVITIES_ARRAY[7] = BOTokens.ML_ACT_PLEASE_APPROVE;
        BOTokens.ML_ACTIVITIES_ARRAY[8] = BOTokens.ML_ACT_FOR_PRESENTATION;
        BOTokens.ML_ACTIVITIES_ARRAY[9] = BOTokens.ML_ACT_CHIEF_ONLY;

        BOTokens.ML_NOTIFIKATION_SELECTION[0] = BOTokens.ML_NOTIFICATION_IN_BASKET;
        BOTokens.ML_NOTIFIKATION_SELECTION[1] = BOTokens.ML_NOTIFICATION_EMAIL;


        BOListConstants.LST_HEADINGS[0] = BOTokens.ML_NAME;
        BOListConstants.LST_HEADINGS[1] = BOTokens.ML_TYPE;
        BOListConstants.LST_HEADINGS[2] = BOTokens.ML_OWNER;
        BOListConstants.LST_HEADINGS[3] = BOTokens.ML_CHANGED;

        BOListConstants.LST_HEADINGS_REDUCED[0] = BOListConstants.LST_HEADINGS[0];

        BOListConstants.LST_HEADINGS_SENTOBJECTCONTAINER[0] = BOTokens.ML_SUBJECT;
        BOListConstants.LST_HEADINGS_SENTOBJECTCONTAINER[1] = BOTokens.ML_ACTIVITIES;
        BOListConstants.LST_HEADINGS_SENTOBJECTCONTAINER[2] = BOTokens.ML_SENTDATE;

        BOListConstants.LST_HEADINGS_ATTACHMENTCONTAINER[0] = BOTokens.ML_NAME;
        BOListConstants.LST_HEADINGS_ATTACHMENTCONTAINER[1] = BOTokens.ML_ATTACHMENTTYPE;
        BOListConstants.LST_HEADINGS_ATTACHMENTCONTAINER[2] = BOTokens.ML_SOURCENAME;
        BOListConstants.LST_HEADINGS_ATTACHMENTCONTAINER[3] = BOTokens.ML_FILESIZE;
        BOListConstants.LST_HEADINGS_ATTACHMENTCONTAINER[4] = BOTokens.ML_CREATIONDATE;

        BOListConstants.LST_HEADINGS_ATTACHMENTCONTAINERREDUCED[0] = BOTokens.ML_NAME;
        BOListConstants.LST_HEADINGS_ATTACHMENTCONTAINERREDUCED[1] = BOTokens.ML_SOURCENAME;
        BOListConstants.LST_HEADINGS_ATTACHMENTCONTAINERREDUCED[2] = BOTokens.ML_FILESIZE;

        BOListConstants.LST_HEADINGS_NEWSCONTAINERREDUCED[0] = BOListConstants.LST_HEADINGS[0];
        BOListConstants.LST_HEADINGS_NEWSCONTAINERREDUCED[1] = BOListConstants.LST_HEADINGS[3];

        BOListConstants.LST_HEADINGS_HELPCONTAINER[0] = BOTokens.ML_NAME;
        BOListConstants.LST_HEADINGS_HELPCONTAINER[1] = BOTokens.ML_GOAL;
        BOListConstants.LST_HEADINGS_HELPCONTAINER[2] = BOTokens.ML_TYPE;
        BOListConstants.LST_HEADINGS_HELPCONTAINER[3] = BOTokens.ML_OWNER;
        BOListConstants.LST_HEADINGS_HELPCONTAINER[4] = BOTokens.ML_CHANGED;

        BOListConstants.LST_HEADINGS_HELPCONTAINERREDUCED[0] = BOListConstants.LST_HEADINGS_HELPCONTAINER[0];
        BOListConstants.LST_HEADINGS_HELPCONTAINERREDUCED[1] = BOListConstants.LST_HEADINGS_HELPCONTAINER[1];

        BOListConstants.LST_HEADINGS_INBOX[0] = BOTokens.ML_SUBJECT;
        BOListConstants.LST_HEADINGS_INBOX[1] = BOTokens.ML_DISTRIBUTENAME;
        BOListConstants.LST_HEADINGS_INBOX[2] = BOTokens.ML_ACTIVITIES;
        BOListConstants.LST_HEADINGS_INBOX[3] = BOTokens.ML_RECEIVED;
        BOListConstants.LST_HEADINGS_INBOX[4] = BOTokens.ML_SENDER;

        BOListConstants.LST_HEADINGS_LOGCONTAINER[0] = BOTokens.ML_NAMEOFOBJECT;
        BOListConstants.LST_HEADINGS_LOGCONTAINER[1] = BOTokens.ML_AKTEURNAME;
        BOListConstants.LST_HEADINGS_LOGCONTAINER[2] = BOTokens.ML_ACTION;
        BOListConstants.LST_HEADINGS_LOGCONTAINER[3] = BOTokens.ML_ACTIONDATE;

        BOListConstants.LST_HEADINGS_RECIPIENT[0] = BOTokens.ML_RECIPIENTENNAME;
        BOListConstants.LST_HEADINGS_RECIPIENT[1] = BOTokens.ML_OBJECTNAME;
        BOListConstants.LST_HEADINGS_RECIPIENT[2] = BOTokens.ML_SENTDATE;
        BOListConstants.LST_HEADINGS_RECIPIENT[3] = BOTokens.ML_READDATE;

        BOListConstants.LST_HEADINGS_LOGCONTAINER_ENTRY[0] = BOTokens.ML_FIELDNAME;
        BOListConstants.LST_HEADINGS_LOGCONTAINER_ENTRY[1] = BOTokens.ML_OLD_VALUE;
        BOListConstants.LST_HEADINGS_LOGCONTAINER_ENTRY[2] = BOTokens.ML_VALUE;

        BOListConstants.LST_HEADINGS_ORDERCONTAINER[0] = BOTokens.ML_NAME;
        BOListConstants.LST_HEADINGS_ORDERCONTAINER[1] = BOTokens.ML_STATE;
        BOListConstants.LST_HEADINGS_ORDERCONTAINER[2] = BOTokens.ML_CHANGED;

        AppMessages.MST_HEADERS[0] = BOTokens.ML_MSTHEADER_INFO;
        AppMessages.MST_HEADERS[1] = BOTokens.ML_MSTHEADER_QUESTION;
        AppMessages.MST_HEADERS[2] = BOTokens.ML_MSTHEADER_WARNING;
        AppMessages.MST_HEADERS[3] = BOTokens.ML_MSTHEADER_ERROR;
        AppMessages.MST_HEADERS[4] = BOTokens.ML_MSTHEADER_DEBUG;



        Buttons.BTN_NAMES[1] = BOTokens.ML_BUTTONEDIT;
        Buttons.BTN_NAMES[2] = BOTokens.ML_BUTTONDELETE;
        Buttons.BTN_NAMES[3] = BOTokens.ML_BUTTONCUT;
        Buttons.BTN_NAMES[4] = BOTokens.ML_BUTTONCOPY;
        Buttons.BTN_NAMES[5] = BOTokens.ML_BUTTONPASTE;
        Buttons.BTN_NAMES[6] = BOTokens.ML_BUTTONDISTRIBUTE;
        Buttons.BTN_NAMES[7] = BOTokens.ML_BUTTONCLEAN;
        Buttons.BTN_NAMES[8] = BOTokens.ML_BUTTONASSIGN;
        Buttons.BTN_NAMES[9] = BOTokens.ML_BUTTONGOTO;
        Buttons.BTN_NAMES[10] = BOTokens.ML_BUTTONSEARCH;
        Buttons.BTN_NAMES[11] = BOTokens.ML_BUTTONHELP;
        Buttons.BTN_NAMES[12] = BOTokens.ML_BUTTONANSWER;
        Buttons.BTN_NAMES[13] = BOTokens.ML_BUTTONOK;
        Buttons.BTN_NAMES[14] = BOTokens.ML_BUTTONCANCEL;
        Buttons.BTN_NAMES[15] = BOTokens.ML_BUTTONLOGIN;
        Buttons.BTN_NAMES[16] = BOTokens.ML_BUTTONSHOPPINGCART;
        Buttons.BTN_NAMES[17] = BOTokens.ML_BUTTONLISTDELETE;
        Buttons.BTN_NAMES[18] = BOTokens.ML_BUTTONREFERENCE;
        Buttons.BTN_NAMES[19] = BOTokens.ML_BUTTONANNOUNCE;
        Buttons.BTN_NAMES[20] = BOTokens.ML_BUTTONUNANNOUNCE;
        Buttons.BTN_NAMES[21] = BOTokens.ML_BUTTONCHANGEPASSWORD;
        Buttons.BTN_NAMES[22] = BOTokens.ML_BUTTONDELETEENTRIES;
        Buttons.BTN_NAMES[23] = BOTokens.ML_BUTTONORDER;
        Buttons.BTN_NAMES[24] = BOTokens.ML_BUTTONRIGHTSEDIT;
        Buttons.BTN_NAMES[25] = BOTokens.ML_BUTTONRIGHTSDELETE;
        Buttons.BTN_NAMES[26] = BOTokens.ML_BUTTONSETRIGHTSREC;
        Buttons.BTN_NAMES[27] = BOTokens.ML_BUTTONCLEANLOG;
        Buttons.BTN_NAMES[28] = BOTokens.ML_BUTTONSENDORDER;
        Buttons.BTN_NAMES[29] = BOTokens.ML_BUTTONANNOUNCE_OTHER;
        Buttons.BTN_NAMES[30] = BOTokens.ML_BUTTONUNANNOUNCE_LIST;
        Buttons.BTN_NAMES[31] = BOTokens.ML_BUTTONPRINT;
        Buttons.BTN_NAMES[32] = BOTokens.ML_BUTTONGOTOCONTAINER;
        Buttons.BTN_NAMES[33] = BOTokens.ML_BUTTONBACK;
        Buttons.BTN_NAMES[34] = BOTokens.ML_BUTTONRIGHTSNEW;
        Buttons.BTN_NAMES[35] = BOTokens.ML_BUTTONLISTDELETERIGHTS;
        Buttons.BTN_NAMES[36] = BOTokens.ML_BUTTONIMPORT;
        Buttons.BTN_NAMES[37] = BOTokens.ML_BUTTONACTIVATE;
        Buttons.BTN_NAMES[38] = BOTokens.ML_BUTTONTOPICNEW;
        Buttons.BTN_NAMES[39] = BOTokens.ML_BUTTONEXPORT;
        Buttons.BTN_NAMES[40] = BOTokens.ML_BUTTONCHANGEORDERSTATE;
        Buttons.BTN_NAMES[41] = BOTokens.ML_BUTTONNEW;
        Buttons.BTN_NAMES[42] = BOTokens.ML_BUTTONLISTCHANGE;
        Buttons.BTN_NAMES[43] = BOTokens.ML_BUTTONASSIGNRIGHTS;
        Buttons.BTN_NAMES[44] = BOTokens.ML_BUTTONLIST_COPY;
        Buttons.BTN_NAMES[45] = BOTokens.ML_BUTTONLIST_CUT;
        Buttons.BTN_NAMES[46] = BOTokens.ML_BUTTONCHECKOUT;
        Buttons.BTN_NAMES[47] = BOTokens.ML_BUTTONCHECKIN;
        Buttons.BTN_NAMES[48] = BOTokens.ML_BUTTONFORWARD;
        Buttons.BTN_NAMES[49] = BOTokens.ML_BUTTONSTARTWORKFLOW;
        Buttons.BTN_NAMES[50] = BOTokens.ML_BUTTONSFINISHWORKFLOW;
        Buttons.BTN_NAMES[51] = BOTokens.ML_BUTTONSABORTWORKFLOW;
        Buttons.BTN_NAMES[52] = BOTokens.ML_BUTTONGENERATETRANSLATOR;
        Buttons.BTN_NAMES[53] = BOTokens.ML_BUTTONEDITBEFORECHECKIN;
        Buttons.BTN_NAMES[54] = BOTokens.ML_BUTTONEDITBEFORECHECKINCONTAINER;
        Buttons.BTN_NAMES[55] = BOTokens.ML_BUTTONEDITBEFORECHECKINCONTAINER;
        Buttons.BTN_NAMES[56] = BOTokens.ML_BUTTONCHECKOUTCONTAINER;
        Buttons.BTN_NAMES[57] = BOTokens.ML_BUTTONSEARCHANDREFERENCE;
        Buttons.BTN_NAMES[58] = BOTokens.ML_BUTTONNEWANDREFERENCE;
        Buttons.BTN_NAMES[59] = BOTokens.ML_BUTTONPUBLISH;
        Buttons.BTN_NAMES[60] = BOTokens.ML_BUTTONGOTOCONTAINEREXT;
        Buttons.BTN_NAMES[61] = BOTokens.ML_BUTTONNEWEXT;
        Buttons.BTN_NAMES[64] = BOTokens.ML_BUTTONCHECKOUT;
        Buttons.BTN_NAMES[65] = BOTokens.ML_BUTTONCHECKIN;
*/
//    } // setDEpendentProperties

} // class BOTokens
