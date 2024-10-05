/*
 * Class: XMLViewerContainer_01.java
 */

// package:
package ibs.di;

// imports:
//KR TODO: unsauber
import ibs.app.AppFunctions;
import ibs.app.AppMessages;
import ibs.bo.BOArguments;
import ibs.bo.BOConstants;
import ibs.bo.BOHelpers;
import ibs.bo.BOListConstants;
import ibs.bo.BOPathConstants;
import ibs.bo.BOTokens;
import ibs.bo.BusinessObject;
import ibs.bo.Container;
import ibs.bo.ContainerElement;
import ibs.bo.IncorrectOidException;
import ibs.bo.OID;
import ibs.bo.Operations;
import ibs.bo.cache.ObjectPool;
import ibs.bo.path.ObjectPathNode;
import ibs.bo.tab.TabConstants;
import ibs.bo.type.Type;
import ibs.bo.type.TypeNotFoundException;
import ibs.di.service.DBMapper;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.io.LayoutConstants;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.query.QueryCreator_01;
import ibs.obj.query.QueryExceptions;
import ibs.obj.query.QueryFactory;
import ibs.obj.query.QueryNotFoundException;
import ibs.service.user.User;
import ibs.service.workflow.WorkflowConstants;
import ibs.tech.html.BuildException;
import ibs.tech.html.FormElement;
import ibs.tech.html.IE302;
import ibs.tech.html.InputElement;
import ibs.tech.html.Page;
import ibs.tech.html.ScriptElement;
import ibs.tech.html.StyleSheetElement;
import ibs.tech.html.TextElement;
import ibs.tech.sql.DBError;
import ibs.tech.sql.DBQueryException;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLConstants;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.SelectQuery;
import ibs.tech.xml.DOMHandler;
import ibs.tech.xml.XMLWriter;
import ibs.tech.xml.XMLWriterException;
import ibs.util.DateTimeHelpers;
import ibs.util.Helpers;
import ibs.util.NameAlreadyGivenException;
import ibs.util.NoAccessException;
import ibs.util.StringHelpers;
import ibs.util.UtilConstants;
import ibs.util.file.FileHelpers;

import java.util.Date;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/******************************************************************************
 * This class represents one object of type XMLContainer with version 01.
 * <BR/>
 *
 * @version     $Id: XMLContainer_01.java,v 1.65 2012/04/20 09:48:18 btatzmann Exp $
 *
 * @author      Bernd Buchegger (BB), 990505
 ******************************************************************************
 */
public class XMLContainer_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
            "$Id: XMLContainer_01.java,v 1.65 2012/04/20 09:48:18 btatzmann Exp $";


    /**
     * Separator for the header fields. <BR/>
     */
    private static final String HEADER_FIELD_DELIMITER = "\n";

    /**
     * Flag to use the standard headers in the list. <BR/>
     */
    public boolean useStandardHeader = true;

    /**
     * alternative fields to use in the header. <BR/>
     */
    public String headerFields = "";

    /**
     * array of additional fields to use in the header. <BR/>
     */
    public String[] headerFieldsArray = null;

    /**
     * array of tokens to use in the header. <BR/>
     */
    public String[] headerTokensArray = null;

    /**
     * array of boolean values to mark the system fields in the header. <BR/>
     */
    public boolean[] headerIsSysFieldsArray = null;

    /**
     * array of alternative db attributes for fields to use in the header. <BR/>
     */
    public String[] headerAttributesArray = null;

    /**
     * Vector with ValueDataElements of childtype which should be shown.
     * in this container. <BR/>
     */
    protected Vector<ValueDataElement> childValues =
        new Vector<ValueDataElement> ();

    /**
     * Table which is used for contentType (if only one exist). <BR/>
     */
    protected String contentTypeTable = null;

    /**
     * Indicates if the content is retrieved from database. <BR/>
     */
    protected boolean getContentFromDB = true;

    /**
     * which types could be contained from this xml-containertype.
     * separated with ',';
     */
    protected String mayContain = null;

    /**
     * The container template can define a alternative token for the
     * name column.
     */
    public String p_nameToken = null;

    /**
     * The container content query can be extended by an additional query.
     */
    protected String p_extensionQueryName = null;

    /**
     * Separator for OID lists.
     */
    protected String OID_LIST_SEPARATOR = "|";
    
    // viewmodes
    /**
     * View mode: content. <BR/>
     */
    public static final int VIEWMODE_CONTENT       = 4;

    /**
     * view mode for xsl: content edit
     */
    public static final int VIEWMODE_CONTENTEDIT      = 6;

    /**************************************************************************
     * This constructor creates a new instance of the class XMLContainer. <BR/>
     */
    public XMLContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();
    } // XMLContainer_01


    /**************************************************************************
     * This constructor creates a new instance of the class XMLContainer. <BR/>
     * It calls the constructor of the super class.
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    @Deprecated
    public XMLContainer_01 (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);
    } // XMLContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    @Override
    public void initClassSpecifics ()
    {
        // set class specifics of super class:
        super.initClassSpecifics ();

        // set name of specific container element:
        this.elementClassName = "ibs.di.XMLViewerContainerElement_01";
        this.viewContent = "v_XMLContainer_01$content";
    } // initClassSpecifics


    /**************************************************************************
     * Sets names of fields which are defined in XML-Definition in
     * string array this.headerFields. <BR/>
     * Sets the db-attributes for this fields, if content is retrieved from db.
     * Sets valueDefinition for formatting in content gui. <BR/>
     * <PRE>
     * ...
     * &lt;CONTENT&gt;
     *   &lt;COLUMN NAME="..." TOKEN="..." EXTENDED="FALSE"/&gt;
     *   ...
     * &lt;/CONTENT&gt;
     * ...
     * </PRE>
     *
     * @param dataElement       dataElement of this container.
     * @param childDataElem     dataElement of childType if here is only one.
     * @param action SQLAction to execute queries. <BR/>
     *
     * @throws  DBError
     *          There occurred an error during database access.
     */
    protected void setFieldsAndAttributes (DataElement dataElement,
                                           DataElement childDataElem,
                                           SQLAction action)
        throws DBError
    {
        ValueDataElement childVde = null;
        ReferencedObjectInfo columnInfo;
        String delim = "";
        String columnName;
        String columnToken;
        String headerAttr;

        // set headernames for content
        if (dataElement != null && dataElement.p_headerFields != null)
        {
            // get the vector with the header fields from the
            // template dataElement
            int size = dataElement.p_headerFields.size ();
            if (size > 0)
            {
                this.useStandardHeader = false;
                this.headerFields = "";
                this.headerFieldsArray = new String [size];
                this.headerTokensArray = new String [size];
                this.headerAttributesArray = new String [size];
                this.headerIsSysFieldsArray = new boolean [size];

                // loop through the header fields set in the template
                for (int i = 0; i < size; i++)
                {
                    // get the column info
                    columnInfo = dataElement.p_headerFields.elementAt (i);
                    columnName = columnInfo.getName ();
                    columnToken = columnInfo.getMultilangToken (this.env).getName ();
                    // set the headerfields info
                    this.headerFieldsArray [i] = columnName;
                    this.headerTokensArray [i] = /*columnInfo.isSysField () ? 
                        MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, columnToken, env) : */columnToken;
                    this.headerIsSysFieldsArray[i] = columnInfo.isSysField ();

                    // set the headerfields
                    // BB: this property does not seem to be in use?
                    this.headerFields += delim + columnName;
                    delim = XMLContainer_01.HEADER_FIELD_DELIMITER;

                    // check if it is a system field
                    if (columnInfo.isSysField ())
                    {
                        headerAttr = DIHelpers.getSysFieldDBField (columnName);
                        this.headerAttributesArray [i] = headerAttr;
                    } // if (columnInfo.isSysField())
                    else if (columnInfo.isExtendedField ())
                    {
                        // this is an extended field that gets its value from
                        // an extension query to the containers content query
                        // we create a pseudo value data element that
                        // acts as column definition.
                        childVde = new ValueDataElement ();
                        childVde.field = columnName;
                        childVde.type = DIConstants.VTYPE_TEXT;
                        childVde.mappingField = columnName;
                        childVde.p_isExtendedColumn = true;
                        this.childValues.addElement (childVde);
                        // set the columnName in the atributes array in order
                        // to make it sortable.
                        this.headerAttributesArray [i] = columnName;
                    } // else if (columnInfo.isExtendedField())
                    else    // not a system field
                    {
                        // is it possible to read the content from the DB
                        // only in that case the result can be sorted
                        if (this.getContentFromDB)
                        {
                            // set db attribute of current field for headers
                            headerAttr = null;
                            // check if we have a system field
                            // get ValueDataElement for current column
                            childVde = childDataElem.getValueElement (columnName);
                            if (childVde != null)
                            {
                                // the db mapping field can be set as header attribute
                                headerAttr = childVde.mappingField;
                                // if current field is fieldRef set correct
                                // db-attribute
                                if (DIConstants.VTYPE_FIELDREF.equals (childVde.type) ||
                                        DIConstants.VTYPE_VALUEDOMAIN.startsWith (childVde.type))
                                {
                                    headerAttr += "_VALUE";
                                } // if (DIConstants.VTYPE_FIELDREF.equals (childVde.type)||
                                  //DIConstants.VTYPE_VALUEDOMAIN.equals (childVde.type))
                                // check if value is type which is saved as CLOB/TEXT
                                // because in MSSQL, TEXT values could not be read
                                // from db via query
                                if (DIConstants.VTYPE_OPTION.equals (childVde.type))
                                {
                                    IOHelpers.showMessage ("VALUEs with type " +
                                        childVde.type +
                                        " can not be shown in XMLContainer." +
                                        " VALUE [" + childVde.field + "]",
                                        this.env);
                                } // if (DIConstants.VTYPE_OPTION.equals (childVde.type))
                                else // type can be sorted
                                {
                                    // remark field definition of currentField
                                    this.childValues.addElement (childVde);
                                    this.headerAttributesArray [i] = headerAttr;
                                } // else type can be sorted
                            } // if (childVde != null)
                            else    // no ValueDataElement for column found
                            {
                                // no value data element found.
                                // this must be an syscolumn
                                // or an extended column
                            } // else no ValueDataElement for column found
                        } // if (this.getContentFromDB)
                        else // data is not read from db. column cannot be sorted
                        {
                            this.childValues.addElement (null);
                            this.headerAttributesArray [i] = null;
                        } // else // data is not read from db. column cannot be sorted
                    } // if (field != null && field.lenght () > 0)
                } // else not a system field
            } // for i
        } // if (dataElement != null)
    } // setHeaderFieldsAndAttributes


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
        // initialize used properties
        this.useStandardHeader = true;
        this.headerFields = "";
        this.headerFieldsArray = null;
        this.childValues = new Vector<ValueDataElement> ();

        // get documenttemplate for this container
        DocumentTemplate_01 template = (DocumentTemplate_01) this.typeObj.getTemplate ();
        DataElement dataElement = template.getTemplateDataElement ();
        DocumentTemplate_01 mayContainTemplate = null; // dt for maycontain-type
        DataElement mayContainDataElement = null;    // de for maycontain-type

        // set mayContain:
        this.mayContain = template.getMayContain ();
        try
        {
            Vector<DocumentTemplate_01> mayContainTemplates =
                template.getMayContainTemplates ();

            // check if there are any may contain templates:
            if (mayContainTemplates != null && mayContainTemplates.size () > 0)
            {
                // if there is only one mayContain Type, get template of type:
                if (mayContainTemplates.size () == 1)
                {
                    // if there is only one may contain type, the content
                    // will be retrieved from db:
                    this.getContentFromDB = true;

                    // get the document template for the first type which can be
                    // contained in this container
                    mayContainTemplate = mayContainTemplates.firstElement ();

                    // check if the template exists:
                    if (mayContainTemplate != null) // template-based object type?
                    {
                        // get DataElement of mayContainType to get tableName of child-type
                        mayContainDataElement =
                            mayContainTemplate.getTemplateDataElement ();
                        this.contentTypeTable = mayContainDataElement.tableName;
                    } // if
                    else                    // basic object type
                    {
                        // set basic table:
                        this.contentTypeTable = "ibs_Object";
                    } // else if basic object type
                } // if
                else                    // more than one allowed data type?
                {
                    // initialize the template:
                    mayContainTemplate = null;
                    boolean templateFound = false;

                    // loop through all datatypes to check if they are all basic types
                    // (i.e. they don't have template files):
                    for (Iterator<DocumentTemplate_01> iter = mayContainTemplates.iterator ();
                         iter.hasNext ();)
                    {
                        if (iter.next () != null)
                        {
                            templateFound = true;
                            break;
                        } // if
                    } // for iter

                    // if all types have no template they are all basic types and the
                    // data may be read from the database:
                    this.getContentFromDB = !templateFound;

                    // check for basic object type:
                    if (!this.getContentFromDB)
                    {
                        // set basic table:
                        this.contentTypeTable = "ibs_Object";
                    } // if
                } // else
            } // if
            else
            {
                // no content type defined
                // don't retrieve the content from database:
                this.getContentFromDB = false;
            } // else
        } // try
        catch (TypeNotFoundException e)
        {
            // set flag:
            this.getContentFromDB = false;
        } // catch

        // check again if any columns have been set
        // if not we can get the data from the database because only
        // standard headers will be shown no matter what has been set before
        if (dataElement.p_headerFields == null ||
            dataElement.p_headerFields.size () == 0)
        {
            this.getContentFromDB = true;
        } // if (dataElement.p_headerFields == null || ...)

        // check if an alternative name token has been set
        if (dataElement.p_nameToken != null)
        {
            this.p_nameToken = dataElement.p_nameToken;
        } // if (dataElement.p_nameToken != null)

        // check if an extension query name has been set
        if (dataElement.p_extensionQueryName != null)
        {
            this.p_extensionQueryName = dataElement.p_extensionQueryName;
        } // if (dataElement.p_nameToken != null)

        // get definition of which fields should be shown in content
        this.setFieldsAndAttributes (dataElement, mayContainDataElement, action);
    } // performRetrieveSpecificData


    /**************************************************************************
     * Create the query to get the container's content out of the database.
     * <BR/>
     * The query or view must at least have the attributes userId and rights.
     * Queries on these attributes have to be addable to this query. <BR/>
     * The query must have at least one constraint within WHERE to ensure that
     * other constraints can be added with AND. <BR/>
     * <B>Standard format:</B>. <BR/>
     *      "SELECT oid, &lt;other attributes&gt; " +
     *      " FROM " + this.viewContent +
     *      " WHERE containerId = " + oid;. <BR/>
     * This method can be overwritten in subclasses. <BR/>
     * As you can see the query should contain at least the oid.
     *
     * @return  The constructed query.
     */
    @Override
    protected String createQueryRetrieveContentData ()
    {
        try
        {
            // construct the common query and return the result:
            return this.createSQLQueryRetrieveContentData ().toValidStringBuilder ()
                .toString ();
        } // try
        catch (DBQueryException e)
        {
            IOHelpers.showMessage ("Error when constructing container query",
                e, this.app, this.sess, this.env, true);
        } // catch

        // return nothing
        return null;
    } // createQueryRetrieveContentData


    /**************************************************************************
     * Create the query to get the container's content out of the database.
     * <BR/>
     * The query or view must at least have the attributes userId and rights.
     * Queries on these attributes have to be addable to this query. <BR/>
     * The query must have at least one constraint within WHERE to ensure that
     * other constraints can be added with AND. <BR/>
     * <B>Standard format:</B>. <BR/>
     *      "SELECT oid, &lt;other attributes&gt; " +
     *      " FROM " + this.viewContent +
     *      " WHERE containerId = " + oid;. <BR/>
     * This method can be overwritten in subclasses. <BR/>
     * As you can see the query should contain at least the oid.
     *
     * @return  The constructed query.
     */
    protected SelectQuery createSQLQueryRetrieveContentData ()
    {
        QueryCreator_01 queryCreator = null;

        // set the standard query
        SelectQuery query = DIHelpers.createCommonContentSQLQuery (
            this.childValues, this.viewContent, this.contentTypeTable,
            this.env);
        // add the container id
        query.extendWhere (new StringBuilder ()
            .append (" v.containerId = ").append (this.oid.toStringBuilderQu ()));

        // check if there is a specific query defined for the container's
        // content:
        if (this.p_extensionQueryName != null &&
            !this.p_extensionQueryName.isEmpty ())
        {
            // instanciate a queryfactory
            QueryFactory qf = new QueryFactory ();
            // initialize the QueryFactory
            qf.initObject (OID.getEmptyOid (), this.user, this.env, this.sess, this.app);
            try
            {
                // try to get queryCreator with specific name
                queryCreator = qf.get (this.p_extensionQueryName);

                // specific extension of query based on the query creator:
                query.add (queryCreator.getSelect (),
                    BOHelpers.replaceSysVar (this, queryCreator.getFrom ()),
                    BOHelpers.replaceSysVar (this, queryCreator.getWhere ()),
                    queryCreator.getGroupBy (),
                    null,
                    queryCreator.getOrderBy ());
            } // try
            catch (QueryNotFoundException e)
            {
                // show exception if query was not found
                IOHelpers.showMessage ( 
                    MultilingualTextProvider.getMessage (QueryExceptions.EXC_BUNDLE,
                        QueryExceptions.ML_EXC_QUERYDOESNOTEXIST_NAME,
                        new String[] {this.p_extensionQueryName}, this.env),
                        this.app, this.sess, this.env);
            } // catch (QueryNotFoundException e)
        } // if (this.p_extensionQueryName != null)
        return query;
    } // createSQLQueryRetrieveContentData


    /**************************************************************************
     * Create the query to get the container's content for multiple forward.
     * <BR/>
     * The query or view must at least have the attributes oid, name,
     * userId and rights.
     *
     * @return  The constructed query.
     */
    @Override
    protected String createQueryRetrieveForwardContentData ()
    {
        try
        {
            SelectQuery selectQuery = this.createSQLQueryRetrieveContentData ();
            selectQuery.extendWhere (new StringBuilder ()
                    .append (" v.containerId = ").append (this.oid.toStringBuilderQu ())
                    .append (" AND v.isLink = 0 ")
                    .append (" AND v.oid IN (SELECT objectId FROM ibs_Workflow_01 ")
                        .append (" WHERE workflowState IN ('")
                        .append (WorkflowConstants.STATE_OPEN_RUNNING)
                        .append ("', '")
                        .append (WorkflowConstants.STATE_OPEN_RUNNING_LASTSTATE)
                        .append ("')")
                        .append (" AND currentOwner = ").append (this.user.id)
                    .append (")"));

            return selectQuery.toValidStringBuilder ().toString ();
        } // try
        catch (DBQueryException e)
        {
            IOHelpers.showMessage ("Error when constructing container query",
                e, this.app, this.sess, this.env, true);
        } // catch

        // return nothing
        return null;
    } // createQueryRetrieveForwardContentData
    
    
    /**************************************************************************
     * Extend the query with constraints regarding the checkout flag. <BR/>
     * This method just adds some constraints to the already existing query.
     * It must be empty or start with "AND...".
     * This method can be overwritten in subclasses. <BR/>
     * 
     * Specific implementation
     * (see also IBS-279 Error when executing List Delete operation on container.)
     *
     * @param isPasteMode Defines if the query is executed within the past mode
     *
     * @return  The extension to the query.
     */
    protected StringBuffer extendQueryConstraintsForCheckOutFlag (boolean isPasteMode)
    {
        // if the query is not executed within past mode the table for the flags attribute
        // has to be set. this is necessary since the query returned by the call
        // DIHelpers.createCommonContentSQLQuery within createSQLQueryRetrieveContentData ()
        // joins two tables both containing the flags column.
        String flagTableName = isPasteMode ? "" : "v.";
        
        StringBuffer queryStr = new StringBuffer ()
            .append (SQLConstants.SQL_AND)
                .append (SQLHelpers.getBitAnd (flagTableName + "flags", "16"))
                .append ("<> 16 ");

        // check if the actual user is the Administrator:
        // (the administrator is allowed to delete objects which are marked
        // as undeletable)
        if (!this.getUser ().username.equalsIgnoreCase (IOConstants.USERNAME_ADMINISTRATOR))
                                    // not Administrator
        {
            // ensure that the object is deletable:
            queryStr
                .append (SQLConstants.SQL_AND)
                    .append (SQLHelpers.getBitAnd (
                            flagTableName + "flags", "" + BOConstants.FLG_NOTDELETABLE))
                    .append ("<> ").append (BOConstants.FLG_NOTDELETABLE)
                    .append (" ");
        } // if not Administrator
        
        return queryStr;
    } // extendQueryCheckCheckOutFlag


    /**************************************************************************
     * Get the element type specific data out of the actual tuple of the query
     * result out of DB. <BR/>
     *
     * @param   action      The action for the database connection.
     * @param   obj         Object representing the list element.
     *
     * @exception   DBError
     *              Error when executing database statement.
     */
    protected void getContainerElementDataDB (SQLAction action,
                                              XMLViewerContainerElement_01 obj)
        throws DBError
    {
        ValueDataElement childValue = null;
        DataElement childDataElem = new DataElement ();

        obj.setApplicationInfo (this.app);

        for (int i = 0; i < this.childValues.size (); i++)
        {
            childValue = new ValueDataElement (this.childValues.elementAt (i));

            // if there is a wrong columnname in container-typedefinition
            if (childValue == null)
            {
                continue;
            } // if

            boolean multiSelection = childValue.multiSelection != null &&
                childValue.multiSelection.equalsIgnoreCase (DIConstants.ATTRVAL_YES);

            // DB - STRINGS
            if (DIConstants.VTYPE_CHAR.equals (childValue.type) ||
                DIConstants.VTYPE_FILE.equals (childValue.type) ||
                DIConstants.VTYPE_URL.equals (childValue.type) ||
                DIConstants.VTYPE_EMAIL.equals (childValue.type) ||
                DIConstants.VTYPE_IMAGE.equals (childValue.type) ||
                DIConstants.VTYPE_OBJECTREF.equals (childValue.type) ||
                DIConstants.VTYPE_QUERYSELECTIONBOX.equals (childValue.type) ||
                DIConstants.VTYPE_SELECTIONBOX.equals (childValue.type) ||
// FOR EUI:
                DIConstants.VTYPE_LONGTEXT.equals (childValue.type) ||
// END
                DIConstants.VTYPE_TEXT.equals (childValue.type) ||
                (DIConstants.VTYPE_VALUEDOMAIN.startsWith (childValue.type) && multiSelection))
            {
                childValue.value = SQLHelpers.dbToAscii (
                    action.getString (childValue.mappingField));
            } // if
            // DB - BOOLEAN
            else if (DIConstants.VTYPE_BOOLEAN.equals (childValue.type))
            {
                childValue.value = (action.getBoolean (childValue.mappingField)) ?
                    MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                        AppMessages.ML_MSG_BOOLTRUE, env) :
                    MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,                 
                        AppMessages.ML_MSG_BOOLFALSE, env);
            } // else if
            // DATE
            else if (DIConstants.VTYPE_DATE.equals (childValue.type))
            {
                // get value for this column in current line of resultset
                Date d = action.getDate (childValue.mappingField);
                // check if there is a value set for this column
                if (action.wasNull () || d == null)
                {
                    childValue.value = "";
                } // if
                else
                {
                    // convert date to string
                    childValue.value = DateTimeHelpers.dateToString (d);
                } // else
            } // else if
            else if (DIConstants.VTYPE_DATETIME.equals (childValue.type))
            {
                // get value for this column in current line of resultset
                Date d = action.getDate (childValue.mappingField);
                // check if there is a value set for this column
                if (action.wasNull () || d == null)
                {
                    childValue.value = "";
                } // if
                else
                {
                    // convert date to string
                    childValue.value = DateTimeHelpers.dateTimeToString (d);
                } // else
            } // else if
            else if (DIConstants.VTYPE_TIME.equals (childValue.type))
            {
                // get value for this column in current line of resultset
                Date d = action.getDate (childValue.mappingField);
                // check if there is a value set for this column
                if (action.wasNull () || d == null)
                {
                    childValue.value = "";
                } // if
                else
                {
                    // convert date to string
                    childValue.value = DateTimeHelpers.timeToString (d);
                } // else
            } // else if
            // INTEGER
            else if (DIConstants.VTYPE_INT.equals (childValue.type) ||
                DIConstants.VTYPE_QUERYSELECTIONBOXINT.equals (childValue.type) ||
                DIConstants.VTYPE_SELECTIONBOXINT.equals (childValue.type))
            {
                childValue.value = "" + action.getInt (childValue.mappingField);
            } // else if
            // DB-NUMBER
            else if (DIConstants.VTYPE_FLOAT.equals (childValue.type) ||
                DIConstants.VTYPE_DOUBLE.equals (childValue.type) ||
                DIConstants.VTYPE_NUMBER.equals (childValue.type) ||
                DIConstants.VTYPE_QUERYSELECTIONBOXNUM.equals (childValue.type))

            {
                Float fValue = new Float (
                    action.getFloat (childValue.mappingField));

                childValue.value = fValue.toString ();
            } // DB-NUMBER
            // MONEY
            else if (DIConstants.VTYPE_MONEY.equals (childValue.type))
            {
                long money = action.getCurrency (childValue.mappingField);
                // check if there is a value set for this column
                if (action.wasNull ())
                {
                    childValue.value = "";
                } // if
                else
                {
                    // convert money to string
                    childValue.value = Helpers.moneyToString (money);
                } // else
            } // else if
            else if (DIConstants.VTYPE_FIELDREF.equals (childValue.type) ||
                    (DIConstants.VTYPE_VALUEDOMAIN.startsWith (childValue.type) && !multiSelection))
            {
                // get value for this column in current line of resultset
                OID localObjId =
                    SQLHelpers.getQuOidValue (action, childValue.mappingField);

                // check if there is a value set for this column
                if (action.wasNull () || localObjId == null ||
                    localObjId.isEmpty ())
                {
                    childValue.value = "";
                } // if
                else
                {
                    // convert oid and value of fieldref to string
                    childValue.value = "" + localObjId +
                        DIConstants.OPTION_DELIMITER +
                        action.getString (
                            childValue.mappingField + "_VALUE");
                } // else
            } // else if
            // REMINDER
            else if (DIConstants.VTYPE_REMINDER.equals (childValue.type))
            {
                // get value for this column in current line of resultset
                Date d = action.getDate (childValue.mappingField);
                // check if there is a value set for this column
                if (action.wasNull () || d == null)
                {
                    childValue.value = "";
                } // if
                else
                {
                    // convert date to string:
                    childValue.value = DateTimeHelpers.dateToString (d);
                } // else
            } // else if
            else
            {
                childValue.value = "type [" + childValue.type + "] not possible";
            } // else

            childDataElem.addValue (childValue);
        } // for

        obj.dataElement = childDataElem;
    } // getContainerElementDataDB


    /**************************************************************************
     * Get the element type specific data out of the actual tuple of the query
     * result for systemdata out of DB and specific data out of XML-Files. <BR/>
     *
     * @param   obj     Object representing the list element.
     *
     * @exception   DBError
     *              Error when executing database statement.
     */
    protected void getContainerElementDataXML (XMLViewerContainerElement_01 obj)
        throws DBError
    {
        // get dataelements of content data

/* KR 20090718 implementation derived from XMLViewerContainer_01:
        // check if we need to read a data file
        if (!this.useStandardHeader && this.headerFieldsArray != null &&
            this.headerFieldsArray.length > 0)
        {
*/
        // create an XMLViewer_01 that can read the viewer file:
        XMLViewer_01 viewer = this.getXMLViewer (obj.oid);
        // did we get any viewer object?
        if (viewer != null)
        {
            // store the dataElement in the ContainerElement
            obj.dataElement = viewer.getDataElement ();
        } // if (viewer != null)
/* KR 20090718 implementation derived from XMLViewerContainer_01:
        } // if
*/
    } // getContainerElementDataXML


    /**************************************************************************
     * Get the element type specific data out of the actual tuple of the query
     * result. <BR/>
     * This metod is used to get all attributes of one element out of the
     * resultset. The attribute names which can be used are the ones which
     * are defined within the resultset of
     * <A HREF="#createQueryRetrieveContentData">createQueryRetrieveContentData</A>.
     * <BR/>
     * <B>Format:</B>. <BR/>
     * for oid properties:
     *      obj.&lt;property&gt; = getQuOidValue (action, "&lt;attribute&gt;");. <BR/>
     * for other properties:
     *      obj.&lt;property&gt; = action.get&lt;type&gt; ("&lt;attribute&gt;");. <BR/>
     * The property <B>oid</B> is already gotten. This must not be done within this
     * method. <BR/>
     *
     * @param   action      The action for the database connection.
     * @param   commonObj   Object representing the list element.
     *
     * @exception   DBError
     *              Error when executing database statement.
     */
    @Override
    protected void getContainerElementData (SQLAction action,
                                            ContainerElement commonObj)
        throws DBError
    {
        super.getContainerElementData (action, commonObj);

        // make type cast for local usage:
        XMLViewerContainerElement_01 obj =
            (XMLViewerContainerElement_01) commonObj;

        // these fields are only read in the xmlviewer container
        // in order to display specific system fields in the result
        obj.creationDate = action.getDate ("creationDate");
        obj.validUntil = action.getDate ("validUntil");

        // set the specific fields:
        obj.useStandardHeader = this.useStandardHeader;
//        obj.useStandardHeader = false;
        obj.headerFieldsArray = this.headerFieldsArray;
        obj.headerIsSysFieldsArray = this.headerIsSysFieldsArray;
        obj.user = this.user;
        obj.setSessionInfo (this.sess);
        obj.env = this.env;

        // if the content was retrieved from db
        if (this.getContentFromDB)
        {
            this.getContainerElementDataDB (action, obj);
        } // if
        else
        // if content was not retrieved from db - get data from xmlfiles
        {
            this.getContainerElementDataXML (obj);
        } // else get data from xmlfiles
    } // getContainerElementData


    /**************************************************************************
     * Set the headings and the orderings for this container. <BR/>
     * Overload this function if you need other headings as the standard container. <BR/>
     */
    @Override
    protected void setHeadingsAndOrderings ()
    {
        if (this.useStandardHeader)
        {
            // set the standard header:
            super.setHeadingsAndOrderings ();
        } // if (useStandardHeader)
        else
        {
            // display individual header:
            this.headings = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE,
                BOListConstants.LST_HEADINGS_REDUCED, env);
            this.orderings = BOListConstants.LST_ORDERINGS_REDUCED;

            // ensure that the headerTokensArray is set:
            if (this.headerTokensArray == null)
            {
                // take the headerFieldsArray:
                this.headerTokensArray = this.headerFieldsArray;
            } // if

            if (this.headerTokensArray != null)
            {
                int headerLength = this.headings.length;
                int newHeaderLength = headerLength + this.headerTokensArray.length;
                String[] newHeadings = new String [newHeaderLength];
                String[] newOrderings = new String [newHeadings.length];

                // copy the standard header into the new header array
                for (int i = 0; i < headerLength; i++)
                {
                    newHeadings[i] = this.headings[i];
                    newOrderings[i] = this.orderings[i];
                } // for

                // now copy the customized headers into the headers array:
                for (int i = headerLength; i < newHeaderLength; i++)
                {
                    newHeadings[i] = this.headerTokensArray[i - headerLength];
                    newOrderings[i] = this.headerAttributesArray[i - headerLength];
                } // for

                // copy the new header array into the headings and orderings
                // properties:
                this.headings = newHeadings;
                this.orderings = newOrderings;
            } // if (this.headerFieldsArray != null)
            // ensure that there is an available ordering taken
            this.ensureAvailableOrdering ();
        } // else display individual name header token has been set

        if (this.p_nameToken != null)
        {
            // create a clone of the headings to ensure that no 
            // base headings are overwritten.
            // this can happen because BOListConstants.LST_HEADINGS_REDUCED
            // is assigned to this.headings and not cloned 
            this.headings = this.headings.clone ();            
            // the name column must always be at the first position
            // in the headings array
            this.headings [0] = this.p_nameToken;
        } // if (this.dataElement.p_nameToken != null)
        // check if an alternative
    } // setHeadingsAndOrderings


    /**************************************************************************
     * Creates a XMLViewer_01 object and sets the environment. <BR/>
     *
     * @param   objOid  Oid of the object.
     *
     * @return  an XMLViewer_01 object
     */
    protected XMLViewer_01 getXMLViewer (OID objOid)
    {
        // get the xml viewer object from the cache and return it:
        
        // Note when getting the object via BOHelpers, a full retrieve is done
        // on the object. This causes the read flag on the object to be set true 
        // for the actual object.
        // This means that when retrieving the content of the xmlcontainer
        // ALL read flags of ALL object are set true!!!
/* 20091122-BB: deactivated        
         return (XMLViewer_01) BOHelpers.getObject (objOid, this.env, false);
*/        
        // alternative approach:
        // we initialize the xmlviewer object and read the dateElement values 
        // via the dbmapper. this does not use the standard object retrieve
        // and therefore does not set the read flag        
        XMLViewer_01 xmlviewer = new XMLViewer_01 ();        
        Type type = ((ObjectPool) this.app.cache).getTypeContainer ().getType (objOid.tVersionId);               
        xmlviewer.typeObj = type;
        xmlviewer.typeName = type.getName ();        
        xmlviewer.initClassSpecifics ();
        xmlviewer.initObject (objOid, this.user, this.env, this.sess, this.app);
        // must be set because DBMapper.isValidDataElement checks the oid
        xmlviewer.dataElement.oid = objOid; 
                        
        // get the db mapper:
        DBMapper mapper = new DBMapper (this.user, this.env, this.sess, this.app);
        // retrieve the xmlviewer specific data element
        if (mapper.retrieveDBEntry (xmlviewer.dataElement))
        {
            return xmlviewer;  
        } // if (mapper.retrieveDBEntry (xmlviewer.dataElement))
        else    // could not retrieve data 
        {
            return null;
        } // else could not retrieve data
    } // getXMLViewer


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

        String xslFile;

        // check if there are any content objects:
        Type currentType = this.typeObj;
        DocumentTemplate_01 typeTemplate =
            (DocumentTemplate_01) currentType.getTemplate ();

        if (typeTemplate == null || this.elements.size () == 0)
        {
            return null;
        } // if (typeTemplate == null || this.elements.size () == 0)

        // create path to xslfile which is used for content view with
        // childobjecttypes
        xslFile = this.app.p_system.p_m2AbsBasePath +
            BOPathConstants.PATH_XSLT +
            typeTemplate.getTemplateDataElement ().p_typeCode +
            "_content.xsl";
        // check if the specific stylesheet file exists:
        if (!FileHelpers.exists (xslFile))
        {
            // if not try the genericcontent.xsl file
            xslFile = this.app.p_system.p_m2AbsBasePath +
                BOPathConstants.PATH_XSLT + "genericcontent.xsl";
            // and check if this file exists
            if (!FileHelpers.exists (xslFile))
            {
                return null;
            } // if
        } // if (!FileHelpers.exists (xslFile))

        this.trace ("XSLT File=" + xslFile);

        // create DomHandler to process xslt transformation:
        DOMHandler processor = new DOMHandler (this.env, this.sess, this.app);
        // get dom tree of current object
        Document doc = this.createDomTree (XMLContainer_01.VIEWMODE_CONTENT);
        processor.serializeDOM (doc);

        // show the domtree of the generated xml?
        if (typeTemplate.getDOMTree ())
        {
            String domString = processor.domToString (doc, "OBJECTS");
            // write the text non formatted
            this.env.write (IE302.TAG_PRE + domString + IE302.TAG_PREEND);
        } // if (typeTemplate.getDOMTree ())

        // generate the layout using the stylesheet file
        return processor.process (doc, xslFile);
    } // showContentXSL



    /**************************************************************************
     * create dom tree for content.
     *
     * the domtree looks like this:
     * <PRE>
     * &lt;OBJECTS ORDERHOW="ASC" ORDERBY="0"&gt;
     *   &lt;OBJECT TYPE="type" TYPECODE="typecode" ICON="icon.gif"&gt;
     *     &lt;SYSTEM&gt;
     *       &lt;OID&gt;0x0101023400000111&lt;/OID&gt;
     *       &lt;NAME&gt;XML-Objekt&lt;/NAME&gt;
     *       &lt;ISLINK&gt;&lt;/ISLINK&gt;
     *       &lt;ISNEW&gt;&lt;/ISNEW&gt;
     *     &lt;/SYSTEM&gt;
     *     &lt;VALUES&gt;
     *       &lt;VALUE FIELD="text" TYPE="TEXT"&gt;this is a text&lt;/VALUE&gt;
     *        ....
     *     &lt;VALUES&gt;
     *   &lt;/OBJECT&gt;
     *   &lt;OBJECT&gt;
     *     ....
     *   &lt;/OBJECT&gt;
     *   ...
     * &lt;/OBJECTS&gt;
     * </PRE>
     *
     * @param   viewMode    The view mode for the dom tree (view, edit).
     *
     * @return  The dom tree.
     *          <CODE>null</CODE> if there occurred an error.
     */
    protected Document createDomTree (int viewMode)
    {
        // instantiation of all possible node objects:
        Element objects = null;
        Document doc = null;

        try
        {
            doc = XMLWriter.createDocument ();
        } // try
        catch (XMLWriterException e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            return doc;
        } // catch

        objects = doc.createElement (DIConstants.ELEM_OBJECTS);
        objects.setAttribute ("ORDERHOW", this.orderHow);
        objects.setAttribute ("ORDERBY", Integer.toString (this.orderBy));
        // the layout name:
        objects.setAttribute ("LAYOUT", this.getUserInfo ().userProfile.layoutName);
        // set the user id and name:
        objects.setAttribute ("USERID",
                              Integer.toString (this.getUser ().id));
        objects.setAttribute (DIConstants.ATTR_USERNAME,
                              this.getUser ().username);
        doc.appendChild (objects);

        // <USER>
        try
        {
            objects.appendChild (doc.importNode (this.getUser ().getDomTree (),
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
        Element system = doc.createElement (DIConstants.ELEM_SYSTEM);
        objects.appendChild (system);

        //<OID>
        Element id = doc.createElement ("OID");
        id.appendChild (doc.createTextNode (this.oid.toString ()));
        system.appendChild (id);

        //<CONTAINER>
        Element container = doc.createElement ("CONTAINER");
        container.setAttribute ("ID", this.containerId.toString ());
        String objPath = "";
        String pathSep = "";
        // note that getObjectPath () buffers the result
        ObjectPathNode node = this.getObjectPath ();
        // construct the path using the / as path separator
        while (node != null && node.getNodeType () != ObjectPathNode.TYPE_ROOT)
        {
            objPath = node.getName () + pathSep + objPath;
            pathSep = "/";
            node = node.getParent ();
        } // while (node != null)

        container.appendChild (doc.createTextNode (objPath));
        system.appendChild (container);

        //<USERID>
        Element userId = doc.createElement ("USERID");
        userId.appendChild (doc.createTextNode (this.user.oid.toString ()));
        system.appendChild (userId);

        //<STATE>
        Element state = doc.createElement (DIConstants.ELEM_STATE);
        state.appendChild (doc.createTextNode ("" + this.state));
        system.appendChild (state);

        //<NAME>
        Element name = doc.createElement (DIConstants.ELEM_NAME);
        name.setAttribute ("INPUT", BOArguments.ARG_NAME);
        name.appendChild (doc.createTextNode (this.name));
        system.appendChild (name);

        //<DESCRIPTION>
        Element description = doc.createElement (DIConstants.ELEM_DESCRIPTION);
        description.setAttribute ("INPUT", BOArguments.ARG_DESCRIPTION);
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
            } // while (token.hasMoreElements ())
        } // if (viewMode == this.VIEWMODE_SHOW || viewMode == this.VIEWMODE_EDIT)

        //<VALIDUNTIL>
        Element validuntil = doc.createElement (DIConstants.ELEM_VALIDUNTIL);
        validuntil.setAttribute ("INPUT", BOArguments.ARG_VALIDUNTIL);
        String validDate = DateTimeHelpers.dateToString (this.validUntil);
        validuntil.appendChild (doc.createTextNode (validDate));
        system.appendChild (validuntil);

        // <SHOWINNEWS>
        Element showInNews = doc.createElement (DIConstants.ELEM_SHOWINNEWS);
        showInNews.setAttribute ("INPUT", BOArguments.ARG_INNEWS);
        String flagText = this.showInNews ?  
            MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                AppMessages.ML_MSG_BOOLTRUE, env) :
            MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,                 
                AppMessages.ML_MSG_BOOLFALSE, env);
        showInNews.appendChild (doc.createTextNode (flagText));
        system.appendChild (showInNews);
        // add objects:
        for (int i = 0;
             this.elements != null && i < this.elements.size ();
             i++)
        {
//System.out.println (this.elements.elementAt (i).getClass ().getName ());
            // get current element
            XMLViewerContainerElement_01 elem =
                (XMLViewerContainerElement_01) this.elements.elementAt (i);

            XMLViewer_01 xmlViewer = null;
            Vector<ValueDataElement> valueDataElements = null;
            
            // check if the view mode is content edit and retrieve the xml viewer object
            if (viewMode == VIEWMODE_CONTENTEDIT)
            {
                xmlViewer = (XMLViewer_01) BOHelpers.getObject (elem.oid, this.env, false, true);
                valueDataElements = xmlViewer.dataElement.values;
            } // if
            else
            {
                valueDataElements = elem.dataElement.values;
            } // else

            // <OBJECT>
            Element object = doc.createElement (DIConstants.ELEM_OBJECT);
            object.setAttribute (
                DIConstants.ATTR_TYPECODE, elem.dataElement.p_typeCode);
            object.setAttribute (DIConstants.ATTR_ICON, elem.dataElement.p_iconName);
            // set the user id and name:
            object.setAttribute ("USERID",
                                 Integer.toString (this.getUser ().id));
            object.setAttribute (DIConstants.ATTR_USERNAME,
                                 this.getUser ().username);
            objects.appendChild (object);

            // <SYSTEM>
            system = doc.createElement (DIConstants.ELEM_SYSTEM);
            object.appendChild (system);

            //<OID>
            String oidStr = (elem.isLink) ?
                            elem.linkedObjectId.toString () :
                            elem.oid.toString ();
            id = doc.createElement ("OID");
            id.appendChild (doc.createTextNode (oidStr));
            system.appendChild (id);

            //<NAME>
            name = doc.createElement (DIConstants.ELEM_NAME);
            if (viewMode == VIEWMODE_CONTENTEDIT)
            {
                // add the input attribute for the the NAME sytem tag
                name.setAttribute ("INPUT", xmlViewer.adoptArgName (BOArguments.ARG_NAME));
            } // if
            name.appendChild (doc.createTextNode (elem.name));
            system.appendChild (name);

            //<DESCRIPTION>
            description = doc.createElement (DIConstants.ELEM_DESCRIPTION);
            if (viewMode == VIEWMODE_CONTENTEDIT)
            {
                // add the input attribute for the the DESCRIPTION sytem tag
                description.setAttribute ("INPUT", xmlViewer.adoptArgName (BOArguments.ARG_DESCRIPTION));
            } // if
            description.appendChild (doc.createTextNode (elem.description));
            system.appendChild (description);

            //<ISNEW>
            Element isNew = doc.createElement ("ISNEW");
            isNew.appendChild (doc.createTextNode (
                (elem.isNew) ? DIConstants.ATTRVAL_YES : DIConstants.ATTRVAL_NO));
            system.appendChild (isNew);

            // <ISLINK>
            Element isLink = doc.createElement ("ISLINK");
            isLink.appendChild (doc.createTextNode (
                (elem.isLink) ? DIConstants.ATTRVAL_YES : DIConstants.ATTRVAL_NO));
            system.appendChild (isLink);

            // <VALUES>
            Element values = doc.createElement (DIConstants.ELEM_VALUES);
            object.appendChild (values);

            // add value tags:
            for (int j = 0; valueDataElements != null &&
                j < valueDataElements.size (); j++)
            {
                ValueDataElement valElem = valueDataElements.elementAt (j);

                // check if the view mode is content edit
                if (viewMode == VIEWMODE_CONTENTEDIT)
                {
                    // fill options data for query selection boxes and value domain fields
                    ValueDataElementTS.fillOptionsData (xmlViewer, valElem);
                    xmlViewer.createDomTreeValueNode (doc, values, valElem, viewMode);
                } // if
                else
                {            
                    // <VALUE>
                    Element value =
                        doc.createElement (DIConstants.ELEM_VALUE);
    
                    // special handling for multiple VALUEDOMAIN fields:
                    boolean multiSelection = valElem.multiSelection != null &&
                        valElem.multiSelection.equalsIgnoreCase (DIConstants.ATTRVAL_YES);
    
                    if (valElem.type.equals (DIConstants.VTYPE_VALUEDOMAIN) && multiSelection)
                    {
                        this.replaceMultipleOidsByValues (valElem);
                    } // if
    
                    value.setAttribute (DIConstants.ATTR_FIELD, valElem.field);
                                   
                    value.setAttribute (DIConstants.ATTR_TYPE, valElem.type);
                    value.setAttribute (DIConstants.ATTR_MULTIPLE,
                        multiSelection ? DIConstants.ATTRVAL_YES : DIConstants.ATTRVAL_NO);
                    value.appendChild (doc.createTextNode (valElem.value));
                    values.appendChild (value);
                } // else
            } // for
        } // for all elements

        // serialize domtree:
        this.serializeDOM (doc);

        // debug domtree:
//        this.env.write ("<P>" + IE302.HCH_NBSP + "</P>" + IE302.TAG_PRE + serializer.domToString (doc, "OBJECTS") + IE302.TAG_PREEND);

        // return the result:
        return doc;
    } // createDomTree


    /**************************************************************************
     * Replaces the OIDs within the value field of the given
     * <code>ValueDataElement</code> with the defined field value.
     *
     * @param valElem <code>ValueDataElement</code> with the flat OIDs value field.
     */
    private void replaceMultipleOidsByValues (ValueDataElement valElem)
    {
        String flatOids = valElem.value;

        StringTokenizer tok = new StringTokenizer (flatOids,
            BOConstants.MULTISELECTION_VALUE_SAPERATOR);

        StringBuffer valueBuffer = new StringBuffer ();

        while (tok.hasMoreTokens ())
        {
            try
            {
                OID refOid = new OID (tok.nextToken ());

                // check if the oid is valid:
                if (!refOid.isEmptyInDomain ())     // oid is valid?
                {
                    // try to get object via oid:
                    BusinessObject refObj = BOHelpers.getObject (
                        refOid, this.env, false, false, false);

                    // get first FIELDS element in referenced object field VALUE
                    ReferencedObjectInfo fri =
                        (valElem.p_subTags != null) ?
                            (ReferencedObjectInfo) valElem.p_subTags.elementAt (0) :
                            null;

                    // get the defined value from the found object
                    String value =
                        DIHelpers.getSysFieldValue (fri.getName (), refObj);

                    valueBuffer.append (value);

                    if (tok.hasMoreTokens ())
                    {
                        valueBuffer
                            .append (BOConstants.MULTISELECTION_VALUE_SAPERATOR);
                    } // if tok.hasMoreTokens()
                } // if oid is valid
            } // try
            catch (IncorrectOidException ex)
            {
                //Ignore this oid
            } // catch

        } // while has more tokens

        valElem.value = valueBuffer.toString ();
    } // replaceMultipleOidsByValues


    /**************************************************************************
     * Serialize a DOM tree. <BR/>
     *
     * @param   doc     the DOM  
     */
    protected void serializeDOM (Document doc)
    {
        DOMHandler serializer = new DOMHandler (this.env, this.sess, this.app);
        serializer.serializeDOM (doc);
        
/* Leads to double output in XMLViewerContainer_01        
        if (this.user.username.equalsIgnoreCase (IOConstants.USERNAME_ADMINISTRATOR))
        {
            String domString = serializer.domToString (doc, "OBJECTS");
            // write the text non formatted
            this.createHTMLHeader (this.app, this.sess, this.env);
            DIHelpers.showDOMInfo (this.env, domString, null);
            this.createHTMLFooter (this.env);
        } // if (mayContainTemplate.getDOMTree () || ...
*/        
        
    } // serializeDOM
    
    
    /**************************************************************************
     * Represent list content with its properties to the user within a change
     * form. <BR/>
     *
     * @param   representationForm  Kind of representation.
     */
    protected void performShowListChangeForm (int representationForm)
    {
        Page page = new Page ("List", false); // the output page

        // Stylesheetfile wird geladen
        StyleSheetElement style = new StyleSheetElement ();
        style.importSS = this.sess.activeLayout.path + this.env.getBrowser () + "/" +
            this.sess.activeLayout.elems[LayoutConstants.SHEETLIST].styleSheet;
        page.head.addElement (style);

        // start with the container representation: show header

        // check if columns shall be reduced
        // according to userprofile settings
//        this.setHeadingsAndOrderings ();

        // set the icon of this object:
        this.setIcon ();

        FormElement form = null;
        
        // Container is no Selectioncontainer
        if (this.isTab ())
        {
//            body = this.createHeader (page,
//                StringHelpers.replace (this.headingName,
//                    UtilConstants.TAG_NAME, this.name),
//                    this.getNavItems (), this.containerName, this.icon, size);
            
            form = this.createFormHeader (page, StringHelpers.replace (this.headingName,
                    UtilConstants.TAG_NAME, this.name), this.getNavItems (), this.containerName,
                    MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_BUTTONLISTCHANGE, env), null,
                    this.icon, this.containerName);
        } // if
        else
        {
//            body = this.createHeader (page,
//                StringHelpers.replace (this.headingName,
//                    UtilConstants.TAG_NAME, this.name),
//                    this.getNavItems (), null, this.icon, size);
            
            form = this.createFormHeader (page, StringHelpers.replace (this.headingName,
                    UtilConstants.TAG_NAME, this.name), this.getNavItems (), null,
                    MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_BUTTONLISTCHANGE, env), null,
                    this.icon, this.containerName);
        } // else
        
        // add hidden fields
        form.addElement (new InputElement (BOArguments.ARG_OID,
                                           InputElement.INP_HIDDEN,
                                           "" + this.oid));

        form.addElement (new InputElement (BOArguments.ARG_FUNCTION,
                                           InputElement.INP_HIDDEN,
                                           "" + AppFunctions.FCT_LISTCHANGE));
        
        form.addElement (new InputElement (BOArguments.ARG_CHANGELIST,
                                            InputElement.INP_HIDDEN,
                                            this.getElementsList ()));

        // if the container supports layout generation  with
        // XSL style sheed use this content-output otherwise
        // get the default layout from the TableElement.
        boolean isXslOutputValid = false;
        String xslOutput = this.showChangeContentXSL ();
        if (xslOutput != null)
        {
            form.addElement (new TextElement (xslOutput));
            isXslOutputValid = true;
        } // if
        else
        {
            // show according message to the user
            IOHelpers.showMessage ("No XSL output found. May occur due to missing stylesheet.", this.env);
            return;
        } // else
        
        this.createFormFooter (form, null, null, null, null, false, false, true, null);
        
        // if the form is generated by a stylesheet
        // set a stylesheet specific onSubmit argument.
        if (isXslOutputValid)
        {
            form.onSubmit = "return xslSubmitAllowed ();";
        } // if (isXslOutputValid)
        
        if (!this.isSingleSelectionContainer)
        {
            // ensure that the content tab is active:
            if (this.p_tabs != null)        // tabs exist?
            {
                this.p_tabs.setActiveTab (0, TabConstants.TC_CONTENT);
            } // if tabs exist

            // create the script to be executed on client:
            if (this.p_isShowCommonScript)
            {
                page.body.addElement (this.getCommonScript (true));
            } // if

            // show message if there are too many entries in the list:
            if (this.areMaxElementsExceeded)
            {
                ScriptElement script = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);

                // add alert message to script:
                script.addScript ("alert (\"" +
                    StringHelpers.replace (this.msgTooMuchElements,
                        UtilConstants.TAG_NUMBER, "" + this.maxElements) +
                    "\\n\\n" +
                    StringHelpers.replace (this.msgDisplayableElements,
                        UtilConstants.TAG_NUMBER, "" + this.maxElements) +
                    "\");\n");

                page.body.addElement (script);
            } // if
        } // if !this.isSingleSelectionContainer

        // build the page and show it to the user:
        try
        {
            // try to bulid the page
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            // show according message to the user
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
    } // performShowListChangeForm
    
    
    /**************************************************************************
     * Generates the HTML code for the list change content view by using a
     * stylesheet file. Which should be named
     * &lt;typecode&gt;_contentEdit.xsl. <BR/>
     *
     * @return      the HTML code or <CODE>null</CODE>
     *              if no stylesheet is defined.
     */
    protected String showChangeContentXSL ()
    {
        String xslFile;

        // check if there are any content objects:
        Type currentType = this.typeObj;
        DocumentTemplate_01 typeTemplate =
            (DocumentTemplate_01) currentType.getTemplate ();

        if (typeTemplate == null || this.elements.size () == 0)
        {
            return null;
        } // if (typeTemplate == null || this.elements.size () == 0)

        // create path to xslfile which is used for content view with
        // childobjecttypes
        xslFile = this.app.p_system.p_m2AbsBasePath +
            BOPathConstants.PATH_XSLT +
            typeTemplate.getTemplateDataElement ().p_typeCode +
            "_contentEdit.xsl";
        
        // check if the specific stylesheet file exists:
        if (!FileHelpers.exists (xslFile))
        {
            // BT 20100726: No generic contentEdit stylesheet
            // available at the moment. Uncomment the following
            // code if one will be added in future.
            
//            // if not try the genericcontent.xsl file
//            xslFile = this.app.p_system.p_m2AbsBasePath +
//                BOPathConstants.PATH_XSLT + "_contentEdit.xsl";
//            // and check if this file exists
//            if (!FileHelpers.exists (xslFile))
//            {
//                return null;
//            } // if
            
            IOHelpers.showMessage ("No contentEdit XSL stylesheet found: " +
                    typeTemplate.getTemplateDataElement ().p_typeCode + "_contentEdit.xsl", this.env);
            
            return null;
        } // if (!FileHelpers.exists (xslFile))

        this.trace ("XSLT File=" + xslFile);

        // create DomHandler to process xslt transformation:
        DOMHandler processor = new DOMHandler (this.env, this.sess, this.app);
        // get dom tree of current object
        Document doc = this.createDomTree (XMLContainer_01.VIEWMODE_CONTENTEDIT);
        processor.serializeDOM (doc);

        // show the domtree of the generated xml?
        if (typeTemplate.getDOMTree ())
        {
            String domString = processor.domToString (doc, "OBJECTS");
            // write the text non formatted
            this.env.write (IE302.TAG_PRE + domString + IE302.TAG_PREEND);
        } // if (typeTemplate.getDOMTree ())

        // generate the layout using the stylesheet file
        return processor.process (doc, xslFile);
    } // showChangeContentXSL
    
    
    /**
     * Generates a string containing all container element oids.
     *
     * @return
     */
    protected String getElementsList ()
    {
        StringBuilder retList = new StringBuilder ();

        // add objects:
        for (int i = 0;
             this.elements != null && i < this.elements.size ();
             i++)
        {
            // get current element
            XMLViewerContainerElement_01 elem =
                (XMLViewerContainerElement_01) this.elements.elementAt (i);
            
            retList.append (elem.oid);
            
            if (i + 1 < this.elements.size ())
            {
                retList.append (OID_LIST_SEPARATOR);
            } // if
        } // for
        
        return retList.toString ();
    } // getElementsList
    
    
    /**************************************************************************
     * change attributes of objects in the containers content
     *
     * @param   operation   The operation code.
     *
     * @throws  NoAccessException
     *          The user does not have the required permissions.
     */
    public void performListChange (int operation)
        throws NoAccessException
    {
        String oids = this.env.getParam (BOArguments.ARG_CHANGELIST);
        
        StringTokenizer tok = new StringTokenizer (oids, OID_LIST_SEPARATOR);
        while (tok.hasMoreTokens ())
        {
            try
            {
                String oid = tok.nextToken ();
                XMLViewer_01 xmlViewer = (XMLViewer_01) BOHelpers.getObject (new OID (oid), env, false, true);
                
                xmlViewer.performChange (Operations.OP_NONE);
            } // try
            catch (IncorrectOidException e)
            {
                IOHelpers.showMessage ("IncorrectOidException during execution of XMLContainer_01.performListChange: ",
                        e, this.env, true);
            } // catch
            catch (NameAlreadyGivenException e)
            {
                IOHelpers.showMessage ("NameAlreadyGivenException during execution of XMLContainer_01.performListChange: ",
                        e, this.env, true);
            } // catch
        } // while
    } // performListChange

} // class XMLContainer_01
