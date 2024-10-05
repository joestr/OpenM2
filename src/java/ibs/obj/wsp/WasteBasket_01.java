/*
 * Class: WasteBasket_01.java
 */

// package:
package ibs.obj.wsp;

// imports:
//KR TODO: unsauber
import ibs.app.AppFunctions;
import ibs.bo.BOConstants;
import ibs.bo.BOListConstants;
import ibs.bo.BOMessages;
import ibs.bo.BOTokens;
import ibs.bo.Buttons;
import ibs.bo.Container;
import ibs.bo.OID;
import ibs.bo.Operations;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.html.TableElement;


/******************************************************************************
 * This class represents one object of type WasteBasket with version 01. <BR/>
 * HINT: WasteBasket can be retreived from the database or be
 * purely generated. <BR/>
 *
 * @version     $Id: WasteBasket_01.java,v 1.14 2010/04/13 15:55:58 rburgermann Exp $
 *
 * @author      Mario Oberdorfer (MO), 001201
 ******************************************************************************
 */
public class WasteBasket_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: WasteBasket_01.java,v 1.14 2010/04/13 15:55:58 rburgermann Exp $";


    /**************************************************************************
     * This constructor creates a new instance of the class WasteBasket_01.
     * <BR/>
     */
    public WasteBasket_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // WasteBasket_01


    /**************************************************************************
     * Creates a WasteBasket_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public WasteBasket_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
    } // WasteBasket_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        this.isMajorContainer = true;
        this.viewContent = "v_WasteBasket_01$delete";
     //   this.name = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NEWS, env); // ???
        this.setIcon ();

        this.elementClassName = "ibs.obj.wsp.WasteBasketElement_01";

        this.orderBy = 1;
        this.ownOrdering = true;
        this.orderHow = BOConstants.ORDER_DESC;

        // set maximum number of entries, that will be shown in list:

        // overwrite alert message if max number of entries exceeded:
        this.msgDisplayableElements = BOMessages.ML_MSG_DISPLAYABLEELEMENTS;
    } // initClassSpecifics


    /**************************************************************************
     * Represent the properties of a NewsContainer object to the user and
     * displays the filter elements set. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showProperties (TableElement table)
    {
        super.showProperties (table);
/*
        // show filter elements
        this.showProperty (table, AppArguments.ARG_NEWSTIMELIMIT, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NEWSTIMELIMIT, env),
            Datatypes.DT_INTEGER, this.getUserInfo ().userProfile.newsTimeLimit);
        // newsShowOnlyUnread
        this.showProperty (table, AppArguments.ARG_NEWSSHOWONLYUNREAD, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NEWSSHOWONLYUNREAD, env),
            Datatypes.DT_BOOL, "" + this.getUserInfo ().userProfile.newsShowOnlyUnread);
*/
    } // showProperties


    /**************************************************************************
     * Create the query to get the container's content out of the database.
     * <BR/>
     *
     * @return  The constructed query.
     */
    protected String createQueryRetrieveContentData ()
    {
        String queryStr =
            "SELECT  DISTINCT " +
            "        oid, state, name, typeName, isLink, " +
            "        linkedObjectId, owner, ownerName, " +
            "        ownerOid, description, isNew, " +
            "        ownerFullname, lastChanged, icon, flags " +
            " FROM  " + this.viewContent +
            " WHERE owner = " + this.user.id +
            " OR changer = " + this.user.id;

        this.debug ("queryStr : " + queryStr);

        return queryStr;
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
            Buttons.BTN_UNDELETE,
        }; // buttons

        // return button array
        return buttons;
    } // setContentButtons


    /**************************************************************************
     * Represent the showUndeleteForm of a WasteBasket_01 object to the user. <BR/>
     */
    public void showUndeleteForm ()
    {
        this.showSelectionForm (0, AppFunctions.FCT_LISTUNDELETE,
                           Operations.OP_READ, AppFunctions.FCT_LISTUNDELETE,
                           MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SELHEADERDELETE, env));
    } // showUndeleteForm


    /**************************************************************************
     * Set the headings and the orderings for this container. <BR/>
     * Overload this function if you need other headings as the standard container. <BR/>
     */
    protected void setHeadingsAndOrderings ()
    {
        // if extended attributes enabled
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

} // class WasteBasket_01
