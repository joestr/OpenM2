/*
 * Class: StoreFunctions.java
 */

// package:
package m2.store;

// imports:


/******************************************************************************
 * Functions for m2. <BR/>
 * This abstract class contains all Functions which are necessary to deal with
 * the classes delivered within this package.<P>
 * Contains application defined object types and derived types with IDs from
 * 0x0101 to 0xFFFF. <BR/>
 *
 * The functions classes for the different components have to be at the
 * following numbers: <BR/>
 * <UL>
 * <LI>documents:           2001 .. 2999    FCT_D...
 * <LI>diary:               3001 .. 3999    FCT_T...
 * <LI>master data:         4001 .. 4999    FCT_S...
 * <LI>catalog of products: 5001 .. 5999    FCT_W...
 * <LI>discussions:         6001 .. 6999    FCT_N...
 * <LI>phone book:          7001 .. 7999    FCT_PB...
 * </UL>
 *
 * @version     $Id: StoreFunctions.java,v 1.11 2007/07/31 19:14:03 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR), 98049
 ******************************************************************************
 */
public abstract class StoreFunctions extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: StoreFunctions.java,v 1.11 2007/07/31 19:14:03 kreimueller Exp $";


    /**
     * Open a shoppingcart form for product. <BR/>
     */
    public static final int FCT_PRODUCT_SHOPPINGCART =  5001;

    /**
     * Open a shoppingcart form for product. <BR/>
     */
    public static final int FCT_PRODUCT_ORDER =  5002;

    /**
     * Sends an order to the order recipient. <BR/>
     */
    public static final int FCT_SENDORDER =  5003;
    /**
     * Load the frameset for the . <BR/>
     */
    public static final int FCT_LOADORDER_FRAMESET = 5004;
    /**
     * Show the order form. <BR/>
     */
    public static final int FCT_SHOWORDER_FORM = 5005;
    /**
     * Put product into shopping cart. <BR/>
     */
    public static final int FCT_PUTIN_CART = 5006;
    /**
     * Show the catalog selection or the order form. <BR/>
     */
    public static final int FCT_ORDER_FORM1 = 5007;
    /**
     * Show the order form. <BR/>
     */
    public static final int FCT_ORDER_FORM2 = 5008;
    /**
     * Store the order form and forward it to the recipient. <BR/>
     */
    public static final int FCT_STORE_ORDER = 5009;
    /**
     * Show the order in a separate window. <BR/>
     */
    public static final int FCT_SHOWORDER_PRINT = 5010;

    /**
     * change state of order. <BR/>
     */
    public static final int FCT_SHOWCHANGESTATEORDERFORM  = 5011;


/////////////////////   CATALOG - NEW //////////////////////////////

    /**
     * show order xml-form. <BR/>
     */
    public static final int FCT_XMLORDER_FORM  = 5101;

    /**
     * display search form for product source. <BR/>
     */
    public static final int FCT_PRODUCTSOURCESHOWSEARCHFROM =  8001;

    /**
     * display search result for product source. <BR/>
     */
    public static final int FCT_PRODUCTSOURCESHOWRESULTLIST =  8002;

    /**
     * Show the shopping cart of the current user. <BR/>
     */
    public static final int FCT_SHOWSHOPPINGCART = 9001;

} // class StoreFunctions
