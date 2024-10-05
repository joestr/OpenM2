/*
 * Class: BbdArguments.java
 */

// package:
package m2.bbd;

// imports:


/******************************************************************************
 * Arguments for the discussion part of m2. <BR/>
 * This abstract class contains all arguments which are necessary to deal with
 * the classes delivered within this package. <P>
 *
 * @version     $Id: BbdArguments.java,v 1.7 2007/07/23 08:21:29 kreimueller Exp $
 *
 * @author      Keim Christine (CK), 980504
 ******************************************************************************
 */
public abstract class BbdArguments extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: BbdArguments.java,v 1.7 2007/07/23 08:21:29 kreimueller Exp $";


    // argument handling for url:
    /**
     * argumentname for the subject of an entry. <BR/>
     */
    public static final String ARG_SUBJECT       = "sub";

    /**
     * argumentname for the content of an entry. <BR/>
     */
    public static final String ARG_CONTENT       = "cont";

    /**
     * argumentname for the level of an entry. <BR/>
     */
    public static final String ARG_LEVEL         = "lvl";

    /**
     * argumentname for the maximum of levels allowed in a discussion. <BR/>
     */
    public static final String ARG_MAXLEVELS     = "mlvl";

    /**
     * argumentname for the type of a discussion. <BR/>
     */
    public static final String ARG_ART           = "art";

    /**
     * argumentname for the Defaultview of a discussion. <BR/>
     */
    public static final String ARG_DEFAULTVIEW   = "dv";

    /**
     * argumentname for the building of the quickview frameset. <BR/>
     */
    public static final String ARG_DISCFRAMESET  = "dfs";

    /**
     * dummyArgument. <BR/>
     */
    public static final String ARG_DUMMY  = "dm";

    /**
     * Argument for level 1 of the XMLDiscussionTemplate. <BR/>
     */
    public static final String ARG_LEVEL1  = "lvl1";

    /**
     * Argument for level 2 of the XMLDiscussionTemplate. <BR/>
     */
    public static final String ARG_LEVEL2  = "lvl2";

    /**
     * Argument for level 3 of the XMLDiscussionTemplate. <BR/>
     */
    public static final String ARG_LEVEL3  = "lvl3";

    // argument handling for url:
    /**
     * . <BR/>
     */
//    public static final String ARG_           = "";

} // class BbdArguments
