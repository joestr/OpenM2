/*
 * Class: AgentMessages.java
 */

// package:
package ibs.di.agent;

// imports:
import ibs.di.agent.ExportAgent_01;
import ibs.di.agent.ImportAgent_01;


/******************************************************************************
 * Messages for ibs.di agents. <BR/>
 * This abstract class contains all tokens which are necessary to deal with
 * the business objects delivered within this package. <BR/>
 *
 * @version     $Id: AgentMessages.java,v 1.26 2007/07/31 19:13:53 kreimueller Exp $
 *
 * @author      Bernd Buchegger (BB), 20000302
 ******************************************************************************
 */
public abstract class AgentMessages extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: AgentMessages.java,v 1.26 2007/07/31 19:13:53 kreimueller Exp $";


    /**
     * Message when parameter is invalid. <BR/>
     */
    public static final String MSG_INVALID_PARAMETER          = "Invalid Parameter: ";

    /**
     * Message when a parameter is missing. <BR/>
     */
    public static final String MSG_MISSING_PARAMETER          = "Missing Parameter!";

    /**
     * Message when initialization failed. <BR/>
     */
    public static final String MSG_INIT_FAILED                = "Initialization failed.";

    /**
     * Message when parametercheck failed. <BR/>
     */
    public static final String MSG_PARAMETERCHECK_FAILED      = "Parametercheck failed.";

    /**
     * Message when missing username parameter. <BR/>
     */
    public static final String MSG_MISSING_USERNAME           = "Missing -USER argument.";

    /**
     * Message when missing object parameter. <BR/>
     */
    public static final String MSG_MISSING_OBJECT           = "Missing -OBJECT parameter.";

    /**
     * Message when invalid domain id. <BR/>
     */
    public static final String MSG_INVALID_DOMAINID           = "Invalid -DOMAIN argument.";

    /**
     * Message when invalid connectortype. <BR/>
     */
    public static final String MSG_INVALID_CONNECTORTYPE      = "Invalid -CONNECTORTYPE argument.";

    /**
     * Message when missing ftpserver argument. <BR/>
     */
    public static final String MSG_MISSING_FTPSERVER          = "Missing -FTPSERVER argument.";

    /**
     * Message when missing ftpuser argument. <BR/>
     */
    public static final String MSG_MISSING_FTPUSER            = "Missing -FTPUSER argument.";

    /**
     * Message when missing mailserver argument. <BR/>
     */
    public static final String MSG_MISSING_MAILSERVER         = "Missing -MAILSERVER argument.";

     /**
     * Message when missing mailreceiver argument. <BR/>
     */
    public static final String MSG_MISSING_MAILRECEIVER       = "Missing -MAILRECEIVER argument.";

    /**
     * Message when missing mailuser argument. <BR/>
     */
    public static final String MSG_MISSING_MAILUSER           = "Missing -MAILUSER argument.";

    /**
     * Message when missing mailprotocol argument. <BR/>
     */
    public static final String MSG_MISSING_MAILPROTOCOL       = "Missing -MAILPROTOCOL argument.";

    /**
     * Message when mailprotocol argument is invalid. <BR/>
     */
    public static final String MSG_UNKOWN_MAILPROTOCOL       = "Unkown mailprotocol.";

    /**
     * Message when missing sap business connector gateway url. <BR/>
     */
    public static final String MSG_MISSING_SAPURL           = "Missing -SAPURL argument.";

    /**
     * Message when missing sap server name. <BR/>
     */
    public static final String MSG_MISSING_SAPNAME           = "Missing -SAPNAME argument.";

    /**
     * Message when missing http multipart target url. <BR/>
     */
    public static final String MSG_MISSING_HTTPMULTIPARTURL  = "Missing -HTTPMULTIPART argument.";

    /**
     * Message when invalid every argument. <BR/>
     */
    public static final String MSG_INVALID_EVERY              = "Invalid -EVERY argument.";

    /**
     * Message when invalid every argument. <BR/>
     */
    public static final String MSG_INVALID_TIME              = "Invalid -TIME argument.";

    /**
     * Message when invalid frequency argument. <BR/>
     */
    public static final String MSG_INVALID_FREQUENCY          = "Invalid -FREQUENCY argument.";

    /**
     * Message when logfile name missing. <BR/>
     */
    public static final String MSG_MISSING_LOGFILENAME        = "Missing -LOGFILENAME argument.";

    /**
     * Message when agent activated. <BR/>
     */
    public static final String MSG_AGENT_ACTIVATED            = "Agent activated...";

    /**
     * Message when no import files found. <BR/>
     */
    public static final String MSG_NO_IMPORTFILES_FOUND       = "No import files found.";

    /**
     * Message when agent closed. <BR/>
     */
    public static final String MSG_AGENT_CLOSED               = "Agent closed.";

    /**
     * Message when no connector available. <BR/>
     */
    public static final String MSG_NO_CONNECTOR_AVAILABLE     = "No connector available!";

    /**
     * Message when trying to login and import data. <BR/>
     */
    public static final String MSG_TRY_IMPORT_LOGIN            = "Trying to login and import data ...";

     /**
     * Message when trying to login and export data. <BR/>
     */
    public static final String MSG_TRY_EXPORT_LOGIN            = "Trying to login and export data ...";
    /**
     * Message for AGENT started with the following settings. <BR/>
     */
    public static final String MSG_AGENT_STARTED_WITH_SETTINGS = "AGENT started with the following settings:";

    /**
     * Message for listening to FILE system using work directory. <BR/>
     */
    public static final String MSG_LISTENING_TO_FILESYSTEM    = "Listening to FILE system using work directory.";

    /**
     * Message for listening to FTP server directory. <BR/>
     */
    public static final String MSG_LISTENING_TO_FTP           = "Listening to FTP server directory.";

    /**
     * Message for listening to EMAIL account. <BR/>
     */
    public static final String MSG_LISTENING_TO_MAIL          = "Listening to EMAIL account.";

    /**
     * Message when using a Servlet connection. <BR/>
     */
    public static final String MSG_USING_SERVLET              = "Using a Servlet connection.";

    /**
     * Message when using an ASP connection. <BR/>
     */
    public static final String MSG_USING_ASP                  = "Using an ASP connection.";

    /**
     * Message when files will be deleted after import. <BR/>
     */
    public static final String MSG_FILES_WILL_BE_DELETED      = "Files will be deleted after import.";

    /**
     * Message for log Settings. <BR/>
     */
    public static final String MSG_LOG_SETTINGS               = "Log Settings:";

    /**
     * Message when log will be appended to existing file. <BR/>
     */
    public static final String MSG_LOG_WILL_BE_APPENDED       = "Log will be appended to existing file.";

    /**
     * Message when Agent sleeping. <BR/>
     */
    public static final String MSG_AGENT_SLEEPING             = "Agent sleeping...";

    /**
     * Message for using the mail-connector. <BR/>
     */
    public static final String MSG_USING_THE_MAILCONNECTOR    = "The email-connector is set ";

    /**
     * Message for using the ftp-connector. <BR/>
     */
    public static final String MSG_USING_THE_FTPCONNECTOR    = "The ftp-connector is set ";

    /**
     * Message for using the file-connector. <BR/>
     */
    public static final String MSG_USING_THE_FILECONNECTOR    = "A file-connector will read from the agent work directory";

    /**
     * Message for using the SAP-connector. <BR/>
     */
    public static final String MSG_USING_THE_SAPCONNECTOR    = "The SAP-connector is set ";

    /**
     * Message for using the HTTP-multipart-connector. <BR/>
     */
    public static final String MSG_USING_THE_HTTPMULTIPARTCONNECTOR    = "The HTTP-multipart-connector is set ";

    /**
     * Message: password not set. <BR/>
     */
    public static final String MSG_MISSING_PASSWORD            = "Password not set!";

    /**
     * Message: please enter the password. <BR/>
     */
    public static final String MSG_ENTER_PASSWORD            = "Please enter the password:";

    /**
     * Message when also container should be exported. <BR/>
     */
    public static final String MSG_INCLUDE_CONTAINER           = "Container objects will be included";

    /**
     * Message when a container is not be used. <BR/>
     */
    public static final String MSG_NOT_INCLUDE_CONTAINER       = "Container objects will not be included (else use -includeContainer)";

    /**
     * Message for recursive export. <BR/>
     */
    public static final String MSG_RECURSIVE                   = "Exporting subobjects is activated.";

    /**
     * Message when recursive export is not used. <BR/>
     */
    public static final String MSG_NOT_RECURSIVE               = "Exporting subobjects is deactivated (else use -recursive)";

    /**
     * Message for hierarichal structure of the export. <BR/>
     */
    public static final String MSG_HIERARCHICAL                = "Hierarchical structure will be preserved. Note that this automatically \n" +
                                                                 "> activates the options -SINGLEFILE, -RECURSIVE and -INCLUDECONTAINER";

    /**
     * Message when not hierarichal structure of the export. <BR/>
     * is used
     */
    public static final String MSG_NOT_HIERARCHICAL            = "Hierarchical structure will be flattened (use -HIERARCHICAL)";

    /**
     * Message for singlefile export activated. <BR/>
     */
    public static final String MSG_SINGLE_FILE                  = "All objects will be written into one single exportfile";

    /**
     * Message for singlefile export deactivated. <BR/>
     */
    public static final String MSG_NOT_SINGLE_FILE              = "A separate file will be written for every object. (use -SINGLEFILE)";

    /**
     * Message for delete objects after export. <BR/>
     */
    public static final String MSG_DELETEOBJECTS                = "Objects will be deleted after export";

    /**
     * Message for not deleting objects after export. <BR/>
     */
    public static final String MSG_NOT_DELETEOBJECTS = "Objects will not be deleted after export";

    /**
     * Message for delete subobjects after export. <BR/>
     */
    public static final String MSG_DELETESUBOBJECTS = "Subobjects of a container will be deleted after export";

    /**
     * Message for not deleting subobjects after export. <BR/>
     */
    public static final String MSG_NOT_DELETESUBOBJECTS = "Subobjects of a container will not be deleted after export";

    /**
     * Message for keymapping activated. <BR/>
     */
    public static final String MSG_RESOLVEKEYMAPPING = "Keymappings will be resolved.";

    /**
     * Message for keymapping deactivated. <BR/>
     */
    public static final String MSG_NOT_RESOLVEKEYMAPPING = "Keymappings will not be resolved. (use -RESOLVEKEYMAPPING)";

    /**
     * Message for exporting result of a query
     */
    public static final String MSG_RESOLVEQUERY = "Result of queries will be exported.";

    /**
     * Message for not deleting subobjects after export. <BR/>
     */
    public static final String MSG_NOT_RESOLVEQUERY = "Result of queries will not be exported. (use -RESOLVEQUERY)";

    /**
     * Message for restoring external IDs. <BR/>
     */
    public static final String MSG_RESTOREEXTERNALIDS = "External IDs will be stored if possible.";

    /**
     * Message for not restoring external IDs. <BR/>
     */
    public static final String MSG_NOT_RESTOREEXTERNALIDS = "External IDs will not be stored. (use -STOREEXTERNALIDS)";

    /**
     * Message for resolving references. <BR/>
     */
    public static final String MSG_RESOLVEREFERENCE = "References will be resolved and the referenced object will be exported.";

    /**
     * Message for not resolving references. <BR/>
     */
    public static final String MSG_NOT_RESOLVEREFERENCE = "References will not be resolved. (use -RESOLVEREFERENCE)";

    /**
     * Message for using the reference oid when resolving references. <BR/>
     */
    public static final String MSG_USEREFERENCEOID = "Use reference oid when resolving references.";

    /**
     * Message for using the reference oid when resolving references. <BR/>
     */
    public static final String MSG_NOT_USEREFERENCEOID = "Reference oid will not be used when resolving references. (use -USEREFERENCEOID)";

    /**
     * Message for frequency type once. <BR/>
     */
    public static final String MSG_AGENT_FREQUENCY_ONCE         = "Agent will be activated only once.";

    /**
     * Message for the agent command line menu. <BR/>
     */
    public static final String MSG_AGENT_MENU =
        ">>> Menu:\r\n" +
        "'a' + <enter> ... print date of last and next activation.\r\n" +
        "'s' + <enter> ... print agent settings.\r\n" +
        "'h' + <enter> ... print agent usage.\r\n" +
        "'x' + <enter> ... exit the agent.";

    /**
     * Message for agent command line usage. <BR/>
     */
    public static final String MSG_IMPORTAGENT_USAGE =
        "Usage:\r\n" +
        "java " + ImportAgent_01.class.getName () + "\r\n" +
        "   -USER <username> -PW <password>\r\n" +
        "   [-DOMAIN <domainId, default: 1>]\r\n" +
        "   -CONTAINER {<importContainer OID>|<m2 path to importContainer>}\r\n" +
        "   [-FREQUENCY {ONCE(=default)|MINUTES|DAY|WEEK|MONTH}]\r\n" +
        "   [-EVERY {<minutes>|<day of month>|MO,TU,WE,TH,FR,SA,SU>]\r\n" +
        "   [-TIME <time>]\r\n" +
        "   [-PATH <work directory, default is current directory>]\r\n" +
        "   [-SERVER <name of m2 server, default is localhost>]\r\n" +
        "   [-APPPATH <m2 application path, default is /m2/>]\r\n" +
        "   [-FILTER <filter for xml importfiles, default is '*.xml'>]\r\n" +
        "   [-CONNECTOR {<connector OID>|<name of connector>\r\n" +
        "                default is fileconnector to work directory>]\r\n" +
        "   [-SCRIPT {<importscript OID>|<name of importscript>}]\r\n" +
        "   [-TRANSLATOR {<translator OID>|<name of translator>}]\r\n" +
        "   [-BACKUP {<backup connector OID>|<name of backup connector}]\r\n" +
        "   [-LOGGING {NONE(=default)|NEW|APPEND}]\r\n" +
        "   [-LOGFILENAME <name of the log file>]\r\n" +
        "   [-LOGPATH <file path for log file, default is work directory set in -PATH>]\r\n" +
        "   [-DELETE] [-SORT {ASC|DESC}] [-WAIT] [-ENABLEWORKFLOW] [-VALIDATE]\r\n" +
        "   [-NOTIFY <YES|NO(=default)>" +
        "    -NOTIFYRECEIVER <receiver> -NOTIFYSENDER <sender> -NOTIFYSUBJECT <subject>]\r\n" +
        "   [-NTDOMAIN] <NT domain> [-NTUSER] <NT user> [-NTPW] <nt password>\r\n" +
        "   [-HELP] [--HELP] [-?]";


    /**
     * Message for agent command line usage. <BR/>
     */
    public static final String MSG_EXPORTAGENT_USAGE =
        "Usage:\r\n" +
        "java " + ExportAgent_01.class.getName () + "\r\n" +
        "   -USER <username> [-PW password]\r\n" +
        "   [-DOMAIN <domainId, default: 1>]\r\n" +
        "   -OBJECT {<exportObject OID>|<m2 path to exportObject>}*\r\n" +
        "   [-FREQUENCY {ONCE(=default)|MINUTES|DAY|WEEK|MONTH}]\r\n" +
        "   [-EVERY {<minutes>|<day of month>|MO,TU,WE,TH,FR,SA,SU>] [-TIME <time>]\r\n" +
        "   [-WORKDIR <working directory, default is .\\>]\r\n" +
        "   [-TEMPDIR <temporary directory, default is .\\temp>]\r\n" +
        "   [-ERRORDIR <errorfiles directory, default is .\\errorfiles>]\r\n" +
        "   [-RETRY <number of retries, default is 10>] \r\n" +
        "   [-NOTIFY <YES|NO(=default)> -NOTIFYMAILSERVER <mailserver>\r\n" +
        "    -NOTIFYRECEIVER <receiver> -NOTIFYSENDER <sender> -NOTIFYSUBJECT <subject>]\r\n" +
        "   [-CONNECTION {ASP(=default)|SERVLET}]\r\n" +
        "   [-SERVER <name of m2 server, default is localhost>]\r\n" +
        "   [-APPPATH <m2 application path, default is /m2/app/>]\r\n" +
        "   [-TRANSLATOR {<translator OID>|<name of translator>}]\r\n" +
        "   [-BACKUP {<backup connector OID>|<name of backup connector}]\r\n" +
        "   [-LOGGING {NONE(=default)|NEW|APPEND}]\r\n" +
        "   [-LOGFILENAME <name of the log file>]\r\n" +
        "   [-LOGPATH <path for the log file, default is the path set in -path>]\r\n" +
        "   [-WAIT] [-HIERARCHICAL] [-SINGLEFILE] [-RECURSIVE] [-INCLUDECONTAINER]\r\n" +
        "   [-DELETE] [-DELETERECURSIVE] [-RESOLVEKEYMAPPING] [-STOREEXTERNALIDS] \r\n" +
        "   [-RESOLVEQUERY] [-RESOLVEREFERENCE] [-USEREFERENCEOID]\r\n" +
        "   [-CONNECTORTYPE {FILE(=default)|FTP|MAIL|SAP|HTTPMULTIPART}]\r\n" +
        "   [-FTPSERVER <ftp server> -FTPUSER <username> -FTPPASSWORD <password>\r\n" +
        "    -FTPPATH <path on ftp server to read the files from>]\r\n" +
        "   [-MAILSERVER <mail server> -MAILPROTOCOL {IMAP|POP3}\r\n" +
        "    -MAILUSER <username> -MAILPASSWORD <password>\r\n" +
        "    -MAILRECEIVER <receiveraddress> -MAILSENDER <senderaddress>]\r\n" +
        "   [-SAPURL <SAP business connector gateway URL> -SAPNAME <name of sap server>]\r\n" +
        "   [-HTTPMULTIPARTURL <HTTP multipart connector target URL>]\r\n" +
        "   [-NTDOMAIN] <NT domain> [-NTUSER] <NT user> [-NTPW] <nt password>\r\n" +
        "   [-HELP] [--HELP] [-?]";


    /**
     * Message for writing files to connector. <BR/>
     */
    public static final String MSG_WRITING_FILES_TO_CONNECTOR = "Writing exported files to connector...";

    /**
     * Message when a file has been successfully written to connector. <BR/>
     */
    public static final String MSG_FILE_WRITTEN = "File has been successfully written to connector";

    /**
     * Message when a file could not have been written to connector. <BR/>
     */
    public static final String MSG_COULD_NOT_WRITE_FILE = "File could not be written to connector";

    /**
     * Message when error files exist. <BR/>
     */
    public static final String MSG_ERROR_FILES_EXISTS =
        "WARNING: there are files that could not have been written to connector! Temporary directory will not be removed.";

    /**
     * Message when temporary directory could not have been deleted. <BR/>
     */
    public static final String MSG_COULD_NOT_DELETE_TEMP_DIR = "WARNING: could not delete temporary directory!";

    /**
     * Message when a file could not have been written to connector. <BR/>
     */
    public static final String MSG_COULD_NOT_DELETE_FILE = "File could not be deleted!";

    /**
     * Message when -NOTIFYMAILSERVER argument has not been set. <BR/>
     */
    public static final String MSG_MISSING_NOTIFYMAILSERVER = "Missing -NOTIFYMAILSERVER argument.";

    /**
     * Message when -NOTIFYRECEIVER argument has not been set. <BR/>
     */
    public static final String MSG_MISSING_NOTIFYRECEIVER = "Missing -NOTIFYRECEIVER argument.";

    /**
     * Message when -NOTIFYSENDER argument has not been set. <BR/>
     */
    public static final String MSG_MISSING_NOTIFYSENDER = "Missing -NOTIFYSENDER argument.";

    /**
     * Message when -CONTAINER argument has not been set. <BR/>
     */
    public static final String MSG_MISSING_IMPORTCONTAINER = "Missing -CONTAINER argument.";

    /**
     * Message when -ERRORDIR argument has invalid. <BR/>
     */
    public static final String MSG_INVALID_ERRORDIR = "Invalid -ERRORDIR argument. Directory does not exist.";

    /**
     * Message when errofiles directory could not have been created. <BR/>
     */
    public static final String MSG_COULD_NOT_CREATE_ERRORDIR = "Could not create errorfiles directory.";

    /**
     * Message when -TEMPDIR argument has invalid. <BR/>
     */
    public static final String MSG_INVALID_TEMPDIR = "Invalid -TEMPDIR argument. Directory does not exist.";

    /**
     * Message when -WORKDIR argument has invalid. <BR/>
     */
    public static final String MSG_INVALID_WORKDIR = "Invalid -WORKDIR argument. Directory does not exist.";

    /**
     * Message when temp directory could not have been created. <BR/>
     */
    public static final String MSG_COULD_NOT_CREATE_TEMPDIR = "Could not create temporary directory.";

    /**
     * Message when -RETRY value is invalid. <BR/>
     */
    public static final String MSG_INVALID_RETRY = "Invalid -RETRY argument. Must be greater or equal 0";

    /**
     * Message when maximal number of retries have beene exceeded by an error file. <BR/>
     */
    public static final String MSG_ERRORFILE_EXCEEDED_RETRIES = "File exceeded maximal number of retries";

    /**
     * Message when errorfile has been moved. <BR/>
     */
    public static final String MSG_MOVING_ERRORFILE = "File moved to";

    /**
     * Message when error file could not have been moved. <BR/>
     */
    public static final String MSG_COULD_NOT_MOVE_ERRORFILE = "Could not move error file";

    /**
     * Message when notification could not have been send. <BR/>
     */
    public static final String MSG_COULD_NOT_SEND_NOTIFICATION = "Could not send notification!";

    /**
     * Message when message could not be added to errorlog. <BR/>
     */
    public static final String MSG_COULD_NOT_WRITE_TO_ERRORLOG = "Could not add entry to errorlog!";

    /**
     * Message when message could not be added to errorlog. <BR/>
     */
    public static final String MSG_COULD_NOT_DELETE_ERROR_DIR = "Errorfiles directory could not be deleted!";

    /**
     * Message when message could not be added to errorlog. <BR/>
     */
    public static final String MSG_CHECKING_ERRORFILES = "Checking number of retries...";

    /**
     * Message when message could not be added to errorlog. <BR/>
     */
    public static final String MSG_ERRORLOG_LOCATION = "Detailed information can be found in the agent errorlog file at:";

    /**
     * Message when no objects have been defined. <BR/>
     */
    public static final String MSG_NO_OBJECTS_SET = "no objects set for export!";

    /**
     * Message when files will be sorted before import in ascending order. <BR/>
     */
    public static final String MSG_FILES_WILL_BE_SORTED_ASC = "Files will be sorted alphabetically (ascending order).";

    /**
     * Message when files will be sorted before import in descending order. <BR/>
     */
    public static final String MSG_FILES_WILL_BE_SORTED_DESC = "Files will be sorted alphabetically (descending order).";

    /**
     * Message: error in NTLM authentication. <BR/>
     */
    public static final String MSG_NTLM_AUTHENTICATION_ERROR = "Error during NTLM authentication.";

    /**
     * Token for user credentials. <BR/>
     */
    public static final String MSG_ENABLEWORKFLOW = "Starting workflow activated.";

    /**
     * Token for user credentials. <BR/>
     */
    public static final String MSG_VALIDATESTRUCTURE = "Structure validation is activated";


} // class AgentMessages
