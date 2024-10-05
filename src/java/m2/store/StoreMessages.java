/*
 * Class: StoreMessages.java
 */

// package:
package m2.store;


// imports:


/******************************************************************************
 * Tokens for the m2 store business objects. <BR/>
 * This abstract class contains all tokens which are necessary to deal with
 * the business objects delivered within this package.
 *
 * @version     $Id: StoreMessages.java,v 1.10 2010/04/07 13:37:07 rburgermann Exp $
 *
 * @author      Thurner Rupert (RT), 980409
 ******************************************************************************
 */
public abstract class StoreMessages extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: StoreMessages.java,v 1.10 2010/04/07 13:37:07 rburgermann Exp $";

    /**
     * Name of bundle where the messages are included. <BR/>
     */
    public static String MSG_BUNDLE = "m2_m2store_messages";

    /**
     * Catalog not found. <BR/>
     */
    public static String ML_NOCATALOG = "ML_NOCATALOG";
    /**
     * Responsible Person/Group for ordering not found. <BR/>
     */
    public static String ML_NOORDRESP = "ML_NOORDRESP";

    /**
     * Success message when product put in shopping cart. <BR/>
     */
    public static String ML_MSG_PUTINCART = "ML_MSG_PUTINCART";

    /**
     * Failure message when product not put in shopping cart. <BR/>
     */
    public static String ML_MSG_NOT_PUTINCART = "ML_MSG_NOT_PUTINCART";
 
    /**
     * Message if no categories found. <BR/>
     */
    public static String ML_MSG_NOPROPSFOUND = "ML_MSG_NOPROPSFOUND";

    /**
     * Message if no categories found. <BR/>
     */
    public static String ML_MSG_ERRORCART = "ML_MSG_ERRORCART";

    /**
     * Message if no categories found. <BR/>
     */
    public static String ML_MSG_NOTVALIDSELECTION = "ML_MSG_NOTVALIDSELECTION";

    /**
     * Message if no categories found. <BR/>
     */
    public static String ML_MSG_ENTERQTY = "ML_MSG_ENTERQTY";

    /**
     * Message if no categories. <BR/>
     */
    public static String ML_MSG_NOCODES = "ML_MSG_NOCODES";

    /**
     * Message if too many categories. <BR/>
     */
    public static String ML_MSG_TOMANY_CODES = "ML_MSG_TOMANY_CODES";

    /**
     * No collections defined. <BR/>
     */
    public static String ML_MSG_NO_COLLECTIONS = "ML_MSG_NO_COLLECTIONS";

    /**
     * Message if orderquantity is under minimum quantity for wich a price is valid. <BR/>
     */
    public static String ML_MSG_WRONGORDERQUANTITY = "ML_MSG_WRONGORDERQUANTITY";

    /**
     * Message if no price is available in simple order form. <BR/>
     */
    public static String ML_MSG_NOPRICE_SIMPLE = "ML_MSG_NOPRICE_SIMPLE";

    /**
     * Message if no price is available in matrix or selection order form. <BR/>
     */
    public static String ML_MSG_NOPRICE_COMPLEX = "ML_MSG_NOPRICE_COMPLEX";

    /**
     * Message when order export hsa been started. <BR/>
     */
    public static String ML_MSG_ORDER_EXPORT_STARTED = "ML_MSG_ORDER_EXPORT_STARTED";

    /**
     * Message when order export failed. <BR/>
     */
    public static String ML_MSG_ORDER_EXPORT_FAILED = "ML_MSG_ORDER_EXPORT_FAILED";

    /**
     * no shoppingcartentries in shoppingcart for order
     */
    public static String ML_MSG_NOSHOPPINGCARTENTRIES = "ML_MSG_NOSHOPPINGCARTENTRIES";

     /**
     * Message if mail-address of orderresponsible is not set. <BR/>
     */
    public static String ML_MSG_WRONG_EMAIL_ORDERRESP = "ML_MSG_WRONG_EMAIL_ORDERRESP";

} // class StoreMessages
