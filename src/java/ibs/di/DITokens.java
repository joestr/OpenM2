/*
 * Class: DITokens.java
 */

// package:
package ibs.di;

import ibs.bo.BOTokens;

// imports:


/******************************************************************************
 * Tokens for ibs.di business objects. <BR/>
 * This abstract class contains all tokens which are necessary to deal with
 * the business objects delivered within this package.
 *
 * @version     $Id: DITokens.java,v 1.52 2010/05/20 12:13:16 btatzmann Exp $
 *
 * @author      Bernd Buchegger (BB), 990104
 ******************************************************************************
 */
public abstract class DITokens extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DITokens.java,v 1.52 2010/05/20 12:13:16 btatzmann Exp $";

    /**
     * Name of bundle where the tokens included. <BR/>
     */
    public static String TOK_BUNDLE = BOTokens.TOK_BUNDLE;

    /**
     * Token: name of importfile. <BR/>
     */
    public static String ML_IMPORTFILE   = "ML_IMPORTFILE";

    /**
     * Token: name of importscript file. <BR/>
     */
    public static String ML_IMPORTSCRIPT     = "ML_IMPORTSCRIPT";

    /**
     * Token: import funktion. <BR/>
     */
    public static String ML_FUNCTIONIMPORT   = "ML_FUNCTIONIMPORT";

    /**
     * Token: attachment file name. <BR/>
     */
    public static String ML_ATTACHMENTFILE   = "ML_ATTACHMENTFILE";

    /**
     * Token: name of import file path. <BR/>
     */
    public static String ML_IMPORTPATH       = "ML_IMPORTPATH";

    /**
     * Token: name of import script file path. <BR/>
     */
    public static String ML_IMPORTSCRIPTPATH = "ML_IMPORTSCRIPTPATH";

    /**
     * Token: name of the file to be uploaded and used for import. <BR/>
     */
    public static String ML_UPLOADFILE       = "ML_UPLOADFILE";

    /**
     * Token: importsource. <BR/>
     */
    public static String ML_IMPORTSOURCE  = "ML_IMPORTSOURCE";

    /**
     * Token: use dir as importsource. <BR/>
     */
    public static String ML_IMPORTSOURCEDIR  = "ML_IMPORTSOURCEDIR";

    /**
     * Token: use connector as importsource. <BR/>
     */
    public static String ML_IMPORTSOURCECONNECTOR = "ML_IMPORTSOURCECONNECTOR";

    /**
     * Token: name of import file path. <BR/>
     */
    public static String ML_IMPORTSOURCEUPLOAD = "ML_IMPORTSOURCEUPLOAD";

    /**
     * Token: delete the import file after import? <BR/>
     */
    public static String ML_DELETEIMPORTFILE = "ML_DELETEIMPORTFILE";

        /**
     * Token: import path sezten. <BR/>
     */
    public static String ML_SETIMPORTPATH    = "ML_SETIMPORTPATH";

    /**
     * Token: exported. <BR/>
     */
    public static String ML_IMPORTED = "ML_IMPORTED";

    /**
     * Token: exported. <BR/>
     */
    public static String ML_NOTIMPORTED = "ML_NOTIMPORTED";

    /**
     * Token: export funktion. <BR/>
     */
    public static String ML_FUNCTIONEXPORT   = "ML_FUNCTIONEXPORT";

    /**
     * Token: name of exportfile. <BR/>
     */
    public static String ML_EXPORTFILE       = "ML_EXPORTFILE";

    /**
     * Token: name of export directory. <BR/>
     */
    public static String ML_EXPORTPATH       = "ML_EXPORTPATH";

    /**
     * Token: name of export destination url. <BR/>
     */
    public static String ML_EXPORTURL       = "ML_EXPORTURL";

    /**
     * Token: export content of a container. <BR/>
     */
    public static String ML_EXPORTCONTENT       = "ML_EXPORTCONTENT";

    /**
     * Token: export content of all subcontainers. <BR/>
     */
    public static String ML_EXPORTCONTENTRECURSIVE = "ML_EXPORTCONTENTRECURSIVE";

    /**
     * Token: prettyprint the export documents. <BR/>
     */
    public static String ML_EXPORTPRETTYPRINT     = "ML_EXPORTPRETTYPRINT";

    /**
     * Token: prettyprint the export documents. <BR/>
     */
    public static String ML_EXPORTCONTAINER     = "ML_EXPORTCONTAINER";

    /**
     * Token: prettyprint the export documents. <BR/>
     */
    public static String ML_EXPORTSINGLEFILE     = "ML_EXPORTSINGLEFILE";

    /**
     * Token: export content of all subcontainers. <BR/>
     */
    public static String ML_EXPORTDESTINATIONDIR = "ML_EXPORTDESTINATIONDIR";

    /**
     * Token: export content of all subcontainers. <BR/>
     */
    public static String ML_EXPORTDESTINATIONURL = "ML_EXPORTDESTINATIONURL";

    /**
     * Token: exported. <BR/>
     */
    public static String ML_EXPORTED = "ML_EXPORTED";

    /**
     * Token: exported. <BR/>
     */
    public static String ML_NOTEXPORTED = "ML_NOTEXPORTED";

    /**
     * Token: object could not be added to the export document . <BR/>
     */
    public static String ML_COULDNOTADDTOEXPORT = "ML_COULDNOTADDTOEXPORT";

    /**
     * Token: object has been added to the export document . <BR/>
     */
    public static String ML_ADDEDTOEXPORT = "ML_ADDEDTOEXPORT";

    /**
     * Token: was deleted after export. <BR/>
     */
    public static String ML_DELETEDAFTEREXPORT = "ML_DELETEDAFTEREXPORT";

    /**
     * Token: could not be deleted after export. <BR/>
     */
    public static String ML_NOT_DELETED = "ML_NOT_DELETED";

    /**
     * Token: log. <BR/>
     */
    public static String ML_LOG = "ML_LOG";

    /**
     * Token: no import script. <BR/>
     */
    public static String ML_NOIMPORTSCRIPT = "ML_NOIMPORTSCRIPT";

    /**
     * Token: Parser fatal error. <BR/>
     */
    public static String ML_PARSERFATALERROR = "ML_PARSERFATALERROR";

    /**
     * Token: Parser error. <BR/>
     */
    public static String ML_PARSERERROR = "ML_PARSERERROR";

    /**
     * Token: Parser Warning. <BR/>
     */
    public static String ML_PARSERWARNING = "ML_PARSERWARNING";

    /**
     * Token: at line. <BR/>
     */
    public static String ML_AT_LINE = "ML_AT_LINE";

    /**
     * Token: column. <BR/>
     */
    public static String ML_COLUMN = "ML_COLUMN";

    /**
     * Token: xml template file. <BR/>
     */
    public static String ML_TEMPLATE = "ML_TEMPLATE";

    /**
     * Token: name of xml template file. <BR/>
     */
    public static String ML_TEMPLATENAME = "ML_TEMPLATENAME";

    /**
     * Token: path for xml template file. <BR/>
     */
    public static String ML_TEMPLATEPATH = "ML_TEMPLATEPATH";

    /**
     * Token: xml workflow template file. <BR/>
     */
    public static String ML_WORKFLOWTEMPLATE = "ML_WORKFLOWTEMPLATE";

    /**
     * Token: name of xml workflow template file. <BR/>
     */
    public static String ML_WORKFLOWTEMPLATENAME = "ML_WORKFLOWTEMPLATENAME";

    /**
     * Token: path for xml workflow template file. <BR/>
     */
    public static String ML_WORKFLOWTEMPLATEPATH = "ML_WORKFLOWTEMPLATEPATH";

    /**
     * Token: workflow allowed. <BR/>
     */
    public static String ML_WORKFLOWALLOWED = "ML_WORKFLOWALLOWED";

    /**
     * Token: use standard information. <BR/>
     */
    public static String ML_USESTANDARDHEADER = "ML_USESTANDARDHEADER";

    /**
     * Token: use alternativ header fields. <BR/>
     */
    public static String ML_HEADERFIELDS = "ML_HEADERFIELDS";

    /**
     * Token: alternativ header fields. <BR/>
     */
    public static String ML_NONSTANDARDHEADER = "ML_NONSTANDARDHEADER";

    /**
     * Token: error. <BR/>
     */
    public static String ML_ERROR = "ML_ERROR";

    /**
     * Token: warning. <BR/>
     */
    public static String ML_WARNING = "ML_WARNING";
    
    /**
     * Hardcoded token: error. <BR/>
     * 
     * Can be used if no multilingual functionality is available,
     * or should not be used (e.g. log files, ...).
     */
    public static String TOK_ERROR = "FEHLER";

    /**
     * Hardcoded token: warning. <BR/>
     * 
     * Can be used if no multilingual functionality is available,
     * or should not be used (e.g. log files, ...).
     */
    public static String TOK_WARNING = "WARNUNG";

    /**
     * Token: writing a log file. <BR/>
     */
    public static String ML_WRITELOGFILE = "ML_WRITELOGFILE";

    /**
     * Token: displaying the log file. <BR/>
     */
    public static String ML_DISPLAYLOGFILE = "ML_DISPLAYLOGFILE";

    /**
     * Token: append the log file. <BR/>
     */
    public static String ML_APPENDLOGFILE = "ML_APPENDLOGFILE";

    /**
     * Token: name of the log file. <BR/>
     */
    public static String ML_LOGFILENAME = "ML_LOGFILENAME";

    /**
     * Token: path of the log file. <BR/>
     */
    public static String ML_LOGFILEPATH = "ML_LOGFILEPATH";

    /**
     * Token: select the connector. <BR/>
     */
    public static String ML_SELECTCONNECTOR = "ML_SELECTCONNECTOR";

    /**
     * Token: check the connector. <BR/>
     */
    public static String ML_SETCONNECTOR = "ML_SETCONNECTOR";

    /**
     * Token: connector type. <BR/>
     */
    public static String ML_CONNECTORTYPE = "ML_CONNECTORTYPE";

    /**
     * Token: select translator. <BR/>
     */
    public static String ML_SELECTTRANSLATOR = "ML_SELECTTRANSLATOR";

    /**
     * Token: select importscript. <BR/>
     */
    public static String ML_SELECTIMPORTSCRIPT = "ML_SELECTIMPORTSCRIPT";

    /**
     * Token: select  importfile. <BR/>
     */
    public static String ML_SELECTIMPORTFILE   = "ML_SELECTIMPORTFILE";

    /**
     * Token: create a backup for import. <BR/>
     */
    public static String ML_CREATEIMPORTBACKUP = "ML_CREATEIMPORTBACKUP";

    /**
     * Token: create a backup for export. <BR/>
     */
    public static String ML_CREATEEXPORTBACKUP = "ML_CREATEEXPORTBACKUP";

    /**
     * Token: select the backup connector. <BR/>
     */
    public static String ML_SELECTBACKUPCONNECTOR = "ML_SELECTBACKUPCONNECTOR";

    /**
     * Token: check the backup connector. <BR/>
     */
    public static String ML_SETBACKUPCONNECTOR = "ML_SETBACKUPCONNECTOR";

    /**
     * Token: backup connector type. <BR/>
     */
    public static String ML_BACKUPCONNECTORTYPE = "ML_BACKUPCONNECTORTYPE";

    /**
     * Token: connector. <BR/>
     */
    public static String ML_CONNECTOR = "ML_CONNECTOR";

    /**
     * Token: translator. <BR/>
     */
    public static String ML_TRANSLATOR = "ML_TRANSLATOR";

    /**
     * Token: filter. <BR/>
     */
    public static String ML_FILTER = "ML_FILTER";

    /**
     * Token: backup connector. <BR/>
     */
    public static String ML_BACKUPCONNECTOR = "ML_BACKUPCONNECTOR";

    /**
     * Token: importinterface
     */
    public static String ML_IMPORTINTERFACE = "ML_IMPORTINTERFACE";

    /**
     * Token: exportinterface
     */
    public static String ML_EXPORTINTERFACE = "ML_EXPORTINTERFACE";

    /**
     * Token: accept the interface. <BR/>
     */
    public static String ML_TAKEINTERFACE = "ML_TAKEINTERFACE";


    /**
     * Token: something has been translated. <BR/>
     */
    public static String ML_TRANSLATED = "ML_TRANSLATED";

    /**
     * Token: something could no be translated. <BR/>
     */
    public static String ML_NOT_TRANSLATED = "ML_NOT_TRANSLATED";

    /**
     * Token: close windows (used for button label). <BR/>
     */
    public static String ML_CLOSE = "ML_CLOSE";

    /**
     * Token: number of objects found. Used for entry in log hwo many import
     * objects have been found in the import document.<BR/>
     */
    public static String ML_OBJECTS_FOUND = "ML_OBJECTS_FOUND";

    /**
     * Token: import has been startet at (date time). <BR/>
     */
    public static String ML_IMPORT_STARTED_AT = "ML_IMPORT_STARTED_AT";

    /**
     * Token: export has been startet at (date time). <BR/>
     */
    public static String ML_EXPORT_STARTED_AT = "ML_EXPORT_STARTED_AT";


    /**
     * Token: export has been finished at (date time). <BR/>
     */
    public static String ML_IMPORT_FINISHED_AT = "ML_IMPORT_FINISHED_AT";

    /**
     * Token: import has been finished at (date time). <BR/>
     */
    public static String ML_EXPORT_FINISHED_AT = "ML_EXPORT_FINISHED_AT";

    /**
     * Token: seconds. <BR/>
     */
    public static String ML_SECONDS = "ML_SECONDS";

    /**
     * Token: time still remaining. <BR/>
     */
    public static String ML_TIME_REMAINING = "ML_TIME_REMAINING";

    /**
     * Token: total amount of time the import consumed. <BR/>
     */
    public static String ML_IMPORT_DURATION = "ML_IMPORT_DURATION";

    /**
     * Token: total amount of time the export consumed. <BR/>
     */
    public static String ML_EXPORT_DURATION = "ML_EXPORT_DURATION";

    /**
     * Token: file name. <BR/>
     */
    public static String ML_FILENAME = "ML_FILENAME";

    /**
     * Token: path. <BR/>
     */
    public static String ML_PATH = "ML_PATH";

    /**
     * Token: path for backup. <BR/>
     */
    public static String ML_BACKUPPATH = "ML_BACKUPPATH";

    /**
     * Token: add. <BR/>
     */
    public static String ML_ADD = "ML_ADD";

    /**
     * Token: delete. <BR/>
     */
    public static String ML_DELETE = "ML_DELETE";

    /**
     * Token: uploaded files. <BR/>
     */
    public static String ML_UPLOADEDFILES = "ML_UPLOADEDFILES";

    /**
     * Token: multiple upload. <BR/>
     */
    public static String ML_MULTIPLEUPLOAD  = "ML_MULTIPLEUPLOAD";

    /**
     * Token: connectortypename. <BR/>
     */
    public static String ML_CONNECTORTYPENAME = "ML_CONNECTORTYPENAME";

    /**
     * Token: serverdirectory. <BR/>
     */
    public static String ML_SERVERDIRECTORY = "ML_SERVERDIRECTORY";

    /**
     * Token: isImportConnector. <BR/>
     */
    public static String ML_ISIMPORTCONNECTOR = "ML_ISIMPORTCONNECTOR";

    /**
     * Token: isImportConnector. <BR/>
     */
    public static String ML_ISEXPORTCONNECTOR = "ML_ISEXPORTCONNECTOR";

    /**
     * Token for name of connector type: NONE. <BR/>
     */
    public static String ML_CONNECTORTYPENAME_NONE   = "ML_CONNECTORTYPENAME_NONE";
    /**
     * Token for name of connector type: FILE. <BR/>
     */
    public static String ML_CONNECTORTYPENAME_FILE   = "ML_CONNECTORTYPENAME_FILE";
    /**
     * Token for name of connector type: FTP. <BR/>
     */
    public static String ML_CONNECTORTYPENAME_FTP    = "ML_CONNECTORTYPENAME_FTP";
    /**
     * Token for name of connector type: EMAIL. <BR/>
     */
    public static String ML_CONNECTORTYPENAME_EMAIL  = "ML_CONNECTORTYPENAME_EMAIL";
    /**
     * Token for name of connector type: DB. <BR/>
     */
    public static String ML_CONNECTORTYPENAME_DB     = "ML_CONNECTORTYPENAME_DB";

    /**
     * Token for ftp connector: server name. <BR/>
     */
    public static String ML_FTPSERVER          = "ML_FTPSERVER";
    /**
     * Token for ftp connector: path on ftp server. <BR/>
     */
    public static String ML_FTPPATH            = "ML_FTPPATH";
    /**
     * Token for ftp connector: user name. <BR/>
     */
    public static String ML_FTPUSER            = "ML_FTPUSER";
    /**
     * Token for ftp connector: password. <BR/>
     */
    public static String ML_FTPUSERPASSWORD    = "ML_FTPUSERPASSWORD";

    /**
     * Token for mail connector: name of mail server. <BR/>
     */
    public static String ML_MAILSERVER         = "ML_MAILSERVER";
    /**
     * Token for mail connector: user name. <BR/>
     */
    public static String ML_MAILUSER           = "ML_MAILUSER";
    /**
     * Token for mail connector: password. <BR/>
     */
    public static String ML_MAILPASSWORD       = "ML_MAILPASSWORD";
    /**
     * Token for mail connector: address of sender. <BR/>
     */
    public static String ML_MAILSENDER         = "ML_MAILSENDER";
    /**
     * Token for mail connector: address of recipient. <BR/>
     */
    public static String ML_MAILRECIPIENT      = "ML_MAILRECIPIENT";
    /**
     * Token for mail connector: addresses of carbon copy recipients. <BR/>
     */
    public static String ML_MAILCC             = "ML_MAILCC";
    /**
     * Token for mail connector: subject. <BR/>
     */
    public static String ML_MAILSUBJECT        = "ML_MAILSUBJECT";
    /**
     * Token for mail connector: protocol. <BR/>
     */
    public static String ML_MAILPROTOCOL       = "ML_MAILPROTOCOL";

    /**
     * Token for filter name: standard m2 xml filter. <BR/>
     */
    public static String ML_FILTER_M2XML       = "ML_FILTER_M2XML";
    /**
     * Token for filter name: oagis order format. <BR/>
     */
    public static String ML_FILTER_OAGISPO     = "ML_FILTER_OAGISPO";
    /**
     * Token for filter name: Ariba cXML order format. <BR/>
     */
    public static String ML_FILTER_CXMLPO      = "ML_FILTER_CXMLPO";
    /**
     * Token for filter name: CommerceOne xCBL order format. <BR/>
     */
    public static String ML_FILTER_XCBLPO      = "ML_FILTER_XCBLPO";
    /**
     * Token for filter name: SAP XML RFC customer order. <BR/>
     */
    public static String ML_FILTER_SAPXMLRFC   = "ML_FILTER_SAPXMLRFC";


    /**
     * Token for the url of a http server. <BR/>
     */
    public static String ML_HTTPSERVERURL      = "ML_HTTPSERVERURL";
    /**
     * Token for the type of a http server. <BR/>
     */
    public static String ML_HTTPSERVERTYPE     = "ML_HTTPSERVERTYPE";

    /**
     * Token for EDISwitchConnector: server address. <BR/>
     */
    public static String ML_EDISERVER          = "ML_EDISERVER";
    /**
     * Token for EDISwitchConnector: server port. <BR/>
     */
    public static String ML_EDISERVERPORT      = "ML_EDISERVERPORT";
    /**
     * Token for EDISwitchConnector: user name. <BR/>
     */
    public static String ML_EDIUSER            = "ML_EDIUSER";
    /**
     * Token for EDISwitchConnector: password. <BR/>
     */
    public static String ML_EDIPASSWORD        = "ML_EDIPASSWORD";
    /**
     * Token for EDISwitchConnector: id of the sender. <BR/>
     */
    public static String ML_EDISENDERID        = "ML_EDISENDERID";
    /**
     * Token for EDISwitchConnector: id of the receiver. <BR/>
     */
    public static String ML_EDIRECEIVERID      = "ML_EDIRECEIVERID";
    /**
     * Token for EDISwitchConnector: reference to application. <BR/>
     */
    public static String ML_EDIAPPLICATIONREF  = "ML_EDIAPPLICATIONREF";

    /**
     * Token for HTTPScriptConnector: address for import. <BR/>
     */
    public static String ML_READURL             = "ML_READURL";
    /**
     * Token for HTTPScriptConnector: address for file list. <BR/>
     */
    public static String ML_DIRURL              = "ML_DIRURL";
    /**
     * Token for HTTPScriptConnector: delimiter in file list. <BR/>
     */
    public static String ML_DIRFILEDELIMITER    = "ML_DIRFILEDELIMITER";
    /**
     * Token for HTTPScriptConnector: address for export. <BR/>
     */
    public static String ML_WRITEURL            = "ML_WRITEURL";
    /**
     * Token for HTTPScriptConnector: mimetpye for export. <BR/>
     */
    public static String ML_EXPORTMIMETYPE      = "ML_EXPORTMIMETYPE";
    /**
     * Token for HTTPScriptConnector: name of export file. <BR/>
     */
    public static String ML_EXPORTFILENAME      = "ML_EXPORTFILENAME";

    /**
     * Token for a dummy import file name. <BR/>
     */
    public static String ML_DUMMYIMPORTFILENAME = "ML_DUMMYIMPORTFILENAME";

    /**
     * Token for hierarchical export option field. <BR/>
     */
    public static String ML_HIERARCHICALEXPORT = "ML_HIERARCHICALEXPORT";

    /**
     * Token for hierarchical export option info text. <BR/>
     */
    public static String ML_HIERARCHICALEXPORTNOTE = "ML_HIERARCHICALEXPORTNOTE";

    /**
     * Token for hint. <BR/>
     */
    public static String ML_HINT = "ML_HINT";

    /**
     * Token for activating the workflow when object is
     * created in an xmlviewercontainer that has a workflow
     * template associated.<BR/>
     */
    public static String ML_ENABLEWORKFLOW = "ML_ENABLEWORKFLOW";

    /**
     * Token for SAPBCXMLRFCConnector: url of SAP Business Connector server. <BR/>
     */
    public static String ML_SAPBCSERVERURL     = "ML_SAPBCSERVERURL";
    /**
     * Token for SAPBCXMLRFCConnector: name of SAP Business Connector server. <BR/>
     */
    public static String ML_SAPBCSERVERNAME    = "ML_SAPBCSERVERNAME";
    /**
     * Token for SAPBCXMLRFCConnector: transport envelope for SAP Business Connector. <BR/>
     */
    public static String ML_SAPBCENVELOPE      = "ML_SAPBCENVELOPE";


    /**
     * Token for ASCIITranslator: separator. <BR/>
     */
    public static String ML_SEPARATOR            = "ML_SEPARATOR";
    /**
     * Token for ASCIITranslator: escape character for separator. <BR/>
     */
    public static String ML_ESCAPESEPARATOR      = "ML_ESCAPESEPARATOR";
    /**
     * Token for ASCIITranslator: does the file include meta data? <BR/>
     */
    public static String ML_ISINCLUDEMETADATA    = "ML_ISINCLUDEMETADATA";
    /**
     * Token for ASCIITranslator: does the file include a header line? <BR/>
     */
    public static String ML_ISINCLUDEHEADER      = "ML_ISINCLUDEHEADER";

    /**
     * Token for buttons in data interchange dialogs: start import. <BR/>
     */
    public static String ML_BUTTON_START_IMPORT    = "ML_BUTTON_START_IMPORT";
    /**
     * Token for buttons in data interchange dialogs: start export. <BR/>
     */
    public static String ML_BUTTON_START_EXPORT    = "ML_BUTTON_START_EXPORT";
    /**
     * Token for buttons in data interchange dialogs: back to previous view. <BR/>
     */
    public static String ML_BUTTON_BACK            = "ML_BUTTON_BACK";
    /**
     * Token for buttons in data interchange dialogs: start multiple upload. <BR/>
     */
    public static String ML_BUTTON_MULTIPLE_UPLOAD = "ML_BUTTON_MULTIPLE_UPLOAD";
    /**
     * Token for buttons in data interchange dialogs: use selected interface. <BR/>
     */
    public static String ML_SETINTERFACE           = "ML_SETINTERFACE";

    /**
     * Token for file filter . <BR/>
     */
    public static String ML_FILEFILTER = "ML_FILEFILTER";

    /**
     * Token for tab. <BR/>
     */
    public static String ML_TAB = "ML_TAB";

    /**
     * Token if there was no query data found to fill the selectionbox. <BR/>
     */
    public static String ML_NORESULT_FORQUERYSELECTIONBOX = "ML_NORESULT_FORQUERYSELECTIONBOX";

    /**
     * Token for interface: filter. <BR/>
     */
    public static String ML_INTERFACE_FILTER           = "ML_INTERFACE_FILTER";
    /**
     * Token for interface: connector. <BR/>
     */
    public static String ML_INTERFACE_CONNECTOR        = "ML_INTERFACE_CONNECTOR";
    /**
     * Token for interface: translator. <BR/>
     */
    public static String ML_INTERFACE_TRANSLATOR       = "ML_INTERFACE_TRANSLATOR";
    /**
     * Token for interface: preserve hierarchical structures? <BR/>
     */
    public static String ML_INTERFACE_HIERARCHICAL     = "ML_INTERFACE_HIERARCHICAL";
    /**
     * Token for interface: include sub objects within export? <BR/>
     */
    public static String ML_INTERFACE_EXPORTCONTAINER  = "ML_INTERFACE_EXPORTCONTAINER";
    /**
     * Token for interface: create recursive export? <BR/>
     */
    public static String ML_INTERFACE_RECURSIVE        = "ML_INTERFACE_RECURSIVE";
    /**
     * Token for interface: export everything into one file? <BR/>
     */
    public static String ML_INTERFACE_SINGLEFILE       = "ML_INTERFACE_SINGLEFILE";
    /**
     * Token for interface: form xml result file? <BR/>
     */
    public static String ML_INTERFACE_PRETTYPRINT      = "ML_INTERFACE_PRETTYPRINT";
    /**
     * Token for interface: write external keys to export file? <BR/>
     */
    public static String ML_INTERFACE_KEYMAPPING       = "ML_INTERFACE_KEYMAPPING";
    /**
     * Token for interface: delete import file after import?. <BR/>
     */
    public static String ML_INTERFACE_DELETE_IMPORT    = "ML_INTERFACE_DELETE_IMPORT";
    /**
     * Token for interface: start workflow immediately after import? <BR/>
     */
    public static String ML_INTERFACE_ENABLE_WORKFLOW  = "ML_INTERFACE_ENABLE_WORKFLOW";

    /**
     * Tokens for formtemplates to show the DOM tree. <BR/>
     */
    public static String ML_SHOWDOMTREE = "ML_SHOWDOMTREE";

    /**
     * Token for default translator name. <BR/>
     * This name is a token which should the user initiate to set an
     * valid filename.
     */
    public static String ML_CHANGE_TO_VALID_FILENAME = "ML_CHANGE_TO_VALID_FILENAME";

    /**
     * Token for log file of translation. <BR/>
     */
    public static String ML_TRANSLATION_LOG_NAME = "ML_TRANSLATION_LOG_NAME";

    /**
     * Token for the extension of the translated file. <BR/>
     * This extension is used for the file which is the output of the
     * translator.
     */
    public static String ML_TRANSEXTENSION = "ML_TRANSEXTENSION";

    /**
     * Token for restoring external IDs. <BR/>
     */
    public static String ML_ISRESTOREEXTERNALID = "ML_ISRESTOREEXTERNALID";

    /**
     * Token for resolving the result of a query. <BR/>
     */
    public static String ML_ISRESOLVEQUERY = "ML_ISRESOLVEQUERY";

    /**
     * Token for resolving the reference as object. <BR/>
     */
    public static String ML_ISRESOLVEREFERENCE = "ML_ISRESOLVEREFERENCE";

    /**
     * Token for resolving the reference as object with
     * the oid of the reference. <BR/>
     */
    public static String ML_ISUSEREFERENCEOID = "ML_ISUSEREFERENCEOID";

    /**
     * Token: resolve the keymapping on export. <BR/>
     */
    public static String ML_ISRESOLVEKEYMAPPING = DITokens.ML_INTERFACE_KEYMAPPING;

    /**
     * Token: resolve the keymapping on export. <BR/>
     * Note that this token has been replaced by ML_ISRESOLVEKEYMAPPING
     */
    public static String ML_EXPORTRESOLVEKEYMAPPING = DITokens.ML_INTERFACE_KEYMAPPING;

    /**
     *  Token: function for SAPBC connector.<BR/>
     */
    public static String ML_SAPBCFUNCTION = "ML_SAPBCFUNCTION";

    /**
     *  Token: content-type for SAPBC connector.<BR/>
     */
    public static String ML_SAPBCCONTENTTYPE = "ML_SAPBCCONTENTTYPE";

    /**
     *  Token: username for SAPBC connector.<BR/>
     */
    public static String ML_SAPBCUSERNAME = "ML_SAPBCUSERNAME";

    /**
     *  Token: password for SAPBC connector.<BR/>
     */
    public static String ML_SAPBCPASSWORD = "ML_SAPBCPASSWORD";

    /**
     *  Token: use name and type for key mapping.<BR/>
     */
    public static String ML_NAMETYPEMAPPING = "ML_NAMETYPEMAPPING";

    /**
     *  Token: keymapping id pattern.<BR/>
     */
    public static String ML_IDPATTERN = "ML_IDPATTERN";

    /**
     *  Token: keymapping domain pattern.<BR/>
     */
    public static String ML_DOMAINPATTERN = "ML_DOMAINPATTERN";

    /**
     *  Token: activate the structure validation.<BR/>
     */
    public static String ML_VALIDATESTRUCTURE = "ML_VALIDATESTRUCTURE";

    /**
     *  Token: activate the structure validation.<BR/>
     */
    public static String ML_INVALID_IMPORT_FIELD = "ML_INVALID_IMPORT_FIELD";

    /**
     *  Token: activate the structure validation.<BR/>
     */
    public static String ML_INVALID_IMPORT_FIELDTYPE = "ML_INVALID_IMPORT_FIELDTYPE";

    /**
     *  Token: force using a type translator.<BR/>
     */
    public static String ML_FORCE_TRANSLATION  = "ML_FORCE_TRANSLATION";

    /**
     *  Token: force using a type translator.<BR/>
     */
    public static String ML_FORCE_TRANSLATOR   = "ML_FORCE_TRANSLATOR";

    /**
     * Token for the document template form: form type code. <BR/>
     */
    public static String ML_FORMTYPECODE       = "ML_FORMTYPECODE";

    /**
     * Name of the DB mapping table. <BR/>
     */
    public static String ML_DBMAPPING_TABLE    = "ML_DBMAPPING_TABLE";

    /**
     * Token for the document template form: form type. <BR/>
     */
    public static String ML_FORMTYPE       = "ML_FORMTYPE";

    /**
     * Token for the document template form: activate db mapping. <BR/>
     */
    public static String ML_DBMAPPED           = "ML_DBMAPPED";

    /**
     * Token for the document template form: update database mapping. <BR/>
     */
    public static String ML_UPDATEMAPPING  = "ML_UPDATEMAPPING";

    /**
     * Token for mapping system values when update database mapping. <BR/>
     */
    public static String ML_UPDATEMAPPING_SYSTEM = "ML_UPDATEMAPPING_SYSTEM";


} // class DITokens
