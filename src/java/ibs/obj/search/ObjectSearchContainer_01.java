/*
 * Class: ObjectSearchContainer_01.java
 */

// package:
package ibs.obj.search;

// imports:
import ibs.bo.BOArguments;
import ibs.bo.OID;
import ibs.bo.SingleSelectionContainer_01;
import ibs.bo.type.TypeConstants;
import ibs.di.DIConstants;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.service.user.User;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLConstants;
import ibs.tech.sql.SQLHelpers;
import ibs.util.NoAccessException;

import java.util.StringTokenizer;


/******************************************************************************
 * This class represents one object of type ObjectSearchContainer with
 * version 01. <BR/>
 *
 * @version     $Id: ObjectSearchContainer_01.java,v 1.34 2013/01/18 10:38:18 rburgermann Exp $
 *
 * @author      Harald Buzzi (HB), 000219
 *
 * @see         ibs.bo.Container
 *******************************************************************************
 */
public class ObjectSearchContainer_01 extends SingleSelectionContainer_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ObjectSearchContainer_01.java,v 1.34 2013/01/18 10:38:18 rburgermann Exp $";


    /**
     * Show the Link to callingObject, or show all matching Objects to
     * searchparameter. <BR/>
     */
    protected int showLink = 0;

    /**
     * Oid of the calling object where a link to a specific object should be
     * added. <BR/>
     */
    public OID callingOid = null;

    /**
     * Types which should be read
     */
    public String showTypes = "";

    /**
     * should the search be recursive
     */
    public boolean searchRecursive = false;

    /**
     * oid of the object from where to start the search
     */
    public OID searchStart = null;

    /**
     * option to include references to other objects in the search result
     */
    protected boolean isIncludeReferences = false;


    /**************************************************************************
     * This constructor creates a new instance of the class. <BR/>
     */
    public ObjectSearchContainer_01 ()
    {
        // call constructor of super class:
        super ();
    } // ObjectSearchContainer_01


    /**************************************************************************
     * This constructor creates a new instance of the class document. <BR/>
     * The compound object id is used as base for getting the
     * {@link ibs.bo.OID#server server}, {@link ibs.bo.OID#type type}, and
     * {@link ibs.bo.OID#id id} of the business object. These values are stored
     * in the special public attributes of this type. <BR/>
     * The user object is also stored in a specific attribute of this object to
     * make sure that the user's context can be used for getting his/her rights.
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public ObjectSearchContainer_01 (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);

        // initialize properties common to all subclasses:
    } // ObjectSearchContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set class specifics of super class:
        super.initClassSpecifics ();

        // this container has no name:
        this.name = "";
    } // initClassSpecifics


    /**************************************************************************
     * Create the query to get the container's content out of the database.
     * <BR/>
     *
     * @return  The constructed query.
     */
    protected String createQueryRetrieveSelectionData ()
    {
        StringBuffer queryStr;          // the query string
/* KR tested and not necessary
        String nameColumn = "";

        // The container object appends the default ORDER BY clause ('ORDER BY name')
        // to the select query. For MS-SQL the column 'name' is ambiguous.
        // To awoid this we append a extra 'name' column.
        if (AppConstants.DB_TYPE == AppConstants.DB_MSSQL)
            nameColumn = ", o.name as name";    // for the ORDER BY clause (ORDER BY name)
*/

        queryStr = new StringBuffer ()
            .append (" SELECT o.oid, o.state, o.name AS name, o.typeCode, o.typeName,")
            .append (" o.isLink, o.linkedObjectId, o.owner, o.ownerName,")
            .append (" o.ownerOid, o.ownerFullname, o.lastChanged, o.isNew,")
            .append (" o.icon, o.description")
/* KR tested and not necessary
            .append (nameColumn)        // for the ORDER BY clause (ORDER BY name)
*/
            .append (" FROM    ").append (this.viewContent).append (" o");

        // if there has to be a recursive search (posNoPath)
        // there has to be a join on ibs_object
        if (this.searchRecursive && this.searchStart != null)
        {
            queryStr.append (", ibs_Object b");
        } // if

        // add the type filter if not empty
        if (this.showTypes != null && !this.showTypes.isEmpty ())
        {
            // showTypes must be transformed into a comma separated
            // list with tVersionIds
            String tVersionIdList = this.createTypeIdList (this.showTypes);
            // if no valid type codes are specified set the list to '0'
            // to avoid a sql syntax error.
            if (tVersionIdList.length () == 0)
            {
                tVersionIdList = "0";
            } // if (tVersionIdList.length () == 0)
            // construct the where clause with the type filter
            queryStr
                .append (" WHERE o.tVersionId IN (")
                .append (tVersionIdList).append (")");
        } //  if (this.showTypes != null && !this.showTypes.equals (""))
        else                            // emtpy; no objecttype can be searched
        {
            queryStr.append (" WHERE 0 = 1");
        } // else empty; no object type can be searched

        // check if references should be included
        if (!this.isIncludeReferences)  // don't include references?
        {
            queryStr.append (" AND o.islink = ").append (0);
        } // if don't include references

        // if there is an object defined to begin the search within
        if (this.searchStart == null)
        {
            // if we have no searchroot defined the query cannot be executed
            // we therefore add a clause that is always false and force
            // the query to return an empty resultset
            queryStr.append (" AND 0 = 1");
        }   // if (this.searchStart == null)
        else        // a searchroot is set
        {
            // search only in the actual container
            if (!this.searchRecursive)  // no recursive search?
            {
                queryStr
                    .append (" AND o.containerId = ")
                    .append (this.searchStart.toStringQu ());
            } // if no recursive search
            else                        // search in underlying folders too
            {
                // search all objects containing the posNoPath of the searchStart object
                queryStr
                    .append (" AND b.oid = ")
                    .append (this.searchStart.toStringQu ())
                    .append (" AND ").append (SQLHelpers.getQueryConditionAttribute (
                        "o.posNoPath", SQLConstants.MATCH_STARTSWITH, "b.posNoPath", false));
            } // else search in underlying folders too
        } // else a searchroot is set
//showDebug ("searchquery = " + queryString);

        return queryStr.toString ();
    } // createQueryRetrieveContentData


    /**************************************************************************
     * Create the query to get the container's content out of the database.
     * <BR/>
     *
     * @return  The constructed query.
     */
    protected String createQueryRetrieveContentData ()
    {
        // use when everybody has flags within his / her views:
        StringBuffer queryStr;          // the query string
        StringBuffer conditionStr;      // the condition

        queryStr = new StringBuffer (this.createQueryRetrieveSelectionData ());
        conditionStr = SQLHelpers.getQueryConditionString (
                new StringBuffer ("o.").append (this.searchColumnName),
                SQLHelpers.mapBO2SQLMatchType (this.matchtype),
                new StringBuffer ().append (this.searchString), true);

        // check if we found a valid condition:
        if (conditionStr != null)
        {
            queryStr.append (" AND ").append (conditionStr);
        } // if

        return queryStr.toString ();
    } // createQueryRetrieveContentData


    /**************************************************************************
     * Gets the parameters which are relevant for this object. <BR/>
     * The <CODE>env</CODE> property is used
     * for getting the parameters. This property must be set before calling
     * this method. <BR/>
     */
    public void getParameters ()
    {
        OID oid     = null;
        String str  = null;
        int num     = 0;

        // get common parameters:
        super.getParameters ();

        // get oid of calling object
        if ((oid = this.env.getOidParam (BOArguments.ARG_CALLINGOID)) != null)
        {
            this.callingOid = oid;
        } // if
        else
        {
            this.callingOid = null;
        } // else

        // get the objecttypes to search for
        if ((str = this.env.getStringParam (BOArguments.ARG_TYPE)) != null)
        {
            this.showTypes = str;
        } // if
        else
        {
            this.showTypes = "";
        } // else

        // get the searchRecursive flag
        if ((num = this.env.getBoolParam (BOArguments.ARG_RECURSIVE)) >=
            IOConstants.BOOLPARAM_FALSE)
        {
            this.searchRecursive = num == IOConstants.BOOLPARAM_TRUE;
        } // if

        // get the oid of the starting container
        if ((this.searchStart =
                this.env.getOidParam (BOArguments.ARG_CONTAINERID)) == null)
        {
            this.searchStart = null;
        } // if

        // show linked object or matching object in container
        if ((num = this.env.getIntParam (BOArguments.ARG_SHOWLINK)) !=
            IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
        {
            this.showLink = num;
        } // if
    } // getParameters ()


    /**************************************************************************
     * Try to read the name of the XMLViewer which this ObjectSearchContainer belongs
     * to, out of the db.
     *
     * @param   operation   Operation to be performed with the object.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     */
    protected void performRetrieveData (int operation) throws NoAccessException
    {
        SQLAction action = null;        // SQlAction for Databaseoperation
        String str;                     // return value of query
        int rowCount;                   // row counter
        StringBuffer queryStr;          // the query string

        // create the query string:
        queryStr = new StringBuffer ()
            .append (" SELECT name")
            .append (" FROM ibs_Object")
            .append (" WHERE oid = ").append (this.callingOid.toStringQu ());

        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();
            rowCount = action.execute (queryStr, false);
            // empty resultset or error while executing ( rowCount < 0)
            if (rowCount <= 0)
            {
                return;                 // terminate this method
            } // if

            // get tuple out of db
            if (!action.getEOF ())
            {
                // try to read name out of tuple
                if ((str = action.getString ("name")) != null)
                {
                    this.containerName = str;
                } // if
            } // if (!action.getEOF ())
        } // try
        catch (DBError e)
        {
            // an error occurred - show name and info
            IOHelpers.showMessage (e, this.app, this.sess, this.env,  false);
        } // catch
        finally
        {
            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            this.releaseDBConnection (action);
        } // finally
    } // performRetrieveData


    /**************************************************************************
     * Takes a list of typecodes separated by a comma and creates a string
     * with a list of corresponding tVersionIds separated by a comma. <BR/>
     * This will be used as filter in the query. <BR/>
     *
     * @param   typeFilter  The string that contains the typecodes.
     *
     * @return  The string with the tversionIds or "" in case the
     *          typeFilter parameter was empty.
     */
    public String createTypeIdList (String typeFilter)
    {
        this.trace ("------------------START createTypeIdList--------------");
        int tVersionId         = 0;
        String tVersionIdList  = "";
        String delim           = "";

        // check if any value has been defined
        if (typeFilter != null && !typeFilter.trim ().isEmpty ())
        {
            StringTokenizer tokenizer;
            // tokenize the showTypes
            // according to the specification for the SELECTION typefilter
            // argument
            // the typenames in the showType are separated by a comma
            tokenizer = new StringTokenizer (typeFilter,
                                             DIConstants.OPTION_DELIMITER);
            // loop through all the typenames
            while (tokenizer.hasMoreTokens ())
            {
                // get a typename
                String typeCode = tokenizer.nextToken ();
                // This type code can be used to get the
                // respective tVersionId from the object pool.
                // tVersionId will be Types.TYPE_NOTYPE in case
                // the type has not been found.
                tVersionId = this.getTypeCache ().getTVersionId (typeCode);
                this.trace ("after transforming " + typeCode);

                if (tVersionId != TypeConstants.TYPE_NOTYPE)
                {
                    // add the tVersionId to the list of tVersionIds we want to
                    // look for
                    tVersionIdList += delim + tVersionId;
                    delim = DIConstants.TYPE_DELIMITER;
                } // if (tVersionId != Types.TYPE_NOTYPE)
            } // while (tokenizer.hasMoreTokens ())
        } // if (typeFilter != null && !typeFilter.trim ().equals (""))
        // if the helpstring is not empty, assign it to showtypes
        this.trace ("return " + tVersionIdList);
        return tVersionIdList;
    } // createTypeIdList

} // class ObjectSearchContainer_01
