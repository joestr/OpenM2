/*
 * Class: TabConstants.java
 */

// package:
package ibs.bo.tab;

// imports:


/******************************************************************************
 * This abstract class contains all constants which are necessary to deal with
 * the tabs delivered within this package. <P>
 *
 * @version     $Id: TabConstants.java,v 1.19 2007/07/10 22:40:02 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR), 980430
 ******************************************************************************
 */
public abstract class TabConstants extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: TabConstants.java,v 1.19 2007/07/10 22:40:02 kreimueller Exp $";

    // the tab bars:
    /**
     * No tab. <BR/>
     */
    public static final int TAB_NONE = -1;


    // some necessary tab codes:
    /**
     * Code of tab Info. <BR/>
     */
    public static final String TC_INFO = "Info";

    /**
     * Code of tab Info. <BR/>
     */
    public static final String TC_CONTENT = "Content";

    /**
     * Code of tab Rights. <BR/>
     */
    public static final String TC_RIGHTS = "Rights";

    /**
     * Code of tab References. <BR/>
     */
    public static final String TC_REFERENCES = "References";


    // the several kinds of tabs:
    /**
     * Tab kind View. <BR/>
     * A view tab represents just a specific view at the object it belongs to.
     */
    public static final int TK_VIEW = 1;

    /**
     * Tab kind Object. <BR/>
     * An object tab is an own object being associated to the object it belongs
     * to.
     */
    public static final int TK_OBJECT = 2;

    /**
     * Tab kind Link. <BR/>
     * A link tab is a link to an independent object somewhere wihin the
     * system. That means that a specific view of that object is displayed
     * within the tab.
     */
    public static final int TK_LINK = 3;

    /**
     * Tab kind Function. <BR/>
     * A function tab does not have a specific content. Its content is
     * determined by evaluating a function at the time when the user selects
     * the tab.
     */
    public static final int TK_FUNCTION = 4;

    /**
     * Default tab kind. <BR/>
     * This value shall be used for initialization purposes if no specific
     * tab kind is needed.
     */
    public static final int TK_DEFAULT = TabConstants.TK_VIEW;

} // class TabConstants
