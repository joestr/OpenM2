/*
 * Class: SearchContainer_01.java
 */

// package:
package ibs.obj.search;

// imports:
import ibs.bo.BOConstants;
import ibs.bo.BOMessages;
import ibs.bo.BOTokens;
import ibs.bo.Buttons;
import ibs.bo.Container;
import ibs.bo.Datatypes;
import ibs.bo.OID;
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.search.SearchQuery;
import ibs.obj.search.SearchQueryElement;
import ibs.service.user.User;
import ibs.tech.html.TableElement;
import ibs.tech.sql.SQLConstants;
import ibs.tech.sql.SQLHelpers;
import ibs.util.UtilConstants;

import java.util.Iterator;


/******************************************************************************
 * This class represents one object of type SearchContainer with version 01. <BR/>
 *
 * @version     $Id: SearchContainer_01.java,v 1.22 2013/01/18 10:38:18 rburgermann Exp $
 *
 * @author      Klaus Reimüller (BB), 980512
 ******************************************************************************
 */
public class SearchContainer_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: SearchContainer_01.java,v 1.22 2013/01/18 10:38:18 rburgermann Exp $";


    /**
     * searchQuery holds the filter elements for the search query. <BR/>
     */
    public SearchQuery searchQuery = null;


    /**************************************************************************
     * This constructor creates a new instance of the class SearchContainer_01.
     * <BR/>
     */
    public SearchContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // SearchContainer_01


    /**************************************************************************
     * Creates a SearchContainer_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public SearchContainer_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
    } // SearchContainer_01


    /***********************************************************************s***
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // initialize class attributes:
        this.isMajorContainer = true;   // container is top of a navigation tree

        // set a default name:
        if (this.name == null || this.name.isEmpty ())
        {
            this.name = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SEARCHRESULT, env);
        } // if

        // container has no physical representation in db
        this.isPhysical = false;

        this.name = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SEARCHRESULT, env);
        this.setIcon ();

        // set maximum number of entries, that will be shown in list
        this.maxElements = 50;

        // overwrite alert message if max number of entries exceeded
        this.msgDisplayableElements = BOMessages.ML_MSG_TOOMUCHELEMENTSSEARCH;
    } // initClassSpecifics


    /**************************************************************************
     * This constructor creates a new instance of the class SearchContainer.
     * <BR/>
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
     * @param   searchQry   the SearchQuery object that holds the filter attributes
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public SearchContainer_01 (OID oid, User user, SearchQuery searchQry)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);

        // init specifics of actual class:
        this.searchQuery = searchQry;   // set the filter attributes object
    } // SearchContainer_01


    /**************************************************************************
     * Represent the properties of a SearchContainer object to the user and
     * displays the filter elements set. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showProperties (TableElement table)
    {
        super.showProperties (table);

        // show filter elements:
        SearchQueryElement elem;
        for (Iterator<SearchQueryElement> iter = this.searchQuery.filters.iterator ();
             iter.hasNext ();)
        {
            // get next element:
            elem = iter.next ();

            if (elem.type == Datatypes.DT_INTEGERRANGE ||
                elem.type == Datatypes.DT_NUMBERRANGE ||
                elem.type == Datatypes.DT_MONEYRANGE ||
                elem.type == Datatypes.DT_DATERANGE)
            {
                this.showProperty (table, "", elem.name, Datatypes.DT_TEXT,
                    elem.value + " - " + elem.range);
            } //if
            else
            {
                this.showProperty (table, "", elem.name, Datatypes.DT_TEXT,
                    elem.value);
            } // else
        } // for iter
    } // showProperties


    /**************************************************************************
     * Create the query to get the container's content out of the database.
     * <BR/>
     * The query or view must at least have the attributes userId and rights.
     * Queries on these attributes have to be addable to this query. <BR/>
     * The query must have at least one constraint within WHERE to ensure that
     * other constraints can be added with AND. <BR/>
     * <B>Standard format:</B><BR/>
     *      "SELECT DISTINCT oid, &lt;other attributes> " +
     *      " FROM " + this.viewContent +
     *      " WHERE containerId = " + oid;<BR/>
     * This method can be overwritten in subclasses. <BR/>
     * As you can see the query should contain at least the oid.
     *
     * @return  The constructed query.
     */
    protected String createQueryRetrieveContentData ()
    {
        // set the searchQuery object to a the default
        // happens when a searchContainer has been selected
        // without having a search specified
// !!!
// !!! Workaround because SearchContainer appears to get cached
// !!!
        if (this.searchQuery == null)
        {
            this.searchQuery = this.getUserInfo ().searchQuery;
        } // if (searchQuery == null)
        else
        {
            // searchQuery already set:
            this.searchQuery = this.getUserInfo ().searchQuery;
        } // else
        if (this.searchQuery != null)
        {
            // searchQuery is now set
        } // if (searchQuery != null)

        // check if we have no query filters
        if (!this.searchQuery.filters.elements ().hasMoreElements ())
        {
            // return with no rowcounts
            // this avoids very long result lists in case no filter has
            // been specified
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_NOSEARCHFILTERS, this.env),
                this.app, this.sess, this.env);
            return null;
        } // if (! searchQuery.filters.elements().hasMoreElements())

        // construct the search filter query string
        StringBuffer searchQueryStr = this.createSearchFilterString ();
//debug ("SearchQueryStr" + searchQueryStr);

        // create the SQL String to select all tuples
        StringBuffer queryStr = new StringBuffer ()
            .append ("SELECT DISTINCT o.oid, o.state, o.name AS name, o.typeName AS typeName,")
            .append ("        o.typeCode AS typeCode, o.isLink,")
            .append ("        o.linkedObjectId, o.owner, o.ownerName as ownerName, o.ownerOid,")
            .append ("        o.ownerFullname AS ownerFullname, o.lastChanged AS lastChanged,")
            .append ("        o.isNew, o.icon, o.description,")
            .append ("        o.flags, o.processState")
            .append (" FROM    ").append (this.viewContent).append (" o, ibs_TVersion tv, ibs_type t ");
        // add query filter for search attributes
        if (searchQueryStr.length () > 0)
        {
            queryStr.append (searchQueryStr);
        } // if (!searchQueryStr.equals(""))
        // construct rest of the query
        queryStr
            .append (" o.tVersionID = tv.id ")
            .append (" AND tv.typeId = t.id ")
            .append (" AND t.isSearchable = ").append (UtilConstants.QRY_TRUE);

//debug ("queryStr = " + queryStr);
        return queryStr.toString ();
    } // createQueryRetrieveContentData


    /**************************************************************************
     * Creates the search filter string for the query. <BR/>
     *
     * @return  The search filter string.
     */
    protected StringBuffer createSearchFilterString ()
    {
        StringBuffer queryStr = new StringBuffer ();
        SearchQueryElement elem;
        StringBuffer op = new StringBuffer ();
        StringBuffer alias = new StringBuffer ();
        StringBuffer table = new StringBuffer ();
        StringBuffer fullName;          // the fully qualified name
                                        // <alias> + <name>
        StringBuffer opAnd = new StringBuffer (" AND ");

        for (Iterator<SearchQueryElement> iter = this.searchQuery.filters
            .iterator (); iter.hasNext ();)
        {
            // get next element:
            elem = iter.next ();

            // make sure this is not an empty object
            if (elem.name != null && !elem.name.isEmpty ())
            {
                this.debug ("SearchQueryElement: name: " + elem.name +
                    " - type: " + elem.type + " - value: " + elem.value +
                    " - range: " + elem.range + " - table: " + elem.table +
                    " - matchtype: " + elem.matchType);

                // check if the attribute is a busines object standard attribute
                if (elem.table.isEmpty ())
                {
                    alias.append ("o.");
                } //if
                else
                {
                    alias.append ("v.");
                    table.append (elem.table);
                } // else

                fullName = new StringBuffer ()
                    .append (alias).append (elem.name);

                // create query string depending on type
                switch (elem.type)
                {
                    case Datatypes.DT_BOOL:     // boolean field
                    case Datatypes.DT_TYPE:     // integer field
                    case Datatypes.DT_INTEGER:  // integer field
                        queryStr
                            .append (op).append (fullName)
                            .append (" = ").append (elem.value);
                        break;

                    case Datatypes.DT_INTEGERRANGE: // integer range
                    case Datatypes.DT_NUMBERRANGE: // number range
                    case Datatypes.DT_MONEYRANGE: // money range
                        if (elem.value != null && elem.value.length () > 0)
                        {
                            queryStr
                                .append (op).append (fullName)
                                .append (" >= ").append (elem.value);
                            op = opAnd;
                        } //if
                        if (elem.range != null && elem.range.length () > 0)
                        {
                            queryStr
                                .append (op).append (fullName)
                                .append (" <= ").append (elem.range);
                        } //if
                        break;

                    case Datatypes.DT_TEXT:     // text field
                    case Datatypes.DT_DESCRIPTION: // text field
                        queryStr
                            .append (op).append (fullName)
                            .append (" LIKE '").append (elem.value).append ("'");
                        break;

                    case Datatypes.DT_DATE:     // date field
                        queryStr
                            .append (op)
                            .append (SQLHelpers.getQueryConditionDateTime (
                                fullName, SQLConstants.MATCH_EXACT,
                                new StringBuffer ().append (elem.value)
                                    .append (" 23:59")));
                        break;

                    case Datatypes.DT_DATERANGE: //  date range
                        if (elem.value != null && !elem.value.isEmpty ())
                        {
                            queryStr
                                .append (op).append (" (")
                                .append (SQLHelpers.getDateDiff (fullName,
                                                                 new StringBuffer (elem.value + " 00:00"),
                                                                 SQLConstants.UNIT_DAY))
                                .append (" >= 0) ");

                            op = opAnd;
                        } //if
                        if (elem.range != null && !elem.range.isEmpty ())
                        {
                            queryStr
                                .append (op).append (" (")
                                .append (SQLHelpers.getDateDiff (fullName,
                                                                 new StringBuffer (elem.range + " 23:59"),
                                                                 SQLConstants.UNIT_DAY))
                                .append (" <= 0) ");
                        } //if
                        break;

                    case Datatypes.DT_SEARCHTEXT: // text field
                        if (elem.value != null && !elem.value.isEmpty ())
                        {
                            StringBuffer conditionStr = SQLHelpers
                                .getQueryConditionString (fullName, SQLHelpers
                                    .mapBO2SQLMatchType (elem.matchType),
                                    new StringBuffer ().append (elem.value), true);

                            // check if we found a valid condition:
                            if (conditionStr != null)
                            {
                                queryStr.append (op).append (conditionStr);
                            } // if
                        } // if
                        break;

                    case Datatypes.DT_SEARCHCLOB: // clob field
                        // There is a need of an extra query for oracle, because you must
                        // use the method dbms_lob.INSTR for comparing a CLOB Object (content of ibs_note_01)
                        // with an inputstring.
                        if (elem.value != null && !elem.value.isEmpty ())
                        {
                            if (elem.matchType.equals (BOConstants.MATCH_EXACT))
                                        // exact match
                            {
                                queryStr
                                    .append (op).append (" dbms_lob.INSTR (")
                                    .append (fullName).append (",'")
                                    .append (elem.value).append ("',1,1) > 0");
                            } // if exact match
                            else if (elem.matchType.equals (BOConstants.MATCH_SUBSTRING))
                                        // substring match
                            {
                                queryStr
                                    .append (op).append (" dbms_lob.INSTR (")
                                    .append (fullName).append (",'")
                                    .append (elem.value).append ("',1,1) > 0");
                            } // else if substring match
                            else if (elem.matchType.equals (BOConstants.MATCH_SOUNDEX))
                                        // SOUNDEX match
                            {
                                queryStr
                                    .append (op).append (" dbms_lob.INSTR (")
                                    .append (fullName).append (",'")
                                    .append (elem.value).append ("',1,1) > 0");
                            } // else if SOUNDEX match
                        } // if
                        break;

                    default:                    // unknown field type
                        queryStr
                            .append (op).append (fullName)
                            .append (" LIKE '").append (elem.value).append ("'");
                        break;
                } // switch type

                // set the operator now:
                op = opAnd;
            } // if (!elem.name.equals(""))
        } // for iter

        if (queryStr.length () > 0)
        {
            // check if tables should be added to queryString
            if (table.length () == 0)
            {
                queryStr = new StringBuffer ()
                    .append (" WHERE ").append (queryStr).append (opAnd);
            } // if
            else
            {
                queryStr = new StringBuffer ()
                    .append (",").append (table).append (" v WHERE ")
                    .append (queryStr).append (" AND o.OID = v.OID AND ");
            } // else
        } // if (!elem.name.equals())
        else
        {
            queryStr = new StringBuffer (" WHERE ");
        } // else

        return queryStr;
    } // createSearchString


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
        // define buttons to be displayed:
        int [] buttons =
        {
//            Buttons.BTN_EDIT,
//            Buttons.BTN_DELETE,
//            Buttons.BTN_CUT,
//            Buttons.BTN_COPY,
//            Buttons.BTN_DISTRIBUTE,
            Buttons.BTN_SEARCH,
//            Buttons.BTN_HELP,
        }; // buttons

        // return button array
        return buttons;
    } // showInfoButtons


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
        // define buttons to be displayed:
        int[] buttons =
        {
            Buttons.BTN_SEARCH,
            Buttons.BTN_LISTDELETE,
            Buttons.BTN_LIST_COPY,
            Buttons.BTN_LIST_CUT,
            Buttons.BTN_DISTRIBUTE,
        }; // buttons
        // return button array
        return buttons;
    } // setContentButtons

} // class SearchContainer_01
