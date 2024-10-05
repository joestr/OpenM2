/*
 * Class: XMLViewer_01.java
 */

// package:
package ibs.di;

// imports:
import ibs.app.AppConstants;
import ibs.app.AppFunctions;
import ibs.bo.BOArguments;
import ibs.bo.BOConstants;
import ibs.bo.BOHelpers;
import ibs.bo.BOMessages;
import ibs.bo.BOPathConstants;
import ibs.bo.BusinessObject;
import ibs.bo.Buttons;
import ibs.bo.ContainerElement;
import ibs.bo.IncorrectOidException;
import ibs.bo.OID;
import ibs.bo.ObjectClassNotFoundException;
import ibs.bo.ObjectInitializeException;
import ibs.bo.ObjectNotFoundException;
import ibs.bo.Operations;
import ibs.bo.SelectionList;
import ibs.bo.SingleSelectionContainerElement_01;
import ibs.bo.States;
import ibs.bo.path.ObjectPathNode;
import ibs.bo.tab.Tab;
import ibs.bo.type.TypeConstants;
import ibs.bo.type.TypeNotFoundException;
import ibs.di.imp.ImportIntegrator;
import ibs.di.service.DBMapper;
import ibs.di.service.DBMappingException;
import ibs.io.Environment;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.query.QueryConstants;
import ibs.obj.query.QueryExecutive;
import ibs.obj.search.ObjectSearchContainer_01;
import ibs.obj.webdav.WebdavData;
import ibs.service.Counter;
import ibs.service.action.ActionException;
import ibs.service.action.Variables;
import ibs.service.conf.ConfConstants;
import ibs.service.conf.ConfHelpers;
import ibs.service.conf.Configuration;
import ibs.service.user.User;
import ibs.service.workflow.WorkflowInstanceInformation;
import ibs.tech.html.FormElement;
import ibs.tech.html.GroupElement;
import ibs.tech.html.InputElement;
import ibs.tech.html.SelectElement;
import ibs.tech.html.TextElement;
import ibs.tech.http.HttpArguments;
import ibs.tech.sql.DBError;
import ibs.tech.sql.DBQueryException;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.SelectQuery;
import ibs.tech.sql.StoredProcedure;
import ibs.tech.sql.StoredProcedureConstants;
import ibs.tech.xml.DOMHandler;
import ibs.tech.xml.XMLWriter;
import ibs.tech.xml.XMLWriterException;
import ibs.tech.xslt.XSLTTransformationException;
import ibs.tech.xslt.XSLTTransformer;
import ibs.util.DateTimeHelpers;
import ibs.util.Helpers;
import ibs.util.NameAlreadyGivenException;
import ibs.util.NoAccessException;
import ibs.util.StringHelpers;
import ibs.util.UtilConstants;
import ibs.util.UtilExceptions;
import ibs.util.crypto.EncryptionManager;
import ibs.util.file.FileHelpers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/******************************************************************************
 * The XMLViewer Object presents itself as an businessobject with an XML
 * file attached that stores the data. The XMLViewer encapsulates a set of
 * access methods to the xml datafile and supports a variety of different
 * datatypes to present the fields defined in the data file to the user. <BR/>
 *
 * @version     $Id: XMLViewer_01.java,v 1.208 2013/01/17 15:21:29 btatzmann Exp $
 *
 * @author      Buchegger Bernd (BB), 981216
 ******************************************************************************
 */
public class XMLViewer_01 extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: XMLViewer_01.java,v 1.208 2013/01/17 15:21:29 btatzmann Exp $";


    /**
     * The data element of the object. <BR/>
     * A data element holds the attributes of the object.
     */
    public DataElement dataElement = null;

    /**
     * The document template object. <BR/>
     */
    protected DocumentTemplate_01 p_templateObj = null;

    /**
     * The display mode for the system section of the object. <BR/>
     */
    protected int systemDisplayMode = 0;

    /**
     * This flag is to show the DOM Tree of forms. <BR/>
     */
    public boolean isShowDOMTree = false;

    /**
     * The oid of the workflow template object. <BR/>
     */
    public OID workflowTemplateOid = null;

    /**
     * The path for the files. <BR/>
     */
    public String path = "";

    /**
     * The string with all the allowed typenames. <BR/>
     */
    public String searchTypes = "";

    /**
     * A flag for recursive search. <BR/>
     */
    public boolean searchRecursive = false;

    /**
     * The OID of the container to start the search within. <BR/>
     */
    public OID searchStart = null;

    // Prototype methods for layout generation with xsl.
    /**
     * view mode for xsl: show
     */
    public static final int VIEWMODE_SHOW       = 0;
    /**
     * view mode for xsl: edit
     */
    public static final int VIEWMODE_EDIT       = 1;
    /**
     * view mode for xsl: transform
     */
    public static final int VIEWMODE_TRANSFORM  = 2;
    /**
     * view mode for xsl: xpath
     */
    public static final int VIEWMODE_XPATH      = 3;

    /**
     * path for xslt translators
     */
    private static String TRANSFORM_XSLT_PATH   =
        BOPathConstants.PATH_APP + BOPathConstants.PATH_TRANS;

    /**
     * name of generic stylesheet for viewing
     */
    private static String GENERIC_VIEW_STYLESHEET = "general/genericview.xsl";
    /**
     * name of generic stylesheet for editing
     */
    private static String GENERIC_EDIT_STYLESHEET = "general/genericedit.xsl";

    /**
     * This flag is true when the object is created during the import process. <BR/>
     */
    private boolean p_isObjectImport = false;

    /**
     * Was the configuration file already loaded?. <BR/>
     */
    private boolean p_isConfigLoaded = false;

    /**
     * Tokens which are get out of the name value of the configuration. <BR/>
     * The array consists of alternating literals and field names. It always
     * starts with a literal. <BR/>
     * So the array should look like:
     * <CODE>{literal, fieldname, literal, ....}</CODE>. <BR/>
     * A field name must be a valid name of a VALUE field of the object type. <BR/>
     * The property is <CODE>null</CODE> if there where no configuration value
     * for the name set.
     */
    private String[] p_configNameTokens = null;


    /**
     * Option to set the current display mode
     */
    private int p_viewMode = -1;


    /**
     * Flag to indicate that the object has already been created.
     */
    private boolean p_isAlreadyCreated = false;



    /**************************************************************************
     * This constructor creates a new instance of the class XMLViewer_01. <BR/>
     */
    public XMLViewer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();
    } // XMLViewer_01


    /**************************************************************************
     * This constructor creates a new instance of the class XMLViewer_01. <BR/>
     * The compound object id is used as base for getting the
     * <A HREF="#server">server</A>, <A HREF="#type">type</A>, and
     * <A HREF="#id">id</A> of the business object. These values are stored in the
     * special public attributes of this type. <BR/>
     * The <A HREF="#user">user object</A> is also stored in a specific attribute
     * of this object to make sure that the user's context can be used for getting
     * his/her rights.
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    @Deprecated
    public XMLViewer_01 (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);
    } // XMLViewer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set the instance's attributes:
        // set extended search flag:
        this.searchExtended = false;

        this.procCreate = "p_XMLViewer_01$create";
        this.procChange = "p_XMLViewer_01$change";
        this.procDelete = "p_XMLViewer_01$delete";
        this.procRetrieve = "p_XMLViewer_01$retrieve";

        // set db table name:
        this.tableName = "ibs_XMLViewer_01";

        // set number of parameters for procedure calls:
        this.specificChangeParameters = 2;
        this.specificRetrieveParameters = 5;

        // set relevant attributes for second frame:
        this.frm2Function = AppFunctions.FCT_SHOWOBJECTCONTENT;
    } // initClassSpecifics


    /**************************************************************************
     * Sets the p_isObjectImport to true. <BR/>
     * This is needed during the import process to avoid the initialization
     * of the form with the template defaults.
     */
    protected void setIsObjectImport ()
    {
        this.p_isObjectImport = true;
    } // setIsObjectImport


    /**************************************************************************
     * Get the p_viewMode property.<BR/>
     *
     * @return the p_viewMode
     */
    public int getViewMode ()
    {
        return this.p_viewMode;
    } // getViewMode


    /**************************************************************************
     * Set the p_viewMode property.<BR/>
     *
     * @param   mode    The displayMode to be set.
     */
    public void setViewMode (int mode)
    {
        this.p_viewMode = mode;
    } // setViewMode


    /**************************************************************************
     * Set the data for the additional (type specific) parameters for
     * performChangeData. <BR/>
     * This method must be overwritten by all subclasses that have to pass
     * type specific data to the change data stored procedure.
     *
     * @param sp        The stored procedure to add the change parameters to.
     */
    @Override
    protected void setSpecificChangeParameters (StoredProcedure sp)
    {
        // set specific parameters of the super class
        super.setSpecificChangeParameters (sp);

//this.env.write ("docObj.p_templateObj = " + this.p_templateObj + "\n");

        // add specific parameters:
        // oid of the template
        BOHelpers.addInParameter (sp, this.p_templateObj.oid);
        // oid of the workflow template
        BOHelpers.addInParameter (sp, this.workflowTemplateOid);
    } // setSpecificChangeParameters


    /**************************************************************************
     * Set the data for the additional (type specific) parameters for
     * performRetrieveData. <BR/>
     * This method must be overwritten by all subclasses that have to pass
     * type specific data to the retrieve data stored procedure.
     *
     * @param sp        The stored procedure the specific retrieve parameters
     *                  should be added to.
     * @param params    Array of parameters the specific retrieve parameters
     *                  have to be added to for beeing able to retrieve the
     *                  results within getSpecificRetrieveParameters.
     * @param lastIndex The index to the last element used in params thus far.
     *
     * @return  The index of the last element used in params.
     */
    @Override
    protected int setSpecificRetrieveParameters (StoredProcedure sp, Parameter[] params,
                                                 int lastIndex)
    {
        int i = super.setSpecificRetrieveParameters (sp, params, lastIndex);

        // set the specific parameters:

        // the template oid:
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // the workflow template oid:
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // display mode for the system section
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);
        // the flag if the form is db-mapped
        // TODO KR 20090713: This is not longer necessary because all objects
        //                   must be db-mapped.
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_BOOLEAN);
        // the flag if the DOM tree should be shown
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_BOOLEAN);

        return i;                       // return the current index
    } // setSpecificRetrieveParameters

    /**************************************************************************
     * Sets the given field to the given value. This method is commonly is in
     * workflows.
     *
     * @param   param       standard vector that contains the field and the
     *                         value to set
     *
     * @return      result vector with holds the result code (1. element)
     *              and the result message (2. element) as strings.
     */
    public Vector<String> setFieldValue (Vector<String> param)
    {
        String resultMsg = "";
        String resultCode = "0";

        // the 1. element in the result vector holds the field.
        // the 2. element in the result vector holds the value.

        // get the field and the value out of the param vector
        String field = param.elementAt (0);
        String value = param.elementAt (1);

        try
        {
            // add the closed date and the user who closed the object
            this.dataElement.changeValue (field, value);

            // set the new value
            this.performChange (Operations.OP_NONE);
            // clear the error code and msg
            resultCode = "0";
            resultMsg = "";
        } // try
        catch (NoAccessException e)
        {
            resultMsg = e.getMessage ();
        } // catch (NoAccessException e)
        catch (NameAlreadyGivenException e)
        {
            resultMsg = e.getMessage ();
        } // catch (NoAccessException e)
        catch (Throwable e)
        {
            resultMsg = Helpers.getStackTraceFromThrowable (e);
            resultCode   = "-1";
        } // catch (Throwable e)

        // set the result vector
        Vector<String> result = new Vector<String> ();
        result.addElement (resultCode);
        result.addElement (resultMsg);
        return result;
    } // setFieldValue


    /**************************************************************************
     * Get the data for the additional (type specific) parameters for
     * performRetrieveData. <BR/>
     * This method must be overwritten by all subclasses that have to get
     * type specific data from the retrieve data stored procedure.
     *
     * @param params    The array of parameters from the retrieve data stored
     *                  procedure.
     * @param lastIndex The index to the last element used in params thus far.
     */
    protected void getSpecificRetrieveParameters (Parameter[] params,
                                                  int lastIndex)
    {
        super.getSpecificRetrieveParameters (params, lastIndex);

        int i = lastIndex;

        // get the specific parameters:
        // ignore the template oid:
        ++i;
        this.workflowTemplateOid = SQLHelpers.getSpOidParam (params[++i]);
        this.systemDisplayMode = params[++i].getValueInteger ();
        // ignore parameter isDBMapped because this is not longer relevant:
        // instead store the is already created flag (at position of is db mapped)
        ++i;
        this.isShowDOMTree = params[++i].getValueBoolean ();
/*
        // some debug information
        if (this.p_templateObj != null)
        {
            showDebug ("Retrieve: templateOID=" + this.p_templateObj.oid.toString ());
            showDebug ("Retrieve: workflowOID=" + this.workflowTemplateOid.toString ());
            showDebug ("Retrieve: systemDisplayMode =" + this.systemDisplayMode);
            showDebug ("Retrieve: dbMapped =" + this.isDBMapped);
            showDebug ("Retrieve: showDOMTree =" + this.isShowDOMTree);
        } // if
*/
    } // getSpecificRetrieveParameters


    /**************************************************************************
     * Initializes a BusinessObject object. <BR/>
     * The compound object id is stored in the <A HREF="#oid">oid</A> property
     * of this object. <BR/>
     * The <A HREF="#user">user object</A> is also stored in a specific
     * property of this object to make sure that the user's context can be used
     * for getting his/her rights. <BR/>
     * <A HREF="#env">env</A> is initialized to the provided object. <BR/>
     * <A HREF="#sess">sess</A> is initialized to the provided object. <BR/>
     * <A HREF="#app">app</A> is initialized to the provided object. <BR/>
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     * @param   env     The actual environment.
     * @param   sess    The session info object.
     * @param   app     The application info object.
     */
    public void initObject (OID oid, User user, Environment env,
                            SessionInfo sess, ApplicationInfo app)
    {
        super.initObject (oid, user, env, sess, app);

        // url encode the search types
        String typeEncoded = "";
        // TODO: KR 20090715 the following condition can never be true.
        //       Thus this must be a mistake.
        if ((this.searchTypes != null) && (this.searchTypes.length () == 0) &&
            (this.searchTypes.equalsIgnoreCase (" ")))
        {
            typeEncoded = IOHelpers.urlEncode (this.searchTypes);
        } // if

        // set Url for search frame (used for OBJECTREF fields)
        // note that the url must be constructed in advance
        this.frm2Url = this.getBaseUrl () +
            HttpArguments.createArg (BOArguments.ARG_FUNCTION, AppFunctions.FCT_SHOWOBJECTCONTENT) +
            HttpArguments.createArg (BOArguments.ARG_OID, (new OID (this
                .getTypeCache ().getTVersionId (TypeConstants.TC_ObjectSearchContainer), 0)).toString ()) +
            HttpArguments.createArg (BOArguments.ARG_CALLINGOID, "" + this.oid) +
            HttpArguments.createArg (BOArguments.ARG_TYPE, typeEncoded) +
            HttpArguments.createArg (BOArguments.ARG_RECURSIVE, this.searchRecursive) +
            HttpArguments.createArg (BOArguments.ARG_CONTAINERID, "" + this.searchStart) +
            HttpArguments.createArg (BOArguments.ARG_SHOWLINK, BOConstants.SHOWLINKEDOBJECT) +
            HttpArguments.createArg (BOArguments.ARG_FIELDNAME, BOArguments.ARG_NAME);

        // if the form contains an OBJECTREF or FIELDREF field
        // the frame set must be shown for the edit form:
        if (this.typeObj != null)
        {
            // Set the document template
            this.setDocumentTemplate (this.getDocumentTemplate ());

            // Retrieve the data element
            DataElement dm = this.getDataElement ();

            if (dm != null)
            {
                for (Iterator<ValueDataElement> iter = dm.values.iterator (); iter.hasNext ();)
                {
                    ValueDataElement vde = iter.next ();

                    if (vde.type.equalsIgnoreCase (DIConstants.VTYPE_OBJECTREF) ||
                        vde.type.equalsIgnoreCase (DIConstants.VTYPE_FIELDREF))
                    {
                        // if the form contains an OBJECTREF of FIELDREF field
                        // the frame set must be shown for the edit form
                        this.showChangeFormAsFrameset = true;
                        this.frm1Size = "*";
                        this.frm2Size = "0";
                        break;
                    } // if OBJECTREF or FIELDREF value
                } // for iter
            } // if dataElement valid
        } // if type object valid
    } // initObject


    /**************************************************************************
     * Gets the paths and oids of the xml and workflow template files
     * and stores it in instance variables (templateOid, templatePath and
     * workflowTemplatePath). <BR/>
     * This information is obtained from the container object (XMLViewerContainer_01).
     */
    protected void retrieveTemplateInformation ()
    {
        // first clear the affected instance variables:
        this.workflowTemplateOid = null;
//        this.isShowDOMTree = false;

        // check if the container is an XMLViewerContainer
        // NEW: or a service-point
        if (this.containerId.tVersionId ==
                this.getTypeCache ().getTVersionId (TypeConstants.TC_XMLViewerContainer) ||
///////////////////////////
// SERVICE POINT HACK: START
            this.containerId.tVersionId ==
                this.getTypeCache ().getTVersionId (TypeConstants.TC_ServicePoint))
//
// SERVICE POINT HACK: END
///////////////////////////
        {
            // instantiate the container object:
/*
            XMLViewerContainer_01 xvc = new XMLViewerContainer_01 ();
            xvc.initObject (this.containerId, this.user, this.env, this.sess, this.app);
*/
            XMLViewerContainer_01 xvc = (XMLViewerContainer_01) BOHelpers
                .getObject (this.containerId, this.env, false, false, false);

            // check if we got the object:
            if (xvc != null)
            {
                // if the container has a valid form template assigned
                // get all informations about the form template:
                if (xvc.p_templateObj != null)
                {
                    // set the template object:
                    this.p_templateObj = xvc.p_templateObj;
                } // if

                // get the workflow oid from the container
                this.workflowTemplateOid = xvc.workflowTemplateOid;
            } // if
        } // if
    } // retrieveTemplateInformation


    /**************************************************************************
     * Read from the User the data used in the Object. <BR/>
     */
    public void getParameters ()
    {
        // get common parameters:
        super.getParameters ();

        // get the parameters to be set in the data element:
        this.getDataElementParameters ();
    } // getParameters


    /**************************************************************************
     * Read the data element values from the user input. <BR/>
     */
    public void getDataElementParameters ()
    {
        ValueDataElement vie;
        // flag indicating if at least one of the ValueDataElements in the list
    	// contains an attached file
        boolean hasFile = false;

        // check if we got an dataElement that holds the data
        if (this.dataElement != null)
        {
            // loop through the values:
            for (Iterator<ValueDataElement> iter = this.dataElement.values.iterator (); iter.hasNext ();)
            {
                vie = iter.next ();

                // get the argument name of the field
                String fieldArgument = this.createArgument (vie.field);
                
                // call type-specific version of the method:
                ValueDataElementTS.getParameters (vie, this.adoptArgName (fieldArgument), this.app, this.sess,
                        this.env, this.getPath (), this.getViewMode ());
                
                // cache the flag value, if the ValueDataElement has an attached file
                if (!hasFile)
                {
                	hasFile = vie.getFileFlag();
                } // if (!hasFile)

            } // for iter
            
            // store the cached flag value
       		this.setFileFlag(hasFile, true);
        } // if
    } // getDataElementParameters


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in an
     * object's info view. <BR/>
     * This method can be overwritten in subclasses to redefine the set of
     * buttons that can be displayed. <BR/>
     *
     * @return  An array with button ids that can potentially be displayed.
     */
    protected int[] setInfoButtons ()
    {
        int[] buttons = this.typeObj.getInfoButtons ();

        // check if there are some buttons set:
        if (buttons == null)
        {
            // set common buttons:
            buttons = new int[]
            {
                Buttons.BTN_EDIT,
                Buttons.BTN_DELETE,
                Buttons.BTN_CUT,
                Buttons.BTN_COPY,
                Buttons.BTN_DISTRIBUTE,
                Buttons.BTN_STARTWORKFLOW,
                Buttons.BTN_FORWARD,
                Buttons.BTN_FINISHWORKFLOW,
//                Buttons.BTN_CHECKOUT,
//                Buttons.BTN_CHECKIN,
            }; // buttons
        } // if

        // return button array:
        return buttons;
    } // setInfoButtons


    /**************************************************************************
     * Is the object type allowed in workflows? <BR/>
     * This method shall be overwritten in subclasses.
     *
     * @return  <CODE>true</CODE> if the object type is allowed in workflows,
     *          <CODE>false</CODE> otherwise.
     */
    protected boolean isWfAllowed ()
    {
        return true;
    } // isWfAllowed


    /**************************************************************************
     * Create a selection box containing the templates the user is allowed to
     * see. <BR/>
     *
     * @param   fieldname   The name of the field used for displaying the
     *                      selection box.
     *
     * @return  The layout group element with the selection box.
     */
    protected GroupElement createTemplatesSelectionBox (String fieldname)
    {
        GroupElement gel = new GroupElement ();
        SQLAction action = null;
        StringBuffer queryStr = new StringBuffer (); // the query
        try
        {
            action = this.getDBConnection ();

            // get the elements out of the database:
            // create the SQL String to select all tuples
            queryStr
                .append (" SELECT o.oid, o.name")
                .append (" FROM v_Container$rights o, ibs_DocumentTemplate_01 t")
                .append (" WHERE o.userId = ").append (this.user.id)
                .append (SQLHelpers.getStringCheckRights (Operations.OP_VIEW))
                .append (" AND o.oid = t.oid AND t.typeId = 0");

            int rowCount = action.execute (queryStr, false);

            // not empty result set ?
            if (rowCount > 0)
            {
                SelectElement sel = new SelectElement (fieldname, false);
                sel.size = 1;
                // get tuples out of db
                while (!action.getEOF ())
                {
                    // add the name and the oid of the document template
                    sel.addOption (action.getString ("name"), action.getString ("oid"));
                    // step one tuple ahead for the next loop
                    action.next ();
                } // while
                gel.addElement (sel);
            } // if
            else
            {
                gel.addElement (new TextElement ( 
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_NOTEMPLATESAVAILABLE, env)));
            } // else if

            // end transaction
            action.end ();
        } // try
        catch (DBError e)
        {
            // an error occurred - show name and info
            gel.addElement (new TextElement (e.getMessage () + e.getError ()));
        } // catch
        finally
        {
            this.releaseDBConnection (action);
        } // finally

        return gel;
    } // createTemplatesSelectionBox


    /**************************************************************************
     * Creates the data of the object from a document template file. <BR/>
     * If the creation of the data was successful the data element is created
     * and the db-mapping is updated.
     *
     * @param   templateObj         The template object.
     */
    protected void createFromTemplate (DocumentTemplate_01 templateObj)
    {
        // check if the data element structure was not not yet initialized:
        if (this.dataElement == null)
        {
            // initialize the structure and store it within this.dataElement:
            this.getDataElement ();
        } // if

        // check if data element was initialized:
        if (this.dataElement != null)
        {
            // set default values for the value data elements where no values exist:


            // when a form object is created the retrieve method is called
            // twice. After the first retrieve the basic default
            // values for the system attributes are set.
            // if this is the second retrieve we have to set the form defaults
            // to overwrite the basic defaults.
            //
            // The form defaults are set selectively depending if we are
            // currently in import or not.
            //
            this.performSetFormDefaults (this.p_isObjectImport);

            // we have to update the attributes in the mapper table:
            try
            {
                // insert the new object in the mapping table:
                this.performObjectMapping (true);
            } // try
            catch (NoDataElementException e)
            {
                IOHelpers.showMessage ("db-mapping error!",
                    e, this.env, true);
            } // catch
            catch (NoTemplateException e)
            {
                IOHelpers.showMessage ("db-mapping error!",
                    e, this.env, true);
            } // catch
            catch (DBMappingException e)
            {
                IOHelpers.showMessage ("db-mapping error!",
                    e, this.env, true);
            } // catch
        } // if
    } // createFromTemplate


    /**************************************************************************
     * Get the data element. <BR/>
     * If the dataElement is <CODE>null</CODE> the method tries to get the
     * information out of the data store.
     *
     * @return  The dataElement for the object.
     */
    protected DataElement getDataElement ()
    {
        // check if we already have a data element
        if (this.dataElement == null)
        {
            // create the data element from the template:
            this.dataElement = (DataElement) this.getDocumentTemplate ().getTemplateDataElement ().clone ();
/* KR 20090901 at this position the object should not be recreated from the template.
            this.createFromTemplate (this.getDocumentTemplate ());
*/
        } // if

        // return the data element:
        return this.dataElement;
    } // getDataElement


    /**************************************************************************
     * Creates a xml document out of an dataElement and writes it to a
     * destination path. <BR/>
     * This method is used by the XMLViewer Object.
     *
     * @param dataElement   the dataElement to create the xml document from
     * @param fileName      the file name
     * @param path          the destination path to write the file to
     *
     * @return  <CODE>true</CODE> if everything was o.k., <CODE>false</CODE>
     *          otherwise.
     *
     * @deprecated  KR 20090823 Use
     *              {@link DIHelpers#writeDataFile(DataElement, String, String, Environment)}
     *              instead.
     */
    @Deprecated
    public boolean writeDataFile (DataElement dataElement,
                                  String fileName, String path)
    {
        // call common method and return the result:
        return DIHelpers.writeDataFile (dataElement, this.getDocumentTemplate (),
            fileName, path, this.env);
    } // writeDataFile


    /**************************************************************************
     * Reads from an XML Viewer Data file and creates a dataElement
     * from the data read. <BR/>
     * The difference between a XMLViewer data file and a template data files
     * is that template files can hold additional attributes for the db-mapping.
     * ATTENTION! This method IGNORES the mapping attributes in the xml file!
     *
     * @param   path        File path to read from.
     * @param   fileName    File name of the file to read from.
     *
     * @return  A DataElement object that holds the data or
     *          <CODE>null</CODE> if the file could not be processed.
     *
     * @deprecated  KR 20090823 Use
     *              {@link DIHelpers#readDataFile(String, String, Environment)} instead.
     */
    @Deprecated
    public DataElement readDataFile (String path, String fileName)
    {
        // call common method and return the result:
        return DIHelpers.readDataFile (path, fileName, this.env);
    } // readDataFile


    /**************************************************************************
     * Reads from an Document template Data file and creates a dataElement
     * from the data read. <BR/>
     * The difference between a XMLViewer data file and a template data files
     * is that template file can hold additional attributes for the db-mapping.
     * This method read also this attributes.
     *
     * @param   path        File path to read from.
     * @param   fileName    File name of the file to read from.
     *
     * @return  A DataElement object that holds the data or
     *          <CODE>null</CODE> if the file could not be processed.
     *
     * @deprecated  KR 20090823 Use
     *              {@link DIHelpers#readTemplateDataFile(String, String, Environment)}
     *              instead.
     */
    @Deprecated
    public DataElement readTemplateDataFile (String path, String fileName)
    {
        // call common method and return the result:
        return DIHelpers.readTemplateDataFile (path, fileName, this.env);
    } // readTemplateDataFile


    /**************************************************************************
     * Gets from Documenttemplate the dataElement
     * for a specific TABOBJECT tag. <BR/>
     *
     * @param tabId             id of tab to get DataElement from
     *
     * @return  an dataElement object that holds the data or null if file
     *          could not be processed
     */
    public DataElement getTabData (int tabId)
    {
//debug ("XMLViewer_01.getTabData (" + tabId + ")");
        // get the common tab data:
        Tab neededData = this.getTabCache ().get (tabId);

        if (neededData != null)
        {

            DocumentTemplate_01 dt = this.getDocumentTemplate ();
            DataElement de = dt.getTemplateDataElement ();

            DataElementList tabList = de.tabElementList;
            Vector<DataElement> tabs = tabList.dataElements;

            DataElement currentTab = null;

            for (int i = 0; i < tabs.size (); i++)
            {
                currentTab = tabs.elementAt (i);

                if (neededData.getCode ().equals (currentTab.p_tabCode))
                {
                    return currentTab;
                } // if
            } // for
        } // if

        return null;
    } // getTabData


    /**************************************************************************
     * Constructs the full path for the XMLViewers XML file. <BR/>
     *
     * @param   fileName  the name of the XMLViewer file.
     *
     * @return the full file path for the XMLViewer XML file
     */
    public String getFilePath (String fileName)
    {
        return this.getPath () + fileName;
    } // getFilePath


    /***************************************************************************
     * Constructs the path for data files. <BR/>
     *
     * @return the file path for the XMLViewer XML file
     *
     * @deprecated KR 20090715 Use
     *             {@link BOHelpers#getFilePath(OID) BOHelpers#getFilePath}
     *             instead.
     */
    @Deprecated
    public String getPath ()
    {
        // call common method:
        return BOHelpers.getFilePath (this.oid);
    } // getPath


    /***************************************************************************
     * Constructs the path for image files. <BR/>
     *
     * @return the file path for the XMLViewer XML file
     *
     * @deprecated KR 20090715 Use
     *             {@link BOHelpers#getImagePath(OID) BOHelpers#getImagePath}
     *             instead.
     */
    @Deprecated
    protected String getImagePath ()
    {
        // call common method and return result:
        return BOHelpers.getImagePath (this.oid);
    } // getImagePath


    /**************************************************************************
     * Reads the object data from an dataElement. <BR/>
     *
     * @param dataElement     the dataElement to read the data from
     */
    public void readImportData (DataElement dataElement)
    {
    	ValueDataElement vie = null;
    	
    	// flag indicating if at least one of the ValueDataElements in the list
    	// contains an attached file
    	boolean hasFile = false;
    	
        // import the system values of the business object
        super.readImportData (dataElement);

        // import the object type specific values:
        // loop through the imported values
        for (Iterator<ValueDataElement> iter = dataElement.values.iterator (); iter.hasNext ();)
        {
            // get the value from the imported dataelement
        	vie = iter.next ();
            ValueDataElementTS.readValueImportData (vie,
                this.dataElement, this.app, this.sess, this.user, this.env);
            
            if (!hasFile)
            {
            	hasFile = vie.getFileFlag();
            } // if (!hasFile)
        } // for iter

        // store the cached flag value
   		this.setFileFlag(hasFile, true);
        
        // TODO BB: this is a hack
        // The files vector is now written into the dataElement of the object instance
        // but it must be set in the dateElement parameter that is used for the 
        // ibs.di.ObjectFactory.readImportFiles () method
        // This was a change within the XMLData-removal issue
        dataElement.files = this.dataElement.files;
        
        // add the order attachments
        if (dataElement.attachmentList != null &&
            !dataElement.attachmentList.isEmpty ())
                                            // is there an attachment ?
        {
            this.dataElement.attachmentList = dataElement.attachmentList;
        } // if is there an attachment
    } // readImportData


    /**************************************************************************
     * Creates the files from the dataElement. <BR/>
     *
     * @param dataElement     the dataElement to read the data from
     *
     * @see ibs.bo.BusinessObject#readImportData
     */
    public void readImportFiles (DataElement dataElement)
    {
        // import the object type specific values:
        // loop through the imported values
        for (Iterator<ValueDataElement> iter = dataElement.values.iterator (); iter.hasNext ();)
        {
            // get the value from the imported dataelement
            ValueDataElement importValue = iter.next ();
            // get the corresponding value from the object dataelement
            ValueDataElement objectValue =
                this.dataElement.getValueElement (importValue.field);

            // get the type of the imported value
            String importType = importValue.type;

            // check if the value was found and the type is correct
            if (objectValue != null &&
                objectValue.type.equalsIgnoreCase (importType))
            {
                // look for values of type FILE or IMAGE
                if (importType.equalsIgnoreCase (DIConstants.VTYPE_FILE) ||
                    importType.equalsIgnoreCase (DIConstants.VTYPE_IMAGE))
                {
                    // set the filename again because it could have been changed
                    objectValue.value = dataElement.getFileName (importValue.field);
                    objectValue.p_size = dataElement.getFileSize (importValue.field);
                } // if value of type FILE or IMAGE
            } // if value found
        } // for iter
    } // readImportFiles


    /**************************************************************************
     * writes the object data to an dataElement. <BR/>
     *
     * @param dataElement     the dataElement to write the data to
     */
    public void writeExportData (DataElement dataElement)
    {
        String tmpValue = null;
        String origValue = null;

        // set the business object specific values
        super.writeExportData (dataElement);

        // copy the values of the dataElement into the dataElement
        if (this.dataElement != null)
        {
             // set the type
            dataElement.typename = this.dataElement.typename;
            // check if we have any values
            if (dataElement.values != null)
            {
                ValueDataElement value;

                // now go through the values, copy them and look for files to
                // export:
                for (Iterator<ValueDataElement> iter = this.dataElement.values.iterator ();
                     iter.hasNext ();)
                {
                    value = iter.next ();

                    origValue = value.value;
                    tmpValue = origValue;
                    // field of type VTYPE_PASSWORD:
                    if (value.type.equalsIgnoreCase (DIConstants.VTYPE_PASSWORD))
                    {
                        // decode the password:
                        tmpValue = EncryptionManager.encrypt (tmpValue);
                        value.value = tmpValue;
                    } // if field of type VTYPE_PASSWORD

                    // make a complete copy of the value element
                    dataElement.addValue (new ValueDataElement (value));
/*
                    dataElement.addValue (value.field, value.type, tmpValue,
                        value.mandatory, value.info, value.typeFilter,
                        value.searchRoot, value.searchRootIdDomain,
                        value.searchRootId, value.searchRecursive,
                        value.mappingField, value.queryName, value.options,
                        value.p_valueUnit, value.emptyOption, value.refresh,
                        value.p_subTags, value.p_domain, value.p_size);
*/

                    // ensure that the valueElement has the original value:
                    if (origValue != null && !origValue.equals (tmpValue))
                    {
                        value.value = origValue;
                    } // if (tmpValue != null && !tmpValue.equals (origValue))

                    // now get the new valueElement we created
                    // it must be now the last element that has been added
                    ValueDataElement newValue = dataElement.values.lastElement ();
                    // look for values of type file
                    if (newValue.type.equalsIgnoreCase (DIConstants.VTYPE_FILE))
                    {
                        // set the export file value but do not add it again
                        // setExportFileValue checks if the filename is unique
                        // if not the fileName will be changed automatically and
                        // the value will be updated too
                        dataElement.setExportFileValue (newValue.field, newValue.value,
                            this.getPath (), false);
                    } // if vie.type.equals (DIConstants.VTYPE_FILE)
                    else if (newValue.type.equalsIgnoreCase (DIConstants.VTYPE_IMAGE))
                    {
                        dataElement.setExportFileValue (newValue.field, newValue.value,
                            this.getImagePath (), false);
                    } // if vie.type.equals (DIConstants.VTYPE_IMAGE)
                } // for iter
            } // if

            // add the list of attachments:
            dataElement.attachmentList = this.dataElement.attachmentList;
        } // if
    } // writeExportData


    /**************************************************************************
     * Load the form configuration file. <BR/>
     */
    protected void loadConfigFile ()
    {
        if (this.dataElement != null && !this.p_isConfigLoaded)
        {
            // the file path of the form configuration file
            String cfgFile = this.app.p_system.p_m2AbsBasePath +
                BOPathConstants.PATH_APPFORMCFG +
                this.dataElement.p_typeCode + ConfConstants.CONFIGFILE_EXTENSION;

//debug.DebugClient.debugln (cfgFile);
            // check if the config file exists:
            if (FileHelpers.exists (cfgFile)) // config file exists?
            {
                try
                {
                    char[] buffer = new char[4096];
                    // open the config file:
                    FileReader f = new FileReader (cfgFile);
                    // read in the configuration:
                    f.read (buffer);

                    StringTokenizer tokenizer =
                        new StringTokenizer (new String (buffer), "\n");
                    while (tokenizer.hasMoreTokens ())
                    {
                        this.parseConfigLine (tokenizer.nextToken ());
                    } // while

                    f.close ();
                    this.p_isConfigLoaded = true;
                } // try
                catch (FileNotFoundException e)
                {
                    // configuration file does not exist.
                    // this is valid.
                    // nothing has to be done.
                } // catch
                catch (IOException e)
                {
                    IOHelpers.showMessage (
                        "XMLViewer_01.loadConfigFile: Error during accessing config file.",
                        e, this.app, this.sess, this.env, true);
                } // catch
            } // if
        } // if
//debug.DebugClient.debugln ("p_buttonList=" + this.p_buttonList);
//debug.DebugClient.debugln ("p_transformFile=" + this.p_transformFile);
    } // loadConfigFile


    /**************************************************************************
     * Parse a line from the form configuration file. <BR/>
     *
     * @param   line    The line to be parsed.
     *
     * @return  <CODE>true</CODE> if the line was successfully parsed,
     *          <CODE>false</CODE> if the line could not be parsed or is not
     *          valid.
     */
    protected boolean parseConfigLine (String line)
    {
//debug.DebugClient.debugln (line);
        boolean retVal = false;

        // check for comment:
        if (line.startsWith (ConfConstants.CONFIGFILE_COMMENT))
        {
            // nothing to do
            retVal = true;
        } // if
        // get the name line:
        else if (line.startsWith ("Name=") ||
                 line.startsWith ("name="))
        {
            // get the name value and parse it:
            String nameValue = line.substring (5).trim ();
            this.p_configNameTokens = ConfHelpers.parseConfValue (nameValue);
        } // else if

        // return the result:
        return retVal;
    } // parseConfigLine


    /***************************************************************************
     * Set the name of the actual business object. <BR/>
     * This method can be overwritten by classes which set specific names.
     */
    protected void setName ()
    {
        String[] tokens = null;

        // get the tokens from the type:
        if (this.typeObj != null)
        {
            tokens = this.typeObj.getNameTemplateTokens ();
        } // if

        // check if there were a name template set in the document template:
        if (tokens == null)
        {
            // load the configuration:
            this.loadConfigFile ();

            // check if there are some tokens set:
            if (this.p_configNameTokens != null)
            {
                tokens = this.p_configNameTokens;
            } // if
        } // if

        // check if there are some tokens set:
        if (tokens != null)
        {
            StringBuffer name = new StringBuffer ();

            // loop through all tokens and construct the name:
            for (int i = 0; i < tokens.length; i++)
            {
                // always start with a literal:
                name.append (tokens[i++]);

                if (i < tokens.length)
                {
                    // now there comes a field:
                    // try to get the field with the specified name:
                    ValueDataElement val =
                        this.dataElement.getValueElement (tokens[i]);

                    // check if the field exists:
                    if (val != null)
                    {
                        // get the new value of the field and set it as name:
                        name.append (val.value);
                    } // if
                    else
                    {
                        // field not found, use the name as literal:
                        name.append (ConfConstants.CONF_VARIABLEDELIM +
                                     tokens[i] +
                                     ConfConstants.CONF_VARIABLEDELIM);
                    } // else if
                } // if
            } // for i

            // set the resulting name:
            this.name = name.toString ();
        } // if
    } // setName



    /**************************************************************************
     * Set the the "Datum der Übermittlung" field in the object.<BR/>
     * This method is intended to be used by the export mechanism. It can be
     * overwritten in subclasses.
     *
     * @return  <CODE>true</CODE> if setting the date was successful,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean setTransmissionDate ()
    {
        boolean isOk = true;

        try
        {
            // try to set the transmission date:
            if (this.dataElement.changeValue (DIConstants.FLD_TRANSMISSIONDATE,
                DateTimeHelpers.dateTimeToString (new Date ())))
                                        // field exists, value set?
            {
                this.env.write ("<LI/>" +  
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_SET_TRANSMISSION_DATE, env) +
                    " " + this.typeName + " '" + this.name + "' ... ");
                // change the object
                this.performChange (Operations.OP_CHANGE);
                this.env.write ("ok");
            } // if field exists, value set
        } // try
        catch (NoAccessException e) // no access to object allowed
        {
            this.showNoAccessMessage (Operations.OP_CHANGE);
            return false;
        } // catch
        catch (NameAlreadyGivenException e) // no access to object allowed
        {
            this.showNameAlreadyGivenMessage ();
            return false;
        } // catch

        // return the result:
        return isOk;
    } // setTransmissionDate


    /**************************************************************************
     * Set the the "Zur Konvertierung weitergeleitet am" field in the object.
     * <BR/>
     * This method is intended to be used by the export mechanism. It can be
     * overwritten in subclasses.
     *
     * @return  <CODE>true</CODE> if setting the date was successful,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean setConversionDate ()
    {
        boolean isOk = true;
        String report = "";

        try
        {
            // check if the field exists:
            if (this.dataElement.exists (DIConstants.FLD_CONVERSIONDATE))
                                        // found the field?
            {
                this.env.write ("<LI/>" +  
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_SET_CONVERSION_DATE, env) +
                    " " + this.typeName + " '" + this.name + "' ... ");
                // set the conversion report
                report = this.dataElement.getImportStringValue (
                    DIConstants.FLD_CONVERSION_REPORT);
                if (report != null && !report.contentEquals (new StringBuffer ("")))
                {
                    this.dataElement.changeValue (DIConstants.FLD_CONVERSION_REPORT, report +
                        "> " + DateTimeHelpers.dateTimeToString (new Date ()) + ": " +
                        "Verfahrensobjekt zur Konvertierung weitergeleitet.\n");
                } // if

                // set the conversion date:
                if (this.dataElement.changeValue (DIConstants.FLD_CONVERSIONDATE,
                    DateTimeHelpers.dateTimeToString (new Date ())))
                                        // setting the value was successful?
                {
                    // change the object
                    this.performChange (Operations.OP_CHANGE);
                    this.env.write ("ok");
                } // if setting the value was successful
            } // if
        } // try
        catch (NoAccessException e) // no access to object allowed
        {
            this.showNoAccessMessage (Operations.OP_CHANGE);
            return false;
        } // catch
        catch (NameAlreadyGivenException e) // no access to object allowed
        {
            this.showNameAlreadyGivenMessage ();
            return false;
        } // catch

        // return the result:
        return isOk;
    } // setConversionDate


    /***************************************************************************
     * Set the reminders for this object. <BR/>
     *
     * @param   objectOid   The oid of the object.
     * @param   objectName  Name of object. Used for reminder subject.
     * @param   app         The Application Info object.
     * @param   sess        The session info object.
     * @param   env         The current environment.
     * @param   user        The user info.
     */
    protected void setReminders (OID objectOid, String objectName,
                                 ApplicationInfo app, SessionInfo sess,
                                 Environment env, User user)
    {
        // check if we got an dataElement that holds the data
        if (this.dataElement != null)
        {
            // loop through all values and check if it is a reminder:
            for (Iterator<ValueDataElement> iter = this.dataElement.values.iterator ();
                 iter.hasNext ();)
            {
                ValueDataElement elem = iter.next ();

                if (DIConstants.VTYPE_REMINDER.equals (elem.type))
                {
                    // set the reminders for the value:
                    elem.setReminders (objectOid, objectName, app, sess, env,
                        user);
                } // if
            } // for iter
        } // if
    } // setReminders


    /**************************************************************************
     * This method implements the reminder 1 function. <BR/>
     *
     * @param   obj     Object that can carry input parameters.
     *
     * @throws  ReminderException
     *          Standard exception throws in reminder methods
     */
    public void remind (Object obj) throws ReminderException
    {
/*
 * m2ReminderObserverJob job = (m2ReminderObserverJob) obj;
 * m2ReminderObserverJobData jobData = (m2ReminderObserverJobData) job.getJdata
 * (); // get the oid of the condition try { // set subject and content for
 * notification email String subject = "Erinnerung zu " + "???"; String content =
 * DIConstants.STANDARD_EMAIL_HEADER + "<FONT FACE=\"Verdana,Arial,sans-serif\"
 * SIZE=\"5\" COLOR=\"BLACK\">" + "<B>Erinnerungsbenachrichtigung</B></FONT><P>" + "<FONT
 * FACE=\"Verdana,Arial,sans-serif\" SIZE=\"3\">" + "Die " + conditionType + " '" +
 * Helpers.createWeblink (conditionOid, condition.name, this.env) + "' wird
 * fällig am " + this.dataElement.getImportStringValue (FLD_NEXTCHECK) + ".<P></FONT>" +
 * DIConstants.STANDARD_EMAIL_FOOTER; remind (subject, "1. Erinnerung zur
 * Auflage", "Zur Erfüllung", FLD_REMINDDATE1,
 * this.dataElement.getImportStringValue (FLD_REMINDERROLE1),
 * FLD_REMINDERRECEIVER1, ElakConstants.REMINDERTYPE_COND_REMIND, conditionOid,
 * conditionOid, this.dataElement.getImportBooleanValue (FLD_REMIND1FACRESP),
 * this.dataElement.getImportBooleanValue (FLD_REMIND1FACPARTRESP), subject,
 * content); } // try catch (NoAccessException e) { throw new ReminderException
 * (e.toString ()); } // catch (NoAccessException e) catch
 * (ObjectNotFoundException e) { throw new ReminderException (e.toString ()); } //
 * catch (ObjectNotFoundException e) catch (AlreadyDeletedException e) { throw
 * new ReminderException (e.toString ()); } // catch (AlreadyDeletedException e)
 */
    } // remind


    /**************************************************************************
     * This method implementes the reminder function. <BR/>
     *
     * @param subjectText       the text used for the subject
     * @param descriptionText   the text used for the description.
     * @param activityText      the text used for the activity
     * @param dateFieldName     the name of the field to set the datetime for
     * @param role              the recipient role
     * @param receiverFieldName the fieldname of the receiver field
     * @param reminderType      the reminder type
     * @param notifyObjOid      the oid of the object used for the notification
     * @param reminderObjOid    the oid of the object used for the reminder package
     * @param isRemindFacResp   option to remind theh facility responsibles
     * @param isRemindFacPartResp option to remind the facility part responsibles
     * @param emailSubject      the subject of the email notification
     * @param emailContent      the content of the email notification
     *
     * @throws  ReminderException
     *              Standard exception throws in reminder methods
     */
    public void remind (String subjectText,
                        String descriptionText,
                        String activityText,
                        String dateFieldName,
                        String role,
                        String receiverFieldName,
                        String reminderType,
                        OID notifyObjOid,
                        OID reminderObjOid,
                        boolean isRemindFacResp,
                        boolean isRemindFacPartResp,
                        String emailSubject,
                        String emailContent)
        throws ReminderException
    {
/*
        Vector allReceivers = new Vector ();
        Vector receivers = null;
        OID [] facilities = null;
        OID [] facilityParts = null;
        Vector partReceivers = null;
        String orgUnit = "";
        String team = "";
        String position = "";
        String receiver = null;
        OID userOid = null;

        try
        {
            // get the receivers of the reminder:
            // create a CommonHelpers object
            CommonHelpers helpers = new CommonHelpers ();
            helpers.initObject (OID.getEmptyOid (), this.user,
                    this.env, this.sess, this.app);
            orgUnit = CommonHelpers.getOrgUnitFromRole (role);
            team = CommonHelpers.getTeamFromRole (role);
            position = CommonHelpers.getPositionFromRole (role);

            // check if a position has been set
            if (position != null && (position.length () > 0))
                partReceivers = helpers.getStaffUserOidRole (position, team, orgUnit);
            else
                partReceivers = helpers.getStaffUserOidSettings (team, orgUnit);
            // add the result to the receivers vector
            CommonHelpers.concatVector (allReceivers, partReceivers);

            // add the receiver if applicable
            receiver = this.dataElement.getImportStringValue (receiverFieldName);
            if (receiver != null && receiver.length () > 19)
            {
                // get the oid of the user that is associated with the staff object
                userOid = helpers.getStaffUserOid (new OID (receiver.substring (0,18)));
                // add the oid to the receivers vector
                allReceivers.addElement (userOid);
            } // if

            // check if the facility responsibles shall be notified
            if (isRemindFacResp)
            {
                // get the associated facilities
                facilities = getFacilities (this.oid);
                // get the responsibles of the facilities
                partReceivers = getFacilityResponsibles (facilities);
                // add the result to the receivers vector
                CommonHelpers.concatVector (allReceivers, partReceivers);
            } // if

            // check if the facility part responsibles shall be notified
            if (isRemindFacPartResp)
            {
                // get the associated facilities
                facilityParts = getFacilityParts (this.oid);
                // get the responsibles of the facilities
                partReceivers = getFacilityResponsibles (facilityParts);
                // add the result to the receivers vector
                CommonHelpers.concatVector (allReceivers, partReceivers);
            } // if

            // eliminate double or multiple entries
            allReceivers = CommonHelpers.distinctOidVector (allReceivers);
            // eliminate all users that have a paket reminder interval
            // set in their configuration
            receivers = helpers.checkPackageReminder (
                allReceivers, reminderObjOid, reminderType,
                emailSubject, emailContent);
            // check if any receivers have been defined and are left for
            // standard notification:
            if (receivers != null && receivers.size () > 0)
            {
                // send a notification
                NotificationTemplate msg = new NotificationTemplate ();
                msg.setSubject (subjectText);
                msg.setDescription (descriptionText);
                msg.setActivities (activityText);
                msg.setContent ("");
                // add the object to the list of distributed objects
                Vector objs = new Vector ();
                objs.addElement (notifyObjOid);
                // send the notification
                NotificationService service = new NotificationService ();
                service.initService (this.user, this.env, this.sess, this.app);
                service.performNotification (receivers, objs, msg, false);
            } // if
            // just in case we had any receivers:
            // change the remind date field and set the actual date
            if (allReceivers != null && allReceivers.size () > 0)
            {
                setActualDate (dateFieldName);
            } // if
        } // try
        catch (NoAccessException e)
        {
            throw new ReminderException (e.toString (), e);
        } // catch (NoAccessException e)
        catch (NameAlreadyGivenException e)
        {
            throw new ReminderException (e.toString (), e);
        } // catch (NameAlreadyGivenException e)
        catch (NotificationFailedException e)
        {
            throw new ReminderException (e.toString (), e);
        } // catch (NotificationFailedException e)
        catch (IncorrectOidException e)
        {
            throw new ReminderException (e.toString (), e);
        } // catch (IncorrectOidException e)
*/
    } // remind


    /***************************************************************************
     * Change the data of a business object in the database. <BR/>
     * <B>THIS METHOD IS A DUMMY WHICH MUST BE OVERWRITTEN IN SUB CLASSES! </B>
     * <BR/>
     * This method tries to store the object into the database. During this
     * operation a rights check is done, too. If this is all right the object is
     * stored and this method terminates otherwise an exception is raised. <BR/>
     *
     * @param operation Operation to be performed with the object.
     *
     * @throws  NoAccessException
     *          The user does not have access to this object
     *          to perform the required operation.
     * @throws  NameAlreadyGivenException
     *          An object with this name already exists. This exception is only
     *          raised by some specific object types which don't allow more
     *          than one object with the same name.
     */
    protected void performChangeData (int operation)
        throws NoAccessException, NameAlreadyGivenException
    {
        try
        {
            super.performChangeData (operation);
            // set the reminders
            this.setReminders (this.oid, this.name, this.app, this.sess, this.env,
                this.user);
        } // try
        catch (NoAccessException e)
        {
            throw e;
        } // catch (NoAccessException e)
        catch (NameAlreadyGivenException e)
        {
            throw e;
        } // catch (NameAlreadyGivenException e)
    } // performChangeData


    /**************************************************************************
     * Change all type specific data that is not changed by performChangeData.
     * <BR/>
     * This method must be overwritten by all subclasses that have to change
     * type specific data.
     *
     * @param   action  This input is a dummy in this class. it will never
     *                  be used.
     *
     * @throws   DBError
     *               This exception is always thrown, if there happens an error
     *               during accessing data.
     */
    protected void performChangeSpecificData (SQLAction action) throws DBError
    {
        // check if we have a data element to be changed
        if (this.dataElement != null)
        {
            // update the attributes in the mapper table:
            try
            {
                // update the object in the mapping table:
                this.performObjectMapping (false);
            } // try
            catch (NoDataElementException e)
            {
                throw new DBError (e.getMessage ());
            } // catch
            catch (NoTemplateException e)
            {
                throw new DBError (e.getMessage ());
            } // catch
            catch (DBMappingException e)
            {
                throw new DBError (e.getMessage ());
            } // catch
        } // if
    } // performChangeSpecificData


    /**************************************************************************
     * Retrieve the type specific data that is not got from the stored
     * procedure. <BR/>
     * This method must be overwritten by all subclasses that have to get type
     * specific data that cannot be got from the retrieve data stored procedure.
     *
     * @param   action  This input is a dummy in this class. it will never
     *                  be used.
     *
     * @throws   DBError
     *               This exception is always thrown, if there happens an error
     *               during accessing data.
     */
    protected void performRetrieveSpecificData (SQLAction action) throws DBError
    {
        boolean isCreatedFromTemplate = false;

        // check if the object was just created:
        // IBS-240 BB/KR/BT 20090910: Tab objects are never created within
        // the mapping table since their state is directly set to ACTIVE,
        // when they are created by the stored procedure during creation of
        // the super object.
        // To enable the creation of tab objects the check for CREATED has
        // been removed as quick and dirty solution. The draw back of this
        // solution is that the isAlreadyCreated query is executed every time
        // an object is retrieved.
        if (/*this.state == States.ST_CREATED &&*/
                !this.isAlreadyCreated ())
        {
            // create the object data from the template:
            this.createFromTemplate (this.getDocumentTemplate ());
            // remember that the object is already created:
            // This is necessary since the state is still CREATED when this method
            // is called when the object is saved the first time.
            this.p_isAlreadyCreated = true;
            // remember that the object was newly created:
            isCreatedFromTemplate = true;
        } // if

        if (this.dataElement == null)
        {
            // for new XMLViewer objects the data must be initialized
            // when the object is instantiated for the first time.
            this.setDocumentTemplate (this.getDocumentTemplate ());

            // read the data element:
            this.dataElement = this.getDataElement ();
            // remember that the object was newly created:
            isCreatedFromTemplate = true;
        } // if

        // if the object is not yet a valid object (state ST_CREATED)
        // we set the default values from the template file.
        if (this.state == States.ST_CREATED && this.dataElement != null)
        {
            // when a form object is created the retrieve method is called
            // twice. After the first retrieve the basic default
            // values for the system attributes are set.
            // if this is the second retrieve we have to set the form defaults
            // to owerwrite the basic defaults.
            //
            // The form defaults are set selectively depending if we are
            // currently in import or not.
            //
            // KR: If we have called createFromTemplate this call is
            // possibly redundant (only if this.dataElement was not null
            // within createFromTemplate.
            if (!isCreatedFromTemplate)
            {
                this.performSetFormDefaults (this.p_isObjectImport);
            } // if

/* DEL TODO KR 20090713: This is not longer necessary.
 *                  Maybe the data have to be written to database instead?
            if (!this.p_isObjectImport)
            {
                // if the xml-object is not created via userinteraction but
                // via java-service, the tags and attributes which were
                // initialized have to be in xml-file before showing the
                // changeform.
                if (!this.writeDataFile (this.dataElement, this.fileName, this.getPath ()))
                {
                    throw new DBError ("writing the xml data file failed!");
                } // if
            } // if is not an importeded object
*/
        } // if

        // read the values from the database in case the p_useXMLDataFile
        // option is not set

        // set the oid in the data element: neccessary?
        this.dataElement.oid = this.oid;
        // get the db mapper:
        DBMapper mapper = new DBMapper (
                this.user, this.env, this.sess, this.app);
        // retrieve the data from the database:
        if (!mapper.retrieveDBEntry (this.dataElement))
        {
            throw new DBError ("Mapping-Error: " +
                mapper.getLogObject ().toString ());
        } // if
    } // performRetrieveSpecificData


    /**************************************************************************
     * Checks if the objects has already been created.
     * This means that there is already an entry in the
     * mapping table.
     *
     * @return if the object already exists in the mapping table
     *
     * @throws  DBQueryException
     *          An error occurred during execution of query.
     */
    public boolean isAlreadyCreated () throws DBQueryException
    {
        // check if the flag is already created:
        if (this.p_isAlreadyCreated)
        {
            return this.p_isAlreadyCreated;
        } // if

        // check if there exists a data element and a valid table name is set:
        if (this.dataElement != null && this.dataElement.tableName != null &&
                // Handle overview objects without database table.
                // For those objects the table name is emptry. 
                !this.dataElement.tableName.isEmpty())
        {
            // create the SQL Query to select the objects:
            SelectQuery query = new SelectQuery (
                new StringBuilder ("o.oid"),
                new StringBuilder (this.dataElement.tableName).append (" o"),
                new StringBuilder ("o.oid = ").append (this.oid.toStringBuilderQu ()),
                null, null, null);

            // execute the query:
            SQLAction action = query.execute ();

            if (action != null)
            {
                // close the query:
                query.close (action);

                // remember the result:
                this.p_isAlreadyCreated = true;
                // and return that the object has already been created:
                return true;
            } // if
        } // if

        // the object has not been created
        return false;
    } // isAlreadyCreated


    /**************************************************************************
     * Sets the default values from the document template in the new form. <BR/>
     * The initialization of the form can be performed in two different cases:
     *  1. the main object is created interactive.
     *  2. the main object is imported.
     * For the secound case not all initialisations are needed.
     *
     * @param   isImportAction      initialization of a imported object
     */
    protected void performSetFormDefaults (boolean isImportAction)
    {
        Variables vars = null;          // the action and system variables

        if (this.dataElement != null && this.p_templateObj != null)
        {
            if (!isImportAction)
            {
                // get all value elements:
                // do for all elements
                for (Iterator<ValueDataElement> iter = this.dataElement.values.iterator ();
                     iter.hasNext ();)
                {
                    // get the next element:
                    ValueDataElement value = iter.next ();

                    // if it is a queryselectionbox
                    if (value.type
                        .startsWith (DIConstants.VTYPE_QUERYSELECTIONBOX))
                    {
                        // fill the options attribute with query data
                        this.fillOptionsWithQueryData (value);
                    } // if

                    // if it is a value domain selection box
                    if (value.type
                        .startsWith (DIConstants.VTYPE_VALUEDOMAIN))
                    {
                        // fill the values and valueDomainElements attribute with query data
                        this.fillValueDomainOptionsWithQueryData (value);
                    } // if
                } // for iter
            } // if

            // KR 20050727 - Important:
            // The ACTIONs shall be executed not only for completely new
            // generated objects, but also for imported objects, which did
            // not exist before and are thus newly generated.
            // Task: EVN050617_6 Copy of template for ImportNew leads to error.
            try
            {
                // load the template to get the ACTIONS block
                // for the initialisation
                DataElement templateData = this.p_templateObj
                    .getTemplateDataElement ();
                // if there are actions for the 'OnInit' event
                // perform the actions and get the resulting variables.
                if (templateData != null &&
                    templateData.p_initActions != null)
                {
                    // perform the init actions and get the resulting
                    // variables
                    vars = templateData.p_initActions.performActions (this,
                        this.env);
                } // if
            } // try
            catch (ActionException e)
            {
                IOHelpers.showMessage (e, this.app, this.sess, this.env,
                    true);
            } // catch

            try
            {
                // check if we already got some variables:
                if (vars == null)
                {
                    // create an empty variables object:
                    vars = new Variables ();
                } // else if no actions defined

                // add the system variables:
                vars.addSysVars (this);

                // resolve all variables in the name, description and validUntil
                // field.
                this.dataElement.name = vars
                    .replaceWithValue (this.dataElement.name, this.env);
                this.dataElement.description = vars
                    .replaceWithValue (this.dataElement.description, this.env);
                this.dataElement.validUntil = vars
                    .replaceWithValue (this.dataElement.validUntil, this.env);

                // resolve all system variables in the values.
                for (Iterator<ValueDataElement> iter = this.dataElement.values.iterator (); iter.hasNext ();)
                {
                    ValueDataElement value = iter.next ();

                    // resolve all variables in the value field:
                    value.value = vars.replaceWithValue (value.value, this.env);
                } // for iter
            } // try
            catch (ActionException e)
            {
                IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            } // catch

            if (!isImportAction)
            {
                // set the default name
                if (this.name.length () == 0 ||
                    this.name.equals (this.dataElement.typename))
                {
                    if (this.dataElement.name.length () > 0)
                    {
                        this.name = this.dataElement.name;
                    } // if
                } // if

                // set the default description
                if (this.dataElement.description.length () > 0)
                {
                    this.description = this.dataElement.description;
                } // if

                // set the default show in news flag
                if (this.dataElement.showInNews != null)
                {
                    this.showInNews = DataElement
                        .resolveBooleanValue (this.dataElement.showInNews);
                } // if

                // set the default valid until date
                if (this.dataElement.validUntil.length () > 0)
                {
                    this.validUntil = DateTimeHelpers
                        .stringToDate (this.dataElement.validUntil);
                } // if
            } // if

            // initialize the tab objects:
            this.performSetTabDefaults (isImportAction);
        } // if
    } // performSetFormDefaults



    /**************************************************************************
     * Sets the default values for the tabs. <BR/>
     * The initialization of the tabs can be performed in two different cases:
     *
     * 1. the main object is created interactive
     * 2. the main object is imported.
     *
     * For the secound case not all initialisations are needed
     *
     * @param   isImportAction      spe
     */
    protected void performSetTabDefaults (boolean isImportAction)
    {
        // if the object is a tab object, the initalisation
        // of the tabs is not necessary because for tab object
        // the m2 base don't creates the tabs.
        if (this.isTab ())
        {
            return;
        } // if

        // check if there are tabs defined
        if (this.dataElement != null && this.dataElement.tabElementList != null)
        {
            int tabPos = 0;

            for (Iterator<DataElement> iter =
                    this.dataElement.tabElementList.dataElements.iterator ();
                 iter.hasNext ();)
            {
                DataElement tab = iter.next ();

                // if tab is not OBJECT Tab - do not set defaults
                if (!DIConstants.TABKIND_OBJECT.equals (tab.p_tabKind))
                {
                    continue;
                } // if

                // get the oid of the tab object
                try
                {
                    String tabCode = tab.p_tabCode;

                    // if no tabcode is defined in the template
                    // set the default tabcode (templateOID + "_" + TypeCode + "_" + TabPos)
                    if (tabCode == null || tabCode.length () == 0)
                    {
                        tabCode = this.p_templateObj.oid.toString () +
                            "_" + this.dataElement.p_typeCode + "_" + tabPos;
                    } // if no specific tatbcode defined

                    // get the oid of the tab object
                    OID tabOid = this.getTabOid (tabCode);
                    // retrieve the object
                    BusinessObject tabObj = this.getObjectCache ().fetchObject
                        (tabOid, this.user, this.sess, this.env, false);

                    // if the tab object is a form set the default values
                    if (tabObj instanceof ibs.di.XMLViewer_01)
                    {
                        XMLViewer_01 viewer = (XMLViewer_01) tabObj;
                        // set the default values explicitly
                        viewer.performSetFormDefaults (isImportAction);
                        // store the changes to the database:
                        viewer.performChange (Operations.OP_CHANGE);
                    } // if
                    // Special initialization for the XMLViewerContainer
                    // For the XMLViewerContainer the template oid must be defined
                    // in the form template.
                    else if (tabObj instanceof ibs.di.XMLViewerContainer_01)
                    {
                        XMLViewerContainer_01 container = (XMLViewerContainer_01) tabObj;
                        // initialize the tab object with the template defaults
                        container.readImportData (tab);
                        // ATTENTION! The procedure p_Object$copy generates an error if
                        // the tab object has no name.
                        // if the name for the tba object is undefined
                        // we set the the type name to avoid db errors
                        if (container.name == null || container.name.length () == 0)
                        {
                            container.name = container.typeName;
                        } // if
                        // store the changes to the database:
                        container.performChange (Operations.OP_CHANGE);
                    } // if
                } // try
                catch (NoAccessException e) // no access to objects allowed
                {
                    // send message to the user:
                    this.showNoAccessMessage (Operations.OP_VIEW);
                } // catch
                catch (ObjectNotFoundException e) // object not found
                {
                    // send message to the user:
                    IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                            BOMessages.ML_MSG_OBJECTNOTFOUND, this.env),
                            this.app, this.sess, this.env);
                } // catch
                catch (TypeNotFoundException e)
                {
                    // send message to the user:
                    IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                            BOMessages.ML_MSG_TYPENOTFOUND, new String[] {this.oid.toString ()}, this.env),
                            this.app, this.sess, this.env);
                } // catch
                catch (ObjectClassNotFoundException e)
                {
                    // send message to the user:
                    IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                            BOMessages.ML_MSG_CLASSNOTFOUND, this.env),
                            this.app, this.sess, this.env);
                } // catch
                catch (ObjectInitializeException e)
                {
                    // send message to the user:
                    IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
                } // catch
                catch (NameAlreadyGivenException e)
                {
                    // send message to the user:
                    IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                            BOMessages.ML_MSG_NAMEALREADYGIVEN, new String[] {this.name}, this.env),
                        this.app, this.sess, this.env);
                } // catch

                // increment the position counter
                tabPos++;
            } // for iter
        } // if
    } // performSetTabDefaults


    /**************************************************************************
     * Get the oid of the object which represents a tab of the actual object
     * determined by the tabs name out of the database. <BR/>
     *
     * @param   tabCode     the tab code of the tab
     *
     * @return  The oid of the object which represents the tab.
     *          null if the tab object was not found.
     *
     * @throws  NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     * @throws  ObjectNotFoundException
     *              The required object was not found during search, i.e. there
     *              does not exist any tab of the actual object with the
     *              required tab code.
     */
    private OID getTabOid (String tabCode)
        throws NoAccessException, ObjectNotFoundException
    {
        OID tabOid = null;                      // oid of the tab object
        Parameter oidParam;
        // create stored procedure call:
        StoredProcedure sp = new StoredProcedure (
            "p_DocumentTemplate_01$getTab", StoredProcedureConstants.RETURN_VALUE);

        // set parameters:
        // the tabcode (input)
        sp.addInParameter (ParameterConstants.TYPE_STRING, tabCode);
        // the oid of the main object (input)
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.oid.toString ());
        // the tab Oid (output)
        oidParam = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);

        // perform the function call:
        int retVal = BOHelpers.performCallFunctionData (sp, this.env);

        if (retVal == UtilConstants.QRY_OBJECTNOTFOUND) // object not found?
        {
            // raise not found exception:
            throw new ObjectNotFoundException (MultilingualTextProvider
                .getMessage (UtilExceptions.EXC_BUNDLE,
                    UtilExceptions.ML_E_OBJECTNOTFOUNDEXCEPTION,
                    new String[] {this.oid.toString ()}, env) + "." + tabCode);
        } // else if object not found
        else if (retVal == UtilConstants.QRY_OK)    // access allowed
        {
            // set object properties - get them out of parameters
            tabOid = SQLHelpers.getSpOidParam (oidParam);
        } // else if access allowed

        // return the oid of the tab object
        return tabOid;
    } // getTabOid



    /**************************************************************************
     * Sets the document template explicitly. <BR/>
     * Normaly the XMLViewer gets his template from the XMLViewerContainer where
     * the object is created. If a form is imported the container of the object
     * can by any container type of the m2 system. In this case the XMLViewer
     * cannot get his template from the container but the template must be set
     * explicitly. This method is used only by the ObjectFactory during the
     * import process. <BR/>
     *
     * @param   docTemplate     the document template to set
     */
    public void setDocumentTemplate (DocumentTemplate_01 docTemplate)
    {
        // to avoid null pointer exceptions
        if (docTemplate == null)
        {
            return;
        } // if

        this.systemDisplayMode = docTemplate.getSystemDisplayMode ();
        this.p_templateObj = docTemplate;
    } // setDocumentTemplate


    /**************************************************************************
     * Returns a instance of the document template for this object and
     * set it in the p_templateObj property. <BR/>
     *
     * @return  The document template or
     *          <CODE>null</CODE> if the oid is not valid or the object was
     *          not found.
     */
    protected DocumentTemplate_01 getDocumentTemplate ()
    {
        // check if this is an instance with a valid oid
        if (this.oid == null || this.oid.isEmpty ())
        {
            return null;
        } // if

        // valid oid
        // has a template already been set?
        if (this.p_templateObj == null)
        {
            this.p_templateObj = (DocumentTemplate_01)
                this.typeObj.getTemplate ();
        } // if
        return this.p_templateObj;
    } // getDocumentTemplate


    /**************************************************************************
     * Set the mapping info of the object. <BR/>
     * This method gets the db mapping information out of the document template
     * definition and stores it within the {@link #dataElement dataElement}.
     * <BR/>
     * The db mapping information consists of the table name (stored in
     * dataElement.tableName) and the attribute names of the table (stored in
     * dataElement.values[i].mappingField).
     *
     * @return  <CODE>true</CODE> if the mapping info was set,
     *          <CODE>false</CODE> otherwise.
     *
     * @throws  NoDataElementException
     *          No data element for the object defined.
     * @throws  NoTemplateException
     *          No template for the object defined or template not found.
     */
    public boolean setMappingInfo ()
        throws NoDataElementException, NoTemplateException
    {
        boolean retVal = false;         // return value of method

        // check if the data element is valid:
        if (this.dataElement == null)
        {
            throw new NoDataElementException ("'" + this.name + "' " + 
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_INVALID_TEMPLATE, env));
        } // if

        // we need an instance of the document template
        // to get the mapping information
        if (this.p_templateObj == null)
        {
            // get the document template:
            this.setDocumentTemplate (this.getDocumentTemplate ());
        } // if

        // check if the template object is valid:
        if (this.p_templateObj == null) // template object not valid?
        {
            throw new NoTemplateException ("'" + this.name + "' " + 
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_TEMPLATE_NOT_FOUND, env));
        } // if template object not valid

        // get the mapping information out from the template object:
        String mappingTable = this.p_templateObj.getMappingTableName ();
        Hashtable<String, String> mappingInfo = this.p_templateObj.getMappingInfo ();

        // check the mapping information:
        if (mappingTable != null && mappingInfo != null)
        {
            // add the mapping information from the template to
            // the object data element:
            // the mapping information consists of the table name and the table
            // field names for the mapped fields

            // set the database table name
            this.dataElement.tableName = mappingTable;

            // loop through the value elements
            // and assign the mapping names to the corresponding values.
            for (Iterator<ValueDataElement> iter = this.dataElement.values.iterator ();
                 iter.hasNext ();)
            {
                // get the next value:
                ValueDataElement value = iter.next ();

                // lookup the database name for the value field
                String mappingName = mappingInfo.get (value.field);
                if (mappingName != null)
                {
                    // set the mapping field name for the value
                    value.mappingField = mappingName;
                } // if
            } // for iter

            // the mapping info was set:
            retVal = true;
        } // if

        // return the result:
        return retVal;
    } // setMappingInfo


    /**************************************************************************
     * Performs the db-mapping for the current object. <BR/>
     * This method first calls {@link #setMappingInfo setMappingInfo} to ensure
     * that the mapping information is set. <BR/>
     * The it performs the mapping, i.e. it stores the object information within
     * the database.
     *
     * @param   isNewObject Determines the action to perform (create/update).
     *
     * @throws  NoDataElementException
     *          No data element for the object defined.
     * @throws  NoTemplateException
     *          No template for the object defined or template not found.
     * @throws  DBMappingException
     *          An exception occurred during the database mapping process.
     */
    public void performObjectMapping (boolean isNewObject)
        throws NoDataElementException, NoTemplateException, DBMappingException
    {
        boolean isAllRight = false;     // everything o.k.?

        // set the oid in the data element:
        this.dataElement.oid = this.oid;
        // get the db mapper:
        DBMapper mapper = new DBMapper (this.user, this.env, this.sess,
            this.app);

        // perform the mapping depending whether the object is newly created
        // or it is updated:
        if (isNewObject)
        {
            isAllRight = mapper.createDBEntry (this.dataElement);
        } // if
        else
        {
            isAllRight = mapper.updateDBEntry (this.dataElement);
        } // else

        // check if there occurred an error:
        if (!isAllRight)
        {
            throw new DBMappingException ("Mapping-Error: " +
                mapper.getLogObject ().toString ());
        } // if
    } // performObjectMapping


///////////////////////////
//
// SERVICE POINT HACK: START
//
//  overwrites original ibsObject.createFormFooter Method -> for instantiation
//  via ServicePoint only; otherwise method will be called with original
//  parameters
//
    /**************************************************************************
     * Creates the footer of a form with an ok and a cancel button. <BR/>
     *
     * @param   form        Form to add the footer.
     * @param   okAction    JavaScript code to be performed when OK button
     *                      pressed.
     * @param   cancelAction JavaScript code to be performed when Cancel button
     *                      pressed.
     * @param   okText      Text to be displayed on top of the OK button.
     * @param   cancelText  Text to be displayed on top of the Cancel button.
     * @param   isNewObject true: shows, that a new object just has been
     *                          created. In this case we show a small
     *                          additional menu for the handling of the new
     *                          object.
     *                      false: standard behavior for editing objects.
     * @param   isNoCancelButton if true there will be no cancel button shown
     *
     * @see #createFormHeader(ibs.tech.html.Page, String, int[], String, String, String, String, String, int)
     */
    protected void createFormFooter (FormElement form, String okAction,
                                     String cancelAction, String okText,
                                     String cancelText, boolean isNewObject,
                                     boolean isNoCancelButton)
    {
        // check type
        if (this.containerId.tVersionId ==
                this.getTypeCache ().getTVersionId (TypeConstants.TC_ServicePoint))
        {
/*
            // create okAction: workflow-action for java-script
            okAction = "top.scripts.callOidFunction ('"
                + oid + "', 'top.loadCont (" + AppFunctions.FCT_STARTWORKFLOW + ")');";
*/

            // call method with new parameters
            // - no menu will be viewed
            // - ok text changed
            super.createFormFooter (form, okAction, cancelAction, okText,
                                    cancelText, false, isNoCancelButton);
        } // if
        else
        {
            // call method with original parameters
            super.createFormFooter (form, okAction, cancelAction, okText,
                                    cancelText, isNewObject, isNoCancelButton);
        } // if
    } // if
//
// SERVICE POINT HACK: END
//
///////////////////////////


    /**************************************************************************
     * Generates the HTML code for the info view by using a stylesheet file. <BR/>
     *
     * @return      the HTML code or <CODE>null</CODE>
     *              if no xsl stylesheet is defined for the form.
     */
    protected String showPropertiesXSL ()
    {
        if (this.dataElement != null)
        {
            // get dom tree of current object
            Document doc = this.createDomTree (XMLViewer_01.VIEWMODE_SHOW);

            String xsltFile = this.app.p_system.p_m2AbsBasePath +
                BOPathConstants.PATH_XSLT + this.dataElement.p_typeCode + "_view.xsl";

            // check if a type specific stylesheet file exists:
            if (!FileHelpers.exists (xsltFile))
            {
                // use the generic stylesheet file:
                xsltFile = this.app.p_system.p_m2AbsBasePath +
                          BOPathConstants.PATH_XSLT + XMLViewer_01.GENERIC_VIEW_STYLESHEET;
            } // if


            // check if the stylesheet file exists:
            if (FileHelpers.exists (xsltFile))
            {
                // generate the layout using this file:
                return new DOMHandler (this.env, this.sess, this.app)
                    .process (doc, xsltFile);
            } // if
        } // if

        return super.showPropertiesXSL ();
    } // showPropertiesXSL


    /**************************************************************************
     * Generates the HTML code for the edit view by using a stylesheet file. <BR/>
     *
     * @return      Vector with the edit dialog info or <CODE>null</CODE>
     *              if no xsl stylesheet is defined for the form.
     *
     *              The returned vector must contain two elements:
     *
     *              1. the HTML code as a String object
     *              2. the multipart flag as a Boolean object
     */
    protected Vector<Object> showFormPropertiesXSL ()
    {
        if (this.dataElement != null)
        {
            boolean isMultipartForm = false;
            String xslOutput = null;

            // scan all value fields to determinate if the form is
            // a multipart form (form with upload fields).
            for (Iterator<ValueDataElement> iter = this.dataElement.values.iterator ();
                 iter.hasNext ();)
            {
                ValueDataElement vie = iter.next ();

                // check if the form contains a upload field.
                // for this field the html form must be a 'multipart' form.
                if (vie.type.equalsIgnoreCase (DIConstants.VTYPE_FILE) ||
                    vie.type.equalsIgnoreCase (DIConstants.VTYPE_IMAGE))
                {
                    isMultipartForm = true;
                } // if

                // fill options data for query selection boxes and value domain fields
                ValueDataElementTS.fillOptionsData (this, vie);
                  //vie.type.startsWith (DIConstants.VTYPE_VALUEDOMAIN))
            } // for iter

            // get dom tree of current object
            Document doc = this.createDomTree (XMLViewer_01.VIEWMODE_EDIT);

            // check if we have to make a multipart form for uploading files:
            if (!isMultipartForm)
            {
                // first get all <VALUE> nodes:
                NodeList valueNodeList =
                    doc.getElementsByTagName (DIConstants.ELEM_VALUE);

                // loop through all nodes:
                for (int i = 0; !isMultipartForm &&
                     i < valueNodeList.getLength (); i++)
                {
                    Element valueNode = (Element) valueNodeList.item (i);
                    String type = valueNode.getAttribute (DIConstants.ATTR_TYPE);

                    // check if the form contains a upload field.
                    // for this field the html form must be a 'multipart' form.
                    if (type.equalsIgnoreCase (DIConstants.VTYPE_FILE) ||
                        type.equalsIgnoreCase (DIConstants.VTYPE_IMAGE))
                    {
                        isMultipartForm = true;
                    } // if
                } // for i
            } // if

            // the path of the type specific stylesheet
            String xslFile = this.app.p_system.p_m2AbsBasePath +
                BOPathConstants.PATH_XSLT +
                this.dataElement.p_typeCode + "_edit.xsl";

            // if a type specific stylesheet exists
            // generate the layout using this stylesheet.
            if (FileHelpers.exists (xslFile))
            {
                xslOutput = new DOMHandler (this.env, this.sess, this.app)
                    .process (doc, xslFile);
            } // if
            else
            {
                // the path of the generic stylesheet
                xslFile = this.app.p_system.p_m2AbsBasePath +
                          BOPathConstants.PATH_XSLT + XMLViewer_01.GENERIC_EDIT_STYLESHEET;

                // if a generic stylesheet file exists
                // generate the layout using this stylesheet.
                if (FileHelpers.exists (xslFile))
                {
                    xslOutput = new DOMHandler (this.env, this.sess, this.app)
                        .process (doc, xslFile);
                } // if
            } // else if

            // check if the xsl output is valid
            if (xslOutput != null)
            {
                Vector<Object> res = new Vector<Object> ();
                // add the output and the multipart flag
                res.addElement (xslOutput);
                res.addElement (new Boolean (isMultipartForm));

                // return the vector
                return res;
            } // if
        } // if

        return super.showFormPropertiesXSL ();
    } // showFormPropertiesXSL


    /**************************************************************************
     * Returns the representation of the object as a DOM object. <BR/>
     *
     * @param   viewMode    The specific view mode.
     *
     * @return  The DOM tree of the object.
     */
    public Document createDomTree (int viewMode)
    {
        Document doc = null;

        // set view mode of current object
        this.setViewMode (viewMode);

        try
        {
            // create a new DOM root:
            doc = XMLWriter.createDocument ();
        } // try
        catch (XMLWriterException e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            return doc;
        } // catch

        //<OBJECT>
        Element object = doc.createElement (DIConstants.ELEM_OBJECT);
        object.setAttribute (
            DIConstants.ATTR_TYPECODE, this.dataElement.p_typeCode);

        // get a MLNAME for this type of object 
        String typeName = this.getMlTypeName ();

        // set the type name of the object
        object.setAttribute (DIConstants.ATTR_TYPE, typeName);

        // the layout name
        object.setAttribute ("LAYOUT", this.getUserInfo ().userProfile.layoutName);

        // the upload path
        object.setAttribute ("UPLOADPATH", this.app.p_system.p_m2AbsBasePath +
            BOPathConstants.PATH_UPLOADAPPLICATIONDIR);

        // the upload url
        object.setAttribute ("UPLOADURL", this.sess.home +
            BOPathConstants.PATH_UPLOADAPPLICATIONDIR);

        // the current user name
        object.setAttribute ("USERNAME", this.user.actUsername);

        // the location a file is shown (in actual or new window)
        object.setAttribute ("SHOWFILESINWINDOW",
            (this.getUserInfo ().userProfile.showFilesInWindows) ?
            	DIConstants.ATTRVAL_YES :
                DIConstants.ATTRVAL_NO );

        // add the head to the xml document
        doc.appendChild (object);

        // <USER>
        try
        {
            object.appendChild (doc.importNode (this.getUser ().getDomTree (),
                true));
        } // try
        catch (DOMException e)
        {
            // should not occur, display error message:
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
        catch (XMLWriterException e)
        {
            // should not occur, display error message:
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch

        //<SYSTEM>
        Element system = this.createDomTreeSystem (doc, viewMode);
        object.appendChild (system);

        //<VALUES>
        Element values = doc.createElement (DIConstants.ELEM_VALUES);
        object.appendChild (values);
        // include values stored in the DataElement values vector:
        this.createDomTreeValues (doc, values, viewMode);

        // <WORKFLOW>
        Element workflow = doc.createElement ("WORKFLOW");
        object.appendChild (workflow);
        // add the workflow information
        this.addWorkflowInfo (workflow, viewMode);

        // <ATTACHMENTS>
        Element attachmentNode = doc.createElement ("ATTACHMENTS");
        object.appendChild (attachmentNode);
        // add the XMLDATA attachments
        this.addDomTreeAttachments (attachmentNode, viewMode);

        this.serializeDOM (doc, viewMode);
        
        // return the DOM document
        return doc;
    } // createDomTree


    /**************************************************************************
     * Create the SYSTEM section of the dom tree and add it directly to the tree.
     * <BR/>
     *
     * @param   doc         The XML document which is used to create new nodes.
     * @param   viewMode    The specific view mode.
     *
     * @return  The newly generated SYSTEM node.
     */
    private Element createDomTreeSystem (Document doc, int viewMode)
    {
        // <SYSTEM>
        Element system = doc.createElement (DIConstants.ELEM_SYSTEM);
        // set the DISPLAY attribute according the display mode
        switch (this.systemDisplayMode)
        {
            case DataElement.DSP_MODE_HIDE:
                system.setAttribute (DIConstants.ATTR_DISPLAY, DIConstants.DISPLAY_NO);
                break;
            case DataElement.DSP_MODE_BOTTOM:
                system.setAttribute (DIConstants.ATTR_DISPLAY, DIConstants.DISPLAY_BOTTOM);
                break;
            default:
                // nothing to do
        } // switch (displayMode)

        // <OID>
        Element id = doc.createElement ("OID");
        id.appendChild (doc.createTextNode (this.oid.toString ()));
        system.appendChild (id);

        // <CONTAINER>
        Element container = doc.createElement ("CONTAINER");
        container.setAttribute ("ID", this.containerId.toString ());
        String objPath = "";
        String pathSep = "";
        // check if the object has just been created
        // in that case we must get the path from the containerId
        // because the newly created object does not have an activated oid yet
        // resulting in an empty container string
        if (this.state == States.ST_CREATED)
        {
            ObjectPathNode node = this.getObjectPath (this.containerId);
            // construct the path using the / as path separator
            while (node != null)
            {
                objPath = node.getName () + pathSep + objPath;
                pathSep = "/";
                node = node.getParent ();
            } // while
        } // if
        else    // object is active
        {
            // note that getObjectPath () buffers the result
            ObjectPathNode node = this.getObjectPath ();
            // construct the path using the / as path separator
            while (node != null && node.getNodeType () != ObjectPathNode.TYPE_ROOT)
            {
                objPath = node.getName () + pathSep + objPath;
                pathSep = "/";
                node = node.getParent ();
            } // while
        } // object is active

        container.appendChild (doc.createTextNode (objPath));
        system.appendChild (container);

        // <USERID>
        Element userId = doc.createElement ("USERID");
        userId.appendChild (doc.createTextNode (this.user.oid.toString ()));
        system.appendChild (userId);

        // <STATE>
        Element state = doc.createElement (DIConstants.ELEM_STATE);
        state.appendChild (doc.createTextNode ("" + this.state));
        system.appendChild (state);

        // <NAME>
        Element name = doc.createElement (DIConstants.ELEM_NAME);
        name.setAttribute ("INPUT", this.adoptArgName (BOArguments.ARG_NAME));
        name.appendChild (doc.createTextNode (this.name));
        system.appendChild (name);

        // <DESCRIPTION>
        Element description = doc.createElement (DIConstants.ELEM_DESCRIPTION);
        description.setAttribute ("INPUT", this.adoptArgName (BOArguments.ARG_DESCRIPTION));
        description.appendChild (doc.createTextNode (this.description));
        system.appendChild (description);
        // ATTENTION!!
        // The line separation is only done for the VIEW and EDIT mode.
        // For the TRANSFORM mode this should not be done!
        if (viewMode == XMLViewer_01.VIEWMODE_SHOW ||
            viewMode == XMLViewer_01.VIEWMODE_EDIT)
        {
            StringTokenizer token = new StringTokenizer (this.description, "\n");
            while (token.hasMoreElements ())
            {
                Element line = doc.createElement (DIConstants.ELEM_LINE);
                line.appendChild (doc.createTextNode (token.nextToken ()));
                description.appendChild (line);
            } // while
        } // if

        // <VALIDUNTIL>
        Element validuntil = doc.createElement (DIConstants.ELEM_VALIDUNTIL);
        validuntil.setAttribute ("INPUT", this.adoptArgName (BOArguments.ARG_VALIDUNTIL));
        String validDate = DateTimeHelpers.dateToString (this.validUntil);
        validuntil.appendChild (doc.createTextNode (validDate));
        system.appendChild (validuntil);

        // <SHOWINNEWS>
        Element showInNews = doc.createElement (DIConstants.ELEM_SHOWINNEWS);
        showInNews.setAttribute ("INPUT", this.adoptArgName (BOArguments.ARG_INNEWS));
        String flagText = this.showInNews ? 
        	DIConstants.ATTRVAL_YES :
            DIConstants.ATTRVAL_NO;
        showInNews.appendChild (doc.createTextNode (flagText));
        system.appendChild (showInNews);

        // add the extended object attributes only for the view and edit mode
        // for transformations this information is not needed.
        if (viewMode == XMLViewer_01.VIEWMODE_SHOW ||
            viewMode == XMLViewer_01.VIEWMODE_EDIT)
        {
            // create the extended attributes and add them directly to the
            // system node in the dom tree:
            this.createDomTreeSystemExt (doc, system, viewMode);
        } // if

        // return the system node:
        return system;
    } // createDomTreeSystem


    /**************************************************************************
     * Create the extended attributes for the SYSTEM section of the dom tree.
     * <BR/>
     * The attributes are directly added to the system section.
     *
     * @param   doc         The XML document which is used to create new nodes.
     * @param   system      The SYSTEM node of the dom tree.
     * @param   viewMode    The specific view mode.
     */
    private void createDomTreeSystemExt (Document doc, Element system, int viewMode)
    {
        // should the extened attributes be shown
        if (this.getUserInfo ().userProfile.showExtendedAttributes)
        {
            system.setAttribute (DIConstants.ATTR_SHOWEXT, "1");
        } // if
        else
        {
            system.setAttribute (DIConstants.ATTR_SHOWEXT, "0");
        } // else if

        // <OWNER USERNAME="">
        Element owner = doc.createElement (DIConstants.ELEM_OWNER);
        if (this.owner != null && this.owner.fullname != null)
        {
            owner.setAttribute (DIConstants.ATTR_USERNAME, this.owner.fullname);
        } // if
        system.appendChild (owner);

        // <CREATED DATE="" USERNAME="">
        Element creation = doc.createElement (DIConstants.ELEM_CREATED);
        creation.setAttribute (DIConstants.ATTR_DATE, DateTimeHelpers.dateTimeToString (this.creationDate));

        if (this.creator != null && this.creator.fullname != null)
        {
            creation.setAttribute (DIConstants.ATTR_USERNAME, this.creator.fullname);
        } // if
        system.appendChild (creation);

        // <CHANGED DATE="" USERNAME="">
        Element changed = doc.createElement (DIConstants.ELEM_CHANGED);
        changed.setAttribute (DIConstants.ATTR_DATE, DateTimeHelpers.dateTimeToString (this.lastChanged));
        // add the name of the changer
        if (this.changer != null && this.changer.fullname != null)
        {
            changed.setAttribute (DIConstants.ATTR_USERNAME, this.changer.fullname);
        } // if
        system.appendChild (changed);

        // if the object is check out add the checkout date and user
        if (this.checkedOut)
        {
            // <CHECKOUT DATE="" USERNAME="">
            Element checkout = doc.createElement (DIConstants.ELEM_CHECKEDOUT);
            // add the checkout date
            checkout.setAttribute (DIConstants.ATTR_DATE, DateTimeHelpers.dateToString (this.checkOutDate));
            if (this.checkOutUserName != null)
            {
                // add the user name
                checkout.setAttribute (DIConstants.ATTR_USERNAME, this.checkOutUserName);
            } // if
            system.appendChild (checkout);

            // <CHECKOUTUSERID>
            Element checkOutUserID = doc.createElement ("CHECKOUTUSERID");
            checkOutUserID.appendChild (doc.createTextNode (this.checkOutUserOid.toString ()));
            system.appendChild (checkOutUserID);

            // <CHECKOUTKEY>
            Element checkOutKey = doc.createElement ("CHECKOUTKEY");
            checkOutKey.appendChild (doc.createTextNode
                (WebdavData.getDateTimeKey (this.checkOutDate)));
            system.appendChild (checkOutKey);

            // <WEBDAVURL>
            Element webdavURL = doc.createElement ("WEBDAVURL");
            webdavURL.appendChild (doc.createTextNode (((Configuration) this.app.configuration).getWebDavURL (this.env)));
            system.appendChild (webdavURL);
        } // if
    } // createDomTreeSystemExt


    /**************************************************************************
     * Create the values for the dom tree and add them directly to the tree.
     * <BR/>
     * The parameter values should contain the already created &lt;VALUES> node.
     * This is where the values are directly added.
     *
     * @param   doc         The XML document which is used to create new nodes.
     * @param   values      The &lt;VALUES> node of the dom tree.
     * @param   viewMode    The specific view mode.
     */
    public void createDomTreeValues (Document doc, Node values, int viewMode)
    {
        // include values stored in the DataElement values vector:
        for (Iterator<ValueDataElement> iter = this.dataElement.values.iterator ();
             iter.hasNext ();)
        {
            // get the value data:
            ValueDataElement vie = iter.next ();

            // create the value node for the dom tree and append it:
            this.createDomTreeValueNode (doc, values, vie, viewMode);
        } // for iter
    } // createDomTreeValues


    /**************************************************************************
     * Create a value for the dom tree and add it directly to the tree. <BR/>
     * The parameter values should contain the already created &lt;VALUES> node.
     * This is where the value is directly added.
     *
     * @param   doc         The XML document which is used to create new nodes.
     * @param   values      The &lt;VALUES> node of the dom tree.
     * @param   vie         The data element representing the data of the value.
     * @param   viewMode    The specific view mode.
     */
    public void createDomTreeValueNode (Document doc, Node values,
                                        ValueDataElement vie, int viewMode)
    {
        String arg = this.createArgument (vie.field);

        // call method of value data element:
        ValueDataElementTS.createDomTreeValueNode (this, doc, values, vie, this.oid,
            this.adoptFieldName (this.createToken (vie.field)),
            arg, this.adoptArgName (arg), viewMode, this.app, this.sess,
            this.user, this.env);
    } // createDomTreeValueNode


    /**************************************************************************
     * Create a value for the dom tree and add it directly to the tree. <BR/>
     * The parameter values should contain the already created &lt;VALUES> node.
     * This is where the value is directly added.
     *
     * @param   doc         The XML document which is used to create new nodes.
     * @param   values      The &lt;VALUES> node of the dom tree.
     * @param   fieldName   The field name for the value.
     * @param   type        The value type.
     * @param   value        The text to be added to the value. If
     *                      <CODE>null</CODE> no text is added.
     * @param   mandatory   Is the value mandatory?
     * @param   readonly    Is the value readonly?
     * @param   valueUnit   The unit of the value.
     * @param   argName     Argument name. If no argument name is defined
     *                      (<CODE>null</CODE>) the fieldName is converted into
     *                      a valid argument name. <BR/>
     *                      Otherwise the method
     *                      {@link BusinessObject#adoptArgName(String)
     *                      adoptArgName}
     *                      is executed on the argument.
     * @param   viewMode    The specific view mode.
     * @param   size        Size of the value's content.
     *
     * @deprecated  KR 20090717 This method seems to be never used.
     */
    @Deprecated
    public void createDomTreeValueNode (Document doc, Node values,
                                        String fieldName, String type,
                                        String value, String mandatory,
                                        String readonly,
                                        String valueUnit, String argName,
                                        int viewMode, long size)
    {
        Element valueNode = null;

        // Dateiname:
        valueNode = doc.createElement (DIConstants.ELEM_VALUE);
        valueNode.setAttribute (DIConstants.ATTR_FIELD, this
            .adoptFieldName (this.createToken (fieldName)));
        valueNode.setAttribute (DIConstants.ATTR_TYPE, type);

        if (valueUnit != null)
        {
            valueNode.setAttribute (DIConstants.ATTR_UNIT, valueUnit);
        } // if

        if (argName != null)
        {
            valueNode.setAttribute ("INPUT", this.adoptArgName (argName));
        } // if
        else
        {
            valueNode.setAttribute ("INPUT",
                this.adoptArgName (this.createArgument (fieldName)));
        } // else

        if (mandatory != null)
        {
            valueNode.setAttribute (DIConstants.ATTR_MANDATORY, mandatory);
        } // if

        if (readonly != null)
        {
            valueNode.setAttribute (DIConstants.ATTR_READONLY, readonly);
        } // if

        if (value != null)
        {
            valueNode.appendChild (doc.createTextNode (value));
        } // if

        // make type-specific add-ons::
        if (type.equalsIgnoreCase (DIConstants.VTYPE_FILE))
        {
            // show name of file without OID !!!
            valueNode.setAttribute (DIConstants.ATTR_URL,
                                    this.oid.toString () + "/" + value);
            valueNode.setAttribute (DIConstants.ATTR_SIZE, "" + size);
        } // if

        // append the value node to the values:
        values.appendChild (valueNode);
    } // createDomTreeValueNode


    /**************************************************************************
     * Inserts the XMLDATA attachments in the DOM tree.
     *
     * @param   attachmentNode  ???
     * @param   viewMode        ???
     */
    protected void addDomTreeAttachments (Node attachmentNode, int viewMode)
    {
        // add the attachments to the dom tree
        if (this.dataElement.attachmentList != null &&
            !this.dataElement.attachmentList.isEmpty ())
        {
            Document doc = attachmentNode.getOwnerDocument ();

            for (Iterator<Node> iter = this.dataElement.attachmentList.iterator ();
                 iter.hasNext ();)
            {
                Document attDoc = (Document) iter.next ();

                // import the attachment in the document
                Node node = doc.importNode (attDoc.getDocumentElement (), true);
                // insert the node in the document
                attachmentNode.appendChild (node);
            } // for iter
        } // if
    } // addDomTreeAttachments


    /**************************************************************************
     * Adds the workflow information to the given DOM node.
     *
     * @param   workflowNode    ???
     * @param   viewMode        ???
     */
    protected void addWorkflowInfo (Node workflowNode, int viewMode)
    {
        Document doc = workflowNode.getOwnerDocument ();

        // add the <OID> tag with the workflow oid
        Node oidNode = doc.createElement ("WFOID");
        workflowNode.appendChild (oidNode);

        // add the <WFSTATE> tag with the current state from the wf
        Node wfStateNode = doc.createElement ("WFSTATE");
        workflowNode.appendChild (wfStateNode);

        // add the <OBJSTATE> tag with the current state from the wf
        Node objStateNode = doc.createElement ("OBJSTATE");
        workflowNode.appendChild (objStateNode);

        // get the workflow informations
        WorkflowInstanceInformation wfInfo = this.getWorkflowInstanceInfo ();
        if (wfInfo != null)
        {
            String wfOid = wfInfo.instanceId.toString ();
            String wfState = wfInfo.workflowState;
            String objState = wfInfo.currentState;

            // add the information
            oidNode.appendChild (doc.createTextNode (wfOid));
            wfStateNode.appendChild (doc.createTextNode (wfState));
            objStateNode.appendChild (doc.createTextNode (objState));
        } // if
    } // addWorkflowInfo


    /**************************************************************************
     * Serialize a DOM tree. <BR/>
     * A check is done if the DOM tree and a link to the template shall be displayed.
     * For the Administration the DOM tree will always be shown.
     *
     * @param   doc     the document to serialize to
     * @param   viewMode    The specific view mode.
     * 
     */
    protected void serializeDOM (Document doc, int viewMode)
    {        
        // in case we are in edit or view mode show the dom tree if applicable
        if (viewMode == XMLViewer_01.VIEWMODE_EDIT || viewMode == XMLViewer_01.VIEWMODE_SHOW)
        {        	
        	// try to show the DOM Tree without <?xml version="1.0" encoding="UTF-8"?>
            if (this.isShowDOMTree || 
            	this.user.username.equalsIgnoreCase(IOConstants.USERNAME_ADMINISTRATOR) ||
            	this.user.username.equalsIgnoreCase(IOConstants.USERNAME_DEBUG))
            {
                DOMHandler serializer = new DOMHandler (this.env, this.sess, this.app);        
                String output = serializer.domToString (doc, "OBJECT");        	
            	
                // show the dom tree info
                this.createHTMLHeader (this.app, this.sess, this.env);
                DIHelpers.showDOMInfo (this.env, output, this.p_templateObj);
                this.createHTMLFooter (this.env);
            } // if (this.isShowDOMTree || ...        	
        } // if (viewMode != XMLViewer_01.VIEWMODE_EDIT || viewMode != XMLViewer_01.VIEWMODE_SHOW)        
        
    } // serializeDOM


    /**************************************************************************
     * Performs a transformation via xslt. <BR/>
     * The result of the transformation is a new m2 object with is imported
     * in the container specified in the first element of the parameter vector.
     * The stylesheet file for the transformation is obtained by calling the
     * method getTransformationFileName(). Overwrite this method to set the
     * correct stylesheet.
     *
     * @param   param       a vector containing the parameters for the
     *                      transformation.
     *                    - The first element contains the path or oid of the
     *                      destination container.
     *                    - The second element - if exists,
     *                      contains the xsl file name (not an absolute path,
     *                      just the name of the file).
     *
     * @return      result vector with holds the result code (1. element)
     *              and the result message (2. element) as strings.
     */
    public Vector<String> performTransformation (Vector<String> param)
    {
        return this.performTransformation (param, null);
    } // performTransformation


    /**************************************************************************
     * Performs a transformation via xslt. <BR/>
     * The result of the transformation is a new m2 object with is imported
     * in the container specified in the first element of the parameter vector.
     * The stylesheet file for the transformation is obtained by calling the
     * method getTransformationFileName(). Overwrite this method to set the
     * correct stylesheet.
     *
     * @param   param       A vector containing the parameters for the
     *                      transformation.
     *                      - The first element contains the path or oid of the
     *                        destination container.
     *                      - The second element - if exists,
     *                        contains the xsl file name (not an absolute path,
     *                        just the name of the file).
     * @param   transPath   The path where the transformation shall take place.
     *                      If it is null the m2 temporary directory is used.
     *                      If it is not null then it must end with a File.separator.
     *
     * @return  Result vector with holds the result code (1. element)
     *          and the result message (2. element) as strings.
     */
    public Vector<String> performTransformation (Vector<String> param, String transPath)
    {
        // the 1. element in the result vector holds the result code.
        // the 2. element in the result vector holds the result message.
        String resultCode = "-1";
        String resultMsg = "ERROR";
        String transP = transPath;

        try
        {
            // the first parameter holds the destination container
            // for the new object.
            String destPath = param.elementAt (0);
            // the oid of the destination container
            OID destinationOid = null;

            // the filename of the xsl file
            String transformationFileName = null;

//debug ("performTransformation: destination=" + destPath);
//debug.DebugClient.debugln ("performTransformation: destination=" + destPath);

            // get the destination oid out of the params
            // check if first element of the vector represents
            // the oid of the destination container
            if (destPath != null)
            {
                try
                {
                    // second parameter is the oid
                    destinationOid = new OID (param.elementAt (0));
                } // try
                catch (IncorrectOidException e1)
                {
                    destinationOid = BOHelpers.resolveObjectPath (destPath, this, this.env);
                } // catch
            } // if destination container given

            // check if param vector has a second element
            // this represents the filename of the xsl file (without path!)
            if (param.size () > 1)
            {
                transformationFileName = param.elementAt (1);
            } // if param contain at least 2 elements

            // check if transformation file name is set to a valid name
            if (transformationFileName == null ||
                transformationFileName.trim ().length () == 0)
            {
                transformationFileName = this.getTransformationFileName ();
            } // transformationfilename not given


            // destination oid found
            if (destinationOid != null)
            {
                // get the temporary directory
                if (transP == null)
                {
                    transP = this.app.p_system.p_m2AbsBasePath +
                        DIConstants.PATH_TEMPROOT;
                } // if

                String transFile = this.oid.toString () + ".xml";
                Document doc = this.createDomTree (XMLViewer_01.VIEWMODE_TRANSFORM);

                if (doc != null && destinationOid != null)
                {
                    // get the transformation file
                    String xslFile = this.app.p_system.p_m2AbsBasePath +
                                     XMLViewer_01.TRANSFORM_XSLT_PATH +
                                     transformationFileName;

                    // translate the file:
                    new XSLTTransformer (xslFile)
                        .translateFile (doc, transP + transFile);
                    doc = null;

                    // now import the new object in the destination container
                    if (this.performImport (transP, transFile, destinationOid))
                    {
                        resultCode = "0";
                        resultMsg = "DONE";
                    } // if

                    // remove the xml file
                    FileHelpers.deleteFile (transP + transFile);
                } // if
            } // if
        } // try
        catch (XSLTTransformationException e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            resultMsg = e.toString ();
        } // catch

        // set the result vector
        Vector<String> result = new Vector<String> ();
        result.addElement (resultCode);
        result.addElement (resultMsg);

        return result;
    } // performTransformation


    /**************************************************************************
     * Returns the file name of the transformation file (xsl). <BR/>
     * Overwrite this method to select the correct stylesheet file.
     * The file must be located in the TRANSFORM_XSLT_PATH directory.
     *
     * @return      the file name of the xsl file for transformations
     */
    public String getTransformationFileName ()
    {
        return "";
    } // getTransformationFile


    /**************************************************************************
     * Imports the given XML file in the given container. <BR/>
     * This method is used to import the xml file of a transformation.
     *
     * @param path              the path of the xml file
     * @param filename          the file name of the xml file
     * @param destinationOid    the oid of the destination container
     *
     * @return  <CODE>true</CODE> on success.
     */
    protected boolean performImport (String path, String filename, OID destinationOid)
    {
        // get a dummy connector for the import
/*
        FileConnector_01 conn = new FileConnector_01 ();
        conn.initObject (OID.getEmptyOid (), this.user, this.env, this.sess, this.app);

        // set the path and file name
        conn.setPath (path);
        conn.setFileName (filename);
*/

        // create the import Integrator
        ImportIntegrator impInt = new ImportIntegrator ();
        impInt.initObject (OID.getEmptyOid (), this.user, this.env, this.sess, this.app);
        impInt.setContainerId (destinationOid);
//        impInt.setConnector (conn);

        // set the flag to suppress HTML output and force text output
        impInt.isShowSettings = false;
        impInt.setGenerateHTML (false);
        impInt.setDisplayLog (false);
        impInt.isGetSettingsFromEnv = false;
        // init a log object:
        Log_01 log = new Log_01 ();
        log.initObject (OID.getEmptyOid (), this.user, this.env, this.sess, this.app);
        log.isDisplayLog = false;

        // perform the import
        impInt.startImport (new String[] {filename}, path, log);

        // check if import was successfuly
        if (!impInt.getIsSuccessful ())
        {
            String msg = impInt.getLog ().toString ();
            IOHelpers.showMessage (msg, this.app, this.sess, this.env);
            return false;
        } // if
        return true;
    } // performImport


    /**************************************************************************
     * Evaluates a XPath expression. <BR/>
     *
     * @param   param       a vector containing the parameters for the
     *                      transformation.
     *
     * @return      result vector with holds the result code (1. element)
     *              and the result message (2. element) as strings.
     */
    public Vector<String> performXPath (Vector<String> param)
    {
        // the 1. element in the result vector holds the result code.
        // the 2. element in the result vector holds the result message.
        // the 3. element in the result vector holds the xpath result.
        String resultCode   = "-1";
        String resultMsg   = "ERROR";
        String resultValue = "";

        try
        {
            // the first parameter holds the destination container
            // for the new object.
            String xpathExpr = param.elementAt (0);

            if (xpathExpr != null)
            {
                Document doc = this.createDomTree (XMLViewer_01.VIEWMODE_XPATH);

                if (doc != null)
                {
                    String xsl =
                        "<?xml version='1.0' encoding='" + DIConstants.CHARACTER_ENCODING + "'?>" +
                        "<xsl:stylesheet version='1.0' xmlns:xsl='" + IOConstants.URL_HTTP + "www.w3.org/1999/XSL/Transform'>" +
                        "<xsl:output method='text' encoding='" + DIConstants.CHARACTER_ENCODING + "'/>" +
                        "<xsl:template match='/'>" +

                        "<DATA><xsl:value-of select=\"" + xpathExpr + "\"/></DATA>" +

                        "</xsl:template>" +
                        "</xsl:stylesheet>";

//debug ("XPath: expr=" + xsl);
//debug.DebugClient.debugln ("XPath: expr=" + xsl);

                    // translate the file:
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream ();

                    new XSLTTransformer (new ByteArrayInputStream (xsl.getBytes ()))
                        .translateFile (doc, outStream);

                    doc = null;

//debug ("XPath: result=" + outStream.toString ());
//debug.DebugClient.debugln ("XPath: result=" + outStream.toString ());

                    // set the result values
                    resultCode = "0";
                    resultMsg = "DONE";
                    resultValue = outStream.toString (DIConstants.CHARACTER_ENCODING);

                } // if
            } // if
        } // try
        catch (XSLTTransformationException e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            resultMsg = e.toString ();
        } // catch
        catch (UnsupportedEncodingException e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            resultMsg = e.toString ();
        } // catch

        // set the result vector
        Vector<String> result = new Vector<String> ();
        result.addElement (resultCode);
        result.addElement (resultMsg);
        result.addElement (resultValue);

        return result;
    } // performXPath


    /**************************************************************************
    * This method get the attributes from the XML-form and give them to
    * the class that handle with this attributes. <BR/>
    * Actualy this is for the type=”SELECTION”, that looks like a selection Box
    * but its similar to the type=”OPTION”.A selection box will be created and
    * get the values whitch where found before.
    *
    * @param    fieldname               name for the selectionbox to use in HTML
    * @param    selectedValue           value with will be selected again whe edit new
    * @param    typeFilter              objects that should be searched
    * @param    searchRoot              from where it should be searched
    * @param    searchRecursive         sould it be searched recursive?"YES"/"NO"
    * @param    isAddEmptyOption        could be displayed a empty option
    *
    * @return   the found objects
    */
    public GroupElement createSelection (String fieldname,
                                         String selectedValue,
                                         String typeFilter,
                                         String searchRoot,
                                         String searchRecursive,
                                         boolean isAddEmptyOption)
    {
        this.trace ("----------------------------START createSelection------------");
        OID searchRootOid;
        SingleSelectionContainerElement_01 containerElement = null;
        ObjectSearchContainer_01 objectSearchContainer;
        GroupElement group = new GroupElement ();
        SelectElement select;
        boolean isSelected;

        // create a object search container instance
        objectSearchContainer = new ObjectSearchContainer_01 ();
        objectSearchContainer.initObject (OID.getEmptyOid (), this.user,
                                          this.env, this.sess, this.app);

        if (searchRoot != null && searchRecursive != null)
        {
            // set the searchRoot
            // get the OID from the searchroot path
            this.trace ("searchRoot" + searchRoot);
            searchRootOid = BOHelpers.resolveObjectPath (searchRoot, this, this.env);
            this.trace ("searchRootOid" + searchRootOid);
            // set the searchRoot for the search
            objectSearchContainer.searchStart = searchRootOid;
            // set if it should be searched recursive
            if (searchRecursive.equalsIgnoreCase ("YES"))
            {
                objectSearchContainer.searchRecursive = true;
            } // if
            else    // it is set to "NO"
            {
                objectSearchContainer.searchRecursive = false;
            } // else it is set to "NO"
            // set the typeFilter for the search
            objectSearchContainer.showTypes = typeFilter;
            try
            {
                // try to search the object in typeFilter
                // take container to get the method which execute the query
                // and save it in the element vector
                objectSearchContainer.retrieveContent (Operations.OP_READ, 0,
                                                       BOConstants.ORDER_ASC);

                // create the selection box with multiselect deactivated
                select = new SelectElement (fieldname, false);
                // set number of lines to be displayed in the selection box
                select.size = 1;
                String selectedOidStr = "";
                // substract the oid from the value to be preselected
                if (selectedValue != null && selectedValue.length () > 0)
                {
                    selectedOidStr = selectedValue.substring (0, OID.EMPTYOID.length ());
                } // if
                // check if an emtpy option should be included
                if (isAddEmptyOption)
                {
                    select.addOption ("", "");
                } // if

                // loop through the vector:
                // get elements out of the elements vector from container
                for (Iterator<ContainerElement> iter = objectSearchContainer.elements.iterator ();
                     iter.hasNext ();)
                {
                    // get a SingleSelectionContainerElement out of the vector:
                    containerElement =
                        (SingleSelectionContainerElement_01) iter.next ();

                    // check if this is the selected option:
                    isSelected =
                        selectedOidStr.equals (containerElement.oid.toString ());
                    // create the option
                    select.addOption (containerElement.name,
                                      containerElement.oid.toString () +
                                      DIConstants.OPTION_DELIMITER +
                                      containerElement.name,
                                      isSelected);
                    // get the name of the found element for showProperties
                    // containerElementName = containerElement.name;
//trace ("name " + containerElement.name);
                } // for iter

                // add the typefilter as hidden element
                group.addElement (new InputElement (fieldname +
                                    DIConstants.SEL_TYPEFILTER,
                    InputElement.INP_HIDDEN, "" + typeFilter));
                // add the searchroot as hidden element
                group.addElement (new InputElement (fieldname +
                                    DIConstants.SEL_SEARCHROOT,
                    InputElement.INP_HIDDEN, "" + searchRoot));
                // add the typefilter as hidden element
                group.addElement (new InputElement (fieldname +
                                    DIConstants.SEL_SEARCHRECURSIVE,
                    InputElement.INP_HIDDEN, "" + searchRecursive));
                // add the selection box to the group element
                group.addElement (select);
            } // try
            catch (NoAccessException e)
            {
                IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            } // catch (NoAccessException e)
        } // if
        else                            // searchroot or searchrecursive = null
        {
            IOHelpers.showMessage ("Error: no searchroot or searchrecursive set!!",
                this.app, this.sess, this.env);
        } // else searchroot or searchrecursive = null

        // return the group element
        return group;
    } // createSelection


    /**************************************************************************
     * The options attribute of the ValueDataElement is set. <BR/>
     *
     * @param   value   the ValueDataElement object
     */
    protected void fillOptionsWithQueryData (ValueDataElement value)
    {
        // save the query result
        SelectionList queryResults = null;
        // save all query values together in options for QUERYSELECTION
        StringBuilder options = new StringBuilder ("");
        // save a single query value
        String part = "";
        // execute the query
        queryResults = this.getQueryData (value.queryName,
            DIConstants.ATTRVAL_YES.equals (value.emptyOption),
            (Vector<Vector<InputParamElement>>) DIHelpers
                .getTemplateSubTags (this.getDocumentTemplate (), value.field));

        // if the query returns valid data
        if (queryResults != null && queryResults.values != null)
        {
            // do for all query values
            for (int i = 0; i < queryResults.values.length; i++)
            {
                // if this is not the first result
                if (i > 0)
                {
                    // add the delimiter to options of QUERYSELECTION
                    options.append (",");
                } // if i > 0 (not the first result)

                // replace the comma by unicode
                part = StringHelpers.replace (queryResults.values[i], ",",
                                       AppConstants.UC_COMMA);

                // add the part to the result string
                options.append (part);
            } // for
        } // if
        // fill the options attribute
        value.options = options.toString ();
    } // fillOptionsWithQueryData


    /**************************************************************************
     * The options attribute of the ValueDataElement is set for ValueDomain
     * types. <BR/>
     *
     * @param   value   the ValueDataElement object
     */
    protected void fillValueDomainOptionsWithQueryData (ValueDataElement value)
    {
        // save the query result
        SelectionList queryResults = null;

        //TODO: get the params from the domain attribute or from the container
        //      Vector<InputParamElement> inParams = new Vector<InputParamElement>();
        //      InputParamElement inParamElement = new InputParamElement (domain,domainValue);
        //      inParams.add (inParamElement);
//        Vector<InputParamElement> inParams = null;

        // create the SQL String
        StringBuffer queryStr = new StringBuffer ()
            .append (" SELECT *")
            .append (" FROM v_getValueDomain")
            .append (" WHERE context = '")
            .append (value.p_context).append ("'")
            .append (" ORDER BY orderCrit, value ASC ");

        // get the query values:
        queryResults =
            this.getQueryData (queryStr, DIConstants.IDTYPE_OBJECTID,
                            DIConstants.ATTRVAL_YES.equals (value.emptyOption));

        // save all query OIDs
        List<String> oids = null;

        // save all query values
        List<String> values = null;

        // if the query returns valid data
        if (queryResults != null && queryResults.values != null)
        {
            oids = new ArrayList<String> ();
            values = new ArrayList<String> ();

            // do for all query values
            for (int i = 0; i < queryResults.values.length; i++)
            {
                // add oids
                oids.add (queryResults.ids[i]);

                // add values
                values.add (queryResults.values[i]);
            } // for
        } // if

        // fill the value attribute
        value.values = oids;

        // fill the options attribute
        value.valueDomainElements = values;
    } // fillOptionsWithQueryData


    /**************************************************************************
     * The chosen query is performed and the chosen column values
     * are returned. <BR/>
     *
     * @param   queryname   name of the query which is filling the selectionbox
     * @param   emptyoption true if there is an empty item
     * @param   subTags     A vector which contains all input- and output
     *                      parameters
     *
     * @return  The resulting selection list.
     */
    protected SelectionList getQueryData (String queryname, boolean emptyoption,
                                          Vector<Vector<InputParamElement>> subTags)
    {
        int inSize = 0;
        Vector<InputParamElement> inParams = null;

        // initialize a SelectionList where ids and values from the query are saved
        SelectionList results = new SelectionList ();
        QueryExecutive qe = new QueryExecutive ();
        // leider noch immer notwendig :(
        qe.initService (this.user, this.env, this.sess, this.app);

        // check if given queryName for query to be executed is not null
        if (queryname != null)
        {
            // set current oid and containerId in order to resolve sysvars
            // referencing object data
            qe.setCurrentObjectOid (this.oid);
            qe.setCurrentContainerId (this.containerId);

            // check if we got any subtags:
            if (subTags != null)
            {
                inParams = subTags.elementAt (0);
                inSize = inParams.size ();
            } // if

            // check if there are any input parameters:
            if (inSize > 0)                 // input parameters found?
            {
                try
                {
                    Variables var = new Variables ();
                    var.addSysVars (this);

                    for (int i = 0; i < inParams.size (); i++)
                                                // loop through all input parameters
                    {
                        InputParamElement inParam = inParams.elementAt (i);

                        qe.addInParameter (inParam.getName (),
                            QueryConstants.FIELDTYPE_STRING,
                            var.replaceWithValue (inParam.getValue (), this.env));
                    } // for loop through all input parameters
                } // try
                catch (ActionException e)
                {
                    IOHelpers.showMessage (e.toString (),
                        this.app, this.sess, this.env);
                } // catch
            } // if input parameters found

            // check if query exists and try to execute it
            if (qe.execute (queryname))
            { // if query with name exists and could be executed
                // get the rowCount of the query
                int rowCount = qe.getRowCount ();
                // check if an error occurred
                if (rowCount < 0)
                {
                    return null;
                } // if
                // check if no entries have been found an no empty option should be added
                else if (rowCount == 0 && (!emptyoption))
                {
                    return null;
                } // else if
                else    // create the selection box
                {
                    // number of the selectionbox entries
                    int num = rowCount + (emptyoption ? 1 : 0);

                    int i = 0;              // counter
                    // initialize the SelectionList
                    results = new SelectionList ();
                    // initialize the string arrays of the selectionlist
                    results.ids = new String[num];
                    results.values = new String[num];

                    // if an empty item in the selectionbox is required
                    if (emptyoption)
                    {
                        results.ids[0] = " ";
                        results.values[0] = " ";
                    } // if
                    else
                    {
                        i = -1;
                    } // else
                    while (!qe.getEOF ())
                    {
                        String myValue;
                        myValue = qe.getColValue ("value");
                        // add another id to the ids array of the selectionlist
                        results.ids[++i] = myValue;
                        // add another value to the values array of the selectionlist
                        results.values[i] = myValue;
                        // get the next row
                        qe.next ();
                    } // while
                } // else create the selection box
            } // if
        } // if
        else                            // if
        {
            return null;
        } // else
        // return the SelectionList
        return results;
    } // getQueryData


    /**************************************************************************
     * This method transforms a string into a valid argument that can be used
     * in a form. <BR/>
     *
     * @param   field   The namme of a field to be transformed into an argument.
     *
     * @return  A valid argument.
     */
    protected String createArgument (String field)
    {
        // replace all characters that could be critical when used in a form
        return DIHelpers.replaceCriticalCharacters (field);
    } // ceateArgument


    /**************************************************************************
     * This method transforms a string into a valid token that can be displayed
     * in a form. <BR/>
     * Note that this method does not do any special with in the XMLViewer
     * class but is meant to be overwritten in subclasses. <BR/>
     *
     * @param   field   The namme of a field to be transformed into an argument.
     *
     * @return  A valid token to be used in a form.
     */
    protected String createToken (String field)
    {
        return field;
    } // createToken


    /**************************************************************************
     * Get the next count of specified counter. <BR/>
     *
     * @param   param       a vector containing the name of the counter
     *                      to be incremented as String in first element.
     *
     * @return      result vector with holds the result code (1. element)
     *              and the result message (2. element) as strings.
     */
    public Vector<String> getNextCount (Vector<String> param)
    {
        String resultValue = null;
        String resultMsg = "";
        String resultCode = "0";

        String counterName = param.elementAt (0);
        String counterFormat = param.elementAt (1);

        // check if a valid counter name was set:
        if (counterName != null && counterName.length () != 0)
        {
            // get the next counter value:
            resultValue = this.getNextCount (counterName, counterFormat);
        } // if
        else
        {
            // set error code:
            resultMsg = "Invalid counter name '" + counterName + "'";
            resultCode = "-1";
        } // else if

        // did we get a valid counter?
        if (resultValue == null)
        {
            // set error code:
            resultMsg = "Could not create counter for '" + counterName + "'";
            resultCode = "-1";
        } // catch

        // set the result vector:
        Vector<String> result = new Vector<String> ();
        result.addElement (resultCode);
        result.addElement (resultMsg);
        result.addElement (resultValue);

        // return the result:
        return result;
    } // getNextCount


    /**************************************************************************
     * Get the next count of specified counter in a given format. <BR/>
     *
     * @param counterName    counter to be incremented
     * @param counterFormat the format of the counter
     *
     * @return      resulting counter ot <code>null</code> otherwise
     */
    public String getNextCount (String counterName, String counterFormat)
    {
        Counter cnt;
        try
        {
            // CONTRAINT: check if a valid counter name was set:
            if (counterName != null && counterName.length () != 0)
            {
                // create the counter object
                cnt = new Counter ();
                cnt.initObject (this.oid, this.user, this.env, this.sess, this.app);
                String counterN = counterName;

                // check if there is a container:
                if (this.containerId != null)
                {
                    // set container oid within counter name:
                    counterN = StringHelpers.replace (counterN,
                        "#LOCAL#", this.containerId.toString ());
                } // if
                // get the next counter value:
                return cnt.getNextFormat (counterN, counterFormat);
            } // if

            // did not get any valid counter name
            return null;
        } // try
        catch (NoAccessException e)
        {
            this.showNoAccessMessage (Operations.OP_READ);
            return null;
        } // catch (NoAccessException e)
    } // getNextCount


    /**************************************************************************
     * resets a counter to 0. <BR/>
     *
     * @param   param       a vector containing the name of the counter
     *                      to be resetted as String in first element.
     *
     * @return      result vector with holds the result code (1. element)
     *              and the result message (2. element) as strings.
     */
    public Vector<String> resetCounter (Vector<String> param)
    {
        Counter cnt = new Counter ();
        String resultMsg = "";
        String resultCode = "0";

        cnt.initObject (this.oid,
                        this.user, this.env, this.sess, this.app);

        try
        {
            cnt.reset (param.elementAt (0));
        } // try
        catch (NoAccessException e)
        {
            resultMsg = e.getMessage ();
            resultCode = "-1";
        } // catch

        // set the result vector
        Vector<String> result = new Vector<String> ();
        result.addElement (resultCode);
        result.addElement (resultMsg);
        result.addElement ("0");

        return result;
    } // resetCounter


    /**************************************************************************
     * Checks out a Businessoject (WebDAV). <BR/>
     *
     * @return  The business object which was checked out.
     *
     * @throws  NoAccessException
     *          The user does not have access to this object to perform the
     *          required operation.
     */
    public BusinessObject webdavCheckOut () throws NoAccessException
    {
        String sourcepath = new String ();
        String targetpath = new String ();
        String targetpath2 = new String ();
        String webdavFilename = new String ();
        ValueDataElement deo;
        WebdavData webdavData = new WebdavData (this.app, this.oid, this.user);

        // path to m2/upload/files:
        sourcepath = webdavData.p_filesDir;
        // path to webdav root + user's webdav directory:
        targetpath = webdavData.p_webdavUserDir;
        // path to webdav root + user's webdav directory + directory of the
        // object:
        targetpath2 = webdavData.p_webdavObjectDir;

        // call the object type specific method:
        this.performCheckOutData (Operations.OP_CHANGE);

//        System.out.println ("starting for loop");
        // loop through the structure:
        for (Iterator<ValueDataElement> iter = this.dataElement.values.iterator (); iter.hasNext ();)
        {
            // get the actual data element:
            deo = iter.next ();

            // check if it is a FILE field and a value has been set:
            if (deo.type.equals (DIConstants.VTYPE_FILE) && deo.value != null &&
                deo.value.length () > 0)
            {
                // get the webdav filename:
                webdavFilename = WebdavData.getWebdavFilename (deo.value,
                    this.checkOutDate);

                // create user's webdav directory:
                if (!FileHelpers.exists (targetpath))
                {
                    FileHelpers.makeDir (targetpath);
                } // if

                // create the webdav directory of the object:
                if (!FileHelpers.exists (targetpath2))
                {
                    FileHelpers.makeDir (targetpath2);
                } // if

                // copy file from m2 file directory to the webdav directory:
                FileHelpers.copyFile (sourcepath + deo.value,
                                      targetpath2 + webdavFilename);
            } // if
        } // for iter

        // return the current object:
        return this;
    } // webdavCheckOut


    /**************************************************************************
     * Checks out a Businessoject (WebDAV). <BR/>
     *
     * @return  The business object which was checked in.
     *
     * @throws  NoAccessException
     *          The user does not have access to this object to perform the
     *          required operation.
     */
    public BusinessObject webdavCheckIn () throws NoAccessException
    {
        String sourcepath2 = new String ();
        String targetpath = new String ();
        String webdavFilename = new String ();
        ValueDataElement deo;
        WebdavData webdavData = new WebdavData (this.app, this.oid, this.user);

        // path to webdav root + user's webdav directory + directory of the
        // object:
        sourcepath2 = webdavData.p_webdavObjectDir;
        // path to m2/upload/files:
        targetpath = webdavData.p_filesDir;

        // check if the user is the one who has checked out the object:
        if (this.user.id == this.checkOutUser.id)
                                        // the user is allowed to check the
                                        // object in?
        {

            // loop through the fields and find the file fields:
            for (Iterator<ValueDataElement> iter = this.dataElement.values.iterator (); iter.hasNext ();)
            {
                // get the actual data element:
                deo = iter.next ();

                // check if it is a FILE field and a value has been set:
                if (deo.type.equals (DIConstants.VTYPE_FILE) &&
                    deo.value != null && deo.value.length () > 0)
                {
                    // get the webdav filename:
                    webdavFilename = WebdavData.getWebdavFilename (deo.value,
                        this.checkOutDate);
                    // copy file back to files directory:
                    FileHelpers.copyFile (sourcepath2 + webdavFilename,
                                          targetpath + deo.value);
                    deo.p_size = FileHelpers.getFileSize (targetpath, deo.value);
                    // delete file in webdav directory:
                    FileHelpers.deleteFile (sourcepath2 + webdavFilename);
                } // if
            } // for iter

            // call the object type specific method:
            // this must be done after the iteration because
            // the this.checkOutDate is within the iteration and this
            // property will be deleted within performCheckInData
            this.performCheckInData (Operations.OP_CHANGE);

            // delete object directory in the user's webdav directory:
            FileHelpers.deleteDir (webdavData.p_webdavObjectDirNoSep);

            // return the current object:
            return this;
        } // if the user is allowed to check the object in

        // check in not allowed
         // raise no access exception:
        throw new NoAccessException (MultilingualTextProvider.getMessage (
            UtilExceptions.EXC_BUNDLE, UtilExceptions.ML_E_NOACCESSEXCEPTION, env));
    } // webdavCheckIn


    /**************************************************************************
     * Creates and returns a copy of this object. <BR/>
     * For any object <tt>x</tt>, the following expressions will be
     * <tt>true</tt>:
     * <blockquote><pre>
     * x.clone () != x
     * x.clone ().getClass () == x.getClass ()
     * x.clone ().equals (x)
     * </pre></blockquote>
     * The object returned by this method is independent of this object (which
     * is being cloned).
     *
     * @return  A clone of this instance. <BR/>
     *
     * @throws  OutOfMemoryError
     *          if there is not enough memory.
     *
     * @see java.lang.Cloneable
     */
    @SuppressWarnings ("unchecked") // suppress compiler warning
    public Object clone () throws OutOfMemoryError
    {
        XMLViewer_01 clone = null;      // the new object

        try
        {
            // call corresponding method of super class:
            clone = (XMLViewer_01) super.clone ();

            // set specific properties:
            // because the clone method of {@link java.lang.Object Object}
            // performs a shallow and not a deep copy of all existing properties
            // we have to perform the deep copy here to ensure that there are
            // no side effects.
            clone.dataElement = (DataElement) this.dataElement.clone ();
            clone.elements = (Vector<ContainerElement>) this.elements.clone ();
            clone.linkedObjectId = (OID) this.linkedObjectId.clone ();
            clone.oid = (OID) this.oid.clone ();
//            clone.p_containerTabs = (TabContainer) this.p_containerTabs.clone ();
//            clone.p_tabs = (TabContainer) this.p_tabs.clone ();
        } // try
        catch (CloneNotSupportedException e)
        {
            return e;
        } // catch CloneNotSupportedException
        catch (OutOfMemoryError e)
        {
            // should not occur
            IOHelpers.printError ("Error when cloning XMLViewer", e, true);
        } // catch OutOfMemoryError

        // return the new object:
        return clone;
    } // clone

} // class XMLViewer_01
