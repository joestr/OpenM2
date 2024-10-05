/*
 * Class: HttpComponentException.java
 */

// package:
package ibs.tech.http;

// imports:


/******************************************************************************
 * Exception thrown if binary read/write in the io-Components fails. <BR/>
 *
 * @version     $Id: HttpComponentException.java,v 1.7 2007/07/23 08:17:33 kreimueller Exp $
 *
 * @author      Keim Christine (CK), 990304
 ******************************************************************************
 */
public class HttpComponentException extends RuntimeException
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: HttpComponentException.java,v 1.7 2007/07/23 08:17:33 kreimueller Exp $";


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
    static final long serialVersionUID = 2209106229386052051L;


    /**************************************************************************
     * Constructor of this class. <BR/>
     */
    public HttpComponentException ()
    {
        // nothing to do
    } // HttpComponentException


    /**************************************************************************
     * Constructor of this class. <BR/>
     *
     * @param   str     Exception string forwarded to super class.
     */
    public HttpComponentException (String str)
    {
        super (str);                    // call constructor of super class
    } // HttpComponentException

} // class HttpComponentException
