/*
 * Class: Inbox_01.java
 */

// package:
package ibs.obj.wsp;

// imports:
import ibs.bo.BOConstants;
import ibs.bo.BOListConstants;
import ibs.bo.BOTokens;
import ibs.bo.Buttons;
import ibs.bo.Container;
import ibs.bo.ContainerElement;
import ibs.bo.IncorrectOidException;
import ibs.bo.OID;
import ibs.bo.Operations;
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.wsp.InboxElement_01;
import ibs.service.user.User;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;
import ibs.util.UtilConstants;


/******************************************************************************
 * This class represents one object of type Inbox with version 01. <BR/>
 *
 * @version     $Id: Inbox_01.java,v 1.15 2010/04/13 15:55:57 rburgermann Exp $
 *
 * @author      Bernd Buchegger (BB), 980602
 ******************************************************************************
 */
public class Inbox_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Inbox_01.java,v 1.15 2010/04/13 15:55:57 rburgermann Exp $";


    /**************************************************************************
     * This constructor creates a new instance of the class Inbox_01. <BR/>
     */
    public Inbox_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();
    } // Inbox_01


    /**************************************************************************
     * Creates a Inbox_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public Inbox_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
    } // Inbox_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        this.viewContent = "v_Inbox_01$content";

        // the name of the class of one element within the list:
        this.elementClassName = "ibs.obj.wsp.InboxElement_01";

        // default order is lastChanged date:
        this.orderBy = BOListConstants.LST_INBOXDEFAULTORDERING;
        this.ownOrdering = true;

//        this.orderBy = BOListConstants.LST_DEFAULTORDERING;
        this.orderHow = BOConstants.ORDER_DESC;

        // set as top container:
        this.isMajorContainer = true;

        // set the instance's attributes:
    } // initClassSpecifics


    /**************************************************************************
     * Create the query to get the container's content out of the database.
     * <BR/>
     *
     * @return  The constructed query.
     */
    protected String createQueryRetrieveContentData ()
    {
        return new StringBuffer ()
            .append (" SELECT *")
            .append (" FROM ").append (this.viewContent)
            .append (" WHERE containerId = ").append (this.oid.toStringQu ())
            .append (" ")
            .toString ();
    } // createQueryRetrieveContentData


    /**************************************************************************
     * Get the element type specific data out of the actual tuple of the query
     * result. <BR/>
     * This metod is used to get all attributes of one element out of the
     * resultset. The attribute names which can be used are the ones which
     * are defined within the resultset of
     * <A HREF="#createQueryRetrieveContentData">createQueryRetrieveContentData</A>.
     * <BR/>
     * <B>Format:</B><BR/>
     * for oid properties:
     *      obj.&lt;property> = getQuOidValue (action, "&lt;attribute>"); <BR/>
     * for other properties:
     *      obj.&lt;property> = action.get&lt;type> ("&lt;attribute>"); <BR/>
     * The property <B>oid</B> is already gotten. This must not be done within this
     * method. <BR/>
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
        super.getContainerElementData (action, commonObj);

        InboxElement_01 obj = (InboxElement_01) commonObj;
        String stringHelp;

        stringHelp = action.getString ("distributedId");

        obj.creationDate = action.getDate ("creationDate");
        // check if oid available and if available create a link
        stringHelp = action.getString ("distributedId");

        try
        {
            obj.distributeId = new OID (stringHelp);
            obj.isLink = !obj.distributeId.isEmpty ();
        } // try
        catch (IncorrectOidException e)
        {
            // should not occur, display error message:
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch

        obj.distributeType = action.getInt ("distributedTVersionId");
        obj.distributeName  = action.getString ("distributedName");
        obj.distributeTypeName  = action.getString ("distributedTypeName");
        obj.distributeIcon = action.getString ("distributedIcon");
        obj.activities = action.getString ("activities");
        obj.creator = action.getString ("sender");
    } // getContainerElementData


    /**************************************************************************
     * Returns the number of unread entries in the users inbox. <BR/>
     *
     * @return  Number of unread entries found in the users inbox
     */
    public int getUnreadMessages ()
    {
        SQLAction action = null;        // the action object used to access the
                                        // database
        int noOfEntries = 0;            // number of unread entries
        int rowCount;                   // row counter
        StringBuffer queryStr = new StringBuffer (); // the query

        // get the elements out of the database:
        // create the SQL String to select all tuples

        queryStr
            .append (" SELECT COUNT(*) as unreadMsg")
            .append (" FROM ").append (this.viewContent)
            .append (" WHERE containerId = ").append (this.oid.toStringQu ())
            .append (" AND userId = ").append (this.user.id)
            .append (SQLHelpers.getStringCheckRights (Operations.OP_VIEW))
            .append (" AND isNew = ").append (UtilConstants.QRY_TRUE);

        this.debug ("QUERYSTRING: " + queryStr);

        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();
            rowCount = action.execute (queryStr, false);
            // empty resultset?
            if (rowCount == 0)
            {
                // terminate this method:
                return noOfEntries;
            } // if
            // error while executing?
            else if (rowCount < 0)
            {
                // terminate this method:
                return noOfEntries;
            } // else if

            // everything ok - go on
            try
            {
                // get tuples out of db
                while (!action.getEOF ())
                {
                    noOfEntries = action.getInt ("unreadMsg");
                    // step one tuple ahead for the next loop
                    action.next ();
                } // while

                // the last tuple has been processed
                // end transaction
                action.end ();
            } // try
            catch (DBError e)
            {
                // an error occurred - show name and info
                IOHelpers.showMessage (e, this.app, this.sess, this.env, false);
            } // catch
            // get tuples out of db
            // the last tuple has been processed
            // end transaction
            action.end ();
        } // try
        catch (DBError dbErr)
        {
            // an error occurred - show name and info
            IOHelpers.showMessage (dbErr, this.app, this.sess, this.env, false);
        } // catch
        finally
        {
            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            this.releaseDBConnection (action);
        } // finally

        return noOfEntries;
    } // getUnreadMessages


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
//            Buttons.BTN_NEW,
//            Buttons.BTN_PASTE,
//            Buttons.BTN_CLEAN,
            Buttons.BTN_SEARCH,
//            Buttons.BTN_HELP,
            Buttons.BTN_LISTDELETE,
//            Buttons.BTN_REFERENCE,
        }; // buttons

        // return button array
        return buttons;
    } // setContentButtons


    /**************************************************************************
     * Set the headings and the orderings for this container. <BR/>
     * Overload this function if you need other headings as the standard container. <BR/>
     */
    protected void setHeadingsAndOrderings ()
    {
/*        if (!this.sess.userInfo.userProfile.showExtendedAttributes)
            // reduced list
        {
            this.headings = BOListConstants.LST_HEADINGS;
            this.orderings = BOListConstants.LST_ORDERINGS;
        } // if showExtendedAttributes

        else
            // show extended attributes
        {
*/
        // set headings
        this.headings = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
            BOListConstants.LST_HEADINGS_INBOX, env);
        // set ordering attributes
        this.orderings = BOListConstants.LST_ORDERINGS_INBOX;
        // ensure that there is an available ordering taken:
        this.ensureAvailableOrdering ();
    } // setHeadingsAndOrderings

} // class Inbox_01
