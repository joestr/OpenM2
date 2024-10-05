
/*
 * Class: SingleSelectionContainer.java
 */

// package:
package ibs.bo;

// imports:
import ibs.bo.BOArguments;
import ibs.bo.BOConstants;
import ibs.bo.Container;
import ibs.bo.ContainerElement;
import ibs.bo.OID;
import ibs.bo.SingleSelectionContainerElement_01;
import ibs.service.user.User;
import ibs.tech.html.RowElement;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;


/******************************************************************************
 * This is a base class for different SelectionContainers. You just have
 * to overload the function createQueryRetrieveSelectionData and you have
 * to set the property searchColumnName to get a new SingleSelectionContainer. <BR/>
 *
 * @version     $Id: SingleSelectionContainer_01.java,v 1.18 2013/01/18 10:38:17 rburgermann Exp $
 *
 * @author      Andreas Jansa (AJ), 990120
 ******************************************************************************
 */
public class SingleSelectionContainer_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: SingleSelectionContainer_01.java,v 1.18 2013/01/18 10:38:17 rburgermann Exp $";


    /**
     * Search parameter matchtype_extension  (exact, near ...). <BR/>
     */
    protected String matchtype = BOConstants.MATCH_NONE;

    /**
     * String contents the filter string for the contentquery. <BR/>
     */
    protected String searchString = "";

    /**
     * fieldName of the field in formProperty with type DT_SEARCHTEXTFUNCTION
     * wich shall be updated with the selected value
     */
    protected String updateFieldName = "";

     /**
      * columnName of column in database where the filter in searchString shall be set
      */
    protected String searchColumnName = "name";


    /**************************************************************************
     * This constructor creates a new instance of the class SingleSelectionContainer_01.
     * <BR/>
     */
    public SingleSelectionContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:

        // set majorContainer true:
        this.isMajorContainer = true;

        this.isSingleSelectionContainer = true;
    } // SingleSelectionContainer_01


    /**************************************************************************
     * Creates a SingleSelectionContainer_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public SingleSelectionContainer_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);

        // initialize properties common to all subclasses:

        // set majorContainer true:
        this.isMajorContainer = true;

        this.isPhysical = false;
        this.isSingleSelectionContainer = true;
    } // SingleSelectionContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        this.elementClassName = "ibs.bo.SingleSelectionContainerElement_01";
    } // initClassSpecifics


    /**************************************************************************
     * Gets the parameters which are relevant for this object. <BR/>
     * The <CODE>env</CODE> property is used
     * for getting the parameters. This property must be set before calling
     * this method. <BR/>
     */
    public void getParameters ()
    {
        String str = null;

        // get parameters relevant for super class:
        super.getParameters ();

        // field to be updated in user_changeform after selection
        if ((str = this.env.getStringParam (BOArguments.ARG_FIELDNAME)) != null)
        {
            this.updateFieldName = str;
        } // if

        // get matchtype
        if ((str = this.env.getStringParam (this.updateFieldName + BOArguments.ARG_MATCHTYPE_EXTENSION)) != null)
        {
            this.matchtype = str;
        } // if

        // get searchstring
        if ((str = this.env.getStringParam (this.updateFieldName)) != null)
        {
            this.searchString = str;
        } // if
        // if there was no input in the searchstring for fullname, matchtype is irrelevant
        if (this.searchString.length () == 0)
        {
            this.matchtype = BOConstants.MATCH_NONE;
        } // if
    } // getParameters


    /**************************************************************************
     * Create the query to get the container's content out of the database.
     * <BR/>
     *
     * @return  The constructed query.
     */
    protected String createQueryRetrieveContentData ()
    {
        StringBuffer queryStr;          // use when everybody has flags within
                                        // his / her views
        StringBuffer conditionStr;      // the condition

        queryStr = new StringBuffer (this.createQueryRetrieveSelectionData ());
        conditionStr = SQLHelpers.getQueryConditionString (
                new StringBuffer ().append (this.searchColumnName),
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
     * Create the query to get the container's content out of the database.
     * <BR/>
     * The query or view must at least have the attributes userId and rights.
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
    //use when everybody has flags within his / her views:
        return new StringBuffer ()
            .append ("SELECT DISTINCT oid, state, name, typeCode, typeName, isLink,")
            .append ("        linkedObjectId, owner, ownerName, ownerOid,")
            .append ("        ownerFullname, lastChanged, isNew, icon, description,")
            .append ("        flags, processState")
            .append (" FROM    " + this.viewContent)
            .append (" WHERE   oid = oid")
            .toString ();
    } // createQueryRetrieveSelectionData


    /**************************************************************************
     * Get the element type specific data out of the actual tuple of the query
     * result. <BR/>
     * This metod is used to get all attributes of one element out of the
     * resultset. The attribute names which can be used are the ones which
     * are defined within the resultset of
     * This method can be overwritten in subclasses. <BR/>
     *
     * @param   action      The action for the database connection.
     * @param   commonObj   Object representing the list element.
     *
     * @exception   DBError
     *              Error when executing database statement.
     */
    protected void getContainerElementData (SQLAction action,
                                            ContainerElement commonObj)
        throws DBError
    {
        // convert common element object to actual type:
        SingleSelectionContainerElement_01 obj =
            (SingleSelectionContainerElement_01) commonObj;
        // get common attributes:
        super.getContainerElementData (action, obj);

        obj.updateFieldName = this.updateFieldName;
    } // getContainerElementData


    /**************************************************************************
     * Create the heading of a list method for IE4.0. <BR/>
     * The property <A HREF="#orderings">orderings</A> is queried to determine
     * if a column shall be orderable or not. If orderings[i] == null the column
     * i is not orderable.
     *
     * @param   headings            Headings to be shown.
     * @param   orderings           Orderings for the headings.
     * @param   orderBy             Property, by which the result is
     *                              sorted.
     * @param   orderHow            Kind of ordering: BOConstants.ORDER_ASC or BOConstants.ORDER_DESC
     *                              null => BOConstants.ORDER_ASC
     *
     * @return  Table row containing the heading of the list.
     */
    protected RowElement createHeading (String[] headings, String[] orderings,
                                        int orderBy, String orderHow)
    {
        // disable the possibility to change the ordering in the containers content
        return this.createHeading (headings, orderings, orderBy, orderHow, false);
    } // createHeading

} // class SingleSelectionContainer_01
