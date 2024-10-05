/*
 * Class: m2Constants.java
 */

// package:
package m2.bbd;

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
 * @deprecated  KR 20070722 This class is never used.
 *
 * @version     $Id: BbdHtmlConstants.java,v 1.1 2007/07/23 08:21:29 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR), 980204
 ******************************************************************************
 */
public abstract class BbdHtmlConstants extends AppConstants
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: BbdHtmlConstants.java,v 1.1 2007/07/23 08:21:29 kreimueller Exp $";


    /**
     * Application name.
     * @deprecated  This value is never used.
     */
    public static final String APPLICATION  =
        new String ("m2");



    // Navigation Constants
    /**
     * Navigation to PRIVATE tab.
     * @deprecated  This value is never used.
     */
    public static final String NAV_PRIVATE          = "private";
    /**
     * Navigation to GROUP tab.
     * @deprecated  This value is never used.
     */
    public static final String NAV_GROUP            = "group";

    /**
     * Control Flag used for example in the discussion to
     * control if the frameset was already build
     * @deprecated  This value is never used.
     */
    public static final String CTR_DONE             = "done";


    // frame names:
    /**
     * @deprecated  This value is never used.
     */
    public static final String FRM_FOLDERLEFT       = new String ("folderleft");
    /**
     * @deprecated  This value is never used.
     */
    public static final String FRM_FOLDERUPLEFT     = new String ("folderupleft");
    /**
     * @deprecated  This value is never used.
     */
    public static final String FRM_FOLDERDOWNLEFT   = new String ("folderdownleft");
    /**
     * @deprecated  This value is never used.
     */
    public static final String FRM_FOLDERDOWN       = new String ("folderdown");
    /**
     * @deprecated  This value is never used.
     */
    public static final String FRM_FOLDERDOWNRIGHT  = new String ("folderdownright");
    /**
     * @deprecated  This value is never used.
     */
    public static final String FRM_FOLDERRIGHT      = new String ("folderright");
    /**
     * @deprecated  This value is never used.
     */
    public static final String FRM_FOLDERUPRIGHT    = new String ("folderupright");
    /**
     * @deprecated  This value is never used.
     */
    public static final String FRM_SPRINGUP         = new String ("springup");
    /**
     * @deprecated  This value is never used.
     */
    public static final String FRM_SPRINGMIDDLE     = new String ("springmiddle");
    /**
     * @deprecated  This value is never used.
     */
    public static final String FRM_SPRINGDOWN       = new String ("springdown");
    /**
     * @deprecated  This value is never used.
     */
    public static final String FRM_DISCTREE         = new String ("disctree");
    /**
     * @deprecated  This value is never used.
     */
    public static final String FRM_DISCENTRY        = new String ("discentry");

    /**
     * @deprecated  This value is never used.
     */
    public static final String FRM_ORDER_UP         = new String ("order_up");
    /**
     * @deprecated  This value is never used.
     */
    public static final String FRM_ORDER_DOWN       = new String ("order_down");
    /**
     * @deprecated  This value is never used.
     */
    public static final String FRM_HELP             = HtmlConstants.FRM_SHEET;

} // class m2Constants
