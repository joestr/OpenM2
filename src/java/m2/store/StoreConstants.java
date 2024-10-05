/*
 * Class: m2Constants_02.java
 */

// package:
package m2.store;

// imports:
import ibs.bo.BOTokens;


/******************************************************************************
 * Constants for the store component. <BR/>
 * This abstract class contains all constants which are necessary to deal with
 * the classes delivered within this package.<P>
 *
 * @version     $Id: StoreConstants.java,v 1.9 2010/04/07 13:37:07 rburgermann Exp $
 *
 * @author      Rupert Thurner (RT), 980518
 ******************************************************************************
 */
public abstract class StoreConstants extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: StoreConstants.java,v 1.9 2010/04/07 13:37:07 rburgermann Exp $";


    // default values:
    /**
     * Default value: sort key. <BR/>
     */
    public static final int DEF_SORTKEY = 50;
    /**
     * Default value: cost. <BR/>
     */
    public static final int DEF_COST = 0;
    /**
     * Default value: sales price. <BR/>
     */
    public static final int DEF_SALESPRICE = 0;
    /**
     * Default value: stock. <BR/>
     */
    public static final int DEF_STOCK = 1;
    /**
     * Default value: quantity. <BR/>
     */
    public static final int DEF_QTY = 1;
    /**
     * Zero value for money. <BR/>
     */
    public static final long CONST_MONEY_ZERO = 0;

    /**
     * . <BR/>
     */
    public static final String[] LST_HEADINGS_COLOR =
    {
        BOTokens.ML_NAME,
        BOTokens.ML_TYPE,
        BOTokens.ML_OWNER,
        BOTokens.ML_CHANGED,
        StoreTokens.ML_COD_SORT,
    }; // LST_HEADINGS_COLOR

    /**
     * . <BR/>
     */
    public static final String[] LST_ORDERINGS_COLOR =
    {
        "name",
        "typeName",
        "owner",
        "lastChanged",
        "sortKey",
    }; // LST_ORDERINGS_COLOR

    /**
     * . <BR/>
     */
    public static final String[] LST_HEADINGS_SIZE =
        StoreConstants.LST_HEADINGS_COLOR;

    /**
     * . <BR/>
     */
    public static final String[] LST_ORDERINGS_SIZE =
        StoreConstants.LST_ORDERINGS_COLOR;

    /**
     * . <BR/>
     */
    public static final String[] LST_HEADINGS_PRODSIZCOL =
    {
        StoreTokens.ML_PRODUCTNO,
        StoreTokens.ML_SIZENAME,
        StoreTokens.ML_COLORNAME,
        StoreTokens.ML_COST,
        StoreTokens.ML_QTY,
    }; // LST_HEADINGS_PRODSIZCOL

    /**
     * . <BR/>
     */
    public static final String[] LST_ORDERINGS_PRODSIZCOL =
    {
        "productNo",
        null,
        null,
        "cost",
        "qty",
    }; // LST_ORDERINGS_PRODSIZCOL

    /**
     * Width of a thumbnail image in a product catalog. <BR/>
     */
    public static final int CONST_WIDTH_THUMBNAIL = 75;
    /**
     * Width of a image of a product . <BR/>
     */
    public static final int CONST_WIDTH_IMAGE = 200;

    /**
     * . <BR/>
     */
    public static final int CONST_FIRSTTAB = 50;

    /**
     * . <BR/>
     */
    public static final int CONST_SECONDTAB = 20;
    /**
     * . <BR/>
     */
    public static final int CONST_MAX_QTY = 3;

    /**
     * . <BR/>
     */
    public static final String CONST_SIGNATURE_LINE =
        "........................................................";


    // frame names:
    /**
     * . <BR/>
     */
    public static final String FRM_ORDER_UP         = new String ("order_up");
    /**
     * . <BR/>
     */
    public static final String FRM_ORDER_DOWN       = new String ("order_down");

    /**
     * server side include file header in print-order-form. <BR/>
     */
    public static final String SSI_ORDERHEADER = "orderheader.htm";

    /**
     * server side include file footer in print-order-form. <BR/>
     */
    public static final String SSI_ORDERFOOTER = "orderfooter.htm";

    /**
     * If no price is given set to this constant. <BR/>
     */
    public static final long PRICE_NOT_SET = Long.MIN_VALUE;

    /**
     * . <BR/>
     */
    public static final String CLASS_PRODUCT_ORDER_HEADER =
        StoreConstants.CLASS_ORDER_HEADER;
    /**
     * . <BR/>
     */
    public static final String CLASS_PRODUCT_ORDER_DIMENSION = "odi";
    /**
     * . <BR/>
     */
    public static final String CLASS_PRODUCT_ORDER_VALUE = "ovl";

    /**
     * class used in stylesheet styleCatalog.css. <BR/>
     */
    public static final String CLASS_PRODUCT_ORDER_NAME = "ona";

    /**
     * . <BR/>
     */
    public static final String CLASS_PRODUCT_ORDER_QUANTITY = "oqt";
    /**
     * . <BR/>
     */
    public static final String CLASS_PRODUCT_ORDER_MATRIX = "matrixtable";
    /**
     * . <BR/>
     */
    public static final String CLASS_PRODUCT_ORDER_TABLE = "ordertable";
    /**
     * . <BR/>
     */
    public static final String CLASS_PRODUCT_COLLECTION_SPECIAL = "csp";
    /**
     * . <BR/>
     */
    public static final String CLASS_PRODUCTGROUP_NUMBER = "pno";
    /**
     * . <BR/>
     */
    public static final String CLASS_PRODUCTGROUP_NAME = "pname";
    /**
     * . <BR/>
     */
    public static final String CLASS_PRODUCTGROUP_DESCR = "pdesc";
    /**
     * . <BR/>
     */
    public static final String CLASS_PRODUCTGROUP_PRICE = "ppric";
    /**
     * . <BR/>
     */
    public static final String CLASS_ORDER_HEADER = "ohd";
    /**
     * . <BR/>
     */
    public static final String[] LST_CLASSORDERROWS = {"or1", "or2"};
    /**
     * . <BR/>
     */
    public static final String CLASS_ORDER_LISTDESCRIPTION = "olistdesc";

} // class StoreConstants
