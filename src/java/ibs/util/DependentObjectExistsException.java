/*
 * Class: DependentObjectExistsException.java
 */

// package:
package ibs.util;

// imports:
import ibs.util.GeneralException;


/******************************************************************************
 * Exception is thrown if other objects have dependences to an object which
 * should be deleted. <BR/>
 *
 * @version     $Id: DependentObjectExistsException.java,v 1.7 2007/07/31 19:14:00 kreimueller Exp $
 *
 * @author      Daniel Janesch (DJ)  001109
 ******************************************************************************
 */
public class DependentObjectExistsException extends GeneralException
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DependentObjectExistsException.java,v 1.7 2007/07/31 19:14:00 kreimueller Exp $";


    /**
     * Serializable version number. <BR/>
     * This value is used by the serialization runtime during deserialization
     * to verify that the sender and receiver of a serialized object have
     * loaded classes for that object that are compatible with respect to
     * serialization. <BR/>
     * If the receiver has loaded a class for the object that has a different
     * serialVersionUID than that of the corresponding sender's class, then
     * deserialization will result in an {@link java.io.InvalidClassException}.
     * <BR/>
     * This field's value has to be changed every time any serialized property
     * definition is changed. Use the tool serialver for that purpose.
     */
    static final long serialVersionUID = 8660788038302577799L;


    /**************************************************************************
     * Create a new exception. <BR/>
     *
     * @param   name    The name of the error object representing the class that
     *                  raised an error first.
     */
    public DependentObjectExistsException (String name)
    {
        super (name);                   // call constructor of upper class
    } // DependentObjectExistsException

} // class DependentObjectExistsException
