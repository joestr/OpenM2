/*
 * Class: QuerySelectContainer_01.java
 */

// package:
package ibs.obj.menu;

// imports:
import ibs.bo.BOConstants;
import ibs.bo.BOListConstants;
import ibs.bo.BOTokens;
import ibs.bo.ContainerElement;
import ibs.bo.OID;
import ibs.bo.Operations;
import ibs.bo.SingleSelectionContainer_01;
import ibs.bo.States;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.menu.QuerySelectContainerElement_01;
import ibs.service.user.User;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLConstants;
import ibs.tech.sql.SQLHelpers;


/******************************************************************************
 * This is a QuerySelectContainer to select objects. <BR/>
 * It works together with the formproperty type DT_SEARCHTEXTFUNCTION. <BR/>
 *
 * @version     $Id: QuerySelectContainer_01.java,v 1.18 2013/01/18 10:38:19 rburgermann Exp $
 *
 * @author      Monika Eisenkolb (ME), 161001
 ******************************************************************************
 */
public class QuerySelectContainer_01 extends SingleSelectionContainer_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: QuerySelectContainer_01.java,v 1.18 2013/01/18 10:38:19 rburgermann Exp $";

    /**************************************************************************
     * Creates a QuerySelectContainer object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     */
    public QuerySelectContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();
        this.searchColumnName = "name";
    } // SelectionContainer


    /**************************************************************************
     * Creates a QuerySelectContainer object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public QuerySelectContainer_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
        this.searchColumnName = "name";
    } // QuerySelectContainer


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        this.elementClassName = "ibs.obj.menu.QuerySelectContainerElement_01";
        this.name = "";
        this.icon = "QuerySelectContainer.gif";
    } // initClassSpecifics


    /**************************************************************************
     * Create the query to get the container's content out of the database.
     * <BR/>
     * The query or view must at least have the attributes uid and rights.
     * Queries on these attributes have to be addable to this query. <BR/>
     * The query must have at least one constraint within WHERE to ensure that
     * other constraints can be added with AND. <BR/>
     * <B>Standard format:</B><BR/>
     *      "SELECT DISTINCT oid, &lt;other attributes> " +
     *      " FROM " + this.viewContent +
     *      " WHERE containerId = " + oid;&lt;BR>
     * This method can be overwritten in subclasses. <BR/>
     * As you can see the query should contain at least the oid.
     *
     * @return  The constructed query.
     */
    protected String createQueryRetrieveSelectionData ()
    {
        return
            "SELECT DISTINCT o.oid, o.state, o.name, o.typeCode, o.typeName, o.isLink," +
                " o.linkedObjectId, o.owner, o.ownerName, o.ownerOid," +
                " o.ownerFullname, o.lastChanged, o.isNew, o.icon," +
                " o.description, o.flags, o.processState" +
            " FROM " + this.viewContent + " o" +
            " WHERE o.state = " + States.ST_ACTIVE +
                " and o.iscontainer = " + SQLConstants.BOOL_TRUE +
                " and o.containerkind = " + BOConstants.CONT_STANDARD +
                " and o.showinmenu = " + SQLConstants.BOOL_TRUE +
                " and o.oid in" +
                    " (select distinct c.containerid" +
                    " FROM v_Container$rights c" +
                    " where c.state = " + States.ST_ACTIVE +
                        " and c.showinmenu = " + SQLConstants.BOOL_TRUE +
                        " and c.userId = " + this.user.id +
                    SQLHelpers.getStringCheckRights (Operations.OP_VIEW) +
                    ")" +
                " and o.oid not in" +
                    " (select m.objectoid" +
                    " from ibs_menutab_01 m)" +
            // exclude workspaces
            " and o.containerId NOT IN" +
                " (select workspacesOid" +
                " from ibs_domain_01)";
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
        QuerySelectContainerElement_01 obj = (QuerySelectContainerElement_01) commonObj;

        // get common attributes:
        super.getContainerElementData (action, obj);
    } // getContainerElementData


    /**************************************************************************
     * Set the headings for this container. This method MUST be overloaded if
     * you have your own subclass of ContainerElement and if you need other
     * headings. <BR/>
     * You have to overload the method setOrderings () as well.
     */
    protected void setHeadingsAndOrderings ()
    {
        this.headings = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
            BOListConstants.LST_HEADINGS_QUERYSELECTCONTAINER, env);
        
        this.orderings = BOListConstants.LST_ORDERINGS_QUERYSELECTCONTAINER;

        // ensure that there is an available ordering taken:
        this.ensureAvailableOrdering ();
    } // setHeadingsAndOrderings

} // class QuerySelectContainer_01
