/*
 * Class: ProductCollectionElement.java
 */

// package:
package m2.store;

// imports:
import ibs.bo.ContainerElement;

import java.util.Vector;


/******************************************************************************
 * This class represents all necessary properties of one element of a
 * ProductCollection. <BR/>
 *
 * @version     $Id: ProductCollectionElement.java,v 1.5 2007/07/31 19:14:03 kreimueller Exp $
 *
 * @author      Bernhard Walter (BW) 980113
 ******************************************************************************
 */
public class ProductCollectionElement extends ContainerElement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ProductCollectionElement.java,v 1.5 2007/07/31 19:14:03 kreimueller Exp $";


    /**
     * . <BR/>
     */
    public int quantity = 0;
    /**
     * . <BR/>
     */
    public Vector<ProductCollectionElementProperty> properties = null;

} // class ProductCollectionElement
