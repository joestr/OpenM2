/*
 * Class: ProductCollectionElement.java
 */

// package:
package m2.store;

// imports:
import ibs.BaseObject;
import ibs.bo.OID;


/******************************************************************************
 * This class represents all necessary properties of one element of a
 * ProductCollection. <BR/>
 *
 * @version     $Id: ProductCollectionElementProperty.java,v 1.5 2007/07/23 08:21:37 kreimueller Exp $
 *
 * @author      Bernhard Walter (BW) 980113
 ******************************************************************************
 */
public class ProductCollectionElementProperty extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ProductCollectionElementProperty.java,v 1.5 2007/07/23 08:21:37 kreimueller Exp $";


    /**
     * . <BR/>
     */
    public OID categoryOid = null;
    /**
     * . <BR/>
     */
    public String categoryName = null;
    /**
     * . <BR/>
     */
    public String value = null;

} // class ProductCollectionElementProperty
