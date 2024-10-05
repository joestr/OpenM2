/*
 * Class: CleanContainer_01.java
 */

// package:
package ibs.obj.wsp;

// imports:
import ibs.bo.BOListConstants;
import ibs.bo.BOTokens;
import ibs.bo.Buttons;
import ibs.bo.Container;
import ibs.bo.OID;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;


/******************************************************************************
 * This class represents one object of type CleanContainer with version 01. <BR/>
 *
 * @version     $Id: CleanContainer_01.java,v 1.14 2010/04/13 15:55:58 rburgermann Exp $
 *
 * @author      Klaus Reimüller (BB), 980512
 ******************************************************************************
 */
public class CleanContainer_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: CleanContainer_01.java,v 1.14 2010/04/13 15:55:58 rburgermann Exp $";


    /**
     * limit for entries to be listed in the CleanContainer. <BR/>
     */
    public int showMaxEntries = 100;


    /**************************************************************************
     * This constructor creates a new instance of the class CleanContainer_01.
     * <BR/>
     */
    public CleanContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // CleanContainer_01


    /**************************************************************************
     * Creates a CleanContainer_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public CleanContainer_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);

        // initialize properties common to all subclasses:
    } // CleanContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // initialize class attributes:

        // set a default name:
        if (this.name == null || this.name.isEmpty ())
        {
            this.name = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_FUNCTIONCLEAN, env);
        } // if

        // set limits for entries to be listed:
        this.showMaxEntries = 100;

        this.isPhysical = false;
        this.name = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_FUNCTIONCLEAN, env);
        this.setIcon ();

        // set the specific view name:
        this.viewContent = "v_CleanContainer_01$content";
    } // initClassSpecifics


    /**************************************************************************
     * Create the query to get the container's content out of the database.
     * <BR/>
     * The query retrieves all objects that have exceeded their valid until
     * date and are deletable by the actual user.
     * <BR/>
     * The query or view must at least have the attributes userId and rights.
     * Queries on these attributes have to be addable to this query. <BR/>
     * The query must have at least one constraint within WHERE to ensure that
     * other constraints can be added with AND. <BR/>
     * <B>Standard format:</B><BR/>
     *      "SELECT DISTINCT oid, &lt;other attributes> " +
     *      " FROM " + this.viewContent +
     *      " WHERE containerId = " + oid;<BR/>
     * This method can be overwritten in subclasses. <BR/>
     * As you can see the query should contain at least the oid.
     *
     * @return  The constructed query.
     */
    protected String createQueryRetrieveContentData ()
    {
        String queryStr =
            "SELECT DISTINCT oid, state, name, " +
            "        typeName, isLink, " +
            "        linkedObjectId, owner, ownerName , ownerOid, " +
            "        ownerFullname, " +
            "        isNew, icon, description, " +
            "        lastChanged, " +
            "        flags " +
            " FROM   " + this.viewContent +
            " WHERE oid = oid ";

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
            Buttons.BTN_SEARCH,
            Buttons.BTN_CLEAN,
//            Buttons.BTN_HELP,
//            Buttons.BTN_LISTDELETE,
//            Buttons.BTN_REFERENCE,
        }; // buttons

        // return button array
        return buttons;
    } // setContentButtons


    /**************************************************************************
     * Set the headings and the orderings for this container. <BR/>
     * Overload this function if you need other headings as the standard
     * container.
     */
    protected void setHeadingsAndOrderings ()
    {
        if (!this.getUserInfo ().userProfile.showExtendedAttributes)
            // do not show extended attributes
        {
            this.headings = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                BOListConstants.LST_HEADINGS_REDUCED, env);
            this.orderings = BOListConstants.LST_ORDERINGS_REDUCED;
        } // if showExtendedAttributes
        else
            // show extended attributes
        {
            this.headings = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                BOListConstants.LST_HEADINGS, env);
            this.orderings = BOListConstants.LST_ORDERINGS;
        } // else don't show extended attributes

        // ensure that there is an available ordering taken:
        this.ensureAvailableOrdering ();
    } // setHeadingsAndOrderings

} // class CleanContainer_01
