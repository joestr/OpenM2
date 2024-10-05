/*
 * Class: BbdConstants.java
 */

// package:
package m2.bbd;

// imports:


/******************************************************************************
 * Constants for the discussion m2 business objects. <BR/>
 * This abstract class contains all constants which are necessary to deal with
 * the business objects delivered within this package.
 *
 * @version     $Id: BbdConstants.java,v 1.7 2007/07/23 08:21:29 kreimueller Exp $
 *
 * @author      Keim Christine (CK), 000428
 ******************************************************************************
 */
public abstract class BbdConstants extends Object
{
    /**
     * Token for the ordering of the discussion container. <BR/>
     */
    public static String ORD_NEWENTRIES = "unknownMessages";

    /**
     * Value for the normal view. <BR/>
     */
    public static int VAL_STANDARD = 1;

    /**
     * Value for the quickview
     */
    public static int VAL_QUICK = 0;

    /**
     * The name of the class in css for discussiontable. <BR/>
     */
    public static final String CLASS_DISC = "disc";

    /**
     * The name of the class in css for list. <BR/>
     */
    public static final String CLASS_DISCTOPIC = "discTopic";

    /**
     * The names of the class in css for the rows. <BR/>
     */
    public static final String[] LST_CLASSDISCROWS = {"discEntry1", "discEntry2"};

} // class BbdConstants
