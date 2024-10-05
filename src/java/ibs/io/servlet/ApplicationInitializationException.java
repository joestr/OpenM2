/*
 * Class: ApplicationInitializationException.java
 */

// package:
package ibs.io.servlet;

// imports:
import ibs.util.GeneralException;


/******************************************************************************
 * This class.... <BR/>
 *
 * @version     $Id: ApplicationInitializationException.java,v 1.4 2007/07/24 21:29:09 kreimueller Exp $
 *
 * @author      Klaus, 25.12.2003
 ******************************************************************************
 */
public class ApplicationInitializationException extends GeneralException
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ApplicationInitializationException.java,v 1.4 2007/07/24 21:29:09 kreimueller Exp $";


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
    static final long serialVersionUID = -5953746228616720848L;

    /**
     * Shall the application be abandoned? <BR/>
     */
    public boolean p_isAbandonApp = false;



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
     * Create a new exception. <BR/>
     *
     * @param   name    The name of the error object representing the class that
     *                  raised an error first.
     */
    public ApplicationInitializationException (String name)
    {
        super (name);                   // call constructor of super class
    } // ApplicationInitializationException


    /**************************************************************************
     * Create a new exception. <BR/>
     *
     * @param   name    The name of the error object representing the class that
     *                  raised an error first.
     * @param   isAbandonApp    Shall the application be abandoned?
     */
    public ApplicationInitializationException (String name, boolean isAbandonApp)
    {
        super (name);                   // call constructor of super class
        this.p_isAbandonApp = isAbandonApp;
    } // ApplicationInitializationException


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
    public ApplicationInitializationException (Throwable cause)
    {
        // call constructor of super class:
        super (cause);
    } // ApplicationInitializationException


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
    public ApplicationInitializationException (String name, Throwable cause)
    {
        // call constructor of super class:
        super (name, cause);
    } // ApplicationInitializationException

} // class ApplicationInitializationException
