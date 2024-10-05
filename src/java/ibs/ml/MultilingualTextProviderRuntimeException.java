/*
 * Class: MultilingualTextProviderRuntimeException.java
 */

// package:
package ibs.ml;

// imports:


/******************************************************************************
 * For unexpected exceptions thrown within MultilingualTextProvider. <BR/>
 *
 * @version     $Id: MultilingualTextProviderRuntimeException.java,v 1.1 2010/03/16 10:00:55 btatzmann Exp $
 *
 * @author      Keim Christine (CK), 990304
 ******************************************************************************
 */
public class MultilingualTextProviderRuntimeException extends RuntimeException
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: MultilingualTextProviderRuntimeException.java,v 1.1 2010/03/16 10:00:55 btatzmann Exp $";


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
    public MultilingualTextProviderRuntimeException ()
    {
        // nothing to do
    } // MultilingualTextProviderRuntimeException


    /**************************************************************************
     * Constructor of this class. <BR/>
     *
     * @param   str     Exception string forwarded to super class.
     */
    public MultilingualTextProviderRuntimeException (String str)
    {
        super (str);                    // call constructor of super class
    } // MultilingualTextProviderRuntimeException

} // class MultilingualTextProviderRuntimeException
