/*
 * Class: QueryCreator_01.java
 */

// package:
package ibs.obj.query;

// imports:
import ibs.bo.BOArguments;
import ibs.bo.BusinessObject;
import ibs.bo.Datatypes;
import ibs.bo.IncorrectOidException;
import ibs.bo.OID;
import ibs.bo.States;
import ibs.di.DataElement;
import ibs.io.Environment;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.ml.MultilangConstants;
import ibs.ml.MultilingualTextInfo;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.ml.Locale_01;
import ibs.service.action.ActionConstants;
import ibs.service.action.ActionException;
import ibs.service.action.Variable;
import ibs.service.action.Variables;
import ibs.service.user.User;
import ibs.tech.html.BlankElement;
import ibs.tech.html.GroupElement;
import ibs.tech.html.HTMLButtonElement;
import ibs.tech.html.IE302;
import ibs.tech.html.InputElement;
import ibs.tech.html.ScriptElement;
import ibs.tech.html.SelectElement;
import ibs.tech.html.TableElement;
import ibs.tech.html.TextElement;
import ibs.tech.sql.DBConnector;
import ibs.tech.sql.DBError;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLConstants;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.StoredProcedure;
import ibs.util.FormFieldRestriction;
import ibs.util.NameAlreadyGivenException;
import ibs.util.NoAccessException;
import ibs.util.StringHelpers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

/******************************************************************************
 * QueryCreator_01 for dynamic Search. <BR/>
 * 
 * @version $Id: QueryCreator_01.java,v 1.70 2010/04/15 16:02:25 rburgermann Exp
 *          $
 * @author Andreas Jansa (AJ), 000918
 */
public class QueryCreator_01 extends BusinessObject implements Cloneable
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag to
     * ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO = "$Id: QueryCreator_01.java,v 1.74 2013/01/16 16:14:13 btatzmann Exp $";

    // constants:

    /**
     * constant for SELECT token in query
     */
    private static final String CONST_SELECT = " SELECT ";

    /**
     * constant for FROM token in query
     */
    private static final String CONST_FROM = " FROM ";

    /**
     * constant for WHERE token in query
     */
    private static final String CONST_WHERE = " WHERE ";

    /**
     * constant for GROUP BY token in query
     */
    private static final String CONST_GROUPBY = " GROUP BY ";

    /**
     * constant for ORDER BY token in query
     */
    private static final String CONST_ORDERBY = " ORDER BY ";

    /**
     * bit pattern for query type
     */
    protected int queryType = 0;

    /**
     * complete string for SELECT - clause in query
     */
    protected String selectString = null;

    /**
     * string for FROM - clause which was fixed for this query
     */
    protected String fromString = null;

    /**
     * string for WHERE - clause which was fixed for this query
     */
    protected String whereString = null;

    /**
     * string for GROUP BY - clause which was fixed for this query
     */
    protected String groupByString = null;

    /**
     * string for ORDER BY - clause which was fixed for this query
     */
    protected String orderByString = null;

    /**
     * column headers of result to be shown in GUI separated by ';'
     */
    protected String columnHeaders = null;

    /**
     * attributes in query due to column headers separated by ';' (in the right
     * sequence relative to columnHeaders)
     */
    protected String queryColumnAttributes = null;

    /**
     * types of attributes of querycolumns separated by ',' (in the right
     * sequence relative to queryColumnAttributes)
     */
    protected String columnTypes = null;

    /**
     * tokens to be shown for search fields, sperated by ','
     */
    protected String searchFields = null;

    /**
     * attributes in query due to search fields, separated by ',' (in the right
     * sequence relative to search fields)
     */
    protected String queryFieldAttributes = null;

    /**
     * types of attributes for search fields, separated by ',' (in the right
     * sequence relative to queryfieldAttributes)
     */
    protected String searchFieldTypes = null;

    /**
     * flag if debugging should be enabled or not. <BR/>
     */
    protected boolean isEnableDebugging = false;

    /**
     * category of the query. used for grouping. <BR/>
     */
    protected String category = null;

    /**
     * vector contents the class QueryAttributeMapper for mapping between column
     * headers, column attributes and column attribute types
     */
    public Vector<QueryAttributeMapper> colAttrMapping = null;

    /**
     * vector contents the class QuerySearchValueMapper for mapping between
     * field names, field attributes, field types and search values for fields
     */
    public Vector<QuerySearchValueMapper> fieldAttrMapping = null;

    /**
     * oid of root container where query should be executed
     */
    public OID rootObjectOid = null;

    /**
     * oid of current object. If query is used for query selection box in
     * xmlform. This represents the oid of this object and is replaced in query
     * with SYSVAR_OID
     */
    protected OID currentObjectOid = null;

    /**
     * container oid of current object. <BR/>
     */
    protected OID currentContainerId = null;

    /**
     * number of results to return if the querycreator is executed
     */
    protected int resultCounter = -1;

    /**
     * Holds the preloaded multilang names for all locales for all column
     * headers.
     */
    private Collection<Map<String, String>> mlColumnHeaderNames = null;

    /**
     * TODO: Descriptions not saved within the QuerySearchValueMapper yet Holds
     * the preloaded multilang descriptions for all locales for all column
     * headers.
     */
    private Collection<Map<String, String>> mlColumnHeaderDescriptions = null;

    /**
     * Holds the preloaded multilang names for all locales for all search fields
     * .
     */
    private Collection<Map<String, String>> mlSearchFieldNames = null;

    /**
     * TODO: Descriptions not saved within the QuerySearchValueMapper yet Holds
     * the preloaded multilang descriptions for all locales for all search
     * fields.
     */
    private Collection<Map<String, String>> mlSearchFieldDescriptions = null;


    // /////////////////////////////////////////////////////////////////////////
    // class methods
    // /////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Produces HTML output to show a query with buttons to hide and show it.
     * Displays a link to a query creator object. <BR>
     * 
     * @param env The current environment.
     * @param queryString The query string
     * @param queryCreator The query creator for which to show the query info.
     */
    public static void showQueryInfo (Environment env, String queryString,
        QueryCreator_01 queryCreator)
    {
        // any template set?
        if (queryCreator != null)
        {
            IOHelpers.showStructure (env, queryString, "hide query",
                "show query", "open " + queryCreator.name, queryCreator.oid);
        } // if (queryCreator != null)
        else
        // no template set
        {
            IOHelpers.showStructure (env, queryString, "hide query",
                "show query", null, null);
        } // else no template set
    } // showQueryInfo


    // /////////////////////////////////////////////////////////////////////////
    // constructors
    // /////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * This constructor creates a new instance of the class QueryCreator_01. <BR/>
     */
    public QueryCreator_01 ()
    {
        // call constructor of super class:
        super ();

        // initialize properties common to all subclasses:
    } // QueryCreator_01


    /**************************************************************************
     * Creates a QueryCreator_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class. <BR/>
     * 
     * @param oid Value for the compound object id.
     * @param user Object representing the user.
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public QueryCreator_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
    } // QueryCreator_01


    // /////////////////////////////////////////////////////////////////////////
    // object data methods - interface to other objects
    // /////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set names of procedures:
        this.procCreate = "p_QueryCreator_01$create";
        this.procRetrieve = "p_QueryCreator_01$retrieve";
        this.procChange = "p_QueryCreator_01$change";

        // set elementClassName:
        // this.elementClassName = "ibs.obj.query.ReportObjectElement";

        // set number of parameters for procedure calls:
        this.specificRetrieveParameters = 6;
        this.specificChangeParameters = 6;

        // set tablename (for CLOB and TEXT - editing:
        this.tableName = "ibs_QueryCreator_01";
    } // initClassSpecifics


    /**************************************************************************
     * Initializes a BusinessObject object. <BR/>
     * The compound object id is stored in the <A HREF="#oid">oid</A> property
     * of this object. <BR/>
     * The <A HREF="#user">user object</A> is also stored in a specific property
     * of this object to make sure that the user's context can be used for
     * getting his/her rights. <BR/>
     * <A HREF="#env">env</A> is initialized to the provided object. <BR/>
     * <A HREF="#sess">sess</A> is initialized to the provided object. <BR/>
     * <A HREF="#app">app</A> is initialized to the provided object. <BR/>
     * 
     * @param oid Value for the compound object id.
     * @param user Object representing the user.
     * @param env The actual environment object.
     * @param sess The actual session info object.
     * @param app The global application info object.
     */
    public void initObject (OID oid, User user, Environment env,
        SessionInfo sess, ApplicationInfo app)
    {
        super.initObject (oid, user, env, sess, app);
        // set the instance's public/protected properties:
        this.fieldAttrMapping = new Vector<QuerySearchValueMapper> ();
        this.colAttrMapping = new Vector<QueryAttributeMapper> ();
    } // initObject


    /**************************************************************************
     * Set the oid of the object that calls the query. <BR/>
     * 
     * @param aOid the oid of the object
     */
    public void setCurrentObjectOid (OID aOid)
    {
        this.currentObjectOid = aOid;
    } // setCurrentObjectOid


    /**************************************************************************
     * Set the oid of the object that calls the query. <BR/>
     * 
     * @param aOid the containerId of the object
     */
    public void setCurrentContainerId (OID aOid)
    {
        this.currentContainerId = aOid;
    } // setCurrentContainerId


    /**************************************************************************
     * get content of selectString. <BR/>
     * 
     * @return string of select clause with String 'SELECT' as beginning.
     */
    public String getSelectString ()
    {
        if (this.selectString != null && !this.selectString.equals (" "))
        {
            return QueryCreator_01.CONST_SELECT + this.selectString;
        } // if

        return "";
    } // getSelectString


    /**************************************************************************
     * Get select clause. <BR/>
     * 
     * @return string of select clause without String 'SELECT' as beginning.
     */
    public String getSelect ()
    {
        return this.selectString;
    } // getSelect


    /**************************************************************************
     * write any content to selectString. <BR/>
     * 
     * @param value string for select clause without 'SELECT' as beginning.
     */
    public void setSelectString (String value)
    {
        this.selectString = value;
    } // setSelectString


    /**************************************************************************
     * get the set resultCounter. <BR/>
     * 
     * @return value of the chosen resultCounter.
     */
    public int getResultCounter ()
    {
        // get the value
        return this.resultCounter;
    } // getResultCounter


    /**************************************************************************
     * set the int value of the resultCounter. <BR/>
     * 
     * @param value chosen resultCounter.
     */
    public void setResultCounter (int value)
    {
        // set the value
        this.resultCounter = value;
    } // setResultCounter


    /**************************************************************************
     * Get the boolean value for enable debugging or not. <BR/>
     * 
     * @param value <CODE>true</CODE> or <CODE>false</CODE> to show debugging.
     */
    public void setEnableDebugging (boolean value)
    {
        this.isEnableDebugging = value;
    } // setEnableDebugging


    /**************************************************************************
     * Get the boolean value for enable debugging. <BR/>
     * 
     * @return <CODE>true</CODE> or <CODE>false</CODE> to show debugging.
     */
    public boolean getEnableDebugging ()
    {
        return this.isEnableDebugging;
    } // getEnableDebugging


    /**************************************************************************
     * get content of fromString. <BR/>
     * 
     * @return string of select clause with String 'FROM' as beginning.
     */
    public String getFromString ()
    {
        if (this.fromString != null && !this.fromString.equals (" "))
        {
            return QueryCreator_01.CONST_FROM + this.fromString;
        } // if

        return "";
    } // getFromString


    /**************************************************************************
     * Get from clause. <BR/>
     * 
     * @return string of select clause without String 'FROM' as beginning.
     */
    public String getFrom ()
    {
        return this.fromString;
    } // getFrom


    /**************************************************************************
     * write any content to fromString. <BR/>
     * 
     * @param value String for from clause without 'FROM' as beginning.
     */
    public void setFromString (String value)
    {
        this.fromString = value;
    } // setFromString


    /**************************************************************************
     * get content of whereString. <BR/>
     * 
     * @return string of where clause with String 'WHERE' as beginning.
     */
    public String getWhereString ()
    {
        if (this.whereString != null && !this.whereString.equals (" "))
        {
            return QueryCreator_01.CONST_WHERE + this.whereString;
        } // if

        return "";
    } // getWhereString


    /**************************************************************************
     * Get where. <BR/>
     * 
     * @return string of where clause without String 'WHERE' as beginning.
     */
    public String getWhere ()
    {
        return this.whereString;
    } // getWhere


    /**************************************************************************
     * write any content to whereString. <BR/>
     * 
     * @param value String for where clause without 'WHERE' as beginning.
     */
    public void setWhereString (String value)
    {
        this.whereString = value;
    } // setWhereString


    /**************************************************************************
     * get value of query type. <BR/>
     * 
     * @return integer for query type
     */
    public int getQueryType ()
    {
        return this.queryType;
    } // getQueryType


    /**************************************************************************
     * set query type. <BR/>
     * 
     * @param value ???
     */
    public void setQueryType (int value)
    {
        this.queryType = value;
    } // setqueryType


    /**************************************************************************
     * Returns true when query is a search query. <BR/>
     * 
     * @return <CODE>true</CODE> if the query is a search query,
     *         <CODE>false</CODE> otherwise.
     */
    public boolean isSearchQuery ()
    {
        return (this.queryType & QueryConstants.QT_SEARCH) == QueryConstants.QT_SEARCH;
    } // isSearchQuery


    /**************************************************************************
     * Returns true when query is a report query. <BR/>
     * 
     * @return <CODE>true</CODE> if the query is a report query,
     *         <CODE>false</CODE> otherwise.
     */
    public boolean isReportQuery ()
    {
        return (this.queryType & QueryConstants.QT_REPORT) == QueryConstants.QT_REPORT;
    } // isReportQuery


    /**************************************************************************
     * Returns true when query is a system query. <BR/>
     * 
     * @return <CODE>true</CODE> if the query is a system query,
     *         <CODE>false</CODE> otherwise.
     */
    public boolean isSystemQuery ()
    {
        return (this.queryType & QueryConstants.QT_SYSTEM) == QueryConstants.QT_SYSTEM;
    } // isSystemQuery


    /**************************************************************************
     * get content of groupByString. <BR/>
     * 
     * @return string of group by clause with String 'GROUP BY' as beginning.
     */
    public String getGroupByString ()
    {
        if (this.groupByString != null && !this.groupByString.equals (" "))
        {
            return QueryCreator_01.CONST_GROUPBY + this.groupByString;
        } // if

        return "";
    } // getGroupByString


    /**************************************************************************
     * Get groupBy clause. <BR/>
     * 
     * @return string of group by clause without String 'GROUP BY' as beginning.
     */
    public String getGroupBy ()
    {
        return this.groupByString;
    } // getGroupBy


    /**************************************************************************
     * write any content to groupByString. <BR/>
     * 
     * @param value string for groupbyclause without 'GROUP BY' as beginning.
     */
    public void setGroupByString (String value)
    {
        this.groupByString = value;
    } // setGroupByString


    /**************************************************************************
     * get content of orderByString. <BR/>
     * 
     * @return string of order by clause with String 'ORDER BY' as beginning.
     */
    public String getOrderByString ()
    {
        if (this.orderByString != null && !this.orderByString.equals (" "))
        {
            return QueryCreator_01.CONST_ORDERBY + this.orderByString;
        } // if

        return "";
    } // getOrderByString


    /**************************************************************************
     * Get orderBy clause. <BR/>
     * 
     * @return string of order by clause without String 'ORDER BY' as beginning.
     */
    public String getOrderBy ()
    {
        return this.orderByString;
    } // getOrderBy


    /**************************************************************************
     * write any content to orderByString. <BR/>
     * 
     * @param value String for orderbyclause without 'ORDER BY' as beginning.
     */
    public void setOrderByString (String value)
    {
        this.orderByString = value;
    } // setOrderByString


    /**************************************************************************
     * get number of columnheaders. <BR/>
     * 
     * @return Number of columnheaders which are set.
     */
    public int getColumnHeaderCount ()
    {
        return this.colAttrMapping.size ();
    } // getColumnHeaderCount


    /**************************************************************************
     * get number of searchfields. <BR/>
     * 
     * @return Number of searchfields which are set.
     */
    public int getSearchFieldCount ()
    {
        return this.fieldAttrMapping.size ();
    } // getSearchFieldCount


    /**************************************************************************
     * Get the definition name of specific search field <BR/>
     * 
     * @param position Position of searchfield to get the name of.
     * @return definition name of the search field on the position 'position'.
     */
    public String getFieldName (int position)
    {
        // Get the correct multilang name for the current user
        Object valueMapper = this.fieldAttrMapping.elementAt (position);
        String value = ((QuerySearchValueMapper) valueMapper).getDefName ();

        return value;
    } // getFieldName


    /**************************************************************************
     * Gets the correct multilang name of specific search field for the locale
     * of the current user. <BR/>
     * 
     * @param position Position of searchfield to get the name of.
     * @return Multilang name of the search field on the position 'position'.
     */
    public String getMlFieldName (int position)
    {
        // Get the correct multilang name for the current user
        Object valueMapper = this.fieldAttrMapping.elementAt (position);
        String value = ((QuerySearchValueMapper) valueMapper)
            .getMlGuiName (MultilingualTextProvider.getUserLocale (env)
                .getLocaleKey ());

        return value;
    } // getMlFieldName


    /**************************************************************************
     * Gets the correct multilang description of specific search field for the
     * locale of the current user. <BR/>
     * 
     * @param position Position of searchfield to get the name of.
     * @return Multilang description of the search field on the position
     *         'position'.
     */
    public String getMlFieldDescription (int position)
    {
        // Get the correct multilang name for the current user
        Object valueMapper = this.fieldAttrMapping.elementAt (position);
        String value = ((QuerySearchValueMapper) valueMapper)
            .getMlGuiDescription (MultilingualTextProvider.getUserLocale (env)
                .getLocaleKey ());

        return value;
    } // getMlFieldDescription


    /**************************************************************************
     * Get index of specific search field. <BR/>
     * 
     * @param fieldName Name of search field to be searched for.
     * @return Position of search field (if there are duplicates - the method
     *         returns the index of the first occurrence)
     */
    public int getFieldNameIndex (String fieldName)
    {
        QuerySearchValueMapper attributeMapper; // the actual mapper

        // search for field name:
        // loop through all elements of the list:
        for (int i = 0; i < this.fieldAttrMapping.size (); i++)
        {
            // get current ColumnMapper
            attributeMapper = this.fieldAttrMapping.elementAt (i);

            // if column name is found return index of column name
            if (attributeMapper.getDefName () != null
                && attributeMapper.getDefName ().equals (fieldName))
            {
                return i;
            } // if
        } // for loop through all field names

        // return -1 if columnName was not found
        return -1;
    } // getFieldNameIndex


    /**************************************************************************
     * get type of specific search field. <BR/>
     * 
     * @param position Position of searchfield to get the type of.
     * @return ???
     */
    public String getFieldType (int position)
    {
        // System.out.println ("getFieldType: " + position + " (" +
        // this.fieldAttrMapping.size () + ")");
        return this.fieldAttrMapping.elementAt (position).queryAttributeType;
    } // getFieldType


    /**************************************************************************
     * Set type for specific searchfield. <BR/>
     * 
     * @param position Position of searchfieldtype to be set.
     * @param value Value for type of searchfield to be set.
     */
    public void setFieldType (int position, String value)
    {
        (this.fieldAttrMapping.elementAt (position)).queryAttributeType = value;
    } // setFieldType


    /**************************************************************************
     * Get query - attribute of specific search field. <BR/>
     * 
     * @param position Position of field to be set.
     * @return ???
     */
    public String getFieldQueryAttribute (int position)
    {
        return this.fieldAttrMapping.elementAt (position).queryAttribute;
    } // getFieldQueryAttribute


    /**************************************************************************
     * Set query - attribute for specific search field. <BR/>
     * 
     * @param position Position of field to be set.
     * @param value The value to be set.
     */
    public void setFieldQueryAttribute (int position, String value)
    {
        (this.fieldAttrMapping.elementAt (position)).queryAttribute = value;
    } // setFieldName


    /**************************************************************************
     * Set type of specific query attribute for search field. <BR/>
     * 
     * @param position Position of field to be set.
     * @param value The value to be set.
     */
    public void setQueryAttrType (int position, String value)
    {
        (this.fieldAttrMapping.elementAt (position)).queryAttributeType = value;
    } // setQueryAttrType


    /**************************************************************************
     * Get search value for specific search field. <BR/>
     * 
     * @param position Position of field to be set.
     * @return ???
     */
    public String getSearchValueForQueryAttr (int position)
    {
        return this.fieldAttrMapping.elementAt (position).searchValue;
    } // getSearchValueForQueryAttr


    /**************************************************************************
     * Set search value for specific search field. <BR/>
     * 
     * @param position Position of field to be set.
     * @param value The value to set.
     */
    public void setSearchValueForQueryAttr (int position, String value)
    {
        QuerySearchValueMapper valueMapper;

        // set search range values in search valuemapping-Object for specific
        // position
        if (position < this.fieldAttrMapping.size ())
        {
            valueMapper = this.fieldAttrMapping.elementAt (position);
            valueMapper.searchValue = value;
        } // if

        // //////////////////////////////////////////////////////////////7
        // AJ TODO - need this for later use !!!! (Import and Export of
        // QueryExecutive !!!
        //
        /*
         * // if field is objectpath, set rootObjectOid if
         * (QueryConstants.FIELDTYPE_OBJECTPATH.equals
         * (valueMapper.queryAttributeType)) { // try to create oid for
         * rootObject try { // create oid and set rootObjectOid OID lOid = new
         * OID (value); this.rootObjectOid = lOid; } // try catch
         * (IncorrectOidException e) { // if oid is wrong do not set
         * rootObjectOid showMessage
         * ("QueryCreator_01.setSearchValueForQueryAttr: " +
         * "IncorrectOidException when creating oid for rootObject, " +
         * " oid = " + value); } // catch catch (NullPointerException e) { // if
         * oid is wrong do not set rootObjectOid showMessage
         * ("QueryCreator_01.setSearchValueForQueryAttr: " +
         * "IncorrectOidException when creating oid for rootObject, " +
         * " oid = " + value); } // catch } // if
         */
    } // setSearchValueForQueryAttr


    /**************************************************************************
     * Set search range values for specific search field. <BR/>
     * 
     * @param position the position in the search field vector
     * @param value the value to set
     * @param range the range value to set
     */
    public void setSearchRangeForQueryAttr (int position, String value,
        String range)
    {
        QuerySearchValueMapper valueMapper;

        // set search range values in search value mapping-Object for specific
        // position
        valueMapper = this.fieldAttrMapping.elementAt (position);
        valueMapper.searchValue = value;
        valueMapper.searchRangeValue = range;
    } // setSearchRangeForQueryAttr


    /**************************************************************************
     * Get match type for specific search field. <BR/>
     * 
     * @param position Position of field to be set.
     * @return ???
     */
    public String getMatchTypeForQueryAttr (int position)
    {
        Object valueMapper = this.fieldAttrMapping.elementAt (position);
        return ((QuerySearchValueMapper) valueMapper).matchType;
    } // getMatchTypeForQueryAttr


    /**************************************************************************
     * Set match type for specific search field. <BR/>
     * 
     * @param position Position of field to be set.
     * @param value The value to be set.
     */
    public void setMatchTypeForQueryAttr (int position, String value)
    {
        if (position < this.fieldAttrMapping.size ())
        {
            Object valueMapper = this.fieldAttrMapping.elementAt (position);
            ((QuerySearchValueMapper) valueMapper).matchType = value;
        } // if
    } // setMatchTypeForQueryAttr


    /**************************************************************************
     * Gets the correct multilang name of specific search field for the locale
     * of the current user. <BR/>
     * 
     * @param position Position of searchfield to get the name of.
     * @return Multilang name of the search field on the position 'position'.
     */
    public String getColumnName (int position)
    {
        // Get the correct multilang name for the current user
        Object attributeMapper = this.colAttrMapping.elementAt (position);
        String value = ((QueryAttributeMapper) attributeMapper).getDefName ();

        return value;
    } // getMlColumnName


    /**************************************************************************
     * Gets the correct multilang name of specific search field for the locale
     * of the current user. <BR/>
     * 
     * @param position Position of searchfield to get the name of.
     * @return Multilang name of the search field on the position 'position'.
     */
    public String getMlColumnName (int position)
    {
        // Get the correct multilang name for the current user
        Object attributeMapper = this.colAttrMapping.elementAt (position);
        String value = ((QueryAttributeMapper) attributeMapper)
            .getMlGuiName (MultilingualTextProvider.getUserLocale (env)
                .getLocaleKey ());

        return value;
    } // getMlColumnName


    /**************************************************************************
     * Gets the correct multilang description of specific search field for the
     * locale of the current user. <BR/>
     * 
     * @param position Position of searchfield to get the name of.
     * @return Multilang description of the search field on the position
     *         'position'.
     */
    public String getMlColumnDescription (int position)
    {
        // Get the correct multilang description for the current user
        Object attributeMapper = this.colAttrMapping.elementAt (position);
        String value = ((QueryAttributeMapper) attributeMapper)
            .getMlGuiDescription (MultilingualTextProvider.getUserLocale (env)
                .getLocaleKey ());

        return value;
    } // getMlColumnDescription


    /**************************************************************************
     * Get query - attribute for specific column. <BR/>
     * 
     * @param position Position of field to be set.
     * @return ???
     */
    public String getColumnQueryAttribute (int position)
    {
        Object attributeMapper = this.colAttrMapping.elementAt (position);
        return ((QueryAttributeMapper) attributeMapper).queryAttribute;
    } // getColumnQueryAttribute


    /**************************************************************************
     * Get index of specific column. <BR/>
     * 
     * @param columnName Name of column for which to get the index.
     * @return position of column (if there are duplicates - the method returns
     *         the index of first occurrence)
     */
    public int getColumNameIndex (String columnName)
    {
        QueryAttributeMapper attributeMapper; // the actual mapper

        // search for column name
        for (int i = 0; i < this.colAttrMapping.size (); i++)
        {
            // get current ColumnMapper
            attributeMapper = this.colAttrMapping.elementAt (i);

            // if column name is found return index of column name
            if (attributeMapper.getDefName () != null
                && attributeMapper.getDefName ().equals (columnName))
            {
                return i;
            } // if
        } // for loop through all column names

        // return -1 if columnName was not found
        return -1;
    } // getColumNameIndex


    /**************************************************************************
     * Get type of specific column. <BR/>
     * 
     * @param position Position of field to be set.
     * @return ???
     */
    public String getColumnType (int position)
    {
        Object tmp = this.colAttrMapping.elementAt (position);
        return ((QueryAttributeMapper) tmp).queryAttributeType;
    } // getColumnName


    /**************************************************************************
     * Convert the search field definitions to input parameter definitions. <BR/>
     * 
     * @return The vector containing the input parameters (class QueryParameter)
     *         or <CODE>null</CODE> if there occurred an error.
     * @see QueryParameter
     */
    public Vector<QueryParameter> getInputParameters ()
    {
        Vector<QueryParameter> inParams = new Vector<QueryParameter> (); // the
                                                                         // result
        String fieldType = null; // type of actual field
        // set data regarding to queryObject
        int searchFieldCount = 0;
        QueryParameter param = null;

        searchFieldCount = this.getSearchFieldCount ();

        // get the parameters from the query creator:
        // walk through all search value fields and get the parameter
        // information
        for (int i = 0; i < searchFieldCount; i++)
        // i is the counter for the searchfieldarry in querycreator (no
        // SYSTEMFIELDS like #OBJECTPATH)
        {
            // get the field type of the actual search field:
            fieldType = this.getFieldType (i);

            // HACK - OBJECTPATH should be normal searchfield (regard in
            // getParameters).
            // - objectpath exist in searchFieldattributeMapper in
            // querycreator but is not handled as searchfield in queryexecutive
            /*
             * if (!fieldType.equals (QueryConstants.FIELDTYPE_OBJECTPATH)) {
             */
            param = new QueryParameter ();

            // initialize parameter for query:
            param.setPos (i);
            param.setName (this.getFieldName (i));
            // set the multilang information
            param.setMlName (this.getMlFieldName (i));
            param.setMlDescription (this.getMlFieldDescription (i));
            param.setType (fieldType);
            param.setValue (null);
            param.setRangeValue (null);
            param.setMatchType (this.getMatchTypeForQueryAttr (i));

            // add the new parameter to the vector:
            inParams.add (param);
            /*
             * } // if
             */
        } // for

        // return the result:
        return inParams;
    } // getInputParameters


    // /////////////////////////////////////////////////////////////////////////
    // object data methods - internal
    // /////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Tokenize a configuration string. <BR/>
     * 
     * @param configString The configuration string to be tokenized.
     * @return The tokenizer which can be used to loop through the several parts
     *         of the string.
     */
    private StringTokenizer tokenizeConfigString (String configString)
    {
        StringTokenizer tokenizer = null;

        // tokenize the string - delimiter = ;
        if (configString != null && configString.trim ().length () > 0)
        {
            // delete all carriage return - line feed from configuration strings
            configString.replace ('\015', ' ');
            configString.replace ('\012', ' ');

            // tokenize
            tokenizer = new StringTokenizer (configString,
                QueryConstants.CONST_DELIMITER);
        } // if

        return tokenizer;
    } // tokenizeConfigString


    /**************************************************************************
     * <BR/>
     */
    protected void performAttributeMapping ()
    {
        // trace ("QueryCreator_01.performAttributeMapping ");
        StringTokenizer headerTokens = null;
        StringTokenizer attributeHeaderTokens = null;
        StringTokenizer columnTypeTokens = null;
        StringTokenizer fieldTokens = null;
        StringTokenizer attributeFieldTokens = null;
        StringTokenizer fieldTypeTokens = null;

        // COLUMNS

        // create vector for mapping
        this.colAttrMapping = new Vector<QueryAttributeMapper> ();

        // iterators to go over all header columns

        Iterator<Map<String, String>> columnHeaderNames = mlColumnHeaderNames != null ? mlColumnHeaderNames
            .iterator ()
            : null;
        Iterator<Map<String, String>> columnHeaderDescriptions = mlColumnHeaderDescriptions != null ? mlColumnHeaderDescriptions
            .iterator ()
            : null;
        // tokenize all configuration strings of query to StringTokenizer due to
        // result list headers
        headerTokens = this.tokenizeConfigString (this.columnHeaders);
        attributeHeaderTokens = this
            .tokenizeConfigString (this.queryColumnAttributes);
        columnTypeTokens = this.tokenizeConfigString (this.columnTypes);

        // fill vector for header/query column mapping
        QueryAttributeMapper attributeMapper = null;
        for (; attributeHeaderTokens != null
            && attributeHeaderTokens.hasMoreElements () && headerTokens != null
            && headerTokens.hasMoreElements () && columnHeaderNames != null
            && columnHeaderNames.hasNext ();)
        {
            // create single mapping line for one header to one query column
            attributeMapper = new QueryAttributeMapper ();
            // set the map with all texts for all locales to the guiName
            attributeMapper.guiName = columnHeaderNames.next ();
            // set the map with all descriptions for all locales to the guiDesc
            attributeMapper.guiDescription = columnHeaderDescriptions.next ();
            // set the original definition name for column header
            attributeMapper.defName = headerTokens.nextToken ().trim ();
            attributeMapper.queryAttribute = attributeHeaderTokens.nextToken ()
                .trim ();

            // get the modifier string
            String type = columnTypeTokens.nextToken ();
            // did we get any data?
            if (type != null)
            {
                type = type.trim ();
                int openingBracket = type
                    .indexOf (QueryConstants.COLUMNTYPE_MODIFIER_PARAM_OPENING_BRACKET);
                int closingBracket = type
                    .indexOf (QueryConstants.COLUMNTYPE_MODIFIER_PARAM_CLOSING_BRACKET);

                // any modifier
                if (openingBracket != -1 && closingBracket != -1)
                {
                    attributeMapper.queryAttributeType = type.substring (0,
                        openingBracket);
                    attributeMapper.queryAttributeTypeModifier = type
                        .substring (openingBracket + 1, closingBracket);
                } // if any modifier
                else
                // no modifier
                {
                    attributeMapper.queryAttributeType = type;
                    attributeMapper.queryAttributeTypeModifier = null;
                } // else no modifier
            } // if did we get any data?

            // add singleline mapper to mapping-vector
            this.colAttrMapping.addElement (attributeMapper);
        } // for

        // FIELDS

        // create vector for mapping
        this.fieldAttrMapping = new Vector<QuerySearchValueMapper> ();

        // iterators to go over all search Fields
        Iterator<Map<String, String>> searchFieldNames = mlSearchFieldNames != null ? mlSearchFieldNames
            .iterator ()
            : null;
        Iterator<Map<String, String>> searchFieldDescriptions = mlSearchFieldDescriptions != null ? mlSearchFieldDescriptions
            .iterator ()
            : null;
        // tokenize all configurationstrings of query to StringTokenizer due to
        // searchfields
        fieldTokens = this.tokenizeConfigString (this.searchFields);
        attributeFieldTokens = this
            .tokenizeConfigString (this.queryFieldAttributes);
        fieldTypeTokens = this.tokenizeConfigString (this.searchFieldTypes);

        // fill vector for searchfield/querycolumn/search value/match type
        // mapping
        QuerySearchValueMapper searchValueMapper = null;

        for (; attributeFieldTokens != null
            && attributeFieldTokens.hasMoreElements () && fieldTokens != null
            && fieldTokens.hasMoreElements () && searchFieldNames != null
            && searchFieldNames.hasNext ();)
        {
            // create single mapping line for one searchfield to one querycolumn
            searchValueMapper = new QuerySearchValueMapper ();
            // set the map with all texts for all locales to the guiName
            searchValueMapper.guiName = searchFieldNames.next ();
            // set the map with all descriptions for all locales to the guiDesc
            searchValueMapper.guiDescription = searchFieldDescriptions.next ();
            // set the original definition name for searchfield
            searchValueMapper.defName = fieldTokens.nextToken ().trim ();
            // set attribute in query for searchfield
            searchValueMapper.queryAttribute = attributeFieldTokens
                .nextToken ().trim ();

            // get type of field out of stringtokenizer
            if (fieldTypeTokens != null) // if there are any fieldtypetokens
            {
                searchValueMapper.queryAttributeType = fieldTypeTokens
                    .nextToken ().trim ();
            } // if

            // add singleline mapper to mapping-vector
            this.fieldAttrMapping.addElement (searchValueMapper);
        } // for
    } // performAttributeMapping


    /**************************************************************************
     * Get multiple attribute of specific column. <BR/>
     * 
     * @param position Position of field to be set.
     * @return ???
     */
    public boolean getMultipleAttribute (int position)
    {
        Object tmp = this.colAttrMapping.elementAt (position);
        String typeModifier = ((QueryAttributeMapper) tmp).queryAttributeTypeModifier;

        return typeModifier != null
            && typeModifier
                .indexOf (QueryConstants.COLUMNTYPE_MODIFIER_MULTIPLE) != -1;
    } // getColumnName


    /**************************************************************************
     * Change the data of a business object in the database using a given
     * operation. <BR/>
     * 
     * @param operation Operation to be performed with the object.
     * @exception NoAccessException The user does not have access to this object
     *                to perform the required operation.
     * @exception NameAlreadyGivenException An object with this name already
     *                exists. This exception is only raised by some specific
     *                object types which don't allow more than one object with
     *                the same name.
     * @see #performChange ()
     * @see #performChangeData (int)
     */
    public void performChange (int operation) throws NoAccessException,
        NameAlreadyGivenException
    {
        // change the data of the object:
        super.performChangeData (operation);

        // if a querycreator is changed, the queryPool has to be updated:
        ((QueryPool) this.app.queryPool).updateQuery (this);

    } // performChange


    /**************************************************************************
     * Delete the object, i.e. delete its properties from the database. <BR/>
     * 
     * @param representationForm Kind of representation.
     * @return boolean which is false if something went wrong
     */
    public boolean delete (int representationForm)
    {
        boolean retValue = true; // return value of this method

        // delete the object and get the return value:
        retValue = super.delete (representationForm);

        // if a querycreator is changed, the queryPool has to be updated:
        ((QueryPool) this.app.queryPool).deleteQuery (this.oid);

        return retValue; // return the return value
    } // delete


    /**************************************************************************
     * Returns a sql action object associated with a connection. <BR/>
     * 
     * @return The action object associated with the required connection.
     * @throws DBError An exception occurred within database statement.
     */
    public SQLAction getQueryDBConnection () throws DBError
    {
        // use the standard database connection:
        return this.getDBConnection ();
    } // getQueryDBConnection


    /**************************************************************************
     * Releases a sql action object associated with a database connection. <BR/>
     * 
     * @param action The action object associated with the connection.
     * @throws DBError An exception occurred within database statement.
     */
    public void releaseQueryDBConnection (SQLAction action) throws DBError
    {
        // use the standard release method:
        this.releaseDBConnection (action);
    } // releaseQueryDBConnection


    /**************************************************************************
     * Create the query which is defined in this querycontainer. <BR/>
     * orderby - clause is added if order by clause is set already. if orderby -
     * clause is not set already this.getOrderByString returns null. <BR/>
     * 
     * @return The constructed query.
     */
    protected StringBuffer getQuery ()
    {
        // trace ("AJ QueryCreator_01.getQuery");
        StringBuffer queryStr = new StringBuffer ();
        User user = this.user; // the current user

        // check if the user is set:
        if (user == null)
        {
            // get the user from the session info:
            user = this.sess.userInfo.getUser ();
        } // if

        // AJ every part of query is checked if it is empty
        // for some database reasons the empty strings contents one blank
        // everytime
        // therefore i had to check if the strings equals to " " not to ""

        // ====== SELECT clause ======
        if (this.selectString != null && this.selectString.length () > 0
            && !this.selectString.equals (" "))
        {
            queryStr.append (QueryCreator_01.CONST_SELECT).append (
                this.selectString);
        } // if
        /*
         * KR 20050421 This does not make sense and also disables replacing of
         * SYSVARs which is done later in this method else { return
         * (queryStr.length () > 0) ? queryStr : null; } // else selectclause
         * not set
         */

        // ====== FROM clause ======
        if (this.fromString != null && this.fromString.length () > 0
            && !this.fromString.equals (" "))
        {
            // for downcompatibility (before R2.2AddOn1
            this.fromString = StringHelpers.replace (this.fromString,
                QueryConstants.SYSVAR_USERID, "" + user.id);

            // add FROM Token and fromString
            queryStr.append (QueryCreator_01.CONST_FROM).append (
                this.fromString);

            // trace ("QueryCreator.getQuery rootObjectOid = " +
            // this.rootObjectOid);
            // if oid for rootobject is set
            if (this.rootObjectOid != null && !this.rootObjectOid.isEmpty ())
            {
                // add table for rootObject condition
                queryStr.append (", (SELECT posnopath ").append (
                    "   FROM ibs_Object ").append ("   WHERE oid = ").append (
                    this.rootObjectOid.toStringQu ()).append (") root ");
            } // if
        } // if
        /*
         * KR 20050421 This does not make sense and also disables replacing of
         * SYSVARs which is done later in this method else { return
         * (queryStr.length () > 0) ? queryStr : null; } // else from clause not
         * set
         */

        // ====== WHERE clause ======

        // operation to concatenate different conditions together:
        StringBuffer sqlOperation = new StringBuffer ();
        // if is set to AND when necessary

        // if there is anything to add to where clause
        if (this.whereString != null && this.whereString.length () > 0
            && !this.whereString.equals (" ") || this.fieldAttrMapping != null
            && this.fieldAttrMapping.size () > 0)
        {
            // flag which marks if CONST_WHERE was added already to queryStr;
            boolean addedWhere = false;

            // extend queryString with userdefined - fix - whereString
            if (this.whereString != null && this.whereString.length () > 0
                && !this.whereString.equals (" "))
            {
                // for downcompatibility (before R2.2AddOn1
                this.whereString = StringHelpers.replace (this.whereString,
                    QueryConstants.SYSVAR_USERID, "" + user.id);

                // add WHERE - Token to queryString
                queryStr.append (QueryCreator_01.CONST_WHERE).append (
                    this.whereString);
                addedWhere = true; // CONST_WHERE was added to queryString
            } // if there are some fix where conditions

            // add conditions for searchfields and search values with match
            // types
            String searchFieldConditions = this
                .getQuerySearchFieldConditions ();

            // if there are some additional conditions
            if (searchFieldConditions != null)
            {
                // if CONST_WHERE was not added already (no conditions set
                // already)
                if (!addedWhere)
                {
                    // add CONST_WHERE and condition string
                    queryStr.append (QueryCreator_01.CONST_WHERE);
                    addedWhere = true; // CONST_WHERE was added to queryString
                } // if !addedWhere
                else
                // else - there are some conditions already
                {
                    // set sql operation to AND to concatenate conditions
                    // together
                    sqlOperation = new StringBuffer (" AND ");
                } // else

                // add condition string to query
                queryStr.append (sqlOperation).append (searchFieldConditions);
            } // if
        } // if there is anything to add to where clause
        /*
         * KR 20050421 This does not make sense and also disables replacing of
         * SYSVARs which is done later in this method else // else - no where
         * clause { return (queryStr.length () > 0) ? queryStr : null; } // else
         */

        // GROUP BY clause
        if (this.groupByString != null && this.groupByString.length () > 0
            && !this.groupByString.equals (" "))
        {
            queryStr.append (QueryCreator_01.CONST_GROUPBY).append (
                this.groupByString);
        } // if

        // ORDER BY clause
        if (this.orderByString != null && this.orderByString.length () > 0
            && !this.orderByString.equals (" "))
        {
            queryStr.append (QueryCreator_01.CONST_ORDERBY).append (
                this.orderByString);
        } // if

        // replace systemvariables in queryStr with values
        Variables vars = new Variables ();

        try
        {
            vars.addSysVars (this);

            // make sure that sysvar #SYSVAR.ELEMOID# is left in query
            // for later use in queryexecutive
            Variable var = new Variable (QueryConstants.SYSVAR_ELEMOID,
                ActionConstants.VARIABLETYPE_TEXT, "18", "",
                QueryConstants.SYSVAR_ELEMOID);
            vars.addEntry (var, this.env);

            // #SYSVAR.CURRENTOBJECTOID should be replaced with the
            // oid of the current object when executing this query
            var = new Variable (QueryConstants.SYSVAR_CURRENTOBJECTOID,
                ActionConstants.VARIABLETYPE_TEXT, "18", "",
                this.currentObjectOid != null ? this.currentObjectOid
                    .toString () : OID.EMPTYOID);
            vars.addEntry (var, this.env);

            // #SYSVAR.CURRENTCONTAINERID should be replaced with the
            // containerId of the current object when executing this query
            var = new Variable (QueryConstants.SYSVAR_CURRENTCONTAINERID,
                ActionConstants.VARIABLETYPE_TEXT, "18", "",
                this.currentContainerId != null ? this.currentContainerId
                    .toString () : OID.EMPTYOID);
            vars.addEntry (var, this.env);

            // TODO BB050829: what is this field used for?
            var = new Variable (QueryConstants.SYSVAR_FIELDVALIDATION,
                ActionConstants.VARIABLETYPE_TEXT, "255", "",
                QueryConstants.SYSVAR_FIELDVALIDATION);
            vars.addEntry (var, this.env);

            // enable the use of query parameters as sysvars:
            // check if there are any query conditions having input parameters:
            if (this.fieldAttrMapping != null) // there are some fields
            {
                String searchValue;

                // loop through all query input parameters and add each of them
                // to the variables:
                for (Iterator<QuerySearchValueMapper> iter = this.fieldAttrMapping
                    .iterator (); iter.hasNext ();)
                {
                    QuerySearchValueMapper elem = iter.next ();

                    searchValue = elem.searchValue;

                    // BB20080721: this is a hack because a QUERYSELECTIONBOX
                    // returns " " when nothing was selected.
                    // Now there are several queries comparing ' ' = ' '
                    // In case the query has not been called via a searchform
                    // this can become 'null' = ' '
                    // Maybe this has more sideeffects?
                    if (searchValue == null
                        && (elem.queryAttributeType
                            .startsWith (QueryConstants.FIELDTYPE_QUERYSELECTION)))
                    {
                        searchValue = " ";
                    } // searchValue

                    vars.addEntry (new Variable (
                        ActionConstants.SYSVAR_PREFIX
                            + ActionConstants.SYSVAR_QUERYPROPERTY
                            + elem.getDefName ()
                            + ActionConstants.VARIABLE_POSTFIX,
                        ActionConstants.VARIABLETYPE_TEXT, "255", "",
                        searchValue), this.env);

                    // check for range query fields:
                    if (elem.queryAttributeType
                        .equals (QueryConstants.FIELDTYPE_INTEGERRANGE)
                        || elem.queryAttributeType
                            .equals (QueryConstants.FIELDTYPE_NUMBERRANGE)
                        || elem.queryAttributeType
                            .equals (QueryConstants.FIELDTYPE_MONEYRANGE)
                        || elem.queryAttributeType
                            .equals (QueryConstants.FIELDTYPE_DATERANGE)
                        || elem.queryAttributeType
                            .equals (QueryConstants.FIELDTYPE_TIMERANGE)
                        || elem.queryAttributeType
                            .equals (QueryConstants.FIELDTYPE_DATETIMERANGE))
                    {
                        // add range field value to system variables:
                        vars.addEntry (new Variable (
                            ActionConstants.SYSVAR_PREFIX
                                + ActionConstants.SYSVAR_QUERYPROPERTY
                                + elem.getDefName ()
                                + BOArguments.ARG_RANGE_EXTENSION
                                + ActionConstants.VARIABLE_POSTFIX,
                            ActionConstants.VARIABLETYPE_TEXT, "255", "",
                            elem.searchRangeValue), this.env);
                    } // if
                } // for iter
            } // if there are some fields

            // now replace system variables within the query:
            queryStr = vars.replaceWithValue (queryStr, this.env);
        } // try
        catch (ActionException e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, false);
        } // catch

        return (queryStr.length () > 0) ? queryStr : null;
    } // getQuery


    /**************************************************************************
     * Create a formatted version of the query for output. <BR/>
     * 
     * @return The formatted query for HTML output.
     */
    protected StringBuffer getFormattedQuery ()
    {
        StringBuffer queryStr = new StringBuffer ();

        queryStr.append (IE302.TAG_PRE);
        if (this.selectString != null && !this.selectString.trim ().isEmpty ())
        {
            queryStr.append (IE302.TAG_BOLDBEGIN).append (
                QueryCreator_01.CONST_SELECT.trim ())
                .append (IE302.TAG_BOLDEND).append ("\n").append (
                    this.selectString);
        } // if
        if (this.fromString != null && !this.fromString.trim ().isEmpty ())
        {
            queryStr.append ("\n").append (IE302.TAG_BOLDBEGIN).append (
                QueryCreator_01.CONST_FROM.trim ()).append (IE302.TAG_BOLDEND)
                .append ("\n").append (this.fromString);
        } // if
        if (this.whereString != null && !this.whereString.trim ().isEmpty ())
        {
            queryStr.append ("\n").append (IE302.TAG_BOLDBEGIN).append (
                QueryCreator_01.CONST_WHERE.trim ()).append (IE302.TAG_BOLDEND)
                .append ("\n").append (this.whereString);
        } // if
        if (this.groupByString != null
            && !this.groupByString.trim ().isEmpty ())
        {
            queryStr.append ("\n").append (IE302.TAG_BOLDBEGIN).append (
                QueryCreator_01.CONST_GROUPBY.trim ()).append (
                IE302.TAG_BOLDEND).append ("\n").append (this.groupByString);
        } // if
        if (this.orderByString != null
            && !this.orderByString.trim ().isEmpty ())
        {
            queryStr.append ("\n").append (IE302.TAG_BOLDBEGIN).append (
                QueryCreator_01.CONST_ORDERBY.trim ()).append (
                IE302.TAG_BOLDEND).append ("\n").append (this.orderByString);
        } // if

        queryStr.append (IE302.TAG_PREEND);

        return queryStr;
    } // getFormattedQuery


    /**************************************************************************
     * Get conditions for where clause in query, which results of set
     * searchFields and searchValues for this fields. The searchValues are set
     * from external object - for example QueryExecutive. <BR/>
     * 
     * @return The constructed condition string - NOT starting with any operator
     *         (AND...).
     */
    public String getQuerySearchFieldConditions ()
    {
        StringBuffer completeConditionString = new StringBuffer ();

        // operation to concatenate different conditions together:
        StringBuffer sqlOperation = new StringBuffer ("");
        // if is set to AND when necessary
        StringBuffer andOperation = new StringBuffer (" AND ");

        // if there are any fields
        if (this.fieldAttrMapping != null)
        {
            // === ADD SEARCHFIELDS WITH SEARCHVALUES AND MATCHTYPES AS
            // CONDITONS TO WHERE CLAUSE ===
            String fieldName = null;
            String fieldType = null; // Type of field [DATE|NUMBER|STRING]
            String quAttribute = null; // attribute in where clause
            String match = null; // matchType
            String searchVal = null; // search value for where - condition
            String rangeVal = null; // value for range types.
            StringBuffer sqlConditionString = null; // SQL - condition with
                                                    // match type and value

            // add searchcols from dynamic search conditions:
            for (Iterator<QuerySearchValueMapper> iter = this.fieldAttrMapping
                .iterator (); iter.hasNext ();)
            {
                QuerySearchValueMapper valueMapper = iter.next ();

                // init sql condition string
                sqlConditionString = null;
                // get current fieldName
                fieldName = valueMapper.getMlGuiName (MultilingualTextProvider
                    .getUserLocale (env).getLocaleKey ());
                // get current fieldType
                fieldType = valueMapper.queryAttributeType;
                // get current queryAttribute
                quAttribute = valueMapper.queryAttribute;
                // get current match type
                match = valueMapper.matchType;
                // get current searchValue
                searchVal = valueMapper.searchValue;
                // get current range value if applicable:
                rangeVal = valueMapper.searchRangeValue;

                // CONSTRAINTS:
                // the searchVal and rangeVal must not be null (indicates no
                // value set)
                // the quAttribute must not be #IGNORE# (indicates attribute
                // disabled in query)
                if (!(quAttribute
                    .equalsIgnoreCase (QueryConstants.CONST_IGNORE) || ((searchVal == null || searchVal
                    .length () == 0) && (rangeVal == null || rangeVal.length () == 0))))
                {
                    // check field type, then match type -> then create
                    // condition string for current field

                    // FIELDTYPE_STRING
                    // if search value is empty do not add condition string
                    // except if match type is 'exact' (search for empty string)
                    if (fieldType.equals (QueryConstants.FIELDTYPE_STRING)
                        && (match != null
                            && !match.equals (SQLConstants.MATCH_EXACT)
                            && searchVal != null && searchVal.length () > 0 || match != null
                            && match.equals (SQLConstants.MATCH_EXACT)))

                    {
                        sqlConditionString = SQLHelpers
                            .getQueryConditionString (quAttribute, match,
                                searchVal, false);
                    } // if string
                    // FIELDTYPE_QUERYSELECTIONNUM
                    if (fieldType
                        .startsWith (QueryConstants.FIELDTYPE_QUERYSELECTIONNUM))
                    {
                        // check if searchVal is set
                        if ((searchVal != null) && !(searchVal.equals ("-1")))
                        {
                            match = SQLConstants.MATCH_EXACT;
                            // multiple selection box?
                            if (fieldType
                                .indexOf (QueryConstants.QUERYSELECTIONBOX_PARAM_MULTIPLE) != -1)
                            {
                                match = SQLConstants.MATCH_IN;
                            } // if (fieldType.indexOf
                              // (QueryConstants.QUERYSELECTIONBOX_PARAM_MULTIPLE)
                              // == -1)
                            // add selected item of the selection box to the
                            // query conditions
                            sqlConditionString = SQLHelpers
                                .getQueryConditionNumber (new StringBuffer (
                                    quAttribute), match, new StringBuffer (
                                    searchVal));
                        } // if
                    } // if queryselectionnum
                    // FIELDTYPE_QUERYSELECTIONOID
                    else if (fieldType
                        .startsWith (QueryConstants.FIELDTYPE_QUERYSELECTIONOID))
                    {
                        // check if searchVal is set
                        if ((searchVal != null)
                            && !(searchVal.equals (OID.EMPTYOID)))
                        {
                            // multiple selection box?
                            if (fieldType
                                .indexOf (QueryConstants.QUERYSELECTIONBOX_PARAM_MULTIPLE) != -1)
                            {
                                // we handle the QUERYSELECTIONOID like a
                                // QUERYSELECTION since the query clause created
                                // is the same (except that the oid are not
                                // in brackets
                                sqlConditionString = SQLHelpers
                                    .getQueryConditionString (quAttribute,
                                        SQLConstants.MATCH_IN, searchVal, false);
                            } // if (fieldType.indexOf
                              // (QueryConstants.QUERYSELECTIONBOX_PARAM_MULTIPLE)
                              // == -1)
                            else
                            // single selection box
                            {
                                // add selected item of the selection box to the
                                // query conditions
                                try
                                {
                                    sqlConditionString = new StringBuffer ()
                                        .append (SQLHelpers
                                            .getQueryConditionOid (quAttribute,
                                                searchVal));
                                } // try
                                catch (IncorrectOidException e)
                                {
                                    IOHelpers
                                        .showMessage (
                                            QueryConstants.EXC_INCORRECTOIDEXCEPTION
                                                + fieldName
                                                + ","
                                                + fieldType
                                                + " oid = " + searchVal, e,
                                            this.app, this.sess, this.env,
                                            false);
                                } // catch
                            } // else single selection box
                        } // if
                    } // if queryselectionoid
                    // FIELDTYPE_QUERYSELECTION
                    else if (fieldType
                        .startsWith (QueryConstants.FIELDTYPE_QUERYSELECTION))
                    {
                        // check if searchVal is set
                        // note that a QUERYSELECTION can have " " as
                        // value. that means no value selected.
                        // these values are ignored through searchVal.trim()
                        if ((searchVal != null)
                            && !(searchVal.trim ().isEmpty ()))
                        {
                            // single selection box?
                            if (fieldType
                                .indexOf (QueryConstants.QUERYSELECTIONBOX_PARAM_MULTIPLE) == -1)
                            {
                                // check for emtpy option
                                // add selected item of the selection box to the
                                // query conditions
                                sqlConditionString = SQLHelpers
                                    .getQueryConditionString (quAttribute,
                                        SQLConstants.MATCH_EXACT, searchVal,
                                        false);
                            } // if (fieldType.indexOf
                              // (QueryConstants.QUERYSELECTIONBOX_PARAM_MULTIPLE)
                              // == -1)
                            else
                            // multiple selection box
                            {
                                sqlConditionString = SQLHelpers
                                    .getQueryConditionString (quAttribute,
                                        SQLConstants.MATCH_IN, searchVal, false);
                            } // else // multiple selection box
                        } // if
                    } // if queryselection
                    // FIELDTYPE_VALUEDOMAINNUM
                    if (fieldType
                        .startsWith (QueryConstants.FIELDTYPE_VALUEDOMAINNUM))
                    {
                        // check if searchVal is set
                        if ((searchVal != null) && !(searchVal.equals ("-1")))
                        {
                            // !multiple
                            if (fieldType
                                .indexOf (QueryConstants.FIELDTYPE_MODIFIER_MULTIPLE) == -1)
                            {
                                // set match type
                                match = SQLConstants.MATCH_EXACT;
                            } // if !multiple
                            else
                            // multiple
                            {
                                // set match type
                                match = SQLConstants.MATCH_SUBSTRING;
                            } // else multiple

                            // add selected item of the selection box to the
                            // query conditions
                            sqlConditionString = SQLHelpers
                                .getQueryConditionNumber (new StringBuffer (
                                    quAttribute), match, new StringBuffer (
                                    searchVal));
                        } // if
                    } // if FIELDTYPE_VALUEDOMAINNUM
                    // FIELDTYPE_VALUEDOMAINOID
                    else if (fieldType
                        .startsWith (QueryConstants.FIELDTYPE_VALUEDOMAINOID))
                    {
                        // check if searchVal is set
                        if ((searchVal != null)
                            && !(searchVal.equals (OID.EMPTYOID)))
                        {
                            // !multiple
                            if (fieldType
                                .indexOf (QueryConstants.FIELDTYPE_MODIFIER_MULTIPLE) == -1)
                            {
                                // add selected item of the selection box to the
                                // query conditions
                                try
                                {
                                    sqlConditionString = new StringBuffer ()
                                        .append (SQLHelpers
                                            .getQueryConditionOid (quAttribute,
                                                searchVal));
                                } // try
                                catch (IncorrectOidException e)
                                {
                                    IOHelpers
                                        .showMessage (
                                            QueryConstants.EXC_INCORRECTOIDEXCEPTION
                                                + fieldName
                                                + ","
                                                + fieldType
                                                + " oid = " + searchVal, e,
                                            this.app, this.sess, this.env,
                                            false);
                                } // catch
                            } // if !multiple
                            else
                            // multiple
                            {
                                // set match type
                                match = SQLConstants.MATCH_SUBSTRING;
                                // add selected item of the selection box to the
                                // query conditions
                                sqlConditionString = SQLHelpers
                                    .getQueryConditionString (quAttribute,
                                        match, searchVal, false);
                            } // else multiple
                        } // if
                    } // if FIELDTYPE_VALUEDOMAINOID
                    // FIELDTYPE_VALUEDOMAIN
                    else if (fieldType
                        .startsWith (QueryConstants.FIELDTYPE_VALUEDOMAIN))
                    {
                        // check if searchVal is set
                        if ((searchVal != null) && !(searchVal.equals (" "))
                            && !(searchVal.length () == 0))
                        {
                            // !multiple
                            if (fieldType
                                .indexOf (QueryConstants.FIELDTYPE_MODIFIER_MULTIPLE) == -1)
                            {
                                // set match type
                                match = SQLConstants.MATCH_EXACT;
                            } // if !multiple
                            else
                            // multiple
                            {
                                // set match type
                                match = SQLConstants.MATCH_SUBSTRING;
                            } // else multiple

                            // add selected item of the selection box to the
                            // query conditions
                            sqlConditionString = SQLHelpers
                                .getQueryConditionString (quAttribute, match,
                                    searchVal, false);
                        } // if
                    } // if FIELDTYPE_VALUEDOMAIN
                    // FIELDTYPE_LONGTEXT
                    else if (fieldType
                        .equals (QueryConstants.FIELDTYPE_LONGTEXT)
                        && searchVal != null && searchVal.length () > 0)
                    {
                        sqlConditionString = new StringBuffer ()
                            .append (SQLHelpers.getQueryConditionLongText (
                                quAttribute, match, searchVal));
                    } // if longtext
                    // FIELDTYPE_BOOLEAN
                    else if (fieldType
                        .equals (QueryConstants.FIELDTYPE_BOOLEAN)
                        && searchVal != null && searchVal.length () > 0)
                    {
                        sqlConditionString = new StringBuffer ()
                            .append (SQLHelpers.getQueryConditionBoolean (
                                quAttribute, searchVal));
                    } // else if boolean

                    // FIELDTYPE_INTEGER
                    else if ((fieldType
                        .equals (QueryConstants.FIELDTYPE_INTEGER)
                        || fieldType
                            .equals (QueryConstants.FIELDTYPE_LESS_INTEGER)
                        || fieldType
                            .equals (QueryConstants.FIELDTYPE_LESS_EQUAL_INTEGER)
                        || fieldType
                            .equals (QueryConstants.FIELDTYPE_GREATER_INTEGER) || fieldType
                        .equals (QueryConstants.FIELDTYPE_GREATER_EQUAL_INTEGER))
                        && searchVal != null && searchVal.length () > 0)
                    {
                        // check which match type should be set for condition
                        // field
                        if (fieldType
                            .equals (QueryConstants.FIELDTYPE_LESS_INTEGER))
                        {
                            match = SQLConstants.MATCH_LESS;
                        } // if
                        else if (fieldType
                            .equals (QueryConstants.FIELDTYPE_LESS_EQUAL_INTEGER))
                        {
                            match = SQLConstants.MATCH_LESSEQUAL;
                        } // else if
                        else if (fieldType
                            .equals (QueryConstants.FIELDTYPE_GREATER_INTEGER))
                        {
                            match = SQLConstants.MATCH_GREATER;
                        } // else if
                        else if (fieldType
                            .equals (QueryConstants.FIELDTYPE_GREATER_EQUAL_INTEGER))
                        {
                            match = SQLConstants.MATCH_GREATEREQUAL;
                        } // else if

                        sqlConditionString = SQLHelpers
                            .getQueryConditionNumber (new StringBuffer (
                                quAttribute), match, new StringBuffer (
                                searchVal));
                    } // else if INTEGER

                    // FIELDTYPE_NUMBER
                    else if ((fieldType
                        .equals (QueryConstants.FIELDTYPE_NUMBER)
                        || fieldType
                            .equals (QueryConstants.FIELDTYPE_LESS_NUMBER)
                        || fieldType
                            .equals (QueryConstants.FIELDTYPE_LESS_EQUAL_NUMBER)
                        || fieldType
                            .equals (QueryConstants.FIELDTYPE_GREATER_NUMBER) || fieldType
                        .equals (QueryConstants.FIELDTYPE_GREATER_EQUAL_NUMBER))
                        && searchVal != null && searchVal.length () > 0)
                    {
                        // check which match type should be set for condition
                        // field
                        if (fieldType
                            .equals (QueryConstants.FIELDTYPE_LESS_NUMBER))
                        {
                            match = SQLConstants.MATCH_LESS;
                        } // if
                        else if (fieldType
                            .equals (QueryConstants.FIELDTYPE_LESS_EQUAL_NUMBER))
                        {
                            match = SQLConstants.MATCH_LESSEQUAL;
                        } // else if
                        else if (fieldType
                            .equals (QueryConstants.FIELDTYPE_GREATER_NUMBER))
                        {
                            match = SQLConstants.MATCH_GREATER;
                        } // else if
                        else if (fieldType
                            .equals (QueryConstants.FIELDTYPE_GREATER_EQUAL_NUMBER))
                        {
                            match = SQLConstants.MATCH_GREATEREQUAL;
                        } // else if

                        sqlConditionString = SQLHelpers
                            .getQueryConditionNumber (new StringBuffer (
                                quAttribute), match, new StringBuffer (
                                searchVal));
                    } // else if NUMBER

                    // FIELDTYPE_MONEY
                    else if ((fieldType.equals (QueryConstants.FIELDTYPE_MONEY)
                        || fieldType
                            .equals (QueryConstants.FIELDTYPE_LESS_MONEY)
                        || fieldType
                            .equals (QueryConstants.FIELDTYPE_LESS_EQUAL_MONEY)
                        || fieldType
                            .equals (QueryConstants.FIELDTYPE_GREATER_MONEY) || fieldType
                        .equals (QueryConstants.FIELDTYPE_GREATER_EQUAL_MONEY))
                        && searchVal != null && searchVal.length () > 0)
                    {
                        // check which match type should be set for condition
                        // field
                        if (fieldType
                            .equals (QueryConstants.FIELDTYPE_LESS_MONEY))
                        {
                            match = SQLConstants.MATCH_LESS;
                        } // if
                        else if (fieldType
                            .equals (QueryConstants.FIELDTYPE_LESS_EQUAL_MONEY))
                        {
                            match = SQLConstants.MATCH_LESSEQUAL;
                        } // else if
                        else if (fieldType
                            .equals (QueryConstants.FIELDTYPE_GREATER_MONEY))
                        {
                            match = SQLConstants.MATCH_GREATER;
                        } // else if
                        else if (fieldType
                            .equals (QueryConstants.FIELDTYPE_GREATER_EQUAL_MONEY))
                        {
                            match = SQLConstants.MATCH_GREATEREQUAL;
                        } // else if

                        sqlConditionString = SQLHelpers.getQueryConditionMoney (
                            new StringBuffer (quAttribute), match,
                            new StringBuffer (searchVal));
                    } // else if MONEY

                    // DATE
                    else if ((fieldType.equals (QueryConstants.FIELDTYPE_DATE)
                        || fieldType
                            .equals (QueryConstants.FIELDTYPE_LESS_DATE)
                        || fieldType
                            .equals (QueryConstants.FIELDTYPE_LESS_EQUAL_DATE)
                        || fieldType
                            .equals (QueryConstants.FIELDTYPE_GREATER_DATE) || fieldType
                        .equals (QueryConstants.FIELDTYPE_GREATER_EQUAL_DATE))
                        && searchVal != null && searchVal.length () > 0)
                    {
                        // check which match type should be set for condition
                        // field
                        if (fieldType
                            .equals (QueryConstants.FIELDTYPE_LESS_DATE))
                        {
                            match = SQLConstants.MATCH_LESS;
                        } // if
                        else if (fieldType
                            .equals (QueryConstants.FIELDTYPE_LESS_EQUAL_DATE))
                        {
                            match = SQLConstants.MATCH_LESSEQUAL;
                        } // else if
                        else if (fieldType
                            .equals (QueryConstants.FIELDTYPE_GREATER_DATE))
                        {
                            match = SQLConstants.MATCH_GREATER;
                        } // else if
                        else if (fieldType
                            .equals (QueryConstants.FIELDTYPE_GREATER_EQUAL_DATE))
                        {
                            match = SQLConstants.MATCH_GREATEREQUAL;
                        } // else if

                        sqlConditionString = new StringBuffer ()
                            .append (SQLHelpers.getQueryConditionDate (
                                quAttribute, match, searchVal));
                    } // else if date
                    // TIME
                    else if ((fieldType.equals (QueryConstants.FIELDTYPE_TIME)
                        || fieldType
                            .equals (QueryConstants.FIELDTYPE_LESS_TIME)
                        || fieldType
                            .equals (QueryConstants.FIELDTYPE_LESS_EQUAL_TIME)
                        || fieldType
                            .equals (QueryConstants.FIELDTYPE_GREATER_TIME) || fieldType
                        .equals (QueryConstants.FIELDTYPE_GREATER_EQUAL_TIME))
                        && searchVal != null && searchVal.length () > 0)
                    {
                        // check which match type should be set for condition
                        // field
                        if (fieldType
                            .equals (QueryConstants.FIELDTYPE_LESS_TIME))
                        {
                            match = SQLConstants.MATCH_LESS;
                        } // if
                        else if (fieldType
                            .equals (QueryConstants.FIELDTYPE_LESS_EQUAL_TIME))
                        {
                            match = SQLConstants.MATCH_LESSEQUAL;
                        } // else if
                        else if (fieldType
                            .equals (QueryConstants.FIELDTYPE_GREATER_TIME))
                        {
                            match = SQLConstants.MATCH_GREATER;
                        } // else if
                        else if (fieldType
                            .equals (QueryConstants.FIELDTYPE_GREATER_EQUAL_TIME))
                        {
                            match = SQLConstants.MATCH_GREATEREQUAL;
                        } // else if

                        sqlConditionString = SQLHelpers.getQueryConditionTime (
                            new StringBuffer (quAttribute), match,
                            new StringBuffer (searchVal));
                    } // else if time
                    // DATETIME
                    else if (fieldType
                        .equals (QueryConstants.FIELDTYPE_DATETIME)
                        && searchVal != null && searchVal.length () > 0)
                    {
                        sqlConditionString = SQLHelpers
                            .getQueryConditionDateTime (new StringBuffer (
                                quAttribute), match, new StringBuffer (
                                searchVal));
                    } // else if datetime

                    // NUMBERRANGE || MONEYRANGE ||
                    // DATERANGE || TIMERANGE || DATETIMERANGE
                    else if (fieldType
                        .equals (QueryConstants.FIELDTYPE_INTEGERRANGE)
                        || fieldType
                            .equals (QueryConstants.FIELDTYPE_NUMBERRANGE)
                        || fieldType
                            .equals (QueryConstants.FIELDTYPE_MONEYRANGE)
                        || fieldType
                            .equals (QueryConstants.FIELDTYPE_DATERANGE)
                        || fieldType
                            .equals (QueryConstants.FIELDTYPE_TIMERANGE)
                        || fieldType
                            .equals (QueryConstants.FIELDTYPE_DATETIMERANGE))
                    {
                        sqlConditionString = this.getQueryRangeCondition (
                            fieldType, quAttribute, searchVal, rangeVal);
                    } // if

                    // FIELDTYPE_OBJECTPATH is used if query should use an
                    // rootobject (object from which
                    // the search starts) - rootObjectOid has to be set before
                    else if (fieldType
                        .equals (QueryConstants.FIELDTYPE_OBJECTPATH)
                        && this.rootObjectOid != null
                        && !this.rootObjectOid.isEmpty ())
                    {
                        // expand queryString with string to start search in
                        // specific container
                        sqlConditionString = SQLHelpers
                            .getQueryConditionAttribute (quAttribute,
                                SQLConstants.MATCH_STARTSWITH,
                                "root.posnopath", true);
                    } // else if FIELDTYPE_OBJECTPATH
                    // FIELDTYPE_OBJECTID
                    else if (fieldType
                        .equals (QueryConstants.FIELDTYPE_OBJECTID)
                        && searchVal != null && searchVal.length () > 0)
                    {
                        // local oid
                        OID searchOid = null;
                        try
                        {
                            // convert string to OID
                            searchOid = new OID (searchVal);

                            // add oid condition to complete condition string
                            sqlConditionString = new StringBuffer ()
                                .append (SQLHelpers.getQueryConditionOid (
                                    quAttribute, searchOid));
                        } // try
                        catch (IncorrectOidException e)
                        {
                            // this exception is handled in QueryExecutive
                            // with javainterface, it is not possible, that oid
                            // is
                            // incorrect, and this objecttype is only used
                            // for javainterface
                        } // catch
                    } // else if FIELDTYPE_OBJECTID

                    // set sqlOperation to AND if there are already some
                    // conditions
                    if (completeConditionString.length () > 0)
                    {
                        sqlOperation = andOperation;
                    } // if

                    // expand completeConditionString with new condition string
                    if (sqlConditionString != null
                        && sqlConditionString.length () > 0)
                    {
                        completeConditionString.append (sqlOperation).append (
                            sqlConditionString);
                    } // expand completeConditionString
                } // constraints fulfilled
            } // for iter
        } // if there are any fields (fieldAttrMapping != null)

        return (completeConditionString.length () > 0) ? completeConditionString
            .toString ()
            : null;
    } // getQuerySearchFieldConditions


    /**************************************************************************
     * Get range condition for where clause in query. <BR/>
     * 
     * @param fieldType The type of the field.
     * @param quAttribute Attribute which shall be compared in the condition.
     * @param searchVal The (lower) search value to compare the attribute with.
     * @param rangeVal The (bigger) range value for comparison.
     * @return The constructed condition string - NOT starting with any operator
     *         (AND...).
     */
    public StringBuffer getQueryRangeCondition (String fieldType,
        String quAttribute, String searchVal, String rangeVal)
    {
        StringBuffer queryConditionString = new StringBuffer ();
        StringBuffer queryCondStrTemp = null;
        StringBuffer concat = new StringBuffer ("");

        // check if we have a valid search value:
        if (searchVal != null && searchVal.length () > 0)
        {
            // get condition:
            queryCondStrTemp = this.getQueryCondition (fieldType, quAttribute,
                searchVal, SQLConstants.MATCH_GREATEREQUAL);

            // check if we found a valid condition:
            if (queryCondStrTemp != null)
            {
                queryConditionString.append (queryCondStrTemp);
            } // if
        } // if (searchVal != null && !searchVal.length () == 0)

        // check if we have a valid range value:
        if (rangeVal != null && rangeVal.length () > 0)
        {
            // check if we have to append two conditions:
            if (queryCondStrTemp != null && queryCondStrTemp.length () != 0)
            {
                concat.append (" AND ");
            } // if

            // get condition:
            queryCondStrTemp = this.getQueryCondition (fieldType, quAttribute,
                rangeVal, SQLConstants.MATCH_LESSEQUAL);

            // check if we found a valid condition:
            if (queryCondStrTemp != null)
            {
                queryConditionString.append (concat).append (queryCondStrTemp);
            } // if
        } // if (searchVal != null && !searchVal.length () == 0)

        // return the result:
        return queryConditionString;
    } // getQueryRangeCondition


    /**************************************************************************
     * Get condition for where clause in query. <BR/>
     * 
     * @param fieldType The type of the field.
     * @param quAttribute Attribute which shall be compared in the condition.
     * @param value The value to compare the attribute with.
     * @param matchType Match type for comparison.
     * @return The constructed condition string - NOT starting with any operator
     *         (AND...).
     */
    public StringBuffer getQueryCondition (String fieldType,
        String quAttribute, String value, String matchType)
    {
        StringBuffer queryConditionString = null;

        // check if we have a valid value:
        if (value != null && value.length () > 0) // value is valid?
        {
            // check field type:
            // INTEGERRANGE || NUMBERRANGE
            if (fieldType.equals (QueryConstants.FIELDTYPE_INTEGERRANGE)
                || fieldType.equals (QueryConstants.FIELDTYPE_NUMBERRANGE))
            {
                // get condition:
                queryConditionString = SQLHelpers.getQueryConditionNumber (
                    new StringBuffer (quAttribute), matchType,
                    new StringBuffer (value));
            } // else if INTEGERRANGE || NUMBERRANGE
            // MONEYRANGE
            else if (fieldType.equals (QueryConstants.FIELDTYPE_MONEYRANGE))
            {
                // get condition:
                queryConditionString = SQLHelpers.getQueryConditionMoney (
                    new StringBuffer (quAttribute), matchType,
                    new StringBuffer (value));
            } // if MONEYRANGE
            // DATERANGE
            else if (fieldType.equals (QueryConstants.FIELDTYPE_DATERANGE))
            {
                // get condition:
                queryConditionString = SQLHelpers.getQueryConditionDateTime (
                    new StringBuffer (quAttribute), matchType,
                    new StringBuffer (value));
            } // if DATERANGE
            // TIMERANGE
            else if (fieldType.equals (QueryConstants.FIELDTYPE_TIMERANGE))
            {
                // get condition:
                queryConditionString = SQLHelpers.getQueryConditionTime (
                    new StringBuffer (quAttribute), matchType,
                    new StringBuffer (value));
            } // if TIMERANGE
            // DATETIMERANGE
            else if (fieldType.equals (QueryConstants.FIELDTYPE_DATETIMERANGE))
            {
                // get condition:
                queryConditionString = SQLHelpers.getQueryConditionDateTime (
                    new StringBuffer (quAttribute), matchType,
                    new StringBuffer (value));
            } // if DATETIMERANGE
        } // if value is valid

        // return the result:
        return queryConditionString;
    } // getQueryCondition


    // /////////////////////////////////////////////////////////////////////////
    // database methods
    // /////////////////////////////////////////////////////////////////////////

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
        // QUERTYPE
        sp.addInParameter (ParameterConstants.TYPE_INTEGER,
            this.queryType);

        // set the specific parameters:
        // GROUP BY
        sp.addInParameter (ParameterConstants.TYPE_STRING,
            this.groupByString);

        // set the specific parameters:
        // ORDER BY
        sp.addInParameter (ParameterConstants.TYPE_STRING,
            this.orderByString);

        // value of the resultCounter
        sp.addInParameter (ParameterConstants.TYPE_INTEGER,
            this.resultCounter);

        // flag of enable debugging
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN,
            this.isEnableDebugging);

        // ORDER BY
        sp.addInParameter (ParameterConstants.TYPE_STRING,
            this.category);
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
        // trace ("AJ QueryCreator_01.setSpecificRetrieveParameters ");
        int i = lastIndex; // initialize params index

        // set the specific parameters:
        // queryType
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);

        // set the specific parameters:
        // groupByString
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);

        // set the specific parameters:
        // orderByString
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);

        // resultCounter
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);

        // enableDebug
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_BOOLEAN);

        // category
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);

        return i; // return the current index
    } // setSpecificRetrieveParameters


    /**************************************************************************
     * Get the data for the additional (type specific) parameters for
     * performRetrieveData. <BR/>
     * This method must be overwritten by all subclasses that have to get type
     * specific data from the retrieve data stored procedure.
     * 
     * @param params The array of parameters from the retrieve data stored
     *            procedure.
     * @param lastIndex The index to the last element used in params thus far.
     */
    protected void getSpecificRetrieveParameters (Parameter [] params,
        int lastIndex)
    {
        // trace ("AJ QueryCreator_01.getSpecificRetrieveParameters ");
        int i = lastIndex; // initialize params index

        // query type:
        this.queryType = params[++i].getValueInteger ();
        // group by
        this.groupByString = params[++i].getValueString ();
        // order by
        this.orderByString = params[++i].getValueString ();
        // resultCounter
        this.resultCounter = params[++i].getValueInteger ();
        // flag if enable debugging
        this.isEnableDebugging = params[++i].getValueBoolean ();
        // category for grouping
        // BB20080708: Note that when storing an empty string the database
        // stores and returns a " ". Is this a general problem in the base?
        // We therefore use a trim() which is not a proper solution

        this.category = params[++i].getValueString ().trim ();
    } // getSpecificRetrieveParameters


    /**************************************************************************
     * Change all type specific data that is not changed by performChangeData. <BR/>
     * This method must be overwritten by all subclasses that have to change
     * type specific data.
     * 
     * @param action SQL Action for Database
     * @exception DBError This exception is always thrown, if there happens an
     *                error during accessing data.
     */
    protected void performChangeSpecificData (SQLAction action) throws DBError
    {
        // trace ("AJ QueryCreator_01.performChangeSpecificData ");
        // SELECT
        this.performChangeTextData (action, this.tableName, "selectString",
            this.selectString);
        // FROM
        this.performChangeTextData (action, this.tableName, "fromString",
            this.fromString);
        // WHERE
        this.performChangeTextData (action, this.tableName, "whereString",
            this.whereString);
        // columnHeaders
        this.performChangeTextData (action, this.tableName, "columnHeaders",
            this.columnHeaders);
        // queryColumnAttributes
        this.performChangeTextData (action, this.tableName,
            "queryAttrForHeaders", this.queryColumnAttributes);
        // columnAttributeTypes
        this.performChangeTextData (action, this.tableName,
            "queryAttrTypesForHeaders", this.columnTypes);
        // searchFields
        this.performChangeTextData (action, this.tableName,
            "searchFieldTokens", this.searchFields);
        // queryFieldAttributes
        this.performChangeTextData (action, this.tableName,
            "queryAttrForFields", this.queryFieldAttributes);
        // queryFieldAttributeTypes
        this.performChangeTextData (action, this.tableName,
            "queryAttrTypesForFields", this.searchFieldTypes);
        // initialize the multilingual fields
        this.initMultilangInfo (MultilingualTextProvider.getAllLocales (env),
            this.name, env);
    } // performChangeSpecificData


    /**************************************************************************
     * Retrieve the type specific data that is not got from the stored
     * procedure. <BR/>
     * This method must be overwritten by all subclasses that have to get type
     * specific data that cannot be got from the retrieve data stored procedure.
     * 
     * @param action The action object associated with the connection.
     * @exception DBError This exception is always thrown, if there happens an
     *                error during accessing data.
     */
    protected void performRetrieveSpecificData (SQLAction action)
        throws DBError
    {
        // trace ("AJ QueryCreator_01.performRetrieveSpecificData ");
        // select
        this.selectString = this.performRetrieveTextData (action,
            this.tableName, "selectString", "p_QueryCreator_01$getExtSelect");

        // from
        this.fromString = this.performRetrieveTextData (action, this.tableName,
            "fromString", "p_QueryCreator_01$getExtFrom");

        // where
        this.whereString = this.performRetrieveTextData (action,
            this.tableName, "whereString", "p_QueryCreator_01$getExtWhere");

        // columnheader
        this.columnHeaders = this.performRetrieveTextData (action,
            this.tableName, "columnHeaders", "p_QueryCreator_01$getExtHead");

        // columnattributes
        this.queryColumnAttributes = this.performRetrieveTextData (action,
            this.tableName, "queryAttrForHeaders",
            "p_QueryCreator_01$getExtColAt");

        // columntypes
        this.columnTypes = this.performRetrieveTextData (action,
            this.tableName, "queryAttrTypesForHeaders",
            "p_QueryCreator_01$getColAtrTp");

        // searchfield names
        this.searchFields = this.performRetrieveTextData (action,
            this.tableName, "searchFieldTokens",
            "p_QueryCreator_01$getExtField");

        // searchfield attributes
        this.queryFieldAttributes = this.performRetrieveTextData (action,
            this.tableName, "queryAttrForFields",
            "p_QueryCreator_01$getExtFieAt");

        // searchfield types
        this.searchFieldTypes = this.performRetrieveTextData (action,
            this.tableName, "queryAttrTypesForFields",
            "p_QueryCreator_01$getExtFieTp");

        // initialize the multilingual fields
        this.initMultilangInfo (MultilingualTextProvider.getAllLocales (env),
            this.name, env);

        this.performAttributeMapping ();
    } // performRetrieveSpecificData


    // /////////////////////////////////////////////////////////////////////////
    // GUI methods
    // /////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Gets the parameters which are relevant for this object. <BR/>
     * The <A HREF="#env">env</A> property is used for getting the parameters.
     * This property must be set before calling this method. <BR/>
     */
    public void getParameters ()
    {
        // trace ("AJ QueryCreator_01.getParameters ");
        String [] strArr = null;
        String text = null;
        int intValue = -1;

        // get other parameters
        super.getParameters ();

        // QUERYTYPE
        if ((strArr = this.env
            .getMultipleFormParam (QueryArguments.ARG_QUERYTYPE)) != null)
        {
            this.queryType = QueryConstants.QT_NONE;

            // create bit pattern for selected queryTypes
            for (int i = 0; i < strArr.length; i++)
            {
                this.queryType += Integer.valueOf (strArr[i]).intValue ();
            } // for
        } // if

        // SELECT
        if ((text = this.env.getParam (QueryArguments.ARG_SELECT)) != null)
        {
            this.selectString = text;
        } // if

        // FROM
        if ((text = this.env.getParam (QueryArguments.ARG_FROM)) != null)
        {
            this.fromString = text;
        } // if

        // WHERE
        if ((text = this.env.getParam (QueryArguments.ARG_WHERE)) != null)
        {
            this.whereString = text;
        } // if

        // GROUP BY
        if ((text = this.env.getParam (QueryArguments.ARG_GROUPBY)) != null)
        {
            this.groupByString = text;
        } // if

        // ORDER BY
        if ((text = this.env.getParam (QueryArguments.ARG_ORDERBY)) != null)
        {
            this.orderByString = text;
        } // if

        // list headers
        if ((text = this.env.getParam (QueryArguments.ARG_COLUMNHEADERS)) != null)
        {
            this.columnHeaders = text;
        } // if

        // column attributes
        if ((text = this.env.getParam (QueryArguments.ARG_COLUMNATTRIBUTES)) != null)
        {
            this.queryColumnAttributes = text;
        } // if

        // column types
        if ((text = this.env.getParam (QueryArguments.ARG_COLUMNTYPES)) != null)
        {
            this.columnTypes = text;
        } // if

        // search - input fields
        if ((text = this.env.getParam (QueryArguments.ARG_FIELDNAMES)) != null)
        {
            this.searchFields = text;
        } // if

        // field attributes
        if ((text = this.env.getParam (QueryArguments.ARG_FIELDATTRIBUTES)) != null)
        {
            this.queryFieldAttributes = text;
        } // if

        // field types
        if ((text = this.env.getParam (QueryArguments.ARG_FIELDTYPES)) != null)
        {
            this.searchFieldTypes = text;
        } // if

        // resultCounter
        if ((intValue = this.env.getIntParam (QueryArguments.ARG_RESULTCOUNTER)) != IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
        {
            this.resultCounter = intValue;
        } // if

        // enable debugging
        if ((intValue = this.env.getBoolParam (QueryArguments.ARG_DEBUGGING)) >= IOConstants.BOOLPARAM_FALSE)
        {
            this.isEnableDebugging = intValue == IOConstants.BOOLPARAM_TRUE;
        } // if

        // category
        if ((text = this.env.getParam (QueryArguments.ARG_CATEGORY)) != null)
        {
            this.category = text;
        } // if

        // map headers and fields to attributes in query
        this.performAttributeMapping ();
    } // getParameters


    /**************************************************************************
     * Represent the properties of a ProductGroup_01 object to the user. <BR/>
     * 
     * @param table Table where the properties shall be added.
     * @see ibs.IbsObject#showProperty (TableElement, String, String, int,
     *      String)
     */
    protected void showProperties (TableElement table)
    {
        // trace ("AJ QueryCreator_01.showProperties ");
        // queryType
        String queryTypeString = "";

        // loop through all properties of this object and display them:
        super.showProperties (table);

        // walk through all possible query types and concatenate string
        // which contains the name of the selected one
        // and uses delimiter ',' between the query typenames
        int bit = 1;
        for (int i = 0; i < QueryConstants.CONST_QUERYTYPE_SELECTION.length; i++, bit *= 2)
        {
            // check if query type is used for search
            if ((this.queryType & bit) == bit)
            {
                // add delimiter if queryTypeString is not empty
                if (queryTypeString.length () > 0)
                {
                    queryTypeString += ",";
                } // if

                // concatenate current query type name to query type string
                queryTypeString += QueryConstants.CONST_QUERYTYPE_SELECTION[i];
            } // if
        } // for

        // category
        this
            .showProperty (table, QueryArguments.ARG_CATEGORY,
                MultilingualTextProvider.getText (QueryTokens.TOK_BUNDLE,
                    QueryTokens.ML_CATEGORY, env), Datatypes.DT_NAME,
                this.category);

        // queryType
        this.showProperty (table, QueryArguments.ARG_QUERYTYPE,
            MultilingualTextProvider.getText (QueryTokens.TOK_BUNDLE,
                QueryTokens.ML_QUERYTYPE, env), Datatypes.DT_DESCRIPTION,
            queryTypeString);

        // FULL QUERY
        StringBuffer query = this.getQuery ();
        if (query == null)
        {
            query = new StringBuffer ();
        } // if

        // open query button
        GroupElement group = new GroupElement ();
        HTMLButtonElement button = new HTMLButtonElement ();
        button.addLabel (MultilingualTextProvider.getText (
            QueryTokens.TOK_BUNDLE, QueryTokens.ML_OPEN_QUERY, env));
        button.isDisabledOnClick = false;
        button.onClick = "top.scripts.showSearch ('" + this.name + "', '"
            + this.oid + "');";
        group.addElement (button);

        group.addElement (new BlankElement ());
        group.addElement (new BlankElement ());
        group.addElement (new BlankElement ());

        // export query button
        button = new HTMLButtonElement ();
        button.addLabel (MultilingualTextProvider.getText (
            QueryTokens.TOK_BUNDLE, QueryTokens.ML_EXPORT_QUERY, env));
        button.isDisabledOnClick = false;
        button.onClick = "exportQuery ();";
        group.addElement (button);

        StringBuffer scriptBuffer = new StringBuffer ().append (
            "function exportQuery ()").append ("\n{").append (
            "\nvar fct = 406; ").append ("\nvar target = top.sheet; ").append (
            "\nvar name = (target == top) ? \"top\" : target.name; ").append (
            "\ntop.callUrl (fct, \"&oid=\" + top.oid + ").append (
            "\n                  \"&cid=\" + top.containerId + ").append (
            "\n                     \"&exo=\" + top.oid, null, name);").append (
            "\n}");

        ScriptElement script = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);
        script.addScript (scriptBuffer);
        group.addElement (script);

        // query
        group.addElement (new TextElement (this.getFormattedQuery ()
            .toString ()));

        this.showFormProperty (table, MultilingualTextProvider.getText (
            QueryTokens.TOK_BUNDLE, QueryTokens.ML_QUERY, env), group);

        // listColumnHeaders
        this.showProperty (table, QueryArguments.ARG_COLUMNHEADERS,
            MultilingualTextProvider.getText (QueryTokens.TOK_BUNDLE,
                QueryTokens.ML_COLUMNHEADERS, env), Datatypes.DT_HTMLTEXT,
            this.columnHeaders);

        // query columns for mapping
        this.showProperty (table, QueryArguments.ARG_COLUMNATTRIBUTES,
            MultilingualTextProvider.getText (QueryTokens.TOK_BUNDLE,
                QueryTokens.ML_COLUMNATTRIBUTES, env), Datatypes.DT_HTMLTEXT,
            this.queryColumnAttributes);

        // query columns for mapping
        this.showProperty (table, QueryArguments.ARG_COLUMNTYPES,
            MultilingualTextProvider.getText (QueryTokens.TOK_BUNDLE,
                QueryTokens.ML_COLUMNTYPES, env), Datatypes.DT_HTMLTEXT,
            this.columnTypes);

        // input-searchfields
        this.showProperty (table, QueryArguments.ARG_FIELDNAMES,
            MultilingualTextProvider.getText (QueryTokens.TOK_BUNDLE,
                QueryTokens.ML_FIELDNAMES, env), Datatypes.DT_HTMLTEXT,
            this.searchFields);

        // query columns for mapping
        this.showProperty (table, QueryArguments.ARG_FIELDATTRIBUTES,
            MultilingualTextProvider.getText (QueryTokens.TOK_BUNDLE,
                QueryTokens.ML_FIELDATTRIBUTES, env), Datatypes.DT_HTMLTEXT,
            this.queryFieldAttributes);

        // input-searchfieldtypes
        this.showProperty (table, QueryArguments.ARG_FIELDTYPES,
            MultilingualTextProvider.getText (QueryTokens.TOK_BUNDLE,
                QueryTokens.ML_FIELDTYPES, env), Datatypes.DT_HTMLTEXT,
            this.searchFieldTypes);

        // if there are no maximal result chosen show an empty option
        if (this.resultCounter <= -1)
        {
            // resultCounter
            this.showProperty (table, QueryArguments.ARG_MAX_RESULTS,
                MultilingualTextProvider.getText (QueryTokens.TOK_BUNDLE,
                    QueryTokens.ML_MAX_RESULTS, env), Datatypes.DT_INTEGER, "");

        } // if (this.resultCounter <= -1)
        else
        // user has entered a value
        {
            // resultCounter
            this.showProperty (table, QueryArguments.ARG_MAX_RESULTS,
                MultilingualTextProvider.getText (QueryTokens.TOK_BUNDLE,
                    QueryTokens.ML_MAX_RESULTS, env), Datatypes.DT_INTEGER,
                this.resultCounter);
        } // else
        // checkbox for enable show debugging
        this.showProperty (table, QueryArguments.ARG_SHOW_QUERY,
            MultilingualTextProvider.getText (QueryTokens.TOK_BUNDLE,
                QueryTokens.ML_SHOW_QUERY, env), Datatypes.DT_BOOL, ""
                + this.isEnableDebugging);

        // HIDDEN FIELDS
        // SELECT
        this.showProperty (table, QueryArguments.ARG_SELECT, // Argument
            "", // token
            Datatypes.DT_HIDDEN, this.selectString);

        // FROM
        this.showProperty (table, QueryArguments.ARG_FROM, // Argument
            "", // token
            Datatypes.DT_HIDDEN, this.fromString);

        // WHERE
        this.showProperty (table, QueryArguments.ARG_WHERE, // Argument
            "", // token
            Datatypes.DT_HIDDEN, this.whereString);

        // ORDER BY
        this.showProperty (table, QueryArguments.ARG_ORDERBY, // Argument
            "", // token
            Datatypes.DT_HIDDEN, this.orderByString);

    } // showProperties


    /***************************************************************************
     * Represent the properties of a ProductGroup_01 object to the user within a
     * form. <BR/>
     * 
     * @param table Table where the properties shall be added.
     * @see ibs.IbsObject#showFormProperty (TableElement, String, String, int,
     *      String)
     */
    protected void showFormProperties (TableElement table)
    {
        // trace ("AJ QueryCreator_01.showFormProperties ");
        // queryType
        int length = QueryConstants.CONST_QUERYTYPE_SELECTION.length;
        String [] queryTypeIds = new String[length];
        String [] selectedIds = new String[length];
        int j = 0;

        // loop through all properties of this object and display them:
        super.showFormProperties (table);
        // trace ("AJ QueryCreator_01.showFormProperties SELECT = " +
        // this.selectString);

        // CATEGORY
        // add a free field and a selection box showing all categories in use
        // selecting a category from the selection box overwrites the text
        // input field
        GroupElement group = new GroupElement ();
        SelectElement select = new SelectElement (
            QueryArguments.ARG_CATEGORY_SELECT);
        select.id = QueryArguments.ARG_CATEGORY_SELECT;
        // read and fill in the categories
        String [] categories = this.getQueryCategories ();
        for (int i = 0; i < categories.length; i++)
        {
            select.addOption (categories[i], categories[i]);
        } // for
        select.onChange = "this.document.getElementById('"
            + QueryArguments.ARG_CATEGORY
            + "').value = this.options[this.selectedIndex].text;";
        group.addElement (select);

        // add a &NBSP;
        group.addElement (new TextElement (IE302.HCH_NBSP));

        // add the input field
        InputElement input = new InputElement (QueryArguments.ARG_CATEGORY,
            InputElement.INP_TEXT, this.category);
        // add the restriction to the field
        FormFieldRestriction restriction = new FormFieldRestriction ();
        restriction.name = QueryArguments.ARG_CATEGORY;
        restriction.dataType = Datatypes.DT_NAME;
        // build JavaScript code for form field restrictions
        StringBuffer restrictionScript = restriction
            .buildRestrictScriptCode (env);
        input.onChange = restrictionScript.toString ();
        // now expand form field restriction for the onSubmit functionality
        // of the form
        this.expandFormFieldRestrictions (restrictionScript);
        input.setSize (restriction, 50, 63);
        group.addElement (input);
        this.showFormProperty (table, MultilingualTextProvider.getText (
            QueryTokens.TOK_BUNDLE, QueryTokens.ML_CATEGORY, env), group);

        // QUERYTYPE
        // create the bit pattern for all query types,
        // for later assigned the query type
        int quTyId = 1;
        for (int i = 0; i < length; i++, quTyId *= 2)
        {
            queryTypeIds[i] = Integer.toString (quTyId);

            // check if current bit pattern for the query type
            if ((this.queryType & quTyId) == quTyId)
            {
                // set id of query type in array for preselected
                // query type
                selectedIds[j++] = Integer.toString (quTyId);
            } // if
        } // for
        this.showFormProperty (table, QueryArguments.ARG_QUERYTYPE,
            MultilingualTextProvider.getText (QueryTokens.TOK_BUNDLE,
                QueryTokens.ML_QUERYTYPE, env), Datatypes.DT_MULTISELECT,
            selectedIds, queryTypeIds,
            QueryConstants.CONST_QUERYTYPE_SELECTION, 3);

        // SELECT
        this.showFormProperty (table, QueryArguments.ARG_SELECT, // Argument
            "SELECT", // token
            Datatypes.DT_HTMLTEXT, this.selectString);

        // trace ("AJ QueryCreator_01.showFormProperties FROM = " +
        // this.fromString);

        // FROM
        this.showFormProperty (table, QueryArguments.ARG_FROM, // Argument
            "FROM", // token
            Datatypes.DT_HTMLTEXT, this.fromString);
        // WHERE
        this.showFormProperty (table, QueryArguments.ARG_WHERE, // Argument
            "WHERE", // token
            Datatypes.DT_HTMLTEXT, this.whereString);

        // GROUP BY
        this.showFormProperty (table, QueryArguments.ARG_GROUPBY, // Argument
            "GROUP BY", // token
            Datatypes.DT_DESCRIPTION, this.groupByString);

        // ORDER BY
        this.showFormProperty (table, QueryArguments.ARG_ORDERBY, // Argument
            "ORDER BY", // token
            Datatypes.DT_DESCRIPTION, this.orderByString);

        // listColumnHeaders
        this.showFormProperty (table, QueryArguments.ARG_COLUMNHEADERS,
            MultilingualTextProvider.getText (QueryTokens.TOK_BUNDLE,
                QueryTokens.ML_COLUMNHEADERS, env), Datatypes.DT_HTMLTEXT,
            this.columnHeaders);

        // query columns for mapping
        this.showFormProperty (table, QueryArguments.ARG_COLUMNATTRIBUTES,
            MultilingualTextProvider.getText (QueryTokens.TOK_BUNDLE,
                QueryTokens.ML_COLUMNATTRIBUTES, env), Datatypes.DT_HTMLTEXT,
            this.queryColumnAttributes);

        // query columns for mapping
        this.showFormProperty (table, QueryArguments.ARG_COLUMNTYPES,
            MultilingualTextProvider.getText (QueryTokens.TOK_BUNDLE,
                QueryTokens.ML_COLUMNTYPES, env), Datatypes.DT_HTMLTEXT,
            this.columnTypes);

        // InputFields for search
        this.showFormProperty (table, QueryArguments.ARG_FIELDNAMES,
            MultilingualTextProvider.getText (QueryTokens.TOK_BUNDLE,
                QueryTokens.ML_FIELDNAMES, env), Datatypes.DT_HTMLTEXT,
            this.searchFields);

        // queryattributes for mapping to searchfields
        this.showFormProperty (table, QueryArguments.ARG_FIELDATTRIBUTES,
            MultilingualTextProvider.getText (QueryTokens.TOK_BUNDLE,
                QueryTokens.ML_FIELDATTRIBUTES, env), Datatypes.DT_HTMLTEXT,
            this.queryFieldAttributes);

        // Types of InputFields for search
        this.showFormProperty (table, QueryArguments.ARG_FIELDTYPES,
            MultilingualTextProvider.getText (QueryTokens.TOK_BUNDLE,
                QueryTokens.ML_FIELDTYPES, env), Datatypes.DT_HTMLTEXT,
            this.searchFieldTypes);

        // resultCounter
        this.showFormProperty (table, QueryArguments.ARG_RESULTCOUNTER,
            MultilingualTextProvider.getText (QueryTokens.TOK_BUNDLE,
                QueryTokens.ML_MAX_RESULTS, env), Datatypes.DT_INTEGER,
            (this.resultCounter <= -1) ? "" : "" + this.resultCounter);

        // enable debugging
        this.showFormProperty (table, QueryArguments.ARG_DEBUGGING,
            MultilingualTextProvider.getText (QueryTokens.TOK_BUNDLE,
                QueryTokens.ML_SHOW_QUERY, env), Datatypes.DT_BOOL, ""
                + this.isEnableDebugging);

    } // showFormProperties


    /**************************************************************************
     * Get all query categories in use within the system. <BR/>
     * 
     * @return a string array containing all query categories in use.
     */
    public String [] getQueryCategories ()
    {
        String [] queryCategories = null;

        Vector<String> resultVector = null;
        int rowCount;
        SQLAction action = null; // the action object used to access the

        StringBuffer queryStr = new StringBuffer ().append (
            " SELECT DISTINCT qc.category ").append (
            " FROM ibs_object oqc, ibs_QueryCreator_01 qc").append (
            " WHERE oqc.oid = qc.oid ").append (" AND oqc.state = ").append (
            States.ST_ACTIVE).append (" ORDER BY qc.category");

        try
        {
            action = DBConnector.getDBConnection ();
            rowCount = action.execute (queryStr, false);
            // empty resultset?
            if (rowCount > 0)
            {
                resultVector = new Vector<String> ();
                // get the oids
                while (!action.getEOF ())
                {
                    resultVector.addElement (action.getString ("category"));
                    action.next ();
                } // while (!action.getEOF())
                queryCategories = new String[resultVector.size ()];
                // now copy the result into an array
                for (int i = 0; i < resultVector.size (); i++)
                {
                    queryCategories[i] = resultVector.elementAt (i);
                } // for i
            } // if (rowCount > 0)
            // end transaction
            action.end ();
        } // try
        catch (DBError e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
        finally
        {
            try
            {
                DBConnector.releaseDBConnection (action);
            } // try
            catch (DBError e)
            {
                IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            } // catch
        } // finally
        // return the categories
        return queryCategories;
    } // getQueryCategories


    /**************************************************************************
     * Reads the object data from an dataElement. <BR/>
     * 
     * @param dataElement the dataElement to read the data from
     */
    public void readImportData (DataElement dataElement)
    {
        // get business object specific values
        super.readImportData (dataElement);

        // get the type specific values
        if (dataElement.exists ("SELECT"))
        {
            this.selectString = dataElement.getImportStringValue ("SELECT");
        } // if
        if (dataElement.exists ("FROM"))
        {
            this.fromString = dataElement.getImportStringValue ("FROM");
        } // if
        if (dataElement.exists ("WHERE"))
        {
            this.whereString = dataElement.getImportStringValue ("WHERE");
        } // if

        // QUERYTYPE
        if (dataElement.exists ("QUERYTYPE"))
        {
            this.queryType = QueryConstants.QT_NONE;

            // get String for QueryTypes
            String queryTypeString = dataElement
                .getImportStringValue ("QUERYTYPE");

            // tokenize queryTypes
            StringTokenizer queryTypeTokens = new StringTokenizer (
                queryTypeString, QueryConstants.CONST_DELIMITER);

            String queryTypeToken = "";

            // map queryTypeStrings to bit pattern (this.queryType)
            for (; queryTypeTokens.hasMoreElements ();)
            {
                queryTypeToken = ((String) queryTypeTokens.nextElement ())
                    .trim ();

                // set bit in bit pattern related to current query type string
                int j = 1;
                for (int i = 0; i < QueryConstants.CONST_QUERYTYPE_SELECTION.length; i++, j *= 2)
                {
                    if (queryTypeToken
                        .equals (QueryConstants.CONST_QUERYTYPE_SELECTION[i]))
                    {
                        this.queryType += j;
                    } // if
                } // for
            } // for
        } // if

        if (dataElement.exists ("GROUPBY"))
        {
            this.groupByString = dataElement.getImportStringValue ("GROUPBY");
        } // if
        if (dataElement.exists ("ORDERBY"))
        {
            this.orderByString = dataElement.getImportStringValue ("ORDERBY");
        } // if
        if (dataElement.exists ("COLUMNHEADERS"))
        {
            this.columnHeaders = dataElement
                .getImportStringValue ("COLUMNHEADERS");
        } // if
        if (dataElement.exists ("COLUMNQUERYATTRIBUTES"))
        {
            this.queryColumnAttributes = dataElement
                .getImportStringValue ("COLUMNQUERYATTRIBUTES");
        } // if
        if (dataElement.exists ("COLUMNTYPES"))
        {
            this.columnTypes = dataElement.getImportStringValue ("COLUMNTYPES");
        } // if
        if (dataElement.exists ("SEARCHFIELDS"))
        {
            this.searchFields = dataElement
                .getImportStringValue ("SEARCHFIELDS");
        } // if
        if (dataElement.exists ("SEARCHFIELDQUERYATTRIBUTES"))
        {
            this.queryFieldAttributes = dataElement
                .getImportStringValue ("SEARCHFIELDQUERYATTRIBUTES");
        } // if
        if (dataElement.exists ("SEARCHFIELDTYPES"))
        {
            this.searchFieldTypes = dataElement
                .getImportStringValue ("SEARCHFIELDTYPES");
        } // if
        if (dataElement.exists ("MAXRESULTS"))
        {
            this.resultCounter = dataElement.getImportIntValue ("MAXRESULTS");
        } // if
        if (dataElement.exists ("DEBUGGING"))
        {
            this.isEnableDebugging = dataElement
                .getImportBooleanValue ("DEBUGGING");
        } // if
        if (dataElement.exists ("CATEGORY"))
        {
            this.category = dataElement.getImportStringValue ("CATEGORY");
        } // if

        // update the query pool with this new query
        ((QueryPool) this.app.queryPool).updateQuery (this);
    } // readImportData


    /**************************************************************************
     * Writes the object data to an dataElement. <BR/>
     * 
     * @param dataElement the dataElement to write the data to
     */
    public void writeExportData (DataElement dataElement)
    {
        // set the business object specific values
        super.writeExportData (dataElement);

        // set the type specific values
        dataElement.setExportValue ("SELECT", this.selectString);
        dataElement.setExportValue ("FROM", this.fromString);
        dataElement.setExportValue ("WHERE", this.whereString);

        // QUERYTYPE
        String queryTypeString = "";

        // map bit pattern to Strings for queryType in XML-File
        int j = 1;
        for (int i = 0; i < QueryConstants.CONST_QUERYTYPE_SELECTION.length; i++, j *= 2)
        {
            if ((this.queryType & j) == j)
            {
                // check if delimiter should be added
                if (queryTypeString.length () > 0)
                {
                    queryTypeString += ",";
                } // if

                queryTypeString += QueryConstants.CONST_QUERYTYPE_SELECTION[i];
            } // if
        } // for
        dataElement.setExportValue ("CATEGORY", this.category);

        dataElement.setExportValue ("QUERYTYPE", queryTypeString);
        dataElement.setExportValue ("GROUPBY", this.groupByString);
        dataElement.setExportValue ("ORDERBY", this.orderByString);
        dataElement.setExportValue ("COLUMNHEADERS", this.columnHeaders);
        dataElement.setExportValue ("COLUMNQUERYATTRIBUTES",
            this.queryColumnAttributes);
        dataElement.setExportValue ("COLUMNTYPES", this.columnTypes);
        dataElement.setExportValue ("SEARCHFIELDS", this.searchFields);
        dataElement.setExportValue ("SEARCHFIELDQUERYATTRIBUTES",
            this.queryFieldAttributes);
        dataElement.setExportValue ("SEARCHFIELDTYPES", this.searchFieldTypes);
        dataElement.setExportValue ("MAXRESULTS", this.resultCounter);
        dataElement.setExportValue ("DEBUGGING", this.isEnableDebugging);
    } // writeExportData


    /**************************************************************************
     * Creates a clone of a QueryCreator - Object. <BR/>
     * 
     * @return A clone of this instance. <BR/>
     *         throws CloneNotSupportedException if the object's class does not
     *         support the <code>Cloneable</code> interface. Subclasses that
     *         override the <code>clone</code> method can also throw this
     *         exception to indicate that an instance cannot be cloned.
     * @throws OutOfMemoryError if there is not enough memory.
     * @see java.lang.Cloneable
     */
    public Object clone () throws OutOfMemoryError // ,
                                                   // CloneNotSupportedException
    {
        Object obj = null;

        try
        {
            // call corresponding method of super class:
            obj = super.clone ();

            // ensure constraints:
            ((QueryCreator_01) obj).performAttributeMapping ();
        } // try
        catch (CloneNotSupportedException e)
        {
            // nothing to do
        } // catch CloneNotSupportedException
        catch (OutOfMemoryError e)
        {
            // nothing to do
        } // catch OutOfMemoryError

        // return the object:
        return obj;
    } // clone


    /***************************************************************************
     * Initializes the multilang info for the query creator for all provided
     * locales. <BR/>
     * 
     * @param locales The locales to init the multilang info for
     * @param queryName The name of the query
     * @param env The current environment
     */
    public void initMultilangInfo (Collection<Locale_01> locales,
        String queryName, Environment env)
    {
        // Initialize the collections for column headers and search fields
        mlColumnHeaderNames = new ArrayList<Map<String, String>> ();
        mlColumnHeaderDescriptions = new ArrayList<Map<String, String>> ();
        mlSearchFieldNames = new ArrayList<Map<String, String>> ();
        mlSearchFieldDescriptions = new ArrayList<Map<String, String>> ();

        // Iterate over all column headers and get the texts for all locales
        StringTokenizer headerTokens = this
            .tokenizeConfigString (this.columnHeaders);
        for (; headerTokens != null && headerTokens.hasMoreElements ();)
        {
            String nextHeaderToken = headerTokens.nextToken ().trim ();

            // Get multilang info for all column headers
            Map<String, String> mlHeaderNames = new HashMap<String, String> ();
            Map<String, String> mlHeaderDescriptions = new HashMap<String, String> ();

            // Iterate over all locales and get the texts for this column:
            Iterator<Locale_01> it = locales.iterator ();
            while (it.hasNext ())
            {
                Locale_01 locale = it.next ();

                // Get the multilang name for this column header
                mlHeaderNames.put (locale.getLocaleKey (),
                    (getMultilangInfo (locale, nextHeaderToken,
                        MultilangConstants.LOOKUP_KEY_POSTFIX_NAME, env)));

                // Get the multilang description for this column header
                mlHeaderDescriptions
                    .put (locale.getLocaleKey (),
                        (getMultilangInfo (locale, nextHeaderToken,
                            MultilangConstants.LOOKUP_KEY_POSTFIX_DESCRIPTION,
                            env)));
            } // while

            // Store list of multilang names for this column header
            mlColumnHeaderNames.add (mlHeaderNames);
            mlColumnHeaderDescriptions.add (mlHeaderDescriptions);
        } // for

        // Iterate over all search fields and get the texts for all locales
        StringTokenizer fieldTokens = this
            .tokenizeConfigString (this.searchFields);
        for (; fieldTokens != null && fieldTokens.hasMoreElements ();)
        {
            String nextFieldToken = fieldTokens.nextToken ().trim ();

            // Get multilang info for all search fields
            Map<String, String> mlFieldNames = new HashMap<String, String> ();
            Map<String, String> mlFieldDescriptions = new HashMap<String, String> ();

            // Iterate over all locales and get the texts for this column:
            Iterator<Locale_01> it = locales.iterator ();
            while (it.hasNext ())
            {
                Locale_01 locale = it.next ();

                // Get the multilang name for this search field
                mlFieldNames.put (locale.getLocaleKey (),
                    (getMultilangInfo (locale, nextFieldToken,
                        MultilangConstants.LOOKUP_KEY_POSTFIX_NAME, env)));

                // Get the multilang description for this search field
                mlFieldDescriptions
                    .put (locale.getLocaleKey (),
                        (getMultilangInfo (locale, nextFieldToken,
                            MultilangConstants.LOOKUP_KEY_POSTFIX_DESCRIPTION,
                            env)));
            } // while

            // Store list of multilang names for this search fields
            mlSearchFieldNames.add (mlFieldNames);
            mlSearchFieldDescriptions.add (mlFieldDescriptions);
        } // for
    } // initMultilangInfo


    /***************************************************************************
     * Get the multilang text from the resource bundle, for a given custom mlKey
     * or with a generic lookup key, otherwise take the key as value. <BR/>
     * 
     * @param locale Locale for which the multilang text should be loaded
     * @param mlKey Token for which a text should be searched
     * @param kindOfField Defines the postfix for the generic lookup field
     * @param env The current environment
     */
    private String getMultilangInfo (Locale_01 locale, String mlKey,
        String kindOfField, Environment env)
    {
        // Define value for field:
        String mlValue = "";
        // Define lookup key:
        String lookupKey = null;
        
        // Check if the field is a system field or a referenced field
        if ((mlKey.charAt (0) == QueryConstants.MARK_FUNCCOL))
        {

            // Separate key to check if a custom ml key is defined
            StringTokenizer separatedKey = 
                new StringTokenizer(mlKey, QueryConstants.MARK_REFCOL);
            int countedKeys = separatedKey.countTokens ();  
            
            if (countedKeys == 1)
            {
                // we have a system column
                mlValue = mlKey;
            } // if
            else if (countedKeys == 2)
            {
                // we have a referenced column / NO fallback token
                String refResourceBundle = separatedKey.nextToken ();
                lookupKey = separatedKey.nextToken ().replace (" ", "_");

                // Retrieve the ml text with the defined lookup key
                MultilingualTextInfo mlValueInfo = MultilingualTextProvider
                    .getMultilingualTextInfo (
                        refResourceBundle.substring (1), lookupKey,
                        locale, env);

                // check if something has been found
                mlValue = mlValueInfo.isFound () ?
                // and use it
                mlValueInfo.getMLValue ()
                    :
                    // (3) fallback - use the key as value
                    mlKey;
            } // else if
            else if (countedKeys > 2)
            {
                // we have a referenced column AND fallback token
                // we handle only the first three tokens, more tokens will be ignored
                String refResourceBundle = separatedKey.nextToken ();
                lookupKey = separatedKey.nextToken ().replace (" ", "_");
                String fallback = separatedKey.nextToken ();

                // Retrieve the ml text with the defined lookup key
                MultilingualTextInfo mlValueInfo = MultilingualTextProvider
                    .getMultilingualTextInfo (
                        refResourceBundle.substring (1), lookupKey,
                        locale, env);

                // check if something has been found
                mlValue = mlValueInfo.isFound () ?
                // and use it
                mlValueInfo.getMLValue ()
                    :
                    // (3) fallback - use the key as value
                    fallback;
            } // else if

        } // if
        else
        {
           // (2) if no custom ml key is defined build a generic key
           lookupKey = MultilingualTextProvider.getQueryGenericLookupKey (
               this.name, mlKey, kindOfField);

            // Retrieve the ml text with the defined lookup key
            MultilingualTextInfo mlValueInfo = MultilingualTextProvider
                .getMultilingualTextInfo (
                    MultilangConstants.RESOURCE_BUNDLE_QUERIES_NAME, lookupKey,
                    locale, env);

            // check if something has been found
            mlValue = mlValueInfo.isFound () ?
            // and use it
            mlValueInfo.getMLValue ()
                :
                // (3) fallback - use the key as value
                mlKey;
        } // if

        return mlValue;
    } // getMultilangInfo

} // class QueryCreator_01
