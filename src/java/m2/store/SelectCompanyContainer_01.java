/*
 * Class: SelectCompanyContainer_01.java
 */

// package:
package m2.store;

// imports:
import ibs.bo.ContainerElement;
import ibs.bo.OID;
import ibs.bo.SingleSelectionContainer_01;
import ibs.service.user.User;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;

import m2.mad.MadTypeConstants;


/******************************************************************************
 * This is a SelectionContainer to select User. <BR/>
 * It works together with the formpropertytype DT_SEARCHTEXTFUNCTION. <BR/>
 *
 * @version     $Id: SelectCompanyContainer_01.java,v 1.11 2013/01/18 10:38:18 rburgermann Exp $
 *
 * @author      Andreas Jansa (AJ), 990120
 ******************************************************************************
 */
public class SelectCompanyContainer_01 extends SingleSelectionContainer_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: SelectCompanyContainer_01.java,v 1.11 2013/01/18 10:38:18 rburgermann Exp $";


    /**************************************************************************
     * Creates a SelectUserContainer object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     */
    public SelectCompanyContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // init specifics of actual class:
    } // SelectionContainer


    /**************************************************************************
     * Creates a SelectUserContainer object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public SelectCompanyContainer_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);

        // init specifics of actual class:
    } // SelectionContainer


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        this.elementClassName = "ibs.bo.SingleSelectionContainerElement_01";
        this.icon = "SelectCompanyContainer.gif";

        this.searchColumnName =
            SQLHelpers.getStrCat (SQLHelpers.getStrCat ("name", "' '"),
                                  "legal_form").toString ();

        this.name = "";
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
        return new StringBuffer ()
            .append (" SELECT DISTINCT o.oid, o.state, o.name, o.typeCode, o.typeName,")
                .append (" o.isLink, o.linkedObjectId, o.owner, o.ownerName,")
                .append (" o.ownerOid, o.ownerFullname, o.lastChanged, o.isNew,")
                .append (" o.icon, o.description, o.flags, o.processState,")
                .append (" c.legal_form AS legalform ")
            .append (" FROM ").append (this.viewContent).append (" o,")
                .append (" mad_Company_01 c")
            .append (" WHERE tVersionId =")
                .append (Integer.toString (this.getTypeCache ()
                    .getTVersionId (MadTypeConstants.TC_Company)))
                .append (" AND c.oid = o.oid ")
            .toString ();
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
        // get common attributes:
        super.getContainerElementData (action, commonObj);

        // add legal form to name of company
        // (made the same in stored proc. p_Company$retrieve)
        commonObj.name += " " + action.getString ("legalform");
    } // getContainerElementData

} // class SelectCompanyContainer_01
