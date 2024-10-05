/*
 * Class: SentObjectContainer_01.java
 */

// package:
package ibs.obj.wsp;

// imports:
import ibs.bo.BOArguments;
import ibs.bo.BOConstants;
import ibs.bo.BOListConstants;
import ibs.bo.BOTokens;
import ibs.bo.Buttons;
import ibs.bo.Container;
import ibs.bo.ContainerElement;
import ibs.bo.Datatypes;
import ibs.bo.OID;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.wsp.SentObjectContainerElement_01;
import ibs.service.user.User;
import ibs.tech.html.TableElement;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLConstants;
import ibs.tech.sql.SQLHelpers;
import ibs.util.DateTimeHelpers;


/******************************************************************************
 * This class represents one object of type SentObjectContainer_01 with version 01.
 * <BR/>
 *
 * @version     $Id: SentObjectContainer_01.java,v 1.25 2010/04/13 15:55:57 rburgermann Exp $
 *
 * @author      Heinz Josef Stampfer (HJ), 980526
 ******************************************************************************
 */
public class SentObjectContainer_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: SentObjectContainer_01.java,v 1.25 2010/04/13 15:55:57 rburgermann Exp $";


    /**************************************************************************
     * This constructor creates a new instance of the class NewsContainer_01.
     * <BR/>
     */
    public SentObjectContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // SentObjectContainer_01


    /**************************************************************************
     * Creates a SentObjectContainer_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public SentObjectContainer_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
    } // SentObjectContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        this.ownOrdering = true;
        this.orderBy = BOListConstants.LST_SENTOBJECTDEFAULTORDERING;
        this.orderHow = BOConstants.ORDER_DESC;
        this.isMajorContainer = true;       //set as top container
        // init the range of the time we want to show BO in SentObjectContainer
        this.elementClassName = "ibs.obj.wsp.SentObjectContainerElement_01";
        this.viewContent = "v_SentObjectCont_01$content";
    } // initClassSpecifics


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
            Buttons.BTN_LISTDELETE,
//            Buttons.BTN_REFERENCE,
//            Buttons.BTN_DELETEENTRIES
        }; // buttons

        // return button array:
        return buttons;
    } // setContentButtons


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

        // return button array:
        return buttons;
    } // setInfoButtons


    /**************************************************************************
     * Create the query to get the container's content out of the database.
     * <BR/>
     *
     * @return  The constructed query.
     */
    protected String createQueryRetrieveContentData ()
    {
/*
        if (AppConstants.DB_TYPE == AppConstants.DB_ORACLE)
        {
            queryStr += " WHERE ((SYSDATE - lastChanged) < " +
                newsTimeLimit + ") ";
        } // if (AppConstants.DB_TYPE == AppConstants.DB_ORACLE)
        else
        {
            queryStr += " WHERE (DATEDIFF (day, lastChanged, getDate ()) < " +
                newsTimeLimit + ") ";
        } // else DB == MSSQL
*/
        StringBuffer filterQueryStr = new StringBuffer ();

        // check if time frame has been set and activated
        if (this.getUserInfo ().userProfile.outboxUseTimeFrame)
        {
            if (this.getUserInfo ().userProfile.outboxTimeFrameFrom != null)
            {
                filterQueryStr
                    .append (" AND ")
                    .append (SQLHelpers.getQueryConditionDateTime (
                        new StringBuffer ("creationDate"),
                        SQLConstants.MATCH_GREATER,
                        new StringBuffer ().append (
                            DateTimeHelpers.dateToString (this.getUserInfo ().userProfile.outboxTimeFrameFrom))
                            .append (" 00:00")));
            } // if
            if (this.getUserInfo ().userProfile.outboxTimeFrameTo != null)
            {
                filterQueryStr
                    .append (" AND ")
                    .append (SQLHelpers.getQueryConditionDateTime (
                        new StringBuffer ("creationDate"),
                        SQLConstants.MATCH_LESS,
                        new StringBuffer (
                            DateTimeHelpers.dateToString (this.getUserInfo ().userProfile.outboxTimeFrameTo))
                            .append (" 23:59")));
            } // if
        } // if
        // check if time limit shall be used
        else if (this.getUserInfo ().userProfile.outboxUseTimeLimit)
        {
            filterQueryStr
                .append (" AND (")
                .append (SQLHelpers.getDateDiff (
                    SQLHelpers.getActDateTime (),
                    new StringBuffer ("creationDate"),
                    SQLConstants.UNIT_DAY))
                .append (" < ")
                .append (this.getUserInfo ().userProfile.outboxTimeLimit)
                .append (") ");
        } // else if

        StringBuffer queryStr = new StringBuffer ()
            .append (" SELECT DISTINCT recipientContainerId, distributeId, distributeTVersionId,")
            .append (" distributeName, distributeTypeName, distributeIcon,")
            .append (" activities, oid, name, state, creationDate, typeName, isLink, linkedObjectID,")
            .append (" owner, ownerName, ownerOid, ownerFullname, lastchanged, isNew, icon, description")
            .append (" FROM ").append (this.viewContent)
            .append ("   WHERE containerId = ").append (this.oid.toStringQu ());

        if (filterQueryStr.length () > 0)
        {
            queryStr.append (filterQueryStr);
        } // if

        return queryStr.toString ();
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
        SentObjectContainerElement_01 obj = (SentObjectContainerElement_01) commonObj;
        obj.creationDate = action.getDate ("creationDate");
        obj.distributeId = SQLHelpers.getQuOidValue (action, "distributeId");
        obj.distributeName  = action.getString ("distributeName");
        obj.distributeType = action.getInt ("distributeTVersionId");
        obj.distributeTypeName = action.getString ("distributeTypeName");
        obj.distributeIcon = action.getString ("distributeIcon");
        obj.activities = action.getString ("activities");
        obj.recipientContainerId = SQLHelpers.getQuOidValue (action, "recipientContainerId");
    } // getContainerElementData


    /**************************************************************************
     * Represent the properties of a Attachment_01 object to the user. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showProperties (TableElement table)
    {
        //  loop through all properties of this object and display them:
        //  super.showProperties (table);

        this.showProperty (table, BOArguments.ARG_NAME,
                      MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NAME, env), Datatypes.DT_NAME, this.name);
        this.showProperty (table, BOArguments.ARG_OWNER,
                      MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_OWNER, env), Datatypes.DT_USER, this.owner);

        // outboxUseTimeLimit
        this.showProperty (table, BOArguments.ARG_OUTBOXUSETIMELIMIT,
                      MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_OUTBOXUSETIMELIMIT, env), Datatypes.DT_BOOL,
                      "" + this.getUserInfo ().userProfile.outboxUseTimeLimit);
        // outboxTimeLimit
        this.showProperty (table, BOArguments.ARG_OUTBOXTIMELIMIT,
                      MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_OUTBOXTIMELIMIT, env), Datatypes.DT_INTEGER,
                      this.getUserInfo ().userProfile.outboxTimeLimit);
        // outboxUseTimeFrame
        this.showProperty (table, BOArguments.ARG_OUTBOXUSETIMEFRAME,
                      MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_OUTBOXUSETIMEFRAME, env), Datatypes.DT_BOOL,
                      "" + this.getUserInfo ().userProfile.outboxUseTimeFrame);

        // outboxTimeFrameFrom - outboxTimeFrameTo
        String timeFrame = "";
        if (this.getUserInfo ().userProfile.outboxTimeFrameFrom != null &&
            this.getUserInfo ().userProfile.outboxTimeFrameTo != null)
        {
            timeFrame = DateTimeHelpers
                .dateToString (this.getUserInfo ().userProfile.outboxTimeFrameFrom) +
                " - " +
                DateTimeHelpers
                    .dateToString (this.getUserInfo ().userProfile.outboxTimeFrameTo);
        } // if
        else if (this.getUserInfo ().userProfile.outboxTimeFrameFrom != null)
        {
            timeFrame = DateTimeHelpers
                .dateToString (this.getUserInfo ().userProfile.outboxTimeFrameFrom) +
                " - ";
        } // else if
        else if (this.getUserInfo ().userProfile.outboxTimeFrameTo != null)
        {
            timeFrame = " - " + DateTimeHelpers
                    .dateToString (this.getUserInfo ().userProfile.outboxTimeFrameTo);
        } // else if
        else
        {
            timeFrame = "";
        } // else
        this.showProperty (table, BOArguments.ARG_OUTBOXTIMEFRAME,
                      MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_OUTBOXTIMEFRAME, env), Datatypes.DT_TEXT,
                      timeFrame);
    } // showProperties


    /**************************************************************************
     * Represent the properties of a Dokument_01 object to the user
     * within a form. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showFormProperties (TableElement table)
    {
        // loop through all properties of this object and display them:

        // super.showFormProperties (table);
        this.showProperty (table, BOArguments.ARG_NAME,
                      MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NAME, env), Datatypes.DT_NAME, this.name);
        this.showProperty (table, BOArguments.ARG_OWNER,
                      MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_OWNER, env), Datatypes.DT_USER, this.owner);

        this.showProperty (table, null, null, Datatypes.DT_SEPARATOR, (String) null);
        // outboxUseTimeLimit
        this.showFormProperty (table, BOArguments.ARG_OUTBOXUSETIMELIMIT,
                          MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_OUTBOXUSETIMELIMIT, env), Datatypes.DT_BOOL,
                          "" + this.getUserInfo ().userProfile.outboxUseTimeLimit);
        // outboxTimeLimit
        this.showFormProperty (table, BOArguments.ARG_OUTBOXTIMELIMIT,
                          MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_OUTBOXTIMELIMIT, env), Datatypes.DT_INTEGER,
                          this.getUserInfo ().userProfile.outboxTimeLimit);
        // outboxUseTimeFrame
        this.showFormProperty (table, BOArguments.ARG_OUTBOXUSETIMEFRAME,
                          MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_OUTBOXUSETIMEFRAME, env), Datatypes.DT_BOOL,
                          "" + this.getUserInfo ().userProfile.outboxUseTimeFrame);
        // outboxTimeFrame From - To
        if (this.getUserInfo ().userProfile.outboxTimeFrameFrom != null)
        {
            this.showFormProperty (table, BOArguments.ARG_OUTBOXTIMEFRAME,
                              MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_OUTBOXTIMEFRAME, env), Datatypes.DT_DATERANGE,
                              DateTimeHelpers.dateToString (this.getUserInfo ().userProfile.outboxTimeFrameFrom));
        } // if
        else
        {
            this.showFormProperty (table, BOArguments.ARG_OUTBOXTIMEFRAME,
                              MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_OUTBOXTIMEFRAME, env), Datatypes.DT_DATERANGE, "");
        } // else
    } // showFormProperties


    /**************************************************************************
     * Set the headings and the orderings for this container. <BR/>
     * Overload this function if you need other headings as the standard container. <BR/>
     */
    protected void setHeadingsAndOrderings ()
    {
/*
        if (!this.getUserInfo ().userProfile.showExtendedAttributes)
            // reduced list
        {
        } // if showExtendedAttributes
        else
            // do not show extended attributes
        {
*/
        // set headings:
        this.headings = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
            BOListConstants.LST_HEADINGS_SENTOBJECTCONTAINER, env);
        // set orderings:
        this.orderings = BOListConstants.LST_ORDERINGS_SENTOBJECTCONTAINER;
        // ensure that there is an available ordering taken:
        this.ensureAvailableOrdering ();
    } // setHeadingsAndOrderings

} // class SentObjectContainer_01
