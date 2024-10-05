/*
 * Class: UserConstants.java
 */

// package:
package ibs.obj.user;

// imports:
//KR TODO: unsauber
import ibs.app.AppConstants;
import ibs.bo.Operations;
import ibs.obj.user.UserTokens;


/******************************************************************************
 * Constants for User. <BR/>
 * This abstract class contains all constants which are necessary to deal with
 * the classes delivered within this package.<P>
 *
 * @version     $Id: UserConstants.java,v 1.7 2010/04/07 13:37:09 rburgermann Exp $
 *
 * @author      Andreas Jansa (AJ), 981216
 ******************************************************************************
 */
public abstract class UserConstants extends AppConstants
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: UserConstants.java,v 1.7 2010/04/07 13:37:09 rburgermann Exp $";


    /**
     * Size of the first frame. <BR/>
     */
    public static final String SIZE_USRFRM1 = "60%";

    /**
     * Token for the ordering in the Person - Selectionlist
     */
    public static final String ARG_ORDER_PPREFIX = "prefix";

    /**
     * Token for the ordering in the Person - Selectionlist
     */
    public static final String ARG_ORDER_PNAME = "fullname";

    /**
     * Token for the ordering in the Person - Selectionlist
     */
    public static final String ARG_ORDER_PFIRM = "company";

    /**
     * Token for the ordering in the Person - Selectionlist
     */
    public static final String ARG_ORDER_PEMAIL = "offemail";

    /**
     * Headings for the container PersonSearchContainer
     * with extended attributes
     */
    public static final String [] LST_HEADINGS_PERSONS = new String []
    {
        UserTokens.ML_PERSONPREFIX,
        UserTokens.ML_PERSONNAME,
        UserTokens.ML_PERSONFIRM,
        UserTokens.ML_PERSONEMAIL,
    }; // LST_HEADINGS_PERSONS

    /**
     * Headings for the container PersonSearchContainer
     * without extended attributes
     */
    public static final String [] LST_HEADINGS_PERSONS_REDUCED = new String []
    {
        UserTokens.ML_PERSONNAME,
        UserTokens.ML_PERSONFIRM,
    }; // LST_HEADINGS_PERSONS_REDUCED

    /**
     * Url type for the link personlist in PersonSearchContainer_01:
     * create reference. <BR/>
     */
    public static final int URLTYPE_CREATEREFERENCE = 1;
    /**
     * Url type for the link personlist in PersonSearchContainer_01:
     * show content. <BR/>
     */
    public static final int URLTYPE_SHOWCONTENT = 3;

    /**
     * Rightalias 'READ'
     */
    public static final int RA_READ =   Operations.OP_VIEW | Operations.OP_READ | Operations.OP_CREATELINK |
                                        Operations.OP_DISTRIBUTE | Operations.OP_VIEWELEMS;
    /**
     * Rightalias 'WRITE'
     */
    public static final int RA_WRITE =  Operations.OP_CHANGE | Operations.OP_DELETE | Operations.OP_NEW |
                                        Operations.OP_ADDELEM | Operations.OP_DELELEM;
    /**
     * Rightalias 'ADMIN'
     */
    public static final int RA_ADMIN =  Operations.OP_VIEWRIGHTS | Operations.OP_SETRIGHTS | Operations.OP_VIEWPROTOCOL;

    /**
     * Rightaliases
     */
    public static final int[] RIGHTALIASES =
    {
        UserConstants.RA_READ,
        UserConstants.RA_WRITE,
        UserConstants.RA_ADMIN,
    }; // RIGHTALIASES


    // colours
    /**
     * Text colour. <BR/>
     */
    public static final String COLOR_TEXT = "black";

    /**
     * Colour of link. <BR/>
     */
    public static final String COLOR_LINK = "DARKBLUE";

    /**
     * Colour of already clicked link. <BR/>
     */
    public static final String COLOR_VLINK = "DARKBLUE";

    /**
     * Colour of link, which is actually clicked. <BR/>
     */
    public static final String COLOR_ALINK = "BLUE";


    /**
     * code for use no outbox filter . <BR/>
     */
    public static final String OUTBOX_NOFILTER    = "1";

    /**
     * code for use timelimit as outbox filter . <BR/>
     */
    public static final String OUTBOX_TIMELIMIT    = "2";

    /**
     * code for use timeframe as outbox filter . <BR/>
     */
    public static final String OUTBOX_TIMEFRAME    = "3";

} // class UserConstants
