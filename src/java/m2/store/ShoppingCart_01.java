/*
 * Class: ShoppingCart_01.java
 */

// package:
package m2.store;

// imports:
import ibs.bo.BOTokens;
import ibs.bo.Buttons;
import ibs.bo.Container;
import ibs.bo.ContainerElement;
import ibs.bo.OID;
import ibs.bo.States;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;


/******************************************************************************
 * This class represents one container object of type Store with version 01.
 * <BR/>
 *
 * @version     $Id: ShoppingCart_01.java,v 1.16 2010/04/13 15:55:57 rburgermann Exp $
 *
 * @author      Rupert Thurner (RT), 980518
 ******************************************************************************
 */
public class ShoppingCart_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ShoppingCart_01.java,v 1.16 2010/04/13 15:55:57 rburgermann Exp $";

    /**
     * Headings of columns. <BR/>
     * These headings are shown at the top of lists.
     */
    public static final String [] LST_HEADINGS_SHOPPINGCART =
    {
        BOTokens.ML_NAME,
        StoreTokens.ML_UNITOFQTY_SHORT,
        StoreTokens.ML_PACKINGUNIT_SHORT,
        StoreTokens.ML_ORDERQTY,
        StoreTokens.ML_TOTALCOST,
    }; // LST_HEADINGS_SHOPPINGCART

    /**
     * Name of a container column. <BR/>
     * These attributes are used for ordering the elements.
     */
    public static final String [] LST_ORDERINGS_SHOPPINGCART =
    {
        "name",
        "unitOfQty",
        "packingUnit",
        "quantity",
        null,
    }; // LST_ORDERINGS_SHOPPINGCART

    
     /*************************************************************************
      * This constructor creates a new instance of the class ShoppingCart_01.
      * <BR/>
      */
    public ShoppingCart_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();
    } // PropertyCategoryContainer_01


    /**************************************************************************
     * This constructor creates a new instance of the class ShoppingCart_01.
     * <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public ShoppingCart_01 (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);
    } // PropertyCategoryContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set type-specific attributes:
        this.viewContent = "v_ShoppingCart$content";
        this.elementClassName = "m2.store.OrderElement_01";
        this.ownOrdering = true;
    } // initClassSpecifics


    /**************************************************************************
     * Set the headings for this container. This method MUST be overloaded if. <BR/>
     * you have your own subclass of ContainerElement and if you need other headings. <BR/>
     * You have to overload the method setOrderings () as well.
     */
    protected void setHeadingsAndOrderings ()
    {
        // set the instance's attributes:

        // set headings:
        this.headings = MultilingualTextProvider.getText (
            StoreTokens.TOK_BUNDLE, ShoppingCart_01.LST_HEADINGS_SHOPPINGCART, env);
        
        // set orderings
        this.orderings = ShoppingCart_01.LST_ORDERINGS_SHOPPINGCART;

        // ensure that there is an available ordering taken:
        this.ensureAvailableOrdering ();
    } // setHeadingsAndOrderings


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
//            Buttons.BTN_SEARCH,
//            Buttons.BTN_LOGIN,
            Buttons.BTN_LISTDELETE,
//            Buttons.BTN_REFERENCE,
            Buttons.BTN_ORDER,
        }; // buttons

        // return button array
        return buttons;
    } // setContentButtons


    /**************************************************************************
     * Create the query to get the container's content out of the database.
     * <BR/>
     *
     * @return  The constructed query.
     */
    protected String createQueryRetrieveContentData ()
    {
        return new StringBuffer ()
            .append (" SELECT DISTINCT oid, icon, description, name, productOid,")
            .append ("      quantity, unitOfQty, packingUnit, price, priceCurrency")
            .append (" FROM    ").append (this.viewContent)
            .append (" WHERE   containerId = ").append (this.oid.toStringQu ())
            .append ("  AND   state = ").append (States.ST_ACTIVE)
            .toString ();
    } // createQueryRetrieveContentData


    /**************************************************************************
     * Get the element type specific data out of the actual tuple of the query
     * result. <BR/>
     *
     * @param   action      The database connection object.
     * @param   commonObj   Object representing the list element.
     *
     * @throws  DBError
     *          Error when executing database statement.
     */
    protected void getContainerElementData (SQLAction action, ContainerElement commonObj)
        throws DBError
    {
        OrderElement_01 obj = (OrderElement_01) commonObj;
        // get element type specific attributes:
        obj.name = action.getString ("name");
        obj.icon = action.getString ("icon");
        obj.description = action.getString ("description");
        obj.productOid = SQLHelpers.getQuOidValue (action, "productOid");
        obj.quantity = action.getInt ("quantity");
        obj.unitOfQty = action.getInt ("unitOfQty");
        obj.packingUnit = action.getString ("packingUnit");
        obj.priceCurrency = action.getString ("priceCurrency");
        obj.price = action.getCurrency ("price");
        if ((this.sess != null) && (this.sess.activeLayout != null))
        {
            obj.layoutpath = this.sess.activeLayout.path;
        } // if
    } // getContainerElementData

} // class ShoppingCart_01
