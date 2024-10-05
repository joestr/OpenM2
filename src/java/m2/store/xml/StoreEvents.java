/*
 * Class: StoreEvents.java
 */

// package:
package m2.store.xml;

// imports:


/******************************************************************************
 * Events for the store component. <BR/>
 * This abstract class contains all Events which are necessary to deal with
 * the classes delivered within the ecommerceinterface. <P>
 *
 * @version     $Id: StoreEvents.java,v 1.3 2007/07/23 08:21:37 kreimueller Exp $
 *
 * @author      Andreas Jansa (AJ), 010109
 ******************************************************************************
 */
public abstract class StoreEvents
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: StoreEvents.java,v 1.3 2007/07/23 08:21:37 kreimueller Exp $";


    /**
     * <BR/>
     */
    public static final int EVT_PUTPRODUCTINSHOPPINGCART = 600;


    /**
     * <BR/>
     */
    public static final int EVT_TRYTOCREATEORDER = 601;


    /**
     * <BR/>
     */
    public static final int EVT_CREATEORDER = 602;

} // class StoreEvents
