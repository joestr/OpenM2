/*
 * Class: SimpleSearchContainer_01.java
 */

// package:
package ibs.obj.search;

// imports:
import ibs.bo.BOArguments;
import ibs.bo.BOConstants;
import ibs.bo.BOMessages;
import ibs.bo.BOTokens;
import ibs.bo.Buttons;
import ibs.bo.Container;
import ibs.bo.OID;
import ibs.io.IOConstants;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.search.SimpleSearchData;
import ibs.service.user.User;
import ibs.tech.sql.SQLConstants;
import ibs.tech.sql.SQLHelpers;


/******************************************************************************
 * This class represents one object of type SimpleSearchContainer with
 * version 01. All properties for this container are stored in the class
 * SimpleSearchData. <BR/>
 *
 * @version     $Id: SimpleSearchContainer_01.java,v 1.21 2013/01/18 10:38:18 rburgermann Exp $
 *
 * @author      Daniel Janesch (DJ), 010215
 *
 * @see         ibs.obj.search.SimpleSearchData
 ******************************************************************************
 */
public class SimpleSearchContainer_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: SimpleSearchContainer_01.java,v 1.21 2013/01/18 10:38:18 rburgermann Exp $";


    /**************************************************************************
     * This constructor creates a new instance of the class
     * SimpleSearchContainer_01. <BR/>
     */
    public SimpleSearchContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // SimpleSearchContainer_01


    /**************************************************************************
     * Creates a SimpleSearchContainer_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public SimpleSearchContainer_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
    } // SimpleSearchContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // initialize class attributes:
        // set a default name:
        if (this.name == null || this.name.isEmpty ())
        {
            this.name = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SEARCHRESULT, env);
        } // if

        // container has no physical representation in db
        this.isPhysical = false;

        this.setIcon ();

        // set maximum number of entries, that will be shown in list
        this.maxElements = BOConstants.MAX_CONTENT_ELEMENTS;

        // overwrite alert message if max number of entries exceeded
        this.msgDisplayableElements = BOMessages.ML_MSG_TOOMUCHELEMENTSSEARCH;
    } // initClassSpecifics


    /**************************************************************************
     * Gets the parameters which are relevant for this object. <BR/>
     * The <A HREF="#env">env</A> property is used for getting the parameters.
     * This property must be set before calling this method. <BR/>
     */
    public void getParameters ()
    {
        String str = null;
        int num = 0;
        OID oid = null;

        // get parameters relevant for super class:
        super.getParameters ();

        // get the search string out of the enviroment:
        if ((str = this.env.getStringParam (BOArguments.ARG_SEARCHVALUE)) != null)
        {
            // store the search value into the session because if the user want
            // to sort the result descending the search value is lost because
            // the search value is in an other Frame as the search result:
            ((SimpleSearchData) this.sess.simpleSearchData).setSearchValue (new StringBuffer (str));
        } // if get the search string out of the enviroment

        // get the kind of search out of the enviroment:
        if ((num = this.env.getBoolParam (BOArguments.ARG_SEARCHGLOBAL)) >
            IOConstants.BOOLPARAM_NOTEXISTS)
                                        // is there a valid bool param
        {
            // true, the search performs in the whole system
            // false, the search performs in the actual container and it
            // subobjects
            ((SimpleSearchData) this.sess.simpleSearchData).setSearchGlobal (num != 0);
        } // if is there a valid bool param

        // get the oid of the container out of the enviroment:
        if ((oid = this.env.getOidParam (BOArguments.ARG_SEARCHROOTCONTAINERID)) != null)
        {
            ((SimpleSearchData) this.sess.simpleSearchData).setSearchRootContainerId (oid);
        } // if get the oid of the container out of the enviroment
    } // getParameters


    /**************************************************************************
     * Create the query to get the data for the searchresult out of the
     * database. <BR/>
     * The query must have at least one constraint within WHERE to ensure that
     * other constraints can be added with AND. <BR/>
     * <B>Standard format:</B><BR/>
     *      " SELECT DISTINCT oid, &lt;other attributes> " +
     *      " FROM " + this.viewContent +
     *      " WHERE  ....;<BR/>
     * This method can be overwritten in subclasses. <BR/>
     * As you can see the query should contain at least the oid.
     *
     * @return  The constructed query.
     */
    protected String createQueryRetrieveContentData ()
    {
        StringBuffer queryStr;          // the query string
        StringBuffer conditionStr1;     // the 1st condition
        StringBuffer conditionStr2;     // the 2nd condition
        SimpleSearchData searchData = (SimpleSearchData) this.sess.simpleSearchData;
                                        // a class which contains all
                                        // informations for the search

        // create the SQL String to select all tuples:
        queryStr = new StringBuffer ()
            .append (" SELECT DISTINCT o.oid, o.state, o.name AS name, o.isLink, o.typeCode AS typeCode, ")
            .append ("        o.typeName AS typeName, o.linkedObjectId, o.owner,")
            .append ("        o.ownerName AS ownerName, o.ownerOid, o.isNew, o.icon,")
            .append ("        o.ownerFullname AS ownerFullname, o.description,")
            .append ("        o.lastChanged AS lastChanged, o.flags, o.processState")
            .append (" FROM   ").append (this.viewContent).append (" o, ibs_Type t");

        if (!searchData.getSearchGlobal ())
                                        // should the search perform in a
                                        // special container ?
        {
            // get the posnopath of the actuall container:
            queryStr
                .append (" , (SELECT posNoPath ")
                .append ("  FROM ibs_Object ")
                .append ("  WHERE oid = ")
                    .append (searchData.getSearchRootContainerId ().toStringQu ())
                .append (" ) p ")
                .append (" WHERE ")
                .append (SQLHelpers.getQueryConditionAttribute (
                    "o.posNoPath", SQLConstants.MATCH_STARTSWITH, "p.posNoPath", true))
                .append (" AND");
        } // if should the search perform in a special container
        else
        {
            queryStr.append (" WHERE");
        } // else

        // some objects should not be found in a searchquery:
        queryStr
            .append (" o.tVersionId = t.actVersion AND")
            .append (" t.isSearchable = 1");

        // concatenate the main search term to the SELECT query
        // and perform a case-insensitive search:
        conditionStr1 = SQLHelpers.getQueryConditionString (
            new StringBuffer ("o.name"), SQLConstants.MATCH_SUBSTRING,
            searchData.getSearchValue (), false);
        conditionStr2 = SQLHelpers.getQueryConditionString (
            new StringBuffer ("o.description"), SQLConstants.MATCH_SUBSTRING,
            searchData.getSearchValue (), false);

        // check if we found valid conditions:
        if (conditionStr1 != null && conditionStr2 != null)
        {
            queryStr
                .append (" AND (")
                .append (conditionStr1)
                .append (" OR ")
                .append (conditionStr2)
                .append (") ");
        } // if
        else if (conditionStr1 != null)
        {
            queryStr.append (" AND ").append (conditionStr1);
        } // else if
        else if (conditionStr2 != null)
        {
            queryStr.append (" AND ").append (conditionStr2);
        } // else if

        return queryStr.toString ();
    } // createQueryRetrieveContentData


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
            Buttons.BTN_LISTDELETE,
            Buttons.BTN_LIST_COPY,
            Buttons.BTN_LIST_CUT,
            Buttons.BTN_DISTRIBUTE,
        }; // buttons
        // return button array
        return buttons;
    } // setContentButtons

} // class SimpleSearchContainer_01
