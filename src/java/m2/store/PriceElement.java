/*
 * Class: PriceElement.java
 */

// package:
package m2.store;

// imports:
import ibs.BaseObject;
import ibs.bo.OID;


/******************************************************************************
 * This class holds 1 code element (color or size). <BR/>
 *
 * @version     $Id: PriceElement.java,v 1.5 2007/07/31 19:14:03 kreimueller Exp $
 *
 * @author      Horst Pichler (HP), 980721
 ******************************************************************************
 */
public class PriceElement extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: PriceElement.java,v 1.5 2007/07/31 19:14:03 kreimueller Exp $";


    /**
     * Sales price of element. <BR/>
     */
    public long salesPrice = 0;
    /**
     * Currency of sales price of element. <BR/>
     */
    public String priceCurrency = "";
    /**
     * Size (oid) of element. <BR/>
     */
    public OID sizeOid = null;
    /**
     * Size (string) of element. <BR/>
     */
    public String sizeName = "";
    /**
     * Color (oid) of element. <BR/>
     */
    public OID colorOid = null;
    /**
     * Color (string) of element. <BR/>
     */
    public String colorName = "";


    /**************************************************************************
     * Creates a new PriceElement. <BR/>
     */
    public PriceElement ()
    {
        // nothing to do
    } // PriceElement

} // PriceElement
