/*
 * Class: AppArguments.java
 */

// package:
package ibs.bo;

// imports:


/******************************************************************************
 * Arguments for ibs applications. <BR/>
 * This abstract class contains all Arguments which are necessary to deal with
 * the classes delivered within this package. <P>
 *
 * @version     $Id: BOArguments.java,v 1.41 2011/05/26 08:28:41 rburgermann Exp $
 *
 * @author      Klaus Reimüller (KR), 980429
 ******************************************************************************
 */
public abstract class BOArguments extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: BOArguments.java,v 1.41 2011/05/26 08:28:41 rburgermann Exp $";


    /**
     * No argument. <BR/>
     */
    public static final String ARG_NOARG = "no";


    // arguments for url:
    /**
     * argumentname for the type of the menu. <BR/>
     */
    public static final String ARG_MENU = "menu";

    /**
     * Open nodes within menu. <BR/>
     */
    public static final String ARG_OPENNODES = "opennodes";

    /**
     * Random number/string concatenated to URL. <BR/>
     */
    public static final String ARG_RANDOM       = "r";
    /**
     * Argument function in URL. <BR/>
     */
    public static final String ARG_FUNCTION     = "fct";

    /**
     * Argument event in URL. <BR/>
     */
    public static final String ARG_EVENT     = "evt";

    /**
     * Argument oid in URL. <BR/>
     */
    public static final String ARG_OID          = "oid";

    /**
     * Argument refoid in URL. <BR/>
     */
    public static final String ARG_REFOID          = "refoid";

    /**
     * Argument containerId in URL. <BR/>
     */
    public static final String ARG_CONTAINERID  = "cid";

    /**
     * Argument state in URL. <BR/>
     */
    public static final String ARG_STATE        = "st";

    /**
     * Argument name in URL. <BR/>
     */
    public static final String ARG_NAME         = "nomen";

    /**
     * Argument owner in URL. <BR/>
     */
    public static final String ARG_OWNER        = "owner";

    /**
     * Argument type in URL. <BR/>
     */
    public static final String ARG_TYPE         = "type";

    /**
     * Argument description in URL. <BR/>
     */
    public static final String ARG_DESCRIPTION  = "desc";

    /**
     * Argument for checkbox in listdelete-form. <BR/>
     */
    public static final String ARG_DELLIST      = "dlst";

    /**
     * Argument for listchange-form. <BR/>
     */
    public static final String ARG_CHANGELIST   = "chnglst";

    /**
     * Reference of a file. <BR/>
     */
    public static final String ARG_FILE = "di";

    /**
     * Reference of a file of type xslt. <BR/>
     * This file is used to translate an old xml-template to an new one.
     */
    public static final String ARG_TRANSLATOR = "tr";

    /**
     * Reference of a HTML Page. <BR/>
     */
    public static final String ARG_HYPERLINK = "hl";
    /**
     *  Switch AttachmentType. <BR/>
     */
    public static final String ARG_SELECT = "sw";

    /**
     *  Switch Master or SupMastertype. <BR/>
     */
    public static final String ARG_MASTER = "msd";

    /**
     * User and time when an object was created. <BR/>
     */
    public static final String ARG_CREATED = "crcrd";

    /**
     * Creator of an object. <BR/>
     */
    public static final String ARG_CREATOR = "cr";

    /**
     * Time when an object was created. <BR/>
     */
    public static final String ARG_CREATIONDATE = "crd";

    /**
     * User and time when an object was last changed. <BR/>
     */
    public static final String ARG_CHANGED = "chchd";

    /**
     * Changer of an object. <BR/>
     */
    public static final String ARG_CHANGER = "ch";

    /**
     * Time when an object was changed. <BR/>
     */
    public static final String ARG_LASTCHANGED = "chd";

    /**
     * The master file  is the most important file to a business object document. <BR/>
     */
    public static final String ARG_ISMASTER = "mf";

    /**
     * The master file  is the most important file to a business object document. <BR/>
     */
    public static final String ARG_MASTERDEFINED = "mdef";

    /**
     * The size of the file. <BR/>
     */
    public static final String ARG_FILESIZE = "fz";

    /**
     * Argument valid until in URL. <BR/>
     */
    public static final String ARG_VALIDUNTIL   = "vu";

    /**
     * Argument readDate in URL. <BR/>
     */
    public static final String ARG_READDATE   = "readd";

    /**
     * Argument tab id in URL. <BR/>
     */
    public static final String ARG_TABID        = "tab";

    /**
     * Name of a tab. <BR/>
     */
    public static final String ARG_TABNAME      = "tabn";

    /**
     * Code of a tab. <BR/>
     */
    public static final String ARG_TABCODE      = "tabc";

    /**
     * Tells if the tab bar shall be displayed. <BR/>
     * This argument must have the values "1" (= true) or "0" (= false)!
     */
    public static final String ARG_SHOWTABBAR = "tabs";

    /**
     * Argument preselected element in URL. <BR/>
     * This argument can be used for tab bars, lists, etc.
     */
    public static final String ARG_PRESELECTED  = "sel";

    /**
     * Argument which tells if an object shall be displayed. <BR/>
     * This argument must have the values "1" (= true) or "0" (= false)!
     */
    public static final String ARG_SHOWOBJECT   = "sho";

    /**
     * Argument orderBy in URL. <BR/>
     */
    public static final String ARG_ORDERBY      = "oBy";

    /**
     * Argument orderHow in URL. <BR/>
     */
    public static final String ARG_ORDERHOW     = "oHow";

    /**
     * Argument groups in distributing object form. <BR/>
     */
    public static final String ARG_GROUPS       = "gr";

    /**
     * Argument groups filter in distributing object form. <BR/>
     */
    public static final String ARG_GROUPSFILTER = "grf";

    /**
     * Argument setting groups filter in distributing object form. <BR/>
     */
    public static final String ARG_SETGROUPSFILTER = "sgrf";

    /**
     * Argument setting groups in distributing object form. <BR/>
     */
    public static final String ARG_SETGROUPS    = "sgr";

    /**
     * Argument users in distributing object form. <BR/>
     */
    public static final String ARG_USERS        = "us";

    /**
     * Argument users filter in distributing object form. <BR/>
     */
    public static final String ARG_USERSFILTER  = "usf";

    /**
     * Argument setting users filter in distributing object form. <BR/>
     */
    public static final String ARG_SETUSERSFILTER = "susf";

    /**
     * Argument add users in distributing object form. <BR/>
     */
    public static final String ARG_ADD          = "add";

    /**
     * Argument remove users in distributing object form. <BR/>
     */
    public static final String ARG_REMOVE       = "rm";

    /**
     * Argument receivers in distributing object form. <BR/>
     */
    public static final String ARG_RECEIVERS    = "rcv";

    /**
     * Argument active groups filter in distributing object form. <BR/>
     */
    public static final String ARG_ACTIVEGROUPSFILTER = "acgf";

    /**
     * Argument active users filter in distributing object form. <BR/>
     */
    public static final String ARG_ACTIVEUSERSFILTER  = "acuf";

    /**
     * Argument active group in distributing object form. <BR/>
     */
    public static final String ARG_ACTIVEGROUP  = "acg";

    /**
     * Argument linkedObjectId in URL. <BR/>
     */
    public static final String ARG_LINKEDOBJECTID  = "lid";

    /**
     * Argument initform: used to indicated if form has just been initialized. <BR/>
     */
    public static final String ARG_INITFORM  = "init";

    /**
     * Argument used to paste as link or duplicate. <BR/>
     */
    public static final String ARG_PASTEKIND  = "paste";

    /**
     * Argument used to read the path of a attachment. <BR/>
     */
    public static final String ARG_PATH  = "path";

    /**
     * Argument used to read the path of a attachment. <BR/>
     */
    public static final String ARG_PATH_ALT  = "pathAlt";

    /**
     * Filter the content of the SendObjectContainer and use days to filter. <BR/>
     */
    public static final String ARG_DAYSSELECTED  = "dsel";

    /**
     * Argument used to  as link or dublicate. <BR/>
     */
    public static final String ARG_NUMBEROFDAYS  = "numofda";

    /**
     * Filter the content of the SendObjectContainer and use "From TO" to filter. <BR/>
     */
    public static final String ARG_FROMTOSELECTED  = "FroTo";

    /**
     * Argument to read the selected startshowdate. <BR/>
     */
    public static final String ARG_STARTSHOWDATE  = "startsho";

    /**
     * Argument to read the selected endshowdate. <BR/>
     */
    public static final String ARG_ENDSHOWDATE  = "endshow";

    /**
     * Filter the content of the SendObjectContainer and use the type of BO to filter. <BR/>
     */
    public static final String ARG_TYPESELECTED  = "tysel";

    /**
     * Argument to read the selected Type of BusinessObject. <BR/>
     */
    public static final String ARG_SELECTEDTYPE  = "selType";

    /**
     * Argument to read the selected Type of BusinessObject. <BR/>
     */
    public static final String ARG_RECIPIENTSELECTED  = "recsel";

    /**
     * Argument to read the selected Type of BusinessObject. <BR/>
     */
    public static final String ARG_RECIPIENTNAME  = "recnam";

    /**
     * Argument to read the selected Type of BusinessObject. <BR/>
     */
    public static final String ARG_DISTRIBUTENAME  = "disnam";

    /**
     * Argument to read the selected Type of BusinessObject. <BR/>
     */
    public static final String ARG_SENTDATE  = "sendat";

    /**
     * range extension: indicates the range field. <BR/>
     */
    public static final String ARG_RANGE_EXTENSION  = "_R";

    /**
     * date range extension: indicates the date field of a datetime. <BR/>
     */
    public static final String ARG_DATE_EXTENSION  = "_d";

    /**
     * time extension: indicates the time field of a datetime. <BR/>
     */
    public static final String ARG_TIME_EXTENSION  = "_t";

    /**
     * match extension: indicates the match type field. <BR/>
     */
    public static final String ARG_MATCHTYPE_EXTENSION  = "_M";

    /**
     * oid extension: indicates the oid field. <BR/>
     */
    public static final String ARG_OID_EXTENSION  = "_OID";

    /**
     * oid extension: indicates the name field. <BR/>
     */
    public static final String ARG_NAME_EXTENSION  = "_NAME";

    /**
     * type of a typespecific search. <BR/>
     */
    public static final String ARG_SEARCHTYPE  = "sty";

    /**
     * flag for extended search form. <BR/>
     */
    public static final String ARG_EXTENDED  = "exs";

    /**
     * Name of user. <BR/>
     */
    public static final String ARG_USERNAME = "usr";

    /**
     * Password of user. <BR/>
     */
    public static final String ARG_PASSWORD = "pw";

    /**
     * Password validation of user. <BR/>
     */
    public static final String ARG_PASSWORD2 = "pw2";
    /**
     * Password validation of user. <BR/>
     */

    public static final String ARG_PARTOF = "pof";
    /**
     * Password validation of user. <BR/>
     */
    public static final String ARG_KINDOFLOG = "kol";

    /**
     * Domain of user. <BR/>
     */
    public static final String ARG_DOMAIN = "dom";

    /**
     * The id of the scheme of a domain. <BR/>
     */
    public static final String ARG_DOMAINSCHEME = "ds";

    /**
     * The home page path of a domain, i.e. the path where the domain can be
     * found via url. <BR/>
     */
    public static final String ARG_HOMEPAGEPATH = "hpp";

    /**
     * This domain scheme property tells whether a domain with this scheme
     * has a catalog management. <BR/>
     */
    public static final String ARG_HASCATALOGMMT = "ki";

    /**
     * This domain scheme property tells whether a domain with this scheme
     * has a data interchange component. <BR/>
     */
    public static final String ARG_HASDATAINTERCHANGE = "din";

    /**
     * This domain scheme property contians the procedure which is used
     * to create the workspace of an user within a domain having this
     * scheme. <BR/>
     */
    public static final String ARG_WORKSPACEPROC = "pfa";

    /**
     * The users old password. <BR/>
     */
    public static final String ARG_OLDPASSWORD = "oldPassword";

    /**
     * The users new password. <BR/>
     */
    public static final String ARG_NEWPASSWORD = "newPassword";

    /**
     * The confirmation of the new password. <BR/>
     */
    public static final String ARG_CONFIRMPASSWORD = "confirmPassword";

    /**
     * Representation form. <BR/>
     */
    public static final String ARG_REPRESENTATION   = "rep";

    /**
     * Property representing the id of the properties of a type. <BR/>
     */
    public static final String ARG_IDPROPERTY = "idp";
    /**
     * Id of the superior type. <BR/>
     */
    public static final String ARG_SUPERTYPEID = "supt";

    /**
     * Code of a type, type version, method, etc. <BR/>
     */
    public static final String ARG_CODE = "cod";

    /**
     * Id of the actual version. <BR/>
     */
    public static final String ARG_ACTVERSIONID = "avid";

    /**
     * Sequence number of the actual version. <BR/>
     */
    public static final String ARG_ACTVERSIONSEQ = "avsq";

    /**
     * The activities to be taken with a distributed object. <BR/>
     */
    public static final String ARG_ACTIVITIES = "act";

    /**
     * The subject under which an object is distributed. <BR/>
     */
    public static final String ARG_SUBJECT = "sub";

/**********************
 * HACK: This description ist not understandable for human being.
 */
    /**
     * Reorder. indicates a list reordering triggerd via a heading. <BR/>
     */
    public static final String ARG_REORDER = "rord";

    /**
     * Time limit for newslist. <BR/>
     */
    public static final String ARG_NEWSTIMELIMIT = "upntl";

    /**
     * Flag to show only unread messages in newslist. <BR/>
     */
    public static final String ARG_NEWSSHOWONLYUNREAD = "upnsor";

    /**
     * Flag to use time limit in outbox. <BR/>
     */
    public static final String ARG_OUTBOXUSETIMELIMIT = "upoutl";

    /**
     * Time limit in outbox. <BR/>
     */
    public static final String ARG_OUTBOXTIMELIMIT = "upotl";

    /**
     * Flag to use time frame in outbox. <BR/>
     */
    public static final String ARG_OUTBOXUSETIMEFRAME = "upoutf";

    /**
     * Time frame in outbox. <BR/>
     */
    public static final String ARG_OUTBOXTIMEFRAME = "upotf";

    /**
     * Argument name of the max result field. <BR/>
     */
    public static final String ARG_OUTBOXFILTER = "upof";

    /**
     * Flag to show complete object attributes. <BR/>
     */
    public static final String ARG_SHOWEXTENDEDATTRIBUTES = "upsea";

    /**
     * Flag to save profile at login. <BR/>
     */
    public static final String ARG_SAVEPROFILE = "sp";

    /**
     * Flag to show files in a separate window. <BR/>
     */
    public static final String ARG_SHOWFILESINWINDOWS = "upsfiw";

    /**
     * Date of last login. <BR/>
     */
    public static final String ARG_LASTLOGIN = "upll";

    /**
     * Freeze a distributed object to ensure that nobody can change it. <BR/>
     */
    public static final String ARG_FREEZE = "froz";

    /**
     * Max. entries beeing displayed in a list. <BR/>
     */
    public static final String ARG_DISPLAYMAXENTRIES = "dme";

    /**
     * Flag to distinguish info/content view for buttons being displayed. <BR/>
     */
    public static final String ARG_ISCONTENTVIEW = "icv";

    /**
     * Name of a file. <BR/>
     */
    public static final String ARG_FILENAME = "fina";

    /**
     * Name of the field of a filename. <BR/>
     */
    public static final String ARG_FILENAMEFIELD = "fifi";

    /**
     * Argument name of the max result field. <BR/>
     */
    public static final String ARG_MAXRESULT = "maxResult";

    /**
     * Argument name for the history function. <BR/>
     */
    public static final String ARG_GOBACK = "bck";

    /**
     * Argument name for the extended function. <BR/>
     */
    public static final String ARG_EXTENDED_FUNCTION = "extfct";

    /**
     * Argument for the hidden field of canceled action. <BR/>
     */
    public static final String ARG_CANCELED = "ccl";

    /**
     * Argument for the create hidden field in a change form. <BR/>
     */
    public static final String ARG_IFCREATE = "ifc";

    /**
     * Argument name of the ftp proxy field which may be used for connecting
     * to the internet via FTP. <BR/>
     */
    public static final String ARG_FTPPROXY = "ftpp";

    /**
     * Argument name of the ftp proxy bypass field which may be used for
     * explicitly connecting directly to some sites via FTP. <BR/>
     */
    public static final String ARG_FTPPROXYBYPASS = "ftppb";

    /**
     * Don't show the state within the button bar. <BR/>
     */
    public static final String ARG_BUTTONSDISABLESTATE = "bbds";

    /**
     * Argument for an Object. <BR/>
     */
    public static final String ARG_OBJECT = "obj";

    /**
     * Argument, which tells if the actual view of an object is shown within
     * a frameset. <BR/>
     */
    public static final String ARG_ISFRAMESET = "frs";

    /**
     * Argument, which holds the oid of the valid layout. <BR/>
     */
    public static final String ARG_LAYOUTID = "lyId";

    /**
     * Argument, which holds the oid of the valid locale. <BR/>
     */
    public static final String ARG_LOCALEID = "locId";
    
    /**
     * Argument, which holds the name of the valid layout. <BR/>
     */
    public static final String ARG_LAYOUT = "lay";

    /**
     * Argument, which holds the name of the valid locale. <BR/>
     */
    public static final String ARG_LOCALE = "loc";
    
    /**
     * Argument, which holds the name of the valid layout. <BR/>
     */
    public static final String ARG_HIERARCHY = "hier";

    /**
     * Argument which holds the oid of the calling object. <BR/>
     * Example: add link from person to user
     * - user is calling obj    =>  oid in this.ARG_CALLINGOID
     * - personSearchContainer is object to show  => oid in this.ARG_OID
     */
    public static final String ARG_CALLINGOID = "calloid";

    /**
     * Argument, wich controls if the linked object or the matching objects
     * shall be shown in a searchContainer (see PersonSearchContainer). <BR/>
     */
    public static final String ARG_SHOWLINK = "shwlnk";

    /**
     * Field to update after selection in searchContainer. <BR/>
     */
    public static final String ARG_FIELDNAME = "fieldname";

/********************
 * HACK: No argument description.
 */
    /**
     * ?????. <BR/>
     */
    public static final String ARG_ORDERSTATE = "ordstate";

    /**
     * Argument if rights should be set recursive. <BR/>
     */
    public static final String ARG_RECURSIVE  = "rec";

    /**
     * Argument for name of a sender. <BR/>
     */
    public static final String ARG_SENDER = "sender";

    /**
     * Argument for Id of the person or group. <BR/>
     */
    public static final String ARG_RPERSONID = "rpid";

    /**
     * Argument for oid of the object for whom the right is set. <BR/>
     */
    public static final String ARG_ROID = "roid";

    /**
     * Argument for displaying objekt in newcontainer. <BR/>
     */
    public static final String ARG_INNEWS = "innews";

    /**
     * Argument to indicate if user works with rightaliases or with all rights. <BR/>
     */
    public static final String ARG_USERIGHTALIASES = "ralia";

    /**
     * Argument for searchcontents. <BR/>
     */
    public static final String ARG_SEARCHCONTENT = "sc";

    /**
     * Argument for help-URL. <BR/>
     */
    public static final String ARG_HELPURL = "hu";

    /**
     * Argument for goal. <BR/>
     */
    public static final String ARG_GOAL = "gl";

    /**
     * Argument for an oid of an object to be exported. <BR/>
     */
    public static final String ARG_EXPORTOID = "exo";

    /**
     * Argument if the frame shall be loaded for the weblink. <BR/>
     */
    public static final String ARG_LOADFRAME = "frame";

    /**
     * Argument for the Path in the webLink. <BR/>
     */
    public static final String ARG_OPATH = "opath";

    /**
     * Argument if the webLink is active. <BR/>
     */
    public static final String ARG_WEBLINK = "webl";

    /**
     * Argument for text that should be found in simple search. <BR/>
     */
    public static final String ARG_SEARCHVALUE = "sval";

    /**
     * Argument which defines where the search should be performed. <BR/>
     * <CODE>true</CODE>: the search has to be performed in whole m2. <BR/>
     * <CODE>false</CODE>: the search has to be performed only in a container
     * and its contents. <BR/>
     */
    public static final String ARG_SEARCHGLOBAL = "sgl";

    /**
     * Argument for oid of the container where the search should start if
     * search is not done globally (i.e. within the container). <BR/>
     */
    public static final String ARG_SEARCHROOTCONTAINERID = "srcid";

    /**
     * Argument of a domain if ssl is required or not. <BR/>
     */
    public static final String ARG_DOMAINSSLREQUIRED = "ssl";

    /**
     * Argument to get the old session back if changing
     * to the insecure mode. <BR/>
     */
    public static final String ARG_SESSIONINFOID = "sid";

    /**
     * Argument to enable the trace server for the actual session. <BR/>
     * The value is <CODE>no</CODE> to disable the trace server or
     * <CODE>session</CODE> to enable the trace server for the actual session
     * or <CODE>application</CODE> to enable the trace server for the whole
     * application context.
     */
    public static final String ARG_ENABLETRACESERVER = "traceserver";

    /**
     * Argument to enable the trace server for the actual session. <BR/>
     * The value is <CODE>no</CODE> to disable the trace server or
     * <CODE>session</CODE> to enable the trace server for the actual session
     * or <CODE>application</CODE> to enable the trace server for the whole
     * application context.
     */
    public static final String ARG_NOTIFICATIONKIND = "notifkind";

    /**
     * Argument to enable the trace server for the actual session. <BR/>
     * The value is <CODE>no</CODE> to disable the trace server or
     * <CODE>session</CODE> to enable the trace server for the actual session
     * or <CODE>application</CODE> to enable the trace server for the whole
     * application context.
     */
    public static final String ARG_SMSACTIV = "smsactiv";

    /**
     * Argument to enable the trace server for the actual session. <BR/>
     * The value is <CODE>no</CODE> to disable the trace server or
     * <CODE>session</CODE> to enable the trace server for the actual session
     * or <CODE>application</CODE> to enable the trace server for the whole
     * application context.
     */
    public static final String ARG_ADDWEBLINK = "addweblink";

    /**
     * Argument for show a download frame to the user. <BR/>
     */
    public static final String ARG_DOWNLOADTRANSLATOR = "dwnldtr";

    /**
     * Step within check reference dialog. <BR/>
     * This argument is used to determine which function shall be executed for
     * subsequent calls of the checkReference function/dialog.
     */
    public static final String ARG_REFSTEP = "refstep";

    /**
     * Argument for a prefix of a virtual object in a selection list. <BR/>
     */
    public static final String ARG_VIRTUALOBJECTPREFIX = "v_";

    /**
     * Argument for a virtual object in a selection list. <BR/>
     */
    public static final String ARG_VIRTUALOBJECT =
        BOArguments.ARG_VIRTUALOBJECTPREFIX + BOArguments.ARG_DELLIST;

    /**
     * Argument for oid of workspacetemplate selectionlsit within user object.
     * <BR/>
     */
    public static final String ARG_WORKSPACETEMPLATE = "wst";

    /**
     * Argument change function in URL. <BR/>
     */
    public static final String ARG_CHANGEFUNCTION = "changefct";

    /**
     * Argument typecode in URL. <BR/>
     */
    public static final String ARG_TYPECODE = "typecode";

    /**
     * The default element out of a list of elements. <BR/>
     */
    public static final String ARG_ISDEFAULT = "isdefault";

    /**
     * The name of an installation package. <BR/>
     */
    public static final String ARG_PACKAGE = "pkg";

    /**
     * The name of an module. <BR/>
     */
    public static final String ARG_MODULE = "module";

    /**
     * Argument indicating a redirect. <BR/>
     */
    public static final String ARG_REDIRECT = "redirect";

    /**
     * Argument indicating a redirect. <BR/>
     */
    public static final String ARG_DISABLE_CHG_PWD = "disablechangepwd";

    /**
     * Argument indicating a redirect. <BR/>
     */
    public static final String ARG_EXTKEYDOMAIN = "iddomain";

    /**
     * Argument indicating a redirect. <BR/>
     */
    public static final String ARG_EXTKEYID = "id";

} // class BOArguments
