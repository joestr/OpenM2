/*
 * Class: ImportIntegrator.java
 */

// package:
package ibs.di.imp;

// imports:
//KR TODO: unsauber
import ibs.app.AppConstants;
import ibs.app.AppFunctions;
import ibs.app.AppMessages;
import ibs.app.CssConstants;
import ibs.bo.BOArguments;
import ibs.bo.BOHelpers;
import ibs.bo.BOMessages;
import ibs.bo.BOPathConstants;
import ibs.bo.BOTokens;
import ibs.bo.BusinessObject;
import ibs.bo.Datatypes;
import ibs.bo.IncorrectOidException;
import ibs.bo.OID;
import ibs.bo.ObjectClassNotFoundException;
import ibs.bo.ObjectInitializeException;
import ibs.bo.ObjectNotFoundException;
import ibs.bo.Operations;
import ibs.bo.type.TypeConstants;
import ibs.bo.type.TypeNotFoundException;
import ibs.di.DIArguments;
import ibs.di.DIConstants;
import ibs.di.DIHelpers;
import ibs.di.DIMessages;
import ibs.di.DITokens;
import ibs.di.DataElement;
import ibs.di.DataElementList;
import ibs.di.Integrator;
import ibs.di.Log_01;
import ibs.di.RTExceptionInvalidLink;
import ibs.di.ReferenceDataElement;
import ibs.di.XMLViewerContainer_01;
import ibs.di.XMLViewer_01;
import ibs.di.connect.ConnectionFailedException;
import ibs.di.connect.Connector_01;
import ibs.di.filter.Filter;
import ibs.di.trans.TranslationFailedException;
import ibs.io.Environment;
import ibs.io.HtmlConstants;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.io.LayoutConstants;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.ref.Referenz_01;
import ibs.obj.workflow.WorkflowService;
import ibs.obj.workflow.WorkflowTemplate_01;
import ibs.service.action.ActionConstants;
import ibs.service.action.ActionException;
import ibs.service.action.Variable;
import ibs.service.action.Variables;
import ibs.service.user.User;
import ibs.service.workflow.UserInteractionRequiredException;
import ibs.tech.html.BlankElement;
import ibs.tech.html.BuildException;
import ibs.tech.html.FormElement;
import ibs.tech.html.GroupElement;
import ibs.tech.html.HTMLButtonElement;
import ibs.tech.html.InputElement;
import ibs.tech.html.NewLineElement;
import ibs.tech.html.Page;
import ibs.tech.html.RowElement;
import ibs.tech.html.StyleSheetElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TableElement;
import ibs.tech.html.TextElement;
import ibs.tech.sql.DBError;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.StoredProcedure;
import ibs.tech.sql.StoredProcedureConstants;
import ibs.util.AlreadyDeletedException;
import ibs.util.DateTimeHelpers;
import ibs.util.Helpers;
import ibs.util.NoAccessException;
import ibs.util.StringHelpers;
import ibs.util.UtilConstants;
import ibs.util.UtilExceptions;
import ibs.util.file.FileHelpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;


/******************************************************************************
 * The Integrator handles Imports from XML Datasources into the m2
 * Application. <BR/>
 *
 * @version     $Id: ImportIntegrator.java,v 1.117 2011/05/10 12:52:08 rburgermann Exp $
 *
 * @author      Buchegger Bernd (BB), 981216
 ******************************************************************************
 */
public class ImportIntegrator extends Integrator
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ImportIntegrator.java,v 1.117 2011/05/10 12:52:08 rburgermann Exp $";


    /**************************************************************************
     * This private class holds the information for an imported reference
     * object. This kind of objects are processed after the normal import to
     * resolve possible forward references. <BR/>
     *
     * @version     $Id: ImportIntegrator.java,v 1.117 2011/05/10 12:52:08 rburgermann Exp $
     *
     * @author      Klaus, 12.10.2003
     **************************************************************************
     */
    private static class ReferenceInfo
    {
        /**
         * The data element for the reference. <BR/>
         */
        public DataElement p_dataElement;
        /**
         * The oid of the container where the reference exists in. <BR/>
         */
        public OID p_containerId;
        /**
         * The operation for the reference. <BR/>
         */
        public String p_operation;


        /**********************************************************************
         * Creates a ReferenceInfo object. <BR/>
         *
         * @param   dataElement The data element for the reference.
         * @param   container   The container where the reference exists in.
         * @param   operation   The operation for the reference.
         */
        public ReferenceInfo (DataElement dataElement, OID container,
            String operation)
        {
            this.p_dataElement = dataElement;
            this.p_containerId = container;
            this.p_operation = operation;
        } // ReferenceInfo
    } // class ReferenceInfo


    /**
     * import script object. <BR/>
     */
    private ImportScript_01 importScript = null;

    /**
     * import scenario object. <BR/>
     */
    private ImportScenario importScenario = null;

    /**
     * operation for the import. <BR/>
     */
    private String operation = null;

    /**
     * flag to delete import files. <BR/>
     */
    private boolean isDeleteImportFiles = false;

    /**
     * flag to enable workflow when object is imported
     * into a xmlviewercontainer that has a workflow template associated. <BR/>
     */
    private boolean isEnableWorkflow = false;

    /**
     * Extension to id for workspace user. <BR/>
     */
    private String p_extKeyWspUserExtension = null;

    /**
     * flag to enable objectname and objecttype for keymapping. <BR/>
     */
    private boolean p_isNameTypeMapping = false;

    /**
     *  option for sorting the import files before import. <BR/>
     */
    private int p_sortImportFiles = UtilConstants.ORDER_NONE;

    /**
     * OID of the import script. <BR/>
     */
    private OID importscriptOid;

    /**
     * oid of the importContainer. <BR/>
     */
    private OID importContainerId;

    /**
     * filter to be used for the connector. <BR/>
     */
    private String p_fileFilter = "";

    /**
     * Check if interface use connector. <BR/>
     */
    private boolean isInterfaceUseConnector = false;

    /**
     * Check if interface Button was clicked. If the button was clicked, the
     * argument has a value. <BR/>
     */
    private String interfaceButtonClick = "";

    /**
     * Check if system variables shall be replaced. <BR/>
     */
    private boolean p_isReplaceSysVars = false;

    /**
     * Option to validate the structure of newly created or updates objects. <BR/>
     */
    private boolean p_isValidate = false;

    /**
     * The list of object references to import. <BR/>
     * To support forward refereneces in the import document Object References
     * are not processed immediatly but stored in this list.
     * This list is processed after the import of all objects in the
     * import document.
     */
    private Vector<ImportIntegrator.ReferenceInfo> p_referenceList =
        new Vector<ImportIntegrator.ReferenceInfo> ();

    /**
     * Array with files to import. <BR/>
     */
    private String [] p_files = null;

    /**
     * File name of import file. <BR/>
     */
    private static final String FILENAME_IMPORTDATA = "importdata.xml";

    /**
     * Name of HTML page. <BR/>
     */
    private static final String PAGENAME = "Import Form";

    /**
     * Error message: error in replaceSysVars. <BR/>
     */
    private static final String ERRM_REPLACESYSVARS =
        "ImportIntegrator.replaceSysVars";


    /***************************************************************************
     * Creates an ImportIntegrator Object. <BR/>
     *
     * @param oid Oid of the object.
     * @param user User that created the object.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    @Deprecated
    public ImportIntegrator (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
    } // ImportIntegrator


    /**************************************************************************
     * Creates an ImportIntegrator Object. <BR/>
     */
    public ImportIntegrator ()
    {
        // call constructor of super class:
        super ();
    } // ImportIntegrator

    
    /**************************************************************************
     * This constructor creates a new instance of the class ImportIntegrator
     * with given user, evn, sess, app.
     * Use this constructor in case importintegrator is used as a helper class
     * and not as an object instance.
     *
     * @param   user    Object representing the user.
     * @param   env     The actual environment.
     * @param   sess    The session info object.
     * @param   app     The application info object.
     */
    public ImportIntegrator (User user, Environment env, SessionInfo sess,
                          	 ApplicationInfo app)
    {
        // call constructor of super class:
        super ();
        // init the object
        this.initObject (OID.getEmptyOid (), user, env, sess, app);
    } // ImportIntegrator
    

    /**************************************************************************
     * Read form the User the data used in the Object. <BR/>
     * HINT: deactivated because the Integrator Object has not yet a
     *       representation in the database. <BR/>
     */
    public void getParameters ()
    {
        // currently deactivated because the Integrator object has not yet a
        // representation in the database
    } // getParameters


    /**************************************************************************
     * Gets the import parameters from the environment. <BR/>
     */
    public void getImportParameters ()
    {
        String str = null;
        OID oid = null;
        int num;

        // get the import container path and set it is applicable:
        // BB HINT: this argument will be provided by an agent
        str = this.env.getStringParam (DIArguments.ARG_IMPORTCONTAINERPATH);
        if (str != null && str.length () > 0)
        {
            // try to resolve the path and get the import container id:
            // relative paths are not allowed
            this.importContainerId =
                BOHelpers.resolveObjectPath (str, this, this.env);
        } // if (oidStr != null && ! oidStr.length () == 0)

        // get the import script:
        oid = this.getCombinedOidParam (DIArguments.ARG_IMPORTSCRIPT,
            DIArguments.ARG_IMPORTSCRIPTNAME,
            this.getTypeCache ().getTypeId (TypeConstants.TC_ImportScript));
        // check if we got the oid:
        if (oid != null)
        {
            this.importScript = this.createImportScriptFromOid (oid);
        } // if (oid != null)

        // get the import script:
        str = this.env.getStringParam (DIArguments.ARG_IMPORTSCENARIOCLASS);
        // check if we got the oid:
        if (str != null && !str.isEmpty ())
        {
            // set a generic import script with a given import scenario
            // class name
            this.importScenario = this.getImportScenario (str);
            // check if initialization succeeded:
            if (this.importScenario == null)
            {
/* A Log cannot be written
                this.log.add (DIConstants.LOG_ERROR,
                              DIMessages.MSG_INVALID_IMPORTSCENARIO +
                              " " + str);
*/
            } // else error with importScenario
        } // if (str != null && str.length() > 0)

        // get the operation for the import:
        str = this.env.getStringParam (DIArguments.ARG_OPERATION);
            
        // check if we got an operation argument:
        if (str != null && !str.isEmpty())
        {
            this.operation = str;
        } // if
        else
        {
            this.operation = DIConstants.OPERATION_DEFAULT; 
        } // else
        
        // get the translator:
        this.translator = this.getTranslatorParam (
            DIArguments.ARG_TRANSLATOR, DIArguments.ARG_TRANSLATORNAME);

        // check if a xml data parameter has been set that holds the
        // import file:
        this.getImportParametersXmlDataFile ();

        // check if we have to delete the importfile:
        num = this.env.getBoolParam (DIArguments.ARG_DELETEIMPORTFILE);
        this.isDeleteImportFiles = num == IOConstants.BOOLPARAM_TRUE;

        // check if starting workflow should be activated:
        num = this.env.getBoolParam (DIArguments.ARG_ENABLEWORKFLOW);
        this.isEnableWorkflow = num == IOConstants.BOOLPARAM_TRUE;

        // check the sort import files option
        num = this.env.getIntParam (DIArguments.ARG_SORTIMPORTFILES);
        if (num != UtilConstants.ORDER_NONE)
        {
            this.p_sortImportFiles = num;
        } // if

        // get parameters for backup:
        // get the backup connector:
        this.p_backupConnector = this.getConnectorParam (
            DIArguments.ARG_BACKUPCONNECTOR,
            DIArguments.ARG_BACKUPCONNECTORNAME, null);

        // remember if a backup shall be created:
        this.p_isCreateBackup = this.p_backupConnector != null;

        // get the parameters for the log:
        if (this.log != null)
        {
            this.log.getParameters ();
        } // if
        else
        {
            // nothing to do
        } // else

        // check the xml response option:
        num = this.env.getBoolParam (DIArguments.ARG_XMLRESPONSE);
        if (num == IOConstants.BOOLPARAM_TRUE) // generate a xml response?
        {
            this.p_isGenerateXMLResponse = true;
            // if this option is activated the displayLog must be deactivated
            this.log.isDisplayLog = false;
        } // if generate a xml response
        else                            // do not generate a xml response
        {
            this.p_isGenerateXMLResponse = false;
        } // else do not generate a xml response

        // check the include log in xml response option
        num = this.env.getBoolParam (DIArguments.ARG_INCLUDELOG);
        this.p_isIncludeLog = num == IOConstants.BOOLPARAM_TRUE;

        // check the name type mapping option:
        num = this.env.getBoolParam (DIArguments.ARG_NAMETYPEMAPPING);
        this.p_isNameTypeMapping = num == IOConstants.BOOLPARAM_TRUE;

        // check the name type mapping option:
        num = this.env.getBoolParam (DIArguments.ARG_VALIDATESTRUCTURE);
        this.p_isValidate = num == IOConstants.BOOLPARAM_TRUE;

        // check the send error notification option and settings:
        num = this.env.getBoolParam (DIArguments.ARG_ERRORNOTIFY);
        this.p_isSendErrorNotification = num == IOConstants.BOOLPARAM_TRUE;
        this.p_errorNotificationSender = this.env
            .getStringParam (DIArguments.ARG_ERRORNOTIFYSENDER);
        this.p_errorNotificationReceiver = this.env
            .getStringParam (DIArguments.ARG_ERRORNOTIFYRECEIVER);
        this.p_errorNotificationSubject = this.env
            .getStringParam (DIArguments.ARG_ERRORNOTIFYSUBJECT);
    } // getImportParameters


    /**************************************************************************
     * Gets the import parameters from the environment. <BR/>
     */
    public void getImportParametersXmlDataFile ()
    {
        String fileName = "";
        OutputStreamWriter fileWriter;

        String str = null;

        // check if a xml data parameter has been set that holds the
        // import file:
        str = this.env.getStringParam (DIArguments.ARG_XMLDATA);
        if (str != null)                // parameter set?
        {
            // create the default file connector:
            // for a temporary directory
            this.connector = this.getDefaultConnector ();
            this.connector.setPath (this.connector.createTempDir ());

            try
            {
                fileName = this.connector.getPath () + ImportIntegrator.FILENAME_IMPORTDATA;
                // write the xml data into a file
                FileOutputStream fos =  new FileOutputStream (fileName);
                fileWriter = new OutputStreamWriter(fos, DIConstants.CHARACTER_ENCODING);
                fileWriter.write (str);
                fileWriter.close ();
                this.connector.setFileName (ImportIntegrator.FILENAME_IMPORTDATA);
            } // try
            catch (IOException e)
            {
                IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
                // deactivate the connector
                this.connector = null;
            } // catch
        } // if parameter set
        else                            // no parameter set
        {
            // try to get a connector
            // set the file filter
            str = this.env.getStringParam (DIArguments.ARG_FILEFILTER);
            if (str != null)
            {
                this.p_fileFilter = str;
            } // if

            // check if import source is a server directory or from upload
            str = this.env.getStringParam (DIArguments.ARG_IMPORTSOURCE);

            // first check if any import source has been set
            if (str != null && str.equals (DIConstants.SOURCE_CONNECTOR))
                                        // source is connector?
            {
                // the import has started via the import form where the
                // user selected import via connector and has selected
                // the files he wants to import
                // get the connector:
                this.connector = this.getConnectorParam (
                    DIArguments.ARG_ACTIVECONNECTOR, null, null);

                // check if the connector was set:
                if (this.connector != null)
                {
                    // set the import files:
                    this.getImportParameterImportFiles ();
                } // if
            } // if source is connector
            else if (str != null && str.equals (DIConstants.SOURCE_UPLOAD))
                                        // source is upload?
            {
                // set import has started via the import form where the
                // user selected import via file upload
                String file = this.getFileParamBO (DIArguments.ARG_UPLOADFILE);
                if (file != null && file.length () > 0)
                {
                    this.p_files = new String [] {file};
                } // if
                else
                {
                    this.p_files = new String [0];
                } // else

                // construct the import files upload path:
                String path = this.env.getStringParam (DIArguments.ARG_UPLOADFILE +
                    AppConstants.DT_FILE_PATH_EXT);
                // cut double //
                path = StringHelpers.replace (path, File.separator +
                    File.separator, File.separator);
                // add a separator if necessary
                path = FileHelpers.addEndingFileSeparator (path);
                // create a fileConnector for the uploaded file and set the path:
                this.connector = this.getDefaultConnector (path);
            } // else if source is upload
            else if (str != null && str.equals (DIConstants.SOURCE_AGENT))
                                        // source is agent?
            {
                // the import has been started via an agent that has set
                // a connector to use and a filter to apply to the result
                // of a connector.dir command
                // get the connector:
                this.connector = this.getConnectorParam (DIArguments.ARG_CONNECTOR,
                    DIArguments.ARG_CONNECTORNAME, null);

                if (this.connector != null)
                {
                    // set the filter
                    str = this.env.getStringParam (DIArguments.ARG_FILEFILTER);
                    if (str != null && str.length () > 0)
                    {
                        this.connector.setFileFilter (str);
                    } // if
                    // set the files array to null to indicate that the
                    // files should be get from the connector later
                    this.p_files = null;
                } // if (this.connector != null)
            } // else if source is agent
            else if (str != null && str.equals (DIConstants.SOURCE_DIR))
                                        // source is directory?
            {
                // the import has been started via an agent who sets
                // a server directory as source and the files he wants to
                // be import
                // get the connector for uploaded files:
                this.connector = this.getConnectorParam (null, null,
                    DIArguments.ARG_IMPORTPATH);

                // check if we got a connector:
                if (this.connector != null)
                {
                    // set the import files:
                    this.getImportParameterImportFiles ();
                } // if (str != null && (! str.length () == 0))
                else    // no path set
                {
                    // delete the connector setting because no path has been set
                    this.connector = null;
                } // else no path set
            } // else if source is directory
            else                        // no import source set
            {
                this.connector = null;
            } // else no import source set
        } // no parameter set
    } // getImportParametersXmlDataFile


    /**************************************************************************
     * Gets the import files parameter from the environment. <BR/>
     */
    public void getImportParameterImportFiles ()
    {
        String [] strArray = null;

        // set the import files:
        strArray = this.env.getMultipleParam (DIArguments.ARG_IMPORTFILE);
        if (strArray != null)
        {
            this.p_files = strArray;
        } // if
        else                            // no files selected
        {
            this.p_files = new String [0];
        } // else
    } // getImportParameterImportFiles


    /**************************************************************************
     * Set the oid of the container to be imported to. <BR/>
     *
     * @param oid   the oid of the container to be set
     */
    public void setContainerId (OID oid)
    {
        this.importContainerId = oid;
    } // setContainerId


    /**************************************************************************
     * Set the import files. <BR/>
     *
     * @param files     a string array containing the import file names
     */
    public void setImportFiles (String [] files)
    {
        this.p_files = files;
    } // setImportFiles


    /**************************************************************************
     * Set the import script. <BR/>
     *
     * @param importScript  the importscript object to set
     */
    public void setImportScript (ImportScript_01 importScript)
    {
        this.importScript = importScript;
    } // setImportScript


    /**************************************************************************
     * Set the isNameTypeMapping option. <BR/>
     *
     * @param isNameTypeMapping     the value to set
     */
    public void setIsNameTypeMapping (boolean isNameTypeMapping)
    {
        this.p_isNameTypeMapping = isNameTypeMapping;
    } // setIsNameTypeMapping


    /**************************************************************************
     * Set the isReplaceSysVars option. <BR/>
     *
     * @param isReplaceSysVars     the value to set
     */
    public void setIsReplaceSysVars (boolean isReplaceSysVars)
    {
        this.p_isReplaceSysVars = isReplaceSysVars;
    } // setIsReplaceSysVars


    /**************************************************************************
     * Set the isValidate option. <BR/>
     *
     * @param validate The p_isValidate to set.
     */
    public void setIsValidate (boolean validate)
    {
        this.p_isValidate = validate;
    } // setIsValidate


    /**************************************************************************
     * Displays the import form. <BR/>
     */
    public void showImportForm ()
    {
        if (true)                       // business object resists on this
                                        // server?
        {
            // show the search form
            this.performShowImportForm ();
        } // if business object resists on this server
        else                            // object resists on another server
        {
            // invoke the object on the other server:
        } // else object resists on another server
    } //showImportForm


    /**************************************************************************
     * Displays the import form. <BR/>
     */
    protected void performShowImportForm ()
    {
        String interfaceOidStr = "";
        String connectorOidStr = "";
        String importscriptOidStr = "";
        String translatorOidStr = "";
        String backupConnectorOidStr = "";
        GroupElement gel;
        InputElement input;
        InputElement button;
        String str;

        // set the m2AbsBasePath
        this.setM2AbsBasePath (StringHelpers.replace (
            this.app.p_system.p_m2AbsBasePath, File.separator + File.separator,
            File.separator));
        // create page
        Page page = new Page (ImportIntegrator.PAGENAME, false);
        // style sheet file
        StyleSheetElement style = new StyleSheetElement ();
        style.importSS = this.sess.activeLayout.path + this.env.getBrowser () +
            "/" + this.sess.activeLayout.elems[LayoutConstants.SHEETINFO].styleSheet;
        page.head.addElement (style);

        // create Header
        // when an import is started it will be opened in a new browser instance
        FormElement form = this.createFormHeader (page, this.name, this
            .getNavItems (), null, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_BUTTONIMPORT, env), null, this.icon,
            this.containerName);
        // add hidden elements:
        form.addElement (new InputElement (BOArguments.ARG_OID,
                                           InputElement.INP_HIDDEN,
                                           "" + this.oid));
        form.addElement (new InputElement (BOArguments.ARG_FUNCTION,
                                           InputElement.INP_HIDDEN,
                                           "" + AppFunctions.FCT_OBJECTIMPORT));
        // BB: do we need to pass the containerID?
        form.addElement (new InputElement (BOArguments.ARG_CONTAINERID,
                                           InputElement.INP_HIDDEN,
                                           "" + this.containerId));
        // create inner table
        TableElement table = new TableElement (2);
        table.border = 0;
        table.ruletype = IOConstants.RULE_NONE;
        table.frametypes = IOConstants.FRAME_BOX;
        table.width = HtmlConstants.TAV_FULLWIDTH;
        table.cellpadding = 5;

        table.classId = CssConstants.CLASS_INFO;
        String[] classIds = new String[2];
        classIds[0] = CssConstants.CLASS_NAME;
        classIds[1] = CssConstants.CLASS_VALUE;
        table.classIds = classIds;

        // check if we have an oid of the interface
        // if true we take the interface for the settings
        // if false we take the settings from the importform
        this.interfaceButtonClick = this.env.getStringParam (DIArguments.ARG_SETINTERFACE);
        if (this.interfaceButtonClick == null)
        {
            this.interfaceButtonClick = "";
        } // if
        if (this.interfaceButtonClick != null && this.interfaceButtonClick.length () > 0)
        {
            this.interfaceOid =
                this.env.getOidParam (DIArguments.ARG_IMPORTINTERFACE);
            if (this.interfaceOid != null)
            {
                // get the settings in the dialog from an interface
                this.getInterfaceSettings (this.interfaceOid);
                // the interface oid string
                interfaceOidStr = this.interfaceOid.toString ();
            } // if (interfaceOid != null)
            else
            {
                interfaceOidStr = "";
               // this.isInterfaceUsed = false;
            } // else
        } // if (!this.isInterfaceUseConnector)
        // check if we read the settings from an interface
        if (!this.isInterfaceUsed)
        {
            // get the settings from the environment
            this.getEnvSettings ();
        } // if (interfaceOidStr == null)
        // create the interface selection box
        gel = new GroupElement ();
        // create an import interface selection box
        gel = this.createSelectionBoxFromObjectType (this.getTypeCache ()
            .getTypeId (DIConstants.TC_IMPORTINTERFACE),
            DIArguments.ARG_IMPORTINTERFACE,
              interfaceOidStr, true);
        // add a line
        gel.addElement (new NewLineElement ());
        // check if the button interface is pressed
        String ivalue = "";
        String iValue2 = "'IValue'";
        // we need a hidden field for the
        // setInterface button to check if it was set
        form.addElement (new InputElement (DIArguments.ARG_SETINTERFACE,
                         InputElement.INP_HIDDEN,
                         ivalue));
        // add the set interface button
        button = new InputElement ("bint", InputElement.INP_BUTTON,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_SETINTERFACE, env));
        // define the function that is called after click on the button
        button.onClick = HtmlConstants.JREF_SHEETFORMTARGET +
            "'" + HtmlConstants.FRM_SHEET + "';" +
            HtmlConstants.JREF_SHEETFORM + BOArguments.ARG_FUNCTION +
            HtmlConstants.JREF_VALUEASSIGN + AppFunctions.FCT_SHOWOBJECTIMPORTFORM + ";" +
            HtmlConstants.JREF_SHEETFORM + DIArguments.ARG_SETINTERFACE +
            HtmlConstants.JREF_VALUEASSIGN + iValue2 +
            "; " + HtmlConstants.JREF_SHEETFORMSUBMIT;
        // add the button to the frame
        gel.addElement (button);
        // add the interface selection box to the frame
        this.showFormProperty (table, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_IMPORTINTERFACE, env), gel);
        // display a separator line
        this.showProperty (table, null, null, Datatypes.DT_SEPARATOR, (String) null);
        // get the paramter of the argument if the button is pressed
        this.interfaceButtonClick = this.env.getStringParam (DIArguments.ARG_SETINTERFACE);

        this.isUseConnector = true;
        gel = new GroupElement ();
        input = new InputElement (DIArguments.ARG_IMPORTSOURCE,
                                  InputElement.INP_RADIO,
                                  DIConstants.SOURCE_CONNECTOR);
        input.checked = this.isUseConnector;
        // check if interface has been used a connector
        if (this.isInterfaceUseConnector)
        {
            this.isUseConnector = true;
        } // if
        gel.addElement (input);
        this.showFormProperty (table, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_IMPORTSOURCECONNECTOR, env), gel);
        // check if the connector oid has been set
        connectorOidStr = "";
        if (this.connectorOid != null)
        {
            connectorOidStr = this.connectorOid.toString ();
        } // if
        // create the connector selection box
        gel = this.createConnectorSelectionBox (DIArguments.ARG_CONNECTOR,
            connectorOidStr, false, true, false);
        // add a new line
        gel.addElement (new NewLineElement ());
        // add the check connector button
        button = new InputElement (DIArguments.ARG_CHECKCONNECTOR, InputElement.INP_BUTTON,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,                                       
                DITokens.ML_SETCONNECTOR, env));
        button.onClick = HtmlConstants.JREF_SHEETFORMTARGET +
            "'" + HtmlConstants.FRM_SHEET + "';" +
            HtmlConstants.JREF_SHEETFORM + BOArguments.ARG_FUNCTION +
            HtmlConstants.JREF_VALUEASSIGN + AppFunctions.FCT_SHOWOBJECTIMPORTFORM + "; " +
            HtmlConstants.JREF_SHEETFORMSUBMIT;
        gel.addElement (button);
        // add the connector selection box to the frame
        this.showFormProperty (table, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_SELECTCONNECTOR, env), gel);

        // get the file filter if there is no interface set
        // take it from the environment
        if (!this.isInterfaceUsed)
        {
            this.p_fileFilter = DIConstants.DEFAULT_FILEFILTER;
            this.p_fileFilter
                = this.env.getStringParam (DIArguments.ARG_FILEFILTER);
            if (this.p_fileFilter == null)
            {
                this.p_fileFilter = DIConstants.DEFAULT_FILEFILTER;
            } // if
        } // if (! this.isInterfaceUsed)
        // create the file filter inputfield
        this.showFormProperty (table, DIArguments.ARG_FILEFILTER,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_FILEFILTER, env), Datatypes.DT_TEXT, this.p_fileFilter);

        // check if a connector has been selected
        if (this.connectorOid != null)
        {
            // set the connector oid string
            connectorOidStr = this.connectorOid.toString ();
            // create the connector object
            this.connector = this.createConnectorFromOid (connectorOidStr);
        } // if (oid != null)
        else    // connector not set
        {
            this.connector = null;
            connectorOidStr = "";
        }   // connector not set
        // store the active connector
        form.addElement (new InputElement (DIArguments.ARG_ACTIVECONNECTOR,
            InputElement.INP_HIDDEN, connectorOidStr));
        // check if we have a connector object and create a file selection box
        if (this.connector != null)
        {
            try
            {
                // initialize the connector but suppress the
                // creation of a temp directory.
                // BB HINT: this need to be done because the connector
                // would create a temp directory in the init method which
                // can leads to several empty temp directories when
                // the user is switching to another connector
                this.connector.isCreateTemp = false;
                this.connector.initConnector ();
/*
                if (!this.isInterfaceUseConnector)
                {
                    this.p_fileFilter = DIConstants.DEFAULT_FILEFILTER;
                } // if (!this.isInterfaceUseConnector)
*/
                this.connector.setFileFilter (this.p_fileFilter);
                String [] names;
                // get the names of the importable objects through the dir
                // command of the connector
                names = this.connector.dir ();
                // apply the filter
                names = this.connector.applyFilter (names);
                // create a selection box with the files from the connector
                gel = DIHelpers.createFileSelectionBox (DIArguments.ARG_IMPORTFILE,
                    names, this.env.getStringParam (DIArguments.ARG_IMPORTFILE),
                    false, true, this.env);
                this.showFormProperty (table, 
                    MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                        DITokens.ML_IMPORTFILE, env), gel);
            } // try
            catch (ConnectionFailedException e)
            {
                // show a javascript popup message with the message from the exception
                this.showPopupMessage (e.getMessage ());
                // display the line with the importfile showing a general error message
                this.showProperty (table,
                    DIArguments.ARG_IMPORTFILE,
                    MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                        DITokens.ML_SELECTIMPORTFILE, env),
                    Datatypes.DT_TEXT,
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_COULD_NOT_INIT_CONNECTOR, env));
            } // catch
        } // if (this.connector != null)
        else    // no connector set
        {
            this.showProperty (table,
                DIArguments.ARG_IMPORTFILE,
                MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                    DITokens.ML_SELECTIMPORTFILE, env),
                Datatypes.DT_TEXT,
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_NO_CONNECTOR_SET, env));
        } // no connector set

        // create a radio box to use upload as import source
        gel = new GroupElement ();
        input = new InputElement (DIArguments.ARG_IMPORTSOURCE,
                                  InputElement.INP_RADIO,
                                  DIConstants.SOURCE_UPLOAD);
        input.checked = !this.isUseConnector;
        gel.addElement (input);
        this.showFormProperty (table, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_IMPORTSOURCEUPLOAD, env), gel);

        // import file name to be uploaded
        str = this.getFileParamBO (DIArguments.ARG_UPLOADFILE);
        if (str != null)
        {
            this.showFormProperty (table, DIArguments.ARG_UPLOADFILE,
                MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                    DITokens.ML_UPLOADFILE, env), Datatypes.DT_IMPORTFILE, str);
        } // if
        else
        {
            this.showFormProperty (table, DIArguments.ARG_UPLOADFILE,
                MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                    DITokens.ML_UPLOADFILE, env), Datatypes.DT_IMPORTFILE, "");
        } // else

        // button to activate multiple upload
        gel = new GroupElement ();
        button = new InputElement (DIArguments.ARG_MULTIPLEUPLOAD,
                                   InputElement.INP_BUTTON,
                                   MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                                       DITokens.ML_BUTTON_MULTIPLE_UPLOAD, env));
        button.onClick = "top.callUrl ('" + AppFunctions.FCT_SHOWMULTIPLEUPLOADFORM +
                         "', null, null, 'multipleupload');";
        gel.addElement (button);
        this.showFormProperty (table, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_MULTIPLEUPLOAD, env), gel);
        // display a separator line
        this.showProperty (table, null, null, Datatypes.DT_SEPARATOR, (String) null);

        // check if the connector oid has been set
        importscriptOidStr = "";
        if (this.importscriptOid != null)
        {
            importscriptOidStr = this.importscriptOid.toString ();
        } // if
        // create the import script selection box
        gel = this.createSelectionBoxFromObjectType (this.getTypeCache ()
            .getTypeId (TypeConstants.TC_ImportScript),
            DIArguments.ARG_IMPORTSCRIPT,
              importscriptOidStr, true);
        // add the import script selection box to the frame
        this.showFormProperty (table, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_IMPORTSCRIPT, env), gel);

        // check if the translator oid has been set
        translatorOidStr = "";
        if (this.translatorOid != null)
        {
            translatorOidStr = this.translatorOid.toString ();
        } // if
        // create a selection box for the
        gel = this.createSelectionBoxFromObjectType (this.getTranslatorTypeIds (),
            DIArguments.ARG_TRANSLATOR, translatorOidStr, true);
        this.showFormProperty (table, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_TRANSLATOR, env), gel);

/* BB TODO: filters will no longer be supported!
        // create an import filter selection box
        gel = DIHelpers.createImportFilterSelectionBox
              (DIArguments.ARG_FILTERID,
               this.filterId, false);
        // add the filter selection box to the frame
        showFormProperty (table, DITokens.TOK_FILTER, gel);
*/

        // show check box for deleting import files
        this.showFormProperty (table, DIArguments.ARG_DELETEIMPORTFILE,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_DELETEIMPORTFILE, env),
            Datatypes.DT_BOOL,
            "" + this.isDeleteImportFiles);

        // show check box for activate workflow when importing into
        // xmlviewer container with an associated workflow
        this.showFormProperty (table, DIArguments.ARG_ENABLEWORKFLOW,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_ENABLEWORKFLOW, env),
            Datatypes.DT_BOOL,
            "" + this.isEnableWorkflow);

        // show check box for activate using object name/object type
        // for foreign key relationships
        this.showFormProperty (table, DIArguments.ARG_NAMETYPEMAPPING,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_NAMETYPEMAPPING, env),
            Datatypes.DT_BOOL,
            "" + this.p_isNameTypeMapping);

        // show check box for activate using object name/object type
        // for foreign key relationships
        this.showFormProperty (table, DIArguments.ARG_VALIDATESTRUCTURE,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_VALIDATESTRUCTURE, env),
            Datatypes.DT_BOOL,
            "" + this.p_isValidate);

/*
        // option to generate an xml response
        showFormProperty (table, DIArguments.ARG_XMLRESPONSE,
                          "XML Response erzeugen",
                          Datatypes.DT_BOOL,
                          "" + this.p_isGenerateXMLResponse);

        // option to include the log in the xml response
        showFormProperty (table, DIArguments.ARG_INCLUDELOG,
                          "Log in die XML Response einfügen",
                          Datatypes.DT_BOOL,
                          "" + this.p_isIncludeLog);
*/

        /*
        // ask user if backup shall be created:
        showFormProperty (table, DIArguments.ARG_CREATEBACKUP,
            DITokens.TOK_CREATEIMPORTBACKUP, Datatypes.DT_BOOL,
            "" + this.p_isCreateBackup);
*/
        // check if the backup connector oid has been set:
        backupConnectorOidStr = "";
        if (this.p_backupConnectorOid != null)
        {
            backupConnectorOidStr = this.p_backupConnectorOid.toString ();
        } // if
        // create the backup connector selection box:
        gel = this.createConnectorSelectionBox (DIArguments.ARG_BACKUPCONNECTOR,
              backupConnectorOidStr, true, false, true);
        // add the connector selection box to the output:
        this.showFormProperty (table, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_SELECTBACKUPCONNECTOR, env), gel);

        // display a separator line:
        this.showProperty (table, null, null, Datatypes.DT_SEPARATOR, (String) null);

        // initialize the log:
        if (this.log == null)
        {
            this.initLog ();
        } // if
        // create the form for log properties:
        this.log.showFormProperties (table);

        form.addElement (table);
        // create footer:
        this.createFormFooter (form, HtmlConstants.JREF_SHEETFORMTARGET +
            "'_blank';" + HtmlConstants.JREF_SHEETFORM +
            BOArguments.ARG_FUNCTION + HtmlConstants.JREF_VALUEASSIGN +
            AppFunctions.FCT_OBJECTIMPORT + ";",
            HtmlConstants.JREF_SHEETFORMTARGET +
                "'" + HtmlConstants.FRM_SHEET + "';" +
                HtmlConstants.JREF_SHEETFORM + BOArguments.ARG_FUNCTION +
                HtmlConstants.JREF_VALUEASSIGN + AppFunctions.FCT_SHOWOBJECT +
                "; " + HtmlConstants.JREF_SHEETFORM + BOArguments.ARG_OID +
                HtmlConstants.JREF_VALUEASSIGN + "'" + this.containerId +
                "'; " + HtmlConstants.JREF_SHEETFORMSUBMIT,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_BUTTON_START_IMPORT, env),
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_BUTTON_BACK, env), false,
            false);
    // build the page and show it to the user:
        try
        {
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
    } // performShowImportForm


    /**************************************************************************
     * Displays the import form. <BR/>
     */
    public void showMultipleUploadForm ()
    {
        if (true)                       // business object resists on this
                                        // server?
        {
            // show the search form
            this.performShowMultipleUploadForm ();
        } // if business object resists on this server
        else                            // object resists on another server
        {
            // invoke the object on the other server:
        } // else object resists on another server
    } // showMultipleUploadForm


    /**************************************************************************
     * Displays the import form. <BR/>
     */
    protected void performShowMultipleUploadForm ()
    {
        GroupElement gel;
        InputElement button;

        // create page
        Page page = new Page ("Multiple Upload Form", false);

        // include style sheet file:
        StyleSheetElement style = new StyleSheetElement ();
        style.importSS = this.sess.activeLayout.path + this.env.getBrowser () +
            "/" + this.sess.activeLayout.elems[LayoutConstants.SHEETINFO].styleSheet;
        page.head.addElement (style);
        // create Header
        // when an import is started it will be opened in a new browser instance
        FormElement form = this.createFormHeader (page, this.name, this
            .getNavItems (), null, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_MULTIPLEUPLOAD, env), "_self", null,
            this.containerName);
        // add hidden elements:
        form.addElement (new InputElement (BOArguments.ARG_FUNCTION, InputElement.INP_HIDDEN, "" +
                                           AppFunctions.FCT_SHOWMULTIPLEUPLOADFORM));
        // create inner table
        TableElement table = new TableElement (2);
        table.border = 0;
        table.ruletype = IOConstants.RULE_NONE;
        table.frametypes = IOConstants.FRAME_BOX;
        table.width = HtmlConstants.TAV_FULLWIDTH;
        table.cellpadding = 5;

        table.classId = CssConstants.CLASS_INFO;
        String[] classIds = new String[2];
        classIds[0] = CssConstants.CLASS_NAME;
        classIds[1] = CssConstants.CLASS_VALUE;
        table.classIds = classIds;

        // get the already uploaded files
        String[] uploadedFiles = this.env.getMultipleFormParam (DIArguments.ARG_UPLOADEDFILE);
        String[] newUploadedFiles = null;

        // check if an action has to be performed:
        if (this.env.getStringParam (DIArguments.ARG_ADD) != null)
        {
            String fileName = this.getFileParamBO (DIArguments.ARG_UPLOADFILE);

            if (fileName != null && fileName.length () > 0)
            {
                if (uploadedFiles != null)
                {
                    newUploadedFiles = new String [uploadedFiles.length + 1];
                    boolean found = false;
                    // copy the filenames array and check if the new file
                    // already exists
                    for (int i = 0; i < uploadedFiles.length; i++)
                    {
                        newUploadedFiles[i] = uploadedFiles[i];
                        // check if the file has already been uploaded
                        found = newUploadedFiles[i].equals (fileName);
                    } // for (int i = 0; i < uploadedFiles.length; i++)
                    // if the file has not been found in the list
                    // of the already uploaded files add it
                    if (!found)
                    {
                        newUploadedFiles [uploadedFiles.length] = fileName;
                    } // if
                    else // the file has already been uploaded
                    {
                        newUploadedFiles [uploadedFiles.length] = null;
                    } // else
                } // if (uploadedFiles != null)
                else    // no files uploaded yet
                {
                    newUploadedFiles = new String[] {fileName};
                } // else
            } // if (fileName != null)
            else        // no changes
            {
                newUploadedFiles = uploadedFiles;
            } // else
        } // if (env.getParam(DIArguments.ARG_ADD != null))
        else if (this.env.getStringParam (DIArguments.ARG_DELETE) != null)
        {
            // get the selected filenames
            String[] markedFiles = this.env.getMultipleFormParam (DIArguments.ARG_MARKEDFILE);
            // construct path
            if (markedFiles != null)
            {
                // get the abs base path
                String m2AbsBasePath = StringHelpers.replace (this.app.p_system.p_m2AbsBasePath,
                    File.separator + File.separator, File.separator);
                // create the path
                String path = m2AbsBasePath + BOPathConstants.PATH_UPLOAD_ABS_IMPORTFILES;
                // loop through the marked files
                for (int i = 0; i < markedFiles.length; i++)
                {
                    // delete the file
                    FileHelpers.deleteFile (path + markedFiles[i]);
                    // delete the entries from the uploaded files list
                    for (int j = 0; j < uploadedFiles.length; j++)
                    {
                        if (uploadedFiles[j] != null && uploadedFiles[j].equals (markedFiles[i]))
                        {
                            uploadedFiles[j] = null;
                            // exit the loop
                            j = uploadedFiles.length;
                        } // if (uploadedFiles[j].equals (markedFiles[i]))
                    } // for (int j = 0; j < uploadedFiles.length (); j++)
                } // for (int i = 0; i < markedFiles.length (); i++)
            } // if (markedFiles != null)
            newUploadedFiles = uploadedFiles;
        } // else if (env.getParam(DIArguments.ARG_DELETE != null))
        else    // no action to be performed
        {
            newUploadedFiles = uploadedFiles;
        } // no action to be performed
        // add the file upload dialog box
        this.showFormProperty (table, DIArguments.ARG_UPLOADFILE,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_UPLOADFILE, env), Datatypes.DT_IMPORTFILE, "");
        // display the list of uploaded files with checkboxes
        gel = new GroupElement ();
        // display the add button
        button = new InputElement (DIArguments.ARG_ADD,
            InputElement.INP_SUBMIT, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_ADD, env));
        gel.addElement (button);
        // now create the file list for the dialog:
        gel.addElement (this.performShowMultipleFileList (newUploadedFiles));
        this.showFormProperty (table, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_UPLOADEDFILES, env), gel);

        // add the close  button
        table.addElement (this.createWindowCloseButton (false));

        form.addElement (table);
        // build the page and show it to the user:
        try
        {
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
    } // performShowMultipleUploadForm


    /**************************************************************************
     * Create a list of files for output within the multiple upload dialog. <BR/>
     *
     * @param   files   The files to be displayed.
     *
     * @return  The group element to be added to the page.
     */
    protected GroupElement performShowMultipleFileList (String[] files)
    {
        GroupElement group = new GroupElement ();
        GroupElement filesElement = new GroupElement ();

        // now loop though the files:
        int fileCount = 0;
        if (files != null)
        {
            for (int i = 0; i < files.length; i++)
            {
                if (files[i] != null)
                {
                    filesElement.addElement (new InputElement (
                        DIArguments.ARG_MARKEDFILE, InputElement.INP_CHECKBOX,
                        files[i]));
                    filesElement.addElement (new BlankElement ());
                    filesElement.addElement (new TextElement (
                        files[i]));
                    filesElement.addElement (new NewLineElement ());
                    filesElement.addElement (new InputElement (
                        DIArguments.ARG_UPLOADEDFILE, InputElement.INP_HIDDEN,
                        files[i]));
                    fileCount++;
                } // if (newUploadedFiles[k] != null)
            } // for (int k = 0; k < newUploadedFiles.length (); k++)
        } //  if (newUploadedFiles != null)
        // add a delete button only when there are any
        if (fileCount > 0)
        {
            // display delete button
            group.addElement (new BlankElement ());
            InputElement button = new InputElement (DIArguments.ARG_DELETE,
                InputElement.INP_SUBMIT, 
                MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                    DITokens.ML_DELETE, env));
            group.addElement (button);
            group.addElement (new NewLineElement ());
            // now display the files
            group.addElement (new NewLineElement ());
            group.addElement (filesElement);
            group.addElement (new NewLineElement ());
            // display selection buttons
            group.addElement (this.createMarkButtons (DIArguments.ARG_MARKEDFILE));
        } // if (files > 0)

        return group;
    } // performShowMultipleFileList


    /**************************************************************************
     * Starts the import of a XML import file. <BR/>
     */
    public void startImport ()
    {
        boolean isNewLog = false;

        // set the m2AbsBasePath
        this.setM2AbsBasePath (this.app.p_system.p_m2AbsBasePath);

        // initialize a new log object if necessary
        if (this.log == null)
        {
            isNewLog = true;
            this.initLog ();
            this.log.isGenerateHtml = this.isGenerateHtml;
            this.log.isDisplayLog = this.p_isDisplayLog;
            // overwrite the user setting: save the log file in any case
            // because the file is attached to the notification e-mail
//            this.log.isWriteLog = true;
        } // if (this.log != null)

        // should we read the settings from the environment
        if (this.isGetSettingsFromEnv)
        {
            // get import parameters from the environment
            this.getImportParameters ();
        } // if (this.isGetSettingsFromEnv)


        // check if the log is new and needs to be initialized:
        if (isNewLog)
        {
            // init the log
            if (!this.log.initLog ())
            {
                this.printError (
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_COULDNOTWRITELOGFILE, env));
            } // if (! this.log.initLog ())
        } // if (isNewLog)

        // check if the import settings should be displayed
        if (this.isGenerateHtml && this.isShowSettings &&
            (!this.p_isGenerateXMLResponse))
        {
            this.performShowImportSettings ();
        } // if

        // we need to put some HTML commands at the beginning for layout purposes
        if (this.log.isDisplayLog && this.isGenerateHtml &&
            (!this.p_isGenerateXMLResponse))
        {
        	this.createHTMLHeader (this.app, this.sess, this.env);
            this.env.write ("<DIV ALIGN=\"LEFT\" STYLE=\"font-size: small;\">");
        } // if (this.log.isDisplayLog)

        // perform the import:
        this.performStartImport ();

        // we need to put some HTML commands at the end for layout purposes:
        if (this.log.isDisplayLog && this.isGenerateHtml &&
            (!this.p_isGenerateXMLResponse))
        {
            this.env.write ("</DIV>");
            this.createHTMLFooter (this.env);
        } // if (this.log.isDisplayLog)

        // now close the log:
        if (!this.log.closeLog ())
        {
            this.printError (
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_COULDNOTWRITELOGFILE, env));
        } // if (! this.log.closeLog ())

        // generate the xml response is applicable
        this.generateXMLResponse ("Import");

        // send an error notification if applicable
        this.sendErrorNotification (
            MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                DIMessages.ML_MSG_IMPORTFAILED, env), "");
    } // startImport


    /**************************************************************************
     * Starts the import with given import files using a log. <BR/>
     * Note that this method should only be used for import files that
     * are located on the server.<BR/>
     * The method has been simplified and does not generate any XML response
     * or send an error notification. It assumes that any import settings
     * will be set in another object and are not be to read from the
     * environment.<BR/>
     * A log object can be passed. If not a new log object will be created.<BR/>
     *
     * @param   files       The import files in a string array.
     * @param   sourcePath  The source directory for the import files.
     * @param   log         The log object to use.
     */
    public void startImport (String[] files, String sourcePath, Log_01 log)
    {
        Connector_01 connector;

        // set the m2AbsBasePath
        this.setM2AbsBasePath (this.app.p_system.p_m2AbsBasePath);

        // initialize a new log object in case is has not been passed
        // as parameter
        if (log == null)
        {
            this.env.write ("<DIV ALIGN=\"LEFT\"><FONT SIZE=\"2\">");
            this.initLog ();
            // init the log
            if (!this.log.initLog ())
            {
                this.printError (
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_COULDNOTWRITELOGFILE, env));
            } // if
        } // if (this.log != null)
        else
        {
            // set the log that has been passed as parameter:
            this.setLog (log);
        } // else

        // check constraint: any files to import?
        if (files == null || files.length == 0)
        {
            this.log.add (DIConstants.LOG_ENTRY, 
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_NOFILESFOUND, env));
            return;
        } // if (files == null || files.length == 0)

        // set the import files
        this.setImportFiles (files);

        // create the file connector:
        connector = this.getDefaultConnector (sourcePath);
        this.setConnector (connector);

        // perform the import:
        this.performStartImport ();

        // now close the log in case it has not been passed:
        if (log == null)
        {
            if (!this.log.closeLog ())
            {
                this.printError (
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_COULDNOTWRITELOGFILE, env));
            } // if
            this.env.write ("</FONT></DIV>");
        } // if (log != null)
    } // startImport


    /**************************************************************************
     * Starts the import of a XML import file. <BR/>
     */
    private void performStartImport ()
    {
        Date startDate;
        Date endDate;

        // write the start time of the import process into the log
        startDate = new Date ();
        this.log.processStartDate = startDate;
        this.log.add (DIConstants.LOG_ENTRY, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_IMPORT_STARTED_AT, env) +
            " " + DateTimeHelpers.dateTimeToString (startDate));

        // initialize the import and check if it was successful:
        if (this.performInitImport ())
        {
            // reset the global success flag:
            this.p_isSuccessful = true;

            // check if we have any files to import:
            if (this.p_files == null || this.p_files.length == 0)
                                        // no files to import?
            {
                this.log.add (DIConstants.LOG_ENTRY, 
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_NOFILESFOUND, env));
            } // if no files to import
            else                        // there are files to import
            {
                // loop through the files and import them:
                for (int i = 0; i < this.p_files.length; i++)
                {
                    try
                    {
                        // import the file and check if successful:
                        if (this.importFile (this.p_files[i]))
                        {
                            this.log.add (DIConstants.LOG_ENTRY,
                                MultilingualTextProvider.getMessage (
                                    DIMessages.MSG_BUNDLE,
                                    DIMessages.ML_MSG_IMPORTSUCCESSFUL, env));
                        } // if
                        else            // import failed
                        {
                            this.log.add (DIConstants.LOG_ERROR,
                                MultilingualTextProvider.getMessage (
                                    DIMessages.MSG_BUNDLE,
                                    DIMessages.ML_MSG_IMPORTFAILED, env));
                            this.p_isSuccessful = false;
                        } // else import failed
                    } // try
                    catch (Exception e)
                    {
                        this.printStackTrace (e);
                        this.p_isSuccessful = false;
                    } // catch (Exception e)
                } // for i

                // check whether import file shall be deleted:
                if (this.isDeleteImportFiles) // delete import files?
                {
                    // delete the import files:
                    if (this.performDeleteImportFiles ())
                    {
                        this.log.add (DIConstants.LOG_ENTRY,
                            MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                                DIMessages.ML_MSG_IMPORTFILEDELETED, env));
                    } // if
                    else
                    {
                        this.log.add (DIConstants.LOG_ERROR,
                            MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                                DIMessages.ML_MSG_COULDNOTDELETEIMPORTFILE, env));
                    } // else
                } // if delete import files
            } // else there are files to import

            // close the importscenario if present:
            if (this.importScenario != null) // importscenario present?
            {
                // perform any action at the end of an importscenario
                this.importScenario.executionOnEnd ();
            } // if importscenario present

            // close the connector:
            // this needs to be done in order to eventually remove temporary folders
            this.connector.close ();

            // close the filter
            // this needs to be done to remove the XML DOM tree
            // and avoid memory leaks:
            this.filter.close ();

            // close the backup connector:
            // this needs to be done in order to remove temporary folders
            if (this.p_backupConnector != null)
            {
                this.p_backupConnector.close ();
            } // if
        } // if (performInitImport ())
        else                            // import could not be initialized
        {
            this.log.add (DIConstants.LOG_ERROR,
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_COULD_NOT_INIT_IMPORT, env));
            // set the global success flag:
            this.p_isSuccessful = false;
        } // else import could not be initialized

        // write the start time of the import process into the log:
        endDate = new Date ();
        this.log.processEndDate = endDate;
        this.log.add (DIConstants.LOG_ENTRY, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_IMPORT_FINISHED_AT, env) + " " +
                     DateTimeHelpers.dateTimeToString (endDate));
        // calculate the total length of the import in seconds
        long diff = (endDate.getTime () - startDate.getTime ()) / 1000;
        this.log.add (DIConstants.LOG_ENTRY, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_IMPORT_DURATION, env) + ": " + diff + " " +
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_SECONDS, env));
    } // performStartImport


    /**************************************************************************
     * Import a certain import file using the import setttings. <BR/>
     *
     * @param importFileName    the name of the file to be imported
     *
     * @return true if the import was  successful or false otherwise
     */
    private boolean importFile (String importFileName)
    {
        String importFileNameLocal = importFileName; // variable for local assignments
        String tempFileName = null;
        String importFilePath = null;

        try
        {
            this.log.add (DIConstants.LOG_ENTRY, 
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_READ_IMPORTFILE, env) +
                ": '" + importFileNameLocal + "'");
            // read the import file from the connector
            this.connector.read (importFileNameLocal);
            // get the name of the import file cause it could have been changed
            importFileNameLocal = this.connector.getFileName ();
            importFilePath = this.connector.getPath ();

            // check if we have to create a backup:
            if (this.p_isCreateBackup && this.p_backupConnector != null)
            {
                this.log.add (DIConstants.LOG_ENTRY,
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_CREATE_BACKUP, env) + 
                    ": '" + importFileNameLocal + "'");
                // write the file to the backup connector:
                this.p_backupConnector.writeFile (importFilePath,
                    importFileNameLocal);
            } // if

            // check if system variables shall be replaced:
            if (this.p_isReplaceSysVars)
            {
                // replace system variables in import file:
                importFileNameLocal = this.replaceSysVars (importFilePath,
                    importFileNameLocal);
            } // if

            // check if a translator has been set:
            if (this.translator != null)
            {
                this.log.add (DIConstants.LOG_ENTRY,
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_TRANSLATION_STARTING, env));
                // start the conversion
                // note that the translate method returns the filename
                // of the translated file. This will be the name of
                // the input file wheras he inputfile will be renamed.
                importFileNameLocal = this.translator.translate (
                    importFilePath, importFileNameLocal, this.log);
                this.log.add (DIConstants.LOG_ENTRY,
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_TRANSLATION_SUCCESSFUL, env));
            } // if (this.translator != null)

            // set the path of the importfile in the filter
            this.filter.setPath (importFilePath);
            // set the name of the import file in the filter
            this.filter.setFileName (importFileNameLocal);
            // init the importFilter and check if  successful
            if (this.filter.init ())
            {
                // try to display the number of elements found in the import document
                // in order to give the user an idea what to except and how many
                // time it will take to finish the import
                int number = this.filter.getElementsLength ();
                // check if any objects could have been count
                if (number >= 0)
                {
                    this.log.add (DIConstants.LOG_ENTRY, number + 
                        MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                            DITokens.ML_OBJECTS_FOUND, env));
                } // if (number >= 0)
                else    // number of elements could not be located
                {
                    this.log.add (DIConstants.LOG_WARNING, 
                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                            DIMessages.ML_MSG_COULD_NOT_DETERMINE_NUMBER_OF_OBJECTS, env));
                } // number of elements could not be located
            } // if (this.filter.init())
            else    // importFilter
            {
                this.log.add (DIConstants.LOG_ERROR, 
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_COULDNOTREADIMPORTFILE, env));
                return false;
            } // else importFilter init was ok

            // now process the import and check if  successful
            if (this.performProcessImport (this.filter, this.importScript))
            {
                // get the name of the file the filter created
                // BB: in which case does that happen???
                tempFileName = this.filter.getFileName ();
                // has a temporary file been created? this is indicated
                // by a name different from the import file name
                if (!tempFileName.equals (importFileNameLocal))
                {
                    // error when deleting file?
                    if (!FileHelpers.deleteFile (this.filter.getPath () +
                            tempFileName))
                    {
                        this.log.add (DIConstants.LOG_WARNING,
                            MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                                DIMessages.ML_MSG_COULD_NOT_DELETE_FILE,
                                new String[] {tempFileName}, env));
                    } // if (! FileHelpers.deleteFile (this.filter.getPath ()
                        // ...
                } // if (! tempFileName.equals (importFileName)
                // return true to indicate that the import was  successful
                return true;
            } // if (performProcessImport (this.filter, this.importScript))

            // import failed
            return false;
        } // try
        catch (TranslationFailedException e)
        {
            this.log.add (DIConstants.LOG_ERROR, e.getMessage ());
            return false;
        } // catch
        catch (ConnectionFailedException e)
        {
            this.log.add (DIConstants.LOG_ERROR, e.getMessage ());
            return false;
        } // catch
        catch (OutOfMemoryError e)
        {
            this.log.add (DIConstants.LOG_ERROR,
                IOHelpers.formatStacktrace (Helpers.getStackTraceFromThrowable (e)));
            return false;
        } // catch
    } // importFile


    /**************************************************************************
     * import dataElement to m2 and create m2 objects in importContainer with
     * given importcontainerId. <BR/>
     *
     * @param   dataElement         The DataElement to be processed.
     * @param   importContainerId   The oid of the targetcontainer.
     * @param   importOperation     The default operation to perform
     *                              (NEW, CHANGE, ...).
     *
     * @return  <CODE>true</CODE> if the Objects where created or
     *          <CODE>false</CODE> otherwise.
     *
     */
    public boolean importElement (DataElement dataElement,
        OID importContainerId, String importOperation)
    {
        this.initObjectFactory ();
        return this.processElement (dataElement, importContainerId, importOperation);
    } // importElement


    /**************************************************************************
     * Shows the output after performing an import. <BR/>
     */
    private void performShowImportSettings ()
    {
        String importFileNames = "";
        String comma = "";

        // create page:
        Page page = new Page (ImportIntegrator.PAGENAME, false);

        // load style sheet file:
        StyleSheetElement style = new StyleSheetElement ();
        style.importSS = this.sess.activeLayout.path + this.env.getBrowser () +
            "/" + this.sess.activeLayout.elems[LayoutConstants.SHEETINFO].styleSheet;
        page.head.addElement (style);

        GroupElement gel = this.createHeader (page, this.name, this
            .getNavItems (), null, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_BUTTONIMPORT, env), this.icon, null,
            false, -1);

        page.body.addElement (gel);

        // create inner table
        TableElement table = new TableElement (2);
        table.border = 0;
        table.width = HtmlConstants.TAV_FULLWIDTH;
        table.cellpadding = 5;

        table.classId = CssConstants.CLASS_INFO;
        String[] classIds = new String[2];
        classIds[0] = CssConstants.CLASS_NAME;
        classIds[1] = CssConstants.CLASS_VALUE;
        table.classIds = classIds;

        // display the preferences we work with:

        // display the selected import files:
        if (this.p_files != null)       // import files selected?
        {
            for (int i = 0; i < this.p_files.length; i++)
            {
                importFileNames += comma + "'" + this.p_files [i] + "'";
                comma = ", ";
            } // for (int i = 0; i < this.p_files.length; i++)
        } // if import files selected
        this.showProperty (table, DIArguments.ARG_IMPORTFILE, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_IMPORTFILE, env),
            Datatypes.DT_TEXT, importFileNames);
        this.log.add (DIConstants.LOG_ENTRY, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_IMPORTFILE, env) + ": " +
            importFileNames, false);

        // display the settings of the connector:
        if (this.connector != null)
        {
            this.connector.setProperties (this.properties);
            this.connector.showSettings (table);
            this.properties = this.connector.getProperties ();
            // add the connector settings to the log:
            this.connector.addSettingsToLog (this.log);
        } // if (this.connector != null)
        else                            // no connector set
        {
            this.showProperty (table, DIArguments.ARG_CONNECTOR,
                MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                    DITokens.ML_CONNECTOR, env), 
                Datatypes.DT_TEXT, 
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_NO_CONNECTOR_SET, env));
        } // no connector set

        // show importscript settings:
        if (this.importScript != null)
        {
            this.importScript.setProperties (this.properties);
            this.importScript.showSettings (table);
            this.properties = this.importScript.getProperties ();
            // add the importScript setting to the log
            this.importScript.addSettingsToLog (this.log);
        } // if (this.importScript != null)
        else
        {
            this.showProperty (table, DIArguments.ARG_IMPORTSCRIPT,
                MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                    DITokens.ML_IMPORTSCRIPT, env),
                Datatypes.DT_TEXT,
                MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                    DITokens.ML_NOIMPORTSCRIPT, env));
        } // else

        // show translator settings:
        if (this.translator != null)
        {
            this.translator.setProperties (this.properties);
            this.translator.showSettings (table);
            this.properties = this.translator.getProperties ();
        } // if (this.importScript != null)
        else                            // no translator set
        {
            this.showProperty (table, DIArguments.ARG_TRANSLATOR,
                MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                    DITokens.ML_TRANSLATOR, env), 
                Datatypes.DT_TEXT,  
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_NO_TRANSLATOR_SET, env));
        } // else no translator set

/* BB TODO: filters will no longer be supported!
        // show the filter settings and check if the filterId is valid
        if (this.filterId >= 0 &&
            this.filterId < DIConstants.IMPORTFILTER_NAMES.length)
        {
            showProperty (table,DIArguments.ARG_FILTERID, DITokens.TOK_FILTER,
                         Datatypes.DT_TEXT, DIConstants.IMPORTFILTER_NAMES [this.filterId]);
        } // if
        else                            // no filter set
        {
            showProperty (table,DIArguments.ARG_FILTERID, DITokens.TOK_FILTER,
                         Datatypes.DT_TEXT, "");
        } // else no filter set
*/

        // display the delete import file settings:
        this.showProperty (table, DIArguments.ARG_DELETEIMPORTFILE,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_DELETEIMPORTFILE, env), 
            Datatypes.DT_BOOL, "" + this.isDeleteImportFiles);
        // add setting to log:
        // add setting to log:
        this.log.add (DIConstants.LOG_ENTRY, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_DELETEIMPORTFILE, env) + ": " +
            (this.isDeleteImportFiles ? 
             MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                 AppMessages.ML_MSG_BOOLTRUE, env) :
             MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,                 
                 AppMessages.ML_MSG_BOOLFALSE, env)), false);

        // display the activate workflow setting:
        this.showProperty (table, DIArguments.ARG_ENABLEWORKFLOW,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_ENABLEWORKFLOW, env),
            Datatypes.DT_BOOL, "" + this.isEnableWorkflow);
        // add setting to log:
        this.log.add (DIConstants.LOG_ENTRY, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_ENABLEWORKFLOW, env) + ": " +
            (this.isDeleteImportFiles ? 
             MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                 AppMessages.ML_MSG_BOOLTRUE, env) :
             MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,                 
                 AppMessages.ML_MSG_BOOLFALSE, env)), false);

        // display the use object name/type key mapping setting:
        this.showProperty (table, DIArguments.ARG_NAMETYPEMAPPING,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_NAMETYPEMAPPING, env), 
            Datatypes.DT_BOOL, "" + this.p_isNameTypeMapping);
        // add setting to log:
        this.log.add (DIConstants.LOG_ENTRY, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_NAMETYPEMAPPING, env) + ": " +
            (this.p_isNameTypeMapping ? 
             MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                 AppMessages.ML_MSG_BOOLTRUE, env) :
             MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,                 
                 AppMessages.ML_MSG_BOOLFALSE, env)), false);

        // display the use object name/type key mapping setting:
        this.showProperty (table, DIArguments.ARG_VALIDATESTRUCTURE,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_VALIDATESTRUCTURE, env), 
            Datatypes.DT_BOOL, "" + this.p_isValidate);
        // add setting to log:
        this.log.add (DIConstants.LOG_ENTRY, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_VALIDATESTRUCTURE, env) + ": " +
            (this.p_isValidate ?  
             MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                 AppMessages.ML_MSG_BOOLTRUE, env) :
             MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,                 
                 AppMessages.ML_MSG_BOOLFALSE, env)), false);

        // display the create backup settings:
        this.showProperty (table, DIArguments.ARG_CREATEBACKUP,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_CREATEIMPORTBACKUP, env), Datatypes.DT_BOOL,
            "" + this.p_isCreateBackup);
        // display the settings of the backup connector:
        if (this.p_backupConnector != null)
        {
            this.p_backupConnector.setProperties (this.properties);
            this.p_backupConnector.showSettings (table);
//            this.properties = this.connector.getProperties ();
            this.p_backupConnector.addSettingsToLog (this.log);
        } // if

        // display the log settings:
        if (this.log != null)
        {
            this.log.setProperties (this.properties);
            this.log.showProperties (table);
            this.properties = this.log.getProperties ();
        } // if (this.log != null)
        // add the close  button
        table.addElement (this.createWindowCloseButton (false));

        // finish the HTML output
        page.body.addElement (table);

        // build the page and show it to the user:
        try
        {
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
    } // performShowImportSettings


    /**************************************************************************
     * Create button for closing the window within a table row. <BR/>
     *
     * @param   isAddForm   Shall there be an own form added?
     *
     * @return  The constructed table row.
     */
    private RowElement createWindowCloseButton (boolean isAddForm)
    {
        RowElement tr = new RowElement (1);
        GroupElement group = new GroupElement ();
        TableDataElement td = null;
        HTMLButtonElement button = null;

//        LineElement line = new LineElement ();
//        line.width = "90%";
//        group.addElement (line);
        button = new HTMLButtonElement ();
        button.addLabel (
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_CLOSE, env));
        button.onClick = "top.window.close();";
        button.isDisabledOnClick = false;
        group.addElement (button);
//        line = new LineElement ();
//        line.width = "75%";
//        group.addElement (line);

        // check if a form shall be added:
        if (isAddForm)
        {
            FormElement buttonForm = new FormElement ("", "");
            buttonForm.addElement (group);
            td = new TableDataElement (buttonForm);
        } // if
        else // no form to be added
        {
            td = new TableDataElement (group);
        } // else // no form to be added

        td.colspan = 2;
        td.alignment = IOConstants.ALIGN_MIDDLE;
        tr.addElement (td);

        // return the result:
        return tr;
    } // createWindowCloseButton


    /**************************************************************************
     * Initializes the import. <BR/>
     *
     * @return true if initialization succeeded or false otherwise
     */
    public boolean performInitImport ()
    {
        String str;                     // temporary string

        try
        {
            // initialize a response object is applicable
            this.initResponse ();

            // check if a importContainerId is present
            // BB HINT: it can happen that via an ImportAgent call the
            // importContainerId could not have been set!!!
            if (this.importContainerId == null)
            {
                str = this.env.getStringParam (DIArguments.ARG_IMPORTCONTAINERPATH);
                this.log.add (DIConstants.LOG_ERROR,
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_CONTAINERNOTFOUND,
                        new String[] {str}, env));
                return false;
            } // if (this.importContainerId == null)

            // check if a connector is present:
            if (this.connector == null)
            {
                // write corresponding message to log and terminate method:
                this.log.add (DIConstants.LOG_ERROR, 
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_NO_CONNECTOR_SET, env));
                return false;
            } // if (this.connector == null)

            // connector is present
            // enable the creation of a temp directory
            this.connector.isCreateTemp = true;
            // initialize the connector:
            this.connector.initConnector ();

            // check if any files have been set:
            // if set to null this indicates that the files should be read
            // from the connector and filtering and sorting can be applied
            if (this.p_files == null)
            {
                // get the file names array from the connector applying the filter
                this.p_files = this.connector.applyFilter (
                        this.connector.dir (), this.p_fileFilter);
                // check if sorting has been activated
                if (this.p_sortImportFiles != UtilConstants.ORDER_NONE)
                {
                    this.connector.sortFiles (this.p_files, this.p_sortImportFiles);
                } // if (this.p_sortImportFiles != UtilConstants.ORDER_NONE)
            } // if (this.p_files == null)

            // ensure that at least an operation is set 
            if (this.operation == null)
            {
                this.operation = DIConstants.OPERATION_DEFAULT;
            } // if

            // ensure that the id extension is initialized:
            this.p_extKeyWspUserExtension = null;

            // initialize an object factory instance:
            this.initObjectFactory ();

            // set the connector in the objectfactory:
            this.objectFactory.setConnector (this.connector);

            // set objectname type mapping option
            this.objectFactory.setIsNameTypeMapping (this.p_isNameTypeMapping);

            // check if a translator has been set:
            if (this.translator != null)
            {
                // set the delete original after translation option
                // in case deleting import files has been activated
                // this will also delete the translated file when
                // deleting the import files
                this.translator.setIsDeleteOriginal (this.isDeleteImportFiles);
                this.translator.setIsPreserveFileName (this.isDeleteImportFiles);
            } // if (this.translator != null)

            // check if an importscript has been set and add a log entry if
            // yes:
            if (this.importScript != null)
            {
                this.log.add (DIConstants.LOG_ENTRY, 
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_IMPORTSCRIPT_SET,
                        new String[] {this.importScript.name}, env));
                // check if an importscenario has been activated:
                if (this.importScript.scenario != null &&
                    this.importScript.scenario.length () > 0)
                {
                    this.importScenario =
                        this.getImportScenario (this.importScript.scenario);
                    // check if initialization succeeded:
                    if (this.importScenario == null)
                    {
                        this.log.add (DIConstants.LOG_ERROR, 
                            MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                                DIMessages.ML_MSG_INVALID_IMPORTSCENARIO, env));
                        return false;
                    } // else error with importScenario
                } // if (importScript.scenario != null && !importScript.scenario.length () == 0)
            } // if (this.importScript != null)

            // check if an importscenario has been set
            // this can now be done directly by passing the importscenario
            // as parameter
            // Note that the response object will be set in the importscenario
            if (this.importScenario != null)
            {
                // do some initialization in the importscenario
                this.initImportScenario (this.importScenario);
                this.log.add (DIConstants.LOG_ENTRY, 
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_IMPORTSCENARIO_SET,
                        new String[] {this.importScenario.getClass ().getName ()}, env));
                // perform any action at the start of the import:
                this.importScenario.executionOnStart ();
            } // if (this.importScenario != null

            // initialize a filter instance:
            this.initFilter ();

            // init backup connector:
            if (this.p_backupConnector != null)
                                        // backup connector is present?
            {
/* KR no file prefix necessary
                // set the file prefix:
                this.p_backupConnector.setFilePrefix (this.p_timestamp + "_");
*/
                // set the m2AbsBasePath:
                this.p_backupConnector
                    .setm2AbsBasePath (this.app.p_system.p_m2AbsBasePath);

                try
                {
                    // initialize the backup connector:
                    this.p_backupConnector
                        .initBackupConnector (this.p_timestamp + "_import");
                } // try
                catch (ConnectionFailedException e1)
                {
                    this.log.add (DIConstants.LOG_ERROR,
                        "error when initializing backup connector: " +
                        e1.getMessage ());
                    return false;
                } // catch
            } // if backup connector is present

            // return true to indicate that import initialization was ok:
            return true;
        } // try
        catch (ConnectionFailedException e)
        {
            this.log.add (DIConstants.LOG_ERROR, e.getMessage ());
            return false;
        } // catch
    } // performInitImport


    /**************************************************************************
     * Processes the import. Uses the importFilter and the importScript
     * set in the objects properties. <BR/>
     *
     * @return true if import succeeded or false otherwise
     */
    public boolean performProcessImport ()
    {
        return this.performProcessImport (this.filter, this.importScript);
    } // performProcessImport


    /**************************************************************************
     * Processes the import with the importFilter set and the importScript
     * if applicable. <BR/>
     *
     * @param filter        the filter to get the data from
     * @param importScript  the importScript to work with
     *
     * @return true if import succeeded or false otherwise
     */
    private boolean performProcessImport (Filter filter,
                                          ImportScript_01 importScript)
    {
        DataElementList dataElementList;
        boolean allOk = true;
        int elementsCount = 0;
        int elementsLength = this.filter.getElementsLength ();
        Date importStartDate = new Date ();

        // initialize the references list
        this.p_referenceList = new Vector<ImportIntegrator.ReferenceInfo> ();

        // check if we can get any objects from the filter
        while (filter.hasMoreObjects ())
        {
            // reset the dataElementList
            dataElementList = null;
            // get a object collection from the filter
            // an object collection contains all objects
            // between an <OBJECTS> tag in the import file
            dataElementList = filter.nextObjectCollection ();
            // check if we got any elements
            if (dataElementList != null)
            {
                // check if importscenario is set
                if (this.importScenario != null)
                {
                    try
                    {
                        // process the importscenario
                        allOk &= this.processImportScenario (dataElementList,
                                importScript, this.importScenario,
                                this.importContainerId, this.operation);
                    } // try
                    catch (Exception e)
                    {
                        this.printStackTrace (e);
                        allOk = false;
                    } // catch (Exception e)
                    // print a status bar
                    this.showStatusBar ();
                } // if (importScenario != null)
                // check if import script present
                else if (importScript != null)
                {
                    try
                    {
                        // check if import script could have been processed
                        allOk &= this.processImportScript (dataElementList, 
                            importScript, this.operation);
                    } // try
                    catch (Exception e)
                    {
                        this.printStackTrace (e);
                        allOk = false;
                    } // catch (Exception e)
                    // print a status bar
                    this.showStatusBar ();
                } // if (importScript != null)
                else    // no import script and no importscenario
                {
                    // process the data elements without import script
                    allOk &= this.processElementList (dataElementList,
                            this.importContainerId, this.operation);
                } // no import script
                // increase the counter of objects already processed
                elementsCount += dataElementList.dataElements.size ();
                // show a forecase how long the import will last
                this.showTimeRemaining (importStartDate, elementsLength, elementsCount);
            } // if (DataElementList != null)
        } // while (importFilter.hasMoreObjects ())


        // now process the list of the references (Links)
        if (this.p_referenceList.size () > 0)
        {
            for (int i = 0; i < this.p_referenceList.size (); i++)
            {
                // get the reference info from the list
                ReferenceInfo ref = this.p_referenceList.elementAt (i);
                allOk &= this.processReference (ref);
            } // for i
        } // if there are refereneces

        return allOk;
    } // performProcessImport


    /**************************************************************************
     * Import a Link object. <BR/>
     *
     * @param   ref     the reference object to process
     *
     * @return  <CODE>true</CODE>, if everything was o.k.,
     *          <CODE>false</CODE> otherwise.
     */
    private boolean processReference (ReferenceInfo ref)
    {
        boolean allOk = false;
        try
        {
            // check the opertion to perform
            if (ref.p_operation.equalsIgnoreCase (DIConstants.OPERATION_NEW) ||
                ref.p_operation.equalsIgnoreCase (DIConstants.OPERATION_NEWONLY))
            {
                //
                // create the Reference object
                Referenz_01 link = (Referenz_01) this.getObjectCache ().fetchNewObject (
                    TypeConstants.TC_Reference, this.user, this.sess, this.env);

                if (link != null)
                {
                    // set containerId of the link object
                    link.containerId = ref.p_containerId;
                    link.isLink = true;

                    try
                    {
                        // read the information from the data element
                        link.readImportData (ref.p_dataElement);
                        // create the new object:
                        link.createActive (0);
                        // retrieve the new create object to get the oid
                        link.retrieve (Operations.OP_NONE);

                        // create the EXTKEY for the link object
                        this.objectFactory.createKeyMapper (link.oid,
                            ref.p_dataElement.id, ref.p_dataElement.idDomain);

                        // add log entry that a reference has been successfully created
                        this.log
                            .add (DIConstants.LOG_ENTRY,  
                                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                                    DIMessages.ML_MSG_LINK_CREATED,
                                    new String[] {ref.p_dataElement.name}, env));
                        allOk = true;
                    } // try
                    catch (RTExceptionInvalidLink e)
                    {
                        // add log entry that a reference
                        this.log
                            .add (DIConstants.LOG_WARNING,  
                                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                                    DIMessages.ML_MSG_COULD_NOT_CREATE_LINK,
                                    new String[] {ref.p_dataElement.name}, env) +
                                " " + e.getMessage ());
                        allOk = false;
                    } // catch
                    catch (NoAccessException e)
                    {
                        // add log entry that a reference
                        this.log
                            .add (DIConstants.LOG_WARNING,  
                                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                                    DIMessages.ML_MSG_COULD_NOT_CREATE_LINK,
                                    new String[] {ref.p_dataElement.name}, env) +
                                " " + e.getMessage ());
                        allOk = false;
                    } // catch
                    catch (AlreadyDeletedException e)
                    {
                        // add log entry that a reference
                        this.log
                            .add (DIConstants.LOG_WARNING,  
                                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                                    DIMessages.ML_MSG_COULD_NOT_CREATE_LINK,
                                    new String[] {ref.p_dataElement.name}, env) +
                                " " + e.getMessage ());
                        allOk = false;
                    } // catch
                    catch (ObjectNotFoundException e)
                    {
                        // add log entry that a reference
                        this.log
                            .add (DIConstants.LOG_WARNING,  
                                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                                    DIMessages.ML_MSG_COULD_NOT_CREATE_LINK,
                                    new String[] {ref.p_dataElement.name}, env) +
                                " " + e.getMessage ());
                        allOk = false;
                    } // catch
                } // if object ready
                else                    // object not created
                {
                    this.log.add (DIConstants.LOG_WARNING,  
                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                            DIMessages.ML_MSG_COULD_NOT_CREATE_LINK,
                            new String[] {ref.p_dataElement.name}, env));
                    allOk = false;
                } // else object not created
            } // if the operation is OPERATION_NEW or OPERATION_NEWONLY
            else
            {
                allOk = this.processElementInternal (ref.p_dataElement,
                    ref.p_containerId, ref.p_operation);
            } // if unsupported operation
        } // try
        catch (TypeNotFoundException e)
        {
            this.log.add (DIConstants.LOG_ERROR, e.toString ());
            allOk = false;
        } // catch
        catch (ObjectClassNotFoundException e)
        {
            this.log.add (DIConstants.LOG_ERROR, e.toString ());
            allOk = false;
        } // catch
        catch (ObjectInitializeException e)
        {
            this.log.add (DIConstants.LOG_ERROR, e.toString ());
            allOk = false;
        } // catch

        return allOk;
    } // processReference


    /**************************************************************************
     * Get the extension for the ext key for actual workspace user. <BR/>
     *
     * @param   actOid  The actual oid.
     *
     * @return  The extension string to be added to the original id.
     */
    private String getExtKeyWspUserExtension (OID actOid)
    {
        // check if the extension has already been computed:
        if (this.p_extKeyWspUserExtension == null)
        {
            // compute the extension:
            // get the user for the current workspace where the object is
            // within:
            // create the extension out of the username:
            this.p_extKeyWspUserExtension = "_" + this.getWspUsername (actOid);
        } // if

        // return the extension:
        return this.p_extKeyWspUserExtension;
    } // getExtKeyWspUserExtension


    /**************************************************************************
     * Get the username for the user of the actual workspace. <BR/>
     * This method tries to find out within which workspace the actual object
     * resides and returns the name of the user to whom the workspace belongs.
     * <BR/>
     * If the object is not within a workspace or it cannot be found the result
     * is <CODE>null</CODE>.
     *
     * @param   actOid  The actual oid.
     *
     * @return  The username or <CODE>null</CODE> if the object is not within
     *          a workspace or it was not found.
     */
    private String getWspUsername (OID actOid)
    {
        int rowCount;                   // row counter
        SQLAction action = null;        // the action object used to access the DB
        StringBuffer queryStr;          // the query string
        String retVal = null;           // the return value

        // get the elements out of the database:
        // create the SQL String to select the user
        // get the user for the current workspace where the object is
        // within:
        queryStr = new StringBuffer ()
            .append ("SELECT u.id, u.name")
            .append (" FROM ibs_User u, ibs_Workspace wsp, ibs_Object wspO,")
            .append (" ibs_Object o, ibs_User own")
            .append (" WHERE u.id = wsp.userId")
            .append (" AND wsp.workspace = wspO.oid")
            .append (" AND o.oid = ").append (actOid.toStringQu ())
            .append (" AND o.posNoPath LIKE wspO.posNoPath + '%'")
            .append (" AND o.owner = own.id")
            .append (" AND u.domainId = own.domainId");

//showDebug ("queryStr: " + queryStr);
        try
        {
            action = this.getDBConnection ();
            rowCount = action.execute (queryStr, false);
            // the result must be exactly one row
            // else there was a db error or the object could not be found
            // or the have been more then one object with this name
            if (rowCount == 1)
            {
                // get the user name:
                retVal = action.getString ("name");
            } // if (rowCount == 1)
            action.end ();
        } // try
        catch (DBError e)
        {
            // possibly some error handling here
        } // catch
        finally
        {
            this.releaseDBConnection (action);
        } // finally

        // return the result:
        return retVal;
    } // getWspUsername


    /**************************************************************************
     * Processes a list of DataElemets with a specific targetcontainer. <BR/>
     *
     * @param dataElementList   the list of DataElements to be prcessed
     * @param importContainerId the oid of the targetcontainer
     * @param importOperation   the default operation to perform (NEW, CHANGE, CHANGE-ONLY,...)
     *
     * @return true if all Objects wheer created or false otherwise
     */
    private boolean processElementList (DataElementList dataElementList,
                                        OID importContainerId,
                                        String importOperation)
    {
        boolean allOk = true;       // return value
        // check if we got any data elements
        if (dataElementList != null)
        {
            // is a specific operation for the dataElementList set?
            if (dataElementList.operation != null)
            {
                importOperation = dataElementList.operation;
            } // if
            
            // loop through the dataElementsList and process each
            // data element separately
            for (Iterator<DataElement> iter = dataElementList.dataElements.iterator ();
                 iter.hasNext ();)
            {
                // get an element from the iterator:
                DataElement dataElement = iter.next ();
                // is a specific operation for the dataElement set?
                if (dataElement.operation != null)
                {
                    // process the dataElement with specific operation:
                    allOk &= this.processElement (dataElement, importContainerId,
                        dataElement.operation);
                } // if
                else
                {
                    // process the dataElement with default operation:
                    allOk &= this.processElement (dataElement, importContainerId,
                        importOperation);
                } // else
            } // for iter
        } // if (dataElementList != null)

        // return value
        return allOk;
    } // processElementList


    /**************************************************************************
     * Processes a single DataElement with a specific targetcontainer. <BR/>
     * This method processes the tabs and subobjects too.
     * If the given dataElement belongs to a Reference_01 object (Link)
     * the dataElement is not processes but insrted in the reference list.
     * The reference list is processed after the normal import process to
     * resolve forward references. (see method processReference())
     *
     * @param dataElement       the DataElement to be prcessed
     * @param importContainerId the oid of the targetcontainer
     * @param importOperation   the default operation to perform (NEW, CHANGE, ...)
     *
     * @return true if the Objects wheer created or false otherwise
     */
    private boolean processElement (DataElement dataElement,
        OID importContainerId, String importOperation)
    {
        // if the data element belongs to an Reference object (Link)
        // the data element is not imported immediatly but stored in
        // the reference list.
        // The reference list is processed after the normal import
        // process to resolve possible forward references.
        if (dataElement.p_typeCode.equals (TypeConstants.TC_Reference))
        {
            // add the data element to the references list
            this.p_referenceList.addElement (
                new ReferenceInfo (dataElement, importContainerId, importOperation));
            return true;
        } // if the data element belongs to an Link object

        // all other object types are processed (imported) immediatly.
        return this.processElementInternal (dataElement, importContainerId, importOperation);
    } // processElement


    /**************************************************************************
     * Processes a single DataElement with a specific targetcontainer. <BR/>
     * This method processes the tabs and subobjects too.
     *
     * @param dataElement       the DataElement to be prcessed
     * @param importContainerId the oid of the targetcontainer
     * @param importOperation   the default operation to perform (NEW, CHANGE, ...)
     *
     * @return true if the Objects wheer created or false otherwise
     */
    private boolean processElementInternal (DataElement dataElement,
        OID importContainerId, String importOperation)
    {
        boolean allOk = false;
        OID newContainerOid = null;

        // set the absolute path
        dataElement.m2AbsBasePath = this.m2AbsBasePath;
        // set path where attached files will be read from
        dataElement.sourcePath = (this.connector != null) ?
                                    this.connector.getPath () :
                                    null;
        // now check if a container has been set in the importdocument

        if (dataElement.containerId != null &&
            dataElement.containerId.length () > 0)
        {
            // resolve the container oid
            newContainerOid = this.getContainerOidFromType (
                dataElement.containerType, dataElement.containerId,
                dataElement.containerIdDomain, dataElement.containerTabName,
                importContainerId);
        } // if (dataElement.container != null)
        else        // no object specific container defined
        {
            newContainerOid = importContainerId;
        } // no object specific container defined

        // check if the workspace user shall be added to the extkey:
        if (dataElement.idAddWspUser)
        {
            // get the extension and add it to the data element id:
            dataElement.extendId (this.getExtKeyWspUserExtension (newContainerOid));
        } // if

        // perform the import operation according to the given default operation
        // BB TODO: Note that NEWONLY and NEW are not really the same!
        //          NEW means create the object without checking key mappings
        //          NEWONLY first checks for key mapping before creating
        allOk = this.performImportOperation (dataElement, importContainerId,
            importOperation, newContainerOid);

        // print a status bar
        this.showStatusBar ();

        return allOk;
    } // processElementInternal


    /**************************************************************************
     * Processes a single DataElement with a specific targetcontainer. <BR/>
     * This method processes the tabs and subobjects too.
     *
     * @param dataElement       the DataElement to be prcessed
     * @param importContainerId the oid of the targetcontainer
     * @param importOperation   the default operation to perform (NEW, CHANGE, ...)
     * @param newContainerOid   Oid of container where the object has to be
     *                          moved to.
     *
     * @return true if the Objects wheer created or false otherwise
     */
    private boolean performImportOperation (DataElement dataElement,
        OID importContainerId, String importOperation, OID newContainerOid)
    {
        boolean allOk = false;
        boolean isNewCreated = false;
        BusinessObject obj = null;

        // perform the import operation according to the given default operation
        // BB TODO: Note that NEWONLY and NEW are not really the same!
        //          NEW means create the object without checking key mappings
        //          NEWONLY first checks for key mapping before creating
        if (importOperation.equalsIgnoreCase (DIConstants.OPERATION_NEW) ||
            importOperation.equalsIgnoreCase (DIConstants.OPERATION_NEWONLY))
        {
            // create the object with the import data now
            obj = this.objectFactory.createObject (dataElement,
                    newContainerOid, false, this.p_isValidate);
            allOk = obj != null;
            // the STARTWORKFLOW option is only available for new created objects
            isNewCreated = true;
        } // if operation is OPERATION_NEW
        else if (importOperation.equalsIgnoreCase (DIConstants.OPERATION_CHANGE))
        {
            // change the object with the import data:
            allOk = this.objectFactory.changeObject (dataElement,
                    newContainerOid, false, this.p_isValidate);

            if (allOk)
            {
                this.log.add (DIConstants.LOG_ENTRY, null,
                    dataElement.typename + " '" +
                    dataElement.name + "' " +  
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_OBJECTCHANGED, env));
            } // if change performed
            else
            {
                this.log.add (DIConstants.LOG_ENTRY, null,
                    dataElement.typename + " '" +
                    dataElement.name + "' " +  
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_COULDNOTCHANGEOBJECT, env));
                // if the change fails we assume the object does not exist.
                // in this case create the object
                obj = this.objectFactory.createObject (dataElement,
                        newContainerOid, false, this.p_isValidate);
                allOk = obj != null;
                // the STARTWORKFLOW option is only available for new created objects
                isNewCreated = true;
            } // if change fails
        } // if operation is OPERATION_CHANGE
        else if (importOperation.equalsIgnoreCase (DIConstants.OPERATION_CHANGEONLY))
        {
            // change the object with the import data including a structural check
            allOk = this.objectFactory.changeObject (dataElement,
                    newContainerOid, false, this.p_isValidate);
            if (allOk)
            {
                this.log.add (DIConstants.LOG_ENTRY, null,
                    dataElement.typename + " '" +
                    dataElement.name + "' " +  
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_OBJECTCHANGED, env));
            } // if operation was successful
            else
            {
                this.log.add (DIConstants.LOG_ERROR, null,
                    dataElement.typename + " '" +
                    dataElement.name + "' " +  
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_COULDNOTCHANGEONLYOBJECT, env));
            } // else if operation was successful
        } // if operation is OPERATION_CHANGEONLY
        else if (importOperation.equalsIgnoreCase (DIConstants.OPERATION_DELETE))
        {
            // operation DELETE means that we try to delete object if it exists
            // try to delete the object
            allOk = this.objectFactory.deleteObject (dataElement);
            if (allOk)
            {
                this.log.add (DIConstants.LOG_ENTRY, null,
                    dataElement.typename + " '" +
                    dataElement.name + "' " +  
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_OBJECTDELETED, env));
            } // if (deleteObject (DataElement))
            else
            {
                this.log.add (DIConstants.LOG_ERROR, null,
                    dataElement.typename + " '" +
                    dataElement.name + "' " +  
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_COULDNOTDELETEOBJECT, env));
            } // else could not delete object
        } // if operation is OPERATION_DELETE
        else if (importOperation.equalsIgnoreCase (DIConstants.OPERATION_CUSTOM))
        {
            // operation CUSTOM means that some sort of defined custom operation
            // will be performed on the object.
            // the script references a custom operation
            // this is a workaround for creating complex objects
            //
            // BB HINT: to be extended in the future
            //
            allOk = this.processCustomOperation (dataElement, importOperation);
        } // else if (ise.operationType.equalsIgnoreCase(DIConstants.OPERATION_CUSTOM))
        else if (importOperation.equalsIgnoreCase (DIConstants.OPERATION_NONE))
        {
            // operation NONE means that the object will be ignored and no
            // operation will be performed on the object
            // we dont want any operation to perform on the object
            // therefore do nothing and ignore the object
            this.log.add (DIConstants.LOG_ENTRY, null,
                dataElement.typename + " '" +
                dataElement.name + "' " +  
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_OBJECTIGNORED, env));
            allOk = true;
        } // if operation is OPERATION_NONE
        else
        {
            // show an error message
            this.log.add (DIConstants.LOG_WARNING, 
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_UNKNOWN_OPERATION, env));
            allOk = false;
        } // else if the operation is invalid


        // check if the main object was processed successfully:
        if (allOk)
        {
            // create possibly existing tabs
            allOk &= this.processTabElements (dataElement.tabElementList,
                        dataElement.oid, importOperation);
            // create possibly existing objects beyond the current
            allOk &= this.processElementList (dataElement.dataElementList,
                        dataElement.oid, importOperation);
            // create references
            allOk &= this.createReferences (dataElement);
            // check if workflow has been enabled
            // and try to start a workflow in case the object is created within
            // an xmlviewercontainter with associated workflow
            if (isNewCreated && this.isEnableWorkflow && obj != null)
            {
                this.checkWorkflow (obj);
            } // if (this.isEnableWorkflow)
        } // creation of the object was a success
        else
        {
            // import operation not successful
            // no other objects belonging to this one may be processed
            dataElement.dataElementList = null;
            dataElement.tabElementList = null;
        } // creation of the object not succeded

        // return the result:
        return allOk;
    } // performImportOperation


    /***************************************************************************
     * Get the oid of the object which represents a tab of the actual object
     * determined by the tabs code out of the database. <BR/>
     *
     * @param   tabCode Code of the tab get from the dataElement.
     * @param   oid     The oid of the object for which to retrieve the tab tab.
     *
     * @return  The oid of the object which represents the tab.
     *          null if the tab object was not found.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     * @exception   ObjectNotFoundException
     *              The required object was not found during search, i.e. there
     *              does not exist any tab of the actual object with the
     *              required name.
     */
    private OID performRetrieveTabData (String tabCode, OID oid)
        throws NoAccessException, ObjectNotFoundException
    {
        OID tabOid = null;                      // oid of the tab object
        // create stored procedure call:
        StoredProcedure sp = new StoredProcedure ("p_DocumentTemplate_01$getTab",
            StoredProcedureConstants.RETURN_VALUE);

        // set parameters:
        // the tab code (input)
        sp.addInParameter (ParameterConstants.TYPE_STRING, tabCode);
        // oid (input)
        sp.addInParameter (ParameterConstants.TYPE_STRING, oid.toString ());

        // tab Oid (output)
        Parameter paramTabOid =
            sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);

        // perform the function call:
        int retVal = BOHelpers.performCallFunctionData (sp, this.env);

        if (retVal == UtilConstants.QRY_OBJECTNOTFOUND) // object not found?
        {
            // raise no access exception
            throw new ObjectNotFoundException (MultilingualTextProvider.getMessage (
                UtilExceptions.EXC_BUNDLE, UtilExceptions.ML_E_OBJECTNOTFOUNDEXCEPTION,
                new String[] {oid.toString ()}, env) + "." + tabCode);
        } // else if object not found
        else if (retVal == UtilConstants.QRY_OK)    // access allowed
        {
            // set object properties - get them out of parameters
            tabOid = SQLHelpers.getSpOidParam (paramTabOid);
        } // else if access allowed

        // return the oid of the tab object
        return tabOid;
    } // performRetrieveTabData


    /**************************************************************************
     * Processes a list of DataElements representing tabs with a specific
     * target container (the object the tabs belong to). <BR/>
     *
     * @param   tabElementList      The list of DataElements to be prcessed.
     * @param   importContainerId   The oid of the targetcontainer.
     * @param   importOperation     The default operation to perform.
     *
     * @return  <CODE>true</CODE> if all Objects where created or
     *          <CODE>false</CODE> otherwise.
     */
    protected boolean processTabElements (DataElementList tabElementList,
        OID importContainerId, String importOperation)
    {
        boolean allOk = true;       // return value

        // check if we got a tab elements list
        if (tabElementList != null)
        {
            // loop through the dataElementsList and process each
            // data element separately
            for (Iterator<DataElement> iter = tabElementList.dataElements.iterator ();
                 iter.hasNext ();)
            {
                // get a tab:
                DataElement dataElement = iter.next ();
                // get the oid of the tab object
                try
                {
                    dataElement.oid = this.performRetrieveTabData (
                        dataElement.p_tabCode, importContainerId);
                } // try
                catch (NoAccessException e) // no access to objects allowed
                {
                    // send message to the user:
                    this.showNoAccessMessage (Operations.OP_VIEW);
                } // catch
                catch (ObjectNotFoundException e) // object not found
                {
                    // send message to the user:
                    IOHelpers.showMessage (MultilingualTextProvider
                        .getMessage(BOMessages.MSG_BUNDLE,
                        BOMessages.ML_MSG_OBJECTNOTFOUND, this.env),
                        this.app, this.sess, this.env);
                } // catch
                // if the tab has been created ...
                if (dataElement.oid != null)
                {
                    // change the tab object
                    this.objectFactory.changeObject (dataElement,
                            null, true, this.p_isValidate);
                    // create possibly existing objects beyond the tab if it exists
                    if (dataElement.dataElementList != null)
                    {
                        allOk = this.processElementList (
                            dataElement.dataElementList, dataElement.oid,
                            importOperation);
                    } // if the tab object has subobjects
                } // if (dataElement.oid != null)
            } // for iter
        } // if (tabElementList != null)
        // return value
        return allOk;
    } // processTabElements


    /**************************************************************************
     * Creates references to an object from the declarations in the
     * importdocument. <BR/>
     *
     * @param   dataElement The dataElement that stores the references
     *                      definitions.
     *
     * @return  <CODE>true</CODE>, if everything was o.k.,
     *          <CODE>false</CODE> otherwise.
     */
    private boolean createReferences (DataElement dataElement)
    {
        ReferenceDataElement ref;
        boolean allOk = true;
        String refContainerName;
        BusinessObject container;

        // check there is a dataElement and references defined within the
        // data element
        if (dataElement != null && dataElement.references != null)
        {
            // get the references from the dataElement
            // loop trough the reference elements and try to resolve
            // all container oids set in the references.
            for (Iterator<ReferenceDataElement> iter = dataElement.references.iterator ();
                 iter.hasNext ();)
            {
                ref = iter.next ();
                // resolve the container oid set in the reference
                container = this.getContainerFromType (ref.containerType,
                    ref.containerId, ref.containerIdDomain,
                    ref.containerTabName, this.importContainerId);
                // create a description of the path that has been resolved
                // that will be used for the log entry
                if (ref.containerType.equalsIgnoreCase (DIConstants.CONTAINER_INHERIT))
                {
                    refContainerName = this.importContainerId.toString ();
                } // if
                else if (ref.containerType.equalsIgnoreCase (DIConstants.CONTAINER_PATH))
                {
                    refContainerName = ref.containerId;
                } // else if
                else        // any other type
                {
                    // only set the container name in case we got a valid
                    // container object and it is not a tab object
                    if (container != null && ref.containerTabName.length () == 0)
                    {
                        refContainerName = container.name;
                    } // if
                    else    // no container or a tab
                    {
                        if (ref.containerType.equalsIgnoreCase (DIConstants.CONTAINER_EXTKEY))
                        {
                            refContainerName = ref.containerIdDomain + " / " + ref.containerId;
                        } // if
                        else    // only display the id
                        {
                            refContainerName = ref.containerId;
                        } // else
                    } // else no container or a tab
                } // else any other type
                // is it a tab then add the tab info
                if (ref.containerTabName.length () > 0)
                {
                    refContainerName += " - " + 
                    MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                        DITokens.ML_TAB, env) + ": " + ref.containerTabName;
                } // if

                // check if we got the container object and create the reference
                if (container != null)
                {
                    // create the reference in the resolved container
                    // and check if it was  successful
                    if (this.objectFactory.performCreateReference (container.oid,
                            dataElement.oid, dataElement.name))
                    {
                        // add log entry that a reference has been successfully created
                        this.log.add (DIConstants.LOG_ENTRY, 
                            MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                                DIMessages.ML_MSG_REFERENCE_CREATED,
                                new String[] {refContainerName}, env));
                    } // if (this.objectFactory.performCreateReference (newContainerOid, ..
                    else                    // error while creating reference
                    {
                        this.log.add (DIConstants.LOG_WARNING, 
                            MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                                DIMessages.ML_MSG_COULD_NOT_CREATE_REFERENCE,
                                new String[] {refContainerName}, env));
                        allOk = false;
                    } // error while setting rights
                } // if (newContainerOid != null)
                else    // could not get the container object
                {
                    // add the log entry
                    this.log.add (DIConstants.LOG_WARNING, 
                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                            DIMessages.ML_MSG_COULD_NOT_CREATE_REFERENCE,
                            new String[] {refContainerName}, env));
                    allOk = false;
                } // could not get the container object
            } // for iter
        } // if (dataElement != null)
        return allOk;
    } // createReferences


    /**************************************************************************
     * Processes the DataElementList in context of an importscript. <BR/>
     * The method checks whether the standard importscript processing has to
     * be performed or if there is a custom import processing defined
     *
     * @param dataElementList the DataElementList that holds a collection of
     *                          import elements
     * @param importScript      the importscript that describes the import
     * @param importOperation   default operation for the import
     *
     * @return <code>true</code> if the importscript processing has succeeded
     *             or <code>false</code> otherwise
     */
    private boolean processImportScript (DataElementList dataElementList,
                                         ImportScript_01 importScript,
                                         String importOperation)
    {
        // CONTRAINT: Any data elements set?
        if (dataElementList == null)
        {
            return true;
        } // if (DataElementList == null)

        // perform standard importscript processing
        return this.performProcessImportScript (dataElementList, importScript, 
            null, importOperation);
    } // processImportScript


    /**************************************************************************
     * Processes the DataElementList via an import scenario. <BR/>
     *
     * @param dataElementList the DataElementList that holds a collection of
     *                          import elements
     * @param importScript      the importscript that describes the import
     * @param importScenario    the importscenario to perform the import
     * @param importContainerId the oid of the importcontainer
     * @param importOperation   default operation for the import
     *
     * @return <code>true</code> if the importscenario processing has succeeded
     *             or <code>false</code> otherwise
     */
    private boolean processImportScenario (DataElementList dataElementList,
                                           ImportScript_01 importScript,
                                           ImportScenario importScenario,
                                           OID importContainerId,
                                           String importOperation)
    {
        // CONTRAINT: Any data elements set?
        if (dataElementList == null)
        {
            return true;
        } // if (DataElementList == null)

        // there is data to be processed
        // check if we have to process an import scenario
        if (importScenario != null)
        {
            // process the importScenario and returns the state
            return importScenario.process (dataElementList, importScript, 
                importContainerId, importOperation);
        } // if (importScript.custom != null && !importScript.custom.length () == 0)

        // this case should not be possible
        return false;
    } // processImportScenario


    /**************************************************************************
     * Processes the DataElementList in context of an standard
     * importscript. <BR/>
     *
     * @param dataElementList   the DataElementList that holds a collection of
     *                          import elements
     * @param importScript      the importscript that describes the import
     * @param containerOid      the oid of the container, where the objects
     *                          should be created in
     * @param importOperation   default operation for the import
     *
     * @return  <CODE>true</CODE> if the importscript processing has succeeded.
     */
    private boolean performProcessImportScript (DataElementList dataElementList,
                                                ImportScript_01 importScript,
                                                OID containerOid,
                                                String importOperation)
    {
        ImportScriptElement ise;
        boolean allOk = true;

        // first check if there are any elements to process:
        if (dataElementList == null)
        {
            return true;
        } // if (dataElementList == null)

        // is a specific operation for the dataElementList set? 
        if (dataElementList.operation != null)
        {
            importOperation = dataElementList.operation; 
        } // if

        // loop through the DataElements
        for (Iterator<DataElement> iter = dataElementList.dataElements.iterator ();
             iter.hasNext ();)
        {
            DataElement dataElement = iter.next ();
            OID specificContainerOid = null;

            // set some DataElement values:
            dataElement.m2AbsBasePath = this.m2AbsBasePath;
            // set the source path where attached files can be read from
            dataElement.sourcePath = this.connector.getPath ();

            // first check if there is already a specific container id defined
            // this means the object is a subobject of another object and we
            // have to use the setting in containerOid
            if (containerOid == null)
            {
                // check if a container has been set in the dataElement
                // this means that the object has its own container settings
                // that will overwrite the importscript settings
                if (dataElement.containerId != null &&
                    dataElement.containerId.length () > 0)
                {
                    // resolve the container oid
                    specificContainerOid = this.getContainerOidFromType (
                        dataElement.containerType, dataElement.containerId,
                        dataElement.containerIdDomain,
                        dataElement.containerTabName, this.importContainerId);
                } // if (dataElement.container != null)
                else        // no object specific container oid defined
                {
                    specificContainerOid = this.importContainerId;
                } // no object specific container oid defined
            } // if (containerOid != null)
            else    // a container oid is defined
            {
                specificContainerOid = containerOid;
            } // a container oid is defined

            // check if the workspace user shall be added to the extkey:
            if (dataElement.idAddWspUser)
            {
                // get the extension and add it to the data element id:
                dataElement.extendId (
                    this.getExtKeyWspUserExtension (specificContainerOid));
            } // if

            // get the matching importScriptElement from the importScript
            // we always first look up the typecode and if this does not exist
            // we look up for the typename
            ise = importScript.find (dataElement.typename, dataElement.p_typeCode);

            // check if we found an importscript element
            if (ise == null)
            {
                // no attached importScriptElement found
                // just create the object as if there would be no importscript defined

                // is a specific operation for the dataElement set?
                if (dataElement.operation != null)
                {
                    allOk &= this.processElement (dataElement, specificContainerOid,
                        dataElement.operation);
                } // if
                else
                {
                    allOk &= this.processElement (dataElement, specificContainerOid,
                        importOperation);
                } // else
            } // if (ise == null)
            else                        // importscript entry found
            {
                // check again if there is already a specific container id defined
                // this means the object is a subobject of another object and we
                // have to use the setting in containerOid
                // BB HINT: note that the specificContainerOid has already been set
                // at the beginning of this method
                if (containerOid == null)
                {
                    // now check if any object specific container has not been set
                    // because that would overwrite any importscript settings
                    if (dataElement.containerId == null ||
                        dataElement.containerId.length () == 0)
                    {
                        // check if the container oid is already stored in
                        // the importScriptElement
                        if (ise.containerOid != null)
                        {
                            specificContainerOid = ise.containerOid;
                        } // if (ise.containerOid != null)
                        else    // else no containerOid set in importScriptElement
                        {
                            // if not calculate the container id with the
                            // settings in the importscript
                            specificContainerOid = this
                                .getContainerOidFromType (ise.containerType,
                                    ise.containerId, ise.containerIdDomain,
                                    ise.containerTabName,
                                    this.importContainerId);
                            // and store the value in the importscript element
                            // for later use
                            ise.containerOid = specificContainerOid;
                        } // else no containerOid set in importScriptElement
                    } // if (specificContainerOid != null)
                } // if (containerOid == null)
                // process the object with the operation defined
                // in the import script element:
                allOk &= this.processElement (
                    dataElement, specificContainerOid, ise.operationType);
            } // else importscript entry found
        } // for iter

        // return success status:
        return allOk;
    } // performProcessImportScript


    /**************************************************************************
     * Processes custom operation for imports. <BR/>
     * HINT: to be extended in the future
     *
     * @param dataElement       section of the DataElement to extract
     *                          the data from
     * @param operationName     name of custom operation
     *
     * @return true if custom script could have been found
     */
    protected boolean processCustomOperation (DataElement dataElement,
                                              String operationName)
    {
        // find out which custom action we need to perform
        if (operationName.equalsIgnoreCase (DIConstants.COP_GROUPUSER))
        {
            // create a link between a user and a group
            //
            // HINT: NOT IMPLEMENTED YET
            //
            return true;
        } // if (operationName.equalsIgnoreCase(DIConstants.CUSTOM_USER))

        // custom operation not known
        return false;
    } // processCustomOperation


    /**************************************************************************
     * Deletes all files used for the import. <BR/>
     *
     * @return  <CODE>true</CODE> if import succeeded or
     *          <CODE>false</CODE> otherwise.
     */
    private boolean performDeleteImportFiles ()
    {
        boolean isAllOk = true;
        String [] importedFiles;

        // delete all selected import files:
        for (int i = 0; i < this.p_files.length; i++)
        {
            isAllOk &= this.performDeleteImportFile (this.p_files [i]);
        } // for i

        // get the names of the files imported additionally
        importedFiles = this.objectFactory.getImportedFiles ();

        // check if we got any file names:
        if (importedFiles != null && importedFiles.length > 0)
                                        // files set?
        {
            // loop through the array and delete the files
            for (int i = 0; i < importedFiles.length; i++)
            {
                // delete the file:
                isAllOk &= this.performDeleteImportFile (importedFiles [i]);
            } // for i
        } // if files set

        // return the result:
        return isAllOk;
    } // performDeleteImportFiles


    /**************************************************************************
     * Deletes a file used for the import through the connector. <BR/>
     *
     * @param fileName  the name of the file to be deleted
     *
     * @return true if the file was deleted or false otherwise.
     */
    private boolean performDeleteImportFile (String fileName)
    {
        try
        {
            // delete the import files via the connector
            if (this.connector.deleteFile (fileName))
            {
                this.log.add (DIConstants.LOG_ENTRY,  
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_FILE_DELETED, 
                        new String[] {fileName}, env));
                return true;
            } // if (this.connector.deleteFile (importFileName)

            // importfile could not be deleted
            this.log.add (DIConstants.LOG_WARNING,  
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_COULD_NOT_DELETE_FILE, 
                    new String[] {fileName}, env));
            return false;
        } // try
        catch (ConnectionFailedException e)
        {
            this.log.add (DIConstants.LOG_ERROR, e.getMessage ());
            return false;
        } // catch
    } // performDeleteImportFile


    /**************************************************************************
     * Get an importScenario class instance from a class name. <BR/>
     *
     * @param scenarioClassName     the name of the importScenario class
     *
     * @return an instance of the ImportScenario set or null otherwise
     */
    private ImportScenario getImportScenario (String scenarioClassName)
    {
        String scenarioClassNameLocal = scenarioClassName; // variable for local assignments
        ImportScenario importScenario = null;

        // check if the full package name has been specified:
        // this could possibly change in the future
        if (scenarioClassNameLocal.indexOf ('.') == -1)
        {
            scenarioClassNameLocal = "ibs.di.imp." + scenarioClassNameLocal;
        } // if (scenarioClassNameLocal.indexOf ('.') == -1)

        try
        {
            // try to get the class
            // if that fails the class and the desired import scenario does not exist
            @SuppressWarnings ("unchecked") // suppress compiler warning
            Class<ImportScenario> importScenarioClass =
                (Class<ImportScenario>) Class.forName (scenarioClassNameLocal);
            // create an instance of the desired ImportScenario
            importScenario = importScenarioClass.newInstance ();
            // initialize the instance
            importScenario.initObject (OID.getEmptyOid (), this.user, this.env, this.sess, this.app);
            // return the importscenario
            return importScenario;
        } // try
        catch (ClassNotFoundException e)
        {
            // importScenario does not exist
            return null;
        } // catch
        catch (IllegalAccessException e)
        {
            return null;
        } // catch
        catch (InstantiationException e)
        {
            return null;
        } // catch
    } // getImportScenario


    /**************************************************************************
     * Initialize the importScenario with log and objectfactory. <BR/>
     *
     * @param importScenario    the importScenario to initialize
     */
    private void initImportScenario (ImportScenario importScenario)
    {
        if (importScenario != null)
        {
            // share the log:
            importScenario.setLog (this.log);
            // share the object factory:
            importScenario.setObjectFactory (this.objectFactory);
            // set the response object
            // in order to enable writing multipart messages
            importScenario.setResponse (this.p_response);
        } // if (importScenario != null)
    } // initImportScenario


    /**************************************************************************
     * Shows a forecase how long the import will take to finish. Writes the
     * forecase to the environment. <BR/>
     *
     * @param startDate         starting Date of import
     * @param elementsLength    total amount of elements
     * @param elementsCount     amount of elements already processed
     */
    private void showTimeRemaining (Date startDate, int elementsLength,
                                    int elementsCount)
    {
        int elementsCountLocal = elementsCount; // variable for local assignments
        long timeElapsed;
        long timeRemaining;

        // the time remaining will only be shown when display log is activated
        if (this.log.isDisplayLog)
        {
            // check if there are any elements. if not do not display a forecast
            if (elementsLength > 0)
            {
                // check if the number of objects processed exceeds the
                // number of elements found
                // HINT: this check is done in order to avoid negative time estimations
                if (elementsCountLocal > elementsLength)
                {
                    elementsCountLocal = elementsLength;
                } // if

                Date now = new Date ();
                timeElapsed = ((now.getTime ()) - startDate.getTime ()) / 1000;
                timeRemaining = (timeElapsed / elementsCountLocal) * (elementsLength - elementsCountLocal);

                if (this.isGenerateHtml)
                {
                    this.env.write ("<DIV ALIGN=\"LEFT\"><LI>" + 
                        MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                            DITokens.ML_TIME_REMAINING, env) + ": " + timeRemaining + " " +
                        MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                            DITokens.ML_SECONDS, env) + " (" + 
                        elementsCountLocal + "/" + elementsLength + ")</LI></DIV>");
                } // if
                else
                {
                    this.env.write (MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                        DITokens.ML_TIME_REMAINING, env) + ": " + timeRemaining + " " +
                        MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                            DITokens.ML_SECONDS, env) + 
                        " (" + elementsCountLocal + "/" + elementsLength + ")\r\n");
                } // else
            } // if (elementsLength >= 0)
        } // if (this.log.isDisplayLog)
    } // showTimeRemaining


    /**************************************************************************
     * Try to start a workflow with an object. <BR/>
     * The method checks if the container of the object is an
     * XMLViewerContainer that has a workflowtemplate assiciated and
     * starts the workflow. <BR/>
     *
     * @param obj    the object to start the workflow with
     */
    protected void checkWorkflow (BusinessObject obj)
    {
        WorkflowTemplate_01 template;
        BusinessObject  container;
        XMLViewerContainer_01 xmlViewerContainer;

        // check if an object has been specified
        if (obj == null)
        {
            return;
        } // if (obj == null)

        // get the container object
        container = this.objectFactory.getObject (obj.containerId, this.user,
                                                  this.sess, this.env);
        // check if container is a xmlviewercontainer
        if (container instanceof ibs.di.XMLViewerContainer_01)
        {
/* KR 020125: not necessary because already done before
            try
            {
*/
            // cast the container instance to a XMLViewerContainer instance
            xmlViewerContainer = (XMLViewerContainer_01) container;
            // Note that we do not need to retrieve the xmlViewerContainer because
            // this seems already to be done in the getObject method
            // check if a workflow template ist set
            if ((!xmlViewerContainer.workflowTemplateOid.isEmpty ()) &&
                xmlViewerContainer.workflowTemplateOid != null)
            {
                // first get the workflow template object
                template = (WorkflowTemplate_01) this.objectFactory.getObject (
                    xmlViewerContainer.workflowTemplateOid, this.user,
                    this.sess, this.env);
/* KR 020125: not necessary because already done before
                    // retrieve without rights check!
                    template.retrieve (0);
*/

                // create workflow-service
                WorkflowService wfservice = null;
                try
                {
                    wfservice
                        = new WorkflowService (this.user, this.env,
                                               this.sess, this.app);
                } // try
                catch (ObjectInitializeException e)
                {
                    // workflow could not be started
                    this.log.add (DIConstants.LOG_ERROR, 
                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                            DIMessages.ML_MSG_WORKFLOW_NOT_STARTED,
                            new String[] {xmlViewerContainer.workflowTemplateName}, env));
                    // exit
                    return;
                } // catch

                // start the workflow for given object
                try
                {
                    if (wfservice.start (obj, template))
                    {
                        // add message to log that the workflow
                        // has been started successfully
                        this.log.add (DIConstants.LOG_ENTRY, 
                            MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                                DIMessages.ML_MSG_WORKFLOW_STARTED,
                                new String[] {xmlViewerContainer.workflowTemplateName}, env));
                    } // if
                    else
                    {
                        // add message to log because an error occurred
                        // note that in case the creation of the
                        // workflow was  successful the workflow
                        // will output appropriate messages
                        this.log.add (DIConstants.LOG_ERROR, 
                            MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                                DIMessages.ML_MSG_WORKFLOW_NOT_STARTED,
                                new String[] {xmlViewerContainer.workflowTemplateName}, env));
                    } // else
                } // try
                catch (UserInteractionRequiredException e)
                {
                    // workflow could not be started
                    this.log.add (DIConstants.LOG_ERROR, 
                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                            DIMessages.ML_MSG_WORKFLOW_NOT_STARTED,
                            new String[] {xmlViewerContainer.workflowTemplateName}, env) +
                            " (UserInteractionRequired)");
                } // catch

            } // if (xmlViewerContainer.workflowAllowed &&  ...
/* KR 020125: not necessary because already done before
            } // try
            catch (NoAccessException e)
            {
//showDebug ("NoAccessException");
                // show error message
                this.log.add (DIConstants.LOG_ERROR, WorkflowMessages.MSG_CREATION_FAILED);
            } // catch
            catch (AlreadyDeletedException e)
            {
//showDebug ("AlreadyDeletedException");
                // show error message
                this.log.add (DIConstants.LOG_ERROR, WorkflowMessages.MSG_CREATION_FAILED);
            } // catch
*/
        } // if (container instanceof ibs.di.XMLViewerContainer_01)
    } // checkWorkflow


    /**************************************************************************
     * Print the stack trace. <BR/>
     *
     * @param e   the exception to print the stack trace
     */
    public void printStackTrace (Exception e)
    {
        this.log.add (DIConstants.LOG_ERROR,
                      Helpers.getStackTraceFromThrowable (e));
    } // printStackTrace


    /**************************************************************************
     * Get the interface object as a XML-Viewer object. Take it and set the
     * settings as an data element. <BR/>
     *
     * @param interfaceOid   oid of an importInterface
     */
    public void getInterfaceSettings (OID interfaceOid)
    {
        XMLViewer_01 importInterface;

        // initialize the importInterface instance
        importInterface = new XMLViewer_01 ();
        importInterface.initObject (interfaceOid, this.user,
                                    this.env, this.sess,
                                    this.app);
        try
        {
            // retrieve the data from the importInterface instance
            importInterface.retrieve (Operations.OP_READ);
            // set the import parameter from the importInterface
            this.setInterfaceSettings (importInterface.dataElement);
        } // try
        catch (NoAccessException e)
        {
            // TODO: handle the exception
        } // catch
        catch (AlreadyDeletedException e)
        {
            // TODO: handle the exception
        } // catch
        catch (ObjectNotFoundException e)
        {
            // TODO: handle the exception
        } // catch
    } // getInterfaceSettings


    /**************************************************************************
     * Set the parameters for the import with the data from an import
     * interface. <BR/>
     *
     * @param dataElement   dataElement instance of an importInterface
     */
    public void setInterfaceSettings (DataElement dataElement)
    {
        String translatorStr = "";
        String importScriptStr = "";
        String translatorOidStr;
        String backupConnectorStr;

        // check if we got a data element:
        if (dataElement != null)
        {
            // set settings for interface connector:
            this.setInterfaceSettingsInterfaceConnector (dataElement);

             // set the filter
            if (dataElement.exists (DIConstants.INTERFACE_IMPORT_FILTER))
            {
                this.filterId = dataElement
                    .getImportIntValue (DIConstants.INTERFACE_IMPORT_FILTER);
            } // if (valueDataElement.field.equalsIgnoreCase
                // (DITokens.TOK_FILTER));

            // set translator
            // get the oidstring from the value that looks like (oid),(name)
            if (dataElement.exists (DIConstants.INTERFACE_TRANSLATOR))
            {
                // get the value of the field
                translatorStr = dataElement
                    .getImportStringValue (DIConstants.INTERFACE_TRANSLATOR);
                // get the oid out of the string
                translatorOidStr =
                    dataElement.getSelectionOidValue (translatorStr);

                try
                {
                    // set the translator oid
                    this.translatorOid = new OID (translatorOidStr);
                } // try
                catch (IncorrectOidException e)
                {
                    // reset the value of the translatorOid
                    this.translatorOid = null;
                } // catch (IncorrectOidException e)
            } // if (valueDataElement.field.equalsIgnoreCase (DITokens.TOK_TRANSLATOR));

            // set importscript
            // get the oidstring from the value that looks like (oid),(name)
            if (dataElement.exists (DIConstants.INTERFACE_IMPORTSCRIPT))
            {
                // get the value of the field
                importScriptStr = dataElement
                    .getImportStringValue (DIConstants.INTERFACE_IMPORTSCRIPT);
                // get the oid out of the string
                String importScriptOidStr = dataElement
                    .getSelectionOidValue (importScriptStr);
                try
                {
                    this.importscriptOid  = new OID (importScriptOidStr);
                } // try
                catch (IncorrectOidException e)
                {
                    this.importscriptOid = null;
                } // catch (IncorrectOidException e)
            } // if (dataElement.exists (DIConstants.INTERFACE_IMPORTSCRIPT))
            else                        // no importscript set
            {
                this.importscriptOid = null;
            } // else no importscript set

            // get the file filter:
            if (dataElement.exists (DIConstants.INTERFACE_FILE_FILTER))
            {
                this.p_fileFilter =
                    dataElement.getImportStringValue (DIConstants.INTERFACE_FILE_FILTER);
            } // if (dataElement.exists (DIConstants.INTERFACE_FILE_FILTER))
            // enable workflow setting:
            if (dataElement.exists  (DIConstants.INTERFACE_ENABLE_WORKFLOW))
            {
                this.isEnableWorkflow = dataElement
                    .getImportBooleanValue (DIConstants.INTERFACE_ENABLE_WORKFLOW);
            } // if (dataElement.exists (DIConstants.INTERFACE_ENABLE_WORKFLOW))
            // delete files after import setting:
            if (dataElement.exists (DIConstants.INTERFACE_DELETE_IMPORT))
            {
                this.isDeleteImportFiles =
                    dataElement.getImportBooleanValue (DIConstants.INTERFACE_DELETE_IMPORT);
            } // if (dataElement.exists (DIConstants.INTERFACE_DELETE_IMPORT))
            // objectname type mapping setting:
            if (dataElement.exists (DIConstants.INTERFACE_NAMETYPEMAPPING))
            {
                this.p_isNameTypeMapping =
                    dataElement.getImportBooleanValue (DIConstants.INTERFACE_NAMETYPEMAPPING);
            } // if (dataElement.exists (DIConstants.INTERFACE_NAMETYPEMAPPING))

            // check if the field backup connector exists:
            if (dataElement.exists (DIConstants.INTERFACE_BACKUPCONNECTOR))
                                        // backup connector field exists?
            {
                // get the value of the field:
                backupConnectorStr = dataElement
                    .getImportStringValue (DIConstants.INTERFACE_BACKUPCONNECTOR);
                // get the oid out of the string:
                String backupConnectorOidStr =
                    dataElement.getSelectionOidValue (backupConnectorStr);

                try
                {
                    // set the backup connector oid:
                    this.p_backupConnectorOid = new OID (backupConnectorOidStr);
                    this.p_isCreateBackup = true;
                } // try
                catch (IncorrectOidException e)
                {
                    // reset the value of the connectorOid:
                    this.p_backupConnectorOid = null;
                    this.p_isCreateBackup = false;
                } // catch (IncorrectOidException e)
            } // if backup connector field exists
            else
            {
                // reset the value of the connectorOid:
                this.p_backupConnectorOid = null;
                this.p_isCreateBackup = false;
            } // else

            // check if a log instance is set:
            if (this.log == null)
            {
                this.initLog ();
            } // if

            // set logging settings:
            this.setInterfaceSettingsLogging (dataElement);

            // mark that the interface has been set
            this.isInterfaceUsed = true;
        } // if data element is not null
    } // setInterfaceSettings


    /**************************************************************************
     * Set the logging parameters for the import with the data from an import
     * interface. <BR/>
     *
     * @param dataElement   dataElement instance of an importInterface
     */
    public void setInterfaceSettingsInterfaceConnector (DataElement dataElement)
    {
        String connectorStr = null;

        // check if we got a data element:
        if (dataElement != null)
        {
            if (dataElement.exists (DIConstants.INTERFACE_CONNECTOR))
            {
                // get the value of the field
                connectorStr = dataElement
                    .getImportStringValue (DIConstants.INTERFACE_CONNECTOR);
                // get the oid out of the string
                String connectorOidStr =
                    dataElement.getSelectionOidValue (connectorStr);

                try
                {
                    // set the connector oid:
                    this.connectorOid = new OID (connectorOidStr);
                    this.isUseConnector = true;
                    this.isInterfaceUseConnector = true;
                } // try
                catch (IncorrectOidException e)
                {
                    // reset the value of the connectorOid:
                    this.connectorOid = null;
                    this.isUseConnector = false;
                    this.isInterfaceUseConnector = false;
                } // catch (IncorrectOidException e)
            } // if
            else
            {
                this.connectorOid = null;
                this.isUseConnector = false;
                this.isInterfaceUseConnector = false;
            } // else
        } // if data element is not null
    } // setInterfaceSettingsInterfaceConnector


    /**************************************************************************
     * Set the logging parameters for the import with the data from an import
     * interface. <BR/>
     *
     * @param dataElement   dataElement instance of an importInterface
     */
    public void setInterfaceSettingsLogging (DataElement dataElement)
    {
        // check if we got a data element:
        if (dataElement != null)
        {
            // display log file
            if (dataElement.exists (DIConstants.LOG_DISPLAY))
            {
                this.p_isDisplayLog = dataElement
                    .getImportBooleanValue (DIConstants.LOG_DISPLAY);
                // set settings in the log attributes
                this.log.isDisplayLog = this.p_isDisplayLog;
            } // if (dataElement.exists (DIConstants.LOG_DISPLAY))
            // write log file
            if (dataElement.exists (DIConstants.LOG_SAVE))
            {
                this.p_isSaveLog = dataElement
                    .getImportBooleanValue (DIConstants.LOG_SAVE);
                // set settings in the log attributes
                this.log.isWriteLog = this.p_isSaveLog;
            } // if (dataElement.exists (DIConstants.LOG_SAVE))
            // append log file
            if (dataElement.exists (DIConstants.LOG_APPEND))
            {
                this.p_isAddtoLog = dataElement
                    .getImportBooleanValue (DIConstants.LOG_APPEND);
                // set settings in the log attributes
                this.log.isAppendLog = this.p_isAddtoLog;
            } // if (dataElement.exists (DIConstants.LOG_APPEND))
            // log file name
            if (dataElement.exists (DIConstants.LOG_NAME))
            {
                this.p_logFileName = dataElement
                    .getImportStringValue (DIConstants.LOG_NAME);
                // set settings in the log attributes
                this.log.setFileName (this.p_logFileName);
            } // if (dataElement.exists (DIConstants.LOG_NAME))
            // log file path
            if (dataElement.exists (DIConstants.LOG_PATH))
            {
                this.p_logFilePath = dataElement
                    .getImportStringValue (DIConstants.LOG_PATH);
                // set settings in the log attributes
                this.log.setPath (this.p_logFilePath);
            } // if (dataElement.exists (DIConstants.LOG_PATH))
        } // if data element is not null
    } // setInterfaceSettingsLogging


    /**************************************************************************
     * Takes the setting for the import from the environment and set it to the class
     * properies. <BR/>
     */
    public void getEnvSettings ()
    {
        String str = null;

        // check if connector or upload has been selected as importsource
        str = this.env.getStringParam (DIArguments.ARG_IMPORTSOURCE);
        if (str != null && str.equals (DIConstants.SOURCE_UPLOAD))
        {
            this.isUseConnector = false;
        } // if
        else
        {
            this.isUseConnector = true;
        } // else
        // get the connector oid
        this.connectorOid = this.env.getOidParam (DIArguments.ARG_CONNECTOR);
        // get the importscript
        this.importscriptOid = this.env.getOidParam (DIArguments.ARG_IMPORTSCRIPT);
        // get the translator
        this.translatorOid = this.env.getOidParam (DIArguments.ARG_TRANSLATOR);
/* BB TODO: filters will no longer be supported!
        // get the is delete import files flag
        this.filterId = this.env.getIntParam (DIArguments.ARG_FILTERID);
*/
        // delete import files option:
        this.isDeleteImportFiles = this.env
            .getBoolParam (DIArguments.ARG_DELETEIMPORTFILE) == IOConstants.BOOLPARAM_TRUE;
        // get the enable workflow flag
        this.isEnableWorkflow = this.env
            .getBoolParam (DIArguments.ARG_ENABLEWORKFLOW) == IOConstants.BOOLPARAM_TRUE;

        // name type mapping option:
        this.p_isNameTypeMapping = this.env
            .getBoolParam (DIArguments.ARG_NAMETYPEMAPPING) == IOConstants.BOOLPARAM_TRUE;

        // get parameters for backup:
        // get the backup connector oid string setting:
        this.p_backupConnectorOid = this.env
            .getOidParam (DIArguments.ARG_BACKUPCONNECTOR);

        // remember if a backup shall be created:
        this.p_isCreateBackup = this.p_backupConnectorOid != null;

        // validate structure option:
        this.p_isValidate = this.env
            .getBoolParam (DIArguments.ARG_VALIDATESTRUCTURE) == IOConstants.BOOLPARAM_TRUE;

        // get the log settings:
        if (this.log == null)
        {
            this.initLog ();
        } // if
        this.log.getParameters ();
        this.isInterfaceUsed = false;
        this.interfaceButtonClick = this.env.getStringParam (DIArguments.ARG_SETINTERFACE);
    } // getEnvSettings


    /**************************************************************************
     * Replaces any occurrences of system variables <CODE>#SYSVAR.????#</CODE>
     * in a file by their corresponding values. <BR/>
     *
     * @param   filePath    Path to the file directory.
     * @param   fileName    Name of the file.
     *
     * @return  The name of the new file where all sysvars are replaced by their
     *          corresponding values.
     *          <CODE>null</CODE> if the file could not be generated.
     */
    protected String replaceSysVars (String filePath, String fileName)
    {
        Variables vars = new Variables ();

        try
        {
            // set the system variables:
            vars.addSysVars (this);

            // variable for username of workspaceowner:
            Variable var = new Variable (
                            "#SYSVAR.WORKSPACEUSERNAME#",
                            ActionConstants.VARIABLETYPE_TEXT,
                            "63",
                            "",
                            this.getWspUsername (this.importContainerId));
            vars.changeEntry (var);

/*
            // variable for useroid of workspaceowner:
            var = new Variable (
                            "#SYSVAR.WORKSPACEUSEROID#",
                            ActionConstants.VARIABLETYPE_TEXT,
                            "63",
                            "",
                            this.getWspUserOid (this.importContainerId));
            vars.changeEntry (var);
*/


            // replace system variables in the source file and return the
            // new file name:
            return this.replaceSysVars (filePath, fileName, vars);
        } // try
        catch (ActionException e)
        {
            IOHelpers.showMessage (ImportIntegrator.ERRM_REPLACESYSVARS, e,
                this.app, this.sess, this.env, false);
            return null;
        } // catch
    } // replaceSysVars


    /**************************************************************************
     * Replaces any occurrences of system variables <CODE>#SYSVAR.????#</CODE>
     * in a file by their corresponding values. <BR/>
     *
     * @param   filePath    Path to the file directory.
     * @param   fileName    Name of the file.
     * @param   variables   The variables to be replaced.
     *
     * @return  The name of the new file where all sysvars are replaced by their
     *          corresponding values.
     *          <CODE>null</CODE> if the file could not be generated.
     */
    protected String replaceSysVars (String filePath, String fileName,
                                     Variables variables)
    {
        String outFileName = FileHelpers.getUniqueFileName (filePath, "o_" + fileName);
        String line = null;
        String newLine = null;
        File inputFile = new File (filePath + fileName);
        File outputFile = new File (filePath + outFileName);
        BufferedReader inputBufferedReader = null;
        FileWriter outputFileWriter = null;

        try
        {
            // read the input file:
            inputBufferedReader = new BufferedReader (new FileReader (inputFile));
            // initialize output file:
            outputFileWriter = new FileWriter (outputFile);

            // check if input file exists:
            if (!inputFile.exists ())
            {
                IOHelpers.showMessage (ImportIntegrator.ERRM_REPLACESYSVARS + ": " +
                                       filePath + fileName + " does not exist.",
                                       this.app, this.sess, this.env);
                return null;
            } // if

            try
            {
                // replace system variables in each line of source file and
                // write it to the output file:
                while ((line = inputBufferedReader.readLine ()) != null)
                {
                    newLine = variables.replaceWithValue (line, this.env);
                    newLine += "\n";
                    outputFileWriter.write (newLine, 0, newLine.length ());
                }  // while
            } // try
            catch (ActionException e)
            {
                IOHelpers.showMessage (ImportIntegrator.ERRM_REPLACESYSVARS, e,
                    this.app, this.sess, this.env, false);
                // close the streams:
                inputBufferedReader.close ();
                outputFileWriter.close ();
                // drop the output file:
                outputFile.delete ();
                return null;
            } // catch

            // close the streams:
            inputBufferedReader.close ();
            outputFileWriter.close ();
            // return the output file name:
            return outFileName;
        } // try
        catch (IOException e)
        {
            IOHelpers.showMessage (ImportIntegrator.ERRM_REPLACESYSVARS,
                                   e, this.app, this.sess, this.env, false);
            return null;
        } // catch
    } // replaceSysVars

} // class ImportIntegrator
