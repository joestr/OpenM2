/*
 * Class: NTLMException.java
 */

// package:
package ibs.tech.ntlm;

// imports:
import ibs.util.GeneralException;


/*******************************************************************************
 * This class implements the errorhandler. <BR/>
 * An error mapping file is used to get meaningful error messages according to
 * the rather abstract error codes returned by implementation specific
 * classes.<P>
 * Note that errors could be chained - this is represented by a vector
 * containing all errors that happened on the way. <BR/>
 *
 * @version     $Id: NTLMException.java,v 1.6 2007/07/23 08:17:33 kreimueller Exp $
 *
 * @author      Bernd Buchegger (BB) 021204
 *******************************************************************************
 */
public class NTLMException extends GeneralException
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: NTLMException.java,v 1.6 2007/07/23 08:17:33 kreimueller Exp $";


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
    static final long serialVersionUID = -6831265548246743168L;


    /***************************************************************************
     * Create a new exception. <BR/>
     *
     * @param   name    The name of the error object representing the class that
     *                  raised an error first.
     */
    public NTLMException (String name)
    {
        super (name);                   // call constructor of upper class
    } // NTLMException


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
    public NTLMException (Throwable cause)
    {
        // call constructor of super class:
        super (cause);
    } // NTLMException


    /**************************************************************************
     * Constructs a new throwable with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * <code>cause</code> is <i>not</i> automatically incorporated in
     * this throwable's detail message.
     *
     * <p>The {@link #fillInStackTrace ()} method is called to initialize
     * the stack trace data in the newly created throwable.
     *
     * @param   name    The name of the error object representing the class that
     *                  raised an error first.
     * @param   cause   The cause (which is saved for later retrieval by the
     *                  {@link #getCause ()} method).  (A <tt>null</tt> value is
     *                  permitted, and indicates that the cause is nonexistent
     *                  or unknown.)
     */
    public NTLMException (String name, Throwable cause)
    {
        // call constructor of super class:
        super (name, cause);
    } // NTLMException

} // class NTLMException
