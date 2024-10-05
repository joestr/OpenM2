/*
 * Class: NoLayoutDefinedException.java
 */

// package:
package ibs.obj.layout;

// imports:
import ibs.util.GeneralException;


/******************************************************************************
 * This class implements the errorhandler. <BR/>
 * An error mapping file is used to get meaningful error messages according to
 * the rather abstract error codes returned by implementation specific
 * classes.<P>
 * Note that errors could be chained - this is represented by a vector
 * containing all errors that happened on the way to and from the database. <BR/>
 *
 * @version     $Id: NoLayoutDefinedException.java,v 1.4 2009/07/24 10:08:01 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR) 990202
 *
 * @see         ibs.obj.layout.LayoutContainer_01
 ******************************************************************************
 */
public class NoLayoutDefinedException extends GeneralException
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: NoLayoutDefinedException.java,v 1.4 2009/07/24 10:08:01 kreimueller Exp $";


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
    static final long serialVersionUID = 5861682531067131324L;


    /**************************************************************************
     * Create a new Errorlist. <BR/>
     *
     * @param   name    The name of the error object representing the class that
     *                  raised an error first.
     */
    public NoLayoutDefinedException (String name)
    {
        super (name);                   // call constructor of upper class
    } // NoLayoutDefinedException

} // class NoLayoutDefinedException
