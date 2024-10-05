/*
 * Class: XMLReaderException.java
 */

// package:
package ibs.tech.xml;

// imports:
import ibs.tech.xml.XMLException;

import org.xml.sax.SAXParseException;


/******************************************************************************
 * An instance of this class is thrown if there occurred any error when
 * reading a document. <BR/>
 *
 * @version     $Id: XMLReaderException.java,v 1.5 2007/07/23 08:17:33 kreimueller Exp $
 *
 * @author      Klaus, 08.12.2003
 ******************************************************************************
 */
public class XMLReaderException extends XMLException
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: XMLReaderException.java,v 1.5 2007/07/23 08:17:33 kreimueller Exp $";


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
    static final long serialVersionUID = -1769051277095634488L;



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
    public XMLReaderException (String name)
    {
        // call constructor of super class:
        super (name);
    } // XMLReaderException


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
    public XMLReaderException (Throwable cause)
    {
        // call constructor of super class:
        super (cause);
    } // XMLReaderException


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
    public XMLReaderException (String name, Throwable cause)
    {
        // call constructor of super class:
        super (name, cause);
    } // XMLReaderException


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Create message out of throwable.
     *
     * @param   cause   The throwable from which to create the message.
     *
     * @return  the detail message string of this <tt>Throwable</tt> instance
     *          (which may be <tt>null</tt>).
     */
    public String createMessage (Throwable cause)
    {
        String message = null;

        // check if the cause was a TransformerException:
        if (cause != null && cause instanceof SAXParseException)
        {
            SAXParseException e = (SAXParseException) cause;
            message = cause.getMessage () +
                " Error processing resource '" + e.getSystemId () +
                "'. Line " + e.getLineNumber () +
                ", Position " + e.getColumnNumber ();
        } // if

        // return the resulting message:
        return message;
    } // createMessage


    /**************************************************************************
     * Returns the detail message string of this throwable.
     *
     * @return  the detail message string of this <tt>Throwable</tt> instance
     *          (which may be <tt>null</tt>).
     */
    public String getMessage ()
    {
        // get the message out of the cause:
        String message = this.createMessage (this.getCause ());

        // check if we got a message:
        if (message == null)
        {
            // get message from super class:
            message = super.getMessage ();
        } // if

        // return the resulting message:
        return message;
    } // getMessage


    /**************************************************************************
     * Creates a localized description of this throwable.
     * Subclasses may override this method in order to produce a
     * locale-specific message.  For subclasses that do not override this
     * method, the default implementation returns the same result as
     * <code>getMessage ()</code>.
     *
     * @return  The localized description of this throwable.
     * @since   JDK1.1
     */
    public String getLocalizedMessage ()
    {
        // get the message out of the cause:
        String message = this.createMessage (this.getCause ());

        // check if we got a message:
        if (message == null)
        {
            // get message from super class:
            message = super.getLocalizedMessage ();
        } // if

        // return the resulting message:
        return message;
    } // getLocalizedMessage

} // class XMLReaderException
