/*
 * Class: StoreArguments.java
 */

// package
package m2.store;

// imports:


/******************************************************************************
 * Arguments for the m2 store. <BR/>
 * This abstract class contains all arguments which are necessary to deal with
 * the classes delivered within this package. <P>
 *
 * @version     $Id: StoreArguments.java,v 1.8 2007/07/23 08:21:37 kreimueller Exp $
 *
 * @author  Thurner Rupert (RT), 980610
 ******************************************************************************
 */
public abstract class StoreArguments extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: StoreArguments.java,v 1.8 2007/07/23 08:21:37 kreimueller Exp $";


    // argument handling for url:
    //product arguments (for a description of the following tokens see class
    //product
//    public static final String ARG_PRODUCT = "prod";
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_PRODUCTOID = "prdo";
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_PRODUCTNO = "prnr";
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_EAN = "ean";
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_PRODUCTDESCRIPTION = "prde";
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_SIZECODEOID = "sico";
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_COLORCODEOID = "coco";
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_SIZECODENAME = "szcn";
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_COLORCODENAME = "clcn";

    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_COST = "cost";
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_COSTGUARANTEEDTILL = "cgt";
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_COSTCURRENCY = "ccur";

    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_PRICE = "sp";
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_PRICECURRENCY = "spcu";

    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_AVAILABLEFROM = "avai";
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_UNITOFQTY = "unit";
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_PACKINGUNIT = "pack";
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_THUMBNAIL = "thum";
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_IMAGE = "imag";
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_STOCK = "stoc";

    //productSizeColor
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_SIZEOID = "sizo";
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_COLOROID = "colo";
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_SIZENAME = "szna";
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_COLORNAME = "clna";
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_QTY = "qty";
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_USERPRICE1 = "up1";
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_USERPRICE2 = "up2";
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_VALIDFROM = "valf";

    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_PRODUCTSIZECOLOROID = "psco";
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_DELIVERYADRESSOID = "deao";
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_DELIVERYOID = "deo";
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_DELIVERYDATE = "ded";
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_PAYMENTOID = "payo";
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_VOUCHERNO = "vo";
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_VOUCHERDATE = "vod";
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_DESCRIPTION2 = "des2";
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_ORIGINATOROID = "oro";
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_RECIPIENTOID = "reo";

    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_CATALOGOID = "cato";

    /**
     * Argument: Catalogs firm. <BR/>
     */
    public static final String ARG_CAT_COMPANY = "cfir";

    /**
     * Argument: Catalogs firm. <BR/>
     */
    public static final String ARG_CAT_PERSON = "ccon";

    /**
     * Argument: Catalogs firm. <BR/>
     */
    public static final String ARG_CAT_LOCKED = "cloc";

    /**
     * Argument: ProductGroup code. <BR/>
     */
    public static final String ARG_PRG_CODE = "pgco";

    /**
     * Argument: ProductGroup season. <BR/>
     */
    public static final String ARG_PRG_SEASON = "pgse";

    /**
     * Argument: ProductGroup thumbnail. <BR/>
     */
    public static final String ARG_PRG_THUMB = "pgtn";

    /**
     * Argument: ProductGroup image. <BR/>
     */
    public static final String ARG_PRG_IMG = "pgim";

    /**
     * Argument: CatalogProductGroup oid of ProductGroup. <BR/>
     */
    public static final String ARG_CPG_PRG = "cpgg";


    /**
     * Argument: Code Element sort key. <BR/>
     */
    public static final String ARG_COD_SORT = "coso";

    /**
     * Argument: Content responsible of a catalog. <BR/>
     */
    public static final String ARG_CAT_CONTRESP = "contResp";
    /**
     * Argument: Order responsible of catalog. <BR/>
     */
    public static final String ARG_CAT_ORDRESP = "ordResp";

    /**
     * Argument: Media Type of ontent responsible of a catalog. <BR/>
     */
    public static final String ARG_CAT_CONTRESPMED = "contRespMed";
    /**
     * Argument: Media Type of order responsible of catalog. <BR/>
     */
    public static final String ARG_CAT_ORDRESPMED = "ordRespMed";
    /**
     * Argument: Name of the category of a properties list. <BR/>
     */
    public static final String ARG_CATEGORYNAME = "can";
    /**
     * Argument: Name of the category of a properties list. <BR/>
     */
    public static final String ARG_CATEGORYOID = "cat";
    /**
     * Argument: Properties list. <BR/>
     */
    public static final String ARG_VALUES = "vl";
    /**
     * Argument: The prefix used for the oid of a property list. <BR/>
     */
    public static final String ARG_PROPERTYOID_PREFIX = "PID";
    /**
     * Argument: The prefix used for the category names of
     *           property lists. <BR/>
     */
    public static final String ARG_PROPERTYCAT_PREFIX = "PC";
    /**
     * Argument: The prefix used for a property checkbox. <BR/>
     */
    public static final String ARG_PROPERTIES_PREFIX = "P";
    /**
     * Argument: the number of properties in a property list. <BR/>
     */
    public static final String ARG_PROPERTIESNR_PREFIX = "PN";
    /**
     * Argument: the number of properties in a property list. <BR/>
     */
    public static final String ARG_USERDEFINED_PREFIX = "USR";
    /**
     * Argument: used for the two step edit dialog. <BR/>
     */
    public static final String ARG_SHOWCHANGEFORM = "scf";
    /**
     * Argument: the two categories for properties currently
     * used for a product. <BR/>
     */
    public static final String[] ARG_CATEGORIES = {"acolor", "asize" };
    /**
     * Argument: the two categories for properties currently
     * used for a product. <BR/>
     */
    public static final String[] ARG_PROPERTYVALUES = {"cvals", "svals" };

    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_COLOR = "cl";
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_SIZE = "sz";
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_COLORS = "cls";
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_SIZES = "szs";
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_ALLCOLORS = "allc";
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_ALLSIZES = "alls";

    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_OLDCOST = "oldc";
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_OLDPRICE = "oldp";
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_ORDER_PREFIX = "B";
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_ORDERCOLOR_PREFIX = "BC";
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_ORDERSIZE_PREFIX = "BS";
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_ORDERPRICE_PREFIX = "BP";

    /**
     * Argument: Indicates that the following arguments com from the
     * order form. <BR/>
     */
    public static final String ARG_ORDERFORM = "of";
    /**
     * Argument: The number of input fields in the order form. <BR/>
     */
    public static final String ARG_NR_ORDERINPUTS = "onr";
    /**
     * The company which is the supplier. <BR/>
     */
    public static final String ARG_SUPPLIERCOMPANY = "scomp";
    /**
     * The person resposible for orders in the supplier company. <BR/>
     */
    public static final String ARG_CONTACTSUPPLIER = "csupl";
    /**
     * The company making the order. <BR/>
     */
    public static final String ARG_CUSTOMERCOMPANY = "ccomp";
    /**
     * The person making the order. <BR/>
     */
    public static final String ARG_CONTACTCUSTOMER = "ccust";
    /**
     * The address where to deliver the order. <BR/>
     */
    public static final String ARG_DELIVERYADDRESS = "da";
    /**
     * The address where to send the bill to. <BR/>
     */
    public static final String ARG_PAYMENTADDRESS = "padr";
    /**
     * The ways of shipment for an order. <BR/>
     */
    public static final String ARG_SHIPPMENT = "ship";
    /**
     * Description of handling when product not available. <BR/>
     */
    public static final String ARG_NOTAVAILABLE = "nav";
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_NOTAVAILABLE_CHOICE = "nac";
    /**
     * Product argument. For a description see class Product. <BR/>
     */
    public static final String ARG_SHIPPMENT_CHOICE = "shc";
    /**
     * Argument for the supplier of an order. <BR/>
     */
    public static final String ARG_SUPPLIER  = "sup";
    /**
     * Argument for the thumbNail as Image flag. <BR/>
     */
    public static final String ARG_THUMBASIMAGE  = "taim";
    /**
     * Argument for the description of the delivery. <BR/>
     */
    public static final String ARG_DELIVERY_DESCRIPTION = "ddesc";
    /**
     * Argument for the number of the change dialog. <BR/>
     */
    public static final String ARG_DIALOGNR = "dnr";
    /**
     * . <BR/>
     */
    public static final String ARG_CATEGORYOID1 = "caoid1";
    /**
     * . <BR/>
     */
    public static final String ARG_CATEGORYOID2 = "caoid2";
    /**
     * . <BR/>
     */
    public static final String ARG_CODEOID1 = "cooid1";
    /**
     * . <BR/>
     */
    public static final String ARG_CODEOID2 = "cooid2";
    /**
     * . <BR/>
     */
    public static final String ARG_CODE1 = "code1";
    /**
     * . <BR/>
     */
    public static final String ARG_CODE2 = "code2";
    /**
     * . <BR/>
     */
    public static final String ARG_CODECATEGORIES = "codec";
    /**
     * . <BR/>
     */
    public static final String ARG_PRODUCTPROFILE = "ppoid";
    /**
     * . <BR/>
     */
    public static final String ARG_PREDEFINEDKEYS = "pdk";
    /**
     * . <BR/>
     */
    public static final String ARG_HASASSORTMENT = "hasa";
    /**
     * . <BR/>
     */
    public static final String ARG_PRODUCTBRAND = "brand";
    /**
     * . <BR/>
     */
    public static final String ARG_PRODUCTBRANDIMAGE = "bimg";
    /**
     * . <BR/>
     */
    public static final String ARG_CODE = "code";
    /**
     * . <BR/>
     */
    public static final String ARG_CODECATEGORY = "codecat";
    /**
     * . <BR/>
     */
    public static final String ARG_NRCODES = "nrcodes";
    /**
     * . <BR/>
     */
    public static final String ARG_PRICEVALIDFORALLVALUES = "prvfa";
    /**
     * . <BR/>
     */
    public static final String ARG_CODEX = "cx";
    /**
     * . <BR/>
     */
    public static final String ARG_CODEY = "cy";
    /**
     * . <BR/>
     */
    public static final String ARG_PRODUCTDIALOGSTEP = "pds";
    /**
     * . <BR/>
     */
    public static final String ARG_VALIDFORALLVALUES = "vfav";
    /**
     * . <BR/>
     */
    public static final String ARG_ORDERCURRENCY = "ocr";
    /**
     * . <BR/>
     */
    public static final String ARG_ORDERPRICE = "op";
    /**
    * Send and save or only save order after creation
    */
    public static final String ARG_SENDORDER    = "seo";
    /**
    * Send and save or only save order after creation
    */
    public static final String ARG_NR_VALUES = "nrval";
    /**
     * . <BR/>
     */
    public static final String ARG_COLLECTION_PREFIX = "cp";
    /**
     * . <BR/>
     */
    public static final String ARG_COLLECTION_PREFIX1 = "cpp";
    /**
     * . <BR/>
     */
    public static final String ARG_COLLECTION_PREFIX2 = "cpo";
    /**
     * . <BR/>
     */
    public static final String ARG_CODEX1 = "cxx";
    /**
     * . <BR/>
     */
    public static final String ARG_CODEY1 = "cyy";
    /**
     * . <BR/>
     */
    public static final String ARG_COLLECTION = "col";
    /**
     * . <BR/>
     */
    public static final String ARG_NRCOLLECTIONS = "nrcol";
    /**
     * . <BR/>
     */
    public static final String ARG_COLLECTIONNAME = "cnm";

    /**
     * Flag to use order export when creating an order. <BR/>
     */
    public static final String ARG_ISORDEREXPORT = "ioex";

    /**
     * connector used for order export. <BR/>
     */
    public static final String ARG_CONNECTOR = "conn";

    /**
     * translator used for order export. <BR/>
     */
    public static final String ARG_TRANSLATOR = "tra";

    /**
     * filter used for order export. <BR/>
     */
    public static final String ARG_FILTER = "fil";

    /**
     * Argument for email of current user. <BR/>
     */
    public static final String ARG_EMAIL_CURRENTUSER = "mailcu";

    /**
     * Argument for email of order responsible. <BR/>
     */
    public static final String ARG_EMAIL_ORDERRESP = "mailor";

    /**
     * Argument for the allowed payments. <BR/>
     */
    public static final String ARG_ALLOWED_PAYMENTS = "allp";

    /**
     * Argument for the paymenttype. <BR/>
     */
    public static final String ARG_PAYMENT_TYPE = "pt";

    /**
     * Argument for the name of the payment type. <BR/>
     */
    public static final String ARG_PAYMENT_TYPE_NAME = "ptn";

    /**
     * Argument for the description of the payment type. <BR/>
     */
    public static final String ARG_PAYMENT_TYPE_DESC = "ptd";

    /**
     * Argument for the available payments. <BR/>
     */
    public static final String ARG_PAYMENT_TYPES_AVAILABLE = "pmt";

    /**
     * Argument for the name of the other payments. <BR/>
     */
    public static final String ARG_PAYMENT_OTHERS = "poth";

    /**
     * Argument for the name of the credit card. <BR/>
     */
    public static final String ARG_CREDITCARD_NAME = "crna";

    /**
     * Argument for the credit card number. <BR/>
     */
    public static final String ARG_CREDITCARD_NUMBER = "crnu";

    /**
     * Argument for the month from the cycle of the credit card. <BR/>
     */
    public static final String ARG_CREDITCARD_EXPIRYMONTH = "crexm";

    /**
     * Argument for the year from the cycle of the credit card. <BR/>
     */
    public static final String ARG_CREDITCARD_EXPIRYYEAR = "crexy";

    /**
     * Argument for the name of the credit card owner. <BR/>
     */
    public static final String ARG_CREDITCARD_OWNER = "crow";

    /**
     * Argument for type of order. <BR/>
     */
    public static final String ARG_ORDERTYPE = "ordtyp";

} // class StoreArguments
