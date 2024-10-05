/*
 * Class: MadConstants.java
 */

// package:
package m2.mad;

// imports:


/******************************************************************************
 * Constants for the MasterData m2 business objects. <BR/>
 * This abstract class contains all constants which are necessary to deal with
 * the business objects delivered within this package.
 *
 * @version     $Id: MadConstants.java,v 1.6 2007/07/23 08:21:36 kreimueller Exp $
 *
 * @author      Keim Christine (CK), 980428
 ******************************************************************************
 */
public abstract class MadConstants extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: MadConstants.java,v 1.6 2007/07/23 08:21:36 kreimueller Exp $";


    /**
     * Orderingtoken for the email of a person. <BR/>
     */
    public static String ORD_EMAIL     = "email";

    /**
     * Show the linked object in search list. <BR/>
     */
    public static final int SHOWSEARCHEDOBJECTS = 3;

} // class MadConstants
