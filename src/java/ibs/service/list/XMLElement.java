/*
 * Class: XMLElement.java
 */

// package:
package ibs.service.list;

// imports:
import ibs.service.list.IXMLElement;
import ibs.util.list.Element;
import ibs.util.list.IElementId;


/******************************************************************************
 * An element of a list which can read xml data. <BR/>
 *
 * @version     $Id: XMLElement.java,v 1.3 2007/07/23 12:34:14 kreimueller Exp $
 *
 * @author      Klaus, 17.12.2003
 ******************************************************************************
 */
public abstract class XMLElement extends Element implements IXMLElement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: XMLElement.java,v 1.3 2007/07/23 12:34:14 kreimueller Exp $";



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a XMLElement object. <BR/>
     *
     * @param   id          Id of the element.
     * @param   name        The element's name.
     */
    public XMLElement (IElementId id, String name)
    {
        // call constructor of super class:
        super (id, name);

        // initialize the other instance properties:
    } // XMLElement


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

} // class XMLElement
