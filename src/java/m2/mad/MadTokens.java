/*
 * Class: MadTokens.java
 */

// package:
package m2.mad;

// imports:

/******************************************************************************
 * Tokens for the MasterData m2 business objects. <BR/>
 * This abstract class contains all tokens which are necessary to deal with
 * the business objects delivered within this package.
 *
 * @version     $Id: MadTokens.java,v 1.9 2010/04/07 13:37:05 rburgermann Exp $
 *
 * @author      Keim Christine (CK), 980605
 ******************************************************************************
 */
public abstract class MadTokens extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: MadTokens.java,v 1.9 2010/04/07 13:37:05 rburgermann Exp $";

    /**
     * Name of bundle where the tokens included. <BR/>
     */
    public static String TOK_BUNDLE = "m2_m2mad_tokens";

    // text tokens
    /**
     * Token for the owner of a company. <BR/>
     */
    public static String ML_COMPOWNER   = "ML_COMPOWNER";

    /**
     * Token for the manager of a company. <BR/>
     */
    public static String ML_MANAGER   = "ML_MANAGER";

    /**
     * Token for the legal Form of a Company. <BR/>
     */
    public static String ML_LEGALFORM       = "ML_LEGALFORM";

    /**
     * Token for the ARA-LicenceNumber of a Company. <BR/>
     */
    public static String ML_ARANR = "ML_ARANR";

    /**
     * Token for the office-email of a person. <BR/>
     */
    public static String ML_OFFEMAIL       = "ML_OFFEMAIL";
    /**
     * Token for the office-homepage of a person. <BR/>
     */
    public static String ML_OFFHOMEPAGE    = "ML_OFFHOMEPAGE";
    /**
     * Token for the title of a person. <BR/>
     */
    public static String ML_TITLE       = "ML_TITLE";
    /**
     * Token for the position of a person. <BR/>
     */
    public static String ML_POSITION       = "ML_POSITION";
    /**
     * Token for the Company where a Person works. <BR/>
     */
    public static String ML_COMPANY       = "ML_COMPANY";
    /**
     * Token for the Address. <BR/>
     */
    public static String ML_ADDRESS     = "ML_ADDRESS";

    /**
     * Token for the Contact Persons. <BR/>
     */
    public static String ML_CONTACTS     = "ML_CONTACTS";

    /**
     * Token for the Prefix of a persoon. <BR/>
     */
    public static String ML_PREFIX     = "ML_PREFIX";

    /**
     * Token for the prefix Mrs. <BR/>
     */
    public static String ML_PREMS     = "ML_PREMS";

    /**
     * Token for the prefix Mstr. <BR/>
     */
    public static String ML_PREMR     = "ML_PREMR";

    /**
     * Token for the name of tabs. <BR/>
     */
    public static String ML_TO     = "ML_TO";

    /**
     * Token for the tax. <BR/>
     */
    public static String ML_MWST     = "ML_MWST";

    /**
     *  Token for 'assigned to'. <BR/>
     */
    public static String ML_ASSIGNED_TO_USER = "ML_ASSIGNED_TO_USER";

} // class MadTokens
