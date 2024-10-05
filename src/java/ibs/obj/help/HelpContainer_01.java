/*
 * Class: HelpContainer_01.java
 */

// package:
package ibs.obj.help;

// imports:
import ibs.bo.BOListConstants;
import ibs.bo.BOTokens;
import ibs.bo.Buttons;
import ibs.bo.Container;
import ibs.bo.ContainerElement;
import ibs.bo.OID;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.help.HelpContainerElement_01;
import ibs.service.user.User;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;


/******************************************************************************
 * This class represents one object of type HelpContainer with version 01. <BR/>
 *
 * @version     $Id: HelpContainer_01.java,v 1.13 2010/04/13 15:55:58 rburgermann Exp $
 *
 * @author      Mario Stegbauer (MS), 990609
 ******************************************************************************
 */
public class HelpContainer_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: HelpContainer_01.java,v 1.13 2010/04/13 15:55:58 rburgermann Exp $";


    /**************************************************************************
     * This constructor creates a new instance of the class HelpContainer_01.
     * <BR/>
     */
    public HelpContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // HelpContainer_01


    /**************************************************************************
     * Creates a HelpContainer_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public HelpContainer_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
    } // HelpContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        this.elementClassName = "ibs.obj.help.HelpContainerElement_01";
        this.viewContent = "v_HelpCont_01$content";

        // set majorContainer true:
        this.isMajorContainer = true;
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
            .append (" SELECT * ")
            .append (" FROM " + this.viewContent)
            .append (" WHERE  containerId = ").append (this.oid.toStringQu ())
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
        HelpContainerElement_01 obj = (HelpContainerElement_01) commonObj;
        obj.goal = action.getString ("goal");
        obj.showInWindow = this.getUserInfo ().userProfile.showFilesInWindows;
    } // getContainerElementData


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
            Buttons.BTN_DELETE,
            Buttons.BTN_REFERENCE,
            Buttons.BTN_SEARCH,
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
            Buttons.BTN_NEW,
            Buttons.BTN_SEARCH,
            Buttons.BTN_LISTDELETE,
            Buttons.BTN_REFERENCE,
        }; // buttons

        // return button array
        return buttons;
    } // setContentButtons


    /**************************************************************************
     * Set the headings and the orderings for this container. <BR/>
     * Overload this function if you need other headings as the standard
     * container. <BR/>
     */
    protected void setHeadingsAndOrderings ()
    {
        if (!this.getUserInfo ().userProfile.showExtendedAttributes)
        {
            // reduced list
            // set headings:
            this.headings = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                BOListConstants.LST_HEADINGS_HELPCONTAINERREDUCED, env);

            // set ordering attributes for the corresponding headings:
            this.orderings = BOListConstants.LST_ORDERINGS_HELPCONTAINERREDUCED;
        } // if
        else
        {
            // set headings:
            this.headings = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                BOListConstants.LST_HEADINGS_HELPCONTAINER, env);

            // set ordering attributes for the corresponding headings:
            this.orderings = BOListConstants.LST_ORDERINGS_HELPCONTAINER;
        } // else

        // ensure that there is an available ordering taken:
        this.ensureAvailableOrdering ();
    } // setHeadingsAndOrderings

} // HelpContainer_01
