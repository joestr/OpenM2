/*
 * Class: m2Constants.java
 */

// package:
package m2.mad;

// imports:
import ibs.app.AppConstants;
import ibs.io.HtmlConstants;


/******************************************************************************
 * Constants for m2. <BR/>
 * This abstract class contains all constants which are necessary to deal with
 * the classes delivered within this package.<P>
 * Contains application defined object types and derived types with IDs from
 * 0x0101 to 0xFFFF. <BR/>
 *
 * @deprecated  KR 20070720 This class is never used.
 *
 * @version     $Id: MadHtmlConstants.java,v 1.1 2007/07/23 08:21:36 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR), 980204
 ******************************************************************************
 */
public abstract class MadHtmlConstants extends AppConstants
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: MadHtmlConstants.java,v 1.1 2007/07/23 08:21:36 kreimueller Exp $";


    /**
     * Application name.
     */
    public static final String APPLICATION  =
        new String ("m2");


    /**
     * Navigation Constant: private. <BR/>
     */
    public static final String NAV_PRIVATE          = "private";
    /**
     * Navigation constant: group. <BR/>
     */
    public static final String NAV_GROUP            = "group";

    /**
     * Control Flag used for example in the discussion to
     * control if the frameset was already build
     */
    public static final String CTR_DONE             = "done";


    // frame names:
    /**
     * Frame name: left. <BR/>
     */
    public static final String FRM_FOLDERLEFT       = new String ("folderleft");
    /**
     * Frame name: upper left. <BR/>
     */
    public static final String FRM_FOLDERUPLEFT     = new String ("folderupleft");
    /**
     * Frame name: lower left. <BR/>
     */
    public static final String FRM_FOLDERDOWNLEFT   = new String ("folderdownleft");
    /**
     * Frame name: lower. <BR/>
     */
    public static final String FRM_FOLDERDOWN       = new String ("folderdown");
    /**
     * Frame name: lower right. <BR/>
     */
    public static final String FRM_FOLDERDOWNRIGHT  = new String ("folderdownright");
    /**
     * Frame name: right. <BR/>
     */
    public static final String FRM_FOLDERRIGHT      = new String ("folderright");
    /**
     * Frame name: upper right. <BR/>
     */
    public static final String FRM_FOLDERUPRIGHT    = new String ("folderupright");
    /**
     * Frame name: spring upper. <BR/>
     */
    public static final String FRM_SPRINGUP         = new String ("springup");
    /**
     * Frame name: spring middle. <BR/>
     */
    public static final String FRM_SPRINGMIDDLE     = new String ("springmiddle");
    /**
     * Frame name: spring lower. <BR/>
     */
    public static final String FRM_SPRINGDOWN       = new String ("springdown");
    /**
     * Frame name: discussion tree. <BR/>
     */
    public static final String FRM_DISCTREE         = new String ("disctree");
    /**
     * Frame name: discussion entry. <BR/>
     */
    public static final String FRM_DISCENTRY        = new String ("discentry");

    /**
     * Frame name: order up. <BR/>
     */
    public static final String FRM_ORDER_UP         = new String ("order_up");
    /**
     * Frame name: order down. <BR/>
     */
    public static final String FRM_ORDER_DOWN       = new String ("order_down");
    /**
     * Frame name: help. <BR/>
     */
    public static final String FRM_HELP             = HtmlConstants.FRM_SHEET;


    /**
     * The name of the class in css for discussiontable. <BR/>
     */
    public static final String CLASS_DISC = "disc";

    /**
     * The name of the class in css for list. <BR/>
     */
    public static final String CLASS_DISCTOPIC = "discTopic";

    /**
     * . <BR/>
     */
    public static final String CLASS_PRODUCT_ORDER_HEADER =
        MadHtmlConstants.CLASS_ORDER_HEADER;
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


    /**
     * The names of the class in css for the rows. <BR/>
     */
    public static final String[] LST_CLASSDISCROWS = {"discEntry1", "discEntry2"};
    /**
     * . <BR/>
     */
    public static final String CLASS_MONTH = "month";

    /**
     * . <BR/>
     */
    public static final String CLASS_CALBODY = "calbody";
    /**
     * . <BR/>
     */
    public static final String CLASS_COLHOUR = "hours";

    /**
     * . <BR/>
     */
    public static final String CLASS_COLTERMIN = "date";

    /**
     * . <BR/>
     */
    public static final String CLASS_COLDAY = "days";

    /**
     * . <BR/>
     */
    public static final String CLASS_TERMTIME = "time";

    /**
     * . <BR/>
     */
    public static final String CLASS_TERMPLACE = "place";

    /**
     * . <BR/>
     */
    public static final String CLASS_TERMPART = "participants";

    /**
     * . <BR/>
     */
    public static final String CLASS_TERMNAME = "name";



    /**
     * Class for the terms on one day: term1. <BR/>
     */
    public static final String CLASS_TERMNAME1 = "termname1";
    /**
     * Class for the terms on one day: term2. <BR/>
     */
    public static final String CLASS_TERMNAME2 = "termname2";
    /**
     * Class for the terms on one day: term3. <BR/>
     */
    public static final String CLASS_TERMNAME3 = "termname3";
    /**
     * Class for the terms on one day: term4. <BR/>
     */
    public static final String CLASS_TERMNAME4 = "termname4";

    /**
     * . <BR/>
     */
    public static final String CLASS_TERMIN = "term";
    /**
     * . <BR/>
     */
    public static final String CLASS_DAY = "day";
    /**
     * . <BR/>
     */
    public static final String CLASS_CALHEADER = "calheader";

    /**
     * . <BR/>
     */
    public static final String CLASS_HOLIDAY = "holiday";

} // class m2Constants
