/*
 * Class: DIMessages.java
 */

// package:
package ibs.di;

// imports:
import ibs.bo.BOMessages;


/******************************************************************************
 * Messages for ibs.di business objects. <BR/>
 * This abstract class contains all tokens which are necessary to deal with
 * the business objects delivered within this package.
 *
 * @version     $Id: DIMessages.java,v 1.53 2011/05/24 14:02:12 rburgermann Exp $
 *
 * @author      Bernd Buchegger (BB), 990105
 ******************************************************************************
 */
public abstract class DIMessages extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DIMessages.java,v 1.53 2011/05/24 14:02:12 rburgermann Exp $";


    /**
     * Name of bundle where the messages are included. <BR/>
     */
    public static String MSG_BUNDLE = BOMessages.MSG_BUNDLE;

    // messages
    /**
     * Message when no xml file has been found for the XMLViewer Object. <BR/>
     */
    public static String ML_MSG_NOVIEWERFILEFOUND = "ML_MSG_NOVIEWERFILEFOUND";

    /**
     * Message when export file could not be written. <BR/>
     */
    public static String ML_MSG_COULDNOTWRITEEXPORTFILE = "ML_MSG_COULDNOTWRITEEXPORTFILE";

    /**
     * Message when log file could not be written. <BR/>
     */
    public static String ML_MSG_COULDNOTWRITELOGFILE = "ML_MSG_COULDNOTWRITELOGFILE";

    /**
     * Message when export file could not be written. <BR/>
     */
    public static String ML_MSG_COULDNOTMOVEFILE  = "ML_MSG_COULDNOTMOVEFILE";

    /**
     * Message when export file could not be written. <BR/>
     */
    public static String ML_MSG_COULDNOTREADFILE = "ML_MSG_COULDNOTREADFILE";

    /**
     * Message when export file could not be written. <BR/>
     */
    public static String ML_MSG_COULDNOTDELETEOBJECT = "ML_MSG_COULDNOTDELETEOBJECT";

    /**
     * Message when export file could not be written. <BR/>
     */
    public static String ML_MSG_COULDNOTCHANGEOBJECT = "ML_MSG_COULDNOTCHANGEOBJECT";

    /**
     * Message when the object update fails. <BR/>
     */
    public static String ML_MSG_COULDNOTCHANGEONLYOBJECT = "ML_MSG_COULDNOTCHANGEONLYOBJECT";

    /**
     * Message when export file could not be written. <BR/>
     */
    public static String ML_MSG_COULDNOTCREATEOBJECT = "ML_MSG_COULDNOTCREATEOBJECT";

    /**
     * Message when export file could not be written. <BR/>
     */
    public static String ML_MSG_COULDNOTCREATEXMLVIEWEROBJECTDIR = "ML_MSG_COULDNOTCREATEXMLVIEWEROBJECTDIR";

   /**
     * Message when export file could not be written. <BR/>
     */
    public static String ML_MSG_COULDNOTCREATEDOCUMENT = "ML_MSG_COULDNOTCREATEDOCUMENT";

    /**
     * End of message: something could not be found. <BR/>
     * The tag <A HREF="UtilConstants.html#UtilConstants.TAG_NAME">UtilConstants.TAG_NAME</A>
     * is used to represent the name of the object.
     */
     // Not necessary anymore after migration to multi lingual handling
     // public static String ML_MSG_COULDNOTBEFOUND =
     //   "\"" + UtilConstants.TAG_NAME + "\" konnte nicht gefunden werden.";

    /**
     * The required object was not found. <BR/>
     * The tag <A HREF="UtilConstants.html#UtilConstants.TAG_NAME">UtilConstants.TAG_NAME</A>
     * is used to represent the name of the object.
     */
    public static String ML_MSG_OBJECTNOTFOUND = "ML_MSG_OBJECTNOTFOUND";

   /**
     * Message when export file could not be written. <BR/>
     */
    public static String ML_MSG_COULDNOTPROCESSOBJECT = "ML_MSG_COULDNOTPROCESSOBJECT";

    /**
     * Message when import file has been deleted. <BR/>
     */
    public static String ML_MSG_COULDNOTDELETEIMPORTFILE = "ML_MSG_COULDNOTDELETEIMPORTFILE";

    /**
     * Message when xml template could not be copied. <BR/>
     */
    public static String ML_MSG_COULDNOTCOPYTEMPLATE = "ML_MSG_COULDNOTCOPYTEMPLATE";

    /**
     * Message when log file has been written. <BR/>
     */
    public static String ML_MSG_COULDNOTWRITELOG = "ML_MSG_COULDNOTWRITELOG";

    /**
     * Message when export file could not be written. <BR/>
     */
    public static String ML_MSG_COULDNOTREADIMPORTFILE = "ML_MSG_COULDNOTREADIMPORTFILE";

    /**
     * Message when export file could not be written. <BR/>
     */
    public static String ML_MSG_COULDNOTREADIMPORTSCRIPTFILE = "ML_MSG_COULDNOTREADIMPORTSCRIPTFILE";

    /**
     * Message when export file could not be written. <BR/>
     */
    public static String ML_MSG_COULDNOTREADVIEWERFILE = "ML_MSG_COULDNOTREADVIEWERFILE";

    /**
     * Message when export file could not be written. <BR/>
     */
    public static String ML_MSG_MALFORMEDURL = "ML_MSG_MALFORMEDURL";

    /**
     * Message when export file could not be written. <BR/>
     */
    public static String ML_MSG_NOURL = "ML_MSG_NOURL";

    /**
     * Message when export file could not be written. <BR/>
     */
    public static String ML_MSG_FILENOTFOUND = "ML_MSG_FILENOTFOUND";

    /**
     * Message when export file could not be written. <BR/>
     */
    public static String ML_MSG_FILEISDIRECTORY = "ML_MSG_FILEISDIRECTORY";

    /**
     * Message when export file could not be written. <BR/>
     */
    public static String ML_MSG_NOFILE = "ML_MSG_NOFILE";

    /**
     * Message when export file could not be written. <BR/>
     */
    public static String ML_MSG_IMPORTFILENOTFOUND = "ML_MSG_IMPORTFILENOTFOUND";

    /**
     * Message when export file could not be written. <BR/>
     */
    public static String ML_MSG_NOIMPORTNODE = "ML_MSG_NOIMPORTNODE";

    /**
     * Message when export file could not be written. <BR/>
     */
    public static String ML_MSG_EMPTYROOT = "ML_MSG_EMPTYROOT";

    /**
     * Message when export file could not be written. <BR/>
     */
    public static String ML_MSG_NOIMPORTDATA = "ML_MSG_NOIMPORTDATA";

    /**
     * Message when export file could not be written. <BR/>
     */
    public static String ML_MSG_NOOBJECTNODESFOUND = "ML_MSG_NOOBJECTNODESFOUND";

    /**
     * Message when export file could not be written. <BR/>
     */
    public static String ML_MSG_ISNOTOBJECTSNODE = "ML_MSG_ISNOTOBJECTSNODE";

    /**
     * Message when export file could not be written. <BR/>
     */
    public static String ML_MSG_NOOBJECTNODE = "ML_MSG_NOOBJECTNODE";

    /**
     * Message when export file could not be written. <BR/>
     */
    public static String ML_MSG_CREATEXMLVIEWEROBJECT = "ML_MSG_CREATEXMLVIEWEROBJECT";

    /**
     * Message when export file could not be written. <BR/>
     */
    public static String ML_MSG_XMLVIEWEROBJECTCREATED = "ML_MSG_XMLVIEWEROBJECTCREATED";

    /**
     * Message when export file could not be created. <BR/>
     */
    public static String ML_MSG_OBJECTCREATED = "ML_MSG_OBJECTCREATED";

    /**
     * Message when export file could not be changed. <BR/>
     */
    public static String ML_MSG_OBJECTCHANGED = "ML_MSG_OBJECTCHANGED";

    /**
     * Message when a object is ignored during the import. <BR/>
     */
    public static String ML_MSG_OBJECTIGNORED = "ML_MSG_OBJECTIGNORED";

    /**
     * Message when the extkey of an object clashes with an already existing object. <BR/>
     */
    public static String ML_MSG_EXTKEY_CLASH = "ML_MSG_EXTKEY_CLASH";

    /**
     * Message when the generation of the extkey for an object failes. <BR/>
     */
    public static String ML_MSG_EXTKEY_ERROR = "ML_MSG_EXTKEY_ERROR";

    /**
     * Message when export file could not be changed. <BR/>
     */
    public static String ML_MSG_OBJECTDELETED = "ML_MSG_OBJECTDELETED";

    /**
     * Message when export file could not be written. <BR/>
     */
    public static String ML_MSG_NOEXPORTOBJECT = "ML_MSG_NOEXPORTOBJECT";

    /**
     * Message when export file could not be written. <BR/>
     */
    public static String ML_MSG_NOEXPORTFILENAME = "ML_MSG_NOEXPORTFILENAME";

    /**
     * Message when object has been exported sucessfully. <BR/>
     */
    public static String ML_MSG_OBJECTEXPORTED = "ML_MSG_OBJECTEXPORTED";

    /**
     * Message when there was no object exported. <BR/>
     */
    public static String ML_MSG_NOPBJECTSEXPORTED = "ML_MSG_NOPBJECTSEXPORTED";

    /**
     * Message when no filename provided. <BR/>
     */
    public static String ML_MSG_NOFILENAME = "ML_MSG_NOFILENAME";

    /**
     * Message when no filename provided. <BR/>
     */
    public static String ML_MSG_INVALIDPATH = "ML_MSG_INVALIDPATH";

    /**
     * Message when import was successfull. <BR/>
     */
    public static String ML_MSG_IMPORTSUCCESSFUL = "ML_MSG_IMPORTSUCCESSFUL";

    /**
     * Message when import has been started. <BR/>
     */
    public static String ML_MSG_IMPORTSTARTED = "ML_MSG_IMPORTSTARTED";

    /**
     * Message when import was successfull. <BR/>
     */
    public static String ML_MSG_IMPORTFINISHED = "ML_MSG_IMPORTFINISHED";

    /**
     * Message when import failed. <BR/>
     */
    public static String ML_MSG_IMPORTFAILED = "ML_MSG_IMPORTFAILED";

    /**
     * Message when export was successfull. <BR/>
     */
    public static String ML_MSG_EXPORTSTARTED = "ML_MSG_EXPORTSTARTED";

    /**
     * Message when export was successfull. <BR/>
     */
    public static String ML_MSG_EXPORTSUCCESSFULL = "ML_MSG_EXPORTSUCCESSFULL";

    /**
     * Message when export has been finished. <BR/>
     */
    public static String ML_MSG_EXPORTFINISHED = "ML_MSG_EXPORTFINISHED";

    /**
     * Message when export failed. <BR/>
     */
    public static String ML_MSG_EXPORTFAILED = "ML_MSG_EXPORTFAILED";

    /**
     * Message when import file has been deleted. <BR/>
     */
    public static String ML_MSG_IMPORTFILEDELETED = "ML_MSG_IMPORTFILEDELETED";

    /**
     * Message how many objects have been exported. <BR/>
     */
    public static String ML_MSG_OBJECTSEXPORTED = "ML_MSG_OBJECTSEXPORTED";

    /**
     * Message when no files could have been found. <BR/>
     */
    public static String ML_MSG_NOFILESFOUND = "ML_MSG_NOFILESFOUND";

    /**
     * Message when no document templates available. <BR/>
     */
    public static String ML_MSG_NOTEMPLATESAVAILABLE = "ML_MSG_NOTEMPLATESAVAILABLE";

    /**
     * Message when rights could have been successfully set. <BR/>
     */
    public static String ML_MSG_RIGHTSSETSUCCESSFULL = "ML_MSG_RIGHTSSETSUCCESSFULL";

    /**
     * Message when rights could no have been set. <BR/>
     */
    public static String ML_MSG_RIGHTSSETFAILED = "ML_MSG_RIGHTSSETFAILED";

    /**
     * Message when log file has been written. <BR/>
     */
    public static String ML_MSG_LOGWRITTEN = "ML_MSG_LOGWRITTEN";

    /**
     * Message when group memberships of a user have been successfully created. <BR/>
     */
    public static String ML_MSG_GROUP_MEMBERSHIPS_CREATED = "ML_MSG_GROUP_MEMBERSHIPS_CREATED";

    /**
     * Message when creation of group memberships of a user has failed. <BR/>
     */
    public static String ML_MSG_GROUP_MEMBERSHIPS_FAILED = "ML_MSG_GROUP_MEMBERSHIPS_FAILED";

    /**
     * Message when no connector has been set yet. <BR/>
     */
    public static String ML_MSG_NO_CONNECTOR_SET = "ML_MSG_NO_CONNECTOR_SET";

    /**
     * Message when no translator has been set yet. <BR/>
     */
    public static String ML_MSG_NO_TRANSLATOR_SET = "ML_MSG_NO_TRANSLATOR_SET";

    /**
     * Message when no objects are available. <BR/>
     */
    public static String ML_MSG_NOOBJECTSAVAILABLE = "ML_MSG_NOOBJECTSAVAILABLE";

    /**
     * Message when translation failed. <BR/>
     */
    public static String ML_MSG_TRANSLATIONFAILED = "ML_MSG_TRANSLATIONFAILED";

    /**
     * Message when writing to connector failed. <BR/>
     */
    public static String ML_MSG_COULDNOTWRITETOCONNECTOR = "ML_MSG_COULDNOTWRITETOCONNECTOR";

    /**
     * Message when writing to backp connector failed. <BR/>
     */
    public static String ML_MSG_COULDNOTWRITETOBACKUPCONNECTOR = "ML_MSG_COULDNOTWRITETOBACKUPCONNECTOR";

    /**
     * Message when the number of objects in the import document could not be determined. <BR/>
     */
    public static String ML_MSG_COULD_NOT_DETERMINE_NUMBER_OF_OBJECTS = "ML_MSG_COULD_NOT_DETERMINE_NUMBER_OF_OBJECTS";

    /**
     * Message when the import could not be initialized. <BR/>
     */
    public static String ML_MSG_COULD_NOT_INIT_IMPORT = "ML_MSG_COULD_NOT_INIT_IMPORT";

    /**
     * Message when the export could not be initialized. <BR/>
     */
    public static String ML_MSG_COULD_NOT_INIT_EXPORT = "ML_MSG_COULD_NOT_INIT_EXPORT";

    /**
     * Message when an object could not be exported. <BR/>
     * The tag <A HREF="UtilConstants.html#UtilConstants.TAG_NAME">UtilConstants.TAG_NAME</A>
     * is used to represent the name of the object.
     */
    public static String ML_MSG_COULD_NOT_EXPORT_OBJECT = "ML_MSG_COULD_NOT_EXPORT_OBJECT";

    /**
     * Message when there are no connectors found. <BR/>
     */
    public static String ML_MSG_NOCONNECTORAVAILABLE = "ML_MSG_NOCONNECTORAVAILABLE";

    /**
     * Message when connector could not be initialized. <BR/>
     */
    public static String ML_MSG_COULD_NOT_INIT_CONNECTOR = "ML_MSG_COULD_NOT_INIT_CONNECTOR";

    /**
     * Message when translation failed. <BR/>
     */
    public static String ML_MSG_CONNECTORFAILED = "ML_MSG_CONNECTORFAILED";

    /**
     * Message when connector cannot not be used as backup connector. <BR/>
     */
    public static String ML_MSG_BACKUP_CONNECTOR_NOT_ALLOWED = "ML_MSG_BACKUP_CONNECTOR_NOT_ALLOWED";

    /**
     * Message for exception error when a directory does not exist. <BR/>
     */
    public static String ML_MSG_DIRECTORY_NOT_EXISTS = "ML_MSG_DIRECTORY_NOT_EXISTS";

    /**
     * Message when the importpath is not specified. <BR/>
     */
    public static String ML_MSG_NO_IMPORTPATH_SPECIFIED  = "ML_MSG_NO_IMPORTPATH_SPECIFIED";

    /**
     * Message when the importpath is not specified. <BR/>
     */
    public static String ML_MSG_IMPORTCONTAINER_NOT_FOUND  = "ML_MSG_IMPORTCONTAINER_NOT_FOUND";

     /**
     * The required container was not found. <BR/>
     * The tag <A HREF="UtilConstants.html#UtilConstants.TAG_NAME">UtilConstants.TAG_NAME</A>
     * is used to represent the name of the object.
     */
    public static String ML_MSG_CONTAINERNOTFOUND = "ML_MSG_CONTAINERNOTFOUND";

    /**
     * The required containeroid was not found. <BR/>
     * The tag <A HREF="UtilConstants.html#UtilConstants.TAG_NAME">UtilConstants.TAG_NAME</A>
     * is used to represent the name of the object.
     */
    // AN this message maybe will be used
    //public static String ML_MSG_CONTAINERIDNOTFOUND = "Importablage mit der OID \"" + UtilConstants.TAG_NAME + DIMessages.MSG_COULDNOTBEFOUND;

    /**
     * Objects could only be imported in container
     */
    // AN this message maybe will be used
    // public static String ML_MSG_ONLYCONTAINERIMPORT = "Import nur in eine Ablage möglich!";

    /**
     * Message when an importscript has been set. <BR/>
     */
    public static String ML_MSG_IMPORTSCRIPT_SET = "ML_MSG_IMPORTSCRIPT_SET";

    /**
     * Message when import rights could not have been set for a user. <BR/>
     */
    public static String ML_MSG_COULD_NOT_SET_RIGHTS_FOR_USER = "ML_MSG_COULD_NOT_SET_RIGHTS_FOR_USER";

    /**
     * Message when import rights could not have been set for a group. <BR/>
     */
    public static String ML_MSG_COULD_NOT_SET_RIGHTS_FOR_GROUP = "ML_MSG_COULD_NOT_SET_RIGHTS_FOR_GROUP";

    /**
     * Message when import rights have been set for a user. <BR/>
     */
    public static String ML_MSG_RIGHTS_SET_FOR_USER = "ML_MSG_RIGHTS_SET_FOR_USER";

    /**
     * Message when import rights have been set for a group. <BR/>
     */
    public static String ML_MSG_RIGHTS_SET_FOR_GROUP = "ML_MSG_RIGHTS_SET_FOR_GROUP";

    /**
     * Message when container id is invalid. <BR/>
     */
    public static String ML_MSG_INVALID_CONTAINERID = "ML_MSG_INVALID_CONTAINERID";

    /**
     * Message when the protocol is invalid
     */
    public static String ML_MSG_PROTOCOL_NOT_SUPPORTED = "ML_MSG_PROTOCOL_NOT_SUPPORTED";

    /**
     * Message when references could be created
     */
    public static String ML_MSG_UNKNOWN_OPERATION = "ML_MSG_UNKNOWN_OPERATION";

    /**
     * Message when a failure in the script has occurred
     */
    public static String ML_MSG_SCRIPT_FAILURE = "ML_MSG_SCRIPT_FAILURE";

    /**
     * Message when a template file is invalid or not found.
     */
    public static String ML_MSG_TEMPLATE_INVALIDFILE = "ML_MSG_TEMPLATE_INVALIDFILE";

    /**
     * Message when db-mapping is not possible for a template file.
     */
    public static String ML_MSG_TEMPLATE_DBMAPPING_IMPOSSIBLE = "ML_MSG_TEMPLATE_DBMAPPING_IMPOSSIBLE";

    /**
     * Message when the type name of a template is not valid.
     */
    public static String ML_MSG_TEMPLATE_TYPENAME_INVALID = "ML_MSG_TEMPLATE_TYPENAME_INVALID";

    /**
     * Message when a form template has the same type name like a other template.
     */
    public static String ML_MSG_TEMPLATE_HAS_TEMPLATETYPENAME = "ML_MSG_TEMPLATE_HAS_TEMPLATETYPENAME";

    /**
     * Message when a form template is changed and the changes are not compatible with
     * the previous definition. <BR/>
     */
    public static String ML_MSG_TEMPLATE_STRUCTURE_CHANGED = "ML_MSG_TEMPLATE_STRUCTURE_CHANGED";

    /**
     * Message when a form template has values with duplicate field names. <BR/>
     */
    public static String ML_MSG_TEMPLATE_DUPLICATE_NAMES = "ML_MSG_TEMPLATE_DUPLICATE_NAMES";

    /**
     * Message when a form template contains a value without a field name. <BR/>
     */
    public static String ML_MSG_INVALID_FIELD_NAME = "ML_MSG_INVALID_FIELD_NAME";

    /**
     * Message when a form template contains a invalid type code in the MAYEXISTIN attribut. <BR/>
     */
    public static String ML_MSG_INVALID_MAYEXISTIN_TYPECODE = "ML_MSG_INVALID_MAYEXISTIN_TYPECODE";

    /**
     * Message when a form template contains a invalid type code in the MAYCONTAIN attribut. <BR/>
     */
    public static String ML_MSG_INVALID_MAYCONTAIN_TYPECODE = "ML_MSG_INVALID_MAYCONTAIN_TYPECODE";

    /**
     * Message when a form template contains a invalid type code in the SUPERTYPECODE attribut. <BR/>
     */
    public static String ML_MSG_INVALID_SUPER_TYPECODE = "ML_MSG_INVALID_SUPER_TYPECODE";

    /**
     * Message when the update of the mapping table is perform.
     */
    public static String ML_MSG_MAPPING_ACTIVATED = "ML_MSG_MAPPING_ACTIVATED";

    /**
     * Message when the update of the mapping table is perform.
     */
    public static String ML_MSG_MAPPINGUPDATE_PERFORMED = "ML_MSG_MAPPINGUPDATE_PERFORMED";

    /**
     * Message when a error occurs during the update of the mapping table. <BR/>
     */
    public static String ML_MSG_MAPPINGUPDATE_ERROR = "ML_MSG_MAPPINGUPDATE_ERROR";

    /**
     * Message when a form has a unknown or invalid typ name.
     */
    public static String ML_MSG_TEMPLATE_NOT_FOUND = "ML_MSG_TEMPLATE_NOT_FOUND";

    /**
     * Message when a form does not match the document template.
     */
    public static String ML_MSG_INVALID_TEMPLATE = "ML_MSG_INVALID_TEMPLATE";

    /**
     * Message when a form has a invalid tab object.
     */
    public static String ML_MSG_INVALID_TABOBJECT = "ML_MSG_INVALID_TABOBJECT";

    /**
     * If any other value than: VIEW, OBJECT, LINK, FCT is used in Attribut KIND
     * of tab TAB. <BR/>
     */
    public static final String ML_MSG_INVALID_TABKIND = "ML_MSG_INVALID_TABKIND";

    /**
     * Message when a form template defines a mapping table name already uses by another template.
     */
    public static String ML_MSG_MAPPINGTABLE_ALREADY_DEFINED = "ML_MSG_MAPPINGTABLE_ALREADY_DEFINED";

    /**
     * Message when an importscript has been set. <BR/>
     */
    public static String ML_MSG_IMPORTSCENARIO_SET = "ML_MSG_IMPORTSCENARIO_SET";

    /**
     * Message when an importscenario is invalid. <BR/>
     */
    public static String ML_MSG_INVALID_IMPORTSCENARIO = "ML_MSG_INVALID_IMPORTSCENARIO";

    /**
     * Message when a workflow could not have been started. <BR/>
     */
    public static String ML_MSG_WORKFLOW_NOT_STARTED = "ML_MSG_WORKFLOW_NOT_STARTED";
    /**
     * Message when a workflow has been started successfully through workflow. <BR/>
     */
    public static String ML_MSG_WORKFLOW_STARTED = "ML_MSG_WORKFLOW_STARTED";

    /**
     * Message that an SAP system reported an error. <BR/>
     */
    public static String ML_MSG_SAP_ERROR = "ML_MSG_SAP_ERROR";

    /**
     * Message when a reference has been created. <BR/>
     */
    public static String ML_MSG_REFERENCE_CREATED = "ML_MSG_REFERENCE_CREATED";

    /**
     * Message when a reference could not have been created. <BR/>
     */
    public static String ML_MSG_COULD_NOT_CREATE_REFERENCE = "ML_MSG_COULD_NOT_CREATE_REFERENCE";

    /**
     * Message when a reference has been created. <BR/>
     */
    public static String ML_MSG_LINK_CREATED = "ML_MSG_LINK_CREATED";

    /**
     * Message when a reference could not have been created. <BR/>
     */
    public static String ML_MSG_COULD_NOT_CREATE_LINK = "ML_MSG_COULD_NOT_CREATE_LINK";

    /**
     * Message when a ftp path was not found. <BR/>
     */
    public static String ML_MSG_FTP_PATH_NOT_FOUND = "ML_MSG_FTP_PATH_NOT_FOUND";

    /**
     * Message when a ftp server was not found. <BR/>
     */
    public static String ML_MSG_FTP_SERVER_NOT_FOUND = "ML_MSG_FTP_SERVER_NOT_FOUND";

    /**
     * Message when a login with a user was not possible. <BR/>
     */
    public static String ML_MSG_COULD_NOT_LOGIN = "ML_MSG_COULD_NOT_LOGIN";

    /**
     * Message start: something happened with a file. <BR/>
     * The tag <A HREF="UtilConstants.html#UtilConstants.TAG_NAME">UtilConstants.TAG_NAME</A>
     * is used to represent the name of the object.
     */
    public static String ML_MSG_FILESTART = "ML_MSG_FILESTART";

    /**
     * Message when a file could be deleted. <BR/>
     */
    public static String ML_MSG_FILE_DELETED = "ML_MSG_FILE_DELETED";

    /**
     * Message when a file could not be deleted. <BR/>
     */
    public static String ML_MSG_COULD_NOT_DELETE_FILE = "ML_MSG_COULD_NOT_DELETE_FILE";

    /**
     * Message when a file could not be written. <BR/>
     */
    public static String ML_MSG_COULD_NOT_WRITE_FILE = "ML_MSG_COULD_NOT_WRITE_FILE";

    /**
     * Message when a file could not be read. <BR/>
     */
    public static String ML_MSG_COULD_NOT_READ_FILE = "ML_MSG_COULD_NOT_READ_FILE";

    /**
     * Message when a email could not be deleted. <BR/>
     */
    public static String ML_MSG_COULD_NOT_DELETE_EMAIL = "ML_MSG_COULD_NOT_DELETE_EMAIL";

    /**
     * Message when a ftp directory could not be read. <BR/>
     */
    public static String ML_MSG_COULD_NOT_READ_FTP_DIR = "ML_MSG_COULD_NOT_READ_FTP_DIR";

    /**
     * Message when the content of a  mail folder could not be read. <BR/>
     */
    public static String ML_MSG_COULD_NOT_READ_MAIL_DIR = "ML_MSG_COULD_NOT_READ_MAIL_DIR";

    /**
     * Message when the content of a  mail folder could not be read. <BR/>
     */
    public static String ML_MSG_COULD_NOT_READ_EDISWITCH_DIR = "ML_MSG_COULD_NOT_READ_EDISWITCH_DIR";

    /**
     * Message when the content of a  mail folder could not be read. <BR/>
     */
    public static String ML_MSG_COULD_NOT_OPEN_MAIL_FOLDER = "ML_MSG_COULD_NOT_OPEN_MAIL_FOLDER";

    /**
     * Message when the content of a  mail folder could not be read. <BR/>
     */
    public static String ML_MSG_MAILSERVER_CONNECTION_FAILED = "ML_MSG_MAILSERVER_CONNECTION_FAILED";

    /**
     * Message when a connection to a web server was not possible. <BR/>
     */
    public static String ML_MSG_HTTP_CONNECTION_FAILED = "ML_MSG_HTTP_CONNECTION_FAILED";

    /**
     * Message when the response of from a web server returned an error. <BR/>
     */
    public static String ML_MSG_HTTP_MULTIPART_ERROR = "ML_MSG_HTTP_MULTIPART_ERROR";

    /**
     * Message when the path or the filenam of the export files ist empty or
     * null. <BR/>
     */
    public static String ML_MSG_EMPTY_EXPORT_STRING = "ML_MSG_EMPTY_EXPORT_STRING";

    /**
     * Message when the connection to a ftp server was not possible. <BR/>
     */
    public static String ML_MSG_FTP_CONNECTION_FAILED = "ML_MSG_FTP_CONNECTION_FAILED";

    /**
     * Message when the connection to an ediswitch server was not possible. <BR/>
     */
    public static String ML_MSG_EDISWITCH_CONNECTION_FAILED = "ML_MSG_EDISWITCH_CONNECTION_FAILED";


    /**
     * Message when the connection to an SAP Business Connector was not possible. <BR/>
     */
    public static String ML_MSG_SAPBC_CONNECTION_FAILED = "ML_MSG_SAPBC_CONNECTION_FAILED";


    /**
     * Message when the connection to an ediswitch server was not possible. <BR/>
     */
    public static String ML_MSG_EDISWITCH_SERVER_NOT_FOUND = "ML_MSG_EDISWITCH_SERVER_NOT_FOUND";

    /**
     * Message when the source file of a translator could not be renamed. <BR/>
     */
    public static String ML_MSG_COULD_NOT_RENAME_SOURCE_FILE = "ML_MSG_COULD_NOT_RENAME_SOURCE_FILE";

    /**
     * Message when some error occurred at creating the translator. <BR/>
     */
    public static String ML_MSG_ERROR_WHILE_CREATING_TRANSLATOR = "ML_MSG_ERROR_WHILE_CREATING_TRANSLATOR";

    /**
     * Message when the new document template has the same structure than
     * the old one. <BR/>
     */
    public static String ML_MSG_STRUCTURE_NOT_CHANGED = "ML_MSG_STRUCTURE_NOT_CHANGED";

    // Type Translator Generator Messages
    /**
     * Message for the translator file to tell the user that the datatype has
     * changed and this should be changed manualy. <BR/>
     */
    public static String ML_MSG_CHANGE_CONTENT = "ML_MSG_CHANGE_CONTENT";

    /**
     * Message that the datatype has changed.
     */
    public static String ML_MSG_TYPE_CHANGE = "ML_MSG_TYPE_CHANGE";
    
    /**
     * Message for the translator file to tell the user to check the automatically generated
     * translation code because the datatype has changed. <BR/>
     */
    public static String ML_MSG_TYPE_CHANGE_CHECK_CONTENT = "ML_MSG_TYPE_CHANGE_CHECK_CONTENT";
    // Type Translator Generator Messages - END
    
    /**
     * Message where to find the log file of translation.. <BR/>
     */
    public static String ML_MSG_LOGFILE_PATH = "ML_MSG_LOGFILE_PATH";
    
    /**
     * Message if typecode used in TYPEFILTER in FIELDREF-field, does
     * not exist. <BR/>
     */
    public static String ML_MSG_INVALIDTYPECODE = "ML_MSG_INVALIDTYPECODE";

    /**
     * Message if field which is not db-mapped has to be db-mapped. <BR/>
     */
    public static String ML_MSG_MISSINGDBMAPPING = "ML_MSG_MISSINGDBMAPPING";

    /**
     * Message: a import file will be read. <BR/>
     */
    public static String ML_MSG_READ_IMPORTFILE = "ML_MSG_READ_IMPORTFILE";

    /**
     * Message: creating backup. <BR/>
     */
    public static String ML_MSG_CREATE_BACKUP = "ML_MSG_CREATE_BACKUP";

    /**
     * Message: starting translation. <BR/>
     */
    public static String ML_MSG_TRANSLATION_STARTING = "ML_MSG_TRANSLATION_STARTING";

    /**
     * Message: a translation was successful. <BR/>
     */
    public static String ML_MSG_TRANSLATION_SUCCESSFUL = "ML_MSG_TRANSLATION_SUCCESSFUL";

    /**
     * Message: set the forwarded to conversion date. <BR/>
     */
    public static String ML_MSG_SET_CONVERSION_DATE = "ML_MSG_SET_CONVERSION_DATE";

    /**
     * Message: set the transmission date. <BR/>
     */
    public static String ML_MSG_SET_TRANSMISSION_DATE = "ML_MSG_SET_TRANSMISSION_DATE";

    /**
     * Message: transmission started.<BR/>
     */
    public static final String ML_MSG_TRANSMISSION_STARTED = "ML_MSG_TRANSMISSION_STARTED";

    /**
     * Message: could not transmit data.<BR/>
     */
    public static final String ML_MSG_COULD_NOT_TRANSMIT_DATA = "ML_MSG_COULD_NOT_TRANSMIT_DATA";
 
    /**
     * Message: error.<BR/>
     */
    public static final String ML_MSG_ERROR = "ML_MSG_ERROR";

    /**
     * Message: Operation UPDATE instead of NEW.<BR/>
     */
    public static final String ML_MSG_OPERATION_UPDATE_INSTEAD_OF_NEW =
        "ML_MSG_OPERATION_UPDATE_INSTEAD_OF_NEW";

    /**
     * Message: Operation NEW instead of UPDATE.<BR/>
     */
    public static final String ML_MSG_OPERATION_NEW_INSTEAD_OF_UPDATE =
        "ML_MSG_OPERATION_NEW_INSTEAD_OF_UPDATE";

    /**
     * Message: Object not found for Operation DELETE.<BR/>
     */
    public static final String ML_MSG_OPERATION_DELETE_OBJECT_NOT_FOUND = 
        "ML_MSG_OPERATION_DELETE_OBJECT_NOT_FOUND";
    
} // class DIMessages
