/*
 * Class: SelectUserContainer_01.java
 */

// package:
package m2.store;

// imports:
import ibs.bo.BOTokens;
import ibs.bo.ContainerElement;
import ibs.bo.OID;
import ibs.bo.SingleSelectionContainer_01;
import ibs.bo.States;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;

import m2.store.SelectUserContainerElement_01;


/******************************************************************************
 * This is a SelectionContainer to select User. <BR/>
 * It works together with the formpropertytype DT_SEARCHTEXTFUNCTION. <BR/>
 *
 * @version     $Id: SelectUserContainer_01.java,v 1.14 2013/01/18 10:38:18 rburgermann Exp $
 *
 * @author      Andreas Jansa (AJ), 990120
 ******************************************************************************
 */
public class SelectUserContainer_01 extends SingleSelectionContainer_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: SelectUserContainer_01.java,v 1.14 2013/01/18 10:38:18 rburgermann Exp $";

    /**
     * Headings of columns. <BR/>
     * These headings are shown at the top of lists.
     */
    public static final String [] LST_HEADINGS_SELECTUSERCONTAINER =
    {
        BOTokens.ML_FULLNAME
    }; // LST_HEADINGS_SELECTUSERCONTAINER

    /**
     * Name of a container column. <BR/>
     * These attributes are used for ordering the elements.
     */
    public static final String [] LST_ORDERINGS_SELECTUSERCONTAINER =
    {
        SelectUserContainer_01.SEARCHCOLUMNNAME,
    }; // LST_ORDERINGS_SELECTUSERCONTAINER

    /**
     * Column name for searching for. <BR/>
     */
    private static final String SEARCHCOLUMNNAME = "fullname";


    /**************************************************************************
     * Creates a SelectUserContainer object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     */
    public SelectUserContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();
        this.searchColumnName = SelectUserContainer_01.SEARCHCOLUMNNAME;

        // init specifics of actual class:
    } // SelectionContainer


    /**************************************************************************
     * Creates a SelectUserContainer object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public SelectUserContainer_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
        this.searchColumnName = SelectUserContainer_01.SEARCHCOLUMNNAME;

        // init specifics of actual class:
    } // SelectUserContainer


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        this.elementClassName = "m2.store.SelectUserContainerElement_01";
        this.name = "";
        this.icon = "SelectUserContainer.gif";
    } // initClassSpecifics


    /**************************************************************************
     * Create the query to get the container's content out of the database.
     * <BR/>
     * The query or view must at least have the attributes uid and rights.
     * Queries on these attributes have to be addable to this query. <BR/>
     * The query must have at least one constraint within WHERE to ensure that
     * other constraints can be added with AND. <BR/>
     * <B>Standard format:</B>. <BR/>
     *      "SELECT DISTINCT oid, &lt;other attributes> " +
     *      " FROM " + this.viewContent +
     *      " WHERE containerId = " + oid;. <BR/>
     * This method can be overwritten in subclasses. <BR/>
     * As you can see the query should contain at least the oid.
     *
     * @return  The constructed query.
     */
    protected String createQueryRetrieveSelectionData ()
    {
        // use when everybody has flags within his / her views:
//////////////////////////////////
//
// HP Tuning: remove v_Container$content --> use ibs_Object instead
//            * new query ignores RIGHTS on users
//            * some unneeded result-attributes will be set to default-values
//
        String oidStr = OID.getEmptyOid ().toStringQu ();
                                        // empty oid as string
        StringBuffer queryStr;          // the query

        queryStr = new StringBuffer ()
/*
            " SELECT DISTINCT o.oid, o.state, o.name, o.typeName, o.isLink, " +
            "         o.linkedObjectId, o.owner, o.ownerName, o.ownerOid, " +
            "         o.ownerFullname, o.lastChanged, o.isNew, o.icon, o.description, " +
            "         o.flags, o.processState, u.fullname " +
            " FROM    " + this.viewContent + " o, ibs_User u" +
            " WHERE   tVersionId =" + Integer.toString (this.app.cache.getTVersionId (Types.TC_User)) +
            " AND     u.oid = o.oid ";
*/
            // get all users that are in the same domain as current user
            // no rights-check for current user on resulting objects
            .append (" SELECT DISTINCT o.oid, o.state, o.name, o.typeCode, o.typeName,")
                .append (" o.isLink, o.linkedObjectId, o.owner,")
                .append (" o.lastChanged, o.icon, o.description,")
                .append (" o.flags, o.processState, u.fullname,")
            // fake values from fake table (see in subquery)
            // - looks weird but it was not possible to set constants at
            // this place
            // problem with user/rights-check that will be added later
            // (see super)
                .append (" f.isNew, f.ownerName, f.ownerOid, f.rights, f.userId,")
                .append (" f.ownerFullname")
            .append (" FROM ibs_Object o, ibs_User u,")
            // fake table: sets fake-values
                .append (" (SELECT 0 AS isNew, '' AS ownerFullname,")
                    .append ("'' AS ownerName, ")
                    .append (oidStr).append (" AS ownerOid,")
                    .append (" 2147483647 AS rights,")
                    .append (this.getUser ().id).append (" AS userId ")
                    .append (SQLHelpers.getDummyTable ("FROM"))
                .append (") f ")
            .append (" WHERE   u.domainId = ").append (this.getUser ().domain)
            .append (" AND     u.oid = o.oid")
            .append (" AND     o.state = ").append (States.ST_ACTIVE)
            .append (" AND     u.state = ").append (States.ST_ACTIVE);
//
// HP Tuning: END
//
//////////////////////////////////
        return queryStr.toString ();
    } // createQueryRetrieveSelectionData


    /**************************************************************************
     * Get the element type specific data out of the actual tuple of the query
     * result. <BR/>
     * This metod is used to get all attributes of one element out of the
     * resultset. The attribute names which can be used are the ones which
     * are defined within the resultset of the Query in createQueryRetrieveSelectionData ()
     *
     * @param   action      The database connection object.
     * @param   commonObj   Object representing the list element.
     *
     * @throws  DBError
     *          Error when executing database statement.
     */
    protected void getContainerElementData (SQLAction action, ContainerElement commonObj)
        throws DBError
    {
        SelectUserContainerElement_01 obj = (SelectUserContainerElement_01) commonObj;

        // get common attributes:
        super.getContainerElementData (action, commonObj);

        // add legal form to name of company
        // (made the same in stored proc. p_Company$retrieve)
        obj.fullname += " " + action.getString (SelectUserContainer_01.SEARCHCOLUMNNAME);
    } // getContainerElementData


    /**************************************************************************
     * Set the headings for this container. This method MUST be overloaded if. <BR/>
     * you have your own subclass of ContainerElement and if you need other headings. <BR/>
     * You have to overload the method setOrderings () as well.
     */
    protected void setHeadingsAndOrderings ()
    {
        this.headings = MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE, 
            SelectUserContainer_01.LST_HEADINGS_SELECTUSERCONTAINER, env);

        this.orderings = SelectUserContainer_01.LST_ORDERINGS_SELECTUSERCONTAINER;

        // ensure that there is an available ordering taken:
        this.ensureAvailableOrdering ();
    } // setHeadingsAndOrderings

} // class SelectUserContainer_01
