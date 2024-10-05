/*
 * Class: RecipientContainer_01.java
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
import ibs.bo.OID;
import ibs.io.IOConstants;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.wsp.RecipientContainerElement_01;
import ibs.service.user.User;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;


/******************************************************************************
 * This class represents one object of type TerminplanContainer with version 01.
 * <BR/>
 *
 * @version     $Id: RecipientContainer_01.java,v 1.15 2010/04/13 15:55:57 rburgermann Exp $
 *
 * @author      Heinz Josef Stampfer (HJ), 980526
 ******************************************************************************
 */
public class RecipientContainer_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: RecipientContainer_01.java,v 1.15 2010/04/13 15:55:57 rburgermann Exp $";


    /**************************************************************************
     * This constructor creates a new instance of the class
     * RecipientContainer_01. <BR/>
     */
    public RecipientContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // RecipientContainer_01


    /**************************************************************************
     * Creates a RecipientContainer_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public RecipientContainer_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);

        // initialize properties common to all subclasses:
    } // RecipientContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        this.viewContent = "v_RecipientCont_01$content";
        this.elementClassName = "ibs.obj.wsp.RecipientContainerElement_01";
        // reset properties
        //   fct = FCT_TERM_OVERLAP_FRAME_LIST;
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
        int num = 0;

        // get parameters relevant for super class:
        super.getParameters ();

        // get order column
        if ((num = this.env.getIntParam (BOArguments.ARG_ORDERBY)) !=
            IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
        {
            this.orderBy = num;
        } // if
        else
        {
            this.orderBy = this.getUserInfo ().orderBy; // set actual order column
        } // else

        // get kind of ordering: ASCending or DESCending
        if ((str = this.env.getStringParam (BOArguments.ARG_ORDERHOW)) != null)
        {
            this.orderHow = str;
        } // if
        else
        {
            this.orderHow = this.getUserInfo ().orderHow; // set actual ordering
        } // else
        // ensure valid ordering:
        if (!this.orderHow.equalsIgnoreCase (BOConstants.ORDER_DESC))
        {
            this.orderHow = BOConstants.ORDER_ASC;
        } // if

        // store ordering:
        (this.getUserInfo ()).orderBy = this.orderBy;
        (this.getUserInfo ()).orderHow = this.orderHow;
    } // getParameters


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
//   Buttons.BTN_NEW,
//   Buttons.BTN_PASTE,
//   Buttons.BTN_CLEAN,
            Buttons.BTN_SEARCH,
//   Buttons.BTN_HELP,
//   Buttons.BTN_LISTDELETE,
//   Buttons.BTN_REFERENCE,
        }; // buttons

        // return button array
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
//Buttons.BTN_EDIT,
//Buttons.BTN_DELETE,
//Buttons.BTN_CUT,
//Buttons.BTN_COPY,
//Buttons.BTN_DISTRIBUTE,
//Buttons.BTN_CLEAN,
//Buttons.BTN_SEARCH,
//Buttons.BTN_HELP,
//Buttons.BTN_LOGIN
        }; // buttons

        // return button array
        return buttons;
    } // showInfoButtons


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

        RecipientContainerElement_01 obj = (RecipientContainerElement_01) commonObj;

        obj.creationDate = action.getDate ("creationDate");
        // check if oid available and if available create a link

        obj.recipientName = action.getString ("recipientName");
        action.getDate ("readDate");

        if (!action.wasNull ())
        {
            obj.readDate = action.getDate ("readDate");
        } // if
        else
        {
            obj.readDate = null;
        } // else

        obj.recipientId = SQLHelpers.getQuOidValue (action, "recipientId");
        obj.distributeId = SQLHelpers.getQuOidValue (action, "distributeId");
        obj.distributeIcon = action.getString ("distributeIcon");
        obj.distributeName = action.getString ("distributeName");
    } // getContainerElementData


    /**************************************************************************
     * Set the headings and the orderings for this container. <BR/>
     * Overload this function if you need other headings as the standard
     * container.
     */
    protected void setHeadingsAndOrderings ()
    {
/*        if (!this.getUserInfo ().userProfile.showExtendedAttributes)
            // reduced list
        {
        } // if showExtendedAttributes
        else
            // show extended attributes
        {
*/
        // set headings
        this.headings = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
            BOListConstants.LST_HEADINGS_RECIPIENT, env);
        // set orderings
        this.orderings = BOListConstants.LST_ORDERINGS_RECIPIENT;
        // ensure that there is an available ordering taken:
        this.ensureAvailableOrdering ();
    } // setHeadingsAndOrderings

} // class RecipientContainer_01
