/*
 * Class: ProductBrandContainer_01.java
 */

// package:
package m2.store;

// imports:
import ibs.bo.Buttons;
import ibs.bo.Container;
import ibs.bo.OID;
import ibs.service.user.User;


/******************************************************************************
 * This class represents one container object of type Store with version 01.
 * <BR/>
 *
 * @version     $Id: ProductBrandContainer_01.java,v 1.9 2009/07/25 00:43:12 kreimueller Exp $
 *
 * @author      Bernhard Walter (BW), 981226
 ******************************************************************************
 */
public class ProductBrandContainer_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ProductBrandContainer_01.java,v 1.9 2009/07/25 00:43:12 kreimueller Exp $";


    /**************************************************************************
     * This constructor creates a new instance of the class
     * ProductBrandContainer_01. <BR/>
     */
    public ProductBrandContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:

        // init specifics of actual class:
    } // ProductBrandContainer_01


    /**************************************************************************
     * Creates a ProductBrandContainer_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     * <A HREF="#elements">elements</A> is initialized. <BR/>
     * <A HREF="#orderBy">orderBy</A> is initialized to
     *      <A HREF="ibs.util.UtilConstants.html#LST_DEFAULTORDERING">UtilConstants.LST_DEFAULTORDERING</A>. <BR/>
     * <A HREF="#orderHow">orderHow</A> is initialized to
     *      <A HREF="ibs.util.BOConstants.html#ORDER_ASC">BOConstants.ORDER_ASC</A>. <BR/>
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public ProductBrandContainer_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);

        // initialize properties common to all subclasses:

        // init specifics of actual class:
    } // ProductBrandContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set type-specific attributes:
        this.icon = "ProductBrandContainer.gif";
    } // initClassSpecifics


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in an
     * object info view. <BR/>
     *
     * @return  An array with button ids that can be displayed.
     */
    protected int[] setInfoButtons ()
    {
        // define buttons to be displayed:
        int [] buttons =
        {
            Buttons.BTN_EDIT,
            Buttons.BTN_DELETE,
//            Buttons.BTN_CUT,
//            Buttons.BTN_COPY,
//            Buttons.BTN_DISTRIBUTE,
//            Buttons.BTN_CLEAN,
            Buttons.BTN_SEARCH,
//            Buttons.BTN_HELP,
//            Buttons.BTN_LOGIN
        }; // buttons

        // return button array
        return buttons;
    } // showInfoButtons


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in a
     * container's content view. <BR/>
     *
     * @return  An array with button ids that can potentially be displayed.
     */
    protected int[] setContentButtons ()
    {
        // define buttons to be displayed:
        int[] buttons =
        {
            Buttons.BTN_NEW,
//            Buttons.BTN_PASTE,
//            Buttons.BTN_CLEAN,
//            Buttons.BTN_SEARCH,
            Buttons.BTN_LISTDELETE,
            Buttons.BTN_LIST_COPY,
            Buttons.BTN_LIST_CUT,
//            Buttons.BTN_HELP,
//            Buttons.BTN_LOGIN
        }; // buttons

        // return button array
        return buttons;
    } // setContentButtons

} // class ProductBrandContainer_01
