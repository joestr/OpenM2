/*
 * Class: StoreTokens.java
 */

// package:
package m2.store;

// imports:
import ibs.tech.html.IE302;
import ibs.util.UtilConstants;


/******************************************************************************
 * Tokens for the m2 store business objects. <BR/>
 * This abstract class contains all tokens which are necessary to deal with
 * the business objects delivered within this package.
 *
 * @version     $Id: StoreTokens.java,v 1.17 2010/04/07 13:37:08 rburgermann Exp $
 *
 * @author      Thurner Rupert (RT), 980610
 ******************************************************************************
 */
public abstract class StoreTokens extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: StoreTokens.java,v 1.17 2010/04/07 13:37:08 rburgermann Exp $";

    /**
     * Name of bundle where the tokens included. <BR/>
     */
    public static String TOK_BUNDLE = "m2_m2store_tokens";

    /**
     * The product. <BR/>
     */
    public static String ML_PRODUCTNAME = "ML_PRODUCTNAME";
    /**
     * The product. <BR/>
     */
    public static String ML_PRODUCTOID = "ML_PRODUCTOID";
    /**
     * An internal number for the product. <BR/>
     */
    public static String ML_PRODUCTNO = "ML_PRODUCTNO";
    /**
     * The European Article Number of the product. <BR/>
     */
    public static String ML_EAN = "ML_EAN";
    /**
     * The size code which applies to the product. <BR/>
     */
    public static String ML_SIZECODEOID = "ML_SIZECODEOID";
    /**
     * The color code which applies to  the product. <BR/>
     */
    public static String ML_COLORCODEOID = "ML_COLORCODEOID";
    /**
     * The size code which applies to the product. <BR/>
     */
    public static String ML_SIZECODENAME = "ML_SIZECODENAME";
    /**
     * The color code which applies to  the product. <BR/>
     */
    public static String ML_COLORCODENAME = "ML_COLORCODENAME";
    /**
     * The price the customer (dealer) has to pay. <BR/>
     */
    public static String ML_COST = "ML_COST";
    /**
     * The date until the price is valid. <BR/>
     */
    public static String ML_COSTGUARANTEEDTILL = "ML_COSTGUARANTEEDTILL";
    /**
     * The currency used for the price. <BR/>
     */
    public static String ML_COSTCURRENCY = "ML_COSTCURRENCY";
    /**
     * The currency used for the sales price. <BR/>
     */
    public static String ML_PRICECURRENCY = "ML_PRICECURRENCY";
    /**
     * The price of a product. <BR/>
     */
    public static String ML_PRICE = "ML_PRICE";
    /**
     * The recommended sales price. <BR/>
     */
    public static String ML_SALESPRICE = "ML_SALESPRICE";
    /**
     * The short form for sales price. <BR/>
     */
    public static String ML_SALESPRICE_SHORT = "ML_SALESPRICE_SHORT";
    /**
     * A user defined value. <BR/>
     */
    public static String ML_USERPRICE1 = "ML_USERPRICE1";
    /**
     * A user defined value. <BR/>
     */
    public static String ML_USERPRICE2 = "ML_USERPRICE2";
    /**
     * Old cost of a product. <BR/>
     */
    public static String ML_COSTOLD = "ML_COSTOLD";
    /**
     * Old price of a product. <BR/>
     */
    public static String ML_PRICEOLD = "ML_PRICEOLD";
    /**
     * The date when product is available. <BR/>
     */
    public static String ML_AVAILABLEFROM = "ML_AVAILABLEFROM";
    /**
     * The smallest unit of quantity a customer may order. <BR/>
     */
    public static String ML_UNITOFQTY = "ML_UNITOFQTY";
    /**
     * The short form of unit of quantity. <BR/>
     */
    public static String ML_UNITOFQTY_SHORT = "ML_UNITOFQTY_SHORT";
    /**
     * The name of the unit. <BR/>
     */
    public static String ML_PACKINGUNIT = "ML_PACKINGUNIT";
    /**
     * The short form of packing unit. <BR/>
     */
    public static String ML_PACKINGUNIT_SHORT = "ML_PACKINGUNIT_SHORT";
    /**
     * If thumbnail is just a smaller image. <BR/>
     */
    public static String ML_THUMBASIMAGE = "ML_THUMBASIMAGE";

    /**
     * The thumbnail image of the product. <BR/>
     */
    public static String ML_THUMBNAIL = "ML_THUMBNAIL";


    /**
     * The image of a product. <BR/>
     */
    public static String ML_IMAGE = "ML_IMAGE";
    /**
     * Description of the availablity. <BR/>
     */
    public static String ML_STOCK = "ML_STOCK";
    /**
     * The size of the product. <BR/>
     */
    public static String ML_SIZEOID = "ML_SIZEOID";
    /**
     * The color of the product. <BR/>
     */
    public static String ML_COLOROID = "ML_COLOROID";
    /**
     * The size of the product. <BR/>
     */
    public static String ML_SIZENAME = "ML_SIZENAME";
    /**
     * The color of the product. <BR/>
     */
    public static String ML_COLORNAME = "ML_COLORNAME";
    /**
     * The quantity. <BR/>
     */
    public static String ML_QTY = "ML_QTY";
    /**
     * The date when offer is valid. <BR/>
     */
    public static String ML_VALIDFROM = "ML_VALIDFROM";
    /**
     * The date when product should be delivered. <BR/>
     */
    public static String ML_DELIVERYDATE = "ML_DELIVERYDATE";
    /**
     * An internal number for the order. <BR/>
     */
    public static String ML_VOUCHERNO = "ML_VOUCHERNO";
    /**
     * The date when the order is done. <BR/>
     */
    public static String ML_VOUCHERDATE = "ML_VOUCHERDATE";
    /**
     * Token: Catalogs firm. <BR/>
     */
    public static String ML_CATALOGOID = "ML_CATALOGOID";
    /**
     * Token: Catalogs firm. <BR/>
     */
    public static String ML_CAT_COMPANY = "ML_CAT_COMPANY";
    /**
     * Token: Catalogs firm. <BR/>
     */
    public static String ML_CAT_PERSON = "ML_CAT_PERSON";
    /**
     * Token: Catalogs firm. <BR/>
     */
    public static String ML_CAT_LOCKED = "ML_CAT_LOCKED";
    /**
     * Token: ProductGroup code. <BR/>
     */
    public static String ML_PRG_CODE = "ML_PRG_CODE";
    /**
     * Token: ProductGroup season. <BR/>
     */
    public static String ML_PRG_SEASON = "ML_PRG_SEASON";
    /**
     * Token: ProductGroup thumbnail. <BR/>
     */
    public static String ML_PRG_THUMB = "ML_PRG_THUMB";

    /**
     * Token: ProductGroup image. <BR/>
     */
    public static String ML_PRG_IMG = "ML_PRG_IMG";
    /**
     * Token: CatalogProductGroup oid of ProductGroup. <BR/>
     */
    public static String ML_CPG_PRG = "ML_CPG_PRG";
    /**
     * Token: Code Element sort key. <BR/>
     */
    public static String ML_COD_SORT = "ML_COD_SORT";
    /**
     * Token: Content responsible of a catalog. <BR/>
     */
    public static String ML_CAT_CONTRESP = "ML_CAT_CONTRESP";
    /**
     * Token: Order responsible of a catalog. <BR/>
     */
    public static String ML_CAT_ORDRESP = "ML_CAT_ORDRESP";
    /**
     *
     */
    public static String ML_COSTBEGINNING_TERM = "ML_COSTBEGINNING_TERM";
    /**
     * Token: Minimum sales price prefix. <BR/>
     */
    public static String ML_SALESPRICEFROM = "ML_SALESPRICEFROM";

    /**
     * Token: Minimum buy price prefix. <BR/>
     */
    public static String ML_COSTFROM = "ML_COSTFROM";

    /**
     * Token: Media Type of ontent responsible of a catalog. <BR/>
     */
    public static String ML_CAT_CONTRESPMED = "ML_CAT_CONTRESPMED";
    /**
     * Token: Media Type of order responsible of catalog. <BR/>
     */
    public static String ML_CAT_ORDRESPMED = "ML_CAT_ORDRESPMED";

    /**
     * Token: send order after creation. <BR/>
     */
    public static String ML_SENDORDER = "ML_SENDORDER";

    /**
     * Token: State of order. <BR/>
     */
    public static String ML_ORDERSTATE = "ML_ORDERSTATE";

    /**
     * The company of a catalog product group. <BR/>
     */
    public static String ML_COMPANY = "ML_COMPANY";

    /**
     * Name for the category of a properties list. <BR/>
     */
    public static String ML_CATEGORY = "ML_CATEGORY";
    /**
     * Name of properties. <BR/>
     */
    public static String ML_VALUES = "ML_VALUES";
    /**
     * The from clause for a price or cost value. <BR/>
     */
    public static String ML_FROM = "ML_FROM";
    /**
     * The state of an order. <BR/>
     */
    public static String ML_STATEORDER = "ML_STATEORDER";
    /**
     * The short form of cost. <BR/>
     */
    public static String ML_COST_SHORTi = "ML_COST_SHORTi";
    /**
     * The short form of totalcost. <BR/>
     */
    public static String ML_TOTALCOST = "ML_TOTALCOST";
    /**
     * When no state found for an order. <BR/>
     */
    public static String ML_STATE_NOTFOUND = "ML_STATE_NOTFOUND";
    /**
     * The possible categories for a property list. <BR/>
     */
    public static String ML_CATEGORY_COLORS = "ML_CATEGORY_COLORS";
    /**
     * Category for property list sizes. <BR/>
     */
    public static String ML_CATEGORY_SIZES = "ML_CATEGORY_SIZES";

    /**
     * List of available categories. <BR/>
     */
    public static String[] TOK_CATEGORIES =
    {
        StoreTokens.ML_CATEGORY_COLORS,
        StoreTokens.ML_CATEGORY_SIZES,
    };

    /**
     * The string in the selection box when the user wants to define his
     * own keys. <BR/>
     */
    public static String ML_SELFDEFINEDKEY = "ML_SELFDEFINEDKEY";
    /**
     * Key for the size of a product. <BR/>
     */
    public static String ML_SIZEKEY = "ML_SIZEKEY";
    /**
     * Key for the color of a product. <BR/>
     */
    public static String ML_COLORKEY = "ML_COLORKEY";
    /**
     * . <BR/>
     */
    public static String ML_COLORS = "ML_COLORS";
    /**
     * . <BR/>
     */
    public static String ML_SIZES = "ML_SIZES";
    /**
     * . <BR/>
     */
    public static String ML_INSTEAD = "ML_INSTEAD";
    /**
     * . <BR/>
     */
    public static String ML_ALLCOLORS = "ML_ALLCOLORS";
    /**
     * . <BR/>
     */
    public static String ML_ALLSIZES = "ML_ALLSIZES";
    /**
     * . <BR/>
     */
    public static String ML_PRICECOLORS = "ML_PRICECOLORS";
    /**
     * . <BR/>
     */
    public static String ML_NOTALLCOLORS = "ML_NOTALLCOLORS";
    /**
     * . <BR/>
     */
    public static String ML_PRICESIZES = "ML_PRICESIZES";
    /**
     * . <BR/>
     */
    public static String ML_NOTALLSIZES = "ML_NOTALLSIZES";
    /**
     * . <BR/>
     */
    public static String ML_OLDCOST = "ML_OLDCOST";
    /**
     * . <BR/>
     */
    public static String ML_OLDPRICE = "ML_OLDPRICE";
    /**
     * . <BR/>
     */
    public static String ML_YES = "ML_YES";
    /**
     * . <BR/>
     */
    public static String ML_NOPRICES_DEFINED = "ML_NOPRICES_DEFINED";
    /**
     * The short form of cost. <BR/>
     */
    public static String ML_COST_SHORT = "ML_COST_SHORT";
    /**
     * . <BR/>
     */
    public static String ML_FORCOLORS = "ML_FORCOLORS";
    /**
     * . <BR/>
     */
    public static String ML_FORSIZES = "ML_FORSIZES";
    /**
     * To term for ordering. <BR/>
     */
    public static String ML_ORDER = "ML_ORDER";
    /**
     * The size of the product. <BR/>
     */
    public static String ML_SIZE = "ML_SIZE";
    /**
     * The color of the product. <BR/>
     */
    public static String ML_COLOR = "ML_COLOR";
    /**
     * Token for the quantity in the orderform. <BR/>
     */
    public static String ML_ORDERQTY = "ML_ORDERQTY";
    /**
     * Message if no prices where defined for product. <BR/>
     */
    public static String ML_NOPRICES = "ML_NOPRICES";
    /**
     * Message if no prices where defined for product and . <BR/>
     */
    public static String ML_SELECT_CATALOG = "ML_SELECT_CATALOG";
    /**
     * Prefix for the name of an order. <BR/>
     */
    public static String ML_ORDER_FROM = "ML_ORDER_FROM";
    /**
     * The company which is the supplier. <BR/>
     */
    public static String ML_SUPPLIERCOMPANY = "ML_SUPPLIERCOMPANY";
    /**
     * The person resposible for orders in the supplier company. <BR/>
     */
    public static String ML_CONTACTSUPPLIER = "ML_CONTACTSUPPLIER";
    /**
     * The company making the order. <BR/>
     */
    public static String ML_CUSTOMERCOMPANY = "ML_CUSTOMERCOMPANY";
    /**
     * The person making the order. <BR/>
     */
    public static String ML_CONTACTCUSTOMER = "ML_CONTACTCUSTOMER";
    /**
     * The address where to deliver the order. <BR/>
     */
    public static String ML_DELIVERYADDRESS = "ML_DELIVERYADDRESS";
    /**
     * The address where to send the bill to. <BR/>
     */
    public static String ML_PAYMENTADDRESS = "ML_PAYMENTADDRESS";
    /**
     * The telephone number of the originator of the order wants to be called back. <BR/>
     */
    public static String ML_RESPONSE_CALLNUMBER = "ML_RESPONSE_CALLNUMBER";
    /**
     * The description of a product in a order. <BR/>
     */
    public static String ML_PRODUCT_DESCRIPTION = "ML_PRODUCT_DESCRIPTION";
    /**
     * The price of a product per unit. <BR/>
     */
    public static String ML_UNITPRICE = "ML_UNITPRICE";
    /**
     * The total price of the order position. <BR/>
     */
    public static String ML_TOTALPRICE = "ML_TOTALPRICE";
    /**
     * Message when there are no entries in the shopping cart and the user
     * clicked the order button. <BR/>
     */
    public static String ML_CART_NOENTRIES  = "ML_CART_NOENTRIES";
    /**
     * Name for the order. <BR/>
     */
    public static String ML_ORDERNAME  = "ML_ORDERNAME";
    /**
     * The products in the order. <BR/>
     */
    public static String ML_ORDERPOSITIONS = "ML_ORDERPOSITIONS";
    /**
     * The address where to deliver the order. <BR/>
     */
    public static String ML_ADDRESS = "ML_ADDRESS";
    /**
     * Description of the ways of shippment in a catalog. <BR/>
     */
    public static String ML_SHIPPMENT = "ML_SHIPPMENT";
    /**
     * Description when product not available in catalog. <BR/>
     */
    public static String ML_NOTAVAILABLE = "ML_NOTAVAILABLE";
    /**
     * When product not available. <BR/>
     */
    public static String ML_NOTAVAILABLE_1 = "ML_NOTAVAILABLE_1";
    /**
     * How to ship the order. <BR/>
     */
    public static String ML_SHIPPMENT_1 = "ML_SHIPPMENT_1";
    /**
     * How to handle the order when products are not available. <BR/>
     */
    public static String ML_NOTAVAILABLE_CHOICE = "ML_NOTAVAILABLE_CHOICE";
    /**
     * How to ship the product. <BR/>
     */
    public static String ML_SHIPPMENT_CHOICE = "ML_SHIPPMENT_CHOICE";
    /**
     * A wait message for the user. <BR/>
     */
    public static String ML_ONE_MOMENT = "ML_ONE_MOMENT";
    /**
     * Token for the order window. <BR/>
     */
    public static String ML_PUTIN_CART = "ML_PUTIN_CART";
    /**
     * Success message when order send. <BR/>
     */
    public static String ML_ORDER_SEND = "ML_ORDER_SEND";
    /**
     * Success message when order was finished. <BR/>
     */
    public static String ML_ORDER_STORE = "ML_ORDER_STORE";


    /**
     * Failure message when order could not be generated. <BR/>
     */
    public static String ML_ORDER_NOTSEND = "ML_ORDER_NOTSEND";
    /**
     * Signature. <BR/>
     */
    public static String ML_SIGNATURE  = "ML_SIGNATURE";
    /**
     * . <BR/>
     */
    public static String ML_ORDER1 = "ML_ORDER1";
    /**
     * . <BR/>
     */
    public static String ML_DATE = "ML_DATE";
    /**
     * The name of the price tab. <BR/>
     */
    public static String ML_PRICES = "ML_PRICES";
    /**
     * The name of the price tab. <BR/>
     */
    public static String ML_DELIVERY_DESCRIPTION = "ML_DELIVERY_DESCRIPTION";

    /**
     * Message if there is no person responsible for order. <BR/>
     */
    public static String ML_NO_ORDERRESP = "ML_NO_ORDERRESP";
    
    /**
     * The name of the price tab. <BR/>
     */
    public static String ML_CODECATEGORIES = "ML_CODECATEGORIES";

    /**
     * The profile of the product. <BR/>
     */
    public static String ML_PRODUCTPROFILE = "ML_PRODUCTPROFILE";
    /**
     * If the product has an assortment to order. <BR/>
     */
    public static String ML_HASASSORTMENT = "ML_HASASSORTMENT";
    /**
     * If the user wishes to use predefined keys. <BR/>
     */
    public static String ML_PREDEFINEDKEYS = "ML_PREDEFINEDKEYS";
    /**
     * . <BR/>
     */
    public static String ML_PRODUCTBRAND = "ML_PRODUCTBRAND";
    /**
     * . <BR/>
     */
    public static String ML_PRICEVALIDFOR = "ML_PRICEVALIDFOR";
    /**
     * . <BR/>
     */
    public static String ML_VALIDFORVALUES = "ML_VALIDFORVALUES";
    /**
     * . <BR/>
     */
    public static String ML_PRODUCTBRAND_IMAGE = "ML_PRODUCTBRAND_IMAGE";
    /**
     * . <BR/>
     */
    public static String ML_FOR = "ML_FOR";
    /**
     * . <BR/>
     */
    public static String ML_VALIDFORCOLORS = "ML_VALIDFORCOLORS";
    /**
     * . <BR/>
     */
    public static String ML_VALIDFORSIZES = "ML_VALIDFORSIZES";
    /**
     * . <BR/>
     */
    public static String ML_VALIDFOR = "ML_VALIDFOR";
    /**
     * . <BR/>
     */
    public static String ML_SAMEPRICE = "ML_SAMEPRICE";
    /**
     * . <BR/>
     */
    public static String ML_ORDERCURRENCY = "ML_ORDERCURRENCY";
    /**
     * . <BR/>
     */
    public static String ML_CURRENCY = "ML_CURRENCY";
    /**
     * . <BR/>
     */
    public static String ML_HEADINGCART = "ML_HEADINGCART";
    /**
     * . <BR/>
     */
    public static String ML_COLLECTIONNAME = "ML_COLLECTIONNAME";
    /**
     * . <BR/>
     */
    public static String ML_COLLECTIONQUANTITY = "ML_COLLECTIONQUANTITY";
    /**
     * . <BR/>
     */
    public static String ML_COLLECTIONELEMENTS = "ML_COLLECTIONELEMENTS";
    /**
     * . <BR/>
     */
    public static String ML_COLLECTION = "ML_COLLECTION";
    /**
     * . <BR/>
     */
    public static String ML_COLLECTIONS = "ML_COLLECTIONS";
    /**
     * . <BR/>
     */
    public static String ML_SET = "ML_SET";
    /**
     * . <BR/>
     */
    public static String ML_COLLECTION_CODES = "ML_COLLECTION_CODES";
    /**
     * . <BR/>
     */
    public static String ML_TOTALQUANTITY = "ML_TOTALQUANTITY";
    /**
     * Token for field in 'put product in shopping cart' - dialog (matrix-form)
     * if there is no price for one combination of two productkeys. <BR/>
     */
    public static String ML_NOPRICEINMATRIX = "ML_NOPRICEINMATRIX";

    // text tags
    /**
     * Term for list of all catalogs. <BR/>
     */
    public static String ML_TERM_STORE = "TERM_STORE";

    /**
     * Standasdvalue for packingunit in product. <BR/>
     */
    public static String ML_CONST_DEFAULTPACKINGUNIT = "ML_CONST_DEFAULTPACKINGUNIT";

    /**
     * Flag to use order export when creating an order. <BR/>
     */
    public static String ML_ISORDEREXPORT = "ML_ISORDEREXPORT";

    /**
     * connector used for order export. <BR/>
     */
    public static String ML_CONNECTOR = "ML_CONNECTOR";

    /**
     * translator used for order export. <BR/>
     */
    public static String ML_TRANSLATOR = "ML_TRANSLATOR";

    /**
     * filter used for order export. <BR/>
     */
    public static String ML_FILTER = "ML_FILTER";

    /**
     * Token for email subject. <BR/>
     */
    public static String ML_MAIL_SUBJECT = "ML_MAIL_SUBJECT";

    /**
     * Token for email content. <BR/>
     */
    public static String ML_MAIL_CONTENT = "ML_MAIL_CONTENT";

    /**
     * Token for checkbox send - email. <BR/>
     */
    public static String ML_MAIL_SENDMAIL = "ML_MAIL_SENDMAIL";

   /**
     * Token for allowed payment types in a catalog. <BR/>
     */
    public static String ML_ALLOWED_PAYMENTS = "ML_ALLOWED_PAYMENTS";

    /**
     * Token for payment type. <BR/>
     */
    public static String ML_PAYMENT_TYPE = "ML_PAYMENT_TYPE";

    /**
     * Token for means of payment. <BR/>
     */
    public static String ML_PAYMENT_MEANS = "ML_PAYMENT_MEANS";

    /**
     * Token for other payments. <BR/>
     */
    public static String ML_PAYMENT_OTHERS = "ML_PAYMENT_OTHERS";

    /**
     * Token for creditcard payments. <BR/>
     */
    public static String ML_PAYMENT_CREDITCARD = "ML_PAYMENT_CREDITCARD";

    /**
     * Array for all payment names. <BR/>
     */
    public static String[] TOK_PAYMENT_NAMES =
    {
        StoreTokens.ML_PAYMENT_OTHERS,
        StoreTokens.ML_PAYMENT_CREDITCARD,
    }; // TOK_PAYMENT_NAMES

    /**
     * Array for all payment ids. <BR/>
     */
    public static String[] TOK_PAYMENT_IDS =
    {
        "0",
        "1",
    }; // TOK_PAYMENT_IDS

    /**
     * Token for creditcard number. <BR/>
     */
    public static String ML_CREDITCARD_NUMBER = "ML_CREDITCARD_NUMBER";

    /**
     * Token for creditcard month expiry. <BR/>
     */
    public static String ML_CREDITCARD_EXPIRYMONTH = "ML_CREDITCARD_EXPIRYMONTH";

    /**
     * Token for creditcard year expiry. <BR/>
     */
    public static String ML_CREDITCARD_EXPIRYYEAR = "ML_CREDITCARD_EXPIRYYEAR";

    /**
     * Token for creditcard owner. <BR/>
     */
    public static String ML_CREDITCARD_OWNER = "ML_CREDITCARD_OWNER";

    /**************************************************************************
     * Set properties which depend on others. <BR/>
     *
     */
    public static void setDependentProperties ()
    {
        // TODO RB: Remove this part after all parts are migrated to MLI usage
/*
        StoreTokens.TOK_CATEGORIES[0] = StoreTokens.TOK_CATEGORY_COLORS;
        StoreTokens.TOK_CATEGORIES[1] = StoreTokens.TOK_CATEGORY_SIZES;
        UtilConstants.TOK_CURRENCIES[0] = UtilConstants.TOK_CURRENCY_ATS;
        UtilConstants.TOK_CURRENCIES[1] = UtilConstants.TOK_CURRENCY_EUR;
        UtilConstants.TOK_CURRENCIES[2] = UtilConstants.TOK_CURRENCY_DM;
        StoreTokens.TOK_PAYMENT_NAMES[0] = StoreTokens.TOK_PAYMENT_OTHERS;
        StoreTokens.TOK_PAYMENT_NAMES[1] = StoreTokens.TOK_PAYMENT_CREDITCARD;
*/
    } // setDependentProperties ()
    
} // class StoreTokens
