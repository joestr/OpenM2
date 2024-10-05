/*
 * Class: AgentConstants.java
 */

// package:
package ibs.di.agent;

// imports:


/******************************************************************************
 * Constants for ibs.di agents. <BR/>
 * This abstract class contains all tokens which are necessary to deal with
 * the business objects delivered within this package. <BR/>
 *
 * @version     $Id: AgentConstants.java,v 1.19 2007/07/31 19:13:53 kreimueller Exp $
 *
 * @author      Bernd Buchegger (BB), 20000302
 ******************************************************************************
 */
public abstract class AgentConstants extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: AgentConstants.java,v 1.19 2007/07/31 19:13:53 kreimueller Exp $";


    /**
     * Agent command line argument: display help screen. <BR/>
     */
    public static final String AGENTARG_HELP             = "-HELP";
    /**
     * Agent command line argument: display unix help screen. <BR/>
     */
    public static final String AGENTARG_HELPUNIX         = "--HELP";
    /**
     * Agent command line argument: frequency of polling. <BR/>
     */
    public static final String AGENTARG_FREQUENCY        = "-FREQUENCY";
    /**
     * Agent command line argument: time at which to perform polling. <BR/>
     */
    public static final String AGENTARG_TIME             = "-TIME";
    /**
     * Agent command line argument: repetition interval. <BR/>
     */
    public static final String AGENTARG_EVERY            = "-EVERY";
    /**
     * Agent command line argument: server name. <BR/>
     */
    public static final String AGENTARG_SERVER           = "-SERVER";
    /**
     * Agent command line argument: application path. <BR/>
     */
    public static final String AGENTARG_APPPATH          = "-APPPATH";
    /**
     * Agent command line argument: connection name. <BR/>
     */
    public static final String AGENTARG_CONNECTION       = "-CONNECTION";
    /**
     * Agent command line argument: user name. <BR/>
     */
    public static final String AGENTARG_USER             = "-USER";
    /**
     * Agent command line argument: user password. <BR/>
     */
    public static final String AGENTARG_PW               = "-PW";
    /**
     * Agent command line argument: domain id. <BR/>
     */
    public static final String AGENTARG_DOMAIN           = "-DOMAIN";
    /**
     * Agent command line argument: path. <BR/>
     */
    public static final String AGENTARG_PATH             = "-PATH";
    /**
     * Agent command line argument: match kind. <BR/>
     */
    public static final String AGENTARG_MATCH            = "-MATCH";
    /**
     * Agent command line argument: name of filter. <BR/>
     */
    public static final String AGENTARG_FILTER           = "-FILTER";
    /**
     * Agent command line argument: object oid. <BR/>
     */
    public static final String AGENTARG_OBJECT           = "-OBJECT";
    /**
     * Agent command line argument: container oid. <BR/>
     */
    public static final String AGENTARG_CONTAINER        = "-CONTAINER";
    /**
     * Agent command line argument: oid or name of connector. <BR/>
     */
    public static final String AGENTARG_CONNECTOR        = "-CONNECTOR";
    /**
     * Agent command line argument: oid or name of translator. <BR/>
     */
    public static final String AGENTARG_TRANSLATOR       = "-TRANSLATOR";
    /**
     * Agent command line argument: oid or name of import script. <BR/>
     */
    public static final String AGENTARG_SCRIPT           = "-SCRIPT";
    /**
     * Agent command line argument: oid or name of backup connector. <BR/>
     */
    public static final String AGENTARG_BACKUPCONNECTOR  = "-BACKUP";
    /**
     * Agent command line argument: name of log file. <BR/>
     */
    public static final String AGENTARG_LOGFILENAME      = "-LOGFILENAME";
    /**
     * Agent command line argument: logging path. <BR/>
     */
    public static final String AGENTARG_LOGPATH          = "-LOGPATH";
    /**
     * Agent command line argument: kind of logging: NONE | NEW | APPEND. <BR/>
     */
    public static final String AGENTARG_LOGGING          = "-LOGGING";
    /**
     * Agent command line argument: delete original files?. <BR/>
     */
    public static final String AGENTARG_DELETE           = "-DELETE";
    /**
     * Agent command line argument: perform recursive deletion?. <BR/>
     */
    public static final String AGENTARG_DELETERECURSIVE  = "-DELETERECURSIVE";
    /**
     * Agent command line argument: wait with first execution until the agent start time is reached?. <BR/>
     */
    public static final String AGENTARG_WAIT             = "-WAIT";
    /**
     * Agent command line argument: create hierarchical export?. <BR/>
     */
    public static final String AGENTARG_HIERARCHICAL     = "-HIERARCHICAL";
    /**
     * Agent command line argument: export everything into a single file?. <BR/>
     */
    public static final String AGENTARG_SINGLEFILE       = "-SINGLEFILE";
    /**
     * Agent command line argument: perform export recursively?. <BR/>
     */
    public static final String AGENTARG_RECURSIVE        = "-RECURSIVE";
    /**
     * Agent command line argument: enable starting of workflow immediately after import?. <BR/>
     */
    public static final String AGENTARG_ENABLEWORKFLOW   = "-ENABLEWORKFLOW";
    /**
     * Agent command line argument: include the container info. <BR/>
     */
    public static final String AGENTARG_INCLUDECONTAINER = "-INCLUDECONTAINER";
    /**
     * Agent command line argument: type of connector. <BR/>
     */
    public static final String AGENTARG_CONNECTORTYPE    = "-CONNECTORTYPE";
    /**
     * Agent command line argument: name of ftp serer. <BR/>
     */
    public static final String AGENTARG_FTPSERVER        = "-FTPSERVER";
    /**
     * Agent command line argument: user name for ftp server. <BR/>
     */
    public static final String AGENTARG_FTPUSER          = "-FTPUSER";
    /**
     * Agent command line argument: password for ftp server. <BR/>
     */
    public static final String AGENTARG_FTPPASSWORD      = "-FTPPASSWORD";
    /**
     * Agent command line argument: path on ftp server. <BR/>
     */
    public static final String AGENTARG_FTPPATH          = "-FTPPATH";
    /**
     * Agent command line argument: name of mail server. <BR/>
     */
    public static final String AGENTARG_MAILSERVER       = "-MAILSERVER";
    /**
     * Agent command line argument: user name for mail server. <BR/>
     */
    public static final String AGENTARG_MAILUSER         = "-MAILUSER";
    /**
     * Agent command line argument: password for mail server. <BR/>
     */
    public static final String AGENTARG_MAILPASSWORD     = "-MAILPASSWORD";
    /**
     * Agent command line argument:. <BR/>
     */
    public static final String AGENTARG_MAILRECEIVER     = "-MAILRECEIVER";
    /**
     * Agent command line argument: sender of email message. <BR/>
     */
    public static final String AGENTARG_MAILSENDER       = "-MAILSENDER";
    /**
     * Agent command line argument: protocol to use for sending email. <BR/>
     */
    public static final String AGENTARG_MAILPROTOCOL     = "-MAILPROTOCOL";
    /**
     * Agent command line argument: url of SAP data. <BR/>
     */
    public static final String AGENTARG_SAPURL           = "-SAPURL";
    /**
     * Agent command line argument: name of SAP server. <BR/>
     */
    public static final String AGENTARG_SAPNAME          = "-SAPNAME";
    /**
     * Agent command line argument: url for http multipart protocol. <BR/>
     */
    public static final String AGENTARG_HTTPMULTIPARTURL = "-HTTPMULTIPARTURL";
    /**
     * Agent command line argument: working directory. <BR/>
     */
    public static final String AGENTARG_WORKDIR          = "-WORKDIR";
    /**
     * Agent command line argument: temporary directory. <BR/>
     */
    public static final String AGENTARG_TEMPDIR          = "-TEMPDIR";
    /**
     * Agent command line argument: error directory. <BR/>
     */
    public static final String AGENTARG_ERRORDIR         = "-ERRORDIR";
    /**
     * Agent command line argument: shall the agent make a retry in case of an error?. <BR/>
     */
    public static final String AGENTARG_RETRY            = "-RETRY";
    /**
     * Agent command line argument: perform notification of error? (YES | NO). <BR/>
     */
    public static final String AGENTARG_NOTIFY           = "-NOTIFY";
    /**
     * Agent command line argument: mail server for error notification. <BR/>
     */
    public static final String AGENTARG_NOTIFYMAILSERVER = "-NOTIFYMAILSERVER";
    /**
     * Agent command line argument: receiver for error notification. <BR/>
     */
    public static final String AGENTARG_NOTIFYRECEIVER   = "-NOTIFYRECEIVER";
    /**
     * Agent command line argument: sender for error notification. <BR/>
     */
    public static final String AGENTARG_NOTIFYSENDER     = "-NOTIFYSENDER";
    /**
     * Agent command line argument: subject for error notification. <BR/>
     */
    public static final String AGENTARG_NOTIFYSUBJECT    = "-NOTIFYSUBJECT";
    /**
     * Agent command line argument: shall queries be resolved during export?. <BR/>
     */
    public static final String AGENTARG_RESOLVEQUERY     = "-RESOLVEQUERY";
    /**
     * Agent command line argument: shall external ids be stored in key mapper table?. <BR/>
     */
    public static final String AGENTARG_RESTOREEXTERNALIDS = "-STOREEXTERNALIDS";
    /**
     * Agent command line argument: shall references be resolved?. <BR/>
     */
    public static final String AGENTARG_RESOLVEREFERENCE = "-RESOLVEREFERENCE";
    /**
     * Agent command line argument: shall reference oids be used during export?. <BR/>
     */
    public static final String AGENTARG_USEREFERENCESOID = "-USEREFERENCEOID";
    /**
     * Agent command line argument: shall the key mapping ids of the objects be exported, too?. <BR/>
     */
    public static final String AGENTARG_RESOLVEKEYMAPPING = "-RESOLVEKEYMAPPING";
    /**
     * Agent command line argument: kind of ordering: ASC | DESC. <BR/>
     */
    public static final String AGENTARG_SORT             = "-SORT";
    /**
     * Agent command line argument: nt domain for automated login. <BR/>
     */
    public static final String AGENTARG_NTDOMAIN         = "-NTDOMAIN";
    /**
     * Agent command line argument: nt user name for automated login. <BR/>
     */
    public static final String AGENTARG_NTUSER           = "-NTUSER";
    /**
     * Agent command line argument: nt password for automated login. <BR/>
     */
    public static final String AGENTARG_NTPW             = "-NTPW";

    /**
     * Value for agent CONNECTION command line argument: Active Server Pages. <BR/>
     */
    public static final String AGENTARG_CONNECTION_ASP      = "ASP";
    /**
     * Value for agent CONNECTION command line argument: Servlet. <BR/>
     */
    public static final String AGENTARG_CONNECTION_SERVLET  = "SERVLET";

    /**
     * Value for agent LOGGING command line argument: no logging. <BR/>
     */
    public static final String AGENTARG_LOGGING_NONE    = "NONE";
    /**
     * Value for agent LOGGING command line argument: create new log. <BR/>
     */
    public static final String AGENTARG_LOGGING_NEW     = "NEW";
    /**
     * Value for agent LOGGING command line argument: append to existing log. <BR/>
     */
    public static final String AGENTARG_LOGGING_APPEND  = "APPEND";

    /**
     * Value for agent FREQUENCY command line argument: perform agent operation only once. <BR/>
     */
    public static final String AGENTARG_FREQUENCY_ONCE      = "ONCE";
    /**
     * Value for agent FREQUENCY command line argument: minutes of agent start time. <BR/>
     */
    public static final String AGENTARG_FREQUENCY_MINUTES   = "MINUTES";
    /**
     * Value for agent FREQUENCY command line argument: day of agent start time. <BR/>
     */
    public static final String AGENTARG_FREQUENCY_DAY       = "DAY";
    /**
     * Value for agent FREQUENCY command line argument: week of agent start time. <BR/>
     */
    public static final String AGENTARG_FREQUENCY_WEEK      = "WEEK";
    /**
     * Value for agent FREQUENCY command line argument: month of agtent start time. <BR/>
     */
    public static final String AGENTARG_FREQUENCY_MONTH     = "MONTH";

    /**
     * Value for agent CONNECTORTYPE command line argument: file connector. <BR/>
     */
    public static final String AGENTARG_CONNECTORTYPE_FILE  = "FILE";
    /**
     * Value for agent CONNECTORTYPE command line argument: ftp connector. <BR/>
     */
    public static final String AGENTARG_CONNECTORTYPE_FTP   = "FTP";
    /**
     * Value for agent CONNECTORTYPE command line argument: mail connector. <BR/>
     */
    public static final String AGENTARG_CONNECTORTYPE_MAIL  = "MAIL";
    /**
     * Value for agent CONNECTORTYPE command line argument: sap connector. <BR/>
     */
    public static final String AGENTARG_CONNECTORTYPE_SAP   = "SAP";
    /**
     * Value for agent CONNECTORTYPE command line argument: http multipart connector. <BR/>
     */
    public static final String AGENTARG_CONNECTORTYPE_HTTPMULTIPART = "HTTPMULTIPART";

    /**
     * Value for agent MAILPROTOCOL command line argument: pop3 protocol. <BR/>
     */
    public static final String AGENTARG_MAILPROTOCOL_POP3   = "POP3";
    /**
     * Value for agent MAILPROTOCOL command line argument: imap protocol. <BR/>
     */
    public static final String AGENTARG_MAILPROTOCOL_IMAP   = "IMAP";

    /**
     * Agent command line argument: working directory. <BR/>
     */
    public static final String AGENTARG_VALIDATESTRUCTURE  = "-VALIDATE";

    /**
     * Frequency type: perform only once. <BR/>
     */
    public static final int FREQUENCYTYPE_ONCE      = 0;
    /**
     * Frequency type: interval minutes. <BR/>
     */
    public static final int FREQUENCYTYPE_MINUTES   = 1;
    /**
     * Frequency type: interval days. <BR/>
     */
    public static final int FREQUENCYTYPE_DAY       = 2;
    /**
     * Frequency type: interval weeks. <BR/>
     */
    public static final int FREQUENCYTYPE_WEEK      = 3;
    /**
     * Frequency type:interval month. <BR/>
     */
    public static final int FREQUENCYTYPE_MONTH     = 4;

    /**
     * Value for weekdays in command line argument every: Monday. <BR/>
     */
    public static final String WEEKDAY_MONDAY       = "MO";
    /**
     * Value for weekdays in command line argument every: Tuesday. <BR/>
     */
    public static final String WEEKDAY_TUESDAY      = "TU";
    /**
     * Value for weekdays in command line argument every: Wednesday. <BR/>
     */
    public static final String WEEKDAY_WEDNESDAY    = "WE";
    /**
     * Value for weekdays in command line argument every: Thursday. <BR/>
     */
    public static final String WEEKDAY_THURSDAY     = "TH";
    /**
     * Value for weekdays in command line argument every: Friday. <BR/>
     */
    public static final String WEEKDAY_FRIDAY       = "FR";
    /**
     * Value for weekdays in command line argument every: Saturday. <BR/>
     */
    public static final String WEEKDAY_SATURDAY     = "SA";
    /**
     * Value for weekdays in command line argument every: Sunday. <BR/>
     */
    public static final String WEEKDAY_SUNDAY       = "SU";

    /**
     * Valid value for -NOTIFY parameter: perform notification. <BR/>
     */
    public static final String NOTIFY_YES       = "YES";
    /**
     * Valid value for -NOTIFY parameter: no notifications. <BR/>
     */
    public static final String NOTIFY_NO        = "NO";

    /**
     * the name of the temporary directory for an agent. <BR/>
     */
    public static final String PATH_TEMP        = "temp";

    /**
     * the name of the errorfiles directory for an agent. <BR/>
     */
    public static final String PATH_ERRORFILES  = "errorfiles";

    /**
     * the name of error log file for an agent. <BR/>
     */
    public static final String FILENAME_ERRORLOG = "errorlog.txt";

    /**
     * Valid value for -SORT parameter. <BR/>
     * ASCendent ordering.
     */
    public static final String AGENTARG_SORT_ASC     = "ASC";

    /**
     * Valid value for -SORT parameter. <BR/>
     * DESCendent ordering.
     */
    public static final String AGENTARG_SORT_DESC    = "DESC";

} // class AgentConstants
