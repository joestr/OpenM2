/*
 * RTExceptionInvalidLink.java
 *
 * Created on 10. Dezember 2001, 14:44
 */

// package:
package ibs.di;

// imports:


/******************************************************************************
 * This exception is used to indicate invalid references during the import
 * process (see method Refernez_01.readImportData()).
 *
 * @version     $Id: RTExceptionInvalidLink.java,v 1.4 2007/08/10 14:56:37 kreimueller Exp $
 *
 * @author  Michael Steiner (MS)
 ******************************************************************************
 */
public class RTExceptionInvalidLink extends RuntimeException
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: RTExceptionInvalidLink.java,v 1.4 2007/08/10 14:56:37 kreimueller Exp $";


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
     * Constructs an instance of <code>RTExceptionInvalidLink</code>
     * with the specified detail message.
     * @param msg the detail message.
     */
    public RTExceptionInvalidLink (String msg)
    {
        super (msg);
    } // RTExceptionInvalidLink

} // RTExceptionInvalidLink
