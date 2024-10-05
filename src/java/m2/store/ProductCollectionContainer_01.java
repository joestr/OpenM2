/*
 * Class: ProductCollectionContainer_01.java
 */

// package:
package m2.store;

// imports:
import ibs.bo.Buttons;
import ibs.bo.Container;
import ibs.bo.OID;
import ibs.service.user.User;


/******************************************************************************
 * This class represents one container object of type ProductCollectionContainer
 * with version 01.
 * <BR/>
 *
 * @version     $Id: ProductCollectionContainer_01.java,v 1.8 2009/07/25 00:43:12 kreimueller Exp $
 *
 * @author      Bernhard Walter (BW), 981226
 ******************************************************************************
 */
public class ProductCollectionContainer_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ProductCollectionContainer_01.java,v 1.8 2009/07/25 00:43:12 kreimueller Exp $";

    /**
     * Icon for the object. <BR/>
     */
    private static final String CLASS_ICON = "ProductCollectionContainer.gif";


    /**************************************************************************
     * This constructor creates a new instance of the class ProductCollectionContainer_01.
     * <BR/>
     */
    public ProductCollectionContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:

        // init specifics of actual class:
    } // ProductCollectionContainer_01


    /**************************************************************************
     * Creates a ProductCollectionContainer_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     * <A HREF="#elements">elements</A> is initialized. <BR/>
     * <A HREF="#orderBy">orderBy</A> is initialized to
     *      <A HREF="ibs.util.UtilConstants.html#LST_DEFAULTORDERING">UtilConstants.LST_DEFAULTORDERING</A>. <BR/>
     * <A HREF="#orderHow">orderHow</A> is initialized to
     *      <A HREF="ibs.util.BOConstants.html#ORDER_ASC">BOConstants.ORDER_ASC</A>. <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public ProductCollectionContainer_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);

        // initialize properties common to all subclasses:

        // init specifics of actual class:
    } // ProductCollectionContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set type-specific attributes:
        this.icon = ProductCollectionContainer_01.CLASS_ICON;
    } // initClassSpecifics


    /**************************************************************************
     * Set the icon of the actual business object. <BR/>
     * If the icon is already set this method leaves it as is.
     * If there is no icon defined yet, the icon name is derived from the name
     * of the type of this object. <BR/>
     */
    protected void setIcon ()
    {
        this.icon = ProductCollectionContainer_01.CLASS_ICON;
    } // setIcon


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

} // class ProductCollectionContainer_01
