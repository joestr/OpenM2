/*
 * Class: XmlStoreTokens.java
 */

// package:
package m2.store.xml;

// imports:
import ibs.BaseObject;


/******************************************************************************
 * Tokens for the m2 AddOns after Release 2.2 and before VOYAGER.
 *
 * @version     $Id: XmlStoreTokens.java,v 1.4 2007/07/23 08:21:37 kreimueller Exp $
 *
 * @author      Andreas Jansa (AJ), 010322
 ******************************************************************************
 */
public abstract class XmlStoreTokens extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: XmlStoreTokens.java,v 1.4 2007/07/23 08:21:37 kreimueller Exp $";


    /**
     * . <BR/>
     */
    public static String TOK_ORDERKINDHEADER =  "Auswahl des Bestellungstypes";

    /**
     * <BR/>
     */
    public static String TOK_ORDERKINDSELECTION = "Bestellungsart";

} // class XmlStoreTokens
