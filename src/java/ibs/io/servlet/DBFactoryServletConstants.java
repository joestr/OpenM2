/**
 * class DBFactoryServletConstants.java
 */

// package:
package ibs.io.servlet;

// imports:


/******************************************************************************
 * This class contains all the constants needed for DBFactoryServlet. <BR/>
 *
 * @version     $Id: DBFactoryServletConstants.java,v 1.6 2007/07/24 21:29:09 kreimueller Exp $
 *
 * @author      CHINNI RANJITH KUMAR
 ******************************************************************************
 */
public class DBFactoryServletConstants extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DBFactoryServletConstants.java,v 1.6 2007/07/24 21:29:09 kreimueller Exp $";


    /**
     * Name of the Servlet config file with complete path.
     */
    public static final String CONFIGFILENAME = "servlet.cfg";
    /**
     * Node name inside Servlet config file.
     */
    public static final String NODE_PARAM = "param";
    /**
     * Node name inside Servlet config file.
     */
    public static final String NODE_CONFIG = "config";
    /**
     * Attribute name inside Servlet config file.
     */
    public static final String ATTR_NAME = "name";
    /**
     * Attribute value inside Servlet config file.
     */
    public static final String ATTR_VALUE_IMPORTCONFIG = "importconfig";
    /**
     * Attribute value inside Servlet config file.
     */
    public static final String ATTR_VALUE_EXPORTCONFIG = "exportconfig";
    /**
     * Attribute value inside Servlet config file.
     */
    public static final String ATTR_VALUE_WORKINGDIR = "workingDir";
    /**
     * Attribute value inside Servlet config file.
     */
    public static final String ATTR_VALUE_CONFIGDIR = "configDir";
    /**
     * Attribute value inside Servlet config file.
     */
    public static final String ATTR_VALUE_IPFILTER = "IPFilter";
    /**
     * Attribute value inside Servlet config file.
     */
    public static final String ATTR_VALUE_LOGGING = "logging";
    /**
     * Attribute value inside Servlet config file.
     */
    public static final String ATTR_VALUE_LOGFILE = "logfile";
    /**
     * Attribute value inside Servlet config file.
     */
    public static final String ATTR_VALUE_MAXLOGENTRIES = "maxLogEntries";
    /**
     * Node value inside Servlet config file.
     */
    public static final String NODE_VALUE = "value";
    /**
     * Constant for the case of Logging is on.
     */
    public static final String LOGGING_ON = "on";
    /**
     * Constant for the case of Logging is off.
     */
    public static final String LOGGING_OFF = "off";
    /**
     * URL Query string parameter.
     */

    public static final String PARAM_CONFIGFILE = "configfile";
    /**
     * URL Query string parameter.
     */
    public static final String PARAM_ACTION = "action";
    /**
     * URL Query string parameter.
     */
    public static final String PARAM_ID = "id";
    /**
     * URL Query string parameter.
     */
    public static final String PARAM_CFG = "cfg";
    /**
     * URL Query string parameter.
     */
    public static final String PARAM_DEBUG = "debug";
    /**
     * URL Query string parameter.
     */
    public static final String PARAM_VERBOSE = "verbose";

    /**
     * URL Query string parameter.
     */
    public static final String PARAM_QUERY = "query";
    /**
     * URL Query string parameter.
     */
    public static final String PARAM_WHERE = "where";
    /**
     * URL Query string parameter.
     */
    public static final String PARAM_TABLE = "table";
    /**
     * URL Query string parameter.
     */
    public static final String PARAM_ATTRIBUTE = "attribute";

    /**
     * URL Query string parameter value.
     */
    public static final String ACTION_DIR = "dir";
    /**
     * URL Query string parameter value.
     */
    public static final String ACTION_READ = "read";
    /**
     * URL Query string parameter value.
     */
    public static final String ACTION_WRITE = "write";
    /**
     * URL Query string parameter value.
     */
    public static final String ON = DBFactoryServletConstants.LOGGING_ON;

    // Separators and constants for DBFactoryServlet:
    /**
     * Separator: comma (","). <BR/>
     */
    public static final String SEPARATOR_COMMA = ",";
    /**
     * Separator: equals ("="). <BR/>
     */
    public static final String SEPARATOR_EQUAL = "=";
    /**
     * Separator: slash ("/"). <BR/>
     */
    public static final String SEPARATOR_FORWARDSLASH = "/";
    /**
     * Separator: backslash ("\\"). <BR/>
     */
    public static final String SEPARATOR_BACKWARDSLASH = "\\";
    /**
     * Separator: unix root separator ("/"). <BR/>
     */
    public static final String UNIX_SEPARATOR = "/";
    /**
     * Separator: windows root separator (":\\"). <BR/>
     */
    public static final String WINDOWS_SEPARATOR = ":\\";
    /**
     * Default import file name. <BR/>
     */
    public static final String IMPORT_FILE = "import.xml";
    /**
     * Default export file name. <BR/>
     */
    public static final String EXPORT_FILE = "export.xml";
    /**
     * Standard XML encoding. <BR/>
     */
    public static final String OUTPUTFORMAT = "UTF-8";
    /**
     * Version number. <BR/>
     */
    public static final String VERSION = "1.0";
    /**
     * Separator: Parameter separator ("&"). <BR/>
     */
    public static final String SEPARATOR_PARAM = "&";
    /**
     * Separator: Space replacement ("+"). <BR/>
     */
    public static final String SEPARATOR_PLUS = "+";
    /**
     * Blank string (" "). <BR/>
     */
    public static final String BLANK = " ";

} // DBFactoryServletConstants
