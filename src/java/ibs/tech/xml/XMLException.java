/*
 * Class: XMLException.java
 */

// package:
package ibs.tech.xml;

// imports:
import ibs.util.GeneralException;


/******************************************************************************
 * A subclass of this exception is thrown if there occurred any error in the
 * xml package. <BR/>
 *
 * @version     $Id: XMLException.java,v 1.2 2007/07/10 14:47:46 kreimueller Exp $
 *
 * @author      Klaus, 08.12.2003
 ******************************************************************************
 */
public abstract class XMLException extends GeneralException
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: XMLException.java,v 1.2 2007/07/10 14:47:46 kreimueller Exp $";



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////


    /**************************************************************************
     * Create a new exception. <BR/>
     * This method calls the constructor of the super class and initializes all
     * properties of this class.
     *
     * @param   name    The name of the error object representing the class that
     *                  raised an error first.
     */
    public XMLException (String name)
    {
        // call constructor of super class:
        super (name);
    } // XMLException


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
    public XMLException (Throwable cause)
    {
        // call constructor of super class:
        super (cause);
    } // XMLException


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
    public XMLException (String name, Throwable cause)
    {
        // call constructor of super class:
        super (name, cause);
    } // XMLException


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

} // class XMLException
