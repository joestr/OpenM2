/*
 * Class: UtilExceptions.java
 */

// package:
package ibs.util;

// imports:


/******************************************************************************
 * Constants for intranet business solutions utilities. <BR/>
 * This abstract class contains all constant exception messages.
 *
 * @version     $Id: UtilExceptions.java,v 1.20 2010/04/07 13:37:04 rburgermann Exp $
 *
 * @author      Ralf Werl (RW), 991213
 ******************************************************************************
 */
public abstract class UtilExceptions extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: UtilExceptions.java,v 1.20 2010/04/07 13:37:04 rburgermann Exp $";

    /**
     * Name of bundle where the exceptions are included. <BR/>
     */
    public static String EXC_BUNDLE = "ibs_ibsbase_exceptions";

    // texts for error messages:
    /**
     * Text for error message: no access exception. <BR/>
     */
    public static String ML_E_NOACCESSEXCEPTION = "ML_E_NOACCESSEXCEPTION";
    /**
     * Text for error message: file access error. <BR/>
     */
    public static String ML_E_DBFILEERROR = "ML_E_DBFILEERROR";
    /**
     * Text for error message: function not possible on all objects. <BR/>
     */
    public static String ML_E_OBJECTNOTAFFECTEDEXCEPTION = "ML_E_OBJECTNOTAFFECTEDEXCEPTION";

    /**
     * Message to be displayed if the application was not correctly
     * initialized. <BR/>
     */
    public static String ML_E_APPLICATIONNOTINITIALIZED = "ML_E_APPLICATIONNOTINITIALIZED";

    /**
     * Text for error message when the required object was not found. <BR/>
     */
    public static String ML_E_OBJECTNOTFOUNDEXCEPTION = "ML_E_OBJECTNOTFOUNDEXCEPTION";

    /**
     * Text for error message when there is no more element. <BR/>
     */
    public static String ML_E_NOMOREELEMENTSEXCEPTION = "ML_E_NOMOREELEMENTSEXCEPTION";

    /**
     * Text for error message when name is already given for the user. <BR/>
     */
    public static String ML_E_NAMEALREADYGIVENEXCEPTION = "ML_E_NAMEALREADYGIVENEXCEPTION";

    /**
     * Text for error message when name is already given for the user. <BR/>
     */
    public static String ML_E_OBJECTDELETED = "ML_E_OBJECTDELETED";

    /**
     * Text for error message if no language defined in database. <BR/>
     */
    public static String ML_E_NOLANGUAGE = "ML_E_NOLANGUAGE";

    /**
     * Text for error message if no upload directory could be created. <BR/>
     */
    public static String ML_E_UPLOAD = "ML_E_UPLOAD";

    /**
     * Message to be displayed if ssl must be used for a domain. <BR/>
     */
    public static String ML_E_SSLMUSTBEUSEDDOMAIN = "ML_E_SSLMUSTBEUSEDDOMAIN";

    /**
     * Message to be displayed if ssl must be used of a user. <BR/>
     */
    public static String ML_E_SSLMUSTBEUSEDUSER = "ML_E_SSLMUSTBEUSEDUSER";

    /**
     * Message to be displayed if ssl is not available. <BR/>
     */
    public static String ML_E_SSLISNOTAVAILABLE = "ML_E_SSLISNOTAVAILABLE";

    /**
     * Message to be displayed if p_ssl is wrong configurated. <BR/>
     */
    public static String ML_E_SSLWRONGCONFIGURED = "ML_E_SSLWRONGCONFIGURED";

    /**
     * Message to be displayed if a configuration error occurred. <BR/>
     */
    public static String ML_E_CONFIGURATIONERROR = "ML_E_CONFIGURATIONERROR";

    /**
     * Message to be displayed if configuration file was not found. <BR/>
     */
    public static String ML_E_CONFIGURATIONERRORFILENOTFOUND = "ML_E_CONFIGURATIONERRORFILENOTFOUND";

    /**
     * Message to be displayed if an io-error occurred while reading the configuration file. <BR/>
     */
    public static String ML_E_CONFIGURATIONERRORIOERROR = "ML_E_CONFIGURATIONERRORIOERROR";

    /**
     * Message to be displayed if a general error occurred while reading the configuration file. <BR/>
     */
    public static String ML_E_CONFIGURATIONERRORGENERALERROR = "ML_E_CONFIGURATIONERRORGENERALERROR";

    /**
     * Message to be displayed if the Encapsulator was not found. <BR/>
     */
    public static String ML_E_CONFIGURATIONERRORNOENCAPSULATORFOUND = "ML_E_CONFIGURATIONERRORNOENCAPSULATORFOUND";

    /**
     * Message to be displayed if an io-error occurred while reading the configuration file. <BR/>
     */
    public static String ML_E_CONFIGURATIONERRORDATABASETYPENOTDEFINED = "ML_E_CONFIGURATIONERRORDATABASETYPENOTDEFINED";

    /**
     * Message to be displayed if a directory could not be created for an upload. <BR/>
     */
    public static String ML_E_NODIRECTORYCREATED = "ML_E_NODIRECTORYCREATED";

    /**
     * Message to be displayed if a file could not be written to the hard disc drive. <BR/>
     */
    public static String ML_E_NOFILEWRITE = "ML_E_NOFILEWRITE";

    /**
     * Message to be displayed if a file could not be moved on the hard disc drive. <BR/>
     */
    public static String ML_E_NOFILEMOVE = "ML_E_NOFILEMOVE";

    /**
     * Message to be displayed if an object should be deleted but there are
     * still objects which depend on it. <BR/>
     */
    public static String ML_E_DEPENDENTOBJECTEXISTS = "ML_E_DEPENDENTOBJECTEXISTS";

    /**
     * Message to be displayed if no ssiurl was given in the ibssystem.cfg. <BR/>
     */
    public static String ML_E_CONFIGURATIONERRORNOSSIURL = "ML_E_CONFIGURATIONERRORNOSSIURL";

    /**
     * Message to be displayed if a wrong ssiurl was given in the ibssystem.cfg. <BR/>
     */
    public static String ML_E_SSIURLNOTFOUND = "ML_E_SSIURLNOTFOUND";

    /**
     * Message to be displayed if the requested serveraddress is not allowed. <BR/>
     */
    public static String ML_E_SERVERREQUESTNOTALLOWED = "ML_E_SERVERREQUESTNOTALLOWED";

    /**
     * Message to be displayed if the requested serveraddress is not allowed. <BR/>
     */
    public static String ML_E_NOSERVERSCONFIGURED = "ML_E_NOSERVERSCONFIGURED";

    /**
     * Message if the stacktrace cannot be gathered from a throwable object.
     */
    public static String ML_E_PRINTSTACKTRACENOTPOSSIBLE = "ML_E_PRINTSTACKTRACENOTPOSSIBLE";

    /**
     * Message if not all objects can be deleted. <BR/>
     */
    public static String ML_E_NOTALLDELETEABLE = "ML_E_NOTALLDELETEABLE";

} // class UtilExceptions
