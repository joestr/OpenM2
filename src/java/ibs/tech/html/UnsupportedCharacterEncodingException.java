/*
 * UnsupportedCharacterEncodingException
 *
 * Created on 10. Dezember 2001, 14:44
 */

// package:
package ibs.tech.html;

// imports:


/******************************************************************************
 * This exception is intended to encapsulate the 
 * <code>java.io.UnsupportedEncodingException</code>. It indicates that an
 * invalid character encoding is configured an used within the application.
 * 
 * This has nothing to do with the character encoding of data sent to the
 * application.
 *
 * @version     $Id: UnsupportedCharacterEncodingException.java,v 1.1 2009/12/15 09:35:10 btatzmann Exp $
 *
 * @author  Bernhard Tatzmann
 ******************************************************************************
 */
public class UnsupportedCharacterEncodingException extends RuntimeException
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: UnsupportedCharacterEncodingException.java,v 1.1 2009/12/15 09:35:10 btatzmann Exp $";


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
    static final long serialVersionUID = 3877082410905827194L;


    /**
     * Constructs an instance of <code>UnsupportedCharacterEncodingException</code>
     * with the specified detail message.
     * @param msg the detail message.
     */
    public UnsupportedCharacterEncodingException (String msg)
    {
        super (msg);
    } // UnsupportedCharacterEncodingException
    
    
    /**
     * Constructs an instance of <code>UnsupportedCharacterEncodingException</code>
     * with the specified detail message and nested exception.
     * @param msg   the detail message.
     * @param ex    the nested exception
     */
    public UnsupportedCharacterEncodingException (String msg, Exception ex)
    {
        super (msg, ex);
    } // UnsupportedCharacterEncodingException

} // UnsupportedCharacterEncodingException
