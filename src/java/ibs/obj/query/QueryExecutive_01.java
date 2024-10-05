/*
 * Class: QueryExecutive_01.java
 */

// package:
package ibs.obj.query;

// imports:
//KR TODO: unsauber
import ibs.app.AppConstants;
import ibs.app.AppFunctions;
import ibs.app.AppMessages;
import ibs.app.CssConstants;
import ibs.bo.BOArguments;
import ibs.bo.BOConstants;
import ibs.bo.BOHelpers;
import ibs.bo.BOMessages;
import ibs.bo.BOPathConstants;
import ibs.bo.BOTokens;
import ibs.bo.Buttons;
import ibs.bo.Container;
import ibs.bo.Datatypes;
import ibs.bo.IncorrectOidException;
import ibs.bo.OID;
import ibs.bo.Operations;
import ibs.bo.SelectionList;
import ibs.bo.States;
import ibs.di.DIArguments;
import ibs.di.DIConstants;
import ibs.di.DITokens;
import ibs.di.DataElement;
import ibs.di.XMLViewer_01;
import ibs.io.Environment;
import ibs.io.HtmlConstants;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.io.LayoutConstants;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.ml.MultilangConstants;
import ibs.ml.MultilingualTextInfo;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.func.FunctionArguments;
import ibs.obj.menu.MenuData_01;
import ibs.obj.search.SearchArguments;
import ibs.obj.search.SearchEvents;
import ibs.service.reporting.ReportingException;
import ibs.service.user.User;
import ibs.tech.html.BlankElement;
import ibs.tech.html.BuildException;
import ibs.tech.html.DivElement;
import ibs.tech.html.FormElement;
import ibs.tech.html.GroupElement;
import ibs.tech.html.HTMLButtonElement;
import ibs.tech.html.IE302;
import ibs.tech.html.InputElement;
import ibs.tech.html.NewLineElement;
import ibs.tech.html.Page;
import ibs.tech.html.RowElement;
import ibs.tech.html.ScriptElement;
import ibs.tech.html.SpanElement;
import ibs.tech.html.StyleSheetElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TableElement;
import ibs.tech.html.TextElement;
import ibs.tech.http.HttpArguments;
import ibs.tech.http.HttpConstants;
import ibs.tech.sql.DBError;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLConstants;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.StoredProcedure;
import ibs.tech.xml.DOMHandler;
import ibs.tech.xml.DOMTreeHelpers;
import ibs.tech.xml.XMLWriter;
import ibs.tech.xml.XMLWriterException;
import ibs.util.DateTimeHelpers;
import ibs.util.NoAccessException;
import ibs.util.StringHelpers;
import ibs.util.file.FileHelpers;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/******************************************************************************
 * QueryExecutive_01 for dynamic reporting. <BR/>
 *
 * @version     $Id: QueryExecutive_01.java,v 1.107 2013/01/16 16:14:13 btatzmann Exp $
 *
 * @author      Andreas Jansa (AJ), 000918
 ******************************************************************************
 */
public class QueryExecutive_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: QueryExecutive_01.java,v 1.107 2013/01/16 16:14:13 btatzmann Exp $";


    /**
     * Field name: oid of query object. <BR/>
     */
    public static final String FIELD_QUERYOBJECTOID = "queryobjectoid";

    /**
     * Field name: name of query object. <BR/>
     */
    public static final String FIELD_QUERYOBJECTNAME = "queryobjectname";

    /**
     * Field name: show search form. <BR/>
     */
    public static final String FIELD_SHOWSEARCHFORM = "showsearchform";

    /**
     * Field name: show dom tree. <BR/>
     */
    public final String FIELD_SHOWDOMTREE = "showdomtree";

    /**
     * oid of related queryObject
     */
    protected OID queryObjectOid = null;

    /**
     * name of related queryObject
     */
    protected String queryObjectName = null;

    /**
     * pointer to related queryObject
     */
    protected QueryCreator_01 queryObject = null;

    /**
     * The query input parameters. <BR/>
     */
    private Vector<QueryParameter> p_inParams = new Vector<QueryParameter> ();

    /**
     * values for search separated with ';'
     */
    private String p_searchValues = null;

    /**
     * match types for searchValues
     */
    private String p_matchTypes = null;

    /**
     * Shall the search form be displayed? <BR/>
     */
    private boolean p_isShowSearchForm = false;

    /**
     * The configuration value if the search form shall be displayed. <BR/>
     * Note that the p_isShowSearchForm property has a multiple function
     * and should therefore not be manipulated.
     * Thus we store the value into the p_isShowSearchFormConfig
     * property in order to be able to add the correct value to the dom tree.
     */
    private boolean p_isShowSearchFormConfig = true;

    /**
     * Shall the dom tree be displayed? <BR/>
     * Default: <CODE>false</CODE>
     */
    protected boolean p_isShowDOMTree = false;

    /**
     * Shall we get search parameters out from the environment? <BR/>
     * Default: <CODE>false</CODE>
     */
    protected boolean p_isGetEnvParams = false;

    /**
     * Is the query currently executed? <BR/>
     * Default: <CODE>false</CODE>
     */
    protected boolean p_isQueryExecuted = false;

    /**
     * oid of root object, where query should be started
     */
    protected OID rootObjectOid = null;

    /**
     * if queryExecutive is used for extended Search and there is an
     * OBJECTPATH-selectionbox u have to set this oid to current containerOid
     * for local-search
     */
    protected OID currentObjectOid = null;

    /**
     * the containerId of the current object. <BR/>
     */
    protected OID currentContainerId = null;

    /**
     * stylesheet path for generic search. <BR/>
     * @deprecated  This constant seems to be never used.
     */
    private static String GENERIC_SEARCH_STYLESHEET =
        "general/genericsearch.xsl";

    /**
     * stylesheet path for generic result. <BR/>
     */
    private static String GENERIC_RESULT_STYLESHEET =
        "general/genericresult.xsl";

    /**
     * stylesheet path for generic download. <BR/>
     */
    private static String GENERIC_DOWNLOAD_STYLESHEET =
        "general/genericdownload.xsl";

    /**
     * Prototype methods for layout generation with xsl.
     */
    public static final int VIEWMODE_CONTENT = 5;

    /**
     * Vector with results of queryexecutive, used to create dom tree.
     */
    protected Vector<ResultRow> results = new Vector<ResultRow> ();


    /**
     * File postfix: download xslt stylesheet. <BR/>
     */
    public static final String FILEPF_DOWNLOAD = "_download.xsl";


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * This constructor creates a new instance of the class QueryExecutive_01.
     * <BR/>
     */
    public QueryExecutive_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // QueryExecutive_01


    /**************************************************************************
     * Creates a QueryExecutive_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public QueryExecutive_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
    } // QueryExecutive_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set names of procedures
        this.procCreate     = "p_QueryExecutive_01$create";
        this.procRetrieve   = "p_QueryExecutive_01$retrieve";
        this.procChange     = "p_QueryExecutive_01$change";

        // set elementClassName
        this.elementClassName = "ibs.obj.query.QueryExecutiveElement_01";

        // set maximum number of entries, that will be shown in list
        this.maxElements = BOConstants.MAX_CONTENT_ELEMENTS;

        // set number of parameters for procedure calls:
        this.specificRetrieveParameters = 6;
        this.specificChangeParameters = this.specificRetrieveParameters;
    } // initClassSpecifics


    /**************************************************************************
     * Initializes a QueryExecutive object. <BR/>
     * The compound object id is stored in the <A HREF="#oid">oid</A> property
     * of this object. <BR/>
     * The {@link #user user object} is also stored in a specific
     * property of this object to make sure that the user's context can be used
     * for getting his/her rights. <BR/>
     * {@link #env env} is initialized to the provided object. <BR/>
     * {@link #sess sess} is initialized to the provided object. <BR/>
     * {@link #app app} is initialized to the provided object. <BR/>
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     * @param   env     The actual environment object.
     * @param   sess    The actual session info object.
     * @param   app     The global application info object.
     */
    public void initObject (OID oid, User user, Environment env,
                            SessionInfo sess, ApplicationInfo app)
    {
        super.initObject (oid, user, env, sess, app);

        // initialize the instance's private properties:

        // set the instance's public/protected properties:

        // this class is used as BusinessObject (Report - QueryExecutive)
        // and as service (enhanced search) - if it is used as
        // service the name has to be set. In that case the object will not
        // be physical
        if (!this.isPhysical)
        {
            this.name = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SEARCHRESULT, env);
            // don't display the common script:
            this.p_isShowCommonScript = false;
        } // if

    } // initObject


    ///////////////////////////////////////////////////////////////////////////
    // object data methods  - interface to other objects
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * ensure that the input parameters are correctly initialized. <BR/>
     * If the input parameters are already initialized nothing is done. <BR/>
     * Otherwise we get the input parameter definitions out of the search
     * parameter definitions of the query creator. <BR/>
     * If there is no query creator set nothing is done.
     */
    private void initInputParameters ()
    {
        // check if the parameters vector has been initialized:
        if (this.p_inParams == null)
        {
            this.p_inParams = new Vector<QueryParameter> ();
        } // if

        // check if the parameters have been initialized:
        if (this.p_inParams.size () == 0 && this.queryObject != null)
        {
            // get the input parameters:
            this.p_inParams = this.queryObject.getInputParameters ();
        } // if
    } // initInputParameters


    /**************************************************************************
     * Set the current input parameters. <BR/>
     * The input parameters come out of the two comma-separated strings
     * searchValues and matchTypes. These strings are the same which are
     * stored within the database.
     *
     * @param   searchValues    The search values to be set.
     * @param   matchTypes      The match types to be set.
     */
    public void setInputParameters (String searchValues, String matchTypes)
    {
        int posSearchValue = 0;
        String searchValue = null;      // the actual search value
        String searchRangeValue = null; // range value of actual search field
        String matchType = null;        // match type of actual search field
        String fieldType = null;        // type of actual search field

        QueryParameter param = null;

        // check if we got search and match type values
        if ((searchValues != null && !searchValues.trim ().isEmpty ()) &&
            (matchTypes != null && !matchTypes.trim ().isEmpty ()))
        {
            String[] singleSearchValues = searchValues
                .split (QueryConstants.CONST_DELIMITER);
            String[] singleMatchTypes = matchTypes
                .split (QueryConstants.CONST_DELIMITER);

            // ensure that the parameters vector has been initialized:
            this.initInputParameters ();

            // walk through all search value fields and try to get the value
            for (Iterator<QueryParameter> iter =  this.p_inParams.iterator (); iter.hasNext ();)
            {
                // get the actual parameter:
                param = iter.next ();

                // get field type
                fieldType = param.getType ();
                // get search value and match type and
                // replace UTC comma value by comma:
                searchValue = singleSearchValues[posSearchValue];
                matchType = singleMatchTypes[posSearchValue];

                // handle the field type:
                if (fieldType.equals (QueryConstants.FIELDTYPE_INTEGERRANGE) ||
                    fieldType.equals (QueryConstants.FIELDTYPE_NUMBERRANGE) ||
                    fieldType.equals (QueryConstants.FIELDTYPE_MONEYRANGE) ||
                    fieldType.equals (QueryConstants.FIELDTYPE_DATETIMERANGE) ||
                    fieldType.equals (QueryConstants.FIELDTYPE_TIMERANGE) ||
                    fieldType.equals (QueryConstants.FIELDTYPE_DATERANGE))
                {
                    // check if we got a value:
                    if (searchValue != null &&
                        (searchValue.trim ().length () > 0) &&
                        !searchValue.equalsIgnoreCase (QueryConstants.CONST_NULL +
                                "-" + QueryConstants.CONST_NULL))
                    {
                        int pos = searchValue.indexOf ("-");

                        // get both the lower and the upper value out of the
                        // value string:
                        // note that range fields have stored their values
                        // in the following format: <value>-<rangeValue>
                        if (pos >= 0)
                        {
                            // get values
                            searchRangeValue = searchValue.substring (pos + 1);
                            searchValue = searchValue.substring (0, pos);

                            // differ between date and time values
                            if (fieldType.equals (QueryConstants.FIELDTYPE_DATERANGE))
                            {
                                // get the date value out of the
                                // value string:
                                // note that date/time field types have stored
                                // their values in the following format:
                                // <date>-<time>
                            	pos = searchRangeValue.indexOf (" ");
                                if (pos >= 0)
                                {
                                    // get date string from range value
                                    searchRangeValue = searchRangeValue.substring (0, pos);
                                } // if

                                pos = searchValue.indexOf (" ");
                                if (pos >= 0)
                                {
                                    // get date string from value
                                    searchValue = searchValue.substring (0, pos);
                                } // if
                            } // else
                            else if (fieldType.equals (QueryConstants.FIELDTYPE_TIMERANGE))
                            {
                                // get the date value out of the
                                // value string:
                                // note that date/time field types have stored
                                // their values in the following format:
                                // <date>-<time>
                            	pos = searchRangeValue.indexOf (" ");
                                if (pos >= 0)
                                {
                                    // get time string from range value
                                    searchRangeValue = searchRangeValue.substring (pos + 1);
                                } // if

                                pos = searchValue.indexOf (" ");
                                if (pos >= 0)
                                {
                                    // get time string from value
                                    searchValue = searchValue.substring (pos + 1);
                                } // if
                            } // else
                        } // if
                        else if (searchValue != null &&
                                (searchValue.trim ().length () > 0))
                        {
                        	// only the search value is set
                        	// differ between date and time values
                        	if (fieldType.equals (QueryConstants.FIELDTYPE_DATERANGE))
                        	{
                        		// get the date value out of the
                        		// value string:
                        		// note that date/time field types have stored
                        		// their values in the following format:
                        		// <date>-<time>
                        		pos = searchValue.indexOf (" ");
                        		if (pos >= 0)
                        		{
                        			// get date string from value
                        			searchValue = searchValue.substring (0, pos);
                        		} // if
                        	}
                        	else if (fieldType.equals (QueryConstants.FIELDTYPE_TIMERANGE))
                        	{
                        		// get the date value out of the
                        		// value string:
                        		// note that date/time field types have stored
                        		// their values in the following format:
                        		// <date>-<time>
                        		pos = searchValue.indexOf (" ");
                        		if (pos >= 0)
                        		{
                        			// get time string from value
                        			searchValue = searchValue.substring (pos + 1);
                        		} // if
                        	}
                        }
                        else
                        {
                            // no search value:
                            searchValue = null;

                            // no search range value:
                            searchRangeValue = null;
                        } // else
                    } // if (value != null && (!value.length () == 0))
                } // if NUMBERRANGE || MONEYRANGE
                else if (fieldType.startsWith (QueryConstants.FIELDTYPE_QUERYSELECTION) ||
                         fieldType.startsWith (QueryConstants.FIELDTYPE_QUERYSELECTIONNUM) ||
                         fieldType.startsWith (QueryConstants.FIELDTYPE_QUERYSELECTIONOID))
                {
                    // is it a singe selection box?
                    if (fieldType.indexOf (QueryConstants.QUERYSELECTIONBOX_PARAM_MULTIPLE) != -1)
                    {
                        // replace UC_COMMA with ","
                        searchValue = StringHelpers.replace (searchValue,
                            AppConstants.UC_COMMA,
                            QueryConstants.CONST_DELIMITER);
                        // in case of a multipleselectionbox we generate a string
                        // as parameter value that can in included into the querystring
                        // value1,value2,value3 (for QUERYSELECTIONNUM, QUERYSELECTIONOID)
                        // 'value1','value2','value3' (for QUERYSELECTION)
                        // in that case we need to read a multivalue
                        //String [] options = searchValue.split(QueryConstants.MULTISELECTION_CONST_DELIMITER);
                        String [] options = searchValue.split (QueryConstants.CONST_DELIMITER);
                        if (options != null && options.length > 0)
                        {
                            StringBuffer multiValStrBuf = new StringBuffer ();
                            String comma = "";

                            // loop though the values and generate the paramter value
                            for (int x = 0; x < options.length; x++)
                            {
                                // check for emtpy option that has " " as value
                                if (!options[x].equals (" "))
                                {
                                    multiValStrBuf.append (comma)
                                        .append (options[x]);
                                    comma = QueryConstants.CONST_DELIMITER;
                                } // if (! options[i].equals(" "))
                            } // for (int x = 0; x < options.length; x++)
                            // any value set now?
                            if (multiValStrBuf.length () > 0)
                            {
                                // set the search value
                                searchValue = multiValStrBuf.toString ();
                            } // if (multiValStrBuf.length() > 0)
                            else    // no value set
                            {
                                // we need to set " " as value to be compatible
                                // with the original QUERYSELECTIONBOX implementation
                                // because some queries use
                                // #SYSVAR.QUERY_searchfield# = ' '
                                // in their expressions.
                                // Note that this is a dirty solution!
                                searchValue = " ";
                            } // else no value set
                        } // if (values.length > 0)
                    } // if multiple selection
                } // else if QUERYSELECTION || QUERYSELECTIONNUM || QUERYSELECTIONOID
                else // any other field type
                {
                    // nothing to do
                } // else normal field type
    /*
                // check if we have a range field:
                if (fieldType.equals (QueryConstants.FIELDTYPE_INTEGERRANGE) ||
                    fieldType.equals (QueryConstants.FIELDTYPE_NUMBERRANGE) ||
                    fieldType.equals (QueryConstants.FIELDTYPE_MONEYRANGE) ||
                    fieldType.equals (QueryConstants.FIELDTYPE_DATETIMERANGE) ||
                    fieldType.equals (QueryConstants.FIELDTYPE_DATERANGE) ||
                    fieldType.equals (QueryConstants.FIELDTYPE_TIMERANGE))
                {
                    // check if we got a value:
                    if (searchValue != null &&
                        (searchValue.trim ().length () > 0) &&
                        !searchValue.equals ("null"))
                    {
                        int pos = searchValue.indexOf ("-");

                        // get both the lower and the upper value out of the
                        // value string:
                        // note that range fields have stored their values
                        // in the following format: <value>-<rangeValue>
                        if (pos >= 0)
                        {
                            searchRangeValue = searchValue.substring (pos + 1);
                            searchValue = searchValue.substring (0, pos);
                        } // if
                        else
                        {
                            // no search range value:
                            searchRangeValue = "";
                        } // else
                    } // if (value != null && (!value.length () == 0))
                } // if (fieldType.equals (QueryConstants.FIELDTYPE_DATETIMERANGE) || ...
    */
                // if searchValue for this field is not defined set to null
                if (searchValue != null &&
                        searchValue.equalsIgnoreCase (QueryConstants.CONST_NULL) &&
                        (searchValue.equals (QueryConstants.CONST_UNDEF) ||
                        searchValue.equals (QueryConstants.CONST_UNDEF2)))
                {
                    searchValue = null;
                } // if
                // if searchRangeValue for this field is not defined set to null
                if (searchRangeValue != null &&
                        searchRangeValue.equalsIgnoreCase (QueryConstants.CONST_NULL) &&
                        (searchRangeValue.equals (QueryConstants.CONST_UNDEF) ||
                        searchRangeValue.equals (QueryConstants.CONST_UNDEF2)))
                {
                    searchRangeValue = null;
                } // if
                // if match type for this field is not defined set to null
                if (matchType != null &&
                        matchType.equalsIgnoreCase (QueryConstants.CONST_NULL) &&
                        (matchType.equals (QueryConstants.CONST_UNDEF) ||
                        matchType.equals (QueryConstants.CONST_UNDEF2)))
                {
                    matchType = null;
                } // if

                // set inparameter for query:
                param.setValue (searchValue);
                param.setRangeValue (searchRangeValue);
                param.setMatchType (matchType);
                // count search value field
                posSearchValue++;
            } // for iter
        } // if
    } // setInputParameters


    /**************************************************************************
     * Set the current query creator. <BR/>
     *
     * @param   qc    The QueryCreator_01 to be set.
     */
    public void setQueryCreator (QueryCreator_01 qc)
    {
        this.queryObject = qc;
    } // setQueryCreator


    /**************************************************************************
     * Set the oid of the current query creator. <BR/>
     *
     * @param   aOid    The oid to be set.
     */
    public void setQueryCreatorOid (OID aOid)
    {
        this.queryObjectOid = aOid;
    } // setQueryCreatorOid


    /**************************************************************************
     * Set the name of the current query creator. <BR/>
     *
     * @param   name    The name of the query creator.
     */
    public void setQueryCreatorName (String name)
    {
        this.queryObjectName = name;
    } // setQueryCreatorOid


    /**************************************************************************
     * Set the oid of the current object. <BR/>
     *
     * @param   aOid    The oid to be set.
     */
    public void setCurrentObjectOid (OID aOid)
    {
        this.currentObjectOid = aOid;
    } // setCurrentObjectOid


    /**************************************************************************
     * Set the oid of the current container. <BR/>
     *
     * @param   aOid    The oid to be set.
     */
    public void setCurrentContainerId (OID aOid)
    {
        this.currentContainerId = aOid;
    } // setCurrentContainerId


    /**************************************************************************
     * Set the oid of the object, at which the search shall start. <BR/>
     *
     * @param   aOid    The oid to be set.
     */
    public void setRootObjectOid (OID aOid)
    {
        this.rootObjectOid = aOid;
    } // setRootObjectOid


    /**************************************************************************
     * Get the query object. <BR/>
     *
     * @return  The query object as QueryCreator_01  or
     *          <CODE>null</CODE> if there is no query creator set.
     */
    public QueryCreator_01 getQueryCreator ()
    {
        // check if query object is set
        if (this.queryObject != null)
        {
            return this.queryObject;
        } // if
        // no query creator set:
        return null;
    } // getQueryCreator


    /**************************************************************************
     * Get the string representation of the query creator's oid. <BR/>
     *
     * @return  The string representation of the oid or
     *          <CODE>null</CODE> if there is no oid for the query creator set.
     */
    public String getQueryCreatorOidString ()
    {
        if (this.queryObjectOid != null) // oid for query creator set?
        {
            return this.queryObjectOid.toString ();
        } // if oid for query creator set

        // no oid set:
        return null;
    } // getQueryCreatorOidString


    /**************************************************************************
     * Get the query creator's oid. <BR/>
     *
     * @return  The oid or
     *          <CODE>null</CODE> if there is no oid for the query creator set.
     */
    public OID getQueryCreatorOid ()
    {
        if (this.queryObjectOid != null) // oid for query creator set?
        {
            return this.queryObjectOid;
        } // if oid for query creator set

        // no oid set:
        return null;
    } // getQueryCreatorOid


    /**************************************************************************
     * Get the name of the query creator. <BR/>
     *
     * @return  The query creator's name.
     */
    public String getQueryCreatorName ()
    {
        return this.queryObjectName;
    } // getQueryCreatorOidString


    /**************************************************************************
     * Get the string representation of the current object's oid. <BR/>
     *
     * @return  The string representation of the oid or
     *          <CODE>null</CODE> if there is no oid for the current object set.
     */
    public String getCurrentObjectOidString ()
    {
        if (this.currentObjectOid != null) // oid for current object set?
        {
            return this.currentObjectOid.toString ();
        } // if oid for current object set

        // no oid set:
        return null;
    } // getCurrentObjectOidString


    /**************************************************************************
     * Get the string representation of the current container's oid. <BR/>
     *
     * @return  The string representation of the oid or
     *          <CODE>null</CODE> if there is no oid for the current container
     *          set.
     */
    public String getCurrentContainerIdString ()
    {
        if (this.currentContainerId != null)
        {
            return this.currentContainerId.toString ();
        } // if

        return null;
    } // getCurrentContainerIdString


    /**************************************************************************
     * Get the string representation of the search root object's oid. <BR/>
     *
     * @return  The string representation of the oid or
     *          <CODE>null</CODE> if there is no oid for the search root object
     *          set.
     */
    public String getRootObjectOidString ()
    {
        if (this.rootObjectOid != null)
        {
            return this.rootObjectOid.toString ();
        } // if

        return null;
    } // getCurrentObjectOidString


    /**************************************************************************
     * Get the search values as string. <BR/>
     *
     * @return  The string representation of the search values or
     *          <CODE>null</CODE> if there is no search values set.
     *
     */
    public String getSearchValues ()
    {
        if (this.p_searchValues != null)
        {
            return this.p_searchValues;
        } // if

        return null;
    } // getSearchValues


    /**************************************************************************
     * Get the match type values as string. <BR/>
     *
     * @return  The string representation of the match type values or
     *          <CODE>null</CODE> if there is no match type values set.
     *
     */
    public String getMatchTypes ()
    {
        if (this.p_matchTypes != null)
        {
            return this.p_matchTypes;
        } // if
        return null;
    } // getMatchTypes


    /**************************************************************************
     * Set the search values as string. <BR/>
     *
     * @param   searchValues    The search values to be set.
     */
    public void setSearchValues (String searchValues)
    {
        // check if we got a value
        if (searchValues != null)
        {
            this.p_searchValues = searchValues;
        } // if
    } // setSearchValues


    /**************************************************************************
     * Set the match type values as string. <BR/>
     *
     * @param   matchTypes  The match types to be set.
     */
    public void setMatchTypes (String matchTypes)
    {
        // check if we got a value
        if (matchTypes != null)
        {
            this.p_matchTypes = matchTypes;
        } // if
    } // setMatchTypes


    /**************************************************************************
     * Get the input parameters as Vector&lt;QueryParameter>. <BR/>
     *
     * @return  The Vector&lt;QueryParameter> of the input parameters or
     *          <CODE>null</CODE> if there are no params set.
     *
     */
    public Vector<QueryParameter> getInParams ()
    {
        if (this.p_inParams != null)
        {
            return this.p_inParams;
        } // if
        return null;
    } // getInParams


    /**************************************************************************
     * Check if the search form shall be displayed. <BR/>
     *
     * @return  <CODE>true</CODE> if the search form shall be displayed,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean isShowSearchForm ()
    {
        // get the property value and return the result:
        return this.p_isShowSearchForm;
    } // isShowSearchForm


    /**************************************************************************
     * Set if the search form shall be displayed. <BR/>
     *
     * @param   isShowSearchForm    Shall the search form be displayed?
     */
    public void setShowSearchForm (boolean isShowSearchForm)
    {
        // set the property value:
        this.p_isShowSearchForm = isShowSearchForm;
    } // setShowSearchForm


    /**************************************************************************
     * Check if the DOM tree shall be displayed. <BR/>
     *
     * @return  <CODE>true</CODE> if the DOM tree shall be displayed,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean isShowDOMTree ()
    {
        // get the property value and return the result:
        return this.p_isShowDOMTree;
    } // isShowDOMTree


    /**************************************************************************
     * Set if the DOM tree shall be displayed. <BR/>
     *
     * @param   isShowDOMTree   Shall the DOM tree be displayed?
     */
    public void setShowDOMTree (boolean isShowDOMTree)
    {
        // set the property value:
        this.p_isShowDOMTree = isShowDOMTree;
    } // setShowDOMTree


    /**************************************************************************
     * Shall we get the search parameters from environment or database? <BR/>
     *
     * @return  <CODE>true</CODE> get input parameters from environment parameters,
     *          <CODE>false</CODE> get input paramaeters from database.
     */
    public boolean isGetEnvParams ()
    {
        // get the property value and return the result:
        return this.p_isGetEnvParams;
    } // isGetEnvParams


    /**************************************************************************
     * Set where to get search parameters from. <BR/>
     *
     * @param   isGetEnvParams   Get input parameters from database or environment?
     */
    public void setGetEnvParams (boolean isGetEnvParams)
    {
        // set the property value:
        this.p_isGetEnvParams = isGetEnvParams;
    } // setGetEnvParams


    /**************************************************************************
     * Check if the query is currently executed. <BR/>
     *
     * @return  <CODE>true</CODE> get input parameters from environment parameters,
     *          <CODE>false</CODE> get input paramaeters from database.
     */
    public boolean getIsQueryExecuted ()
    {
        // get the property value and return the result:
        return this.p_isQueryExecuted;
    } // getIsQueryExecuted


    /**************************************************************************
     * Set if the query is currently executed. <BR/>
     *
     * @param   isQueryExecuted The value to be set.
     */
    public void setIsQueryExecuted (boolean isQueryExecuted)
    {
        // set the property value:
        this.p_isQueryExecuted = isQueryExecuted;
        // set where to get search parameters for value field fieldref and
        // when query is currently executed
        this.p_isGetEnvParams = isQueryExecuted;
    } // setIsQueryExecuted


    ///////////////////////////////////////////////////////////////////////////
    // database functions
    ///////////////////////////////////////////////////////////////////////////

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
        // oid of queryobject
        BOHelpers.addInParameter (sp, this.queryObjectOid);


/*
        // searchValues
        // get count of different searchFields
        int searchFieldCount = 0;
        if (this.queryObject != null)
            searchFieldCount = this.queryObject.getSearchFieldCount ();

        // concatinate all searchvalues and matchtpyes with separator for DB
        this.searchValues = null;
        this.matchTypes = null;
        for (int j=0; j < searchFieldCount; j++)
        {

            // searchvalues

            // add separator to searchValuesString if it is not the first searchValue
            if (this.searchValues != null)
                this.searchValues += ";";
            else //searchvalues is null
                this.searchValues = "";  // initialize searchValue string

            // add searchvalue to searchValuesString
            str = this.queryObject.getSearchValueForQueryAttr (j);
            // if there is no searchvalue for this field - set to undef
            if ( str != null && !str.length () == 0)
                this.searchValues += str;
            else
                this.searchValues += QueryConstants.CONST_UNDEF;

            // match types

            // add separator to searchValuesString if it is not the first searchValue
            if (this.matchTypes != null)
                this.matchTypes += ";";
            else //searchvalues is null
                this.matchTypes = "";  // initialize searchValue string

            // add match type to matchTypeString
            str = this.queryObject.getMatchTypeForQueryAttr (j);
            // if there is no match type for this field - set to undef
            if ( str != null && !str.length () == 0)
                this.matchTypes += str;
            else
                this.matchTypes += QueryConstants.CONST_UNDEF;
        }
*/
        // searchvalues
        sp.addInParameter (ParameterConstants.TYPE_STRING,
            this.p_searchValues);

        // match types
        sp.addInParameter (ParameterConstants.TYPE_STRING,
            this.p_matchTypes);

        // oid of rootObject
        BOHelpers.addInParameter (sp, this.rootObjectOid);

        // shall the search form be displayed:
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN,
            this.p_isShowSearchForm);

        // showDOMTree
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN,
            this.p_isShowDOMTree);
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
        // queryObjectOid
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);

        // searchValues
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);

        // matchTypes
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);

        // rootObjectOid
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);

        // shall the search form be displayed:
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_BOOLEAN);

        // shall the DOM tree be displayed:
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_BOOLEAN);

        return i;                       // return the current index
    } // setSpecificRetrieveParameters


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
        int i = lastIndex;              // initialize params index
        OID lOid = null;

        // queryObjectOid
        if (!(lOid = SQLHelpers.getSpOidParam (params[++i])).isEmpty ())
        {
            this.queryObjectOid = lOid;
        } // if

        // searchValues
        this.p_searchValues = params[++i].getValueString ();

        // matchTypes
        this.p_matchTypes = params[++i].getValueString ();

        // rootObject
        this.rootObjectOid = SQLHelpers.getSpOidParam (params[++i]);

        // shall the search form be displayed:
        this.p_isShowSearchForm = params[++i].getValueBoolean ();
        // BB 20070808: the p_isShowSearchForm has a multiple function
        // and should therefore not be manipulated
        // thus we store the value into the p_isShowSearchFormSetting
        // property in order to be able to add the value to the dom tree
        this.p_isShowSearchFormConfig = this.p_isShowSearchForm;

        // shall the DOM tree be displayed:
        this.p_isShowDOMTree = params[++i].getValueBoolean ();
    } // getSpecificRetrieveParameters


    /**************************************************************************
     * Set input paramters for query executive. <BR/>
     *
     * @param   qe      The query executive for which to set the parameters.
     */
    private void setQueryExecutiveInputParameters (QueryExecutive qe)
    {
        // loop through all input parameters and set each of them within the
        // query executive:
        for (Iterator<QueryParameter> iter = this.p_inParams.iterator (); iter.hasNext ();)
        {
            qe.addInParameter (iter.next ());
        } // for iter
    } // setQueryExecutiveInputParameters


    /**************************************************************************
     * Instantiate and initialize a QueryFactory. <BR/>
     *
     * @return  an initialized QueryFactory Object
     */
    protected QueryFactory getQueryFactory ()
    {
        QueryFactory qf = new QueryFactory (); // instantiate a queryfactory

        // initialize the QueryFactory
        qf.initObject (OID.getEmptyOid (), this.user, this.env, this.sess,
            this.app);

        // return QueryFactory
        return qf;
    } // getQueryFactory


    /**************************************************************************
     * Prepare assigned queryObject for execution. <BR/>
     *
     * @return  <CODE>true</CODE> if queryObject is ok,
     *          <CODE>false</CODE> if queryObject was not prepared
     *                  (if queryObject with given oid does not exist in pool
     *                  or if neither the name nor the oid for the querycreator
     *                  is set in query executive)
     */
    public boolean prepareQueryCreator ()
    {
        // check if the query object is already set:
        if (this.queryObject != null &&
            (this.queryObjectOid == null ||
             this.queryObject.oid.equals (this.queryObjectOid)))
                                        // query object already set?
        {
            // set oid of rootObject
            this.queryObject.rootObjectOid = this.rootObjectOid;

            // set oid of currentObject when searchbutton was clicked
            this.queryObject.currentObjectOid = this.currentObjectOid;
            this.queryObject.currentContainerId = this.currentContainerId;
        } // if query object already set
        else                            // no query object set
        {
            // check if the queryObject in the session should be used
            // if object is not stored on database (used for enhanced search)
            // and no querycreatoroid is set to get query from pool
            // and no querycreator is set, set the last query creator
            // which was stored in session (if there is one)
            // - this is needed for the back - button after search and for the
            // sort in the resultlist
            if (this.queryObjectOid == null && this.queryObjectName == null &&
                this.oid != null && this.oid.isTemp () &&
                this.sess.queryObject != null)
            {
                this.queryObject = (QueryCreator_01) this.sess.queryObject;
            } // if
            else if (this.queryObjectName != null || this.queryObjectOid != null)
            {
                // when the queryexecutive is not called for sort or via history
                // the querycreator had to be fetched from the querypool

                try
                {
                    // create a queryfactory to connect to querypool
                    QueryFactory qf = this.getQueryFactory ();

                    // check if name or oid should be used to get query out of pool
                    if (this.queryObjectName != null)
                    {
                        this.queryObject = qf.get (this.queryObjectName);
                        this.queryObjectOid = this.queryObject.oid;
                    } // if
                    else if (this.queryObjectOid != null &&
                             !this.queryObjectOid.isEmpty ())
                        // if there is an valid oid  (u get an OID_NOOBJECT when
                        // the report is created)
                    {
                        // get clone of querycreator with the given oid
                        this.queryObject = qf.get (this.queryObjectOid);
                        this.queryObjectName = this.queryObject.name;
                    } // else if
                } // try
                catch (QueryNotFoundException e)
                {
                    // show exception if query was not found
                    String exc = null;

                    if (this.queryObjectName != null)
                    {
                        exc =  
                            MultilingualTextProvider.getMessage (QueryExceptions.EXC_BUNDLE,
                                QueryExceptions.ML_EXC_QUERYDOESNOTEXIST_NAME,
                                new String[] {"" + this.queryObjectName}, env);
                    } // if
                    else
                    {
                        exc =  
                            MultilingualTextProvider.getMessage (QueryExceptions.EXC_BUNDLE,
                                QueryExceptions.ML_EXC_QUERYDOESNOTEXIST_OID,
                                new String[] {"" + this.queryObjectOid}, env);
                    } // else

                    IOHelpers.showMessage (
                        "QueryExecutive.execute: ERROR " + exc,
                        this.app, this.sess, this.env);
                    return false;
                } // catch

                // set oid of rootObject
                this.queryObject.rootObjectOid = this.rootObjectOid;

                // set oid of currentObject when searchbutton was clicked
                this.queryObject.currentObjectOid = this.currentObjectOid;
                this.queryObject.currentContainerId = this.currentContainerId;

                // if query executive is not stored on database (used for enhanced search)
                // set the current query creator to session for later use
                // (for sort in result of enhanced search)
                if (this.oid != null && this.oid.isTemp ())
                {
                    this.sess.queryObject = this.queryObject;
                } // if
            } // else get query from querypool
            else if (this.queryObject == null)
                // if there is no name and no oid, leave method
            {
                return false;
            } // else

            // because the query creator could have been changed we must
            // reinitialize the search field parameters:
            this.p_inParams = null;
            // initialize the input parameters for the query object:
            this.initInputParameters ();
            // check if query is currently executed
            // the parameters shall be retrieved from the environment
            if (this.isGetEnvParams ())
            {
                // get search field parameters from the environment parameters:
                QueryExecutive_01.getEnvSearchfieldParameters (this.p_inParams,
                        this.env, null, this.getIsQueryExecuted ());
            } // if
            // parameters shall be retrieved from the database:
            else
            {
                // get saved search field parameters:
                QueryExecutive_01.getSavedSearchfieldParameters (this.p_inParams,
                        this.p_searchValues, this.p_matchTypes);
            } // else
        } // else

        return true;
    } // prepareQueryCreator


    /**************************************************************************
     * Retrieve the type specific data that is not got from the stored
     * procedure. <BR/>
     * This method must be overwritten by all subclasses that have to get type
     * specific data that cannot be got from the retrieve data stored procedure.
     *
     * @param   action      The action object associated with the connection.
     *
     * @exception    DBError
     *               This exception is always thrown, if there happens an error
     *               during accessing data.
     */
    protected void performRetrieveSpecificData (SQLAction action) throws DBError
    {
        // check if queryexecutive is physical
        if (!this.oid.isTemp ())
                // if queryexecutive is physical, the querycreator
                // has to be prepared
        {
            this.prepareQueryCreator ();
            this.setInputParameters (this.p_searchValues, this.p_matchTypes);
        } // if if queryexecutive id physical
        else
        {
            // don't display the common script:
            this.p_isShowCommonScript = false;
        } // else
    } // performRetrieveSpecificData


    /**************************************************************************
     * Get the content for this dynamic Query out of the database. <BR/>
     * <BR/>
     * First this method tries to load the objects from the database. During
     * this operation a rights check is done, too. If this is all right the
     * objects are returned otherwise an exception is raised. <BR/>
     *
     * @param   operation         Operation to be performed with the objects.
     * @param   orderBy           Property, by which the result shall be
     *                            sorted. If this parameter is null the
     *                            default order is by name.
     * @param   orderHow          Kind of ordering: BOConstants.ORDER_ASC or BOConstants.ORDER_DESC
     *                            null => BOConstants.ORDER_ASC
     * @param   selectedElements  object ids that are marked for paste
     *
     * @exception   NoAccessException
     *              The user does not have access to these objects to perform
     *              the required operation.
     */
    protected void performRetrieveContentData (int operation, int orderBy,
                                               String orderHow,
                                               Vector<OID> selectedElements)
        throws NoAccessException
    {
        // locals
        QueryExecutiveElement_01 obj = null;        // element for one resultrow
        QueryExecutive qe = new QueryExecutive ();  // for query execution
        boolean orderByStringWasChanged = false; // indicates if the orderBy
                                        // String of queryObject was changed
                                        // before execution
        int jcounter = 0;               // counter for the container elements

        qe.initObject (this.oid, this.user, this.env, this.sess, this.app);

        // empty the elements vector:
        this.elements.removeAllElements ();

        // EXECUTE QUERY

        // if object is not physical, it was not retrieved, so
        // the preparation of querycreator is not necessary
        // - the data for retrieveContent is already prepared in
        // getParameters.
        if (!this.oid.isTemp () || this.queryObject == null)
        {
            // try to get QueryCreator with the oid this.queryObjectOid
            if (!this.prepareQueryCreator ())
            {
                return;
            } // if
        } // if

        // check if query is called via reordering
        // in that case do not set the input parameter again because they did
        // not change
        if (this.env.getIntParam (BOArguments.ARG_REORDER) ==
                IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
        {
            // set input parameters for query in queryexecutive:
            this.setQueryExecutiveInputParameters (qe);

            // if order by - clause is not set already in queryCreator
            if ((this.queryObject.getOrderByString () == null ||
                 this.queryObject.getOrderByString ().trim ().length () == 0) &&
                this.orderings != null &&
                this.orderBy >= 0)
            {
                // set order by string in querycreator
                this.queryObject.setOrderByString (
                    this.orderings[this.orderBy] + " " + this.orderHow);

                // indicate that the order has been changed:
                orderByStringWasChanged = true;
            } // if order by - clause
        } // if
        else                            // reordering activated
        {
            // in case we habe any orderings and an orderBy value is set
            if (this.orderings != null && this.orderBy >= 0)
            {
                // set order by string in querycreator
                this.queryObject.setOrderByString (
                    this.orderings[this.orderBy] + " " + this.orderHow);
                // indicate that order by has been changed
                orderByStringWasChanged = true;
            } // if (this.orderings != null && this.orderBy >= 0)
        } // reordering activated


        // if query with oid exist and could be executed
        if (qe.execute (this.queryObject))
        {
            this.results = qe.results;

            while (!qe.getEOF ())
            {
                // create an instance of the element's class:
                obj = new QueryExecutiveElement_01 ();
                // add element to list of elements:
                this.elements.addElement (obj);

                // fill containerelement with data
                this.getContainerElementData (obj, qe);

                qe.next ();
                jcounter += 1;
            } // while - walk through all resultrows

            // set maxElements to the chosen resultCounter of the querycreator
            // and areMaxElementsExceeded to true for showing the message box for the user
            this.maxElements = jcounter;
            this.areMaxElementsExceeded = !qe.notAll;
        } // if query with oid exist in querypool

        // reset orderbystring if it was changed
        if (orderByStringWasChanged)
        {
            this.queryObject.setOrderByString (null);
        } // if
    } // performRetrieveContentData


    /**************************************************************************
     * Get the element type specific data out of the actual tuple of the query
     * result. <BR/>
     *
     * @param   obj     Object representing the list element.
     * @param   qe      The query executive for executing the query.
     */
    protected void getContainerElementData (QueryExecutiveElement_01 obj,
                                            QueryExecutive qe)
    {
        // locals
        String resultValue = null;  // value of column in resultset
        String columnName = null;   // name of column  (columnheader)
        String mlColumnName = null;   // multilang name of column  (columnheader)
        String mlColumnDescription = null;   // multilang description of column  (columnheader)        
        String columnType = null;   // Type of column - to use right convertion
        int columnCount = qe.getColCount ();    // columnCount of current Row

        // get columnvalues
        for (int i = 0; i < columnCount; i++)
        {
            // get column data
            columnName = qe.getColName (i);
            mlColumnName = qe.getMlColName (i);
            mlColumnDescription = qe.getMlColDescription (i);
            resultValue = qe.getColValue (i);
            columnType = qe.getColType (i);

            // FUNCTIONAL COLUMNS
            if (columnType.equals (QueryConstants.COLUMNTYPE_FUNCCOL))
            {
                // FUNCCOL_OBJECTID
                if (columnName.equals (QueryConstants.FUNCCOL_OBJECTID))
                {
                    try
                    {
                        obj.oid = new OID (resultValue);
                    } // try
                    catch (IncorrectOidException e)
                    {
                        IOHelpers.showMessage (
                            "QueryExecutive.getContainerElementData" +
                            " IncorrectOidException for oid = " + resultValue +
                            " ErrorMessage = " + e.toString (),
                            this.app, this.sess, this.env);
                    } // catch
                } // if
                // FUNCCOL_TYPEIMAGE
                else if (columnName.equals (QueryConstants.FUNCCOL_TYPEIMAGE))
                {
                    // set name for objecticon
                    obj.icon = resultValue;
                    // objecticon should be displayed
                    obj.addObjectIcon = true;
                } // else if
                // FUNCCOL_ISNEW
                else if (columnName.equals (QueryConstants.FUNCCOL_ISNEW))
                {
                    // set isNew flag (true if user has not already read this object)
                    obj.isNew = resultValue.equals ("1");
                    // isNewIcon should be displayed
                    obj.addIsNewIcon = true;
                } // else if
                // FUNCCOL_ISLINK
                else if (columnName.equals (QueryConstants.FUNCCOL_ISLINK))
                {
                    // set isLink flag (true if object is link to other object)
                    obj.isLink = resultValue.equals ("1");
                    // isLink Icon should be displayed
                    obj.addIsLinkIcon = true;
                } // else if
            } // if FUNCCOL
/* BB: not sure where this is needed!
            else if (columnType.equals (QueryConstants.COLUMNTYPE_BOOLEAN))
            {
                // set value for this column in containerelement
                if (resultValue.equalsIgnoreCase ("true"))
                    obj.colData.addElement (AppMessages.MSG_BOOLTRUE);
                else
                    obj.colData.addElement ("" + AppMessages.MSG_BOOLFALSE);
                // set column datatype to standard
                obj.colDataType.addElement (columnType);
                 // set column name to standard
                obj.colName.addElement (columnName);
            } // if COLUMNTYPE_BOOLEAN
*/
            else
            {
                // set value for this column in containerelement
                obj.colData.addElement (resultValue);

                // set column datatype to standard
                obj.colDataType.addElement (columnType);

                 // set column name to standard
                obj.colName.addElement (columnName);
                
                // set multilang name 
                obj.colMlName.addElement (mlColumnName);

                // set multilang description 
                obj.colMlDescription.addElement (mlColumnDescription);

            } // else not a FUNCCOL
        } // for - walk through all columns

        // set layoutpath in containerelem if layoutpath is set
        // (would not be set when exporting a queryexecutive
        if (this.sess.activeLayout != null)
        {
            obj.layoutpath = this.sess.activeLayout.path;
        } // if
    } // getContainerElementData


    /**************************************************************************
     * Add system part to dom tree. <BR/>
     *
     * @param   viewMode    The view mode setting.
     *
     * @return  An array containing 2 elements:
     *          <LI>The document ({@link org.w3c.dom.Document})</LI>
     *          <LI>The root node ({@link org.w3c.dom.Element})</LI><BR />
     *          <CODE>null</CODE> if there occurred an error.
     */
    protected Node[] createBasicDomTree (int viewMode)
    {
        Document doc = null;            // the document
        Element rootElem = null;        // the root element
        Node[] retVal = null;           // return value

        try
        {
            doc = XMLWriter.createDocument ();
        } // try
        catch (XMLWriterException e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            return retVal;
        } // catch

        rootElem = doc.createElement (QueryConstants.XML_QUERY);

        rootElem.setAttribute ("NAME", this.queryObject.name);
        rootElem.setAttribute ("LAYOUT", this.getUserInfo ().userProfile.layoutName);

        // add the show search form setting in order to be able to deactivate the
        // change search button
        rootElem.setAttribute ("SHOWSEARCHFORM", ("" + this.p_isShowSearchFormConfig).toUpperCase ());

        // check if we got an query object
        if (this.queryObject != null)
        {
            // <QUERYCREATOR>
            rootElem.appendChild (doc.importNode (DOMTreeHelpers.createDomTree (
                                this.queryObject,
                                XMLViewer_01.VIEWMODE_EDIT, true, false, false,
                                false, false, false, DIConstants.ELEM_QUERYCREATOR),
                                true));
        } // if
        // <USER>
        try
        {
            rootElem.appendChild (doc.importNode (this.getUser ().getDomTree (),
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

        Element system = doc.createElement (DIConstants.ELEM_SYSTEM);

        //<OID>
        Element id = doc.createElement ("OID");
        id.appendChild (doc.createTextNode (this.oid.toString ()));
        system.appendChild (id);

        //<STATE>
        Element actState = doc.createElement (DIConstants.ELEM_STATE);
        actState.appendChild (doc.createTextNode ("" + this.state));
        system.appendChild (actState);

        //<NAME>
        Element actName = doc.createElement (DIConstants.ELEM_NAME);
        actName.appendChild (doc.createTextNode (this.name));
        system.appendChild (actName);

        //<DESCRIPTION>
        Element actDescription = doc.createElement (DIConstants.ELEM_DESCRIPTION);
        actDescription.appendChild (doc.createTextNode (this.description));
        system.appendChild (actDescription);

        // add system section to queryNode
        rootElem.appendChild (system);

        // add root element to document node:
        doc.appendChild (rootElem);

        // set return value:
        retVal = new Node[] {doc, rootElem};

        // return the result:
        return retVal;
    } // createBasicDomTree


    /**************************************************************************
     * Add form for the search query to dom tree. <BR/>
     *
     * @param   viewMode    The view mode setting.
     *
     * @return  The resulting dom tree.
     */
    protected Document createSearchFormDomTree (int viewMode)
    {
        Document doc = null;            // the document
        Element rootElem = null;        // the root element
        Node[] domTreeInfo = null;      // info of basic dom tree
        Element valuesNode = null;

        // create system part:
        domTreeInfo = this.createBasicDomTree (viewMode);

        // check if the basic dom tree was created:
        if (domTreeInfo != null)
        {
            // extract the relevant information:
            doc = (Document) domTreeInfo[0];
            rootElem = (Element) domTreeInfo[1];

            // oid of current object when search was started
            rootElem.setAttribute ("CURRENTOBJECTOID", "" +
                ((this.getCurrentObjectOidString () == null) ? OID.EMPTYOID :
                    this.getCurrentObjectOidString ()));
            // oid of object which is the root object of search
            if (this.containerId != null)
            {
                rootElem.setAttribute (QueryConstants.XMLA_CONTAINEROID,
                    this.containerId.toString ());
            } // if
            else
            {
                rootElem.setAttribute (QueryConstants.XMLA_CONTAINEROID, OID.EMPTYOID);
            } // else
            // remember if the common shall be displayed:
            rootElem.setAttribute ("ISSHOWCOMMONSCRIPT",
                Boolean.toString (this.p_isShowCommonScript));

            // check if query creator could be prepared
            if (!this.prepareQueryCreator ())
            {
                // if something went wrong  (maybe assigned querycreator
                // was deleted ...)
                return doc;
            } // if

            // TODO: CT remove this
            /* add oid of query creator:
            rootElem.setAttribute ("QUERYCREATOROID",
                (this.queryObject == null) ? OID.EMPTYOID :
                    this.queryObject.oid.toString ());
            // add name of query creator:
            rootElem.setAttribute ("QUERYCREATORNAME",
                (this.queryObject == null) ? "" : this.queryObject.name);
            */

            //<VALUES>
            valuesNode = doc.createElement (DIConstants.ELEM_VALUES);
            rootElem.appendChild (valuesNode);
            // include values stored in the inparams vector:
            this.createDomTreeValues (doc, valuesNode, viewMode);

            // serialize domtree:
            // BB TODO: why serializing the DOM tree?
            // This does not seem to have any reasonable effect
            // and slows down performance
//            DOMHandler serializer = new DOMHandler (this.env, this.sess, this.app);
//            serializer.serializeDOM (doc);

            // debug domtree:
/*
            this.env.write ("<DIV ALIGN=\"LEFT\">" + IE302.TAG_PRE +
                serializer.domToString (doc, QueryConstants.XML_QUERY) +
                IE302.TAG_PREEND + "</DIV>");
*/
        } // if

        return doc;
    } // createSearchFormDomTree


    /**************************************************************************
     * Create the values for the dom tree and add them directly to the tree.
     * <BR/>
     * The parameter values should contain the already created &lt;VALUES> node.
     * This is where the values are directly added.
     *
     * @param   doc         The XML document which is used to create new nodes.
     * @param   valuesNode  The &lt;VALUES> node of the dom tree.
     * @param   viewMode    The view mode setting.
     */
    public void createDomTreeValues (Document doc, Node valuesNode, int viewMode)
    {
        // check if there are any search parameters:
        if (this.p_inParams != null)
        {
            // walk through all search value fields and create one inputfield
            // for each loop through all possible input parameters and add a
            // condition for each of them, which was set:
            for (Iterator<QueryParameter> iter = this.p_inParams.iterator ();
                iter.hasNext ();)
            {
                QueryParameter param = iter.next ();

                // create the value node for the dom tree and append it:
                this.createDomTreeValueNode (doc, valuesNode, param, viewMode);
            } // for iter
        } // if
    } // createDomTreeValues


    /**************************************************************************
     * Add a value node for a query parameter. <BR/>
     *
     * @param   doc         The XML document which is used to create new nodes.
     * @param   valuesNode  The &lt;VALUES> node of the dom tree.
     * @param   param       The parameter from which to create the node.
     * @param   viewMode    The view mode setting.
     */
    protected void createDomTreeValueNode (Document doc, Node valuesNode,
                                           QueryParameter param, int viewMode)
    {
        String searchValue = null;
        String fieldName = null;
        String mlFieldName = null;        
        String mlFieldDescription = null;        
        String fieldType = null;
        String fieldTypeParam = null;
        String argName = null;
        Element valueNode = null;
        int selectLength = 0;
        String[] selectIds = null;
        String[] selectValues = null;
        String[] selectGroupingIds = null;
        boolean selectIsSorted = false;
        int pos = 0;

        // get data for current field:
        fieldName = param.getName ();
        fieldType = param.getType ();
        argName = QueryArguments.ARG_SEARCHFIELD + param.pos;
        // get multi lang information for current field:
        mlFieldName = param.getMlName ();
        mlFieldDescription = param.getMlDescription ();
        
        // handle parameters in field type:
        pos = fieldType.indexOf ("(");
        if ((pos = fieldType.indexOf ("(")) > 0 && fieldType.endsWith (")"))
                                // field type has parameters?
        {
            // get the parameters from the fieldType:
            fieldTypeParam = fieldType.substring (
                pos + 1, fieldType.length () - 1).trim ();
            // get the raw field type:
            fieldType = fieldType.substring (0, pos).trim ();
        } // if field type has parameters


        // get searchvalue for current search field:
        if (fieldType != null &&
            !fieldType.equals (QueryConstants.FIELDTYPE_OBJECTPATH))
        {
            searchValue = param.getValue ();

            if (searchValue == null)
            {
                searchValue = "";
            } // if
        } // if
        else
        {
            searchValue = "";
        } // else

        // show search fields - check field type:
        if (fieldType != null)
        {
            // show selection for root object of search
            // if FIELDTYPE_OBJECTPATH is used, show selection for root object of query
            if (fieldType.equals (QueryConstants.FIELDTYPE_OBJECTPATH))
            {
                argName = QueryArguments.ARG_ROOTOBJECTSELECTION;

                // if rootObjectOid is set to predefined value, use this oid
                // as root oid
                if (this.rootObjectOid != null &&
                    !this.rootObjectOid.isEmpty ())
                {
                    // show selected Querytemplate (QueryCreator)
                    selectIds = new String[] {
                        MultilingualTextProvider.getText (QueryTokens.TOK_BUNDLE,
                            QueryTokens.ML_SEARCHLOCAL, env)};
                    selectValues = new String[] {this.rootObjectOid.toString ()};
                } // if rootObject is set to predefined value
                else
                    // else rootObject is not set to predefined value,
                    // show selectionbox
                {
                    // count of fix selections in objectpath - selectionbox.
                    int fixSelections = 0;
                    int j = 0;
                    MenuData_01 help = null;

                    // if search in current container is possible fixSelections
                    // are 2 (for global and current-container-search)
                    // otherwise 1 (only for global search)
                    if (this.currentObjectOid != null &&
                        !this.currentObjectOid.isEmpty () &&
                        !this.currentObjectOid.isTemp ()) // container has to be physical
                    {
                        fixSelections = 2;
                    } // if
                    else
                    {
                        fixSelections = 1;
                    } // else

                    // get count of menutabs add 2
                    // (for entries *global and *current container)
                    selectLength = this.sess.menus.size () + fixSelections;

                    selectIds = new String [selectLength];
                    selectValues = new String [selectLength];

                    selectIds [j] = 
                        MultilingualTextProvider.getText (QueryTokens.TOK_BUNDLE,
                            QueryTokens.ML_SEARCHGLOBAL, env);
                    selectValues [j++] = OID.EMPTYOID;

                    // if search in current container is possible
                    if (fixSelections == 2) // container has to be physical
                    {
                        // add token for local-search to selectionbox
                        selectIds [j] = 
                            MultilingualTextProvider.getText (QueryTokens.TOK_BUNDLE,
                                QueryTokens.ML_SEARCHLOCAL, env);
                        selectValues [j++] = "" + this.currentObjectOid;
                    } // if

                    // fill names and oids of menutabs in selectionlist:
                    for (Iterator<MenuData_01> iter = this.sess.menus.iterator ();
                         iter.hasNext ();)
                    {
                        help = iter.next ();
                        selectIds [j] = help.name;
                        selectValues [j++] = "" + help.oid;
                    } // for iter
                } // else rootObject is not set to predefined value,
            } // if FIELDTYPE_OBJECTPATH is used - show selection for root object
/* KR 20060213 not necessary
            // FIELDTYPE_MONEY
            else if (fieldType.equals (QueryConstants.FIELDTYPE_MONEY))
            {
                // nothing to do
            } // if money
            // FIELDTYPE_<CONDITION>_MONEY
            else if (
                (fieldType.equals (QueryConstants.FIELDTYPE_LESS_MONEY) ||
                fieldType.equals (QueryConstants.FIELDTYPE_LESS_EQUAL_MONEY) ||
                fieldType.equals (QueryConstants.FIELDTYPE_GREATER_MONEY) ||
                fieldType.equals (QueryConstants.FIELDTYPE_GREATER_EQUAL_MONEY)))
            {
                // nothing to do
            } // else if condition money
            // FIELDTYPE_MONEYRANGE
            else if (fieldType.equals (QueryConstants.FIELDTYPE_MONEYRANGE))
            {
                // nothing to do
            } // if MONEYRANGE

            // FIELDTYPE_DATE
            else if (fieldType.equals (QueryConstants.FIELDTYPE_DATE))
            {
                // nothing to do
            } // if date
            // FIELDTYPE_<CONDITION>_DATE
            else if (
                (fieldType.equals (QueryConstants.FIELDTYPE_LESS_DATE) ||
                fieldType.equals (QueryConstants.FIELDTYPE_LESS_EQUAL_DATE) ||
                fieldType.equals (QueryConstants.FIELDTYPE_GREATER_DATE) ||
                fieldType.equals (QueryConstants.FIELDTYPE_GREATER_EQUAL_DATE)))
            {
                // nothing to do
            } // else if condition date
            // FIELDTYPE_TIME
            else if (fieldType.equals (QueryConstants.FIELDTYPE_TIME))
            {
                // nothing to do
            } // if time
            // FIELDTYPE_<CONDITION>_TIME
            else if (
                (fieldType.equals (QueryConstants.FIELDTYPE_LESS_TIME) ||
                fieldType.equals (QueryConstants.FIELDTYPE_LESS_EQUAL_TIME) ||
                fieldType.equals (QueryConstants.FIELDTYPE_GREATER_TIME) ||
                fieldType.equals (QueryConstants.FIELDTYPE_GREATER_EQUAL_TIME)))
            {
                // nothing to do
            } // else if condition time
            // FIELDTYPE_DATETIME
            else if (fieldType.equals (QueryConstants.FIELDTYPE_DATETIME))
            {
                // nothing to do
            } // if datetime
            // FIELDTYPE_DATERANGE
            else if (fieldType.equals (QueryConstants.FIELDTYPE_DATERANGE))
            {
                // nothing to do
            } // if daterange
            // FIELDTYPE_TIMERANGE
            else if (fieldType.equals (QueryConstants.FIELDTYPE_TIMERANGE))
            {
                // nothing to do
            } // if timerange
            // FIELDTYPE_DATETIMERANGE
            else if (fieldType.equals (QueryConstants.FIELDTYPE_DATETIMERANGE))
            {
                // nothing to do
            } // if datetimerange

            // FIELDTYPE_INTEGER
            else if (fieldType.equals (QueryConstants.FIELDTYPE_INTEGER))
            {
                // nothing to do
            } // if INTEGER
            // FIELDTYPE_<CONDITION>_INTEGER
            else if (
                (fieldType.equals (QueryConstants.FIELDTYPE_LESS_INTEGER) ||
                fieldType.equals (QueryConstants.FIELDTYPE_LESS_EQUAL_INTEGER) ||
                fieldType.equals (QueryConstants.FIELDTYPE_GREATER_INTEGER) ||
                fieldType.equals (QueryConstants.FIELDTYPE_GREATER_EQUAL_INTEGER)))
            {
                // nothing to do
            } // else if condition INTEGER
            // FIELDTYPE_INTEGERRANGE
            else if (fieldType.equals (QueryConstants.FIELDTYPE_INTEGERRANGE))
            {
                // nothing to do
            } // if INTEGERRANGE

            // FIELDTYPE_NUMBER
            else if (fieldType.equals (QueryConstants.FIELDTYPE_NUMBER))
            {
                // nothing to do
            } // if number
            // FIELDTYPE_<CONDITION>_NUMBER
            else if (
                (fieldType.equals (QueryConstants.FIELDTYPE_LESS_NUMBER) ||
                fieldType.equals (QueryConstants.FIELDTYPE_LESS_EQUAL_NUMBER) ||
                fieldType.equals (QueryConstants.FIELDTYPE_GREATER_NUMBER) ||
                fieldType.equals (QueryConstants.FIELDTYPE_GREATER_EQUAL_NUMBER)))
            {
                // nothing to do
            } // else if condition number
            // FIELDTYPE_NUMBERRANGE
            else if (fieldType.equals (QueryConstants.FIELDTYPE_NUMBERRANGE))
            {
                // nothing to do
            } // if NUMBERRANGE

            // FIELDTYPE_STRING
            else if (
                fieldType.equals (QueryConstants.FIELDTYPE_STRING) ||
                fieldType.equals (QueryConstants.FIELDTYPE_LONGTEXT))
            {
                // nothing to do
            } // else if string
            // FIELDTYPE_BOOLEAN
            else if (fieldType.equals (QueryConstants.FIELDTYPE_BOOLEAN))
            {
                // nothing to do
            } // else if boolean
*/
            // FIELDTYPE_QUERYSELECTION
            // FIELDTYPE_QUERYSELECTIONOID
            // FIELDTYPE_QUERYSELECTIONNUM
            else if (
                    fieldType.startsWith (QueryConstants.FIELDTYPE_QUERYSELECTION) ||
                    fieldType.startsWith (QueryConstants.FIELDTYPE_QUERYSELECTIONOID) ||
                    fieldType.startsWith (QueryConstants.FIELDTYPE_QUERYSELECTIONNUM))
            {
                // name of the query that is filling the SELECTIONBOX:
                String queryName = fieldTypeParam;

                if (queryName != null && queryName.length () > 0)
                                        // there is a valid query name?
                {
                    String idType = null;
                    pos = queryName.indexOf (";");
                    // check if queryName contains the MULTIPLE parameter
                    if (pos > 0)
                    {
                        // substring
                        queryName = queryName.substring (0, pos);
                    } // if

                    if (fieldType
                        .startsWith (QueryConstants.FIELDTYPE_QUERYSELECTIONOID))
                    {
                        idType = DIConstants.IDTYPE_OBJECTID;
                    } // if
                    else if (fieldType
                        .startsWith (QueryConstants.FIELDTYPE_QUERYSELECTIONNUM))
                    {
                        idType = DIConstants.IDTYPE_NUMBER;
                    } // else if
                    else if (fieldType
                        .startsWith (QueryConstants.FIELDTYPE_QUERYSELECTION))
                    {
                        idType = DIConstants.IDTYPE_STRING;
                    } // else if

                    // get the query values:
                    SelectionList queryResult =
                        this.getQueryData (queryName, idType, true);

                    // check if the query returned valid data:
                    if (queryResult != null)
                    {
                        // set the values:
                        selectIds = queryResult.ids;
                        selectValues = queryResult.values;
                        selectGroupingIds = queryResult.groupingIds;
                        selectIsSorted = true;
                    } // if
                } // if there is a valid query name
            } // else if query selection
            // FIELDTYPE_VALUEDOMAIN
            else if (fieldType.startsWith (QueryConstants.FIELDTYPE_VALUEDOMAIN))
            {
                // name of the context that defines the container
                // of the VALUEDOMAINELEMENTS, which will filled to
                // the SELECTIONBOX
                String context = null;

                // field type modifier string
                String modifier = null;

                // get the context name from the fieldType
                // and check if it is valid
                if (fieldType.endsWith (")") &&
                        (modifier = fieldType
                            .substring (fieldType.indexOf ("(") + 1,
                                fieldType.length () - 1)).length () > 0 &&
                                (context = this.getContextFromFieldTypeModifier (modifier)) != null)
                {
                    // id type
                    String idType = null;

                    // check field type
                    if (fieldType
                        .startsWith (QueryConstants.FIELDTYPE_VALUEDOMAINOID))
                    {
                        idType = DIConstants.IDTYPE_OBJECTID;
                    } // if
                    else if (fieldType
                        .startsWith (QueryConstants.FIELDTYPE_VALUEDOMAINNUM))
                    {
                        idType = DIConstants.IDTYPE_NUMBER;
                    } // else if
                    else if (fieldType
                        .startsWith (QueryConstants.FIELDTYPE_VALUEDOMAIN))
                    {
                        idType = DIConstants.IDTYPE_STRING;
                    } // else if

                    // create the SQL String
                    StringBuffer queryStr = new StringBuffer ()
                        .append (" SELECT *")
                        .append (" FROM v_getValueDomain")
                        .append (" WHERE context = '")
                        .append (context).append ("'")
                        .append (" ORDER BY orderCrit ASC ");

                    // get the query values:
                    SelectionList queryResult =
                        this.getQueryData (queryStr, idType, true);

                    // check if the query returned valid data:
                    if (queryResult != null)
                    {
                        // set the values:
                        selectIds = queryResult.ids;
                        selectValues = queryResult.values;
                        selectIsSorted = true;
                    } // if
                } // if
            } // else if VALUEDOMAIN selection
/* KR 20060213 not known field types use the standard mechanism
            // NOT_VALID
            else
            {
                // show error message for invalid field type
                IOHelpers.showMessage (fieldName + " (" + fieldType + "): " +
                    QueryConstants.EXC_WRONGFIELDTYPE,
                    this.app, this.sess, this.env);
            } // else not valid
*/
        } // if fieldType != null

        // create the <VALUE> element and set the attributes:
        valueNode = doc.createElement (DIConstants.ELEM_VALUE);
        // field name:
        valueNode.setAttribute (DIConstants.ATTR_FIELD, fieldName);
        // multilang field name:
        valueNode.setAttribute (DIConstants.ATTR_MLNAME, mlFieldName);
        // multilang field description:
        valueNode.setAttribute (DIConstants.ATTR_MLDESCRIPTION, mlFieldDescription);
        // field type:
        valueNode.setAttribute (DIConstants.ATTR_TYPE, fieldType);
        // set field type parameters:
        if (fieldTypeParam != null && fieldTypeParam.length () > 0)
        {
            valueNode.setAttribute (DIConstants.ATTR_TYPE + "PARAM",
                fieldTypeParam);
        } // if
        // set the input field name:
        valueNode.setAttribute ("INPUT", argName);

        // create selection nodes:
        this.createDomTreeSelectionData (doc, valueNode, selectIds,
            selectValues, selectGroupingIds, searchValue, selectIsSorted);

        // add value node to values node:
        valuesNode.appendChild (valueNode);
    } // createSearchFormDomTree

    /**
     * Returns the value domain context from the given field type modifier.
     *
     * @param modifier        modifier string
     * @return                value domain context
     */
    private String getContextFromFieldTypeModifier (String modifier)
    {
        StringTokenizer tok = new StringTokenizer (modifier, QueryConstants.FIELDTYPE_MODIFIER_PARAM_DELIMITER);

        while (tok.hasMoreTokens ())
        {
            String token = tok.nextToken ().trim ();

            // != multiple modifier
            if (!tok.equals (QueryConstants.FIELDTYPE_MODIFIER_MULTIPLE))
            {
                return token;
            } // if != multiple modifier

        } // while hasMoreTokens

        return null;
    } // getContextFromFieldTypeModifier


    /**************************************************************************
     * Add a value node for a query parameter. <BR/>
     *
     * @param   doc             The XML document which is used to create
     *                          new nodes.
     * @param   parentNode      The parent node in the dom tree where
     *                          to add the selection data.
     * @param   ids             Ids for the selection.
     * @param   values          Values for the selection.
     * @param   groupingIds        Grouping ids for the selection.
     * @param   preselectedId   Value which shall be preselected.
     * @param   isSorted        Is the list already sorted?
     */
    protected void createDomTreeSelectionData (Document doc,
                                               Node parentNode,
                                               String[] ids,
                                               String[] values,
                                               String[] groupingIds,
                                               String preselectedId,
                                               boolean isSorted)
    {
        // create selection option nodes:
        if (ids != null && ids.length > 0)
        {
            Element optionNode = null;      // current option node
            String id = null;               // current id
            String value = null;            // current value
            String groupingId = null;       // current groupingId
            int [] sorts = null;            // sorted array (indexes of names)
            int preselected = -1;           // index of preselected element

            // ensure that all ids are set:
            this.ensureValidIds (ids, values);

            // search for preselected value:
            preselected = this.findValue (ids, preselectedId, preselected);

            // get the ordering of the arrays by values:
            sorts = this.sortList (values, isSorted);

            // create select elements:
            for (int i = 0; i < ids.length; i++)
            {
                // get id and value:
                id = StringHelpers
                    .replace (ids[sorts[i]], AppConstants.UC_COMMA, QueryConstants.CONST_DELIMITER);
                value = StringHelpers.replace (values[sorts[i]],
                    AppConstants.UC_COMMA, QueryConstants.CONST_DELIMITER);
                groupingId = StringHelpers.replace (groupingIds[sorts[i]],
                        AppConstants.UC_COMMA, QueryConstants.CONST_DELIMITER);

                // create option node:
                optionNode = doc.createElement ("OPTION");
                optionNode.setAttribute (DIConstants.ATTR_ID, id);
                
                // set the group id attribute only if it is not empty
                if (groupingId != null && !groupingId.isEmpty ())
                {
                    optionNode.setAttribute (DIConstants.ATTR_GROUPING_ID, groupingId);
                } // if
                
                if (sorts[i] == preselected)
                {
                    optionNode.setAttribute (DIConstants.ATTR_SELECTED,
                        DIConstants.BOOL_TRUE);
                } // if
                // add the value:
                optionNode.appendChild (doc.createTextNode (value));
                // add the option node to the parent:
                parentNode.appendChild (optionNode);
            } // for i
        } // if
    } // createDomTreeSelectionData


    /**************************************************************************
     * Add Results of this systemquery to dom tree.
     *
     * the domtree-part looks like this:
     * <PRE>
     *      &lt;QUERY&gt;
     *          &lt;VALUE FIELD="xxx" TYPE="QUERY" QUERYNAME="xxx"&gt;
     *              &lt;RESULTROW&gt;
     *                  &lt;RESULTELEMENT NAME="xnamex" TYPE="xtypey"&gt;
     *                    xvaluex&lt;/RESULTELEMENT&gt;
     *              &lt;/RESULTROW&gt;
     *              &lt;RESULTROW&gt;
     *                  &lt;RESULTELEMENT NAME="xnamex" TYPE="xtypey"&gt;
     *                    xvaluex&lt;/RESULTELEMENT&gt;
     *              &lt;/RESULTROW&gt;
     *          &lt;/VALUE&gt;
     *      &lt;/QUERY&gt;
     * </PRE>
     *
     * @param   viewMode    The view mode setting.
     *
     * @return  The resulting dom tree.
     */
    protected Document createResultDomTree (int viewMode)
    {
        Document doc = null;            // the document
        Element rootElem = null;        // the root element
        Node[] domTreeInfo = null;      // info of basic dom tree
        Node rowNode = null;
        Element colNode = null;
        ResultRow result = new ResultRow (); // a resultrow
        String actType = "";
        String value = "";

        // create system part:
        domTreeInfo = this.createBasicDomTree (viewMode);

        // check if the basic dom tree was created:
        if (domTreeInfo != null)
        {
            // extract the relevant information:
            doc = (Document) domTreeInfo[0];
            rootElem = (Element) domTreeInfo[1];

            rootElem.setAttribute ("ORDERBY", Integer.toString (this.orderBy));
            rootElem.setAttribute ("ORDERHOW", this.orderHow);

            // add RESULTROWs:
// KR TODO: Check if this is correct: loop through this.elements, but then read this.results
            for (int i = 0;
                 this.elements != null && i < this.elements.size ();
                 i++)
            {
                result = this.results.elementAt (i);
                // create row node
                rowNode = doc.createElement ("RESULTROW");

                for (int j = 0; j < result.getElementCount (); j++)
                {
                    // get the type:
                    actType = result.getType (j);
                    value = result.getValue (j);

                    // instanciate nodes for resultelements
                    colNode = doc.createElement ("RESULTELEMENT");
                    colNode.setAttribute (DIConstants.ATTR_NAME, result
                        .getName (j));
                    colNode.setAttribute (DIConstants.ATTR_TYPE, actType);
                    colNode.setAttribute (DIConstants.ATTR_DBFIELD, result
                        .getAttribute (j));
                    colNode.setAttribute (DIConstants.ATTR_MULTIPLE, result
                        .getMultipleAttribute (j) ? DIConstants.ATTRVAL_YES :
                        DIConstants.ATTRVAL_NO);
                    // add multilang information
                    colNode.setAttribute (DIConstants.ATTR_MLNAME, result
                        .getMlName (j));
                    colNode.setAttribute (DIConstants.ATTR_MLDESCRIPTION, result
                        .getMlDescription (j));

                    // check if we have a boolean type
                    if (actType.equals (QueryConstants.COLUMNTYPE_BOOLEAN))
                    {
                        // replace the true/false values with the appropriate tokens
                        // note that this can also be done in the stylesheet
                        if (value.equalsIgnoreCase ("true"))
                        {
                            colNode.appendChild (doc.createTextNode ( 
                                MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                                    AppMessages.ML_MSG_BOOLTRUE, env)));
                        } // if
                        else
                        {
                            colNode.appendChild (doc.createTextNode ( 
                                MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                                    AppMessages.ML_MSG_BOOLFALSE, env)));
                        } // else
                    } // if (type.equals (QueryConstants.COLUMNTYPE_BOOLEAN))
                    else // not a boolean type. set value for this column in containerelement
                    {
                        // first convert characters that have been escaped
                        value = SQLHelpers.dbToAscii (value);
                        // and add the value
                        if (value != null)
                        {
                            colNode.appendChild (doc.createTextNode (value));
                        } // if (value != null)
                        else    // empty value
                        {
                            // do not append any text child
                        } // else empty value
                    } // else not a boolean type. set value for this column in containerelement

                    // add columnnode to rownode in domtree
                    rowNode.appendChild (colNode);
                } // for j

                // add rownode to queryNode
                rootElem.appendChild (rowNode);
            } // for i

            // serialize domtree:
            // BB TODO: why serializing the DOM tree?
            // This does not seem to have any reasonable effect
            // and slows down performance
//            DOMHandler serializer = new DOMHandler (this.env, this.sess, this.app);
//            serializer.serializeDOM (doc);

            // debug domtree:
/*
             this.env.write ("<DIV ALIGN=\"LEFT\">" + IE302.TAG_PRE +
                serializer.domToString (doc, QueryConstants.XML_QUERY) +
                IE302.TAG_PREEND + "</DIV>");
*/
        } // if

        return doc;
    } // createDomTree


    /**************************************************************************
     * Show the content of the Container, i.e. its elements. <BR/>
     * The objects are gotten from the database (with rights checked) and
     * represented to the user in the required form. <BR/>
     *
     * @param   representationForm  Kind of representation.
     */
    public void showContent (int representationForm)
    {
        // check if we have to show the search form:
        if (this.p_isShowSearchForm)    // show search form?
        {
            // display the search form:
            this.performShowSearchForm ();
        } // if show search form
        else                            // standard mechanism
        {
            // check if a queryObject has been set
            if (this.queryObject == null)
            {
                // if not try to set the queryobjekt from the session
                // because in that case it was a LIST call to reorder the result
                // and the LIST call does not make sure the queryObject is set
                //
                // BB20060812 Bugfix:
                // reordering a queryresult always led to the
                // error message: Mehrdeutiger Spaltenname 'name'. (SQLState=01000);
                // Reason:
                // the querycreator object is not set when the user clicks an
                // reorder link in the queryresult.
                // Solution:
                // the queryCreator_01 object is stored within the session
                // in order to be reused and need to be set here.
                this.queryObject = (QueryCreator_01) this.sess.queryObject;
            } // if (this.queryObject == null)

            // call common method:
            super.showContent (representationForm);
        } // else standard mechanism
    } // showContent


    /**************************************************************************
     * Show the search form for the query executive. <BR/>
     */
    private void performShowSearchForm ()
    {
        // create html output just to display the tabs and buttons of the
        // query executive:
        Page page = new Page (false);   // the output page
        String searchUrl = null;        // the url to redirect to

        // build url to show search form:
        searchUrl = this.getBaseUrlGet () +
            // the actual oid:
            HttpArguments.createArg (BOArguments.ARG_OID,
                this.oid.toString ()) +
            // the function:
            HttpArguments.createArg (BOArguments.ARG_FUNCTION,
                              AppFunctions.FCT_OBJECTSEARCH) +
            // event to show search frameset:
            HttpArguments.createArg (FunctionArguments.ARG_EVENT,
                              SearchEvents.EVT_CLICKOKSEARCHSELECTIONFORM) +
            // oid of selected querycreator (type to be searched)
            HttpArguments.createArg (SearchArguments.ARG_QUERYCREATOROID,
                              (this.queryObjectOid == null) ?
                                 OID.EMPTYOID :
                                 this.queryObjectOid.toString ()) +
            // oid of current object when search was started
            HttpArguments.createArg (SearchArguments.ARG_CURRENTOBJECTOID,
                              this.oid.toString ()) +
            // oid of object which is the root object of search
            HttpArguments.createArg (SearchArguments.ARG_ROOTOBJECTOID,
                              this.containerId.toString ());
/*
            // oid of queryexecutive
            HttpArguments.createArg (BOArguments.ARG_OID,
                              ((this.queryExec == null) ?
                                 OID.EMPTYOID :
                                 this.queryExec.oid.toString ()));
*/

        // add redirection to page body:
        page.body.onLoad = "document.location.href = '" + searchUrl + "'";

        if (this.p_isShowCommonScript)
        {
            // create the script for displaying tabs and buttons:
            page.body.addElement (this.getCommonScript (false));
        } // if

        // build the page and show it to the user:
        try
        {
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage ("QueryExecutive_01.showSearchForm", e,
                this.app, this.sess, this.env, true);
        } // catch


/* KR code for directly displaying the search frameset and search form:
        // create functionobject for searchfunction
        FunctionSearch func = new FunctionSearch ();

        // initialize function with current values (is used for lokal search)
        func.initFunction (AppFunctions.FCT_OBJECTSEARCH, this.getUser (),
                           this.env, this.sess, this.app);

        // call search function:
        func.showSearch (this.queryObjectOid, this.oid, this.containerId);
*/
/*
        // check if the search has to be displayed:
        int event = func.getEvent ();
        if (event == SearchEvents.EVT_BUILDCALLINGSEARCHFORM)
                                // don't display search button?
        {
            // set the search button inactive:
            disableButton (values, Buttons.BTN_SEARCH);
        } // if don't display search button

        // call sequence control of function
        func.start ();
*/
    } // performShowSearchForm


    /**************************************************************************
     * show the search form with the selection fields - use an QueryExecutive.
     * <BR/>
     */
    public void showSearchForm ()
    {
        // create page:
        Page page = new Page ("SearchForm", false);

        // Stylesheetfile wird geladen
        StyleSheetElement style = new StyleSheetElement ();
        style.importSS = this.sess.activeLayout.path + this.env.getBrowser () +
            "/" +
            this.sess.activeLayout.elems[LayoutConstants.SHEETINFO].styleSheet;
        page.head.addElement (style);

        // set the icon of this object:
        this.setIcon ();

        // create Header
        FormElement form = this.createFormHeader (page, null, this.getNavItems (),
            null, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_FUNCTIONSEARCH, env), null, "SearchContainer.gif",
            this.containerName);

        // set target to second frame (frame is created in showSearchFrame)
        form.target = HtmlConstants.FRM_SHEET2;

        // add hidden elements:
        // oid of queryexecutive
        form.addElement (new InputElement (BOArguments.ARG_OID,
            InputElement.INP_HIDDEN, "" +
                ((this.oid == null) ? OID.EMPTYOID : this.oid.toString ())));
        // containerId
        form.addElement (new InputElement (BOArguments.ARG_CONTAINERID,
                            InputElement.INP_HIDDEN, "" + this.containerId));
        // functionId
        form.addElement (new InputElement (BOArguments.ARG_FUNCTION,
                            InputElement.INP_HIDDEN,
                            "" + AppFunctions.FCT_OBJECTSEARCH));
        // eventId to be called
        form.addElement (new InputElement (FunctionArguments.ARG_EVENT,
                            InputElement.INP_HIDDEN,
                            "" + SearchEvents.EVT_CLICKOKSEARCHFORM));
        // oid of querycreator
        form.addElement (new InputElement (SearchArguments.ARG_QUERYCREATOROID,
            InputElement.INP_HIDDEN, "" +
                ((this.getQueryCreatorOidString () == null) ? OID.EMPTYOID :
                    this.getQueryCreatorOidString ())));
        // oid of current object
        form.addElement (new InputElement (
            SearchArguments.ARG_CURRENTOBJECTOID, InputElement.INP_HIDDEN, "" +
                ((this.getCurrentObjectOidString () == null) ? OID.EMPTYOID :
                    this.getCurrentObjectOidString ())));

        // get the xsl output:
        String xslFormOutput = this.showSearchFormPropertiesXSL ();
        if (xslFormOutput != null)
        {
            // add the outout to the form:
            form.addElement (new TextElement (xslFormOutput));
        } // if (xslFormOutput != null)
        else
        {
            // create inner table
            TableElement table = new TableElement (2);
            table.border = 0;
            table.width = HtmlConstants.TAV_FULLWIDTH;

            table.classId = CssConstants.CLASS_INFO;
            String[] classIds = new String[2];
            classIds[0] = CssConstants.CLASS_NAME;
            classIds[1] = CssConstants.CLASS_VALUE;
            table.classIds = classIds;


            // loop through all properties in searchForm:
            this.showFormPropertiesSearch (table);

            form.addElement (table);
        } // else

        // check the actual user:
        if (this.getUser ().actUsername.equalsIgnoreCase (IOConstants.USERNAME_ADMINISTRATOR))
        {
            // create the script to be executed on client:
            page.body.addElement (this.getCommonScript (false));
        } // if
        else
        {
            // deactivate tabs and buttons:
            page.body.addElement (this.getButtonsTabsDeactivationScript ());
        } // else

        // create footer - do not show cancelbutton (last parameter = true)

        // create a custom button bar:
        GroupElement grButtons = new GroupElement ();
        grButtons.addElement (new BlankElement ());
        grButtons.addElement (new BlankElement ());
        grButtons.addElement (new BlankElement ());


        HTMLButtonElement resetButton =
            new HTMLButtonElement ("BUTT_RESET", HTMLButtonElement.INP_BUTTON);
        resetButton.onClick = "this.form.reset ();";
        resetButton.addLabel (
            MultilingualTextProvider.getText (QueryTokens.TOK_BUNDLE,
                QueryTokens.ML_NEW_SEARCH, env));
        resetButton.title = 
            MultilingualTextProvider.getText (QueryTokens.TOK_BUNDLE,
                QueryTokens.ML_NEW_SEARCH, env);
        resetButton.isDisabledOnClick = false;
        grButtons.addElement (resetButton);

        // add button to download the search result:
        this.addDownloadButton (this.queryObjectName, grButtons);

        // add button to invoke the reporting engine if applicable:
        this.addReportButton (this.queryObjectName, grButtons);

        // create the footer with the custom buttons
        this.createFormFooter (form, null, null, "   " + 
            MultilingualTextProvider.getText (QueryTokens.TOK_BUNDLE,
                QueryTokens.ML_SEARCH, env)
             + "   ", null, false, true, grButtons);

        // build the page and show it to the user:
        try
        {
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage ("FunctionSearch.showSearchForm",
                                   e.getMsg (), this.app, this.sess, this.env);
        } // catch
    } // showSearchForm


    /**************************************************************************
     * Generates the HTML code for the content view by using a stylesheet file.
     * Which should be named &lt;typecode>_content.xsl. <BR/>
     *
     * @return      the HTML code or <CODE>null</CODE>
     *              if no stylesheet is defined.
     */
    protected String showSearchFormPropertiesXSL ()
    {
        if (this.queryObject == null)
        {
            return null;
        } // if

        String xslFile = this.app.p_system.p_m2AbsBasePath +
            BOPathConstants.PATH_XSLT + this.queryObject.name + "_search.xsl";

        // if a type specific stylesheet file exists
        // generate the layout using this file.
        if (FileHelpers.exists (xslFile))
        {
            // create DomHandler to process xslt transformation
            DOMHandler processor = new DOMHandler (this.env, this.sess, this.app);
            // get dom tree of current object
            Document doc = this.createSearchFormDomTree (QueryExecutive_01.VIEWMODE_CONTENT);

            // show domtree:
            if (this.p_isShowDOMTree)
            {
                String domString = processor.domToString (doc, QueryConstants.XML_QUERY);
                // write the text non formatted
                this.env.write (IE302.TAG_PRE + IE302.HCH_NBSP + IE302.TAG_NEWLINE +
                    IE302.HCH_NBSP + IE302.TAG_NEWLINE + domString + IE302.TAG_PREEND);
            } // if

            return processor.process (doc, xslFile);
        } // if

/* KR 20060123 TODO: generic search stylesheet does not exist yet.
        xslFile = this.app.p_system.p_m2AbsBasePath +
                  BOPathConstants.PATH_XSLT + GENERIC_SEARCH_STYLESHEET;

        // if a generic stylesheet file exists
        // generate the layout using this file.
        if (FileHelpers.exists (xslFile))
        {
            return processor.process (doc, xslFile);
        } // if
*/

        return null;
    } // showSearchFormPropertiesXSL


    /**************************************************************************
     * Generates the HTML code for the content view by using a stylesheet file.
     * Which should be named &lt;typecode>_content.xsl. <BR/>
     *
     * @return      the HTML code or <CODE>null</CODE>
     *              if no stylesheet is defined.
     */
    protected String showContentXSL ()
    {
        if (this.queryObject == null)
        {
            return null;
        } // if

        String xslFile = this.app.p_system.p_m2AbsBasePath +
            BOPathConstants.PATH_XSLT + this.queryObject.name + "_result.xsl";

        // create DomHandler to process xslt transformation
        DOMHandler processor = new DOMHandler (this.env, this.sess, this.app);
        // get dom tree of current object
        Document doc = this.createResultDomTree (QueryExecutive_01.VIEWMODE_CONTENT);

        // show domtree
        if (this.p_isShowDOMTree)
        {
            String domString = processor.domToString (doc, QueryConstants.XML_QUERY);
            // write the text non formatted
            this.env.write (IE302.TAG_PRE + IE302.HCH_NBSP + IE302.TAG_NEWLINE +
                IE302.HCH_NBSP + IE302.TAG_NEWLINE + domString +
                IE302.TAG_PREEND);
        } // if

        // if a type specific stylesheet file exists
        // generate the layout using this file.
        if (FileHelpers.exists (xslFile))
        {
            return processor.process (doc, xslFile);
        } // if

        xslFile = this.app.p_system.p_m2AbsBasePath +
                  BOPathConstants.PATH_XSLT + QueryExecutive_01.GENERIC_RESULT_STYLESHEET;

        // if a generic stylesheet file exists
        // generate the layout using this file.
        if (FileHelpers.exists (xslFile))
        {
            return processor.process (doc, xslFile);
        } // if

        return null;
    } // showContentXSL


    /**************************************************************************
     * Generates the output for the download. <BR/>
     * There is a xslt file user which should be named
     * &lt;typecode&gt;_download.xsl. <BR/>
     *
     * @return      the generated output for download or <CODE>null</CODE>
     *              if no stylesheet is defined.
     */
    protected String showContentXSLDownload ()
    {
        if (this.queryObject == null)
        {
            return null;
        } // if

        String xslFile = this.app.p_system.p_m2AbsBasePath +
            BOPathConstants.PATH_XSLT + this.queryObject.name + QueryExecutive_01.FILEPF_DOWNLOAD;

        // create DomHandler to process xslt transformation
        DOMHandler processor = new DOMHandler (this.env, this.sess, this.app);
        // get dom tree of current object
        Document doc = this.createResultDomTree (QueryExecutive_01.VIEWMODE_CONTENT);

/*
        String domString = processor.domToString (doc, QueryConstants.XML_QUERY);
        // write the text non formatted
        this.env.write (IE302.TAG_PRE + IE302.HCH_NBSP + IE302.TAG_NEWLINE + IE302.HCH_NBSP + IE302.TAG_NEWLINE + domString + IE302.TAG_PREEND);
*/

        // if a type specific stylesheet file exists
        // generate the layout using this file.
        if (FileHelpers.exists (xslFile))
        {
            return processor.process (doc, xslFile);
        } // if

        xslFile = this.app.p_system.p_m2AbsBasePath +
                  BOPathConstants.PATH_XSLT + QueryExecutive_01.GENERIC_DOWNLOAD_STYLESHEET;

        // if a generic stylesheet file exists
        // generate the layout using this file.
        if (FileHelpers.exists (xslFile))
        {
            return processor.process (doc, xslFile);
        } // if

        return null;
    } // showContentXSLDownload


    ///////////////////////////////////////////////////////////////////////////
    // GUI functions
    ///////////////////////////////////////////////////////////////////////////


   /**************************************************************************
     * Set the headings and the orderings for this container. <BR/>
     * Overload this function if you need other headings as the standard container. <BR/>
     */
    protected void setHeadingsAndOrderings ()
    {
        int columnCount = 0;            // count of columns for headers
        int headerCount = 0;
        boolean orderByIsSet = false;

        // check if a queryCreator is assigned to this queryExecutive:
        if (this.queryObject != null)
        {
            columnCount = this.queryObject.getColumnHeaderCount ();

            // check if order by clause is set already:
/* BB TODO: deactivated. this would prevent a query from reordering
            if (this.queryObject.getOrderByString () != null)
                orderByIsSet = true;
*/
        } // if queryObject exist

        // filter m2-FUNCCOLIABLES from Headers
        // boolarray where every single bool marks if current
        // headerName is headerName or M2-SYSTEMVARIABLE
        boolean [] isColHeaderName = new boolean [columnCount];
        String headerName = new String ();

        // go through all columnNames - could be an
        // header or a M2-SYSTEMVARIABLE
        for (int i = 0; i < columnCount; i++)
        {
            headerName = this.queryObject.getMlColumnName (i);

            // check if headerName is headerName of FUNCCOL
            if (headerName.charAt (0) == QueryConstants.MARK_FUNCCOL)
            {
                isColHeaderName[i] = false;
            }  // if current columnName is FUNCCOLIABLE
            else  // current columnName is Header
            {
                headerCount++;
                isColHeaderName[i] = true;
            } // else current columnName is Header
        } // for - loop through all columnNames

        // HACK BEGIN
        if (columnCount <= 0)
        {
            this.headings = new String [] {"Name"};
            this.orderings = new String []{"name"};
        } // if
        else  // HACK END
        {
            // create new Stringarrays for headings and orderings
            this.headings = new String [headerCount];
            this.orderings = new String [headerCount];
        } // if columnCoung


        // attributename in query
        String quAttributeName = null;
        String colHeader = null;
        String colType = null;
        int j = 0;  // counter for current position in headings
        // set headings and orderings dynamic
        for (int i = 0; i < columnCount; i++)
        {
            // if current columnName is HEADER and no
            // SYSTEMVARIABLE, add header to headings
            if (isColHeaderName [i])
            {
                // set header for column nr. j
                colHeader = this.queryObject.getMlColumnName (i);
                colType = this.queryObject.getColumnType (i);

                this.headings [j] = colHeader;


                // check if orderby is set fix already in queryCreator
                //  - no dynamic orderings or current headertype is type
                // which u can't order.
                if (orderByIsSet ||
                    colType != null &&
                    (colType.equals (QueryConstants.COLUMNTYPE_IMAGE)  ||
                     colType.equals (QueryConstants.COLUMNTYPE_BUTTON) ||
                     colType.equals (QueryConstants.COLUMNTYPE_BUTTON_TEXT) ||
                     colType.startsWith (QueryConstants.COLUMNTYPE_BUTTON_IMAGE) ||
                     colType.equals (QueryConstants.COLUMNTYPE_INPUT_DATE) ||
                     colType.startsWith (QueryConstants.COLUMNTYPE_INPUT_STRING) ||
                     colType.startsWith (QueryConstants.COLUMNTYPE_INPUT_INTEGER) ||
                     colType.startsWith (QueryConstants.COLUMNTYPE_INPUT_NUMBER) ||
                     colType.startsWith (QueryConstants.COLUMNTYPE_INPUT_MONEY)))
                {
                    // if orderBy-Clause is set already in QueryCreator, do not
                    // support the changing of orderings in ContainerContent
                    this.orderings [j] = null;
                } // if
                else
                {
                    // set ordering for column nr. j
                    quAttributeName = this.queryObject.getColumnQueryAttribute (i);
                    this.orderings [j] = quAttributeName;
                } // else

                // increase counter for current header to be set
                j++;
            } // if current columnName is Header
        } // for

        // ensure that there is an available ordering taken:
        this.ensureAvailableOrdering ();
    } // setHeadingsAndOrderings


    /**************************************************************************
     * Gets the parameters which are relevant for this object. <BR/>
     * The <A HREF="#env">env</A> property is used for getting the parameters.
     * This property must be set before calling this method. <BR/>
     */
    public void getParameters ()
    {
        OID localOid = null;
        String str = null;
        int num = 0;
        boolean isReordering = false;   // is this just a reordering call?

        super.getParameters ();

        // check if a reordering shall be done:
        if (this.env.getIntParam (BOArguments.ARG_REORDER) !=
            IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
        {
            isReordering = true;
            // don't display the search dialog:
            this.p_isShowSearchForm = false;
        } // if

        // oid of queryCreator (selected in selection list)
        // if this environment variable is set, the change or search form is
        // shown. We have to get the search and match types parameter out from
        // the environment.
        if ((localOid = this.env.getOidParam (QueryArguments.ARG_QUERYCREATOR)) !=
            null)
        {
            this.queryObjectOid = localOid;
            // read parameters out from environment
            this.setGetEnvParams (true);
        } // if
        else
        {
            // read parameters out from parameters
            this.setGetEnvParams (false);
        } // else

        // could be given via javaScript call of showQuery (name, rootOid)
        if ((str = this.env.getStringParam (QueryArguments.ARG_QUERYCREATORNAME)) !=
            null)
        {
            // set values for query string
            this.queryObjectName = str;
        } // if

        // oid of rootObject (if field type OBJECTPATH is used,
        // or via javaScript call of showQuery  (...))
        if ((localOid = this.env.getOidParam (
            QueryArguments.ARG_ROOTOBJECTSELECTION)) != null)
        {
            this.rootObjectOid = localOid;
        } // if

        // oid of current object if query is used
        // to interact with current object
        if ((localOid = this.env.getOidParam (
            QueryArguments.ARG_CURRENTOBJECTOID)) != null)
        {
            this.currentObjectOid = localOid;
        } // if

        // showSearchForm
        if ((num = this.env.getBoolParam (this
            .adoptArgName (QueryArguments.ARG_SHOWSEARCHFORM))) >=
                IOConstants.BOOLPARAM_FALSE)
        {
            this.p_isShowSearchForm = num == IOConstants.BOOLPARAM_TRUE;
        } // if

        // show DOM tree:
        if ((num = this.env.getBoolParam (DIArguments.ARG_SHOWDOMTREE)) >
            IOConstants.BOOLPARAM_NOTEXISTS)
        {
            this.p_isShowDOMTree = num == IOConstants.BOOLPARAM_TRUE;
        } // if

        // check if this is just reordering:
        // in that case we don't have to change any query parameters.
        if (!isReordering)              // no reordering?
        {
            // get search parameters
            this.getSearchParameters (this.env);
        } // if no reordering
    } // getParameters



    /**************************************************************************
     * Gets the search parameters which are relevant for this object. <BR/>
     * The <A HREF="#env">env</A> property is used for getting the search parameters.
     * This property must be set before calling this method. <BR/>
     *
     * @param   env     The current environment.
     */
    public void getSearchParameters (Environment env)
    {
        // initialise match types and searchvalues wich are read in getParameters
        StringBuffer newMatchTypes = new StringBuffer ();
        StringBuffer newSearchValues = new StringBuffer ();

        // prepare querycreator with given oid or name and with rootOid
        this.prepareQueryCreator ();

        // check if parameters shall be retrieved from the environment
        if (this.isGetEnvParams ())
        {
            // get search field parameters from the environment parameters:
            QueryExecutive_01.getEnvSearchfieldParameters (this.p_inParams, env,
                    null, this.getIsQueryExecuted ());
        } // if
        // parameters shall be retrieved from the database:
        else if (this.state != States.ST_UNKNOWN)
        {
            // get saved search field parameters:
            QueryExecutive_01.getSavedSearchfieldParameters (this.p_inParams,
                    this.p_searchValues, this.p_matchTypes);
        } // else

        // check if there are any input parameters
        // do not set p_searchValues and p_matchTypes when query is executed
        // this will overwrite saved search and match type values and the
        // wrong values will be shown when switching from content to info view
        if (this.p_inParams.size () > 0 && !this.getIsQueryExecuted ())
        {
            // initialise match types and searchvalues which are read:
            newMatchTypes = new StringBuffer ();
            newSearchValues = new StringBuffer ();
            String sep = "";

            // create comma-separated strings for storing in database:
            // loop through all possible parameters and handle each of them:
            for (Iterator<QueryParameter> iter = this.p_inParams.iterator ();
                iter.hasNext ();)
            {
                QueryParameter param = iter.next ();
                String fieldType = param.getType ();
                String searchValue = param.getValue ();
                String rangeValue = param.getRangeValue ();
                String matchType = param.getMatchType ();

                // append separator:
                newSearchValues.append (sep);
                newMatchTypes.append (sep);

                if (!fieldType.equals (QueryConstants.FIELDTYPE_OBJECTPATH))
                {
                    sep = QueryConstants.CONST_DELIMITER;
                } // if

                if (fieldType.equals (QueryConstants.FIELDTYPE_DATETIME))
                {
                    // try to get value of inputfield for special
                    // searchvalue
                    if (param.p_isValueSet)
                    {
                        // set values for querystring
                        this.queryObject.setSearchValueForQueryAttr (
                            param.pos, searchValue);

                        // set searchvalue
                        newSearchValues.append (searchValue);

                        // set match types for values in querystring
                        this.queryObject.setMatchTypeForQueryAttr (
                            param.pos, matchType);

                        // set match type for current searchvalue
                        newMatchTypes.append (matchType);
                    } // if
                    else // environment parameter does not exist for search field
                    {
                        newSearchValues.append (QueryConstants.CONST_UNDEF);
                        newMatchTypes.append (QueryConstants.CONST_UNDEF);
                    } // else no environment parameter exist for
                } // else if DATETIME
                else if (
                    fieldType.equals (QueryConstants.FIELDTYPE_INTEGERRANGE) ||
                    fieldType.equals (QueryConstants.FIELDTYPE_NUMBERRANGE) ||
                    fieldType.equals (QueryConstants.FIELDTYPE_MONEYRANGE) ||
                    fieldType.equals (QueryConstants.FIELDTYPE_DATETIMERANGE) ||
                    fieldType.equals (QueryConstants.FIELDTYPE_DATERANGE) ||
                    fieldType.equals (QueryConstants.FIELDTYPE_TIMERANGE))
                {
                    // try to get value of inputfield for special searchvalue
                    if (param.p_isValueSet)
                    {
                        // set values for querystring
                        this.queryObject.setSearchRangeForQueryAttr (
                            param.pos, searchValue, rangeValue);

                        // check if rangeValue is set
                        if (rangeValue != null && rangeValue.length () > 0)
                        {
                            // set new searchValue
                            searchValue += "-" + rangeValue;
                        } // if

                        // set searchvalue
                        newSearchValues.append (searchValue);
                        newMatchTypes.append (QueryConstants.CONST_UNDEF);
                    } // if (date != null || dateRange != null)
                    else // environment parameter does not exist for search field
                    {
                        newSearchValues.append (QueryConstants.CONST_UNDEF);
                        newMatchTypes.append (QueryConstants.CONST_UNDEF);
                    } // else no environment parameter exist for
                } // else if DATETIMERANGE || DATERANGE || TIMERANGE
                else
                // normal field type
                {
                    // try to get value of inputfield for special
                    // searchvalue
                    if (param.p_isValueSet)
                    {
                        // set values for querystring
                        this.queryObject.setSearchValueForQueryAttr (
                            param.pos, searchValue);

                        // replace delimiter with unicode
                        searchValue = StringHelpers.replace (searchValue, QueryConstants.CONST_DELIMITER,
                            AppConstants.UC_COMMA);
                        // set searchvalue
                        newSearchValues.append (searchValue);

                        // set match types for values in querystring
                        this.queryObject.setMatchTypeForQueryAttr (
                            param.pos, matchType);

                        // set match type for current searchvalue
                        newMatchTypes.append (matchType);
                    } // if
                    else
                    // no env-param exist for search field - used for
                    // referenced object-Field
                    {
                        newSearchValues
                            .append (QueryConstants.CONST_UNDEF);
                        newMatchTypes.append (QueryConstants.CONST_UNDEF);
                    } // else no environment parameter exist for search field
                } // else normal field type
            } // for iter
            if (newMatchTypes.length () > 0 &&
                    newSearchValues.length () > 0)
            {
                this.p_matchTypes = newMatchTypes.toString ();
                this.p_searchValues = newSearchValues.toString ();
            } // if
        } // if
    } // getSearchParameters


    /**************************************************************************
     * Gets the parameters which are relevant for the search fields of this
     * object. <BR/>
     *
     * @param   inParams    The parameters to be read from the environment.
     * @param   env         The environment used for getting the parameters.
     * @param   prefix      The prefix which has to be prepended before each
     *                      parameter name.
     *
     * @return  A vector containing all parameters or
     *          <CODE>null</CODE> if there do not exist any search field
     *          parameters for the query.
     */
    public static Vector<QueryParameter> getSearchfieldParameters (
                                                   Vector<QueryParameter> inParams,
                                                   Environment env,
                                                   String prefix)
    {
           // return the result:
        return QueryExecutive_01.getSearchfieldParameters (inParams, env, null,
                null, prefix);
    } // getSearchfieldParameters



    /**************************************************************************
     * Gets the parameters which are relevant for the search fields of this
     * object. <BR/>
     *
     * @param   inParams        The parameters to be read from the environment.
     * @param   env             The environment used for getting the parameters.
     * @param   searchValues    The searchValues used for getting the parameters.
     * @param   matchTypes      The matchTypes used for getting the parameters.
     * @param   prefix             The prefix which has to be prepended before each
     *                          parameter name.
     *
     * @return  A vector containing all parameters or
     *          <CODE>null</CODE> if there do not exist any search field
     *          parameters for the query.
     */
    public static Vector<QueryParameter> getSearchfieldParameters (
                                                   Vector<QueryParameter> inParams,
                                                   Environment env,
                                                   String searchValues,
                                                   String matchTypes,
                                                   String prefix)
    {
        // check if there are any parameters to be set:
        if (inParams == null)
        {
            return inParams;
        } // if
        // check if the search parameters have to be set from database:
        else if (env == null && searchValues != null && matchTypes != null)
        {
            // return the result:
            // return the result:
            return QueryExecutive_01.getSavedSearchfieldParameters (inParams,
                    searchValues, matchTypes);
        } // if
        // check if the search parameters have to be set from the environment:
        else
        {
            // return the result:
            return QueryExecutive_01.getEnvSearchfieldParameters (inParams, env,
                    prefix, false);
        } // if
    } // getSearchfieldParameters


    /**************************************************************************
     * Gets the parameters which are relevant for the search fields of this
     * object. <BR/>
     *
     * @param   inParams        The parameters to be read from the environment.
     * @param   searchValues    The values to search for.
     * @param   matchTypes      The used match types.
     *
     * @return  A vector containing all parameters or
     *          <CODE>null</CODE> if there do not exist any search field
     *          parameters for the query.
     */
    public static Vector<QueryParameter> getSavedSearchfieldParameters (
                                                   Vector<QueryParameter> inParams,
                                                   String searchValues,
                                                   String matchTypes)
    {
        int posSearchValue = 0;
        String strVal = null;
        String strRange = null;
        String strMatchType = null;        // match type of actual search field
        String fieldType = null;        // type of actual search field
        QueryParameter param = null;    // the actual query parameter

        // check if there are any parameters to be set:
        if (inParams == null || searchValues == null || matchTypes == null ||
            searchValues.trim ().isEmpty () || matchTypes.trim ().isEmpty ())
        {
            return inParams;
        } // if

        String[] singleSearchValues = searchValues
            .split (QueryConstants.CONST_DELIMITER);
        String[] singleMatchTypes = matchTypes
            .split (QueryConstants.CONST_DELIMITER);

        // walk through all search value fields and try to get the value
        for (Iterator<QueryParameter> iter = inParams.iterator (); iter.hasNext ();)
        {
            // get the actual parameter:
            param = iter.next ();

            // get field type:
            fieldType = param.getType ();

            // get search value:
            strVal = StringHelpers.replace (
                    singleSearchValues[posSearchValue],
                    AppConstants.UC_COMMA, QueryConstants.CONST_DELIMITER);

            // get match type:
            strMatchType = singleMatchTypes[posSearchValue];

            // check if we got a value
            if (strVal.equals (QueryConstants.CONST_NULL))
            {
                strVal = null;
            } // if

            // handle the field type:
            if (fieldType.equals (QueryConstants.FIELDTYPE_INTEGERRANGE) ||
                fieldType.equals (QueryConstants.FIELDTYPE_NUMBERRANGE) ||
                fieldType.equals (QueryConstants.FIELDTYPE_MONEYRANGE) ||
                fieldType.equals (QueryConstants.FIELDTYPE_DATERANGE) ||
                fieldType.equals (QueryConstants.FIELDTYPE_DATETIMERANGE) ||
                fieldType.equals (QueryConstants.FIELDTYPE_TIMERANGE))
            {
                if (strVal != null && !strVal.equals (QueryConstants.CONST_NULL +
                        "-"    + QueryConstants.CONST_NULL))
                {
                    int pos = strVal.indexOf ("-");

                    // get both the lower and the upper value out of the
                    // value string:
                    // note that range fields have stored their values
                    // in the following format: <value>-<rangeValue>
                    if (pos >= 0)
                    {
                        // try to get value of input field for special search value:
                        strRange = strVal.substring (pos + 1);
                        strVal = strVal.substring (0, pos);
                    } // if

                    if (fieldType.equals (QueryConstants.FIELDTYPE_TIMERANGE))
                    {
                        // check if we got a range:
                        if (strRange != null &&
                            strRange.equals (QueryConstants.CONST_NULL))
                        {
                            pos = strRange.indexOf (" ");
                            // get the date value out of the
                            // value string:
                            // note that date/time field types have stored
                            // their values in the following format:
                            // <date>-<time>
                            if (pos >= 0)
                            {
                                // get time string
                                strRange = strRange.substring (pos + 1);
                                strVal = strVal.substring (pos + 1);
                            } // if
                        } // if
                        // we got no range value:
                        else
                        {
                            strRange = null;
                        } // if
                    } // else

                    // check if we got a value:
                    if (strVal.equals (QueryConstants.CONST_NULL) ||
                        strVal.trim ().isEmpty ())
                    {
                        strVal = null;
                    } // if

                    // check if we got a range:
                    if (strRange != null &&
                        strRange.equals (QueryConstants.CONST_NULL))
                    {
                        strRange = null;
                    } // if
                } // if (date != null || dateRange != null)
                else    // no no environment parameter exist for search field
                {
                    // set parameter value:
                    strVal = null;
                    strRange = null;
                } // else no environment parameter exist for search field
            } // if NUMBERRANGE || MONEYRANGE
            else if (fieldType.startsWith (QueryConstants.FIELDTYPE_QUERYSELECTION) ||
                     fieldType.startsWith (QueryConstants.FIELDTYPE_QUERYSELECTIONNUM) ||
                     fieldType.startsWith (QueryConstants.FIELDTYPE_QUERYSELECTIONOID))
            {
                // check if we got an value
                if (strVal != null &&
                    !strVal.equals (QueryConstants.CONST_NULL) &&
                    !strVal.trim ().isEmpty ())
                {
                    // is it a singe selection box?
                    if (fieldType.indexOf (QueryConstants.QUERYSELECTIONBOX_PARAM_MULTIPLE) != -1)
                    {
                        // replace UC_COMMA with ","
                        strVal = StringHelpers.replace (strVal,
                             AppConstants.UC_COMMA, QueryConstants.CONST_DELIMITER);
                        // in case of a mulitpleselectionbox we generate a string
                        // as parameter value that can in included into the querystring
                        // value1,value2,value3 (for QUERYSELECTIONNUM, QUERYSELECTIONOID)
                        // 'value1','value2','value3' (for QUERYSELECTION)
                        // in that case we need to read a multivalue
                        String [] options =
                            strVal.split (QueryConstants.CONST_DELIMITER);

                        if (options != null && options.length > 0)
                        {
                            StringBuffer multiValStrBuf = new StringBuffer ();
                            String comma = "";

                            // loop though the values and generate the paramter value
                            for (int x = 0; x < options.length; x++)
                            {
                                // check for emtpy option that has " " as value
                                if (!options[x].equals (" "))
                                {
                                    multiValStrBuf.append (comma)
                                        .append (options[x]);
                                    comma = QueryConstants.CONST_DELIMITER;
                                } // if (! options[i].equals(" "))
                            } // for (int i = 0; i < options.length; i++)
                            // any value set now?
                            if (multiValStrBuf.length () > 0)
                            {
                                // set the parameter value
                                strVal = multiValStrBuf.toString ();
                            } // if (multiValStrBuf.length() > 0)
                            else    // no value set
                            {
                                // we need to set " " as value to be compatible
                                // with the original QUERYSELECTIONBOX implementation
                                // because some queries use
                                // #SYSVAR.QUERY_searchfield# = ' '
                                // in their expressions.
                                // Note that this is a dirty solution!
                                strVal = " ";
                            } // else no value set
                        } // if (values.length > 0)
                    } // if
                } // else // multiple selection
                else // no values selected
                {
                    // set parameter value:
                    // if no search value is set, set it to ' '
                    // because of side effects with existing queries,
                    // where SYSTEM variables are used
                    strVal = " ";
                } // else // no values selected
            } // else if QUERYSELECTION || QUERYSELECTIONNUM || QUERYSELECTIONOID
            else // any other fieldtype
            {
                // nothing to do
            } // else normal field type

            // set parameters
            param.setValue (strVal);
            param.setRangeValue (strRange);
            param.setMatchType (strMatchType);

            // count search value field
            posSearchValue++;
        } // for iter

        // return the result:
        return inParams;
    } // getSavedSearchfieldParameters


    /**************************************************************************
     * Gets the parameters which are relevant for the search fields of this
     * object. <BR/>
     *
     * @param   inParams    The parameters to be read from the environment.
     * @param   env         The environment used for getting the parameters.
     * @param   prefix      The prefix which has to be prepended before each
     *                      parameter name.
     * @param   isQueryExecuted Is the query already executed?
     *
     * @return  A vector containing all parameters or
     *          <CODE>null</CODE> if there do not exist any search field
     *          parameters for the query.
     */
    public static Vector<QueryParameter> getEnvSearchfieldParameters (
                                                   Vector<QueryParameter> inParams,
                                                   Environment env,
                                                   String prefix,
                                                   boolean isQueryExecuted)
    {
        String argName = null;
        String strVal = null;
        String strRange = null;
        Date date = null;
        Date dateRange = null;
        QueryParameter param = null;    // the actual query parameter
        String fieldType = null;        // type of actual field

        // check if there are any parameters to be set:
        if (inParams == null)
        {
            return inParams;
        } // if

        // walk through all search value fields and try to get the value
        for (Iterator<QueryParameter> iter = inParams.iterator (); iter.hasNext ();)
        {
            // get the actual parameter:
            param = iter.next ();

            // compute argument name:
            if (prefix != null)
            {
                argName = prefix + QueryArguments.ARG_SEARCHFIELD + param.pos;
            } // if
            else
            {
                argName = QueryArguments.ARG_SEARCHFIELD + param.pos;
            } // else

            // get field type:
            fieldType = param.getType ();

            // handle the field type:
            if (fieldType.equals (QueryConstants.FIELDTYPE_INTEGERRANGE) ||
                fieldType.equals (QueryConstants.FIELDTYPE_NUMBERRANGE) ||
                fieldType.equals (QueryConstants.FIELDTYPE_MONEYRANGE))
            {
                // try to get value of input field for special search value:
                strVal = env.getStringParam (argName);
                strRange = env.getStringParam (
                        argName + BOArguments.ARG_RANGE_EXTENSION);

                if (strVal != null || strRange != null)
                {
                    // check if we got a value:
                    if (strVal == null)
                    {
                        strVal = "";
                    } // if

                    // check if we got a range:
                    if (strRange == null)
                    {
                        strRange = "";
                    } // if

                    // set parameter value:
                    param.setRange (strVal, strRange);
                } // if (date != null || dateRange != null)
                else    // no no environment parameter exist for search field
                {
                    // set parameter value:
                    param.setRange (null, null);
                } // else no environment parameter exist for search field
                // set parameter match type:
                param.setMatchType (null);
            } // if NUMBERRANGE || MONEYRANGE

            else if (fieldType.equals (QueryConstants.FIELDTYPE_DATETIME))
            {
                // try to get value of inputfield for special searchvalue
                if ((date = env.getDateTimeParam (argName)) != null)
                {
                    strVal = DateTimeHelpers.dateTimeToString (date);

                    // set parameter value:
                    param.setValue (strVal);
                } // if

                // check if we got a value set, otherwise we do not need to
                // set a match type value
                // set match type for each search field
                if (strVal != null && !strVal.trim ().isEmpty ())
                {
                    if ((strVal = env.getParam (argName +
                        BOArguments.ARG_MATCHTYPE_EXTENSION)) != null)
                    {
                        // set parameter match type:
                        param.setMatchType (strVal);
                    } // if
                    else
                    {
                        // set parameter match type:
                        param.setMatchType (null);
                    } // else
                } // if
                else
                {
                    // set parameter match type:
                    param.setMatchType (null);
                } // else
            } // if DATETIME
            else if (fieldType.equals (QueryConstants.FIELDTYPE_DATETIMERANGE))
            {
                // try to get value of inputfield for special searchvalue
                date = env.getDateTimeParam (argName);
                dateRange = env.getDateTimeParam (argName +
                    BOArguments.ARG_RANGE_EXTENSION);

                if ((date != null && !date.toString ().equalsIgnoreCase (
                    QueryConstants.CONST_NULL)) ||
                    (dateRange != null && !dateRange.toString ()
                        .equalsIgnoreCase (QueryConstants.CONST_NULL)))
                {
                    // check if we got a date
                    if (date != null)
                    {
                        strVal = DateTimeHelpers.dateTimeToString (date);
                    } // if
                    else
                    {
                        strVal = "";
                    } // else

                    // check if we got a daterange
                    if (dateRange != null)
                    {
                        strRange = DateTimeHelpers.dateTimeToString (dateRange);
                    } // if
                    else
                    {
                        strRange = "";
                    } // else

                    // set parameter value:
                    param.setRange (strVal, strRange);
                } // if (date != null || dateRange != null)
                else    // no no environment parameter exist for search field
                {
                    // set parameter value:
                    param.setRange (null, null);
                } // else no environment parameter exist for search field
                // set parameter match type:
                param.setMatchType (null);
            } // else if DATETIMERANGE
            else if (fieldType.equals (QueryConstants.FIELDTYPE_DATERANGE))
            {
                // try to get value of inputfield for special searchvalue
                date = env.getDateParam (argName);
                dateRange = env.getDateParam (argName +
                    BOArguments.ARG_RANGE_EXTENSION);

                if ((date != null && !date.toString ().equalsIgnoreCase (
                    QueryConstants.CONST_NULL)) ||
                    (dateRange != null && !dateRange.toString ()
                        .equalsIgnoreCase (QueryConstants.CONST_NULL)))
                {
                    // check if we got a date
                    if (date != null)
                    {
                        strVal = DateTimeHelpers.dateTimeToString (date);
                    } // if
                    else
                    {
                        strVal = "";
                    } // else

                    // check if we got a daterange
                    if (dateRange != null)
                    {
                        dateRange.setTime (dateRange.getTime () + 86399000); // 23:59:59
                        strRange = DateTimeHelpers.dateTimeToString (dateRange);
                    } // if
                    else
                    {
                        strRange = "";
                    } // else

                    // set parameter value:
                    param.setRange (strVal, strRange);
                } // if (date != null || dateRange != null)
                else    // no no environment parameter exist for search field
                {
                    // set parameter value:
                    param.setRange (null, null);
                } // else no environment parameter exist for search field
                // set parameter match type:
                param.setMatchType (null);
            } // else if DATERANGE
            else if (fieldType.equals (QueryConstants.FIELDTYPE_TIMERANGE))
            {
                // try to get value of input field for special search value:
                date = env.getTimeParam (argName);
                dateRange = env.getTimeParam (argName +
                    BOArguments.ARG_RANGE_EXTENSION);

                if ((date != null && !date.toString ().equalsIgnoreCase (
                    QueryConstants.CONST_NULL)) ||
                    (dateRange != null && !dateRange.toString ()
                        .equalsIgnoreCase (QueryConstants.CONST_NULL)))
                {
                    // check if we got a date
                    if (date != null)
                    {
                        strVal = DateTimeHelpers.timeToString (date);
                    } // if
                    else
                    {
                        strVal = "";
                    } // else

                    // check if we got a date range
                    if (dateRange != null)
                    {
                        strRange = DateTimeHelpers.timeToString (dateRange);
                    } // if
                    else
                    {
                        strRange = "";
                    } // else

                    // set parameter value:
                    param.setRange (strVal, strRange);
                    // set parameter match type:
                    param.setMatchType (null);
                } // if (date != null || dateRange != null)
                else    // no no environment parameter exist for search field
                {
                    // set parameter value:
                    param.setRange (null, null);
                } // else no environment parameter exist for search field
                // set parameter match type:
                param.setMatchType (null);
            } // else if TIMERANGE
            else if (fieldType.startsWith (QueryConstants.FIELDTYPE_QUERYSELECTION) ||
                     fieldType.startsWith (QueryConstants.FIELDTYPE_QUERYSELECTIONNUM) ||
                     fieldType.startsWith (QueryConstants.FIELDTYPE_QUERYSELECTIONOID))
            {
                // is it a singe selection box?
                if (fieldType.indexOf (QueryConstants.QUERYSELECTIONBOX_PARAM_MULTIPLE) == -1)
                {
                    strVal = env.getStringParam (argName);
                    // try to get value of the searchfield
                    // note that selecting the emtpy option returns " "
                    if (strVal != null && !strVal.trim ().isEmpty ())
                    {
                        param.setValue (strVal);
                    } // if
                    else // no value set for searchfield
                    {
                        param.setValue (null);
                    } // else no value set for searchfield
                } // if (fieldType.indexOf (QueryConstants.QUERYSELECTIONBOX_PARAM_MULTIPLE) == -1)
                else // multiple selection
                {
                    // in case of a mulitpleselectionbox we generate a string
                    // as parameter value that can in included into the querystring
                    // value1,value2,value3 (for QUERYSELECTIONNUM, QUERYSELECTIONOID)
                    // 'value1','value2','value3' (for QUERYSELECTION)

                    // in that case we need to read a multivalue
                    String [] options = env.getMultipleParam (argName);
                    if (options != null && options.length > 0)
                    {
                        StringBuffer multiValStrBuf = new StringBuffer ();
                        String comma = "";
                        String brackets = "";
                        // in case of fieldtype FIELDTYPE_QUERYSELECTION
                        // the values must be surrounded by brackets
                        if (!(fieldType.startsWith (QueryConstants.FIELDTYPE_QUERYSELECTIONNUM) ||
                              fieldType.startsWith (QueryConstants.FIELDTYPE_QUERYSELECTIONOID)))
                        {
                            brackets = "'";
                        } // if (! (fieldType.startsWith (QueryConstants.FIELDTYPE_QUERYSELECTIONNUM) ||
                        // loop though the values and generate the paramter value
                        for (int i = 0; i < options.length; i++)
                        {
                            // check for emtpy option that has " " as value
                            if (!options[i].equals (" "))
                            {
                                multiValStrBuf.append (comma)
                                    .append (brackets)
                                    .append (options[i])
                                    .append (brackets);
                                comma = QueryConstants.CONST_DELIMITER;
                            } // if (! options[i].equals(" "))
                        } // for (int i = 0; i < options.length; i++)
                        // any value set now?
                        if (multiValStrBuf.length () > 0)
                        {
                            // set the parameter value
                            param.setValue (multiValStrBuf.toString ());
                        } // if (multiValStrBuf.length() > 0)
                        else    // no value set
                        {
                            // we need to set " " as value to be compatible
                            // with the original QUERYSELECTIONBOX implementation
                            // because some queries use
                            // #SYSVAR.QUERY_searchfield# = ' '
                            // in their expressions.
                            // Note that this is a dirty solution!
                            param.setValue (null);
                        } // else no value set
                    } // if (values.length > 0)
                    else // no values selected
                    {
                        param.setValue (null);
                    } // else // no values selected
                } // else // multiple selection
                // set parameter match type:
                param.setMatchType (null);
            } // else if QUERYSELECTION || QUERYSELECTIONNUM || QUERYSELECTIONOID
            else // any other fieldtype
            {
                // try to get value of inputfield for special searchvalue
                if ((strVal = env.getStringParam (argName)) != null)
                {
                    // check if we got a value
                    if (!strVal.trim ().isEmpty ())
                    {
                        // set parameter value:
                        param.setValue (strVal);
                    } // if
                    else
                    {
                        // set parameter value:
                        param.setValue (null);
                    } // else
                } // if
                else
                // no env-param exist for search field - used for referenced object-Field
                {
                    // set parameter value:
                    param.setValue (null);
                } // else no environment parameter exist for search field

                // check if we got a value set, otherwise we do not need to
                // set a match type value
                // set match type for each search field
                if (strVal != null && !strVal.trim ().isEmpty ())
                {
                    if ((strVal = env.getParam (argName +
                        BOArguments.ARG_MATCHTYPE_EXTENSION)) != null)
                    {
                        // set parameter match type:
                        param.setMatchType (strVal);
                    } // if
                    else
                    // no env-param exist for match type - used for referenced object-Field
                    {
                        // set parameter match type:
                        param.setMatchType (null);
                    } // else no environment parameter exist for search field
                } // if
                else
                {
                    // set parameter match type:
                    param.setMatchType (null);
                } // else
            } // else normal field type
        } // for iter

        // return the result:
        return inParams;
    } // getEnvSearchfieldParameters


    /**************************************************************************
     * Represent the properties of a QueryExecutive_01 object to the user. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     *
     * @see ibs.IbsObject#showProperty(TableElement, String, String, int, String)
     */
    public void showProperties (TableElement table)
    {
        // save name and oid of queryCreator in helpvariables
        String queryCreatorName = "";
        String queryCreatorOid = "";

        if (this.queryObject != null)
        {
            queryCreatorName = this.queryObject.name;
            queryCreatorOid  = this.queryObject.oid.toString ();
        } // if

        // loop through all properties of this object and display them:
        super.showProperties (table);

        // object specific attributes

        // Reporttemplate (QueryObject)
        this.showProperty (table, QueryArguments.ARG_QUERYCREATORNAME, 
            MultilingualTextProvider.getText (QueryTokens.TOK_BUNDLE,
                QueryTokens.ML_QUERYCREATOR, env),
            Datatypes.DT_NAME, queryCreatorName);

        // show search form:
        this.showProperty (table, QueryArguments.ARG_SHOWSEARCHFORM,
            MultilingualTextProvider.getText (QueryTokens.TOK_BUNDLE,
                QueryTokens.ML_SHOWSEARCHFORM, env),
            Datatypes.DT_BOOL, "" + this.p_isShowSearchForm);

        // show DOM tree:
        this.showProperty (
            table, DIArguments.ARG_SHOWDOMTREE,    
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_SHOWDOMTREE, env),
            Datatypes.DT_BOOL, "" + this.p_isShowDOMTree);

        // show the search properties
        this.showPropertiesSearch (table);

        // oid of Reporttemplate (QueryObject)
        this.showProperty (table,
                      QueryArguments.ARG_QUERYCREATOR,  // Argument
                      "",  // token
                      Datatypes.DT_HIDDEN,
                      queryCreatorOid);
    } // showProperties


    /**************************************************************************
     * Shows the search properties. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     *
     * @see ibs.IbsObject#showProperty(TableElement, String, String, int, String)
     */
    public void showPropertiesSearch (TableElement table)
    {
        // show searchspecific properties
        // get saved search field parameters
        // this is required, causes the a side when switching from content
        // to info view, when environment search parameters are retrieved
        this.setGetEnvParams (false);
        this.getSearchParameters (this.env);

        // searchValues:
        String searchValueString = "";

        // check if there are any search parameters:
        if (this.queryObject != null && this.p_inParams != null)
        {
            String fieldName = "";
            String fieldType = null;
            String searchValue = null;
            String rangeValue = null;

            // loop through all possible input parameters and add a condition
            // for each of them, which was set:
            for (Iterator<QueryParameter> iter = this.p_inParams.iterator ();
                iter.hasNext ();)
            {
                // get current parameter
                QueryParameter param = iter.next ();

                // get data for current field:
                fieldName = param.getName ();

                // get field type
                fieldType = param.getType ();

                // get search value
                searchValue = param.getValue ();

                // check if search value is set
                if (searchValue == null ||
                    searchValue.equalsIgnoreCase (QueryConstants.CONST_NULL))
                {
                    searchValue = "";
                } // if

                // handle the field type:
                if (fieldType.equals (QueryConstants.FIELDTYPE_INTEGERRANGE) ||
                    fieldType.equals (QueryConstants.FIELDTYPE_NUMBERRANGE) ||
                    fieldType.equals (QueryConstants.FIELDTYPE_MONEYRANGE) ||
                    fieldType.equals (QueryConstants.FIELDTYPE_DATETIMERANGE) ||
                    fieldType.equals (QueryConstants.FIELDTYPE_TIMERANGE) ||
                    fieldType.equals (QueryConstants.FIELDTYPE_DATERANGE))
                {
                    // get range value
                    rangeValue = param.getRangeValue ();

                    // check if range value is set
                    if (rangeValue == null ||
                        rangeValue.equalsIgnoreCase (QueryConstants.CONST_NULL))
                    {
                        rangeValue = "";
                    } // if

                    // check if we got a value
                    if (!searchValue.isEmpty ())
                    {
                        int pos = searchValue.indexOf (" ");

                        // get both the lower and the upper value out of the
                        // value string:
                        // note that range fields have stored their values
                        // in the following format: <value>-<rangeValue>
                        if (pos >= 0)
                        {
                            // check if field is of type date
                            if (fieldType.equals (QueryConstants.FIELDTYPE_DATERANGE))
                            {
                                searchValue = searchValue.substring (0, pos);
                            } // else
                        } // if
                    } // if
                    // check if we got a value
                    if (!rangeValue.isEmpty ())
                    {
                        int pos = rangeValue.indexOf (" ");

                        // get both the lower and the upper value out of the
                        // value string:
                        // note that range fields have stored their values
                        // in the following format: <value>-<rangeValue>
                        if (pos >= 0)
                        {
                            // check if field is of type date
                            if (fieldType.equals (QueryConstants.FIELDTYPE_DATERANGE))
                            {
                                rangeValue = rangeValue.substring (0, pos);
                            } // else
                        } // if
                        rangeValue = " - " + rangeValue;
                    } // if
                    // set search range value
                    searchValue = searchValue + rangeValue;
                } // if
                // else if QUERYSELECTION || QUERYSELECTIONNUM || QUERYSELECTIONOID
                else if (fieldType.startsWith (QueryConstants.FIELDTYPE_QUERYSELECTION) ||
                        fieldType.startsWith (QueryConstants.FIELDTYPE_QUERYSELECTIONNUM) ||
                        fieldType.startsWith (QueryConstants.FIELDTYPE_QUERYSELECTIONOID))
                {
                    // is it a singe selection box?
                    if (fieldType
                        .indexOf (QueryConstants.QUERYSELECTIONBOX_PARAM_MULTIPLE) != -1)
                    {
                        // is it a singe selection box?
                        if (searchValue.indexOf (AppConstants.UC_COMMA) != -1)
                        {
                            // replace UC_COMMA with ","
                            searchValue = StringHelpers.replace (searchValue,
                                AppConstants.UC_COMMA,
                                QueryConstants.CONST_DELIMITER);
                            // in case of a mulitpleselectionbox we generate a
                            // string as parameter value that can in included
                            // into the querystring
                            // value1,value2,value3 (for QUERYSELECTIONNUM,
                            // QUERYSELECTIONOID)
                            // 'value1','value2','value3' (for QUERYSELECTION)
                            // in that case we need to read a multivalue
                            String[] options = searchValue
                                .split (QueryConstants.CONST_DELIMITER);

                            if (options != null && options.length > 0)
                            {
                                StringBuffer multiValStrBuf = new StringBuffer ();
                                String comma = "";

                                // loop though the values and generate the
                                // paramter value
                                for (int x = 0; x < options.length; x++)
                                {
                                    // check for emtpy option that has " " as
                                    // value
                                    if (!options[x].equals (" "))
                                    {
                                        multiValStrBuf.append (comma).append (
                                            options[x]);
                                        comma = QueryConstants.CONST_DELIMITER;
                                    } // if (! options[i].equals(" "))
                                } // for (int i = 0; i < options.length; i++)
                                // any value set now?
                                if (multiValStrBuf.length () > 0)
                                {
                                    // set the parameter value
                                    searchValue = multiValStrBuf.toString ();
                                } // if (multiValStrBuf.length() > 0)
                                else
                                // no value set
                                {
                                    // we need to set " " as value to be
                                    // compatible
                                    // with the original QUERYSELECTIONBOX
                                    // implementation
                                    // because some queries use
                                    // #SYSVAR.QUERY_searchfield# = ' '
                                    // in their expressions.
                                    // Note that this is a dirty solution!
                                    searchValue = " ";
                                } // else no value set
                            } // if (values.length > 0)
                            else
                            // no values selected
                            {
                                // set parameter value:
                                searchValue = "";
                            } // else // no values selected
                        } // if
                    } // if // multiple selection
                } // else if
                // show search value
                searchValueString += fieldName + " = " + searchValue +
                    IE302.TAG_NEWLINE;
            } // for iter
        } // if

        // show searchValues
        this.showProperty (table, QueryArguments.ARG_SEARCHVALUES,
            MultilingualTextProvider.getText (QueryTokens.TOK_BUNDLE,
                QueryTokens.ML_SEARCHVALUES, env),
            Datatypes.DT_NAME, searchValueString);
    } // showPropertiesSearch


    /**************************************************************************
     * Represent the properties of a QueryExecutive_01 object to the user
     * within a form. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     *
     * @see ibs.IbsObject#showFormProperty(TableElement, String, String, int, String)
     */
    protected void showFormProperties (TableElement table)
    {
        // show default form properties
        super.showFormProperties (table);
        // show search form:
        this.showFormProperty (table, QueryArguments.ARG_SHOWSEARCHFORM,
            MultilingualTextProvider.getText (QueryTokens.TOK_BUNDLE,
                QueryTokens.ML_SHOWSEARCHFORM, env),
            Datatypes.DT_BOOL, "" + this.p_isShowSearchForm);

        // show DOM tree:
        this.showFormProperty (
            table, DIArguments.ARG_SHOWDOMTREE,    
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_SHOWDOMTREE, env),
            Datatypes.DT_BOOL, "" + this.p_isShowDOMTree);

        this.showFormPropertiesSearch (table);
    } // showFormProperties


    /**************************************************************************
     * Check if user is administrator.  <BR/>
     * In that case add a link to open the query definition. <BR/>
     * This is done to easy coding. <BR/>
     *
     * @param actUsername       The name of current user.
     * @param queryObjectOid       The oid of the query object.
     * @param group             The groupelement to add the button to.
     */
    public void addQueryDefinitionButton (String actUsername, OID queryObjectOid,
                                  GroupElement group)
    {
        // check if user is administrator.
        // in that case add a link to open the query definition
        // this is done to easy coding
        if (actUsername != null && actUsername.equalsIgnoreCase (IOConstants.USERNAME_ADMINISTRATOR) && this.queryObjectOid != null)
        {
            group.addElement (new BlankElement ());
            group.addElement (new BlankElement ());
            group.addElement (new BlankElement ());

            HTMLButtonElement button = new HTMLButtonElement ();
            button.addLabel (
                MultilingualTextProvider.getText (QueryTokens.TOK_BUNDLE,
                    QueryTokens.ML_OPEN_QUERY_DEFINITION, env));
            button.isDisabledOnClick = false;
            button.onClick =
                IOHelpers.getShowObjectJavaScript (queryObjectOid.toString ());
            button.type = HTMLButtonElement.INP_BUTTON;
            group.addElement (button);
        } // if
    } // addQueryDefinitionButton


    /**************************************************************************
     * Show the queryattributes to be searched in. <BR/>
     * if queryexecutive has no querycreator show an selectionbox
     * with all querycreators to assign to queryexecutive. <BR/>
     *
     * THIS IS THE ONLY PLACE WHERE THIS METHOD SHOULD EXIST !!!
     * (IT IS DEPRECATED IN OTHER CLASSES !!!)
     *
     * @param   table       Table where the properties shall be added.
     */
    public void showFormPropertiesSearch (TableElement table)
    {
        // check if query template was not set already
        if (this.queryObject == null || this.state == States.ST_CREATED)
        {
            // get selection list of all report templates in system, where
            // the current user has rights on
            SelectionList queryObjectList = this.performRetrieveQueryCreatorSelectionList ();

            // show selection box for query template
            // 0 index of preselected if preselected value (queryObjectOid)
            // is not set
            this.showFormProperty (table, QueryArguments.ARG_QUERYCREATOR,
                MultilingualTextProvider.getText (QueryTokens.TOK_BUNDLE,
                    QueryTokens.ML_QUERYCREATOR, env),
                Datatypes.DT_SELECT, "", queryObjectList.ids, queryObjectList.values, 0);
        } // if querytemplate was not set already
        else
        {
            // check if query creator could be prepared
            if (!this.prepareQueryCreator ())
            {
                // if something went wrong  (maybe assigned querycreator
                // was deleted ...)
                return;
            } // if

            // show oid of querycreator in hiddenfield to get it for execution

            // show selected Querytemplate (QueryCreator)
            GroupElement group = new GroupElement ();

            SpanElement spanName = new SpanElement ();
            spanName.classId = CssConstants.CLASS_NAME;
            // if available get the multilingual name for the 'name'
            // DONE in case of IBS-733
            // TODO: Eventually the name in the QueryCreator should already be translated?!?
            String qryName = this.queryObject.name;
            String qryOrgName = qryName;
            String queryNameLookupKey = MultilingualTextProvider
                .getQueryGenericLookupKey (qryOrgName, qryOrgName, 
                    MultilangConstants.LOOKUP_KEY_POSTFIX_NAME);
            // Retrieve the ml text with the defined lookup key
            MultilingualTextInfo mlValueInfoName = MultilingualTextProvider
                .getMultilingualTextInfo (
                    MultilangConstants.RESOURCE_BUNDLE_QUERIES_NAME,
                    queryNameLookupKey,
                    MultilingualTextProvider.getUserLocale (env), env);
            if (mlValueInfoName.isFound ())
            {
                qryName = mlValueInfoName.getMLValue ();
            } // if
            spanName.addElement (new TextElement (qryName));
            group.addElement (spanName);
            // check if user is administrator.
            // in that case add a link to open the query definition
            this.addQueryDefinitionButton (this.getUser ().actUsername,
                this.queryObjectOid, group);

            // add the description if not null
            if (this.queryObject.description != null &&
                this.queryObject.description.length () > 0)
            {

                DivElement divDesc = new DivElement ();
                divDesc.alignment = IOConstants.ALIGN_LEFT;
                divDesc.classId = CssConstants.CLASS_VALUE;
                // if available get the multilingual name for the 'description'                
                // DONE in case of IBS-733
                // TODO: Eventually the name in the QueryCreator should already be translated?!?
                String qryDesc = this.queryObject.description;
                String queryDescLookupKey = MultilingualTextProvider
                    .getQueryGenericLookupKey (qryOrgName, qryOrgName, 
                        MultilangConstants.LOOKUP_KEY_POSTFIX_DESCRIPTION);
                // Retrieve the ml text with the defined lookup key
                MultilingualTextInfo mlValueInfoDesc = MultilingualTextProvider
                    .getMultilingualTextInfo (
                        MultilangConstants.RESOURCE_BUNDLE_QUERIES_NAME,
                        queryDescLookupKey,
                        MultilingualTextProvider.getUserLocale (env), env);
                if (mlValueInfoDesc.isFound ())
                {
                    qryDesc = mlValueInfoDesc.getMLValue ();
                } // if

                divDesc.addElement (IOHelpers.getTextField (qryDesc));
                group.addElement (divDesc);
            } // // add the description

            group.addElement (new NewLineElement ());

            group.addElement (new InputElement (QueryArguments.ARG_QUERYCREATOR,
                    InputElement.INP_HIDDEN, "" + this.queryObject.oid));

            RowElement tr = new RowElement (1);
            table.addElement (tr);
            TableDataElement td = new TableDataElement (group);
            tr.addElement (td);
            td.colspan = 2;
            td.classId = CssConstants.CLASS_NAME;
            td.alignment = IOConstants.ALIGN_LEFT;

            /* BB: alternative solution with showFormProperty
            showFormProperty (table,
                    QueryTokens.TOK_QUERYCREATOR,  // token
                    group);
*/
/* BB: former approach
            showProperty (table,
                      QueryArguments.ARG_QUERYCREATOR,  // Argument
                      "",  // token
                      Datatypes.DT_HIDDEN,
                      "" + this.queryObject.oid);

            showProperty (table,
                      QueryArguments.ARG_QUERYCREATORNAME,  // Argument
                      QueryTokens.TOK_QUERYCREATOR,  // token
                      Datatypes.DT_NAME,
                      this.queryObject.name);
*/

            // show searchspecific properties
            // get saved search field parameters:
            QueryExecutive_01.getSavedSearchfieldParameters (this.p_inParams,
                    this.p_searchValues, this.p_matchTypes);

            // check if there are any search parameters:
            if (this.p_inParams != null)
            {
                String searchValue = null;
                String rangeValue = null;
                String fieldName = null;
                String fieldType = null;
                String argName = null;
                int matchType = 0;
                String matchTypeValue = null;

                // walk through all search value fields and create one inputfield for each
                // loop through all possible input parameters and add a condition
                // for each of them, which was set:
                for (Iterator<QueryParameter> iter = this.p_inParams.iterator (); iter.hasNext ();)
                {
                    QueryParameter param = iter.next ();

                    // get data for current field:
                    // get multilang field name
                    fieldName = param.getMlName ();
                    // get field type
                    fieldType = param.getType ();
                    // get environment argument name
                    argName = QueryArguments.ARG_SEARCHFIELD + param.pos;
                    // get match type value
                    matchTypeValue = param.getMatchType ();
                    // check if a match type is set
                    if (matchTypeValue != null &&
                        !matchTypeValue.isEmpty () &&
                        !matchTypeValue.equalsIgnoreCase (QueryConstants.CONST_UNDEF) &&
                        !matchTypeValue.equalsIgnoreCase (QueryConstants.CONST_UNDEF2) &&
                        !matchTypeValue.equalsIgnoreCase (QueryConstants.CONST_NULL))
                    {
                        // set match type
                        matchType = Integer.parseInt (matchTypeValue);
                    } // if
                    else
                    {
                        // no match type set
                        matchType = 0;
                    } // else
/*
                        fieldAttribute = this.queryObject.getFieldQueryAttribute (i);
*/

                    // get searchvalue for current search field:
                    if (fieldType != null &&
                        !fieldType.equals (QueryConstants.FIELDTYPE_OBJECTPATH))
                    {
                        searchValue = param.getValue ();
                        rangeValue = param.getRangeValue ();
/*
                            matchValue = param.getMatchType ();
*/
                        if (searchValue == null ||
                            searchValue
                                .equalsIgnoreCase (QueryConstants.CONST_NULL))
                        {
                            searchValue = "";
                        } // if

                        if (rangeValue == null ||
                            rangeValue.equalsIgnoreCase (QueryConstants.CONST_NULL))
                        {
                            rangeValue = "";
                        } // if
                    } // if
                    else
                    {
                        searchValue = "";
                        rangeValue = "";
                    } // else

    // showformsearchproperty (type, name, value)

                    // show search fields - check field type
                    if (fieldType != null)
                    {
                        // show selection for root object of search
                        // if FIELDTYPE_OBJECTPATH is used, show selection for root object of query
                        if (fieldType.equals (QueryConstants.FIELDTYPE_OBJECTPATH))
                        {
                            // if rootObjectOid is set to predefined value, use this oid
                            // as root oid
                            if (this.rootObjectOid != null &&
                                !this.rootObjectOid.isEmpty ())
                            {
                                // show selected Querytemplate (QueryCreator)
                                this.showProperty (table,
                                          QueryArguments.ARG_ROOTOBJECTSELECTION,
                                          fieldName,  // token
                                          Datatypes.DT_HIDDEN,
                                          "" + this.rootObjectOid);
                            } // if rootObject is set to predefined value
                            else
                                // else rootObject is not set to predefined value,
                                // show selection box
                            {
                                // count of fix selections in object path - selection box.
                                int fixSelections = 0;


                                // if search in current container is possible fixSelections are 2
                                // (for global and current-container-search) otherwise 1 (only for global search)
                                if (this.currentObjectOid != null &&
                                    !this.currentObjectOid.isEmpty () &&
                                    !this.currentObjectOid.isTemp ()) // container has to be physical
                                {
                                    fixSelections = 2;
                                } // if
                                else
                                {
                                    fixSelections = 1;
                                } // else

                                int j = 0;

                                // get count of menu tabs add 2 (for entries *global and *current container
                                int menuLength = this.sess.menus.size () + fixSelections;

                                String[] selectNames = new String [menuLength];
                                String[] selectNameIds = new String [menuLength];

                                selectNames [j] = 
                                    MultilingualTextProvider.getText (QueryTokens.TOK_BUNDLE,
                                        QueryTokens.ML_SEARCHGLOBAL, env);
                                selectNameIds [j++] = OID.EMPTYOID;


                                // if search in current container is possible
                                if (this.currentObjectOid != null &&
                                    !this.currentObjectOid.isEmpty () &&
                                    !this.currentObjectOid.isTemp ()) // container has to be physical
                                {
                                    // add token for local-search to selectionbox
                                    selectNames [j] = 
                                        MultilingualTextProvider.getText (QueryTokens.TOK_BUNDLE,
                                            QueryTokens.ML_SEARCHLOCAL, env);
                                    selectNameIds [j++] = "" + this.currentObjectOid;
                                } // if

                                MenuData_01 help = new MenuData_01 ();

                                // fill names and oids of menutabs in selectionlist
                                for (; j < menuLength; j++)
                                {
                                    help = this.sess.menus.elementAt (j - fixSelections);
                                    selectNames [j] = help.getMlName (env);
                                    selectNameIds [j] = "" + help.oid;
                                } // for


                                // show selection field for root object of search
                                this.showFormProperty (table,
                                                QueryArguments.ARG_ROOTOBJECTSELECTION,
                                                fieldName,
                                                Datatypes.DT_SELECT,
                                                "" + this.rootObjectOid,
                                                selectNameIds,
                                                selectNames,
                                                0 // index of preselected if preseclected value if (rootObjectOid) is not set
                                );
                            } // else rootObject is not set to predefined value,
                              // show selectionbox
                        } // if FIELDTYPE_OBJECTPATH is used - show selection for root object

                        // FIELDTYPE_MONEY
                        else if (fieldType.equals (QueryConstants.FIELDTYPE_MONEY))
                        {
                            // show inputfield for special searchvalue for valuetype MONEY
                            // and set matchtype value
                            this.showFormProperty (table, // table
                                          argName,  // argument
                                          fieldName,  // token
                                          Datatypes.DT_SEARCHMONEY, // field type
                                          searchValue,    // search value
                                          matchType);    // match type value
                        } // if money
                        // FIELDTYPE_<CONDITION>_MONEY
                        else if (
                            fieldType.equals (QueryConstants.FIELDTYPE_LESS_MONEY) ||
                            fieldType.equals (QueryConstants.FIELDTYPE_LESS_EQUAL_MONEY) ||
                            fieldType.equals (QueryConstants.FIELDTYPE_GREATER_MONEY) ||
                            fieldType.equals (QueryConstants.FIELDTYPE_GREATER_EQUAL_MONEY))
                        {
                            String matchtype = SQLConstants.MATCH_NONE;

                            // check which match type should be set for condition field
                            if (fieldType.equals (QueryConstants.FIELDTYPE_LESS_MONEY))
                            {
                                matchtype = SQLConstants.MATCH_LESS;
                            } // if
                            else if (fieldType.equals (QueryConstants.FIELDTYPE_LESS_EQUAL_MONEY))
                            {
                                matchtype = SQLConstants.MATCH_LESSEQUAL;
                            } // else if
                            else if (fieldType.equals (QueryConstants.FIELDTYPE_GREATER_MONEY))
                            {
                                matchtype = SQLConstants.MATCH_GREATER;
                            } // else if
                            else if (fieldType.equals (QueryConstants.FIELDTYPE_GREATER_EQUAL_MONEY))
                            {
                                matchtype = SQLConstants.MATCH_GREATEREQUAL;
                            } // else if

                            // show inputfield for special searchvalue for valuetype MONEY
                            this.showFormProperty (table,
                                          argName,  // Argument
                                          fieldName,  // token
                                          Datatypes.DT_MONEY,
                                          searchValue);

                            // show fixed match type due to field type
                            this.showProperty (table,
                                      argName +
                                        BOArguments.ARG_MATCHTYPE_EXTENSION,  // Argument
                                      "",  // token
                                      Datatypes.DT_HIDDEN,
                                      matchtype);
                        } // else if condition money
                        // FIELDTYPE_MONEYRANGE
                        else if (fieldType.equals (QueryConstants.FIELDTYPE_MONEYRANGE))
                        {
                            // show inputfield for special searchvalue for
                            // valuetype MONEYRANGE
                            this.showFormProperty (table,
                                          argName,  // Argument
                                          fieldName,  // token
                                          Datatypes.DT_MONEYRANGE,
                                          searchValue);
                        } // if MONEYRANGE

                        // FIELDTYPE_DATE
                        else if (fieldType.equals (QueryConstants.FIELDTYPE_DATE))
                        {
                            // show inputfield for special searchvalue for valuetype DATE
                            // and set matchtype value
                            this.showFormProperty (table, // table
                                          argName,  // argument
                                          fieldName,  // token
                                          Datatypes.DT_SEARCHDATE, // field type
                                          searchValue,    // search value
                                          matchType);    // match type value
                        } // if date
                        // FIELDTYPE_<CONDITION>_DATE
                        else if (
                            fieldType.equals (QueryConstants.FIELDTYPE_LESS_DATE) ||
                            fieldType.equals (QueryConstants.FIELDTYPE_LESS_EQUAL_DATE) ||
                            fieldType.equals (QueryConstants.FIELDTYPE_GREATER_DATE) ||
                            fieldType.equals (QueryConstants.FIELDTYPE_GREATER_EQUAL_DATE))
                        {
                            String matchtype = SQLConstants.MATCH_NONE;

                            // check which match type should be set for condition field
                            if (fieldType.equals (QueryConstants.FIELDTYPE_LESS_DATE))
                            {
                                matchtype = SQLConstants.MATCH_LESS;
                            } // if
                            else if (fieldType.equals (QueryConstants.FIELDTYPE_LESS_EQUAL_DATE))
                            {
                                matchtype = SQLConstants.MATCH_LESSEQUAL;
                            } // else if
                            else if (fieldType.equals (QueryConstants.FIELDTYPE_GREATER_DATE))
                            {
                                matchtype = SQLConstants.MATCH_GREATER;
                            } // else if
                            else if (fieldType.equals (QueryConstants.FIELDTYPE_GREATER_EQUAL_DATE))
                            {
                                matchtype = SQLConstants.MATCH_GREATEREQUAL;
                            } // else if

                            // show input field for special searchvalue for valuetype DATE
                            this.showFormProperty (table,
                                          argName,  // Argument
                                          fieldName,  // token
                                          Datatypes.DT_DATE,
                                          searchValue);

                            // show fixed match type due to field type
                            this.showFormProperty (table,
                                      argName +
                                        BOArguments.ARG_MATCHTYPE_EXTENSION,  // Argument
                                      "",  // token
                                      Datatypes.DT_HIDDEN,
                                      matchtype);
                        } // else if condition date
                        // FIELDTYPE_TIME
                        else if (fieldType.equals (QueryConstants.FIELDTYPE_TIME))
                        {
                            // show inputfield for special searchvalue for valuetype TIME
                            // and set matchtype value
                            this.showFormProperty (table, // table
                                          argName,  // argument
                                          fieldName,  // token
                                          Datatypes.DT_SEARCHTIME, // field type
                                          searchValue,    // search value
                                          matchType);    // match type value
                        } // if time
                        // FIELDTYPE_<CONDITION>_TIME
                        else if (
                            fieldType.equals (QueryConstants.FIELDTYPE_LESS_TIME) ||
                            fieldType.equals (QueryConstants.FIELDTYPE_LESS_EQUAL_TIME) ||
                            fieldType.equals (QueryConstants.FIELDTYPE_GREATER_TIME) ||
                            fieldType.equals (QueryConstants.FIELDTYPE_GREATER_EQUAL_TIME))
                        {
                            String matchtype = SQLConstants.MATCH_NONE;

                            // check which match type should be set for condition field
                            if (fieldType.equals (QueryConstants.FIELDTYPE_LESS_TIME))
                            {
                                matchtype = SQLConstants.MATCH_LESS;
                            } // if
                            else if (fieldType.equals (QueryConstants.FIELDTYPE_LESS_EQUAL_TIME))
                            {
                                matchtype = SQLConstants.MATCH_LESSEQUAL;
                            } // else if
                            else if (fieldType.equals (QueryConstants.FIELDTYPE_GREATER_TIME))
                            {
                                matchtype = SQLConstants.MATCH_GREATER;
                            } // else if
                            else if (fieldType.equals (QueryConstants.FIELDTYPE_GREATER_EQUAL_TIME))
                            {
                                matchtype = SQLConstants.MATCH_GREATEREQUAL;
                            } // else if

                            // show inputfield for special searchvalue for valuetype TIME
                            this.showFormProperty (table,
                                          argName,  // Argument
                                          fieldName,  // token
                                          Datatypes.DT_TIME,
                                          searchValue);

                            // show fixed match type due to field type
                            this.showFormProperty (table,
                                      argName +
                                        BOArguments.ARG_MATCHTYPE_EXTENSION,  // Argument
                                      "",  // token
                                      Datatypes.DT_HIDDEN,
                                      matchtype);
                        } // else if condition time
                        // FIELDTYPE_DATETIME
                        else if (fieldType.equals (QueryConstants.FIELDTYPE_DATETIME))
                        {
                            // show inputfield for special searchvalue for valuetype DATE
                            // and set matchtype value
                            this.showFormProperty (table, // table
                                          argName,  // argument
                                          fieldName,  // token
                                          Datatypes.DT_SEARCHDATETIME, // field type
                                          searchValue,    // search value
                                          matchType);    // match type value
                        } // if datetime
                        // FIELDTYPE_DATERANGE
                        else if (fieldType.equals (QueryConstants.FIELDTYPE_DATERANGE))
                        {
                            // show inputfield for special searchvalue for valuetype DATERANGE
                            this.showFormProperty (table,
                                          argName,  // Argument
                                          fieldName,  // token
                                          Datatypes.DT_DATERANGE,
                                          DateTimeHelpers.stringToDate (searchValue),
                                          DateTimeHelpers.stringToDate (rangeValue));
                        } // if daterange
                        // FIELDTYPE_TIMERANGE
                        else if (fieldType.equals (QueryConstants.FIELDTYPE_TIMERANGE))
                        {
                            // show inputfield for special searchvalue for valuetype TIMERANGE
                            // replace <undef> entries for undefined values with ""
                            this.showFormProperty (table,
                                          argName,  // Argument
                                          fieldName,  // token
                                          Datatypes.DT_TIMERANGE,
                                          DateTimeHelpers.stringToTime (searchValue),
                                          DateTimeHelpers.stringToTime (rangeValue));
                        } // if timerange
                        // FIELDTYPE_DATETIMERANGE
                        else if (fieldType.equals (QueryConstants.FIELDTYPE_DATETIMERANGE))
                        {
                            // show inputfield for special searchvalue for valuetype DATETIMERANGE
                            this.showFormProperty (table,
                                          argName,  // Argument
                                          fieldName,  // token
                                          Datatypes.DT_DATETIMERANGE,
                                          DateTimeHelpers.stringToDateTime (searchValue),
                                          DateTimeHelpers.stringToDateTime (rangeValue));
                        } // if datetimerange

                        // FIELDTYPE_INTEGER
                        else if (fieldType.equals (QueryConstants.FIELDTYPE_INTEGER))
                        {
                            // show inputfield for special searchvalue for valuetype NUMBER
                            // and set matchtype value
                            this.showFormProperty (table, // table
                                          argName,  // argument
                                          fieldName,  // token
                                          Datatypes.DT_SEARCHTEXT, // field type
                                          searchValue,    // search value
                                          matchType);    // match type value
                        } // if INTEGER
                        // FIELDTYPE_<CONDITION>_INTEGER
                        else if (
                            fieldType.equals (QueryConstants.FIELDTYPE_LESS_INTEGER) ||
                            fieldType.equals (QueryConstants.FIELDTYPE_LESS_EQUAL_INTEGER) ||
                            fieldType.equals (QueryConstants.FIELDTYPE_GREATER_INTEGER) ||
                            fieldType.equals (QueryConstants.FIELDTYPE_GREATER_EQUAL_INTEGER))
                        {
                            String matchtype = SQLConstants.MATCH_NONE;

                            // check which match type should be set for condition field
                            if (fieldType.equals (QueryConstants.FIELDTYPE_LESS_INTEGER))
                            {
                                matchtype = SQLConstants.MATCH_LESS;
                            } // if
                            else if (fieldType.equals (QueryConstants.FIELDTYPE_LESS_EQUAL_INTEGER))
                            {
                                matchtype = SQLConstants.MATCH_LESSEQUAL;
                            } // else if
                            else if (fieldType.equals (QueryConstants.FIELDTYPE_GREATER_INTEGER))
                            {
                                matchtype = SQLConstants.MATCH_GREATER;
                            } // else if
                            else if (fieldType.equals (QueryConstants.FIELDTYPE_GREATER_EQUAL_INTEGER))
                            {
                                matchtype = SQLConstants.MATCH_GREATEREQUAL;
                            } // else if

                            // show inputfield for special searchvalue for valuetype INTEGER
                            this.showFormProperty (table,
                                          argName,  // Argument
                                          fieldName,  // token
                                          Datatypes.DT_INTEGER,
                                          searchValue);

                            // show fixed match type due to field type:
                            this.showFormProperty (table,
                                      argName +
                                        BOArguments.ARG_MATCHTYPE_EXTENSION,  // Argument
                                      "",  // token
                                      Datatypes.DT_HIDDEN,
                                      matchtype);
                        } // else if condition INTEGER
                        // FIELDTYPE_INTEGERRANGE
                        else if (fieldType.equals (QueryConstants.FIELDTYPE_INTEGERRANGE))
                        {                            // show inputfield for special searchvalue for
                            // valuetype INTEGERRANGE:
                            this.showFormProperty (table,
                                          argName,  // Argument
                                          fieldName,  // token
                                          Datatypes.DT_INTEGERRANGE,
                                          searchValue,
                                          rangeValue);
                        } // if INTEGERRANGE

                        // FIELDTYPE_NUMBER
                        else if (fieldType.equals (QueryConstants.FIELDTYPE_NUMBER))
                        {
                            // show inputfield for special searchvalue for valuetype NUMBER
                            // and set matchtype value
                            this.showFormProperty (table, // table
                                          argName,  // argument
                                          fieldName,  // token
                                          Datatypes.DT_SEARCHNUMBER, // field type
                                          searchValue,    // search value
                                          matchType);    // match type value
                        } // if number
                        // FIELDTYPE_<CONDITION>_NUMBER
                        else if (
                            fieldType.equals (QueryConstants.FIELDTYPE_LESS_NUMBER) ||
                            fieldType.equals (QueryConstants.FIELDTYPE_LESS_EQUAL_NUMBER) ||
                            fieldType.equals (QueryConstants.FIELDTYPE_GREATER_NUMBER) ||
                            fieldType.equals (QueryConstants.FIELDTYPE_GREATER_EQUAL_NUMBER))
                        {
                            String matchtype = SQLConstants.MATCH_NONE;

                            // check which match type should be set for condition field
                            if (fieldType.equals (QueryConstants.FIELDTYPE_LESS_NUMBER))
                            {
                                matchtype = SQLConstants.MATCH_LESS;
                            } // if
                            else if (fieldType.equals (QueryConstants.FIELDTYPE_LESS_EQUAL_NUMBER))
                            {
                                matchtype = SQLConstants.MATCH_LESSEQUAL;
                            } // else if
                            else if (fieldType.equals (QueryConstants.FIELDTYPE_GREATER_NUMBER))
                            {
                                matchtype = SQLConstants.MATCH_GREATER;
                            } // else if
                            else if (fieldType.equals (QueryConstants.FIELDTYPE_GREATER_EQUAL_NUMBER))
                            {
                                matchtype = SQLConstants.MATCH_GREATEREQUAL;
                            } // else if

                            // show inputfield for special searchvalue for valuetype NUMBER
                            this.showFormProperty (table,
                                          argName,  // Argument
                                          fieldName,  // token
                                          Datatypes.DT_NUMBER,
                                          searchValue);

                            // show fixed match type due to field type:
                            this.showFormProperty (table,
                                      argName +
                                        BOArguments.ARG_MATCHTYPE_EXTENSION,  // Argument
                                      "",  // token
                                      Datatypes.DT_HIDDEN,
                                      matchtype);
                        } // else if condition number
                        // FIELDTYPE_NUMBERRANGE
                        else if (fieldType.equals (QueryConstants.FIELDTYPE_NUMBERRANGE))
                        {                            // show inputfield for special searchvalue for
                            // valuetype NUMBERRANGE:
                            this.showFormProperty (table,
                                          argName,  // Argument
                                          fieldName,  // token
                                          Datatypes.DT_NUMBERRANGE,
                                          searchValue,
                                          rangeValue);
                        } // if NUMBERRANGE

                        // FIELDTYPE_STRING
                        else if (
                            fieldType.equals (QueryConstants.FIELDTYPE_STRING) ||
                            fieldType.equals (QueryConstants.FIELDTYPE_LONGTEXT))
                        {
                            // show inputfield for special searchvalue for valuetype STRING
                            // and set matchtype value
                            this.showFormProperty (table, // table
                                          argName,  // argument
                                          fieldName,  // token
                                          Datatypes.DT_SEARCHTEXT, // field type
                                          searchValue,    // search value
                                          matchType);    // match type value
                        } // else if string
                        // FIELDTYPE_BOOLEAN
                        else if (fieldType.equals (QueryConstants.FIELDTYPE_BOOLEAN))
                        {
                            // show inputfield for special searchvalue for valuetype BOOLEAN
                            this.showFormProperty (table,
                                    argName,
                                    fieldName,
                                    Datatypes.DT_EMPTYBOOL,
                                    searchValue);
                        } // else if boolean
                        // FIELDTYPE_VALUEDOMAIN
                        else if (fieldType.startsWith (QueryConstants.FIELDTYPE_VALUEDOMAIN))
                        {
                            // name of the context that defines the container
                            // of the VALUEDOMAINELEMENTS, which will filled to
                            // the SELECTIONBOX
                            String context = null;

                            // field type modifier string
                            String modifier = null;

                            // get the context name from the fieldType
                            // and check if it is valid
                            if (fieldType.endsWith (")") &&
                                    (modifier = fieldType
                                        .substring (fieldType.indexOf ("(") + 1,
                                            fieldType.length () - 1)).length () > 0 &&
                                            (context = this.getContextFromFieldTypeModifier (modifier)) != null)
                            {
                                // id value type
                                String idType = null;

                                if (fieldType.startsWith (QueryConstants.FIELDTYPE_VALUEDOMAINOID))
                                {
                                    idType = DIConstants.IDTYPE_OBJECTID;
                                } // if
                                else if (fieldType.startsWith (QueryConstants.FIELDTYPE_VALUEDOMAINNUM))
                                {
                                    idType = DIConstants.IDTYPE_NUMBER;
                                } // else if
                                else if (fieldType.startsWith (QueryConstants.FIELDTYPE_VALUEDOMAIN))
                                {
                                    idType = DIConstants.IDTYPE_STRING;
                                } // else if

                                // show SELECTIONBOX filled with query data
                                this.showFormProperty (table,
                                        argName,
                                        fieldName,
                                        Datatypes.DT_VALUEDOMAIN,
                                        searchValue,
                                        idType,
                                        true,
                                        context);
                            } // if
                            // there is no context name
                            else
                            {
                                // show error message for invalid field type
                                this.showProperty (table,
                                              QueryArguments.ARG_FIELDTYPEERROR,  // Argument
                                              fieldName,  // token
                                              Datatypes.DT_NAME,
                                              QueryConstants.EXC_WRONGFIELDTYPE);
                            } // else
                        } // else if boolean
                        // FIELDTYPE_QUERYSELECTION
                        // FIELDTYPE_QUERYSELECTIONOID
                        // FIELDTYPE_QUERYSELECTIONNUM
                        else if (
                                fieldType.startsWith (QueryConstants.FIELDTYPE_QUERYSELECTION) ||
                                fieldType.startsWith (QueryConstants.FIELDTYPE_QUERYSELECTIONOID) ||
                                fieldType.startsWith (QueryConstants.FIELDTYPE_QUERYSELECTIONNUM))
                        {
                            // note that a QUERYSELECTIONBOX supports the
                            // the following parameters within brackets
                            // QUERYSELECTIONBOX (queryname; MULTIPLE)
                            String paramStr = fieldType.substring (fieldType
                                .indexOf ("(") + 1, fieldType.indexOf (")"));
                            // if there is a valid query name
                            if (paramStr != null && paramStr.length () > 1)
                            {
                                String queryName;
                                String addParam;
                                boolean isMultiple = false;
                                int pos;
                                // the queryname must the first parameter
                                // if the paramStr does contain a ";"
                                // we have additional paramters
                                if ((pos = paramStr.indexOf (";")) != -1)
                                {
                                    queryName = paramStr.substring (0, pos).trim ();
                                    addParam = paramStr.substring (pos);
                                    // check if multiple is set
                                    isMultiple = addParam.indexOf (QueryConstants.QUERYSELECTIONBOX_PARAM_MULTIPLE) != -1;
                                    // add any additional parameters here
                                    // ...
                                } // if (paramStr.indexOf(";") != -1)
                                else    // no additional parameters set
                                {
                                    queryName = paramStr.trim ();
                                } // else no additional parameters set

                                String idType = null;

                                if (fieldType.startsWith (QueryConstants.FIELDTYPE_QUERYSELECTIONOID))
                                {
                                    idType = DIConstants.IDTYPE_OBJECTID;
                                } // if
                                else if (fieldType.startsWith (QueryConstants.FIELDTYPE_QUERYSELECTIONNUM))
                                {
                                    idType = DIConstants.IDTYPE_NUMBER;
                                } // else if
                                else if (fieldType.startsWith (QueryConstants.FIELDTYPE_QUERYSELECTION))
                                {
                                    idType = DIConstants.IDTYPE_STRING;
                                } // else if

                                // check if string contains "'"?
                                if (searchValue.indexOf ("'") != -1)
                                {
                                    // replace "'"
                                    searchValue = StringHelpers.replace (
                                        searchValue, "'", "");
                                } // if
                                // show SELECTIONBOX filled with query data
                                this.showFormProperty (table, argName, fieldName,
                                                  Datatypes.DT_QUERYSELECTIONBOX,
                                                  queryName,
                                                  searchValue,
                                                  idType, true, isMultiple);

                                // show fixed match type due to field type
                                this.showFormProperty (table,
                                      argName +
                                        BOArguments.ARG_MATCHTYPE_EXTENSION,  // Argument
                                      "",  // token
                                      Datatypes.DT_HIDDEN,
                                      SQLConstants.MATCH_EXACT);
                            } // if there is a valid query name
                            else                // there is no query name
                            {
                                // show error message for invalid field type
                                this.showProperty (table,
                                              QueryArguments.ARG_FIELDTYPEERROR,  // Argument
                                              fieldName,  // token
                                              Datatypes.DT_NAME,
                                              QueryConstants.EXC_WRONGFIELDTYPE);
                            } // else
                        } // else if query selection
                        // NOT_VALID
                        else
                        {
                            // show error message for invalid field type
                            this.showProperty (table,
                                          QueryArguments.ARG_FIELDTYPEERROR,  // Argument
                                          fieldName,  // token
                                          Datatypes.DT_NAME,
                                          QueryConstants.EXC_WRONGFIELDTYPE);
                        } // else not valid
                    } // if fieldType != null
    // showformsearchproperty end
                } // for iter
            } // if
        } // if
    } // showFormPropertiesSearch


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in a
     * container's content view. <BR/>
     * This method can be overwritten in subclasses to redefine the set of
     * buttons that can be displayed. <BR/>
     *
     * @return  An array with button ids that can potentially be displayed.
     */
    protected int[] setContentButtons ()
    {
        // local variables
        int[] buttons = null;

        // this class is used as BusinessObject (Report - QueryExecutive)
        // and as service (enhanced search) - if it is used as
        // service, only the tab CONTENT should be displayed.
        if (this.oid != null && this.oid.isTemp ())
        {
            // define buttons to be displayed:
            buttons = new int[]
            {
                Buttons.BTN_BACK,
            }; // buttons
        } // if
        else
        {
            // define buttons to be displayed:
            buttons = new int[]
            {
                Buttons.BTN_SEARCH,
                Buttons.BTN_LISTDELETE,
                Buttons.BTN_LIST_COPY,
                Buttons.BTN_LIST_CUT,
                Buttons.BTN_DISTRIBUTE,
            }; // buttons
        } // else

        // return button array
        return buttons;
    } // setContentButtons


    /**************************************************************************
     * Get a selectionList with queryCreators which should be
     * selectable in advanced search out of the database. <BR/>
     *
     * @return  ???
     */
    protected SelectionList performRetrieveQueryCreatorSelectionList ()
    {
        StringBuffer queryStr;          // the query string

        // get the elements out of the database:
        // create the SQL String to select all tuples
        queryStr = new StringBuffer ()
            .append ("SELECT distinct")
            .append (" s.oid, s.name")
            .append (" FROM ").append (this.viewSelectionList).append (" s, ")
            .append ("ibs_QueryCreator_01 q")
            .append (" WHERE   s.userId = ").append (this.user.id)
            .append (SQLHelpers.getStringCheckRights (Operations.OP_VIEW))
/* KR not necessary because of oid join
            + "    AND s.tVersionId IN (" +
            getCache ().getTVersionId (Types.TC_QueryCreator) + ", " +
            getCache ().getTVersionId (Types.TC_DBQueryCreator) + ")";
*/
            // get only queryCreators which should be selectable for advanced search
            .append (" AND ")
            .append (SQLHelpers.getBitAnd ("q.queryType", "2"))
            .append (" = 2")
            .append (" AND s.oid = q.oid ")
            .append (" ORDER BY name ").append (BOConstants.ORDER_ASC);

        return this.performRetrieveSelectionListDataQuery (false, queryStr.toString ());
    } // performRetrieveQueryCreatorSelectionList


    /**************************************************************************
     * tokenize a string with delimiter QueryConstants.CONST_DELIMITER.
     *
     * @param   values  string to be tokenized
     *
     * @return  StringTokenizer with tokens of given string.
     */
    protected StringTokenizer tokenizeString (String values)
    {
        StringTokenizer tokenizer = null;

        // check if there are values:
        if (values != null &&
            values.length () > 0 &&
            !values.equals (" "))
        {
            tokenizer = new StringTokenizer (values,
                QueryConstants.CONST_DELIMITER);
        } // if

        return tokenizer;
    } // tokenizeString


    //
    // import / export methods
    //
    /**************************************************************************
     * Reads the object data from a data element. <BR/>
     *
     * @param dataElement     the dataElement to read the data from
     */
    public void readImportData (DataElement dataElement)
    {
        String queryObjectOidStr = null;

        // get business object specific values
        super.readImportData (dataElement);
        // get the type specific values
        if (dataElement.exists (QueryExecutive_01.FIELD_QUERYOBJECTOID))
        {
            try
            {
                queryObjectOidStr =
                    dataElement.getImportStringValue (QueryExecutive_01.FIELD_QUERYOBJECTOID);
                this.queryObjectOid = new OID (queryObjectOidStr);
            } // try
            catch (IncorrectOidException e)
            {
                IOHelpers.printError ("readImportData: Incorrect oid " + queryObjectOidStr, this, e, true);
            } // catch
        } // if
        if (dataElement.exists (QueryExecutive_01.FIELD_QUERYOBJECTNAME))
        {
            this.queryObjectName =
                dataElement.getImportStringValue (QueryExecutive_01.FIELD_QUERYOBJECTNAME);
        } // if

        if (dataElement.exists (QueryExecutive_01.FIELD_SHOWSEARCHFORM))
        {
            this.p_isShowSearchForm =
                dataElement.getImportBooleanValue (QueryExecutive_01.FIELD_SHOWSEARCHFORM);
        } // if

        // show DOM tree:
        if (dataElement.exists (this.FIELD_SHOWDOMTREE))
        {
            this.p_isShowDOMTree =
                dataElement.getImportBooleanValue (this.FIELD_SHOWDOMTREE);
        } // if

        // prepare querycreator with given oid or name and with rootOid
        this.prepareQueryCreator ();
    } // readImportData


    /**************************************************************************
     * Writes the object data to a data element. <BR/>
     *
     * @param dataElement     the dataElement to write the data to
     */
    public void writeExportData (DataElement dataElement)
    {
        // set the business object specific values
        super.writeExportData (dataElement);

        // set the type specific values:
        if (this.queryObjectOid != null)
        {
            dataElement.setExportValue (QueryExecutive_01.FIELD_QUERYOBJECTOID,
                this.queryObjectOid.toString ());
        } // if

        if (this.queryObjectName != null)
        {
            dataElement.setExportValue (
                QueryExecutive_01.FIELD_QUERYOBJECTNAME, this.queryObjectName);
        } // if
        else if (this.queryObject != null)
        {
            dataElement.setExportValue (
                QueryExecutive_01.FIELD_QUERYOBJECTNAME, this.queryObject.name);
        } // if

        // show search form:
        dataElement.setExportValue (QueryExecutive_01.FIELD_SHOWSEARCHFORM,
            this.p_isShowSearchForm);

        // show DOM tree:
        dataElement.setExportValue (this.FIELD_SHOWDOMTREE,
            this.p_isShowDOMTree);
    } // writeExportData


    /**************************************************************************
     * Export an object and all his document and plan subobjects using
     * the EDIAKT format. <BR/>
     * Note that this method has been prepared to be called from a workflow. <BR/>
     */
    public void performDownload ()
    {
        // store the original value:
        int origResultCounter = this.queryObject.getResultCounter ();

        try
        {
            // reset the ResultCounter in order to force
            // reading the complete query result
            this.queryObject.setResultCounter (0);

            // try to retrieve the content of this container:
            this.retrieveContent (Operations.OP_VIEW, this.orderBy, this.orderHow);
        } // try
        catch (NoAccessException e) // no access to objects allowed
        {
            // send message to the user:
            this.showNoAccessMessage (Operations.OP_VIEWELEMS);
        } // catch
    
        // create xslt output:
        String xslOutput = this.showContentXSLDownload ();
     
        // restore the max counter
        this.queryObject.setResultCounter (origResultCounter);

        try
        {
            // check if there was an output created:
            if (xslOutput != null)
            {
                // retrieve the properly encoded byte[] 
                byte[] xslOutputData = xslOutput.toString().getBytes(DIConstants.CHARACTER_ENCODING_XLS);
    
                // write the output to the stream:
                this.env.setContentType ("text/plain charset=" + DIConstants.CHARACTER_ENCODING_XLS);
    //            this.env.setContentType ("application/msexcel");
    //            this.env.setContentType ("application/octet-stream");
    //            this.env.setContentType ("application/acad");
    //            this.env.setContentType ("application/x-www-form-urlencoded");
                this.env.addHeaderEntry ("Content-Length", xslOutputData.length + "");
                this.env.addHeaderEntry ("Accept-Ranges", "Bytes");
                this.env.addHeaderEntry (HttpConstants.HTTP_HEADER_FILENAME,
                    this.queryObjectName + ".xls");
                this.env.addHeaderEntry ("Cache-Control", "no-cache");
                this.env.addHeaderEntry ("Pragma", "no-cache");
                this.env.write (xslOutputData, DIConstants.BYTE_ORDER_MARK_XLS);
            } // if
            else                            // could not generate any output
            {
                // display a popup message.
                // BB TODO: must de replaced by a message token
                // "Suchergebnis konnte nicht gespeichert werden"
                this.showPopupMessage ("Could not save search result.");
            } // could not generate any output
        } // try
        catch (UnsupportedEncodingException e)
        {
            this.showPopupMessage ("Error while setting the file encoding: " + e.toString ());
        } // catch
    } // performDownload


    /**************************************************************************
     * This method is the interface for a reporting engine.
     * Note that the query string will be generated first and than
     * submitted to the reporting engine using a queryString. <BR/>
     */
    public void openReport ()
    {
        StringBuffer queryStr;

        // check first if a reporting engine is present
        if (this.app.hasReportingEngine ())
        {
            QueryExecutive qe = new QueryExecutive ();  // for query execution
            qe.initObject (this.oid, this.user, this.env, this.sess, this.app);
            // if object is not physical, it was not retrieved, so
            // the preparation of querycreator is not necessary
            // - the data for retrieveContent is already prepared in
            // getParameters.
            if (!this.oid.isTemp () || this.queryObject == null)
            {
                // try to get QueryCreator with the oid this.queryObjectOid
                if (!this.prepareQueryCreator ())
                {
                    // TODO: use a multilanguage token
                    this.showPopupMessage ("Could not generate query");
                } // if (!prepareQueryCreator ())
            } // if (!this.oid.isTemp () || this.queryObject == null)
            this.setQueryExecutiveInputParameters (qe);
            // generate the url string
            queryStr = qe.getQuery (this.queryObject);

            try
            {
                // not open the report with the queryStr
                this.app.getReportingEngine ().openReport (
                    this.queryObjectName, queryStr.toString (), this.env);
            } // try
            catch (ReportingException e)
            {
                // TODO: use a multilanguage token
                this.showPopupMessage ("Error while opening reportingEngine: " + e.toString ());
            } // catch (ReportingException e)
        } // if (this.app.hasReportingEngine ())
        else // reporting engine not availabel present
        {
            // Note that this case is unlikely because
            // the "Report" Button will only be shown in case
            // an ReportingEngine is present but it can happen
            // in case someone deactivated or changed the reporting engine
            // configuration
            // generate javascript code to close the opened window ();
            this.env.write ("<HTML><BODY>");
            this.env.write ("<SCRIPT LANGUAGE=\"Javascript\">");
            // TODO: use a multilanguage token
            this.env.write ("alert ('Reporting engine has been deactivated!');");
            this.env.write ("window.close ();");
            this.env.write ("</SCRIPT></BODY></HTML>");
        } // else reporting engine not availabel present
    } // showReport


    /**************************************************************************
     * Check if the reporting engine supports the query with the given query
     * name and display a report button. <BR/>
     *
     * @param queryObjectName   The name of the query object.
     * @param grButtons         The groupelement to add the button to.
     */
    private void addReportButton (String queryObjectName,
                                  GroupElement grButtons)
    {
        // check if a reporting engine is present and of a report
        // exists for the given queryObjektName
        if (this.app.hasReportingEngine () &&
            this.app.getReportingEngine ().isReportDefined (queryObjectName))
        {
            // remove the spaces in the query object name that is used as window
            // name:
            String windowName = StringHelpers.replace (queryObjectName, " ", "");

            // remove all non word characters:
            windowName = windowName.replaceAll ("\\W", "");

            grButtons.addElement (new BlankElement ());
            grButtons.addElement (new BlankElement ());
            grButtons.addElement (new BlankElement ());

            HTMLButtonElement reportButton =
                new HTMLButtonElement ("BUTT_REPORT", HTMLButtonElement.INP_BUTTON);
            reportButton.onClick =
                "var target = this.form.target; " +
                "var fct = this.form.fct.value;" +
                "var reportWin = window.open ('', " +
                "'" + windowName + "','" +
                "location=no,menubar=no,scrollbars=yes,directories=no,toolbar=no,status=no,resizable=yes" +
                "');" +
                "reportWin.focus ();" +
                "this.form.target = reportWin.name;" +
                "this.form.fct.value = " + AppFunctions.FCT_GENERATEREPORT + ";" +
                "this.form.submit ();" +
                "this.form.target = target; this.form.fct.value = fct;";
            reportButton.addLabel (
                MultilingualTextProvider.getText (QueryTokens.TOK_BUNDLE,
                    QueryTokens.ML_OPEN_REPORT, env));
            reportButton.title = 
                MultilingualTextProvider.getText (QueryTokens.TOK_BUNDLE,
                    QueryTokens.ML_OPEN_REPORT_TITLE, env);

            grButtons.addElement (reportButton);
        } // if (this.app.hasReportingEngine () && ...
    } // addReportButton


    /**************************************************************************
     * Check if the system provides a download stylesheet and add the
     * corresponding download button. <BR/>
     *
     * @param queryObjectName   The name of the query object.
     * @param grButtons         The groupelement to add the button to.
     */
    private void addDownloadButton (String queryObjectName,
                                    GroupElement grButtons)
    {
        // CONSTRAINT: a queryObjectName must be given
        if (queryObjectName != null && queryObjectName.length () > 0)
        {
            String xslFile = this.app.p_system.p_m2AbsBasePath +
                BOPathConstants.PATH_XSLT +
                queryObjectName + QueryExecutive_01.FILEPF_DOWNLOAD;

            // check if a type specific download stylesheet file exists
            if (!FileHelpers.exists (xslFile))
            {
                xslFile = this.app.p_system.p_m2AbsBasePath +
                    BOPathConstants.PATH_XSLT +
                    QueryExecutive_01.GENERIC_DOWNLOAD_STYLESHEET;
                // check if a generic stylesheet exists
                if (!FileHelpers.exists (xslFile))
                {
                    // no stylesheets supported
                    return;
                } // if (!FileHelpers.exists (xslFile))
            } // if (! FileHelpers.exists (xslFile))

            // add the button
            grButtons.addElement (new BlankElement ());
            grButtons.addElement (new BlankElement ());
            grButtons.addElement (new BlankElement ());

            HTMLButtonElement saveButton =
                new HTMLButtonElement ("BUTT_DOWNLOAD", HTMLButtonElement.INP_BUTTON);
            saveButton.onClick =
                    "var target = this.form.target; " +
                    "var fct = this.form.fct.value;" +
                    "this.form.target = '" + HtmlConstants.FRM_TEMP + "';" +
                    "this.form.fct.value = " + AppFunctions.FCT_DOWNLOAD + ";" +
                    "this.form.submit ();" +
                    "this.form.target = target; this.form.fct.value = fct;";
            saveButton.addLabel (
                MultilingualTextProvider.getText (QueryTokens.TOK_BUNDLE,
                    QueryTokens.ML_SAVE, env));
            saveButton.title = 
                MultilingualTextProvider.getText (QueryTokens.TOK_BUNDLE,
                    QueryTokens.ML_SAVE_TITLE, env);

            grButtons.addElement (saveButton);
        } // if (queryObjectName != null && queryObjectName.length () == 0)
    } // addDownloadButton


    /**************************************************************************
     * Evaluate the function to be performed. <BR/>
     *
     * @param   function    The function to be performed.
     *
     * @return  Function to be performed after this method. <BR/>
     *          {@link ibs.app.AppFunctions#FCT_NOFUNCTION FCT_NOFUNCTION}
     *          if there is no function or the function was already performed.
     */
    public int evalFunction (int function)
    {
        int resultFunction = AppFunctions.FCT_NOFUNCTION;
                                        // the resulting function
        switch (function)               // perform function
        {
            case AppFunctions.FCT_DOWNLOAD:
                // perform download:
                this.performDownload ();
                break;

            case AppFunctions.FCT_GENERATEREPORT:
                // perform download:
                this.openReport ();
                break;

            default:                    // unknown function
                resultFunction = super.evalFunction (function);
                                        // evaluate function in super class
        } // switch function

        // return which function shall be performed after this method:
        return resultFunction;
    } // evalFunction

    /**************************************************************************
     * Show a delete confirmation message to the user. <BR/>
     *
     * @param   representationForm  Kind of representation.
     */
    protected void performShowDeleteConfirmation (int representationForm)
    {
        int countReferences = 0;        // number of references
        Page page = new Page ("DeleteConfirmation", false);
        String url = this.getBaseUrl () +
            HttpArguments.createArg (BOArguments.ARG_FUNCTION, AppFunctions.FCT_OBJECTDELETE) +
            HttpArguments.createArg (BOArguments.ARG_OID, "" + this.oid) +
            HttpArguments.createArg (BOArguments.ARG_CONTAINERID, "" + this.containerId);
        String message = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                this.msgDeleteConfirm, env); // the message for the user

        // check if there are any references to the objects which shall be
        // deleted:
        countReferences = this.checkReferences (true);

        // set the correct message:
        if (countReferences > 0)        // at least one reference found?
        {
            message = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    this.msgDeleteConfirmRef, 
                    new String[] {"" + countReferences}, env);
        } // if at least one reference found

        // show script:
        ScriptElement script = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);
        StringBuffer scriptBuffer = new StringBuffer (
            "if (confirm (\"" +
            message +
            "\"))\n" +
            "top.callUrl (\"" + url + "\", null, null, \"" + this.frmSheet + "\");\n");
        scriptBuffer.append ("top.scripts.showSearchFrame(false,false,false);");
        scriptBuffer.append ("\n");

        script.addScript (scriptBuffer);
        page.body.addElement (script);

        // build the page and show it to the user:
        try
        {
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage (e.getMsg (), this.app, this.sess, this.env);
        } // catch
    } // performShowDeleteConfirmation


    /**************************************************************************
     * Execute a given query and return the output as string. <BR/>
     * Used to embed the query output in other html pages or html emails. <BR/>
     *
     * @param queryObject     the query object to be executed
     *
     * @return  The output of the query.
     */
    public String getQueryOutput (QueryCreator_01 queryObject)
    {

        // locals
        QueryExecutiveElement_01 obj = null;        // element for one resultrow
        QueryExecutive qe = new QueryExecutive ();  // for query execution
        int jcounter = 0;               // counter for the container elements

        // did we got a query object?
        if (queryObject != null)
        {
            this.queryObject = queryObject;
            qe.initObject (this.oid, this.user, this.env, this.sess, this.app);
            // empty the elements vector:
            this.elements.removeAllElements ();

            // if query with oid exist and could be executed
            if (qe.execute (this.queryObject))
            {
                this.results = qe.results;

                while (!qe.getEOF ())
                {
                    // create an instance of the element's class:
                    obj = new QueryExecutiveElement_01 ();
                    // add element to list of elements:
                    this.elements.addElement (obj);

                    // fill containerelement with data
                    this.getContainerElementData (obj, qe);

                    qe.next ();
                    jcounter += 1;
                } // while - walk through all resultrows

                // set maxElements to the chosen resultCounter of the querycreator
                // and areMaxElementsExceeded to true for showing the message box for the user
                this.maxElements = jcounter;
                this.areMaxElementsExceeded = !qe.notAll;
            } // if query with oid exist in querypool

            // now create the XSL output
            return this.showContentXSL ();
        } // if (queryObject != null)

        // no query object set
        return null;
    } // performRetrieveContentData


    /**************************************************************************
     * Returns if the object is a query. <BR/>
     *
     * @return  <CODE>true</CODE> if the object is a query,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean isQuery ()
    {
        return true;
    } // isQuery

} // class QueryExecutive_01
