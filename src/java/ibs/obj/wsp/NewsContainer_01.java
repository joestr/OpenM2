/*
 * Class: NewsContainer_01.java
 */

// package:
package ibs.obj.wsp;

// imports:
import ibs.bo.BOArguments;
import ibs.bo.BOConstants;
import ibs.bo.BOListConstants;
import ibs.bo.BOMessages;
import ibs.bo.BOTokens;
import ibs.bo.Buttons;
import ibs.bo.Container;
import ibs.bo.Datatypes;
import ibs.bo.OID;
import ibs.bo.Operations;
import ibs.io.Environment;
import ibs.io.IOHelpers;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.html.TableElement;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLConstants;
import ibs.tech.sql.SQLHelpers;
import ibs.util.UtilConstants;


/******************************************************************************
 * This class represents one object of type NewsContainer with version 01. <BR/>
 * HINT: NewsContainer can be retrieved from the database or be
 * purely generated. <BR/>
 *
 * @version     $Id: NewsContainer_01.java,v 1.20 2010/04/13 15:55:57 rburgermann Exp $
 *
 * @author      Klaus Reimüller (KR), 980428
 ******************************************************************************
 */
public class NewsContainer_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: NewsContainer_01.java,v 1.20 2010/04/13 15:55:57 rburgermann Exp $";


    /**************************************************************************
     * This constructor creates a new instance of the class NewsContainer_01.
     * <BR/>
     */
    public NewsContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // NewsContainer_01


    /**************************************************************************
     * Creates a NewsContainer_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public NewsContainer_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
    } // NewsContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        this.isMajorContainer = true;
        this.viewContent = "v_NewsContainer$content";
        this.name = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NEWS, env);
        this.setIcon ();

        this.elementClassName = "ibs.obj.wsp.NewsContainerElement_01";

        this.orderBy = 1;
        this.ownOrdering = true;
        this.orderHow = BOConstants.ORDER_DESC;

        // set maximum number of entries, that will be shown in list:
        this.maxElements = BOConstants.MAX_CONTENT_ELEMENTS;

        // overwrite alert message if max number of entries exceeded:
        this.msgDisplayableElements = BOMessages.ML_MSG_DISPLAYABLEELEMENTS;
    } // initClassSpecifics


    /**************************************************************************
     * Initializes a BusinessObject object. <BR/>
     * The compound object id is stored in the <A HREF="#oid">oid</A> property
     * of this object. <BR/>
     * The <A HREF="#user">user object</A> is also stored in a specific
     * property of this object to make sure that the user's context can be used
     * for getting his/her rights. <BR/>
     * <A HREF="#env">env</A> is initialized to the provided object. <BR/>
     * <A HREF="#sess">sess</A> is initialized to the provided object. <BR/>
     * <A HREF="#app">app</A> is initialized to the provided object. <BR/>
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     * @param   env     The actual environment object.
     * @param   sess    The actual session info object.
     * @param   app     The global application info object.
     */
    public void initObject (OID oid, User user, Environment env,
                            SessionInfo sess, ApplicationInfo app)
    {
        super.initObject (oid, user, env, sess, app);
        if (this.sess != null)
        {
            if (this.getUserInfo () != null)
            {
                if (this.getUserInfo ().userProfile != null)
                {
                    if (this.getUserInfo ().userProfile.showExtendedAttributes)
                    {
                        this.orderBy = 3;
                        this.ownOrdering = true;
                        this.orderHow = BOConstants.ORDER_DESC;
                    } // if
                } // if
            } // if
        } // if
    } // initObject


    /**************************************************************************
     * Represent the properties of a NewsContainer object to the user and
     * displays the filter elements set. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showProperties (TableElement table)
    {
        super.showProperties (table);
        // show filter elements
        this.showProperty (table, BOArguments.ARG_NEWSTIMELIMIT, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NEWSTIMELIMIT, env),
            Datatypes.DT_INTEGER, this.getUserInfo ().userProfile.newsTimeLimit);
        // newsShowOnlyUnread
        this.showProperty (table, BOArguments.ARG_NEWSSHOWONLYUNREAD, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NEWSSHOWONLYUNREAD, env),
            Datatypes.DT_BOOL, "" + this.getUserInfo ().userProfile.newsShowOnlyUnread);
    } // showProperties


    /**************************************************************************
     * Create the query to get the container's content out of the database.
     * <BR/>
     *
     * @return  The constructed query.
     */
    protected String createQueryRetrieveContentData ()
    {
        String newsTimeLimit = "1";
        StringBuffer queryStr;

        // take news timelimit from the user's profile
        if (this.getUserInfo ().userProfile.newsTimeLimit > 0)
        {
            newsTimeLimit = "" + this.getUserInfo ().userProfile.newsTimeLimit;
        } // if

        queryStr = new StringBuffer ()
            .append ("SELECT  DISTINCT")
            .append ("        oid, state, name, typeName, isLink,")
            .append ("        linkedObjectId, owner, ownerName, ownerOid, description,")
            .append ("        ownerFullname, lastChanged, isNew, icon, flags")
            .append (" FROM ").append (this.viewContent)
            .append (" WHERE ")
            .append (SQLHelpers.getDateDiff (new StringBuffer ("lastChanged"),
                                             SQLHelpers.getActDateTime (),
                                             SQLConstants.UNIT_DAY))
            .append (" < ").append (newsTimeLimit);

        // check if only unread news to show
        if (this.getUserInfo ().userProfile.newsShowOnlyUnread)
        {
            queryStr
                .append (" AND isNew = ").append (UtilConstants.QRY_TRUE);
        } // if

//debug("queryStr : " + queryStr);

        return queryStr.toString ();
    } // createQueryRetrieveContentData


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
            Buttons.BTN_EDIT,
//            Buttons.BTN_DELETE,
//            Buttons.BTN_CUT,
//            Buttons.BTN_COPY,
//            Buttons.BTN_DISTRIBUTE,
            Buttons.BTN_SEARCH,
//            Buttons.BTN_HELP,
        }; // buttons

        // return button array
        return buttons;
    } // setInfoButtons


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
            Buttons.BTN_SEARCH,
//            Buttons.BTN_HELP,
//           Buttons.BTN_LISTDELETE,
//           Buttons.BTN_REFERENCE,
        }; // buttons

        // return button array
        return buttons;
    } // setContentButtons


    /**************************************************************************
     * Returns the number of entries in the newsContainer. <BR/>
     *
     * @return  Number of entries found in the newsContainer
     */
    public int getEntries ()
    {
        SQLAction action = null;        // the action object used to access the
                                        // database
        int noOfEntries = 0;            // number of unread entries
        int rowCount;                   // row counter
        StringBuffer queryStr;          // the query
        String newsTimeLimit = "1";

        // take news timelimit from the user's profile
        if (this.getUserInfo ().userProfile.newsTimeLimit > 0)
        {
            newsTimeLimit = "" + this.getUserInfo ().userProfile.newsTimeLimit;
        } // if

        // get the elements out of the database:
        // create the SQL String to select all tuples
        queryStr = new StringBuffer ()
            .append (" SELECT COUNT(*) as unreadMsg ")
            .append (" FROM   ").append (this.viewContent)
            .append (" WHERE  userId = ").append (this.user.id)
            .append (" AND ")
            .append (SQLHelpers.getDateDiff (new StringBuffer ("lastChanged"),
                                             SQLHelpers.getActDateTime (),
                                             SQLConstants.UNIT_DAY))
            .append (" < ").append (newsTimeLimit).append (" ");

        // check if only unread news to show
        if (this.getUserInfo ().userProfile.newsShowOnlyUnread)
        {
            queryStr
                .append (" AND isNew = ").append (UtilConstants.QRY_TRUE);
        } //if
        queryStr
            .append (SQLHelpers.getStringCheckRights (Operations.OP_VIEW));

//debug ("QUERYSTRING: " + queryStr);

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

            // get number of entries
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
    } // getEntries


    /**************************************************************************
     * Set the headings and the orderings for this container. <BR/>
     * Overload this function if you need other headings as the standard container. <BR/>
     */
    protected void setHeadingsAndOrderings ()
    {
        if (!this.getUserInfo ().userProfile.showExtendedAttributes)
        {
            // reduced list
            // set headings:
            this.headings = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                BOListConstants.LST_HEADINGS_NEWSCONTAINERREDUCED, env);
            // set ordering attributes for the corresponding headings:
            this.orderings = BOListConstants.LST_ORDERINGS_NEWSCONTAINERREDUCED;
        } // if
        else
        {
            super.setHeadingsAndOrderings ();
        } // else
        // ensure that there is an available ordering taken:
        this.ensureAvailableOrdering ();
    } // setHeadingsAndOrderings

} // class NewsContainer_01
