/*
 * Class: IntegratorArguments.java
 */

// package:
package ibs.di;

// imports:
import ibs.BaseObject;


/******************************************************************************
 * Arguments for ibs.di <BR/>
 * This abstract class contains all arguments which are necessary to deal with
 * the classes delivered within this package. <P>
 *
 * @version     $Id: DIArguments.java,v 1.41 2010/12/23 13:08:24 rburgermann Exp $
 *
 * @author      Bernd Buchegger (BB), 990104
 ******************************************************************************
 */
public abstract class DIArguments extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DIArguments.java,v 1.41 2010/12/23 13:08:24 rburgermann Exp $";


    /**
     * Argument which holds the name of the importfile. <BR/>
     */
    public static final String ARG_IMPORTFILE = "imf";

    /**
     * Argument which holds the oid of the importcontainer. <BR/>
     */
    public static final String ARG_IMPORTCONTAINERID = "ico";

    /**
     * Argument which holds the name of the importscript file  <BR/>
     */
    public static final String ARG_IMPORTSCRIPT = "isf";

    /**
     * Argument which holds the name of the attachment file  <BR/>
     */
    public static final String ARG_ATTACHMENTFILE = "atf";

    /**
     * Argument which holds the source of the import  <BR/>
     */
    public static final String ARG_IMPORTSOURCE = "isc";

    /**
     * Argument which holds the path of the import directory  <BR/>
     */
    public static final String ARG_IMPORTPATH = "ipa";

    /**
     * Argument which holds the path of the importscript directory  <BR/>
     */
    public static final String ARG_IMPORTSCRIPTPATH = "isp";

    /**
     * Argument which holds the name of the file to be uploaded
     * and used as import file. <BR/>
     */
    public static final String ARG_UPLOADFILE = "upf";

    /**
     * Argument which holds the flag to delete the import file
     * after an successfull import. <BR/>
     */
    public static final String ARG_DELETEIMPORTFILE = "dif";

    /**
     * Argument which holds the name of the set importpath button.<BR/>
     */
    public static final String ARG_SETIMPORTPATH = "sip";

    /**
     * Argument which holds the name of the set importscriptpath button.<BR/>
     */
    public static final String ARG_SETIMPORTSCRIPTPATH = "sisp";

    /**
     * Argument which holds the name of the actvie path for importfiles.<BR/>
     */
    public static final String ARG_ACTIVEIMPORTPATH = "aip";

    /**
     * Argument which holds the name of the active path for importscripts.<BR/>
     */
    public static final String ARG_ACTIVEIMPORTSCRIPTPATH = "aisp";

    /**
     * Argument which holds objects to export. <BR/>
     */
    public static final String ARG_OBJECT = "obj";

    /**
     * Argument which holds the name of the exportfile.<BR/>
     */
    public static final String ARG_EXPORTFILE = "exf";

    /**
     * Argument: export file name. <BR/>
     * This is different from {@link #ARG_EXPORTFILE ARG_EXPORTFILE} because
     * it is possible to use both arguments at the same time.
     */
    public static final String ARG_EXPORTFILENAME = "expfn";

    /**
     * Argument which holds the name of the export path.<BR/>
     */
    public static final String ARG_EXPORTPATH = "exp";

    /**
     * Argument which holds the name of the export url.<BR/>
     */
    public static final String ARG_EXPORTURL = "exu";

    /**
     * Argument which holds the flag for exporting content of a container.<BR/>
     */
    public static final String ARG_EXPORTCONTENT = "exc";

    /**
     * Argument which holds the flag for exporting content of a container
     * recursively. <BR/>
     */
    public static final String ARG_EXPORTCONTENTRECURSIVE = "excr";

    /**
     * Argument which holds the flag for export into a single file. <BR/>
     */
    public static final String ARG_EXPORTSINGLEFILE = "exsf";

    /**
     * Argument which holds the flag if the container object is in the
     * export. <BR/>
     */
    public static final String ARG_EXPORTCONTAINER = "exco";

    /**
     * Argument which holds the type of destination (path or url). <BR/>
     */
    public static final String ARG_EXPORTDESTINATION = "exd";

    /**
     * Argument which holds the flag for prettyprinting the export documents. <BR/>
     */
    public static final String ARG_EXPORTPRETTYPRINT     = "epp";

    /**
     * Argument which holds the log. <BR/>
     */
    public static final String ARG_LOG = "log";

    /**
     * Argument which holds the value of the selected xml template file. <BR/>
     */
    public static final String ARG_TEMPLATE = "tmpl";

    /**
     * Argument which holds the value of the selected xml template file
     * from the 'new form' dialog. This should be used ONLY in this
     * dialog context!!!<BR/>
     */
    public static final String ARG_VIEWER_TEMPLATE = "vtmpl";

    /**
     * Argument which holds the name of the xml template file. <BR/>
     */
    public static final String ARG_TEMPLATENAME = "tmpln";

    /**
     * Argument which holds the name of the xml templates path. <BR/>
     */
    public static final String ARG_TEMPLATEPATH = "tmplp";

    /**
     * Argument which holds the value of the selected xml workflow template file. <BR/>
     */
    public static final String ARG_WORKFLOWTEMPLATE = "wftmpl";

    /**
     * Argument which holds the name of the xml workflow template file. <BR/>
     */
    public static final String ARG_WORKFLOWTEMPLATENAME = "wftmpln";

    /**
     * Argument which holds the name of the xml workflow templates path. <BR/>
     */
    public static final String ARG_WORKFLOWTEMPLATEPATH = "wftmplp";

    /**
     * Argument which holds workflow allowed. <BR/>
     */
    public static final String ARG_WORKFLOWALLOWED = "wfall";

    /**
     * Extension that will be added to option arguments. <BR/>
     */
    public static final String ARG_OPTION_EXTENSION = "_op";

    /**
     * Arguments that holds the flag to use the standard header. <BR/>
     */
    public static final String ARG_USESTANDARDHEADER = "ush";

    /**
     * Arguments that holds the template oid. <BR/>
     */
    public static final String ARG_TEMPLATEOID = "toid";

    /**
     * Arguments that holds the header fields. <BR/>
     */
    public static final String ARG_HEADERFIELDS = "hfd";

    /**
     * Arguments that holds the switch for writing a log file. <BR/>
     */
    public static final String ARG_WRITELOGFILE = "wlf";

    /**
     * Arguments that holds the switch for displaying the log file. <BR/>
     */
    public static final String ARG_DISPLAYLOGFILE = "dlf";

    /**
     * Arguments that holds the switch for append the log file. <BR/>
     */
    public static final String ARG_APPENDLOGFILE = "alf";

    /**
     * Arguments that holds the name of the log file. <BR/>
     */
    public static final String ARG_LOGFILENAME = "lfn";

    /**
     * Arguments that holds the path of the log file. <BR/>
     */
    public static final String ARG_LOGFILEPATH = "lfp";

    /**
     * Argument that holds the oid of the connector . <BR/>
     */
    public static final String ARG_CONNECTOR = "con";

    /**
     * Argument that holds the name of a connector . <BR/>
     */
    public static final String ARG_CONNECTORNAME = "conn";

    /**
     * Argument that holds the oid of the active connector. <BR/>
     */
    public static final String ARG_ACTIVECONNECTOR = "acon";

    /**
     * Argument that holds the flag for multiple upload. <BR/>
     */
    public static final String ARG_MULTIPLEUPLOAD = "mup";

    /**
     * Argument that holds the translator oid. <BR/>
     */
    public static final String ARG_TRANSLATOR = "trans";

    /**
     * Argument that holds the switch for creating a backup. <BR/>
     */
    public static final String ARG_CREATEBACKUP = "dicrbup";

    /**
     * Argument that holds the oid of the backup connector . <BR/>
     */
    public static final String ARG_BACKUPCONNECTOR = "dibupcon";

    /**
     * Argument that holds the name of a backup connector . <BR/>
     */
    public static final String ARG_BACKUPCONNECTORNAME = "dibupconn";

    /**
     * Argument for the check connector button. <BR/>
     */
    public static final String ARG_CHECKCONNECTOR = "cconn";

    /**
     * Argument for an uploaded file. <BR/>
     */
    public static final String ARG_UPLOADEDFILE = "upldf";

    /**
     * Argument for markedfile. <BR/>
     */
    public static final String ARG_MARKEDFILE = "mkdf";

    /**
     * Argument for an add button. <BR/>
     */
    public static final String ARG_ADD = "add";

    /**
     * Argument for an delete button. <BR/>
     */
    public static final String ARG_DELETE = "del";

    /**
     * Argument for an delete subobject. <BR/>
     */
    public static final String ARG_ISDELETERECURSIVE = "dltre";

    /**
     * Argument for an delete object. <BR/>
     */
    public static final String ARG_ISDELETE = "dlt";

    /**
     * General argument#1 for connector. <BR/>
     */
    public static final String ARG_ARG1 = "arg1";
    /**
     * General argument#2 for connector. <BR/>
     */
    public static final String ARG_ARG2 = "arg2";
    /**
     * General argument#3 for connector. <BR/>
     */
    public static final String ARG_ARG3 = "arg3";
    /**
     * General argument#4 for connector. <BR/>
     */
    public static final String ARG_ARG4 = "arg4";
    /**
     * General argument#5 for connector. <BR/>
     */
    public static final String ARG_ARG5 = "arg5";
    /**
     * General argument#6 for connector. <BR/>
     */
    public static final String ARG_ARG6 = "arg6";
    /**
     * General argument#7 for connector. <BR/>
     */
    public static final String ARG_ARG7 = "arg7";
    /**
     * General argument#8 for connector. <BR/>
     */
    public static final String ARG_ARG8 = "arg8";
    /**
     * General argument#9 for connector. <BR/>
     */
    public static final String ARG_ARG9 = "arg9";

    /**
     * Argument which defines if the connector may be used for import. <BR/>
     */
    public static final String ARG_ISIMPORTCONNECTOR = "iicon";
    /**
     * Argument which defines if the connector may be used for export. <BR/>
     */
    public static final String ARG_ISEXPORTCONNECTOR = "iecon";

    /**
     * Argument for connectors typename. <BR/>
     */
    public static final String ARG_CONNECTORTYPENAME = "ctn";

    /**
     * Argument for connectors type. <BR/>
     */
    public static final String ARG_CONNECTORTYPE = "ct";

    /**
     * Argument for name of importscript (used by agents). <BR/>
     */
    public static final String ARG_IMPORTSCRIPTNAME = "isn";

    /**
     * Argument for an import scenario class name. <BR/>
     */
    public static final String ARG_IMPORTSCENARIOCLASS = "isclass";

    /**
     * Argument for name of translator (used by agents). <BR/>
     */
    public static final String ARG_TRANSLATORNAME = "trn";

    /**
     * Argument for path of the importcontainer (used by agents). <BR/>
     */
    public static final String ARG_IMPORTCONTAINERPATH  = "icp";

    /**
     * Argument for activating the hierarchical export. <BR/>
     */
    public static final String ARG_HIERARCHICALEXPORT   = "hex";

    /**
     * Argument for updating the mapping for document templates. <BR/>
     */
    public static final String ARG_UPDATEMAPPING        = "udbm";

    /**
     * Argument for reloading the object types into the type cache. <BR/>
     */
    public static final String ARG_RELOADTYPES        = "relty";

    /**
     * Argument for enabling the workflow when object is
     * created in an xmlviewercontainer that has a workflow
     * template associated.<BR/>
     */
    public static final String ARG_ENABLEWORKFLOW       = "ewf";

    /**
     * Argument for the separator of an ASCIITranslator.<BR/>
     */
    public static final String ARG_SEPARATOR            = "sep";

    /**
     * Argument for the escape sequence of an ASCIITranslator separator.<BR/>
     */
    public static final String ARG_ESCAPESEPARATOR      = "esep";

    /**
     * Argument for the include metadata option in ASCIITranslator_01.<BR/>
     */
    public static final String ARG_ISINCLUDEMETADATA    = "imd";

    /**
     * Argument for the include header option in ASCIITranslator_01.<BR/>
     */
    public static final String ARG_ISINCLUDEHEADER      = "ihe";

    /**
     * Argument for the export interface. <BR/>
     */
    public static final String ARG_EXPORTINTERFACE      = "eif";

    /**
     * Argument for the active export interface. <BR/>
     */
    public static final String ARG_ACTIVEEXPORTINTERFACE = "aeif";

    /**
     * Argument for the import interface. <BR/>
     */
    public static final String ARG_IMPORTINTERFACE       = "iif";

    /**
     * Argument for the active import interface. <BR/>
     */
    public static final String ARG_ACTIVEIMPORTINTERFACE = "aiif";

    /**
     * Argument for the accept interface button. <BR/>
     */
    public static final String ARG_SETINTERFACE = "seti";

    /**
     * Argument for the file filter in the import dialog.<BR/>
     */
    public static final String ARG_FILEFILTER      = "ffi";

    /**
     * Argument for form template to show the dom tree. <BR/>
     */
    public static final String ARG_SHOWDOMTREE       = "sdt";

    /**
     * Argument for the filter used for interfaces. <BR/>
     */
    public static final String ARG_FILTERID         = "fid";

    /**
     * Argument for getting the new template file for automatically generating
     * a translator. <BR/>
     */
    public static final String ARG_NEWTEMPLATE = "dintmp";

    /**
     * Argument for the extension of the translated file. <BR/>
     * This extension is used for the file which is the output of the
     * translator.
     */
    public static String ARG_TRANSEXTENSION = "transext";

    /**
     * Argument for restoring external ids if applicable. <BR/>
     */
    public static String ARG_ISRESTOREEXTERNALID = "irexid";

    /**
     * Argument for resolving the result of a query for the export
     * instead of exporting the query itself. <BR/>
     */
    public static String ARG_ISRESOLVEQUERY = "irqu";

    /**
     * Argument for resolving the reference as object. <BR/>
     */
    public static String ARG_ISRESOLVEREFERENCE = "irref";

    /**
     * Argument for resolving the reference as object with
     * the oid of the reference. <BR/>
     */
    public static String ARG_ISUSEREFERENCEOID = "iurefid";

    /**
     * Argument which holds the flag for resolving the keymapping
     * when exporting objects. In case a keymapping does not exist
     * the standard oid will be used.<BR/>
     */
    public static final String ARG_ISRESOLVEKEYMAPPING = "rkm";

    /**
     * Argument that holds xml data. <BR/>
     */
    public static final String ARG_XMLDATA = "xmldata";

    /**
     * Argument that holds the option to generate an xml response. <BR/>
     */
    public static final String ARG_XMLRESPONSE = "xmlreply";

    /**
     * Argument that holds the option to include the log in the xml response. <BR/>
     * Note that this argument is not supported at the moment but specified.
     * The same option is controlled via the isDisplayLog option.<BR/>
     */
    public static final String ARG_INCLUDELOG = "inclog";

    /**
     * Argument that holds the option to send an notification in case
     * of errors. <BR/>
     */
    public static final String ARG_ERRORNOTIFY = "enotify";

    /**
     * Argument that holds the error notification email sender. <BR/>
     */
    public static final String ARG_ERRORNOTIFYSENDER = "enotifysen";

    /**
     * Argument that holds the error notification email receiver. <BR/>
     */
    public static final String ARG_ERRORNOTIFYRECEIVER = "enotifyrec";

    /**
     * Argument that holds the error notification subject. <BR/>
     */
    public static final String ARG_ERRORNOTIFYSUBJECT = "enotifysub";

    /**
     * Argument for activate debugging. <BR/>
     */
    public static final String ARG_ISDEBUG = "isdebug";

    /**
     * Argument for a response string. <BR/>
     */
    public static final String ARG_RESPONSE = "res";

    /**
     * Argument which holds the sorting import files option.<BR/>
     */
    public static final String ARG_SORTIMPORTFILES = "isSort";

    /**
     * Argument which holds the name type mapping import option.<BR/>
     */
    public static final String ARG_NAMETYPEMAPPING = "isNTM";

    /**
     * Extension that will be added to file arguments. <BR/>
     */
    public static final String ARG_FILE_EXTENSION = "_FILE";

    /**
     * Argument for keymapping id pattern. <BR/>
     */
    public static final String ARG_IDPATTERN = "IDP";

    /**
     * Argument for keymapping domain pattern. <BR/>
     */
    public static final String ARG_DOMAINPATTERN = "DOMP";

    /**
     * Argument which holds the validate structure option.<BR/>
     */
    public static final String ARG_VALIDATESTRUCTURE = "isValStr";

    /**
     * force using the type translator. <BR/>
     */
    public static final String ARG_FORCE_TRANSLATOR = "forceTT";

    /**
     * map system values when updating dbmapping. <BR/>
     */
    public static final String ARG_UPDATEMAPPING_SYSTEM = "udbmsysv";

    /**
     * which import operation should be triggered. <BR/>
     */
    public static final String ARG_OPERATION = "op";

} // class DIArguments
