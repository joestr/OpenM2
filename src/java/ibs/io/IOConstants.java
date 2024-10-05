/*
 * Class: IOConstants.java
 */

// package:
package ibs.io;

// imports:


/******************************************************************************
 * This is the IOConstant-Object
 *
 * @version     $Id: IOConstants.java,v 1.15 2007/07/31 19:13:56 kreimueller Exp $
 *
 * @author      Christine Keim (CK), 980318
 ******************************************************************************
 */
public class IOConstants extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: IOConstants.java,v 1.15 2007/07/31 19:13:56 kreimueller Exp $";


    // MSIE ... musste ich auf 4 setzen, da die Browserabfrage
    // aufgrund des Installiertseins von Java SDK 2.0 eine
    // fehlerhafte Aussage zurueckliefert. MSIE 4 statt MSIE 3...
    // ist beim test auf einer maschine ohne sdk zu ändern
    // in "MSIE 3".

    /**
     * Browser type: Netscape 3. <BR/>
     */
    public static final String NS3              =  new String ("NS 3");
    /**
     * Browser type: MS Internet Explorer 4. <BR/>
     */
    public static final String MSIE4            =  new String ("IE40");
    /**
     * Browser type: Netscape 4. <BR/>
     */
    public static final String NS4              =  new String ("NS40");
    /**
     * Browser type: Netscape 6. <BR/>
     */
    public static final String NS6              =  new String ("NS60");
    /**
     * Browser type: all browsers. <BR/>
     */
    public static final String ALL_BROWSERS     =  new String ("css");


    // url protocol prefixes:
    /**
     * HTTP Protocol. <BR/>
     */
    public static final String PROT_HTTP = "http";

    /**
     * HTTPS Protocol for SSL. <BR/>
     */
    public static final String PROT_HTTPS = "https";

    /**
     * FTP Protocol. <BR/>
     */
    public static final String PROT_FTP = "ftp";

    /**
     * File Protocol. <BR/>
     */
    public static final String PROT_FILE = "file";

    /**
     * Javascript Protocol. <BR/>
     */
    public static final String PROT_JAVASCRIPT = "javascript";

    /**
     * Separator between protocol name and url. <BR/>
     */
    public static final String URL_PROTOCOLSEP = ":";

    /**
     * Separator between protocol name and url. <BR/>
     */
    public static final String URL_PROTOCOLSEPCOMPLETE =
        IOConstants.URL_PROTOCOLSEP + "//";

    /**
     * Prefix for HTTP urls. <BR/>
     */
    public static final String URL_HTTP =
        IOConstants.PROT_HTTP + IOConstants.URL_PROTOCOLSEPCOMPLETE;
    /**
     * Prefix for HTTP urls; lower case string for string comparisons. <BR/>
     */
    public static final String URL_HTTP_LC = IOConstants.URL_HTTP.toLowerCase ();

    /**
     * Prefix for HTTPS urls. <BR/>
     */
    public static final String URL_HTTPS =
        IOConstants.PROT_HTTPS + IOConstants.URL_PROTOCOLSEPCOMPLETE;

    /**
     * Prefix for FTP urls. <BR/>
     */
    public static final String URL_FTP =
        IOConstants.PROT_FTP + IOConstants.URL_PROTOCOLSEPCOMPLETE;

    /**
     * Prefix for file urls. <BR/>
     */
    public static final String URL_FILE =
        IOConstants.PROT_FILE + IOConstants.URL_PROTOCOLSEPCOMPLETE;

    /**
     * Prefix for javascript urls. <BR/>
     */
    public static final String URL_JAVASCRIPT =
        IOConstants.PROT_JAVASCRIPT + IOConstants.URL_PROTOCOLSEP;



    // alignments:
    /**
     * Left alignment. <BR/>
     */
    public static final String ALIGN_LEFT = "LEFT";

    /**
     * Centric alignment. <BR/>
     */
    public static final String ALIGN_CENTER = "CENTER";

    /**
     * Right alignment. <BR/>
     */
    public static final String ALIGN_RIGHT = "RIGHT";

    /**
     * Bleed over left side alignment. <BR/>
     */
    public static final String ALIGN_BLEEDLEFT = "BLEEDLEFT";

    /**
     * Bleed over right side alignment. <BR/>
     */
    public static final String ALIGN_BLEEDRIGHT = "BLEEDRIGHT";

    /**
     * Justified alignment. <BR/>
     */
    public static final String ALIGN_JUSTIFY = "JUSTIFY";

    /**
     * Top alignment. <BR/>
     */
    public static final String ALIGN_TOP = "TOP";

    /**
     * Middle alignment. <BR/>
     */
    public static final String ALIGN_MIDDLE = "MIDDLE";

    /**
     * Bottom alignment. <BR/>
     */
    public static final String ALIGN_BOTTOM = "BOTTOM";

    /**
     * Baseline alignment. <BR/>
     */
    public static final String ALIGN_BASELINE = "BASELINE";

    // outer borders
    /**
     * Border on all sides. <BR/>
     */
    public static final String FRAME_BORDER = "BORDER";

    /**
     * No outside borders. <BR/>
     */
    public static final String FRAME_VOID = "VOID";

    /**
     * Border on top side. <BR/>
     */
    public static final String FRAME_ABOVE = "ABOVE";

    /**
     * Border on bottom side. <BR/>
     */
    public static final String FRAME_BELOW = "BELOW";

    /**
     * Border on top and bottom sides. <BR/>
     */
    public static final String FRAME_HSIDES = "HSIDES";

    /**
     * Border on left-hand side. <BR/>
     */
    public static final String FRAME_LHS = "LHS";

    /**
     * Border on right-hand side. <BR/>
     */
    public static final String FRAME_RHS = "RHS";

    /**
     * Border on left and right sides. <BR/>
     */
    public static final String FRAME_VSIDES = "VSIDES";

    /**
     * Border on all sides. <BR/>
     */
    public static final String FRAME_BOX = "BOX";


    // inner borders
    /**
     * No interior borders. <BR/>
     */
    public static final String RULE_NONE = "NONE";

    /**
     * Horizontal borders between all table groups (THEAD, TBODY, TFOOT,
     * COLGROUP). <BR/>
     */
    public static final String RULE_GROUPS = "GROUPS";

    /**
     * Horizontal borders between all rows. <BR/>
     */
    public static final String RULE_ROWS = "ROWS";

    /**
     * Vertical borders between all columns. <BR/>
     */
    public static final String RULE_COLS = "COLS";

    /**
     * Border on all rows an columns. <BR/>
     */
    public static final String RULE_ALL = "ALL";


    /**
     * Message: The browser is not supported. <BR/>
     */
    public static final String BROWSERNOTSUPPORTED      =  new String ("Browser not supported");

    /**
     * Image for up folder. <BR/>
     */
    public static final String DISPLAYFOLDERUPURL           = new String ("Images\\global\\folderup.jpg");

    /**
     * Frameset: Logo page. <BR/>
     * @deprecated  Seems to be not used.
     */
    public static final String DISPLAYLOGO                  = new String ("logo.htm");
    /**
     * Frameset: Left folder. <BR/>
     * @deprecated  Seems to be not used.
     */
    public static final String DISPLAYFOLDERLEFT            = new String ("folderleft.htm");
    /**
     * Frameset: Upper left folder. <BR/>
     * @deprecated  Seems to be not used.
     */
    public static final String DISPLAYFOLDERUPLEFT          = new String ("folderupleft.htm");
    /**
     * Frameset: Down left folder. <BR/>
     * @deprecated  Seems to be not used.
     */
    public static final String DISPLAYFOLDERDOWNLEFT        = new String ("folderdownleft.htm");
    /**
     * Frameset: Down folder. <BR/>
     * @deprecated  Seems to be not used.
     */
    public static final String DISPLAYFOLDERDOWN            = new String ("folderdown.htm");
    /**
     * Frameset: Down right folder. <BR/>
     * @deprecated  Seems to be not used.
     */
    public static final String DISPLAYFOLDERDOWNRIGHT       = new String ("folderdownright.htm");
    /**
     * Frameset: Right folder. <BR/>
     * @deprecated  Seems to be not used.
     */
    public static final String DISPLAYFOLDERRIGHT           = new String ("folderright.htm");
    /**
     * Frameset: Upper right folder. <BR/>
     * @deprecated  Seems to be not used.
     */
    public static final String DISPLAYFOLDERUPRIGHT         = new String ("folderupright.htm");
    /**
     * Frameset: Upper folder. <BR/>
     * @deprecated  Seems to be not used.
     */
    public static final String DISPLAYFOLDERUP              = new String ("folderup.htm");
    /**
     * Frameset: Spring Up. <BR/>
     * @deprecated  Seems to be not used.
     */
    public static final String DISPLAYSPRINGUP              = new String ("springup.htm");
    /**
     * Frameset: Spring middle. <BR/>
     * @deprecated  Seems to be not used.
     */
    public static final String DISPLAYSPRINGMIDDLE          = new String ("springmiddle.htm");
    /**
     * Frameset: Spring down. <BR/>
     * @deprecated  Seems to be not used.
     */
    public static final String DISPLAYSPRINGDOWN            = new String ("springdown.htm");


    /**
     * Servervariable : AUTH_TYPE
     * The authentication method that the server uses to validate users when they
     * attempt to access a protected script.
     */
    public static final String SV_AUTH_TYPE     =  new String ("AUTH_TYPE");

    /**
     * Servervariable : CONTENT_LENGTH
     * The length of the content as given by the client.
     */
    public static final String SV_CONTENT_LENGTH =  new String ("CONTENT_LENGTH");

    /**
     * Servervariable : CONTENT_TYPE
     * The data type of the content. Used with queries that have attached information,
     * such as the HTTP queries POST and PUT.
     */
    public static final String SV_CONTENT_TYPE      =  new String ("CONTENT_TYPE");

    /**
     * Servervariable : GATEWAY_INTERFACE
     * The revision of the CGI specification used by the server. Format: CGI/revision
     */
    public static final String SV_GATEWAY_INTERFACE =  new String ("GATEWAY_INTERFACE");

    /**
     * Servervariable : HTTP_USER_AGENT
     * The browsertype & version
     */
    public static final String SV_USER_AGENT  =  new String ("HTTP_USER_AGENT");

    /**
     * Servervariable : LOGON_USER
     * The Windows NT® account that the user is logged into.
     */
    public static final String SV_LOGON_USER  =  new String ("LOGON_USER");

    /**
     * Servervariable : PATH_INFO
     * Extra path information as given by the client. You can
     * access scripts by using their virtual path and the PATH_INFO server variable.
     * If this information comes from a URL it is decoded by the server before it is
     * passed to the CGI script.
     */
    public static final String SV_PATH_INFO =  new String ("PATH_INFO");

    /**
     * Servervariable : PATH_TRANSLATED
     * A translated version of PATH_INFO that takes the path and performs any necessary
     * virtual-to-physical mapping.
     */
    public static final String SV_PATH_TRANSLATED =  new String ("PATH_TRANSLATED");

    /**
     * Servervariable : QUERY_STRING
     * Query information stored in the string following the question mark (?)
     * in the HTTP request.
     */
    public static final String SV_QUERY_STRING  =  new String ("QUERY_STRING");

    /**
     * Servervariable : REMOTE_ADDR
     * The IP address of the remote host making the request.
     */
    public static final String SV_REMOTE_ADDR =  new String ("REMOTE_ADDR");

    /**
     * Servervariable : REMOTE_HOST
     * The name of the host making the request. If the server does not have this
     * information, it will set REMOTE_ADDR and leave this empty.
     */
    public static final String SV_REMOTE_HOST =  new String ("REMOTE_HOST");

    /**
     * Servervariable : REQUEST_METHOD
     * The method used to make the request. For HTTP, this is GET, HEAD, POST, and so on.
     */
    public static final String SV_REQUEST_METHOD =  new String ("REQUEST_METHOD");

    /**
     * Server variable: REQUEST_LINE. <BR/>
     * The full HTTP request line supplied by the client.
     */
    public static final String SV_REQUEST_LINE =  new String ("REQUEST_LINE");

    /**
     * Servervariable : SCRIPT_MAP
     * Gives the base portion of the URL.
     */
    public static final String SV_SCRIPT_MAP =  new String ("SCRIPT_MAP");

    /**
     * Servervariable : SCRIPT_NAME
     * A virtual path to the script being executed.
     * This is used for self-referencing URLs.
     */
    public static final String SV_SCRIPT_NAME =  new String ("SCRIPT_NAME");

    /**
     * Servervariable : SERVER_NAME
     * The server's host name, DNS alias, or IP address as it would appear
     * in self-referencing URLs.
     */
    public static final String SV_SERVER_NAME =  new String ("SERVER_NAME");

    /**
     * Servervariable : SERVER_PORT
     * The port number to which the request was sent.
     */
    public static final String SV_SERVER_PORT =  new String ("SERVER_PORT");

    /**
     * Servervariable : SERVER_PORT_SECURE
     * A string that contains either 0 or 1. If the request is being handled
     * on the secure port, then this will be 1. Otherwise, it will be 0.
     */
    public static final String SV_SERVER_PORT_SECURE  =  new String ("SERVER_PORT_SECURE");

    /**
     * Servervariable : SERVER_PROTOCOL
     * The name and revision of the request information protocol. Format: protocol/revision
     */
    public static final String SV_SERVER_PROTOCOL  =  new String ("SERVER_PROTOCOL");

    /**
     * Servervariable : URL
     * The name and revision of the request information protocol. Format: protocol/revision
     */
    public static final String SV_URL  =  new String ("URL");

    /**
     * Servervariable : SERVER_SOFTWARE
     * Gives the base portion of the URL.
     */
    public static final String SV_SERVER_SOFTWARE  =  new String ("SERVER_SOFTWARE");

    /**
     * Servervariable : HTTPS
     * The server sets this variable to on (if secure mode is used)
     * or off.
     */
    public static final String SV_HTTPS  =  new String ("HTTPS");

    /**
     * Content type of HTTP request: multipart form data. <BR/>
     */
    public static final String CONT_WWWFORM =
        "application/x-www-form-urlencoded";

    /**
     * Content type of HTTP request: multipart form data. <BR/>
     */
    public static final String CONT_MULTIPARTFORM = "multipart/form-data";

    /**
     * Delimiter within CONTENT_TYPE which separates the real contentType from
     * its additional information. <BR/>
     */
    public static final String CONT_DELIMITER = ";";

    /**
     * Type for content type of HTTP request: multipart form data. <BR/>
     */
    public static final int CT_WWWFORM = 1;

    /**
     * Type for content type of HTTP request: multipart form data. <BR/>
     */
    public static final int CT_MULTIPARTFORM = 2;

    /**
     * Type for content type of HTTP request: multipart form data. <BR/>
     */
    public static final int CT_WWWGET = 3;

    /**
     * Integer parameter does not exist or is invalid. <BR/>
     */
    public static final int INTPARAM_NOTEXISTS_OR_INVALID = -1;

    /**
     * Boolean parameter has value FALSE. <BR/>
     */
    public static final int BOOLPARAM_FALSE    = 0;

    /**
     * Boolean parameter has value TRUE. <BR/>
     */
    public static final int BOOLPARAM_TRUE = 1;

    /**
     * Boolean parameter does not exist. <BR/>
     */
    public static final int BOOLPARAM_NOTEXISTS = -1;

    /**
     * Boolean parameter is invalid. <BR/>
     */
    public static final int BOOLPARAM_INVALID = -2;

    /**
     * Header variable name for the host. <BR/>
     */
    public static final String HV_HOST = new String ("HOST");

    /**
     * Header variable name for the Accept field. <BR/>
     */
    public static final String HV_ACCEPT = new String ("ACCEPT");

    /**
     * Header variable name for the Accept-Language field. <BR/>
     */
    public static final String HV_ACCEPTLANGUAGE = new String ("ACCEPT-LANGUAGE");

    /**
     * Header variable name for the Accept-Language field. <BR/>
     */
    public static final String HV_ACCEPTENCODING = new String ("ACCEPT-ENCODING");

    /**
     * Header variable name for the Accept-Language field. <BR/>
     */
    public static final String HV_USERAGENT = new String ("USER-AGENT");

    /**
     * Header variable name for the Accept-Language field. <BR/>
     */
    public static final String HV_CONNECTION = new String ("CONNECTION");

    /**
     * Header variable name for the Accept-Language field. <BR/>
     */
    public static final String HV_REFERER = new String ("REFERER");

    /**
     * Button id for a submit button.<BR/>
     */
    public static final String BUTTONID_SUBMIT = new String ("BUTT_SUBMIT");

    /**
     * Button id for a cancel button.<BR/>
     */
    public static final String BUTTONID_CANCEL = new String ("BUTT_CANCEL");


    // standardized user names:
    /**
     * Username of administrator user. <BR/>
     */
    public static final String USERNAME_ADMINISTRATOR = "Administrator";

    /**
     * Username of debug user. <BR/>
     */
    public static final String USERNAME_DEBUG = "Debug";

} // class IOConstants
