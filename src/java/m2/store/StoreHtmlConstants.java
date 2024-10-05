/*
 * Class: StoreHtmlConstants.java
 */

// package:
package m2.store;

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
 * @version     $Id: StoreHtmlConstants.java,v 1.1 2007/07/23 08:21:37 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR), 980204
 ******************************************************************************
 */
public final class StoreHtmlConstants extends AppConstants
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: StoreHtmlConstants.java,v 1.1 2007/07/23 08:21:37 kreimueller Exp $";


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
     * Frame name: help. <BR/>
     */
    public static final String FRM_HELP             = HtmlConstants.FRM_SHEET;

} // class StoreHtmlConstants
