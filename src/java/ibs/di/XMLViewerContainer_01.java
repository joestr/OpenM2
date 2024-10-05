/*
 * Class: XMLViewerContainer_01.java
 */

// package:
package ibs.di;

// imports:
//KR TODO: unsauber
import ibs.bo.BOHelpers;
import ibs.bo.BOPathConstants;
//KR TODO: unsauber
import ibs.bo.Buttons;
//KR TODO: unsauber
import ibs.bo.Datatypes;
//KR TODO: unsauber
import ibs.bo.IncorrectOidException;
//KR TODO: unsauber
import ibs.bo.OID;
//KR TODO: unsauber
import ibs.bo.ObjectClassNotFoundException;
//KR TODO: unsauber
import ibs.bo.ObjectInitializeException;
//KR TODO: unsauber
import ibs.bo.ObjectNotFoundException;
//KR TODO: unsauber
import ibs.bo.Operations;
//KR TODO: unsauber
import ibs.bo.type.Type;
//KR TODO: unsauber
import ibs.bo.type.TypeConstants;
//KR TODO: unsauber
import ibs.bo.type.TypeNotFoundException;
import ibs.di.DataElement;
import ibs.di.DIArguments;
import ibs.di.DIConstants;
import ibs.di.DIHelpers;
import ibs.di.DIMessages;
import ibs.di.DITokens;
import ibs.di.DocumentTemplate_01;
import ibs.di.ValueDataElement;
//KR TODO: unsauber
import ibs.io.IOConstants;
//KR TODO: unsauber
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
//KR TODO: unsauber
import ibs.service.user.User;
import ibs.tech.html.GroupElement;
import ibs.tech.html.IE302;
import ibs.tech.html.InputElement;
import ibs.tech.html.NewLineElement;
import ibs.tech.html.SelectElement;
import ibs.tech.html.TableElement;
import ibs.tech.html.TextElement;
import ibs.tech.sql.DBError;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.StoredProcedure;
import ibs.tech.xml.DOMHandler;
import ibs.util.UtilConstants;
import ibs.util.file.FileHelpers;

import java.util.Iterator;
import java.util.Vector;

import org.w3c.dom.Document;


/******************************************************************************
 * This class represents one object of type XMLViewerContainer with version 01.
 * <BR/>
 * A document template file can be attached to the XMLViewerContainer object.
 * Every new created XMLViewer Object within an XMLViewerContainer will be
 * automatically assigned the document template. There must always be a document
 * template assigned. Additionally a set of attributes can be selected from the
 * document template file that will be used as headers in the list. In case there
 * are no attributes selected the standard headers will be shown. <BR/>
 *
 * @version     $Id: XMLViewerContainer_01.java,v 1.72 2013/01/16 16:14:11 btatzmann Exp $
 *
 * @author      Bernd Buchegger (BB), 990505
 *
 * @see     ibs.bo.BusinessObject
 * @see     ibs.di.DocumentTemplate_01
 * @see     ibs.di.XMLViewer_01
 ******************************************************************************
 */
public class XMLViewerContainer_01 extends XMLContainer_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: XMLViewerContainer_01.java,v 1.72 2013/01/16 16:14:11 btatzmann Exp $";


    /**
     * Separator for the header fields. <BR/>
     */
    private static final String HEADER_FIELD_DELIMITER = "\n";

    /**
     * The template object. <BR/>
     */
    protected DocumentTemplate_01 p_templateObj = null;

    /**
     * OID of the template file to use. <BR/>
     *
     * @deprecated  Use {@link #p_templateObj p_templateObj} instead.
     */
    @Deprecated
    private OID templateOid = null;

    /**
     * import element that holds the document template structure file. <BR/>
     */
    public DataElement dataElement;

    /**
     * OID of the workflow template file to use. <BR/>
     */
    public OID workflowTemplateOid = null;

    /**
     * if workflows are allowed. <BR/>
     */
    public boolean workflowAllowed = true;

    /**
     * Name of the workflow template. <BR/>
     */
    public String workflowTemplateName = "";

    /**
     * Filename of the workflow template. <BR/>
     */
    public String workflowTemplateFileName = "";

    /**
     * path of the workflow template. <BR/>
     */
    public String workflowTemplatePath = "";



    /**************************************************************************
     * This constructor creates a new instance of the class XMLViewerContainer. <BR/>
     */
    public XMLViewerContainer_01 ()
    {
        // call constructor of super class:
        super ();

        // initialize properties common to all subclasses:
    } // XMLViewerContainer_01


    /**************************************************************************
     * This constructor creates a new instance of the class XMLViewerContainer. <BR/>
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
    public XMLViewerContainer_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);

        // initialize properties common to all subclasses:
    } // XMLViewerContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    @Override
    public void initClassSpecifics ()
    {
        // set class specifics of super class:
        super.initClassSpecifics ();

        // set stored procedure names:
        this.procCreate =     "p_XMLViewerContainer_01$create";
        this.procChange =     "p_XMLViewerContainer_01$change";
        this.procRetrieve =   "p_XMLViewerContainer_01$retr";
        this.procDelete =     "p_XMLViewerContainer_01$delete";

        // set number of parameters for procedure calls:
        this.specificRetrieveParameters = 12;
        this.specificChangeParameters   = 5;
    } // initClassSpecifics


    /**************************************************************************
     * Gets the parameters which are relevant for this object. <BR/>
     * The <A HREF="#env">env</A> property is used for getting the parameters.
     * This property must be set before calling this method. <BR/>
     */
    @Override
    public void getParameters ()
    {
        int num = 0;
        String str;
        String[] fields;

        super.getParameters ();
        // get useStandardHeader
        if ((num = this.env.getBoolParam (DIArguments.ARG_USESTANDARDHEADER)) !=
            IOConstants.BOOLPARAM_NOTEXISTS)
        {
            if (num == IOConstants.BOOLPARAM_TRUE)
            {
                this.useStandardHeader = true;
            } // if
            else if (num == IOConstants.BOOLPARAM_FALSE)
            {
                this.useStandardHeader = false;
            } // else
        } //if

        // get templateOid and templateFileName
        if ((str = this.env.getStringParam (DIArguments.ARG_TEMPLATE)) != null)
        {
            try
            {
                // extract the oid and the name out of the <oid>/<filename> pair
                // we get from the template selection box
                String [] tokens = DIHelpers.getTokens (str, "/");
                this.setTemplateOid (new OID (tokens[0]));
//                this.templateName = tokens[1];
//showDebug("getParameters templateName: " + this.templateName);
            } // try
            catch (IncorrectOidException e)
            {
                this.setTemplateOid (null);
//                this.templateName = "";
            } // catch
        } // if ((str = env.getStringParam (DIArguments.ARG_TEMPLATE)) != null)

        // get workflowTemplateOid and workflowTemplateName
        if ((str = this.env.getStringParam (DIArguments.ARG_WORKFLOWTEMPLATE)) != null)
        {
            try
            {
                // extract the oid and the name out of the <oid>/<filename> pair
                // we get from the workflowTemplate selection box
                String [] tokens = DIHelpers.getTokens (str, "/");

                if (tokens.length > 1)
                {
                    this.workflowTemplateOid = new OID (tokens[0]);
                    this.workflowTemplateName = tokens[1];
                } // if (tokens.length > 1)
                else // workflow template selection box is empty
                {
                    this.workflowTemplateOid = OID.getEmptyOid ();
                    this.workflowTemplateName = "";
                } // workflow template selection box is empty
            } // try
            catch (IncorrectOidException e)
            {
                this.workflowTemplateOid = null;
                this.workflowTemplateName = "";
            } // catch
        } // if ((str = env.getStringParam (DIArguments.ARG_TEMPLATE)) != null)

        // get the header fields
        if ((fields = this.env.getMultipleParam (DIArguments.ARG_HEADERFIELDS)) != null)
        {
            this.headerFieldsArray = fields;
            String delim = "";
            this.headerFields = "";
            // componse the header fields string
            for (int i = 0; i < this.headerFieldsArray.length; i++)
            {
                this.headerFields += delim + this.headerFieldsArray[i];
                delim = XMLViewerContainer_01.HEADER_FIELD_DELIMITER;
            } // for
        } // if ((fields  = env.getMultipleParam(DIArguments.ARG_HEADERFIELDS) != null)
    } // getParameters


    /**************************************************************************
     * Represent the properties of a XMLViewerContainer_01 object to the user. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     *
     * @see ibs.IbsObject#showProperty(TableElement, String, String, int, Date)
     */
    @Override
    protected void showProperties (TableElement table)
    {
        // loop through all properties of this object and display them:
        super.showProperties (table);

        // check if we have a template:
        if (this.templateOid != null && !this.templateOid.isEmpty ())
        {
            // template
            this.showProperty (table, DIArguments.ARG_TEMPLATE,
                MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                    DITokens.ML_TEMPLATE, env),
                Datatypes.DT_TEXT, this.p_templateObj.name);
            // use standard header
            this.showProperty (table, DIArguments.ARG_USESTANDARDHEADER,
                MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                    DITokens.ML_USESTANDARDHEADER, env),
                Datatypes.DT_BOOL, "" + this.useStandardHeader);
            // alternative header fields
            this.showProperty (table, DIArguments.ARG_HEADERFIELDS,
                MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                    DITokens.ML_NONSTANDARDHEADER, env),
                Datatypes.DT_TEXTAREA, this.headerFields);
        } // if
        else
        {   // no template available
            // show a message that there are no templates
            this.showProperty (table, DIArguments.ARG_TEMPLATE,
                MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                    DITokens.ML_TEMPLATE, env),
                Datatypes.DT_TEXT,  
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_NOTEMPLATESAVAILABLE, env));
        } // else no template available

        // workflow template
        this.showProperty (table, DIArguments.ARG_WORKFLOWTEMPLATE,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_WORKFLOWTEMPLATE, env),
            Datatypes.DT_TEXT, this.workflowTemplateName);
    } // showProperties


    /**************************************************************************
     * Represent the properties of a XMLViewerContainer_01 object to the user
     * within a form. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     *
     * @see ibs.IbsObject#showFormProperty(TableElement, String, GroupElement)
     */
    @Override
    protected void showFormProperties (TableElement table)
    {
        GroupElement gel;
        GroupElement wfgel;

        // loop through all properties of this object and display them:
        super.showFormProperties (table);

        // check if we have a template
        if (this.templateOid == null || this.templateOid.isEmpty ())
        {
            gel = this.createTemplatesSelectionBox (DIArguments.ARG_TEMPLATE);
            this.showFormProperty (table,   
                MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                    DITokens.ML_TEMPLATE, env), gel);

            // workflow template
            wfgel = this.createWorkflowTemplatesSelectionBox (
                DIArguments.ARG_WORKFLOWTEMPLATE, null, true);
            this.showFormProperty (table,   
                MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                    DITokens.ML_WORKFLOWTEMPLATE, env), wfgel);
        } // if (this.templateOid == null)
        else
        {
            // check if we need to load the dataElement
            if (this.dataElement == null)
            {
                //get the data out of the xml file
                // get the oid out of the path
                String templateOid =
                    DIHelpers.getOidFromPath (this.p_templateObj.path);
                // construct the full filepath
                String filePath = BOHelpers.getFilePath (templateOid);
                filePath = FileHelpers.addEndingFileSeparator (filePath);
                // read the data from the importfile
                this.dataElement =
                    DIHelpers.readDataFile (filePath, this.p_templateObj.fileName, this.env);
            } //if
            // display the template settings
            if (this.dataElement != null)
            {
                // name template
                this.showProperty (table, DIArguments.ARG_TEMPLATENAME,
                    MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                        DITokens.ML_TEMPLATENAME, env),
                    Datatypes.DT_TEXT, this.p_templateObj.name);
                // workflow template
                wfgel = this.createWorkflowTemplatesSelectionBox (
                    DIArguments.ARG_WORKFLOWTEMPLATE,
                    this.workflowTemplateName, true);
                this.showFormProperty (table,   
                    MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                        DITokens.ML_WORKFLOWTEMPLATE, env), wfgel);

                // use standard header
                this.showFormProperty (table,
                    DIArguments.ARG_USESTANDARDHEADER,
                    MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                        DITokens.ML_USESTANDARDHEADER, env),
                    Datatypes.DT_BOOL, "" + this.useStandardHeader);

                // display the fields with checkboxes
                GroupElement group = new GroupElement ();
                NewLineElement nl = new NewLineElement ();
                ValueDataElement value;

                for (Iterator<ValueDataElement> iter = this.dataElement.values.iterator ();
                     iter.hasNext ();)
                {
                    value = iter.next ();

                    // discard separator and remark types
                    if (!value.type.equalsIgnoreCase (DIConstants.VTYPE_SEPARATOR) &&
                        !value.type.equalsIgnoreCase (DIConstants.VTYPE_REMARK) &&
                        !value.type.equalsIgnoreCase (DIConstants.VTYPE_PASSWORD) &&
                        !value.type.equalsIgnoreCase (DIConstants.VTYPE_LONGTEXT) &&
                        !value.type.equalsIgnoreCase (DIConstants.VTYPE_HTMLTEXT))
                    {
                        InputElement input = new InputElement (DIArguments.ARG_HEADERFIELDS, InputElement.INP_CHECKBOX, value.field);
                        // try to find the field in the list of selected fields

                        if (this.headerFields.indexOf (value.field) > -1)
                        {
                            input.checked = true;
                        } // if
                        group.addElement (input);
                        TextElement text = new TextElement (value.field);
                        group.addElement (text);
                        group.addElement (nl);
                    } // if (!value.type.equalsIgnoreCase (DIConstants.VTYPE_SEPARATOR))
                } // for iter

                this.showFormProperty (table,   
                    MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                        DITokens.ML_HEADERFIELDS, env), group);
            } // if (this.dataElement != null)
            else                        // no dataElement found
            {
                // nothing to do
            } // no dataElement found
        } // else (templateOid != null)
    } // showFormProperties


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in a
     * container's content view. <BR/>
     * This method can be overwritten in subclasses to redefine the set of
     * buttons that can be displayed. <BR/>
     *
     * @return  An array with button ids that can potentially be displayed.
     */
    @Override
    protected int[] setContentButtons ()
    {
        // define buttons to be displayed:
        int[] buttons =
        {
            Buttons.BTN_NEW,
            Buttons.BTN_PASTE,
            Buttons.BTN_REFERENCE,
            Buttons.BTN_LISTDELETE,
            Buttons.BTN_LIST_COPY,
            Buttons.BTN_LIST_CUT,
            Buttons.BTN_DISTRIBUTE,
            Buttons.BTN_LISTFORWARD,
        }; // buttons

        // return button array
        return buttons;
    } // setContentButtons sepp


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in an
     * object's info view. <BR/>
     * This method can be overwritten in subclasses to redefine the set of
     * buttons that can be displayed. <BR/>
     *
     * @return  An array with button ids that can potentially be displayed.
     */
    @Override
    protected int[] setInfoButtons ()
    {
        // define buttons to be displayed:
        int [] buttons =
        {
//            Buttons.BTN_NEW,
            Buttons.BTN_EDIT,
            Buttons.BTN_DELETE,
            Buttons.BTN_CUT,
            Buttons.BTN_COPY,
            Buttons.BTN_DISTRIBUTE,
        }; // buttons

        // return button array
        return buttons;
    } // setInfoButtons


    /**************************************************************************
     * Is the object type allowed in workflows? <BR/>
     * This method shall be overwritten in subclasses.
     *
     * @return  <CODE>true</CODE> if the object type is allowed in workflows,
     *          <CODE>false</CODE> otherwise.
     */
    @Override
    protected boolean isWfAllowed ()
    {
        return true;
    } // isWfAllowed


    /**************************************************************************
     * Create a selection box containing the templates the user is allowed to
     * see. <BR/>
     *
     * @param   fieldname   The name of the field with which the selection box
     *                      is displayed.
     *
     * @return  The layout group element containing the created selection box.
     */
    protected GroupElement createTemplatesSelectionBox (String fieldname)
    {
        SQLAction action = null;        // the action object used to access the
                                        // database
        int rowCount;                   // row counter
        GroupElement gel = new GroupElement ();
        SelectElement sel;
        String name;
        StringBuffer queryStr;          // the query string

        // get the elements out of the database:
        // create the SQL String to select all tuples
        queryStr = new StringBuffer ()
            .append (" SELECT o.oid, o.name")
            .append (" FROM v_Container$rights o, ibs_DocumentTemplate_01 t")
            .append (" WHERE o.userId = ").append (this.user.id)
            .append (SQLHelpers.getStringCheckRights (Operations.OP_VIEW))
            .append (" AND o.oid = t.oid ")
            .append (" AND t.objectSuperType != 'DiscXMLViewer'")
            .append (" ORDER BY o.name");
//debug ("QUERYSTRING: " + queryStr);

        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();
            rowCount = action.execute (queryStr, false);

            if (rowCount == 0)          // empty resultset?
            {
                gel.addElement (new TextElement ( 
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_NOTEMPLATESAVAILABLE, env)));
                return gel;           // terminate this method
            } // if empty resultset
            else if (rowCount < 0)      // error while executing
            {
                gel.addElement (new TextElement ( 
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_NOTEMPLATESAVAILABLE, env)));
                return gel;
            } // else if error while executing

            sel = new SelectElement (fieldname, false);
            sel.size = 1;

            // get tuples out of db:
            while (!action.getEOF ())
            {
                // create the option
                // the value of the option is <oid of tempalte>/<name of template>
                // this ensures that we get the 2 values we need
                // the 2 strings must be reconstructed while processing
                OID quOid = SQLHelpers.getQuOidValue (action, "oid");
                name = action.getString ("name");

                // check if oid and name is not null
                if (quOid != null && !name.isEmpty ())
                {
                    // check if the oid of the current template
                    // is the oid of the current selected template
                    boolean isSelected = false;
                    if (this.templateOid != null)
                    {
                        isSelected = this.templateOid.equals (quOid);
                    } // if (this.templateOid != null)

                    // add the name and oid to the selection box
                    sel.addOption (name, "" + quOid + "/" + name, isSelected);
                } // if
                // step one tuple ahead for the next loop
                action.next ();
            } // while
            gel.addElement (sel);
            // end transaction
            action.end ();
        } // try
        catch (DBError dbErr)
        {
            // an error occurred - show name and info
            gel.addElement (new TextElement (dbErr.getMessage () + dbErr.getError ()));
            return gel;
        } // catch
        finally
        {
            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            this.releaseDBConnection (action);
        } // finally
        return gel;
    } // createTemplatesSelectionBox


    /**************************************************************************
     * Retrieve the type specific data that is not got from the stored
     * procedure. <BR/>
     * This method must be overwritten by all subclasses that have to get type
     * specific data that cannot be got from the retrieve data stored procedure.
     *
     * @param action    SQLAction for Databaseoperation
     *
     * @exception    DBError
     *               This exception is always thrown, if there happens an error
     *               during accessing data.
     */
    @Override
    protected void performRetrieveSpecificData (SQLAction action) throws DBError
    {
        DocumentTemplate_01 mayContainTemplate;
        DataElement mayContainDataElement = null;

        // initialize used properties
        this.childValues = new Vector<ValueDataElement> ();

        // try to get mayContainTemplate
        if ((mayContainTemplate = this.p_templateObj) == null)
        {
            return;
        } // if

        // de for maycontain-type:
        mayContainDataElement = mayContainTemplate.getTemplateDataElement ();

        // set table of content Type:
        if (mayContainDataElement != null)
        {
            this.contentTypeTable = mayContainDataElement.tableName;
        } // if
        else
        {
            this.contentTypeTable = null;
        } // else

        // check if maycontain - type is db-mapped
        if (this.contentTypeTable != null &&
            !this.contentTypeTable.trim ().isEmpty ())
        {
            this.getContentFromDB = true;
        } // if
        else
        {
            this.getContentFromDB = false;
        } // else

        // get definition of which fields should be shown in content
        this.setFieldsAndAttributes (mayContainDataElement);
    } // performRetrieveSpecificData


    /**************************************************************************
     * Fetch an documenttemplate via its oid.
     *
     * @param   templateOid The oid of the template to be fetched.
     *
     * @return  found documenttemplate,
     *          <CODE>null</CODE> if an error occurred.
     */
    DocumentTemplate_01 fetchTemplate (OID templateOid)
    {
        if (templateOid != null && templateOid.tVersionId > 0)
        {
            try
            {
                return (DocumentTemplate_01)
                    this.getObjectCache ().fetchObject (this.templateOid,
                                                        this.user,
                                                        this.sess,
                                                        this.env, true);
            } // try
            catch (ObjectNotFoundException e)
            {
                IOHelpers.showMessage (
                    "XMLViewerContainer_01.performRetrieveSpecificData: " +
                    "ObjectNotFoundException when fetching mayContainTemplate",
                    this.app, this.sess, this.env);
                return null;
            } // catch
            catch (TypeNotFoundException e)
            {
                IOHelpers.showMessage (
                    "XMLViewerContainer_01.performRetrieveSpecificData: " +
                    "TypeNotFoundException when fetching mayContainTemplate",
                    this.app, this.sess, this.env);
                return null;
            } // catch
            catch (ObjectClassNotFoundException e)
            {
                IOHelpers.showMessage (
                    "XMLViewerContainer_01.performRetrieveSpecificData: " +
                    "ObjectClassNotFoundException when fetching mayContainTemplate",
                    this.app, this.sess, this.env);
                return null;
            } // catch
            catch (ObjectInitializeException e)
            {
                IOHelpers.showMessage (
                    "XMLViewerContainer_01.performRetrieveSpecificData: " +
                    "ObjectClassNotFoundException when fetching mayContainTemplate",
                    this.app, this.sess, this.env);
                return null;
            } // catch
        } // if

        return null;
    } // fetchTemplate


    /**************************************************************************
     * Sets names of fields which are defined in XML-Definition in
     * string array this.headerFiels. <BR/>
     * Sets the db-attributes for this fields, if content is retrieved from db.
     * Sets valueDefinition for formatating in content gui.
     *
     * @param   childDataElem   dataElement of childType if here is only one.
     *
     * @throws  DBError
     *          An error occurred during database access.
     */
    private void setFieldsAndAttributes (DataElement childDataElem)
        throws DBError
    {
        ValueDataElement childVde = null;
        int size;

        // set headernames for content
        if (this.headerFieldsArray != null)
        {
            // get the vector with the header fields from the
            // template dataElement
            if ((size = this.headerFieldsArray.length) > 0)
            {
                this.headerAttributesArray = new String [size];

                for (int i = 0; i < size; i++)
                {
                    String field = this.headerFieldsArray [i];
                    // set db attribute of current field for headers
                    if (this.getContentFromDB)
                    {
                        // get ValueDataElement for current column
                        if ((childVde = childDataElem.getValueElement (field)) != null)
                        {
                            // get mappingField for current Field
                            this.headerAttributesArray[i] =
                                childVde.mappingField;

                            // if current field is fieldRef set specific
                            // db-attribute
                            if (DIConstants.VTYPE_FIELDREF.equals (childVde.type) ||
                                    DIConstants.VTYPE_VALUEDOMAIN.startsWith (childVde.type))
                            {
                                this.headerAttributesArray[i] += "_VALUE";
                            } // if

                            // check if value is type which is saved as CLOB/TEXT
                            // because in MSSQL, TEXT values could not be read
                            // from db via query
                            if (DIConstants.VTYPE_OPTION.equals (childVde.type))
                            {
                                IOHelpers.showMessage (
                                    "VALUEs with type " +
                                    childVde.type +
                                    " can not be shown in XMLViewerContainer." +
                                    " VALUE [" + childVde.field + "]" +
                                    " use VALUE TYPE='SELECTIONBOX' instead",
                                    this.env);
                            } // if
                            else
                            {
                                // remark fielddefinition of currentField
                                this.childValues.addElement (childVde);
                            } // else
                        } // if
                        else
                        // if ValueDataElement for column does not exist
                        // (if the  columnname does not exist as value)
                        {
                            // set attribute to null
                            this.headerAttributesArray[i] = null;
                        } // else
                    } // if  ValueDataElement for current column exist
                    else
                        // else ValueDataElement for column does not exist
                    {
                        this.childValues.addElement (null);
                    } // esle
                } // for i
            } // if (size > 0)
        } // if (dataElement != null)
    } // setHeaderFieldsAndAttributes


    /**************************************************************************
     * Reads the object data from an dataElement. <BR/>
     *
     * @param dataElement     the dataElement to read the data from
     */
    @Override
    public void readImportData (DataElement dataElement)
    {
        // get business object specific values
        super.readImportData (dataElement);

        // get the type specific values
        if (dataElement.exists ("useStandardHeader"))
        {
            this.useStandardHeader = dataElement.getImportBooleanValue ("useStandardHeader");
        } // if
        if (dataElement.exists ("headerFields"))
        {
            this.headerFields = dataElement.getImportStringValue ("headerFields");
            // check if header fields are empty
            if (this.headerFields != null && !this.headerFields.trim ().isEmpty ())
            {
                this.headerFieldsArray = DIHelpers.getTokens (
                    this.headerFields,
                    XMLViewerContainer_01.HEADER_FIELD_DELIMITER);
            } // if
            else
            {
                this.headerFieldsArray = null;
            } // else
        } // if (dataElement.exists ("headerFields"))

        // the field 'templateOID' holds the OID of the template object.
        if (dataElement.exists ("templateOID"))
        {
            String oidString = dataElement.getImportStringValue ("templateOID");
            // did we get any valid oid?
            if (oidString != null &&
                oidString.toLowerCase ().startsWith (UtilConstants.NUM_START_HEX))
            {
                try
                {
                    this.setTemplateOid (new OID (oidString));
                } // try
                catch (IncorrectOidException e)
                {
                    // should not occur, thus display an error message:
                    IOHelpers.showMessage (
                        "XMLViewerContainer_01.readImportData: templateOid \"" +
                            oidString + "\" not valid", e, this.app, this.sess,
                        this.env, true);
                } // catch
            } // if
        } // if (dataElement.exists ("templateOID"))
        // the field 'templateTypeCode' holds the type code of the template
        else if (dataElement.exists ("templateTypeCode"))
        {
            // get the type code of the template object
            String templateTypeCode =
                dataElement.getImportStringValue ("templateTypeCode");
            if (templateTypeCode != null && !templateTypeCode.isEmpty ())
            {
                // get the type:
                Type type = this.getTypeCache ().findType (templateTypeCode);

                // check if the type was found:
                if (type != null)
                {
                    // set the template object:
                    this.setTemplate ((DocumentTemplate_01) type.getTemplate ());
                } // if
                else
                {
                    // should not occur, thus display an error message:
                    IOHelpers.showMessage (
                        "XMLViewerContainer_01.readImportData: invalid" +
                        " template type code \"" + templateTypeCode + "\"",
                        this.app, this.sess, this.env);
                } // else
            } // if valid template path
        } // if (dataElement.exists ("templateTypeCode"))
        // the field 'templatePath' holds the m2 path of the template object.
        else if (dataElement.exists ("templatePath"))
        {
            // get the path of the template object
            String templatePath = dataElement.getImportStringValue ("templatePath");
            if (templatePath != null && !templatePath.isEmpty ())
            {
                // get the OID of the template object
                boolean[] isContainer = new boolean[1];
                OID oid = BOHelpers.resolveObjectPath (templatePath,
                    isContainer, this, this.env);

                // check if the OID has the currect type
                if (oid != null &&
                    oid.type == this.getTypeCache ().getTypeId (TypeConstants.TC_DocumentTemplate))
                {
                    this.setTemplateOid (oid);
                } // if oid has correct type
                else
                {
                    // should not occur, thus display an error message:
                    IOHelpers.showMessage (
                        "XMLViewerContainer_01.readImportData: invalid" +
                        " template OID \"" + oid + "\"",
                        this.app, this.sess, this.env);
                } // else
            } // if valid template path
        } // if (dataElement.exists ("templateName"))

        // the field 'workflowTemplateOID' holds the OID of the
        // workflow template object.
        if (dataElement.exists ("workflowTemplateOID"))
        {
            String oidString = dataElement.getImportStringValue ("workflowTemplateOID");
            // check if a valid oid has been set
            if (oidString != null &&
                oidString.toLowerCase ().startsWith (UtilConstants.NUM_START_HEX))
            {
                try
                {
                    this.workflowTemplateOid = new OID (oidString);
                } // try
                catch (IncorrectOidException e)
                {
                    // should not occur, thus display an error message:
                    IOHelpers.showMessage (
                        "XMLViewerContainer_01.readImportData: invalid workflow" +
                        " template OID \"" + oidString + "\"",
                        e, this.app, this.sess, this.env, true);
                } // catch
            } // if (oidString != null &&
        } // if (dataElement.exists ("workflowTemplateOID"))
        // the field 'workflowTemplatePath' holds the m2 path of the
        // workflow template object.
        else if (dataElement.exists ("workflowTemplatePath"))
        {
            // get the path of the workflow template object
            String templatePath = dataElement.getImportStringValue ("workflowTemplatePath");
            if (templatePath != null && !templatePath.isEmpty ())
            {
                // get the OID of the workflow template object
                boolean[] isContainer = new boolean[1];
                OID oid = BOHelpers.resolveObjectPath (templatePath,
                    isContainer, this, this.env);

                // check if the OID has the currect type
                if (oid != null &&
                    oid.type == this.getTypeCache ().getTypeId (TypeConstants.TC_WorkflowTemplate))
                {
                    this.workflowTemplateOid = oid;
                } // if oid has correct type
                else
                {
                    // should not occur, thus display an error message:
                    IOHelpers.showMessage (
                        "XMLViewerContainer_01.readImportData: invalid" +
                        " workflow template OID \"" + oid + "\"",
                        this.app, this.sess, this.env);
                } // else
            } // if valid template path
        } // if (dataElement.exists ("workflowTemplateName"))
    } // readImportData


    /**************************************************************************
     * writes the object data to an dataElement. <BR/>
     *
     * @param dataElement     the dataElement to write the data to
     */
    @Override
    public void writeExportData (DataElement dataElement)
    {
        // set the business object specific values
        super.writeExportData (dataElement);

        // set the type specific values
        dataElement.setExportValue ("useStandardHeader", this.useStandardHeader);
        dataElement.setExportValue ("headerFields", this.headerFields);
        dataElement.setExportValue ("templateOID", this.templateOid.toString ());
        dataElement.setExportValue ("templateTypeCode",
                                    this.p_templateObj.getObjectTypeCode ());
        dataElement.setExportValue ("workflowTemplateOID", this.workflowTemplateOid.toString ());
    } // writeExportData


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
        // set the specific parameters:
        // useStandardHeader
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN,
                        this.useStandardHeader);
        // templateOid
        sp.addInParameter (ParameterConstants.TYPE_STRING,
                        (this.templateOid == null) ?
                        "" :
                        this.templateOid.toString ());

        // headerFields
        sp.addInParameter (ParameterConstants.TYPE_STRING,
                        (this.headerFields == null) ?
                        "" :
                        this.headerFields);
        // workflowTemplateOid
        sp.addInParameter (ParameterConstants.TYPE_STRING,
                        (this.workflowTemplateOid == null) ?
                        "" :
                        this.workflowTemplateOid.toString ());
        //workflowAllowed
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN,
                        this.workflowAllowed);
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
        int i = lastIndex;              // initialize params index

        // set the specific parameters:
        // useStandardHeader
        params[++i] = sp.addOutParameter(ParameterConstants.TYPE_BOOLEAN);
        // templateOid
        params[++i] = sp.addOutParameter(ParameterConstants.TYPE_VARBYTE);
        // template typeId
        params[++i] = sp.addOutParameter(ParameterConstants.TYPE_INTEGER);
        // headerFields
        params[++i] = sp.addOutParameter(ParameterConstants.TYPE_STRING);
        // templateName
        params[++i] = sp.addOutParameter(ParameterConstants.TYPE_STRING);
        // templateFileName
        params[++i] = sp.addOutParameter(ParameterConstants.TYPE_STRING);
        // templatePath
        params[++i] = sp.addOutParameter(ParameterConstants.TYPE_STRING);
        // workflowTemplateOid
        params[++i] = sp.addOutParameter(ParameterConstants.TYPE_VARBYTE);
        //workflowAllowed
        params[++i] = sp.addOutParameter(ParameterConstants.TYPE_BOOLEAN);
        // workflowTemplateName
        params[++i] = sp.addOutParameter(ParameterConstants.TYPE_STRING);
        // workflowTemplateFileName
        params[++i] = sp.addOutParameter(ParameterConstants.TYPE_STRING);
        // workflowTemplatePath
        params[++i] = sp.addOutParameter(ParameterConstants.TYPE_STRING);

        return i;                       // return the current index
    } // setSpecificRetrieveParameters


    /**************************************************************************
     * Get the data for the additional (type specific) parameters for
     * performRetrieveData. <BR/>
     * This method must be overwritten by all subclasses that have to get
     * type specific data from the retrieve data stored procedure.
     *
     * @param params        The array of parameters from the retrieve data stored
     *                   procedure.
     * @param lastIndex The index to the last element used in params thus far.
     */
    @Override
    protected void getSpecificRetrieveParameters (Parameter[] params,
                                                  int lastIndex)
    {
        int i = lastIndex;              // initialize params index

        // get the specific parameters:
        // get type specific values
        this.useStandardHeader = params[++i].getValueBoolean ();
        // template OID
        this.setTemplateOid (SQLHelpers.getSpOidParam (params[++i]));
        // tVersionId of the form type
        ++i;
        // headerFields
        this.headerFields = params[++i].getValueString ();
        // check if header fields are empty
        if (this.headerFields != null && !this.headerFields.trim ().isEmpty ())
        {
            this.headerFieldsArray = DIHelpers.getTokens (this.headerFields,
                XMLViewerContainer_01.HEADER_FIELD_DELIMITER);
        } // if
        else
        {
            this.headerFieldsArray = null;
        } // else
        // templateName
//        this.templateName = params[++i].getValueString ();
        ++i;
        // templateFileName
        ++i;
        // templatePath
        ++i;
        // workflowTemplateOid
        this.workflowTemplateOid = SQLHelpers.getSpOidParam (params[++i]);
        // workflowAllowed
        this.workflowAllowed = params[++i].getValueBoolean ();
        // templateName
        this.workflowTemplateName = params[++i].getValueString ();
        // templateFileName
        this.workflowTemplateFileName = params[++i].getValueString ();
        // templatePath
        this.workflowTemplatePath = params[++i].getValueString ();
    } // getSpecificRetrieveParameters


    /**************************************************************************
     * Generates the HTML code for the content view by using a stylesheet file.
     * Which should be named &lt;typecode&gt;_content.xsl. <BR/>
     *
     * @return      the HTML code or <CODE>null</CODE>
     *              if no stylesheet is defined.
     */
    @Override
    protected String showContentXSL ()
    {
        // get typecode of content objects
        // and check if there are any content objects
        DocumentTemplate_01 mayContainTemplate = null;
        if ((mayContainTemplate = this.p_templateObj) == null ||
            this.elements.size () == 0 ||
            mayContainTemplate.getTemplateDataElement () == null)
        {
            return null;
        } // if

        // crate path to xslfile which is used for content view with
        // childobjecttypes
        String xslFile = this.app.p_system.p_m2AbsBasePath +
            BOPathConstants.PATH_XSLT +
            mayContainTemplate.getTemplateDataElement ().p_typeCode +
            "_content.xsl";

        this.trace ("XSLT File=" + xslFile);

        // create DomHandler to process xslt transformation:
        DOMHandler processor = new DOMHandler (this.env, this.sess, this.app);
        // get dom tree of current object
        Document doc = this.createDomTree (XMLContainer_01.VIEWMODE_CONTENT);
        processor.serializeDOM (doc);

        // show domtree
        if (mayContainTemplate.getDOMTree () || 
            this.user.username.equalsIgnoreCase(IOConstants.USERNAME_ADMINISTRATOR))
        {
            String domString = processor.domToString (doc, "OBJECTS");
            // write the text non formatted
            this.createHTMLHeader (this.app, this.sess, this.env);
            DIHelpers.showDOMInfo (this.env, domString, this.p_templateObj);
            this.createHTMLFooter (this.env);
        } // if (mayContainTemplate.getDOMTree () || ...

        // if a type specific stylesheet file exists
        // generate the layout using this file.
        if (FileHelpers.exists (xslFile))
        {
            return processor.process (doc, xslFile);
        } // if

        xslFile = this.app.p_system.p_m2AbsBasePath +
                  BOPathConstants.PATH_XSLT + "genericcontent.xsl";

        // if a generic stylesheet file exists
        // generate the layout using this file.
        if (FileHelpers.exists (xslFile))
        {
            return processor.process (doc, xslFile);
        } // if

        return null;
    } // showContentXSL


    /**************************************************************************
     * Set the template. <BR/>
     *
     * @param   templateObj The template object.
     */
    private void setTemplate (DocumentTemplate_01 templateObj)
    {
        if (templateObj != null)
        {
            this.p_templateObj = templateObj;
            this.templateOid = templateObj.oid;
        } // if
        else
        {
            this.p_templateObj = null;
            this.templateOid = null;
        } // else
    } // setTemplate


    /**************************************************************************
     * Set the template through its oid. <BR/>
     * This method gets the template object out of the type cache and sets
     * this template.
     *
     * @param   oid     The oid of the template to be set.
     */
    private void setTemplateOid (OID oid)
    {
        if (oid != null)
        {
            this.setTemplate ((DocumentTemplate_01)
                this.getTypeCache ().getTemplate (oid));
        } // if
        else
        {
            this.setTemplate (null);
        } // else
    } // setTemplateOid


    /**************************************************************************
     * Get the ids of all types which are allowed to be contained within the
     * actual object type. <BR/>
     * This method depends on the value of the oid and gets the corresponding
     * object type out of the object pool. Then it gets the types which may be
     * contained within this type out of the type itself.
     *
     * @return  An array with the allowed type ids.
     */
//**************
//* HACK by MS!!
    @Override
    public String[] getTypeIds ()
    {
//showDebug ("getTypeIds: typeID = " + this.p_templateTVersionId);
        // CONSTRAINT: a template object must be set
        if (this.p_templateObj == null)
        {
            return null;
        } // if

        int templateTVersionId = this.p_templateObj.getObjectTVersionId ();
        String[] typeIds = null;        // the type ids for the object type

        // check if there is a valid template tVersionId:
        if (templateTVersionId != 0)
        {
            typeIds = new String[1];
            typeIds[0] = "" + templateTVersionId;
        } // if (this.p_templateTVersionId != 0)

        return typeIds;                 // return the computed type ids
    } // getTypeIds
//* HACK by MS!!
//**************


    /**************************************************************************
     * Check if an object of a specific type can be inserted within this
     * object. <BR/>
     * The parameter should be a valid tVersionId. Currently this method also
     * works if this is just a type id.
     *
     * @param   type    Type of the object which shall be inserted.
     *
     * @return true if the object is allowed to be inserted, false otherwise.
     */
    @Override
    public boolean isAllowedType (int type)
    {
        String[] typeIds = this.getTypeIds (); // ids of all allowed types
        if (typeIds != null)
        {
            for (int i = 0; i < typeIds.length; i++)
            {
                if (typeIds [i].equals ("" + type))
                {
                    return true;
                } // if
            } // for (int i = 0; i < typeIds.length; i++)
        } // if (typeIds != null)
        return false;
    } // isAllowedType

} // class XMLViewerContainer_01
