/*
 * Class: DomainSchemeContainer_01.java
 */

// package:
package ibs.obj.dom;

// imports:
import ibs.bo.BOConstants;
import ibs.bo.Container;
import ibs.bo.ContainerElement;
import ibs.bo.OID;
import ibs.bo.Operations;
import ibs.bo.States;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.service.user.User;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;
import ibs.util.NoAccessException;

import java.util.Vector;


/******************************************************************************
 * This class represents one object of type Dokument with version 01. <BR/>
 *
 * @version     $Id: DomainSchemeContainer_01.java,v 1.18 2009/07/24 20:57:11 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR), 000220
 *
 * @see     ibs.obj.dom.DomainScheme_01
 * @see     ibs.obj.dom.Domain_01
 ******************************************************************************
 */
public class DomainSchemeContainer_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DomainSchemeContainer_01.java,v 1.18 2009/07/24 20:57:11 kreimueller Exp $";


    /**************************************************************************
     * This constructor creates a new instance of the class
     * DomainSchemeContainer_01. <BR/>
     */
    public DomainSchemeContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();
    } // DomainSchemeContainer_01


    /**************************************************************************
     * Creates a DomainSchemeContainer_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public DomainSchemeContainer_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
    } // DomainSchemeContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set the instance's attributes:
    } // initClassSpecifics


    /**************************************************************************
     * Get the Container's content out of the database. <BR/>
     * <BR/>
     * First this method tries to load the objects from the database. During
     * this operation a rights check is done, too. If this is all right the
     * objects are returned otherwise an exception is raised. <BR/>
     *
     * ATTENTION: THIS METHOD IS A REIMPLEMENTATION OF THE SAME METHOD IN
     * ibs.bo.Container, BECAUSE THERE IS A SPECIAL QUERY PART FOR DELETION
     * FORMS. <BR/>
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
        this.debug (" --- " + this.getClass ().getName () + ".performRetrieveContentData ANFANG --- ");
        String orderHowLocal = orderHow; // variable for local assignments
        Class<? extends ContainerElement> elementClass = null;
        SQLAction action = null;        //SQLAction for Databaseoperations
        ContainerElement obj;
        int rowCount;                   // row counter
        int i;                          // index of actual tuple
        this.size = 0;
        StringBuffer queryStr;

        // ensure a correct ordering:
        if (!orderHowLocal.equalsIgnoreCase (BOConstants.ORDER_DESC)) // not descending?
        {
            orderHowLocal = BOConstants.ORDER_ASC; // order ascending
        } // if

        // empty the elements vector:
        this.elements.removeAllElements ();

        // get the query string
        String query = null;
        // if no elements selected prepare a query to get the content data from the data base
        if (null == selectedElements)
        {
            query = this.createQueryRetrieveContentData ();
        } // if
        // if elements are selected prepare a query to check which may be displayed
        else
        {
            query = this.createQueryCopyData (selectedElements);
        } // else

        // check if the string is null
        if (query == null)
        {
            // no query string constructed: that means abort the db operation
            return;
        } // if (query == null)
        // get the elements out of the database:
        // create the SQL String to select all tuples

        queryStr = new StringBuffer (query);
/*************
 * HACK!
 * necessary for the retrieve of the deletable objects
 * having regard to the checkOut-flag
 */
        if ((operation & Operations.OP_DELETE) == Operations.OP_DELETE)
        {
            queryStr
                .append (" AND")
                    .append (SQLHelpers.getBitAnd ("flags", "16"))
                    .append (" <> 16");

            // check if the actual user is the Administrator:
            // (the administrator is allowed to delete objects which are marked
            // as undeletable)
            if (!this.getUser ().username.equalsIgnoreCase (IOConstants.USERNAME_ADMINISTRATOR) &&
                !this.getUser ().username.equalsIgnoreCase ("Admin"))
                                        // not Administrator
            {
                // ensure that the object is deletable:
                queryStr
                    .append (" AND ")
                        .append (SQLHelpers.getBitAnd (
                                 "flags", "" + BOConstants.FLG_NOTDELETABLE))
                        .append ("<> ").append (BOConstants.FLG_NOTDELETABLE)
                        .append (" ");
            } // if not Administrator

            // ensure that just domain schemes which are not used in a domain can
            // be displayed:
            queryStr
                .append (" AND oid NOT IN")
                .append (" (SELECT ds.oid")
                .append (" FROM ibs_Object obj, ibs_Domain_01 d, ibs_DomainScheme_01 ds")
                .append (" WHERE   obj.oid = d.oid")
                .append (" AND obj.state = ").append (States.ST_ACTIVE)
                .append (" AND d.scheme = ds.id) ");
        } // if
/**************
 * HACK Ende
 *
 */
        queryStr
            .append ("    AND userId = ").append (this.user.id)
            .append (SQLHelpers.getStringCheckRights (operation))
            .append (" ORDER BY ")
            .append (this.orderings[orderBy]).append (" ").append (orderHowLocal);

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();
            this.debug ("vor container action.execute (queryStr, false);");
            rowCount = action.execute (queryStr, false);

            // empty resultset?
            if (rowCount == 0)
            {
                return;                 // terminate this method
            } // if
            // error while executing?
            else if (rowCount < 0)
            {
                return;
            } // else if

            // everything ok - go on

            // get the class of one element of this container:
            elementClass = this.getElementClass ();

            // get tuples out of db
            i = 0;                      // initialize loop counter

            while ((this.maxElements == 0 || i++ < this.maxElements) &&
                                        // maximum number of tuples not reached?
                   !action.getEOF ())   // there are tuples left?
            {
                // create a new object:
                try
                {
                    // create an instance of the element's class:
                    obj = elementClass.newInstance ();

                    // temporary solution for the problem: what happens if
                    // one element occures more then one time in the resultset of
                    // the selectstatement - problem is solved in method
                    // getContainerElementData (see Class ProductGroup_01)

                    // add element to list of elements:
                    this.elements.addElement (obj);
                    // get and set values for element:
                    obj.oid = SQLHelpers.getQuOidValue (action, "oid");
                    // get specific data of container element:
                    this.getContainerElementData (action, obj);
                } // try
                catch (IllegalAccessException e)
                {
                    IOHelpers.showMessage (
                        "DomainSchemeContainer: error when creating new object.",
                        e, this.app, this.sess, this.env, true);
                } // catch
                catch (InstantiationException e)
                {
                    IOHelpers.showMessage (
                        "DomainSchemeContainer: error when creating new object.",
                        e, this.app, this.sess, this.env, true);
                } // catch
                this.size++;
                // step one tuple ahead for the next loop:
                action.next ();
            } // while

            // check if there are more tuples left
            if (!action.getEOF ())
            {
                // indicate that maximum number of elements have been exceeded
                this.areMaxElementsExceeded = true;
            } // if

            // the last tuple has been processed
            // end transaction:
            action.end ();
        } // try
        catch (DBError e)
        {
            // an error occurred - show name and info
            IOHelpers.showMessage (e, this.app, this.sess, this.env, false);
        } // catch
        finally
        {
            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            this.releaseDBConnection (action);
        } // finally
        this.debug (" --- Container.performRetrieveContentData ENDE --- ");
    } // performRetrieveContentData

} // class DomainSchemeContainer_01
