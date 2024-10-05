/*
 * Class: BOConstants.java
 */

// package:
package ibs.bo;

// imports:
import ibs.util.UtilConstants;


/******************************************************************************
 * Constants for business objects. <BR/>
 * This abstract class contains all constants which are necessary to deal with
 * the utilities delivered within this package.
 *
 * @version     $Id: BOConstants.java,v 1.36 2012/07/16 07:39:49 gweiss Exp $
 *
 * @author      Bernd Buchegger (BB), 980415
 ******************************************************************************
 */
public abstract class BOConstants extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: BOConstants.java,v 1.36 2012/07/16 07:39:49 gweiss Exp $";


    /**
     * File name extension for image. <BR/>
     */
    public static final String IMG_FILENAMEEXT = ".gif";

    // type of ordering of elements within a container:
    /**
     * Order the elements in ASCending order. <BR/>
     */
    public static final String ORDER_ASC        = "ASC";

    /**
     * Order the elements in DESCending order. <BR/>
     */
    public static final String ORDER_DESC       = "DESC";

    /**
     * Prefix for image name for ordering. <BR/>
     */
    public static final String IMG_ORDERPREFIX = "Order";

    /**
     * Prefix for image name for informations. <BR/>
     */
    public static final String IMG_INFOPREFIX = "info";

    /**
     * Show ordering ASCending. <BR/>
     */
    public static final String IMG_ORDERASC     =
        BOConstants.IMG_ORDERPREFIX + BOConstants.ORDER_ASC +
        BOConstants.IMG_FILENAMEEXT;

    /**
     * Show ordering DESCending. <BR/>
     */
    public static final String IMG_ORDERDESC    =
        BOConstants.IMG_ORDERPREFIX + BOConstants.ORDER_DESC +
        BOConstants.IMG_FILENAMEEXT;

    /**
     * Show ordering ASCending. <BR/>
     */
    public static final String IMG_INFO     =
        BOConstants.IMG_INFOPREFIX + BOConstants.IMG_FILENAMEEXT;


    // container/object relationships
    /**
     * "Standard" container/object relationship. <BR/>
     */
    public static final int CONT_STANDARD = 1;

    /**
     * "PartOf" container/object relationship. <BR/>
     * This means that each object within the container is a part of the
     * container, i.e. it cannot exist without the container and is not
     * visible without the container.
     */
    public static final int CONT_PARTOF = 2;

    /**
     * code for no restrictions. <BR/>
     */
    public static final String MATCH_NONE       = "0";

    /**
     * code for substring string match. <BR/>
     */
    public static final String MATCH_SUBSTRING  = "1";

    /**
     * code for exact string match . <BR/>
     */
    public static final String MATCH_EXACT      = "2";

    /**
     * code for soundex match . <BR/>
     */
    public static final String MATCH_SOUNDEX    = "3";

    /**
     * code for greater number match. <BR/>
     */
    public static final String MATCH_GREATER    = "4";

    /**
     * code for less number match. <BR/>
     */
    public static final String MATCH_LESS       = "5";

    /**
     * code for greater-equal number match. <BR/>
     */
    public static final String MATCH_GREATEREQUAL = "6";

    /**
     * code for less-equal number match. <BR/>
     */
    public static final String MATCH_LESSEQUAL = "7";

    /**
     * Code for substring at the beginning string match. <BR/>
     */
    public static final String MATCH_STARTSWITH  = "8";

    /**
     * Code for substring at the end string match. <BR/>
     */
    public static final String MATCH_ENDSWITH  = "9";


    /**
     * A fail when you paste in the cuted structure . <BR/>
     */
    public static final int ORY_CUT_INSERT_FAIL    = 11;

    /**
     * A objekt has a masterattachment and it is a file . <BR/>
     */
    public static final int FLG_FILEMASTER = 1;

    /**
     * A objekt has a masterattachment and it is a Hyperlink. <BR/>
     */
    public static final int FLG_HYPERMASTER = 2;

    /**
     * A objekt has a masterattachment and it is a Hyperlink. <BR/>
     */
    public static final int FLG_ISWEBLINK = 32;

    /**
     * Object is assigned to a workflow. <BR/>
     */
    public static final int FLG_WORKFLOW = 64;

    /**
     * Object is not deletable. <BR/>
     */
    public static final int FLG_NOTDELETABLE = 128;

    /**
     * Object is not changeable. <BR/>
     */
    public static final int FLG_NOTCHANGEABLE = 256;

    /**
     * Object does not have attached files. <BR/>
     */
    public static final int FLG_HASFILE = 512;

    /**
     * The max length of the inputstring in the entryfield name or subject. <BR/>
     */
    public static final int MAX_LENGTH_NAME = 63;

    /**
     * The max length of the inputstring in the entryfield description <BR/>
     */
    public static final int MAX_LENGTH_DESCRIPTION = 255;

    /**
     * Show the linked object in searchlist<BR/>
     */
    public static final int SHOWLINKEDOBJECT = 1;

    /**
     * Show the linked object in searchlist<BR/>
     */
    public static final int SHOWSEARCHEDOBJECTS = 3;

    /**
     * Short menu for the behavior of the creation or new business objects.<BR/>
     */
    public static final String NEW_BUSINESS_OBJECT_MENU = "NBOM";


    // Short menu for the behavior of the creation or new business objects.
    /**
     * Nothing to do. <BR/>
     */
    public static final int BONEWMENU_NOTHING = 0;
    /**
     * Create a new business object. <BR/>
     */
    public static final int BONEWMENU_NEW_BUSINESS_OBJECT = 1;
    /**
     * Display the actual business object. <BR/>
     */
    public static final int BONEWMENU_SHOW_BUSINESS_OBJECT = 2;
    /**
     * Go back to the container. <BR/>
     */
    public static final int BONEWMENU_BACK_TO_THE_CLIPBOARD = 3;
    /**
     * Display the rights management of the object. <BR/>
     */
    public static final int BONEWMENU_TO_RIGHTS = 4;
    /**
     * Re-display the form for changing the object. <BR/>
     */
    public static final int BONEWMENU_RESHOW_FORM = 5;

    /**
     * The separator used to separate the single values of a multi selection field in the database. <BR/>
     */
    public static final String MULTISELECTION_VALUE_SAPERATOR = "|";

    /**
     *  Person/Group/Role ordering <BR/>
     */
    public static String ORD_PGR = "pName";

     /**
     *  fullname ordering <BR/>
     */
    public static String ORD_FULLNAME = "fullname";

    /**
     *  <BR/> ownerName ordering
     */
    public static String ORD_OWNERNAME = "ownerName";

    /**
     *  Token to order by status
     */
    public static String ORD_STATUS = "state";

    /**
     * Standard separator tokens for container paths. <BR/>
     */
    public static final String PATH_SEPARATOR = "/\\";
    /**
     * Forward separator token for container paths. <BR/>
     */
    public static final String PATH_FORWARDSEPARATOR = "/";
    /**
     * Backward separator token for container paths. <BR/>
     */
    public static final String PATH_BACKWARDSEPARATOR = "\\";

    /**
     * Standard separator token for object paths. <BR/>
     */
    public static final String OBJECTPATH_SEPARATOR = "/\\>";

    /**
     * The joker character for the user name in the object path. <BR/>
     */
    public static final String OBJECTPATH_USERJOKER = "*";

    /**
     * The placeholder which is used to define that all users shall be used
     * in the object path. <BR/>
     */
    public static final String OBJECTPATH_ALLUSERS = "#ALLUSERS#";

    /**
     * root string  for container paths. <BR/>
     */
    public static final String PATH_ROOT = ".";

    /**
     * back string for container paths. <BR/>
     */
    public static final String PATH_BACK = "..";


    /**
     * maximum of displayed elements in contentview of some container types
     */
    public static final int MAX_CONTENT_ELEMENTS = 100;

    /**
     * delimiter for any usage
     */
    public static final String DELIMITER = ";";


    /**
     * common physical link. <BR/>
     */
    public static final int LINKTYPE_PHYSICAL = 1;

    /**
     * links wich are pointet on main object of referenceTab and
     * and which are physically in any tab of other object
     */
    public static final int LINKTYPE_INTAB = 2;

    /**
     * links wich are pointet on main object of referenceTab and
     * and which are physically in content of other object
     */
    public static final int LINKTYPE_INCONTENT = 3;

    /**
     * A link object. <BR/>
     * This reference defines an object which is defined as link (Reference
     * object).
     */
    public static final int REF_LINK = 1;

    /**
     * A reference to another object. <BR/>
     * This reference describes a reference which can be defined through the
     * OBJECTREF type in XML.
     */
    public static final int REF_OBJECTREF = 2;

    /**
     * A reference to another object within a field. <BR/>
     * This reference describes a reference which can be defined through the
     * FIELDREF type in XML.
     */
    public static final int REF_FIELDREF = 3;

    /**
     * A reference to another object within a field. <BR/>
     * This reference describes a reference which can be defined through the
     * VALUEDOMAIN type in XML.
     */
    public static final int REF_VALUEDOMAIN = 4;

    /**
     * A reference to another object within a field. <BR/>
     * This reference describes a reference which can be defined through the
     * multiselection type in XML.
     */
    public static final int REF_MULTIPLE = 5;

    /**
     * Java script function call that displays the info buttons. <BR/>
     * The tag <A HREF="UtilConstants.html#UtilConstants.TAG_NAME">UtilConstants.TAG_NAME</A>
     * is used to represent the code of the actual button bar.
     * Call for IE 4
    */
    public static final String CALL_SHOWBUTTONSINFO     =
          new String ("top.createButtonBar ('" + UtilConstants.TAG_NAME + "',\'" +
              UtilConstants.TAG_NAME2  + "'," + UtilConstants.TAG_NAME3 + "," +
              UtilConstants.TAG_NAME4 + ");");

    /**
     * Java script function call that displays the content buttons. <BR/>
     * The tag <A HREF="UtilConstants.html#UtilConstants.TAG_NAME">UtilConstants.TAG_NAME</A>
     * is used to represent the code of the actual button bar.
     * call for ie4
     */
    public static final String CALL_SHOWBUTTONSCONTENT  =
          new String (BOConstants.CALL_SHOWBUTTONSINFO);

    /**
     * java script function call that displays buttons. <BR/>
     */
    public static final String CALL_SHOWBUTTON  =
        new String ("top.showButton");

    /**
     * JavaScript function call that ensures that no buttons are displayed.
     * <BR/>
     */
    public static final String CALL_SHOWBUTTONSEMPTY =
        new String ("top.scripts.clearButtonBar ();");

    /**
     * JavaScript function call that ensures that no tabs are displayed.
     * <BR/>
     */
    public static final String CALL_SHOWTABSEMPTY =
        new String ("top.scripts.clearTabBar ();");

    /**
     * Java script function call that displays the content buttons. <BR/>
     * The tag <A HREF="UtilConstants.html#UtilConstants.TAG_NAME">UtilConstants.TAG_NAME</A>
     * is used to represent the code of the actual button bar.
     */
    public static final String CALL_SHOWTABBAR  =
        new String ("top.createTabBar (\'" + UtilConstants.TAG_NAME + "\',\'" +
            UtilConstants.TAG_NAME2 + "\',\'" +
            UtilConstants.TAG_NUMBER + "\',\'" +
            UtilConstants.TAG_NAME3 + "\');");

    /**
     * JavaScript function call that ensures that no tabs are displayed.
     * <BR/>
     */
    public static final String CALL_SHOWTABSBUTTONSEMPTY =
        new String (BOConstants.CALL_SHOWTABSEMPTY + BOConstants.CALL_SHOWBUTTONSEMPTY);

    /**
     * Argument value for argument redirect, indicating that a redirect to the welcome page should be performed.
     */
    public static final String ARG_VALUE_WELCOME = "welc";

} // class BOConstants
