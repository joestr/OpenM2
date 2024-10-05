/*
 * Class: AgentTokens.java
 */

// package:
package ibs.di.agent;

// imports:


/******************************************************************************
 * Tokens for ibs.di business objects. <BR/>
 * This abstract class contains all tokens which are necessary to deal with
 * the business objects delivered within this package.
 *
 * @version     $Id: AgentTokens.java,v 1.18 2007/07/31 19:13:53 kreimueller Exp $
 *
 * @author      Bernd Buchegger (BB), 20000302
 ******************************************************************************
 */
public abstract class AgentTokens extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: AgentTokens.java,v 1.18 2007/07/31 19:13:53 kreimueller Exp $";


    /**
     * Token for processing import file. <BR/>
     */
    public static final String TOK_PROCESSING_IMPORTFILE      = "processing import file";

    /**
     * Token for processing import files. <BR/>
     */
    public static final String TOK_PROCESSING_IMPORTFILES      = "processing import file(s)";

    /**
     * Token for processing export file. <BR/>
     */
    public static final String TOK_PROCESSING_EXPORTFILE      = "processing export file";

    /**
     * Token for last activation at. <BR/>
     */
    public static final String TOK_LAST_ACTIVATION_AT         = "Last activation at";

    /**
     * Token for next activation at. <BR/>
     */
    public static final String TOK_NEXT_ACTIVATION_AT         = "Next activation at";

    /**
     * Token for filter for importfiles. <BR/>
     */
    public static final String TOK_FILEFILTER                  = "Filter for importfiles";

    /**
     * Token for work directory. <BR/>
     */
    public static final String TOK_WORKDIR                  = "Work directory";

    /**
     * Token for invoke import with User. <BR/>
     */
    public static final String TOK_INVOKE_IMPORT_WITH_USER    = "Invoke import with User";


    /**
     * Token for invoke export with User. <BR/>
     */
    public static final String TOK_INVOKE_EXPORT_WITH_USER    = "Invoke export with User";
    /**
     * Token for and DomainId. <BR/>
     */
    public static final String TOK_AND_DOMAINID               = "and DomainId";

    /**
     * Token for FTP-server. <BR/>
     */
    public static final String TOK_FTPSERVER                  = "FTP-server";

    /**
     * Token for with user. <BR/>
     */
    public static final String TOK_WITH_USER                  = "with user";

    /**
     * Token for with receiver. <BR/>
     */
    public static final String TOK_WITH_RECEIVER              = "with receiver";

    /**
     * Token for with sender. <BR/>
     */
    public static final String TOK_WITH_SENDER                = "with sender";

    /**
     * Token for using FTP-server path. <BR/>
     */
    public static final String TOK_USING_FTP_PATH             = "using FTP-server path";

    /**
     * Token for mail-server. <BR/>
     */
    public static final String TOK_MAILSERVER                 = "mail-server";

    /**
     * Token for mail-protocol. <BR/>
     */
    public static final String TOK_MAILPROTOCOL               = "mail-protocol";

    /**
     * Token for using importcontainer OID. <BR/>
     */
    public static final String TOK_USING_IMPORTCONTAINEROID   = "Using importcontainer OID";

    /**
     * Token for using importcontainer path. <BR/>
     */
    public static final String TOK_USING_IMPORTCONTAINERPATH  = "Using importcontainer path";

    /**
     * Token for on server. <BR/>
     */
    public static final String TOK_ON_SERVER                  = "On server";

    /**
     * Token for on local server. <BR/>
     */
    public static final String TOK_ON_LOCAL_SERVER            = "On local server";

    /**
     * Token for with application path. <BR/>
     */
    public static final String TOK_WITH_APPPATH               = "with application-path";

    /**
     * Token for using importscript OID. <BR/>
     */
    public static final String TOK_USING_IMPORTSCRIPTOID      = "Using ImportScript OID";

    /**
     * Token for using importscript name. <BR/>
     */
    public static final String TOK_USING_IMPORTSCRIPTNAME      = "Using ImportScript name";

    /**
     * Token for using translator OID. <BR/>
     */
    public static final String TOK_USING_TRANSLATOROID        = "Using Translator OID";

    /**
     * Token for. <BR/>
     */
    public static final String TOK_USING_TRANSLATORNAME       = "Using Translator name";

    /**
     * Token for. <BR/>
     */
    public static final String TOK_AGENTS_ACTIVATED_EVERY     = "Agent will be activated every";

    /**
     * Token for. <BR/>
     */
    public static final String TOK_MINUTES                    = "minutes";

    /**
     * Token for agent will be activated every day at. <BR/>
     */
    public static final String TOK_AGENTS_ACTIVATED_EVERY_DAY = "Agent will be activated every day at";

    /**
     * Token for agent will be activated at. <BR/>
     */
    public static final String TOK_AGENTS_ACTIVATED_AT       = "Agent will be activated at";

    /**
     * Token for at the following days of the week. <BR/>
     */
    public static final String TOK_AT_WEEKDAYS               = "at the following days of the week";

    /**
     * Token for every. <BR/>
     */
    public static final String TOK_EVERY                    = "every";

    /**
     * Token for day of month. <BR/>
     */
    public static final String TOK_DAY_OF_MONTH             = "day of a month";

    /**
     * Token for name for logfile. <BR/>
     */
    public static final String TOK_LOGFILENAME              = "Name for logfile";

    /**
     * Token for path for logfile. <BR/>
     */
    public static final String TOK_LOGFILEPATH              = "Path for logfile";

    /**
     * Token for the exportobjects. <BR/>
     */
    public static final String TOK_OBJECT_FOR_EXPORT        = "Export object";

    /**
     * Token for the name of the SAP business connector gateway. <BR/>
     */
    public static final String TOK_SAPNAME                  = "SAP server name";

    /**
     * Token for the name of the SAP server. <BR/>
     */
    public static final String TOK_SAPURL                   = "SAP Business Connector gateway URL";

    /**
     * Token for the target URL of the HTTP multipart connector. <BR/>
     */
    public static final String TOK_HTTPMULTIPARTURL         = "HTTP Multipart Connector target URL";

    /**
     * Token for the temp dir. <BR/>
     */
    public static final String TOK_TEMPDIR                  = "Temporary directory";

    /**
     * Token for the errorfiles dir. <BR/>
     */
    public static final String TOK_ERRORFILESDIR            = "Directory to write error files to";

    /**
     * Token for number of retries. <BR/>
     */
    public static final String TOK_RETRIES                  = "Number of retries";

    /**
     * Token for activated error notification. <BR/>
     */
    public static final String TOK_NOTIFY_ACTIVATED         = "Error notification is activated";

    /**
     * Token for deactivated error notification. <BR/>
     */
    public static final String TOK_NOTIFY_DEACTIVATED       = "Error notification is deactivated.";

    /**
     * Token for error notification mail subject. <BR/>
     */
    public static final String TOK_MAILSUBJECT              = "subject text";

    /**
     * Token for number of retry. <BR/>
     */
    public static final String TOK_TRY                      = "try";

    /**
     * Token for name of error log file. <BR/>
     */
    public static final String TOK_ERRORLOGFILENAME         = "Agent errorlog file";

    /**
     * Token for name of error log file. <BR/>
     */
    public static final String TOK_LOGFILE  = "Exportlog file";

    /**
     * Token for the working directory. <BR/>
     */
    public static final String TOK_WORKDIRECTORY = "Working directory";

    /**
     * Token for using connector oid. <BR/>
     */
    public static final String TOK_USING_CONNECTOROID = "Using Connector OID";
    /**
     * Token for using connector name. <BR/>
     */
    public static final String TOK_USING_CONNECTORNAME = "Using Connector name";

    /**
     * Token for using backup connector oid. <BR/>
     */
    public static final String TOK_USING_BACKUPCONNECTOROID =
        "Using Backup Connector OID";
    /**
     * Token for using backup connector name. <BR/>
     */
    public static final String TOK_USING_BACKUPCONNECTORNAME =
        "Using Backup Connector name";

    /**
     * Token for user credentials. <BR/>
     */
    public static final String TOK_NT_USERCREDENTIALS = "Using user credentials";

} // class AgentTokens
