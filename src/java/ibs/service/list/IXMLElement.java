/*
 * Class: IXMLElement.java
 */

// package:
package ibs.service.list;

// imports:
import ibs.util.list.IElement;
import ibs.util.list.ListException;

import java.io.File;

import org.w3c.dom.Node;


/******************************************************************************
 * This interface defines an element of a xml element list. All classe, whose
 * objects can be elements of such a list, must implement this interface. <BR/>
 *
 * @version     $Id: IXMLElement.java,v 1.4 2007/07/31 19:13:58 kreimueller Exp $
 *
 * @author      Klaus, 20.12.2003
 ******************************************************************************
 */
public interface IXMLElement extends IElement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: IXMLElement.java,v 1.4 2007/07/31 19:13:58 kreimueller Exp $";



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Get the element type specific data out of the actual element data. <BR/>
     * This method is used to get all values of one element out of the
     * result set. <BR/>
     * <B>example:</B>. <BR/>
     * <PRE>
     * NamedNodeMap attributes = elemData.getAttributes ();
     * this.p_value = XMLHelpers.getAttributeValue (attributes, "itemName");
     * </PRE>
     *
     * @param   elemData    The data for the element.
     * @param   dataFile    The file which contains the data.
     *
     * @throws  ListException
     *          An error occurred during parsing the element.
     */
    void setProperties (Node elemData, File dataFile)
        throws ListException;

} // interface IXMLElement
