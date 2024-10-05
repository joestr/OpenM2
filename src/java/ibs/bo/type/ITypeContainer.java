/*
 * Class: ITypeContainer.java
 */

// package:
package ibs.bo.type;

// imports:
import ibs.util.list.IElement;
import ibs.util.list.IElementContainer;


/******************************************************************************
 * This class.... <BR/>
 *
 * @version     $Id: ITypeContainer.java,v 1.4 2009/07/23 14:27:27 kreimueller Exp $
 *
 * @author      Klaus, 21.12.2003
 *
 * @param   <E> Class for which to create the container.
 *              Must be subclass of IElement.
 ******************************************************************************
 */
public interface ITypeContainer<E extends IElement> extends IElementContainer<E>
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ITypeContainer.java,v 1.4 2009/07/23 14:27:27 kreimueller Exp $";



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

} // interface ITypeContainer
