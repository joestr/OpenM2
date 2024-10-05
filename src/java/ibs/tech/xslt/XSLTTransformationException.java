/*
 * Class: XSLTTransformationException.java
 */

// package:
package ibs.tech.xslt;

// imports:
import ibs.util.GeneralException;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.transform.TransformerException;

import org.apache.xml.utils.DefaultErrorHandler;


/******************************************************************************
 * This class implements the errorhandler. <BR/>
 * Note that errors could be chained - this is represented by a vector
 * containing all errors that happened on the way of the error raising. <BR/>
 *
 * @version     $Id: XSLTTransformationException.java,v 1.6 2007/07/23 08:17:33 kreimueller Exp $
 *
 * @author      Klaus, 30.09.2003
 ******************************************************************************
 */
public class XSLTTransformationException extends GeneralException
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: XSLTTransformationException.java,v 1.6 2007/07/23 08:17:33 kreimueller Exp $";


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
    static final long serialVersionUID = -6410168903124983994L;


    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Create a new exception. <BR/>
     *
     * @param   name    The name of the error object representing the class that
     *                  raised the error first.
     */
    public XSLTTransformationException (String name)
    {
        super (name);                   // call constructor of upper class
    } // XSLTTranslationException


    /**************************************************************************
     * Create a new exception. <BR/>
     *
     * @param   cause   The cause of this exception, i.e. the exception which
     *                  caused this one.
     */
    public XSLTTransformationException (Throwable cause)
    {
        super (cause);                   // call constructor of upper class
    } // XSLTTranslationException


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Returns the detail message string of this throwable.
     *
     * @return  the detail message string of this <tt>Throwable</tt> instance
     *          (which may be <tt>null</tt>).
     */
    public String getMessage ()
    {
        String message = super.getMessage ();
        Throwable cause = this.getCause ();

        // check if the cause was a TransformerException:
        if (cause != null && cause instanceof TransformerException)
        {
            StringWriter sw = new StringWriter ();
            DefaultErrorHandler.printLocation (new PrintWriter (sw), cause);
            // concatenate the message of the TransformerException:
            message = sw.toString () + message;
            // concatenate the message of the TransformerException:
//            message += ((TransformerException) cause).getMessageAndLocation ();
        } // if (cause != null && cause instanceof TransformerException)

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
        String message = super.getLocalizedMessage ();
        Throwable cause = this.getCause ();

        // check if the cause was a TransformerException:
        if (cause != null && cause instanceof TransformerException)
        {
            StringWriter sw = new StringWriter ();
            DefaultErrorHandler.printLocation (new PrintWriter (sw), cause);
            // concatenate the message of the TransformerException:
            message = sw.toString () + message;
            // concatenate the message of the TransformerException:
//            message += ((TransformerException) cause).getMessageAndLocation ();
        } // if

        // return the resulting message:
        return message;
    } // getLocalizedMessage


/*
    public static SourceLocator getRootSourceLocator (Throwable exception)
    {
        SourceLocator locator = null;
        Throwable cause = exception;
        // Try to find the locator closest to the cause.
        do
        {
            if (cause instanceof SAXParseException)
            {
                locator = new SAXSourceLocator ((SAXParseException) cause);
            } // if (cause instanceof SAXParseException)
            else    // not a sax parse exception
            {
                if (cause instanceof TransformerException)
                {
                    SourceLocator causeLocator =
                        ((TransformerException) cause).getLocator ();
                    if (null != causeLocator)
                        locator = causeLocator;
                } // if (cause instanceof TransformerException)
            } // else not a sax parse exception
            if (cause instanceof TransformerException)
                cause = ((TransformerException) cause).getCause ();
            else
            {
                if (cause instanceof WrappedRuntimeException)
                    cause = ((WrappedRuntimeException) cause).getException ();
                else
                {
                    if (cause instanceof SAXException)
                        cause = ((SAXException) cause).getException ();
                    else
                        cause = null;
                }
            }
        } while (null != cause);
        return locator;
    } // getRootSourceLocator
*/
} // class XSLTTransformationException
