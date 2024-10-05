/*
 * Class: OrderClass_01.java
 */

// package:
package m2.store.xml;

// imports:
import ibs.bo.Buttons;
import ibs.bo.States;
import ibs.di.XMLViewer_01;
import ibs.io.LayoutConstants;
import ibs.tech.html.Page;
import ibs.tech.html.StyleSheetElement;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;


/******************************************************************************
 * Class to be used for all types which are used as orders. <BR/>
 *
 * It's been implemented for woodstars, but could be used for all projects
 * where the different orders are identified via it's m2-type. <BR/>
 *
 * There has to be an entry of the responsible ordertype for all
 * shoppingcartentries in the column 'orderType' in table
 * 'm2_shoppingcartentry_01'. <BR/>
 *
 * @version     $Id: OrderClass_01.java,v 1.8 2007/07/31 19:14:03 kreimueller Exp $
 *
 * @author      Andreas Jansa (AJ) 040201
 ******************************************************************************
 */
public class OrderClass_01 extends XMLViewer_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: OrderClass_01.java,v 1.8 2007/07/31 19:14:03 kreimueller Exp $";


    /**************************************************************************
     * This constructor creates a new instance of the class OrderClass_01. <BR/>
     */
    public OrderClass_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();
        // init specifics of actual class:
    } // OrderClass_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        super.initClassSpecifics ();
        this.showExtendedCreationMenu = false;
    } // initClassSpecifics


    /**************************************************************************
     * Change all type specific data that is not changed by performChangeData.
     * <BR/>
     * This method moves all shoppingcartlines for the current order
     * (identified by its m2-type) from shoppingcart into the current order.
     * <BR/>
     *
     * @param   aAction The action object associated with the connection.
     *
     * @throws  DBError
     *          This exception is always thrown, if there happens
     *          an error during accessing data.
     */
    protected void performChangeSpecificData (SQLAction aAction) throws DBError
    {

        super.performChangeSpecificData (aAction);

        // if order was just created
        if (this.state == States.ST_CREATED)
        {
            // put shopping cart entries for this order form shopping cart to order
            String statement = "UPDATE ibs_Object SET containerId = " +
                this.oid.toStringQu () +
                " WHERE oid IN (SELECT oid FROM m2_shoppingcartentry_01 WHERE orderType = '" +
                this.dataElement.p_typeCode + "') AND containerId = " +
                " (SELECT shoppingCart FROM ibs_workspace WHERE userId = " +
                this.user.id + ")";

            aAction.execute (statement, true);
        } // if state = CREATED
    } // performChangeSpecificData


    /**************************************************************************
     * Insert style sheet information in a standard change form. <BR/>
     *
     * @param   page    The page which holds the form.
     */
    protected void insertChangeFormStyles (Page page)
    {
        // Stylesheetfile wird geladen
        StyleSheetElement style = new StyleSheetElement ();
        style.importSS = this.sess.activeLayout.path + this.env.getBrowser () +
            "/" +
            this.sess.activeLayout.elems[LayoutConstants.SHEETINFO].styleSheet;
        page.head.addElement (style);
        // Stylesheetfile wird geladen
        style = new StyleSheetElement ();
        style.importSS = this.sess.activeLayout.path + this.env.getBrowser () +
            "/" +
            this.sess.activeLayout.elems[LayoutConstants.SHEETLIST].styleSheet;
        page.head.addElement (style);
    } // insertChangeFormStyles


    /**************************************************************************
     * Insert style sheet information in a standard info view . <BR/>
     *
     * @param   page    The page with the info view.
     */
    protected void insertInfoStyles (Page page)
    {
        // Stylesheetfile wird geladen
        StyleSheetElement style = new StyleSheetElement ();
        style.importSS = this.sess.activeLayout.path + this.env.getBrowser () +
            "/" +
            this.sess.activeLayout.elems[LayoutConstants.SHEETINFO].styleSheet;
        page.head.addElement (style);
        // Stylesheetfile wird geladen
        style = new StyleSheetElement ();
        style.importSS = this.sess.activeLayout.path + this.env.getBrowser () +
            "/" +
            this.sess.activeLayout.elems[LayoutConstants.SHEETLIST].styleSheet;
        page.head.addElement (style);
    } // insertInfoStyles


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
            Buttons.BTN_SEARCH,
        }; // buttons

        // return button array
        return buttons;
    } // setInfoButtons

} // class OrderClass_01
