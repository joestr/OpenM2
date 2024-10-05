/*
 * Class: BbdTokens.java
 */

// package:
package m2.bbd;

// imports:

/******************************************************************************
 * Tokens for the discussion m2 business objects. <BR/>
 * This abstract class contains all tokens which are necessary to deal with
 * the business objects delivered within this package.
 *
 * @version     $Id: BbdTokens.java,v 1.11 2010/04/07 13:37:10 rburgermann Exp $
 *
 * @author      Keim Christine (CK), 980504
 ******************************************************************************
 */
public abstract class BbdTokens extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: BbdTokens.java,v 1.11 2010/04/07 13:37:10 rburgermann Exp $";

    /**
     * Name of bundle where the tokens included. <BR/>
     */
    public static String TOK_BUNDLE = "m2_m2bbd_tokens";

    // text tokens

    /**
     * Token for the type of a discussion. <BR/>
     */
    public static String ML_ART       = "ML_ART";

    /**
     * Token for the type of presentation of a discussion. <BR/>
     */
    public static String ML_DEFAULTVIEW = "ML_DEFAULTVIEW";

    /**
     * Token for the normal view. <BR/>
     */
    public static String ML_STNDVIEW = "ML_STNDVIEW";

    /**
     * Token for the quickview
     */
    public static String ML_QUICKVIEW = "ML_QUICKVIEW";

    /**
     * Token for the normal view. <BR/>
     */
    public static String ML_DISCENTRY = "ML_DISCENTRY";

    /**
     * Token for the heading of the discussioncontainer. <BR/>
     */
    public static String ML_NEWENTRIES = "ML_NEWENTRIES";

    /**
     * Token for the heading of the answer of an entry. <BR/>
     */
    public static String ML_ANSWER = "ML_ANSWER";

    /**
     * Token for the heading of the discussioncontainer. <BR/>
     */
    public static String ML_NEW = "ML_NEW";

    /**
     * Token for the name of an title. <BR/>
     */
    public static String ML_TITLENAME = "ML_TITLENAME";

    /**
     * Token for level 1 of a XMLDiscussionTemplate. <BR/>
     */
    public static String ML_LEVEL1 = "ML_LEVEL1";

    /**
     * Token for level 2 of a XMLDiscussionTemplate. <BR/>
     */
    public static String ML_LEVEL2 = "ML_LEVEL2";

    /**
     * Token for level 3 of a XMLDiscussionTemplate. <BR/>
     */
    public static String ML_LEVEL3 = "ML_LEVEL3";

    /**
     * Token for XMLDiscussionTemplate. <BR/>
     */
    public static String ML_XMLDISCTEMPLATE = "ML_XMLDISCTEMPLATE";

} // class BbdTokens
