/*
 * Class: UserTokens.java
 */

// package:
package ibs.obj.user;

// imports:
import ibs.obj.user.UserConstants;
import ibs.tech.html.IE302;


/******************************************************************************
 * Tokens for the user part of the intranet basis architecture. <BR/>
 * This abstract class contains all tokens which are necessary to deal with
 * the objects delivered within this package.
 *
 * @version     $Id: UserTokens.java,v 1.19 2011/11/21 12:49:48 gweiss Exp $
 *
 * @author      Keim Christine (CK), 980617
 ******************************************************************************
 */
public abstract class UserTokens extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: UserTokens.java,v 1.19 2011/11/21 12:49:48 gweiss Exp $";


    // text tokens

    /**
     * The legend token. <BR/>
     */
    public static String ML_LEGEND = "ML_LEGEND";

    /**
     * Prefix for the name of the RightsObject. <BR/>
     */
    public static String ML_PRENAME = "ML_PRENAME";

    /**
     * Postfix for the name of the RightsObject. <BR/>
     */
    public static String ML_POSTNAME = "ML_POSTNAME";

    /**
     * Token for FormNew of Rights. <BR/>
     */
    public static String ML_FCTNEWRIGHTS = "ML_FCTNEWRIGHTS";

    /**
     *  Token for list of legitimated
     */
    public static String ML_LEGITIMATED = "ML_LEGITIMATED";

    /**
     *  Token for the status of a group (heading of the table)
     */
    public static String ML_STATUS = "ML_STATUS";

    /**
     *  Token for the active status
     */
    public static String ML_ACTIVE = "ML_ACTIVE";

    /**
     *  Token for the checking of the Password
     */
    public static String ML_CHECKPASSWORD = "ML_CHECKPASSWORD";

    /**
     *  Token for the memberShip
     */
    public static String ML_MEMBERSHIP = "ML_MEMBERSHIP";

    /**
     *  Token for the workSpace
     */
    public static String ML_PRIVATE = "ML_PRIVATE";

    /**
     *  Token for the Link to the Person
     */
    public static String ML_LINKED = "ML_LINKED";

    /**
     *  Token for deleting the Link to the Person
     */
    public static String ML_LINKDEL = "ML_LINKDEL";

    /**
     *  Token to indicate RightsUpdate recursive or not
     */
    public static String ML_RECURSIVE = "ML_RECURSIVE";

    /**
     *  Token to indicate RightsUpdate recursive or not
     */
    public static String ML_DELRECURSIVE = "ML_DELRECURSIVE";

    /**
     * Token for the header in the Person - Selectionlist
     */
    public static String ML_PERSONPREFIX = "ML_PERSONPREFIX";

    /**
     * Token for the header in the Person - Selectionlist
     */
    public static String ML_PERSONNAME = "ML_PERSONNAME";

    /**
     * Token for the header in the Person - Selectionlist
     */
    public static String ML_PERSONFIRM = "ML_PERSONFIRM";

    /**
     * Token for the header in the Person - Selectionlist
     */
    public static String ML_PERSONEMAIL = "ML_PERSONEMAIL";

    /**
     * Token for the content of the Rightscontainer - indicates that one simple
     * right is full awarded
     */
    public static String ML_FULLRIGHTSHORT = "ML_FULLRIGHTSHORT";

    /**
     * Token for the content of the Rightscontainer - indicates that one simple
     * right is partial awarded
     */
    public static String ML_HALFRIGHTSHORT = "ML_HALFRIGHTSHORT";

    /**
     * Non multilingual token for the content of the Rightscontainer - indicates that one simple
     * right is not awarded
     */
    public static String TOK_NORIGHTSHORT = IE302.HCH_NBSP;

    /**
     * Token for the Right - indicates that one simple
     * right is full awarded
     */
    public static String ML_FULLRIGHTLONG = "ML_FULLRIGHTLONG";

    /**
     * Token for the Right - indicates that one simple
     * right is full awarded
     */
    public static String ML_HALFRIGHTLONG = "ML_HALFRIGHTLONG";

    /**
     * Token for the Right - indicates that one simple
     * right is full awarded
     */
    public static String ML_NORIGHTLONG = "ML_NORIGHTLONG";

    /**
     * Token for the Right - indicates that one simple
     * right is full awarded
     */
    public static String ML_FULLRIGHTDESCRIPTION = "ML_FULLRIGHTDESCRIPTION";

    /**
     * Token for the Right - indicates that one simple
     * right is full awarded
     */
    public static String ML_HALFRIGHTDESCRIPTION = "ML_HALFRIGHTDESCRIPTION";

    /**
     * Token for the Right - indicates that one simple
     * right is full awarded
     */
    public static String ML_NORIGHTDESCRIPTION = "ML_NORIGHTDESCRIPTION";

    /**
     * no changes
     */
    public static String ML_NOCHANGE = "ML_NOCHANGE";


    /**
     * Name for Rightalias 'READ'
     */
    public static String ML_RANREAD = "ML_RANREAD";

    /**
     * Name for Rightalias 'WRITE'
     */
    public static String ML_RANWRITE = "ML_RANWRITE";

    /**
     * Name for Rightalias 'ADMIN'
     */
    public static String ML_RANADMIN = "ML_RANADMIN";

    /**
     * set group in rights-assign dialog
     */
    public static String ML_SETGROUP = "ML_SETGROUP";
    /**
     * assign group in rights-assign dialog
     */
    public static String ML_ASSIGNGROUP = "ML_ASSIGNGROUP";
    /**
     * assign user in rights-assign dialog
     */
    public static String ML_ASSIGNUSER = "ML_ASSIGNUSER";
    /**
     * remove right in rights-assign dialog
     */
    public static String ML_REMOVERIGHT = "ML_REMOVERIGHT";

    /**
     * header for PersonSearchContainer_01 if the container is used to search person
     */
    public static String ML_PERSONSEARCHHEADER_SEARCH = "ML_PERSONSEARCHHEADER_SEARCH";

    /**
     * header for PersonSearchContainer_01 if the container is used to show linked person
     */
    public static String ML_PERSONSEARCHHEADER_SHOWLINK = "ML_PERSONSEARCHHEADER_SHOWLINK";

    /**
     * Tokens for rights changes. <BR/>
     */
    public static String[] ML_CHANGERIGHTS =
    {
        UserTokens.ML_FULLRIGHTLONG,
        UserTokens.ML_NORIGHTLONG,
        UserTokens.ML_NOCHANGE,
    }; // ML_CHANGERIGHTS

    /**
     * rightaliases
     */
    public static String[] ML_RIGHTALIASES =
    {
        UserTokens.ML_RANREAD,
        UserTokens.ML_RANWRITE,
        UserTokens.ML_RANADMIN,
    }; // ML_RIGHTALIASES

    /**
     * Token for the emailsddress
     */
    public static String ML_NOTIFICATION_EMAILADRESS = "ML_NOTIFICATION_EMAILADRESS";

     /**
     * Token for the sms-Emailadress
     */
    public static String ML_NOTIFICATION_SMSEMAILADRESS = "ML_NOTIFICATION_SMSEMAILADRESS";



//    /********************************************************************
//     * Set the dependent properties. <BR/>
//     * properties from this and other files (initialized Arrays)
//     * that are build of the Attributes of this File
//     */
//    commented out by gw
//    reason: empty function
//    
//    public static void setDependentProperties ()
//    {
        // TODO RB: Remove this part after all parts are migrated to MLI usage        
/*
        UserTokens.ML_CHANGERIGHTS[0] = UserTokens.ML_FULLRIGHTLONG;
        UserTokens.ML_CHANGERIGHTS[1] = UserTokens.ML_NORIGHTLONG;
        UserTokens.ML_CHANGERIGHTS[2] = UserTokens.ML_NOCHANGE;

        UserTokens.ML_RIGHTALIASES[0] = UserTokens.ML_RANREAD;
        UserTokens.ML_RIGHTALIASES[1] = UserTokens.ML_RANWRITE;
        UserTokens.ML_RIGHTALIASES[2] = UserTokens.ML_RANADMIN;

        UserConstants.LST_HEADINGS_PERSONS[0] = UserTokens.ML_PERSONPREFIX;
        UserConstants.LST_HEADINGS_PERSONS[1] = UserTokens.ML_PERSONNAME;
        UserConstants.LST_HEADINGS_PERSONS[2] = UserTokens.ML_PERSONFIRM;
        UserConstants.LST_HEADINGS_PERSONS[3] = UserTokens.ML_PERSONEMAIL;

        UserConstants.LST_HEADINGS_PERSONS_REDUCED[0] = UserTokens.ML_PERSONNAME;
        UserConstants.LST_HEADINGS_PERSONS_REDUCED[1] = UserTokens.ML_PERSONFIRM;
*/
//    } // setDependentProperties

} // class UserTokens
