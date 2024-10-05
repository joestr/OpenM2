/*
 * Class: NoDataElementException.java
 */

// package:
package ibs.di;

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
 * @version     $Id: NoDataElementException.java,v 1.1 2009/08/28 14:25:16 kreimueller Exp $
 *
 * @author     Klaus Reimüller (KR), 30.07.2009
 ******************************************************************************
 */
public class NoDataElementException extends GeneralException
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: NoDataElementException.java,v 1.1 2009/08/28 14:25:16 kreimueller Exp $";


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
    static final long serialVersionUID = -3959795364021896411L;


    /**************************************************************************
     * Create a new exception. <BR/>
     *
     * @param   name    The name of the error object representing the class that
     *                  raised an error first.
     */
    public NoDataElementException (String name)
    {
        super (name);                   // call constructor of upper class
    } // NoDataElementException


    /**************************************************************************
     * Constructs a new throwable with the specified cause and a detail
     * message of <tt> (cause==null ? null : cause.toString ())</tt> (which
     * typically contains the class and detail message of <tt>cause</tt>).
     * This constructor is useful for throwables that are little more than
     * wrappers for other throwables (for example, {@link
     * java.security.PrivilegedActionException}).
     *
     * <p>The {@link #fillInStackTrace ()} method is called to initialize
     * the stack trace data in the newly created throwable.
     *
     * @param   cause   The cause (which is saved for later retrieval by the
     *                  {@link #getCause ()} method).  (A <tt>null</tt> value is
     *                  permitted, and indicates that the cause is nonexistent
     *                  or unknown.)
     */
    public NoDataElementException (Throwable cause)
    {
        // call constructor of super class:
        super (cause);
    } // NoDataElementException


    /**************************************************************************
     * Constructs a new throwable with the specified detail message and cause.
     * <br/>
     * <p>Note that the detail message associated with <code>cause</code> is
     * <i>not</i> automatically incorporated in this throwable's detail message.
     * </p>
     * <p>The {@link #fillInStackTrace ()} method is called to initialize
     * the stack trace data in the newly created throwable. </p>
     *
     * @param   name    The name of the error object representing the class that
     *                  raised an error first.
     * @param   cause   The cause (which is saved for later retrieval by the
     *                  {@link #getCause ()} method).  (A <tt>null</tt> value is
     *                  permitted, and indicates that the cause is nonexistent
     *                  or unknown.)
     */
    public NoDataElementException (String name, Throwable cause)
    {
        // call constructor of super class:
        super (name, cause);
    } // NoDataElementException

} // class NoDataElementException
