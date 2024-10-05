/*
 * Class: MadArguments.java
 */

// package:
package m2.mad;

// imports:


/******************************************************************************
 * Arguments for the MasterData part of m2. <BR/>
 * This abstract class contains all arguments which are necessary to deal with
 * the classes delivered within this package. <P>
 *
 * @version     $Id: MadArguments.java,v 1.6 2007/07/31 19:14:02 kreimueller Exp $
 *
 * @author      Keim Christine (CK), 980605
 ******************************************************************************
 */
public abstract class MadArguments extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: MadArguments.java,v 1.6 2007/07/31 19:14:02 kreimueller Exp $";


    // argument handling for url:
    /**
     * argumentname for the owner of a company. <BR/>
     */
    public static final String ARG_COMPOWNER    = "cown";

    /**
     * argumentname for the manager of a company. <BR/>
     */
    public static final String ARG_MANAGER      = "man";

    /**
     * argumentname for the legal form of a company. <BR/>
     */
    public static final String ARG_LEGALFORM     = "lgf";

    /**
     * argumentname for the ARA-Licencenumber of the company. <BR/>
     */
    public static final String ARG_ARANR     = "aranr";

    /**
     * argumentname for the office-email. <BR/>
     */
    public static final String ARG_OFFEMAIL  = "offem";

    /**
     * argumentname for the office-homepage. <BR/>
     */
    public static final String ARG_OFFHOMEPAGE  = "offhome";

    /**
     * argumentname for the title of the person. <BR/>
     */
    public static final String ARG_TITLE  = "title";

    /**
     * argumentname for the position of a person. <BR/>
     */
    public static final String ARG_POSITION  = "pos";

    /**
     * argumentname for the name of the company. <BR/>
     */
    public static final String ARG_COMPANY = "comp";

    /**
     * argumentname for the prefix of a person. <BR/>
     */
    public static final String ARG_PREFIX = "pre";

    /**
     * argumentname for the tax. <BR/>
     */
    public static final String ARG_MWST = "mwst";

     /**
     * argumentname for the LinkedUser. <BR/>
     */
    public static final String ARG_LINKED_TO = "Linked";

    /**
     * Argument which holds the oid of the calling object. <BR/>
     * Example: add link from person to user
     * - user is calling obj    =>  oid in this.ARG_CALLINGOID
     * - personSearchContainer is object to show  => oid in this.ARG_OID
     */
    public static final String ARG_CALLINGOID = "calloid";

    /**
     * Argument, wich controls if the linked object or the matching objects
     * shall be shown in a searchContainer (see PersonSearchContainer). <BR/>
     */
    public static final String ARG_SHOWLINK = "shwlnk";

    /**
     * oid extension: indicates the oid field. <BR/>
     */
    public static final String ARG_OID_EXTENSION  = "_OID";

    // HACK: The following properties need some description.
    /**
     * Street. <BR/>
     */
    public static final String ARG_STREET = "str";

    /**
     * zip. <BR/>
     */
    public static final String ARG_ZIP = "zip";

    /**
     * town. <BR/>
     */
    public static final String ARG_TOWN = "town";

    /**
     * mailbox. <BR/>
     */
    public static final String ARG_MAILBOX = "mb";

    /**
     * country. <BR/>
     */
    public static final String ARG_COUNTRY = "ctr";

    /**
     * tel. <BR/>
     */
    public static final String ARG_TEL = "tel";

    /**
     * fax. <BR/>
     */
    public static final String ARG_FAX = "fax";

    /**
     * email. <BR/>
     */
    public static final String ARG_EMAIL = "email";

    /**
     * homepage. <BR/>
     */
    public static final String ARG_HOMEPAGE = "hp";

    /**
     * name. <BR/>
     */
    public static final String ARG_NAME = "aname";

} // class MadArguments
