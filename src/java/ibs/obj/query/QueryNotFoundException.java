/*
 * Class: QueryNotFoundException.java
 */

// package:
package ibs.obj.query;

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
 * @version     $Id: QueryNotFoundException.java,v 1.5 2009/07/24 00:55:59 kreimueller Exp $
 *
 * @author      Andreas Jansa (AJ) 010209
 ******************************************************************************
 */
public class QueryNotFoundException extends GeneralException
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: QueryNotFoundException.java,v 1.5 2009/07/24 00:55:59 kreimueller Exp $";


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
    static final long serialVersionUID = -1927432014367273588L;


    /**************************************************************************
     * Create a new exception. <BR/>
     *
     * @param   name    The name of the error object representing the class that
     *                  raised an error first.
     */
    public QueryNotFoundException (String name)
    {
        super (name);                   // call constructor of usper class
    } // QueryNotFoundException

} // class QueryNotFoundException
