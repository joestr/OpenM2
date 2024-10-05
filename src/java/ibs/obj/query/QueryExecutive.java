/*
 * Class: QueryExecutive.java
 */

// package:
package ibs.obj.query;

// imports:
import ibs.bo.BOConstants;
import ibs.bo.BOHelpers;
import ibs.bo.BOTokens;
import ibs.bo.BusinessObject;
import ibs.bo.IncorrectOidException;
import ibs.bo.OID;
import ibs.di.DIHelpers;
import ibs.io.Environment;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.query.QueryConstants;
import ibs.obj.query.QueryCreator_01;
import ibs.obj.query.QueryExceptions;
import ibs.obj.query.QueryFactory;
import ibs.obj.query.QueryNotFoundException;
import ibs.obj.query.QueryParameter;
import ibs.obj.query.ResultElement;
import ibs.obj.query.ResultRow;
import ibs.service.user.User;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLConstants;
import ibs.tech.sql.SQLHelpers;
import ibs.util.DateTimeHelpers;
import ibs.util.Helpers;
import ibs.util.StringHelpers;

import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;


/******************************************************************************
 * An QueryExecutive object. <BR/>
 * To perform defined sql queries within querycreators on a database. <BR/>
 * The resultset is stored in QueryExecutive and could be read out via
 * different interfaces. <BR/>
 *
 * @version     $Id: QueryExecutive.java,v 1.47 2010/04/15 15:31:13 rburgermann Exp $
 *
 * @author      Andreas Jansa (AJ)  010104
 ******************************************************************************
 */
public class QueryExecutive extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: QueryExecutive.java,v 1.47 2010/04/15 15:31:13 rburgermann Exp $";


    /**
     * Vector with inputparameters for querycondition in query. <BR/>
     */
    protected Vector<QueryParameter> inParameters = new Vector<QueryParameter> ();

    /**
     * Vector with all resultrows. <BR/>
     */
    protected Vector<ResultRow> results = new Vector<ResultRow> ();

    /**
     * index of current row in resultset. <BR/>
     */
    protected int curIndex = 0;

    /**
     * if all tuples are returned or not. <BR/>
     */
    public boolean notAll = true;

    /** oid of current object that that calls a query
     */
    protected OID currentObjectOid = null;

    /**
     * container oid of current object that calls a query
     */
    protected OID currentContainerId = null;



    /**************************************************************************
     * Initializes a Servize. <BR/>
     *
     * The {@link ibs.IbsObject#oid oid} is set to an empty oid. <BR/>
     *
     * @param   aUser   Object representing the user.
     * @param   aEnv    The actual call environment.
     * @param   aSess   The actual session info.
     * @param   aApp    The global application info.
     */
    public void initService (User aUser, Environment aEnv,
                             SessionInfo aSess, ApplicationInfo aApp)
    {
// HACK AJ BEGIN
        // there has to be an empty oid:
        this.setOid (OID.getEmptyOid ());
// HACK AJ END
        super.initObject (this.oid, aUser, aEnv, aSess, aApp);
    } // initService


    /**************************************************************************
     * Set the oid of the object that calls the query. <BR/>
     *
     * @param aOid  the oid of the object
     */
    public void setCurrentObjectOid (OID aOid)
    {
        this.currentObjectOid = aOid;
    } // setCurrentObjectOid


    /***************************************************************************
     * Set the oid of the object that calls the query. <BR/>
     *
     * @param aOid  the containerId of the object
     */
    public void setCurrentContainerId (OID aOid)
    {
        this.currentContainerId = aOid;
    } // setCurrentContainerId


    /**************************************************************************
     * add inputparameter for querycondition in query to be executed with
     * method execute (...). <BR/>
     *
     * @param   name    name of searchparam (searchfield) in querytemplate
     * @param   type    type of searchparam in querytemplate
     *                  possible are all PARAMTYPEs in QueryConstants
     * @param   value   value for querycondition
     */
    public void addInParameter (String name, String type, String value)
    {
        this.addInParameter (name, type, value, SQLConstants.MATCH_EXACT);
    } // addParameter


    /**************************************************************************
     * add inputparameter for querycondition in query to be executed with
     * method execute (...). <BR/>
     *
     * @param   name    name of searchparam (searchfield) in querytemplate
     * @param   type    type of searchparam in querytemplate
     *                  possible are all PARAMTYPEs in QueryConstants
     * @param   value   value for querycondition
     * @param   match   matchtype for querycondition
     *                  possible are all MATCHes in QueryConstants
     */
    public void addInParameter (String name, String type, String value, String match)
    {
//trace ("QueryExecutive.addInParameter (" + name + "," + type + "," + value + ", " + match + ")");
        QueryParameter param = new QueryParameter ();

        // initialize parameter for query:
        param.setName   (name);
        // TODO RB: Initialize multilang fields anywise?
        param.setType   (type);
        param.setValue  (value);
        param.setMatchType (match);

        this.inParameters.addElement (param);
    } // addInParameter


    /**************************************************************************
     * add inputparameter for querycondition in query to be executed with
     * method execute (...). <BR/>
     *
     * @param   pos     position for inputparameter in query creator
     * @param   type    type of searchparam in querytemplate
     *                  possible are all PARAMTYPEs in QueryConstants
     * @param   value   value for querycondition
     */
    public void addInParameter (int pos, String type, String value)
    {
        this.addInParameter (pos, type, value, SQLConstants.MATCH_EXACT);
    } // addParameter


    /**************************************************************************
     * add inputparameter for querycondition in query to be executed with
     * method execute (...). <BR/>
     *
     * @param   pos     position for inputparameter in query creator
     * @param   type    type of searchparam in querytemplate
     *                  possible are all PARAMTYPEs in QueryConstants
     * @param   value   value for querycondition
     * @param   match   matchtype for querycondition
     *                  possible are all MATCHes in QueryConstants
     */
    public void addInParameter (int pos, String type, String value, String match)
    {
//trace ("QueryExecutive.addInParameter (" + pos + "," + type + "," + value + ", " + match + ")");
        QueryParameter param = new QueryParameter ();

        // initialize parameter for query:
        param.setPos    (pos);
        param.setType   (type);
        param.setValue  (value);
        param.setMatchType (match);

        this.inParameters.addElement (param);
    } // addInParameter


    /**************************************************************************
     * add inputparameter for querycondition in query to be executed with
     * method execute (...). <BR/>
     *
     * @param   param   The parameter which has already the necessary values
     *                  set.
     */
    public void addInParameter (QueryParameter param)
    {
        this.inParameters.addElement (param);
    } // addInParameter


    /**************************************************************************
     * store data of current resultrow of a SQLAction in QueryExecutive
     * results. <BR/>
     *
     * @param   action          SQLAction after executing a query.
     * @param   queryCreator    The query creator.
     *
     * @throws  DBError
     *          An exception occurred during a database operation.
     */
    protected void setResultData (SQLAction action, QueryCreator_01 queryCreator)
        throws DBError
    {
//this.debug ("AJ QueryExecutive.setResultData");
        // local variables
        int columnCount = 0;                // count of columns in resultset
        ResultRow result = new ResultRow (); // a resultrow
        String resultValue = null;          // value of a resultcolumn
        boolean boolValue = false;
        String attributeName = null;        // name of attribute for column
        String type = null;                 // type of current result column

        // get columnCount of resultset
        if (queryCreator != null)
        {
            columnCount = queryCreator.colAttrMapping.size ();
        } // if

        // get data of current resultrow in aAction
        for (int i = 0; i < columnCount; i++)
        {
            // possible resultElements
            ResultElement resultElem = new ResultElement ();

            // get name of queryattribute
            attributeName = queryCreator.getColumnQueryAttribute (i);
            resultElem.setAttribute (attributeName);
            // get name of current queryAttribute/column
            resultElem.setName (queryCreator.getColumnName (i));
            // get the multilang name and description of current queryAttribute/column
            resultElem.setMlName (queryCreator.getMlColumnName (i));            
            resultElem.setMlDescription (queryCreator.getMlColumnDescription (i));            
            // get type of current query/column
            type = queryCreator.getColumnType (i);
            resultElem.setType (type);
            // get multiple attribute of current query/column
            resultElem.setMultiple (queryCreator.getMultipleAttribute (i));

            // SYSVAR
            if (type.equals (QueryConstants.COLUMNTYPE_FUNCCOL))
            {
                if (resultElem.getName ().equals (QueryConstants.FUNCCOL_OBJECTID))
                {
                    // get oid value:
                    OID localOid =
                        SQLHelpers.getQuOidValue (action, attributeName);

                    // check if there is a value set for this column
                    // convert oid to string:
                    resultValue = this.getResultValue (
                        action.wasNull () || localOid == null, "" + localOid);
                } // if
                else
                {
                    // get value for this column in current line of resultset:
                    resultValue = SQLHelpers.dbToAscii (
                        action.getString (attributeName));
                } // else
            } // else if column is of type IMAGE, STRING, BUTTON and
            // IMAGE, NUMBER, STRING, BUTTON or BUTTON_TEXT
            else if (type.equals (QueryConstants.COLUMNTYPE_IMAGE) ||
                type.equals (QueryConstants.COLUMNTYPE_STRING) ||
                type.equals (QueryConstants.COLUMNTYPE_BUTTON) ||
                type.equals (QueryConstants.COLUMNTYPE_BUTTON_TEXT) ||
                type.equals (QueryConstants.COLUMNTYPE_INPUT_STRING) ||
                type.startsWith (QueryConstants.COLUMNTYPE_INPUT_INTEGER) ||
                type.startsWith (QueryConstants.COLUMNTYPE_INPUT_NUMBER) ||
                type.startsWith (QueryConstants.COLUMNTYPE_INPUT_MONEY) ||
                type.startsWith (QueryConstants.COLUMNTYPE_INPUT_DATE))
            {
                // get value for this column in current line of resultset
                resultValue = SQLHelpers.dbToAscii (
                    action.getString (attributeName));
            } // else if column is of type IMAGE, STRING, BUTTON ...
            else if (type.equals (QueryConstants.COLUMNTYPE_NUMBER))
            {
                Float fValue = new Float (
                    action.getFloat (attributeName));

                resultValue = fValue.toString ();

            } // else if column is of type NUMBER
            else if (type.equals (QueryConstants.COLUMNTYPE_VALUEDOMAIN))
            {
                String colValue = SQLHelpers.dbToAscii (
                    action.getString (attributeName));

                if (colValue != null)
                {
                    // get object of referenced oid if there is one oid
                    try
                    {
                        OID refOid;
                        StringBuffer colValueBuffer = new StringBuffer ();

                        // is !multiple
                        if (!queryCreator.getMultipleAttribute (i))
                        {
                            refOid = new OID (colValue);

                            // check if the oid is valid:
                            if (!refOid.isEmptyInDomain ())     // oid is valid?
                            {
                                // try to get object via oid:
                                BusinessObject refObj = BOHelpers.getObject (
                                    refOid, this.env, false, false, false);

                                // get the name value from the found object
                                // this could extended in future by adding the field as modifier
                                colValueBuffer.append (DIHelpers.getSysFieldValue (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NAME, env), refObj));
                            } // if oid is valid
                        } // if !multiple
                        else // is multiple
                        {
                            StringTokenizer tok = new StringTokenizer (colValue, BOConstants.MULTISELECTION_VALUE_SAPERATOR);
                            while (tok.hasMoreTokens ())
                            {
                                refOid = new OID (tok.nextToken ());

                                // check if the oid is valid:
                                if (!refOid.isEmptyInDomain ())     // oid is valid?
                                {
                                    // try to get object via oid:
                                    BusinessObject refObj = BOHelpers.getObject (
                                        refOid, this.env, false, false, false);

                                    // get the name value from the found object
                                    // this could extended in future by adding the field as modifier
                                    String value = DIHelpers.getSysFieldValue (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NAME, env), refObj);
                                    colValueBuffer.append (value);

                                    if (tok.hasMoreTokens ())
                                    {
                                        colValueBuffer.append (BOConstants.MULTISELECTION_VALUE_SAPERATOR);
                                    } // if tok.hasMoreTokens()
                                } // if oid is valid
                            } // while has more tokens
                        } //else is multiple

                        resultValue = colValueBuffer.toString ();
                    } // try
                    catch (IncorrectOidException e)
                    {
                        resultValue = "";
                    } // catch
                } // else if
                else
                {
                    resultValue = "";
                } // else

            } // else if column is of type VALUEDOMAIN
            // BUTTON_IMAGE
            else if (type.startsWith (QueryConstants.COLUMNTYPE_BUTTON_IMAGE))
                                        // column is of type BUTTON_IMAGE
            {
                // read the string of the COLUMNTYPE:
                String image = type;

                // separate the Image FROM COLUMNTYPE:
                image = image.substring (QueryConstants.COLUMNTYPE_BUTTON_IMAGE.length ());
                image = image.trim ();

                // control COLUMNTYPE-syntax:
                if (image.startsWith ("(") && image.endsWith (")"))
                {
                    // get value for this column in current line of resultset:
                    resultValue = action.getString (attributeName);
                } // control COLUMNTYPE-syntax
                else
                {
                    // error at COLUMNTYPE-syntax:
                    resultElem.setType (QueryConstants.COLUMNTYPE_STRING);
                    resultValue = QueryConstants.EXC_WRONGCOLUMNTYPE;
                } // else
            } // else if column is of type BUTTON_IMAGE
            // MONEY
            else if (type.equals (QueryConstants.COLUMNTYPE_MONEY))
            { // columnvalue is no m2-systemvalue
                // get value for this column in current line of resultset
                long money = action.getCurrency (attributeName);
                // check if there is a value set for this column
                if (action.wasNull ())
                {
                    resultValue = null;
                } // if
                else
                {
                    // convert money to string
                    resultValue = Helpers.moneyToString (money);
                } // else
            } // else if MONEY
            // INTEGER
            else if (type.equals (QueryConstants.COLUMNTYPE_INTEGER))
            { // columnvalue is no m2-systemvalue
                // get value for this column in current line of resultset
                int integ = action.getInt (attributeName);

                // check if there is a value set for this column
                // convert integer to string:
                resultValue = this.getResultValue (action.wasNull (), "" + integ);
            } // else if INTEGER
            // BOOLEAN
            else if (type.equals (QueryConstants.COLUMNTYPE_BOOLEAN))
            { // columnvalue is no m2-systemvalue
                // get value for this column in current line of resultset
                boolValue = action.getBoolean (attributeName);

                // check if there is a value set for this column
                // convert integer to string:
                resultValue = this.getResultValue (action.wasNull (), "" + boolValue);
            } // else if BOOLEAN
            // DATE
            else if (type.equals (QueryConstants.COLUMNTYPE_DATE))
            { // columnvalue is no m2-systemvalue
                // get value for this column in current line of resultset
                Date d = action.getDate (attributeName);

                // check if there is a value set for this column
                // convert date to string:
                resultValue = this.getResultValue (action.wasNull (),
                                (d == null)? null : DateTimeHelpers.dateToString (d));
            } // else if DATE
            // TIME
            else if (type.equals (QueryConstants.COLUMNTYPE_TIME))
            { // columnvalue is no m2-systemvalue
                // get value for this column in current line of resultset
                Date d = action.getDate (attributeName);

                // check if there is a value set for this column
                // convert date to string:
                resultValue = this.getResultValue (action.wasNull (),
                    (d == null) ? null : DateTimeHelpers.timeToString (d));
            } // else if TIME
            // DATETIME
            else if (type.equals (QueryConstants.COLUMNTYPE_DATETIME))
            { // columnvalue is no m2-systemvalue
                // get value for this column in current line of resultset
                Date d = action.getDate (attributeName);

                // check if there is a value set for this column
                // convert date to string:
                resultValue = this.getResultValue (action.wasNull (),
                    (d == null) ? null : DateTimeHelpers.dateTimeToString (d));
            } // else DATETIME
            // OBJECTID
            else if (type.equals (QueryConstants.COLUMNTYPE_OBJECTID))
            {
                // columnvalue is no m2-systemvalue
                // get value for this column in current line of resultset
                OID localObjId = SQLHelpers.getQuOidValue (action, attributeName);

                // check if there is a value set for this column
                // convert date to string:
                resultValue = this.getResultValue (
                    action.wasNull () || localObjId == null, "" + localObjId);
            } // else OBJECTID

            // set value of current resultcolumn in resultelement
            resultElem.setValue (resultValue);

            // add resultelement to resultrow
            result.addElement (resultElem);
        } // for

        // add resultrow to resultvector
        this.results.addElement (result);
    } // setResultData


    /**************************************************************************
     * Get a result value. <BR/>
     *
     * @param   nullCondition   <CODE>true</CODE> if the result shall be
     *                          <CODE>null</CODE>, <CODE>false</CODE> otherwise.
     * @param   value           The value to be set.
     *
     * @return  The result value. <BR/>
     *          <CODE>null</CODE> if <CODE>nullCondition</CODE> is
     *          <CODE>true</CODE> or <CODE>value</CODE> was <CODE>null</CODE>.
     */
    private String getResultValue (boolean nullCondition, String value)
    {
        String resultValue = null;      // value of a resultcolumn

        // check if there is a value set for this column
        if (nullCondition)
        {
            resultValue = null;
        } // if
        else
        {
            // convert integer to string
            resultValue = value;
        } // else

        // return the result:
        return resultValue;
    } // setResultValue


    /**************************************************************************
     * Execute query which is specified in specific querycreator and
     * store resultset. <BR/>
     *
     * @param   queryName   name of querycreator with required query to be
     *                      performed.
     *
     * @return  <CODE>true</CODE> if no error occured
     */
    public boolean execute (String queryName)
    {
//this.debug ("QueryExecutive.execute (" + queryName + ")");
        QueryFactory qf = this.getQueryFactory ();
        QueryCreator_01 queryCreator = null;

        try
        {
            // try to get queryCreator with specific name
            queryCreator = qf.get (queryName);
        } // try
        catch (QueryNotFoundException e)
        {
            // show exception if query was not found
            String exc = 
                MultilingualTextProvider.getMessage (QueryExceptions.EXC_BUNDLE,
                    QueryExceptions.ML_EXC_QUERYDOESNOTEXIST_NAME,
                    new String[] {queryName}, env);

            IOHelpers.showMessage (
                "QueryExecutive.execute: ERROR " + exc,
                this.app, this.sess, this.env);

            return false;
        } // catch

        // set the oid and containerId of object that uses the query:
        queryCreator.setCurrentObjectOid (this.currentObjectOid);
        queryCreator.setCurrentContainerId (this.currentContainerId);
        // execute the query:
        return this.execute (queryCreator);
    } // execute


    /**************************************************************************
     * Execute query which is specified in specific querycreator and
     * store resultset. <BR/>
     *
     * @param   queryOid    oid of querycreator with required query to be
     *                      performed.
     *
     * @return  <CODE>true</CODE> if no error occured
     */
    public boolean execute (OID queryOid)
    {
//this.debug ("QueryExecutive.execute (" + queryOid + ")");
        QueryFactory qf = this.getQueryFactory ();
        QueryCreator_01 queryCreator = null;

        try
        {
            // try to get queryCreator with specific oid
            queryCreator = qf.get (queryOid);
        } // try
        catch (QueryNotFoundException e)
        {
            // show exception if query was not found
            String exc =  
                MultilingualTextProvider.getMessage (QueryExceptions.EXC_BUNDLE,
                    QueryExceptions.ML_EXC_QUERYDOESNOTEXIST_OID,
                    new String[] {(queryOid == null) ? "null" : queryOid.toString ()}, env);

            IOHelpers.showMessage (
                "QueryExecutive.execute: ERROR " + exc,
                this.app, this.sess, this.env);
            return false;
        } // catch

        // set the oid and containerId of object that uses the query:
        queryCreator.setCurrentObjectOid (this.currentObjectOid);
        queryCreator.setCurrentContainerId (this.currentContainerId);
        // execute the query:
        return this.execute (queryCreator);
    } // execute


    /**************************************************************************
     * Execute query which is specified in specific querycreator and
     * store resultset. <BR/>
     *
     * @param   queryCreator    full initialized queryCreator Object
     *
     * @return  true if no error occured
     *          false if queryCreator was null or an dbError occured
     */
    public boolean execute (QueryCreator_01 queryCreator)
    {
//this.debug ("QueryExecutive.execute (" + queryCreator + ")");
        // check parameters
        if (queryCreator == null)
        {
            return false;
        } // if

        // local variables:
        SQLAction aAction = null;
        StringBuffer queryStr = null;
        int rowCount = 0;

        // get querystring of querytemplate:
        queryStr = this.getQuery (queryCreator);
//this.debug ("QueryExecutive.execute QUERY: " + queryStr);
        // check if we got a valid queryStr
        if (queryStr == null)
        {
            // return false to indivate the error
            return false;
        } // if (queryStr == null)
        
        // shall the query be shown?
        if (queryCreator.getEnableDebugging ())
/* BB: for later use        	
        	this.user.username.equalsIgnoreCase(IOConstants.USERNAME_ADMINISTRATOR) ||
        	this.user.username.equalsIgnoreCase(IOConstants.USERNAME_DEBUG))
*/        	
        {
        	// TODO: we have a problem when the new form is called and the object
        	// has a query field and the new object has a FIELDREF or OBJECTREF
        	// that produces a FRAMESET
        	// in that case the query is called and produces HTML output
        	// that is in conflict with displaying the FRAMESET!!!        	
            this.createHTMLHeader (this.app, this.sess, this.env);
            QueryCreator_01.showQueryInfo (this.env, queryStr.toString(), queryCreator);               
            this.createHTMLFooter (this.env);
        } // if (enableDebugging)

        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else:
            aAction = queryCreator.getQueryDBConnection ();

            try
            {
                // execute query on db:
                rowCount = aAction.execute (queryStr, false);

                // error
                if (rowCount < 0)
                {
                    return false;       // terminate this method
                } // if

                // empty resultset
                if (rowCount == 0)
                {
                    return true;        // terminate this method
                } // if

                int count = 0;          // chosen results of the querycreator
                int j = 1;              // counts the results

                // if the value of the resultCounter is the default value
                // take the constant value
                if ((count = queryCreator.getResultCounter ()) < 0)
                {
                    // do only set the default max value in case of a search
                    // query:
                    if (queryCreator.isSearchQuery ())
                    {
                        count = BOConstants.MAX_CONTENT_ELEMENTS;
                    } // if
                    else
                    {
                        // note that the value 0 means no max value:
                        count = 0;
                    } // else
                } // if

                // get tuples out of db: either all tuples or only special number
                while ((!aAction.getEOF ()) && (count == 0 || j <= count))
                {
                    this.setResultData (aAction, queryCreator);
                    j += 1;             // increment the counter
                    aAction.next ();
                } // while

                //  set the boolean value if not all tuples were returned
                if (!aAction.getEOF ())
                {
                    this.notAll = false;
                } // if

                // end transaction:
                aAction.end ();
            } // try
            catch (DBError e)
            {
                // an error occurred - show name and info:
                IOHelpers.showMessage (e, this.app, this.sess, this.env, false);
                return false;
            } // catch
            finally
            {
                try
                {
                    // close db connection in every case - only workaround -
                    // db connection must be handled somewhere else:
                    queryCreator.releaseQueryDBConnection (aAction);
                } // try
                catch (DBError e)
                {
                    IOHelpers.printWarning ("execute", this, e, true);
                    IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
                    return false;
                } // catch DBError
            } // finally
        } // try
        catch (DBError e)
        {
            // an error occurred - show name and info:
            IOHelpers.showMessage (e, this.app, this.sess, this.env, false);
            return false;
        } // catch

        return true;
    } // execute


    /**************************************************************************
     * Generate and return the query string with the given queryCreator
     * store resultset. <BR/>
     *
     * @param queryCreator     the queryCreator object to generate the
     *                         query string from
     *
     * @return  the generated query string or null or an dbError occured
     */
    public StringBuffer getQuery (QueryCreator_01 queryCreator)
    {
//this.debug ("QueryExecutive.getQuery (" + queryCreator + ")");
        // check parameters
        if (queryCreator == null)
        {
            return null;
        } // if

        // local variables:
        QueryParameter qp = null;
        String fieldType = null;
        String rangeBegin = null;
        String rangeEnd = null;

        // walk through all inputparameters and set them in querycreator:
        for (int i = 0; this.inParameters.size () > i; i++)
        {
            // get next inputparameter for querycreator:
            qp = this.inParameters.elementAt (i);

            // index where parameter should be set:
            int searchFieldIndex = -1;

            // try to find position for input parameter:
            searchFieldIndex = qp.getPos ();
            if (searchFieldIndex < 0)
            {
                // try to find position for searchvalue via paramters name
                searchFieldIndex = queryCreator.getFieldNameIndex (qp.getName ());
            } // if

/*
Bessere Exception -> wenn searchFieldIndex > spaltenanzahl, dann ist
die position falsch gesetzt. jetzt wird nur ausgegeben, daﬂ es den
namen des inputparamters nicht in den suchfeldern der query gibt.
*/

            // if inputparameter is not possible for querycreator:
            if (searchFieldIndex < 0 ||
                searchFieldIndex >= queryCreator.getSearchFieldCount ())
            {
                String exc =  
                    MultilingualTextProvider.getMessage (QueryExceptions.EXC_BUNDLE,
                        QueryExceptions.ML_EXC_QUERYPARAMDOESNOTEXIST,
                        new String[] {queryCreator.name, "" + qp.getName ()}, env);

                // show error message:
                IOHelpers.showMessage (
                    "QueryExecutive.execute: ERROR " + exc,
                    this.app, this.sess, this.env);
                return null;
            } // if
/* #IGNORE# is checked in QueryCreator
 * Here we have to set the value because it may be used as a query variable
            // check if this is a #IGNORE# field
            else if (queryCreator.getFieldQueryAttribute (searchFieldIndex)
                        .equalsIgnoreCase (QueryConstants.CONST_IGNORE))
            {
                // IGNORE the field
            } // else if (queryCreator.getFieldQueryAttribute (searchFieldIndex)
*/

            // inputparameter possible for querycr.
            // we need to determine the type first
            fieldType = queryCreator.getFieldType (searchFieldIndex);

            // check if we have a range field
            if (fieldType.equals (QueryConstants.FIELDTYPE_INTEGERRANGE) ||
                fieldType.equals (QueryConstants.FIELDTYPE_NUMBERRANGE) ||
                fieldType.equals (QueryConstants.FIELDTYPE_MONEYRANGE) ||
                fieldType.equals (QueryConstants.FIELDTYPE_DATETIMERANGE) ||
                fieldType.equals (QueryConstants.FIELDTYPE_DATERANGE) ||
                fieldType.equals (QueryConstants.FIELDTYPE_TIMERANGE))
            {
                // get the value of the input parameter
                rangeBegin = qp.getValue ();
                rangeEnd = qp.getRangeValue ();

                // check if we got a value
                if ((rangeBegin != null && !rangeBegin.isEmpty ()) ||
                    (rangeEnd != null && !rangeEnd.isEmpty ()))
                {
                    // set inputparameter as searchvalue in querycreator:
                    queryCreator.setSearchRangeForQueryAttr (
                        searchFieldIndex, rangeBegin, rangeEnd);
                } // if (value != null && (!value.equals ("")))
            } // if (fieldType.equals (QueryConstants.FIELDTYPE_DATETIMERANGE) || ...
            else                // not a range type
            {
                // set inputparameter as searchvalue in querycreator:
                queryCreator.setSearchValueForQueryAttr (
                    searchFieldIndex, qp.getValue ());

                // set matchtype for searchvalue:
                queryCreator.setMatchTypeForQueryAttr (
                    searchFieldIndex, qp.getMatchType ());
            } // else not a range type
        } // for i

        // generate and return the query string
        return queryCreator.getQuery ();
    } // getQuery

    /**************************************************************************
     * Go to first resultrow. <BR/>
     */
    public void first ()
    {
//trace ("QueryExecutive.first ()");
        // place for some checks - connection ...
        this.curIndex = 0;
    } // end


    /**************************************************************************
     * Go to next resultrow. <BR/>
     */
    public void next ()
    {
//trace ("QueryExecutive.next ()");
        // place for some checks - connection ...
        this.curIndex++;
    } // end


    /**************************************************************************
     * Go to previous resultrow. <BR/>
     *
     * @throws  DBError
     *          An error occurred during database operation.
     */
    public void previous () throws DBError
    {
//trace ("QueryExecutive.previous ()");
        // place for some checks - connection ...
        this.curIndex--;
    } // previous


    /**************************************************************************
     * Checks if En-Of-File of resultset is reached. <BR/>
     *
     * @return  true if current resultrow is the last row in resultset. <BR/>
     */
    public boolean getEOF ()
    {
//trace ("QueryExecutive.getEOF ()");
        return this.curIndex >= this.results.size ();
    } // getEOF


    /**************************************************************************
     * Get count of rows in resultset. <BR/>
     *
     * @return  Number of rows in resultset.
     */
    public int getRowCount ()
    {
//trace ("QueryExecutive.getRowCount ()");
        return this.results.size ();
    } // getRowCount


    /**************************************************************************
     * Get count of columns in current resultrow. <BR/>
     *
     * @return  Number of columns in current resultrow.
     *          <CODE>-1</CODE> if no restultrow exists.
     */
    public int getColCount ()
    {
//trace ("QueryExecutive.getColCount ()");

        // check if any row exists
        if (this.results.size () != 0)
        {
            return (this.results.elementAt (0)).getElementCount ();
        } // if

        return -1;
    } // getColCount


    /**************************************************************************
     * Get value of specific column in current resultrow. <BR/>
     *
     * @param   index   Index of required column.
     *
     * @return  Result value of column.
     */
    public String getColValue (int index)
    {
// trace ("QueryExecutive.getColValue (" + index + ") row = " + this.curIndex);
        String result = (this.results.elementAt (this.curIndex)).getValue (index);

        return (result == null) ? "" : result;
    } // getValue


    /**************************************************************************
     * Get value of specific column in current resultrow. <BR/>
     *
     * @param   name    Name of required column.
     *
     * @return  Result value of column.
     */
    public String getColValue (String name)
    {
 // trace ("QueryExecutive.getColValue (" + name + ") row = " + this.curIndex);
        String result = (this.results.elementAt (this.curIndex)).getValue (name);
        return (result == null) ? "" : result;
    } // getColValue


    /**************************************************************************
     * Get type of specific column in current resultrow. <BR/>
     * Possible types are all COLUMNTYPEs in QueryConstants. <BR/>
     *
     * @param   index   Index of required column.
     *
     * @return  Type of column.
     */
    public String getColType (int index)
    {
// trace ("QueryExecutive.getColType (" + index + ") row = " + this.curIndex);
        return (this.results.elementAt (this.curIndex)).getType (index);
    } // getType


    /**************************************************************************
     * Get multiple attribute of specific column in current resultrow. <BR/>
     *
     * @param   index   Index of required column.
     *
     * @return  multiple.
     */
    public boolean getMultipleAttribute (int index)
    {
// trace ("QueryExecutive.getColType (" + index + ") row = " + this.curIndex);
        return (this.results.elementAt (this.curIndex)).getMultipleAttribute (index);
    } // getMultipleAttribute


    /**************************************************************************
     * Get type of specific column in current row. <BR/>
     * Possible types are all COLUMNTYPEs in QueryConstants. <BR/>
     *
     * @param   name    Name of required column.
     *
     * @return  Type of column.
     */
    public String getColType (String name)
    {
// trace ("QueryExecutive.getColType (" + name + ") row = " + this.curIndex);
        return (this.results.elementAt (this.curIndex)).getType (name);
    } // getType


    /**************************************************************************
     * Get name of specific column in current resultrow. <BR/>
     *
     * @param   index   Index of required column in current row.
     *
     * @return  Name of column.
     */
    public String getColName (int index)
    {
// trace ("QueryExecutive.getColName (" + index + ") row = " + this.curIndex);
        return (this.results.elementAt (this.curIndex)).getName (index);
    } // getName


    /**************************************************************************
     * Get multilang name of specific column in current resultrow. <BR/>
     *
     * @param   index   Index of required column in current row.
     *
     * @return  Multilang name of column.
     */
    public String getMlColName (int index)
    {
        return (this.results.elementAt (this.curIndex)).getMlName (index);
    } // getMlColName


    /**************************************************************************
     * Get multilang description of specific column in current resultrow. <BR/>
     *
     * @param   index   Index of required column in current row.
     *
     * @return  multilang description of column.
     */
    public String getMlColDescription (int index)
    {
        return (this.results.elementAt (this.curIndex)).getMlDescription (index);
    } // getMlColDescription


    /**************************************************************************
     * Insanciate and initialize a QueryFactory. <BR/>
     *
     * @return  An initialized QueryFactory Object.
     */
    protected QueryFactory getQueryFactory ()
    {
        // instanciate a queryfactory
        QueryFactory qf = new QueryFactory ();

        // initialize the QueryFactory
        qf.initObject (OID.getEmptyOid (), this.user, this.env, this.sess, this.app);

        // return QueryFactory
        return qf;
    } // getQueryFactory
          
    
} // class QueryExecutive
