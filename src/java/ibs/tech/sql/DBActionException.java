/*
 * Class: DBActionException.java
 */

// package:
package ibs.tech.sql;

// imports:
import java.sql.SQLException;


/******************************************************************************
 * The DBActionException is thrown if an Operation (Query or Procedure)
 * fails. <BR/>
 *
 * @version     $Id: DBActionException.java,v 1.9 2007/07/23 08:17:33 kreimueller Exp $
 *
 * @author      Mark Wassermann (MW)
 ******************************************************************************
 */
public class DBActionException extends DBError
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DBActionException.java,v 1.9 2007/07/23 08:17:33 kreimueller Exp $";


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
    static final long serialVersionUID = 7363010354591733180L;


    /**************************************************************************
     * Constructor of this class. <BR/>
     *
     * @param   name    The name of the error object representing the class that
     *                  raised an error first.
     */
    public DBActionException (String name)
    {
        super (name);
    } // DBActionException

    /**************************************************************************
     * Create a new exception. <BR/>
     *
     * @param runtimeObject    The object of the runtime class where
     *                         the exception occurs.
     * @param chainedException The chained exception if one exists, else null.
     * @param displayChained   The flag if the chained exception shall be displayed
     *                         or not. If t was null then chained is ignored.
     */
    public DBActionException (Object runtimeObject,
                              Throwable chainedException,
                              boolean displayChained)
    {
        this (chainedException);
    } // DBActionException


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
    public DBActionException (Throwable cause)
    {
        // class constructor of super class:
        super (cause);
    } // DBActionException

    /**************************************************************************
     * Returns the detail message string of this throwable.
     *
     * @return  the detail message string of this <tt>Throwable</tt> instance
     *          (which may be <tt>null</tt>).
     */
    public String getMessage ()
    {
        Throwable cause;                // the chained exception

        // get common message:
        String message = super.getMessage ();

        // for SQLExceptions add the sql state:
        if ((cause = this.getCause ()) != null &&
            cause instanceof SQLException)
        {
            message +=
                " (SQLState=" + (((SQLException) cause).getSQLState ()) + ")";
        } // if

        // return the result:
        return message;
    } // getMessage

} // DBActionException
