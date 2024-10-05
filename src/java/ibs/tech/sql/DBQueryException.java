/*
 * Class: DBQueryException.java
 */

// package:
package ibs.tech.sql;

// imports:


/******************************************************************************
 * The DBConnectionException is thrown if their is a problem with the
 * connection. <BR/>
 *
 * @version     $Id: DBQueryException.java,v 1.4 2007/07/23 08:17:33 kreimueller Exp $
 *
 * @author      Mark Wassermann (MW)
 ******************************************************************************
 */
public class DBQueryException extends DBError
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DBQueryException.java,v 1.4 2007/07/23 08:17:33 kreimueller Exp $";


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
    static final long serialVersionUID = 6565055604726735190L;


    /**************************************************************************
     * Create a new exception. <BR/>
     *
     * @param   name    The name of the error object representing the class that
     *                  raised an error first.
     */
    public DBQueryException (String name)
    {
        super (name);                   // call constructor of upper class
    } // DBQueryException


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
    public DBQueryException (Throwable cause)
    {
        // call constructor of super class:
        super (cause);
    } // DBQueryException


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
    public DBQueryException (String name, Throwable cause)
    {
        // call constructor of super class:
        super (name, cause);
    } // DBQueryException


    /**************************************************************************
     * Create a new exception. <BR/>
     *
     * @param runtimeObject    The object of the runtime class where
     *                         the exception occurs.
     * @param chainedException The chained exception if one exists, else null.
     * @param displayChained   The flag if the chained exception shall be displayed
     *                         or not. If t was null then chained is ignored.
     */
    public DBQueryException (Object runtimeObject,
                                  Throwable chainedException,
                                  boolean displayChained)
    {
        super (chainedException);
    } // DBConnectionException

} // DBQueryException
