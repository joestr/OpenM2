/*
 * Class: ExportIntegrator.java
 */

// package:
package ibs.di.exp;

// imports:
//TODO: unsauber
import ibs.app.AppFunctions;
import ibs.app.CssConstants;
import ibs.bo.BOArguments;
import ibs.bo.BOConstants;
import ibs.bo.BOHelpers;
import ibs.bo.BOMessages;
import ibs.bo.BOTokens;
import ibs.bo.BusinessObject;
import ibs.bo.Container;
import ibs.bo.ContainerElement;
import ibs.bo.Datatypes;
import ibs.bo.IncorrectOidException;
import ibs.bo.OID;
import ibs.bo.ObjectNotFoundException;
import ibs.bo.Operations;
import ibs.bo.States;
import ibs.bo.type.TypeConstants;
import ibs.di.DIArguments;
import ibs.di.DIConstants;
import ibs.di.DIHelpers;
import ibs.di.DIMessages;
import ibs.di.DITokens;
import ibs.di.DataElement;
import ibs.di.FileDataElement;
import ibs.di.Integrator;
import ibs.di.KeyMapper;
import ibs.di.Log_01;
import ibs.di.MultipartElement;
import ibs.di.ObjectFactory;
import ibs.di.Response;
import ibs.di.XMLViewer_01;
import ibs.di.connect.ConnectionFailedException;
import ibs.di.connect.Connector_01;
import ibs.di.trans.TranslationFailedException;
import ibs.di.trans.Translator_01;
import ibs.io.HtmlConstants;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.io.LayoutConstants;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.query.QueryExecutive_01;
import ibs.obj.ref.ReferenzContainerElement_01;
import ibs.obj.ref.Referenz_01;
import ibs.service.user.User;
import ibs.tech.html.BuildException;
import ibs.tech.html.FormElement;
import ibs.tech.html.GroupElement;
import ibs.tech.html.InputElement;
import ibs.tech.html.LineElement;
import ibs.tech.html.NewLineElement;
import ibs.tech.html.Page;
import ibs.tech.html.RowElement;
import ibs.tech.html.StyleSheetElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TableElement;
import ibs.util.AlreadyDeletedException;
import ibs.util.DateTimeHelpers;
import ibs.util.Helpers;
import ibs.util.NoAccessException;
import ibs.util.StringHelpers;
import ibs.util.UtilConstants;
import ibs.util.file.FileHelpers;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Vector;


/******************************************************************************
 * The Integrator handles Imports from XML Datasources into the m2
 * Application and Exports from m2 BusinessObjects to XML Datasources. <BR/>
 * This is part of the data integration server.<BR/>
 *
 * @version     $Id: ExportIntegrator.java,v 1.87 2010/05/11 13:20:36 btatzmann Exp $
 *
 * @author      Buchegger Bernd (BB), 981216
 ******************************************************************************
 */
public class ExportIntegrator extends Integrator
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ExportIntegrator.java,v 1.87 2010/05/11 13:20:36 btatzmann Exp $";


    /**
     * oid of the exportContainer. <BR/>
     */
    private OID p_exportContainerId;

    /**
     * Name of the export file. <BR/>
     * Note that patterns for the file name creation can be used.
     */
    private String p_exportFileName = null;

    /**
     * PrettyPrint the export output. <BR/>
     */
/*
    private boolean p_isPrettyPrint = false;
*/

    /**
     * export containers contents recursively. <BR/>
     */
    private boolean p_isRecursive = false;

    /**
     * flag if also containers should be exported. <BR/>
     */
    private boolean p_isExportContainer = false;

    /**
     * Option if a single export file should be created. <BR/>
     */
    private boolean p_isSingleFile = false;

    /**
     * Option if the object should be deleted after export. <BR/>
     */
    private boolean p_isDelete = false;

    /**
     * Option if the subobjects should be deleted after export. <BR/>
     */
    private boolean p_isDeleteRecursive = false;

    /**
     * Option for hierarchical Export has to be performed or not . <BR/>
     */
    private boolean p_isHierarchicalExport = false;

    /**
     * Option for restoring external ids if applicable. <BR/>
     */
    private boolean p_isRestoreExternalId = false;

    /**
     * Option for solving a query and exporting the result instead of
     * the query. <BR/>
     */
    private boolean p_isResolveQuery = false;

    /**
     * Flag if references should be resolved and if the referenced objecth
     * should be exported. <BR/>
     * Note that this feature can lead to infinite loops in the export.<BR/>
     */
    private boolean p_isResolveReference = false;

    /**
     * Flag if the oid of the reference should be used when resolving
     * a reference.<BR/>
     */
    private boolean p_isUseReferencesOid = false;

    /**
     * Flag if keymapping should be resolved when exporting objects.<BR/>
     */
    private boolean p_isResolveKeyMapping = false;

    /**
     * Counts the exported objects. <BR/>
     */
    private int p_exportedObjects = 0;

    /**
     * Contains the successfully exported objects. <BR/>
     */
    private Vector<BusinessObject> p_exportedObjectsVector =
        new Vector<BusinessObject> ();

    /**
     * Flag to include references in export.<BR/>
     */
    private boolean p_isIncludeReference = true;

    /**
     * The metadata for the Export Integrator <BR/>
     */
    private DataElement p_elementMetadata = null;

    /**
     * Pattern for the keymapping ID.<BR/>
     */
    private String p_idPattern = null;

    /**
     * Pattern for the keymapping DOMAIN.<BR/>
     */
    private String p_domainPattern = null;

    /**
     * Flag to include the result of a queryfield in the export. <BR/>
     */
    protected boolean p_isExportQueryResults = false;

    /**
     * String for replacement: OID. <BR/>
     */
    private static final String REPL_OID = "#OID#";

    /**
     * Name of export form. <BR/>
     */
    private static final String FORM_NAME = "Export Form";

    /**
     * Value which defines that a condition is <CODE>true</CODE>. <BR/>
     */
    private static final String CONDITION_TRUE = "true";

    /**
     * Default file name extension for export file name. <BR/>
     */
    private static final String EXPFILENAME_DEFAULT = ".xml";


    /**************************************************************************
     * Creates an ExportIntegrator Object. <BR/>
     */
    public ExportIntegrator ()
    {
        // call constructor of super class ObjectReference:
        super ();
    } // ExportIntegrator


    /**************************************************************************
     * Creates an ExportIntegrator Object. <BR/>
     *
     * @param oid   oid of the object
     * @param user  user that created the object
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    @Deprecated
    public ExportIntegrator (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);
    } // ExportIntegrator


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        this.log = new Log_01 ();
    } // initClassSpecifics


    /***************************************************************************
     * Read form the User the data used in the Object. <BR/>
     * HINT: deactiveted because the Integrator Object has not yet a
     *       representation in the database. <BR/>
     */
    public void getParameters ()
    {
//        super.getParameters ();
    } // getParameters


    /**************************************************************************
     * Sets Metadata elements. <BR/>
     *
     * @param   dataElement The data element to be set.
     */
    public void setMetadataElement (DataElement dataElement)
    {
        this.p_elementMetadata = dataElement;
    } // setMetadataElement


    /**************************************************************************
     * Returns the Metadata elements. <BR/>
     *
     * @return  The data element.
     */
    public DataElement getMetadataElement ()
    {
        return this.p_elementMetadata;
    } // getMetadataElement


    /**************************************************************************
     * Get the keymapping domain pattern. <BR/>
     *
     * @return the keymapping domain pattern
     */
    public String getDOMAINPattern ()
    {
        return this.p_domainPattern;
    } // getDOMAINPattern


    /**************************************************************************
     * Get the keymapping id pattern. <BR/>
     *
     * @return the keymapping id pattern
     */
    public String getIDPattern ()
    {
        return this.p_idPattern;
    } // getIDPattern


    /**************************************************************************
     * This method gets the isExportQueryResults. <BR/>
     *
     * @return Returns the isExportQueryResults.
     */
    public boolean isExportQueryResults ()
    {
        //get the property value and return the result:
        return this.p_isExportQueryResults;
    } // isExportQueryResults


    /**************************************************************************
     * Set the keymapping domain pattern. <BR/>
     *
     * @param string    the keymapping domain pattern
     */
    public void setDomainPattern (String string)
    {
        this.p_domainPattern = string;
    } // setDOMAINPattern


    /**************************************************************************
     * Set the keymapping id pattern. <BR/>
     *
     * @param string    the keymapping id pattern
     */
    public void setIdPattern (String string)
    {
        this.p_idPattern = string;
    } // setIDPattern


    /**************************************************************************
     * Set the oid of the container the export has been started from.<BR/>
     *
     * @param oid   the oid of the container to be set
     */
    public void setContainerId (OID oid)
    {
        this.p_exportContainerId = oid;
    } // setContainerId


    /**************************************************************************
     * Set the name template for the export file.<BR/>
     *
     * @param exportFileName   the name of the export file template
     */
    public void setExportFileName (String exportFileName)
    {
        this.p_exportFileName = exportFileName;
    } // setExportFileName


    /**************************************************************************
     * Set the isRecursive option.<BR/>
     *
     * @param   isRecursive The value for the option.
     */
    public void setIsRecursive (boolean isRecursive)
    {
        this.p_isRecursive = isRecursive;
    } // setIsRecursive


    /**************************************************************************
     * Set the isExportContainer option.<BR/>
     *
     * @param   isExportContainer   The value for the option.
     */
    public void setIsExportContainer (boolean isExportContainer)
    {
        this.p_isExportContainer = isExportContainer;
    } // setIsExportContainer


    /**************************************************************************
     * Set the isSingleFile option.<BR/>
     *
     * @param   isSingleFile    The value for the option.
     */
    public void setIsSingleFile (boolean isSingleFile)
    {
        this.p_isSingleFile = isSingleFile;
    } // setIsSingleFile


    /**************************************************************************
     * Set the isDelete option.<BR/>
     *
     * @param   isDelete    The value for the option.
     */
    public void setIsDelete (boolean isDelete)
    {
        this.p_isDelete = isDelete;
    } // setIsDelete


    /**************************************************************************
     * Set the isDeleteRecursive option.<BR/>
     *
     * @param   isDeleteRecursive   The value for the option.
     */
    public void setIsDeleteRecursive (boolean isDeleteRecursive)
    {
        this.p_isDeleteRecursive = isDeleteRecursive;
    } // setIsDeleteRecursive


    /**************************************************************************
     * Set the isHierarchicalExport option.<BR/>
     *
     * @param   isHierarchicalExport    The value for the option.
     */
    public void setIsHierarchicalExport (boolean isHierarchicalExport)
    {
        this.p_isHierarchicalExport = isHierarchicalExport;
    } // setIsHierarchicalExport


    /**************************************************************************
     * Set the isRestoreExternalId option.<BR/>
     *
     * @param   isRestoreExternalId The value for the option.
     */
    public void setIsRestoreExternalId (boolean isRestoreExternalId)
    {
        this.p_isRestoreExternalId = isRestoreExternalId;
    } // setIsRestoreExternalId


    /**************************************************************************
     * Set the isResolveQuery option.<BR/>
     *
     * @param   isResolveQuery  The value for the option.
     */
    public void setIsResolveQuery (boolean isResolveQuery)
    {
        this.p_isResolveQuery = isResolveQuery;
    } // setIsResolveQuery


    /**************************************************************************
     * Set the isResolveReference option.<BR/>
     *
     * @param   isResolveReference  The value for the option.
     */
    public void setIsResolveReference (boolean isResolveReference)
    {
        this.p_isResolveReference = isResolveReference;
    } // setIsResolveReference


    /**************************************************************************
     * Set the isUseReferencesOid option.<BR/>
     *
     * @param   isUseReferencesOid  The value for the option.
     */
    public void setIsUseReferencesOid (boolean isUseReferencesOid)
    {
        this.p_isUseReferencesOid = isUseReferencesOid;
    } // setIsUseReferencesOid


    /**************************************************************************
     * Set the isResolveKeyMapping option.<BR/>
     *
     * @param   isResolveKeyMapping The value for the option.
     */
    public void setIsResolveKeyMapping (boolean isResolveKeyMapping)
    {
        this.p_isResolveKeyMapping = isResolveKeyMapping;
    } // setIsResolveKeyMapping


    /**************************************************************************
     * Set the isIncludeReference option.<BR/>
     *
     * @param   isIncludeReference  The value for the option.
     */
    public void setIsIncludeReference (boolean isIncludeReference)
    {
        this.p_isIncludeReference = isIncludeReference;
    } // setIsIncludeReference


    /**************************************************************************
     * This method sets the isExportQueryResults. <BR/>
     *
     * @param isExportQueryResults The isExportQueryResults to set.
     */
    public void setExportQueryResults (boolean isExportQueryResults)
    {
        //set the property value:
        this.p_isExportQueryResults = isExportQueryResults;
    } // setExportQueryResults



    /***************************************************************************
     * Gets the export parameters from the environment.<BR/>
     */
    public void getExportParameters ()
    {
        String str;
        int num;

        // get the connector:
        this.connector = this.getConnectorParam (DIArguments.ARG_CONNECTOR,
            DIArguments.ARG_CONNECTORNAME, DIArguments.ARG_EXPORTPATH);

        // try to set the export file name
        str = this.env.getStringParam (DIArguments.ARG_EXPORTFILE);
        if (str != null && (str.length () > 0))
        {
            this.p_exportFileName = str;
        } // if (str != null && str.length () > 0)

        // try to set the keymapper domain pattern
        str = this.env.getStringParam (DIArguments.ARG_DOMAINPATTERN);
        if (str != null && (str.length () > 0))
        {
            this.setDomainPattern (str);
        } // if (str != null && str.length () > 0)

        // try to set the keymapper id pattern
        str = this.env.getStringParam (DIArguments.ARG_IDPATTERN);
        if (str != null && (str.length () > 0))
        {
            this.setIdPattern (str);
        } // if (str != null && str.length () > 0)

        // get the translator:
        this.translator = this.getTranslatorParam (
            DIArguments.ARG_TRANSLATOR, DIArguments.ARG_TRANSLATORNAME);

        // get the filter id
        this.filterId = this.env.getIntParam (DIArguments.ARG_FILTERID);
        // set the filter object
        this.setFilter (DIHelpers.getExportFilter (this.filterId, env));

        // check if we include container in export flag
        num = this.env.getBoolParam (DIArguments.ARG_EXPORTCONTAINER);
        this.p_isExportContainer = num == IOConstants.BOOLPARAM_TRUE;

        // check pretty print flag
/* deactivated
        num = this.env.getBoolParam (DIArguments.ARG_EXPORTPRETTYPRINT);
        this.p_isPrettyPrint = num == IOConstants.BOOLPARAM_TRUE;
*/

        // check export to single file flag
        num = this.env.getBoolParam (DIArguments.ARG_EXPORTSINGLEFILE);
        this.p_isSingleFile = num == IOConstants.BOOLPARAM_TRUE;

        // check export recursive flag
        num = this.env.getBoolParam (DIArguments.ARG_EXPORTCONTENTRECURSIVE);
        this.p_isRecursive = num == IOConstants.BOOLPARAM_TRUE;

         // check if object delete
        num = this.env.getBoolParam (DIArguments.ARG_ISDELETE);
        this.p_isDelete = num == IOConstants.BOOLPARAM_TRUE;

        // check if subobject delete
        num = this.env.getBoolParam (DIArguments.ARG_ISDELETERECURSIVE);
        this.p_isDeleteRecursive = num == IOConstants.BOOLPARAM_TRUE;

        // check if external IDs should be restored.
        num = this.env.getBoolParam (DIArguments.ARG_ISRESTOREEXTERNALID);
        this.p_isRestoreExternalId = num == IOConstants.BOOLPARAM_TRUE;

        // check if result of a query should be resolved and exported
        num = this.env.getBoolParam (DIArguments.ARG_ISRESOLVEQUERY);
        this.p_isResolveQuery = num == IOConstants.BOOLPARAM_TRUE;

        // check if references should be resolved
        num = this.env.getBoolParam (DIArguments.ARG_ISRESOLVEREFERENCE);
        this.p_isResolveReference = num == IOConstants.BOOLPARAM_TRUE;

        // check if when references should be resolved the oid of the
        // reference should be used
        num = this.env.getBoolParam (DIArguments.ARG_ISUSEREFERENCEOID);
        this.p_isUseReferencesOid = num == IOConstants.BOOLPARAM_TRUE;

        // check if keymapping should be resolved
        num = this.env.getBoolParam (DIArguments.ARG_ISRESOLVEKEYMAPPING);
        this.p_isResolveKeyMapping = num == IOConstants.BOOLPARAM_TRUE;


        // get the parameters for the log
        if (this.log != null)
        {
            this.log.getParameters ();
        } // if
        else
        {
//            showDebug ("log == null!");
        } // else

        // check preserve hierarchicalExport
        num = this.env.getBoolParam (DIArguments.ARG_HIERARCHICALEXPORT);
        this.p_isHierarchicalExport = num == IOConstants.BOOLPARAM_TRUE;
        if (this.p_isHierarchicalExport)
        {
            this.p_isSingleFile = true;
            this.p_isRecursive = true;
            this.p_isExportContainer = true;
        } // if

        // check generate xml response option
        num = this.env.getBoolParam (DIArguments.ARG_XMLRESPONSE);
        this.p_isGenerateXMLResponse = num == IOConstants.BOOLPARAM_TRUE;
        if (this.p_isGenerateXMLResponse)
        {
            // if this option is activated the displayLog must be deactivated
            this.log.isDisplayLog = false;
        } // if (num == IOConstants.BOOLPARAM_TRUE)

        // get parameters for backup:
        // get the backup connector:
        this.p_backupConnector = this.getConnectorParam (
            DIArguments.ARG_BACKUPCONNECTOR,
            DIArguments.ARG_BACKUPCONNECTORNAME, null);

        // remember if a backup shall be created:
        this.p_isCreateBackup = this.p_backupConnector != null;

        // check the include log in xml response option
        num = this.env.getBoolParam (DIArguments.ARG_INCLUDELOG);
        this.p_isIncludeLog = num == IOConstants.BOOLPARAM_TRUE;

        // check the send error notification option and settings:
        num = this.env.getBoolParam (DIArguments.ARG_ERRORNOTIFY);
        this.p_isSendErrorNotification = num == IOConstants.BOOLPARAM_TRUE;
        this.p_errorNotificationSender = this.env.getStringParam (DIArguments.ARG_ERRORNOTIFYSENDER);
        this.p_errorNotificationReceiver = this.env.getStringParam (DIArguments.ARG_ERRORNOTIFYRECEIVER);
        this.p_errorNotificationSubject = this.env.getStringParam (DIArguments.ARG_ERRORNOTIFYSUBJECT);
    } // getExportParameters


    /**************************************************************************
     * Displays the export form.<BR/>
     *
     * @param selectedOIDs  an array with object OIDs to be exported
     */
    public void showExportForm (OID[] selectedOIDs)
    {
        if (true)                       // business object resists on this
                                        // server?
        {
            // show the Search Form
            this.performShowExportForm (selectedOIDs);
        } // if business object resists on this server
        else                            // object resists on another server
        {
            // invoke the object on the other server:
        } // else object resists on another server
    } // showExportForm


    /**************************************************************************
     * Displays the export form. <BR/>
     *
     * @param selectedOIDs  an array with object OIDs to be exported
     */
    protected void performShowExportForm (OID[] selectedOIDs)
    {
        GroupElement gel;
        InputElement button;
        String interfaceOidStr = "";
        String translatorOidStr = "";
        String connectorOidStr = "";
        String backupConnectorOidStr = "";

        // set the m2AbsBasePath
        this.setM2AbsBasePath (this.app.p_system.p_m2AbsBasePath);

        // create page
        Page page = new Page (ExportIntegrator.FORM_NAME, false);

        // stylesheet will be loaded
        StyleSheetElement style = new StyleSheetElement ();
        style.importSS = this.sess.activeLayout.path + this.env.getBrowser () +
            "/" + this.sess.activeLayout.elems[LayoutConstants.SHEETINFO].styleSheet;
        page.head.addElement (style);

        // create Header
        FormElement form = this.createFormHeader (page, this.name, this
            .getNavItems (), null, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_BUTTONEXPORT, env), null, this.icon,
            this.containerName);

        // add hidden elements:
        form.addElement (new InputElement (BOArguments.ARG_OID,
            InputElement.INP_HIDDEN, "" + this.p_exportContainerId));
        form.addElement (new InputElement (BOArguments.ARG_FUNCTION,
            InputElement.INP_HIDDEN, "" + AppFunctions.FCT_LISTEXPORT));
        form.addElement (new InputElement (BOArguments.ARG_CONTAINERID,
            InputElement.INP_HIDDEN, "" + this.containerId));
        // add the selected items as hidden elements
        for (int j = 0; j < selectedOIDs.length; j++)
        {
            form.addElement (new InputElement (BOArguments.ARG_EXPORTOID,
                InputElement.INP_HIDDEN, "" + selectedOIDs [j]));
        } // for (int j = 0; j < selectedOIDs.length ; j++)

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

        // check if interfaceOid is not null
        // if true we take the interface for the settings
        // if false we take the settings from the importform
        this.interfaceOid = this.env.getOidParam (DIArguments.ARG_EXPORTINTERFACE);
        // check if an interface is set
        if (this.interfaceOid != null)
        {
            // get the settings from the interface
            this.getInterfaceSettings (this.interfaceOid);
            // set the interface oid string
            interfaceOidStr = this.interfaceOid.toString ();
        } // if (interfaceOidStr != null)
        else    // no interface set
        {
            interfaceOidStr = "";
        } // else
        // check if an interface has been used
        if (!this.isInterfaceUsed)
        {
            // get the settings from the environment
            this.getEnvSettings ();
        } // if (interfaceOidStr == null)
        gel = new GroupElement ();
        // create the interface selection box
        gel = this.createSelectionBoxFromObjectType (this.getTypeCache ()
            .getTypeId (DIConstants.TC_EXPORTINTERFACE),
            DIArguments.ARG_EXPORTINTERFACE, interfaceOidStr, true);
        // add a line break
        gel.addElement (new NewLineElement ());
        // add the set interface button
        button = new InputElement (DIArguments.ARG_SETINTERFACE,
                                   InputElement.INP_BUTTON,
                                   MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                                       DITokens.ML_SETINTERFACE, env));
        // define the function for the click on the interface button
        button.onClick = HtmlConstants.JREF_SHEETFORMTARGET + "'" +
            HtmlConstants.FRM_SHEET + "';" + HtmlConstants.JREF_SHEETFORM +
            BOArguments.ARG_FUNCTION + HtmlConstants.JREF_VALUEASSIGN +
            AppFunctions.FCT_SHOWEXPORTFORM + ";" +
            HtmlConstants.JREF_SHEETFORMSUBMIT;
        gel.addElement (button);
        this.showFormProperty (table, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_EXPORTINTERFACE, env), gel);
        // display a separator line
        this.showProperty (table, null, null, Datatypes.DT_SEPARATOR, (String) null);

        // export file name pattern:
        this.showFormProperty (table, DIArguments.ARG_EXPORTFILE,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_EXPORTFILE, env), Datatypes.DT_TEXT, this.p_exportFileName);

        // store the interface
        form.addElement (new InputElement (
            DIArguments.ARG_ACTIVEEXPORTINTERFACE, InputElement.INP_HIDDEN,
            interfaceOidStr));

        // check if the connector oid has been set
        connectorOidStr = "";
        if (this.connectorOid != null)
        {
            connectorOidStr = this.connectorOid.toString ();
        } // if
        // create the connector selection box
        if (connectorOidStr.length () == 0)
        {
            // create the connector selection box
            gel = this.createConnectorSelectionBox (DIArguments.ARG_CONNECTOR,
                  "", false, false, true);
        } // if (connectorOidStr.length () == 0)
        else
        {
            // create the connector selection box
            gel = this.createConnectorSelectionBox (DIArguments.ARG_CONNECTOR,
                  connectorOidStr, false, false, true);
        } // else oid string is not empty
        this.showFormProperty (table, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_SELECTCONNECTOR, env), gel);
        // showFormProperty (table, DITokens.TOK_CONNECTOR, gel);

        // check if the translator oid has been set
        translatorOidStr = "";
        if (this.translatorOid != null)
        {
            translatorOidStr = this.translatorOid.toString ();
        } // if
        if (translatorOidStr.length () == 0)
        {
            // create a selection box for translators
            gel = this.createSelectionBoxFromObjectType (
                this.getTranslatorTypeIds (), DIArguments.ARG_TRANSLATOR, "", true);
        } // if (translatorOidStr.length () == 0)
        else
        {
            // create a selection box for translators
            gel = this.createSelectionBoxFromObjectType (this
                .getTranslatorTypeIds (), DIArguments.ARG_TRANSLATOR,
                translatorOidStr, true);
        } // else
        // add the translator selection box to the frame
        this.showFormProperty (table, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_TRANSLATOR, env), gel);

        // create a filter selection box:
/* BB TODO: the filters are not supported anymore!!!
        gel = DIHelpers.createExportFilterSelectionBox
              (DIArguments.ARG_FILTERID, this.filterId, false);
        this.showFormProperty (table, DITokens.TOK_FILTER, gel);
*/

        // Show the note regarding the hierarcial note
        this.showProperty (table, null, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_HINT, env),
            Datatypes.DT_TEXT, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_HIERARCHICALEXPORTNOTE, env));

        //create checkbox for hierarchial export when it's true
        this.showFormProperty (table, DIArguments.ARG_HIERARCHICALEXPORT,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_HIERARCHICALEXPORT, env), 
            Datatypes.DT_BOOL, "" + this.p_isHierarchicalExport);

        // create checkbox for export containers when container export is true
        this.showFormProperty (table, DIArguments.ARG_EXPORTCONTAINER,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_EXPORTCONTAINER, env), 
            Datatypes.DT_BOOL, "" + this.p_isExportContainer);

        // create checkbox for export container content recursivly when true
        this.showFormProperty (table, DIArguments.ARG_EXPORTCONTENTRECURSIVE,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_EXPORTCONTENTRECURSIVE, env), 
            Datatypes.DT_BOOL, "" + this.p_isRecursive);

        // create checkbox for write everything in one exportfile when it is true
        this.showFormProperty (table, DIArguments.ARG_EXPORTSINGLEFILE,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_EXPORTSINGLEFILE, env), 
            Datatypes.DT_BOOL, "" + this.p_isSingleFile);

        // create checkbox exporting the result of a query instead the
        // query object
        this.showFormProperty (table, DIArguments.ARG_ISRESOLVEQUERY,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_ISRESOLVEQUERY, env), 
            Datatypes.DT_BOOL, "" + this.p_isResolveQuery);

        // create checkbox for exporting the referenced object instead of the
        // reference
        this.showFormProperty (table, DIArguments.ARG_ISRESOLVEREFERENCE,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_ISRESOLVEREFERENCE, env),
            Datatypes.DT_BOOL, "" + this.p_isResolveReference);

        // create checkbox for when exporting the referenced object instead
        // of the reference use the oid of the reference
        this.showFormProperty (table, DIArguments.ARG_ISUSEREFERENCEOID,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_ISUSEREFERENCEOID, env),
            Datatypes.DT_BOOL, "" + this.p_isUseReferencesOid);

/*
        // option to generate an xml response
        this.showFormProperty (table, DIArguments.ARG_XMLRESPONSE,
                          "XML Response erzeugen",
                          Datatypes.DT_BOOL,
                          "" + this.p_isGenerateXMLResponse);

        // option to include the log in the xml response
        this.showFormProperty (table, DIArguments.ARG_INCLUDELOG,
                          "Log in die XML Response einfügen",
                          Datatypes.DT_BOOL,
                          "" + this.p_isIncludeLog);
*/
        // KEYMAPPING settings
        // show a separator line
        this.showProperty (table, null, null, Datatypes.DT_SEPARATOR, (String) null);

        // keymapper domain pattern
        this.showFormProperty (table, DIArguments.ARG_DOMAINPATTERN,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_DOMAINPATTERN, env),
            Datatypes.DT_TEXT, this.p_domainPattern);

        // keymapper id pattern
        this.showFormProperty (table, DIArguments.ARG_IDPATTERN,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_IDPATTERN, env), 
            Datatypes.DT_TEXT, this.p_idPattern);

        // create checkbox for resolving the keymapping of the exported
        // object
        this.showFormProperty (table, DIArguments.ARG_ISRESOLVEKEYMAPPING,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_ISRESOLVEKEYMAPPING, env),
            Datatypes.DT_BOOL, "" + this.p_isResolveKeyMapping);

        // create checkbox for restoring external IDs from a response if
        // applicable
        this.showFormProperty (table, DIArguments.ARG_ISRESTOREEXTERNALID,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_ISRESTOREEXTERNALID, env),
            Datatypes.DT_BOOL, "" + this.p_isRestoreExternalId);

        // display a separator line:
        this.showProperty (table, null, null, Datatypes.DT_SEPARATOR, (String) null);

/*
        // ask user if backup shall be created:
        this.showFormProperty (table, DIArguments.ARG_CREATEBACKUP,
            DITokens.TOK_CREATEEXPORTBACKUP, Datatypes.DT_BOOL,
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

        // initialize the log:
        if (this.log == null)
        {
            this.initLog ();
        } // if
        // create the form for log properties:
        this.log.showFormProperties (table);

        form.addElement (table);
        // create footer:
        this.createFormFooter (form, HtmlConstants.JREF_SHEETFORMTARGET + "'" +
            HtmlConstants.FRM_BLANK + "';", HtmlConstants.JREF_SHEETFORMTARGET +
            "'" + HtmlConstants.FRM_SHEET + "';" +
            HtmlConstants.JREF_SHEETFORM + BOArguments.ARG_FUNCTION +
            HtmlConstants.JREF_VALUEASSIGN + AppFunctions.FCT_SHOWOBJECT + ";" +
            HtmlConstants.JREF_SHEETFORM + BOArguments.ARG_OID +
            HtmlConstants.JREF_VALUEASSIGN + "'" + this.p_exportContainerId +
            "'; " + HtmlConstants.JREF_SHEETFORMSUBMIT,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_BUTTON_START_EXPORT, env),
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
    } // performShowExport Form


    /**************************************************************************
     * Export an object. <BR/>
     * The object is found through possible types and a specific object name.
     * <BR/>
     * The result can be used for methods called within workflow actions.
     *
     * @param   isGetSettingsFromEnv    Shall the settings be read from the
     *                                  environment?
     * @param   tVersionIds             Possible tVersionIds for exported obj.
     * @param   objName                 Namr of object to be exported.
     * @param   connectorName           Name of connector for writing the
     *                                  result to.
     * @param   translatorName          Name of translator.
     * @param   exportFileName          Target name of exported file.
     * @param   isDisplayLogStr         Shall the log be displayed in GUI?
     * @param   isWriteLogStr           Shall the log be written to log file?
     * @param   logFileName             File name for logging.
     * @param   logPath                 Path for log file.
     * @param   isExportQueryResults    Shall an exported query be evaluated and
     *                                  the results be exported?
     * @param   isSetConversionDate     Shall the conversion be set?
     *
     * @return  Result vector with holds the result code (1. element)
     *          and the result message (2. element) as strings.
     */
    public Vector<String> exportObject (boolean isGetSettingsFromEnv,
                                int[] tVersionIds, String objName,
                                String connectorName, String translatorName,
                                String exportFileName, String isDisplayLogStr,
                                String isWriteLogStr, String logFileName,
                                String logPath, boolean isExportQueryResults,
                                boolean isSetConversionDate)
    {
        OID objOid = null;

        objOid = this.getOidFromObjectName (tVersionIds, objName);

        if (objOid != null)
        {
            String[] oidStrings = {objOid.toString ()};

            // call common method:
            return this.exportObjects (isGetSettingsFromEnv, oidStrings,
                connectorName, translatorName, exportFileName, isDisplayLogStr,
                isWriteLogStr, logFileName, logPath, isExportQueryResults,
                isSetConversionDate);
        } // if

        Vector<String> result = new Vector<String> ();
        result.addElement ("-1");
        result.addElement ("exportObject: No oid found for name \"" +
                           objName + "\"!");

        return result;
    } // exportObject


    /**************************************************************************
     * Perform export of objects. <BR/>
     * The result can be used for methods called within workflow actions.
     *
     * @param   isGetSettingsFromEnv    Shall the settings be read from the
     *                                  environment?
     * @param   oidStrings              The oids of the objects to be exported.
     * @param   connectorName           Name of connector for writing the
     *                                  result to.
     * @param   translatorName          Name of translator.
     * @param   exportFileName          Target name of exported file.
     * @param   isDisplayLogStr         Shall the log be displayed in GUI?
     * @param   isWriteLogStr           Shall the log be written to log file?
     * @param   logFileName             File name for logging.
     * @param   logPath                 Path for log file.
     * @param   isExportQueryResults    Shall an exported query be evaluated and
     *                                  the results be exported?
     * @param   isSetConversionDate     Shall the conversion be set?
     *
     * @return  Result vector with holds the result code (1. element)
     *          and the result message (2. element) as strings.
     */
    public Vector<String> exportObjects (boolean isGetSettingsFromEnv,
                                 String[] oidStrings, String connectorName,
                                 String translatorName, String exportFileName,
                                 String isDisplayLogStr, String isWriteLogStr,
                                 String logFileName, String logPath,
                                 boolean isExportQueryResults,
                                 boolean isSetConversionDate)
    {
        String exportFileNameLocal = exportFileName; // variable for local assignments
        OID connectorOid = null;
        OID translatorOid = null;
        Connector_01 connector = null;
        Translator_01 translator = null;
        Vector<String> result = new Vector<String> ();
        String resultCode = "0";
        String resultMsg = "";
        String str = "";
/*
        Vector exportedObjects = null;
*/

        try
        {
/*
            CommonHelpers integrationHelpers = new CommonHelpers ();
            integrationHelpers.initObject (OID.getEmptyOid (), this.user,
                    this.env, this.sess, this.app);
*/

            // general export settings
            this.isShowSettings = false;
            this.p_isDisplayLog = true;
            this.setIsSingleFile (true);
            this.setExportQueryResults (isExportQueryResults);

            // should be export parameters be read from the internal call parameters?
            if (!isGetSettingsFromEnv)
            {
                // tell the exportintegrator not to read settings from environment
                this.isGetSettingsFromEnv = false;

                // check if an export filename has been set:
                if (exportFileNameLocal != null && (exportFileNameLocal.length () > 0))
                {
                    // try to replace the OID template by the oid of the
                    // procedure object. This needs to be done because the oid
                    // in the file name has been requested but the export
                    // integrator cannot replace it by itself due to
                    // multiple object export
                    exportFileNameLocal = StringHelpers.replace (
                        exportFileNameLocal, ExportIntegrator.REPL_OID,
                        this.oid.toString ());
                } // if (exportFileName != null && (exportFileName.length () >
                    // 0))

                 // create the connector
                connectorOid = this.getOidFromObjectName (
                        this.getConnectorTypeIds (), connectorName);
                if (connectorOid != null)
                {
                    connector = this.createConnectorFromOid (connectorOid);
                } // if
                // could the translator be created?
                if (connector != null)
                {
                    // set the connector
                    this.setConnector (connector);
                } // if (connector != null)
                else    // could not create connector
                {
                    result.addElement ("-1");
                    result.addElement ("Konnektor konnte nicht angelegt werden!");
                    return result;
                } // could not create connector

                // create the translator
                translatorOid = this.getOidFromObjectName (
                       this.getTranslatorTypeIds (), translatorName);
                if (translatorOid != null)
                {
                    translator = this.createTranslatorFromOid (translatorOid);
                } // if
                // could the translator be created?
                if (translator != null)
                {
                    // set the translator
                    this.setTranslator (translator);
                } // if (translator != null)
                else    // could not create translator
                {
                    result.addElement ("-1");
                    result.addElement ("Translator konnte nicht angelegt werden!");
                    return result;
                } // if (translator == null)

                // set the export file name
                if (exportFileNameLocal != null &&
                    !exportFileNameLocal.equalsIgnoreCase ("UNDEFINED"))
                {
                    this.setExportFileName (exportFileNameLocal);
                } // if
                else
                {
                    this.setExportFileName (this.name +
                        ExportIntegrator.EXPFILENAME_DEFAULT);
                } // else

                // set the isDisplayLog option
                this.p_isDisplayLog = isDisplayLogStr
                    .equalsIgnoreCase (ExportIntegrator.CONDITION_TRUE);
                // set the isWriteLog option
                this.p_isSaveLog = isWriteLogStr
                    .equalsIgnoreCase (ExportIntegrator.CONDITION_TRUE);
                // should the log be written
                if (this.p_isSaveLog)
                {
                    // set the log filename and path:
                    this.p_logFileName = logFileName;
                    this.p_logFilePath = logPath;
                } // if (exportIntegrator.p_isSaveLog)
                // ensure to write a single export file
                this.setIsSingleFile (true);
            } // if (param.size () > 0)
            else    // read the export parameters from the environment
            {
                // Note that the isWriteSingleFile parameter should be set to true
                // in order to create a single export file. default is false!
                this.isGetSettingsFromEnv = true;
                // try to get the exportfilename parameter
                // note that this parameter should not have the same name as
                // the ibs.di.DIArguments.ARG_EXPORTFILE because
                // the exportintegrator will try to read this parameter
                // this must be done this way because of the #OID# and #PON#
                // placeholders!
                str = this.env.getParam (DIArguments.ARG_EXPORTFILENAME);
                if (str != null && str.length () > 0)
                {
                    // try to replace the OID template by the oid of the
                    // procedure object. This needs to be done because the oid
                    // in the file name has been requested but the export
                    // integrator cannot replace it by itself due to
                    // multiple object export
                    exportFileNameLocal = StringHelpers.replace (str,
                        ExportIntegrator.REPL_OID, this.oid.toString ());
                } // if (this.env.getParam (ARG_EXPORTFILENAME) != null)
                else // no export file name set
                {
                    // set a default name in case no exportfilename has been set
                    exportFileNameLocal = this.name +
                        ExportIntegrator.EXPFILENAME_DEFAULT;
                } // else no export file name set
                // set the new export file name in the export integrator
                this.setExportFileName (exportFileNameLocal);
            } // read the export parameters from the exvironment

            this.env.write ("<DIV ALIGN=\"LEFT\">");
/*
            this.env.write ("<FONT SIZE=\"3\">"
                + DIMessages.MSG_TRANSMISSION_STARTED + "</FONT><P>");
            // first check the data of the object
            this.env.write ("<FONT SIZE=\"2\">");
//            formObject = checkData (new OID (oidStrings[0]));
            this.env.write ("</FONT>");
*/

            // start the export:
/*
            exportedObjects =
*/
            this.startExport (oidStrings);

            // check if export was successful
            if (this.getIsSuccessful ())
            {
                // nothing to do
                // maybe there can be a loop over all objects
            } // if (exportIntegrator.getIsSuccessful ())
            else                        // export was not successful
            {
                // set the error message that is stored in the log of the
                // exportintegrator
                resultCode = "-1";
                resultMsg =  
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_ERROR, env) +
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_COULD_NOT_TRANSMIT_DATA, env)
                    + ": " + this.getLog ().errorsToString ();
            } // else export was not successful

            this.env.write ("</DIV><P>");
        } // try
        catch (Throwable e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            resultMsg = Helpers.getStackTraceFromThrowable (e);
            resultCode   = "-1";
        } // catch (Throwable e)

        // in case of an error show the error message in a popup
        if (!resultCode.equals ("0"))
        {
            this.showPopupMessage (resultMsg);
        } // if

        // set the result vector
        result.addElement (resultCode);
        result.addElement (resultMsg);
        return result;
    } // exportObjects


    /**************************************************************************
     * Performs the export with the name an export interface. <BR/>
     * The settings of the interfaces will be used.
     *
     * @param    selectedOidStrings      StringArray of the object to export
     * @param    interfaceName           Name of the interface to use
     */
    public void startExport (String [] selectedOidStrings,
                             String interfaceName)
    {
        // initialize the exportInterface instance
        // create the exportinterface
        OID exportInterfaceOid = this.getExportInterfaceOid (interfaceName);
        this.startExport (selectedOidStrings, exportInterfaceOid);
    } // startExport


    /**************************************************************************
     * Performs the export with an Oid of an export interface. <BR/>
     * The settings from the interface are used. Also the setting
     * from the export log. <BR/>
     *
     * @param    selectedOidStrings      String Array of the objects to export
     * @param    exportInterfaceOid      Oid of the interface to use
     */
    public void startExport (String [] selectedOidStrings,
                             OID exportInterfaceOid)
    {
        // initialize the exportInterface instance
        XMLViewer_01 exportInterface = new XMLViewer_01 ();
        // create the exportinterface
        exportInterface.initObject (exportInterfaceOid, this.user,
                                    this.env, this.sess, this.app);
        try
        {
            // retrieve the data of the exportInterface instance
            exportInterface.retrieve (Operations.OP_READ);
            // set the export parameter from the exportInterface
            this.getExportInterfaceParameter (exportInterface.dataElement);
            // mark that the parameters have already been read
            this.isGetSettingsFromEnv = false;
            // now start the export
            this.startExport (selectedOidStrings);
        } // try
        catch (NoAccessException e)
        {
            this.log.add (DIConstants.LOG_ERROR, e.getMessage ());
        } // catch
        catch (AlreadyDeletedException e)
        {
            this.log.add (DIConstants.LOG_ERROR, e.getMessage ());
        } // catch
        catch (ObjectNotFoundException e)
        {
            this.log.add (DIConstants.LOG_ERROR, e.getMessage ());
        } // catch
    } // startExport


    /**************************************************************************
     * Starts the export. <BR/>
     *
     * @param selectedOidStrings    an array with oidStrings to be exported
     *                              this are the objects that are used
     *
     * @return  A vector containing the objects which where successfully
     *          exported. <CODE>null</CODE> if there occurred an error.
     */
    public Vector<BusinessObject> startExport (String[] selectedOidStrings)
    {
        Vector<BusinessObject> retVal = null;

        // set the m2AbsBasePath
        this.setM2AbsBasePath (this.app.p_system.p_m2AbsBasePath);

        // get a log object
        if (this.log == null)
        {
            this.initLog ();
        } // if (this.log == null)
        // should we read the settings from the environment variables
        if (this.isGetSettingsFromEnv)
        {
            // set the export parameters
            this.getExportParameters ();
        } // if (this.isGetSettingsFromEnv)
        // init the log and check if the initialization was successful
        if (!this.log.initLog ())
        {
            this.printError ( 
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_COULDNOTWRITELOGFILE, env));
        } // if (! this.log.initLog ())
        // should the export settings be displayed?
        if (this.isGenerateHtml && this.isShowSettings && (!this.p_isGenerateXMLResponse))
        {
            // show the export results
            this.performShowExportSettings ();
        } // if (this.isGenerateHtml && this.isShowSettings && (! this.p_isGenerateXMLResponse))
        // show a message that the import has been started
        // showMessage (DIMessages.MSG_EXPORTSTARTED);
        // we need to put some HTML commands at the beginning for layout purposes
        if (this.log.isDisplayLog && this.isGenerateHtml && (!this.p_isGenerateXMLResponse))
        {
            this.env.write ("<HTML><HEAD></HEAD><BODY><DIV ALIGN=\"LEFT\"><FONT SIZE=\"2\">");
        } // if (this.log.isDisplayLog && this.isGenerateHtml && (! this.p_isGenerateXMLResponse))
        // write the start time of the import process into the log
        Date startDate = new Date ();
        this.log.processStartDate = startDate;
        this.log.add (DIConstants.LOG_ENTRY, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_EXPORT_STARTED_AT, env) + " " +
            DateTimeHelpers.dateTimeToString (startDate));

        // initialize the export:
        if (this.performInitExport ())
        {
            // check if metadata should be exported
            if (this.p_elementMetadata != null)
            {
                this.filter.setMetadataElement (this.p_elementMetadata);
            } // if (this.p_elementMetadata != null)

            // perform the export of the selected objects
            retVal = this.performExport (selectedOidStrings);

            // close the connector:
            // this needs to be done in order to remove temporary folders
            this.connector.close ();

            // close the filter:
            // this needs to be done to remove the XML DOM tree
            // and avoid memory leaks
            this.filter.close ();

            // close the backup connector:
            // this needs to be done in order to remove temporary folders
            if (this.p_backupConnector != null)
            {
                this.p_backupConnector.close ();
            } // if
        } // if (performInitExport ())
        else                            // export could not be initialized
        {
            this.log.add (DIConstants.LOG_ERROR, 
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_COULD_NOT_INIT_EXPORT, env));
            this.p_isSuccessful = false;
        } // else import could not be initialized
        // write the start time of the import process into the log
        Date endDate = new Date ();
        this.log.processEndDate = endDate;
        this.log.add (DIConstants.LOG_ENTRY, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_EXPORT_FINISHED_AT, env) + " " +
            DateTimeHelpers.dateTimeToString (endDate));
        // calculate the total length of the import in seconds
        long diff = (endDate.getTime () - startDate.getTime ()) / 1000;
        this.log.add (DIConstants.LOG_ENTRY, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_EXPORT_DURATION, env) + ": " + diff + " " +
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_SECONDS, env));
        // we need to put some HTML commands at the end for layout purposes
        if (this.log.isDisplayLog && this.isGenerateHtml && (!this.p_isGenerateXMLResponse))
        {
            this.env.write ("</FONT></DIV><HR></BODY></HTML>");
        } // if (this.log.isDisplayLog && this.isGenerateHtml && (! this.p_isGenerateXMLResponse))
        // now close the log and check if it was successful
        if (!this.log.closeLog ())
        {
            this.printError ( 
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_COULDNOTWRITELOGFILE, env));
        } // if (! this.log.closeLog ())

        // generate an xml response if applicable
        this.generateXMLResponse ("Export");

        // send an error notification if applicable
        this.sendErrorNotification ( 
            MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                DIMessages.ML_MSG_EXPORTFAILED, env), "");

        // return the result:
        return retVal;
    } // startExport


    /**************************************************************************
     * Displays the export settings and the export log. <BR/>
     */
    public void performShowExportSettings ()
    {
        // create page
        Page page = new Page (ExportIntegrator.FORM_NAME, false);

        // load Stylesheet file
        StyleSheetElement style = new StyleSheetElement ();
        style.importSS = this.sess.activeLayout.path + this.env.getBrowser () +
            "/" + this.sess.activeLayout.elems[LayoutConstants.SHEETINFO].styleSheet;
        page.head.addElement (style);
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
        // show the header
        page.body.addElement (this.createHeader (page, this.name, this.getNavItems (),
                null, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_BUTTONEXPORT, env),
                this.icon, this.containerName, false, -1));
        // show the preferences we worked with
        // template for export file
        if (this.p_exportFileName != null && (this.p_exportFileName.length () > 0))
        {
            this.showProperty (table, DIArguments.ARG_EXPORTFILE,
                MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                    DITokens.ML_EXPORTFILE, env),
                Datatypes.DT_TEXT, this.p_exportFileName);
        } // if (this.p_exportFileName != null && (this.p_exportFileName.length () > 0))

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

        // check the translator
        if (this.translator != null)
        {
            this.translator.setProperties (this.properties);
            this.translator.showSettings (table);
            this.properties = this.translator.getProperties ();
        } // if (this.translator != null)
        else
        {
            this.showProperty (table, DIArguments.ARG_TRANSLATOR,
                MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                    DITokens.ML_TRANSLATOR, env),
                Datatypes.DT_TEXT,  
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_NO_TRANSLATOR_SET, env));
        } // else

/* BB TODO: filters will no longer be supported
        // show the filter settings and check if the filterId is valid
        if (this.filterId >= 0 && this.filterId < DIConstants.EXPORTFILTER_NAMES.length)
            this.showProperty (table,DIArguments.ARG_FILTERID, DITokens.TOK_FILTER,
                         Datatypes.DT_TEXT, DIConstants.EXPORTFILTER_NAMES [this.filterId]);
        else
            this.showProperty (table,DIArguments.ARG_FILTERID, DITokens.TOK_FILTER,
                         Datatypes.DT_TEXT, "");
*/
        // show if export is hierarchical:
        this.showProperty (table, DIArguments.ARG_HIERARCHICALEXPORT,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_HIERARCHICALEXPORT, env),
            Datatypes.DT_BOOL, "" + this.p_isHierarchicalExport);
        // include container objects in export
        this.showProperty (table, DIArguments.ARG_EXPORTCONTAINER,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_EXPORTCONTAINER, env),
            Datatypes.DT_BOOL, "" + this.p_isExportContainer);
        // show if export was recursively
        this.showProperty (table, DIArguments.ARG_EXPORTCONTENTRECURSIVE,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_EXPORTCONTENTRECURSIVE, env),
            Datatypes.DT_BOOL, "" + this.p_isRecursive);
        // write everything in one file
        this.showProperty (table, DIArguments.ARG_EXPORTSINGLEFILE,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_EXPORTSINGLEFILE, env),
            Datatypes.DT_BOOL, "" + this.p_isSingleFile);
        // resolve query result
        this.showProperty (table, DIArguments.ARG_ISRESOLVEQUERY,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_ISRESOLVEQUERY, env), 
            Datatypes.DT_BOOL, "" + this.p_isResolveQuery);
        // resolve reference
        this.showProperty (table, DIArguments.ARG_ISRESOLVEREFERENCE,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_ISRESOLVEREFERENCE, env),
            Datatypes.DT_BOOL, "" + this.p_isResolveReference);
        // use reference oid when resolve reference
        this.showProperty (table, DIArguments.ARG_ISUSEREFERENCEOID,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_ISUSEREFERENCEOID, env), 
            Datatypes.DT_BOOL, "" + this.p_isUseReferencesOid);

        // KEYMAPPING settings
        // keymapper domain pattern
        this.showProperty (table, DIArguments.ARG_DOMAINPATTERN,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_DOMAINPATTERN, env),
            Datatypes.DT_TEXT, this.p_domainPattern);
        // keymapper id pattern
        this.showProperty (table, DIArguments.ARG_IDPATTERN,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_IDPATTERN, env),
            Datatypes.DT_TEXT, this.p_idPattern);
        // resolving the keymapping
        this.showProperty (table, DIArguments.ARG_ISRESOLVEKEYMAPPING,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_ISRESOLVEKEYMAPPING, env),
            Datatypes.DT_BOOL, "" + this.p_isResolveKeyMapping);
        // restoring external IDs from a response
        this.showProperty (table, DIArguments.ARG_ISRESTOREEXTERNALID,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_ISRESTOREEXTERNALID, env),
            Datatypes.DT_BOOL, "" + this.p_isRestoreExternalId);

        // display the create backup settings:
        this.showProperty (table, DIArguments.ARG_CREATEBACKUP,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_CREATEEXPORTBACKUP, env),
            Datatypes.DT_BOOL, "" + this.p_isCreateBackup);
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
        RowElement tr = new RowElement (1);
        GroupElement group = new GroupElement ();
        TableDataElement td = new TableDataElement (group);
        td.colspan = 2;
        tr.addElement (td);
        table.addElement (tr);
        td.alignment = IOConstants.ALIGN_MIDDLE;
        FormElement buttonForm = new FormElement ("", "");
        LineElement line = new LineElement ();
        line.width = "90%";
        buttonForm.addElement (line);
        InputElement button = new InputElement ("", InputElement.INP_BUTTON,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_CLOSE, env));
        button.onClick = IOConstants.URL_JAVASCRIPT + "top.window.close ();";
        buttonForm.addElement (button);
        line = new LineElement ();
        line.width = "75%";
        buttonForm.addElement (line);
        group.addElement (buttonForm);

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
    } // performShowExportSettings


    /**************************************************************************
     * Initializes the export. <BR/>
     *
     * @return true if initialization succeeded or false otherwise
     */
    public boolean performInitExport ()
    {
        try
        {
            // check if a connector is present:
            if (this.connector == null)
            {
                // write corresponding message to log and terminate method:
                this.log.add (DIConstants.LOG_ERROR,  
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_NO_CONNECTOR_SET, env));
                return false;
            } // if

            // connector is present
            // set the m2AbsBasePath
            this.connector.setm2AbsBasePath (this.app.p_system.p_m2AbsBasePath);
            // initialize the connector:
            this.connector.initConnector ();

            // initialize the filter:
            this.initFilter ();
            // set the export query results option in the filter
            this.filter.setExportQueryResults (this.isExportQueryResults ());
            // set the metadata elements of the filter
            this.filter.setMetadataElement (this.p_elementMetadata);

            // check if a translator has been set:
            // in that case we have to set the option to
            // delete the original files after export
            if (this.translator != null)
            {
                // set the translator options:
                this.translator.setIsDeleteOriginal (true);
                this.translator.setIsPreserveFileName (true);
/*
                // if an export filename has been set preserve the name
                // for the translated file:
                if (this.p_exportFileName != null)
                    this.translator.setIsPreserveFileName (true);
*/
            } // if (this.translator != null)

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
                        .initBackupConnector (this.p_timestamp + "_export");
                } // try
                catch (ConnectionFailedException e1)
                {
                    this.log.add (DIConstants.LOG_ERROR,
                        "error when initializing backup connector: " +
                        e1.getMessage ());
                    return false;
                } // catch
            } // if backup connector is present

            // return true to indicate that export initialization was ok:
            return true;
        } // try
        catch (ConnectionFailedException e)
        {
            this.log.add (DIConstants.LOG_ERROR, e.getMessage ());
            return false;
        } // catch
    } // performInitExport


    /**************************************************************************
     * Performs an export of a list of objects.<BR/>
     *
     * @param selectedOidStrings    an array with oidStrings to be exported
     *
     * @return  A vector containing the objects which where successfully
     *          exported. <CODE>null</CODE> if there occurred an error.
     */
    public Vector<BusinessObject> performExport (String[] selectedOidStrings)
    {
        this.p_exportedObjects = 0;
        this.p_exportedObjectsVector = new Vector<BusinessObject> ();
        this.performExportInternal (selectedOidStrings, false);
        return this.p_exportedObjectsVector;
    } // performExport


    /**************************************************************************
     * Performs an export of a list of objects.<BR/>
     *
     * @param   selectedOidStrings      an array with oidStrings to be exported
     * @param   isRecursiveExport       indicates the method is called recursive
     */
    public void performExportInternal (String[] selectedOidStrings,
                                         boolean isRecursiveExport)
    {
        boolean isExportOk = false;
        BusinessObject obj = null;

        // create an objectFactory in order to create the object
        ObjectFactory objectFactory = this.getObjectFactory ();
        // loop through the selectedOIDs Array
        for (int i = 0; i < selectedOidStrings.length; i++)
        {
            // no export was already started
            isExportOk = false;
            // try to resolve the oid of the object we want to export
            OID exportOid = this.resolveOid (selectedOidStrings[i]);
            // check if we got an incorrect oid
            if (exportOid == null)
            {
                // add to log the message object XXX was not found
                this.log.add (DIConstants.LOG_ERROR, 
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_OBJECTNOTFOUND,
                        new String[] {selectedOidStrings[i]}, env));
            } // if (exportOid == null)
            else    // we are sure to have an oid and now check if it is valid
            {
                // got a valid oid
                // create a java instance of the object we want to export
                // note this can be a reference to an object
                // so we need to keep a copy in order to delete
                // the original object later
                BusinessObject origObj = objectFactory.getObject (exportOid,
                    this.user, this.sess, this.env);
                // check if the object is a reference and should be ignored
                // this is the case when we are not at top level and the
                // isResolveReference flag is deactivated
                if (!(isRecursiveExport && this.p_isIncludeReference && this
                    .isReference (origObj)))
                {
                    // try to resolve the object as reference
                    obj = this.resolveReference (origObj);
                    // check if the object could be created
                    if (obj != null)
                    {
                        // export the object and all subobjects
                        isExportOk = this.performObjectTreeExport (obj,
                            this.connector.getPath (), !isRecursiveExport);
                    } // if (obj != null)
                    else                    // got the object
                    {
                        // add the message oid not found to log
                        this.log.add (DIConstants.LOG_ERROR, 
                            MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                                BOMessages.ML_MSG_OBJECTOIDNOTFOUND,
                                new String[] {exportOid.toString ()}, env));
                    } // else if (obj != null)
                    // check if the export object should be deleted
                    // we only allow deleting in case the export was
                    // successful. Note that we assume that a container export
                    // is always successful
                    if (this.p_isDelete)
                    {
                        // check if we have the right to delete the object
                        if (isExportOk && obj.checkObjectRights (origObj.oid, this.user.id, Operations.OP_DELETE))
                        {
                            // delete the object
                            origObj.delete (Operations.OP_DELETE);
                            this.log.add (DIConstants.LOG_ENTRY, origObj.oid, origObj.typeName  + " '" +
                                obj.name  + "' " + 
                                MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                                    DITokens.ML_DELETEDAFTEREXPORT, env) + ".");
                        } // if (isExportOk && obj.checkObjectRights (origObj.oid, this.user.id, Operations.OP_DELETE))
                        else    // export was not ok or no permission
                        {
                            this.log.add (DIConstants.LOG_ENTRY, origObj.oid, origObj.typeName  + " '" +
                                obj.name  + "' " + 
                                MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                                    DITokens.ML_NOT_DELETED, env) + ".");
                        } // else export was not ok or no permission
                    } // if (this.p_isDelete)
                } // if (! (isRecursiveExport && this.p_isResolveReferences && isReference (origObj)))
                else
                {
                    // object ignored, nothing to do
                } // else
            } // else got a valid oid
        } // for (int i = 0; i < selectedOIDs.length; i++)

        // now write the export document (DOM tree)
        // this is not necessary for recursive calls!!
        if (!isRecursiveExport)
        {
            // check if we had to export into a single file
            // in that case write the export document now
            if (this.p_isSingleFile)
            {
                // set the export file name
                String path = this.connector.getPath ();
                String fileName = this.generateFileName ();
                // ensure that the filename is unique
                fileName = FileHelpers.getUniqueFileName (path, fileName);
                this.filter.setFileName (fileName);
                this.filter.setPath (path);
                // now write the file to the export destination
                if (this.writeExportData ())
                {
                    // add the number of exported object to the log entry
                    this.log.add (DIConstants.LOG_ENTRY, "" + this.p_exportedObjects +
                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                            DIMessages.ML_MSG_OBJECTSEXPORTED, env));
                    // mark that export was successful
                    this.p_isSuccessful = true;
                } // if (writeExportData ())
                else        // object export not ok
                {
                    this.log.add (DIConstants.LOG_ERROR,  
                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                            DIMessages.ML_MSG_EXPORTFAILED, env));
                } // else object export not ok
            } // if (this.p_isSingleFile)
            else    // export files have already been written
            {
                // add the number of exported object to the log entry
                this.log.add (DIConstants.LOG_ENTRY, "" + this.p_exportedObjects +
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_OBJECTSEXPORTED, env));
                // mark that export was successful
                this.p_isSuccessful = true;
            } // export files have already been written
        } // if (!isRecursiveExport)
    } // performExport


    /**************************************************************************
     * Performs the export of the object and if needed all subobjects.<BR/>
     *
     * @param   obj             the object to export
     * @param   destination     destination path or url and filename
     * @param   isResolveLinks  Shall the links be resolved, too?
     *
     * @return  <CODE>true</CODE> if object could be exported and
     *          <CODE>false</CODE> if not
     */
    private boolean performObjectTreeExport (BusinessObject obj,
        String destination, boolean isResolveLinks)
    {
        boolean isExportOk = false;

        // check if its hierarchical export
        if (this.p_isHierarchicalExport)
        {
            // check if we have to resolve a query
            if (this.p_isResolveQuery && obj instanceof QueryExecutive_01)
            {
                // export the content of the query but not the query itself
                this.p_exportedObjects += this.performContainerExport (
                    (Container) obj, destination, this.p_isRecursive,
                    this.p_isDeleteRecursive);
                isExportOk = true;
            } // if (this.p_isResolveQuery && obj instanceof QueryExecutive_01)
            // check if the object is a container
            else if (obj instanceof Container)
            {
                // set the insertion point for hierarchical structures
                //this.filter.setInsertionPoint (false);
                // check if we can export the object
                if (this.performObjectExport (obj, destination))
                {
                    // increase the counter of successfully
                    // exported objects
                    this.p_exportedObjects++;
                    isExportOk = true;
                    // export the content of the container
                    this.p_exportedObjects += this.performContainerExport (
                        (Container) obj, destination, this.p_isRecursive,
                        this.p_isDeleteRecursive);
                } // if (performObjectExport (obj, destination))
                else    // object could not be exported
                {
                    this.log.add (DIConstants.LOG_ERROR, 
                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                            DIMessages.ML_MSG_COULD_NOT_EXPORT_OBJECT,
                            new String[] {obj.name}, env));
                } // else object export not ok
                // revert the insertion point to the parent
                //this.filter.revertInsertionPoint ();
            } // if (obj instanceof Container)
            else    // export of a standard object
            {
                if (this.performObjectExport (obj, destination))
                {
                    // increase the counter of successfully
                    // exported objects
                    this.p_exportedObjects++;
                    isExportOk = true;
                } // if (performObjectExport (obj, destination))
                else    // object could not be exported
                {
                    this.log.add (DIConstants.LOG_ERROR, 
                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                            DIMessages.ML_MSG_COULD_NOT_EXPORT_OBJECT,
                            new String[] {obj.name}, env));
                } // else object could not be exported
            } // else export of a standard object
        } // if (this.p_isHierarchicalExport)
        else    // hierarchical export not activated
        {
            // check if we have to resolve a query and export the result
            if (this.p_isResolveQuery && obj instanceof QueryExecutive_01)
            {
                // export the content of the query but not the query itself
                this.p_exportedObjects += this.performContainerExport (
                    (Container) obj, destination, this.p_isRecursive,
                    this.p_isDeleteRecursive);
                isExportOk = true;
            } // if (this.p_isResolveQuery && obj instanceof QueryExecutive_01)
            // check if we have to export a container
            else if (obj instanceof Container)
            {
                // check if the container object itself should be exported
                if (this.p_isExportContainer)
                {
                    // export the container object
                    if (this.performObjectExport (obj, destination))
                    {
                        // increase the counter of successfully
                        // exported objects
                        this.p_exportedObjects++;
                        isExportOk = true;
                    } // if (performObjectExport (obj, destination))
                    else     // object export not ok
                    {
                        this.log.add (DIConstants.LOG_ERROR, 
                            MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                                DIMessages.ML_MSG_COULD_NOT_EXPORT_OBJECT,
                                new String[] {obj.name}, env));
                    } // else object export not ok
                } // if (this.p_isExportContainer)
                else    // container object itself should not be exported
                {
                    isExportOk = true;
                } // else if (this.p_isExportContainer)
                // check if the content of the container should be exported
                // in case the object is an query and resolving the query is
                // activated the content has already been exported above
                if (this.p_isRecursive)
                {
                    this.p_exportedObjects += this.performContainerExport (
                        (Container) obj, destination, this.p_isRecursive,
                        this.p_isDeleteRecursive);
                    // we assume that the container export was ok
                    isExportOk = true;
                } // if (this.p_isRecursive)
            } // if (obj instanceof Container)
            else  // export of a standard object
            {
                // export the object
                if (this.performObjectExport (obj, destination))
                {
                    // increase the counter of successfully
                    // exported objects
                    this.p_exportedObjects++;
                    isExportOk = true;
                } // if (performObjectExport (obj, destination))
            } // else export of a standard object
        } // else not hierarchical export

        // if the export was not successfuly add the error message to the log
        if (!isExportOk)
        {
            this.log.add (DIConstants.LOG_ERROR, 
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_COULD_NOT_EXPORT_OBJECT,
                    new String[] {obj.name}, env));
        } // if (!isExportOk)
        // return the result of the operation
        return isExportOk;
    } // performObjectTreeExport


    /**************************************************************************
     * Performs the export of the object. The BusinesObject writes its data
     * into an DataElement object that will be uses as input for the
     * filter to create the requested export output.
     * This method assumes that the business object has already
     * been retrieved.<BR/>
     *
     * @param obj                   the object to export
     * @param destination           destination path or url and filename
     *
     * @return true if object could be exported and false if not
     */
    public boolean performObjectExport (BusinessObject obj, String destination)
    {
/* KR 020125: not necessary because already done before
        // we need to retrieve the object data first
        try
        {
            obj.retrieve (Operations.OP_VIEW);
            // now perform the export
        } // try
        catch (NoAccessException e)
        {
            obj.showNoAccessMessage (Operations.OP_VIEW);
            return false;
        } // catch
        catch (AlreadyDeletedException e) // no access to objects allowed
        {
            // send message to the user:
            obj.showAlreadyDeletedMessage ();
            return false;
        } // catch
        catch (Exception e)
        {
//showDebug ("Exception: " + e.toString ());
            // send message to the user:
            return false;
        } // catch
*/

        // check if the object does not exists
        // check this from the class BusinessObject
        if (obj.state == States.ST_NONEXISTENT)
        {
            // show log entry object with OID not found
            this.log.add (DIConstants.LOG_ERROR, 
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_OBJECTNOTFOUND,
                    new String[] {obj.name}, env));
            // return false if object not exists
            return false;
        } // if (obj.state == States.ST_NONEXISTENT)

        // create a DataElement that will hold the data
        DataElement dataElement = new DataElement ();
        dataElement.m2AbsBasePath = this.m2AbsBasePath;
        dataElement.oid = obj.oid;
        dataElement.p_isTabObject = obj.isTab ();
        dataElement.typename = obj.typeName;
        // get the data from the businessObject and write it
        // to a DataElement that will hold the data
        obj.writeExportData (dataElement);

        // set the keymapping in the data element of the object:
        this.setKeyMapping (obj, dataElement);

        // export physical files if necessary:
        // this must be done before the xml document will be written
        // because the names of the files can change in order to avoid
        // overwriting of existing files
        this.writeExportFiles (dataElement);
        // check whether to print a status bar
        this.showStatusBar ();

        // check if we have to write into a single file
        if (this.p_isSingleFile)
        {
            // first export the main object
            if (this.filter.add (dataElement))
            {
                this.log.add (DIConstants.LOG_ENTRY, obj.oid, dataElement.typename + " '" +
                    dataElement.name + "' " + 
                    MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                        DITokens.ML_ADDEDTOEXPORT, env) + ".");
                // add the object to the successfully exported objects:
                this.p_exportedObjectsVector.add (obj);

                // the tabs are exported only in the hierarchical export mode
                if (this.p_isHierarchicalExport)
                {
                    // now export the tab objects
                    this.performTabObjectExport (obj, dataElement);
                } // if hierarchical export
            } // if (this.filter.add (dataElement))
            else        // could not add the object
            {
                this.log.add (DIConstants.LOG_ERROR, obj.oid, dataElement.typename + " '" +
                    dataElement.name + "' " + 
                    MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                        DITokens.ML_COULDNOTADDTOEXPORT, env) + ".");
                return false;
            } // could not add the object
        } // if (this.p_isSingleFile)
        else            // create the export document and write it
        {
            // generate filename
            String fileName = this.generateFileName (obj);
            // check for unique filename
            fileName = FileHelpers.getUniqueFileName (destination, fileName);
            this.filter.setFileName (fileName);
            this.filter.setPath (destination);

            // write the data element and create the export document
            if (this.writeDataElement (dataElement))
            {
                // display message that export was successful
                this.log.add (DIConstants.LOG_ENTRY, obj.oid, dataElement.typename + " '" +
                    dataElement.name + "' " + 
                    MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                        DITokens.ML_EXPORTED, env) + ".");
                // add the object to the successfully exported objects:
                this.p_exportedObjectsVector.add (obj);
            } // if (writeDataElement (dataElement, destination, fileName))
            else    // export file could not have been written
            {
                this.log.add (DIConstants.LOG_ERROR, obj.oid, dataElement.typename + " '" +
                    dataElement.name + "' " + 
                    MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                        DITokens.ML_NOTEXPORTED, env) + ".");
                return false;
            } // export file could not have been written
        } // create the export document and write it

        try
        {
            // call object's finish export method:
            obj.finishExport ();
        } // try
        catch (ExportException e)
        {
            this.log.add (DIConstants.LOG_ERROR, obj.oid,
                dataElement.typename + " '" + dataElement.name +
                "' Error in finishExport: " + e.getMessage () + ".");
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch

        return true;
    } // performObjectExport


    /**************************************************************************
     * Performs the export of an container. <BR/>
     * All objects within this container will be wrapped into export xml files
     * and written to a destination path.
     *
     * @param container         container object or subclass
     * @param exportPath        path of export destination
     * @param isRecursive       true if traverse subcontainers is set
     * @param isDeleteContent   flag to delete the content after export
     *
     * @return  The number of objects exported.
     */
    /**************************************************************************
     * This method ... <BR/>
     *
     * @param   container       The container from which to export objects.
     * @param   exportPath      Path to which to export the objects.
     * @param   isRecursive     Shall the export be done recursively?
     * @param   isDeleteContent Shall the content of the container be deleted
     *                          after successful export?
     *
     * @return  The number of exported objects.
     */
    public int performContainerExport (Container container, String exportPath,
                                       boolean isRecursive,
                                       boolean isDeleteContent)
    {
        int exportedObjects = 0;
        BusinessObject obj = null;
        OID origOid = null;

        // create a objectFactory in order to create the object
        ObjectFactory objectFactory = this.getObjectFactory ();

        // get the content of the container
        // the centent of the container will be stored in an element vector
        try
        {
            container.retrieveContent (Operations.OP_VIEWELEMS,
                container.orderBy, container.orderHow);
        } // try
        catch (NoAccessException e)
        {
            container.showNoAccessMessage (Operations.OP_VIEWELEMS);
            return 0;
        } // catch

        // check if the conatiner is empty
        if (container.elements.size () == 0)
        {
            return 0;
        } // if container is empty

/*
        // find out if the container object is a discussion
        // this is necessary in order to be able to export the
        // correct objects. see comment below
        // Note that the retrieveContent of a discussion returns all
        // topic AND all entry objects. there should only be the
        // topic objects in order to ensure a correct exporting because
        // the entry objects are also sub objects of a topic object.
        boolean isDiscussion =
            container.typeName.equalsIgnoreCase (
                getCache ().getTypeName (m2Types.TC_Discussion));

        String entryTypeCode = getCache ().getTypeName (m2Types.TC_Beitrag);
*/

        if (this.p_isHierarchicalExport)
        {
            // change the insertion point (OPEN THE OBJECTS TAG)
            this.filter.setInsertionPoint (false);
        } // if p_isHierarchicalExport

        // now loop through the elements and create the export files
        for (Iterator<ContainerElement> iter = container.elements.iterator ();
            iter.hasNext ();)
        {
            // get the container element:
            ContainerElement ce = iter.next ();

            // check if the container element is exportable:
            // Note that the retrieveContent of a discussion returns all
            // topic AND all entry objects. there should only be the
            // topic objects in order to ensure a correct exporting because
            // the entry objects are also sub objects of a topic object.
            boolean isExportableElement = ce.isExportable (this.app);

            // the bidirectional link elements must be ignored
            // because this elements are not physical objects
            // and should not be exported
            if (ce instanceof ReferenzContainerElement_01)
            {
                ReferenzContainerElement_01 link = (ReferenzContainerElement_01) ce;
                if (link.getLinkType () != BOConstants.LINKTYPE_PHYSICAL)
                {
                    isExportableElement = false;
                } // if not physical link
            } // if the element is a reference element
            // if the isResolveReference flag is deactivated
            // do not resolve any references and exclude them from export
            if ((!this.p_isIncludeReference) && ce.isLink)
            {
                isExportableElement = false;
            } // if ((! this.p_isResolveReferences) && (ce.isLink))
            // if the container is a discussion we need to ignore every
            // Beitrag object within the content of the discussion because
            // every Beitrag object itself contains again its answers
            // that means that only topic objects can be directly exported
            // from a discussion content
            if (isExportableElement)
            {
                // save the original oid
                origOid = ce.oid;
                // check if the object has been retrieved within a query
                // note that queryies do not set the linkedObjectId property
                if (container instanceof QueryExecutive_01)
                {
                    // create an instance of the object type using the objectFactory
                    obj = objectFactory.getObject (ce.oid, this.user, this.sess, this.env);
                    if (this.p_isResolveReference)
                    {
                        obj = this.resolveReference (obj);
                        // check if a reference has been resolved and if the oid of
                        // the reference should be used
                        if (obj != null && this.p_isUseReferencesOid)
                        {
                            obj.setOid (origOid);
                        } // if
                    } // if (this.p_isResolveReference)
                } // if (container instanceof QueryExecutive_01)
                else    // no query result
                {
                    // if this is a reference to another object
                    // and the resolve reference option is activated,
                    // the linked object is exported instead of the reference.
                    if (ce.isLink && this.p_isResolveReference)
                    {
                        // create an instance of the object type using the objectFactory
                        obj = objectFactory.getObject (ce.linkedObjectId, this.user, this.sess, this.env);
                        // check if a reference has been resolved and if the oid of
                        // the reference should be used
                        if (obj != null && this.p_isUseReferencesOid)
                        {
                            obj.setOid (origOid);
                        } // if
                    } // if (ce.isLink && this.p_isResolveReference)
                    else    // no reference
                    {
                        // create an instance of the object type using the objectFactory
                        obj = objectFactory.getObject (ce.oid, this.user, this.sess, this.env);
                    } // else no reference
                } // else no query result

                // check if the object could be created
                if (obj == null)
                {
                    this.log.add (DIConstants.LOG_ERROR, null,  
                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                            DIMessages.ML_MSG_OBJECTNOTFOUND,
                            new String[] {this.oid.toString ()}, env));
                } // if (obj == null)
                else    // object has been sucessfully created
                {
                    // check if the content of a query has to be exported
                    if (this.p_isResolveQuery &&
                        obj instanceof QueryExecutive_01)
                    {
                        // export the content of the query but not the query itself
                        this.p_exportedObjects += this.performContainerExport (
                            (Container) obj, exportPath, isRecursive, false);
                    } // if (this.p_isResolveQuery && obj instanceof QueryExecutive_01)
                    // check if the object is a container object
                    else if (obj instanceof Container &&
                        !(obj instanceof QueryExecutive_01))
                    {
                        // check whether to export the container object too
                        if (this.p_isHierarchicalExport ||
                            this.p_isExportContainer)
                        {
                            // change the insertion point to this container
                            // this.filter.setInsertionPoint (false);
                            // perform the export and check if successful
                            if (this.performObjectExport (obj, exportPath))
                            {
                                exportedObjects++;
                            } // if performObjectExport ok
                            else        // object export not ok
                            {
                                this.log.add (DIConstants.LOG_ERROR,  
                                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                                        DIMessages.ML_MSG_COULD_NOT_EXPORT_OBJECT,
                                        new String[] {obj.name}, env));
                            } // else object export not ok
                        } // if (this.p_isExportContainer)

                        // check whether to traverse the subcontainer
                        if (this.p_isHierarchicalExport || isRecursive)
                        {
                            // export the objects in the subcontainer recursivly
                            exportedObjects += this.performContainerExport (
                                (Container) obj, exportPath, isRecursive, false);
                        } // if (isRecursive)
                        // Revert the insertion point to the parent
                        //this.filter.revertInsertionPoint ();
                    } // if (obj instanceof Container)
                    else            // found non-container object
                    {   // export the object
                        if (this.performObjectExport (obj, exportPath))
                        {
                            exportedObjects++;
                        } // if (performObjectExport (obj, exportPath))
                        else        // object export not ok
                        {
                            this.log.add (DIConstants.LOG_ERROR,  
                                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                                    DIMessages.ML_MSG_COULD_NOT_EXPORT_OBJECT,
                                    new String[] {obj.name}, env));
                        } // else object export not ok
                    } // else found non-container object
                } // else object has been sucessfully created
            } // if (isExportableElement)
            else    // object ignored
            {
                // object ignored, nothing to do
            } // else
        } // for iter

        if (this.p_isHierarchicalExport)
        {
            // change the insertion point back (CLOSE the OBJECTS TAG)
            this.filter.revertInsertionPoint ();
        } // if p_isHierarchicalExport

        // check if content of the container should be deleted
        if (isDeleteContent)
        {
            // delete the content of the container
            this.deleteSubobjects (container);
        } // if (this.p_isDeleteRecursive)

        // return the number of objects exported
        return exportedObjects;
    } // performContainerExport


    /**************************************************************************
     * Writes all export files to the destination paths. <BR/>
     *
     * @param   dataElement The DataElement object that holds the export data.
     *
     * @return  <CODE>true</CODE> if all files could have been written or
     *          <CODE>false</CODE> otherwise.
     */
    protected boolean writeExportFiles (DataElement dataElement)
    {
        FileDataElement fileDataElement;
        String newFileName;
        boolean allOk = true;

        // do we have any data?
        if (dataElement.files != null && dataElement.files.size () > 0)
        {
            // get the files from the data element:
            // loop through all the file elements in the DataElement:
            for (Iterator<FileDataElement> iter = dataElement.files.iterator (); iter.hasNext ();)
            {
                fileDataElement = iter.next ();
                // now write the file to the destination path
                newFileName = this.writeExportFile (fileDataElement.path,
                                               fileDataElement.fileName);
                // check if filename changed
                if (!fileDataElement.fileName.equals (newFileName))
                {
                    // change the value for the filename in the DataElement
                    dataElement.changeValue (fileDataElement.field, newFileName);
                } // if (! file.filename.equals (fileName)

                // check if file could be written
                if (newFileName == null || newFileName.length () == 0)
                {
                    // add a log entry
                    this.log.add (DIConstants.LOG_WARNING, 
                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                            DIMessages.ML_MSG_COULD_NOT_WRITE_FILE,
                            new String[] {fileDataElement.fileName}, env));
                    // indiacte that an error occurred
                    allOk = false;
                } // if (newFileName == null || newFileName.length () == 0)
            } // for iter
        } // if (dataElement.files != null && dataElement.files.size () > 0)

        return allOk;
    } // writeExportFiles


    /**************************************************************************
     * Writes a file to a connector and ensures that the file name is
     * unique. Returns the filename because it could have been changed
     * and null if an exception occurred.<BR/>
     *
     * @param   sourcePath      The path of the source file.
     * @param   sourceFileName  The name of the file to be written.
     *
     * @return  The name of the file or
     *          <CODE>null</CODE> in case the file could not have been written.
     */
    protected String writeExportFile (String sourcePath, String sourceFileName)
    {
        String fileName = null;         // the name of the exported file

        try
        {
            // write the file to the connector and return the fileName
            // in case it has been changed
            fileName = this.connector.writeFile (sourcePath, sourceFileName);
        } // try
        catch (ConnectionFailedException e)
        {
            // return null to indicate that the file could not have been written
            fileName = null;
        } // catch

        // check if we have to create a backup:
        // IMPORTANT: this has to be done BEFORE writing the file to
        // the output connector because if there occurs an error during
        // writing the file the method terminates immediately!
        if (this.p_isCreateBackup && this.p_backupConnector != null)
        {
            try
            {
                // set file name:
                if (fileName != null)
                {
                    this.p_backupConnector.setFileName (fileName);
                } // if

                // try to write the data to the connector:
                this.p_backupConnector.writeFile (sourcePath, sourceFileName);
/*
                // copy the file to the backup destination:
                FileHelpers.copyFile (
                    FileHelpers.addEndingFileSeparator (sourcePath) +
                        sourceFileName,
                    this.p_backupDir + fileName);
*/
            } // if
            catch (ConnectionFailedException e)
            {
                // add the message of the exception to the log
                this.log.add (DIConstants.LOG_ERROR, e.getMessage ());
                this.log.add (DIConstants.LOG_ERROR, 
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_COULDNOTWRITETOBACKUPCONNECTOR, env));
            } // catch
        } // if

        return fileName;
    } // writeExportFile


    /**************************************************************************
     * Returns the default connector object. This is a fileconnector set to
     * the default import directory. <BR/>
     *
     * BB HINT: This method is only used for test purposes
     *
     * @return the fileConnector object
     */
    protected Connector_01 getDefaultConnector ()
    {
        return super.getDefaultConnector (
            this.m2AbsBasePath + DIConstants.PATH_EXPORT);
    } // getDefaultConnector


    /**************************************************************************
     * Creates a xml document out of an DataElement and writes it to a
     * the export destination.<BR/>
     *
     * @param dataElement     the DataElement to create the xml document from
     *
     * @return  ???
     */
    public boolean writeDataElement (DataElement dataElement)
    {
        // try to initialize the export
        if (this.filter.create (dataElement))
        {
            return this.writeExportData ();
        } // if (isPath)

        return false;
    } // writeDataElement


    /**************************************************************************
     * Writes the export data. Writes first the export document from the filter
     * into the filesystem. Performs a translation if necessary and writes
     * the export data to the connector. <BR/>
     *
     * @return  <CODE>true</CODE> if writing was successful or
     *          <CODE>false</CODE> otherwise.
     */
    private boolean writeExportData ()
    {
        String exportFileName;

        // first write the data from the filter to the filesystem
        if (this.filter.write ())
        {
            // set the name of the export file
            exportFileName = this.filter.getFileName ();
            // start the translation in case there is a translator present
            if (this.translator != null)
            {
                // apply a translator.
                // NOTE that the file name changes in case the translation was
                // successful
                exportFileName =
                    this.applyTranslator (this.filter.getPath (), exportFileName);
            } // if (this.translator != null)

            // check if we still have a valid file name:
            if (exportFileName != null && exportFileName.length () > 0)
            {
                String exportPath = this.filter.getPath ();

                // check if we have to create a backup:
                // IMPORTANT: this has to be done BEFORE writing the file to
                // the output connector because if there occurs an error during
                // writing the file the method terminates immediately!
                if (this.p_isCreateBackup && this.p_backupConnector != null)
                {
                    try
                    {
                        // try to write the data to the connector:
                        this.p_backupConnector.writeFile (exportPath,
                            exportFileName);
/*
                        // copy the file to the backup destination:
                        FileHelpers.copyFile (
                            FileHelpers.addEndingFileSeparator (exportPath) +
                                exportFileName,
                            this.p_backupDir + exportFileName);
*/
                    } // try
                    catch (ConnectionFailedException e)
                    {
                        // add the message of the exception to the log
                        this.log.add (DIConstants.LOG_ERROR, e.getMessage ());
                        this.log.add (DIConstants.LOG_ERROR, 
                            MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                                DIMessages.ML_MSG_COULDNOTWRITETOBACKUPCONNECTOR, env));
                        // return false to indicate that file could not
                        // have been written
                        return false;
                    } // catch
                } // if

                // set the filename and the path from the filter:
                this.connector.setFileName (exportFileName);
                this.connector.setPath (exportPath);

                // try to write the data to the connector:
                try
                {
                    // write the export file to the connector:
                    this.connector.write ();
                    // check if external ids should be restored
                    if (this.p_isRestoreExternalId)
                    {
                        // response external ids from the response of the connector
                        this.restoreExternalIDs (this.connector.getResponse ());
                    } // if (this.p_isRestoreExternalId)
                    // return true to indicate that file has been written
                    return true;
                } // try
                catch (ConnectionFailedException e)
                {
                    // add the message of the exception to the log
                    this.log.add (DIConstants.LOG_ERROR, e.getMessage ());
                    this.log.add (DIConstants.LOG_ERROR,  
                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                            DIMessages.ML_MSG_COULDNOTWRITETOCONNECTOR, env));
                    // return false to indicate that file could not
                    // have been written
                    return false;
                } // catch
            } // if (startTranslator (this.filter.getPath, this.filter.getFileName))

            // could not translate
            return false;
        } // if (this.filter.write ())

        // filter could not write
        return false;
    } // writeExportData


    /**************************************************************************
     * Apply a translator to a file. <BR/>
     *
     * @param path             the path to the file to be translated
     * @param fileName         the name of the file to be translated
     *
     * @return  the filename of the translated file or the name of the
     *          original file in case there was no translator present
     */
    private String applyTranslator (String path, String fileName)
    {
        String translatedFileName = fileName;

        try
        {
            // now try to convert the file
            // note that the translate method returns the filename
            // of the translated file. This will be the name of
            // the input file wheras he inputfile will be renamed.
            translatedFileName =
                this.translator.translate (path, fileName, this.log);

            // add a log entry:
            this.log.add (DIConstants.LOG_ENTRY, path + fileName + " " +
                MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                    DITokens.ML_TRANSLATED, env) + ".");
            // return the name of the translated file
            return translatedFileName;
        } // try
        catch (TranslationFailedException e)
        {
            this.log.add (DIConstants.LOG_ERROR, path + fileName + " " +
                MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                    DITokens.ML_NOT_TRANSLATED, env) + "! (" + e.getMessage () + ")");
            // return the filename
            return translatedFileName;
        } // catch
    } // applyTranslator


    /**************************************************************************
     * Checks if an object is a reference.<BR/>
     *
     * @param   obj     The object to be tested.
     *
     * @return  true if the object is a reference and false otherwise
     */
    private boolean isReference (BusinessObject obj)
    {
        // check if Object exists
        if (obj == null || obj.state == States.ST_NONEXISTENT)
        {
            return false;               // return null if object not exists
        } // if (obj.state == States.ST_NONEXISTENT)

        // object exists
        // check if the object is a reference
        if (obj instanceof Referenz_01)
        {
            return true;
        } // if

        return false;
    } // isReference


    /**************************************************************************
     * Checks if an object is a reference and returns the referenced object.<BR/>
     *
     * @param   obj     The object to be tested.
     *
     * @return  The referenced object or the object itself if it is no reference.
     */
    private BusinessObject resolveReference (BusinessObject obj)
    {
        BusinessObject resultObj = null;

        // check if Object exists
        if (obj == null || obj.state == States.ST_NONEXISTENT)
        {
            return null;               // return null if object not exists
        } // if (obj.state == States.ST_NONEXISTENT)

        // object exists
        // check if the object is a reference
        if (obj instanceof Referenz_01)
        {
            // now create a new object with the linkedId
            ObjectFactory objectFactory = this.getObjectFactory ();
            resultObj = objectFactory.getObject (obj.linkedObjectId, this.user,
                this.sess, this.env);
            return resultObj;
        } // if (obj instanceof Referenz_01)

        // object is not a reference
        return obj;
    } // resolveReference


    /**************************************************************************
     * Deletes all sub objects of a container. <BR/>
     * This method is called if the option -deleteRecursive is activated.
     * ? In case the container in which the subobjects are will not be deleted. ?
     *
     * @param   container   The container in which the subobjects shall be
     *                      deleted.
     */
    private void deleteSubobjects (Container container)
    {
        BusinessObject deleteObject = null;
        ContainerElement ce = null;

        // get the elements of the container:
        // delete all elements while no element exist
        for (Iterator<ContainerElement> iter = container.elements.iterator ();
            iter.hasNext ();)
        {
            // we get from the container a container element
            ce = iter.next ();
            // get the oid saved in the container element
            // get an object from the oid
            deleteObject = this.objectFactory.getObject (ce.oid, this.user,
                                                         this.sess, this.env);
            // delete the objects in the container
            if (deleteObject.delete (Operations.OP_DELETE))
            {
                // show message that objects are deleted after export
                this.log.add (DIConstants.LOG_ENTRY, this.oid, ce.typeName  + " '" +
                    ce.name  + "' " + 
                    MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                        DITokens.ML_DELETEDAFTEREXPORT, env)  + ".");
            } // if
            else    // could not delete object
            {
                this.log.add (DIConstants.LOG_ENTRY, this.oid, ce.typeName  + " '" +
                    ce.name  + "' " + 
                    MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                        DITokens.ML_NOT_DELETED, env) + ".");
            } // could not delete object
        } // for iter
    } // deleteSubobjects ()


    /**************************************************************************
     * Gets the DOMAIN/ID pair for the given object and stores the information
     * in the dataElement.
     *
     * @param   dataElement     The dataElement of object to export.
     *
     * @return  <CODE>true</CODE> on success otherwise <CODE>false</CODE>
     */
    private boolean resolveKeyMapping (DataElement dataElement)
    {
        // The mapping is performed by a KeyMapper object
        KeyMapper keyMapper = new KeyMapper (this.user, this.env, this.sess, this.app);
        // Get the external key
        KeyMapper.ExternalKey extKey = keyMapper.performResolveMapping (dataElement.oid, true);
        // Store the result in the dataElement
        if (extKey != null)
        {
            dataElement.id = extKey.getId ();
            dataElement.idDomain = extKey.getDomain ();
            return true;
        } // if extKey is found
        return false;
    } // resolveKeyMapping


    /**************************************************************************
     * Takes a string and assumes this is an oid of an object or a path
     * to an object. Tries to resolve the path and returns a valid oid. If
     * the value could not be resolved it returns null.<BR/>
     *
     * @param   oidStr      The string that should contain an oid or
     *                      a path to an object.
     *
     * @return  The resolved oid or <CODE>null</CODE> if it could not be found.
     */
    private OID resolveOid (String oidStr)
    {
        OID oid = null;
        boolean [] boolArray = {true};

        try
        {
            // check if the value is an oid and starts with 0x
            // if this oid is too long or too short it is handled like an object
            if (oidStr.startsWith (UtilConstants.NUM_START_HEX))
            {
                // try to convert oidString to a oid
                oid = new OID (oidStr);
            } // if
            else    // oid string is possibly a path definition
            {
                // we assume that the value is a path
                // definition and try to resolve it
                oid = BOHelpers.resolveObjectPath (
                    oidStr, boolArray, this, this.env);
            } // oid string is a path definition
        } // try
        catch (IncorrectOidException e)
        {
            // conversion failed. we assume that
            // the value is a path definition and try to
            // resolve it once again
            oid = BOHelpers.resolveObjectPath (
                oidStr, boolArray, this, this.env);
        } // catch
        return oid;
    } // resolveOid


    /**************************************************************************
     * Creates an interface oid from the name of the interface. <BR/>
     *
     * @param   interfaceName           name of the interface
     *
     * @return   The oid of the interface object.
     */
    public OID getExportInterfaceOid (String interfaceName)
    {
        OID exportInterfaceOid = null;
        int tVersionId = 0;

        // get the tVersionId out of the database with the type export interface
        tVersionId = this.getTypeCache ().getTVersionId (DIConstants.TC_EXPORTINTERFACE);
        // get the Oid from the name
        exportInterfaceOid = this.getOidFromObjectName (tVersionId, interfaceName);
        // return the export interface oid
        return exportInterfaceOid;
    } // getExportInterfaceOid


    /**************************************************************************
     * Is used from components which handles with interfaces to get the settings
     * from it. <BR/>
     * This method takes the values from the XML-form without log settings of
     * the form fields and write them to the environment and initialize the
     * objects to be need. <BR/>
     *
     * @param dataElement     dataElement instance of an exportInterface
     */
    public void getExportInterfaceParameter (DataElement dataElement)
    {
        // set the connector
        // get the oidstring from the value that looks like (oid),(name)
        if (dataElement.exists (DIConstants.INTERFACE_CONNECTOR))
        {
            // get the value of the field
            String connectorStr =
                dataElement.getImportStringValue (DIConstants.INTERFACE_CONNECTOR);
            // get the oid out of the string
            String connectorOidStr =
                dataElement.getSelectionOidValue (connectorStr);
            // set the connector instance
            this.setConnector (this.createConnectorFromOid (connectorOidStr));
        } // if (dataElement.exists (DIConstants.INTERFACE_CONNECTOR))
        // set translator
        if (dataElement.exists (DIConstants.INTERFACE_TRANSLATOR))
        {
            // get the value of the field
            String translatorStr =
                dataElement.getImportStringValue (DIConstants.INTERFACE_TRANSLATOR);
            // get the oid out of the string
            String translatorOidStr =
                dataElement.getSelectionOidValue (translatorStr);
            // set the translator instance
            this.setTranslator (this.createTranslatorFromOid (translatorOidStr));
        } // if (dataElement.exists (DIConstants.INTERFACE_TRANSLATOR));
        // set the filter
        if (dataElement.exists (DIConstants.INTERFACE_EXPORT_FILTER))
        {
            this.filterId =
                dataElement.getImportIntValue (DIConstants.INTERFACE_EXPORT_FILTER);
            // set the filter object
            this.setFilter (DIHelpers.getExportFilter (this.filterId, env));
        } //  if (dataElement.exists (DIConstants.INTERFACE_EXPORT_FILTER))
        // hierarchical export
        if (dataElement.exists (DIConstants.INTERFACE_HIERARCHICAL))
        {
            this.p_isHierarchicalExport =
                dataElement.getImportBooleanValue (DIConstants.INTERFACE_HIERARCHICAL);
        } // if (dataElement.exists (DIConstants.INTERFACE_HIERARCHICAL))
        else
        {
            this.p_isHierarchicalExport = false;
        } // else
        // export container
        if (dataElement.exists (DIConstants.INTERFACE_EXPORTCONTAINER))
        {
            this.p_isExportContainer =
                dataElement.getImportBooleanValue (DIConstants.INTERFACE_EXPORTCONTAINER);
        } // if (dataElement.exists (DIConstants.INTERFACE_EXPORTCONTAINER))
        else
        {
            this.p_isExportContainer = false;
        } // else
        // export recursive
        if (dataElement.exists (DIConstants.INTERFACE_RECURSIVE))
        {
            this.p_isRecursive = dataElement
                .getImportBooleanValue (DIConstants.INTERFACE_RECURSIVE);
        } // if (dataElement.exists (DIConstants.INTERFACE_RECURSIVE))
        else
        {
            this.p_isRecursive = false;
        } // else
        // singlefile
        if (dataElement.exists (DIConstants.INTERFACE_SINGLEFILE))
        {
            this.p_isSingleFile =
                dataElement.getImportBooleanValue (DIConstants.INTERFACE_SINGLEFILE);
        } // if (dataElement.exists (DIConstants.INTERFACE_SINGELEFILE))
        else
        {
            this.p_isSingleFile = false;
        } // else
        // set the backup connector
        // get the oidstring from the value that looks like (oid),(name)
        if (dataElement.exists (DIConstants.INTERFACE_BACKUPCONNECTOR))
        {
            // get the value of the field
            String backupConnectorStr = dataElement
                .getImportStringValue (DIConstants.INTERFACE_BACKUPCONNECTOR);
            // get the oid out of the string
            String backupConnectorOidStr =
                dataElement.getSelectionOidValue (backupConnectorStr);
            // set the connector instance
            this.setConnector (this.createConnectorFromOid (backupConnectorOidStr));
        } // if (dataElement.exists (DIConstants.INTERFACE_CONNECTOR))
/*
        // is pretty print
        if (dataElement.exists (DIConstants.INTERFACE_PRETTYPRINT))
        {
            this.p_isPrettyPrint =
                dataElement.getImportBooleanValue (DIConstants.INTERFACE_PRETTYPRINT);
        } // if (dataElement.exists (DIConstants.INTERFACE_PRETTYPRINT))
        else
        {
            this.p_isPrettyPrint = false;
        } // else
*/

        // get the log settings from the interface
        this.getLogSettingsFromInterface (dataElement);
    } // getExportInterfaceParameter


    /**************************************************************************
     * Get the setting for the export from the environment and write it to the
     * class properties. <BR/>
     */
    public void getEnvSettings ()
    {
        OID oidParam = null;
        int num = 0;
        String str;

        // get the connector oid string setting
        oidParam = this.env.getOidParam (DIArguments.ARG_CONNECTOR);
        if (oidParam != null)
        {
            // create the connector object
            this.connectorOid = oidParam;
        } // if (oidParam != null)

        // get the translator oid string setting
        oidParam = this.env.getOidParam (DIArguments.ARG_TRANSLATOR);
        if (oidParam != null)
        {
            // create the connector object
            this.translatorOid = oidParam;
        } // if (oidParam != null)

/* BB TODO: filters are not supported anymore
        // get the filter id setting
        num = this.env.getIntParam (DIArguments.ARG_FILTERID);
        if (num != -1)
        {
            // create the connector object
            this.filterId = num;
        } // if (num != null)
*/
        // check if we include container in export flag
        num = this.env.getBoolParam (DIArguments.ARG_EXPORTCONTAINER);
        this.p_isExportContainer = num == IOConstants.BOOLPARAM_TRUE;
        // check export to single file flag
        num = this.env.getBoolParam (DIArguments.ARG_EXPORTSINGLEFILE);
        this.p_isSingleFile = num == IOConstants.BOOLPARAM_TRUE;
        // check export recursive flag
        num = this.env.getBoolParam (DIArguments.ARG_EXPORTCONTENTRECURSIVE);
        this.p_isRecursive = num == IOConstants.BOOLPARAM_TRUE;
         // check if object delete
        num = this.env.getBoolParam (DIArguments.ARG_ISDELETE);
        this.p_isDelete = num == IOConstants.BOOLPARAM_TRUE;
        // check if subobject delete
        num = this.env.getBoolParam (DIArguments.ARG_ISDELETERECURSIVE);
        this.p_isDeleteRecursive = num == IOConstants.BOOLPARAM_TRUE;
        // check if external keymappings should be resolved
        num = this.env.getBoolParam (DIArguments.ARG_ISRESOLVEKEYMAPPING);
        this.p_isResolveKeyMapping = num == IOConstants.BOOLPARAM_TRUE;
        // check if reference oid should be used when references are resolved
        num = this.env.getBoolParam (DIArguments.ARG_ISUSEREFERENCEOID);
        this.p_isUseReferencesOid = num == IOConstants.BOOLPARAM_TRUE;
        // check if external keymappings should be restored from response
        num = this.env.getBoolParam (DIArguments.ARG_ISRESTOREEXTERNALID);
        this.p_isRestoreExternalId = num == IOConstants.BOOLPARAM_TRUE;
        // check if query should be resolved
        num = this.env.getBoolParam (DIArguments.ARG_ISRESOLVEQUERY);
        this.p_isResolveQuery = num == IOConstants.BOOLPARAM_TRUE;
        // check if references should be resolved
        num = this.env.getBoolParam (DIArguments.ARG_ISRESOLVEREFERENCE);
        this.p_isResolveReference = num == IOConstants.BOOLPARAM_TRUE;

        // export filename pattern
        str = this.env.getStringParam (DIArguments.ARG_EXPORTFILE);
        if (str != null)
        {
            this.p_exportFileName = str;
        } // if
        // keymapping domain pattern
        str = this.env.getStringParam (DIArguments.ARG_DOMAINPATTERN);
        if (str != null)
        {
            this.p_domainPattern = str;
        } // if
        // keymapping id pattern
        str = this.env.getStringParam (DIArguments.ARG_IDPATTERN);
        if (str != null)
        {
            this.p_idPattern = str;
        } // if

        // check preserve hierarchicalExport
        num = this.env.getBoolParam (DIArguments.ARG_HIERARCHICALEXPORT);
        this.p_isHierarchicalExport = num == IOConstants.BOOLPARAM_TRUE;
        if (this.p_isHierarchicalExport)
        {
            this.p_isSingleFile = true;
            this.p_isRecursive = true;
            this.p_isExportContainer = true;
        } // if

        // get parameters for backup:
        // get the backup connector oid string setting:
        this.p_backupConnectorOid = this.env
            .getOidParam (DIArguments.ARG_BACKUPCONNECTOR);

        // remember if a backup shall be created:
        this.p_isCreateBackup = this.p_backupConnectorOid != null;

        // check if we have a log instance
        if (this.log == null)
        {
            this.initLog ();
        } // if
        // get the parameters for the log instance
        this.log.getParameters ();
        this.isInterfaceUsed = false;
    } // getEnvSettings


    /**************************************************************************
     * Set the export parameters with the data from an export interface. <BR/>
     * An user can choose an interface which contains all setting for the export.
     * Now we have to get the interface parameters out of the XML-form and
     * take them for the export and write them class properties. <BR/>
     *
     * @param dataElement     dataElement instance of an exportInterface
     */
    public void setInterfaceSettings (DataElement dataElement)
    {
        String translatorStr = "";

        // check if dataElement is not null
        if (dataElement != null)
        {
            // check if the field connector exist
            if (dataElement.exists (DIConstants.INTERFACE_CONNECTOR))
            {
                // get the value of the field
                String connectorStr = dataElement
                    .getImportStringValue (DIConstants.INTERFACE_CONNECTOR);
                // get the oid out of the string
                String connectorOidStr =
                    dataElement.getSelectionOidValue (connectorStr);

                try
                {
                    // set the connector oid:
                    this.connectorOid = new OID (connectorOidStr);
                    this.isUseConnector = true;
                } // try
                catch (IncorrectOidException e)
                {
                    // reset the value of the connectorOid:
                    this.connectorOid = null;
                    this.isUseConnector = false;
                } // catch (IncorrectOidException e)
            } // if ((valueDataElement.field.equalsIgnoreCase (DITokens.TOK_CONNECTOR))
            else
            {
                // reset the value of the connectorOid:
                this.connectorOid = null;
                this.isUseConnector = false;
            } // else

            // filter
            if (dataElement.exists (DIConstants.INTERFACE_EXPORT_FILTER))
            {
                // check if filter id is not invalid
                if (this.filterId <= -1)
                {
                    this.filterId = 0;
                } // if (this.filterId <= -1)
                this.filterId = dataElement
                    .getImportIntValue (DIConstants.INTERFACE_EXPORT_FILTER);
            } // if (valueDataElement.field.equalsIgnoreCase (DITokens.TOK_FILTER));

            // set translator
            // get the oidstring from the value that looks like (oid),(name)
            if (dataElement.exists (DIConstants.INTERFACE_TRANSLATOR))
            {
                // get the value of the field
                translatorStr = dataElement
                    .getImportStringValue (DIConstants.INTERFACE_TRANSLATOR);
                // get the oid out of the string
                String translatorOidStr = dataElement.getSelectionOidValue (translatorStr);
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

            // hierarchical export
            if (dataElement.exists (DIConstants.INTERFACE_HIERARCHICAL))
            {
                this.p_isHierarchicalExport = dataElement
                    .getImportBooleanValue (DIConstants.INTERFACE_HIERARCHICAL);
            } // if (dataElement.exists (DIConstants.INTERFACE_HIERARCHICAL))
            else
            {
                this.p_isHierarchicalExport = false;
            } // else

            // export container
            if (dataElement.exists (DIConstants.INTERFACE_EXPORTCONTAINER))
            {
                this.p_isExportContainer = dataElement
                    .getImportBooleanValue (DIConstants.INTERFACE_EXPORTCONTAINER);
            } // if (dataElement.exists
                // (DIConstants.INTERFACE_EXPORTCONTAINER))
            else
            {
                this.p_isExportContainer = false;
            } // else

            // export recursive
            if (dataElement.exists  (DIConstants.INTERFACE_RECURSIVE))
            {
                this.p_isRecursive = dataElement
                    .getImportBooleanValue (DIConstants.INTERFACE_RECURSIVE);
            } // if (dataElement.exists (DIConstants.INTERFACE_RECURSIVE))
            else
            {
                this.p_isRecursive = false;
            } // else

            // singlefile
            if (dataElement.exists (DIConstants.INTERFACE_SINGLEFILE))
            {
                this.p_isSingleFile = dataElement
                    .getImportBooleanValue (DIConstants.INTERFACE_SINGLEFILE);
            } // if (dataElement.exists (DIConstants.INTERFACE_SINGELEFILE))
            else
            {
                this.p_isSingleFile = false;
            } // else
/*
            // is pretty print
            if (dataElement.exists (DIConstants.INTERFACE_PRETTYPRINT))
            {
                this.p_isPrettyPrint = dataElement.getImportBooleanValue
                                     (DIConstants.INTERFACE_PRETTYPRINT);
            } // if (dataElement.exists  (DIConstants.INTERFACE_PRETTYPRINT))
            else
            {
                this.p_isPrettyPrint = false;
            } // else
*/

            // check if the field backup connector exists:
            if (dataElement.exists (DIConstants.INTERFACE_BACKUPCONNECTOR))
                                        // backup connector field exists?
            {
                // get the value of the field:
                String backupConnectorStr = dataElement
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

            // check if a log instance esists
            if (this.log == null)
            {
                this.initLog ();
            } // if
            // get the log settings from the interface
            this.getLogSettingsFromInterface (dataElement);
            // mark that the interface has been set
            this.isInterfaceUsed = true;
        } // if dataElement is not null
    } // setInterfaceParameter


    /**************************************************************************
     * Get the interface object as a XML-Viewer object. And initialize the
     * object. <BR/>
     *
     * @param interfaceOid   oid of an exportInterface
     */
    public void getInterfaceSettings (OID interfaceOid)
    {
        // initialize the exportInterface instance
        XMLViewer_01 exportInterface = new XMLViewer_01 ();
        exportInterface.initObject (interfaceOid,
            this.user, this.env, this.sess, this.app);
        try
        {
            // retrieve the data from the exportInterface instance
            exportInterface.retrieve (Operations.OP_READ);
            // set the export parameter from the exportInterface
            this.setInterfaceSettings (exportInterface.dataElement);
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
     * Get the settings of a log from an export interface.<BR/>
     *
     * @param dataElement   the dataElement that holds the interface
     *                      properties
     */
    public void getLogSettingsFromInterface (DataElement dataElement)
    {
        if (this.log == null)
        {
            this.initLog ();
        } // if

        // display logfile
        if (dataElement.exists (DIConstants.LOG_DISPLAY))
        {
            this.p_isDisplayLog = dataElement
                .getImportBooleanValue (DIConstants.LOG_DISPLAY);
            // set settings in the log attributes
            this.log.isDisplayLog = this.p_isDisplayLog;
        } // if (valueDataElement.field.equalsIgnoreCase (DITokens.TOK_DISPLAYLOGFILE)
        // writelogfile
        if (dataElement.exists (DIConstants.LOG_SAVE))
        {
            this.p_isSaveLog = dataElement
                .getImportBooleanValue (DIConstants.LOG_SAVE);
            // set settings in the log attributes
            this.log.isWriteLog = this.p_isSaveLog;
        } // if (valueDataElement.field.equalsIgnoreCase (DITokens.TOK_WRITELOGFILE))
        // appendlogfile
        if (dataElement.exists (DIConstants.LOG_APPEND))
        {
            this.p_isAddtoLog = dataElement
                .getImportBooleanValue (DIConstants.LOG_APPEND);
            // set settings in the log attributes
            this.log.isAppendLog = this.p_isAddtoLog;
        } // if (valueDataElement.field.equalsIgnoreCase (DITokens.TOK_APPENDLOGFILE)
        // logfile name
        if (dataElement.exists (DIConstants.LOG_NAME))
        {
            this.p_logFileName = dataElement
                .getImportStringValue (DIConstants.LOG_NAME);
            // set settings in the log attributes
            this.log.setFileName (this.p_logFileName);
        } // if (valueDataElement.field.equalsIgnoreCase (DITokens.TOK_LOGFILENAME))
        // logfilepath
        if (dataElement.exists (DIConstants.LOG_PATH))
        {
            this.p_logFilePath = dataElement
                .getImportStringValue (DIConstants.LOG_PATH);
            // set settings in the log attributes
            this.log.setPath (this.p_logFilePath);
        } // if (valueDataElement.field.equalsIgnoreCase (DITokens.TOK_LOGFILEPATH))
    } // getLogSettingsFromInterface


    /**************************************************************************
     * Performs the export of the tab objects for the given object OID. <BR/>
     *
     * @param   obj         The object for with the tabs shold by exported.
     * @param   dataElement The DataElement of the object for with the tabs
     *                      shold by exported.
     *
     * @return  <CODE>true</CODE> if the export was successfully,
     *          otherwise <CODE>false</CODE>.
     */
    private boolean performTabObjectExport (BusinessObject obj,
                                            DataElement dataElement)
    {
        String[] tabOids = null;

        // get the OIDs of all tab object
        Vector<OID> oids = obj.performGetTabOids ();
        // transfer the oids from the Vector in a string array
        if (oids != null && oids.size () > 0)
        {
            // could get tab oids from the business object
            // the following tabs cannot not be exported:
            //   * Rights tab (RightsContainer)
            //   * Log tab (LogContainer)
            //   * Template tab of Discussions (XMLDiscussionTemplateContainer)
            //
            // get the type ids for this kind of tabs
            int rightsContTypeId = this.getTypeCache ().getTypeId ("RightsContainer");
            int protContTypeId = this.getTypeCache ().getTypeId ("LogContainer");
            int templContTypeId = this.getTypeCache ().getTypeId ("XMLDiscussionTemplateContainer");
            int membershipTypeId = this.getTypeCache ().getTypeId ("MembershipContainer");

            // extract only the exportable oids from the vector
            Vector<String> tabsToExport = new Vector<String> ();
            for (Iterator<OID> iterator = oids.iterator (); iterator
                .hasNext ();)
            {
                OID tabOid = iterator.next ();
                // check if the tab is exportable
                if (tabOid.type != rightsContTypeId &&
                    tabOid.type != protContTypeId &&
                    tabOid.type != templContTypeId &&
                    tabOid.type != membershipTypeId)
//                    tabOid.type != refContTypeId)
                {
                    tabsToExport.addElement (tabOid.toString ());
                } // if tab is exportable
            } // for iterator
            // transfer the oid strings from the vector in an array of strings
            tabOids = tabsToExport.toArray (new String[0]);
        } // if (oids.size () > 0)

        // export the tab objects
        if (tabOids != null && tabOids.length > 0)
        {
            // set the new insertion point (OPEN the TABS tag)
            this.filter.setInsertionPoint (true);
            this.performExportInternal (tabOids, true);
            // revert the insertion point (CLOSe the TABS tag)
            this.filter.revertInsertionPoint ();
        } // if there are tabs to export

        return true;
    } // performTabObjectExport


    /**************************************************************************
     * Returns an array with all translator type ids available for export. <BR/>
     * Note that this method must be extended in case new translator types
     * are added to the system. <BR/>
     *
     * @return  An array with all translator type ids available or
     *          <CODE>null</CODE> if there are none.
     */
    public int [] getTranslatorTypeIds ()
    {
        int[] translatorTypeIds =
        {
            this.getTypeCache ().getTypeId (TypeConstants.TC_Translator),
        };
        // return the array
        return translatorTypeIds;
    } // getTranslatorTypeIds


    /**************************************************************************
     * Generate an export file name.<BR/>
     * This is a wrapper method for generateFileName (BusinessObject object)<BR/>
     *
     * @return  The generated filename.
     */
    private String generateFileName ()
    {
        return this.generateFileName (null);
    } // generateFileName


    /**************************************************************************
     * Generates an export file name.<BR/>
     * If an export filename has been set it applies the pattern resolution.
     * In case not it generates the filename "export_{timestamp}.xml" or
     * "{objectname}.xml" in case an object has been passed as parameter.<BR/>
     *
     * @param obj   A BusinessObject whose data can be used in the template.
     *
     * @return the generated filename
     */
    private String generateFileName (BusinessObject obj)
    {
        String fileName = "";

        // check if a filename has been set
        if (this.p_exportFileName != null && (this.p_exportFileName.length () > 0))
        {
            fileName = this.resolvePatterns (this.p_exportFileName, obj);
        } // if (this.p_exportFileName.length () > 0)
        else                            // no filename set
        {
            // generate standard name:
            // if an object has been passed as parameter we use the object name
            if (obj != null)
            {
                fileName = obj.name;
            } // if
            else                        // no object set
            {
                if (this.p_timestamp == null)
                {
                    this.p_timestamp = DateTimeHelpers.getTimestamp ();
                } // if

                // generate a standard filename:
                fileName = DIConstants.PATH_EXPORTFILENAME + "_" +
                    this.p_timestamp;
//                    DateTimeHelpers.getDateString ();
            } // else
            // add the extension
            fileName += DIConstants.FILEEXTENSION_XML;
        } // no filename set - generate one
        return fileName;
    } // generateFileName


    /**************************************************************************
     * Resolve pattern placeholders in a string.<BR/>
     * In case an object is passed some additional patterns can be applied.<BR/>
     *
     * The following patterns can be applied.
     * <UL>
     * <LI>#dd# ... will be replaced by the actual day
     * <LI>#MM# ... will be replaced by the actual month
     * <LI>#yy# ... will be replaced by the actual year (2 digits)
     * <LI>#yyyy# ... will be replaced by the actual year (4 digits)
     * <LI>#hh# ... will be replaced by the actual hour
     * <LI>#mm# ... will be replaced by the actual minutes
     * <LI>#ss# ... will be replaced by the actual seconds
     * <LI>#ms# ... will be replaced by the actual milliseconds
     * <LI>#USERNAME# ... will be replaced by the actual username
     * <LI>#USERFULLNAME# ... will be replaced by the actual user fullname
     * <LI>#DOMAIN# ... will be replaced by the actual domainname
     * <LI>#DOMAINID# ... will be replaced by the actual domain id
     * </UL>
     * In case an object has been passed as parameter:
     * <UL>
     * <LI>#NAME# ... will be replaced by the name of the object
     * <LI>#OID# ... will be replaced by the oid of the object
     * <LI>#ID# ... will be replaced by the id of the object
     * <LI>#CONTAINERID# ... will be replaced by the containerid of the object
     * <LI>#TYPENAME# ... will be replaced by the typename of the object
     * <LI>#TYPECODE# ... will be replaced by the typecode of the object
     * </UL><BR/>
     *
     * @param   patternStr  The pattern to be resolved.
     * @param   obj         A BusinessObject whose data can be used for the
     *                      patterns.
     *
     * @return the string with resolved patterns
     */
    private String resolvePatterns (String patternStr, BusinessObject obj)
    {
        String pattern = patternStr;    // variable for local assignments
        GregorianCalendar calendar = null;
        String year;
        String day;
        String month;
        String hour;
        String minute;
        String second;
        String millisecond;

        // check if a patternStr has been set and contains a pattern character
        if (pattern != null && (pattern.length () > 0) &&
            (pattern.indexOf ("#") > -1))
        {
            calendar = new GregorianCalendar ();
            // create a 2-digit numbers for month
            month = "" + (calendar.get (Calendar.MONTH) + 1);
            if (month.length () == 1)
            {
                month = "0" + month;
            } // if
            // create a 2-digit numbers for day
            day = "" + calendar.get (Calendar.DAY_OF_MONTH);
            if (day.length () == 1)
            {
                day = "0" + day;
            } // if
            // create a 2-digit numbers for hour
            hour = "" + calendar.get (Calendar.HOUR_OF_DAY);
            if (hour.length () == 1)
            {
                hour = "0" + hour;
            } // if
            // create a 2-digit numbers for minute
            minute = "" + calendar.get (Calendar.MINUTE);
            if (minute.length () == 1)
            {
                minute = "0" + minute;
            } // if
            // create a 2-digit numbers for second
            second = "" + calendar.get (Calendar.SECOND);
            if (second.length () == 1)
            {
                second = "0" + second;
            } // if
            // create a 3-digit numbers for millisecond
            millisecond = "" + calendar.get (Calendar.MILLISECOND);
            if (millisecond.length () == 1)
            {
                millisecond = "00" + millisecond;
            } // if
            else if (millisecond.length () == 2)
            {
                millisecond = "0" + millisecond;
            } // else if

            // try to resolve the templates
            pattern = StringHelpers.replace (pattern, "#dd#", day);
            pattern = StringHelpers.replace (pattern, "#MM#", month);
            year = "" + calendar.get (Calendar.YEAR);
            pattern = StringHelpers.replace (pattern, "#yy#", year.substring (3));
            pattern = StringHelpers.replace (pattern, "#yyyy#", year);
            pattern = StringHelpers.replace (pattern, "#hh#", hour);
            pattern = StringHelpers.replace (pattern, "#mm#", minute);
            pattern = StringHelpers.replace (pattern, "#ss#", second);
            pattern = StringHelpers.replace (pattern, "#ms#", millisecond);
            pattern = StringHelpers.replace (pattern, "#USERNAME#", this.user.username);
            pattern = StringHelpers.replace (pattern, "#USERFULLNAME#", this.user.fullname);
            pattern = StringHelpers.replace (pattern, "#DOMAIN#", this.user.domainName);
            pattern = StringHelpers.replace (pattern, "#DOMAINID#", "" + this.user.domain);
            // in case an object has been passed we can apply object specific
            // patters
            if (obj != null)
            {
                pattern = StringHelpers.replace (pattern, "#NAME#", obj.name);
                pattern = StringHelpers.replace (pattern,
                    ExportIntegrator.REPL_OID, "" + obj.oid);
                pattern = StringHelpers.replace (pattern, "#ID#", "" + obj.oid.id);
                pattern = StringHelpers.replace (pattern, "#CONTAINERID#", "" + obj.containerId);
                pattern = StringHelpers.replace (pattern, "#TYPENAME#", obj.typeObj.getName ());
                pattern = StringHelpers.replace (pattern, "#TYPECODE#", obj.typeObj.getCode ());
            } // if (obj != null)
        } // if (patternStr != null && (patternStr.length () > 0) && ...
        return pattern;
    } // resolvePatterns


    /**************************************************************************
     * ???. <BR/>
     *
     * @param   response    ???.
     */
    public void restoreExternalIDs (Response response)
    {
        ObjectFactory objectFactory = null;
        MultipartElement multipartElement = null;
        String idDomain = "";
        String id = "";
        String oidStr = "";

        // did we get a multipart response?
        if (response != null && response.getIsMultipartResponse ())
        {
            // get a ObjectFactory
            objectFactory = this.getObjectFactory ();
            // loop through the entries in the response and generate the
            // key mappings
            for (int i = 0; i < response.p_responseElements.size (); i++)
            {
                multipartElement = response.p_responseElements.elementAt (i);
                // check if the entry was a successful operation
                if (multipartElement.p_responseType == DIConstants.RESPONSE_SUCCESS)
                {
                    // get the values for the key mapping
                    oidStr = multipartElement.p_objectReference;
                    idDomain = multipartElement.getValue ("idDomain");
                    id = multipartElement.getValue ("id");
                    // check if we got a valid object reference and a key
                    if (oidStr != null && id != null && (oidStr.length () > 0) && (id.length () > 0))
                    {
                        try
                        {
                            // create the key mapping
                            objectFactory.createKeyMapper (new OID (oidStr), id, idDomain);
                        } // try
                        catch (IncorrectOidException e)
                        {
                            // this error should not occur
                            // if it does display it:
                            IOHelpers.showMessage (e,
                                this.app, this.sess, this.env, true);
                        } // catch (IncorrectOidException e)
                    } // if (oidStr != null && id != null && (oidStr.length () > 0) && (id.length () > 0))
                } // if (multipartElement.p_responseType == DIConstants.RESPONSE_SUCCESS)
                else if (multipartElement.p_responseType == DIConstants.RESPONSE_ERROR)
                {
                    // check if we have a log
                    if (this.log != null)
                    {
                        this.log.add (DIConstants.LOG_ERROR,
                                "(" + multipartElement.p_errorCode + ")" +
                                multipartElement.p_errorMessage);
                    } // if (this.log != null)
                } // else
            } // for (int i = 0; i < response.p_responseElements.size (); i++)
        } // if (response != null)
    } // restoreExternalIDs


    /**************************************************************************
     * Set the key mapping in the dataElement of an object for the export.<BR/>
     *
     * @param obj           the business object
     * @param dataElement   the dataElement of the business object
     */
    private void setKeyMapping (BusinessObject obj, DataElement dataElement)
    {
        // reset the keymapping in the data element
        dataElement.id = "";
        dataElement.idDomain = "";

        // set key mapping only for non tab objects
        if (!dataElement.p_isTabObject)
        {
            // check if an ID or DOMAIN pattern has been set
            if (this.p_idPattern != null || this.p_domainPattern != null)
            {
                if (this.p_idPattern != null)
                {
                    dataElement.id = this.resolvePatterns (this.p_idPattern,
                        obj);
                } // if
                if (this.p_domainPattern != null)
                {
                    dataElement.idDomain = this.resolvePatterns (
                        this.p_domainPattern, obj);
                } // if
            } // if (this.p_IDPattern != null || this.p_DOMAINPattern != null)
            else    // no pattern set
            {
                // should the stored keymapping be resolved
                if (this.p_isResolveKeyMapping)
                {
                    // try to resolve the keymapping
                    if (!this.resolveKeyMapping (dataElement))
                    {
                        // show log entry object with OID not found
                        this.log.add (DIConstants.LOG_WARNING,  
                            MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                                DIMessages.ML_MSG_EXTKEY_ERROR,
                                new String[] {obj.name}, env));
                    } // if keymapping failed
                } // if (this.p_isResolveKeyMapping)
            } // else no pattern set
        } // if the object is not a tab
    } // setKeyMapping

} // class ExportIntegrator
